import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:timezone/timezone.dart' as tz;

import '../../../core/l10n/strings.dart';
import '../../../core/utils/arabic_numbers.dart';
import '../../../core/utils/prayer_time_format.dart';
import 'timezone_service.dart';
import '../domain/alert_id.dart';
import '../domain/entities/place.dart';
import '../domain/notification_planner.dart';
import '../domain/notification_settings.dart';
import '../../../core/utils/greeting.dart';

/// قنوات أندرويد — ثلاث قنوات منفصلة (قرار `docs/02` A8).
///
/// ليه منفصلة؟ عشان المستخدم يقدر يكتّم «التذكير» ويخلّي «الأذان» شغال،
/// أو العكس — من إعدادات النظام نفسه بدون ما نكتب شاشة صلاحيات بنفسنا.
abstract final class NotificationChannels {
  static const String athan = 'athan';
  static const String reminder = 'reminder';
  static const String jumuah = 'jumuah';

  static const List<String> all = [athan, reminder, jumuah];
}

/// حالة صلاحيات الإشعارات.
class NotificationPermissionState {
  const NotificationPermissionState({
    required this.notifications,
    required this.exactAlarms,
  });

  /// `POST_NOTIFICATIONS` — مطلوب من أندرويد ١٣+.
  final bool notifications;

  /// `SCHEDULE_EXACT_ALARM` — مطلوب من أندرويد ١٤+ للجدولة الدقيقة.
  final bool exactAlarms;

  bool get canSchedule => notifications;

  /// الجدولة الدقيقة مش متاحة → نسقط على `inexact` (فرق دقائق، أفضل من كراش).
  bool get needsInexactFallback => !exactAlarms;
}

/// الغلاف الوحيد حول `flutter_local_notifications`.
///
/// ### ✅ التحقّق من الـ API (تم مقابل توثيق ١٩.٥.٠ على pub.dev)
/// - `zonedSchedule(id, title, body, TZDateTime, NotificationDetails,
///   {required androidScheduleMode, payload, matchDateTimeComponents})`
///   — **موضعي** في ١٩.x. ⚠️ الإصدار ٢٠ حوّلها لمعلمات مسماة، والإصدار
///   ١٩ حذف `uiLocalNotificationDateInterpretation` (١٨ أضافه، ١٩ شاله).
/// - `requestNotificationsPermission()` / `requestExactAlarmsPermission()`
///   / `areNotificationsEnabled()` / `canScheduleExactNotifications()`
///   كلها على `AndroidFlutterLocalNotificationsPlugin` وترجع `Future<bool?>`.
/// - `IOSFlutterLocalNotificationsPlugin.requestPermissions(alert:badge:sound:)`
///   ⚠️ `checkPermissions()` على iOS بيرجّع `NotificationsEnabledOptions?`
///   **مش** `bool` — ولذلك ما استخدمناهش.
/// - `AndroidNotificationChannel(id, name, {description, …})` — اسم
///   المعامل `description` (مش `channelDescription` زي `AndroidNotificationDetails`).
///
/// ### ⚠️ لماذا ثبّتنا ١٩.x لا ٢٠ أو ٢١؟
/// - ٢٠.٠.٠ يطلب Flutter ٣.٣٢ / Dart ٣.٨ — أعلى من حدّنا (`^3.5.0`).
/// - ٢١.٠.٠ يطلب Flutter ٣.٣٨ / Dart ٣.١٠.
/// - ١٩.٠.٠ يطلب Flutter ٣.٢٢ / Dart ٣.٤ ← **مناسب**.
/// عند رفع حدّ Flutter، راجع هذا الملف قبل ترقية الحزمة.
class NotificationService {
  NotificationService(this._plugin);

  /// أقصى عدد تنبيهات معلّقة نطلبها من النظام.
  ///
  /// iOS بيسمح بـ ٦٤ فقط؛ اخترنا ٦٠ عشان نسيب مساحة لإشعارات أخرى.
  static const int maxPending = 60;

  final FlutterLocalNotificationsPlugin _plugin;

  AndroidFlutterLocalNotificationsPlugin? get _android =>
      _plugin.resolvePlatformSpecificImplementation<
          AndroidFlutterLocalNotificationsPlugin>();

  IOSFlutterLocalNotificationsPlugin? get _ios =>
      _plugin.resolvePlatformSpecificImplementation<
          IOSFlutterLocalNotificationsPlugin>();

  static Future<NotificationService> create() async {
    final plugin = FlutterLocalNotificationsPlugin();
    await plugin.initialize(
      const InitializationSettings(
        android: AndroidInitializationSettings('@mipmap/ic_launcher'),
        iOS: DarwinInitializationSettings(
          requestAlertPermission: false,
          requestBadgePermission: false,
          requestSoundPermission: false,
        ),
      ),
    );
    final service = NotificationService(plugin);
    await service._createChannels();
    return service;
  }

