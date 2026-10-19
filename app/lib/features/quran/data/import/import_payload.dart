import 'dart:convert';

import '../../domain/entities/ayah.dart';
import '../../domain/entities/surah.dart';

/// نتيجة تحليل ملف الاستيراد.
class PayloadParseResult {
  const PayloadParseResult({this.payload, this.errors = const <String>[]});

  final ImportPayload? payload;

  /// كل الأخطاء المكتشفة (لا نتوقّف عند أول خطأ).
  final List<String> errors;

  bool get ok => payload != null && errors.isEmpty;

  @override
  String toString() =>
      'PayloadParseResult(ok=$ok, ${errors.length} خطأ, '
      '${payload?.ayahs.length ?? 0} آية)';
}

/// أصل المحتوى — يُعرض في شاشة «المصادر والتراخيص» (`docs/05` §3.6).
class Provenance {
  const Provenance({
    required this.name,
    required this.license,
    required this.url,
    required this.allowsPermanentStorage,
    this.author,
    this.fetchedOn,
  });

  final String name;
  final String license;
  final String url;
  final bool allowsPermanentStorage;
  final String? author;
  final String? fetchedOn;

  @override
  String toString() => 'Provenance($name · $license)';
}

/// حمولة الاستيراد كما نقرأها من الملف.
///
/// ⚠️ **الشكل ثابت وموثّق** في `docs/09-quran-data.md`. أي تغيير هنا
/// يحتاج تحديث الملف و`tool/fetch_quran_data.sh` معًا.
class ImportPayload {
  const ImportPayload({
    required this.provenance,
    required this.surahs,
    required this.ayahs,
    this.translations = const <String, List<TranslationEntry>>{},
  });

  final Provenance provenance;
  final List<Surah> surahs;
  final List<Ayah> ayahs;
  final Map<String, List<TranslationEntry>> translations;

  List<String> get editions => translations.keys.toList()..sort();

  static PayloadParseResult parse(String jsonText) {
    Object? decoded;
    try {
      decoded = jsonDecode(jsonText);
    } catch (error) {
      return PayloadParseResult(errors: ['ملف JSON غير صالح: $error']);
    }
    if (decoded is! Map) {
      return const PayloadParseResult(errors: ['الجذر يجب أن يكون كائنًا']);
    }
    return _fromMap(decoded);
  }

  static PayloadParseResult _fromMap(Map<Object?, Object?> root) {
    final errors = <String>[];

    // ── المصدر ──
    final sourceRaw = root['source'];
    if (sourceRaw is! Map) {
      errors.add('حقل "source" مفقود أو غير كائن');
    }
    final provenance = _provenance(sourceRaw is Map ? sourceRaw : const {});
    if (!provenance.allowsPermanentStorage) {
      errors.add(
        '⚖️ رخصة المصدر لا تسمح بالتخزين الدائم — '
        'لا يجوز استيراده (راجع docs/05 §3.1)',
      );
    }

    // ── السور ──
    final surahsRaw = root['surahs'];
    final surahs = <Surah>[];
    if (surahsRaw is! List) {
      errors.add('حقل "surahs" مفقود أو غير مصفوفة');
    } else {
      final seen = <int>{};
      for (final entry in surahsRaw) {
        if (entry is! Map) {
          errors.add('سورة بصيغة خاطئة');
          continue;
        }
        final surah = _surah(entry);
        if (surah == null) {
          errors.add('سورة ناقصة الحقول: $entry');
          continue;
        }
        if (!seen.add(surah.id)) {
          errors.add('سورة مكرّرة: ${surah.id}');
          continue;
        }
        surahs.add(surah);
      }
    }
    if (surahs.isNotEmpty && surahs.length != 114) {
      errors.add('عدد السور ${surahs.length} — المتوقّع ١١٤');
    }

    // ── الآيات ──
    final ayahsRaw = root['ayahs'];
    final ayahs = <Ayah>[];
    if (ayahsRaw is! List) {
      errors.add('حقل "ayahs" مفقود أو غير مصفوفة');
    } else {
      for (final entry in ayahsRaw) {
        if (entry is! Map) {
          errors.add('آية بصيغة خاطئة');
          continue;
        }
        final ayah = _ayah(entry);
        if (ayah == null) {
          errors.add('آية ناقصة الحقول: $entry');
          continue;
        }
        ayahs.add(ayah);
      }
    }
    if (ayahs.isNotEmpty && ayahs.length != 6236) {
      errors.add('عدد الآيات ${ayahs.length} — المتوقّع ٦٢٣٦ (حفص)');
    }

    // ── اتساق عدد آيات كل سورة ──
    if (surahs.isNotEmpty && ayahs.isNotEmpty) {
      final actual = <int, int>{};
      for (final ayah in ayahs) {
        actual[ayah.surahId] = (actual[ayah.surahId] ?? 0) + 1;
      }
      for (final surah in surahs) {
        final found = actual[surah.id] ?? 0;
        if (found != surah.ayahCount) {
          errors.add(
            'سورة ${surah.id}: الملف يقول ${surah.ayahCount} آية '
            'لكن وُجد $found',
          );
        }
      }
    }

    // ── الترجمات ──
    final translations = <String, List<TranslationEntry>>{};
    final translationsRaw = root['translations'];
    if (translationsRaw is Map) {
      for (final entry in translationsRaw.entries) {
        final edition = entry.key;
        if (edition is! String || edition.isEmpty) continue;
        final list = entry.value;
        if (list is! List) {
          errors.add('ترجمة "$edition" ليست مصفوفة');
          continue;
        }
        final parsed = <TranslationEntry>[];
        for (final item in list) {
          final t = _translation(item);
          if (t != null) parsed.add(t);
        }
        translations[edition] = parsed;
      }
    }

    if (errors.isNotEmpty) {
      return PayloadParseResult(errors: errors);
    }
    return PayloadParseResult(
      payload: ImportPayload(
        provenance: provenance,
        surahs: surahs,
        ayahs: ayahs,
        translations: translations,
      ),
    );
  }

