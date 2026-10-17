#!/usr/bin/env bash
#
# يجهّز مجلد android/ بعد `flutter create` — ينقل ملفات أثر الأصلية
# (الودجت) ويضبط توقيع الإصدار، ويطبع ما يجب لصقه يدويًا في المانيفست.
#
# الاستخدام (من مجلد app/):
#   flutter create --platforms=android --org com.athar --project-name athar .
#   ./tool/setup_android.sh
#
# ⚠️ السكربت **لا يعدّل** AndroidManifest.xml تلقائيًا: تعديل ملفات
# المنصّة آليًا سهل يكسر البناء بطرق صعبة التشخيص. يطبعه على الشاشة
# ويترك اللصق خطوة واحدة واضحة.
set -euo pipefail

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
NATIVE_DIR="$APP_DIR/native/android"
ANDROID_DIR="$APP_DIR/android"
MAIN_DIR="$ANDROID_DIR/app/src/main"
PKG="com.athar.athar"

if [[ ! -d "$ANDROID_DIR" ]]; then
  echo "❌ مجلد android/ غير موجود. شغّل أولًا:"
  echo "   cd $APP_DIR && flutter create --platforms=android --org com.athar --project-name athar ."
  exit 1
fi

echo "→ تجهيز android/ في: $ANDROID_DIR"

# ── ١. ملف الكوتلن ──
KT_DIR="$MAIN_DIR/kotlin/${PKG//.//}"
mkdir -p "$KT_DIR"
cp "$NATIVE_DIR/kotlin/AtharWidgetProvider.kt" "$KT_DIR/AtharWidgetProvider.kt"
echo "   ✓ kotlin/.../AtharWidgetProvider.kt"

# ── ٢. الموارد (layout · drawable · xml · values) ──
for dir in layout drawable xml values; do
  if [[ -d "$NATIVE_DIR/res/$dir" ]]; then
    mkdir -p "$MAIN_DIR/res/$dir"
    cp -n "$NATIVE_DIR/res/$dir"/*.xml "$MAIN_DIR/res/$dir/" 2>/dev/null || true
    echo "   ✓ res/$dir"
  fi
done

# ── ٣. توقيع الإصدار (يُدرج مرة واحدة فقط) ──
GRADLE_FILE=""
[[ -f "$ANDROID_DIR/app/build.gradle.kts" ]] && GRADLE_FILE="$ANDROID_DIR/app/build.gradle.kts"
[[ -f "$ANDROID_DIR/app/build.gradle" ]] && GRADLE_FILE="$ANDROID_DIR/app/build.gradle"

if [[ -n "$GRADLE_FILE" ]]; then
  if grep -q "keystorePropertiesFile" "$GRADLE_FILE"; then
    echo "   ✓ التوقيع مُعدّ مسبقًا"
  else
    cp "$GRADLE_FILE" "$GRADLE_FILE.bak"
    SNIPPET="$NATIVE_DIR/build.gradle.snippet"
    # نُدرج قبل أول `buildTypes {` — بـ awk لتجنّب الاعتماد على python/perl
    awk -v snippet="$SNIPPET" '
      !inserted && /^[[:space:]]*buildTypes[[:space:]]*\{/ {
        while ((getline line < snippet) > 0) print line
        inserted = 1
        print ""
        print "    // ═══ الكتل الأصلية (من flutter create) ═══"
      }
      { print }
    ' "$GRADLE_FILE" > "$GRADLE_FILE.new"
    mv "$GRADLE_FILE.new" "$GRADLE_FILE"
    echo "   ✓ أضفت إعداد التوقيع (نسخة احتياطية: $(basename "$GRADLE_FILE").bak)"
  fi

  if grep -q "compileSdk" "$GRADLE_FILE"; then
    echo "   ⚠ تأكد إن compileSdk ≥ 35 (مطلوب لـ flutter_local_notifications 19)"
  fi
fi

# ── ٤. ما يجب لصقه يدويًا ──
echo ""
echo "════════════════════════════════════════════════════════════"
echo "الخطوة الأخيرة (يدويًا): ألصق هذا داخل وسم <application> في"
echo "$MAIN_DIR/AndroidManifest.xml"
echo "════════════════════════════════════════════════════════════"
sed -n '/^<receiver/,/^<\/receiver>/p' "$NATIVE_DIR/AndroidManifest.snippet.xml"
echo "════════════════════════════════════════════════════════════"
echo ""
echo "وبعدها:"
echo "  flutter build apk --release"
echo ""
echo "💡 نصيحة: ارفع مجلد android/ إلى Git بعد الإعداد، عشان مستقبل الودجت"
echo "   وإعداد التوقيع ما يضيعوش كل مرة."
