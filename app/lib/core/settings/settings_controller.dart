import 'package:flutter/material.dart' show Locale, ThemeMode;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../l10n/app_locale.dart';
import '../l10n/strings.dart';
import 'app_settings.dart';

/// يتجاوز في `main()` بعد ما `SharedPreferences.getInstance()` تخلص.
final sharedPreferencesProvider = Provider<SharedPreferences>((ref) {
  throw UnimplementedError(
    'sharedPreferencesProvider must be overridden in main()',
  );
});

/// الحالة العامة للإعدادات.
final settingsProvider =
    StateNotifierProvider<SettingsController, AppSettings>((ref) {
  throw UnimplementedError('settingsProvider must be overridden in main()');
});

// ══════════════════════════════════════════════════════════════
// مشتقات مريحة — كل شاشة بتحتاج واحدة من دول بدل الكائن كله
// ══════════════════════════════════════════════════════════════

final currentLocaleProvider =
    Provider<AppLocale>((ref) => ref.watch(settingsProvider).locale);

final stringsProvider =
    Provider<Strings>((ref) => Strings(ref.watch(currentLocaleProvider)));

final themeModeProvider =
    Provider<ThemeMode>((ref) => ref.watch(settingsProvider).themeMode);

final textScaleProvider =
    Provider<double>((ref) => ref.watch(settingsProvider).textScale);

final useArabicDigitsProvider =
    Provider<bool>((ref) => ref.watch(settingsProvider).useArabicDigits);

/// يتحكم في الإعدادات ويحفظها فورًا في `SharedPreferences`.
class SettingsController extends StateNotifier<AppSettings> {
  SettingsController(this._prefs, AppSettings initial) : super(initial);

  final SharedPreferences _prefs;

  static const String storageKey = 'athar.settings.v1';

  /// يقرأ الإعدادات عند الإقلاع.
  ///
  /// أول تشغيل: نحترم لغة الجهاز (لو عربية/إنجليزية/فرنسية)،
  /// وإلا نبدأ بالعربية — وبعدين شاشة أول تشغيل بتأكد الاختيار.
  static AppSettings initial(
    SharedPreferences prefs,
    List<Locale> platformLocales,
  ) {
    final raw = prefs.getString(storageKey);
    if (raw == null || raw.isEmpty) {
      return AppSettings(
        locale: AppLocale.fromPlatformLocales(platformLocales),
      );
    }
    return AppSettings.decode(raw);
  }

  Future<void> _persist() => _prefs.setString(storageKey, state.encode());

  Future<void> setLocale(AppLocale locale) async {
    if (locale == state.locale) return;
    state = state.copyWith(locale: locale);
    await _persist();
  }

  Future<void> setThemeMode(ThemeMode mode) async {
    if (mode == state.themeMode) return;
    state = state.copyWith(themeMode: mode);
    await _persist();
  }

  Future<void> setTextScale(double scale) async {
    if (scale == state.textScale) return;
    state = state.copyWith(textScale: scale);
    await _persist();
  }

  Future<void> setUseArabicDigits(bool value) async {
    if (value == state.useArabicDigits) return;
    state = state.copyWith(useArabicDigits: value);
    await _persist();
  }

  Future<void> completeOnboarding() async {
    if (state.onboardingCompleted) return;
    state = state.copyWith(onboardingCompleted: true);
    await _persist();
  }

  /// للاستخدام في الاختبارات فقط.
  Future<void> resetAll() async {
    state = const AppSettings();
    await _persist();
  }
}
