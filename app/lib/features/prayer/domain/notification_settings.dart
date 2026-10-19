import 'dart:convert';

import '../../../core/l10n/app_locale.dart';
import '../../../core/utils/greeting.dart' show PrayerNameKey, prayerOrder;

/// وضع التنبيه لكل صلاة (A8) — كل صلاة مستقلة عن الباقي.
enum PrayerAlertMode {
  /// أذان كامل (ملف صوتي مرفق — إن لم يكن مرفقًا يسقط تلقائيًا للنغمة).
  adhan,

  /// أذان مختصر (أول الأذان فقط).
  shortAdhan,

  /// نغمة تنبيه قصيرة (صوت النظام).
  beep,

  /// اهتزاز فقط — بلا صوت.
  vibrate,

  /// إشعار صامت على الشاشة فقط.
  silent,

  /// لا إشعار إطلاقًا.
  off;

  bool get isEnabled => this != PrayerAlertMode.off;
  bool get hasSound =>
      this == PrayerAlertMode.adhan ||
      this == PrayerAlertMode.shortAdhan ||
      this == PrayerAlertMode.beep;
  bool get hasVibration =>
      this == PrayerAlertMode.adhan ||
      this == PrayerAlertMode.shortAdhan ||
      this == PrayerAlertMode.vibrate;

  /// هل يحتاج ملف صوت مرفق في أصول التطبيق؟
  bool get needsBundledAudio =>
      this == PrayerAlertMode.adhan || this == PrayerAlertMode.shortAdhan;
}

/// أصوات الأذان المتاحة (A9).
///
/// ⚠️ **لا نرفق أي ملف صوتي في المستودع.** كل الأصوات `bundled: false`
/// حتى يضيفها المالك بنفسه بعد التحقق من الترخيص (راجع `docs/05` §3.3
/// و`app/assets/audio/README.md`).
class AdhanSound {
  const AdhanSound({
    required this.id,
    required this.nameAr,
    required this.nameEn,
    required this.nameFr,
    this.assetPath = '',
    this.resourceName = '',
    this.bundled = false,
  });

  final String id;
  final String nameAr;
  final String nameEn;
  final String nameFr;

  /// مسار الأصل داخل `assets/` — يُترك فارغًا لصوت النظام.
  ///
  /// ⚠️ الأصل المُعلن في `pubspec.yaml` لا يصلح لإشعارات أندرويد: المنصّة
  /// تشترط **موردًا خامًا** في `android/app/src/main/res/raw/`، ولذلك نقرأ
  /// الصوت من [resourceName] لا من هنا.
  final String assetPath;

  /// اسم المورد الخام (بدون امتداد) داخل
  /// `android/app/src/main/res/raw/` — مثال: `adhan_makkah`.
  final String resourceName;

  /// هل الملف مرفق فعلًا في التطبيق؟
  final bool bundled;

  bool get isSystemSound => !bundled;

  String nameFor(AppLocale locale) => switch (locale) {
        AppLocale.ar => nameAr,
        AppLocale.en => nameEn,
        AppLocale.fr => nameFr,
      };

  /// يحتاج ملفًا لا نملكه بعد (رخصة غير متحقّق منها).
  bool get needsBundledAudio => assetPath.isNotEmpty;

  /// جاهز للتشغيل من الإشعار؟
  bool get canPlayFromNotification => bundled && resourceName.isNotEmpty;
}

abstract final class AdhanSounds {
  /// نغمة النظام — الخيار الافتراضي الآمن والمتاح دائمًا.
  static const system = AdhanSound(
    id: 'system',
    nameAr: 'نغمة النظام',
    nameEn: 'System tone',
    nameFr: 'Sonnerie du système',
  );

  static const makkah = AdhanSound(
    id: 'makkah',
    nameAr: 'أذان مكة',
    nameEn: 'Makkah adhan',
    nameFr: 'Adhan de La Mecque',
    assetPath: 'assets/audio/adhan_makkah.mp3',
    resourceName: 'adhan_makkah',
  );

