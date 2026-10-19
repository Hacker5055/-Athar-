import 'package:flutter_test/flutter_test.dart';

import 'package:athar/features/quran/domain/entities/quran_reference.dart';
import 'package:athar/features/quran/domain/search_query.dart';

void main() {
  group('QuranSearchParser — التصنيف', () {
    test('استعلام فارغ', () {
      for (final raw in ['', '   ', '\n']) {
        final q = QuranSearchParser.parse(raw);
        expect(q.kind, QuranSearchKind.empty, reason: raw);
        expect(q.isEmpty, isTrue);
      }
    });

    test('نص عربي → arabic', () {
      final q = QuranSearchParser.parse('الرحمن');
      expect(q.kind, QuranSearchKind.arabic);
      expect(q.kind.needsFullTextSearch, isTrue);
    });

    test('نص لاتيني → latin', () {
      final q = QuranSearchParser.parse('allah is light');
      expect(q.kind, QuranSearchKind.latin);
    });

    test('العربي المتشكّل يُطبَّع', () {
      final q = QuranSearchParser.parse('الرَّحۡمَٰنِ');
      expect(q.normalized, 'الرحمن');
      expect(q.tokens, ['الرحمن']);
    });
  });

  group('QuranSearchParser — تعبير FTS5', () {
    test('كلمة واحدة → بادئة', () {
      expect(QuranSearchParser.buildMatchExpression(['الحمد']), '"الحمد"*');
    });

    test('كلمات متعددة → AND', () {
      expect(
        QuranSearchParser.buildMatchExpression(['الحمد', 'لله']),
        '"الحمد"* AND "لله"*',
      );
    });

    test('قائمة فارغة → سلسلة فارغة (لا استعلام)', () {
      expect(QuranSearchParser.buildMatchExpression(const []), '');
    });

    test('⚠️ تهريب علامات التنصيص — حقن FTS5', () {
      // بدون التهريب، هذا يكسر تعبير MATCH
      expect(
        QuranSearchParser.buildMatchExpression(['قل"هو']),
        '"قل""هو"*',
      );
    });

    test('تعبير جاهز من الاستعلام', () {
      final q = QuranSearchParser.parse('الحمد لله');
      expect(q.matchExpression, '"الحمد"* AND "لله"*');
    });

    test('سقف عدد الكلمات = ٨', () {
      final q = QuranSearchParser.parse(
        List.generate(20, (i) => 'كلمة$i').join(' '),
      );
      expect(q.tokens.length, QuranSearchParser.maxTokens);
    });

    test('سقف طول الكلمة الواحدة', () {
      final long = 'ا' * 500;
      final q = QuranSearchParser.parse(long);
      expect(
        q.tokens.first.length,
        QuranSearchParser.maxTokenLength,
      );
    });

    test('الكلمات الفارغة تُحذف', () {
      final q = QuranSearchParser.parse('الحمد    لله');
      expect(q.tokens, ['الحمد', 'لله']);
    });
  });

  group('QuranReferenceParser — آية:سورة', () {
    test('2:255', () {
      final ref = QuranReferenceParser.parse('2:255');
      expect(ref, isA<AyahReference>());
      expect((ref! as AyahReference).surah, 2);
      expect(ref.ayah, 255);
    });

    test('2 255 و 2/255', () {
      for (final raw in ['2 255', '2/255', '2.255']) {
        final ref = QuranReferenceParser.parse(raw);
        expect(ref, isA<AyahReference>(), reason: raw);
      }
    });

    test('الأرقام العربية-الهندية: ٢:٢٥٥', () {
      final ref = QuranReferenceParser.parse('٢:٢٥٥');
      expect(ref, isA<AyahReference>());
      expect((ref! as AyahReference).surah, 2);
      expect(ref.ayah, 255);
    });

    test('سورة خارج النطاق → null', () {
      expect(QuranReferenceParser.parse('115:1'), isNull);
      expect(QuranReferenceParser.parse('0:1'), isNull);
      expect(QuranReferenceParser.parse('2:999'), isNull);
    });

    test('المسافات الزائدة', () {
      final ref = QuranReferenceParser.parse('  2:255  ');
      expect(ref, isA<AyahReference>());
    });
  });

  group('QuranReferenceParser — جزء · صفحة · حزب · سورة', () {
    test('الجزء', () {
      for (final raw in ['ج 3', 'جزء 3', 'الجزء 3']) {
        final ref = QuranReferenceParser.parse(raw);
        expect(ref, isA<JuzReference>(), reason: raw);
        expect((ref! as JuzReference).juz, 3);
      }
    });

    test('الصفحة', () {
      for (final raw in ['ص 42', 'صفحة 42', 'الصفحة 42']) {
        final ref = QuranReferenceParser.parse(raw);
        expect(ref, isA<PageReference>(), reason: raw);
        expect((ref! as PageReference).page, 42);
      }
    });

    test('الحزب', () {
      final ref = QuranReferenceParser.parse('حزب 7');
      expect(ref, isA<HizbReference>());
      expect((ref! as HizbReference).hizb, 7);
    });

    test('سورة برقم فقط', () {
      final ref = QuranReferenceParser.parse('36');
      expect(ref, isA<SurahReference>());
      expect((ref! as SurahReference).surah, 36);
    });

    test('خارج النطاق → null', () {
      expect(QuranReferenceParser.parse('ج 31'), isNull);
      expect(QuranReferenceParser.parse('ص 900'), isNull);
      expect(QuranReferenceParser.parse('حزب 61'), isNull);
      expect(QuranReferenceParser.parse('115'), isNull);
    });

    test('نص عادي ليس مرجعًا', () {
      expect(QuranReferenceParser.parse('الرحمن'), isNull);
      expect(QuranReferenceParser.parse('allah'), isNull);
      expect(QuranReferenceParser.parse(''), isNull);
    });

    test('looksLikeReference', () {
      expect(QuranReferenceParser.looksLikeReference('2:255'), isTrue);
      expect(QuranReferenceParser.looksLikeReference('الرحمن'), isFalse);
    });
  });

  group('التكامل', () {
    test('المرجع يسبق البحث النصي', () {
      final q = QuranSearchParser.parse('2:255');
      expect(q.kind, QuranSearchKind.reference);
      expect(q.kind.needsFullTextSearch, isFalse);
      expect(q.matchExpression, '');
    });

    test('بحث فرنسي في الترجمات', () {
      final q = QuranSearchParser.parse('Le Très-Haut');
      expect(q.kind, QuranSearchKind.latin);
      expect(q.tokens.isNotEmpty, isTrue);
    });
  });
}
