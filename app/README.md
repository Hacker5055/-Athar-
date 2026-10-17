# أثر · Athar — مشروع Flutter (الأسبوعان ١ + ٢)

> حالة：**مكتوبة يدويًا وغير مُجمَّعة بعد** — مفيش Flutter SDK في بيئة التطوير المستخدمة هنا.
> لازم تشغّل الخطوات اللي تحت على جهازك مرة واحدة على الأقل قبل أي تعديل.

هذا مشروع تطبيق **أثر** كما ورد في `docs/06-roadmap.md`:

- **الأسبوع ١** — الأساس + نظام التصميم + الترجمة + الهيكل والتنقّل ✅
- **الأسبوع ٢** — **المواقيت**: الحساب الفلكي + الموقع + المدن + طرق الحساب + المذهب ✅

ما زال placeholder بانتظار أسابيع ٣–٤: الإشعارات والأذان، الورد القرآني، الأذكار، ومتتبع «أثري».

---

## ١. التشغيل لأول مرة

```bash
cd app

# ١) توليد مجلدات المنصّات (android/ · ios/ · web/)
#    الآمن: لا يمسّ lib/ ولا pubspec.yaml، يضيف مجلدات المنصّات الناقصة فقط.
flutter create --platforms=android,ios,web --org com.athar --project-name athar .

# ٢) تثبيت الحزم
flutter pub get

# ٣) تحليل + اختبارات
flutter analyze
flutter test

# ٤) تشغيل
flutter run
```

> `flutter create .` هي الخطوة الوحيدة المفقودة: الملفات الموجودة هنا تغطّي كل ما يخصّ `lib/`،
> ومجلدات المنصّات أفضل ما تُولَّد من SDK نفسه (توقيعات، Gradle، Info.plist).

### تنظيف سريع بعد أول تحليل

بعض قواعد `analysis_options.yaml` صارمة (`require_trailing_commas`، `prefer_const_*`):

```bash
dart fix --apply     # يصلح الأغلبية تلقائيًا
flutter analyze
```

---

## ٢. صلاحيات الموقع (لازم تضاف يدويًا بعد `flutter create`)

`geolocator` مش هيشتغل من غير الإعلان عن الصلاحية في ملفات المنصّة:

**`android/app/src/main/AndroidManifest.xml`** (فوق وسم `<application>`):

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

**`ios/Runner/Info.plist`**:

```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>نحتاج موقعك لحساب مواقيت الصلاة بدقة. الموقع لا يخرج من جهازك.</string>
```

> ⚠️ **لا نطلب الإذن في أول تشغيل أبدًا** — بس لما المستخدم يضغط
> «تحديد موقعي تلقائيًا» (راجع `docs/03` §3.1). والوضع اليدوي (اختيار مدينة)
> شغال بالكامل بدون أي صلاحية.

---

## ٣. ما تحتويه السقالة

```
lib/
├── main.dart                     → يستدعي bootstrap() فقط
├── app/
│   ├── bootstrap.dart            → WidgetsBinding + SharedPreferences + ProviderScope (overrides)
│   ├── app.dart                  → MaterialApp.router (locale · theme · textScaler)
│   └── router.dart               → Routes + routerProvider + StatefulShellRoute (٥ تبويبات)
├── core/
│   ├── l10n/                     → AppLocale + Strings + ٣ حزم (~٢٠٠ مفتاح)
│   ├── settings/                 → AppSettings (JSON) + SettingsController +Providers
│   ├── theme/                    → ألوان · مسافات · خطوط · ثيم فاتح/داكن · ثيم القراءة
│   ├── utils/                    → أرقام عربية-هندية · تحية · مفاتيح الصلوات
│   └── widgets/                  → ٩ مكوّنات مشتركة + widgets.dart
└── features/
    ├── shell/                    → AppShell (شريط سفلي + StatefulNavigationShell)
    ├── onboarding/               → اختيار اللغة فقط (مفيش أذونات — docs/03 §3.1)
    ├── home/                     → الشاشة الرئيسية (بيانات placeholder)
    ├── quran · adhkar · qibla/   → شاشات «قريبًا» (أسابيع ٢–٣)
    ├── more/                     → المزيد + معرض نظام التصميم
    ├── settings/                 → اللغة · الثيم · حجم النص · الأرقام العربية
    └── shared/                   → ComingSoonView
```

