import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../data/database/app_database.dart';
import '../data/drift_quran_repository.dart';
import '../data/import/quran_importer.dart';
import '../domain/entities/ayah.dart';
import '../domain/entities/surah.dart';
import '../domain/quran_repository.dart';
import '../domain/search_query.dart';
import 'quran_settings_controller.dart';

/// قاعدة البيانات — تُفتح مرة واحدة وتُشارك في التطبيق كله.
///
/// `keepAlive` ضمنيًا: الـ Provider يُقرأ من شاشات كثيرة، وDrift
/// يتكفّل بإغلاقها عند التخلّص منها.
final appDatabaseProvider = Provider<AppDatabase>((ref) {
  final db = AppDatabase.defaults();
  ref.onDispose(db.close);
  return db;
});

/// المستودع — التطبيق كله يتعامل مع الواجهة، لا مع Drift.
final quranRepositoryProvider = Provider<QuranRepository>((ref) {
  return DriftQuranRepository(ref.watch(appDatabaseProvider));
});

final quranImporterProvider = Provider<QuranImporter>((ref) {
  return QuranImporter(ref.watch(appDatabaseProvider));
});

/// حالة قاعدة القرآن (مستوردة ولا لأ) — StreamProvider لتحديث الواجهة فورًا.
final quranStatusProvider = StreamProvider<QuranDbStatus>((ref) {
  return ref.watch(quranRepositoryProvider).watchStatus();
});

/// كل السور.
final surahsProvider = Provider<List<Surah>>((ref) {
  final async = ref.watch(_surahsFutureProvider);
  return async.valueOrNull ?? const <Surah>[];
});

final _surahsFutureProvider = FutureProvider<List<Surah>>((ref) {
  return ref.watch(quranRepositoryProvider).getSurahs();
});

/// سورة واحدة بالرقم.
final surahProvider = Provider.family<Surah?, int>((ref, id) {
  final all = ref.watch(surahsProvider);
  for (final s in all) {
    if (s.id == id) return s;
  }
  return null;
});

/// ⚠️ **إصدار الترجمة الفعلي** — يحترم اختيار المستخدم، ويرجع
/// لأول إصدار متاح لو لم يختر (أو لو اختار إصدارًا لم يُستورد).
///
/// لماذا `Provider` لا `StateProvider`: القيمة **مشتقة** من مصدرين
/// (اختيار المستخدم + ما هو مستورد فعلًا). تخزينها كحالة مستقلة يفتح
/// باب التناقض: اختيار محفوظ لإصدار غير موجود.
final effectiveEditionProvider = Provider<String?>((ref) {
  final chosen = ref.watch(quranSettingsProvider).edition;
  final available = ref.watch(quranStatusProvider).valueOrNull?.editions ??
      const <String>[];
  if (available.isEmpty) return null;
  if (chosen != null && available.contains(chosen)) return chosen;
  return _prefer(available, 'fr.') ?? _prefer(available, 'en.') ?? available.first;
});

String? _prefer(List<String> editions, String prefix) {
  for (final e in editions) {
    if (e.startsWith(prefix)) return e;
  }
  return null;
}

/// كل الإصدارات المستوردة (لشاشة اختيار الترجمة).
final availableEditionsProvider = Provider<List<String>>((ref) {
  return ref.watch(quranStatusProvider).valueOrNull?.editions ??
      const <String>[];
});

// ── آيات ─────────────────────────────────────────────────
/// آيات سورة كاملة.
final surahAyahsProvider =
    FutureProvider.family<List<Ayah>, int>((ref, surahId) {
  return ref
      .watch(quranRepositoryProvider)
      .getAyahsForSurah(surahId, edition: ref.watch(effectiveEditionProvider));
});

/// آيات جزء كامل (٣٠ جزء — الآن صالح بعد حسم التخطيط).
final juzAyahsProvider = FutureProvider.family<List<Ayah>, int>((ref, juz) {
  return ref
      .watch(quranRepositoryProvider)
      .getAyahsForJuz(juz, edition: ref.watch(effectiveEditionProvider));
});

/// آيات صفحة مصحف (٦٠٤ صفحة — الآن صالح بعد حسم التخطيط).
final pageAyahsProvider = FutureProvider.family<List<Ayah>, int>((ref, page) {
  return ref
      .watch(quranRepositoryProvider)
      .getAyahsForPage(page, edition: ref.watch(effectiveEditionProvider));
});

// ── بحث ──────────────────────────────────────────────────
/// نص البحث كما يكتبه المستخدم (خام).
final quranSearchQueryProvider = StateProvider<String>((ref) => '');

/// نتائج البحث.
///
/// **لماذا `family` على النص المطبَّع؟** التطبيع يجعل الاستعلامات
/// المتكافئة (`الرحمن` و `الرَّحمٰن`) تشترك في نفس النتيجة المحفوظة.
final quranSearchResultsProvider =
    FutureProvider.family<List<AyahMatch>, QuranSearchQuery>((ref, query) async {
  if (query.kind == QuranSearchKind.reference && query.reference != null) {
    final ayah = await ref
        .watch(quranRepositoryProvider)
        .resolveReference(query.reference!);
    if (ayah == null) return const <AyahMatch>[];
    final all = ref.watch(surahsProvider);
    Surah? surah;
    for (final s in all) {
      if (s.id == ayah.surahId) surah = s;
    }
    return <AyahMatch>[AyahMatch(ayah: ayah, surah: surah)];
  }
  return ref.watch(quranRepositoryProvider).search(
        query,
        limit: 50,
        edition: ref.watch(effectiveEditionProvider),
      );
});