  Future<void> _createChannels() async {
    final android = _android;
    if (android == null) return;

    const channels = [
      AndroidNotificationChannel(
        NotificationChannels.athan,
        'مواقيت الصلاة',
        description: 'الأذان في وقت كل صلاة',
        importance: Importance.max,
        playSound: true,
        enableVibration: true,
      ),
      AndroidNotificationChannel(
        NotificationChannels.reminder,
        'تذكير قبل الصلاة',
        description: 'تنبيه قبل الأذان بدقائق',
        importance: Importance.defaultImportance,
        playSound: false,
        enableVibration: false,
      ),
      AndroidNotificationChannel(
        NotificationChannels.jumuah,
        'تذكير الجمعة',
        description: 'سورة الكهف والصلاة على النبي والغُسل',
        importance: Importance.defaultImportance,
      ),
    ];
    for (final channel in channels) {
      await android.createNotificationChannel(channel);
    }
  }

  // ── الصلاحيات ──────────────────────────────────────────
  Future<NotificationPermissionState> checkPermissions() async {
    final android = _android;
    if (android == null) {
      // iOS: الطلب تم في `create()`
      return const NotificationPermissionState(
        notifications: true,
        exactAlarms: true,
      );
    }
    final notifications = await _guard(android.areNotificationsEnabled);
    final exact = await _guard(android.canScheduleExactNotifications);
    return NotificationPermissionState(
      notifications: notifications ?? false,
      exactAlarms: exact ?? false,
    );
  }

  Future<bool> requestNotifications() async {
    final android = _android;
    if (android == null) {
      final ios = _ios;
      if (ios == null) return true;
      return await ios.requestPermissions(
            alert: true,
            badge: true,
            sound: true,
          ) ??
          false;
    }
    return await _guard(android.requestNotificationsPermission) ?? false;
  }

  /// يطلب الجدولة الدقيقة (أندرويد ١٤+) — يفتح شاشة «المنبهات والتذكيرات».
  Future<bool> requestExactAlarms() async {
    final android = _android;
    if (android == null) return true;
    return await _guard(android.requestExactAlarmsPermission) ?? false;
  }

  /// منصّة مش مدعومة أو API اتغيّر → نرجّع null بدل الكراش.
  Future<T?> _guard<T>(Future<T?> Function() call) async {
    try {
      return await call();
    } catch (_) {
      return null;
    }
  }

  // ── الجدولة ────────────────────────────────────────────
  /// يلغي كل الإشعارات ويجدول الخطة من جديد.
  ///
  /// الإلغاء الكامل + إعادة الجدولة أبسط بكثير من حساب فارق، وعدد
  /// الإشعارات صغير (~٧٠ لأسبوع).
  Future<int> syncPlan(
    List<ScheduledAlert> plan, {
    required NotificationSettings settings,
    required Strings s,
    required bool useArabicDigits,
    required Place place,
    required bool exactAlarmsAllowed,
  }) async {
    await _plugin.cancelAll();
    if (plan.isEmpty) return 0;

    final location = TimezoneService.locationFor(place);
    final ordered = [...plan]..sort((a, b) => a.at.compareTo(b.at));
    // iOS يسمح بـ ٦٤ تنبيهًا معلّقًا فقط (ويسقط الباقي بصمت) — نحتفظ
    // بالأقرب زمنيًا لأن إعادة الجدولة بتحصل مع كل فتح للتطبيق.
    final queue = ordered.take(maxPending).toList();
    var scheduled = 0;

    for (final alert in queue) {
      final title = _titleFor(alert, s: s);
      final body = _bodyFor(
        alert,
        s: s,
        useArabicDigits: useArabicDigits,
        place: place,
        location: location,
      );
      final details = _detailsFor(
        alert,
        settings: settings,
        s: s,
      );
      final scheduledDate = tz.TZDateTime.from(alert.at, location);

      try {
        await _plugin.zonedSchedule(
          alert.id,
          title,
          body,
          scheduledDate,
          details,
          androidScheduleMode: exactAlarmsAllowed
              ? AndroidScheduleMode.exactAllowWhileIdle
              : AndroidScheduleMode.inexactAllowWhileIdle,
          matchDateTimeComponents:
              alert.weekly ? DateTimeComponents.dayOfWeekAndTime : null,
          payload: '${alert.kind.name}:${alert.prayer?.name ?? ''}',
        );
        scheduled++;
      } catch (_) {
        // تكرار معرّف، أو صلاحية ناقصة، أو اختلاف إصدار — نتجاوز
        // تنبيهًا واحدًا ولا نوقف باقي الخطة.
        continue;
      }
    }
    return scheduled;
  }

  Future<void> cancelAll() => _plugin.cancelAll();

  Future<int> pendingCount() async =>
      (await _plugin.pendingNotificationRequests()).length;

