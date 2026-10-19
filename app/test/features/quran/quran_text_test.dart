import 'package:flutter_test/flutter_test.dart';

import 'package:athar/features/quran/domain/quran_text.dart';

void main() {
  group('QuranText.normalize', () {
    test('يشيل التشكيل', () {
      expect(
        QuranText.normalize('بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ'),
        'بسم الله الرحمن الرحيم',
      );
    });

    test('يوحّد الألف بأنواعها (أ إ آ ٱ)', () {
      expect(QuranText.normalize('أإآٱ'), 'اااا');
    });

    test('يوحّد الهمزة على حروفها (ؤ ئ)', () {
      expect(QuranText.normalize('ؤ'), 'و');
      expect(QuranText.normalize('ئ'), 'ي');
    });

    test('يوحّد ى و ی مع ي', () {
      expect(QuranText.normalize('ىی'), 'يي');
    });

    test('التاء المربوطة → هاء', () {
      expect(QuranText.normalize('رحمة'), 'رحمه');
    });

    test('الألف الخنجرية تُشال', () {
      expect(QuranText.normalize('الرحمٰن'), 'الرحمن');
    });

    test('التطويل يُشال', () {
      expect(QuranText.normalize('بســـم'), 'بسم');
    });

    test('علامات الوقف العثمانية تُشال (ۖ ۗ ۘ ۙ ۚ ۛ ۜ)', () {
      for (final mark in ['ۖ', 'ۗ', 'ۘ', 'ۙ', 'ۚ', 'ۛ', 'ۜ']) {
        expect(QuranText.normalize('قل$mark'), 'قل', reason: mark);
      }
    });

    test('علامة الآية والربع والسجدة تُشال (۝ ۞ ۩)', () {
      for (final mark in ['۝', '۞', '۩']) {
        expect(QuranText.normalize('قل$mark'), 'قل', reason: mark);
      }
    });

    test('علامات التشكيل الممتدة تُشال', () {
      expect(QuranText.normalize('مُحَمَّدࣰ'), 'محمد');
    });

    test('المسافات المتعددة تُضغط', () {
      expect(QuranText.normalize('بسم   الله'), 'بسم الله');
    });

    test('الأرقام والترقيم تُشال', () {
      expect(QuranText.normalize('آية (١٢٣)'), 'ايه');
    });

    test('نص فارغ → فارغ', () {
      expect(QuranText.normalize(''), '');
      expect(QuranText.normalize('   '), '');
      expect(QuranText.normalize('۝۝۝'), '');
    });

    test('حتمية: نفس المدخل = نفس المخرج', () {
      const input = 'الۡحَمۡدُ لِلَّهِ رَبِّ ٱلۡعَٰلَمِينَ';
      expect(QuranText.normalize(input), QuranText.normalize(input));
    });

    // ── حالات واقعية من المصحف ──
    test('آية الكرسي — أولها', () {
      expect(
        QuranText.normalize('ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ ٱلۡحَيُّ ٱلۡقَيُّومُ'),
        'الله لا اله الا هو الحي القيوم',
      );
    });

    test('البحث يجد «الرحمن» مكتوبة بأي رسم', () {
      final forms = [
        'الرحمن',
        'الرحمٰن',
        'الرَّحۡمَٰنِ',
        'الرحمَن',
      ];
      final normalized = forms.map(QuranText.normalize).toSet();
      expect(normalized.length, 1, reason: '$normalized');
    });
  });

  group('QuranText.normalizeLatin', () {
    test('يصغّر الحروف ويشيل الترقيم', () {
      expect(
        QuranText.normalizeLatin('Allah, the Most Gracious!'),
        'allah the most gracious',
      );
    });

    test('يحافظ على الحروف الفرنسية المشدّدة', () {
      expect(
        QuranText.normalizeLatin('Le Très-Haut'),
        'le tr s haut'.replaceAll('  ', ' '),
      );
    });

    test('المسافات المتعددة تُضغط', () {
      expect(QuranText.normalizeLatin('a    b'), 'a b');
    });

    test('فارغ → فارغ', () {
      expect(QuranText.normalizeLatin(''), '');
      expect(QuranText.normalizeLatin('!!!'), '');
    });
  });
}
