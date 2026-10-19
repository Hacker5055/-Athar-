#!/usr/bin/env bash
# يجلب بيانات القرآن وتخطيط المصحف ويحوّلها إلى ملف استيراد أثر.
#
# ⚠️ **لا يضيف أي شيء إلى المستودع.** الناتج يُكتب في مجلد مؤقت
# (أو حيث تقوله بـ --out-dir)، لأن نص القرآن لا يُرفق بـ git — قرار
# الترخيص والتحقق من النص مسؤولية المالك (docs/05 §3 و docs/09).
#
# الاستعمال:
#   ./tool/fetch_quran_data.sh                      # النص العثماني + التخطيط
#   ./tool/fetch_quran_data.sh --with-translations   # + فرنسية حميد الله + إنجليزية صحيح
#   ./tool/fetch_quran_data.sh --out-dir ~/Downloads
set -euo pipefail

# ── المنابع (مثبّتة بإصدارها) ────────────────────────────────
QURAN_JSON_URL="https://codeload.github.com/risan/quran-json/tar.gz/refs/heads/main"
# quran-meta: MIT — بيانات وصفية (جزء · حزب · صفحة · سجدة)، لا نص قرآني
QURAN_META_VERSION="7.0.0"
QURAN_META_URL="https://registry.npmjs.org/quran-meta/-/quran-meta-${QURAN_META_VERSION}.tgz"

WITH_TRANSLATIONS=0
OUT_DIR="${TMPDIR:-/tmp}/athar-quran"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --with-translations) WITH_TRANSLATIONS=1; shift ;;
    --out-dir) OUT_DIR="${2:?--out-dir يحتاج مسارًا}"; shift 2 ;;
    -h|--help) sed -n '2,14p' "$0"; exit 0 ;;
    *) echo "خيار غير معروف: $1" >&2; exit 1 ;;
  esac
done

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WORK="$OUT_DIR/src"
mkdir -p "$WORK"

echo "→ (١/٣) تنزيل quran-json (النص العثماني — CC-BY 3.0)"
TARBALL="$WORK/quran-json.tar.gz"
if [[ ! -f "$TARBALL" ]]; then
  curl -sSL --fail --max-time 600 -o "$TARBALL" "$QURAN_JSON_URL"
fi

echo "→ استخراج الملفات المطلوبة فقط"
tar xzf "$TARBALL" -C "$WORK" \
  quran-json-main/data/tanzil/uthmani.json \
  quran-json-main/data/tanzil/chapters.json \
  quran-json-main/data/meta/sources.json

TRANSLATION_ARGS=()
if [[ "$WITH_TRANSLATIONS" == "1" ]]; then
  tar xzf "$TARBALL" -C "$WORK" \
    quran-json-main/data/quranenc/french_hameedullah.json \
    quran-json-main/data/quranenc/english_saheeh.json
  TRANSLATION_ARGS+=(--with-translations french_hameedullah english_saheeh)
fi

echo
echo "→ فحص الرخص المسجّلة في المصدر"
python3 - "$WORK" <<'PY'
import json, sys, pathlib
p = pathlib.Path(sys.argv[1]) / "quran-json-main/data/meta/sources.json"
entries = {e["path"]: e for e in json.loads(p.read_text(encoding="utf-8"))}
for path in ("data/tanzil/uthmani.json", "data/tanzil/chapters.json",
             "data/quranenc/french_hameedullah.json",
             "data/quranenc/english_saheeh.json"):
    e = entries.get(path)
    if e is None:
        continue  # قد لا يُطلب إلا النص
    status = e.get("license_status")
    mark = "✅" if status == "granted" else "⛔"
    print(f"  {mark} {path}: {status} — {e.get('license_url')}")
    if status != "granted":
        print("     ⛔ المصدر لا يصرّح بإعادة النشر — لا نكمل")
        sys.exit(1)
PY

echo
echo "→ (٢/٣) تنزيل quran-meta ${QURAN_META_VERSION} (تخطيط المصحف — MIT)"
mkdir -p "$WORK/quran-meta"
if [[ ! -f "$WORK/quran-meta.tgz" ]]; then
  curl -sSL --fail --max-time 300 -o "$WORK/quran-meta.tgz" "$QURAN_META_URL"
fi
tar xzf "$WORK/quran-meta.tgz" -C "$WORK/quran-meta"
echo "  ✅ MIT — بيانات وصفية فقط (٣٠ جزء · ٦٠ حزب · ٦٠٤ صفحة · ١٥ سجدة)"

echo
echo "→ (٣/٣) التحويل"
python3 "$ROOT/tool/convert_quran_json.py" \
  --src "$WORK/quran-json-main" \
  --layout "$WORK/quran-meta/package/dist/HafsLists-*.js" \
  --out "$OUT_DIR/quran-import.json" \
  --accept-licenses \
  "${TRANSLATION_ARGS[@]+"${TRANSLATION_ARGS[@]}"}"

echo
echo "✓ جاهز: $OUT_DIR/quran-import.json"
echo
echo "الخطوة التالية (يدويًا، بعد مراجعة النص):"
echo "  1. افتح الملف وتأكّد من سلامة النص العثماني"
echo "  2. انسخه إلى جهاز/مجلد يقرأه التطبيق"
echo "  3. استورد من داخل التطبيق (يتحقّق من الرخصة قبل الكتابة)"
echo
echo "⚠️  لا ترفع هذا الملف إلى المستودع — راجع docs/09-quran-data.md"
