import 'reciter.dart';

/// مسار صوتي واحد = **آية واحدة** عند قارئ واحد.
///
/// لماذا آية-بآية لا سورة كاملة؟
/// لأن المتابع يقرأ مع الصوت، والتطبيق يعلّم الآية المشغّلة الآن.
/// التشغيل آية-بآية هو ما يجعل «القارئ يتلو والقلب يتابع» ممكنًا.
/// ونحتفظ بوضع السورة كاملة ([QuranAudioMode.surahFile]) لمن يريد
/// استماعًا متصلًا بلا تتبّع.
class AudioTrack {
  const AudioTrack({
    required this.globalAyah,
    required this.surah,
    required this.ayah,
    required this.remoteUrl,
    required this.localFileName,
    this.title,
  });

  /// الرقم العالمي للآية (١ … ٦٢٣٦) — مفتاح الملف عند المصدر.
  final int globalAyah;

  final int surah;
  final int ayah;

  /// عنوان البثّ على الـ CDN.
  final String remoteUrl;

  /// اسم الملف على الجهاز — ثابت ومستقل عن اللغة.
  final String localFileName;

  /// عنوان بشري (`البقرة ٢٥٥`) — يُبنى من قاعدة البيانات، وقد يكون
  /// فارغًا إن لم تتوفر أسماء السور (حينها يكتفي الإعلام بالقارئ).
  final String? title;

  @override
  String toString() => 'AudioTrack($surah:$ayah, $globalAyah)';

  @override
  bool operator ==(Object other) =>
      other is AudioTrack &&
      other.globalAyah == globalAyah &&
      other.localFileName == localFileName;

  @override
  int get hashCode => Object.hash(globalAyah, localFileName);
}

/// كيف نبني قائمة التشغيل؟
enum QuranAudioMode {
  /// ملف لكل آية — تتبّع دقيق للآية المشغّلة.
  byAyah,

  /// ملف واحد للسورة — استماع متصل بلا تتبّع آية-بآية.
  ///
  /// ⚠️ كتالوجه **منفصل تمامًا** عن [byAyah]: ١٥٨ قارئًا في مقابل
  /// ٢٢، ولا يشترك بينهما إلا قارئ واحد (`ar.alafasy`). فلا يجوز
  /// افتراض أن قارئ السورة متاح للتنزيل آية-بآية.
  surahFile,
}

/// حالة تنزيل ملف واحد.
enum AudioDownloadState { missing, downloading, downloaded, failed }

/// وصف خطة تشغيل كاملة — يُحسب محضًا قبل لمس الشبكة أو القرص.
class QuranAudioPlan {
  const QuranAudioPlan({
    required this.reciter,
    required this.mode,
    required this.tracks,
    required this.bitrate,
  });

  final Reciter reciter;
  final QuranAudioMode mode;
  final List<AudioTrack> tracks;
  final int bitrate;

  bool get isEmpty => tracks.isEmpty;
  int get length => tracks.length;

  /// متوسط مدة الآية بالثواني — ⚠️ تقدير لتقدير الحجم فقط.
  static const int secondsPerAyahEstimate = 10;

  /// التقدير التقريبي للحجم بالبايت.
  ///
  /// ⚠️ **تقدير لا يقين**: لا نعرف مدة كل آية قبل التنزيل، فنستخدم
  /// متوسط ١٠ ثوانٍ (قريب من الواقع في التلاوة المرتّلة). الغرض
  /// **تحذير المستخدم** قبل أن يبدأ تنزيل القرآن كاملًا (~١ جيجا)،
  /// لا محاسبته بالبايت.
  int get estimatedBytes =>
      (tracks.length * secondsPerAyahEstimate * bitrate * 1000) ~/ 8;

  @override
  String toString() =>
      'QuranAudioPlan(${reciter.id}, ${mode.name}, ${tracks.length})';
}
