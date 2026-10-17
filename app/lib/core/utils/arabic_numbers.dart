/// تحويل وتنسيق الأرقام بين الأرقام اللاتينية والعربية-الهندية.
///
/// القرار: الأرقام العربية-الهندية هي الافتراضية في الواجهة العربية
/// (أقرب لقراءة المصحف)، لكنها **اختيار مستخدم** — وفي السياقات
/// التقنية (الأرقام التسلسلية، الإحداثيات) نستخدم اللاتينية دائمًا.
class ArabicNumbers {
  const ArabicNumbers._();

  static const List<String> _arabicDigits = [
    '٠',
    '١',
    '٢',
    '٣',
    '٤',
    '٥',
    '٦',
    '٧',
    '٨',
    '٩',
  ];

  static final RegExp _digitPattern = RegExp(r'[0-9]');

  /// "255" → "٢٥٥"
  static String toArabic(String input) => input.replaceAllMapped(
        _digitPattern,
        (match) => _arabicDigits[int.parse(match.group(0)!)],
      );

  /// "٢٥٥" → "255"
  static String toLatin(String input) {
    final buffer = StringBuffer();
    for (final unit in input.codeUnits) {
      // ٠ = U+0660 … ٩ = U+0669
      if (unit >= 0x0660 && unit <= 0x0669) {
        buffer.write(unit - 0x0660);
      } else {
        buffer.writeCharCode(unit);
      }
    }
    return buffer.toString();
  }

  /// ينسّق رقمًا مع مراعاة اختيار المستخدم.
  static String format(
    num value, {
    bool useArabicDigits = true,
    int? fractionDigits,
  }) {
    final text = fractionDigits == null
        ? '$value'
        : value.toStringAsFixed(fractionDigits);
    return useArabicDigits ? toArabic(text) : text;
  }

  /// فاصل آلاف: 1287 → "1,287" أو "١٬٢٨٧"
  static String withSeparators(
    num value, {
    bool useArabicDigits = true,
  }) {
    final isNegative = value < 0;
    final digits = toLatin(value.abs().round().toString());
    final buffer = StringBuffer();
    for (var i = 0; i < digits.length; i++) {
      if (i > 0 && (digits.length - i) % 3 == 0) {
        buffer.write(',');
      }
      buffer.write(digits[i]);
    }
    final sign = isNegative ? '-' : '';
    // الفاصل العربي التقليدي U+066C
    final result = '$sign$buffer';
    return useArabicDigits
        ? toArabic(result).replaceAll(',', '٬')
        : result;
  }

  /// "01:24:36" → "٠١:٢٤:٣٦" (العدّاد التنازلي للصلاة)
  static String formatDuration(
    Duration duration, {
    bool useArabicDigits = true,
    bool showHours = true,
  }) {
    final hours = duration.inHours;
    final minutes = duration.inMinutes.remainder(60);
    final seconds = duration.inSeconds.remainder(60);

    final text = showHours
        ? '${_pad(hours)}:${_pad(minutes)}:${_pad(seconds)}'
        : '${_pad(minutes)}:${_pad(seconds)}';
    return useArabicDigits ? toArabic(text) : text;
  }

  static String _pad(int value) => value.toString().padLeft(2, '0');
}
