/// عناوين ملفات التلاوة على Islamic Network CDN.
///
/// ### ⚠️ الأنماط هنا **متحقّق منها** لا مكتوبة من الذاكرة
/// مصدر التحقّق: فهرس الـ CDN المنشور نفسه
/// • `https://cdn.islamic.network/quran/info/by-ayah/info.json`
/// • `https://cdn.islamic.network/quran/info/by-surah/info.json`
///
/// وما تأكّد عمليًا:
/// • ملف الآية `128/ar.alafasy/6236.mp3` → موجود
/// • ملف وهمي `128/ar.alafasy/99999.mp3` → `AccessDenied` (غير موجود)
/// • أسماء ملفات السورة: `1.mp3` … `114.mp3` (١١٤ ملفًا = رقم السورة)
///
/// ### الفخّ الذي وقعنا فيه ثم تجنّبناه
/// الاسم ليس `{رقم السورة}{رقم الآية}` بصفر بادئ (`001001.mp3`) كما هو
/// شائع في مصادر أخرى. هنا **الرقم العالمي للآية** (١ … ٦٢٣٦).
/// الدليل: وجود `10.mp3` — ولا يمكن أن يكون `سورة ١٠ آية ٠`.
abstract final class QuranAudioSource {
  /// ملفات الآية.
  static const String ayahBase = 'https://cdn.islamic.network/quran/audio';

  /// ملفات السورة كاملة — ⚠️ مسار مختلف (`audio-surah` لا `audio`).
  static const String surahBase =
      'https://cdn.islamic.network/quran/audio-surah';

  /// معدلات البِتّ المتاحة لملفات الآية (من فهرس المصدر).
  static const List<int> ayahBitrates = <int>[32, 40, 48, 64, 128, 192, 320];

  /// معدلات البِتّ المتاحة لملفات السورة.
  static const List<int> surahBitrates = <int>[96, 128];

  static const int defaultBitrate = 128;

  /// ملف آية واحدة.
  ///
  /// [globalAyah] هو **الرقم العالمي** للآية (١ … ٦٢٣٦) ويُحسب بـ
  /// [QuranAyahIndex.globalOf] — لا `{سورة}{آية}`.
  static String ayahUrl({
    required String reciterId,
    required int globalAyah,
    int bitrate = defaultBitrate,
  }) =>
      '$ayahBase/$bitrate/$reciterId/$globalAyah.mp3';

  /// ملف سورة كاملة.
  static String surahUrl({
    required String reciterId,
    required int surah,
    int bitrate = defaultBitrate,
  }) =>
      '$surahBase/$bitrate/$reciterId/$surah.mp3';

  /// اسم الملف على الجهاز — ثابت ومستقل عن اللغة.
  static String localFileName({
    required String reciterId,
    required int globalAyah,
  }) =>
      '${reciterId}_$globalAyah.mp3';
}

/// التحويل بين (سورة، آية) و**الرقم العالمي** للآية.
///
/// الرقم العالمي هو ما يطلبه الـ CDN، فالحساب يجب أن يكون في مكان
/// واحد قابل للاختبار — خطأ بآية واحدة يعني تشغيل التلاوة الخطأ.
abstract final class QuranAyahIndex {
  /// إجمالي آيات القرآن (رواية حفص).
  static const int total = 6236;

  /// الرقم العالمي لآية (١-based). يُمرَّر عدد آيات كل سورة.
  ///
  /// [surahAyahCounts] قائمة بطول ١١٤، فهرسها `رقم السورة - ١`.
  static int globalOf(int surah, int ayah, List<int> surahAyahCounts) {
    if (surah < 1 || surah > 114) {
      throw ArgumentError.value(surah, 'surah', 'خارج ١..١١٤');
    }
    var sum = 0;
    for (var i = 0; i < surah - 1 && i < surahAyahCounts.length; i++) {
      sum += surahAyahCounts[i];
    }
    return sum + ayah;
  }

  /// عكس [globalOf] — أي (سورة، آية) يقابل هذا الرقم العالمي؟
  static ({int surah, int ayah}) fromGlobal(
    int globalAyah,
    List<int> surahAyahCounts,
  ) {
    if (globalAyah < 1 || globalAyah > total) {
      throw ArgumentError.value(globalAyah, 'globalAyah', 'خارج ١..٦٢٣٦');
    }
    var remaining = globalAyah;
    for (var i = 0; i < surahAyahCounts.length; i++) {
      if (remaining <= surahAyahCounts[i]) {
        return (surah: i + 1, ayah: remaining);
      }
      remaining -= surahAyahCounts[i];
    }
    throw ArgumentError.value(globalAyah, 'globalAyah', 'يتجاوز مجموع الآيات');
  }
}
