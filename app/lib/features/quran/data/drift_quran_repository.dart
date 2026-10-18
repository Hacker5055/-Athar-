import 'package:drift/drift.dart';

import '../domain/entities/ayah.dart';
import '../domain/entities/quran_reference.dart';
import '../domain/entities/surah.dart';
import '../domain/quran_repository.dart';
import '../domain/search_query.dart';
import 'database/app_database.dart';

/// تنفيذ [QuranRepository] فوق Drift.
///
/// ⚠️ **أسماء مولّدة يجب التحقّق منها بعد أول `build_runner`:**
/// | جدول | صف | مرافق |
/// |---|---|---|
/// | `SurahRows` | `SurahRow` | `SurahRowsCompanion` |
/// | `AyahRows` | `AyahRow` | `AyahRowsCompanion` |
/// | `TranslationRows` | `TranslationRow` | `TranslationRowsCompanion` |
/// | `ProvenanceRows` | `ProvenanceRow` | `ProvenanceRowsCompanion` |
/// لو اختلفت، بدّلها هنا فقط — هذا الملف هو نقطة التبديل الوحيدة.
///
/// **كل العدود بـ SQL خام** (`SELECT COUNT(*) AS c`) لا بـ API العدّ
/// الجاهز — عشان نضمن نفس السلوك على كل إصدار Drift.
class DriftQuranRepository implements QuranRepository {
  const DriftQuranRepository(this._db);

  final AppDatabase _db;

  // ── الحالة ─────────────────────────────────────────────
  @override
  Stream<QuranDbStatus> watchStatus() =>
      _watchCount('ayah_rows').asyncMap(_status);

  @override
  Future<QuranDbStatus> status() => _status(await _countOf('ayah_rows'));

  /// عدد الصفوف بـ SQL خام (لا API العدّ الجاهز) — نفس السلوك على كل إصدار.
  Stream<int> _watchCount(String table) => _db
      .customSelect('SELECT COUNT(*) AS c FROM $table')
      .watchSingle()
      .map((row) => row.read<int>('c'));

  Future<int> _countOf(String table) async {
    final rows =
        await _db.customSelect('SELECT COUNT(*) AS c FROM $table').get();
    return rows.first.read<int>('c');
  }

  Future<QuranDbStatus> _status(int ayahCount) async {
    final surahCount = await _count('surah_rows').getSingle();
    final editions = await _db
        .customSelect(
          'SELECT DISTINCT edition FROM translation_rows ORDER BY edition',
          readsFrom: {_db.translationRows},
        )
        .get();
    final sources = await _db.select(_db.provenanceRows).get();

    return QuranDbStatus(
      imported: ayahCount > 0,
      surahCount: surahCount,
      ayahCount: ayahCount,
      editions: editions.map((r) => r.read<String>('edition')).toList(),
      sourceName: sources.isEmpty ? null : sources.first.name,
    );
  }

  // ── السور ──────────────────────────────────────────────
  @override
  Stream<List<Surah>> watchSurahs() => _db.select(_db.surahRows).watch();

  @override
  Future<List<Surah>> getSurahs() => _db.select(_db.surahRows).get();

  @override
  Future<Surah?> getSurah(int id) =>
      (_db.select(_db.surahRows)..where((t) => t.id.equals(id)))
          .getSingleOrNull();

  // ── الآيات ─────────────────────────────────────────────
  @override
  Future<List<Ayah>> getAyahsForSurah(int surahId, {String? edition}) =>
      _ayahsWhere((t) => t.surahId.equals(surahId), edition: edition);

  @override
  Future<List<Ayah>> getAyahsPage({
    required int offset,
    required int limit,
    String? edition,
  }) async {
    final query = _db.select(_db.ayahRows)
      ..orderBy([
        (t) => OrderingTerm.asc(t.surahId),
        (t) => OrderingTerm.asc(t.ayah),
      ])
      ..limit(limit, offset: offset);
    return _withTranslations(await query.get(), edition: edition);
  }

  @override
  Future<Ayah?> getAyah(int surahId, int ayahNumber, {String? edition}) async {
    final rows = await _ayahsWhere(
      (t) => t.surahId.equals(surahId) & t.ayah.equals(ayahNumber),
      edition: edition,
      limit: 1,
    );
    return rows.isEmpty ? null : rows.first;
  }

  @override
  Future<List<Ayah>> getAyahsForJuz(int juz, {String? edition}) =>
      _ayahsWhere((t) => t.juz.equals(juz), edition: edition);

  @override
  Future<List<Ayah>> getAyahsForPage(int page, {String? edition}) =>
      _ayahsWhere((t) => t.page.equals(page), edition: edition);

  Future<List<Ayah>> _ayahsWhere(
    Expression<bool> Function(AyahRows t) predicate, {
    String? edition,
    int? limit,
  }) async {
    final query = _db.select(_db.ayahRows)
      ..where(predicate)
      ..orderBy([
        (t) => OrderingTerm.asc(t.surahId),
        (t) => OrderingTerm.asc(t.ayah),
      ]);
    if (limit != null) query.limit(limit);
    return _withTranslations(await query.get(), edition: edition);
  }

