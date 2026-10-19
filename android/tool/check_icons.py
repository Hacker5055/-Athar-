#!/usr/bin/env python3
"""يفحصُ كلَّ ملفّ كوتلين: أيُّ أيقونةٍ تُستعملُ بلا استيراد؟"""
import re
from pathlib import Path

ROOT = Path("/home/user/-Athar-")
SRC = ROOT / "android/app/src/main/java/com/athar/athar"

problems = 0
for p in sorted(SRC.rglob("*.kt")):
    t = p.read_text(encoding="utf-8")
    body = re.sub(r"^import .*$", "", t, flags=re.M)
    imps = set(re.findall(r"^import\s+([\w.]+)$", t, re.M))
    used = set(re.findall(r"Icons\.(?:AutoMirrored\.)?(?:Filled|Outlined|Default)\.(\w+)", body))
    for name in sorted(used):
        if not any(i.endswith("." + name) for i in imps):
            print(f"❌ {p.relative_to(ROOT)}: {name} بلا استيراد")
            problems += 1
print(f"\n{'❌' if problems else '✅'} أيقوناتٌ بلا استيراد: {problems}")
raise SystemExit(1 if problems else 0)
