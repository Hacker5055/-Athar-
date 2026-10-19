#!/usr/bin/env python3
"""يفحص تزاحم توقيعات JVM في مصادر Kotlin.

**لماذا هذا الفاحص موجود:** Kotlin يسمح بتزاحم في **المصدر**
حين يختلف نوعا المعاملين في generics فقط:

    fun jsonOf(m: Map<String, Set<String>>): JSONObject
    fun jsonOf(a: Map<PrayerSlot, Int>): JSONObject

Kotlin يختار الدالّة الصحيحة، لكنّ JVM **يمحو** الـ generics
فتصبحان `jsonOf(Ljava/util/Map;)Lorg/json/JSONObject;` — واحدة.
والكود يمرّ في المحرّر وفي القراءة، ويفشل في البناء وحده.

الخطأ ظهر في CI لا محليًّا، لأنّ المحلّي لا يبني دائمًا.

الاستعمال:  python3 android/tool/check_signatures.py
"""

import collections
import pathlib
import re
import sys

APP = pathlib.Path(__file__).resolve().parents[1] / "app" / "src"


def erase(text: str) -> str:
    """يحذف ما تمحوه JVM من التوقيع: الـ generics واسم صاحب النوع.

    ⚠️ حذف الـ generics لا يكون بتعبير نمطي ساذج: `Map<String,
    Set<String>>` فيه `<` داخل `<>`، فالنمط `<[^<>]*>` يزيل
    innermost فقط فيبقى `Map<String, Set>` — وهو **ليس** ما تمحوه
    JVM. فلا بدّ من مطابقة الأقواس زوجًا زوجًا.
    """
    text = text.strip()
    out = []
    depth = 0
    for ch in text:
        if ch == "<":
            depth += 1
            continue
        if ch == ">":
            depth -= 1
            continue
        if depth == 0:
            out.append(ch)
    text = "".join(out)
    text = re.sub(r"\b\w+\??\s*\.", "", text)
    return text


def split_params(raw: str) -> list[str]:
    depth, current, parts = 0, "", []
    for ch in raw:
        if ch in "<([":
            depth += 1
        elif ch in ">)]":
            depth -= 1
        if ch == "," and depth == 0:
            parts.append(current)
            current = ""
        else:
            current += ch
    if current.strip():
        parts.append(current)
    return parts


def signatures(path: pathlib.Path) -> list[tuple[str, tuple[str, ...]]]:
    source = path.read_text(encoding="utf-8")
    found = []
    pattern = re.compile(
        r"(?:private |internal |public |protected )?fun\s+"
        r"(?:<[^>]*>\s*)?([A-Za-z_]\w*)\s*\(([^)]*)\)",
        re.S,
    )
    for match in pattern.finditer(source):
        name, raw = match.group(1), match.group(2)
        if not raw.strip():
            continue
        types = []
        for part in split_params(raw):
            part = part.split("=", 1)[0]
            types.append(erase(part.split(":", 1)[1]) if ":" in part else "?")
        found.append((name, tuple(types)))
    return found


def main() -> int:
    clashes = []
    for path in sorted(APP.rglob("*.kt")):
        seen = collections.Counter(signatures(path))
        for (name, sig), count in seen.items():
            if count > 1:
                clashes.append((path, name, sig, count))
    if not clashes:
        print("✅ لا تزاحم في توقيعات JVM")
        return 0
    for path, name, sig, count in clashes:
        print("⚠️ %s :: fun %s(%s)  ×%d" % (path, name, ", ".join(sig), count))
    print("\n❌ %d تزاحم — غيّر اسم إحدى الدوال" % len(clashes))
    return 1


if __name__ == "__main__":
    sys.exit(main())
