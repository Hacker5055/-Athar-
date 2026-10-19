import '../../../../core/utils/greeting.dart' show PrayerNameKey, prayerOrder;
import 'place.dart';

/// نتيجة العدّ التنازلي: الصلاة الجاية + وقتها + الوقت المتبقي.
class NextPrayer {
  const NextPrayer({
    required this.key,
    required this.at,
    required this.remaining,
    required this.isTomorrow,
  });

  final PrayerNameKey key;

  /// لحظة الصلاة (UTC instant).
  final DateTime at;

  /// المتبقي حتى الصلاة — لا يتجاوز الصفر.
  final Duration remaining;

  /// true لو الصلاة الجاية هي فجر اليوم التالي.
  final bool isTomorrow;

  bool get isPast => remaining <= Duration.zero;
}

/// يوم صلاة كامل لمكان معيّن.
///
/// **كل الأوقات محفوظة كـ UTC instants** — يعني `DateTime` بـ `isUtc == true`.
/// السبب: المقارنة مع `DateTime.now()` تبقى صحيحة، والتحويل لعرضها
/// بيحصل عند العرض فقط عبر حزمة `timezone` (عشان التوقيت الصيفي).
class PrayerDay {
  const PrayerDay({
    required this.date,
    required this.place,
    required this.times,
  });

  /// تاريخ اليوم التقويمي عند المكان (بصيغة UTC: y-m-d والباقي أصفار).
  final DateTime date;
  final Place place;

  /// الأوقات الستة (بما فيها الشروق) كـ UTC instants.
  final Map<PrayerNameKey, DateTime> times;

  bool get isComplete => times.length == PrayerNameKey.values.length;

  DateTime? timeFor(PrayerNameKey key) => times[key];

  /// الصلاة الحالية (آخر صلاة دخل وقتها) — `null` قبل الفجر.
  PrayerNameKey? currentAt(DateTime now) {
    PrayerNameKey? current;
    for (final key in prayerOrder) {
      final at = times[key];
      if (at == null) continue;
      if (!at.isAfter(now)) current = key;
    }
    return current;
  }

  /// أول صلاة لم يدخل وقتها بعد — `null` لو انتهى اليوم (يبقى فجر الغد).
  PrayerNameKey? nextKeyAfter(DateTime now) {
    for (final key in prayerOrder) {
      final at = times[key];
      if (at != null && at.isAfter(now)) return key;
    }
    return null;
  }

  /// الصلاة الجاية مع المتبقي لها — تستخدم يوم الغد لفجر الغد.
  NextPrayer? nextPrayer(DateTime now, {PrayerDay? tomorrow}) {
    final key = nextKeyAfter(now);
    if (key != null) {
      final at = times[key]!;
      return NextPrayer(
        key: key,
        at: at,
        remaining: at.difference(now),
        isTomorrow: false,
      );
    }

    // انتهت صلوات اليوم → فجر الغد.
    final fajrTomorrow = tomorrow?.timeFor(PrayerNameKey.fajr);
    if (fajrTomorrow == null) return null;

    return NextPrayer(
      key: PrayerNameKey.fajr,
      at: fajrTomorrow,
      remaining: fajrTomorrow.difference(now),
      isTomorrow: true,
    );
  }

  /// «منتصف الليل الإسلامي» = منتصف الفترة بين المغرب والفجر.
  /// مفيد لأذكار آخر الليل ولقواعد خطوط العرض العالية.
  DateTime? get islamicMidnight {
    final maghrib = times[PrayerNameKey.maghrib];
    final tomorrowFajr = times[PrayerNameKey.fajr];
    final tomorrowSunrise = times[PrayerNameKey.sunrise];
    if (maghrib == null) return null;

    // نستخدم الشروق كتقدير لفجر الغد لو مش متاح
    final end = tomorrowFajr ?? tomorrowSunrise;
    if (end == null) return null;

    var nextFajr = end;
    if (!nextFajr.isAfter(maghrib)) {
      nextFajr = nextFajr.add(const Duration(days: 1));
    }
    return maghrib.add(nextFajr.difference(maghrib) ~/ 2);
  }
}
