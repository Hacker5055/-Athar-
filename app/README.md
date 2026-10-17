# أثر · Athar — مشروع Flutter (الأسابيع ١–٤)

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

## ٢. الصلاحيات (لازم تضاف يدويًا بعد `flutter create`)

### ٢.١ الموقع

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

### ٢.٢ الإشعارات والأذان (أسبوع ٣)

**`android/app/src/main/AndroidManifest.xml`**:

```xml
<!-- أندرويد ١٣+: إذن الإشعارات -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<!-- الجدولة الدقيقة (الأذان في ثانيته) — يُمنح من المستخدم، وبيُسحب أحيانًا -->
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<!-- إعادة الجدولة بعد إعادة التشغيل -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
```

> بديل `SCHEDULE_EXACT_ALARM` هو `USE_EXACT_ALARM`: **لا يطلب إذن المستخدم**
> لكن جوجل بيراجعه يدويًا وقت النشر ويشترط إن التطبيق منبّه/تقويم.
> اخترنا `SCHEDULE_EXACT_ALARM` (أسبوع ٣) لأن الأذان **منبّه ديني** —
> والاستخدام واضح ومبرّر في سياسة جوجل. الكود بيتحمّل غيابها: بيسقط على
> `inexactAllowWhileIdle` وبيعرض banner للمستخدم بدل ما يكراش.

**`ios/Runner/Info.plist`** — لا شيء إضافي (الإذن بيُطلب وقت التشغيل).

> ⚠️ **ثلاثة أشياء لازم تتأكد منها على أندرويد:**
> 1. `compileSdk 35` على الأقل (مطلوب من `flutter_local_notifications` ١٩).
> 2. تفعيل **desugaring** (حتى لو مش بتستخدم الجدولة) — وإلا البناء يفشل.
> 3. على أجهزة **Samsung / Xiaomi / Huawei**، إدارة البطارية العدوانية تقدر
>    تقتل التنبيهات رغم كل الصلاحيات — الشاشة بتعرض banner توجيهي.

---

## ٣. ما تحتويه السقالة

```
lib/
├── main.dart                     → يستدعي bootstrap() فقط
├── app/
│   ├── bootstrap.dart            → WidgetsBinding + SharedPreferences + ProviderScope (overrides)
│   ├── app.dart                  → MaterialApp.router + مراقب دورة الحياة (إعادة الجدولة)
│   └── router.dart               → Routes + routerProvider + StatefulShellRoute (٥ تبويبات)
├── core/
│   ├── l10n/                     → AppLocale + Strings + ٣ حزم (٢٥٤ مفتاحًا)
│   ├── settings/                 → AppSettings (JSON) + SettingsController +Providers
│   ├── theme/                    → ألوان · مسافات · خطوط · ثيم فاتح/داكن · ثيم القراءة
│   ├── utils/                    → أرقام عربية-هندية · تحية · مفاتيح الصلوات
│   └── widgets/                  → ٩ مكوّنات مشتركة + widgets.dart
└── features/
    ├── shell/                    → AppShell (شريط سفلي + StatefulNavigationShell)
    ├── onboarding/               → اختيار اللغة فقط (مفيش أذونات — docs/03 §3.1)
    ├── home/                     → الشاشة الرئيسية (بيانات placeholder)
    ├── quran · adhkar · qibla/   → شاشات «قريبًا» (أسابيع ٢–٣)
    ├── athary/                   → «أثري» المتتبع: ٨ عبادات · سلسلة · شبكة ٣٠ يوم
    ├── quran/                    → الأسبوع ٥: قاعدة + استيراد + بحث (domain/data/application)
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

## ٤. المواقيت · الإشعارات · «أثري» (أسابيع ٢ + ٣ + ٤)

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
    ├── pages/    prayer_settings_page · city_picker_page · notifications_page
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

### ⚠️ نقاط التحقّق عند أول كومبايل (أسبوع ٢)

السقالة مكتوبة يدويًا (مافيش Flutter SDK في بيئة التطوير)، فلازم تتحقّق من
دي عند أول `flutter test` — وكلها متجمّعة في **ملف واحد**:

`lib/features/prayer/data/adhan_dart_engine.dart`

1. `CalculationMethodParameters.other()` — لو مش موجود بالإصدار، استبدله بـ
   `CalculationMethodParameters.muslimWorldLeague()` **وراجع الاختبارات**
   (MWL قد يحمل تعديلات خاصة بالطريقة).
2. أسماء الخصائص `fajrAngle · ishaAngle · ishaInterval · maghribAngle · madhab · highLatitudeRule`.
3. `Madhab.hanafi / Madhab.shafi` و `HighLatitudeRule.*`.

### ٤.١ الإشعارات والأذان (أسبوع ٣)

```
features/prayer/
├── domain/
│   ├── alert_id.dart                 معرّف إشعار حتمي (deterministic)
│   ├── notification_settings.dart    PrayerAlertMode · AdhanSound · NotificationSettings
│   └── notification_planner.dart     «متى» فقط — محض بالكامل وقابل للاختبار
├── data/
│   └── notification_service.dart     الغلاف الوحيد حول flutter_local_notifications
├── application/
│   ├── notification_settings_controller.dart  (يُحفظ تحت athar.notifications.v1)
│   └── notification_controller.dart           الخطة · الصلاحيات · المزامنة
└── presentation/
    └── pages/notifications_page.dart          شاشة الإعدادات الكاملة