  static const madinah = AdhanSound(
    id: 'madinah',
    nameAr: 'أذان المدينة',
    nameEn: 'Madinah adhan',
    nameFr: 'Adhan de Médine',
    assetPath: 'assets/audio/adhan_madinah.mp3',
    resourceName: 'adhan_madinah',
  );

  static const egypt = AdhanSound(
    id: 'egypt',
    nameAr: 'أذان مصر',
    nameEn: 'Egyptian adhan',
    nameFr: 'Adhan égyptien',
    assetPath: 'assets/audio/adhan_egypt.mp3',
    resourceName: 'adhan_egypt',
  );

  static const quds = AdhanSound(
    id: 'quds',
    nameAr: 'أذان القدس',
    nameEn: 'Al-Quds adhan',
    nameFr: 'Adhan d’Al-Qods',
    assetPath: 'assets/audio/adhan_quds.mp3',
    resourceName: 'adhan_quds',
  );

  static const turkish = AdhanSound(
    id: 'turkish',
    nameAr: 'أذان تركي',
    nameEn: 'Turkish adhan',
    nameFr: 'Adhan turc',
    assetPath: 'assets/audio/adhan_turkish.mp3',
    resourceName: 'adhan_turkish',
  );

  static const List<AdhanSound> all = [
    system,
    makkah,
    madinah,
    egypt,
    quds,
    turkish,
  ];

  static AdhanSound byId(String? id) => all.firstWhere(
        (sound) => sound.id == id,
        orElse: () => system,
      );
}

/// إعدادات الإشعارات — تُحفظ تحت `athar.notifications.v1`.
class NotificationSettings {
  const NotificationSettings({
    this.masterEnabled = true,
    this.modes = const <PrayerNameKey, PrayerAlertMode>{},
    this.reminderMinutes = const <PrayerNameKey, int>{},
    this.jumuahReminder = true,
    this.jumuahMinutesBefore = 90,
    this.adhanSoundId = AdhanSoundOption.systemId,
  });

  /// مفتاح رئيسي — يوقف كل الإشعارات دفعة واحدة (مفيد للسفر/الصمت).
  final bool masterEnabled;

  /// وضع التنبيه لكل صلاة (الافتراضي: أذان للكل عدا الشروق).
  final Map<PrayerNameKey, PrayerAlertMode> modes;

  /// دقائق التذكير قبل كل صلاة (٠ = بلا تذكير).
  final Map<PrayerNameKey, int> reminderMinutes;

  /// تذكير الجمعة (سورة الكهف · الصلاة على النبي · الغُسل) — A13.
  final bool jumuahReminder;

  /// كم دقيقة قبل الظهر يوم الجمعة.
  final int jumuahMinutesBefore;

  /// معرّف صوت الأذان من [AdhanSounds].
  final String adhanSoundId;

  static const List<int> reminderChoices = [0, 5, 10, 15, 20, 30];

  PrayerAlertMode modeFor(PrayerNameKey prayer) =>
      (prayer == PrayerNameKey.sunrise)
          ? PrayerAlertMode.off // الشروق ليست صلاة → لا أذان
          : (modes[prayer] ?? PrayerAlertMode.adhan);

  int reminderFor(PrayerNameKey prayer) => reminderMinutes[prayer] ?? 0;

  AdhanSound get sound => AdhanSounds.byId(adhanSoundId);

  /// هل يطلب المستخدم أذانًا صوتيًا لأي صلاة؟
  bool get wantsBundledAudio =>
      masterEnabled && prayerOrder.any((p) => modeFor(p).needsBundledAudio);

  /// الصوت المطلوب مش مرفق — نعرض تنبيهًا بدل نغمة صامتة مفاجئة.
  bool get audioUnavailable => wantsBundledAudio && !sound.canPlayFromNotification;

