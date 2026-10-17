import 'package:flutter/material.dart' show ThemeMode;
import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/l10n/app_locale.dart';
import 'package:athar/core/settings/app_settings.dart';

void main() {
  group('AppSettings', () {
    test('القيم الافتراضية: عربي + ثيم النظام', () {
      const settings = AppSettings();
      expect(settings.locale, AppLocale.ar);
      expect(settings.themeMode, ThemeMode.system);
      expect(settings.textScale, 1);
      expect(settings.useArabicDigits, isTrue);
      expect(settings.onboardingCompleted, isFalse);
    });

    test('copyWith يغيّر قيمة واحدة فقط', () {
      const settings = AppSettings();
      final updated = settings.copyWith(locale: AppLocale.fr);
      expect(updated.locale, AppLocale.fr);
      expect(updated.themeMode, settings.themeMode);
      expect(updated.textScale, settings.textScale);
    });

    test('ترميز وفك ترميز يرجّع نفس الكائن', () {
      const settings = AppSettings(
        locale: AppLocale.fr,
        themeMode: ThemeMode.dark,
        textScale: 1.15,
        useArabicDigits: false,
        onboardingCompleted: true,
      );
      final decoded = AppSettings.decode(settings.encode());
      expect(decoded.locale, settings.locale);
      expect(decoded.themeMode, settings.themeMode);
      expect(decoded.textScale, settings.textScale);
      expect(decoded.useArabicDigits, settings.useArabicDigits);
      expect(decoded.onboardingCompleted, settings.onboardingCompleted);
    });

    test('JSON بايظ يرجّع الإعدادات الافتراضية بدل ما يوقّع التطبيق', () {
      final fromGarbage = AppSettings.decode('{not json at all');
      expect(fromGarbage.locale, AppLocale.ar);

      final fromNull = AppSettings.decode(null);
      expect(fromNull.themeMode, ThemeMode.system);

      final fromWrongType = AppSettings.decode('"just a string"');
      expect(fromWrongType.textScale, 1);

      final fromMissingKeys = AppSettings.decode('{}');
      expect(fromMissingKeys.locale, AppLocale.ar);
      expect(fromMissingKeys.onboardingCompleted, isFalse);
    });

    test('أكواد لغة غير معروفة تسقط على العربية', () {
      final decoded = AppSettings.decode('{"locale":"de"}');
      expect(decoded.locale, AppLocale.ar);
    });
  });
}
