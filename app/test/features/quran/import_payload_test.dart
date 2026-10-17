import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';

import 'package:athar/features/quran/data/import/import_payload.dart';

/// يبني ملف استيراد **متسقًا ذاتيًا** (١١٤ سورة · ٦٢٣٦ آية).
///
/// البيانات اصطناعية عن قصد: نختبر المُحلّل والمدقّق، لا نص القرآن.
Map<String, Object> buildPayload({
  int surahCount = 114,
  int totalAyahs = 6236,
  bool allowsStorage = true,
  Map<String, List<Map<String, Object>>>? translations,
  bool omitSource = false,
  int? wrongAyahCountForSurah1,
  bool duplicateSurah = false,
  bool emptyAyahText = false,
}) {
  // نوزّع الآيات: ١١٣ سورة × ٥٤ آية + الباقي للسورة الأخيرة
  final counts = <int>[];
  var remaining = totalAyahs;
  for (var i = 0; i < surahCount; i++) {
    final take = i == surahCount - 1
        ? remaining
        : (remaining > (surahCount - i - 1) * 54 ? 54 : 1);
    counts.add(take);
    remaining -= take;
  }

  final surahs = <Map<String, Object>>[];
  final ayahs = <Map<String, Object>>[];
  for (var s = 0; s < surahCount; s++) {
    final id = s + 1;
    final declared = (id == 1 && wrongAyahCountForSurah1 != null)
        ? wrongAyahCountForSurah1
        : counts[s];
    surahs.add({
      'id': id,
      'name_ar': 'سورة $id',
      'name_en': 'Surah $id',
      'name_fr': 'Sourate $id',
      'translit_en': 'Surah $id',
      'revelation': id % 2 == 0 ? 'medinan' : 'meccan',
      'ayah_count': declared,
      'bismillah_pre': id != 9,
      'start_juz': 1,
      'start_page': 1,
    });
    for (var a = 1; a <= counts[s]; a++) {
      ayahs.add({
        'surah': id,
        'ayah': a,
        'text': emptyAyahText ? '' : 'نص $id:$a',
        'juz': (a % 30) + 1,
        'hizb': (a % 60) + 1,
        'page': (a % 604) + 1,
        'sajda': false,
      });
    }
  }
  if (duplicateSurah) surahs.add(surahs.first);

  return {
    if (!omitSource)
      'source': {
        'name': 'مصدر اختبار',
        'license': 'CC-BY-3.0',
        'url': 'https://example.invalid',
        'allowsPermanentStorage': allowsStorage,
        'author': 'مؤلف',
        'fetchedOn': '2026-10-18',
      },
    'surahs': surahs,
    'ayahs': ayahs,
    if (translations != null) 'translations': translations,
  };
}

String jsonOf(Map<String, Object> payload) => jsonEncode(payload);