### نظام التصميم

منقول حرفيًا من `docs/04-design-system.md` (الاتجاه المختار: **العمارة والضوء**):

| الملف | المحتوى |
|---|---|
| `core/theme/app_colors.dart` | ١٨ لونًا رمزيًا `AppColorSet` للفاتح والداكن |
| `core/theme/app_spacing.dart` | `AppSpacing` (4→48) · `AppRadius` · `AppDuration` · `AppCurve` · `AppSize` |
| `core/theme/app_typography.dart` | سلم ٨ أنماط + نمط القرآن (22sp افتراضي، حتى 40sp، تباعد 2.15) |
| `core/theme/app_theme.dart` | `AppTheme.light()/dark()` + `context.colors` / `context.textStyles` / `context.isRtl` |
| `core/theme/quran_theme.dart` | ثيمات القراءة: ورق · Sepia · ليلي (مستقل عن ثيم التطبيق) |

الوصول من أي مكان:

```dart
final c = context.colors;      // ألوان الثيم الحالي
final t = context.textStyles;  // أنماط النص
final s = ref.watch(stringsProvider);  // ترجمة اللغة الحالية
```

### المكوّنات (`core/widgets/widgets.dart`)

`Gap` · `VSpace` · `HSpace` · `AtharButton` · `AtharButtonVariant` · `AtharIconButton` ·
`AtharCard` · `SectionHeader` · `AtharSettingsTile` · `ForwardChevron` · `AtharChip` ·
`AtharBadge` · `showAtharSheet` · `AtharEmptyState` · `AtharTextField` ·
`ProgressRing` · `ProgressBar` · `WirdRow`

### التنقّل

| المسار | الشاشة |
|---|---|
| `/onboarding` | اختيار اللغة (مرة واحدة) |
| `/home` · `/quran` · `/adhkar` · `/qibla` · `/more` | التبويبات الخمسة |
| `/settings` · `/design-system` | صفحات ثانوية من «المزيد» |

`StatefulShellRoute.indexedStack` يحفظ حالة كل تبويب (موضع التمرير، البحث، إلخ).
النقر على التبويب النشط يُعيده لأوله. بوابة: من لم يُكمل الإعداد يُوجَّه إلى `/onboarding`.

---

## ٤. المواقيت (أسبوع ٢)

```
features/prayer/
├── domain/                 ← صفر اعتماد على أي حزمة خارجية
│   ├── entities/           Place · GeoPoint · PrayerSettings · PrayerDay
│   ├── prayer_methods.dart ١٥ طريقة حساب معرّفة **بزواياها صريحةً**
│   ├── prayer_times_engine.dart   واجهة المحرك
│   └── location_repository.dart   واجهة الموقع + LocationAccessStatus
├── data/                   ← التنفيذ
│   ├── adhan_dart_engine.dart     ⚠️ الملف الوحيد اللي يستورد adhan_dart
│   ├── geolocator_location_repository.dart
│   ├── device_timezone.dart       flutter_timezone
│   ├── timezone_service.dart      IANA عبر حزمة timezone
│   └── cities.dart                ~٦٥ مدينة مرفقة (أوفلاين)
├── application/            ← Riverpod
│   ├── prayer_settings_controller.dart  (يُحفظ تحت athar.prayer.v1)
│   ├── prayer_providers.dart
│   └── location_controller.dart
└── presentation/
    ├── pages/    prayer_settings_page · city_picker_page
    └── widgets/  next_prayer_card · prayer_times_list
```

### قرارات تقنية مهمة

