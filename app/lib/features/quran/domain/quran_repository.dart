import 'entities/ayah.dart';
import 'entities/quran_reference.dart';
import 'entities/surah.dart';
import 'search_query.dart';

/// حالة قاعدة بيانات القرآن.
class QuranDbStatus {
  const QuranDbStatus({
    this.imported = false,
    this.surahCount = 0,
    this.ayahCount = 0,
    this.editions = const <String>[],
    this.sourceName,
  });

  final bool imported;
  final int surahCount;
  final int ayahCount;
  final List<String> editions;

  /// اسم المصدر المستورد (يظهر في «المصادر والتراخيص»).
  final String? sourceName;

  bool get isComplete => ayahCount >= 6236;

  /// الرقم المتوقّع لآيات القرآن (حفص).
  static const int expectedAyahCount = 6236;
  static const int expectedSurahCount = 114;
}

/// واجهة الوصول للقرآن — الـ `domain` لا يعرف Drift ولا SQLite.
abstract class QuranRepository {
  /// يراقب الحالة (يتحدّث تلقائيًا بعد الاستيراد).
  Stream<QuranDbStatus> watchStatus();

  Future<QuranDbStatus> status();

  /// كل السور مرتّبة.
  Future<List<Surah>> getSurahs();

  Stream<List<Surah>> watchSurahs();

  Future<Surah?> getSurah(int id);

  /// آيات سورة كاملة (مقيّدة بعدد للذاكرة — ٢٨٦ كحد أقصى طبيعي).
  Future<List<Ayah>> getAyahsForSurah(int surahId, {String? edition});

  /// صفحة آيات (الترقيم — `docs/05` §7: لا نحمّل سورة كاملة بلا داعٍ).
  Future<List<Ayah>> getAyahsPage({
    required int offset,
    required int limit,
    String? edition,
  });

  Future<Ayah?> getAyah(int surahId, int ayahNumber, {String? edition});

  /// آيات جزء كامل.
  Future<List<Ayah>> getAyahsForJuz(int juz, {String? edition});

  /// آيات صفحة مصحف.
  Future<List<Ayah>> getAyahsForPage(int page, {String? edition});

  /// البحث النصي الكامل (FTS5).
  Future<List<AyahMatch>> search(
    QuranSearchQuery query, {
    int limit = 50,
    String? edition,
  });

  /// يقفز إلى مرجع (جزء/صفحة/سورة) — يرجّع أول آية فيه.
  Future<Ayah?> resolveReference(QuranReference reference, {String? edition});
}