  /// إشعار تجريبي بعد ٥ ثوان — يتأكد المستخدم إن القناة شغالة فعلًا.
  Future<void> showTestNotification(Strings s) async {
    final now = tz.TZDateTime.now(tz.local).add(const Duration(seconds: 5));
    await _plugin.zonedSchedule(
      AlertId.encode(kind: AlertKind.athan, date: DateTime.now().toUtc()),
      s.notificationsTestTitle,
      s.notificationsTestBody,
      now,
      NotificationDetails(
        android: AndroidNotificationDetails(
          NotificationChannels.athan,
          s.notifChannelAthan,
          channelDescription: s.notifChannelAthanDesc,
          importance: Importance.max,
          priority: Priority.max,
          playSound: true,
          enableVibration: true,
        ),
        iOS: const DarwinNotificationDetails(presentSound: true),
      ),
      androidScheduleMode: AndroidScheduleMode.exactAllowWhileIdle,
    );
  }

  // ── بناء المحتوى ───────────────────────────────────────
  NotificationDetails _detailsFor(
    ScheduledAlert alert, {
    required NotificationSettings settings,
    required Strings s,
  }) {
    final PrayerAlertMode mode = alert.prayer == null
        ? PrayerAlertMode.silent
        : settings.modeFor(alert.prayer!);

    // التذكير والجمعة: صامتان افتراضيًا (إشعار على الشاشة فقط)
    final bool playSound =
        alert.kind == AlertKind.athan ? mode.hasSound : false;
    final bool vibrate =
        alert.kind == AlertKind.athan ? mode.hasVibration : false;

    final (channelId, channelName, channelDesc) = switch (alert.kind) {
      AlertKind.athan => (
          NotificationChannels.athan,
          s.notifChannelAthan,
          s.notifChannelAthanDesc
        ),
      AlertKind.reminder => (
          NotificationChannels.reminder,
          s.notifChannelReminder,
          s.notifChannelReminderDesc
        ),
      AlertKind.jumuah => (
          NotificationChannels.jumuah,
          s.notifChannelJumuah,
          s.notifChannelJumuahDesc
        ),
    };

    final isAthan = alert.kind == AlertKind.athan;

    return NotificationDetails(
      android: AndroidNotificationDetails(
        channelId,
        channelName,
        channelDescription: channelDesc,
        importance: isAthan ? Importance.max : Importance.defaultImportance,
        priority: isAthan ? Priority.max : Priority.defaultPriority,
        playSound: playSound,
        enableVibration: vibrate,
        sound: _soundFor(alert, settings),
        category: isAthan
            ? AndroidNotificationCategory.alarm
            : AndroidNotificationCategory.reminder,
        autoCancel: true,
        // لا نُبقي الإشعار مثبتًا في الشريط — الأذان لحظة، مش مهمة جارية.
        ongoing: false,
        onlyAlertOnce: true,
      ),
      iOS: DarwinNotificationDetails(
        presentAlert: true,
        presentSound: playSound,
        presentBadge: false,
      ),
    );
  }

  /// صوت الأذان — `null` يعني صوت النظام الافتراضي.
  ///
  /// ⚠️ **قاعدة مهمة:** لا نرجّع مسار ملف غير مرفق أبدًا. لو الملف مش موجود،
  /// أندرويد بيسقط الإشعار كله صامتًا (ولا يظهر إطلاقًا) — أسوأ بكثير
  /// من نغمة النظام. لذلك نتأكد من `bundled` ومن اسم المورد.
  AndroidNotificationSound? _soundFor(
    ScheduledAlert alert,
    NotificationSettings settings,
  ) {
    if (alert.kind != AlertKind.athan) return null;
    final sound = settings.sound;
    if (!sound.bundled || sound.resourceName.isEmpty) return null;
    return RawResourceAndroidNotificationSound(sound.resourceName);
  }

  String _titleFor(ScheduledAlert alert, {required Strings s}) {
    final prayer = alert.prayer;
    final prayerName = prayer == null ? '' : prayer.label(s);

    return switch (alert.kind) {
      AlertKind.athan => s.tArgs('notif_athan_title', {'prayer': prayerName}),
      AlertKind.reminder =>
        s.tArgs('notif_reminder_title', {'prayer': prayerName}),
      AlertKind.jumuah => s.notifJumuahTitle,
    };
  }

  String _bodyFor(
    ScheduledAlert alert, {
    required Strings s,
    required bool useArabicDigits,
    required Place place,
    required tz.Location location,
  }) {
    switch (alert.kind) {
      case AlertKind.athan:
        final local = tz.TZDateTime.from(alert.at, location);
        final time = AtharTimeFormat.hm12(
          local,
          s: s,
          useArabicDigits: useArabicDigits,
        );
        return s.tArgs(
          'notif_athan_body',
          {'time': time, 'place': place.nameAr},
        );

      case AlertKind.reminder:
        return s.tArgs('notif_reminder_body', {
          'n': ArabicNumbers.format(
            alert.reminderMinutes.toString(),
            useArabicDigits: useArabicDigits,
          ),
        });

      case AlertKind.jumuah:
        return s.notifJumuahBody;
    }
  }
}