| القرار | السبب |
|---|---|
| **كل الأوقات UTC instants جوه `PrayerDay`** | المقارنة مع `DateTime.now()` تبقى صحيحة؛ التحويل للعرض بيحصل عند العرض فقط |
| **الطرق معرّفة بالزوايا، مش بالـ presets** | أي preset جاهز قد يحمل «تعديلات خاصة بالطريقة»؛ وكمان نضمن تطابقها مع البروتوتايب |
| **التعديل اليدوي بالدقائق نطبّقه إحنا** | أوضح من `PrayerAdjustments`، وبيوفّر علينا تعامل مع API متغيّر |
| **ما استخدمنا `currentPrayer/nextPrayer` من الحزمة** | نوع الراجع اتغيّر بين إصداراتها (String ← Prayer)؛ حسبناهم إحنا في `PrayerDay` |
| **~٦٥ مدينة مرفقة كبذرة** | أوفلاين أولًا؛ التوسعة لاحقًا بملف JSON |
| **المنطقة الزمنية من IANA مش من إزاحة ثابتة** | عشان التوقيت الصيفي (DST) ما يبوّظش المواقيت |

### ⚠️ نقاط التحقّق عند أول كومبايل

السقالة مكتوبة يدويًا (مافيش Flutter SDK في بيئة التطوير)، فلازم تتحقّق من
دي عند أول `flutter test` — وكلها متجمّعة في **ملف واحد**:

`lib/features/prayer/data/adhan_dart_engine.dart`

1. `CalculationMethodParameters.other()` — لو مش موجود بالإصدار، استبدله بـ
   `CalculationMethodParameters.muslimWorldLeague()` **وراجع الاختبارات**
   (MWL قد يحمل تعديلات خاصة بالطريقة).
2. أسماء الخصائص `fajrAngle · ishaAngle · ishaInterval · maghribAngle · madhab · highLatitudeRule`.
3. `Madhab.hanafi / Madhab.shafi` و `HighLatitudeRule.*`.
4. `prayerTimes.fajr` إلخ — الكود متدرّج على الحالتين (nullable ولا لأ).

---

## ٥. الاختبارات

```bash
flutter test
```

| الملف | يغطّي |
|---|---|
| `test/core/utils/arabic_numbers_test.dart` | تحويل الأرقام، الفواصل، تنسيق العدّاد |
| `test/core/l10n/strings_test.dart` | **تطابق مفاتيح اللغات الثلاث**، الرجوع للعربية، `tArgs` |
| `test/core/settings/app_settings_test.dart` | JSON ذهابًا/إيابًا، سقوط آمن عند بيانات تالفة |
| `test/core/widgets/athar_button_test.dart` | نقر الزر، الحالة المعطّلة، التحميل، هدف 48px |
| `test/core/utils/search_text_test.dart` | تطبيع البحث العربي (تشكيل، همزات، تاء مربوطة) واللاتيني |
| `test/features/prayer/adhan_engine_test.dart` | **١٢ حالة مرجعية + ٦ ثوابت فلكية** — أهم اختبار في المشروع |
| `test/features/prayer/prayer_day_test.dart` | الحالي/الجاي، فجر الغد، منتصف الليل الإسلامي |
| `test/features/prayer/prayer_settings_test.dart` | JSON ذهابًا/إيابًا، تلف، تقييد التعديلات ±٦٠ |
| `test/features/prayer/cities_test.dart` | تحليل كل الصفوف، البحث بالثلاث لغات، المسافة والاتجاه |

### 🔬 `adhan_engine_test.dart` — شبكة الأمان الحقيقية

القيم المرجعية فيه **مولّدة من كود البروتوتايب** (`prototype/assets/js/calc.js`)
بنفس الإحداثيات والطرق، بالتوقيت العالمي. اتأكدنا أول إن `calc.js` يطلّع
نفس القيم المتحقق منها سابقًا للقاهرة ٢٠٢٦-١٠-١٧ (مصرية/شافعي، بتوقيت مصر +٣):
`05:32 06:58 12:40 15:55 18:22 19:39`.

**التفاوت المسموح ٣ دقائق** — لأن `calc.js` يستخدم صيغة PrayTimes المبسّطة
وAdhan يستخدم صيغ Meeus الأدق (الفرق الطبيعي ٠–٢ دقيقة). والهدف من الاختبار
مش مطابقة الخوارزميات، إنما **صيد سوء استخدام الـ API**: طريقة غلط، مذهب غلط،
أو تحويل منطقة زمنية غلط — ودي أخطاء بعشرات الدقائق.

