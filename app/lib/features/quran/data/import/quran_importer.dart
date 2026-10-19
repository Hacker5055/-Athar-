import 'package:drift/drift.dart';

import '../../domain/quran_text.dart';
import '../database/app_database.dart';
import 'import_payload.dart';

/// نتيجة عملية الاستيراد.
class ImportReport {
  const ImportReport({
    required this.surahs,
    required this.ayahs,
    required this.translationRows,
    required this.durationMs,
    this.errors = const <String>[],
  });

  final int surahs;
  final int ayahs;
  final int translationRows;
  final int durationMs;
  final List<String> errors;

  bool get ok => errors.isEmpty;

  @override
  String toString() => '$surahs سورة · $ayahs آية · '
      '$translationRows ترجمة في ${durationMs}ms'
      '${ok ? '' : ' · ${errors.length} خطأ'}';
}

/// يستورد القرآن من ملف JSON إلى قاعدة Drift + فهرس FTS5.
///
/// ### لماذا لا نرفق النص في المستودع؟
/// `docs/05` §3: كثير من المصادر تسمح بالعرض داخل التطبيق لكن **تمنع**
/// تقديمه كحزمة بيانات. ولأن أثر «أوفلاين أولًا»، نحتاج مصدرًا مرخّصًا
/// للتخزين الدائم — والقرار ده للمالك بعد مراجعة الترخيص، مش للكود.
/// راجع `docs/09-quran-data.md`.
class QuranImporter {
  const QuranImporter(this._db);

  final AppDatabase _db;

  /// يستورد حمولة كاملة في **معاملة واحدة**.
  ///
  /// المعاملة الواحدة مهمّة: ٦٢٣٦ إدخالًا منفردًا قد يستغرق دقائق،
  /// وداخل معاملة واحدة يصبح أقل من ثانية. ولو فشل أي جزء، نتراجع
  /// بالكامل ولا نترك قاعدة نصف ممتلئة (أسوأ من فارغة).
  Future<ImportReport> import(ImportPayload payload) async {
    final stopwatch = Stopwatch()..start();
    final errors = <String>[];

    try {
      await _db.transaction(() async {
        // ١) نبدأ من نظيف — الاستيراد عملية استبدال كاملة
        await _db.delete(_db.ayahRows).go();
        await _db.delete(_db.surahRows).go();
        await _db.delete(_db.translationRows).go();
        await _db.delete(_db.provenanceRows).go();
        await _db.customStatement('DELETE FROM ayahs_fts');
        await _db.customStatement('DELETE FROM translations_fts');

        // ٢) السور
        await _db.batch((batch) {
          batch.insertAll(
            _db.surahRows,
            payload.surahs.map(
              (s) => SurahRowsCompanion.insert(
                id: Value(s.id),
                nameAr: s.nameAr,
                nameEn: s.nameEn,
                nameFr: s.nameFr,
                translitEn: Value(s.translitEn),
                revelation: Value(s.revelation.name),
                ayahCount: s.ayahCount,
                bismillahPre: Value(s.bismillahPre),
                startJuz: Value(s.startJuz),
                startPage: Value(s.startPage),
              ),
            ),
          );
        });

        // ٣) الآيات + فهرسها (على دفعات لتجنّب استعلام واحد ضخم)
        const chunkSize = 500;
        for (var start = 0; start < payload.ayahs.length; start += chunkSize) {
          final end = (start + chunkSize).clamp(0, payload.ayahs.length);
          final chunk = payload.ayahs.sublist(start, end);

          await _db.batch((batch) {
            batch.insertAll(
              _db.ayahRows,
              chunk.map(
                (a) => AyahRowsCompanion.insert(
                  surahId: a.surahId,
                  ayah: a.ayah,
                  textUthmani: a.textUthmani,
                  textNormalized: QuranText.normalize(a.textUthmani),
                  juz: Value(a.juz),
                  hizb: Value(a.hizb),
                  page: Value(a.page),
                  isSajda: Value(a.isSajda),
                ),
              ),
            );
          });

          await _db.batch((batch) {
            for (final a in chunk) {
              batch.customStatement(
                'INSERT INTO ayahs_fts(surah_id, ayah, text_normalized) '
                'VALUES (?, ?, ?)',
                [
                  a.surahId,
                  a.ayah,
                  QuranText.normalize(a.textUthmani),
                ],
              );
            }
          });
        }

        // ٤) الترجمات + فهرسها
        var translationRows = 0;
        for (final entry in payload.translations.entries) {
          final edition = entry.key;
          final rows = entry.value;
          translationRows += rows.length;

          const tChunk = 500;
          for (var start = 0; start < rows.length; start += tChunk) {
            final end = (start + tChunk).clamp(0, rows.length);
            final chunk = rows.sublist(start, end);

            await _db.batch((batch) {
              batch.insertAll(
                _db.translationRows,
                chunk.map(
                  (t) => TranslationRowsCompanion.insert(
                    edition: edition,
                    surahId: t.surah,
                    ayah: t.ayah,
                    content: t.text,
                    textNormalized: Value(QuranText.normalizeLatin(t.text)),
                  ),
                ),
              );
            });

            await _db.batch((batch) {
              for (final t in chunk) {
                batch.customStatement(
                  'INSERT INTO translations_fts'
                  '(edition, surah_id, ayah, text_normalized) '
                  'VALUES (?, ?, ?, ?)',
                  [
                    edition,
                    t.surah,
                    t.ayah,
                    QuranText.normalizeLatin(t.text),
                  ],
                );
              }
            });
          }
        }

        // ٥) الأصل (لشاشة «المصادر والتراخيص»)
        await _db
            .into(_db.provenanceRows)
            .insertOnConflictUpdate(ProvenanceRowsCompanion.insert(
              name: payload.provenance.name,
              license: payload.provenance.license,
              url: Value(payload.provenance.url),
              allowsPermanentStorage:
                  Value(payload.provenance.allowsPermanentStorage),
              author: Value(payload.provenance.author),
              fetchedOn: Value(payload.provenance.fetchedOn),
              importedAt: DateTime.now(),
            ));
      });
    } catch (error) {
      errors.add('فشل الاستيراد: $error');
    }

    stopwatch.stop();
    return ImportReport(
      surahs: payload.surahs.length,
      ayahs: payload.ayahs.length,
      translationRows: payload.translations.values
          .fold<int>(0, (sum, list) => sum + list.length),
      durationMs: stopwatch.elapsedMilliseconds,
      errors: errors,
    );
  }

  /// يمسح كل بيانات القرآن (قبل إعادة الاستيراد).
  Future<void> clear() async {
    await _db.transaction(() async {
      await _db.delete(_db.ayahRows).go();
      await _db.delete(_db.surahRows).go();
      await _db.delete(_db.translationRows).go();
      await _db.delete(_db.provenanceRows).go();
      await _db.customStatement('DELETE FROM ayahs_fts');
      await _db.customStatement('DELETE FROM translations_fts');
    });
  }
}
