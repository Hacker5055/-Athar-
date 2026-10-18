#!/usr/bin/env python3
"""يولّد `Strings.kt` من حزم فليتر في `app/lib/core/l10n/bundles/`.

⚠️ لماذا مولّد لا نقل يدوي؟
٣٢١ مفتاحًا × ٣ لغات = ٩٦٣ نصًّا. النقل اليدوي يضمن خطأً مطبعيًّا
في مكان ما، وخطأ في نصّ مترجم لا يظهر في البناء بل يظهر للمستخدم
الفرنسي وحده. ثم إن المولّد يُعاد تشغيله حين تتغيّر الحزمة الأصلية.

⚠️ ولماذا لا نستخدم موارد أندرويد (`values-en/`)؟
لأن تبديل اللغة من داخل التطبيق قبل API 33 يحتاج `AppCompatDelegate`
ومعمَلة إضافية، ولأن **اكتمال الترجمة يصير قابلًا للاختبار على JVM**
حين تكون الحزم كائنات Kotlin: اختبار واحد يثبت أن اللغات الثلاث
تحمل المفاتيح ذاتها. وهو اختبار لا يمكن كتابته على موارد XML.
"""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
BUNDLES = ROOT / "app/lib/core/l10n/bundles"
OUT = ROOT / "android/app/src/main/java/com/athar/athar/i18n/Strings.kt"

KEY_RE = re.compile(r"^\s*'([A-Za-z0-9_]+)'\s*:\s*")

# كلمات Kotlin المحجوزة — لا تصلح أسماءً للخصائص
KEYWORDS = {
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun",
    "if", "in", "interface", "is", "null", "object", "package", "return",
    "super", "this", "throw", "true", "try", "typealias", "typeof", "val",
    "var", "when", "while", "by", "catch", "constructor", "delegate",
    "dynamic", "field", "file", "finally", "get", "import", "init", "param",
    "property", "receiver", "set", "setparam", "value", "where",
}


def unescape_dart(raw: str) -> str:
    """يفكّ تهريب Dart: \\' و \\\\ و \\n و \\$."""
    out, i = [], 0
    while i < len(raw):
        ch = raw[i]
        if ch == "\\" and i + 1 < len(raw):
            nxt = raw[i + 1]
            out.append({"n": "\n", "t": "\t", "r": "\r", "'": "'",
                        '"': '"', "\\": "\\", "$": "$"}.get(nxt, nxt))
            i += 2
        else:
            out.append(ch)
            i += 1
    return "".join(out)


def escape_kotlin(text: str) -> str:
    """يهّرب نصًّا ليصلح داخل سلسلة Kotlin بين علامتَي تنصيص.

    ⚠️ `$` يبدأ قالبًا في Kotlin كما في Dart — إن لم يُهرَّب يصير
    النص ناقصًا أو يفشل التجميع.
    """
    return (text
            .replace("\\", "\\\\")
            .replace('"', '\\"')
            .replace("$", "\\$")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
            .replace("\r", ""))


def parse_bundle(path: Path) -> dict:
    """يستخرج `{مفتاح: نص}` من ملف Dart، متجاهلًا التعليقات.

    ⚠️ القيمة قد تكون **على السطر التالي**: منسّق Dart يكسّر النصوص
    الطويلة هكذا. محللٌ يقرأ سطرًا واحدًا فحسب يفقدها بصمت — فيبدو
    المفتاح «ناقصًا» في لغة وهو موجود، فتُترجم الواجهة إلى العربية
    دون سبب ظاهر.
    """
    entries, order = {}, []
    lines = path.read_text(encoding="utf-8").splitlines()
    index = 0
    while index < len(lines):
        line = lines[index]
        index += 1
        stripped = line.strip()
        if stripped.startswith("//") or not stripped:
            continue
        match = KEY_RE.match(line)
        if not match:
            continue
        key = match.group(1)
        rest = line[match.end():].strip()
        if not rest:  # القيمة على سطر تالٍ
            while index < len(lines):
                following = lines[index].strip()
                index += 1
                if not following or following.startswith("//"):
                    continue
                rest = following
                break
        if not rest.startswith("'"):
            continue
        # نزيل الفاصلة الختامية ثم علامتَي التنصيص
        if rest.endswith(","):
            rest = rest[:-1].rstrip()
        if not rest.endswith("'") or len(rest) < 2:
            continue
        value = unescape_dart(rest[1:-1])
        entries[key] = value
        order.append(key)
    return {"entries": entries, "order": order}


