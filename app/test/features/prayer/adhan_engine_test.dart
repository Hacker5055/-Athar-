import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/utils/greeting.dart';
import 'package:athar/features/prayer/data/adhan_dart_engine.dart';
import 'package:athar/features/prayer/data/cities.dart';
import 'package:athar/features/prayer/domain/entities/place.dart';
import 'package:athar/features/prayer/domain/entities/prayer_settings.dart';
import 'package:athar/features/prayer/domain/prayer_methods.dart';

/// ══════════════════════════════════════════════════════════════════
/// القيم المرجعية — **ولّدت من كود البروتوتايب** (`prototype/assets/js/calc.js`)
/// بنفس الإحداثيات والطرق، بالتوقيت العالمي (UTC).
///
/// اتأكدنا أول إن calc.js يطلّع نفس القيم المتحقق منها سابقًا للقاهرة
/// ٢٠٢٦-١٠-١٧ بالطريقة المصرية/الشافعي: 05:32 06:58 12:40 15:55 18:22 19:39
/// (بتوقيت مصر +٣).
///
/// **التفاوت المسموح ٣ دقائق** مش دقة أقل — السبب أن calc.js يستخدم صيغة
/// PrayTimes المبسّطة بينما Adhan تستخدم صيغ Meeus الأدق، والفرق الطبيعي
/// بينهم ٠–٢ دقيقة. الهدف من الاختبار هنا catching **سوء استخدام الـ API**
/// (طريقة غلط، مذهب غلط، تحويل منطقة زمنية غلط) — ودي أخطاء بعشرات الدقائق.
/// ══════════════════════════════════════════════════════════════════
class _Fixture {
  const _Fixture(this.cityId, this.method, this.madhab, this.y, this.m, this.d,
      this.fajr, this.sunrise, this.dhuhr, this.asr, this.maghrib, this.isha);

  final String cityId;
  final String method;
  final MadhabId madhab;
  final int y, m, d;
  final String fajr, sunrise, dhuhr, asr, maghrib, isha;

  List<String> get all => [fajr, sunrise, dhuhr, asr, maghrib, isha];
}

const _toleranceMinutes = 3;

const _fixtures = [
  // القاهرة — الطريقة المصرية (شافعي ثم حنفي)
  _Fixture('cairo', 'egyptian', MadhabId.shafi, 2026, 10, 18,
      '02:32', '03:59', '09:40', '12:54', '15:21', '16:38'),
  _Fixture('cairo', 'egyptian', MadhabId.hanafi, 2026, 10, 18,
      '02:32', '03:59', '09:40', '13:43', '15:21', '16:38'),
  // القاهرة — رابطة العالم الإسلامي (يناير)
  _Fixture('cairo', 'mwl', MadhabId.shafi, 2026, 1, 15,
      '03:28', '04:52', '10:05', '12:58', '15:18', '16:37'),
  // القاهرة — أم القرى (يونيو، عشاء = مغرب + ٩٠)
  _Fixture('cairo', 'umm_alqura', MadhabId.shafi, 2026, 6, 21,
      '01:15', '02:54', '09:57', '13:32', '16:59', '18:29'),
  // مكة — أم القرى
  _Fixture('makkah', 'umm_alqura', MadhabId.shafi, 2026, 10, 18,
      '02:01', '03:18', '09:06', '12:25', '14:54', '16:24'),
  // باريس — فرنسا ١٥°
  _Fixture('paris', 'france15', MadhabId.shafi, 2026, 10, 18,
      '04:49', '06:16', '11:36', '14:23', '16:54', '18:21'),
  // لندن — رابطة العالم الإسلامي (يناير)
  _Fixture('london', 'mwl', MadhabId.shafi, 2026, 1, 15,
      '05:59', '07:59', '12:10', '14:02', '16:22', '18:15'),
  // لندن — ISNA
  _Fixture('london', 'isna', MadhabId.shafi, 2026, 10, 18,
      '04:58', '06:30', '11:46', '14:27', '17:00', '18:32'),
  // إسطنبول — تركيا
  _Fixture('istanbul', 'turkey', MadhabId.shafi, 2026, 10, 18,
      '02:48', '04:19', '09:49', '12:51', '15:18', '16:45'),
  // نيويورك — ISNA (يونيو: المغرب والعشاء بعد منتصف الليل UTC)
  _Fixture('newyork', 'isna', MadhabId.shafi, 2026, 6, 21,
      '07:45', '09:25', '16:58', '20:58', '00:31', '02:11'),
  // الدار البيضاء — رابطة العالم الإسلامي
  _Fixture('casablanca', 'mwl', MadhabId.shafi, 2026, 10, 18,
      '05:15', '06:38', '12:15', '15:26', '17:53', '19:11'),
  // جاكرتا — الفجر قبل منتصف الليل UTC (يوم السابق)
  _Fixture('jakarta', 'mwl', MadhabId.shafi, 2026, 10, 18,
      '21:20', '22:30', '04:38', '07:46', '10:45', '11:52'),
];

int _minutesOfDayUtc(DateTime t) {
  final utc = t.toUtc();
  return utc.hour * 60 + utc.minute + (utc.second >= 30 ? 1 : 0);
}

/// يقارن وقتًا فعليًا بـ "HH:MM" مع التفاف يوم كامل (±١٢ ساعة)
/// عشان الحالات اللي الفجر فيها بيكون على اليوم السابق بالـ UTC.
void _expectNear(DateTime actual, String expectedHm, String label) {
  final parts = expectedHm.split(':');
  final expected = int.parse(parts[0]) * 60 + int.parse(parts[1]);
  final a = _minutesOfDayUtc(actual);
  final delta = ((a - expected + 720) % 1440) - 720;
  expect(
    delta.abs(),
    lessThanOrEqualTo(_toleranceMinutes),
    reason:
        '$label — المتوقع $expectedHm UTC، الفعلي ${actual.toUtc().toIso8601String()} '
        '(فرق $delta دقيقة، المسموح $_toleranceMinutes)',
  );
}

