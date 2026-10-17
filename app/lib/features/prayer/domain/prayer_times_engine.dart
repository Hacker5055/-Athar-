import 'entities/prayer_day.dart';
import 'entities/prayer_settings.dart';
import 'entities/place.dart';

/// واجهة محرك الحساب الفلكي.
///
/// السبب: نفصل التطبيق عن `adhan_dart` نفسه. لو تغيّر الـ API أو قرّرنا
/// نبدّل المكتبة، يتعدّل ملف واحد بس (`data/adhan_dart_engine.dart`).
abstract interface class PrayerTimesEngine {
  /// يحسب مواقيت يوم واحد.
  ///
  /// [date] تاريخ تقويمي (y-m-d) **عند المكان** لا عند الجهاز.
  /// النتيجة: أوقات كـ UTC instants.
  PrayerDay calculate({
    required Place place,
    required DateTime date,
    required PrayerSettings settings,
  });

  /// اسم المحرك ومصدره — يظهر في شاشة «المصادر والتراخيص».
  String get engineName;
  String get engineSource;
}
