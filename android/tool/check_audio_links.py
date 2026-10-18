"""يمسح توفّر التلاوة على المصدر: قارئ × جودة × مسار.

⚠️ لماذا أداة منفصلة لا اختبار وحدة: الاختبار يجب أن يمرّ بلا شبكة،
وهذا الفحص **هو** شبكة. نفصلهما فلا يصير جناح الاختبارات هشًّا كلما
تعثّر خادم المصدر.

⚠️ لماذا مسح لا فحص واحد: الجرعة الأولى أثبتت أن الفشل قد يكون
**جودةً لا قارئًا**. من اكتفى بمسح واحد يخرج بقائمة مخطئين وهمية
ويحذف قرّاء يعملون على جودة أخرى.

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

# الجودات التي يعلنها المصدر
BITRATES = (192, 128, 64, 48, 40, 32)

# ⚠️ المساران مختلفان عند المصدر: توفّر القارئ في أحدهما لا يعني
# توفّره في الآخر. في أول مسح كان `ar.alafasy` يبثّ السورة وحده.
PATHS = ("audio-surah", "audio")

# عيّنة صغيرة: أول القرآن وآخره. نتحقق من وجود الملف لا من كماله.
SURAH_SAMPLE = (1, 114)
VERSE_SAMPLE = (1, 6236)


def parse_catalog() -> list[str]:
    """يستخرج مُعرّفات القرّاء من سطور `Reciter(...)`."""
    text = CATALOG.read_text(encoding="utf-8")
    found = re.findall(r'Reciter\(\s*"([^"]+)"', text)
    if not found:
        raise SystemExit("لم يُعثر على أيّ قارئ في Reciter.kt — تغيّر الشكل؟")
    return found


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


def probe(rid: str, bitrate: int, path: str, numbers: tuple[int, ...]) -> bool:
    codes = [head_status(f"{BASE}/{path}/{bitrate}/{rid}/{n}.mp3") for n in numbers]
    return all(c in (200, 206) for c in codes)


def sweep(rid: str) -> tuple[str, list[tuple[int, str]]]:
    """يجرب كل جودة على كل مسار، ويردّ ما نجح مرتّبًا من الأعلى جودة."""
    hits: list[tuple[int, str]] = []

    def one(bitrate: int, path: str) -> tuple[int, str] | None:
        numbers = SURAH_SAMPLE if path == "audio-surah" else VERSE_SAMPLE
        return (bitrate, path) if probe(rid, bitrate, path, numbers) else None

    tasks = [(b, p) for b in BITRATES for p in PATHS]
    with concurrent.futures.ThreadPoolExecutor(max_workers=8) as pool:
        for result in pool.map(lambda t: one(*t), tasks):
            if result:
                hits.append(result)

    # الأفضل أولًا: جودة أعلى، ومسار السورة أوفر للمشغّل (ملف واحد)
    hits.sort(key=lambda h: (h[1] != "audio-surah", -h[0]))
    return rid, hits


def main() -> int:
    catalog = parse_catalog()
    print(f"الكتالوج: {len(catalog)} قارئًا")
    print(f"الجودات: {list(BITRATES)} · المسارات: {list(PATHS)}")
    print("العيّنة: سورة ١ و١١٤ · آية ١ و٦٢٣٦\n")

    results: list[tuple[str, list[tuple[int, str]]]] = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=6) as pool:
        results = list(pool.map(sweep, catalog))

    print(f"{'قارئ':<26} {'الأفضل':<14} كل ما يبثّ")
    print("-" * 96)
    dead: list[str] = []
    for rid, hits in results:
        if not hits:
            dead.append(rid)
            print(f"{rid:<26} {'—':<14} لا شيء يبثّ")
            continue
        best = f"{hits[0][0]}·{hits[0][1]}"
        rest = ", ".join(f"{b}·{p}" for b, p in hits)
        print(f"{rid:<26} {best:<14} {rest}")

    alive = len(results) - len(dead)
    print()
    print(f"يبثّ: {alive}/{len(results)}")
    surah_ready = [r for r, h in results if any(p == "audio-surah" for _, p in h)]
    print(f"يبثّ السورة كاملة: {len(surah_ready)}")
    ayah_ready = [r for r, h in results if any(p == "audio" for _, p in h)]
    print(f"يبثّ الآية مفردة: {len(ayah_ready)}")

    if dead:
        print()
        print(f"❌ لا يبثّ بأيّ جودة: {', '.join(dead)}")
        print("هؤلاء يُحذفون من الكتالوج: زرّ يعمل ولا يخرج صوتًا أسوأ من غيابه.")
        return 1

    print("\n✅ كل قارئ يبثّ على جودة واحدة على الأقل.")
    print("حدّث `Reciter.kt` بالجودة المثبتة هنا، لا بالمنسوبة في التوثيق.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
