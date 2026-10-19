import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/utils/search_text.dart';

void main() {
  group('SearchText.normalizeArabic', () {
    test('يشيل التشكيل والتطويل', () {
      expect(SearchText.normalizeArabic('القَاهِرَة'), 'القاهره');
      expect(SearchText.normalizeArabic('القـاهرة'), 'القاهرة');
      expect(SearchText.normalizeArabic('مُحَمَّدٌ'), 'محمد');
    });

    test('يوحّد الهمزات والألف والياء والتاء المربوطة', () {
      expect(SearchText.normalizeArabic('أحمد'), 'احمد');
      expect(SearchText.normalizeArabic('إبراهيم'), 'ابراهيم');
      expect(SearchText.normalizeArabic('آدم'), 'ادم');
      expect(SearchText.normalizeArabic('مكة'), 'مكه');
      expect(SearchText.normalizeArabic('مصطفى'), 'مصطفي');
      expect(SearchText.normalizeArabic('مسؤول'), 'مسوول');
    });

    test('القاهرة بكل أشكالها = نفس النص', () {
      final forms = ['القاهرة', 'القاهره', 'القـاهرة', 'القَاهِرَة', 'القاهره '];
      final normalized = forms.map(SearchText.normalizeArabic).toSet();
      expect(normalized.length, 1);
    });

    test('النص الفاضي يفضل فاضي', () {
      expect(SearchText.normalizeArabic(''), '');
      expect(SearchText.normalizeArabic('   '), '');
    });
  });

  group('SearchText.normalizeLatin', () {
    test('يصغّر الحروف ويشيل التشكيل الأوروبي', () {
      expect(SearchText.normalizeLatin('Cairo'), 'cairo');
      expect(SearchText.normalizeLatin('Le Caire'), 'le caire');
      expect(SearchText.normalizeLatin('São Paulo'), 'sao paulo');
      expect(SearchText.normalizeLatin('Zürich'), 'zurich');
      expect(SearchText.normalizeLatin('München'), 'munchen');
    });
  });

  group('SearchText.normalize', () {
    test('يختار التطبيع المناسب تلقائيًا', () {
      expect(SearchText.normalize('القَاهِرَة'), 'القاهره');
      expect(SearchText.normalize('Paris'), 'paris');
    });
  });
}
