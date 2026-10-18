"""يتحقّق أن كل قارئ في الكتالوج يبثّ فعلًا من المصدر.

⚠️ لماذا أداة منفصلة لا اختبار وحدة: الاختبار يجب أن يمرّ بلا شبكة،
وهذا الفحص **هو** شبكة. نفصلهما فلا يصير جناح الاختبارات هشًّا كلما
تعثّر خادم المصدر.

يقرأ الكتالوج من `Reciter.kt` نفسه لا من نسخة مكرّرة: نسخة ثانية
تصدق يوم تُكتب وتكذب بعد أول تعديل.
"""

from __future__ import annotations

import concurrent.futures
import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CATALOG = ROOT / "app/src/main/java/com/athar/athar/quran/Reciter.kt"
BASE = "https://cdn.islamic.network/quran"

# ⚠️ نفحص سورة الفاتحة وسورة الناس: الأولى ملف صغير والثانية آخر
# ملف في المصحف — سورة واحدة قد تنجح مصادفةً وغيرها مفقود.
PROBE_SURAHS = (1, 114)

# ⚠️ المساران مختلفان عند المصدر، وتوفّر القارئ في أحدهما لا يعني
# توفّره في الآخر: ١٢ قارئًا من ١٣ ردّوا ٤٠٣ على `audio-surah` حين
# كان `ar.alafasy` يعمل. نفحص الاثنين ونقرأ المصفوفة قبل أن نبني
# المشغّل على مسار يبدو في التوثيق أنه العام.
PATHS = ("audio-surah", "audio")

# آية كونية للفحص: ١ أول القرآن، ٦٢٣٦ آخره
PROBE_VERSES = (1, 6236)


def parse_catalog() -> list[tuple[str, int]]:
    """يستخرج (المعرّف، الجودة) من سطور `Reciter(...)`."""
    text = CATALOG.read_text(encoding="utf-8")
    pattern = re.compile(r'Reciter\(\s*"([^"]+)"\s*,.*?(\d{2,3})\s*\)')
    found = pattern.findall(text)
    if not found:
        raise SystemExit("لم يُعثر على أيّ قارئ في Reciter.kt — تغيّر الشكل؟")
    return [(rid, int(bit)) for rid, bit in found]


def head_status(url: str) -> int:
    """يطلب أول بايت فقط: نتحقق من وجود الملف لا من تحميله."""
    result = subprocess.run(
        [
            "curl", "-sS", "-o", "/dev/null", "-w", "%{http_code}",
            "-r", "0-0", "--max-time", "20", url,
        ],
        capture_output=True,
        text=True,
    )
    try:
        return int(result.stdout.strip() or 0)
    except ValueError:
        return 0


def check(reciter: tuple[str, int]) -> tuple[str, int, list[tuple[str, int]]]:
    """يجرب مسار السورة ومسار الآية ويردّ (المسار، الرمز) لكل محاولة."""
    rid, bitrate = reciter
    out: list[tuple[str, int]] = []
    for surah in PROBE_SURAHS:
        out.append((f"surah/{surah}", head_status(f"{BASE}/audio-surah/{bitrate}/{rid}/{surah}.mp3")))
    for verse in PROBE_VERSES:
        out.append((f"ayah/{verse}", head_status(f"{BASE}/audio/{bitrate}/{rid}/{verse}.mp3")))
    return rid, bitrate, out


def main() -> int:
    catalog = parse_catalog()
    print(f"الكتالوج: {len(catalog)} قارئًا، فحص السور {list(PROBE_SURAHS)}\n")

    # ⚠️ المخرجات مصفوفة لا حكم ثنائي: نحن نقيس توفّر المصدر، وقرار
    # أيّ مسار نبني عليه المشغّل يحتاج الصورة كلها لا قائمة المخطئين.
    rows: list[tuple[str, int, list[tuple[str, int]]]] = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=8) as pool:
        rows = list(pool.map(check, catalog))

    def ok(code: int) -> bool:
        return code in (200, 206)

    def cell(code: int) -> str:
        return "OK " if ok(code) else str(code)

    header = "".join(f"{label:>12}" for label, _ in rows[0][2])
    print(f"{'قارئ':<26}{'kbps':>6}{header}")
    for rid, bitrate, probes in rows:
        line = "".join(f"{cell(c):>12}" for _, c in probes)
        print(f"{rid:<26}{bitrate:>6}{line}")

    surah_ok = [rid for rid, _, pr in rows if all(ok(c) for l, c in pr if l.startswith("surah"))]
    ayah_ok = [rid for rid, _, pr in rows if all(ok(c) for l, c in pr if l.startswith("ayah"))]
    print()
    print(f"يبثّ السورة كاملة: {len(surah_ok)}/{len(rows)}")
    print(f"يبثّ الآية مفردة: {len(ayah_ok)}/{len(rows)}")

    broken = [rid for rid in (r[0] for r in rows) if rid not in surah_ok and rid not in ayah_ok]
    if broken:
        print()
        print(f"❌ لا يبثّ لا سورة ولا آية: {', '.join(broken)}")
        print("أخرِجهم من الكتالوج: زرّ يعمل ولا يسمع صوتًا أسوأ من غيابه.")
        return 1
    print("✅ كل قارئ يبثّ على مسار واحد على الأقل.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
