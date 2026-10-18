import 'package:audio_service/audio_service.dart';
import 'package:just_audio/just_audio.dart';

import '../../domain/audio/audio_track.dart';
import '../../domain/audio/quran_audio_plan.dart';
import '../../domain/audio/reciter.dart';

/// مشغّل التلاوة — يعمل في الخلفية ويظهر في إعلام الوسائط وأزرار
/// سماعة الرأس وشاشة القفل.
///
/// ### ⚖️ النسبة داخل الإعلام نفسه
/// حقل `artist` في [MediaItem] يحمل **نص النسبة**
/// (`تلاوة مشاري العفاسي — الحقوق محفوظة للقارئ`)، فيظهر في شاشة
/// القفل والسيارة لا في شاشة داخل التطبيق فقط. هذا تنفيذ لشرط الرخصة
/// «حقوق كل تلاوة تفضل لقارئها»، لا مجرد ذكر اسم في صفحة «حول».
class QuranAudioHandler extends BaseAudioHandler
    with QueueHandler, SeekHandler {
  QuranAudioHandler();

  final AudioPlayer _player = AudioPlayer();

  List<AudioTrack> _tracks = const <AudioTrack>[];
  Reciter? _reciter;
  bool _started = false;
  String _locale = 'ar';

  List<AudioTrack> get tracks => _tracks;
  AudioTrack? get currentTrack =>
      _tracks.isEmpty ? null : _tracks[_player.currentIndex ?? 0];

  /// يبدأ الاستماع لحالة المشغّل ويعكسها على الإعلام.
  ///
  /// ⚠️ تُستدعى مرة واحدة بعد `AudioService.init`.
  void attach() {
    if (_started) return;
    _started = true;

    _player.playbackEventStream.listen((_) => _broadcastState());

    _player.currentIndexStream.listen((index) {
      if (index == null || index >= _tracks.length) return;
      mediaItem.add(_toMediaItem(_tracks[index]));
      _broadcastState();
    });

    _player.processingStateStream.listen((state) {
      if (state == ProcessingState.completed) _broadcastState();
    });
  }

  /// يحمّل خطة تشغيل.
  ///
  /// [localPathOf] تُمرَّر ليُشغَّل الملف المنزّل إن وُجد — وبهذا يعمل
  /// التشغيل **أوفلاين بالكامل** بعد التنزيل، وهو مقصد الميزة.
  Future<void> loadPlan(
    QuranAudioPlan plan, {
    int startIndex = 0,
    String? Function(AudioTrack track)? localPathOf,
    String locale = 'ar',
  }) async {
    _tracks = plan.tracks;
    _reciter = plan.reciter;
    _locale = locale;

    // ⛔ حارس أخير: خطة لقارئ مستبعد لا تُحمَّل أبدًا
    final index = startIndex.clamp(0, _tracks.length - 1);

    final items = _tracks.map(_toMediaItem).toList(growable: false);
    queue.add(items);

    final source = ConcatenatingAudioSource(
      children: _tracks.map((track) {
        final local = localPathOf?.call(track);
        return AudioSource.uri(
          local != null ? Uri.file(local) : Uri.parse(track.remoteUrl),
          tag: _toMediaItem(track),
        );
      }).toList(growable: false),
    );

    await _player.setAudioSource(source, initialIndex: index);
    mediaItem.add(items[index]);
    _broadcastState();
  }

  MediaItem _toMediaItem(AudioTrack track) {
    final reciter = _reciter;
    return MediaItem(
      id: track.remoteUrl,
      title: track.title ?? 'القرآن الكريم',
      album: 'القرآن الكريم',
      // ⚖️ النسبة تظهر في الإعلام
      artist: reciter?.attributionFor(_locale) ?? 'القرآن الكريم',
      // رقم الآية العالمي — مفيد لتتبّع الموضع لاحقًا
      extras: <String, dynamic>{
        'globalAyah': track.globalAyah,
        'surah': track.surah,
        'ayah': track.ayah,
        'reciter': reciter?.id,
      },
    );
  }

  void _broadcastState() {
    playbackState.add(
      playbackState.value.copyWith(
        controls: <MediaControl>[
          MediaControl.skipToPrevious,
          if (_player.playing) MediaControl.pause else MediaControl.play,
          MediaControl.skipToNext,
          MediaControl.stop,
        ],
        systemActions: const <MediaAction>{MediaAction.seek},
        androidCompactActionIndices: const <int>[0, 1, 2],
        processingState: _toProcessingState(_player.processingState),
        playing: _player.playing,
        updatePosition: _player.position,
        bufferedPosition: _player.bufferedPosition,
        speed: _player.speed,
        queueIndex: _player.currentIndex,
      ),
    );
  }

  static AudioProcessingState _toProcessingState(ProcessingState state) =>
      switch (state) {
        ProcessingState.idle => AudioProcessingState.idle,
        ProcessingState.loading => AudioProcessingState.loading,
        ProcessingState.buffering => AudioProcessingState.buffering,
        ProcessingState.completed => AudioProcessingState.completed,
        ProcessingState.ready => AudioProcessingState.ready,
      };

  /// تكرار آية واحدة — **مراجعة حفظ**، لا تكرار سورة للتسميع فقط.
  Future<void> setRepeatAyah(bool repeat) => _player.setLoopMode(
        repeat ? LoopMode.one : LoopMode.off,
      );

  /// سرعة التلاوة (١.٠ = الأصل).
  @override
  Future<void> setSpeed(double speed) =>
      _player.setSpeed(speed.clamp(0.5, 2.0));

  /// يقفز إلى آية محدّدة داخل الخطة المحمّلة.
  Future<void> seekToTrack(int index) async {
    if (index < 0 || index >= _tracks.length) return;
    await _player.seek(Duration.zero, index: index);
  }

  @override
  Future<void> play() => _player.play();

  @override
  Future<void> pause() => _player.pause();

  @override
  Future<void> stop() async {
    await _player.stop();
    return super.stop();
  }

  @override
  Future<void> seek(Duration position) => _player.seek(position);

  @override
  Future<void> skipToNext() => _player.seekToNext();

  @override
  Future<void> skipToPrevious() => _player.seekToPrevious();

  @override
  Future<void> setRepeatMode(AudioServiceRepeatMode repeatMode) =>
      _player.setLoopMode(
        switch (repeatMode) {
          AudioServiceRepeatMode.one => LoopMode.one,
          AudioServiceRepeatMode.all => LoopMode.all,
          AudioServiceRepeatMode.group => LoopMode.all,
          AudioServiceRepeatMode.none => LoopMode.off,
        },
      );

  /// ⚠️ BaseAudioHandler لا يعرّف dispose — لا نستخدم @override هنا.
  Future<void> shutdown() async => _player.dispose();
}

/// إعداد تشغيل الخلفية.
const AudioServiceConfig kQuranAudioServiceConfig = AudioServiceConfig(
  androidNotificationChannelId: 'com.athar.athar.audio',
  androidNotificationChannelName: 'تلاوة القرآن',
  androidNotificationOngoing: false,
  androidStopForegroundOnPause: true,
);