```

**الفصل بين «متى» و«ماذا»** — أهم قرار معماري في الأسبوع:

| الطبقة | المسؤولية | تعتمد على حزم؟ |
|---|---|---|
| `notification_planner.dart` | يحسب **متى** يُطلق كل تنبيه | ❌ صفر — محض ١٠٠٪ |
| `notification_service.dart` | يبني **ماذا** (العنوان · النص · القناة · الصوت) ويجدوله | ✅ flutter_local_notifications |

السبب: المنطق اللي يهمّ المستخدم (توقيت الأذان، التذكير، الجمعة) يتختبر
بدون محاكاة أي منصّة، والكود المعرّض لتغيّر الـ API يبقى محصورًا في ملف واحد.

**معرّف الإشعار حتمي** — `kind × 1,000,000 + dayEpoch × 10 + prayerSlot`
(الأساس ٢٠٠٠-٠١-٠١). يعني نقدر نلغي/نحدّث تنبيهًا بعينه من غير خريطة محفوظة
في التخزين، والأقصى ≈ ٣ ملايين (بعيد عن حدّ `int` في أندرويد = ٢.١ مليار).

**ثلاث قنوات أندرويد منفصلة** (`athan` · `reminder` · `jumuah`): عشان المستخدم
يكتّم «التذكير» ويخلّي «الأذان» شغال من إعدادات النظام نفسه، بدون شاشة صلاحيات منّا.

| القرار | السبب |
|---|---|
| **إعادة جدولة كاملة (cancelAll ثم جدولة) بدل حساب فارق** | عدد التنبيهات صغير (~٤٠) وأبسط بكتير من تتبّع الحالة |
| **سقف ٦٠ تنبيهًا معلّقًا** | iOS يسمح بـ ٦٤ فقط ويسقط الزيادة **بصمت**؛ نحتفظ بالأقرب زمنيًا |
| **أفق ٧ أيام** | إعادة الجدولة بتحصل عند فتح التطبيق وعند الرجوع للمقدمة |
| **الشروق مستحيل له أذان** | ليس صلاة — مقفول في المخطّط **وفي** `modeFor()` (خطّان للدفاع) |
| **لا تذكير لصلاة موقوفة (`off`)** | لو المستخدم أوقف العشاء، التذكير إزعاج بلا معنى |
| **صوت الأذان `null` حتى إشعار آخر** | لا نُمرّر مسار ملف غير مرفق أبدًا — أندرويد بيسقط الإشعار كله |
| **الجدولة الدقيقة اختيارية** | `canScheduleExactNotifications()` وإلا `inexactAllowWhileIdle` |
| **`flutter_local_notifications` مثبّت على ١٩.x** | ٢٠ يطلب Flutter ٣.٣٢ و٢١ يطلب ٣.٣٨، وحدّنا `sdk: ^3.5.0` |

#### ⚠️ نقاط التحقّق عند أول كومبايل (أسبوع ٣)

**تم التحقّق من كل واحدة مقابل توثيق ١٩.٥.٠ على pub.dev** — لكن راجعها لو غيّرت الإصدار:

1. `zonedSchedule(id, title, body, TZDateTime, NotificationDetails, {required androidScheduleMode, payload, matchDateTimeComponents})`
   — **موضعي** في ١٩.x. الإصدار ٢٠ حوّلها لمعلمات مسماة.
2. **لا يوجد** `uiLocalNotificationDateInterpretation` في ١٩ (أُضيف في ١٨ وحُذف في ١٩).
3. `AndroidNotificationChannel(id, name, {description, …})` — `description` **مش** `channelDescription`.
4. `IOSFlutterLocalNotificationsPlugin.checkPermissions()` بيرجّع `NotificationsEnabledOptions?` **مش** `bool` — ما استخدمناهش.

> **الأصوات:** كل الأصوات في `AdhanSounds` عندها `bundled: false` و`assetPath`
> كمرجع فقط. لإضافة أذان حقيقي: ضع الملف في
> `android/app/src/main/res/raw/adhan_makkah.mp3` (مورد خام، **مش** أصل Flutter)،
> ثم غيّر `bundled: true` و`resourceName`. الأصول المُعلنة في `pubspec.yaml`
> **لا تعمل** مع إشعارات أندرويد. راجع `assets/audio/README.md` و`docs/05` §3.3 أولًا.

### ٤.٢ «أثري» — المتتبع (أسبوع ٤)

```
features/athary/
├── domain/                 ← محض ١٠٠٪ (لا حزم، لا منصّة)
│   ├── habit.dart          ٨ عبادات ثابتة
│   ├── daily_record.dart   يوم واحد + مستويات الكثافة + مفاتيح التاريخ
│   └── tracker_state.dart  السلسلة · الشبكة · الاقتراح · التقليم
├── data/tracker_repository.dart        (يُحفظ تحت athar.tracker.v1)
├── application/tracker_controller.dart
└── presentation/
    ├── pages/athary_page.dart
    └── widgets/athary_widgets.dart · athary_home_card.dart
