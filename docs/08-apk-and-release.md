# ٠٨ — بناء الـ APK والنشر

> **الهدف من هذا الملف:** في نهاية الأسبوع ١٤ يكون عندك ملف APK جاهز
> للتنزيل والتثبيت، وAAB جاهز لمتجر جوجل — بخطوات واضحة سواء من
> جهازك أو من GitHub Actions بدون تثبيت Flutter أصلًا.

---

## ١. ثلاثة مسارات — اختر واحدًا

| المسار | متى تستخدمه | تحتاج |
|---|---|---|
| **GitHub Actions** | الأسهل — كل push يبني APK | حساب GitHub فقط |
| **السكربت المحلي** `tool/build_apk.sh` | عندك Flutter مثبّت | Flutter + Android SDK |
| **يدوي** | تريد تحكمًا كاملًا | نفس السابق |

### المسار ١: GitHub Actions (الموصى به)

موجود مسبقًا في `.github/workflows/build-apk.yml`. يعمل تلقائيًا:

1. ادفع أي commit على أي فرع.
2. اذهب إلى تبويب **Actions** → آخر تشغيل.
3. نزّل `athar-apk-<sha>` من قسم **Artifacts**.
4. افتح المجلد: فيه `app-armeabi-v7a-release.apk` · `app-arm64-v8a-release.apk` · `app-x86_64-release.apk` · `app-debug.apk` · `app-release.aab`.

> أي جهاز أندرويد حديث (٢٠١٩+) يكفيه **`app-arm64-v8a-release.apk`**.
> لو شكّكت في نوع الجهاز، استخدم `app-debug.apk` (يعمل على الكل، أكبر حجمًا).

**لإصدار رسمي:** ارفع وسمًا بصيغة `v1.0.0` وسيُنشئ العمل إصدارًا على GitHub
مرفقة به كل الملفات:
```bash
git tag v0.1.0 && git push origin v0.1.0
```

### المسار ٢: السكربت المحلي

```bash
cd app

# مرة واحدة فقط
flutter create --platforms=android --org com.athar --project-name athar .
./tool/setup_android.sh          # ينقل الودجت + يضبط التوقيع

# البناء
./tool/build_apk.sh              # debug
./tool/build_apk.sh --release    # release APK + AAB
```

### المسار ٣: يدوي

```bash
cd app
flutter pub get
flutter test
flutter build apk --release --split-per-abi
# الناتج: build/app/outputs/flutter-apk/
```

---

## ٢. التوقيع (مطلوب للنشر)

بدون توقيع، لن يقبل متجر جوجل التطبيق، ولن يتمكن المستخدم من الترقية
لاحقًا بدون حذف التطبيق أولًا (فقدان كل البيانات).

### أنشئ مفتاحك مرة واحدة

```bash
keytool -genkey -v -keystore ~/athar-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 -alias athar
```

> ⚠️ **احفظ هذا الملف وكلمة السرّ في مكان آمن خارج المستودع.**
> فقدانه يعني أنك لن تستطيع ترقية التطبيق أبدًا.

### محليًا: `app/android/key.properties`

```properties
storeFile=/absolute/path/athar-release.jks
storePassword=********
keyAlias=athar
keyPassword=********
```
(مُدرج في `.gitignore` — لا يُرفع أبدًا.)

### في GitHub Actions: ٤ Secrets

اذهب إلى **Settings → Secrets and variables → Actions** وأضف:

| الاسم | القيمة |
|---|---|
| `KEYSTORE_BASE64` | `base64 -i athar-release.jks` (سطر واحد) |
| `KEYSTORE_PASSWORD` | كلمة سر المتجر |
| `KEY_ALIAS` | `athar` |
| `KEY_PASSWORD` | كلمة سر المفتاح |

بدونها: العمل يبني release **غير موقّع** ويكمل (لا يفشل).

---

## ٣. ما يجب ضبطه قبل أول إصدار

### `android/app/build.gradle`

- `compileSdk` **≥ 35** (مطلوب من `flutter_local_notifications` 19)
- `minSdk` **24** على الأقل
- تفعيل **desugaring**:
  ```gradle
  compileOptions {
      coreLibraryDesugaringEnabled true
      sourceCompatibility JavaVersion.VERSION_11
      targetCompatibility JavaVersion.VERSION_11
  }
  dependencies {
      coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:2.1.4'
  }
  ```
- `applicationId` فريد (مثل `com.athar.app`) — لا يمكن تغييره بعد النشر
- `versionCode` / `versionName` في `pubspec.yaml`: `version: 1.0.0+1`

### `android/app/src/main/AndroidManifest.xml`

- `<uses-permission>` الموجودة في `app/README.md` §٢ (الموقع + الإشعارات)
- مستقبل الودجت `<receiver android:name=".AtharWidgetProvider">`
  (يُنسخ من `app/native/android/AndroidManifest.snippet.xml`)
- `android:label="@string/app_name"` واسم التطبيق بالعربية

### أيقونة التطبيق

