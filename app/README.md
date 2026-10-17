# أثر · Athar — سقالة Flutter (الأسبوع ١)

> حالة：**مكتوبة يدويًا وغير مُجمَّعة بعد** — مفيش Flutter SDK في بيئة التطوير المستخدمة هنا.
> لازم تشغّل الخطوات اللي تحت على جهازك مرة واحدة على الأقل قبل أي تعديل.

هذه هي سقالة تطبيق **أثر** كما وردت في `docs/06-roadmap.md` (الأسبوع ١: الأساس + نظام التصميم + الترجمة + الهيكل والتنقّل).
البيانات الحقيقية (حساب الصلاة، نص القرآن، الأذكار، البوصلة) تأتي في الأسابيع ٢–٣ — السقالة **بلا بيانات عن قصد**.

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

## ٢. ما تحتويه السقالة

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

## ٣. الاختبارات

```bash
flutter test
```

| الملف | يغطّي |
|---|---|
| `test/core/utils/arabic_numbers_test.dart` | تحويل الأرقام، الفواصل، تنسيق العدّاد |
| `test/core/l10n/strings_test.dart` | **تطابق مفاتيح اللغات الثلاث**، الرجوع للعربية، `tArgs` |
| `test/core/settings/app_settings_test.dart` | JSON ذهابًا/إيابًا، سقوط آمن عند بيانات تالفة |
| `test/core/widgets/athar_button_test.dart` | نقر الزر، الحالة المعطّلة، التحميل، هدف 48px |

اختبار تطابق المفاتيح هو الأهم: أي مفتاح يُضاف لحزمة ويُنسى في الأخرى سيفشل فورًا.

---

## ٤. ما **ليس** موجودًا بعد (خريطة الأسابيع)

| الأسبوع | المحتوى | الحزم المتوقّعة |
|---|---|---|
| ٢ | الصلاة: `adhan_dart` + `geolocator` + إشعارات | `adhan_dart`, `geolocator`, `geocoding`, `flutter_local_notifications`, `timezone` |
| ٣ | القرآن: `Drift` + FTS5 + بيانات Uthmani | `drift`, `sqlite3_flutter_libs`, `dio`, `just_audio`, `audio_service` |
| ٤ | أذكار · تسبيح · قبلة (تصحيح الميل المغناطيسي!) | `flutter_compass`, `sensors_plus`, `shared_preferences` |
| ٥–١٤ | المتتبع «أثري»، التقويم الهجري، النسخ الاحتياطي، الإطلاق | راجع `docs/06` |

⚠️ **تذكير تقني من البحث:** زاوية القبلة تُقاس من **الشمال الحقيقي**، وقراءة المغناطيس تُعطي **الشمال المغناطيسي**.
لازم إضافة *زاوية الميل المغناطيسي* (declination) قبل المقارنة — راجع `docs/05` وملاحظات `qibla_math`.

---

## ٥. ملاحظات مهمة

- **الخطوط:** السقالة تستخدم `google_fonts` مؤقتًا (`AppTypography.useGoogleFonts = true`).
  للإطلاق: حمّل الخطوط، ضعها في `assets/fonts/`، فعّل بلوك `fonts:` في `pubspec.yaml`،
  وبدّل العلم إلى `false` — راجع `assets/fonts/README.md` (فيه جدول التراخيص؛
  **خط المصحف العثماني ليس OFL** ويحتاج مراجعة `docs/05` §3).
- **التراخيص:** لا تستخدم Quran.com API v4 تجاريًا. راجع `docs/05` §3 قبل أي مصدر بيانات.
- **حجم النص:** `AtharApp` يقيّد مقياس نص النظام بين 0.85× و2× — لا يكسر التصميم ولا يمنع التكبير.
- **الإعدادات:** كائن واحد `AppSettings` محفوظ كـ JSON تحت مفتاح `athar.settings.v1`.
  `sharedPreferencesProvider` و`settingsProvider` يرميان `UnimplementedError` بدون override في `bootstrap()`.

---

## ٦. المراجع

- `../docs/04-design-system.md` — مصدر الحقيقة للألوان والخطوط والمكوّنات
- `../docs/05-technical-plan-flutter.md` — المعمارية، الحزم، التراخيص
- `../docs/06-roadmap.md` — خطة الـ١٤ أسبوعًا
- `../prototype/` — النموذج التفاعلي (١٤ شاشة) الذي نُقلت منه الترجمات والألوان
