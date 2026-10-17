import 'package:intl/intl.dart';

import '../l10n/app_locale.dart';
import '../l10n/strings.dart';
import 'arabic_numbers.dart';

/// تنسيق الأوقات والتواريخ في أثر.
///
/// **مهم:** دوال `hm`/`hm12` لا تقوم بأي تحويل منطقي زمني — هي بتقرأ
/// الساعة والدقيقة من الـ [DateTime] زي ما هو. يعني المتصل لازم يمرّر
/// `TZDateTime` في التوقيت الصح (عبر حزمة `timezone`)، والمقارنات
/// والعدّادات لازم تستخدم الـ UTC instants الأصلية.
abstract final class AtharTimeFormat {
  /// `05:32` — ٢٤ ساعة.
  static String hm(DateTime t, {required bool useArabicDigits}) {
    final h = t.hour.toString().padLeft(2, '0');
    final m = t.minute.toString().padLeft(2, '0');
    return ArabicNumbers.format('$h:$m', useArabicDigits: useArabicDigits);
  }

  /// `٥:٣٢ ص` / `5:32 AM` — ١٢ ساعة مع اللاحقة حسب اللغة.
  static String hm12(
    DateTime t, {
    required Strings s,
    required bool useArabicDigits,
  }) {
    final hour24 = t.hour;
    final isPm = hour24 >= 12;
    var hour12 = hour24 % 12;
    if (hour12 == 0) hour12 = 12;

    final suffix = isPm ? s.timePm : s.timeAm;
    final h = hour12.toString();
    final m = t.minute.toString().padLeft(2, '0');
    return '${ArabicNumbers.format('$h:$m', useArabicDigits: useArabicDigits)} '
        '$suffix';
  }

  /// التاريخ الميلادي بصيغة طويلة بلغة الواجهة.
  /// `السبت، ١٨ أكتوبر ٢٠٢٦`
  static String longDate(
    DateTime t, {
    required AppLocale locale,
    required bool useArabicDigits,
  }) {
    final pattern = switch (locale) {
      AppLocale.ar => 'EEEE، d MMMM y',
      AppLocale.en => 'EEEE, d MMMM y',
      AppLocale.fr => 'EEEE d MMMM y',
    };
    final text = DateFormat(pattern, locale.code).format(t);
    return ArabicNumbers.format(text, useArabicDigits: useArabicDigits);
  }

  /// اسم اليوم فقط.
  static String weekday(
    DateTime t, {
    required AppLocale locale,
  }) =>
      DateFormat('EEEE', locale.code).format(t);
}
