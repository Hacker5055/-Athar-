import 'package:geolocator/geolocator.dart' as geo;

import '../domain/entities/place.dart' show GeoPoint;
import '../domain/location_repository.dart';

/// تنفيذ واجهة الموقع باستخدام حزمة `geolocator`.
///
/// **قاعدة مهمة من `docs/03` §3.1:** التطبيق لا يطلب إذن الموقع في أول تشغيل.
/// الطلب يجي فقط لما المستخدم يختار «تحديد موقعي تلقائيًا» — عندها الطلب
/// له سياق واضح ومقبول.
class GeolocatorLocationRepository implements LocationRepository {
  const GeolocatorLocationRepository();

  @override
  Future<bool> isServiceEnabled() async {
    try {
      return await geo.Geolocator.isLocationServiceEnabled();
    } catch (_) {
      return false;
    }
  }

  @override
  Future<LocationAccessStatus> checkPermission() async {
    try {
      return _map(await geo.Geolocator.checkPermission());
    } catch (_) {
      return LocationAccessStatus.unsupported;
    }
  }

  @override
  Future<LocationAccessStatus> requestPermission() async {
    try {
      return _map(await geo.Geolocator.requestPermission());
    } catch (_) {
      return LocationAccessStatus.unsupported;
    }
  }

  @override
  Future<GeoPoint?> getLastKnown() async {
    try {
      final position = await geo.Geolocator.getLastKnownPosition();
      if (position == null) return null;
      return GeoPoint(
        latitude: position.latitude,
        longitude: position.longitude,
      );
    } catch (_) {
      return null;
    }
  }

  @override
  Future<GeoPoint?> getCurrent() async {
    try {
      final position = await geo.Geolocator.getCurrentPosition(
        locationSettings: const geo.LocationSettings(
          accuracy: geo.LocationAccuracy.medium,
          timeLimit: Duration(seconds: 15),
        ),
      );
      return GeoPoint(
        latitude: position.latitude,
        longitude: position.longitude,
      );
    } catch (_) {
      // مهلة، أو صلاحية مرفوضة، أو GPS مقفول — نرجّع null والواجهة تتعامل.
      return null;
    }
  }

  @override
  Future<bool> openSystemSettings() async {
    try {
      return await geo.Geolocator.openAppSettings();
    } catch (_) {
      return false;
    }
  }

  @override
  Future<bool> openServiceSettings() async {
    try {
      return await geo.Geolocator.openLocationSettings();
    } catch (_) {
      return false;
    }
  }

  LocationAccessStatus _map(geo.LocationPermission permission) {
    if (permission == geo.LocationPermission.always ||
        permission == geo.LocationPermission.whileInUse) {
      return LocationAccessStatus.granted;
    }
    if (permission == geo.LocationPermission.deniedForever) {
      return LocationAccessStatus.deniedForever;
    }
    if (permission == geo.LocationPermission.denied) {
      return LocationAccessStatus.denied;
    }
    return LocationAccessStatus.unsupported;
  }
}
