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


def check(reciter: tuple[str, int]) -> tuple[str, int, list[int]]:
    rid, bitrate = reciter
    codes = []
    for surah in PROBE_SURAHS:
        url = f"{BASE}/audio-surah/{bitrate}/{rid}/{surah}.mp3"
        codes.append(head_status(url))
    return rid, bitrate, codes


def main() -> int:
    catalog = parse_catalog()
    print(f"الكتالوج: {len(catalog)} قارئًا، فحص السور {list(PROBE_SURAHS)}\n")

    broken: list[str] = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=8) as pool:
        for rid, bitrate, codes in pool.map(check, catalog):
            ok = all(c == 200 or c == 206 for c in codes)
            mark = "✅" if ok else "❌"
            print(f"{mark} {rid:<26} {bitrate} kbps → {codes}")
            if not ok:
                broken.append(rid)

    print()
    if broken:
        print(f"❌ {len(broken)} قارئ لا يبثّ: {', '.join(broken)}")
        print("أخرِجهم من الكتالوج: زرّ يعمل ولا يسمع صوتًا أسوأ من غيابه.")
        return 1
    print(f"✅ كل القرّاء ({len(catalog)}) يبثّون.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
