import 'dart:math' as math;

import '../../../../core/l10n/app_locale.dart';

/// نقطة جغرافية خام (بدون اسم) — نتيجة الـ GPS.
class GeoPoint {
  const GeoPoint({required this.latitude, required this.longitude});

  final double latitude;
  final double longitude;

  /// نصف قطر الأرض بالكيلومتر (متوسط).
  static const double earthRadiusKm = 6371.0088;

  /// المسافة بالكيلومتر بين نقطتين — قانون هافرساين (Haversine).
  double distanceKmTo(GeoPoint other) {
    const deg2rad = math.pi / 180;
    final lat1 = latitude * deg2rad;
    final lat2 = other.latitude * deg2rad;
    final dLat = (other.latitude - latitude) * deg2rad;
    final dLng = (other.longitude - longitude) * deg2rad;

    final h = _sq(math.sin(dLat / 2)) +
        math.cos(lat1) * math.cos(lat2) * _sq(math.sin(dLng / 2));

    final c = 2 * math.asin(math.sqrt(h.clamp(0.0, 1.0)));
    return earthRadiusKm * c;
  }

  /// الاتجاه (bearing) من هذه النقطة إلى [other] بالدرجات من الشمال الحقيقي.
  ///
  /// ⚠️ هذه **الشمال الحقيقي** — لا تُقارن بقراءة البوصلة (المغناطيسية)
  /// قبل إضافة زاوية الميل المغناطيسي (declination). راجع `docs/05`.
  double bearingToDeg(GeoPoint other) {
    const deg2rad = math.pi / 180;
    final lat1 = latitude * deg2rad;
    final lat2 = other.latitude * deg2rad;
    final dLng = (other.longitude - longitude) * deg2rad;

    final y = math.sin(dLng) * math.cos(lat2);
    final x = math.cos(lat1) * math.sin(lat2) -
        math.sin(lat1) * math.cos(lat2) * math.cos(dLng);

    return (math.atan2(y, x) / deg2rad + 360) % 360;
  }

  static double _sq(double v) => v * v;
}

/// مكان محفوظ: مدينة من القائمة، أو الموقع الحالي من الـ GPS.
///
/// [timeZoneId] معرّف IANA (مثل `Africa/Cairo`) — ضروري لأن
/// `adhan_dart` يرجّع الأوقات بـ UTC، ولازم نحوّلها للتوقيت المحلي صح
/// (خصوصًا مع التوقيت الصيفي DST).
class Place {
  const Place({
    required this.id,
    required this.latitude,
    required this.longitude,
    required this.timeZoneId,
    required this.nameAr,
    required this.nameEn,
    required this.nameFr,
    this.countryCode = '',
    this.isAuto = false,
  });

  final String id;
  final double latitude;
  final double longitude;
  final String timeZoneId;

  final String nameAr;
  final String nameEn;
  final String nameFr;
  final String countryCode;

  /// true لو المكان جاي من الـ GPS (وقتها الاسم = «موقعي الحالي»).
  final bool isAuto;

  GeoPoint get point => GeoPoint(latitude: latitude, longitude: longitude);

  String nameFor(AppLocale locale) => isAuto
      ? switch (locale) {
          AppLocale.ar => 'موقعي الحالي',
          AppLocale.en => 'My current location',
          AppLocale.fr => 'Ma position actuelle',
        }
      : switch (locale) {
          AppLocale.ar => nameAr,
          AppLocale.en => nameEn,
          AppLocale.fr => nameFr,
        };

  /// المكان الافتراضي قبل ما المستخدم يحدّد موقعه.
  static const fallback = Place(
    id: 'cairo',
    latitude: 30.0444,
    longitude: 31.2357,
    timeZoneId: 'Africa/Cairo',
    nameAr: 'القاهرة',
    nameEn: 'Cairo',
    nameFr: 'Le Caire',
    countryCode: 'EG',
  );

  /// مكان من إحداثيات الـ GPS.
  factory Place.auto({
    required double latitude,
    required double longitude,
    required String timeZoneId,
  }) =>
      Place(
        id: 'auto',
        latitude: latitude,
        longitude: longitude,
        timeZoneId: timeZoneId,
        nameAr: 'موقعي الحالي',
        nameEn: 'My current location',
        nameFr: 'Ma position actuelle',
        isAuto: true,
      );

  Map<String, dynamic> toJson() => {
        'id': id,
        'lat': latitude,
        'lng': longitude,
        'tz': timeZoneId,
        'ar': nameAr,
        'en': nameEn,
        'fr': nameFr,
        'cc': countryCode,
        'auto': isAuto,
      };

  /// يُرجع `null` لو البيانات ناقصة — فضّل السقوط على الافتراضي بدل الكراش.
  static Place? fromJson(Object? raw) {
    if (raw is! Map) return null;
    final lat = _num(raw['lat']);
    final lng = _num(raw['lng']);
    final tz = raw['tz'];
    if (lat == null || lng == null) return null;
    if (tz is! String || tz.isEmpty) return null;
    final id = raw['id'];
    return Place(
      id: (id is String && id.isNotEmpty) ? id : 'auto',
      latitude: lat,
      longitude: lng,
      timeZoneId: tz,
      nameAr: (raw['ar'] is String) ? raw['ar'] as String : 'موقعي الحالي',
      nameEn: (raw['en'] is String) ? raw['en'] as String : 'My current location',
      nameFr: (raw['fr'] is String) ? raw['fr'] as String : 'Ma position actuelle',
      countryCode: (raw['cc'] is String) ? raw['cc'] as String : '',
      isAuto: raw['auto'] == true,
    );
  }

  static double? _num(Object? v) => (v is num) ? v.toDouble() : null;
}
