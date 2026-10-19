#!/usr/bin/env python3
"""يفحص الأسطرَ المضافةَ في فرق العمل بحثًا عن كلماتٍ لاتينية داخل نصٍّ عربي.

المنهج: في كل سطرٍ فيه حرفٌ عربيّ:
  ١) نُسقط شواهد الكود (`` `...` ``) والمُعرّفات البرمجية
     (snake_case وcamelCase وPascalCase و`a.b`) — فهي كودٌ لا نصٌّ.
  ٢) ما تبقّى من كلماتٍ لاتينيةٍ هو نصٌّ داخل عربي، فيُفحص.
"""
import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]

CODE_SPAN = re.compile(r"`[^`]*`")
QUALIFIED = re.compile(r"\b[A-Za-z_][A-Za-z0-9_]*\.[A-Za-z_][A-Za-z0-9_.]*")
SNAKE = re.compile(r"\b[A-Za-z][A-Za-z0-9]*_[A-Za-z0-9_]+\b")
CAMEL = re.compile(r"\b[a-z]+[A-Z][A-Za-z0-9]*\b")
PASCAL = re.compile(r"\b[A-Z][a-z]+[A-Za-z0-9]*\b")
ARABIC = re.compile(r"[؀-ۿ]")
LATIN_WORD = re.compile(r"[A-Za-z]{2,}")
# سطورُ خريطةِ المفاتيحِ المولَّدة: `"key" to "value",` — ليس نصًّا.
MAP_ENTRY = re.compile(r'^\s*"[A-Za-z0-9_]+"\s+to\s+".*",\s*$')
# تعريفاتُ الكود: «val x = …» و«fun f(…)» — اسمُ المتغيّرِ كودٌ لا نصٌّ.
DECL = re.compile(r"\b(?:val|var|fun|class|object|interface|const|lateinit)\s+[A-Za-z_][A-Za-z0-9_]*")

ALLOWED = {
    "Kotlin", "Compose", "Android", "APK", "UI", "UX", "PDF", "P0", "P1", "P2",
    "AndroidX", "Material", "Google", "Jetpack", "Gradle", "SDK", "JSON", "YAML",
    "OK", "TODO", "NOTE", "FIXME", "dp", "sp", "MPL", "OFL", "MIT", "CC",
    # مراجعُ داخلِ المستودع: `docs/09` و`docs/04` — مساراتٌ لا نصوص.
    "docs",
    # ⚠️ لوحاتُ OLED شائعةٌ في الهواتف، والحديثُ عنها بغير اسمها
    # يُدخلها في قائمةِ الكلماتِ الدخيلة.
    "OLED",
}


def strip_code(line: str) -> str:
    out = DECL.sub(" ", line)
    out = CODE_SPAN.sub(" ", out)
    out = QUALIFIED.sub(" ", out)
    out = SNAKE.sub(" ", out)
    out = CAMEL.sub(" ", out)
    out = PASCAL.sub(" ", out)
    out = re.sub(r"https?://\S+", " ", out)
    out = re.sub(r"\]\([^)]*\)", " ", out)
    return out


def scan(text: str, label: str) -> list:
    problems = []
    for n, line in enumerate(text.splitlines(), 1):
        if not ARABIC.search(line) or MAP_ENTRY.match(line):
            continue
        stripped = strip_code(line)
        for word in LATIN_WORD.findall(stripped):
            if word in ALLOWED:
                continue
            problems.append(f"{label}:{n}: «{word}» → {line.strip()[:100]}")
    return problems


def main() -> int:
    diff = subprocess.run(
        ["git", "diff", "-U0"], cwd=ROOT, capture_output=True, text=True
    ).stdout
    by_file: dict = {}
    cur = None
    for line in diff.splitlines():
        if line.startswith("+++ b/"):
            cur = line[6:]
        elif line.startswith("+") and not line.startswith("+++") and cur:
            by_file.setdefault(cur, []).append(line[1:])

    problems = []
    for f, texts in by_file.items():
        problems += scan("\n".join(texts), f)

    new_dir = ROOT / "android/app/src/main/java/com/athar/athar/ui/components"
    for p in sorted(new_dir.glob("*.kt")):
        problems += scan(p.read_text(encoding="utf-8"), f"components/{p.name}")

    if not problems:
        print("✅ لا كلماتٍ لاتينية داخل النصوص العربية (الأسطرُ المضافة)")
        return 0
    for pr in problems:
        print("⚠️", pr)
    print(f"\n❌ {len(problems)} كلمة دخيلة")
    return 1


if __name__ == "__main__":
    sys.exit(main())
