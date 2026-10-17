import '../../../core/utils/greeting.dart' show PrayerNameKey, prayerOrder;
import 'alert_id.dart';
import 'entities/prayer_day.dart';
import 'notification_settings.dart';

/// تنبيه مجدول — **متى** فقط.
///
/// فصلنا «متى» (هنا، محض وقابل للاختبار بالكامل) عن «ماذا»
/// (نص الإشعار وصوته، يبنيهما `NotificationService` وقت الجدولة).
class ScheduledAlert {
  const ScheduledAlert({
    required this.id,
    required this.kind,
    required this.at,
    this.prayer,
    this.weekly = false,
    this.reminderMinutes = 0,
  });

  final int id;
  final AlertKind kind;

  /// لحظة التنبيه (UTC instant).
  final DateTime at;

  final PrayerNameKey? prayer;

  /// يتكرر أسبوعيًا (تذكير الجمعة).
  final bool weekly;

  /// لتنبيهات التذكير فقط: كم دقيقة قبل الصلاة.
  final int reminderMinutes;

  bool get isReminder => kind == AlertKind.reminder;

  @override
  String toString() =>
      'ScheduledAlert(${kind.name}, ${prayer?.name}, ${at.toIso8601String()}'
      '${weekly ? ', weekly' : ''})';
}

/// يبني خطة الجدولة من مواقيت الأيام القادمة + إعدادات المستخدم.
///
/// **محض تمامًا** — لا حزم خارجية ولا منصّة.
abstract final class NotificationPlanner {
  /// كم يومًا نجدول مسبقًا.
  ///
  /// أسبوع كامل يكفي لأن إعادة الجدولة بتحصل عند فتح التطبيق وعند أي
  /// تغيير في الإعدادات، وفي الوقت نفسه يبقي عدد الإشعارات المعلّقة صغيرًا.
  static const int defaultAheadDays = 7;

  static List<ScheduledAlert> plan({
    required List<PrayerDay> days,
    required NotificationSettings settings,
    required DateTime now,
  }) {
    if (!settings.masterEnabled) return const <ScheduledAlert>[];

    final alerts = <ScheduledAlert>[];

    for (final day in days) {
      for (final prayer in prayerOrder) {
        final at = day.timeFor(prayer);
        if (at == null) continue;

        final mode = settings.modeFor(prayer);

        // ١) أذان الصلاة — نتخطّى ما فات وقته
        if (mode.isEnabled && !at.isBefore(now)) {
          alerts.add(
            ScheduledAlert(
              id: AlertId.encode(
                kind: AlertKind.athan,
                date: day.date,
                prayer: prayer,
              ),
              kind: AlertKind.athan,
              at: at,
              prayer: prayer,
            ),
          );
        }

        // ٢) تذكير قبلها — لا تذكير لصلاة أوقف المستخدم أذانها
        final reminder = settings.reminderFor(prayer);
        if (reminder > 0 && mode.isEnabled) {
          final reminderAt = at.subtract(Duration(minutes: reminder));
          if (!reminderAt.isBefore(now)) {
            alerts.add(
              ScheduledAlert(
                id: AlertId.encode(
                  kind: AlertKind.reminder,
                  date: day.date,
                  prayer: prayer,
                ),
                kind: AlertKind.reminder,
                at: reminderAt,
                prayer: prayer,
                reminderMinutes: reminder,
              ),
            );
          }
        }
      }
    }

    // ٣) تذكير الجمعة — أسبوعي متكرر
    final jumuah = _jumuahAlert(days: days, settings: settings, now: now);
    if (jumuah != null) alerts.add(jumuah);

    return alerts;
  }

  /// أول جمعة قادمة داخل النطاق.
  static ScheduledAlert? _jumuahAlert({
    required List<PrayerDay> days,
    required NotificationSettings settings,
    required DateTime now,
  }) {
    if (!settings.jumuahReminder) return null;

    for (final day in days) {
      if (day.date.weekday != DateTime.friday) continue;
      final dhuhr = day.timeFor(PrayerNameKey.dhuhr);
      if (dhuhr == null) continue;

      var at = dhuhr.subtract(
        Duration(minutes: settings.jumuahMinutesBefore),
      );
      // لو فات معادها، التكرار الأسبوعي هيوافق الجمعة الجاية
      if (at.isBefore(now)) {
        at = at.add(const Duration(days: 7));
      }

      return ScheduledAlert(
        id: AlertId.encode(kind: AlertKind.jumuah, date: day.date),
        kind: AlertKind.jumuah,
        at: at,
        prayer: PrayerNameKey.dhuhr,
        weekly: true,
      );
    }
    return null;
  }
}
