package com.athar.athar.i18n

/// ⚠️ **ملف مولَّد** — لا تُعدّله يدويًا.
/// أعِد توليده بـ `python3 android/tool/gen_strings.py`.
/// المصدر: `app/lib/core/l10n/bundles/` (٣٢١ مفتاحًا × ٣ لغات).
///
/// ⚠️ أي مفتاح ناقص في لغة **يرجع للعربية** لا إلى المفتاح نفسه:
/// نصّ خاطئ بالإنجليزية في تطبيق قرآني أسوأ من نصّ عربي صحيح.
@Suppress("TooManyFunctions", "LongParameterList")
class Strings(val locale: AppLocale) {

    /// Lookup بمفتاح حر (للحالات الديناميكية).
    fun t(key: String): String =
        bundle()[key] ?: kStringsAr[key] ?: key

    /// Lookup مع استبدال متغيّرات على شكل `{name}`.
    fun tArgs(key: String, vararg pairs: Pair<String, Any>): String {
        var value = t(key)
        for ((name, arg) in pairs) value = value.replace("{$name}", "$arg")
        return value
    }

    private fun bundle(): Map<String, String> = when (locale) {
        AppLocale.AR -> kStringsAr
        AppLocale.EN -> kStringsEn
        AppLocale.FR -> kStringsFr
    }

