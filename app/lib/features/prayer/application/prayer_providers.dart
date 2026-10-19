import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/utils/greeting.dart' show PrayerNameKey;
import '../data/adhan_dart_engine.dart';
import '../data/cities.dart';
import '../data/timezone_service.dart';
import '../domain/entities/place.dart';
import '../domain/entities/prayer_day.dart';
import '../domain/entities/prayer_settings.dart';
import '../domain/prayer_times_engine.dart';
import 'prayer_settings_controller.dart';

/// المحرك الفلكي — نقطة التبديل الوحيدة لو غيّرنا مكتبة الحساب.
final prayerEngineProvider =
    Provider<PrayerTimesEngine>((ref) => const AdhanDartEngine());

/// المكان الفعّال للحساب.
///
/// - وضع تلقائي: آخر إحداثيات معروفة من الـ GPS.
/// - وضع يدوي: المدينة المختارة.
/// - لو الاتنين فاضيين: القاهرة كافتراضي آمن.
final activePlaceProvider = Provider<Place>((ref) {
  final settings = ref.watch(prayerSettingsProvider);
  if (settings.locationMode == LocationMode.auto) {
    final last = settings.lastKnown;
    if (last != null) return last;
  }
  return Cities.byId(settings.manualCityId) ?? Place.fallback;
});

/// التاريخ التقويمي **عند المكان** — مش تاريخ الجهاز.
/// (١١ مساءً في باريس ≠ نفس اليوم في مكة)
final civilDateProvider = Provider<DateTime>((ref) {
  final place = ref.watch(activePlaceProvider);
  return TimezoneService.civilDateAt(place, DateTime.now());
});

/// مواقيت يوم بعينه — محسوبة مرة واحدة ومخزّنة (الحساب فوري، مش async).
final prayerDayProvider =
    Provider.family<PrayerDay?, DateTime>((ref, civilDate) {
  final engine = ref.watch(prayerEngineProvider);
  final place = ref.watch(activePlaceProvider);
  final settings = ref.watch(prayerSettingsProvider);
  try {
    return engine.calculate(
      place: place,
      date: civilDate,
      settings: settings,
    );
  } catch (error) {
    // أي خطأ في الحساب مش لازم يوقّع الشاشة — الواجهة تعرض حالة فارغة.
    return null;
  }
});

/// مواقيت اليوم (حسب تاريخ المكان).
final todayPrayerProvider = Provider<PrayerDay?>((ref) {
  return ref.watch(prayerDayProvider(ref.watch(civilDateProvider)));
});

/// مواقيت الغد — محتاجينها عشان «فجر الغد» بعد العشاء.
final tomorrowPrayerProvider = Provider<PrayerDay?>((ref) {
  final today = ref.watch(civilDateProvider);
  return ref.watch(prayerDayProvider(today.add(const Duration(days: 1))));
});

/// نبضة كل ثانية للعدّ التنازلي.
///
/// `autoDispose` عشان الستريم يقفل لما مفيش شاشة بتسمعه.
final clockTickProvider =
    StreamProvider.autoDispose<DateTime>((ref) async* {
  yield DateTime.now();
  yield* Stream.periodic(
    const Duration(seconds: 1),
    (_) => DateTime.now(),
  );
});

/// الصلاة الجاية + المتبقي لها — يتحدّث كل ثانية.
final nextPrayerProvider = Provider<NextPrayer?>((ref) {
  ref.watch(clockTickProvider);
  final today = ref.watch(todayPrayerProvider);
  if (today == null) return null;
  final tomorrow = ref.watch(tomorrowPrayerProvider);
  return today.nextPrayer(DateTime.now(), tomorrow: tomorrow);
});

/// الصلاة الحالية (أو `null` قبل الفجر).
final currentPrayerProvider = Provider<PrayerNameKey?>((ref) {
  ref.watch(clockTickProvider);
  final today = ref.watch(todayPrayerProvider);
  if (today == null) return null;
  return today.currentAt(DateTime.now());
});

/// يحوّل لحظة UTC إلى التوقيت المحلي للمكان — للعرض فقط.
DateTime? localTimeFor(PrayerDay day, PrayerNameKey key) {
  final instant = day.timeFor(key);
  if (instant == null) return null;
  return TimezoneService.toLocal(day.place, instant);
}
