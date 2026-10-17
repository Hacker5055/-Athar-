import 'package:flutter/material.dart';

/// ثيمات القراءة — **مستقلة** عن ثيم التطبيق.
///
/// السبب: المستخدم ممكن يكون مختار ثيم داكن للتطبيق بس عايز يقرأ
/// على «ورق» فاتح (والعكس). القراءة لها منطقها الخاص.
enum QuranPageTheme {
  paper,
  sepia,
  night,
}

/// ألوان ثيم قراءة واحد.
@immutable
class QuranThemeColors {
  const QuranThemeColors({
    required this.background,
    required this.text,
    required this.accent,
    required this.markerBorder,
  });

  final Color background;
  final Color text;
  final Color accent;
  final Color markerBorder;

  static const QuranThemeColors paper = QuranThemeColors(
    background: Color(0xFFFDF8EC),
    text: Color(0xFF1F1A14),
    accent: Color(0xFF0F6E5C),
    markerBorder: Color(0xFFB9E0D5),
  );

  static const QuranThemeColors sepia = QuranThemeColors(
    background: Color(0xFFF2E4CB),
    text: Color(0xFF4A3720),
    accent: Color(0xFF0A5346),
    markerBorder: Color(0xFFC8AF84),
  );

  static const QuranThemeColors night = QuranThemeColors(
    background: Color(0xFF151A18),
    text: Color(0xFFDCE6DF),
    accent: Color(0xFF3FBF9E),
    markerBorder: Color(0xFF17503F),
  );
}

extension QuranPageThemeX on QuranPageTheme {
  QuranThemeColors get colors {
    switch (this) {
      case QuranPageTheme.paper:
        return QuranThemeColors.paper;
      case QuranPageTheme.sepia:
        return QuranThemeColors.sepia;
      case QuranPageTheme.night:
        return QuranThemeColors.night;
    }
  }

  /// الثيم الافتراضي بناءً على ثيم التطبيق.
  static QuranPageTheme defaultFor(Brightness appBrightness) =>
      appBrightness == Brightness.dark
          ? QuranPageTheme.night
          : QuranPageTheme.paper;
}
