import 'app_locale.dart';
import 'bundles/strings_ar.dart';
import 'bundles/strings_en.dart';
import 'bundles/strings_fr.dart';

/// الوصول إلى كل نصوص التطبيق بطريقة آمنة على الأنواع.
///
/// ```dart
/// final s = ref.watch(stringsProvider);
/// Text(s.tabHome);
/// Text(s.tArgs('last_30', {'n': '30'}));
/// ```
///
/// أي مفتاح ناقص في لغة معينة **يرجع للعربية** بدل ما يرجع المفتاح،
/// لأن نص غلط بالإنجليزية في تطبيق قرآني أسوأ من نص عربي.
class Strings {
  const Strings(this.locale);

  final AppLocale locale;

  static const Map<AppLocale, Map<String, String>> _bundles = {
    AppLocale.ar: kStringsAr,
    AppLocale.en: kStringsEn,
    AppLocale.fr: kStringsFr,
  };

  /// Lookup بمفتاح حر (للحالات الديناميكية مثل أسماء السور).
  String t(String key) =>
      _bundles[locale]?[key] ?? kStringsAr[key] ?? key;

  /// Lookup مع استبدال متغيّرات على شكل `{name}`.
  ///
  /// ```dart
  /// s.tArgs('streak_value', {'n': '12'})  // → "١٢ يوم متصل"
  /// ```
  String tArgs(String key, Map<String, Object> args) {
    var value = t(key);
    for (final entry in args.entries) {
      value = value.replaceAll('{${entry.key}}', '${entry.value}');
    }
    return value;
  }

  // ══════════════════════════════════════════════════════════
  // الهوية
  // ══════════════════════════════════════════════════════════
  String get appName => t('app_name');
  String get tagline => t('tagline');

  // ══════════════════════════════════════════════════════════
  // التحيات
  // ══════════════════════════════════════════════════════════
  String get hiMorning => t('hi_morning');
  String get hiAfternoon => t('hi_afternoon');
  String get hiEvening => t('hi_evening');
  String get hiNight => t('hi_night');

  // ══════════════════════════════════════════════════════════
  // التبويبات
  // ══════════════════════════════════════════════════════════
  String get tabHome => t('tab_home');
  String get tabQuran => t('tab_quran');
  String get tabAdhkar => t('tab_adhkar');
  String get tabQibla => t('tab_qibla');
  String get tabMore => t('tab_more');

  // ══════════════════════════════════════════════════════════
  // الصلاة
  // ══════════════════════════════════════════════════════════
  String get nextPrayer => t('next_prayer');
  String get remaining => t('remaining');
  String get today => t('today');
  String get now => t('now');
  String get madhabLabel => t('madhab_label');
  String get shafi => t('shafi');
  String get hanafi => t('hanafi');
  String get fajr => t('fajr');
  String get sunrise => t('sunrise');
  String get dhuhr => t('dhuhr');
  String get asr => t('asr');
  String get maghrib => t('maghrib');
  String get isha => t('isha');
  String get prayerTimes => t('prayer_times');
  String get monthlyCalendar => t('monthly_calendar');
  String get calcSettings => t('calc_settings');
  String get notifications => t('notifications');
  String get adhanSound => t('adhan_sound');
  String get reminderBefore => t('reminder_before');

  // ══════════════════════════════════════════════════════════
  // الرئيسية
  // ══════════════════════════════════════════════════════════
  String get continueReading => t('continue_reading');
  String get ayahNo => t('ayah_no');
  String get wirdTitle => t('wird_title');
  String get wirdSub => t('wird_sub');
  String get adhkarToday => t('adhkar_today');
  String get done => t('done');
  String get of => t('of');
  String get quickActions => t('quick_actions');
  String get verseOfDay => t('verse_of_day');
  String get qaQuran => t('qa_quran');
  String get qaAdhkar => t('qa_adhkar');
  String get qaQibla => t('qa_qibla');
  String get qaTasbih => t('qa_tasbih');
  String get qaDuas => t('qa_duas');
  String get qaNames => t('qa_names');