def camel(snake: str) -> str:
    parts = snake.split("_")
    head, tail = parts[0], parts[1:]
    name = head + "".join(p.capitalize() for p in tail)
    return name + "_" if name in KEYWORDS else name


def main() -> int:
    bundles = {}
    for locale, filename in (("AR", "strings_ar.dart"),
                             ("EN", "strings_en.dart"),
                             ("FR", "strings_fr.dart")):
        data = parse_bundle(BUNDLES / filename)
        bundles[locale] = data
        print(f"  {locale}: {len(data['entries'])} مفتاحًا من {filename}")

    reference = bundles["AR"]["order"]
    print(f"\nالمرجع (ar): {len(reference)} مفتاحًا")

    # ⚠️ إنذار مبكر: مفتاح ناقص في لغة يعني نصًّا يسقط إلى العربية
    for locale in ("EN", "FR"):
        missing = [k for k in reference if k not in bundles[locale]["entries"]]
        extra = [k for k in bundles[locale]["order"] if k not in set(reference)]
        if missing:
            print(f"  ⚠️ {locale}: {len(missing)} ناقص — {missing[:5]}")
        if extra:
            print(f"  ⚠️ {locale}: {len(extra)} زائد — {extra[:5]}")

    # تصادم أسماء الخصائص
    names = {}
    for key in reference:
        names.setdefault(camel(key), []).append(key)
    clashes = {n: ks for n, ks in names.items() if len(ks) > 1}
    if clashes:
        print("  ❌ تصادم أسماء:", clashes)
        return 1

    lines = []
    w = lines.append
    w("package com.athar.athar.i18n")
    w("")
    w("/// ⚠️ **ملف مولَّد** — لا تُعدّله يدويًا.")
    w("/// أعِد توليده بـ `python3 android/tool/gen_strings.py`.")
    w("/// المصدر: `app/lib/core/l10n/bundles/` (٣٢١ مفتاحًا × ٣ لغات).")
    w("///")
    w("/// ⚠️ أي مفتاح ناقص في لغة **يرجع للعربية** لا إلى المفتاح نفسه:")
    w("/// نصّ خاطئ بالإنجليزية في تطبيق قرآني أسوأ من نصّ عربي صحيح.")
    w("@Suppress(\"TooManyFunctions\", \"LongParameterList\")")
    w("class Strings(val locale: AppLocale) {")
    w("")
    w("    /// Lookup بمفتاح حر (للحالات الديناميكية).")
    w("    fun t(key: String): String =")
    w("        bundle()[key] ?: kStringsAr[key] ?: key")
    w("")
    w("    /// Lookup مع استبدال متغيّرات على شكل `{name}`.")
    w("    fun tArgs(key: String, vararg pairs: Pair<String, Any>): String {")
    w("        var value = t(key)")
    w("        for ((name, arg) in pairs) value = value.replace(\"{$name}\", \"$arg\")")
    w("        return value")
    w("    }")
    w("")
    w("    private fun bundle(): Map<String, String> = when (locale) {")
    for locale in ("AR", "EN", "FR"):
        w(f"        AppLocale.{locale} -> kStrings{locale.title()}")
    w("    }")
    w("")

    # ⚠️ كائن مرافق **واحد** يضمّ الحزم الثلاث: الكوتلين لا يسمح
    # بأكثر من كائن مرافق، فجمعها في واحد بدل ثلاثة.
    w("    companion object {")
    for locale in ("AR", "EN", "FR"):
        entries = bundles[locale]["entries"]
        w("")
        w(f"        val kStrings{locale.title()}: Map<String, String> = mapOf(")
        for key in reference:
            value = entries.get(key)
            if value is None:
                continue  # الناقص يُترَك ليسقط إلى العربية
            w(f"            \"{key}\" to \"{escape_kotlin(value)}\",")
        w("        )")
    w("    }")
    w("")

    w("    // ══════════════════════════════════════════════")
    w("    // الوصول المكتوب النوع — خطأ في الاسم يظهر وقت")
    w("    // التجميع لا وقت التشغيل")
    w("    // ══════════════════════════════════════════════")
    w("")
    for key in reference:
        w(f"    val {camel(key)}: String get() = t(\"{key}\")")

    w("}")

    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"\n✅ كُتب {OUT.relative_to(ROOT)} — {len(lines)} سطرًا")
    return 0


if __name__ == "__main__":
    sys.exit(main())