  static Provenance _provenance(Map<Object?, Object?> m) => Provenance(
        name: (m['name'] as String?) ?? 'غير مسمّى',
        license: (m['license'] as String?) ?? 'غير محدّد',
        url: (m['url'] as String?) ?? '',
        allowsPermanentStorage: m['allowsPermanentStorage'] == true,
        author: m['author'] as String?,
        fetchedOn: m['fetchedOn'] as String?,
      );

  static Surah? _surah(Map<Object?, Object?> m) {
    final id = _int(m['id']);
    final nameAr = m['name_ar'] as String?;
    final ayahCount = _int(m['ayah_count']);
    if (id == null || nameAr == null || ayahCount == null) return null;
    if (id < 1 || id > 114) return null;
    return Surah(
      id: id,
      nameAr: nameAr,
      nameEn: (m['name_en'] as String?) ?? nameAr,
      nameFr: (m['name_fr'] as String?) ?? (m['name_en'] as String?) ?? nameAr,
      translitEn: (m['translit_en'] as String?) ?? '',
      revelation: RevelationType.fromString(m['revelation'] as String?),
      ayahCount: ayahCount,
      bismillahPre: m['bismillah_pre'] != false,
      startJuz: _int(m['start_juz']) ?? 1,
      startPage: _int(m['start_page']) ?? 1,
    );
  }

  static Ayah? _ayah(Map<Object?, Object?> m) {
    final surah = _int(m['surah']);
    final ayah = _int(m['ayah']);
    final text = m['text'] as String?;
    if (surah == null || ayah == null || text == null) return null;
    if (surah < 1 || surah > 114) return null;
    if (text.trim().isEmpty) return null;
    return Ayah(
      surahId: surah,
      ayah: ayah,
      textUthmani: text,
      juz: _clamp(_int(m['juz']), 1, 30),
      hizb: _clamp(_int(m['hizb']), 1, 60),
      page: _clamp(_int(m['page']), 1, 604),
      isSajda: m['sajda'] == true,
    );
  }

  static TranslationEntry? _translation(Object? item) {
    if (item is! Map) return null;
    final surah = _int(item['surah']);
    final ayah = _int(item['ayah']);
    final text = item['text'] as String?;
    if (surah == null || ayah == null || text == null) return null;
    return TranslationEntry(surah: surah, ayah: ayah, text: text);
  }

  static int? _int(Object? value) =>
      value is int ? value : int.tryParse('${value ?? ''}');

  static int _clamp(int? value, int min, int max) {
    final v = value ?? min;
    return v < min ? min : (v > max ? max : v);
  }
}

/// سطر ترجمة واحد.
class TranslationEntry {
  const TranslationEntry({
    required this.surah,
    required this.ayah,
    required this.text,
  });

  final int surah;
  final int ayah;
  final String text;
}
