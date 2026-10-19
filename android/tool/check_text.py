#!/usr/bin/env python3
"""يفحص النصوص العربية من كلماتٍ لاتينية داخلها.

**لماذا هذا الفاحص:** النصوص العربية تمرّ من نموذجٍ يُولّد نصًّا،
وكلمةٌ لاتينية واحدة (مثل «funky») تخرج في تطبيقٍ عربي. والعارض
لا يظهر في البناء ولا في الفحوص — يظهر في **الترجمة**.

ولا يُبنى الفاحص على «كلّ كلمةٍ لاتينية خطأ» — فبعضها مقصود:
أسماءProper، ومُبدّلات `{n}`، و«آداب» 영어، وأسماء المصادر.

الاستعمال:  python3 android/tool/check_text.py
"""

import pathlib
import re
import sys

BUNDLE = (
    pathlib.Path(__file__).resolve().parents[2]
    / "app"
    / "lib"
    / "core"
    / "l10n"
    / "bundles"
    / "strings_ar.dart"
)

# كلماتٌ لاتينية مشروعة داخل النصّ العربي.
ALLOWED = {
    # أسماء المصادر والرخص
    "Adhan", "Dart", "Sepia", "ISNA", "The", "Clear", "Quran",
    "islamic", "network", "alquran", "cloud", "tanzil", "net",
    # أسماء دوالٍ ومُبدّلات
    "n", "d", "m", "deg", "alt", "az", "name", "size", "total",
    "done", "source", "prayer", "time", "place", "count", "target",
    "key", "label", "value", "id", "url", "type", "lang",
    # أسماء الرخص
    "CC", "BY", "ND",
}

# كلماتٌ لاتينية مختلطة عمدًا (حروفٌ إ|latينية داخل الكلمة).
WORD = re.compile(r"[A-Za-z]{2,}")


def main() -> int:
    if not BUNDLE.exists():
        print("⚠️ لا ملفّ الحزمة: %s" % BUNDLE)
        return 0
    source = BUNDLE.read_text(encoding="utf-8")
    body = re.search(r"\{\s*(.*?)\n\};\s*$", source, re.S)
    if not body:
        print("⚠️ تعذّر قراءة الحزمة")
        return 0

    problems = []
    for match in re.finditer(r"'([a-z0-9_]+)':\s*'((?:[^'\\]|\\.)*)'", body.group(1)):
        key, value = match.group(1), match.group(2)
        # المتغيّرات {name} ليست نصًّا.
        value = re.sub(r"\{[a-z0-9_]+\}", " ", value)
        for word in WORD.findall(value):
            if word in ALLOWED:
                continue
            problems.append((key, word))

    if not problems:
        print("✅ لا كلماتٍ لاتينية في النصوص العربية")
        return 0
    for key, word in problems:
        print("⚠️ %s → %s" % (key, word))
    print("\n❌ %d كلمة لاتينية داخل نصّ عربي" % len(problems))
    return 1


if __name__ == "__main__":
    sys.exit(main())
