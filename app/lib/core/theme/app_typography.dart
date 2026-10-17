import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:athar/core/theme/app_colors.dart';

/// سلم الخطوط — مطابق لـ `docs/04-design-system.md`.
///
/// ⚠️ السقالة دلوقتي بتستخدم حزمة `google_fonts` (بتنزّل الخط مرة واحدة).
/// للإطلاق فعّل `useGoogleFonts = false` وارفق الخطوط في `assets/fonts/`
/// (راجع `assets/fonts/README.md`).
class AppTypography {
  const AppTypography._();

  static const bool useGoogleFonts = true;

  /// أسماء العائلات عند تعطيل `google_fonts` (لازم تكون مرفقة في pubspec).
  static const String fontFamilyDisplay = 'Amiri';
  static const String fontFamilyUi = 'IBMPlexSansArabic';
  static const String fontFamilyQuran = 'UthmanicHafs';

  // ── المقاسات (sp) ────────────────────────────────────────
  static const double display = 32; // عنوان شاشة كبير
  static const double headline = 24; // عنوان الشاشة
  static const double title = 20; // عنوان بطاقة
  static const double subtitle = 17; // عنوان فرعي
  static const double body = 15; // النص الأساسي
  static const double caption = 13; // نصوص مساعدة
  static const double overline = 11; // تسميات صغيرة

  /// حجم النص القرآني الافتراضي (قابل للتكبير حتى [quranFontSizeMax]).
  static const double quranFontSize = 22;
  static const double quranFontSizeMin = 16;
  static const double quranFontSizeMax = 40;

  /// تباعد الأسطر للنص القرآني — أعلى من الطبيعي عشان التشكيل ما يتقصّش.
  static const double quranLineHeight = 2.15;

  /// يبني الـ [TextTheme] الكامل لثيم معيّن.
  static TextTheme textTheme(AppColorSet colors, Brightness brightness) {
    final base = ThemeData(brightness: brightness).textTheme;

    final theme = TextTheme(
      displayLarge: _style(base.displayLarge, colors.ink, display, 40,
          FontWeight.w700),
      headlineSmall: _style(base.headlineSmall, colors.ink, headline, 32,
          FontWeight.w700),
      titleLarge:
          _style(base.titleLarge, colors.ink, title, 28, FontWeight.w600),
      titleMedium:
          _style(base.titleMedium, colors.ink, subtitle, 24, FontWeight.w600),
      bodyLarge: _style(base.bodyLarge, colors.ink, body, 24, FontWeight.w400),
      bodyMedium:
          _style(base.bodyMedium, colors.ink70, 14, 22, FontWeight.w400),
      bodySmall:
          _style(base.bodySmall, colors.ink60, caption, 18, FontWeight.w400),
      labelLarge:
          _style(base.labelLarge, colors.onPrimary, 14, 20, FontWeight.w600),
      labelMedium:
          _style(base.labelMedium, colors.ink70, 12, 16, FontWeight.w600),
      labelSmall:
          _style(base.labelSmall, colors.ink60, overline, 14, FontWeight.w600),
    );

    if (!useGoogleFonts) return theme;

    // خط الواجهة (عربي/لاتيني) على كل السلّم …
    final ui = GoogleFonts.ibmPlexSansArabicTextTheme(theme);
    // … والعناوين تاخد خط نسخ كلاسيكي.
    return ui.copyWith(
      displayLarge: GoogleFonts.amiriTextStyle(textStyle: ui.displayLarge),
      headlineSmall: GoogleFonts.amiriTextStyle(textStyle: ui.headlineSmall),
      titleLarge: GoogleFonts.amiriTextStyle(textStyle: ui.titleLarge),
    );
  }

  /// نمط النص القرآني.
  ///
  /// [fontSize] بييجي من إعداد المستخدم (16 → 40).
  static TextStyle quranStyle({
    required Color color,
    double fontSize = quranFontSize,
    double height = quranLineHeight,
  }) {
    final base = TextStyle(
      color: color,
      fontSize: fontSize,
      height: height,
      fontWeight: FontWeight.w400,
      fontFamily: useGoogleFonts ? null : fontFamilyQuran,
    );
    if (!useGoogleFonts) return base;
    // لحد ما نرفق خط عثماني: Amiri هو أقرب خط متاح لرسم المصحف.
    return GoogleFonts.amiriTextStyle(textStyle: base);
  }

  static TextStyle _style(
    TextStyle? base,
    Color color,
    double size,
    double height,
    FontWeight weight,
  ) {
    return (base ?? const TextStyle()).copyWith(
      color: color,
      fontSize: size,
      height: height / size,
      fontWeight: weight,
      fontFamily: useGoogleFonts ? null : fontFamilyUi,
    );
  }
}
