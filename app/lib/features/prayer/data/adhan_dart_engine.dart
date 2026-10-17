import 'package:adhan_dart/adhan_dart.dart';

import '../../../core/utils/greeting.dart' show PrayerNameKey;
import '../domain/entities/prayer_day.dart';
import '../domain/entities/prayer_settings.dart';
import '../domain/entities/place.dart';
import '../domain/prayer_methods.dart';
import '../domain/prayer_times_engine.dart';

/// المحرك الفلكي — **الملف الوحيد في المشروع اللي بيستورد `adhan_dart`**.
///
/// ```dart
/// // لو تغيّر API الحزمة، التعديل كله هنا وبس.
/// ```
///
/// ### ⚠️ نقاط التحقّق عند أول كومبايل (مش قادر أجمّع هنا — مفيش Flutter SDK):
/// 1. `CalculationMethodParameters.other()` — موجود في Adhan JS كـ `Other()`
///    (زوايا صفر بلا تعديلات خاصة بالطريقة). لو مش موجود بالإصدار ده،
///    استبدلها بـ `CalculationMethodParameters.muslimWorldLeague()`
///    … بس وقتها راجع اختبار `adhan_engine_test.dart` لأن MWL قد يحمل تعديلات.
/// 2. أسماء الخصائص: `fajrAngle · ishaAngle · ishaInterval · maghribAngle ·
///    madhab · highLatitudeRule` — موثّقة في جدول الـ README للحزمة.
/// 3. `Madhab.hanafi / Madhab.shafi` و `HighLatitudeRule.*` — camelCase.
/// 4. `prayerTimes.fajr` إلخ: في ٢.x غير قابلة للـ null. الكود تحت
///    متدرّج على الحالتين (بيقبل `DateTime?` ويرمي خطأ واضح لو بقت null).
/// 5. `currentPrayer/nextPrayer` **مش مستخدمين** عن قصد — نوع الراجع اتغيّر
///    بين الإصدارات (String ← Prayer). إحنا نحسب الحالي/الجاي إحنا في
///    `PrayerDay`، وده أوضح وأثبت.
class AdhanDartEngine implements PrayerTimesEngine {
  const AdhanDartEngine();

  @override
  String get engineName => 'Adhan Dart';

  @override
  String get engineSource => 'https://pub.dev/packages/adhan_dart';

  @override
  PrayerDay calculate({
    required Place place,
    required DateTime date,
    required PrayerSettings settings,
  }) {
    final method = PrayerMethods.byId(settings.methodId);

    // Other() = زوايا صفر → نبني كل حاجة صريحة (مطابق لجدول البروتوتايب).
    final params = CalculationMethodParameters.other()
      ..fajrAngle = method.fajrAngle
      ..ishaAngle = method.ishaAngle
      ..ishaInterval = method.ishaInterval
      ..madhab = settings.madhab == MadhabId.hanafi
          ? Madhab.hanafi
          : Madhab.shafi
      ..highLatitudeRule = _highLatitudeRule(settings.highLatitudeRule);

    final maghribAngle = method.maghribAngle;
    if (maghribAngle != null) {
      params.maghribAngle = maghribAngle;
    }

    final prayerTimes = PrayerTimes(
      coordinates: Coordinates(place.latitude, place.longitude),
      date: date,
      calculationParameters: params,
      precision: true,
    );

    final raw = <PrayerNameKey, DateTime?>{
      PrayerNameKey.fajr: prayerTimes.fajr,
      PrayerNameKey.sunrise: prayerTimes.sunrise,
      PrayerNameKey.dhuhr: prayerTimes.dhuhr,
      PrayerNameKey.asr: prayerTimes.asr,
      PrayerNameKey.maghrib: prayerTimes.maghrib,
      PrayerNameKey.isha: prayerTimes.isha,
    };

    // التعديل اليدوي بالدقائق (A4) نطبّقه إحنا، مش المكتبة — أوضح وأثبت.
    final times = <PrayerNameKey, DateTime>{};
    for (final entry in raw.entries) {
      times[entry.key] = _utc(entry.value, entry.key.name).add(
        Duration(minutes: settings.adjustmentFor(entry.key)),
      );
    }

    return PrayerDay(
      date: DateTime.utc(date.year, date.month, date.day),
      place: place,
      times: times,
    );
  }

  /// يقبل `DateTime?` عشان يشتغل سواء كانت الحقول nullable ولا لأ.
  DateTime _utc(DateTime? value, String name) {
    if (value == null) {
      throw StateError(
        'adhan_dart: $name = null — تحقّق من إصدار الحزمة (المتوقع ٢.x)',
      );
    }
    return value.isUtc ? value : value.toUtc();
  }

  HighLatitudeRule _highLatitudeRule(HighLatitudeRuleId id) => switch (id) {
        HighLatitudeRuleId.none => HighLatitudeRule.none,
        HighLatitudeRuleId.middleOfTheNight =>
          HighLatitudeRule.middleOfTheNight,
        HighLatitudeRuleId.seventhOfTheNight =>
          HighLatitudeRule.seventhOfTheNight,
        HighLatitudeRuleId.twilightAngle => HighLatitudeRule.twilightAngle,
      };
}