```bash
flutter pub run flutter_launcher_icons
```
أو استبدل `android/app/src/main/res/mipmap-*/ic_launcher.png` يدويًا.

---

## ٤. الودجت

`home_widget` **لا يرسم الودجت** — الرسم كود أصلي:

```
app/native/android/
├── kotlin/AtharWidgetProvider.kt        ← المنطق
├── res/layout/athar_widget.xml          ← الشكل
├── res/xml/athar_widget_info.xml        ← تعريف النظام
├── res/drawable/athar_widget_bg.xml     ← الخلفية
├── res/values/athar_widget_strings.xml  ← الوصف
├── build.gradle.snippet                 ← التوقيع
└── AndroidManifest.snippet.xml          ← المستقبل
```

`tool/setup_android.sh` ينقلها كلها ويطبع ما يجب لصقه يدويًا.

**قيود `RemoteViews` في أندرويد** (مهمة للتصميم):
- ❌ لا خطوط مرفقة (نستخدم خط النظام داخل الودجت فقط)
- ❌ لا عناصر مخصّصة ولا SVG — مستطيلات ونصوص وألوان ثابتة فقط
- ❌ لا تحديث أسرع من ٣٠ دقيقة تلقائيًا (نتحدّث من Dart عند فتح التطبيق)

---

## ٥. قائمة تحقّق قبل الإطلاق (أسبوع ١٣–١٤)

- [ ] `flutter test` أخضر بالكامل
- [ ] `flutter analyze` بلا تحذيرات
- [ ] APK موقّع ومُثبَّت على جهازين حقيقيين على الأقل (قديم + حديث)
- [ ] الأذان يرنّ في وقته فعلًا على جهاز حقيقي (ليس المحاكي)
- [ ] الودجت يظهر ويُحدَّث بعد فتح التطبيق
- [ ] التطبيق يعمل **بلا إنترنت** (وضع الطيران) — المواقيت والأذكار
- [ ] RTL سليم بالعربية والفرنسية
- [ ] **لا إعلانات ولا تتبّع ولا أي طلب شبكة** (`docs/07` — أثر مجاني للأبد)
- [ ] سياسة خصوصية منشورة تقول بوضوح: «لا نجمع أي بيانات، ولا نطلب حسابًا»
- [ ] لقطات المتجر + وصف بالثلاث لغات

---

## ٦. حِجم APK المتوقّع

| النوع | الحجم التقريبي |
|---|---|
| `app-arm64-v8a-release.apk` | ~18–25 MB |
| `app-release.aab` | ~20–28 MB |
| `app-debug.apk` | ~50–70 MB (يحتوي أدوات التصحيح) |

سيكبر مع إضافة نص القرآن (أسابيع ٥–٨) — حينها نفعّل
`--split-per-abi` + `--obfuscate` + تقسيم الأصول.

---

## أحجام الـ APK المتطابقة ليست خللًا

قد يخرج إصداران متتاليان بحجم **مطابق بالبايت**. حدث بين
٠.١٦/٠.١٧ و٠.١٩/٠.٢٠ و٠.٢٥/٠.٢٦.

السبب: `classes.dex` مُبطَّن إلى حدّ ١٦ كيلوبايت (١٦٬٣٨٤ بايت).
فإضافة كود أقلّ من التبطين المتبقّي **لا تغيّر حجم الملف**، وتجاوزه
يزيده ١٦٬٣٨٤ بايت دفعة واحدة.

| الانتقال | الفرق |
|---|---|
| ٠.١٥←٠.١٦ · ٠.٢٠←٠.٢١ · ٠.٢١←٠.٢٢ · ٠.٢٤←٠.٢٥ | ‪+16,384‬ |
| ٠.١٧←٠.١٨ | ‪+1,524‬ |
| ٠.٢٢←٠.٢٣ | ‪+2,508‬ |
| ٠.٢٣←٠.٢٤ | ‪−4‬ |
| ٠.١٦←٠.١٧ · ٠.١٩←٠.٢٠ · ٠.٢٥←٠.٢٦ | **٠** |

### الدليل

الفرق **السالب** (−4 بين ٠.٢٣ و٠.٢٤) هو الحاسِم: ملف لا يصغر
بإضافة كود، إلا إن كانت أجزاء أخرى (`resources.arsc` والموارد)
تنقص بينما بقي `classes.dex` داخل حدّ التبطين نفسه.

### كيف تتحقّق من جِدّة ملف بلا تنزيله

- **`.published_at`** لا `.created_at`: الأوّل ساعة **انتهاء** العمل
  (بعد ٦–٨ دقائق من الإنشاء)، والثاني ساعة إنشاء كائن الإصدار.
- **معرّف الأصل (`asset.id`)**: مختلف ⇒ رُفع ملف جديد لا أُعيد
  استخدام القديم.

> ⚠️ تنزيل الأصول من `release-assets.githubusercontent.com`
> محجوب في بيئة التطوير، فالطريقتان أعلاه هما المتاح.