  NotificationSettings copyWith({
    bool? masterEnabled,
    Map<PrayerNameKey, PrayerAlertMode>? modes,
    Map<PrayerNameKey, int>? reminderMinutes,
    bool? jumuahReminder,
    int? jumuahMinutesBefore,
    String? adhanSoundId,
  }) =>
      NotificationSettings(
        masterEnabled: masterEnabled ?? this.masterEnabled,
        modes: modes ?? this.modes,
        reminderMinutes: reminderMinutes ?? this.reminderMinutes,
        jumuahReminder: jumuahReminder ?? this.jumuahReminder,
        jumuahMinutesBefore: jumuahMinutesBefore ?? this.jumuahMinutesBefore,
        adhanSoundId: adhanSoundId ?? this.adhanSoundId,
      );

  NotificationSettings copyWithMode(PrayerNameKey prayer, PrayerAlertMode mode) {
    final next = Map<PrayerNameKey, PrayerAlertMode>.from(modes);
    next[prayer] = mode;
    return copyWith(modes: next);
  }

  NotificationSettings copyWithReminder(PrayerNameKey prayer, int minutes) {
    final next = Map<PrayerNameKey, int>.from(reminderMinutes);
    final clamped = reminderChoices.contains(minutes) ? minutes : 0;
    if (clamped == 0) {
      next.remove(prayer);
    } else {
      next[prayer] = clamped;
    }
    return copyWith(reminderMinutes: next);
  }

  // ── الترميز ────────────────────────────────────────────
  String encode() => jsonEncode({
        'on': masterEnabled,
        'modes': {
          for (final entry in modes.entries) entry.key.name: entry.value.name,
        },
        'rem': {
          for (final entry in reminderMinutes.entries)
            if (entry.value > 0) entry.key.name: entry.value,
        },
        'jum': jumuahReminder,
        'jumMin': jumuahMinutesBefore,
        'sound': adhanSoundId,
      });

  static NotificationSettings decode(String? raw) {
    if (raw == null || raw.isEmpty) return const NotificationSettings();
    Object? data;
    try {
      data = jsonDecode(raw);
    } catch (_) {
      return const NotificationSettings();
    }
    if (data is! Map) return const NotificationSettings();

    final modes = <PrayerNameKey, PrayerAlertMode>{};
    final modesRaw = data['modes'];
    if (modesRaw is Map) {
      final byName = PrayerAlertMode.values.asNameMap();
      final prayerByName = PrayerNameKey.values.asNameMap();
      for (final entry in modesRaw.entries) {
        final prayer = prayerByName[entry.key];
        final mode = byName[entry.value];
        if (prayer != null && mode != null) modes[prayer] = mode;
      }
    }

    final rem = <PrayerNameKey, int>{};
    final remRaw = data['rem'];
    if (remRaw is Map) {
      final prayerByName = PrayerNameKey.values.asNameMap();
      for (final entry in remRaw.entries) {
        final prayer = prayerByName[entry.key];
        final minutes = entry.value;
        if (prayer != null &&
            minutes is int &&
            reminderChoices.contains(minutes)) {
          rem[prayer] = minutes;
        }
      }
    }

    final jumMin = data['jumMin'];
    final sound = data['sound'];

    return NotificationSettings(
      masterEnabled: data['on'] is bool ? data['on'] as bool : true,
      modes: modes,
      reminderMinutes: rem,
      jumuahReminder: data['jum'] is bool ? data['jum'] as bool : true,
      jumuahMinutesBefore:
          (jumMin is int) ? jumMin.clamp(30, 240) : 90,
      adhanSoundId:
          (sound is String && sound.isNotEmpty) ? sound : AdhanSoundOption.systemId,
    );
  }
}

/// ثوابت مساعدة للصوت الافتراضي.
abstract final class AdhanSoundOption {
  static const String systemId = 'system';
}