```

| القرار | السبب |
|---|---|
| **٨ عبادات ثابتة في v1** | التخصيص يفتح باب تصميم كبير (أهداف · أيقونات · ترتيب) — مؤجّل لما بعد الإطلاق |
| **اليوم = يوم مدني محلي** | اليوم العبادي (من المغرب) يفتح أسئلة صعبة؛ مُسجّل كمرشّح بعد الإطلاق |
| **قاعدة التسامح في السلسلة** | لو اليوم فاضي نعدّ من إمبارح — بدونها تظهر السلسلة «صفر» كل صباح |
| **اليوم الفارغ لا يُخزّن** | أوضح في التصحيح وأصغر في التخزين |
| **التقليم عند ٤٠٠ يوم** | يمنع نمو `SharedPreferences` بلا حدّ على جهاز المستخدم |
| **`previousDay` بالبناء لا بـ `subtract`** | `subtract(Duration(days:1))` = ٢٤ ساعة بالضبط ≠ يوم مدني في التوقيت الصيفي |
| **اللون مش الإشارة الوحيدة** | كل خلية لها `tooltip` بالتاريخ والعدد + عدّاد نصّي فوق (إمكانية الوصول) |

### ٤.٣ الودجت (أسبوع ٤)

`home_widget` **لا يرسم الودجت** — هو قناة بيانات فقط؛ الرسم كود أصلي
في `native/android/` ويُنسخ بـ `tool/setup_android.sh`.

```
data/home_widget_service.dart  ← يكتب: next_name · next_time · next_in · times · place
native/android/                ← Kotlin + layout + xml + manifest snippet
```

يُحدَّث عند: فتح التطبيق · الرجوع للمقدمة · إكمال أول تشغيل.

**قيود `RemoteViews`:** لا خطوط مرفقة (خط النظام فقط داخل الودجت)،
لا عناصر مخصّصة ولا SVG، ولا تحديث تلقائي أسرع من ٣٠ دقيقة.

---

## ٦. الاختبارات

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
| `test/features/prayer/alert_id_test.dart` | **حتمية المعرّف**، فرادته عبر ٧×٦×٣، فك الترميز، حدود int32 |
| `test/features/prayer/notification_planner_test.dart` | «متى» بالكامل: الشروق · ما فات · التذكير · لفّ الجمعة · الفرادة |
| `test/features/prayer/notification_settings_test.dart` | JSON ذهابًا/إيابًا، تلف، تقييد ٣٠–٢٤٠، **وأن لا صوت مُرفق** |
| `test/features/athary/tracker_state_test.dart` | السلسلة (وقاعدة التسامح) · الشبكة · التقليم · JSON · عبور السنة الكبيسة |

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

## ٧. ما **ليس** موجودًا بعد (خريطة الأسابيع)

| الأسبوع | المحتوى | الحالة |
|---|---|---|
| ٢ | المواقيت: الحساب الفلكي · الموقع · المدن · الطرق · المذهب | ✅ **تم** |
| ٣ | الإشعارات: الأذان · التذكير · تذكير الجمعة · القنوات | ✅ **تم** (الأصوات مؤجّلة لحقوق الترخيص) |
| ٤ | الشاشة الرئيسية + الودجت الأساسي + «أثري» v1 | ✅ **تم** — `home_widget`, `shared_preferences` |
| ٦–٨ | القرآن: شاشة القارئ · تلاوة · تفسير | ⬜ `dio`, `just_audio`, `audio_service` |
| — | ✅ **تم في ٥**: Drift + FTS5 + نص عثماني + استيراد + بحث | ✅ `drift`, `drift_flutter`, `sqlite3_flutter_libs` |
| ٩–١١ | أذكار · تسبيح · قبلة (**تصحيح الميل المغناطيسي!**) | ⬜ `flutter_compass`, `sensors_plus` |
| ١٢–١٤ | التلميع والهجري والإطلاق | ⬜ راجع `docs/06` |

⚠️ **تذكير تقني من البحث:** زاوية القبلة تُقاس من **الشمال الحقيقي**، وقراءة المغناطيس تُعطي **الشمال المغناطيسي**.
لازم إضافة *زاوية الميل المغناطيسي* (declination) قبل المقارنة — راجع `docs/05` وملاحظات `qibla_math`.

---

## ٨. ملاحظات مهمة

- **الخطوط:** السقالة تستخدم `google_fonts` مؤقتًا (`AppTypography.useGoogleFonts = true`).
  للإطلاق: حمّل الخطوط، ضعها في `assets/fonts/`، فعّل بلوك `fonts:` في `pubspec.yaml`،
  وبدّل العلم إلى `false` — راجع `assets/fonts/README.md` (فيه جدول التراخيص؛
  **خط المصحف العثماني ليس OFL** ويحتاج مراجعة `docs/05` §3).
- **التراخيص:** لا تستخدم Quran.com API v4 تجاريًا. راجع `docs/05` §3 قبل أي مصدر بيانات.
- **حجم النص:** `AtharApp` يقيّد مقياس نص النظام بين 0.85× و2× — لا يكسر التصميم ولا يمنع التكبير.
- **الإعدادات:** كائن واحد `AppSettings` محفوظ كـ JSON تحت مفتاح `athar.settings.v1`،
  وإعدادات الصلاة منفصلة تحت `athar.prayer.v1`، وإعدادات الإشعارات تحت `athar.notifications.v1`.
  (ثلاثة مفاتيح عن قصد: تلف واحد ما يبوّظش الباقي.)
  `sharedPreferencesProvider` و`settingsProvider` يرميان `UnimplementedError` بدون override في `bootstrap()`.
- **لو `flutter pub get` فشل على إصدار حزمة:** القيود مقصودة إنها واسعة بس محافظة
  (مثلًا `geolocator: ^13.0.2` بدل ١٤ لأن ١٤ يطلب Flutter ≥ ٣.٢٩،
  و`flutter_local_notifications: ^19.4.2` بدل ٢٢ لأن ٢٠+ يطلب Flutter ≥ ٣.٣٢).
  لو أردت أحدث إصدار متاح: `flutter pub upgrade --major-versions`.

---

## ٨½. القرآن (الأسبوع ٥)

**الكود جاهز — البيانات ليست في المستودع.** نص القرآن لا يُرفع إلى git
(قرار ترخيصي، راجع `docs/09` §1)، فالتطبيق يبدأ بقاعدة فارغة ويستوردها
من ملف.

```bash
# ١) تنزيل + تحويل — لا يلمس المستودع
./tool/fetch_quran_data.sh --with-translations --out-dir ~/athar-data

