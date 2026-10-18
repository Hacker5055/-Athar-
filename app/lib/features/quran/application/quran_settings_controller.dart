import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../../core/settings/settings_controller.dart' show sharedPreferencesProvider;
import '../../../core/theme/quran_theme.dart';
import '../data/quran_settings_repository.dart';
import '../domain/quran_settings.dart';

final quranSettingsRepositoryProvider =
    Provider<QuranSettingsRepository>((ref) {
  return QuranSettingsRepository(ref.watch(sharedPreferencesProvider));
});

/// تفضيلات القراءة — محفوظة تحت `athar.quran.v1`.
final quranSettingsProvider =
    StateNotifierProvider<QuranSettingsController, QuranSettings>((ref) {
  return QuranSettingsController(ref.watch(quranSettingsRepositoryProvider));
});

class QuranSettingsController extends StateNotifier<QuranSettings> {
  QuranSettingsController(this._repository) : super(_repository.load());

  final QuranSettingsRepository _repository;

  Future<void> setFontScale(double value) async {
    state = state.copyWith(fontScale: value);
    await _repository.save(state);
  }

  /// خطوة واحدة للتكبير/التصغير (أزرار + و − في ورقة الإعدادات).
  Future<void> nudgeFontScale(double delta) =>
      setFontScale(state.fontScale + delta);

  Future<void> setPageTheme(QuranPageTheme value) async {
    state = state.copyWith(pageTheme: value);
    await _repository.save(state);
  }

  Future<void> setShowTranslation(bool value) async {
    state = state.copyWith(showTranslation: value);
    await _repository.save(state);
  }

  Future<void> setEdition(String? value) async {
    state = state.withEdition(value);
    await _repository.save(state);
  }

  Future<void> reset() async {
    state = const QuranSettings();
    await _repository.save(state);
  }
}