  /// يضيف الترجمة في استعلام **واحد** — لا N استعلام.
  Future<List<Ayah>> _withTranslations(
    List<AyahRow> rows, {
    String? edition,
  }) async {
    if (rows.isEmpty || edition == null) {
      return rows.map(_toAyah).toList(growable: false);
    }
    final keys = rows.map((r) => '${r.surahId}:${r.ayah}').toSet();
    // نقيّد بالسور المطلوبة فقط — جلب كل الترجمة (٦٢٣٦ سطرًا) بلا داعٍ
    final surahIds = rows.map((r) => r.surahId).toSet().toList();
    final translations = await (_db.select(_db.translationRows)
          ..where(
            (t) => t.edition.equals(edition) & t.surahId.isIn(surahIds),
          ))
        .get();
    final byKey = <String, String>{
      for (final t in translations)
        if (keys.contains('${t.surahId}:${t.ayah}'))
          '${t.surahId}:${t.ayah}': t.content,
    };
    return rows
        .map((r) => _toAyah(r, translation: byKey['${r.surahId}:${r.ayah}']))
        .toList(growable: false);
  }

  // ── البحث ──────────────────────────────────────────────
  @override
  Future<List<AyahMatch>> search(
    QuranSearchQuery query, {
    int limit = 50,
    String? edition,
  }) async {
    if (!query.kind.needsFullTextSearch) return const <AyahMatch>[];

    final match = query.matchExpression;
    if (match.isEmpty) return const <AyahMatch>[];

    final results = query.kind == QuranSearchKind.arabic
        ? await _searchAyahs(match, limit)
        : await _searchTranslations(match, limit, edition);

    if (results.isEmpty) return const <AyahMatch>[];

    final surahs = await getSurahs();
    final byId = <int, Surah>{for (final s in surahs) s.id: s};
    return results
        .map((r) => AyahMatch(ayah: r, surah: byId[r.surahId]))
        .toList(growable: false);
  }

  static const String _ayahColumns =
      'a.surah_id AS surah_id, a.ayah AS ayah, '
      'a.text_uthmani AS text_uthmani, a.juz AS juz, '
      'a.hizb AS hizb, a.page AS page, a.is_sajda AS is_sajda';

  Future<List<Ayah>> _searchAyahs(String match, int limit) async {
    final rows = await _db
        .customSelect(
          'SELECT $_ayahColumns '
          'FROM ayahs_fts f '
          'JOIN ayah_rows a '
          '  ON a.surah_id = f.surah_id AND a.ayah = f.ayah '
          'WHERE ayahs_fts MATCH ? '
          'ORDER BY bm25(ayahs_fts) '
          'LIMIT ?',
          variables: [
            Variable.withString(match),
            Variable.withInt(limit),
          ],
          readsFrom: {_db.ayahRows},
        )
        .get();
    return rows.map(_fromRow).toList(growable: false);
  }

  Future<List<Ayah>> _searchTranslations(
    String match,
    int limit,
    String? edition,
  ) async {
    final sql = StringBuffer(
      'SELECT $_ayahColumns, t.text AS translation '
      'FROM translations_fts f '
      'JOIN ayah_rows a '
      '  ON a.surah_id = f.surah_id AND a.ayah = f.ayah '
      'JOIN translation_rows t '
      '  ON t.surah_id = f.surah_id AND t.ayah = f.ayah '
      '  AND t.edition = f.edition '
      'WHERE translations_fts MATCH ? ',
    );
    final variables = <Variable>[Variable.withString(match)];
    if (edition != null) {
      sql.write('AND f.edition = ? ');
      variables.add(Variable.withString(edition));
    }
    sql.write('ORDER BY bm25(translations_fts) LIMIT ?');
    variables.add(Variable.withInt(limit));

    final rows = await _db
        .customSelect(
          sql.toString(),
          variables: variables,
          readsFrom: {_db.ayahRows, _db.translationRows},
        )
        .get();
    return rows
        .map((r) => _fromRow(r, translation: r.read<String?>('translation')))
        .toList(growable: false);
  }

  // ── المراجع ────────────────────────────────────────────
  @override
  Future<Ayah?> resolveReference(
    QuranReference reference, {
    String? edition,
  }) =>
      switch (reference) {
        AyahReference(:final surah, :final ayah) =>
          getAyah(surah, ayah, edition: edition),
        SurahReference(:final surah) => getAyah(surah, 1, edition: edition),
        JuzReference(:final juz) =>
          _firstOf((t) => t.juz.equals(juz), edition: edition),
        PageReference(:final page) =>
          _firstOf((t) => t.page.equals(page), edition: edition),
        HizbReference(:final hizb) =>
          _firstOf((t) => t.hizb.equals(hizb), edition: edition),
      };

  Future<Ayah?> _firstOf(
    Expression<bool> Function(AyahRows t) predicate, {
    String? edition,
  }) async {
    final rows = await _ayahsWhere(predicate, edition: edition, limit: 1);
    return rows.isEmpty ? null : rows.first;
  }

  // ── تحويل الصفوف ───────────────────────────────────────
  Ayah _toAyah(AyahRow row, {String? translation}) => Ayah(
        surahId: row.surahId,
        ayah: row.ayah,
        textUthmani: row.textUthmani,
        translation: translation,
        juz: row.juz,
        hizb: row.hizb,
        page: row.page,
        isSajda: row.isSajda,
      );

  Ayah _fromRow(QueryRow row, {String? translation}) => Ayah(
        surahId: row.read<int>('surah_id'),
        ayah: row.read<int>('ayah'),
        textUthmani: row.read<String>('text_uthmani'),
        translation: translation,
        juz: row.read<int>('juz'),
        hizb: row.read<int>('hizb'),
        page: row.read<int>('page'),
        isSajda: row.read<int>('is_sajda') == 1,
      );
}
