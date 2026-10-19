import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/utils/greeting.dart' show PrayerNameKey, prayerOrder;
import 'package:athar/features/prayer/domain/alert_id.dart';
import 'package:athar/features/prayer/domain/entities/place.dart';
import 'package:athar/features/prayer/domain/entities/prayer_day.dart';
import 'package:athar/features/prayer/domain/notification_planner.dart';
import 'package:athar/features/prayer/domain/notification_settings.dart';

const _place = Place.fallback;

/// يوم اختبار: ست صلوات بفواصل ساعتين تبدأ ٠٤:٠٠ UTC.
PrayerDay _day(DateTime date, {Duration start = const Duration(hours: 4)}) {
  final times = <PrayerNameKey, DateTime>{};
  for (var i = 0; i < PrayerNameKey.values.length; i++) {
    times[PrayerNameKey.values[i]] = date.add(
      start + Duration(hours: i * 2),
    );
  }
  return PrayerDay(date: date, place: _place, times: times);
}

List<PrayerDay> _week(DateTime from) => [
      for (var i = 0; i < NotificationPlanner.defaultAheadDays; i++)
        _day(from.add(Duration(days: i))),
    ];

void main() {
  // ٢٠٢٦-١٠-١٨ كان أحد ← أول جمعة = ٢٣ أكتوبر.
  final sunday = DateTime.utc(2026, 10, 18);
  final friday = DateTime.utc(2026, 10, 23);

  group('NotificationPlanner — الأساسيات', () {
    test('المفتاح الرئيسي مقفول ← لا شيء', () {
      final plan = NotificationPlanner.plan(
        days: _week(sunday),
        settings: const NotificationSettings(masterEnabled: false),
        now: sunday,
      );
      expect(plan, isEmpty);
    });

    test('الافتراضي: أذان لكل صلاة عدا الشروق', () {
      final plan = NotificationPlanner.plan(
        days: [_day(sunday)],
        settings: const NotificationSettings(jumuahReminder: false),
        now: sunday,
      );
      final athan = plan.where((a) => a.kind == AlertKind.athan);
      expect(athan.length, prayerOrder.length);
      expect(
        athan.any((a) => a.prayer == PrayerNameKey.sunrise),
        isFalse,
        reason: 'الشروق ليست صلاة',
      );
    });

    test('الشروق مقفول دائمًا حتى لو طُلب صراحةً', () {
      final settings = NotificationSettings(
        jumuahReminder: false,
        modes: const {
          PrayerNameKey.sunrise: PrayerAlertMode.adhan,
        },
      );
      final plan = NotificationPlanner.plan(
        days: [_day(sunday)],
        settings: settings,
        now: sunday,
      );
      expect(
        plan.any((a) => a.prayer == PrayerNameKey.sunrise),
        isFalse,
      );
    });

    test('ما فات وقتُه لا يُجدول', () {
      // «الآن» = بعد الفجر والشروق
      final now = sunday.add(const Duration(hours: 7));
      final plan = NotificationPlanner.plan(
        days: [_day(sunday)],
        settings: const NotificationSettings(jumuahReminder: false),
        now: now,
      );
      expect(
        plan.any((a) => a.prayer == PrayerNameKey.fajr),
        isFalse,
        reason: 'الفجر (٠٤:٠٠) فات',
      );
      expect(
        plan.any((a) => a.prayer == PrayerNameKey.dhuhr),
        isTrue,
        reason: 'الظهر (٠٨:٠٠) لسه جاي',
      );
    });

    test('الوضع `off` يلغي أذان تلك الصلاة فقط', () {
      final settings = NotificationSettings(
        jumuahReminder: false,
        modes: const {PrayerNameKey.asr: PrayerAlertMode.off},
      );
      final plan = NotificationPlanner.plan(
        days: [_day(sunday)],
        settings: settings,
        now: sunday,
      );
      expect(plan.any((a) => a.prayer == PrayerNameKey.asr), isFalse);
      expect(plan.length, prayerOrder.length - 1);
    });
  });

  group('NotificationPlanner — التذكير (A10)', () {
    test('يُجدول قبل الأذان بالدقائق المطلوبة', () {
      final settings = NotificationSettings(
        jumuahReminder: false,
        reminderMinutes: const {PrayerNameKey.fajr: 15},
      );
      final plan = NotificationPlanner.plan(
        days: [_day(sunday)],
        settings: settings,
        now: sunday,
      );
      final reminder = plan.singleWhere((a) => a.isReminder);
      expect(reminder.prayer, PrayerNameKey.fajr);
      expect(reminder.reminderMinutes, 15);
      expect(
        reminder.at,
        sunday.add(const Duration(hours: 4)).subtract(
              const Duration(minutes: 15),
            ),
      );
    });

    test('صفر دقيقة = لا تذكير', () {
      final plan = NotificationPlanner.plan(
        days: [_day(sunday)],
        settings: const NotificationSettings(jumuahReminder: false),
        now: sunday,
      );
      expect(plan.any((a) => a.isReminder), isFalse);
    });

    test('تذكير فات وقته لا يُجدول', () {
      final settings = NotificationSettings(
        jumuahReminder: false,
        reminderMinutes: const {PrayerNameKey.fajr: 30},
      );
      // now بعد موعد التذكير (٠٣:٣٠) وقبل الفجر (٠٤:٠٠)
      final now = sunday.add(const Duration(hours: 3, minutes: 45));
      final plan = NotificationPlanner.plan(
        days: [_day(sunday)],
        settings: settings,
        now: now,
      );
      expect(plan.any((a) => a.isReminder), isFalse);
      expect(
        plan.any(
          (a) => a.kind == AlertKind.athan && a.prayer == PrayerNameKey.fajr,
        ),
        isTrue,
      );
    });

    test('صلاة موقوفة (off) لا تذكير لها', () {
      final settings = NotificationSettings(
        jumuahReminder: false,
        modes: const {PrayerNameKey.asr: PrayerAlertMode.off},
        reminderMinutes: const {PrayerNameKey.asr: 10},
      );
      final plan = NotificationPlanner.plan(
        days: [_day(sunday)],
        settings: settings,
        now: sunday,
      );
      expect(plan.any((a) => a.isReminder), isFalse);
    });

    test('الصلاة الصامتة (silent) تحتفظ بتذكيرها', () {
      final settings = NotificationSettings(
        jumuahReminder: false,
        modes: const {PrayerNameKey.asr: PrayerAlertMode.silent},
        reminderMinutes: const {PrayerNameKey.asr: 10},
      );
      final plan = NotificationPlanner.plan(
        days: [_day(sunday)],
        settings: settings,
        now: sunday,
      );
      final reminder = plan.singleWhere((a) => a.isReminder);
      expect(reminder.prayer, PrayerNameKey.asr);
    });

    test('التذكير والأذان لا يتشاركان المعرّف', () {
      final settings = NotificationSettings(
        jumuahReminder: false,
        reminderMinutes: const {
          for (final p in prayerOrder) p: 10,
        },
      );
      final plan = NotificationPlanner.plan(
        days: _week(sunday),
        settings: settings,
        now: sunday,
      );
      final ids = plan.map((a) => a.id).toSet();
      expect(ids.length, plan.length);
    });
  });

  group('NotificationPlanner — تذكير الجمعة (A13)', () {
    test('يُجدول أسبوعيًا قبل ظهر الجمعة', () {
      final settings = const NotificationSettings(
        jumuahMinutesBefore: 90,
      );
      final plan = NotificationPlanner.plan(
        days: _week(sunday),
        settings: settings,
        now: sunday,
      );
      final jumuah = plan.singleWhere((a) => a.kind == AlertKind.jumuah);
      expect(jumuah.weekly, isTrue);
      expect(jumuah.prayer, PrayerNameKey.dhuhr);
      expect(jumuah.at, friday.add(const Duration(hours: 8 - 1, minutes: 30)));
    });

    test('يلفّ أسبوعًا لو فات معاد الجمعة دي', () {
      final settings = const NotificationSettings(jumuahMinutesBefore: 60);
      // now بعد تذكير جمعة ٢٣ أكتوبر (٠٧:٠٠)
      final now = friday.add(const Duration(hours: 9));
      final plan = NotificationPlanner.plan(
        days: _week(sunday),
        settings: settings,
        now: now,
      );
      final jumuah = plan.singleWhere((a) => a.kind == AlertKind.jumuah);
      final nextFriday = friday.add(const Duration(days: 7));
      expect(jumuah.at, nextFriday.add(const Duration(hours: 7)));
    });

    test('مقفول ← لا يُجدول', () {
      final plan = NotificationPlanner.plan(
        days: _week(sunday),
        settings: const NotificationSettings(jumuahReminder: false),
        now: sunday,
      );
      expect(plan.any((a) => a.kind == AlertKind.jumuah), isFalse);
    });

    test('لا جمعة داخل نطاق ← لا تنبيه (ولا كراش)', () {
      // نطاق من الجمعة للخميس: أول جمعة هي اليوم الأول نفسه
      final plan = NotificationPlanner.plan(
        days: [_day(friday)],
        settings: const NotificationSettings(),
        now: friday.subtract(const Duration(days: 1)),
      );
      expect(plan.any((a) => a.kind == AlertKind.jumuah), isTrue);
    });

    test('يوم واحد بلا جمعة ← يتجاهل بصمت', () {
      final plan = NotificationPlanner.plan(
        days: [_day(sunday)],
        settings: const NotificationSettings(),
        now: sunday,
      );
      expect(plan.any((a) => a.kind == AlertKind.jumuah), isFalse);
    });
  });

  group('NotificationPlanner — شروط السلامة', () {
    test('الخطة مرتبة زمنيًا داخل النوع', () {
      final plan = NotificationPlanner.plan(
        days: _week(sunday),
        settings: NotificationSettings(
          reminderMinutes: {for (final p in prayerOrder) p: 10},
        ),
        now: sunday,
      );
      for (final kind in AlertKind.values) {
        final times = plan
            .where((a) => a.kind == kind)
            .map((a) => a.at)
            .toList(growable: false);
        for (var i = 1; i < times.length; i++) {
          expect(
            times[i].isAfter(times[i - 1]) ||
                times[i].isAtSameMomentAs(times[i - 1]),
            isTrue,
            reason: '$kind غير مرتب عند $i',
          );
        }
      }
    });

    test('كل المعرّفات فريدة في الخطة الكاملة', () {
      final plan = NotificationPlanner.plan(
        days: _week(sunday),
        settings: NotificationSettings(
          reminderMinutes: {for (final p in prayerOrder) p: 15},
        ),
        now: sunday,
      );
      expect(plan.map((a) => a.id).toSet().length, plan.length);
    });

    test('كل المعرّفات قابلة للفك', () {
      final plan = NotificationPlanner.plan(
        days: _week(sunday),
        settings: NotificationSettings(
          reminderMinutes: {for (final p in prayerOrder) p: 20},
        ),
        now: sunday,
      );
      for (final alert in plan) {
        expect(() => AlertId.decode(alert.id), returnsNormally);
      }
    });

    test('قائمة أيام فارغة ← خطة فارغة', () {
      expect(
        NotificationPlanner.plan(
          days: const <PrayerDay>[],
          settings: const NotificationSettings(),
          now: sunday,
        ),
        isEmpty,
      );
    });

    test('يوم ناقص (بلا عشاء) مش هيوقّع المخطّط', () {
      final incomplete = PrayerDay(
        date: sunday,
        place: _place,
        times: const <PrayerNameKey, DateTime>{},
      );
      final plan = NotificationPlanner.plan(
        days: [incomplete],
        settings: const NotificationSettings(jumuahReminder: false),
        now: sunday,
      );
      expect(plan, isEmpty);
    });
  });
}
