import 'audio_track.dart';
import 'quran_audio_source.dart' show QuranAudioSource, QuranAyahIndex;
import 'reciter.dart';

/// يبني خطة تشغيل التلاوة — **محض بالكامل**.
///
/// لماذا محض؟ لأن خطأ بآية واحدة هنا يعني تشغيل التلاوة الخطأ، وهذا
/// النوع من الأخطاء لا يُكتشف بالنظر إلى الشاشة بل بالاختبار. فكل
/// المنطق الحسابي هنا، والشبكة والقرص في طبقة أخرى.
///
/// ⚠️ يستقبل [Reciter] **جاهزًا** لا معرّفه: الكتالوج في طبقة البيانات،
/// والنطاق لا يستورد منها. على المستدعي أن يمرّر قارئًا من
/// `offerableReciters` — أي اجتاز سياسة الرخص ([AudioLicensePolicy]).
abstract final class QuranAudioPlanner {
  /// يبني خطة آية-بآية من نطاق **أرقام عالمية** (شامل الطرفين).
  ///
  /// [fromGlobalAyah] و [toGlobalAyah] داخل ١ … ٦٢٣٦.
  /// [surahAyahCounts] عدد آيات كل سورة (١١٤ مدخلًا).
  /// [surahNames] أسماء السور إن توفرت (مفتاح = رقم السورة).
  static QuranAudioPlan byAyahRange({
    required Reciter reciter,
    required int fromGlobalAyah,
    required int toGlobalAyah,
    required List<int> surahAyahCounts,
    Map<int, String>? surahNames,
    int bitrate = QuranAudioSource.defaultBitrate,
  }) {
    _checkGlobal(fromGlobalAyah, 'fromGlobalAyah');
    _checkGlobal(toGlobalAyah, 'toGlobalAyah');
    if (toGlobalAyah < fromGlobalAyah) {
      throw ArgumentError.value(
        toGlobalAyah,
        'toGlobalAyah',
        'أصغر من fromGlobalAyah',
      );
    }

    final tracks = <AudioTrack>[];
    for (var g = fromGlobalAyah; g <= toGlobalAyah; g++) {
      final place = QuranAyahIndex.fromGlobal(g, surahAyahCounts);
      final name = surahNames?[place.surah];
      tracks.add(
        AudioTrack(
          globalAyah: g,
          surah: place.surah,
          ayah: place.ayah,
          remoteUrl: QuranAudioSource.ayahUrl(
            reciterId: reciter.id,
            globalAyah: g,
            bitrate: bitrate,
          ),
          localFileName: QuranAudioSource.localFileName(
            reciterId: reciter.id,
            globalAyah: g,
          ),
          title: name == null ? null : '$name ${place.ayah}',
        ),
      );
    }
    return QuranAudioPlan(
      reciter: reciter,
      mode: QuranAudioMode.byAyah,
      tracks: List.unmodifiable(tracks),
      bitrate: bitrate,
    );
  }

  /// خطة لسورة كاملة.
  static QuranAudioPlan surah({
    required Reciter reciter,
    required int surah,
    required List<int> surahAyahCounts,
    Map<int, String>? surahNames,
    int bitrate = QuranAudioSource.defaultBitrate,
  }) {
    final from = QuranAyahIndex.globalOf(surah, 1, surahAyahCounts);
    final to = from + surahAyahCounts[surah - 1] - 1;
    return byAyahRange(
      reciter: reciter,
      fromGlobalAyah: from,
      toGlobalAyah: to,
      surahAyahCounts: surahAyahCounts,
      surahNames: surahNames,
      bitrate: bitrate,
    );
  }

  /// خطة لآية واحدة — لزر «اسمع هذه الآية».
  static QuranAudioPlan singleAyah({
    required Reciter reciter,
    required int surah,
    required int ayah,
    required List<int> surahAyahCounts,
    Map<int, String>? surahNames,
    int bitrate = QuranAudioSource.defaultBitrate,
  }) {
    final g = QuranAyahIndex.globalOf(surah, ayah, surahAyahCounts);
    return byAyahRange(
      reciter: reciter,
      fromGlobalAyah: g,
      toGlobalAyah: g,
      surahAyahCounts: surahAyahCounts,
      surahNames: surahNames,
      bitrate: bitrate,
    );
  }

  /// خطة ملف سورة كاملة (وضع [QuranAudioMode.surahFile]).
  ///
  /// ⚠️ لا تخلط هذا بـ [surah]: هذا **ملف واحد متصل** بلا تتبّع آية
  /// بآية، وكتالوج قرّائه مختلف كما هو مذكور في [QuranAudioMode].
  static QuranAudioPlan surahFile({
    required Reciter reciter,
    required int surah,
    Map<int, String>? surahNames,
    int bitrate = QuranAudioSource.defaultBitrate,
  }) =>
      QuranAudioPlan(
        reciter: reciter,
        mode: QuranAudioMode.surahFile,
        tracks: List.unmodifiable(<AudioTrack>[
          AudioTrack(
            globalAyah: 0,
            surah: surah,
            ayah: 0,
            remoteUrl: QuranAudioSource.surahUrl(
              reciterId: reciter.id,
              surah: surah,
              bitrate: bitrate,
            ),
            localFileName: '${reciter.id}_surah_$surah.mp3',
            title: surahNames?[surah],
          ),
        ]),
        bitrate: bitrate,
      );

  static void _checkGlobal(int value, String name) {
    if (value < 1 || value > QuranAyahIndex.total) {
      throw ArgumentError.value(value, name, 'خارج ١..٦٢٣٦');
    }
  }
}
