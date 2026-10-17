import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/l10n/strings.dart';
import '../../../core/settings/settings_controller.dart';
import '../data/notification_service.dart';
import '../domain/entities/prayer_day.dart';
import '../domain/notification_planner.dart';
import '../domain/notification_settings.dart';
import 'notification_settings_controller.dart';
import 'prayer_providers.dart';
import 'prayer_settings_controller.dart';

/// الغلاف الحيّ لخدمة الإشعارات — المكان الوحيد اللي بناء الخدمة بيحصل فيه.
final notificationServiceProvider =
    FutureProvider<NotificationService>((ref) => NotificationService.create());

/// الخطة المحسوبة — **محضة**: تعتمد على الإعدادات + المكان + المواقيت.
///
/// ⚠️ `now` تُقرأ لحظة البناء؛ لذلك الخطة تتحدّث عند أي تغيير في
/// الإعدادات أو المكان، ومع كل استدعاء `sync()`. التنبيهات اللي فات وقتها
/// بتتصفّى تلقائيًا في المخطّط.
final notificationPlanProvider = Provider<List<ScheduledAlert>>((ref) {
  final settings = ref.watch(notificationSettingsProvider);
  if (!settings.masterEnabled) return const <ScheduledAlert>[];

  final place = ref.watch(activePlaceProvider);
  final engine = ref.watch(prayerEngineProvider);
  final prayerSettings = ref.watch(prayerSettingsProvider);
  final today = ref.watch(civilDateProvider);

  final days = <PrayerDay>[];
  for (var i = 0; i < NotificationPlanner.defaultAheadDays; i++) {
    final date = today.add(Duration(days: i));
    try {
      days.add(
        engine.calculate(
          place: place,
          date: date,
          settings: prayerSettings,
        ),
      );
    } catch (_) {
      // يوم واحد فشل حسابه مش سبب يوقف بقية الأسبوع.
    }
  }

  return NotificationPlanner.plan(
    days: days,
    settings: settings,
    now: DateTime.now(),
  );
});

/// حالة المزامنة كما يراها المستخدم على الشاشة.
class NotificationStatus {
  const NotificationStatus({
    this.permissions,
    this.scheduledCount = 0,
    this.syncing = false,
    this.lastError,
  });

  final NotificationPermissionState? permissions;

  /// عدد التنبيهات المجدولة فعلًا عند النظام.
  final int scheduledCount;

  final bool syncing;

  /// رسالة خطأ موجزة للعرض (إن وُجدت).
  final String? lastError;

  bool get hasPermissionIssue =>
      permissions != null && !permissions!.notifications;

  NotificationStatus copyWith({
    NotificationPermissionState? permissions,
    int? scheduledCount,
    bool? syncing,
    Object? lastError = _sentinel,
  }) =>
      NotificationStatus(
        permissions: permissions ?? this.permissions,
        scheduledCount: scheduledCount ?? this.scheduledCount,
        syncing: syncing ?? this.syncing,
        lastError: identical(lastError, _sentinel)
            ? this.lastError
            : lastError?.toString(),
      );
}

const Object _sentinel = Object();

/// يحسب الخطة ويدفعها للنظام — ويحدّث الحالة المعروضة.
///
/// الاستخدام:
/// ```dart
/// await ref.read(notificationStatusProvider.notifier).sync();
/// ```
final notificationStatusProvider =
    StateNotifierProvider<NotificationStatusController, NotificationStatus>(
        (ref) => NotificationStatusController(ref));

class NotificationStatusController extends StateNotifier<NotificationStatus> {
  NotificationStatusController(this._ref) : super(const NotificationStatus());

  final Ref _ref;

  /// يقرأ الصلاحيات من النظام ويحدّث الحالة فقط (بدون جدولة).
  Future<void> refreshPermissions() async {
    final service = await _ref.read(notificationServiceProvider.future);
    final permissions = await service.checkPermissions();
    state = state.copyWith(permissions: permissions);
  }

  /// يطلب إذن الإشعارات من النظام ثم يحدّث الحالة.
  Future<bool> requestPermission() async {
    final service = await _ref.read(notificationServiceProvider.future);
    final granted = await service.requestNotifications();
    await refreshPermissions();
    return granted;
  }

  /// يطلب الجدولة الدقيقة (أندرويد ١٤+) ثم يحدّث الحالة.
  Future<bool> requestExactAlarms() async {
    final service = await _ref.read(notificationServiceProvider.future);
    final granted = await service.requestExactAlarms();
    await refreshPermissions();
    return granted;
  }

  /// يعيد جدولة كل شيء من الصفر.
  ///
  /// يُستدعى عند: أول تشغيل · أي تغيير إعداد · تغيير المكان ·
  /// العودة للتطبيق (بعد صلاحية `SCHEDULE_EXACT_ALARM` مثلًا).
  Future<void> sync() async {
    state = state.copyWith(syncing: true, lastError: null);

    final service = await _ref.read(notificationServiceProvider.future);
    final settings = _ref.read(notificationSettingsProvider);

    try {
      if (!settings.masterEnabled) {
        await service.cancelAll();
        state = state.copyWith(scheduledCount: 0, syncing: false);
        return;
      }

      final permissions = await service.checkPermissions();
      final plan = _ref.read(notificationPlanProvider);
      final place = _ref.read(activePlaceProvider);
      final s = _ref.read(stringsProvider);
      final useArabicDigits = _ref.read(useArabicDigitsProvider);

      final count = await service.syncPlan(
        plan,
        settings: settings,
        s: s,
        useArabicDigits: useArabicDigits,
        place: place,
        exactAlarmsAllowed: permissions.exactAlarms,
      );

      state = state.copyWith(
        permissions: permissions,
        scheduledCount: count,
        syncing: false,
      );
    } catch (error) {
      // فشل الجدولة (صلاحية/إصدار/منصّة) — **لا** نوقّع التطبيق.
      state = state.copyWith(syncing: false, lastError: error);
    }
  }
}
