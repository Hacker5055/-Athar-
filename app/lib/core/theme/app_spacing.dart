import 'package:flutter/material.dart';

/// المقاسات — كلها مضاعفات للشبكة الأساسية (4).
class AppSpacing {
  const AppSpacing._();

  static const double xs = 4;
  static const double sm = 8;
  static const double md = 12;
  static const double lg = 16;
  static const double xl = 20;
  static const double xxl = 24;
  static const double xxxl = 32;
  static const double huge = 48;

  /// هامش الشاشة الأفقي حسب `docs/04`.
  static const double screenPaddingH = 20;
  static const double screenPaddingV = 16;

  static const EdgeInsets screenPadding =
      EdgeInsets.symmetric(horizontal: screenPaddingH);
  static const EdgeInsets cardPadding = EdgeInsets.all(lg);
  static const EdgeInsets rowPadding =
      EdgeInsets.symmetric(horizontal: lg, vertical: md);
}

/// أنصاف أقطار الحواف.
class AppRadius {
  const AppRadius._();

  static const double chip = 12;
  static const double button = 14;
  static const double card = 20;
  static const double sheet = 24;
  static const double pill = 999;

  static final BorderRadius chipRadius = BorderRadius.circular(chip);
  static final BorderRadius buttonRadius = BorderRadius.circular(button);
  static final BorderRadius cardRadius = BorderRadius.circular(card);
  static final BorderRadius sheetRadius =
      BorderRadius.vertical(top: Radius.circular(sheet));
}

/// مدد الحركة — متطابقة مع `docs/04` (قسم الحركة).
class AppDuration {
  const AppDuration._();

  static const Duration tap = Duration(milliseconds: 120);
  static const Duration state = Duration(milliseconds: 200);
  static const Duration screen = Duration(milliseconds: 260);
  static const Duration sheet = Duration(milliseconds: 320);
  static const Duration progress = Duration(milliseconds: 600);
}

/// منحنيات الحركة.
class AppCurve {
  const AppCurve._();

  static const Curve standard = Curves.easeOutCubic;
  static const Curve emphasized = Curves.easeOutQuart;
  static const Curve compass = Curves.easeOutCubic;
}

/// مقاسات ثابتة للعناصر.
class AppSize {
  const AppSize._();

  /// الحد الأدنى لأي هدف قابل للنقر (إرشادات Material + راحة الإبهام).
  static const double minTouchTarget = 48;
  static const double appBarHeight = 56;
  static const double bottomNavHeight = 72;
  static const double iconButton = 48;
  static const double cardElevation = 1;
}
