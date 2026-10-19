import 'package:flutter_timezone/flutter_timezone.dart';

import '../domain/entities/place.dart';
import 'cities.dart';

/// معرّف المنطقة الزمنية للجهاز (IANA، مثل `Africa/Cairo`).
///
/// معزول في ملف صغير لأن الحزمة بتعمل استدعاء منصّة — وسهل نبدّلها أو نستبدلها
/// في الاختبارات. لو فشلت (منصة مش مدعومة زي الويب) بنقدّر التوقيت
/// من أقرب مدينة معروفة.
abstract final class DeviceTimezone {
  static Future<String> resolveOr({required GeoPoint fallbackTo}) async {
    try {
      // typed as Object? عشان يشتغل سواء رجّعت الحزمة String أو String?
      final Object? name = await FlutterTimezone.getLocalTimezone();
      if (name is String && name.contains('/')) return name;
    } catch (_) {
      // المنصة مش مدعومة أو الحزمة فشلت — نكمل بالتقدير.
    }
    final nearest = Cities.nearestTo(fallbackTo);
    return nearest?.timeZoneId ?? Place.fallback.timeZoneId;
  }
}
