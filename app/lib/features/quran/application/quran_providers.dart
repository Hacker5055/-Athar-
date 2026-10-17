import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../data/database/app_database.dart';
import '../data/drift_quran_repository.dart';
import '../data/import/quran_importer.dart';
import '../domain/quran_repository.dart';

/// قاعدة البيانات — تُفتح مرة واحدة وتُشارك في التطبيق كله.
///
/// `keepAlive` لأن إغلاقها وإعادة فتحها أغلى من إبقائها مفتوحة،
/// والقرآن يُقرأ من كل شاشة تقريبًا.
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

/// إصدار الترجمة المفضّلة — `null` = بلا ترجمة.
///
/// في v1 نقرأ أول إصدار متاح؛ اختيار المستخدم يأتي مع شاشة القارئ (أسبوع ٦).
final preferredEditionProvider = Provider<String?>((ref) {
  final status = ref.watch(quranStatusProvider).valueOrNull;
  final editions = status?.editions ?? const <String>[];
  if (editions.isEmpty) return null;
  final french = editions.where((e) => e.startsWith('fr.')).toList();
  if (french.isNotEmpty) return french.first;
  final english = editions.where((e) => e.startsWith('en.')).toList();
  if (english.isNotEmpty) return english.first;
  return editions.first;
});

/// كل السور.
final surahsProvider = FutureProvider((ref) {
  return ref.watch(quranRepositoryProvider).getSurahs();
});