  // ══════════════════════════════════════════════════════════
  // القرآن
  // ══════════════════════════════════════════════════════════
  String get quranIndex => t('quran_index');
  String get searchPh => t('search_ph');
  String get filterAll => t('filter_all');
  String get filterMeccan => t('filter_meccan');
  String get filterMedinan => t('filter_medinan');
  String get verses => t('verses');
  String get juz => t('juz');
  String get meccan => t('meccan');
  String get medinan => t('medinan');
  String get readerSettings => t('reader_settings');
  String get fontSize => t('font_size');
  String get translation => t('translation');
  String get tafsir => t('tafsir');
  String get reciter => t('reciter');
  String get none => t('none');
  String get playSurah => t('play_surah');
  String get repeat => t('repeat');
  String get speed => t('speed');
  String get downloadOffline => t('download_offline');
  String get saveMark => t('save_mark');
  String get bookmark => t('bookmark');
  String get note => t('note');
  String get color => t('color');
  String get share => t('share');
  String get copy => t('copy');
  String get savedMsg => t('saved_msg');
  String get bookmarks => t('bookmarks');

  // ══════════════════════════════════════════════════════════
  // الأذكار والأدعية
  // ══════════════════════════════════════════════════════════
  String get adhkarCat => t('adhkar_cat');
  String get morning => t('morning');
  String get evening => t('evening');
  String get afterPrayer => t('after_prayer');
  String get sleep => t('sleep');
  String get wake => t('wake');
  String get food => t('food');
  String get mosque => t('mosque');
  String get travel => t('travel');
  String get anxiety => t('anxiety');
  String get rain => t('rain');
  String get countOf => t('count_of');
  String get tapToCount => t('tap_to_count');
  String get completeAll => t('complete_all');
  String get reset => t('reset');
  String get duaLibrary => t('dua_library');
  String get source => t('source');

  // ══════════════════════════════════════════════════════════
  // أسماء الله الحسنى
  // ══════════════════════════════════════════════════════════
  String get namesTitle => t('names_title');
  String get namesSub => t('names_sub');

  // ══════════════════════════════════════════════════════════
  // القبلة
  // ══════════════════════════════════════════════════════════
  String get qiblaTitle => t('qibla_title');
  String get qiblaHint => t('qibla_hint');
  String get distance => t('distance');
  String get bearing => t('bearing');
  String get calibrate => t('calibrate');
  String get simulate => t('simulate');
  String get howWithout => t('how_without');

  // ══════════════════════════════════════════════════════════
  // السبحة
  // ══════════════════════════════════════════════════════════
  String get tasbihTitle => t('tasbih_title');
  String get target => t('target');

  // ══════════════════════════════════════════════════════════
  // المتتبع «أثري»
  // ══════════════════════════════════════════════════════════
  String get trackerTitle => t('tracker_title');
  String get trackerSub => t('tracker_sub');
  String get streakDays => t('streak_days');
  String get thisWeek => t('this_week');
  String get last30 => t('last_30');
  String get habits => t('habits');

  // ══════════════════════════════════════════════════════════
  // التقويم
  // ══════════════════════════════════════════════════════════
  String get calendarTitle => t('calendar_title');
  String get hijri => t('hijri');
  String get gregorian => t('gregorian');
  String get upcoming => t('upcoming');

  // ══════════════════════════════════════════════════════════
  // المزيد والإعدادات
  // ══════════════════════════════════════════════════════════
  String get moreTitle => t('more_title');
  String get settings => t('settings');
  String get settingsTitle => t('settings_title');
  String get about => t('about');
  String get shareApp => t('share_app');
  String get rateApp => t('rate_app');
  String get general => t('general');
  String get language => t('language');
  String get theme => t('theme');
  String get textSize => t('text_size');
  String get backup => t('backup');
  String get sources => t('sources');
  String get privacy => t('privacy');
  String get themeLight => t('theme_light');
  String get themeDark => t('theme_dark');
  String get themeAuto => t('theme_auto');
  String get sizeSmall => t('size_small');
  String get sizeNormal => t('size_normal');
  String get sizeLarge => t('size_large');
  String get sizeXlarge => t('size_xlarge');
  String get appearance => t('appearance');
  String get arabicDigits => t('arabic_digits');
  String get arabicDigitsSub => t('arabic_digits_sub');
  String get systemDefault => t('system_default');

  // ══════════════════════════════════════════════════════════
  // عن التطبيق
  // ══════════════════════════════════════════════════════════
  String get aboutTitle => t('about_title');
  String get aboutBody => t('about_body');