Place _placeOf(String id) {
  final place = Cities.byId(id);
  assert(place != null, 'مدينة مش موجودة في القائمة: $id');
  return place!;
}

void main() {
  const engine = AdhanDartEngine();

  group('AdhanDartEngine — قيم مرجعية', () {
    for (final fixture in _fixtures) {
      test('${fixture.cityId} · ${fixture.method} · ${fixture.madhab.name} · '
          '${fixture.y}-${fixture.m}-${fixture.d}', () {
        final settings = PrayerSettings(
          methodId: fixture.method,
          madhab: fixture.madhab,
        );
        final day = engine.calculate(
          place: _placeOf(fixture.cityId),
          date: DateTime.utc(fixture.y, fixture.m, fixture.d),
          settings: settings,
        );

        expect(day.isComplete, isTrue);

        final keys = PrayerNameKey.values;
        for (var i = 0; i < keys.length; i++) {
          final actual = day.timeFor(keys[i]);
          expect(actual, isNotNull, reason: '${keys[i].name} = null');
          _expectNear(actual!, fixture.all[i], keys[i].name);
        }
      });
    }
  });

  group('AdhanDartEngine — ثوابت لا تكسرها أي طريقة', () {
    final place = _placeOf('cairo');
    final date = DateTime.utc(2026, 10, 18);
    const settings = PrayerSettings();

    final day = engine.calculate(place: place, date: date, settings: settings);

    test('الترتيب: فجر < شروق < ظهر < عصر < مغرب < عشاء', () {
      final order = [
        PrayerNameKey.fajr,
        PrayerNameKey.sunrise,
        PrayerNameKey.dhuhr,
        PrayerNameKey.asr,
        PrayerNameKey.maghrib,
        PrayerNameKey.isha,
      ];
      for (var i = 1; i < order.length; i++) {
        final prev = day.timeFor(order[i - 1])!;
        final cur = day.timeFor(order[i])!;
        expect(cur.isAfter(prev), isTrue,
            reason: '${order[i].name} لازم يكون بعد ${order[i - 1].name}');
      }
    });

    test('كل الأوقات راجعة بصيغة UTC', () {
      for (final entry in day.times.entries) {
        expect(entry.value.isUtc, isTrue, reason: entry.key.name);
      }
    });

    test('الظهر في نص الطريق بين الشروق والمغرب (±٣ دقائق)', () {
      final sunrise = day.timeFor(PrayerNameKey.sunrise)!;
      final maghrib = day.timeFor(PrayerNameKey.maghrib)!;
      final dhuhr = day.timeFor(PrayerNameKey.dhuhr)!;
      final mid = sunrise.add(maghrib.difference(sunrise) ~/ 2);
      expect(
        dhuhr.difference(mid).inMinutes.abs(),
        lessThanOrEqualTo(_toleranceMinutes),
      );
    });

    test('العصر الحنفي بعد العصر الشافعي', () {
      final shafi = engine.calculate(
        place: place,
        date: date,
        settings: settings.copyWith(madhab: MadhabId.shafi),
      );
      final hanafi = engine.calculate(
        place: place,
        date: date,
        settings: settings.copyWith(madhab: MadhabId.hanafi),
      );
      expect(
        hanafi.timeFor(PrayerNameKey.asr)!.isAfter(
          shafi.timeFor(PrayerNameKey.asr)!,
        ),
        isTrue,
      );
      // الفجر والعشاء لا يتأثران بالمذهب
      expect(hanafi.timeFor(PrayerNameKey.fajr),
          shafi.timeFor(PrayerNameKey.fajr));
      expect(hanafi.timeFor(PrayerNameKey.isha),
          shafi.timeFor(PrayerNameKey.isha));
    });

    test('زاوية فجر أكبر = فجر أبكر', () {
      final a12 = engine.calculate(
        place: place,
        date: date,
        settings: settings.copyWith(methodId: 'france12'),
      );
      final a18 = engine.calculate(
        place: place,
        date: date,
        settings: settings.copyWith(methodId: 'france18'),
      );
      expect(
        a18.timeFor(PrayerNameKey.fajr)!.isBefore(
          a12.timeFor(PrayerNameKey.fajr)!,
        ),
        isTrue,
      );
    });

    test('أم القرى: العشاء = المغرب + ٩٠ دقيقة بالظبط', () {
      final umm = engine.calculate(
        place: place,
        date: date,
        settings: settings.copyWith(methodId: 'umm_alqura'),
      );
      final diff = umm
          .timeFor(PrayerNameKey.isha)!
          .difference(umm.timeFor(PrayerNameKey.maghrib)!);
      expect(diff.inMinutes, 90);
    });

    test('التعديل اليدوي يزحّف الوقت بالدقائق بالظبط', () {
      final adjusted = engine.calculate(
        place: place,
        date: date,
        settings: settings.copyWithAdjustment(PrayerNameKey.fajr, 15),
      );
      final diff = adjusted
          .timeFor(PrayerNameKey.fajr)!
          .difference(day.timeFor(PrayerNameKey.fajr)!);
      expect(diff.inMinutes, 15);
      // باقي الصلوات تفضل زي ما هي
      expect(adjusted.timeFor(PrayerNameKey.dhuhr),
          day.timeFor(PrayerNameKey.dhuhr));
    });
  });

  test('بيانات المحرك تظهر في شاشة المصادر', () {
    expect(engine.engineName, isNotEmpty);
    expect(engine.engineSource, contains('http'));
  });
}
