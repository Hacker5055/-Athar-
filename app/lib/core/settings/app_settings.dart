import 'dart:convert';

import 'package:flutter/material.dart' show ThemeMode;

import '../l10n/app_locale.dart';

/// كل إعدادات التطبيق في immutable value object واحد.
///
/// سبب الدمج في كائن واحد: أي تغيير في إعداد واحد بيكتب JSON واحد،
/// فمفيش حالة «اللغة اتحفظت والمظهر لأ» لو التطبيق اتقفل فجأة.
class AppSettings {
  const AppSettings({
    this.locale = AppLocale.ar,
    this.themeMode = ThemeMode.system,
    this.textScale = 1,
    this.useArabicDigits = true,
    this.onboardingCompleted = false,
  });

  final AppLocale locale;
  final ThemeMode themeMode;

  /// مضاعف حجم الخط (0.85 = صغير، 1 = متوسط، 1.15 = كبير، 1.3 = كبير جدًا).
  final double textScale;

  /// عرض الأرقام بالأرقام العربية-الهندية (١٢٣) بدل (123).
  final bool useArabicDigits;

  final bool onboardingCompleted;

  AppSettings copyWith({
    AppLocale? locale,
    ThemeMode? themeMode,
    double? textScale,
    bool? useArabicDigits,
    bool? onboardingCompleted,
  }) {
    return AppSettings(
      locale: locale ?? this.locale,
      themeMode: themeMode ?? this.themeMode,
      textScale: textScale ?? this.textScale,
      useArabicDigits: useArabicDigits ?? this.useArabicDigits,
      onboardingCompleted: onboardingCompleted ?? this.onboardingCompleted,
    );
  }

  Map<String, dynamic> toJson() => {
        'locale': locale.code,
        'themeMode': themeMode.name,
        'textScale': textScale,
        'useArabicDigits': useArabicDigits,
        'onboardingCompleted': onboardingCompleted,
      };

  factory AppSettings.fromJson(Map<String, dynamic> json) {
    return AppSettings(
      locale: AppLocale.fromCode(json['locale'] as String?),
      themeMode: _themeModeFromName(json['themeMode'] as String?),
      textScale: (json['textScale'] as num?)?.toDouble() ?? 1,
      useArabicDigits: json['useArabicDigits'] as bool? ?? true,
      onboardingCompleted: json['onboardingCompleted'] as bool? ?? false,
    );
  }

  /// يقرأ من JSON — بيرجع الإعدادات الافتراضية لو الملف بايظ
  /// (مفيش حاجة أسوأ من تطبيق مش بيفتح لأن إعداد اتكتب غلط).
  static AppSettings decode(String? raw) {
    if (raw == null || raw.isEmpty) return const AppSettings();
    try {
      final decoded = jsonDecode(raw);
      if (decoded is! Map<String, dynamic>) return const AppSettings();
      return AppSettings.fromJson(decoded);
    } on FormatException {
      return const AppSettings();
    } on TypeError {
      return const AppSettings();
    }
  }

  String encode() => jsonEncode(toJson());

  static ThemeMode _themeModeFromName(String? name) {
    switch (name) {
      case 'light':
        return ThemeMode.light;
      case 'dark':
        return ThemeMode.dark;
      default:
        return ThemeMode.system;
    }
  }
}
