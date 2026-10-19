#!/usr/bin/env python3
"""يفحصُ كلَّ ملفّ كوتلين: رمزٌ مستعملٌ بلا استيرادٍ ولا تعريف؟

مبدأُ الفحص: مفرداتُ المستودع كلِّها (ما استُورد في أيّ ملفّ)
تشكّلُ قاموسَ المشروع. فمن استعمل رمزًا من القاموس في ملفٍ لم
يستورده، فهو ملفٌ لا يُترجَم.
"""
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "android/app/src/main/java/com/athar/athar"

# الملفُ المولَّدُ يُقرأ ليُعرف ما يصرِّحُ به، لكن لا يُفحص:
# أسماءُ خصائصِه مشتقّةٌ من المفاتيح.
GENERATED = "Strings.kt"
files = sorted(SRC.rglob("*.kt"))
parsed = []
for p in files:
    t = p.read_text(encoding="utf-8")
    body = re.sub(r"^import .*$", "", t, flags=re.M)
    body = re.sub(r"/\*.*?\*/", " ", body, flags=re.S)
    body = re.sub(r"//[^\n]*", " ", body)
    # النصوصُ داخلُ السلاسلِ نصوصٌ لا رموز: «Text» مفتاحُ JSON
    # و«Font» اسمُ رخصةٍ في نصٍّ — ولا يُستوردُ أيٌّ منهما.
    body = re.sub(r'"(?:\\.|[^"\\])*"', '""', body)
    paths = re.findall(r"^import\s+([\w.]+)$", t, re.M)
    imps = {i.split(".")[-1] for i in paths}
    pkg = re.search(r"^package\s+([\w.]+)", t, re.M)
    declared = set(re.findall(r"\b(?:class|interface|object|enum class|data class|fun|val|var|typealias)\s+(\w+)", t))
    parsed.append((p, body, imps, pkg, declared, paths))

# قاموسُ المشروع: كلُّ ما استُورد في أيّ ملفّ.
vocab = set()
full_vocab = set()
for _, _, imps, _, _, paths in parsed:
    vocab |= imps
    full_vocab |= set(paths)

# رموزٌ من خارج المشروع (حزمُ أندرويد وكوتلن) — نتجاهلها.
# ⚠️ **حزمةُ java.lang وحدها** هي التي يُستعملُ منها بلا استيراد.
# سائرُ الحزم — حتى أندرويد وكوتلن — لا يُوجدُ فيها رمزٌ بلا
# استيرادٍ صريح. فتسهُيلُها يُخفي أخطاءً حقيقيّةً (كـ«Arrangement»
# في شاشةِ الأذكار).
FOREIGN = re.compile(r"^java\.lang\.")

problems = 0
for p, body, imps, pkg, declared, paths in parsed:
    if p.name == GENERATED:
        continue
    # رموزُ الحزمةِ نفسِها: كلُّ ما صرِّحَ به في ملفٍّ من الحزمة.
    same_pkg = set()
    if pkg:
        for q, qbody, _, qpkg, qdecl, _ in parsed:
            if qpkg and qpkg.group(1) == pkg.group(1):
                same_pkg |= qdecl
    # الرموزُ المستعملة في النصّ.
    # ما بعد نقطةٍ ليس رمزًا مستعملًا بل جزءٌ من اسمٍ مُطوَّل.
    body = re.sub(r"\.\w+", " ", body)
    used = set(re.findall(r"\b([A-Z]\w{2,})\b", body))
    for name in sorted(used):
        if name in imps or name in declared or name in same_pkg:
            continue
        # هل هو من قاموس المشروع؟ (أي استُورد في مكانٍ ما)
        if name not in vocab:
            continue
        # هل هو من حزمةٍ خارجية؟ (اسمٌ عامّ مثل Activity أو Bundle)
        if any(i.endswith("." + name) and FOREIGN.match(i) for i in full_vocab):
            continue
        print(f"❌ {p.relative_to(ROOT)}: {name} بلا استيراد")
        problems += 1
print(f"\n{'❌' if problems else '✅'} رموزٌ بلا استيراد: {problems}")
raise SystemExit(1 if problems else 0)
