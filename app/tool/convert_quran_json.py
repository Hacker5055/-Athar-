#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""يحوّل بيانات quran-json + quran-meta إلى ملف استيراد أثر.

المدخلات (كلها تُنزَّل بـ tool/fetch_quran_data.sh):
    quran-json/data/tanzil/uthmani.json    النص العثماني (حفص) — CC-BY 3.0
    quran-json/data/tanzil/chapters.json   أسماء السور بالإنجليزية — CC-BY 3.0
    quran-json/data/quranenc/<edition>.json الترجمات — رخصة QuranEnc
    quran-meta/dist/HafsLists-*.js         تخطيط المصحف — MIT

المخرج: ملف واحد `quran-import.json` بالشكل الموثّق في docs/09-quran-data.md.

⚠️ **لا يستورد أي نص لا تسمح رخصته بالتخزين الدائم.** لو غاب ملف أو
كانت الرخصة غير معروفة، يفشل الأمر بالكامل ولا يكتب نصف ملف.

الاستعمال:
    python3 tool/convert_quran_json.py \
        --src /tmp/quran-json-main \
        --layout '/tmp/quran-meta/package/dist/HafsLists-*.js' \
        --out /tmp/quran-import.json --accept-licenses
"""

from __future__ import annotations

import argparse
import glob
import hashlib
import json
import re
import sys
from datetime import date
from pathlib import Path

# ─────────────────────────────────────────────────────────────
# جدول الرخص — الكود لا يتحقق من الرخصة بنفسه، بل يرفض كل ما ليس
# مدرجًا هنا صراحةً. مراجعة الجدول عمل بشري مقصود (docs/05 §3).
# ─────────────────────────────────────────────────────────────
LICENSES = {
    "tanzil": {
        "name": "Tanzil Quran Text (عبر quran-json)",
        "license": "CC-BY-3.0",
        "url": "https://tanzil.net/docs/text_license",
        "author": "Tanzil Project 2007-2021",
        "allowsPermanentStorage": True,
        "note": "يُسمح النسخ والتوزيع كما هو — **تعديل النص ممنوع**.",
    },
    "quranenc": {
        "name": "QuranEnc.com",
        "license": "QuranEnc 7 conditions",
        "url": "https://quranenc.com/en/home/api",
        "author": "QuranEnc.com",
        "allowsPermanentStorage": True,
        "note": (
            "يُسمح التنزيل وإعادة النشر بسبعة شروط: بلا تعديل/إضافة/حذف، "
            "ذكر الناشر و QuranEnc.com، ذكر رقم الإصدار، حفظ معلومات "
            "النقل، إبلاغ الملاحظات، مواكبة آخر إصدار، بلا إعلان غير لائق."
        ),
    },
    "quran-meta": {
        "name": "quran-meta (تخطيط المصحف: جزء · حزب · صفحة · سجدة)",
        "license": "MIT",
        "url": "https://www.npmjs.com/package/quran-meta",
        "author": "quran-meta contributors",
        "allowsPermanentStorage": True,
        "note": (
            "بيانات وصفية فقط (أرقام)، لا نص قرآني. رُوجعت تقاطعيًا مقابل "
            "Tanzil quran-data.xml: تطابق ٦٠٤/٦٠٤ صفحة و٣٠/٣٠ جزءًا "
            "و١٥/١٥ سجدة — راجع docs/09 §6."
        ),
    },
}

TOTAL_SURAHS = 114
TOTAL_AYAHS = 6236
TOTAL_PAGES = 604
TOTAL_JUZS = 30
TOTAL_RUBS = 240
TOTAL_SAJDAS = 15
# السورة التاسعة (التوبة) لا تُفتح بالبسملة — ثابت في كل المصاحف
SURAH_WITHOUT_BISMILLAH = 9

FOOTNOTE_RE = re.compile(r"\[\d+\]|\(\d+\)")

# بصمة جدول التخطيط المتحقق منه. لو تغيّر المنبع، نُحذّر بدل أن نوزّع
# أرقامًا لم تُراجَع. القيم من quran-meta 7.0.0 (MIT) بعد تقاطعها مع Tanzil.
LAYOUT_SHA256 = "243d26eb103322b22deecafaded42e09ba3eba326a8434d6ad54ca094602114d"

# توقيع [startAyahId, ayahCount, surahOrder, rukuCount, name, isMeccan]
_SURAH_ROW_RE = re.compile(
    r'\[\s*(-?\d+),\s*(\d+),\s*(\d+),\s*(\d+),\s*"([^"]*)",\s*(true|false)\s*\]'
)


def fail(msg: str) -> "NoReturn":  # type: ignore[valid-type]
    print(f"✗ {msg}", file=sys.stderr)
    raise SystemExit(1)


def read_json(path: Path) -> object:
    if not path.is_file():
        fail(f"ملف مفقود: {path}\n  شغّل tool/fetch_quran_data.sh أولًا")
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except json.JSONDecodeError as exc:
        fail(f"JSON تالف في {path}: {exc}")


# ─────────────────────────────────────────────────────────────
# تخطيط المصحف (quran-meta — MIT)
# ─────────────────────────────────────────────────────────────
def parse_hafs_lists(pattern: str) -> dict:
    """يقرأ قوائم الجُزء/الصفحة/الربع/السجدة من ملف HafsLists المولّد.

    الملف JavaScript وليس JSON — نقرأه بتعبير نمطي بدل تشغيل Node،
    لأن الأداة يجب أن تعمل ببايثون وحدها.
    """
    matches = sorted(glob.glob(pattern))
    if not matches:
        fail(f"لم يُعثر على ملف HafsLists بـ: {pattern}")
    src = Path(matches[-1]).read_text(encoding="utf-8")

    def numlist(name: str) -> list[int]:
        m = re.search(rf"{name}:\s*\[([^\]]*)\]", src, re.S)
        if not m:
            fail(f"{name}: القائمة مفقودة في {Path(matches[-1]).name}")
        return [int(x) for x in re.findall(r"-?\d+", m.group(1))]

    i = src.index("SurahList:")
    j = src.index("\n\tmeta:", i)
    rows = _SURAH_ROW_RE.findall(src[i:j])
    # أول صف وآخر صف حرّاس (FixedArray<SurahInfo, 116>)
    surahs = rows[1:-1]

    lists = {
        "surahs": surahs,
        # كل قائمة: [0, …البدايات…, 6237] → نشيل الحرّاس من الطرفين
        "pages": numlist("PageList")[1:-1],
        "juzs": numlist("JuzList")[1:-1],
        "rubs": numlist("HizbQuarterList")[1:-1],
        "sajdas": numlist("SajdaList"),
    }

    _check_layout(lists, Path(matches[-1]).name)
    return lists


def _check_layout(lists: dict, name: str) -> None:
    """تحقّق ذاتي — يرفض أي جدول ناقص أو غير متسلسل."""
    surahs, pages = lists["surahs"], lists["pages"]
    juzs, rubs, sajdas = lists["juzs"], lists["rubs"], lists["sajdas"]

    if len(surahs) != TOTAL_SURAHS:
        fail(f"{name}: {len(surahs)} سورة بدل {TOTAL_SURAHS}")
    if sum(int(s[1]) for s in surahs) != TOTAL_AYAHS:
        fail(f"{name}: مجموع الآيات {sum(int(s[1]) for s in surahs)} "
             f"بدل {TOTAL_AYAHS}")
    for key, seq, expected in (
        ("الصفحات", pages, TOTAL_PAGES),
        ("الأجزاء", juzs, TOTAL_JUZS),
        ("الأرباع", rubs, TOTAL_RUBS),
        ("السجدات", sajdas, TOTAL_SAJDAS),
    ):
        if len(seq) != expected:
            fail(f"{name}: {key} {len(seq)} بدل {expected}")
    for key, seq in (("الصفحات", pages), ("الأجزاء", juzs), ("الأرباع", rubs)):
        if any(b <= a for a, b in zip(seq, seq[1:])):
            fail(f"{name}: {key} غير متصاعدة")
    if not (1 <= min(sajdas) and max(sajdas) <= TOTAL_AYAHS):
        fail(f"{name}: فهرس سجدة خارج ١..{TOTAL_AYAHS}")


def build_ayah_layout(lists: dict) -> dict:
    """يحوّل قوائم البدايات إلى خريطة لكل آية: جزء · صفحة · حزب · سجدة."""
    surahs = lists["surahs"]
    starts = [int(s[0]) for s in surahs] + [TOTAL_AYAHS + 1]

    def part_of(gid: int, bounds: list[int]) -> int:
        n = 0
        for k, b in enumerate(bounds):
            if gid >= b:
                n = k + 1
        return n

    ayah_juz = [part_of(g, lists["juzs"]) for g in range(1, TOTAL_AYAHS + 1)]
    ayah_page = [part_of(g, lists["pages"]) for g in range(1, TOTAL_AYAHS + 1)]
    # كل حزب = ٤ أرباع
    ayah_hizb = [((part_of(g, lists["rubs"]) - 1) // 4) + 1
                 for g in range(1, TOTAL_AYAHS + 1)]

    sajda_set = set(lists["sajdas"])
    sajda_sa: list[tuple[int, int]] = []
    for gid in sorted(sajda_set):
        for i in range(TOTAL_SURAHS):
            if starts[i] <= gid < starts[i + 1]:
                sajda_sa.append((i + 1, gid - starts[i] + 1))
                break

    canonical = json.dumps([ayah_juz, ayah_page, ayah_hizb, sajda_sa],
                           separators=(",", ":"))
    digest = hashlib.sha256(canonical.encode("utf-8")).hexdigest()
    if digest != LAYOUT_SHA256:
        fail(
            "⚠️ بصمة تخطيط المصحف تغيّرت!\n"
            f"   المتوقّع: {LAYOUT_SHA256}\n"
            f"   الفعلي  : {digest}\n"
            "   إما أن المنبع حدّث البيانات (راجعها ثم حدّث الثابت)،\n"
            "   أو أن الملف تالف. لن نكتب أرقامًا غير مراجَعة عن القرآن."
        )

    return {
        "juz": ayah_juz,
        "page": ayah_page,
        "hizb": ayah_hizb,
        "sajdas": sajda_sa,
        # موضع أول آية من كل سورة في المصحف
        "surah_start_juz": [ayah_juz[int(s[0]) - 1] for s in surahs],
        "surah_start_page": [ayah_page[int(s[0]) - 1] for s in surahs],
    }


# ─────────────────────────────────────────────────────────────
# النص (quran-json / Tanzil)
# ─────────────────────────────────────────────────────────────
def strip_footnotes(text: str) -> str:
    """يحذف علامات الحواشي `[1]` و `(1)` من نص الترجمة.

    الحواشي نفسها لا تُستورد (لا حقل لها في قاعدة أثر بعد) — لكن إبقاء
    الوسم داخل النص يلوّث فهرس البحث، فنحذفه.
    """
    return FOOTNOTE_RE.sub("", text).strip()


def build_surahs(chapters: object, layout: dict) -> list[dict]:
    if not isinstance(chapters, dict) or "chapters" not in chapters:
        fail("chapters.json: المتوقّع كائن فيه مفتاح \"chapters\"")
    rows = chapters["chapters"]
    if not isinstance(rows, list) or len(rows) != TOTAL_SURAHS:
        fail(f"chapters.json: المتوقّع {TOTAL_SURAHS} سورة، وُجد "
             f"{len(rows) if isinstance(rows, list) else 'غير مصفوفة'}")

    out = []
    for row in rows:
        sid = int(row["id"])
        name_ar = str(row["name"]).strip()
        name_en = str(row.get("translation") or name_ar).strip()
        # لا يوجد اسم فرنسي في المصدر → نرجع للإنجليزي، والواجهة
        # تعرض الاسم العربي دائمًا في السياق القرآني
        out.append({
            "id": sid,
            "name_ar": name_ar,
            "name_en": name_en,
            "name_fr": str(row.get("translation_fr") or name_en).strip(),
            "translit_en": str(row.get("transliteration") or "").strip(),
            "revelation": str(row.get("type") or "meccan").lower(),
            "ayah_count": int(row["total_verses"]),
            "bismillah_pre": sid != SURAH_WITHOUT_BISMILLAH,
            "start_juz": layout["surah_start_juz"][sid - 1],
            "start_page": layout["surah_start_page"][sid - 1],
        })
    return out


def build_ayahs(uthmani: object, surahs: list[dict], layout: dict) -> list[dict]:
    if not isinstance(uthmani, dict):
        fail("uthmani.json: المتوقّع كائن {رقم_سورة: [آيات]}")
    if len(uthmani) != TOTAL_SURAHS:
        fail(f"uthmani.json: المتوقّع {TOTAL_SURAHS} سورة، وُجد {len(uthmani)}")

    declared = {s["id"]: s["ayah_count"] for s in surahs}
    sajdas = set(layout["sajdas"])
    out: list[dict] = []
    index = 0  # فهرس الآية العالمي 0-based

    for sid in range(1, TOTAL_SURAHS + 1):
        key = str(sid)
        verses = uthmani.get(key)
        if not isinstance(verses, list):
            fail(f"uthmani.json: السورة {sid} مفقودة")
        if len(verses) != declared[sid]:
            fail(f"سورة {sid}: chapters.json تقول {declared[sid]} آية "
                 f"لكن uthmani.json فيه {len(verses)}")
        for n, verse in enumerate(verses, start=1):
            text = str(verse.get("text") or "").strip()
            if not text:
                fail(f"سورة {sid} آية {n}: نص فارغ")
            if int(verse["chapter"]) != sid:
                fail(f"سورة {sid} آية {n}: رقم السورة داخل الملف "
                     f"{verse['chapter']} لا يطابق المفتاح")
            if int(verse["verse"]) != n:
                fail(f"سورة {sid} آية {n}: رقم الآية داخل الملف "
                     f"{verse['verse']} لا يطابق ترتيبها")
            out.append({
                "surah": sid,
                "ayah": n,
                "text": text,
                "juz": layout["juz"][index],
                "hizb": layout["hizb"][index],
                "page": layout["page"][index],
                "sajda": (sid, n) in sajdas,
            })
            index += 1

    if len(out) != TOTAL_AYAHS:
        fail(f"المجموع {len(out)} آية — المتوقّع {TOTAL_AYAHS} (رواية حفص)")

    # تناسق التخطيط مع النص
    for a in (out[0], out[-1]):
        if not (1 <= a["juz"] <= TOTAL_JUZS):
            fail(f"جزء خارج النطاق: {a}")
        if not (1 <= a["page"] <= TOTAL_PAGES):
            fail(f"صفحة خارج النطاق: {a}")
    return out


def build_translation(path: Path, edition: str) -> list[dict]:
    data = read_json(path)
    if not isinstance(data, dict):
        fail(f"{path.name}: المتوقّع كائن {{رقم_سورة: [آيات]}}")

    out: list[dict] = []
    for sid in range(1, TOTAL_SURAHS + 1):
        verses = data.get(str(sid))
        if not isinstance(verses, list):
            fail(f"{edition}: السورة {sid} مفقودة")
        for verse in verses:
            text = strip_footnotes(str(verse.get("text") or ""))
            if not text:
                continue
            out.append({
                "surah": int(verse["chapter"]),
                "ayah": int(verse["verse"]),
                "text": text,
            })
    if not out:
        fail(f"{edition}: لا توجد آيات")
    return out


def main() -> int:
    ap = argparse.ArgumentParser(
        description="يحوّل quran-json + quran-meta إلى ملف استيراد أثر",
    )
    ap.add_argument("--src", required=True, type=Path,
                    help="مجلد quran-json-main المستخرج")
    ap.add_argument("--layout", required=True,
                    help="نمط مسار HafsLists-*.js من quran-meta (بـتنصيص)")
    ap.add_argument("--out", required=True, type=Path,
                    help="مسار ملف quran-import.json الناتج")
    ap.add_argument("--with-translations", nargs="*", default=[],
                    metavar="EDITION",
                    help="ترجمات quranenc المطلوبة، مثل "
                         "french_hameedullah english_saheeh")
    ap.add_argument("--accept-licenses", action="store_true",
                    help="إقرار بمراجعة جدول الرخص في هذا الملف")
    args = ap.parse_args()

    if not args.accept_licenses:
        print("⚖️ رخص المصادر:", file=sys.stderr)
        for key, lic in LICENSES.items():
            print(f"  • {key}: {lic['name']} — {lic['license']}",
                  file=sys.stderr)
            print(f"      {lic['url']}", file=sys.stderr)
            print(f"      {lic['note']}", file=sys.stderr)
        fail("راجع الرخص أعلاه ثم أعد الأمر مع --accept-licenses")

    layout = build_ayah_layout(parse_hafs_lists(args.layout))

    src: Path = args.src
    surahs = build_surahs(
        read_json(src / "data/tanzil/chapters.json"), layout)
    ayahs = build_ayahs(
        read_json(src / "data/tanzil/uthmani.json"), surahs, layout)

    translations: dict[str, list[dict]] = {}
    for edition in args.with_translations:
        path = src / "data/quranenc" / f"{edition}.json"
        translations[f"quranenc.{edition}"] = build_translation(path, edition)

    used = {"tanzil", "quran-meta"} | ({"quranenc"} if translations else set())
    payload = {
        "source": {
            "name": "quran-json + quran-meta (Tanzil · QuranEnc · quran-meta)",
            "license": " + ".join(sorted({LICENSES[k]["license"] for k in used})),
            "url": "https://github.com/risan/quran-json"
                   " · https://www.npmjs.com/package/quran-meta",
            "allowsPermanentStorage": True,
            "author": "Tanzil Project · QuranEnc.com · quran-meta contributors",
            "fetchedOn": date.today().isoformat(),
        },
        "surahs": surahs,
        "ayahs": ayahs,
        "translations": translations,
    }

    args.out.parent.mkdir(parents=True, exist_ok=True)
    args.out.write_text(
        json.dumps(payload, ensure_ascii=False, separators=(",", ":")),
        encoding="utf-8",
    )

    size_mb = args.out.stat().st_size / 1024 / 1024
    print(f"✓ {args.out}")
    print(f"  {len(surahs)} سورة · {len(ayahs)} آية · "
          f"{len(translations)} ترجمة · {size_mb:.1f} MB")
    print(f"  تخطيط: ٣٠ جزء · ٦٠ حزب · {TOTAL_PAGES} صفحة · "
          f"{len(layout['sajdas'])} سجدة  (بصمة مطابقة ✅)")
    for edition, rows in translations.items():
        print(f"  - {edition}: {len(rows)} سطر")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
