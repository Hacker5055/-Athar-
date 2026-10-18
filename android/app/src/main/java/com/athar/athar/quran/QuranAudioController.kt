package com.athar.athar.quran

import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/// ما يعرضه المشغّل — حالة واحدة صريحة لا أعلام متفرّقة.
///
/// ⚠️ `reciterId` و`surah` محفوظان في كل حالة: الواجهة تعرف ماذا
/// يعمل الآن فتُظهر «إيقاف» بدل «تشغيل» حين يعود المستخدم للشاشة،
/// ولا تعتمد على متغيّر منفصل قد يفقد التزامن.
sealed class QuranAudioState {
    object Idle : QuranAudioState()

    data class Loading(val reciterId: String, val surah: Int) : QuranAudioState()

    data class Playing(val reciterId: String, val surah: Int) : QuranAudioState()

    data class Paused(val reciterId: String, val surah: Int) : QuranAudioState()

    /// ⚠️ الرسالة للمستخدم لا رمز الخطأ: «تعذّر تشغيل التلاوة —
    /// تحقّق من الاتصال» أنفع من `what=-38`.
    data class Error(val message: String) : QuranAudioState()

    val isActive: Boolean
        get() = this is Loading || this is Playing || this is Paused
}

/// مشغّل تلاوة السور — [MediaPlayer] المدمج بلا أيّ مكتبة خارجية.
///
/// ⚠️ لماذا المدمج لا ExoPlayer: كل اعتماد جديد خطر على بناء لا
/// نترجمه محليًّا. وبثّ ملف mp3 واحد لا يحتاج ما تضيفه ExoPlayer
/// من طوابير وتخبئة. حين نحتاج تشغيلًا في الخلفية مع إشعار،
/// ننتقل إلى Media3 حينها لا الآن.
class QuranAudioController {

    private val _state = MutableStateFlow<QuranAudioState>(QuranAudioState.Idle)
    val state: StateFlow<QuranAudioState> = _state.asStateFlow()

    private var player: MediaPlayer? = null

    /// السورة المطلوبة الآن — تُقرأ قبل أن ينتهي التحضير، فلا نعتمد
    /// على الحالة وحدها لمعرفة ماذا نُعدّ.
    private var pending: Pair<String, Int>? = null

    /// تشغيل سورة، أو إيقافها إن كانت هي العاملة الآن.
    ///
    /// ⚠️ التبديل بين القرّاء يقطع التشغيل الحالي ويبدأ من جديد:
    /// الاستمرار بالصوت القديم بعد اختيار قارئ آخر كذبٌ على المستخدم.
    fun toggle(reciter: Reciter, surah: Int) {
        val current = _state.value
        val sameSurah = when (current) {
            is QuranAudioState.Loading -> current.reciterId == reciter.id && current.surah == surah
            is QuranAudioState.Playing -> current.reciterId == reciter.id && current.surah == surah
            is QuranAudioState.Paused -> current.reciterId == reciter.id && current.surah == surah
            else -> false
        }

        when {
            sameSurah && current is QuranAudioState.Playing -> pause()
            sameSurah && current is QuranAudioState.Paused -> resume()
            else -> play(reciter, surah)
        }
    }

    private fun play(reciter: Reciter, surah: Int) {
        val url = QuranAudio.surahUrl(reciter, surah)
        if (url == null) {
            _state.value = QuranAudioState.Error("رقم سورة غير صحيح")
            return
        }

        releasePlayer()
        pending = reciter.id to surah
        _state.value = QuranAudioState.Loading(reciter.id, surah)

        val mp = MediaPlayer()
        player = mp
        try {
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build(),
            )
            mp.setDataSource(url)
            mp.setOnPreparedListener {
                // ⚠️ قد يكون المستخدم قد بدّل السورة أثناء التحضير؛
                // نتحقّق قبل التشغيل وإلا سمع غير ما طلب.
                if (pending == reciter.id to surah) {
                    it.start()
                    _state.value = QuranAudioState.Playing(reciter.id, surah)
                }
            }
            mp.setOnCompletionListener {
                if (pending == reciter.id to surah) {
                    _state.value = QuranAudioState.Idle
                }
            }
            mp.setOnErrorListener { _, _, _ ->
                if (pending == reciter.id to surah) {
                    _state.value =
                        QuranAudioState.Error("تعذّر تشغيل التلاوة — تحقّق من الاتصال")
                }
                true
            }
            // ⚠️ غير متزامن: التحضير المتزامن يجمّد واجهة القراءة،
            // والملف على الشبكة لا في الذاكرة.
            mp.prepareAsync()
        } catch (e: Exception) {
            releasePlayer()
            _state.value = QuranAudioState.Error("تعذّر تشغيل التلاوة — تحقّق من الاتصال")
        }
    }

    private fun pause() {
        val current = _state.value as? QuranAudioState.Playing ?: return
        runCatching { player?.pause() }
        _state.value = QuranAudioState.Paused(current.reciterId, current.surah)
    }

    private fun resume() {
        val current = _state.value as? QuranAudioState.Paused ?: return
        runCatching { player?.start() }
        _state.value = QuranAudioState.Playing(current.reciterId, current.surah)
    }

    /// إيقاف تامّ: يُرجع الحالة إلى [QuranAudioState.Idle].
    fun stop() {
        releasePlayer()
        _state.value = QuranAudioState.Idle
    }

    /// ⚠️ تحرير الموارد واجب: ترك [MediaPlayer] حيًّا بعد ترك الشاشة
    /// يترك مشغّلًا يُصرَف من البطارية ولا يراه أحد.
    fun release() {
        releasePlayer()
        _state.value = QuranAudioState.Idle
    }

    private fun releasePlayer() {
        pending = null
        try {
            player?.apply {
                if (isPlaying) stop()
            }
        } catch (e: IllegalStateException) {
            // ⚠️ MediaPlayer يرمي إن أُوقف في حالة غير صالحة؛ الخطأ
            // هنا لا يعني فشلًا يرى المستخدم أثره.
        }
        try {
            player?.release()
        } catch (e: Exception) {
            // لا شيء: الإفلات متسامح بطبعه
        }
        player = null
    }
}