  // ══════════════════════════════════════════════════════════
  // نصوص عامة وحالات فارغة
  // ══════════════════════════════════════════════════════════
  String get emptyBookmarksTitle => t('empty_bm_t');
  String get emptyBookmarksBody => t('empty_bm_s');
  String get emptyNotesTitle => t('empty_note_t');
  String get save => t('save');
  String get cancel => t('cancel');
  String get apply => t('apply');
  String get ok => t('ok');
  String get retry => t('retry');
  String get loading => t('loading');
  String get errorGeneric => t('error_generic');
  String get on => t('on');
  String get off => t('off');
  String get comingSoon => t('coming_soon');
  String get demoData => t('demo_data');

  // ══════════════════════════════════════════════════════════
  // أول تشغيل
  // ══════════════════════════════════════════════════════════
  String get onboardingTitle => t('onboarding_title');
  String get onboardingSubtitle => t('onboarding_subtitle');
  String get chooseLanguage => t('choose_language');
  String get start => t('start');
  String get skip => t('skip');

  // ══════════════════════════════════════════════════════════
  // معرض نظام التصميم
  // ══════════════════════════════════════════════════════════
  String get designSystem => t('design_system');
  String get dsColors => t('ds_colors');
  String get dsTypography => t('ds_typography');
  String get dsSpacing => t('ds_spacing');
  String get dsRadii => t('ds_radii');
  String get dsComponents => t('ds_components');
  String get dsButtons => t('ds_buttons');
  String get dsInputs => t('ds_inputs');
  String get dsFeedback => t('ds_feedback');

  // ══════════════════════════════════════════════════════════
  // شاشات السقالة (أسبوع ١)
  // ══════════════════════════════════════════════════════════
  String get week1Note => t('week1_note');
  String get comingWeek2 => t('coming_week2');
  String get prayerSoonTitle => t('prayer_soon_title');
  String get prayerSoonBody => t('prayer_soon_body');
  String get quranSoonTitle => t('quran_soon_title');
  String get quranSoonBody => t('quran_soon_body');
  String get adhkarSoonTitle => t('adhkar_soon_title');
  String get adhkarSoonBody => t('adhkar_soon_body');
  String get qiblaSoonTitle => t('qibla_soon_title');
  String get qiblaSoonBody => t('qibla_soon_body');

  // ══════════════════════════════════════════════════════════
  // المواقيت (أسبوع ٢)
  // ══════════════════════════════════════════════════════════
  String get prayerSettingsTitle => t('prayer_settings_title');
  String get prayerTimesTitle => t('prayer_times_title');
  String get prayerSettingsOpen => t('prayer_settings_open');
  String get prayerEngineCredit => t('prayer_engine_credit');
  String get prayerLocation => t('prayer_location');
  String get prayerChooseCity => t('prayer_choose_city');
  String get prayerUseMyLocation => t('prayer_use_my_location');
  String get prayerLocationRationale => t('prayer_location_rationale');
  String get prayerLocationUpdated => t('prayer_location_updated');
  String get prayerLocationDenied => t('prayer_location_denied');
  String get prayerLocationDeniedForever => t('prayer_location_denied_forever');
  String get prayerLocationServiceOff => t('prayer_location_service_off');
  String get prayerLocationTimeout => t('prayer_location_timeout');
  String get prayerLocationUnknown => t('prayer_location_unknown');
  String get prayerOpenSettings => t('prayer_open_settings');
  String get prayerMethod => t('prayer_method');
  String get prayerMadhab => t('prayer_madhab');
  String get prayerMadhabSub => t('prayer_madhab_sub');
  String get prayerHighLatitude => t('prayer_high_latitude');
  String get prayerHighLatitudeSub => t('prayer_high_latitude_sub');
  String get prayerAdjustments => t('prayer_adjustments');
  String get prayerAdjustmentsSub => t('prayer_adjustments_sub');
  String get prayerAdjustedNotice => t('prayer_adjusted_notice');
  String get prayerClearAdjustments => t('prayer_clear_adjustments');
  String get prayerNextLabel => t('prayer_next_label');
  String get prayerTomorrowFajr => t('prayer_tomorrow_fajr');
  String get prayerNow => t('prayer_now');
  String get prayerUpcoming => t('prayer_upcoming');
  String get prayerUnavailable => t('prayer_unavailable');
  String get citySearchHint => t('city_search_hint');
  String get cityNoResults => t('city_no_results');
  String get cityNoResultsHint => t('city_no_results_hint');
  String get timeAm => t('time_am');
  String get timePm => t('time_pm');