زيادة على كده فيه **ثوابت لا تكسرها أي طريقة** (ما تحتاجش قيم مرجعية):
الترتيب بين الصلوات، الظهر في منتصف الطريق بين الشروق والمغرب،
العصر الحنفي بعد الشافعي، زاوية فجر أكبر = فجر أبكر،
أم القرى: العشاء = المغرب + ٩٠ دقيقة بالظبط، والتعديل اليدوي يزحّف بالدقيقة.

اختبار تطابق مفاتيح الترجمة هو كمان مهم: أي مفتاح يُضاف لحزمة ويُنسى في الأخرى سيفشل فورًا.

---

## ٦. ما **ليس** موجودًا بعد (خريطة الأسابيع)

| الأسبوع | المحتوى | الحالة |
|---|---|---|
| ٢ | المواقيت: الحساب الفلكي · الموقع · المدن · الطرق · المذهب | ✅ **تم** |
| ٣ | الإشعارات: الأذان · التذكير · أصوات · قنوات | ⬜ التالي — `flutter_local_notifications`, `timezone`, `just_audio` |
| ٤ | الشاشة الرئيسية + الودجت الأساسي + «أثري» v1 | ⬜ `home_widget`, `shared_preferences` |
| ٥–٨ | القرآن: Drift + FTS5 + نص عثماني + تلاوة + بحث | ⬜ `drift`, `sqlite3_flutter_libs`, `dio`, `just_audio`, `audio_service` |
| ٩–١١ | أذكار · تسبيح · قبلة (**تصحيح الميل المغناطيسي!**) | ⬜ `flutter_compass`, `sensors_plus` |
| ١٢–١٤ | التلميع والهجري والإطلاق | ⬜ راجع `docs/06` |

⚠️ **تذكير تقني من البحث:** زاوية القبلة تُقاس من **الشمال الحقيقي**، وقراءة المغناطيس تُعطي **الشمال المغناطيسي**.
لازم إضافة *زاوية الميل المغناطيسي* (declination) قبل المقارنة — راجع `docs/05` وملاحظات `qibla_math`.

---

## ٧. ملاحظات مهمة

- **الخطوط:** السقالة تستخدم `google_fonts` مؤقتًا (`AppTypography.useGoogleFonts = true`).
  للإطلاق: حمّل الخطوط، ضعها في `assets/fonts/`، فعّل بلوك `fonts:` في `pubspec.yaml`،
  وبدّل العلم إلى `false` — راجع `assets/fonts/README.md` (فيه جدول التراخيص؛
  **خط المصحف العثماني ليس OFL** ويحتاج مراجعة `docs/05` §3).
- **التراخيص:** لا تستخدم Quran.com API v4 تجاريًا. راجع `docs/05` §3 قبل أي مصدر بيانات.
- **حجم النص:** `AtharApp` يقيّد مقياس نص النظام بين 0.85× و2× — لا يكسر التصميم ولا يمنع التكبير.
- **الإعدادات:** كائن واحد `AppSettings` محفوظ كـ JSON تحت مفتاح `athar.settings.v1`،
  وإعدادات الصلاة منفصلة تحت `athar.prayer.v1`.
  `sharedPreferencesProvider` و`settingsProvider` يرميان `UnimplementedError` بدون override في `bootstrap()`.
- **لو `flutter pub get` فشل على إصدار حزمة:** القيود مقصودة إنها واسعة بس محافظة
  (مثلًا `geolocator: ^13.0.2` بدل ١٤ لأن ١٤ يطلب Flutter ≥ ٣.٢٩).
  لو أردت أحدث إصدار متاح: `flutter pub upgrade --major-versions`.

---

## ٨. المراجع

- `../docs/04-design-system.md` — مصدر الحقيقة للألوان والخطوط والمكوّنات
- `../docs/05-technical-plan-flutter.md` — المعمارية، الحزم، التراخيص
- `../docs/06-roadmap.md` — خطة الـ١٤ أسبوعًا
- `../prototype/` — النموذج التفاعلي (١٤ شاشة) الذي نُقلت منه الترجمات والألوان
