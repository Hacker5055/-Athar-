import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../../core/settings/settings_controller.dart' show sharedPreferencesProvider;
import '../../../core/utils/greeting.dart' show PrayerNameKey;
import '../domain/notification_settings.dart';

const String notificationSettingsKey = 'athar.notifications.v1';

/// إعدادات الإشعارات — محفوظة تحت `athar.notifications.v1`.
///
/// **منفصلة عن إعدادات الصلاة** عن قصد: لو تلفت، نرجّع للافتراضي
/// (أذان لكل الصلوات) بدون ما نبوّظ طريقة الحساب أو المكان.
final notificationSettingsProvider =
    StateNotifierProvider<NotificationSettingsController, NotificationSettings>(
        (ref) {
  final prefs = ref.watch(sharedPreferencesProvider);
  return NotificationSettingsController(prefs);
});

class NotificationSettingsController
    extends StateNotifier<NotificationSettings> {
  NotificationSettingsController(this._prefs)
      : super(
          NotificationSettings.decode(
            _prefs.getString(notificationSettingsKey),
          ),
        );

  final SharedPreferences _prefs;

  Future<void> _save() =>
      _prefs.setString(notificationSettingsKey, state.encode());

  /// المفتاح الرئيسي — يقفل كل شيء (السفر، الاجتماعات، …).
  Future<void> setMasterEnabled(bool value) async {
    if (value == state.masterEnabled) return;
    state = state.copyWith(masterEnabled: value);
    await _save();
  }

  Future<void> setMode(PrayerNameKey prayer, PrayerAlertMode mode) async {
    if (mode == state.modeFor(prayer)) return;
    state = state.copyWithMode(prayer, mode);
    await _save();
  }

  /// يطبّق نفس الوضع على كل الصلوات دفعة واحدة (عدا الشروق).
  Future<void> setModeForAll(PrayerAlertMode mode) async {
    final next = <PrayerNameKey, PrayerAlertMode>{
      for (final prayer in PrayerNameKey.values)
        if (prayer != PrayerNameKey.sunrise) prayer: mode,
    };
    state = state.copyWith(modes: next);
    await _save();
  }

  Future<void> setReminder(PrayerNameKey prayer, int minutes) async {
    if (minutes == state.reminderFor(prayer)) return;
    state = state.copyWithReminder(prayer, minutes);
    await _save();
  }

  Future<void> setJumuahReminder(bool value) async {
    if (value == state.jumuahReminder) return;
    state = state.copyWith(jumuahReminder: value);
    await _save();
  }

  Future<void> setJumuahMinutesBefore(int minutes) async {
    final clamped = minutes.clamp(30, 240);
    if (clamped == state.jumuahMinutesBefore) return;
    state = state.copyWith(jumuahMinutesBefore: clamped);
    await _save();
  }

  Future<void> setSound(String soundId) async {
    if (soundId == state.adhanSoundId) return;
    state = state.copyWith(adhanSoundId: soundId);
    await _save();
  }

  Future<void> resetAll() async {
    state = const NotificationSettings();
    await _save();
  }
}
