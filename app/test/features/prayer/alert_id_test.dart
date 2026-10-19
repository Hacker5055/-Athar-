import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/utils/greeting.dart' show PrayerNameKey;
import 'package:athar/features/prayer/domain/alert_id.dart';

void main() {
  const date = '2026-10-18'; // أي يوم ثابت

  DateTime d(String ymd) {
    final parts = ymd.split('-').map(int.parse).toList();
    return DateTime.utc(parts[0], parts[1], parts[2]);
  }

  group('AlertId', () {
    test('مستقر: نفس المدخلات ← نفس المعرّف', () {
      final a = AlertId.encode(
        kind: AlertKind.athan,
        date: d(date),
        prayer: PrayerNameKey.fajr,
      );
      final b = AlertId.encode(
        kind: AlertKind.athan,
        date: d(date),
        prayer: PrayerNameKey.fajr,
      );
      expect(a, b);
    });

    test('فريد لكل (نوع · يوم · صلاة)', () {
      final ids = <int>{
        for (final kind in AlertKind.values)
          for (final prayer in PrayerNameKey.values)
            AlertId.encode(kind: kind, date: d(date), prayer: prayer),
      };
      // ٣ أنواع × ٦ أوقات = ١٨ معرّفًا مختلفًا
      expect(ids.length, AlertKind.values.length * PrayerNameKey.values.length);
    });

    test('فريد عبر سبعة أيام متتالية', () {
      final base = d(date);
      final ids = <int>{
        for (var i = 0; i < 7; i++)
          for (final prayer in PrayerNameKey.values)
            AlertId.encode(
              kind: AlertKind.athan,
              date: base.add(Duration(days: i)),
              prayer: prayer,
            ),
      };
      expect(ids.length, 7 * PrayerNameKey.values.length);
    });

    test('فك الترميز يعيد نفس المدخلات', () {
      for (final kind in AlertKind.values) {
        for (final prayer in PrayerNameKey.values) {
          final id = AlertId.encode(
            kind: kind,
            date: d(date),
            prayer: prayer,
          );
          final decoded = AlertId.decode(id);
          expect(decoded.kind, kind, reason: 'kind $kind');
          expect(decoded.prayer, prayer, reason: 'prayer $prayer');
          expect(decoded.date, d(date));
        }
      }
    });

    test('تذكير الجمعة: معرّف واحد بلا صلاة مرتبطة', () {
      final id = AlertId.encode(kind: AlertKind.jumuah, date: d(date));
      final decoded = AlertId.decode(id);
      expect(decoded.kind, AlertKind.jumuah);
      expect(decoded.prayer, isNull); // خانة الصلاة = ٠
      expect(decoded.date, d(date));
    });

    test('المعرّفات موجبة وفي نطاق int32 الآمن', () {
      final id = AlertId.encode(
        kind: AlertKind.jumuah,
        date: d('2100-12-31'),
        prayer: PrayerNameKey.isha,
      );
      expect(id, greaterThan(0));
      // أندرويد بيستخدم int جافا (٣٢ بت) لمعرّف الإشعار
      expect(id, lessThan(2147483647));
    });

    test('يوم ٢٠٠٠-٠١-٠١ هو الأساس (dayEpoch = ٠)', () {
      final id = AlertId.encode(
        kind: AlertKind.athan,
        date: d('2000-01-01'),
        prayer: PrayerNameKey.fajr,
      );
      // kind=0 → 0 · dayEpoch=0 → 0 · slot(فجر)=1
      expect(id, 1);
      final jumuahId = AlertId.encode(
        kind: AlertKind.jumuah,
        date: d('2000-01-01'),
      );
      expect(jumuahId, 2 * 1000000);
    });
  });
}
