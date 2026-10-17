#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""يحوّل بيانات quran-json إلى ملف استيراد أثر.

المدخل (بصيغة https://github.com/risan/quran-json):
    <src>/data/tanzil/uthmani.json     النص العثماني (حفص) — CC-BY 3.0
    <src>/data/tanzil/chapters.json    أسماء السور — CC-BY 3.0
    <src>/data/quranenc/<edition>.json ترجمات — رخصة QuranEnc

المخرج: ملف واحد `quran-import.json` بالشكل الموثّق في docs/09-quran-data.md.

⚠️ **لا يستورد أي نص لا يسمح رخصته بالتخزين الدائم.** لو غاب ملف أو
كانت الرخصة غير معروفة، يفشل الأمر بالكامل ولا يكتب نصف ملف.

الاستعمال:
    python3 tool/convert_quran_json.py --src /tmp/quran-json-main \
        --out /tmp/quran-import.json --accept-licenses
"""

from __future__ import annotations

import argparse
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
}

TOTAL_SURAHS = 114
TOTAL_AYAHS = 6236
# السورة التاسعة (التوبة) لا تُفتح بالبسملة — ثابت في كل المصاحف
SURAH_WITHOUT_BISMILLAH = 9

FOOTNOTE_RE = re.compile(r"\[\d+\]|\(\d+\)")


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


def strip_footnotes(text: str) -> str:
    """يحذف علامات الحواشي `[1]` و `(1)` من نص الترجمة.

    الحواشي نفسها لا تُستورد (لا حقل لها في قاعدة أثر بعد) — لكن إبقاء
    الوسم داخل النص يلوّث فهرس البحث، فنحذفه.
    """
    return FOOTNOTE_RE.sub("", text).strip()


def build_surahs(chapters: object) -> list[dict]:
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
            # ⚠️ الجزء/الصفحة: لا يوفّرها هذا المصدر (راجع docs/09 §«فجوات»)
            "start_juz": None,
            "start_page": None,
        })
    return out


def build_ayahs(uthmani: object, surahs: list[dict]) -> list[dict]:
    if not isinstance(uthmani, dict):
        fail("uthmani.json: المتوقّع كائن {رقم_سورة: [آيات]}")
    if len(uthmani) != TOTAL_SURAHS:
        fail(f"uthmani.json: المتوقّع {TOTAL_SURAHS} سورة، وُجد {len(uthmani)}")

    declared = {s["id"]: s["ayah_count"] for s in surahs}
    out: list[dict] = []
    for sid in range(1, TOTAL_SURAHS + 1):
        key = str(sid)
        verses = uthmani.get(key)
        if not isinstance(verses, list):
            fail(f"uthmani.json: السورة {sid} مفقودة")
        if len(verses) != declared[sid]:
            fail(f"سورة {sid}: chapters.json تقول {declared[sid]} آية "
                 f"لكن uthmani.json فيه {len(verses)}")
        for index, verse in enumerate(verses, start=1):
            text = str(verse.get("text") or "").strip()
            if not text:
                fail(f"سورة {sid} آية {index}: نص فارغ")
            if int(verse["chapter"]) != sid:
                fail(f"سورة {sid} آية {index}: رقم السورة داخل الملف "
                     f"{verse['chapter']} لا يطابق المفتاح")
            if int(verse["verse"]) != index:
                fail(f"سورة {sid} آية {index}: رقم الآية داخل الملف "
                     f"{verse['verse']} لا يطابق ترتيبها")
            out.append({
                "surah": sid,
                "ayah": index,
                "text": text,
                # ⚠️ الجزء/الحزب/الصفحة/السجدة: لا يوفّرها هذا المصدر
                "juz": None,
                "hizb": None,
                "page": None,
                "sajda": False,
            })

    if len(out) != TOTAL_AYAHS:
        fail(f"المجموع {len(out)} آية — المتوقّع {TOTAL_AYAHS} (رواية حفص)")
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
        description="يحوّل quran-json إلى ملف استيراد أثر",
    )
    ap.add_argument("--src", required=True, type=Path,
                    help="مجلد quran-json-main المستخرج")
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

    src: Path = args.src
    surahs = build_surahs(read_json(src / "data/tanzil/chapters.json"))
    ayahs = build_ayahs(read_json(src / "data/tanzil/uthmani.json"), surahs)

    translations: dict[str, list[dict]] = {}
    for edition in args.with_translations:
        path = src / "data/quranenc" / f"{edition}.json"
        translations[f"quranenc.{edition}"] = build_translation(path, edition)

    payload = {
        "source": {
            "name": "quran-json (Tanzil + QuranEnc)",
            "license": " + ".join(
                sorted({LICENSES["tanzil"]["license"]}
                       | ({LICENSES["quranenc"]["license"]}
                          if translations else set()))
            ),
            "url": "https://github.com/risan/quran-json",
            "allowsPermanentStorage": True,
            "author": "Tanzil Project · QuranEnc.com",
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
    for edition, rows in translations.items():
        print(f"  - {edition}: {len(rows)} سطر")
    print("  ⚠️ الجزء/الحزب/الصفحة غير متوفّرة من هذا المصدر — "
          "راجع docs/09 §«فجوات معروفة»")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
