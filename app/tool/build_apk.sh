#!/usr/bin/env bash
#
# يبني أثر.
#
#   ./tool/build_apk.sh              → debug APK  (للتجربة السريعة)
#   ./tool/build_apk.sh --release    → release APK (split-per-abi) + AAB
#   ./tool/build_apk.sh --aab        → AAB فقط (متجر جوجل)
#
# التوقيع: لو `android/key.properties` موجود، البناء يُوقّع تلقائيًا.
# غير موجود → release APK غير موقّع (صالح للتجربة، **مش** للنشر).
set -euo pipefail

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$APP_DIR"

MODE="${1:---debug}"
OUT="build/outputs"

echo "→ flutter pub get"
flutter pub get

echo "→ التحليل"
flutter analyze || echo "   ⚠ analyzer أبلغ عن ملاحظات (غير مانع)"

echo "→ الاختبارات"
flutter test

mkdir -p "$OUT"

case "$MODE" in
  --release)
    echo "→ release APK (split per ABI) + AAB"
    flutter build apk --release --split-per-abi
    flutter build appbundle --release
    cp -f build/app/outputs/flutter-apk/*.apk "$OUT/" 2>/dev/null || true
    cp -f build/app/outputs/bundle/release/*.aab "$OUT/" 2>/dev/null || true
    ;;
  --aab)
    echo "→ AAB فقط (متجر جوجل)"
    flutter build appbundle --release
    cp -f build/app/outputs/bundle/release/*.aab "$OUT/" 2>/dev/null || true
    ;;
  *)
    echo "→ debug APK"
    flutter build apk --debug
    cp -f build/app/outputs/flutter-apk/*.apk "$OUT/" 2>/dev/null || true
    ;;
esac

echo ""
echo "✅ الناتج في: $OUT"
ls -lh "$OUT" 2>/dev/null || true
