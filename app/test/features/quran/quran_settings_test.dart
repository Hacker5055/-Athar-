import 'package:flutter_test/flutter_test.dart';

import 'package:athar/core/theme/quran_theme.dart';
import 'package:athar/features/quran/domain/quran_settings.dart';

void main() {
  group('QuranSettings — الافتراضي', () {
    test('القيم الافتراضية', () {
      const s = QuranSettings();
      expect(s.fontScale, 1.0);
      expect(s.pageTheme, QuranPageTheme.paper);
      expect(s.showTranslation, isTrue);
      expect(s.edition, isNull);
    });
  });

  group('QuranSettings — تقييد حجم الخط', () {
    test('داخل النطاق كما هو', () {
      const s = QuranSettings();
      expect(s.clampScale(1.0), 1.0);
      expect(s.clampScale(1.5), 1.5);
    });

    test('أصغر من الحد الأدنى → الحد الأدنى', () {
      const s = QuranSettings();
      expect(s.clampScale(0.1), QuranSettings.minFontScale);
      expect(s.clampScale(0), QuranSettings.minFontScale);
      expect(s.clampScale(-5), QuranSettings.minFontScale);
    });

    test('أكبر من الحد الأعلى → الحد الأعلى', () {
      const s = QuranSettings();
      expect(s.clampScale(99), QuranSettings.maxFontScale);
      expect(s.clampScale(2.5), QuranSettings.maxFontScale);
    });

    test('copyWith يقيّد أيضًا (لا ثقة في المتصل)', () {
      const s = QuranSettings();
      expect(s.copyWith(fontScale: 10).fontScale, QuranSettings.maxFontScale);
      expect(s.copyWith(fontScale: 0.01).fontScale, QuranSettings.minFontScale);
    });

    test('الحد الأدنى لا يقل عن ٠.٧٥ — التشكيل يلتصق تحتها', () {
      expect(QuranSettings.minFontScale >= 0.75, isTrue);
    });
  });

  group('QuranSettings — copyWith', () {
    test('يغيّر الثيم فقط', () {
      const s = QuranSettings(fontScale: 1.4);
      final next = s.copyWith(pageTheme: QuranPageTheme.night);
      expect(next.pageTheme, QuranPageTheme.night);
      expect(next.fontScale, 1.4, reason: 'لا يلمس ما لم يُطلب');
      expect(next.showTranslation, s.showTranslation);
    });

    test('withEdition(null) يمسح الاختيار صراحةً', () {
      const s = QuranSettings(edition: 'fr.hamidullah');
      expect(s.withEdition(null).edition, isNull);
      expect(s.withEdition('en.sahih').edition, 'en.sahih');
    });

    test('copyWithout edition يحافظ على القيمة القديمة', () {
      const s = QuranSettings(edition: 'fr.hamidullah');
      expect(s.copyWith(fontScale: 1.2).edition, 'fr.hamidullah');
    });
  });

  group('QuranSettings — المساواة', () {
    test('قيم متساوية → كائنات متساوية', () {
      const a = QuranSettings(fontScale: 1.2, pageTheme: QuranPageTheme.sepia);
      const b = QuranSettings(fontScale: 1.2, pageTheme: QuranPageTheme.sepia);
      expect(a, b);
      expect(a.hashCode, b.hashCode);
    });

    test('اختلاف بسيط → كائنات مختلفة', () {
      const a = QuranSettings(fontScale: 1.2);
      const b = QuranSettings(fontScale: 1.25);
      expect(a, isNot(b));
    });
  });

  group('QuranSearchQuery — المساواة (مفتاح family)', () {
    test('نصان متكافئان بعد التطبيع → نفس المفتاح', () {
      final a = QuranSearchParser.parse('الرحمن');
      final b = QuranSearchParser.parse('الرَّحمٰن');
      expect(a.normalized, b.normalized);
      expect(a, b, reason: 'family provider يعيد استخدام النتيجة');
      expect(a.hashCode, b.hashCode);
    });

    test('نصان مختلفان → مفتاحان مختلفان', () {
      expect(QuranSearchParser.parse('الرحمن'),
          isNot(QuranSearchParser.parse('الرحيم')));
    });

    test('المرجع جزء من المساواة', () {
      expect(QuranSearchParser.parse('2:255'),
          isNot(QuranSearchParser.parse('2:256')));
    });
  });
}
