import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../../core/settings/settings_controller.dart' show sharedPreferencesProvider;
import '../../../core/utils/greeting.dart' show PrayerNameKey;
import '../domain/entities/place.dart';
import '../domain/entities/prayer_settings.dart';
import '../domain/prayer_methods.dart' show HighLatitudeRuleId, MadhabId;

const String prayerSettingsKey = 'athar.prayer.v1';

/// إعدادات الصلاة — محفوظة في `SharedPreferences` تحت `athar.prayer.v1`.
///
/// منفصلة عن `AppSettings` عن قصد: أكبر، وبتتغيّر بمعدّل مختلف،
/// ومينفعش نخلّي تلفها يبوّظ إعدادات التطبيق كلها.
final prayerSettingsProvider =
    StateNotifierProvider<PrayerSettingsController, PrayerSettings>((ref) {
  final prefs = ref.watch(sharedPreferencesProvider);
  return PrayerSettingsController(prefs);
});

class PrayerSettingsController extends StateNotifier<PrayerSettings> {
  PrayerSettingsController(this._prefs)
      : super(PrayerSettings.decode(_prefs.getString(prayerSettingsKey)));

  final SharedPreferences _prefs;

  Future<void> _save() => _prefs.setString(prayerSettingsKey, state.encode());

  Future<void> setMethod(String methodId) async {
    if (methodId == state.methodId) return;
    state = state.copyWith(methodId: methodId);
    await _save();
  }

  Future<void> setMadhab(MadhabId madhab) async {
    if (madhab == state.madhab) return;
    state = state.copyWith(madhab: madhab);
    await _save();
  }

  Future<void> setHighLatitudeRule(HighLatitudeRuleId rule) async {
    if (rule == state.highLatitudeRule) return;
    state = state.copyWith(highLatitudeRule: rule);
    await _save();
  }

  Future<void> setAdjustment(PrayerNameKey key, int minutes) async {
    final clamped = minutes.clamp(-60, 60);
    if (clamped == state.adjustmentFor(key)) return;
    state = state.copyWithAdjustment(key, clamped);
    await _save();
  }

  Future<void> clearAdjustments() async {
    if (!state.hasAdjustments) return;
    state = state.copyWith(adjustments: const <PrayerNameKey, int>{});
    await _save();
  }

  Future<void> selectCity(String cityId) async {
    state = state.copyWith(
      locationMode: LocationMode.manual,
      manualCityId: cityId,
    );
    await _save();
  }

  /// يفعّل الوضع التلقائي بعد ما الـ controller يجيب الإحداثيات.
  Future<void> setAutoLocation(Place newPlace) async {
    state = state.copyWith(
      locationMode: LocationMode.auto,
      lastKnown: newPlace,
    );
    await _save();
  }

  /// يرجع للمدينة المختارة يدويًا (بدون مسح آخر موقع معروف).
  Future<void> useManualLocation() async {
    state = state.copyWith(locationMode: LocationMode.manual);
    await _save();
  }

  Future<void> resetAll() async {
    state = const PrayerSettings();
    await _save();
  }
}
