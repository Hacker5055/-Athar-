import 'dart:io';

import 'package:dio/dio.dart';
import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';

import '../../domain/audio/audio_track.dart';
import '../../domain/audio/reciter.dart';

/// مخزن ملفات التلاوة على الجهاز.
///
/// ### ⚠️ لماذا مجلد المستندات لا مجلد مؤقت؟
/// لأن التنزيل قصدُه **الاستماع بلا إنترنت** — في الصلاة، في السفر،
/// في مترو. لو حفظناه في `getTemporaryDirectory` لكان النظام في حلّ
/// من حذفه وقت ضغط المساحة، ولفقد المستخدم تنزيله بلا سبب يفهمه.
///
/// ### ⚠️ لماذا لا نستأذن تخزينًا؟
/// لا نستخدم أي صلاحية. المجلد داخل حيز التطبيق الخاص، يمحوه النظام
/// تلقائيًا مع إلغاء التثبيت. نفس موقفنا من `MANAGE_EXTERNAL_STORAGE`
/// في الأسبوع ٦: لا نطلب صلاحية نستطيع الاستغناء عنها.
class QuranAudioStore {
  QuranAudioStore({Dio? dio}) : _dio = dio ?? Dio();

  final Dio _dio;

  /// اسم المجلد الجذري داخل مستندات التطبيق.
  static const String rootFolderName = 'athar_audio';

  Future<Directory> _root() async {
    final docs = await getApplicationDocumentsDirectory();
    return Directory(p.join(docs.path, rootFolderName));
  }

  Future<Directory> reciterDir(Reciter reciter) async {
    final dir = Directory(p.join((await _root()).path, reciter.id));
    if (!await dir.exists()) await dir.create(recursive: true);
    return dir;
  }

  Future<String> localPathFor(Reciter reciter, AudioTrack track) async =>
      p.join((await reciterDir(reciter)).path, track.localFileName);

  /// هل الملف منزّل **وكامل**؟
  ///
  /// ⚠️ وجود الملف لا يكفي — التنزيل المقطوع يترك ملفًا ناقصًا يصمت
  /// في منتصف الآية. لذلك ننزّل إلى `.part` ثم ننقل، ولا يُعتبر
  /// الملف منزّلًا إلا بعد اكتمال النقل.
  Future<bool> isDownloaded(Reciter reciter, AudioTrack track) async {
    final file = File(await localPathFor(reciter, track));
    return file.exists();
  }

  Future<void> download(
    Reciter reciter,
    AudioTrack track, {
    CancelToken? cancelToken,
    void Function(int received, int total)? onProgress,
  }) async {
    final target = File(await localPathFor(reciter, track));
    if (await target.exists()) return;

    final temp = File('${target.path}.part');
    try {
      await _dio.download(
        track.remoteUrl,
        temp.path,
        cancelToken: cancelToken,
        onReceiveProgress: onProgress,
      );
      // النقل الذري: إما ملف كامل أو لا ملف
      await temp.rename(target.path);
    } on DioException catch (_) {
      if (await temp.exists()) await temp.delete();
      rethrow;
    }
  }

  /// ينزّل خطة كاملة — يتوقف عند أول فشل (شبكة أو إلغاء).
  Future<int> downloadPlan(
    Reciter reciter,
    List<AudioTrack> tracks, {
    CancelToken? cancelToken,
    void Function(int done, int total)? onProgress,
  }) async {
    var done = 0;
    for (final track in tracks) {
      await download(reciter, track, cancelToken: cancelToken);
      done++;
      onProgress?.call(done, tracks.length);
    }
    return done;
  }

  /// ⚠️ يمحو كل تلاوة قارئ — مطلوب شرعًا عند الانسحاب
  /// ([AudioLicenseStatus.withdrawn]): إخفاء القارئ من الواجهة لا
  /// يكفي إن بقيت ملفاته على الجهاز.
  Future<void> deleteReciter(Reciter reciter) async {
    final dir = Directory(p.join((await _root()).path, reciter.id));
    if (await dir.exists()) await dir.delete(recursive: true);
  }

  Future<int> sizeOfReciter(Reciter reciter) async {
    final dir = Directory(p.join((await _root()).path, reciter.id));
    if (!await dir.exists()) return 0;
    var total = 0;
    await for (final entity
        in dir.list(recursive: true, followLinks: false)) {
      if (entity is File) total += await entity.length();
    }
    return total;
  }

  /// يمحو كل ملفات `.part` المتروكة من تنزيلات مقطوعة.
  Future<int> clearPartialDownloads() async {
    final root = await _root();
    if (!await root.exists()) return 0;
    var removed = 0;
    await for (final entity
        in root.list(recursive: true, followLinks: false)) {
      if (entity is File && entity.path.endsWith('.part')) {
        await entity.delete();
        removed++;
      }
    }
    return removed;
  }
}