    companion object {

        val kStringsAr: Map<String, String> = mapOf(
            "app_name" to "أثر",
            "tagline" to "خلّي أثرك باقي",
            "hi_morning" to "صباح النور",
            "hi_afternoon" to "يوم مبارك",
            "hi_evening" to "مساء الخير",
            "hi_night" to "ليلة طيبة",
            "tab_home" to "الرئيسية",
            "tab_quran" to "القرآن",
            "tab_adhkar" to "الأذكار",
            "tab_qibla" to "القبلة",
            "tab_more" to "المزيد",
            "next_prayer" to "الصلاة القادمة",
            "remaining" to "يتبقّى",
            "today" to "اليوم",
            "now" to "الآن",
            "madhab_label" to "المذهب",
            "shafi" to "شافعي",
            "hanafi" to "حنفي",
            "fajr" to "الفجر",
            "sunrise" to "الشروق",
            "sunrise_no_adhan" to "الشروق: لا أذان — ليست صلاة",
            "dhuhr" to "الظهر",
            "asr" to "العصر",
            "maghrib" to "المغرب",
            "isha" to "العشاء",
            "prayer_times" to "المواقيت",
            "monthly_calendar" to "التقويم الشهري",
            "calc_settings" to "إعدادات الحساب",
            "notifications" to "الإشعارات",
            "adhan_sound" to "صوت الأذان",
            "reminder_before" to "تذكير قبل الصلاة",
            "continue_reading" to "تابع القراءة",
            "ayah_no" to "آية",
            "wird_title" to "وردي اليوم",
            "wird_sub" to "خطة ختمة ٣٠ يومًا",
            "adhkar_today" to "أذكاري اليوم",
            "done" to "أكملت",
            "of" to "من",
            "quick_actions" to "إجراءات سريعة",
            "verse_of_day" to "آية اليوم",
            "qa_quran" to "القرآن",
            "qa_adhkar" to "أذكار",
            "qa_qibla" to "القبلة",
            "qa_tasbih" to "السبحة",
            "qa_duas" to "أدعية",
            "qa_names" to "أسماء الله",
            "quran_index" to "فهرس السور",
            "search_ph" to "ابحث في السور والآيات…",
            "filter_all" to "الكل",
            "filter_meccan" to "مكية",
            "filter_medinan" to "مدنية",
            "verses" to "آية",
            "juz" to "الجزء",
            "meccan" to "مكية",
            "medinan" to "مدنية",
            "reader_settings" to "إعدادات القراءة",
            "font_size" to "حجم الخط",
            "translation" to "الترجمة",
            "tafsir" to "التفسير",
            "reciter" to "القارئ",
            "none" to "بدون",
            "play_surah" to "استماع",
            "repeat" to "تكرار",
            "speed" to "السرعة",
            "download_offline" to "تحميل للاستماع أوفلاين",
            "save_mark" to "حفظ العلامة",
            "bookmark" to "علامة",
            "note" to "ملاحظة",
            "color" to "تلوين",
            "share" to "مشاركة",
            "copy" to "نسخ",
            "saved_msg" to "تم الحفظ",
            "bookmarks" to "المحفوظات",
            "adhkar_cat" to "تصنيفات الأذكار",
            "morning" to "أذكار الصباح",
            "evening" to "أذكار المساء",
            "after_prayer" to "بعد الصلاة",
            "sleep" to "أذكار النوم",
            "wake" to "الاستيقاظ",
            "food" to "الطعام",
            "mosque" to "المسجد",
            "travel" to "السفر",
            "anxiety" to "الهمّ والحزن",
            "rain" to "المطر",
            "count_of" to "عدد",
            "tap_to_count" to "اضغط للتسبيح",
            "complete_all" to "أكملت المجموعة",
            "reset" to "تصفير",
            "dua_library" to "مكتبة الأدعية",
            "source" to "المصدر",
            "names_title" to "أسماء الله الحسنى",
            "names_sub" to "اضغط على أي اسم لسماع النطق",
            "qibla_title" to "اتجاه القبلة",
            "qibla_hint" to "وجّه الهاتف وانتظر ثبات القراءة",
            "distance" to "المسافة إلى مكة",
            "bearing" to "زاوية القبلة",
            "calibrate" to "حرّك الجهاز على شكل ∞ للمعايرة",
            "simulate" to "محاكاة دوران الجهاز",
            "how_without" to "كيف أعرف القبلة بدون بوصلة؟",
            "tasbih_title" to "السبحة",
            "target" to "الهدف",
            "tracker_title" to "أثري",
            "tracker_sub" to "متتبع العبادات اليومية",
            "streak_days" to "يوم متصل",
            "this_week" to "هذا الأسبوع",
            "last_30" to "آخر ٣٠ يومًا",
            "habits" to "عباداتي",
            "calendar_title" to "التاريخ والمناسبات",
            "hijri" to "التاريخ الهجري",
            "gregorian" to "التاريخ الميلادي",
            "upcoming" to "المناسبات القادمة",
            "more_title" to "المزيد",
            "settings" to "الإعدادات",
            "settings_title" to "الإعدادات",
            "about" to "عن أثر",
            "share_app" to "شارك التطبيق",
            "rate_app" to "قيّم التطبيق",
            "general" to "عام",
            "language" to "اللغة",
            "theme" to "المظهر",
            "text_size" to "حجم النص",
            "backup" to "نسخة احتياطية",
            "sources" to "المصادر والتراخيص",
            "privacy" to "الخصوصية",
            "theme_light" to "فاتح",
            "theme_dark" to "داكن",
            "theme_auto" to "تلقائي",
            "size_small" to "صغير",
            "size_normal" to "متوسط",
            "size_large" to "كبير",
            "size_xlarge" to "كبير جدًا",
            "appearance" to "المظهر واللغة",
            "arabic_digits" to "الأرقام العربية",
            "arabic_digits_sub" to "عرض الأرقام بصيغة ١٢٣ بدل 123",
            "system_default" to "حسب النظام",
            "about_title" to "عن أثر",
            "about_body" to "تطبيق إسلامي شامل، يعمل أوفلاين، بلا إعلانات ولا جمع بيانات. كل المحتوى موثّق المصدر، والمواقيت محسوبة فلكيًا محليًا على جهازك.",
            "empty_bm_t" to "لا توجد علامات بعد",
            "empty_bm_s" to "اضغط مطوّلًا على أي آية لحفظها هنا",
            "empty_note_t" to "لا توجد ملاحظات",
            "save" to "حفظ",
            "cancel" to "إلغاء",
            "apply" to "تطبيق",
            "ok" to "حسنًا",
            "retry" to "إعادة المحاولة",
            "loading" to "جارٍ التحميل…",
            "error_generic" to "حدث خطأ ما",
            "on" to "مفعّل",
            "off" to "معطّل",
            "coming_soon" to "قريبًا",
            "demo_data" to "بيانات تجريبية",
            "onboarding_title" to "أثر",
            "onboarding_subtitle" to "تطبيق إسلامي واحد للصلاة والقرآن والأذكار والقبلة — شغال أوفلاين وبلا إعلانات.",
            "choose_language" to "اختر لغتك",
            "start" to "ابدأ",
            "skip" to "تخطّي",
            "design_system" to "نظام التصميم",
            "ds_colors" to "الألوان",
            "ds_typography" to "الخطوط",
            "ds_spacing" to "المسافات",
            "ds_radii" to "الحواف",
            "ds_components" to "المكوّنات",
            "ds_buttons" to "الأزرار",
            "ds_inputs" to "حقول الإدخال",
            "ds_feedback" to "التغذية الراجعة",
            "week1_note" to "الأساس جاهز: الهيكل، نظام التصميم، وثلاث لغات.",
            "coming_week2" to "المواقيت الحقيقية هتيجي في أسبوع ٢",
            "prayer_soon_title" to "المواقيت قريبًا",
            "prayer_soon_body" to "الحساب الفلكي للمواقيت (١٠+ طرق حساب، مذهب، تعديل يدوي) بيتبني دلوقتي في أسبوع ٢.",
            "quran_soon_title" to "المصحف قريبًا",
            "quran_soon_body" to "فهرس ١١٤ سورة والقارئ بالنص العثماني والتفسير والتلاوة — أسبوع ٢ و٣.",
            "adhkar_soon_title" to "الأذكار قريبًا",
            "adhkar_soon_body" to "١٠ تصنيفات أذكار بعدّاد لكل ذكر، ومكتبة أدعية — أسبوع ٣.",
            "qibla_soon_title" to "البوصلة قريبًا",
            "qibla_soon_body" to "بوصلة القبلة بدمج المستشعرات وتنبيه المعايرة وبديل الشمس — أسبوع ٣.",
            "prayer_settings_title" to "إعدادات المواقيت",
            "prayer_times_title" to "المواقيت",
            "prayer_settings_open" to "المواقيت",
            "prayer_engine_credit" to "الحساب الفلكي: Adhan Dart",
            "prayer_location" to "الموقع",
            "prayer_choose_city" to "اختيار المدينة",
            "prayer_use_my_location" to "تحديد موقعي تلقائيًا",
            "prayer_location_rationale" to "محتاج إذن الموقع عشان نحسب المواقيت بدقة. مش بنخزّن موقعك ولا بنرسله لأي حد.",
            "prayer_location_updated" to "تم تحديث الموقع",
            "prayer_location_denied" to "مافيش إذن للوصول للموقع — تقدر تختار مدينة يدويًا.",
            "prayer_location_denied_forever" to "الإذن مرفوض نهائيًا. افتح إعدادات التطبيق لتفعيله.",
            "prayer_location_service_off" to "خدمة الموقع مقفولة من الجهاز.",
            "prayer_location_timeout" to "ماقدرناش نحدد الموقع. جرّب تاني أو اختار مدينة.",
            "prayer_location_unknown" to "حصلت مشكلة غير متوقعة في تحديد الموقع.",
            "prayer_open_settings" to "فتح الإعدادات",
            "prayer_method" to "طريقة الحساب",
            "prayer_madhab" to "المذهب",
            "prayer_madhab_sub" to "بيأثر على وقت العصر فقط (طول الظل).",
            "prayer_high_latitude" to "خطوط العرض العالية",
            "prayer_high_latitude_sub" to "للمناطق اللي الفجر أو العشاء فيها ما بيظهرش بوضوح (أوروبا وأمريكا الشمالية).",
            "prayer_adjustments" to "تعديل يدوي",
            "prayer_adjustments_sub" to "زوّد أو قلّل دقائق عن الحساب لو مواقيت مسجدك مختلفة.",
            "prayer_adjusted_notice" to "المواقيت معدّلة يدويًا — مش الحساب الفلكي المباشر.",
            "prayer_clear_adjustments" to "مسح كل التعديلات",
            "prayer_next_label" to "الصلاة القادمة",
            "prayer_tomorrow_fajr" to "فجر الغد",
            "prayer_now" to "الآن",
            "prayer_upcoming" to "القادمة",
            "prayer_unavailable" to "تعذّر حساب المواقيت",
            "city_search_hint" to "ابحث عن مدينة…",
            "city_no_results" to "مفيش نتائج",
            "city_no_results_hint" to "جرّب كتابة الاسم بالعربية أو الإنجليزية أو الفرنسية.",
            "time_am" to "ص",
            "time_pm" to "م",
            "home_week2_note" to "المواقيت دلوقتي محسوبة فلكيًا على جهازك (بدون إنترنت). الورد والأذكار والمتتبع لسه في الطريق.",
            "notif_athan_title" to "حان وقت {prayer}",
            "notif_athan_body" to "{time} — {place}",
            "notif_reminder_title" to "{prayer} قريبًا",
            "notif_reminder_body" to "فاضل {n} دقيقة على الأذان",
            "notif_jumuah_title" to "يوم الجمعة",
            "notif_jumuah_body" to "سورة الكهف · الصلاة على النبي · الاغتسال",
            "notif_channel_athan" to "الأذان",
            "notif_channel_athan_desc" to "الأذان في وقت كل صلاة",
            "notif_channel_reminder" to "تذكير قبل الصلاة",
            "notif_channel_reminder_desc" to "تنبيه هادئ قبل الأذان بدقائق",
            "notif_channel_jumuah" to "تذكير الجمعة",
            "notif_channel_jumuah_desc" to "تذكير أسبوعي بيوم الجمعة",
            "notifications_test_title" to "إشعار تجريبي",
            "notifications_test_body" to "لو شُفت الرسالة دي يبقى الإشعارات شغالة تمام.",
            "notifications_master" to "تشغيل الإشعارات",
            "notifications_master_desc" to "لو مقفولة، أثر مش هيبعت أي تنبيه خالص",
            "notifications_test" to "جرّب إشعار دلوقتي",
            "notifications_test_queued" to "هيوصلك إشعار تجريبي بعد ٥ ثواني",
            "notifications_off_hint" to "الإشعارات مقفولة — فعّلها عشان تسمع الأذان في وقته",
            "notifications_permission_needed" to "محتاج إذن الإشعارات من النظام",
            "notifications_permission_action" to "فعّل الإشعارات",
            "notifications_exact_needed" to "عشان الأذان يكون في ثانيته بالظبط، فعّل «المنبهات والتذكيرات»",
            "notifications_exact_needed_short" to "الجدولة الدقيقة مقفولة — ممكن يتأخر الأذان دقايق",
            "notifications_exact_action" to "فتح الإعدادات",
            "notifications_per_prayer" to "الأذان لكل صلاة",
            "notifications_per_prayer_hint" to "اختار لكل صلاة أذان ولا اهتزاز ولا صامت",
            "notifications_reminder" to "تذكير قبل الأذان",
            "notifications_reminder_desc" to "إشعار على الشاشة بدون صوت",
            "notifications_reminder_minutes" to "قبل الأذان بـ",
            "notifications_minutes_off" to "بدون تذكير",
            "notifications_jumuah" to "تذكير الجمعة",
            "notifications_jumuah_desc" to "سورة الكهف · الصلاة على النبي · الاغتسال",
            "notifications_jumuah_before" to "قبل صلاة الجمعة بـ",
            "notifications_sound" to "صوت الأذان",
            "notifications_sound_desc" to "كل الأصوات موقوفة مؤقتًا لحين التأكد من حقوقها",
            "notifications_mode_adhan" to "أذان",
            "notifications_mode_beep" to "نغمة",
            "notifications_mode_vibrate" to "اهتزاز",
            "notifications_mode_silent" to "صامت",
            "notifications_mode_off" to "مقفول",
            "notifications_scheduled" to "إشعار مجدول",
            "notifications_scheduled_many" to "إشعارات مجدولة",
            "notifications_mode_short_adhan" to "أذان مختصر",
            "notifications_minutes_value" to "{n} دقيقة",
            "notifications_scheduled_none" to "مفيش تنبيهات مجدولة",
            "athary_open" to "فتح أثري",
            "athary_best" to "أفضل",
            "athary_less" to "أقل",
            "athary_more" to "أكثر",
            "athary_next" to "اللي فاضلك",
            "athary_reset" to "مسح السجل",
            "athary_reset_confirm" to "همسح كل سجل «أثري» نهائيًا. مفيش رجوع.",
            "athary_future_note" to "في الإصدار الكامل: ملخص أسبوعي، أهداف مخصصة، ويوم راحة واحد أسبوعيًا يحافظ على السلسلة.",
            "home_week4_note" to "المواقيت والإشعارات شغالين فعلًا، و«أثري» بيحسب سلسلتك محليًا على جهازك. الورد القرآني والأذكار لسه في الطريق (أسابيع ٥–١١).",
            "widget_label" to "ودجت أثر",
            "widget_description" to "الصلاة القادمة ومواقيت اليوم على الشاشة الرئيسية",
            "widget_next_prayer" to "القادمة",
            "widget_in" to "بعد",
            "widget_no_data" to "افتح أثر لحساب المواقيت",
            "widget_add" to "إضافة الودجت",
            "widget_add_hint" to "اضغط مطوّلًا على الشاشة الرئيسية ← ودجت ← أثر",
            "quran_tab_surah" to "السور",
            "quran_tab_juz" to "الأجزاء",
            "quran_tab_page" to "الصفحات",
            "quran_empty_title" to "القرآن ليس محمّلًا على جهازك",
            "quran_empty_body" to "نص القرآن لا يُرفق مع التطبيق — قرار متعمّد لحماية حقوق النص والتحقّق منه. استورد ملفًا واحدًا مرة واحدة، وبعدها يشتغل بلا إنترنت تمامًا.",
            "quran_import_action" to "استيراد ملف القرآن",
            "quran_importing" to "جاري الاستيراد…",
            "quran_import_failed" to "لم يتم الاستيراد",
            "quran_reader_settings" to "إعدادات القراءة",
            "quran_font_size" to "حجم الخط",
            "quran_font_smaller" to "أصغر",
            "quran_font_bigger" to "أكبر",
            "quran_reading_theme" to "ثيم القراءة",
            "quran_theme_paper" to "ورق",
            "quran_theme_sepia" to "Sepia",
            "quran_theme_night" to "ليلي",
            "quran_show_translation" to "إظهار الترجمة",
            "quran_no_translation" to "لا توجد ترجمة محمّلة",
            "quran_edition" to "الترجمة",
            "quran_page_word" to "صفحة",
            "quran_sajda" to "سجدة",
            "quran_search_title" to "البحث في القرآن",
            "quran_search_hint" to "اكتب كلمة، أو رقم آية (2:255)، أو «ص ٤٢»",
            "quran_search_empty" to "لا توجد نتائج",
            "quran_results_count" to "{n} نتيجة",
            "quran_no_ayahs" to "لا توجد آيات",
            "audio_listen" to "اسمع",
            "audio_stop" to "إيقاف",
            "audio_reciter" to "القارئ",
            "audio_choose_reciter" to "اختيار القارئ",
            "audio_reciter_default" to "الافتراضي",
            "audio_download" to "تنزيل",
            "audio_download_surah" to "تنزيل السورة",
            "audio_download_juz" to "تنزيل الجزء",
            "audio_download_all" to "تنزيل القرآن كاملًا",
            "audio_downloaded" to "منزّل",
            "audio_delete_download" to "حذف التنزيل",
            "audio_downloading" to "جارٍ التنزيل…",
            "audio_download_progress" to "{done} من {total}",
            "audio_size_estimate" to "≈ {size}",
            "audio_needs_internet" to "التشغيل الأول يحتاج اتصالًا بالإنترنت",
            "audio_playback_failed" to "تعذّر تشغيل التلاوة",
            "audio_speed" to "سرعة التلاوة",
            "audio_repeat_ayah" to "تكرار الآية",
            "audio_sources" to "المصادر والتراخيص",
            "audio_rights_note" to "حقوق كل تلاوة محفوظة لقارئها",
            "audio_licensed_noncommercial" to "مسموح للاستخدام غير التجاري",
            "audio_no_bundled_audio" to "لا يحتوي التطبيق على أي ملف صوتي — كل التلاوات تُبثّ أو تُنزّل عند الطلب",
            "audio_cancel" to "إلغاء",
            "audio_delete_confirm" to "حذف تلاوة {name} من الجهاز؟",
            "audio_storage_used" to "المساحة المستخدمة: {size}",
            "back" to "رجوع",
            "change" to "تغيير",
            "north" to "شمال",
            "south" to "جنوب",
            "east" to "شرق",
            "west" to "غرب",
            "adhkar_search_hint" to "ابحث في الأقسام",
            "adhkar_items_count" to "{n} أذكار",
            "prayer_my_location" to "موقعي الحالي",
            "prayer_none_left" to "انتهت صلوات اليوم",
            "qibla_no_sensor" to "هذا الجهاز لا يحتوي على بوصلة",
            "qibla_bearing_from_north" to "زاوية القبلة من الشمال: {deg}°",
            "qibla_device_direction" to "اتجاه الهاتف",
            "qibla_aligned" to "أنت باتجاه القبلة ✓",
            "qibla_turn_right" to "أدر الهاتف يمينًا {deg}°",
            "qibla_turn_left" to "أدر الهاتف يسارًا {deg}°",
            "qibla_bearing_geo" to "القبلة على {deg}° من الشمال الجغرافي",
            "quran_load_failed" to "تعذّر تحميل القرآن",
            "quran_uthmani_source" to "النص العثماني: {source}",
            "minutes_short" to "{n} د",
            "habit_fajr" to "صلاة الفجر في وقتها",
            "habit_five_prayers" to "الصلوات الخمس",
            "habit_quran" to "ورد قرآني",
            "habit_morning_adhkar" to "أذكار الصباح",
            "habit_evening_adhkar" to "أذكار المساء",
            "habit_duha" to "صلاة الضحى",
            "habit_sadaqa" to "صدقة",
            "habit_fasting" to "صيام",
            "habit_tasbih" to "تسبيح مكتمل",
            "athary_share" to "مشاركة ملخّص الأسبوع",
            "share_week" to "هذا الأسبوع",
            "share_active_days" to "أيامٌ نشطة",
            "share_acts" to "عبادة",
            "share_streak" to "أيامٌ متّصلة",
            "share_footer" to "خلّي أثرك باقي — أثر",
            "share_text" to "هذا أسبوعي في أثر.",
            "share_failed" to "تعذّر إنشاء الصورة.",
            "ruqya_title" to "الرقية الشرعية",
            "ruqya_note" to "ما في هذا التطبيق دعاءٌ جديد: هو جمعٌ لمقتطفاتٍ مشهورة من حصن المسلم والقرآن، بنصّها كما هي، من مصادر التطبيق نفسها.",
            "ruqya_quran_title" to "من القرآن",
            "ruqya_adhkar_title" to "من حصن المسلم",
            "ruqya_kursi" to "آية الكرسي",
            "ruqya_baqarah_last" to "آخر آيتي البقرة",
            "ruqya_ikhlas" to "سورة الإخلاص",
            "ruqya_falaq" to "سورة الفلق",
            "ruqya_nas" to "سورة الناس",
            "ruqya_play" to "تلاوة المقتطف",
            "ruqya_missing" to "المقتطف غير متاح في هذه النسخة.",
            "tracker_today_empty" to "ما سجّلت شيء اليوم — ابدأ بواحدة",
            "tracker_days" to "يوم",
            "more_asma" to "أسماء الله الحسنى",
            "adhkar_after_prayer" to "أذكار بعد الصلاة",
            "asma_search_hint" to "ابحث بالاسم أو المعنى",
            "asma_count" to "٩٩ اسمًا",
            "asma_empty" to "لا شيء يطابق بحثك",
            "asma_note" to "الأسماء ومعانيها من كتاب «الأنوار الساطعة في شرح أسماء الله الحسنى» (سعيد القحطاني). المعاني بالعربية وحدها: لم نجد مصدرًا مرخّصًا لترجمتها، ولن ننشئ نصًا دينيًا من عندنا.",
            "method_egyptian" to "الهيئة المصرية للمساحة",
            "method_umm_al_qura" to "أم القرى — مكة",
            "method_mwl" to "رابطة العالم الإسلامي",
            "method_isna" to "ISNA — أمريكا الشمالية",
            "method_france" to "فرنسا ١٥°",
            "method_custom" to "مخصّص",
            "notif_channel_adhkar" to "الأذكار",
            "notif_channel_adhkar_desc" to "تذكير بأذكار الصباح والمساء",
            "notif_morning_adhkar_title" to "أذكار الصباح",
            "notif_morning_adhkar_body" to "حان وقت أذكار الصباح",
            "notif_evening_adhkar_title" to "أذكار المساء",
            "notif_evening_adhkar_body" to "حان وقت أذكار المساء",
            "settings_morning_adhkar" to "تذكير أذكار الصباح",
            "settings_evening_adhkar" to "تذكير أذكار المساء",
            "settings_adhkar_offset" to "بعد الأذان بدقائق",
            "quran_translation" to "الترجمة",
            "quran_translation_none" to "النص العربي فقط",
            "quran_translation_pickthall" to "پيكثال",
            "quran_translation_itani" to "إيتاني",
            "quran_credit_pickthall" to "معاني القرآن المجيد — محمد مارمادوك پيكثال، ١٩٣٠. ملكية عامة.",
            "quran_credit_itani" to "The Clear Quran — طلال إيتاني. CC BY-ND 4.0: يُعرض كما هو دون تعديل.",
            "quran_juz" to "الجزء",
            "quran_page" to "الصفحة",
            "quran_reciter" to "القارئ",
            "quran_play" to "استماع السورة",
            "quran_pause" to "إيقاف مؤقت",
            "quran_resume" to "متابعة",
            "quran_stop" to "إيقاف",
            "quran_audio_credit" to "التلاوة: islamic.network (alquran.cloud) — حق التلاوة لقارئها.",
            "quran_audio_error" to "تعذّر تشغيل التلاوة — تحقّق من الاتصال.",
            "quran_audio_loading" to "جارٍ تحميل التلاوة…",
            "theme_mode" to "الثيم",
            "theme_system" to "تلقائي (حسب النظام)",
            "font_small" to "صغير",
            "font_normal" to "متوسط",
            "font_large" to "كبير",
            "font_huge" to "كبير جدًا",
            "reader_theme" to "ثيم القراءة",
            "reader_normal" to "عادي",
            "reader_night" to "ليلي",
            "reader_sepia" to "سيبيا",
            "font_preview" to "هذا نموذج لحجم الخط",
            "notif_channel_playback" to "التلاوة",
            "notif_channel_playback_desc" to "إشعار التشغيل الجاري والتحكّم به",
            "quran_download" to "تنزيل",
            "quran_download_done" to "محفوظة على الجهاز",
            "quran_delete_download" to "حذف التنزيل",
            "quran_downloading" to "جارٍ التنزيل…",
            "quran_download_hint" to "التنزيل متاح لمن يبثّ السورة كاملة بملف واحد",
            "quran_offline_ready" to "تُشغَّل بلا اتصال",
            "quran_bookmarks" to "المفضلة",
            "quran_bookmark_add" to "حفظ الآية",
            "quran_bookmark_remove" to "إزالة من المفضلة",
            "quran_bookmarks_empty" to "لا آيات محفوظة بعد",
            "quran_bookmarks_empty_body" to "اضغط علامة الحفظ بجانب أي آية لتجدها هنا",
            "adhkar_mine" to "أذكاري",
            "adhkar_mine_body" to "أذكار تكتبها أنت — بلا تخريج، فهي من عندك لا من الكتاب",
            "adhkar_add" to "إضافة",
            "adhkar_add_hint" to "اكتب الذكر",
            "adhkar_repeat_count" to "عدد المرات",
            "adhkar_mine_empty" to "لم تكتب شيئًا بعد",
            "adhkar_delete" to "حذف",
            "tasbih_undo" to "رجوع",
            "tasbih_reset" to "تصفير",
            "tasbih_rounds" to "دورات",
            "tasbih_total" to "الإجمالي",
            "tab_tasbih" to "السبحة",
            "hijri_month_1" to "محرم",
            "hijri_month_2" to "صفر",
            "hijri_month_3" to "ربيع الأول",
            "hijri_month_4" to "ربيع الآخر",
            "hijri_month_5" to "جمادى الأولى",
            "hijri_month_6" to "جمادى الآخرة",
            "hijri_month_7" to "رجب",
            "hijri_month_8" to "شعبان",
            "hijri_month_9" to "رمضان",
            "hijri_month_10" to "شوال",
            "hijri_month_11" to "ذو القعدة",
            "hijri_month_12" to "ذو الحجة",
            "hijri_adjust" to "ضبط الهجري",
            "hijri_adjust_body" to "بعض البلدان ترى الهلال قبل غيرها. اختر الإزاحة إن كان التاريخ عندك مختلفًا.",
            "hijri_today" to "اليوم",
            "quran_ayah" to "آية",
            "khatmah_title" to "الختمة",
            "khatmah_start" to "ابدأ ختمة",
            "khatmah_plan" to "مدة الختمة",
            "khatmah_days" to "{n} يومًا",
            "khatmah_today" to "ورد اليوم",
            "khatmah_from" to "من",
            "khatmah_to" to "إلى",
            "khatmah_read" to "اقرأ الورد",
            "khatmah_done" to "أنجزت الورد",
            "khatmah_progress" to "أنجزت {n} من {m}",
            "khatmah_restart" to "إعادة البداية",
            "khatmah_stop" to "إلغاء الخطة",
            "khatmah_finished" to "أتممت الختمة",
            "khatmah_finished_body" to "الحمد لله. ابدأ ختمة أخرى متى شئت.",
            "khatmah_per_day" to "{n} آية كل يوم",
            "event_hijri_new_year" to "رأس السنة الهجرية",
            "event_ashura" to "عاشوراء",
            "event_mawlid" to "المولد النبوي",
            "event_isra" to "الإسراء والمعراج",
            "event_ramadan" to "رمضان",
            "event_qadr" to "ليلة القدر",
            "event_eid_fitr" to "عيد الفطر",
            "event_arafah" to "يوم عرفة",
            "event_eid_adha" to "عيد الأضحى",
            "event_days_left" to "باقي {n} يومًا",
            "event_today" to "اليوم",
            "event_tomorrow" to "غدًا",
            "hl_title" to "خطوط العرض العالية",
            "hl_middle" to "وسط الليل",
            "hl_seventh" to "سُبع الليل",
            "hl_twilight" to "زاوية الشفق",
            "month_prev" to "الشهر الماضي",
            "month_next" to "الشهر التالي",
            "backup_title" to "النسخ الاحتياطي",
            "backup_body" to "كل ما تملكه في أثر: المواقيت وإعداداتها، آخر موضع قراءة، العلامات، الختمة، أثري، السبحة، والأذكار التي أضفتها.",
            "backup_export" to "تصدير نسخة",
            "backup_import" to "استعادة من ملف",
            "backup_exported" to "حُفظت النسخة",
            "backup_restored" to "استُعيدت النسخة",
            "backup_bad_file" to "هذا الملف ليس نسخة أثر، أو هو من إصدار أحدث",
            "backup_failed" to "تعذّرت العملية",
            "hifz_title" to "وضع الحفظ",
            "hifz_body" to "يُخفي التطبيق الآية كلمة كلمة كلّما كرّرتها، حتّى تسترجعها من ذاكرتِك وحدها.",
            "hifz_from" to "من",
            "hifz_to" to "إلى",
            "hifz_surah" to "السورة",
            "hifz_ayah" to "الآية",
            "hifz_count" to "عدد الآيات: {n}",
            "hifz_start" to "ابدأ الحفظ",
            "hifz_change" to "تغيير النطاق",
            "hifz_reveal" to "أظهر",
            "hifz_hide" to "أخفِ",
            "hifz_repeat" to "كرّرت",
            "hifz_memorized" to "محفوظة",
            "hifz_progress" to "{a} من {b}",
            "hifz_repeats_of" to "التكرار {a} من {b}",
            "hifz_finished" to "حفظت النطاق كلّه",
            "quran_hizb" to "الأحزاب — حزبان في كل جزء",
            "ramadan_day" to "اليوم {a} من رمضان",
            "ramadan_iftar" to "الإفطار",
            "ramadan_suhur" to "آخر وقت السحور",
            "ramadan_last_ten" to "العشر الأواخر",
            "weekly_title" to "ملخّص الأسبوع",
            "weekly_range" to "{a} — {b}",
            "weekly_acts" to "{n} عبادة في {d} يوم",
            "weekly_completion" to "نسبة الإتمام: {n}٪",
            "weekly_better" to "أكثر من الأسبوع الذي قبله",
            "weekly_worse" to "أقلّ من الأسبوع الذي قبله",
            "weekly_same" to "مثل الأسبوع الذي قبله",
            "weekly_streak" to "السلسلة الآن: {n} يومًا",
            "weekly_best_streak" to "أطول سلسلة: {n} يومًا",
            "weekly_top" to "أكثر ما فعلته",
            "adjust_title" to "تعديل المواقيت",
            "adjust_body" to "إن اختلف حساب أثر عن توقيت مسجدك، عدّل الفرق بالدقائق. والثلاثون سقفًا: ما جاوزها ليس فرق توقيت.",
            "adjust_reset" to "تصفير الكل",
            "adjust_minutes" to "{n} د",
            "adjust_open" to "تعديل",
            "mushaf_mode" to "وضع المصحف",
            "mushaf_page" to "صفحة {n}",
            "mushaf_jump" to "اذهب إلى صفحة",
            "mushaf_of" to "من ٦٠٤",
            "mushaf_note" to "حدود الصفحات من المصحف المدني برواية حفص. والنصّ عثماني كما هو.",
            "mushaf_open" to "وضع المصحف",
            "mushaf_next" to "التالية",
            "mushaf_prev" to "السابقة",
            "surah_filter_all" to "الكل",
            "surah_filter_meccan" to "مكية",
            "surah_filter_medinan" to "مدنية",
            "surah_sections" to "السور",
            "prayer_remaining" to "متبقٍّ",
            "zakah_title" to "حاسبة الزكاة",
            "zakah_gold" to "الذهب",
            "zakah_silver" to "الفضة",
            "zakah_cash" to "النقود",
            "zakah_fitr" to "زكاة الفطر",
            "zakah_grams" to "الجرامات",
            "zakah_amount" to "المبلغ",
            "zakah_people" to "عدد الأنفس",
            "zakah_price" to "سعر الجرام",
            "zakah_food_price" to "سعر الكيلو",
            "zakah_nisab" to "النصاب",
            "zakah_due" to "الواجب",
            "zakah_not_liable" to "لم يبلغ النصاب",
            "zakah_liable" to "بلغ النصاب",
            "zakah_note" to "الأسعار تُدخلها أنت: سعر الذهب والفضة يتغيّر كلّ يوم، وسعرٌ في التطبيق يقدّم زكاةً خاطئة بثقة.",
            "zakah_source" to "النصاب: ٨٥ جرامًا ذهبًا أو ٥٩٥ جرامًا فضة، و٢٫٥ كجم قوتًا للفرد في الفطر.",
            "qibla_sun_title" to "القبلة من الشمس",
            "qibla_sun_body" to "لا تحتاج البوصلة: مرّتين في السنة تشرق الشمس في اتجاه القبلة بالضبط. في تلك اليومين، قف عند الشرق ووجه نفسك — فأنت مُتّجه إلى الكعبة",
            "qibla_sun_days" to "أيام الشروق",
            "qibla_sun_none" to "لا يوجد في هذه السنة يومٌ تشرق فيه الشمس في اتجاه القبلة من مكانك.",
            "qibla_sun_shadow" to "وقت الظلّ اليوم",
            "qibla_sun_shadow_none" to "لا يبلغ الظلّ القبلة اليوم.",
            "qibla_sun_now" to "الشمس الآن على ارتفاع {alt}° واتجاه {az}°",
            "qibla_sun_pointing" to "الظلّ الآن يشير إلى القبلة",
            "tasbih_sessions" to "سجلّ الجلسات",
            "tasbih_today" to "اليوم",
            "tasbih_best" to "أطول جلسة",
            "tasbih_sessions_count" to "{n} جلسة",
            "tasbih_counts" to "{n} ذكرًا",
            "tasbih_no_sessions" to "لا جلسات بعد.",
            "tasbih_speak" to "نطق العدد",
            "tasbih_sessions_today" to "جلسات اليوم",
            "tasbih_week" to "آخر ٧ أيام",
            "tasbih_speak_hint" to "ينطق العدد بصوت عالٍ عند كل نقرة — أطفئه إن أزعجك.",
            "tasbih_volume" to "العدّ بأزرار الصوت",
            "tasbih_volume_hint" to "أزرار الصوت تعدّ ذكرًا بدلًا من تغيير الصوت — غيّر الصوت من إعدادات الجهاز.",
            "silence_title" to "وضع الصمت أثناء الصلاة",
            "silence_body" to "كتمُ الوسائط والاهتزاز في نافذة الصلاة. يبدأ بعد الأذان بدقيقتين ليبقى صوتُ الأذان مسموعًا، وينتهي بانتهاء النافذة. وهو معطّلٌ حتى توافق عليه.",
            "silence_need_access" to "يحتاج التطبيق إذن «عدم الإزعاج» ليرفع الصوت عن الوسائط والتنبيهات. اضغط للسماح به من إعدادات النظام.",
            "silence_allow" to "السماح بالوصول",
            "silence_minutes" to "مدة الصمت",
            "month_share" to "مشاركة الجدول",
            "month_share_text" to "جدول مواقيت الصلاة.",
            "month_share_footer" to "مواقيت الصلاة — {place} — أثر",
        )

        val kStringsEn: Map<String, String> = mapOf(
            "app_name" to "Athar",
            "tagline" to "Leave a trace of good",
            "hi_morning" to "Good morning",
            "hi_afternoon" to "Good afternoon",
            "hi_evening" to "Good evening",
            "hi_night" to "Good night",
            "tab_home" to "Home",
            "tab_quran" to "Quran",
            "tab_adhkar" to "Adhkar",
            "tab_qibla" to "Qibla",
            "tab_more" to "More",
            "next_prayer" to "Next prayer",
            "remaining" to "remaining",
            "today" to "Today",
            "now" to "Now",
            "madhab_label" to "Madhab",
            "shafi" to "Shafi’i",
            "hanafi" to "Hanafi",
            "fajr" to "Fajr",
            "sunrise" to "Sunrise",
            "sunrise_no_adhan" to "Sunrise: no adhan — it is not a prayer",
            "dhuhr" to "Dhuhr",
            "asr" to "Asr",
            "maghrib" to "Maghrib",
            "isha" to "Isha",
            "prayer_times" to "Prayer times",
            "monthly_calendar" to "Monthly calendar",
            "calc_settings" to "Calculation settings",
            "notifications" to "Notifications",
            "adhan_sound" to "Adhan sound",
            "reminder_before" to "Reminder before prayer",
            "continue_reading" to "Continue reading",
            "ayah_no" to "Ayah",
            "wird_title" to "My daily portion",
            "wird_sub" to "30-day khatma plan",
            "adhkar_today" to "Today’s adhkar",
            "done" to "Completed",
            "of" to "of",
            "quick_actions" to "Quick actions",
            "verse_of_day" to "Verse of the day",
            "qa_quran" to "Quran",
            "qa_adhkar" to "Adhkar",
            "qa_qibla" to "Qibla",
            "qa_tasbih" to "Tasbih",
            "qa_duas" to "Supplications",
            "qa_names" to "Names of Allah",
            "quran_index" to "Surah index",
            "search_ph" to "Search surahs & verses…",
            "filter_all" to "All",
            "filter_meccan" to "Meccan",
            "filter_medinan" to "Medinan",
            "verses" to "verses",
            "juz" to "Juz",
            "meccan" to "Meccan",
            "medinan" to "Medinan",
            "reader_settings" to "Reading settings",
            "font_size" to "Font size",
            "translation" to "Translation",
            "tafsir" to "Tafsir",
            "reciter" to "Reciter",
            "none" to "None",
            "play_surah" to "Listen",
            "repeat" to "Repeat",
            "speed" to "Speed",
            "download_offline" to "Download for offline",
            "save_mark" to "Save bookmark",
            "bookmark" to "Bookmark",
            "note" to "Note",
            "color" to "Highlight",
            "share" to "Share",
            "copy" to "Copy",
            "saved_msg" to "Saved",
            "bookmarks" to "Saved",
            "adhkar_cat" to "Adhkar categories",
            "morning" to "Morning adhkar",
            "evening" to "Evening adhkar",
            "after_prayer" to "After prayer",
            "sleep" to "Before sleep",
            "wake" to "Waking up",
            "food" to "Eating",
            "mosque" to "Mosque",
            "travel" to "Travel",
            "anxiety" to "Anxiety & grief",
            "rain" to "Rain",
            "count_of" to "Count",
            "tap_to_count" to "Tap to count",
            "complete_all" to "Set completed",
            "reset" to "Reset",
            "dua_library" to "Supplications library",
            "source" to "Source",
            "names_title" to "The 99 Names of Allah",
            "names_sub" to "Tap a name to hear it",
            "qibla_title" to "Qibla direction",
            "qibla_hint" to "Point the phone and hold steady",
            "distance" to "Distance to Makkah",
            "bearing" to "Qibla bearing",
            "calibrate" to "Move the device in a ∞ to calibrate",
            "simulate" to "Simulate device rotation",
            "how_without" to "How to find Qibla without a compass?",
            "tasbih_title" to "Tasbih",
            "target" to "Target",
            "tracker_title" to "My trace",
            "tracker_sub" to "Daily worship tracker",
            "streak_days" to "day streak",
            "this_week" to "This week",
            "last_30" to "Last 30 days",
            "habits" to "My habits",
            "calendar_title" to "Calendar & events",
            "hijri" to "Hijri date",
            "gregorian" to "Gregorian date",
            "upcoming" to "Upcoming events",
            "more_title" to "More",
            "settings" to "Settings",
            "settings_title" to "Settings",
            "about" to "About Athar",
            "share_app" to "Share the app",
            "rate_app" to "Rate the app",
            "general" to "General",
            "language" to "Language",
            "theme" to "Theme",
            "text_size" to "Text size",
            "backup" to "Backup",
            "sources" to "Sources & licenses",
            "privacy" to "Privacy",
            "theme_light" to "Light",
            "theme_dark" to "Dark",
            "theme_auto" to "Auto",
            "size_small" to "Small",
            "size_normal" to "Normal",
            "size_large" to "Large",
            "size_xlarge" to "Extra large",
            "appearance" to "Appearance & language",
            "arabic_digits" to "Arabic-Indic digits",
            "arabic_digits_sub" to "Show numbers as ١٢٣ instead of 123",
            "system_default" to "Follow system",
            "about_title" to "About Athar",
            "about_body" to "An all-in-one Islamic app that works offline, with no ads and no data collection. Every piece of content is sourced, and prayer times are computed astronomically on your device.",
            "empty_bm_t" to "No bookmarks yet",
            "empty_bm_s" to "Long-press any verse to save it here",
            "empty_note_t" to "No notes yet",
            "save" to "Save",
            "cancel" to "Cancel",
            "apply" to "Apply",
            "ok" to "OK",
            "retry" to "Retry",
            "loading" to "Loading…",
            "error_generic" to "Something went wrong",
            "on" to "On",
            "off" to "Off",
            "coming_soon" to "Coming soon",
            "demo_data" to "Demo data",
            "onboarding_title" to "Athar",
            "onboarding_subtitle" to "One Islamic app for prayer, Quran, adhkar and Qibla — offline, with no ads.",
            "choose_language" to "Choose your language",
            "start" to "Get started",
            "skip" to "Skip",
            "design_system" to "Design system",
            "ds_colors" to "Colors",
            "ds_typography" to "Typography",
            "ds_spacing" to "Spacing",
            "ds_radii" to "Radii",
            "ds_components" to "Components",
            "ds_buttons" to "Buttons",
            "ds_inputs" to "Inputs",
            "ds_feedback" to "Feedback",
            "week1_note" to "Foundation ready: structure, design system and three languages.",
            "coming_week2" to "Real prayer times arrive in week 2",
            "prayer_soon_title" to "Prayer times soon",
            "prayer_soon_body" to "Astronomical prayer-time calculation (10+ methods, madhab, manual offsets) is being built in week 2.",
            "quran_soon_title" to "The Quran soon",
            "quran_soon_body" to "An index of all 114 surahs plus the reader with Uthmani text, tafsir and recitation — weeks 2 and 3.",
            "adhkar_soon_title" to "Adhkar soon",
            "adhkar_soon_body" to "Ten adhkar categories with a counter per dhikr, plus a supplications library — week 3.",
            "qibla_soon_title" to "Compass soon",
            "qibla_soon_body" to "The Qibla compass with sensor fusion, calibration prompts and a sun-based fallback — week 3.",
            "prayer_settings_title" to "Prayer times settings",
            "prayer_times_title" to "Prayer times",
            "prayer_settings_open" to "Prayer times",
            "prayer_engine_credit" to "Astronomical calculation: Adhan Dart",
            "prayer_location" to "Location",
            "prayer_choose_city" to "Choose a city",
            "prayer_use_my_location" to "Use my location",
            "prayer_location_rationale" to "Location permission is needed for accurate times. We never store or send your location.",
            "prayer_location_updated" to "Location updated",
            "prayer_location_denied" to "Location permission denied — you can pick a city manually.",
            "prayer_location_denied_forever" to "Permission permanently denied. Open app settings to enable it.",
            "prayer_location_service_off" to "Location services are turned off on this device.",
            "prayer_location_timeout" to "Could not determine your location. Try again or pick a city.",
            "prayer_location_unknown" to "An unexpected error occurred while getting your location.",
            "prayer_open_settings" to "Open settings",
            "prayer_method" to "Calculation method",
            "prayer_madhab" to "Madhab",
            "prayer_madhab_sub" to "Affects the Asr time only (shadow length).",
            "prayer_high_latitude" to "High latitude rule",
            "prayer_high_latitude_sub" to "For regions where Fajr or Isha are not clearly defined (Europe, North America).",
            "prayer_adjustments" to "Manual adjustment",
            "prayer_adjustments_sub" to "Add or subtract minutes if your local mosque differs from the calculation.",
            "prayer_adjusted_notice" to "Times are manually adjusted — not the raw astronomical calculation.",
            "prayer_clear_adjustments" to "Clear all adjustments",
            "prayer_next_label" to "Next prayer",
            "prayer_tomorrow_fajr" to "Fajr tomorrow",
            "prayer_now" to "Now",
            "prayer_upcoming" to "Next",
            "prayer_unavailable" to "Could not calculate prayer times",
            "city_search_hint" to "Search for a city…",
            "city_no_results" to "No results",
            "city_no_results_hint" to "Try typing the name in Arabic, English or French.",
            "time_am" to "AM",
            "time_pm" to "PM",
            "home_week2_note" to "Prayer times are now calculated astronomically on your device, offline. The wird, adhkar and tracker are still on the way.",
            "notif_athan_title" to "Time for {prayer}",
            "notif_athan_body" to "{time} — {place}",
            "notif_reminder_title" to "{prayer} soon",
            "notif_reminder_body" to "{n} minutes until adhan",
            "notif_jumuah_title" to "Friday",
            "notif_jumuah_body" to "Surah Al-Kahf · Blessings on the Prophet · Ghusl",
            "notif_channel_athan" to "Adhan",
            "notif_channel_athan_desc" to "Adhan at the start of each prayer",
            "notif_channel_reminder" to "Pre-prayer reminder",
            "notif_channel_reminder_desc" to "A silent reminder a few minutes before adhan",
            "notif_channel_jumuah" to "Friday reminder",
            "notif_channel_jumuah_desc" to "A weekly reminder on Friday",
            "notifications_test_title" to "Test notification",
            "notifications_test_body" to "If you can see this, notifications are working.",
            "notifications_master" to "Enable notifications",
            "notifications_master_desc" to "When off, Athar sends nothing at all",
            "notifications_test" to "Send a test notification",
            "notifications_test_queued" to "A test notification will arrive in 5 seconds",
            "notifications_off_hint" to "Notifications are off — turn them on to hear the adhan on time",
            "notifications_permission_needed" to "Athar needs the system notification permission",
            "notifications_permission_action" to "Enable notifications",
            "notifications_exact_needed" to "For adhan at the exact second, enable “Alarms & reminders”",
            "notifications_exact_needed_short" to "Exact scheduling is off — adhan may be a few minutes late",
            "notifications_exact_action" to "Open settings",
            "notifications_per_prayer" to "Adhan for each prayer",
            "notifications_per_prayer_hint" to "Pick adhan, vibrate or silent for each prayer",
            "notifications_reminder" to "Reminder before adhan",
            "notifications_reminder_desc" to "A silent on-screen reminder",
            "notifications_reminder_minutes" to "Before adhan by",
            "notifications_minutes_off" to "No reminder",
            "notifications_jumuah" to "Friday reminder",
            "notifications_jumuah_desc" to "Surah Al-Kahf · Blessings on the Prophet · Ghusl",
            "notifications_jumuah_before" to "Before Friday prayer by",
            "notifications_sound" to "Adhan sound",
            "notifications_sound_desc" to "All sounds are paused until their licensing is verified",
            "notifications_mode_adhan" to "Adhan",
            "notifications_mode_beep" to "Beep",
            "notifications_mode_vibrate" to "Vibrate",
            "notifications_mode_silent" to "Silent",
            "notifications_mode_off" to "Off",
            "notifications_scheduled" to "notification scheduled",
            "notifications_scheduled_many" to "notifications scheduled",
            "notifications_mode_short_adhan" to "Short adhan",
            "notifications_minutes_value" to "{n} min",
            "notifications_scheduled_none" to "No notifications scheduled",
            "athary_open" to "Open Athary",
            "athary_best" to "Best",
            "athary_less" to "Less",
            "athary_more" to "More",
            "athary_next" to "Still ahead of you",
            "athary_reset" to "Clear history",
            "athary_reset_confirm" to "This erases your entire Athary history. There is no undo.",
            "athary_future_note" to "In the full version: a weekly summary, custom goals, and one weekly rest day that preserves your streak.",
            "home_week4_note" to "Prayer times and notifications are live, and Athary tracks your streak locally on your device. The Quran portion and adhkar are still on the way (weeks 5–11).",
            "widget_label" to "Athar widget",
            "widget_description" to "The next prayer and today’s times on your home screen",
            "widget_next_prayer" to "Next",
            "widget_in" to "in",
            "widget_no_data" to "Open Athar to calculate prayer times",
            "widget_add" to "Add the widget",
            "widget_add_hint" to "Long-press your home screen → Widgets → Athar",
            "quran_tab_surah" to "Surahs",
            "quran_tab_juz" to "Juz",
            "quran_tab_page" to "Pages",
            "quran_empty_title" to "The Quran is not on your device yet",
            "quran_empty_body" to "The Quranic text is not bundled with the app — a deliberate choice to protect and verify the text. Import one file once, and it works fully offline afterwards.",
            "quran_import_action" to "Import Quran file",
            "quran_importing" to "Importing…",
            "quran_import_failed" to "Import failed",
            "quran_reader_settings" to "Reading settings",
            "quran_font_size" to "Font size",
            "quran_font_smaller" to "Smaller",
            "quran_font_bigger" to "Bigger",
            "quran_reading_theme" to "Reading theme",
            "quran_theme_paper" to "Paper",
            "quran_theme_sepia" to "Sepia",
            "quran_theme_night" to "Night",
            "quran_show_translation" to "Show translation",
            "quran_no_translation" to "No translation loaded",
            "quran_edition" to "Translation",
            "quran_page_word" to "Page",
            "quran_sajda" to "Prostration",
            "quran_search_title" to "Search the Quran",
            "quran_search_hint" to "Type a word, a verse (2:255), or “p. 42”",
            "quran_search_empty" to "No results",
            "quran_results_count" to "{n} results",
            "quran_no_ayahs" to "No verses",
            "audio_listen" to "Listen",
            "audio_stop" to "Stop",
            "audio_reciter" to "Reciter",
            "audio_choose_reciter" to "Choose reciter",
            "audio_reciter_default" to "Default",
            "audio_download" to "Download",
            "audio_download_surah" to "Download surah",
            "audio_download_juz" to "Download juz",
            "audio_download_all" to "Download the whole Quran",
            "audio_downloaded" to "Downloaded",
            "audio_delete_download" to "Delete download",
            "audio_downloading" to "Downloading…",
            "audio_download_progress" to "{done} of {total}",
            "audio_size_estimate" to "≈ {size}",
            "audio_needs_internet" to "First playback needs internet",
            "audio_playback_failed" to "Playback failed",
            "audio_speed" to "Recitation speed",
            "audio_repeat_ayah" to "Repeat ayah",
            "audio_sources" to "Sources & licenses",
            "audio_rights_note" to "Each recitation’s rights belong to its reciter",
            "audio_licensed_noncommercial" to "Permitted for non-commercial use",
            "audio_no_bundled_audio" to "The app ships no audio — recitations stream or download on demand",
            "audio_cancel" to "Cancel",
            "audio_delete_confirm" to "Delete {name}’s recitation from this device?",
            "audio_storage_used" to "Storage used: {size}",
            "back" to "Back",
            "change" to "Change",
            "north" to "North",
            "south" to "South",
            "east" to "East",
            "west" to "West",
            "adhkar_search_hint" to "Search categories",
            "adhkar_items_count" to "{n} adhkar",
            "prayer_my_location" to "My location",
            "prayer_none_left" to "No more prayers today",
            "qibla_no_sensor" to "This device has no compass",
            "qibla_bearing_from_north" to "Qibla bearing from north: {deg}°",
            "qibla_device_direction" to "Phone direction",
            "qibla_aligned" to "You are facing the Qibla ✓",
            "qibla_turn_right" to "Turn the phone right {deg}°",
            "qibla_turn_left" to "Turn the phone left {deg}°",
            "qibla_bearing_geo" to "Qibla is at {deg}° from true north",
            "quran_load_failed" to "Failed to load the Quran",
            "quran_uthmani_source" to "Uthmani text: {source}",
            "minutes_short" to "{n} min",
            "habit_fajr" to "Fajr on time",
            "habit_five_prayers" to "Five daily prayers",
            "habit_quran" to "Quran portion",
            "habit_morning_adhkar" to "Morning adhkar",
            "habit_evening_adhkar" to "Evening adhkar",
            "habit_duha" to "Duha prayer",
            "habit_sadaqa" to "Charity",
            "habit_fasting" to "Fasting",
            "habit_tasbih" to "Completed tasbih",
            "athary_share" to "Share the week summary",
            "share_week" to "This week",
            "share_active_days" to "Active days",
            "share_acts" to "Acts",
            "share_streak" to "Day streak",
            "share_footer" to "Leave your trace — Athar",
            "share_text" to "This is my week in Athar.",
            "share_failed" to "Could not create the image.",
            "ruqya_title" to "Ruqya — Quran & Adhkar",
            "ruqya_note" to "Nothing new here: a collection of well-known passages from Hisn al-Muslim and the Quran, in their own wording, taken from the app's own sources.",
            "ruqya_quran_title" to "From the Quran",
            "ruqya_adhkar_title" to "From Hisn al-Muslim",
            "ruqya_kursi" to "Ayat al-Kursi",
            "ruqya_baqarah_last" to "The last two verses of Al-Baqarah",
            "ruqya_ikhlas" to "Surah Al-Ikhlas",
            "ruqya_falaq" to "Surah Al-Falaq",
            "ruqya_nas" to "Surah An-Nas",
            "ruqya_play" to "Play the passage",
            "ruqya_missing" to "This passage is not available in this copy.",
            "tracker_today_empty" to "Nothing logged today — start with one",
            "tracker_days" to "days",
            "more_asma" to "The 99 Names",
            "adhkar_after_prayer" to "Adhkar after prayer",
            "asma_search_hint" to "Search by name or meaning",
            "asma_count" to "99 names",
            "asma_empty" to "Nothing matches your search",
            "asma_note" to "Names and meanings are from Saeed al-Qahtani’s book on the Beautiful Names. Meanings are Arabic-only: we found no licensed source for translations, and we will not author religious text ourselves.",
            "method_egyptian" to "Egyptian Survey Authority",
            "method_umm_al_qura" to "Umm al-Qura — Makkah",
            "method_mwl" to "Muslim World League",
            "method_isna" to "ISNA — North America",
            "method_france" to "France 15°",
            "method_custom" to "Custom",
            "notif_channel_adhkar" to "Adhkar",
            "notif_channel_adhkar_desc" to "Reminder for morning and evening adhkar",
            "notif_morning_adhkar_title" to "Morning adhkar",
            "notif_morning_adhkar_body" to "Time for the morning adhkar",
            "notif_evening_adhkar_title" to "Evening adhkar",
            "notif_evening_adhkar_body" to "Time for the evening adhkar",
            "settings_morning_adhkar" to "Morning adhkar reminder",
            "settings_evening_adhkar" to "Evening adhkar reminder",
            "settings_adhkar_offset" to "Minutes after the adhan",
            "quran_translation" to "Translation",
            "quran_translation_none" to "Arabic only",
            "quran_translation_pickthall" to "Pickthall",
            "quran_translation_itani" to "Itani",
            "quran_credit_pickthall" to "The Meaning of the Glorious Koran — Marmaduke Pickthall, 1930. Public domain.",
            "quran_credit_itani" to "The Clear Quran — Talal Itani. CC BY-ND 4.0, shown verbatim and unmodified.",
            "quran_juz" to "Juz’",
            "quran_page" to "Page",
            "quran_reciter" to "Reciter",
            "quran_play" to "Play surah",
            "quran_pause" to "Pause",
            "quran_resume" to "Resume",
            "quran_stop" to "Stop",
            "quran_audio_credit" to "Recitation: islamic.network (alquran.cloud) — rights belong to each reciter.",
            "quran_audio_error" to "Could not play the recitation — check your connection.",
            "quran_audio_loading" to "Loading recitation…",
            "theme_mode" to "Theme",
            "theme_system" to "Automatic (follow system)",
            "font_small" to "Small",
            "font_normal" to "Normal",
            "font_large" to "Large",
            "font_huge" to "Very large",
            "reader_theme" to "Reading theme",
            "reader_normal" to "Normal",
            "reader_night" to "Night",
            "reader_sepia" to "Sepia",
            "font_preview" to "This is a preview of the font size",
            "notif_channel_playback" to "Recitation",
            "notif_channel_playback_desc" to "Now-playing notification and its controls",
            "quran_download" to "Download",
            "quran_download_done" to "Saved on device",
            "quran_delete_download" to "Delete download",
            "quran_downloading" to "Downloading…",
            "quran_download_hint" to "Download is available for reciters who stream the full surah as one file",
            "quran_offline_ready" to "Plays without a connection",
            "quran_bookmarks" to "Favourites",
            "quran_bookmark_add" to "Save verse",
            "quran_bookmark_remove" to "Remove from favourites",
            "quran_bookmarks_empty" to "No saved verses yet",
            "quran_bookmarks_empty_body" to "Tap the bookmark next to any verse to find it here",
            "adhkar_mine" to "My adhkar",
            "adhkar_mine_body" to "Adhkar you write yourself — with no source reference, since they are yours and not from the book",
            "adhkar_add" to "Add",
            "adhkar_add_hint" to "Write the dhikr",
            "adhkar_repeat_count" to "Repetitions",
            "adhkar_mine_empty" to "Nothing written yet",
            "adhkar_delete" to "Delete",
            "tasbih_undo" to "Undo",
            "tasbih_reset" to "Reset",
            "tasbih_rounds" to "Rounds",
            "tasbih_total" to "Total",
            "tab_tasbih" to "Tasbih",
            "hijri_month_1" to "Muharram",
            "hijri_month_2" to "Safar",
            "hijri_month_3" to "Rabi al-Awwal",
            "hijri_month_4" to "Rabi al-Thani",
            "hijri_month_5" to "Jumada al-Ula",
            "hijri_month_6" to "Jumada al-Akhirah",
            "hijri_month_7" to "Rajab",
            "hijri_month_8" to "Shaban",
            "hijri_month_9" to "Ramadan",
            "hijri_month_10" to "Shawwal",
            "hijri_month_11" to "Dhul Qadah",
            "hijri_month_12" to "Dhul Hijjah",
            "hijri_adjust" to "Hijri adjustment",
            "hijri_adjust_body" to "Some countries sight the crescent before others. Pick the offset if your date differs.",
            "hijri_today" to "Today",
            "quran_ayah" to "Verse",
            "khatmah_title" to "Khatmah",
            "khatmah_start" to "Start a khatmah",
            "khatmah_plan" to "Duration",
            "khatmah_days" to "{n} days",
            "khatmah_today" to "Today’s portion",
            "khatmah_from" to "From",
            "khatmah_to" to "To",
            "khatmah_read" to "Read the portion",
            "khatmah_done" to "Mark as read",
            "khatmah_progress" to "{n} of {m} done",
            "khatmah_restart" to "Restart",
            "khatmah_stop" to "Cancel plan",
            "khatmah_finished" to "You completed the khatmah",
            "khatmah_finished_body" to "All praise is for God. Begin another whenever you like.",
            "khatmah_per_day" to "{n} verses a day",
            "event_hijri_new_year" to "Islamic New Year",
            "event_ashura" to "Ashura",
            "event_mawlid" to "The Prophet’s Birthday",
            "event_isra" to "Isra and Mi’raj",
            "event_ramadan" to "Ramadan",
            "event_qadr" to "Laylat al-Qadr",
            "event_eid_fitr" to "Eid al-Fitr",
            "event_arafah" to "Day of Arafah",
            "event_eid_adha" to "Eid al-Adha",
            "event_days_left" to "{n} days to go",
            "event_today" to "Today",
            "event_tomorrow" to "Tomorrow",
            "hl_title" to "High latitudes",
            "hl_middle" to "Middle of night",
            "hl_seventh" to "Seventh of night",
            "hl_twilight" to "Twilight angle",
            "month_prev" to "Previous month",
            "month_next" to "Next month",
            "backup_title" to "Backup",
            "backup_body" to "Everything you own in Athar: prayer times and their settings, last reading position, bookmarks, your khatmah plan, the tracker, the counter, and the adhkar you added.",
            "backup_export" to "Export backup",
            "backup_import" to "Restore from file",
            "backup_exported" to "Backup saved",
            "backup_restored" to "Backup restored",
            "backup_bad_file" to "This file is not an Athar backup, or it is from a newer version",
            "backup_failed" to "The operation failed",
            "hifz_title" to "Memorization",
            "hifz_body" to "The app hides the verse word by word each time you repeat it, until you recall it from memory alone.",
            "hifz_from" to "From",
            "hifz_to" to "To",
            "hifz_surah" to "Surah",
            "hifz_ayah" to "Verse",
            "hifz_count" to "{n} verses",
            "hifz_start" to "Start memorizing",
            "hifz_change" to "Change range",
            "hifz_reveal" to "Reveal",
            "hifz_hide" to "Hide",
            "hifz_repeat" to "I repeated it",
            "hifz_memorized" to "Memorized",
            "hifz_progress" to "{a} of {b}",
            "hifz_repeats_of" to "Repeat {a} of {b}",
            "hifz_finished" to "You memorized the whole range",
            "quran_hizb" to "Hizbs — two per juz",
            "ramadan_day" to "Day {a} of Ramadan",
            "ramadan_iftar" to "Iftar",
            "ramadan_suhur" to "Suhoor ends",
            "ramadan_last_ten" to "The last ten nights",
            "weekly_title" to "Weekly summary",
            "weekly_range" to "{a} — {b}",
            "weekly_acts" to "{n} acts in {d} days",
            "weekly_completion" to "Completion: {n}%",
            "weekly_better" to "More than the week before",
            "weekly_worse" to "Less than the week before",
            "weekly_same" to "Same as the week before",
            "weekly_streak" to "Streak now: {n} days",
            "weekly_best_streak" to "Best streak: {n} days",
            "weekly_top" to "What you did most",
            "adjust_title" to "Adjust the times",
            "adjust_body" to "If Athar’s calculation differs from your mosque, shift it by minutes. Thirty is the cap: beyond that it is not a time difference.",
            "adjust_reset" to "Reset all",
            "adjust_minutes" to "{n} min",
            "adjust_open" to "Adjust",
            "mushaf_mode" to "Mushaf mode",
            "mushaf_page" to "Page {n}",
            "mushaf_jump" to "Go to page",
            "mushaf_of" to "of 604",
            "mushaf_note" to "Page boundaries follow the Madinah mushaf (Hafs). The text is Uthmani, unchanged.",
            "mushaf_open" to "Mushaf mode",
            "mushaf_next" to "Next",
            "mushaf_prev" to "Previous",
            "surah_filter_all" to "All",
            "surah_filter_meccan" to "Meccan",
            "surah_filter_medinan" to "Medinan",
            "surah_sections" to "Surahs",
            "prayer_remaining" to "Remaining",
            "zakah_title" to "Zakah calculator",
            "zakah_gold" to "Gold",
            "zakah_silver" to "Silver",
            "zakah_cash" to "Cash",
            "zakah_fitr" to "Zakat al-Fitr",
            "zakah_grams" to "Grams",
            "zakah_amount" to "Amount",
            "zakah_people" to "Number of people",
            "zakah_price" to "Price per gram",
            "zakah_food_price" to "Price per kilo",
            "zakah_nisab" to "Nisab",
            "zakah_due" to "Due",
            "zakah_not_liable" to "Below the nisab",
            "zakah_liable" to "Nisab reached",
            "zakah_note" to "You enter the prices: gold and silver change every day, and a price baked into the app gives a wrong zakah with confidence.",
            "zakah_source" to "Nisab: 85 g of gold or 595 g of silver, and 2.5 kg of staple food per person for Fitr.",
            "qibla_sun_title" to "Qibla by the sun",
            "qibla_sun_body" to "No compass needed: twice a year the sun rises exactly in the qibla direction. On those days, face the east — and you face the Kaaba.",
            "qibla_sun_days" to "Sunrise days",
            "qibla_sun_none" to "In this year, no day brings the sun up in the qibla direction from where you are.",
            "qibla_sun_shadow" to "Shadow time today",
            "qibla_sun_shadow_none" to "The shadow does not reach the qibla today.",
            "qibla_sun_now" to "The sun is now at {alt}° altitude, {az}° bearing",
            "qibla_sun_pointing" to "Your shadow points to the qibla now",
            "tasbih_sessions" to "Session log",
            "tasbih_today" to "Today",
            "tasbih_best" to "Longest session",
            "tasbih_sessions_count" to "{n} sessions",
            "tasbih_counts" to "{n} counts",
            "tasbih_no_sessions" to "No sessions yet.",
            "tasbih_speak" to "Speak the count",
            "tasbih_sessions_today" to "Sessions today",
            "tasbih_week" to "Last 7 days",
            "tasbih_speak_hint" to "Speaks the count aloud on every tap — turn it off if it gets in the way.",
            "tasbih_volume" to "Count with volume buttons",
            "tasbih_volume_hint" to "Volume buttons count a dhikr instead of changing the volume — change the volume in the device settings.",
            "silence_title" to "Silence during prayer",
            "silence_body" to "Mutes media and vibration during the prayer window. It starts two minutes after the adhan so the adhan itself stays audible, and it ends when the window ends. It stays off until you allow it.",
            "silence_need_access" to "The app needs “Do Not Disturb” access to mute media and notifications. Tap to allow it in the system settings.",
            "silence_allow" to "Allow access",
            "silence_minutes" to "Silence duration",
            "month_share" to "Share the timetable",
            "month_share_text" to "The prayer timetable.",
            "month_share_footer" to "Prayer times — {place} — Athar",
        )

        val kStringsFr: Map<String, String> = mapOf(
            "app_name" to "Athar",
            "tagline" to "Laisse une trace de bien",
            "hi_morning" to "Bonjour",
            "hi_afternoon" to "Bon après-midi",
            "hi_evening" to "Bonsoir",
            "hi_night" to "Bonne nuit",
            "tab_home" to "Accueil",
            "tab_quran" to "Coran",
            "tab_adhkar" to "Invocations",
            "tab_qibla" to "Qibla",
            "tab_more" to "Plus",
            "next_prayer" to "Prochaine prière",
            "remaining" to "restant",
            "today" to "Aujourd’hui",
            "now" to "Maintenant",
            "madhab_label" to "Madhab",
            "shafi" to "Chaféite",
            "hanafi" to "Hanafite",
            "fajr" to "Fajr",
            "sunrise" to "Lever",
            "sunrise_no_adhan" to "Lever du soleil : pas d’adhan — ce n’est pas une prière",
            "dhuhr" to "Dohr",
            "asr" to "Asr",
            "maghrib" to "Maghreb",
            "isha" to "Icha",
            "prayer_times" to "Horaires",
            "monthly_calendar" to "Calendrier mensuel",
            "calc_settings" to "Paramètres de calcul",
            "notifications" to "Notifications",
            "adhan_sound" to "Son de l’adhan",
            "reminder_before" to "Rappel avant la prière",
            "continue_reading" to "Continuer la lecture",
            "ayah_no" to "Verset",
            "wird_title" to "Ma part du jour",
            "wird_sub" to "Plan de khatma sur 30 jours",
            "adhkar_today" to "Invocations du jour",
            "done" to "Terminé",
            "of" to "sur",
            "quick_actions" to "Actions rapides",
            "verse_of_day" to "Verset du jour",
            "qa_quran" to "Coran",
            "qa_adhkar" to "Invocations",
            "qa_qibla" to "Qibla",
            "qa_tasbih" to "Tasbih",
            "qa_duas" to "Douas",
            "qa_names" to "Noms d’Allah",
            "quran_index" to "Index des sourates",
            "search_ph" to "Rechercher sourates & versets…",
            "filter_all" to "Tous",
            "filter_meccan" to "Mecquoises",
            "filter_medinan" to "Médinoises",
            "verses" to "versets",
            "juz" to "Juz",
            "meccan" to "Mecquoise",
            "medinan" to "Médinoise",
            "reader_settings" to "Paramètres de lecture",
            "font_size" to "Taille du texte",
            "translation" to "Traduction",
            "tafsir" to "Tafsir",
            "reciter" to "Récitateur",
            "none" to "Aucune",
            "play_surah" to "Écouter",
            "repeat" to "Répéter",
            "speed" to "Vitesse",
            "download_offline" to "Télécharger hors ligne",
            "save_mark" to "Enregistrer",
            "bookmark" to "Marque-page",
            "note" to "Note",
            "color" to "Surligner",
            "share" to "Partager",
            "copy" to "Copier",
            "saved_msg" to "Enregistré",
            "bookmarks" to "Enregistrés",
            "adhkar_cat" to "Catégories",
            "morning" to "Invocations du matin",
            "evening" to "Invocations du soir",
            "after_prayer" to "Après la prière",
            "sleep" to "Avant le sommeil",
            "wake" to "Au réveil",
            "food" to "Repas",
            "mosque" to "Mosquée",
            "travel" to "Voyage",
            "anxiety" to "Angoisse & tristesse",
            "rain" to "Pluie",
            "count_of" to "Nombre",
            "tap_to_count" to "Appuyez pour compter",
            "complete_all" to "Série terminée",
            "reset" to "Réinitialiser",
            "dua_library" to "Bibliothèque de douas",
            "source" to "Source",
            "names_title" to "Les 99 Noms d’Allah",
            "names_sub" to "Touchez un nom pour l’écouter",
            "qibla_title" to "Direction de la Qibla",
            "qibla_hint" to "Orientez le téléphone et attendez",
            "distance" to "Distance vers La Mecque",
            "bearing" to "Azimut de la Qibla",
            "calibrate" to "Bougez l’appareil en ∞ pour calibrer",
            "simulate" to "Simuler la rotation",
            "how_without" to "Trouver la Qibla sans boussole ?",
            "tasbih_title" to "Tasbih",
            "target" to "Objectif",
            "tracker_title" to "Ma trace",
            "tracker_sub" to "Suivi des adorations",
            "streak_days" to "jours consécutifs",
            "this_week" to "Cette semaine",
            "last_30" to "30 derniers jours",
            "habits" to "Mes habitudes",
            "calendar_title" to "Calendrier & événements",
            "hijri" to "Date hégirienne",
            "gregorian" to "Date grégorienne",
            "upcoming" to "Événements à venir",
            "more_title" to "Plus",
            "settings" to "Paramètres",
            "settings_title" to "Paramètres",
            "about" to "À propos",
            "share_app" to "Partager l’app",
            "rate_app" to "Noter l’app",
            "general" to "Général",
            "language" to "Langue",
            "theme" to "Thème",
            "text_size" to "Taille du texte",
            "backup" to "Sauvegarde",
            "sources" to "Sources & licences",
            "privacy" to "Confidentialité",
            "theme_light" to "Clair",
            "theme_dark" to "Sombre",
            "theme_auto" to "Auto",
            "size_small" to "Petit",
            "size_normal" to "Normal",
            "size_large" to "Grand",
            "size_xlarge" to "Très grand",
            "appearance" to "Apparence & langue",
            "arabic_digits" to "Chiffres arabo-indiens",
            "arabic_digits_sub" to "Afficher les nombres ١٢٣ au lieu de 123",
            "system_default" to "Selon le système",
            "about_title" to "À propos d’Athar",
            "about_body" to "Une application islamique complète, hors ligne, sans publicité ni collecte de données. Tout le contenu est sourcé, et les horaires sont calculés astronomiquement sur votre appareil.",
            "empty_bm_t" to "Aucun marque-page",
            "empty_bm_s" to "Appui long sur un verset pour l’enregistrer",
            "empty_note_t" to "Aucune note",
            "save" to "Enregistrer",
            "cancel" to "Annuler",
            "apply" to "Appliquer",
            "ok" to "OK",
            "retry" to "Réessayer",
            "loading" to "Chargement…",
            "error_generic" to "Une erreur est survenue",
            "on" to "Activé",
            "off" to "Désactivé",
            "coming_soon" to "Bientôt",
            "demo_data" to "Données de démo",
            "onboarding_title" to "Athar",
            "onboarding_subtitle" to "Une seule application islamique pour la prière, le Coran, les invocations et la Qibla — hors ligne et sans publicité.",
            "choose_language" to "Choisissez votre langue",
            "start" to "Commencer",
            "skip" to "Passer",
            "design_system" to "Design system",
            "ds_colors" to "Couleurs",
            "ds_typography" to "Typographie",
            "ds_spacing" to "Espacements",
            "ds_radii" to "Rayons",
            "ds_components" to "Composants",
            "ds_buttons" to "Boutons",
            "ds_inputs" to "Champs",
            "ds_feedback" to "Retours",
            "week1_note" to "Les bases sont prêtes : structure, design system et trois langues.",
            "coming_week2" to "Les vrais horaires arrivent en semaine 2",
            "prayer_soon_title" to "Horaires bientôt",
            "prayer_soon_body" to "Le calcul astronomique des horaires (10+ méthodes, madhab, ajustements manuels) est en cours en semaine 2.",
            "quran_soon_title" to "Le Coran bientôt",
            "quran_soon_body" to "L’index des 114 sourates et le lecteur en texte othmani, avec tafsir et récitation — semaines 2 et 3.",
            "adhkar_soon_title" to "Invocations bientôt",
            "adhkar_soon_body" to "Dix catégories d’invocations avec compteur par dhikr, et une bibliothèque de douas — semaine 3.",
            "qibla_soon_title" to "Boussole bientôt",
            "qibla_soon_body" to "La boussole Qibla avec fusion de capteurs, calibration et méthode solaire de secours — semaine 3.",
            "prayer_settings_title" to "Paramètres des horaires de prière",
            "prayer_times_title" to "Horaires de prière",
            "prayer_settings_open" to "Horaires de prière",
            "prayer_engine_credit" to "Calcul astronomique : Adhan Dart",
            "prayer_location" to "Localisation",
            "prayer_choose_city" to "Choisir une ville",
            "prayer_use_my_location" to "Utiliser ma position",
            "prayer_location_rationale" to "L’autorisation de localisation est nécessaire pour des horaires précis. Votre position n’est jamais stockée ni envoyée.",
            "prayer_location_updated" to "Position mise à jour",
            "prayer_location_denied" to "Autorisation de localisation refusée — vous pouvez choisir une ville manuellement.",
            "prayer_location_denied_forever" to "Autorisation refusée définitivement. Ouvrez les paramètres de l’application pour l’activer.",
            "prayer_location_service_off" to "Le service de localisation est désactivé sur cet appareil.",
            "prayer_location_timeout" to "Impossible de déterminer votre position. Réessayez ou choisissez une ville.",
            "prayer_location_unknown" to "Une erreur inattendue est survenue lors de la localisation.",
            "prayer_open_settings" to "Ouvrir les paramètres",
            "prayer_method" to "Méthode de calcul",
            "prayer_madhab" to "Madhab",
            "prayer_madhab_sub" to "N’affecte que l’horaire du Asr (longueur de l’ombre).",
            "prayer_high_latitude" to "Règle des hautes latitudes",
            "prayer_high_latitude_sub" to "Pour les régions où le Fajr ou l’Icha ne sont pas clairement définis (Europe, Amérique du Nord).",
            "prayer_adjustments" to "Ajustement manuel",
            "prayer_adjustments_sub" to "Ajoutez ou retirez des minutes si votre mosquée locale diffère du calcul.",
            "prayer_adjusted_notice" to "Les horaires sont ajustés manuellement — ce n’est pas le calcul astronomique brut.",
            "prayer_clear_adjustments" to "Effacer tous les ajustements",
            "prayer_next_label" to "Prochaine prière",
            "prayer_tomorrow_fajr" to "Fajr demain",
            "prayer_now" to "Maintenant",
            "prayer_upcoming" to "Prochaine",
            "prayer_unavailable" to "Impossible de calculer les horaires de prière",
            "city_search_hint" to "Rechercher une ville…",
            "city_no_results" to "Aucun résultat",
            "city_no_results_hint" to "Essayez le nom en arabe, en anglais ou en français.",
            "time_am" to "AM",
            "time_pm" to "PM",
            "home_week2_note" to "Les horaires sont désormais calculés astronomiquement sur votre appareil, hors ligne. Le wird, les invocations et le suivi arrivent ensuite.",
            "notif_athan_title" to "C'est l'heure de {prayer}",
            "notif_athan_body" to "{time} — {place}",
            "notif_reminder_title" to "{prayer} bientôt",
            "notif_reminder_body" to "{n} minutes avant l’adhan",
            "notif_jumuah_title" to "Vendredi",
            "notif_jumuah_body" to "Sourate Al-Kahf · Prière sur le Prophète · Ghusl",
            "notif_channel_athan" to "Adhan",
            "notif_channel_athan_desc" to "Adhan au début de chaque prière",
            "notif_channel_reminder" to "Rappel avant la prière",
            "notif_channel_reminder_desc" to "Un rappel silencieux quelques minutes avant l’adhan",
            "notif_channel_jumuah" to "Rappel du vendredi",
            "notif_channel_jumuah_desc" to "Un rappel hebdomadaire le vendredi",
            "notifications_test_title" to "Notification de test",
            "notifications_test_body" to "Si vous voyez ceci, les notifications fonctionnent.",
            "notifications_master" to "Activer les notifications",
            "notifications_master_desc" to "Si désactivé, Athar n’envoie rien du tout",
            "notifications_test" to "Envoyer une notification de test",
            "notifications_test_queued" to "Une notification de test arrivera dans 5 secondes",
            "notifications_off_hint" to "Notifications désactivées — activez-les pour entendre l’adhan à l’heure",
            "notifications_permission_needed" to "Athar a besoin de l’autorisation système",
            "notifications_permission_action" to "Activer les notifications",
            "notifications_exact_needed" to "Pour un adhan à la seconde près, activez « Alarmes et rappels »",
            "notifications_exact_needed_short" to "Planification exacte désactivée — l’adhan peut avoir quelques minutes de retard",
            "notifications_exact_action" to "Ouvrir les paramètres",
            "notifications_per_prayer" to "Adhan pour chaque prière",
            "notifications_per_prayer_hint" to "Choisissez adhan, vibreur ou silencieux pour chaque prière",
            "notifications_reminder" to "Rappel avant l’adhan",
            "notifications_reminder_desc" to "Un rappel silencieux à l’écran",
            "notifications_reminder_minutes" to "Avant l’adhan de",
            "notifications_minutes_off" to "Aucun rappel",
            "notifications_jumuah" to "Rappel du vendredi",
            "notifications_jumuah_desc" to "Sourate Al-Kahf · Prière sur le Prophète · Ghusl",
            "notifications_jumuah_before" to "Avant la prière du vendredi de",
            "notifications_sound" to "Son de l’adhan",
            "notifications_sound_desc" to "Tous les sons sont suspendus jusqu’à vérification de leurs licences",
            "notifications_mode_adhan" to "Adhan",
            "notifications_mode_beep" to "Bip",
            "notifications_mode_vibrate" to "Vibreur",
            "notifications_mode_silent" to "Silencieux",
            "notifications_mode_off" to "Désactivé",
            "notifications_scheduled" to "notification planifiée",
            "notifications_scheduled_many" to "notifications planifiées",
            "notifications_mode_short_adhan" to "Adhan court",
            "notifications_minutes_value" to "{n} min",
            "notifications_scheduled_none" to "Aucune notification planifiée",
            "athary_open" to "Ouvrir Athary",
            "athary_best" to "Record",
            "athary_less" to "Moins",
            "athary_more" to "Plus",
            "athary_next" to "Il vous reste",
            "athary_reset" to "Effacer l’historique",
            "athary_reset_confirm" to "Ceci efface tout votre historique Athary. Aucun retour possible.",
            "athary_future_note" to "Dans la version complète : un résumé hebdomadaire, des objectifs personnalisés, et un jour de repos par semaine qui préserve votre série.",
            "home_week4_note" to "Les horaires et les notifications sont actifs, et Athary suit votre série localement sur votre appareil. La part de Coran et les invocations arrivent plus tard (semaines 5–11).",
            "widget_label" to "Widget Athar",
            "widget_description" to "La prochaine prière et les horaires du jour sur votre écran d’accueil",
            "widget_next_prayer" to "Prochaine",
            "widget_in" to "dans",
            "widget_no_data" to "Ouvrez Athar pour calculer les horaires",
            "widget_add" to "Ajouter le widget",
            "widget_add_hint" to "Appui long sur l’écran d’accueil → Widgets → Athar",
            "quran_tab_surah" to "Sourates",
            "quran_tab_juz" to "Juz",
            "quran_tab_page" to "Pages",
            "quran_empty_title" to "Le Coran n’est pas encore sur votre appareil",
            "quran_empty_body" to "Le texte coranique n’est pas fourni avec l’application — un choix délibéré pour protéger et vérifier le texte. Importez un fichier une seule fois, puis tout fonctionne hors ligne.",
            "quran_import_action" to "Importer le fichier Coran",
            "quran_importing" to "Importation…",
            "quran_import_failed" to "Échec de l’import",
            "quran_reader_settings" to "Paramètres de lecture",
            "quran_font_size" to "Taille du texte",
            "quran_font_smaller" to "Plus petit",
            "quran_font_bigger" to "Plus grand",
            "quran_reading_theme" to "Thème de lecture",
            "quran_theme_paper" to "Papier",
            "quran_theme_sepia" to "Sépia",
            "quran_theme_night" to "Nuit",
            "quran_show_translation" to "Afficher la traduction",
            "quran_no_translation" to "Aucune traduction chargée",
            "quran_edition" to "Traduction",
            "quran_page_word" to "Page",
            "quran_sajda" to "Prosternation",
            "quran_search_title" to "Rechercher dans le Coran",
            "quran_search_hint" to "Tapez un mot, un verset (2:255) ou « p. 42 »",
            "quran_search_empty" to "Aucun résultat",
            "quran_results_count" to "{n} résultats",
            "quran_no_ayahs" to "Aucun verset",
            "audio_listen" to "Écouter",
            "audio_stop" to "Arrêter",
            "audio_reciter" to "Récitateur",
            "audio_choose_reciter" to "Choisir le récitateur",
            "audio_reciter_default" to "Par défaut",
            "audio_download" to "Télécharger",
            "audio_download_surah" to "Télécharger la sourate",
            "audio_download_juz" to "Télécharger le juz",
            "audio_download_all" to "Télécharger tout le Coran",
            "audio_downloaded" to "Téléchargé",
            "audio_delete_download" to "Supprimer le téléchargement",
            "audio_downloading" to "Téléchargement…",
            "audio_download_progress" to "{done} sur {total}",
            "audio_size_estimate" to "≈ {size}",
            "audio_needs_internet" to "La première écoute nécessite Internet",
            "audio_playback_failed" to "Échec de la lecture",
            "audio_speed" to "Vitesse de récitation",
            "audio_repeat_ayah" to "Répéter le verset",
            "audio_sources" to "Sources et licences",
            "audio_rights_note" to "Les droits de chaque récitation appartiennent à son récitateur",
            "audio_licensed_noncommercial" to "Autorisé pour un usage non commercial",
            "audio_no_bundled_audio" to "L’application n’intègre aucun audio",
            "audio_cancel" to "Annuler",
            "audio_delete_confirm" to "Supprimer la récitation de {name} ?",
            "audio_storage_used" to "Espace utilisé : {size}",
            "back" to "Retour",
            "change" to "Changer",
            "north" to "Nord",
            "south" to "Sud",
            "east" to "Est",
            "west" to "Ouest",
            "adhkar_search_hint" to "Rechercher une catégorie",
            "adhkar_items_count" to "{n} invocations",
            "prayer_my_location" to "Ma position",
            "prayer_none_left" to "Plus de prières aujourd’hui",
            "qibla_no_sensor" to "Cet appareil n’a pas de boussole",
            "qibla_bearing_from_north" to "Direction depuis le nord : {deg}°",
            "qibla_device_direction" to "Direction du téléphone",
            "qibla_aligned" to "Vous êtes face à la Qibla ✓",
            "qibla_turn_right" to "Tournez le téléphone à droite de {deg}°",
            "qibla_turn_left" to "Tournez le téléphone à gauche de {deg}°",
            "qibla_bearing_geo" to "La Qibla est à {deg}° du nord géographique",
            "quran_load_failed" to "Échec du chargement du Coran",
            "quran_uthmani_source" to "Texte othmanien : {source}",
            "minutes_short" to "{n} min",
            "habit_fajr" to "Fajr à l’heure",
            "habit_five_prayers" to "Cinq prières",
            "habit_quran" to "Part de Coran",
            "habit_morning_adhkar" to "Invocations du matin",
            "habit_evening_adhkar" to "Invocations du soir",
            "habit_duha" to "Prière de Doha",
            "habit_sadaqa" to "Aumône",
            "habit_fasting" to "Jeûne",
            "habit_tasbih" to "Tasbih complet",
            "athary_share" to "Partager le résumé de la semaine",
            "share_week" to "Cette semaine",
            "share_active_days" to "Jours actifs",
            "share_acts" to "Actes",
            "share_streak" to "Jours d’affilée",
            "share_footer" to "Laisse ta trace — Athar",
            "share_text" to "Voici ma semaine dans Athar.",
            "share_failed" to "Impossible de créer l’image.",
            "ruqya_title" to "Ruqya — Coran & Adhkar",
            "ruqya_note" to "Rien de nouveau ici : un choix de passages connus du Hisn al-Muslim et du Coran, dans leurs propres mots, puisés aux sources de l'application.",
            "ruqya_quran_title" to "Du Coran",
            "ruqya_adhkar_title" to "Du Hisn al-Muslim",
            "ruqya_kursi" to "L’Ayat al-Kursi",
            "ruqya_baqarah_last" to "Les deux derniers versets d’Al-Baqara",
            "ruqya_ikhlas" to "Sourate Al-Ikhlas",
            "ruqya_falaq" to "Sourate Al-Falaq",
            "ruqya_nas" to "Sourate An-Nas",
            "ruqya_play" to "Lire le passage",
            "ruqya_missing" to "Ce passage n’est pas disponible dans cette version.",
            "tracker_today_empty" to "Rien aujourd’hui — commencez par une",
            "tracker_days" to "jours",
            "more_asma" to "Les 99 Noms",
            "adhkar_after_prayer" to "Adhkar après la prière",
            "asma_search_hint" to "Rechercher par nom ou sens",
            "asma_count" to "99 noms",
            "asma_empty" to "Aucun résultat",
            "asma_note" to "Les noms et leurs sens proviennent du livre de Saeed al-Qahtani sur les Beaux Noms. Les sens sont en arabe uniquement : nous n’avons trouvé aucune source sous licence pour les traductions, et nous n’écrirons pas nous-mêmes un texte religieux.",
            "method_egyptian" to "Autorité égyptienne du cadastre",
            "method_umm_al_qura" to "Umm al-Qura — La Mecque",
            "method_mwl" to "Ligue islamique mondiale",
            "method_isna" to "ISNA — Amérique du Nord",
            "method_france" to "France 15°",
            "method_custom" to "Personnalisé",
            "notif_channel_adhkar" to "Adhkar",
            "notif_channel_adhkar_desc" to "Rappel des adhkar du matin et du soir",
            "notif_morning_adhkar_title" to "Adhkar du matin",
            "notif_morning_adhkar_body" to "C’est l’heure des adhkar du matin",
            "notif_evening_adhkar_title" to "Adhkar du soir",
            "notif_evening_adhkar_body" to "C’est l’heure des adhkar du soir",
            "settings_morning_adhkar" to "Rappel des adhkar du matin",
            "settings_evening_adhkar" to "Rappel des adhkar du soir",
            "settings_adhkar_offset" to "Minutes après l’adhan",
            "quran_translation" to "Traduction",
            "quran_translation_none" to "Arabe seulement",
            "quran_translation_pickthall" to "Pickthall",
            "quran_translation_itani" to "Itani",
            "quran_credit_pickthall" to "The Meaning of the Glorious Koran — Marmaduke Pickthall, 1930. Domaine public.",
            "quran_credit_itani" to "The Clear Quran — Talal Itani. CC BY-ND 4.0, affiché textuellement et sans modification.",
            "quran_juz" to "Juz",
            "quran_page" to "Page",
            "quran_reciter" to "Récitateur",
            "quran_play" to "Écouter la sourate",
            "quran_pause" to "Pause",
            "quran_resume" to "Reprendre",
            "quran_stop" to "Arrêter",
            "quran_audio_credit" to "Récitation : islamic.network (alquran.cloud) — les droits appartiennent à chaque récitateur.",
            "quran_audio_error" to "Lecture impossible — vérifiez votre connexion.",
            "quran_audio_loading" to "Chargement de la récitation…",
            "theme_mode" to "Thème",
            "theme_system" to "Automatique (système)",
            "font_small" to "Petite",
            "font_normal" to "Moyenne",
            "font_large" to "Grande",
            "font_huge" to "Très grande",
            "reader_theme" to "Thème de lecture",
            "reader_normal" to "Normal",
            "reader_night" to "Nuit",
            "reader_sepia" to "Sépia",
            "font_preview" to "Aperçu de la taille du texte",
            "notif_channel_playback" to "Récitation",
            "notif_channel_playback_desc" to "Notification de lecture en cours et ses commandes",
            "quran_download" to "Télécharger",
            "quran_download_done" to "Enregistré sur l’appareil",
            "quran_delete_download" to "Supprimer le téléchargement",
            "quran_downloading" to "Téléchargement…",
            "quran_download_hint" to "Le téléchargement est disponible pour les récitateurs qui diffusent la sourate entière en un seul fichier",
            "quran_offline_ready" to "Écoute sans connexion",
            "quran_bookmarks" to "Favoris",
            "quran_bookmark_add" to "Enregistrer le verset",
            "quran_bookmark_remove" to "Retirer des favoris",
            "quran_bookmarks_empty" to "Aucun verset enregistré",
            "quran_bookmarks_empty_body" to "Touchez le signet à côté d’un verset pour le retrouver ici",
            "adhkar_mine" to "Mes adhkar",
            "adhkar_mine_body" to "Des adhkar que vous écrivez vous-mêmes — sans référence, car ils sont de vous et non du livre",
            "adhkar_add" to "Ajouter",
            "adhkar_add_hint" to "Écrivez le dhikr",
            "adhkar_repeat_count" to "Répétitions",
            "adhkar_mine_empty" to "Rien d’écrit pour l’instant",
            "adhkar_delete" to "Supprimer",
            "tasbih_undo" to "Annuler",
            "tasbih_reset" to "Réinitialiser",
            "tasbih_rounds" to "Cycles",
            "tasbih_total" to "Total",
            "tab_tasbih" to "Tasbih",
            "hijri_month_1" to "Mouharram",
            "hijri_month_2" to "Safar",
            "hijri_month_3" to "Rabi al-Awwal",
            "hijri_month_4" to "Rabi ath-Thani",
            "hijri_month_5" to "Joumada al-Oula",
            "hijri_month_6" to "Joumada al-Akhira",
            "hijri_month_7" to "Rajab",
            "hijri_month_8" to "Chaabane",
            "hijri_month_9" to "Ramadan",
            "hijri_month_10" to "Chawwal",
            "hijri_month_11" to "Dhoul Qiada",
            "hijri_month_12" to "Dhoul Hijja",
            "hijri_adjust" to "Ajustement hégirien",
            "hijri_adjust_body" to "Certains pays observent le croissant avant d’autres. Choisissez le décalage si votre date diffère.",
            "hijri_today" to "Aujourd’hui",
            "quran_ayah" to "Verset",
            "khatmah_title" to "Khatma",
            "khatmah_start" to "Commencer une khatma",
            "khatmah_plan" to "Durée",
            "khatmah_days" to "{n} jours",
            "khatmah_today" to "Portion du jour",
            "khatmah_from" to "De",
            "khatmah_to" to "À",
            "khatmah_read" to "Lire la portion",
            "khatmah_done" to "Marquer comme lu",
            "khatmah_progress" to "{n} sur {m} accomplis",
            "khatmah_restart" to "Recommencer",
            "khatmah_stop" to "Annuler le plan",
            "khatmah_finished" to "Vous avez achevé la khatma",
            "khatmah_finished_body" to "Louange à Dieu. Commencez-en une autre quand vous voulez.",
            "khatmah_per_day" to "{n} versets par jour",
            "event_hijri_new_year" to "Nouvel an hégirien",
            "event_ashura" to "Achoura",
            "event_mawlid" to "Naissance du Prophète",
            "event_isra" to "Isra et Mi’radj",
            "event_ramadan" to "Ramadan",
            "event_qadr" to "Laylat al-Qadr",
            "event_eid_fitr" to "Aïd al-Fitr",
            "event_arafah" to "Jour de Arafat",
            "event_eid_adha" to "Aïd al-Adha",
            "event_days_left" to "Dans {n} jours",
            "event_today" to "Aujourd’hui",
            "event_tomorrow" to "Demain",
            "hl_title" to "Hautes latitudes",
            "hl_middle" to "Milieu de la nuit",
            "hl_seventh" to "Septième de la nuit",
            "hl_twilight" to "Angle crépusculaire",
            "month_prev" to "Mois précédent",
            "month_next" to "Mois suivant",
            "backup_title" to "Sauvegarde",
            "backup_body" to "Tout ce que vous possédez dans Athar : les horaires et leurs réglages, la dernière position de lecture, les signets, votre plan de khatma, le suivi, le compteur et les invocations ajoutées.",
            "backup_export" to "Exporter la sauvegarde",
            "backup_import" to "Restaurer depuis un fichier",
            "backup_exported" to "Sauvegarde enregistrée",
            "backup_restored" to "Sauvegarde restaurée",
            "backup_bad_file" to "Ce fichier n’est pas une sauvegarde Athar, ou provient d’une version plus récente",
            "backup_failed" to "L’opération a échoué",
            "hifz_title" to "Mémorisation",
            "hifz_body" to "L’application masque le verset mot à mot à chaque répétition, jusqu’à ce que vous le rappeliez de mémoire.",
            "hifz_from" to "De",
            "hifz_to" to "À",
            "hifz_surah" to "Sourate",
            "hifz_ayah" to "Verset",
            "hifz_count" to "{n} versets",
            "hifz_start" to "Commencer",
            "hifz_change" to "Changer la plage",
            "hifz_reveal" to "Afficher",
            "hifz_hide" to "Masquer",
            "hifz_repeat" to "J’ai répété",
            "hifz_memorized" to "Mémorisé",
            "hifz_progress" to "{a} sur {b}",
            "hifz_repeats_of" to "Répétition {a} sur {b}",
            "hifz_finished" to "Vous avez mémorisé toute la plage",
            "quran_hizb" to "Hizbs — deux par juz",
            "ramadan_day" to "Jour {a} du Ramadan",
            "ramadan_iftar" to "Iftar",
            "ramadan_suhur" to "Fin du souhour",
            "ramadan_last_ten" to "Les dix dernières nuits",
            "weekly_title" to "Résumé hebdomadaire",
            "weekly_range" to "{a} — {b}",
            "weekly_acts" to "{n} actes en {d} jours",
            "weekly_completion" to "Achèvement : {n} %",
            "weekly_better" to "Plus que la semaine précédente",
            "weekly_worse" to "Moins que la semaine précédente",
            "weekly_same" to "Comme la semaine précédente",
            "weekly_streak" to "Série actuelle : {n} jours",
            "weekly_best_streak" to "Meilleure série : {n} jours",
            "weekly_top" to "Ce que vous avez le plus fait",
            "adjust_title" to "Ajuster les horaires",
            "adjust_body" to "Si le calcul d’Athar diffère de votre mosquée, décalez-le de quelques minutes. Trente est le plafond : au-delà, ce n’est plus un écart d’horaire.",
            "adjust_reset" to "Tout réinitialiser",
            "adjust_minutes" to "{n} min",
            "adjust_open" to "Ajuster",
            "mushaf_mode" to "Mode mushaf",
            "mushaf_page" to "Page {n}",
            "mushaf_jump" to "Aller à la page",
            "mushaf_of" to "sur 604",
            "mushaf_note" to "Les limites des pages suivent le mushaf de Médine (Hafs). Le texte est ourthani, inchangé.",
            "mushaf_open" to "Mode mushaf",
            "mushaf_next" to "Suivante",
            "mushaf_prev" to "Précédente",
            "surah_filter_all" to "Toutes",
            "surah_filter_meccan" to "Mecquoises",
            "surah_filter_medinan" to "Médinoises",
            "surah_sections" to "Sourates",
            "prayer_remaining" to "Restant",
            "zakah_title" to "Calculateur de zakât",
            "zakah_gold" to "Or",
            "zakah_silver" to "Argent",
            "zakah_cash" to "Liquidités",
            "zakah_fitr" to "Zakât al-Fitr",
            "zakah_grams" to "Grammes",
            "zakah_amount" to "Montant",
            "zakah_people" to "Nombre de personnes",
            "zakah_price" to "Prix au gramme",
            "zakah_food_price" to "Prix au kilo",
            "zakah_nisab" to "Nisâb",
            "zakah_due" to "Dû",
            "zakah_not_liable" to "Nisâb non atteint",
            "zakah_liable" to "Nisâb atteint",
            "zakah_note" to "Les prix sont saisis par vous : l’or et l’argent changent chaque jour, et un prix inscrit dans l’application donne un zakât faux avec assurance.",
            "zakah_source" to "Nisâb : 85 g d’or ou 595 g d’argent, et 2,5 kg de nourriture par personne pour le Fitr.",
            "qibla_sun_title" to "Qibla par le soleil",
            "qibla_sun_body" to "Pas besoin de boussole : deux fois par an, le soleil se lève exactement dans la direction de la Qibla. Ces jours-là, tournez-vous vers l’est — vous ferez face à la Kaaba.",
            "qibla_sun_days" to "Jours de lever",
            "qibla_sun_none" to "Cette année, aucun jour ne fait lever le soleil dans la direction de la Qibla d’ici.",
            "qibla_sun_shadow" to "Heure de l’ombre aujourd’hui",
            "qibla_sun_shadow_none" to "L’ombre n’atteint pas la Qibla aujourd’hui.",
            "qibla_sun_now" to "Le soleil est à {alt}° d’altitude, {az}° d’azimut",
            "qibla_sun_pointing" to "Votre ombre pointe vers la Qibla",
            "tasbih_sessions" to "Journal des sessions",
            "tasbih_today" to "Aujourd’hui",
            "tasbih_best" to "Session la plus longue",
            "tasbih_sessions_count" to "{n} sessions",
            "tasbih_counts" to "{n} dhikr",
            "tasbih_no_sessions" to "Aucune session pour l’instant.",
            "tasbih_speak" to "Prononcer le compte",
            "tasbih_sessions_today" to "Sessions du jour",
            "tasbih_week" to "7 derniers jours",
            "tasbih_speak_hint" to "Prononce le compte à chaque appui — désactivez-le s’il gêne.",
            "tasbih_volume" to "Compter avec les boutons de volume",
            "tasbih_volume_hint" to "Les boutons de volume comptent un dhikr au lieu de changer le volume — changez le volume dans les réglages.",
            "silence_title" to "Silence pendant la prière",
            "silence_body" to "Coupe les médias et les vibrations pendant la fenêtre de prière. Commence deux minutes après l’adhan pour que l’adhan reste audible, et finit à la fin de la fenêtre. Désactivé tant que vous ne l’autorisez pas.",
            "silence_need_access" to "L’application a besoin de l’accès « Ne pas déranger » pour couper les médias et les notifications. Touchez pour l’autoriser dans les réglages du système.",
            "silence_allow" to "Autoriser l’accès",
            "silence_minutes" to "Durée du silence",
            "month_share" to "Partager le tableau",
            "month_share_text" to "Le tableau des heures de prière.",
            "month_share_footer" to "Heures de prière — {place} — Athar",
        )
    }

