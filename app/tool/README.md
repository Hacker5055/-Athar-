# `tool/` — سكربتات المشروع

| الملف | الوظيفة |
|---|---|
| `setup_android.sh` | ينقل ملفات الودجت الأصلية إلى `android/` بعد `flutter create` |
| `build_apk.sh` | يبني APK/AAB موقّعًا (أو غير موقّع للتجربة) |

> ⚠️ كلاهما **اختياري** — نفس النتيجة متاحة بـ GitHub Actions
> (`.github/workflows/build-apk.yml`) بدون تثبيت Flutter محليًا.

## التشغيل

```bash
cd app

# ١) مرة واحدة فقط: توليد مجلد android/
flutter create --platforms=android --org com.athar --project-name athar .

# ٢) نقل ملفات الودجت + طباعة ما يجب لصقه في المانيفست
./tool/setup_android.sh

# ٣) البناء
./tool/build_apk.sh            # debug APK (سريع، للتجربة)
./tool/build_apk.sh --release  # release APK + AAB (يوقّع لو key.properties موجود)
```

## التوقيع (`app/android/key.properties`)

**لا ترفع هذا الملف ولا ملف `.jks` إلى Git أبدًا.** أنشئه محليًا:

```properties
storeFile=/absolute/path/athar-release.jks
storePassword=********
keyAlias=athar
keyPassword=********
```

ولو أردت البناء في GitHub Actions، ارفع محتوياته كـ Secrets:
`KEYSTORE_BASE64` · `KEYSTORE_PASSWORD` · `KEY_ALIAS` · `KEY_PASSWORD`.
راجع `docs/08-apk-and-release.md`.
