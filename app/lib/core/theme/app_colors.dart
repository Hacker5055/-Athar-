import 'package:flutter/material.dart';

/// الألوان الأساسية للهوية (ثابتة في الثيمين).
///
/// القيم هنا مطابقة لـ `docs/04-design-system.md` وللبروتوتايب في
/// `prototype/assets/css/styles.css` — أي تغيير لازم يتعمل في التلاتة مع بعض.
class AppColors {
  const AppColors._();

  // العلامة التجارية
  static const Color primary = Color(0xFF0F6E5C); // زمردي عميق
  static const Color accent = Color(0xFFC9A227); // ذهبي دافئ
  static const Color ink = Color(0xFF12211D); // حبر (النص الأساسي)

  // دلالية (تُستخدم في حالات مش مرتبطة بالثيم)
  static const Color success = Color(0xFF2E7D5B);
  static const Color warning = Color(0xFFB4690E);
  static const Color danger = Color(0xFFB3261E);

  // الكعبة / عناصر محايدة
  static const Color white = Color(0xFFFFFFFF);
  static const Color black = Color(0xFF000000);
}

/// مجموعة ألوان كاملة لثيم واحد.
///
/// الفكرة: `ThemeData` يتولد من الكائن ده، فمفيش لون hardcoded في أي شاشة.
@immutable
class AppColorSet {
  const AppColorSet({
    required this.bg,
    required this.surface,
    required this.surface2,
    required this.surface3,
    required this.primary,
    required this.primary600,
    required this.primary100,
    required this.primary200,
    required this.onPrimary,
    required this.accent,
    required this.accent100,
    required this.ink,
    required this.ink70,
    required this.ink60,
    required this.ink40,
    required this.border,
    required this.borderStrong,
    required this.success,
    required this.successBg,
    required this.warning,
    required this.warningBg,
    required this.danger,
    required this.statusBarIconBrightness,
  });

  /// الثيم الفاتح — «العمارة والضوء».
  static const AppColorSet light = AppColorSet(
    bg: Color(0xFFFBF8F2), // عاجي
    surface: Color(0xFFFFFFFF),
    surface2: Color(0xFFF4F0E7),
    surface3: Color(0xFFEBE5D8),
    primary: Color(0xFF0F6E5C),
    primary600: Color(0xFF0A5346),
    primary100: Color(0xFFD9EFE8),
    primary200: Color(0xFFB9E0D5),
    onPrimary: Color(0xFFFFFFFF),
    accent: Color(0xFFC9A227),
    accent100: Color(0xFFF8EFD3),
    ink: Color(0xFF12211D),
    ink70: Color(0xFF3F5450),
    ink60: Color(0xFF5C6F69),
    ink40: Color(0xFF8C9C96),
    border: Color(0xFFE7DFD2),
    borderStrong: Color(0xFFD8CDBC),
    success: Color(0xFF2E7D5B),
    successBg: Color(0xFFDFF1E7),
    warning: Color(0xFFB4690E),
    warningBg: Color(0xFFFBEEDC),
    danger: Color(0xFFB3261E),
    statusBarIconBrightness: Brightness.dark,
  );

  /// الثيم الداكن — «الليل الدافئ».
  static const AppColorSet dark = AppColorSet(
    bg: Color(0xFF0C1512),
    surface: Color(0xFF13211D),
    surface2: Color(0xFF182923),
    surface3: Color(0xFF21362F),
    primary: Color(0xFF3FBF9E),
    primary600: Color(0xFF57D0B2),
    primary100: Color(0xFF12352D),
    primary200: Color(0xFF17503F),
    onPrimary: Color(0xFF06201A),
    accent: Color(0xFFE5C158),
    accent100: Color(0xFF3A301A),
    ink: Color(0xFFECF3F0),
    ink70: Color(0xFFC3D4CE),
    ink60: Color(0xFF9DB3AC),
    ink40: Color(0xFF6F8781),
    border: Color(0xFF223A33),
    borderStrong: Color(0xFF2E4B42),
    success: Color(0xFF5FD1A0),
    successBg: Color(0xFF12352A),
    warning: Color(0xFFF0B45E),
    warningBg: Color(0xFF3A2A12),
    danger: Color(0xFFFF8175),
    statusBarIconBrightness: Brightness.light,
  );

  final Color bg;
  final Color surface;
  final Color surface2;
  final Color surface3;
  final Color primary;
  final Color primary600;
  final Color primary100;
  final Color primary200;
  final Color onPrimary;
  final Color accent;
  final Color accent100;
  final Color ink;
  final Color ink70;
  final Color ink60;
  final Color ink40;
  final Color border;
  final Color borderStrong;
  final Color success;
  final Color successBg;
  final Color warning;
  final Color warningBg;
  final Color danger;
  final Brightness statusBarIconBrightness;
}
