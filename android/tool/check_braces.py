#!/usr/bin/env python3
"""فاحصُ الأقواس: يزيل التعليقات والنصوص ثم يتأكد أن الأقواس متوازنة.

**لماذا هذا الفاحص:** فحوصُ الأقواس تلتقط خطأً بشريًّا واحدًا قبل
أن يبنيَه أندرويد. وهو لا يكتفي بالعدّ: فهو **يزيل** التعليقات
والنصوص (بما فيها النصوصُ الخام ذات علامات الاقتباس الثلاث)، وإلا
عدَّ أقواسَ المثال في تعليقٍ أو نصٍّ فحَسبها خطأً.

⚠️ **الترتيبُ مهم**: النصوصُ تُزال **قبل** التعليقات، وإلا عُدّت
«//» داخل رابطٍ (https://…) تعليقًا فبتلعмот بقيةَ السطر —
ومنها علامةُ الإغلاق — فظهر خطأٌ ليس في الكود.

الاستعمال:  python3 android/tool/check_braces.py
"""
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1] / "app" / "src"

TRIPLE = '"' * 3

# نصوصٌ خام (ثلاث علامات اقتباس)
RAW = re.compile(TRIPLE + r"[\s\S]*?" + TRIPLE, re.S)
# نصوصٌ عادية (بعلامتَي اقتباس) — مع مراعاة التهريب
STR = re.compile(r'"(?:\\.|[^"\\\n])*"')
# محارفٌ (بعلامة اقتباس واحدة)
CHR = re.compile(r"'(?:\\.|[^'\\\n])'")
# تعليقاتُ كتلة
BLOCK = re.compile(r"/\*.*?\*/", re.S)
# تعليقاتُ سطرٍ واحد
LINE = re.compile(r"//[^\n]*")


def strip(src: str) -> str:
    src = RAW.sub('""', src)
    src = STR.sub('""', src)
    src = CHR.sub("''", src)
    src = BLOCK.sub("", src)
    src = LINE.sub("", src)
    return src


def main() -> int:
    files = sorted(ROOT.rglob("*.kt"))
    bad = []
    for path in files:
        src = strip(path.read_text(encoding="utf-8"))
        for open_c, close_c in (("{", "}"), ("(", ")"), ("[", "]")):
            if src.count(open_c) != src.count(close_c):
                bad.append((str(path), open_c, src.count(open_c), src.count(close_c)))
    if bad:
        for row in bad:
            print("❌ %s → %s: %d ≠ %d" % row)
        return 1
    print("✅ الأقواس: فُحص %d ملفًا" % len(files))
    return 0


if __name__ == "__main__":
    sys.exit(main())
