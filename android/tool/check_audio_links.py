"""يتحقّق أن ما نُصدّره من قرّاء يبثّ فعلًا من المصدر.

⚠️ لماذا أداة منفصلة لا اختبار وحدة: الاختبار يجب أن يمرّ بلا شبكة،
وهذا الفحص **هو** شبكة. نفصلهما فلا يصير جناح الاختبارات هشًّا كلما
تعثّر خادم المصدر.

⚠️ لماذا تحقّق لا استكشاف: الاستكشاف (قارئ × جودة × مسار) علّمنا
ما نجهل — ثمانية قرّاء يبثّون وخمسة لا يبثّون إطلاقًا، واثنان فقط
يبثّان السورة كاملة. أما الآن فالسؤال أضيق وأهم: هل **المُثبَّت في
الكتالوج** يعمل؟ وذلك طلب واحد لكل قارئ بدل مئات، ودقيقتان بدل
ثمانية عشر.

وإن فشل قارئ، يستكشف له بديلًا ويسمّيه — فلا نحذف قارئًا نقلت جودته.

يقرأ الكتالوج من `Reciter.kt` نفسه لا من نسخة مكرّرة: نسخة ثانية
تصدق يوم تُكتب وتكذب بعد أول تعديل.
"""

from __future__ import annotations

import concurrent.futures
import re
import subprocess
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CATALOG = ROOT / "app/src/main/java/com/athar/athar/quran/Reciter.kt"
BASE = "https://cdn.islamic.network/quran"

# الجودات التي قد يبثّ بها المصدر — تُستخدم عند البحث عن بديل فحسب
BITRATES = (192, 128, 64, 48, 40, 32)

# ⚠️ ٤٠٣ قد يكون غياب الملف وقد يكون تحديدًا للمعدّل: مسح أطلق ٣١٢
# طلبًا معًا أسكت خمسة قرّاء دفعة واحدة، وبينهم أشهر الأسماء. نميّز
# الحالين بإعادة محاولة بعد مهلة وبعامل مستخدم معلوم.
UA = "Athar/1.0 (Islamic companion app; +https://github.com/Hacker5055/-Athar-)"
MAX_WORKERS = 4
RETRY_AFTER = 3

# عيّنة صغيرة: نتحقق من وجود الملف لا من كماله
SURAH_SAMPLE = (1,)
VERSE_SAMPLE = (1,)


def parse_catalog() -> list[tuple[str, int, str]]:
    """يستخرج (المعرّف، الجودة، الوضع) من سطور `Reciter(...)`."""
    text = CATALOG.read_text(encoding="utf-8")
    found = re.findall(
        r'Reciter\(\s*"([^"]+)"\s*,.*?ReciterMode\.(\w+)\s*,\s*(\d{2,3})\s*\)',
        text,
    )
    if not found:
        raise SystemExit("لم يُعثر على أيّ قارئ في Reciter.kt — تغيّر الشكل؟")
    return [(rid, int(bit), mode) for rid, mode, bit in found]


def head_status(url: str) -> int:
    """يطلب أول بايت فقط: نتحقق من وجود الملف لا من تحميله."""
    for attempt in (1, 2):
        result = subprocess.run(
            [
                "curl", "-sS", "-o", "/dev/null", "-w", "%{http_code}",
                "-r", "0-0", "--max-time", "25", "-A", UA, url,
            ],
            capture_output=True,
            text=True,
        )
        try:
            code = int(result.stdout.strip() or 0)
        except ValueError:
            code = 0
        if code in (200, 206):
            return code
        if code in (403, 429) and attempt < 2:
            time.sleep(RETRY_AFTER * attempt)
            continue
        return code
    return 0


def url_for(rid: str, bitrate: int, mode: str, number: int) -> str:
    path = "audio-surah" if mode == "SURAH" else "audio"
    return f"{BASE}/{path}/{bitrate}/{rid}/{number}.mp3"


def verify(entry: tuple[str, int, str]) -> tuple[str, int, str, bool]:
    rid, bitrate, mode = entry
    sample = SURAH_SAMPLE if mode == "SURAH" else VERSE_SAMPLE
    codes = [head_status(url_for(rid, bitrate, mode, n)) for n in sample]
    return rid, bitrate, mode, all(c in (200, 206) for c in codes)


def find_alternative(rid: str, mode: str) -> list[tuple[int, str]]:
    """يبحث عمّا يبثّ فعلًا لقارئ تعذّر وضعه المثبَّت."""
    hits: list[tuple[int, str]] = []

    def one(bitrate: int, path_mode: str) -> tuple[int, str] | None:
        sample = SURAH_SAMPLE if path_mode == "SURAH" else VERSE_SAMPLE
        code = head_status(url_for(rid, bitrate, path_mode, sample[0]))
        return (bitrate, path_mode) if code in (200, 206) else None

    tasks = [(b, m) for b in BITRATES for m in ("SURAH", "AYAH")]
    with concurrent.futures.ThreadPoolExecutor(max_workers=MAX_WORKERS) as pool:
        for result in pool.map(lambda t: one(*t), tasks):
            if result:
                hits.append(result)
    hits.sort(key=lambda h: (h[1] != "SURAH", -h[0]))
    return hits


def main() -> int:
    catalog = parse_catalog()
    print(f"الكتالوج: {len(catalog)} قارئًا — نتحقّق ممّا يُصدَّر فعلًا\n")

    results: list[tuple[str, int, str, bool]] = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=MAX_WORKERS) as pool:
        results = list(pool.map(verify, catalog))

    broken: list[tuple[str, int, str]] = []
    print(f"{'قارئ':<26}{'الجودة':>7}  {'الوضع':<7} النتيجة")
    print("-" * 60)
    for rid, bitrate, mode, ok in results:
        mark = "يبثّ" if ok else "لا يبثّ"
        print(f"{rid:<26}{bitrate:>7}  {mode:<7} {mark}")
        if not ok:
            broken.append((rid, bitrate, mode))

    alive = len(results) - len(broken)
    print(f"\nيبثّ: {alive}/{len(results)}")

    if not broken:
        print("✅ كل قرّاء الكتالوج يبثّون على ما أُثبت لهم.")
        return 0

    print("\n⟳ نبحث عن بديل لمن تعذّر:")
    for rid, bitrate, mode in broken:
        alternatives = find_alternative(rid, mode)
        if alternatives:
            best = alternatives[0]
            print(f"  {rid}: جرّب {best[0]}·{best[1]} (المتاح: {alternatives})")
        else:
            print(f"  {rid}: لا يبثّ بأيّ جودة — احذفه من الكتالوج")

    print("\n❌ قارئ في الكتالوج لا يبثّ: زرّ يعمل ولا يخرج صوتًا أسوأ من غيابه.")
    return 1


if __name__ == "__main__":
    sys.exit(main())
