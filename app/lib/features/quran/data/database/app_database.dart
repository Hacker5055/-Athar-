import 'package:drift/drift.dart';
import 'package:drift/native.dart';
import 'package:drift_flutter/drift_flutter.dart';

part 'app_database.g.dart';

/// سورة — بيانات الفهرس فقط.
class SurahRows extends Table {
  IntColumn get id => integer()();
  TextColumn get nameAr => text()();
  TextColumn get nameEn => text()();
  TextColumn get nameFr => text()();
  TextColumn get translitEn => text().withDefault(const Constant(''))();
  TextColumn get revelation => text().withDefault(const Constant('meccan'))();
  IntColumn get ayahCount => integer()();
  BoolColumn get bismillahPre => boolean().withDefault(const Constant(true))();
  IntColumn get startJuz => integer().withDefault(const Constant(1))();
  IntColumn get startPage => integer().withDefault(const Constant(1))();

  @override
  Set<Column<Object>>? get primaryKey => {id};
}

/// آية — النص العثماني + نسخة مطبّعة للفهرس + الموقع في المصحف.
///
/// `textNormalized` مخزّنة (لا محسوبة وقت البحث) لأن تطبيع ٦٢٣٦ آية
/// عند كل استعلام سيكون بطيئًا بلا داعٍ.
class AyahRows extends Table {
  IntColumn get surahId => integer().references(SurahRows, #id)();
  IntColumn get ayah => integer()();
  TextColumn get textUthmani => text()();
  TextColumn get textNormalized => text()();
  IntColumn get juz => integer()();
  IntColumn get hizb => integer()();
  IntColumn get page => integer()();
  BoolColumn get isSajda => boolean().withDefault(const Constant(false))();

  @override
  Set<Column<Object>>? get primaryKey => {surahId, ayah};
}

/// ترجمة آية بإصدار محدّد (`en.sahih` · `fr.hamidullah`).
class TranslationRows extends Table {
  TextColumn get edition => text()();
  IntColumn get surahId => integer()();
  IntColumn get ayah => integer()();
  TextColumn get text => text()();
  TextColumn get textNormalized => text().withDefault(const Constant(''))();

  @override
  Set<Column<Object>>? get primaryKey => {edition, surahId, ayah};
}

/// أصل المحتوى — شاشة «المصادر والتراخيص» (`docs/05` §3.6).
class ProvenanceRows extends Table {
  TextColumn get name => text()();
  TextColumn get license => text()();
  TextColumn get url => text().withDefault(const Constant(''))();
  BoolColumn get allowsPermanentStorage =>
      boolean().withDefault(const Constant(false))();
  TextColumn get author => text().nullable()();
  TextColumn get fetchedOn => text().nullable()();
  DateTimeColumn get importedAt => dateTime()();

  @override
  Set<Column<Object>>? get primaryKey => {name};
}

/// علامة مرجعية (أسبوع ٨) — الجدول موجود من الآن لأن الترحيل لاحقًا أصعب.
class BookmarkRows extends Table {
  IntColumn get surahId => integer()();
  IntColumn get ayah => integer()();
  DateTimeColumn get createdAt => dateTime()();

  @override
  Set<Column<Object>>? get primaryKey => {surahId, ayah};
}

/// ملاحظة على آية (أسبوع ٨).
class NoteRows extends Table {
  IntColumn get id => integer().autoIncrement()();
  IntColumn get surahId => integer()();
  IntColumn get ayah => integer()();
  TextColumn get body => text()();
  DateTimeColumn get updatedAt => dateTime()();
}

/// قاعدة أثر.
///
/// ### ⚠️ `app_database.g.dart` **غير مُرفق** بالمستودع
/// يُولّد بـ `dart run build_runner build --delete-conflicting-outputs`
/// (مُضمّن في `tool/build_apk.sh` وفي عمل GitHub Actions).
@DriftDatabase(
  tables: [
    SurahRows,
    AyahRows,
    TranslationRows,
    ProvenanceRows,
    BookmarkRows,
    NoteRows,
  ],
)
class AppDatabase extends _$AppDatabase {
  AppDatabase(super.e);

  /// المنفذ الافتراضي على كل المنصّات (عبر `drift_flutter`).
  AppDatabase.defaults() : super(driftDatabase(name: 'athar'));

  /// قاعدة في الذاكرة — للاختبارات.
  AppDatabase.forTesting() : super(NativeDatabase.memory());

  @override
  int get schemaVersion => 1;

  @override
  MigrationStrategy get migration => MigrationStrategy(
        onCreate: (m) async {
          await m.createAll();

          // ── فهرس البحث (FTS5) ────────────────────────
          // التطبيع يتم في دارت (راجع QuranText) ولذلك نستخدم
          // `unicode61` العادي بلا `remove_diacritics` — نتيجة
          // متطابقة على أندرويد وiOS بغض النظر عن إصدار SQLite.
          await customStatement(
            'CREATE VIRTUAL TABLE ayahs_fts USING fts5('
            'surah_id UNINDEXED, ayah UNINDEXED, text_normalized, '
            "tokenize='unicode61')",
          );
          await customStatement(
            'CREATE VIRTUAL TABLE translations_fts USING fts5('
            'edition UNINDEXED, surah_id UNINDEXED, ayah UNINDEXED, '
            "text_normalized, tokenize='unicode61')",
          );

          // ── فهارس مساعدة ─────────────────────────────
          await customStatement(
            'CREATE INDEX idx_ayahs_juz ON ayah_rows(juz)',
          );
          await customStatement(
            'CREATE INDEX idx_ayahs_page ON ayah_rows(page)',
          );
        },
        beforeOpen: (details) async {
          await customStatement('PRAGMA foreign_keys = ON');
        },
      );
}