void main() {
  group('ImportPayload — الحالة السليمة', () {
    test('ملف كامل يُقرأ', () {
      final result = ImportPayload.parse(jsonOf(buildPayload()));
      expect(result.ok, isTrue, reason: result.errors.join(' | '));
      expect(result.payload!.surahs.length, 114);
      expect(result.payload!.ayahs.length, 6236);
    });

    test('حقول الأصل تُقرأ', () {
      final payload = ImportPayload.parse(jsonOf(buildPayload())).payload!;
      expect(payload.provenance.name, 'مصدر اختبار');
      expect(payload.provenance.license, 'CC-BY-3.0');
      expect(payload.provenance.allowsPermanentStorage, isTrue);
    });

    test('الترجمات تُقرأ وتُرتّب', () {
      final result = ImportPayload.parse(
        jsonOf(
          buildPayload(
            translations: {
              'en.sahih': [
                {'surah': 1, 'ayah': 1, 'text': 'In the name'},
              ],
              'fr.hamidullah': [
                {'surah': 1, 'ayah': 1, 'text': 'Au nom'},
              ],
            },
          ),
        ),
      );
      final payload = result.payload!;
      expect(payload.editions, ['en.sahih', 'fr.hamidullah']);
      expect(payload.translations['en.sahih']!.length, 1);
    });

    test('القيم المفقودة تُستبدل بافتراضات آمنة', () {
      final minimal = {
        'source': {'name': 'x', 'license': 'y', 'allowsPermanentStorage': true},
        'surahs': [
          {
            'id': 1,
            'name_ar': 'الفاتحة',
            'ayah_count': 2,
            'revelation': 'meccan',
          },
        ],
        'ayahs': [
          {'surah': 1, 'ayah': 1, 'text': 'نص'},
          {'surah': 1, 'ayah': 2, 'text': 'نص'},
        ],
      };
      // ١ سورة ≠ ١١٤ خطأ متوقّع — لكن القراءة نفسها سليمة
      final result = ImportPayload.parse(jsonEncode(minimal));
      expect(
        result.errors.any((e) => e.contains('١١٤')),
        isTrue,
      );
    });
  });

  group('ImportPayload — ⚖️ حارس الترخيص', () {
    test('مصدر لا يسمح بالتخزين الدائم يُرفض', () {
      final result = ImportPayload.parse(
        jsonOf(buildPayload(allowsStorage: false)),
      );
      expect(result.ok, isFalse);
      expect(
        result.errors.any((e) => e.contains('التخزين الدائم')),
        isTrue,
        reason: result.errors.join(' | '),
      );
    });

    test('بدون قسم source → خطأ', () {
      final result = ImportPayload.parse(jsonOf(buildPayload(omitSource: true)));
      expect(result.ok, isFalse);
      expect(result.errors.any((e) => e.contains('source')), isTrue);
    });
  });

  group('ImportPayload — التحقّق', () {
    test('عدد السور ≠ ١١٤', () {
      final result = ImportPayload.parse(
        jsonOf(buildPayload(surahCount: 10, totalAyahs: 540)),
      );
      expect(result.errors.any((e) => e.contains('١١٤')), isTrue);
    });

    test('عدد الآيات ≠ ٦٢٣٦', () {
      final result = ImportPayload.parse(
        jsonOf(buildPayload(surahCount: 114, totalAyahs: 100)),
      );
      expect(result.errors.any((e) => e.contains('٦٢٣٦')), isTrue);
    });

    test('تناقض عدد آيات سورة', () {
      final result = ImportPayload.parse(
        jsonOf(buildPayload(wrongAyahCountForSurah1: 999)),
      );
      expect(
        result.errors.any((e) => e.contains('سورة 1')),
        isTrue,
        reason: result.errors.join(' | '),
      );
    });

    test('سورة مكرّرة', () {
      final result = ImportPayload.parse(
        jsonOf(buildPayload(duplicateSurah: true)),
      );
      expect(result.errors.any((e) => e.contains('مكرّرة')), isTrue);
    });

    test('آية بنص فارغ', () {
      final result = ImportPayload.parse(
        jsonOf(buildPayload(emptyAyahText: true)),
      );
      expect(result.ok, isFalse);
    });
  });

  group('ImportPayload — التالف', () {
    test('JSON غير صالح', () {
      final result = ImportPayload.parse('ليس JSON');
      expect(result.ok, isFalse);
      expect(result.errors.first, contains('غير صالح'));
    });

    test('جذر ليس كائنًا', () {
      expect(ImportPayload.parse('[]').ok, isFalse);
      expect(ImportPayload.parse('null').ok, isFalse);
    });

    test('حقول مفقودة تمامًا', () {
      expect(ImportPayload.parse('{}').ok, isFalse);
    });

    test('لا يرمي استثناءً أبدًا', () {
      for (final bad in ['', '{', '}}', '[1,2]', '{"surahs": 5}']) {
        expect(() => ImportPayload.parse(bad), returnsNormally);
      }
    });
  });

  group('ImportPayload — تقييد القيم', () {
    test('الجزء والصفحة والحزب تُقيَّد', () {
      final payload = {
        'source': {'name': 'x', 'license': 'y', 'allowsPermanentStorage': true},
        'surahs': [
          {'id': 1, 'name_ar': 'ا', 'ayah_count': 1},
        ],
        'ayahs': [
          {'surah': 1, 'ayah': 1, 'text': 'ن', 'juz': 99, 'page': 9999, 'hizb': 0},
        ],
      };
      final result = ImportPayload.parse(jsonEncode(payload));
      final ayah = result.payload!.ayahs.single;
      expect(ayah.juz, 30);
      expect(ayah.page, 604);
      expect(ayah.hizb, 1);
    });

    test('سورة برقم خارج ١..١١٤ تُرفض', () {
      final payload = {
        'source': {'name': 'x', 'license': 'y', 'allowsPermanentStorage': true},
        'surahs': [
          {'id': 200, 'name_ar': 'ا', 'ayah_count': 1},
        ],
        'ayahs': [
          {'surah': 200, 'ayah': 1, 'text': 'ن'},
        ],
      };
      final result = ImportPayload.parse(jsonEncode(payload));
      expect(result.ok, isFalse);
    });
  });
}