    // ══════════════════════════════════════════════
    // الوصول المكتوب النوع — خطأ في الاسم يظهر وقت
    // التجميع لا وقت التشغيل
    // ══════════════════════════════════════════════

    val appName: String get() = t("app_name")
    val tagline: String get() = t("tagline")
    val hiMorning: String get() = t("hi_morning")
    val hiAfternoon: String get() = t("hi_afternoon")
    val hiEvening: String get() = t("hi_evening")
    val hiNight: String get() = t("hi_night")
    val tabHome: String get() = t("tab_home")
    val tabQuran: String get() = t("tab_quran")
    val tabAdhkar: String get() = t("tab_adhkar")
    val tabQibla: String get() = t("tab_qibla")
    val tabMore: String get() = t("tab_more")
    val nextPrayer: String get() = t("next_prayer")
    val remaining: String get() = t("remaining")
    val today: String get() = t("today")
    val now: String get() = t("now")
    val madhabLabel: String get() = t("madhab_label")
    val shafi: String get() = t("shafi")
    val hanafi: String get() = t("hanafi")
    val fajr: String get() = t("fajr")
    val sunrise: String get() = t("sunrise")
    val sunriseNoAdhan: String get() = t("sunrise_no_adhan")
    val dhuhr: String get() = t("dhuhr")
    val asr: String get() = t("asr")
    val maghrib: String get() = t("maghrib")
    val isha: String get() = t("isha")
    val prayerTimes: String get() = t("prayer_times")
    val monthlyCalendar: String get() = t("monthly_calendar")
    val calcSettings: String get() = t("calc_settings")
    val notifications: String get() = t("notifications")
    val adhanSound: String get() = t("adhan_sound")
    val reminderBefore: String get() = t("reminder_before")
    val continueReading: String get() = t("continue_reading")
    val ayahNo: String get() = t("ayah_no")
    val wirdTitle: String get() = t("wird_title")
    val wirdSub: String get() = t("wird_sub")
    val adhkarToday: String get() = t("adhkar_today")
    val done: String get() = t("done")
    val of: String get() = t("of")
    val quickActions: String get() = t("quick_actions")
    val verseOfDay: String get() = t("verse_of_day")
    val qaQuran: String get() = t("qa_quran")
    val qaAdhkar: String get() = t("qa_adhkar")
    val qaQibla: String get() = t("qa_qibla")
    val qaTasbih: String get() = t("qa_tasbih")
    val qaDuas: String get() = t("qa_duas")
    val qaNames: String get() = t("qa_names")
    val quranIndex: String get() = t("quran_index")
    val searchPh: String get() = t("search_ph")
    val filterAll: String get() = t("filter_all")
    val filterMeccan: String get() = t("filter_meccan")
    val filterMedinan: String get() = t("filter_medinan")
    val verses: String get() = t("verses")
    val juz: String get() = t("juz")
    val meccan: String get() = t("meccan")
    val medinan: String get() = t("medinan")
    val readerSettings: String get() = t("reader_settings")
    val fontSize: String get() = t("font_size")
    val translation: String get() = t("translation")
    val tafsir: String get() = t("tafsir")
    val reciter: String get() = t("reciter")
    val none: String get() = t("none")
    val playSurah: String get() = t("play_surah")
    val repeat: String get() = t("repeat")
    val speed: String get() = t("speed")
    val downloadOffline: String get() = t("download_offline")
    val saveMark: String get() = t("save_mark")
    val bookmark: String get() = t("bookmark")
    val note: String get() = t("note")
    val color: String get() = t("color")
    val share: String get() = t("share")
    val copy: String get() = t("copy")
    val savedMsg: String get() = t("saved_msg")
    val bookmarks: String get() = t("bookmarks")
    val adhkarCat: String get() = t("adhkar_cat")
    val morning: String get() = t("morning")
    val evening: String get() = t("evening")
    val afterPrayer: String get() = t("after_prayer")
    val sleep: String get() = t("sleep")
    val wake: String get() = t("wake")
    val food: String get() = t("food")
    val mosque: String get() = t("mosque")
    val travel: String get() = t("travel")
    val anxiety: String get() = t("anxiety")
    val rain: String get() = t("rain")
    val countOf: String get() = t("count_of")
    val tapToCount: String get() = t("tap_to_count")
    val completeAll: String get() = t("complete_all")
    val reset: String get() = t("reset")
    val duaLibrary: String get() = t("dua_library")
    val source: String get() = t("source")
    val namesTitle: String get() = t("names_title")
    val namesSub: String get() = t("names_sub")
    val qiblaTitle: String get() = t("qibla_title")
    val qiblaHint: String get() = t("qibla_hint")
    val distance: String get() = t("distance")
    val bearing: String get() = t("bearing")
    val calibrate: String get() = t("calibrate")
    val simulate: String get() = t("simulate")
    val howWithout: String get() = t("how_without")
    val tasbihTitle: String get() = t("tasbih_title")
    val target: String get() = t("target")
    val trackerTitle: String get() = t("tracker_title")
    val trackerSub: String get() = t("tracker_sub")
    val streakDays: String get() = t("streak_days")
    val thisWeek: String get() = t("this_week")
    val last30: String get() = t("last_30")
    val habits: String get() = t("habits")
    val calendarTitle: String get() = t("calendar_title")
    val hijri: String get() = t("hijri")
    val gregorian: String get() = t("gregorian")
    val upcoming: String get() = t("upcoming")
    val moreTitle: String get() = t("more_title")
    val settings: String get() = t("settings")
    val settingsTitle: String get() = t("settings_title")
    val about: String get() = t("about")
    val shareApp: String get() = t("share_app")
    val rateApp: String get() = t("rate_app")
    val general: String get() = t("general")
    val language: String get() = t("language")
    val theme: String get() = t("theme")
    val textSize: String get() = t("text_size")
    val backup: String get() = t("backup")
    val sources: String get() = t("sources")
    val privacy: String get() = t("privacy")
    val themeLight: String get() = t("theme_light")
    val themeDark: String get() = t("theme_dark")
    val themeAuto: String get() = t("theme_auto")
    val sizeSmall: String get() = t("size_small")
    val sizeNormal: String get() = t("size_normal")
    val sizeLarge: String get() = t("size_large")
    val sizeXlarge: String get() = t("size_xlarge")
    val appearance: String get() = t("appearance")
    val arabicDigits: String get() = t("arabic_digits")
    val arabicDigitsSub: String get() = t("arabic_digits_sub")
    val systemDefault: String get() = t("system_default")
    val aboutTitle: String get() = t("about_title")
    val aboutBody: String get() = t("about_body")
    val emptyBmT: String get() = t("empty_bm_t")
    val emptyBmS: String get() = t("empty_bm_s")
    val emptyNoteT: String get() = t("empty_note_t")
    val save: String get() = t("save")
    val cancel: String get() = t("cancel")
    val apply: String get() = t("apply")
    val ok: String get() = t("ok")
    val retry: String get() = t("retry")
    val loading: String get() = t("loading")
    val errorGeneric: String get() = t("error_generic")
    val on: String get() = t("on")
    val off: String get() = t("off")
    val comingSoon: String get() = t("coming_soon")
    val demoData: String get() = t("demo_data")
    val onboardingTitle: String get() = t("onboarding_title")
    val onboardingSubtitle: String get() = t("onboarding_subtitle")
    val chooseLanguage: String get() = t("choose_language")
    val start: String get() = t("start")
    val skip: String get() = t("skip")
    val designSystem: String get() = t("design_system")
    val dsColors: String get() = t("ds_colors")
    val dsTypography: String get() = t("ds_typography")
    val dsSpacing: String get() = t("ds_spacing")
    val dsRadii: String get() = t("ds_radii")
    val dsComponents: String get() = t("ds_components")
    val dsButtons: String get() = t("ds_buttons")
    val dsInputs: String get() = t("ds_inputs")
    val dsFeedback: String get() = t("ds_feedback")
    val week1Note: String get() = t("week1_note")
    val comingWeek2: String get() = t("coming_week2")
    val prayerSoonTitle: String get() = t("prayer_soon_title")
    val prayerSoonBody: String get() = t("prayer_soon_body")
    val quranSoonTitle: String get() = t("quran_soon_title")
    val quranSoonBody: String get() = t("quran_soon_body")
    val adhkarSoonTitle: String get() = t("adhkar_soon_title")
    val adhkarSoonBody: String get() = t("adhkar_soon_body")
    val qiblaSoonTitle: String get() = t("qibla_soon_title")
    val qiblaSoonBody: String get() = t("qibla_soon_body")
    val prayerSettingsTitle: String get() = t("prayer_settings_title")
    val prayerTimesTitle: String get() = t("prayer_times_title")
    val prayerSettingsOpen: String get() = t("prayer_settings_open")
    val prayerEngineCredit: String get() = t("prayer_engine_credit")
    val prayerLocation: String get() = t("prayer_location")
    val prayerChooseCity: String get() = t("prayer_choose_city")
    val prayerUseMyLocation: String get() = t("prayer_use_my_location")
    val prayerLocationRationale: String get() = t("prayer_location_rationale")
    val prayerLocationUpdated: String get() = t("prayer_location_updated")
    val prayerLocationDenied: String get() = t("prayer_location_denied")
    val prayerLocationDeniedForever: String get() = t("prayer_location_denied_forever")
    val prayerLocationServiceOff: String get() = t("prayer_location_service_off")
    val prayerLocationTimeout: String get() = t("prayer_location_timeout")
    val prayerLocationUnknown: String get() = t("prayer_location_unknown")
    val prayerOpenSettings: String get() = t("prayer_open_settings")
    val prayerMethod: String get() = t("prayer_method")
    val prayerMadhab: String get() = t("prayer_madhab")
    val prayerMadhabSub: String get() = t("prayer_madhab_sub")
    val prayerHighLatitude: String get() = t("prayer_high_latitude")
    val prayerHighLatitudeSub: String get() = t("prayer_high_latitude_sub")
    val prayerAdjustments: String get() = t("prayer_adjustments")
    val prayerAdjustmentsSub: String get() = t("prayer_adjustments_sub")
    val prayerAdjustedNotice: String get() = t("prayer_adjusted_notice")
    val prayerClearAdjustments: String get() = t("prayer_clear_adjustments")
    val prayerNextLabel: String get() = t("prayer_next_label")
    val prayerTomorrowFajr: String get() = t("prayer_tomorrow_fajr")
    val prayerNow: String get() = t("prayer_now")
    val prayerUpcoming: String get() = t("prayer_upcoming")
    val prayerUnavailable: String get() = t("prayer_unavailable")
    val citySearchHint: String get() = t("city_search_hint")
    val cityNoResults: String get() = t("city_no_results")
    val cityNoResultsHint: String get() = t("city_no_results_hint")
    val timeAm: String get() = t("time_am")
    val timePm: String get() = t("time_pm")
    val homeWeek2Note: String get() = t("home_week2_note")
    val notifAthanTitle: String get() = t("notif_athan_title")
    val notifAthanBody: String get() = t("notif_athan_body")
    val notifReminderTitle: String get() = t("notif_reminder_title")
    val notifReminderBody: String get() = t("notif_reminder_body")
    val notifJumuahTitle: String get() = t("notif_jumuah_title")
    val notifJumuahBody: String get() = t("notif_jumuah_body")
    val notifChannelAthan: String get() = t("notif_channel_athan")
    val notifChannelAthanDesc: String get() = t("notif_channel_athan_desc")
    val notifChannelReminder: String get() = t("notif_channel_reminder")
    val notifChannelReminderDesc: String get() = t("notif_channel_reminder_desc")
    val notifChannelJumuah: String get() = t("notif_channel_jumuah")
    val notifChannelJumuahDesc: String get() = t("notif_channel_jumuah_desc")
    val notificationsTestTitle: String get() = t("notifications_test_title")
    val notificationsTestBody: String get() = t("notifications_test_body")
    val notificationsMaster: String get() = t("notifications_master")
    val notificationsMasterDesc: String get() = t("notifications_master_desc")
    val notificationsTest: String get() = t("notifications_test")
    val notificationsTestQueued: String get() = t("notifications_test_queued")
    val notificationsOffHint: String get() = t("notifications_off_hint")
    val notificationsPermissionNeeded: String get() = t("notifications_permission_needed")
    val notificationsPermissionAction: String get() = t("notifications_permission_action")
    val notificationsExactNeeded: String get() = t("notifications_exact_needed")
    val notificationsExactNeededShort: String get() = t("notifications_exact_needed_short")
    val notificationsExactAction: String get() = t("notifications_exact_action")
    val notificationsPerPrayer: String get() = t("notifications_per_prayer")
    val notificationsPerPrayerHint: String get() = t("notifications_per_prayer_hint")
    val notificationsReminder: String get() = t("notifications_reminder")
    val notificationsReminderDesc: String get() = t("notifications_reminder_desc")
    val notificationsReminderMinutes: String get() = t("notifications_reminder_minutes")
    val notificationsMinutesOff: String get() = t("notifications_minutes_off")
    val notificationsJumuah: String get() = t("notifications_jumuah")
    val notificationsJumuahDesc: String get() = t("notifications_jumuah_desc")
    val notificationsJumuahBefore: String get() = t("notifications_jumuah_before")
    val notificationsSound: String get() = t("notifications_sound")
    val notificationsSoundDesc: String get() = t("notifications_sound_desc")
    val notificationsModeAdhan: String get() = t("notifications_mode_adhan")
    val notificationsModeBeep: String get() = t("notifications_mode_beep")
    val notificationsModeVibrate: String get() = t("notifications_mode_vibrate")
    val notificationsModeSilent: String get() = t("notifications_mode_silent")
    val notificationsModeOff: String get() = t("notifications_mode_off")
    val notificationsScheduled: String get() = t("notifications_scheduled")
    val notificationsScheduledMany: String get() = t("notifications_scheduled_many")
    val notificationsModeShortAdhan: String get() = t("notifications_mode_short_adhan")
    val notificationsMinutesValue: String get() = t("notifications_minutes_value")
    val notificationsScheduledNone: String get() = t("notifications_scheduled_none")
    val atharyOpen: String get() = t("athary_open")
    val atharyBest: String get() = t("athary_best")
    val atharyLess: String get() = t("athary_less")
    val atharyMore: String get() = t("athary_more")
    val atharyNext: String get() = t("athary_next")
    val atharyReset: String get() = t("athary_reset")
    val atharyResetConfirm: String get() = t("athary_reset_confirm")
    val atharyFutureNote: String get() = t("athary_future_note")
    val homeWeek4Note: String get() = t("home_week4_note")
    val widgetLabel: String get() = t("widget_label")
    val widgetDescription: String get() = t("widget_description")
    val widgetNextPrayer: String get() = t("widget_next_prayer")
    val widgetIn: String get() = t("widget_in")
    val widgetNoData: String get() = t("widget_no_data")
    val widgetAdd: String get() = t("widget_add")
    val widgetAddHint: String get() = t("widget_add_hint")
    val quranTabSurah: String get() = t("quran_tab_surah")
    val quranTabJuz: String get() = t("quran_tab_juz")
    val quranTabPage: String get() = t("quran_tab_page")
    val quranEmptyTitle: String get() = t("quran_empty_title")
    val quranEmptyBody: String get() = t("quran_empty_body")
    val quranImportAction: String get() = t("quran_import_action")
    val quranImporting: String get() = t("quran_importing")
    val quranImportFailed: String get() = t("quran_import_failed")
    val quranReaderSettings: String get() = t("quran_reader_settings")
    val quranFontSize: String get() = t("quran_font_size")
    val quranFontSmaller: String get() = t("quran_font_smaller")
    val quranFontBigger: String get() = t("quran_font_bigger")
    val quranReadingTheme: String get() = t("quran_reading_theme")
    val quranThemePaper: String get() = t("quran_theme_paper")
    val quranThemeSepia: String get() = t("quran_theme_sepia")
    val quranThemeNight: String get() = t("quran_theme_night")
    val quranShowTranslation: String get() = t("quran_show_translation")
    val quranNoTranslation: String get() = t("quran_no_translation")
    val quranEdition: String get() = t("quran_edition")
    val quranPageWord: String get() = t("quran_page_word")
    val quranSajda: String get() = t("quran_sajda")
    val quranSearchTitle: String get() = t("quran_search_title")
    val quranSearchHint: String get() = t("quran_search_hint")
    val quranSearchEmpty: String get() = t("quran_search_empty")
    val quranResultsCount: String get() = t("quran_results_count")
    val quranNoAyahs: String get() = t("quran_no_ayahs")
    val audioListen: String get() = t("audio_listen")
    val audioStop: String get() = t("audio_stop")
    val audioReciter: String get() = t("audio_reciter")
    val audioChooseReciter: String get() = t("audio_choose_reciter")
    val audioReciterDefault: String get() = t("audio_reciter_default")
    val audioDownload: String get() = t("audio_download")
    val audioDownloadSurah: String get() = t("audio_download_surah")
    val audioDownloadJuz: String get() = t("audio_download_juz")
    val audioDownloadAll: String get() = t("audio_download_all")
    val audioDownloaded: String get() = t("audio_downloaded")
    val audioDeleteDownload: String get() = t("audio_delete_download")
    val audioDownloading: String get() = t("audio_downloading")
    val audioDownloadProgress: String get() = t("audio_download_progress")
    val audioSizeEstimate: String get() = t("audio_size_estimate")
    val audioNeedsInternet: String get() = t("audio_needs_internet")
    val audioPlaybackFailed: String get() = t("audio_playback_failed")
    val audioSpeed: String get() = t("audio_speed")
    val audioRepeatAyah: String get() = t("audio_repeat_ayah")
    val audioSources: String get() = t("audio_sources")
    val audioRightsNote: String get() = t("audio_rights_note")
    val audioLicensedNoncommercial: String get() = t("audio_licensed_noncommercial")
    val audioNoBundledAudio: String get() = t("audio_no_bundled_audio")
    val audioCancel: String get() = t("audio_cancel")
    val audioDeleteConfirm: String get() = t("audio_delete_confirm")
    val audioStorageUsed: String get() = t("audio_storage_used")
    val back: String get() = t("back")
    val change: String get() = t("change")
    val north: String get() = t("north")
    val south: String get() = t("south")
    val east: String get() = t("east")
    val west: String get() = t("west")
    val adhkarSearchHint: String get() = t("adhkar_search_hint")
    val adhkarItemsCount: String get() = t("adhkar_items_count")
    val prayerMyLocation: String get() = t("prayer_my_location")
    val prayerNoneLeft: String get() = t("prayer_none_left")
    val qiblaNoSensor: String get() = t("qibla_no_sensor")
    val qiblaBearingFromNorth: String get() = t("qibla_bearing_from_north")
    val qiblaDeviceDirection: String get() = t("qibla_device_direction")
    val qiblaAligned: String get() = t("qibla_aligned")
    val qiblaTurnRight: String get() = t("qibla_turn_right")
    val qiblaTurnLeft: String get() = t("qibla_turn_left")
    val qiblaBearingGeo: String get() = t("qibla_bearing_geo")
    val quranLoadFailed: String get() = t("quran_load_failed")
    val quranUthmaniSource: String get() = t("quran_uthmani_source")
    val minutesShort: String get() = t("minutes_short")
    val habitFajr: String get() = t("habit_fajr")
    val habitFivePrayers: String get() = t("habit_five_prayers")
    val habitQuran: String get() = t("habit_quran")
    val habitMorningAdhkar: String get() = t("habit_morning_adhkar")
    val habitEveningAdhkar: String get() = t("habit_evening_adhkar")
    val habitDuha: String get() = t("habit_duha")
    val habitSadaqa: String get() = t("habit_sadaqa")
    val habitFasting: String get() = t("habit_fasting")
    val habitTasbih: String get() = t("habit_tasbih")
    val atharyShare: String get() = t("athary_share")
    val shareWeek: String get() = t("share_week")
    val shareActiveDays: String get() = t("share_active_days")
    val shareActs: String get() = t("share_acts")
    val shareStreak: String get() = t("share_streak")
    val shareFooter: String get() = t("share_footer")
    val shareText: String get() = t("share_text")
    val shareFailed: String get() = t("share_failed")
    val ruqyaTitle: String get() = t("ruqya_title")
    val ruqyaNote: String get() = t("ruqya_note")
    val ruqyaQuranTitle: String get() = t("ruqya_quran_title")
    val ruqyaAdhkarTitle: String get() = t("ruqya_adhkar_title")
    val ruqyaKursi: String get() = t("ruqya_kursi")
    val ruqyaBaqarahLast: String get() = t("ruqya_baqarah_last")
    val ruqyaIkhlas: String get() = t("ruqya_ikhlas")
    val ruqyaFalaq: String get() = t("ruqya_falaq")
    val ruqyaNas: String get() = t("ruqya_nas")
    val ruqyaPlay: String get() = t("ruqya_play")
    val ruqyaMissing: String get() = t("ruqya_missing")
    val trackerTodayEmpty: String get() = t("tracker_today_empty")
    val trackerDays: String get() = t("tracker_days")
    val moreAsma: String get() = t("more_asma")
    val adhkarAfterPrayer: String get() = t("adhkar_after_prayer")
    val asmaSearchHint: String get() = t("asma_search_hint")
    val asmaCount: String get() = t("asma_count")
    val asmaEmpty: String get() = t("asma_empty")
    val asmaNote: String get() = t("asma_note")
    val methodEgyptian: String get() = t("method_egyptian")
    val methodUmmAlQura: String get() = t("method_umm_al_qura")
    val methodMwl: String get() = t("method_mwl")
    val methodIsna: String get() = t("method_isna")
    val methodFrance: String get() = t("method_france")
    val methodCustom: String get() = t("method_custom")
    val notifChannelAdhkar: String get() = t("notif_channel_adhkar")
    val notifChannelAdhkarDesc: String get() = t("notif_channel_adhkar_desc")
    val notifMorningAdhkarTitle: String get() = t("notif_morning_adhkar_title")
    val notifMorningAdhkarBody: String get() = t("notif_morning_adhkar_body")
    val notifEveningAdhkarTitle: String get() = t("notif_evening_adhkar_title")
    val notifEveningAdhkarBody: String get() = t("notif_evening_adhkar_body")
    val settingsMorningAdhkar: String get() = t("settings_morning_adhkar")
    val settingsEveningAdhkar: String get() = t("settings_evening_adhkar")
    val settingsAdhkarOffset: String get() = t("settings_adhkar_offset")
    val quranTranslation: String get() = t("quran_translation")
    val quranTranslationNone: String get() = t("quran_translation_none")
    val quranTranslationPickthall: String get() = t("quran_translation_pickthall")
    val quranTranslationItani: String get() = t("quran_translation_itani")
    val quranCreditPickthall: String get() = t("quran_credit_pickthall")
    val quranCreditItani: String get() = t("quran_credit_itani")
    val quranJuz: String get() = t("quran_juz")
    val quranPage: String get() = t("quran_page")
    val quranReciter: String get() = t("quran_reciter")
    val quranPlay: String get() = t("quran_play")
    val quranPause: String get() = t("quran_pause")
    val quranResume: String get() = t("quran_resume")
    val quranStop: String get() = t("quran_stop")
    val quranAudioCredit: String get() = t("quran_audio_credit")
    val quranAudioError: String get() = t("quran_audio_error")
    val quranAudioLoading: String get() = t("quran_audio_loading")
    val themeMode: String get() = t("theme_mode")
    val themeSystem: String get() = t("theme_system")
    val fontSmall: String get() = t("font_small")
    val fontNormal: String get() = t("font_normal")
    val fontLarge: String get() = t("font_large")
    val fontHuge: String get() = t("font_huge")
    val readerTheme: String get() = t("reader_theme")
    val readerNormal: String get() = t("reader_normal")
    val readerNight: String get() = t("reader_night")
    val readerSepia: String get() = t("reader_sepia")
    val fontPreview: String get() = t("font_preview")
    val notifChannelPlayback: String get() = t("notif_channel_playback")
    val notifChannelPlaybackDesc: String get() = t("notif_channel_playback_desc")
    val quranDownload: String get() = t("quran_download")
    val quranDownloadDone: String get() = t("quran_download_done")
    val quranDeleteDownload: String get() = t("quran_delete_download")
    val quranDownloading: String get() = t("quran_downloading")
    val quranDownloadHint: String get() = t("quran_download_hint")
    val quranOfflineReady: String get() = t("quran_offline_ready")
    val quranBookmarks: String get() = t("quran_bookmarks")
    val quranBookmarkAdd: String get() = t("quran_bookmark_add")
    val quranBookmarkRemove: String get() = t("quran_bookmark_remove")
    val quranBookmarksEmpty: String get() = t("quran_bookmarks_empty")
    val quranBookmarksEmptyBody: String get() = t("quran_bookmarks_empty_body")
    val adhkarMine: String get() = t("adhkar_mine")
    val adhkarMineBody: String get() = t("adhkar_mine_body")
    val adhkarAdd: String get() = t("adhkar_add")
    val adhkarAddHint: String get() = t("adhkar_add_hint")
    val adhkarRepeatCount: String get() = t("adhkar_repeat_count")
    val adhkarMineEmpty: String get() = t("adhkar_mine_empty")
    val adhkarDelete: String get() = t("adhkar_delete")
    val tasbihUndo: String get() = t("tasbih_undo")
    val tasbihReset: String get() = t("tasbih_reset")
    val tasbihRounds: String get() = t("tasbih_rounds")
    val tasbihTotal: String get() = t("tasbih_total")
    val tabTasbih: String get() = t("tab_tasbih")
    val hijriMonth1: String get() = t("hijri_month_1")
    val hijriMonth2: String get() = t("hijri_month_2")
    val hijriMonth3: String get() = t("hijri_month_3")
    val hijriMonth4: String get() = t("hijri_month_4")
    val hijriMonth5: String get() = t("hijri_month_5")
    val hijriMonth6: String get() = t("hijri_month_6")
    val hijriMonth7: String get() = t("hijri_month_7")
    val hijriMonth8: String get() = t("hijri_month_8")
    val hijriMonth9: String get() = t("hijri_month_9")
    val hijriMonth10: String get() = t("hijri_month_10")
    val hijriMonth11: String get() = t("hijri_month_11")
    val hijriMonth12: String get() = t("hijri_month_12")
    val hijriAdjust: String get() = t("hijri_adjust")
    val hijriAdjustBody: String get() = t("hijri_adjust_body")
    val hijriToday: String get() = t("hijri_today")
    val quranAyah: String get() = t("quran_ayah")
    val khatmahTitle: String get() = t("khatmah_title")
    val khatmahStart: String get() = t("khatmah_start")
    val khatmahPlan: String get() = t("khatmah_plan")
    val khatmahDays: String get() = t("khatmah_days")
    val khatmahToday: String get() = t("khatmah_today")
    val khatmahFrom: String get() = t("khatmah_from")
    val khatmahTo: String get() = t("khatmah_to")
    val khatmahRead: String get() = t("khatmah_read")
    val khatmahDone: String get() = t("khatmah_done")
    val khatmahProgress: String get() = t("khatmah_progress")
    val khatmahRestart: String get() = t("khatmah_restart")
    val khatmahStop: String get() = t("khatmah_stop")
    val khatmahFinished: String get() = t("khatmah_finished")
    val khatmahFinishedBody: String get() = t("khatmah_finished_body")
    val khatmahPerDay: String get() = t("khatmah_per_day")
    val eventHijriNewYear: String get() = t("event_hijri_new_year")
    val eventAshura: String get() = t("event_ashura")
    val eventMawlid: String get() = t("event_mawlid")
    val eventIsra: String get() = t("event_isra")
    val eventRamadan: String get() = t("event_ramadan")
    val eventQadr: String get() = t("event_qadr")
    val eventEidFitr: String get() = t("event_eid_fitr")
    val eventArafah: String get() = t("event_arafah")
    val eventEidAdha: String get() = t("event_eid_adha")
    val eventDaysLeft: String get() = t("event_days_left")
    val eventToday: String get() = t("event_today")
    val eventTomorrow: String get() = t("event_tomorrow")
    val hlTitle: String get() = t("hl_title")
    val hlMiddle: String get() = t("hl_middle")
    val hlSeventh: String get() = t("hl_seventh")
    val hlTwilight: String get() = t("hl_twilight")
    val monthPrev: String get() = t("month_prev")
    val monthNext: String get() = t("month_next")
    val backupTitle: String get() = t("backup_title")
    val backupBody: String get() = t("backup_body")
    val backupExport: String get() = t("backup_export")
    val backupImport: String get() = t("backup_import")
    val backupExported: String get() = t("backup_exported")
    val backupRestored: String get() = t("backup_restored")
    val backupBadFile: String get() = t("backup_bad_file")
    val backupFailed: String get() = t("backup_failed")
    val hifzTitle: String get() = t("hifz_title")
    val hifzBody: String get() = t("hifz_body")
    val hifzFrom: String get() = t("hifz_from")
    val hifzTo: String get() = t("hifz_to")
    val hifzSurah: String get() = t("hifz_surah")
    val hifzAyah: String get() = t("hifz_ayah")
    val hifzCount: String get() = t("hifz_count")
    val hifzStart: String get() = t("hifz_start")
    val hifzChange: String get() = t("hifz_change")
    val hifzReveal: String get() = t("hifz_reveal")
    val hifzHide: String get() = t("hifz_hide")
    val hifzRepeat: String get() = t("hifz_repeat")
    val hifzMemorized: String get() = t("hifz_memorized")
    val hifzProgress: String get() = t("hifz_progress")
    val hifzRepeatsOf: String get() = t("hifz_repeats_of")
    val hifzFinished: String get() = t("hifz_finished")
    val quranHizb: String get() = t("quran_hizb")
    val ramadanDay: String get() = t("ramadan_day")
    val ramadanIftar: String get() = t("ramadan_iftar")
    val ramadanSuhur: String get() = t("ramadan_suhur")
    val ramadanLastTen: String get() = t("ramadan_last_ten")
    val weeklyTitle: String get() = t("weekly_title")
    val weeklyRange: String get() = t("weekly_range")
    val weeklyActs: String get() = t("weekly_acts")
    val weeklyCompletion: String get() = t("weekly_completion")
    val weeklyBetter: String get() = t("weekly_better")
    val weeklyWorse: String get() = t("weekly_worse")
    val weeklySame: String get() = t("weekly_same")
    val weeklyStreak: String get() = t("weekly_streak")
    val weeklyBestStreak: String get() = t("weekly_best_streak")
    val weeklyTop: String get() = t("weekly_top")
    val adjustTitle: String get() = t("adjust_title")
    val adjustBody: String get() = t("adjust_body")
    val adjustReset: String get() = t("adjust_reset")
    val adjustMinutes: String get() = t("adjust_minutes")
    val adjustOpen: String get() = t("adjust_open")
    val mushafMode: String get() = t("mushaf_mode")
    val mushafPage: String get() = t("mushaf_page")
    val mushafJump: String get() = t("mushaf_jump")
    val mushafOf: String get() = t("mushaf_of")
    val mushafNote: String get() = t("mushaf_note")
    val mushafOpen: String get() = t("mushaf_open")
    val mushafNext: String get() = t("mushaf_next")
    val mushafPrev: String get() = t("mushaf_prev")
    val surahFilterAll: String get() = t("surah_filter_all")
    val surahFilterMeccan: String get() = t("surah_filter_meccan")
    val surahFilterMedinan: String get() = t("surah_filter_medinan")
    val surahSections: String get() = t("surah_sections")
    val prayerRemaining: String get() = t("prayer_remaining")
    val zakahTitle: String get() = t("zakah_title")
    val zakahGold: String get() = t("zakah_gold")
    val zakahSilver: String get() = t("zakah_silver")
    val zakahCash: String get() = t("zakah_cash")
    val zakahFitr: String get() = t("zakah_fitr")
    val zakahGrams: String get() = t("zakah_grams")
    val zakahAmount: String get() = t("zakah_amount")
    val zakahPeople: String get() = t("zakah_people")
    val zakahPrice: String get() = t("zakah_price")
    val zakahFoodPrice: String get() = t("zakah_food_price")
    val zakahNisab: String get() = t("zakah_nisab")
    val zakahDue: String get() = t("zakah_due")
    val zakahNotLiable: String get() = t("zakah_not_liable")
    val zakahLiable: String get() = t("zakah_liable")
    val zakahNote: String get() = t("zakah_note")
    val zakahSource: String get() = t("zakah_source")
    val qiblaSunTitle: String get() = t("qibla_sun_title")
    val qiblaSunBody: String get() = t("qibla_sun_body")
    val qiblaSunDays: String get() = t("qibla_sun_days")
    val qiblaSunNone: String get() = t("qibla_sun_none")
    val qiblaSunShadow: String get() = t("qibla_sun_shadow")
    val qiblaSunShadowNone: String get() = t("qibla_sun_shadow_none")
    val qiblaSunNow: String get() = t("qibla_sun_now")
    val qiblaSunPointing: String get() = t("qibla_sun_pointing")
    val tasbihSessions: String get() = t("tasbih_sessions")
    val tasbihToday: String get() = t("tasbih_today")
    val tasbihBest: String get() = t("tasbih_best")
    val tasbihSessionsCount: String get() = t("tasbih_sessions_count")
    val tasbihCounts: String get() = t("tasbih_counts")
    val tasbihNoSessions: String get() = t("tasbih_no_sessions")
    val tasbihSpeak: String get() = t("tasbih_speak")
    val tasbihSessionsToday: String get() = t("tasbih_sessions_today")
    val tasbihWeek: String get() = t("tasbih_week")
    val tasbihSpeakHint: String get() = t("tasbih_speak_hint")
    val tasbihVolume: String get() = t("tasbih_volume")
    val tasbihVolumeHint: String get() = t("tasbih_volume_hint")
    val silenceTitle: String get() = t("silence_title")
    val silenceBody: String get() = t("silence_body")
    val silenceNeedAccess: String get() = t("silence_need_access")
    val silenceAllow: String get() = t("silence_allow")
    val silenceMinutes: String get() = t("silence_minutes")
    val monthShare: String get() = t("month_share")
    val monthShareText: String get() = t("month_share_text")
    val monthShareFooter: String get() = t("month_share_footer")
}
