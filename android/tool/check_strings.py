#!/usr/bin/env python3
"""يتحقّق أن كل `s.xxx` مستخدم في الواجهة له خاصية فعلية في `Strings`.

⚠️ لماذا هذا الفاحص موجود؟
كتبتُ `s.sunriseNoAdhan` من رأسي، ظنًّا أن المفتاح موجود — ولم يكن.
الكوتلين لا يترجم، لكن الخطأ الحقيقي ليس في التجميع: هو أنني **كنت
سأكتشفه بعد ست دقائق من وقت البناء**، أو أسوأ — لو كان الاسم قريبًا
من مفتاح موجود لمرّ وترجمت الواجهة شيئًا غير مقصود.

هذا الفاحص يحوّل «اسم خاصية من رأسي» من خطأ يُكتشف على CI إلى خطأ
يُكتشف في ثانية، قبل أي دفع.
"""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
STRINGS = ROOT / "android/app/src/main/java/com/athar/athar/i18n/Strings.kt"
SOURCES = ROOT / "android/app/src/main/java/com/athar/athar/ui"

PROPERTY = re.compile(r"\bval\s+([A-Za-z_][A-Za-z0-9_]*)\s*:\s*String\s+get\(\)")
# ⚠️ `t` و`tArgs` **دوال** لا خصائص — فاحص يتجاهلهما يبلغ عن خمسة
# إنذارات كاذبة، فيفقد ثقتي فيه فأتجاهله، فأفقد الحماية كلها.
FUNCTION = re.compile(r"\bfun\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(")
USAGE = re.compile(r"\bs\.([A-Za-z_][A-Za-z0-9_]*)")


def main() -> int:
    if not STRINGS.exists():
        print(f"❌ لا وجود لـ {STRINGS}")
        return 2

    text = STRINGS.read_text(encoding="utf-8")
    defined = set(PROPERTY.findall(text)) | set(FUNCTION.findall(text))
    props = len(set(PROPERTY.findall(text)))
    print(f"خصائص معرَّفة في Strings: {props} (+ دوال: "
          f"{', '.join(sorted(set(FUNCTION.findall(text))))})")

    missing, files = {}, 0
    for path in sorted(SOURCES.rglob("*.kt")):
        text = path.read_text(encoding="utf-8")
        used = set(USAGE.findall(text))
        if not used:
            continue
        files += 1
        unknown = sorted(used - defined)
        if unknown:
            missing[path.name] = unknown

    if missing:
        print(f"\n❌ خصائص مستخدمة وغير معرَّفة ({sum(len(v) for v in missing.values())}):")
        for name, keys in missing.items():
            for key in keys:
                print(f"   {name}: s.{key}")
        return 1

    print(f"✅ كل الخصائص المستخدمة معرَّفة ({files} ملف واجهة)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
