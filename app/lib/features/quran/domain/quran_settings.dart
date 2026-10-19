import '../../../core/theme/quran_theme.dart';

/// تفضيلات القراءة — محفوظة على الجهاز، ومستقلة عن ثيم التطبيق.
///
/// ### لماذا منفصلة عن `AppSettings`؟
/// ثيم التطبيق قرار عام (فاتح/داكن للواجهة كلها)، وثيم القراءة قرار
/// لحظي خاص بشاشة القرآن: المستخدم ممكن يقرأ على «ورق» فاتح والتطبيق
/// كله داكن. الخلط بينهم يجعل الإعدادين يتصارعان.
class QuranSettings {
  const QuranSettings({
    this.fontScale = 1.0,
    this.pageTheme = QuranPageTheme.paper,
    this.showTranslation = true,
    this.edition,
  });

  /// مضاعف حجم الخط القرآني (الأساس ٢٢sp).
  final double fontScale;

  final QuranPageTheme pageTheme;

  final bool showTranslation;

  /// إصدار الترجمة المختار — `null` = آخر إصدار مستخدم (أو المتاح).
  final String? edition;

  // ── الحدود ──────────────────────────────────────────────
  // الحد الأدنى ٠.٧٥: أقل من كده التشكيل العثماني يلتصق ويُقرأ غلط.
  // الحد الأعلى ٢.٠: أكبر من كده الآية الواحدة تملأ الشاشة.
  static const double minFontScale = 0.75;
  static const double maxFontScale = 2.0;
  static const double defaultFontScale = 1.0;
  static const double fontScaleStep = 0.05;

  double clampScale(double value) =>
      value < minFontScale
          ? minFontScale
          : (value > maxFontScale ? maxFontScale : value);

  QuranSettings copyWith({
    double? fontScale,
    QuranPageTheme? pageTheme,
    bool? showTranslation,
    Object? edition = _sentinel,
  }) =>
      QuranSettings(
        fontScale: fontScale == null ? this.fontScale : clampScale(fontScale),
        pageTheme: pageTheme ?? this.pageTheme,
        showTranslation: showTranslation ?? this.showTranslation,
        edition: identical(edition, _sentinel)
            ? this.edition
            : edition as String?,
      );

  /// نسخة بترجمة مختارة — مع تمرير `null` صريح (يمسح الاختيار).
  QuranSettings withEdition(String? value) =>
      copyWith(edition: value);

  static const Object _sentinel = Object();

  @override
  bool operator ==(Object other) =>
      other is QuranSettings &&
      other.fontScale == fontScale &&
      other.pageTheme == pageTheme &&
      other.showTranslation == showTranslation &&
      other.edition == edition;

  @override
  int get hashCode =>
      Object.hash(fontScale, pageTheme, showTranslation, edition);

  @override
  String toString() =>
      'QuranSettings(×$fontScale, ${pageTheme.name}, '
      'tr=$showTranslation, ${edition ?? '-'})';
}