  // ══════════════════════════════════════════════════════════
  // الشاشة الرئيسية (أسبوع ٢)
  // ══════════════════════════════════════════════════════════
  String get homeWeek2Note => t('home_week2_note');
  // ══ الإشعارات والأذان (أسبوع ٣) ══
  String get notifAthanTitle => t('notif_athan_title');
  String get notifAthanBody => t('notif_athan_body');
  String get notifReminderTitle => t('notif_reminder_title');
  String get notifReminderBody => t('notif_reminder_body');
  String get notifJumuahTitle => t('notif_jumuah_title');
  String get notifJumuahBody => t('notif_jumuah_body');
  String get notifChannelAthan => t('notif_channel_athan');
  String get notifChannelAthanDesc => t('notif_channel_athan_desc');
  String get notifChannelReminder => t('notif_channel_reminder');
  String get notifChannelReminderDesc => t('notif_channel_reminder_desc');
  String get notifChannelJumuah => t('notif_channel_jumuah');
  String get notifChannelJumuahDesc => t('notif_channel_jumuah_desc');
  String get notificationsTestTitle => t('notifications_test_title');
  String get notificationsTestBody => t('notifications_test_body');
  String get notificationsMaster => t('notifications_master');
  String get notificationsMasterDesc => t('notifications_master_desc');
  String get notificationsTest => t('notifications_test');
  String get notificationsTestQueued => t('notifications_test_queued');
  String get notificationsOffHint => t('notifications_off_hint');
  String get notificationsPermissionNeeded => t('notifications_permission_needed');
  String get notificationsPermissionAction => t('notifications_permission_action');
  String get notificationsExactNeeded => t('notifications_exact_needed');
  String get notificationsExactNeededShort => t('notifications_exact_needed_short');
  String get notificationsExactAction => t('notifications_exact_action');
  String get notificationsPerPrayer => t('notifications_per_prayer');
  String get notificationsPerPrayerHint => t('notifications_per_prayer_hint');
  String get notificationsReminder => t('notifications_reminder');
  String get notificationsReminderDesc => t('notifications_reminder_desc');
  String get notificationsReminderMinutes => t('notifications_reminder_minutes');
  String get notificationsMinutesOff => t('notifications_minutes_off');
  String get notificationsJumuah => t('notifications_jumuah');
  String get notificationsJumuahDesc => t('notifications_jumuah_desc');
  String get notificationsJumuahBefore => t('notifications_jumuah_before');
  String get notificationsSound => t('notifications_sound');
  String get notificationsSoundDesc => t('notifications_sound_desc');
  String get notificationsModeAdhan => t('notifications_mode_adhan');
  String get notificationsModeBeep => t('notifications_mode_beep');
  String get notificationsModeVibrate => t('notifications_mode_vibrate');
  String get notificationsModeSilent => t('notifications_mode_silent');
  String get notificationsModeOff => t('notifications_mode_off');
  String get notificationsScheduled => t('notifications_scheduled');
  String get notificationsScheduledMany => t('notifications_scheduled_many');
  String get notificationsModeShortAdhan => t('notifications_mode_short_adhan');
  String get notificationsMinutesValue => t('notifications_minutes_value');
  String get notificationsScheduledNone => t('notifications_scheduled_none');
  // ══ أثري · الودجت (أسبوع ٤) ══
  String get atharyOpen => t('athary_open');
  String get atharyBest => t('athary_best');
  String get atharyLess => t('athary_less');
  String get atharyMore => t('athary_more');
  String get atharyNext => t('athary_next');
  String get atharyReset => t('athary_reset');
  String get atharyResetConfirm => t('athary_reset_confirm');
  String get atharyFutureNote => t('athary_future_note');
  String get homeWeek4Note => t('home_week4_note');
  String get widgetLabel => t('widget_label');
  String get widgetDescription => t('widget_description');
  String get widgetNextPrayer => t('widget_next_prayer');
  String get widgetIn => t('widget_in');
  String get widgetNoData => t('widget_no_data');
  String get widgetAdd => t('widget_add');
  String get widgetAddHint => t('widget_add_hint');
}