# ٢) الاستيراد من داخل التطبيق (يتحقّق من الرخصة أولًا)
```

| المسار | ما يفعله |
|---|---|
| `lib/features/quran/domain/quran_text.dart` | تطبيع النص (يشيل التشكيل، يوحّد أ/إ/آ/ٱ) |
| `lib/features/quran/domain/search_query.dart` | تصنيف الاستعلام + بناء تعبير FTS5 |
| `lib/features/quran/domain/quran_repository.dart` | الواجهة — `domain` لا يستورد Drift ولا Flutter |
| `lib/features/quran/data/database/app_database.dart` | الجداول + `ayahs_fts` / `translations_fts` |
| `lib/features/quran/data/import/import_payload.dart` | قراءة + **تدقيق** ملف الاستيراد |
| `lib/features/quran/data/import/quran_importer.dart` | إدخال مجمّع في معاملة واحدة |
| `tool/fetch_quran_data.sh` · `tool/convert_quran_json.py` | التنزيل والتحويل |

**⚠️ خطوة مطلوبة قبل أول تشغيل:** ملف `app_database.g.dart` **مولّد
وغير مُرفق** (مستثنى في `.gitignore`). شغّل:

```bash
flutter pub get
dart run build_runner build --delete-conflicting-outputs
```

> التوثيق الكامل — شكل الملف، جدول الرخص، الفجوات المعروفة — في
> **[`docs/09-quran-data.md`](../docs/09-quran-data.md)**.

---

## ٩. بناء الـ APK

**أسهل طريقة (بدون تثبيت Flutter):** ادفع أي commit → تبويب **Actions** →
نزّل `athar-apk-<sha>` من **Artifacts**. الملف موجود مسبقًا في
`.github/workflows/build-apk.yml`.

**محليًا:**
```bash
flutter create --platforms=android --org com.athar --project-name athar .
./tool/setup_android.sh     # ينقل الودجت + يضبط التوقيع
./tool/build_apk.sh --release
```

| الناتج | الاستخدام |
|---|---|
| `app-arm64-v8a-release.apk` | أي هاتف أندرويد حديث (٢٠١٩+) |
| `app-debug.apk` | يعمل على الكل، أكبر حجمًا |
| `app-release.aab` | متجر جوجل |

**التوقيع:** أنشئ `android/key.properties` محليًا، أو ارفع
`KEYSTORE_BASE64` · `KEYSTORE_PASSWORD` · `KEY_ALIAS` · `KEY_PASSWORD`
كـ Secrets في GitHub. بدونها يبني release **غير موقّع** (للتجربة فقط).

> التفاصيل الكاملة — التوقيع، `compileSdk 35`، desugaring، الأيقونة،
> وقائمة التحقّق قبل الإطلاق — في **[`docs/08-apk-and-release.md`](../docs/08-apk-and-release.md)**.


- `../docs/04-design-system.md` — مصدر الحقيقة للألوان والخطوط والمكوّنات
- `../docs/05-technical-plan-flutter.md` — المعمارية، الحزم، التراخيص
- `../docs/06-roadmap.md` — خطة الـ١٤ أسبوعًا
- `../docs/09-quran-data.md` — بيانات القرآن: الشكل · المصادر · الاستيراد
- `../prototype/` — النموذج التفاعلي (١٤ شاشة) الذي نُقلت منه الترجمات والألوان
