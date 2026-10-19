/// سورة — بيانات الفهرس فقط (لا نص).
class Surah {
  const Surah({
    required this.id,
    required this.nameAr,
    required this.nameEn,
    required this.nameFr,
    required this.translitEn,
    required this.revelation,
    required this.ayahCount,
    required this.bismillahPre,
    /// أول جزء تبدأ فيه السورة (لترتيب الفهرس).
    this.startJuz = 1,
    this.startPage = 1,
  });

  /// ١ … ١١٤
  final int id;

  final String nameAr;
  final String nameEn;
  final String nameFr;
  final String translitEn;
  final RevelationType revelation;
  final int ayahCount;

  /// هل الآية الأولى بسملة؟ (الفاتحة نعم، التوبة لا)
  final bool bismillahPre;

  final int startJuz;
  final int startPage;

  @override
  String toString() => 'Surah($id, $nameAr)';

  @override
  bool operator ==(Object other) =>
      other is Surah &&
      other.id == id &&
      other.nameAr == nameAr &&
      other.ayahCount == ayahCount &&
      other.revelation == revelation;

  @override
  int get hashCode => Object.hash(id, nameAr, ayahCount, revelation);
}

enum RevelationType {
  meccan,
  medinan;

  static RevelationType fromString(String? value) =>
      value == 'medinan' ? RevelationType.medinan : RevelationType.meccan;
}
