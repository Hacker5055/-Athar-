import 'dart:ui';

/// اللغات المدعومة في أثر.
///
/// العربية هي الافتراضية والمرجع لأي نص ناقص في لغة أخرى.
enum AppLocale {
  ar,
  en,
  fr;

  /// كود اللغة حسب ISO 639-1 (`ar` / `en` / `fr`).
  static AppLocale fromCode(String? code) {
    switch (code) {
      case 'ar':
        return AppLocale.ar;
      case 'en':
        return AppLocale.en;
      case 'fr':
        return AppLocale.fr;
      default:
        return AppLocale.ar;
    }
  }

  /// يطابق أفضل لغة من لغات الجهاز، وإلا يرجع العربية.
  static AppLocale fromPlatformLocales(List<Locale> locales) {
    for (final locale in locales) {
      switch (locale.languageCode.toLowerCase()) {
        case 'ar':
          return AppLocale.ar;
        case 'fr':
          return AppLocale.fr;
        case 'en':
          return AppLocale.en;
      }
    }
    return AppLocale.ar;
  }

  static const List<Locale> supportedLocales = [
    Locale('ar'),
    Locale('en'),
    Locale('fr'),
  ];
}

/// مساعدات على [AppLocale].
extension AppLocaleX on AppLocale {
  /// كود اللغة (`ar` / `en` / `fr`).
  String get code => name;

  Locale get locale => Locale(name);

  /// اتجاه النص — العربية فقط RTL.
  TextDirection get textDirection =>
      this == AppLocale.ar ? TextDirection.rtl : TextDirection.ltr;

  bool get isRtl => this == AppLocale.ar;

  /// اسم اللغة بلغتها نفسها (يُعرض في قائمة اختيار اللغة).
  String get nativeName {
    switch (this) {
      case AppLocale.ar:
        return 'العربية';
      case AppLocale.en:
        return 'English';
      case AppLocale.fr:
        return 'Français';
    }
  }

  /// اسم اللغة بالإنجليزية (يُعرض في سياق تقني/تشخيصي).
  String get englishName {
    switch (this) {
      case AppLocale.ar:
        return 'Arabic';
      case AppLocale.en:
        return 'English';
      case AppLocale.fr:
        return 'French';
    }
  }
}
