import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/utils/arabic_numbers.dart';

void main() {
  group('ArabicNumbers', () {
    test('يحوّل الأرقام اللاتينية إلى عربية-هندية', () {
      expect(ArabicNumbers.toArabic('255'), '٢٥٥');
      expect(ArabicNumbers.toArabic('0'), '٠');
      expect(ArabicNumbers.toArabic('0123456789'), '٠١٢٣٤٥٦٧٨٩');
    });

    test('يحوّل الأرقام العربية-الهندية إلى لاتينية', () {
      expect(ArabicNumbers.toLatin('٢٥٥'), '255');
      expect(ArabicNumbers.toLatin('سورة ١١٢'), 'سورة 112');
    });

    test('الذهاب والعودة لا يغيّر القيمة', () {
      const original = '1287';
      expect(
        ArabicNumbers.toLatin(ArabicNumbers.toArabic(original)),
        original,
      );
    });

    test('النص بدون أرقام يمر كما هو', () {
      expect(ArabicNumbers.toArabic('البقرة'), 'البقرة');
    });

    test('format يحترم اختيار المستخدم', () {
      expect(
        ArabicNumbers.format(255, useArabicDigits: true),
        '٢٥٥',
      );
      expect(
        ArabicNumbers.format(255, useArabicDigits: false),
        '255',
      );
    });

    test('withSeparators يضيف فاصل الآلاف', () {
      expect(
        ArabicNumbers.withSeparators(1287, useArabicDigits: false),
        '1,287',
      );
      expect(
        ArabicNumbers.withSeparators(1000, useArabicDigits: false),
        '1,000',
      );
      expect(
        ArabicNumbers.withSeparators(99, useArabicDigits: false),
        '99',
      );
    });

    test('formatDuration ينسّق العدّاد التنازلي', () {
      expect(
        ArabicNumbers.formatDuration(
          const Duration(hours: 1, minutes: 24, seconds: 36),
          useArabicDigits: false,
        ),
        '01:24:36',
      );
      expect(
        ArabicNumbers.formatDuration(
          const Duration(minutes: 5, seconds: 3),
          useArabicDigits: false,
          showHours: false,
        ),
        '05:03',
      );
      expect(
        ArabicNumbers.formatDuration(
          const Duration(hours: 12, minutes: 30),
          useArabicDigits: true,
        ),
        '١٢:٣٠:٠٠',
      );
    });
  });
}
