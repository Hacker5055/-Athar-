# الخطوط المطلوبة

السقالة دلوقتي بتستخدم حزمة `google_fonts` (بتنزّل الخط مرة واحدة بتخزين مؤقت).
للإطلاق **لازم** نرفق الخطوط جوه التطبيق لسببين:

1. **أوفلاين أولًا** — مبدأ أساسي في `docs/05`.
2. **رسم المصحف** — النص القرآني محتاج خط عثماني حقيقي، مش خط نسخ عادي.

---

## القائمة

| العائلة | الاستخدام | المصدر | الترخيص |
|---|---|---|---|
| **UthmanicHafs** (أو `me_quran`) | النص القرآني | [quran.com fonts](https://fonts.quran.com/) / بديل: `me_quran` من naskh | راجع ترخيص المصدر بدقة قبل الإطلاق |
| **Amiri** | العناوين (وزن 400/700) | [Google Fonts](https://fonts.google.com/specimen/Amiri) | OFL |
| **Amiri Quran** | بديل للنص القرآني لو مش عايز UthmanicHafs | [Google Fonts](https://fonts.google.com/specimen/Amiri+Quran) | OFL |
| **IBM Plex Sans Arabic** | نص الواجهة (300/400/500/600/700) | [Google Fonts](https://fonts.google.com/specimen/IBM+Plex+Sans+Arabic) | OFL |
| **Inter** | النص اللاتيني (EN/FR) | [Google Fonts](https://fonts.google.com/specimen/Inter) | OFL |

> كل خطوط Google Fonts أعلاه بترخيص **SIL Open Font License 1.1** — آمنة للتضمين التجاري.
> **الاستثناء الوحيد:** خط المصحف العثماني — راجع `docs/05` §3 قبل أي استخدام تجاري.

---

## طريقة التركيب

1. نزّل الملفات في `app/assets/fonts/`.
2. فعّل بلوك `fonts:` في `pubspec.yaml` (موجود جاهز ومعلّق).
3. غيّر `AppTypography.useGoogleFonts` إلى `false` في
   `lib/core/theme/app_typography.dart`.
4. حدّث `AppTypography.fontFamilyUi` / `fontFamilyDisplay` / `fontFamilyQuran`
   بأسماء العائلات المرفقة.

---

## ملاحظات تصميم

- **حجم النص القرآني الافتراضي:** 22sp، قابل للتكبير حتى 40sp.
- **تباعد الأسطر للقرآن:** 1.9–2.15 (أعلى من الطبيعي لراحة القراءة).
- **التشكيل:** مفعّل دائمًا في النص القرآني — لا تفعل `height` أقل من 1.8 وإلا التشكيل هيتقصّ.
- لو هتضيف روايات تانية (ورش/قالون) هتحتاج خطوط رسم مختلفة تمامًا.
