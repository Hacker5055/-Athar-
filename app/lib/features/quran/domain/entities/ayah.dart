import 'surah.dart';

/// آية واحدة بالنص العثماني + موقعها في المصحف.
class Ayah {
  const Ayah({
    required this.surahId,
    required this.ayah,
    required this.textUthmani,
    this.translation,
    this.juz = 1,
    this.hizb = 1,
    this.page = 1,
    this.isSajda = false,
  });

  /// ١ … ١١٤
  final int surahId;

  /// رقم الآية داخل السورة (١-based)
  final int ayah;

  /// النص بالرسم العثماني كما هو (للعرض).
  final String textUthmani;

  /// ترجمة اختيارية (مرتبطة بإصدار معيّن).
  final String? translation;

  /// ١ … ٣٠
  final int juz;

  /// ١ … ٦٠
  final int hizb;

  /// ١ … ٦٠٤ (مصحف المدينة)
  final int page;

  final bool isSajda;

  String get key => '$surahId:$ayah';

  @override
  String toString() => 'Ayah($key)';

  @override
  bool operator ==(Object other) =>
      other is Ayah &&
      other.surahId == surahId &&
      other.ayah == ayah &&
      other.textUthmani == textUthmani;

  @override
  int get hashCode => Object.hash(surahId, ayah, textUthmani);
}

/// نتيجة بحث: الآية + نص مُبرز للكلمة المطابقة.
class AyahMatch {
  const AyahMatch({
    required this.ayah,
    this.surah,
    this.snippet,
    this.rank = 0,
  });

  final Ayah ayah;

  /// السورة (تُملأ عند الحاجة لعرض الاسم).
  final Surah? surah;

  /// مقتطف قصير حول موضع المطابقة.
  final String? snippet;

  /// ترتيب النتيجة (FTS5 `rank`) — الأصغر أفضل.
  final double rank;

  String get key => ayah.key;
}
