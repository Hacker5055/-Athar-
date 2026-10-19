import 'package:timezone/timezone.dart' as tz;

import '../domain/entities/place.dart';

/// خدمة التوقيت — كل التحويلات من/إلى التوقيت المحلي بتمر من هنا.
///
/// السبب: `adhan_dart` يرجّع الأوقات كـ **UTC instants**، والتطبيق لازم يعرضها
/// بالتوقيت المحلي للمكان المختار (مش توقيت الجهاز) — خصوصًا مع التوقيت الصيفي.
abstract final class TimezoneService {
  static bool _initialized = false;

  /// لازم تتنده مرة واحدة في `bootstrap()` قبل أي استخدام.
  static void ensureInitialized() {
    if (_initialized) return;
    tz.initializeTimeZones();
    _initialized = true;
  }

  /// منطقة زمنية للمكان — تسقط على UTC لو المعرّف غلط (بدل الكراش).
  static tz.Location locationFor(Place place) {
    ensureInitialized();
    try {
      return tz.getLocation(place.timeZoneId);
    } catch (_) {
      return tz.UTC;
    }
  }

  /// يحوّل لحظة UTC إلى التوقيت المحلي للمكان (للعرض فقط).
  static tz.TZDateTime toLocal(Place place, DateTime instant) =>
      tz.TZDateTime.from(instant, locationFor(place));

  /// التاريخ التقويمي (y-m-d) عند المكان لهذه اللحظة.
  ///
  /// ⚠️ مهم: الحساب الفلكي بياخد «تاريخ عند المكان»، وممكن يختلف عن تاريخ الجهاز
  /// (مثلًا لما تكون الساعة ١١ مساءً في باريس واليوم اتغيّر في مكة).
  static DateTime civilDateAt(Place place, DateTime now) {
    final local = toLocal(place, now);
    return DateTime.utc(local.year, local.month, local.day);
  }
}
