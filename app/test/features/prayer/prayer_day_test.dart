import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/utils/greeting.dart';
import 'package:athar/features/prayer/domain/entities/place.dart';
import 'package:athar/features/prayer/domain/entities/prayer_day.dart';

/// يوم اصطناعي: ٢٠٢٦-٠١-١٥ بالتوقيت العالمي.
/// الأوقات كلها UTC instants عشان المقارنة مع `DateTime.now()` تبقى صحيحة.
PrayerDay _day(Map<PrayerNameKey, int> minutesFromMidnight) {
  final base = DateTime.utc(2026, 1, 15);
  return PrayerDay(
    date: base,
    place: Place.fallback,
    times: {
      for (final entry in minutesFromMidnight.entries)
        entry.key: base.add(Duration(minutes: entry.value)),
    },
  );
}

final _typical = _day({
  PrayerNameKey.fajr: 5 * 60 + 32, // 05:32
  PrayerNameKey.sunrise: 6 * 60 + 58, // 06:58
  PrayerNameKey.dhuhr: 12 * 60 + 40, // 12:40
  PrayerNameKey.asr: 15 * 60 + 55, // 15:55
  PrayerNameKey.maghrib: 18 * 60 + 22, // 18:22
  PrayerNameKey.isha: 19 * 60 + 39, // 19:39
});

DateTime _at(DateTime base, int minutes) => base.add(Duration(minutes: minutes));

void main() {
  group('PrayerDay — الحالي والجاي', () {
    test('قبل الفجر: لا صلاة حالية، والجاي هو الفجر', () {
      final now = _at(DateTime.utc(2026, 1, 15), 3 * 60);
      expect(_typical.currentAt(now), isNull);
      expect(_typical.nextKeyAfter(now), PrayerNameKey.fajr);
    });

    test('بين الفجر والشروق: الحالي فجر، والجاي الظهر', () {
      final now = _at(DateTime.utc(2026, 1, 15), 6 * 60);
      expect(_typical.currentAt(now), PrayerNameKey.fajr);
      // الشروق مش صلاة — مش بيتحسب في «الجاي»
      expect(_typical.nextKeyAfter(now), PrayerNameKey.dhuhr);
    });

    test('بعد العصر: الحالي عصر، والجاي المغرب', () {
      final now = _at(DateTime.utc(2026, 1, 15), 17 * 60);
      expect(_typical.currentAt(now), PrayerNameKey.asr);
      expect(_typical.nextKeyAfter(now), PrayerNameKey.maghrib);
    });

    test('بعد العشاء: لا صلاة جاية اليوم → فجر الغد', () {
      final now = _at(DateTime.utc(2026, 1, 15), 22 * 60);
      expect(_typical.currentAt(now), PrayerNameKey.isha);
      expect(_typical.nextKeyAfter(now), isNull);

      // يوم الغد (١٦ يناير) — يكفيه الفجر لاختبار «فجر الغد».
      final tomorrow = PrayerDay(
        date: DateTime.utc(2026, 1, 16),
        place: Place.fallback,
        times: {
          PrayerNameKey.fajr: DateTime.utc(2026, 1, 16, 5, 33),
          PrayerNameKey.dhuhr: DateTime.utc(2026, 1, 16, 12, 41),
        },
      );

      final next = _typical.nextPrayer(now, tomorrow: tomorrow);
      expect(next, isNotNull);
      expect(next!.key, PrayerNameKey.fajr);
      expect(next.isTomorrow, isTrue);
      expect(next.at, DateTime.utc(2026, 1, 16, 5, 33));
    });

    test('المتبقي موجب قبل الصلاة وصفر بعدها', () {
      final before = _at(DateTime.utc(2026, 1, 15), 12 * 60);
      final next = _typical.nextPrayer(before);
      expect(next!.key, PrayerNameKey.dhuhr);
      expect(next.remaining, const Duration(minutes: 40));
      expect(next.isPast, isFalse);
    });

    test('nextPrayer بدون يوم غد يرجّع null بعد العشاء', () {
      final now = _at(DateTime.utc(2026, 1, 15), 23 * 60);
      expect(_typical.nextPrayer(now), isNull);
    });
  });

  group('PrayerDay — منتصف الليل الإسلامي', () {
    test('يبقى في منتصف الفترة بين المغرب وفجر الغد', () {
      final maghrib = DateTime.utc(2026, 1, 15, 18, 22);
      final fajrTomorrow = DateTime.utc(2026, 1, 16, 5, 32);
      final day = PrayerDay(
        date: DateTime.utc(2026, 1, 15),
        place: Place.fallback,
        times: {
          PrayerNameKey.maghrib: maghrib,
          PrayerNameKey.fajr: DateTime.utc(2026, 1, 15, 5, 32),
        },
      );

      final full = fajrTomorrow.difference(maghrib);
      final expected = maghrib.add(Duration(minutes: full.inMinutes ~/ 2));

      final actual = day.islamicMidnight;
      expect(actual, isNotNull);
      expect(actual!.difference(expected).inMinutes.abs(), lessThanOrEqualTo(1));
      // منتصف الليل لازم يكون بعد المغرب
      expect(actual.isAfter(maghrib), isTrue);
    });

    test('بدون مغرب مفيش منتصف ليل', () {
      final day = PrayerDay(
        date: DateTime.utc(2026, 1, 15),
        place: Place.fallback,
        times: {PrayerNameKey.fajr: DateTime.utc(2026, 1, 15, 5, 32)},
      );
      expect(day.islamicMidnight, isNull);
    });
  });

  group('PrayerDay — اكتمال', () {
    test('isComplete يحتاج الأوقات الستة', () {
      expect(_typical.isComplete, isTrue);
      final partial = PrayerDay(
        date: DateTime.utc(2026, 1, 15),
        place: Place.fallback,
        times: {PrayerNameKey.fajr: DateTime.utc(2026, 1, 15, 5, 32)},
      );
      expect(partial.isComplete, isFalse);
      expect(partial.timeFor(PrayerNameKey.dhuhr), isNull);
    });
  });
}
