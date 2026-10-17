import 'entities/place.dart' show GeoPoint;

/// حالة الوصول للموقع — مستقلة عن أنواع حزمة `geolocator`
/// عشان الواجهة (domain) ما تعتمدش على حزمة خارجية.
enum LocationAccessStatus {
  /// الإذن ممنوح والخدمة شغّالة.
  granted,

  /// المستخدم رفض الإذن (لسه ممكن نطلب تاني).
  denied,

  /// رفض دائم — لازم يتوجّه لإعدادات النظام بنفسه.
  deniedForever,

  /// خدمة الموقع مقفولة من النظام.
  serviceDisabled,

  /// الصلاحية غير مدعومة على الجهاز/المنصة.
  unsupported;

  bool get canUse => this == LocationAccessStatus.granted;
  bool get needsSettings =>
      this == LocationAccessStatus.deniedForever ||
      this == LocationAccessStatus.serviceDisabled;
}

/// مصدر الموقع — التطبيق كله بيكلم الواجهة دي مش `geolocator` مباشرةً.
abstract interface class LocationRepository {
  Future<bool> isServiceEnabled();

  Future<LocationAccessStatus> checkPermission();

  /// يطلب الإذن — **لا نطلبه أبدًا في أول تشغيل** (راجع `docs/03` §3.1).
  Future<LocationAccessStatus> requestPermission();

  /// آخر موقع معروف (مخزّن في النظام) — يعمل أوفلاين ومش بيشغّل GPS.
  Future<GeoPoint?> getLastKnown();

  /// الموقع الحالي — قد يستغرق ثوانٍ وقد يفشل.
  Future<GeoPoint?> getCurrent();

  /// يفتح إعدادات النظام (لحالة الرفض الدائم).
  Future<bool> openSystemSettings();

  /// يفتح إعدادات خدمة الموقع.
  Future<bool> openServiceSettings();
}
