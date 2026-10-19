import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'app_colors.dart';
import 'app_spacing.dart';
import 'app_typography.dart';

/// يبني `ThemeData` من نظام التصميم في `docs/04`.
///
/// كل ثيم = [AppColorSet] + [TextTheme] + معايرة المكوّنات (buttons,
/// cards, inputs, navigation…). أي شاشة تستخدم `context.colors`
/// بدل أي لون hardcoded.
class AppTheme {
  const AppTheme._();

  static ThemeData light() => _build(AppColorSet.light, Brightness.light);
  static ThemeData dark() => _build(AppColorSet.dark, Brightness.dark);

  static ThemeData _build(AppColorSet c, Brightness brightness) {
    final textTheme = AppTypography.textTheme(c, brightness);

    final colorScheme = ColorScheme(
      brightness: brightness,
      primary: c.primary,
      onPrimary: c.onPrimary,
      primaryContainer: c.primary100,
      onPrimaryContainer: c.primary600,
      secondary: c.accent,
      onSecondary: AppColors.ink,
      secondaryContainer: c.accent100,
      onSecondaryContainer: AppColors.ink,
      error: c.danger,
      onError: AppColors.white,
      errorContainer: c.danger,
      onErrorContainer: AppColors.white,
      background: c.bg,
      onBackground: c.ink,
      surface: c.surface,
      onSurface: c.ink,
      surfaceVariant: c.surface2,
      onSurfaceVariant: c.ink60,
      outline: c.border,
      outlineVariant: c.borderStrong,
      shadow: AppColors.black,
      scrim: AppColors.black,
      inverseSurface: c.ink,
      onInverseSurface: c.bg,
      inversePrimary: c.primary200,
      // يمنع M3 من تلوين الأسطح بلون primary عند الرفع.
      surfaceTint: Colors.transparent,
    );

    return ThemeData(
      useMaterial3: true,
      brightness: brightness,
      colorScheme: colorScheme,
      scaffoldBackgroundColor: c.bg,
      canvasColor: c.bg,
      primaryColor: c.primary,
      textTheme: textTheme,
      fontFamily:
          AppTypography.useGoogleFonts ? null : AppTypography.fontFamilyUi,

      // ── شريط العنوان ────────────────────────────────────
      appBarTheme: AppBarTheme(
        backgroundColor: c.bg,
        foregroundColor: c.ink,
        surfaceTintColor: Colors.transparent,
        elevation: 0,
        scrolledUnderElevation: 1,
        centerTitle: false,
        titleSpacing: AppSpacing.xl,
        toolbarHeight: AppSize.appBarHeight,
        titleTextStyle: textTheme.headlineSmall,
        iconTheme: IconThemeData(color: c.ink70, size: 24),
        actionsIconTheme: IconThemeData(color: c.ink70, size: 24),
        systemOverlayStyle: SystemUiOverlayStyle(
          statusBarColor: Colors.transparent,
          statusBarIconBrightness: c.statusBarIconBrightness,
          statusBarBrightness: brightness,
        ),
      ),

      // ── البطاقات ────────────────────────────────────────
      cardTheme: CardThemeData(
        color: c.surface,
        elevation: 0,
        margin: EdgeInsets.zero,
        clipBehavior: Clip.antiAlias,
        shape: RoundedRectangleBorder(
          borderRadius: AppRadius.cardRadius,
          side: BorderSide(color: c.border, width: 1),
        ),
      ),

      // ── الأزرار ─────────────────────────────────────────
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: c.primary,
          foregroundColor: c.onPrimary,
          disabledBackgroundColor: c.surface3,
          disabledForegroundColor: c.ink40,
          elevation: 0,
          shadowColor: Colors.transparent,
          minimumSize: const Size(0, AppSize.minTouchTarget),
          padding: const EdgeInsets.symmetric(
            horizontal: AppSpacing.xl,
            vertical: AppSpacing.md,
          ),
          shape: RoundedRectangleBorder(borderRadius: AppRadius.buttonRadius),
          textStyle: textTheme.labelLarge,
        ),
      ),
      filledButtonTheme: FilledButtonThemeData(
        style: FilledButton.styleFrom(
          backgroundColor: c.primary100,
          foregroundColor: c.primary600,
          elevation: 0,
          minimumSize: const Size(0, AppSize.minTouchTarget),
          padding: const EdgeInsets.symmetric(
            horizontal: AppSpacing.lg,
            vertical: AppSpacing.md,
          ),
          shape: RoundedRectangleBorder(borderRadius: AppRadius.buttonRadius),
          textStyle: textTheme.labelLarge,
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          backgroundColor: Colors.transparent,
          foregroundColor: c.primary,
          disabledForegroundColor: c.ink40,
          elevation: 0,
          minimumSize: const Size(0, AppSize.minTouchTarget),
          padding: const EdgeInsets.symmetric(
            horizontal: AppSpacing.xl,
            vertical: AppSpacing.md,
          ),
          side: BorderSide(color: c.borderStrong, width: 1.5),
          shape: RoundedRectangleBorder(borderRadius: AppRadius.buttonRadius),
          textStyle: textTheme.labelLarge,
        ),
      ),
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: c.primary,
          disabledForegroundColor: c.ink40,
          minimumSize: const Size(0, AppSize.minTouchTarget),
          padding: const EdgeInsets.symmetric(horizontal: AppSpacing.md),
          shape: RoundedRectangleBorder(borderRadius: AppRadius.buttonRadius),
          textStyle: textTheme.labelLarge,
        ),
      ),

      // ── حقول الإدخال ────────────────────────────────────
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: c.surface2,
        isDense: true,
        contentPadding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.md,
          vertical: AppSpacing.md,
        ),
        hintStyle: textTheme.bodyMedium?.copyWith(color: c.ink40),
        prefixIconColor: c.ink40,
        suffixIconColor: c.ink40,
        border: _inputBorder(c.border),
        enabledBorder: _inputBorder(c.border),
        focusedBorder: _inputBorder(c.primary, 1.6),
        errorBorder: _inputBorder(c.danger),
        focusedErrorBorder: _inputBorder(c.danger, 1.6),
      ),

      // ── الورقة السفلية ──────────────────────────────────
      bottomSheetTheme: BottomSheetThemeData(
        backgroundColor: c.bg,
        modalBackgroundColor: c.bg,
        elevation: 0,
        clipBehavior: Clip.antiAlias,
        showDragHandle: true,
        dragHandleColor: c.borderStrong,
        shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(
            top: Radius.circular(AppRadius.sheet),
          ),
        ),
      ),

      // ── شريط التنقل السفلي ──────────────────────────────
      navigationBarTheme: NavigationBarThemeData(
        backgroundColor: c.surface,
        surfaceTintColor: Colors.transparent,
        elevation: 0,
        height: AppSize.bottomNavHeight,
        indicatorColor: c.primary100,
        indicatorShape:
            RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
        // TODO(migrate): استبدل بـ WidgetStateProperty لما نرفع أدنى إصدار Flutter لـ 3.32+
        iconTheme: MaterialStateProperty.resolveWith<IconThemeData>((states) {
          final selected = states.contains(MaterialState.selected);
          return IconThemeData(
            color: selected ? c.primary : c.ink40,
            size: 22,
          );
        }),
        labelTextStyle:
            MaterialStateProperty.resolveWith<TextStyle>((states) {
          final selected = states.contains(MaterialState.selected);
          return (textTheme.labelSmall ?? const TextStyle()).copyWith(
            fontSize: 11,
            fontWeight: FontWeight.w600,
            color: selected ? c.primary : c.ink40,
          );
        }),
      ),

      // ── الفواصل والقوائم ────────────────────────────────
      dividerTheme: DividerThemeData(
        color: c.border,
        space: 1,
        thickness: 1,
      ),
      listTileTheme: ListTileThemeData(
        iconColor: c.ink60,
        textColor: c.ink,
        minLeadingWidth: 24,
        contentPadding: const EdgeInsets.symmetric(
          horizontal: AppSpacing.lg,
          vertical: AppSpacing.xs,
        ),
      ),

      // ── الشرائح (chips) ─────────────────────────────────
      chipTheme: ChipThemeData(
        backgroundColor: c.surface2,
        selectedColor: c.primary,
        disabledColor: c.surface3,
        labelStyle: textTheme.labelMedium?.copyWith(color: c.ink70),
        secondaryLabelStyle: textTheme.labelMedium?.copyWith(color: c.onPrimary),
        side: BorderSide(color: c.border),
        shape: RoundedRectangleBorder(borderRadius: AppRadius.chipRadius),
        padding: const EdgeInsets.symmetric(horizontal: AppSpacing.md),
        showCheckmark: false,
      ),

      // ── المزلاجات والمؤشرات ─────────────────────────────
      sliderTheme: SliderThemeData(
        activeTrackColor: c.primary,
        inactiveTrackColor: c.surface3,
        thumbColor: c.primary,
        overlayColor: c.primary100,
      ),
      progressIndicatorTheme: ProgressIndicatorThemeData(
        color: c.primary,
        linearTrackColor: c.surface3,
        circularTrackColor: c.surface3,
      ),
      segmentedButtonTheme: SegmentedButtonThemeData(
        style: SegmentedButton.styleFrom(
          backgroundColor: c.surface2,
          foregroundColor: c.ink70,
          selectedBackgroundColor: c.primary,
          selectedForegroundColor: c.onPrimary,
          side: BorderSide(color: c.border),
          shape: RoundedRectangleBorder(borderRadius: AppRadius.chipRadius),
          minimumSize: const Size(0, 40),
          textStyle: textTheme.labelMedium,
        ),
      ),

      // ── التغذية الراجعة ─────────────────────────────────
      snackBarTheme: SnackBarThemeData(
        backgroundColor: c.ink,
        contentTextStyle: textTheme.bodyMedium?.copyWith(color: c.bg),
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: AppRadius.chipRadius),
      ),
      dialogTheme: DialogThemeData(
        backgroundColor: c.surface,
        elevation: 0,
        shape: RoundedRectangleBorder(borderRadius: AppRadius.cardRadius),
      ),
      iconTheme: IconThemeData(color: c.ink70, size: 24),
    );
  }

  static OutlineInputBorder _inputBorder(Color color, [double width = 1]) =>
      OutlineInputBorder(
        borderRadius: AppRadius.chipRadius,
        borderSide: BorderSide(color: color, width: width),
      );
}

/// وصول سريع لألوان الثيم الحالي من أي `context`.
/// بديل ثابت لمعالجة الشفافية.
///
/// `withOpacity` مهمّل في Flutter ٣.٢٧+، و`withValues` مش موجود قبلها —
/// فبنستخدم [Color.value] + [Color.fromRGBO] الاتنين مستقرّين من زمن طويل.
extension AtharColorAlphaX on Color {
  /// يرجّع نفس اللون بشفافية [opacity] (٠..١).
  Color fade(double opacity) => Color.fromRGBO(
        (value >> 16) & 0xFF,
        (value >> 8) & 0xFF,
        value & 0xFF,
        opacity.clamp(0.0, 1.0),
      );
}

extension AtharThemeX on BuildContext {
  AppColorSet get colors =>
      Theme.of(this).brightness == Brightness.dark
          ? AppColorSet.dark
          : AppColorSet.light;

  TextTheme get textStyles => Theme.of(this).textTheme;

  bool get isRtl => Directionality.of(this) == TextDirection.rtl;
}
