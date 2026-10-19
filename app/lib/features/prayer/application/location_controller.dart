import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../data/device_timezone.dart';
import '../data/geolocator_location_repository.dart';
import '../domain/entities/place.dart';
import '../domain/location_repository.dart';
import 'prayer_settings_controller.dart';

/// حالة الموقع في الواجهة.
class LocationState {
  const LocationState({
    this.status = LocationAccessStatus.unsupported,
    this.isBusy = false,
    this.failure,
  });

  final LocationAccessStatus status;

  /// جارٍ تحديد الموقع (GPS بيشتغل).
  final bool isBusy;

  /// سبب الفشل — يُترجم في الواجهة.
  final LocationFailure? failure;

  LocationState copyWith({
    LocationAccessStatus? status,
    bool? isBusy,
    LocationFailure? failure,
    bool clearFailure = false,
  }) =>
      LocationState(
        status: status ?? this.status,
        isBusy: isBusy ?? this.isBusy,
        failure: clearFailure ? null : (failure ?? this.failure),
      );
}

/// أسباب فشل تحديد الموقع — مفاتيح ثابتة تترجمها الواجهة.
enum LocationFailure {
  serviceDisabled,
  permissionDenied,
  permissionDeniedForever,
  timeout,
  unknown,
}

final locationRepositoryProvider = Provider<LocationRepository>((ref) {
  return const GeolocatorLocationRepository();
});

/// يتحكم في إذن الموقع وتحديده.
///
/// **لا يُطلب الإذن تلقائيًا أبدًا** — بس لما المستخدم يضغط «تحديد موقعي».
/// راجع `docs/03` §3.1.
final locationControllerProvider =
    StateNotifierProvider<LocationController, LocationState>((ref) {
  return LocationController(ref);
});

class LocationController extends StateNotifier<LocationState> {
  LocationController(this._ref) : super(const LocationState());

  final Ref _ref;

  LocationRepository get _repo => _ref.read(locationRepositoryProvider);

  /// يتحقق من الحالة الحالية بدون ما يطلب إذن.
  Future<void> refreshStatus() async {
    if (!await _repo.isServiceEnabled()) {
      state = state.copyWith(
        status: LocationAccessStatus.serviceDisabled,
        isBusy: false,
      );
      return;
    }
    final status = await _repo.checkPermission();
    state = state.copyWith(status: status, isBusy: false);
  }

  /// يحدّد الموقع ويفعّل الوضع التلقائي.
  ///
  /// يرجّع `true` لو نجح.
  Future<bool> useMyLocation({bool requireFresh = true}) async {
    state = state.copyWith(isBusy: true, clearFailure: true);

    // ١) خدمة الموقع شغّالة؟
    if (!await _repo.isServiceEnabled()) {
      state = state.copyWith(
        status: LocationAccessStatus.serviceDisabled,
        isBusy: false,
        failure: LocationFailure.serviceDisabled,
      );
      return false;
    }

    // ٢) الإذن
    var status = await _repo.checkPermission();
    if (status == LocationAccessStatus.denied) {
      status = await _repo.requestPermission();
    }

    if (status == LocationAccessStatus.deniedForever) {
      state = state.copyWith(
        status: status,
        isBusy: false,
        failure: LocationFailure.permissionDeniedForever,
      );
      return false;
    }
    if (!status.canUse) {
      state = state.copyWith(
        status: status,
        isBusy: false,
        failure: status == LocationAccessStatus.denied
            ? LocationFailure.permissionDenied
            : LocationFailure.unknown,
      );
      return false;
    }

    // ٣) الإحداثيات — نجرّب آخر موقع معروف الأول (أسرع وأوفلاين)
    var point = requireFresh ? null : await _repo.getLastKnown();
    point ??= await _repo.getCurrent();
    point ??= await _repo.getLastKnown();

    if (point == null) {
      state = state.copyWith(
        status: status,
        isBusy: false,
        failure: LocationFailure.timeout,
      );
      return false;
    }

    // ٤) المنطقة الزمنية: نستخدم توقيت الجهاز (المستخدم غالبًا في نفس بلده)
    // … ولو مش متاح نستخدم أقرب مدينة معروفة كتقدير.
    final tzId = await DeviceTimezone.resolveOr(
      fallbackTo: point,
    );

    final place = Place.auto(
      latitude: point.latitude,
      longitude: point.longitude,
      timeZoneId: tzId,
    );

    await _ref.read(prayerSettingsProvider.notifier).setAutoLocation(place);
    state = state.copyWith(
      status: status,
      isBusy: false,
      clearFailure: true,
    );
    return true;
  }

  Future<void> openSettings() async {
    if (state.status == LocationAccessStatus.serviceDisabled) {
      await _repo.openServiceSettings();
    } else {
      await _repo.openSystemSettings();
    }
  }
}
