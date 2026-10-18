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
///
/// `ayah` فارغ يعني «السورة كاملة»، ورقم يعني «الآن نسمع هذه الآية»
/// — وهو فرق يراه المستخدم في المصحف الذي يتابع عليه.
sealed class QuranAudioState {
    object Idle : QuranAudioState()

    data class Loading(val reciterId: String, val surah: Int, val ayah: Int?) : QuranAudioState()

    data class Playing(val reciterId: String, val surah: Int, val ayah: Int?) : QuranAudioState()

    data class Paused(val reciterId: String, val surah: Int, val ayah: Int?) : QuranAudioState()

    /// ⚠️ الرسالة للمستخدم لا رمز الخطأ: «تعذّر تشغيل التلاوة —
    /// تحقّق من الاتصال» أنفع من `what=-38`.
    data class Error(val message: String) : QuranAudioState()

    val isActive: Boolean
        get() = this is Loading || this is Playing || this is Paused

    val surahOrNull: Int?
        get() = when (this) {
            is Loading -> surah
            is Playing -> surah
            is Paused -> surah
            else -> null
        }

    val reciterIdOrNull: String?
        get() = when (this) {
            is Loading -> reciterId
            is Playing -> reciterId
            is Paused -> reciterId
            else -> null
        }
}

/// مشغّل التلاوة — [MediaPlayer] المدمج بلا أيّ مكتبة خارجية.
///
/// ⚠️ لماذا المدمج لا ExoPlayer: كل اعتماد جديد خطر على بناء لا
/// نترجمه محليًّا. وبثّ ملفات mp3 متتابعة لا يحتاج ما تضيفه
/// ExoPlayer من طوابير وتخبئة. حين نحتاج تشغيلًا في الخلفية مع
/// إشعار، ننتقل إلى Media3 حينها لا الآن.
class QuranAudioController {

    private val _state = MutableStateFlow<QuranAudioState>(QuranAudioState.Idle)
    val state: StateFlow<QuranAudioState> = _state.asStateFlow()

    private var player: MediaPlayer? = null

    /// طابور الروابط: ملف واحد للسورة، أو ملف لكل آية.
    private var queue: List<String> = emptyList()

    /// أرقام الآيات الموازية للطابور — لنعرف أيّ آية نسمع الآن.
    private var ayahNumbers: List<Int> = emptyList()

    private var index = 0

    /// المطلوب الآن (القارئ والسورة) — يُقرأ قبل أن ينتهي التحضير،
    /// فلا نعتمد على الحالة وحدها لمعرفة ماذا نُعدّ.
    private var token: String? = null

    /// تشغيل سورة، أو إيقافها إن كانت هي العاملة الآن.
    ///
    /// ⚠️ التبديل بين القرّاء يقطع التشغيل الحالي ويبدأ من جديد:
    /// الاستمرار بالصوت القديم بعد اختيار قارئ آخر كذبٌ على المستخدم.
    fun toggle(reciter: Reciter, surah: Int, ayahCount: Int) {
        val current = _state.value
        val sameTarget =
            current.reciterIdOrNull == reciter.id && current.surahOrNull == surah

        when {
            sameTarget && current is QuranAudioState.Playing -> pause()
            sameTarget && current is QuranAudioState.Paused -> resume()
            else -> play(reciter, surah, ayahCount)
        }
    }

    private fun play(reciter: Reciter, surah: Int, ayahCount: Int) {
        val urls = buildQueue(reciter, surah, ayahCount)
        if (urls.isEmpty()) {
            _state.value = QuranAudioState.Error("لا تتوفّر تلاوة لهذه السورة")
            return
        }

        releasePlayer()
        queue = urls
        ayahNumbers = if (reciter.mode == ReciterMode.AYAH) (1..urls.size).toList() else emptyList()
        index = 0
        token = "${reciter.id}#$surah"
        startAt(reciter, surah, 0)
    }

    /// ⚠️ الطابور يُبنى من وضع القارئ لا من رغبتنا: من يبثّ السورة
    /// كاملة نأخذها منه، ومن لا يبثّ إلا آية آية نصفّ آيات السورة.
    private fun buildQueue(reciter: Reciter, surah: Int, ayahCount: Int): List<String> {
        return when (reciter.mode) {
            ReciterMode.SURAH -> listOfNotNull(QuranAudio.surahUrl(reciter, surah))
            ReciterMode.AYAH -> (1..ayahCount).mapNotNull { ayah ->
                QuranAudio.ayahUrl(reciter, surah, ayah)
            }
        }
    }

    private fun startAt(reciter: Reciter, surah: Int, at: Int) {
        if (at !in queue.indices) {
            // انتهت السورة
            releasePlayer()
            _state.value = QuranAudioState.Idle
            return
        }
        index = at
        val mine = "${reciter.id}#$surah"
        val currentAyah = ayahNumbers.getOrNull(at)
        _state.value = QuranAudioState.Loading(reciter.id, surah, currentAyah)

        // ⚠️ إعادة استخدام المشغّل بعد `reset()`: إنشاء مشغّل جديد مع
        // كل آية يكدّس كائنات غير محرّرة طوال سورة من ٢٨٦ آية.
        val mp = player ?: MediaPlayer()
        player = mp
        mp.reset()
        // ⚠️ `reset()` يمسح سمات الصوت فتُعاد بعده لا قبله — تعيين
        // يسبق الـ reset لغو لا أثر له.
        mp.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build(),
        )

        try {
            mp.setDataSource(queue[at])
            mp.setOnPreparedListener {
                // ⚠️ قد يكون المستخدم بدّل السورة أثناء التحضير؛ نتحقّق
                // قبل التشغيل وإلا سمع غير ما طلب.
                if (token == mine) {
                    it.start()
                    _state.value = QuranAudioState.Playing(reciter.id, surah, currentAyah)
                }
            }
            mp.setOnCompletionListener {
                if (token == mine) startAt(reciter, surah, at + 1)
            }
            mp.setOnErrorListener { _, _, _ ->
                if (token == mine) {
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
        _state.value = QuranAudioState.Paused(current.reciterId, current.surah, current.ayah)
    }

    private fun resume() {
        val current = _state.value as? QuranAudioState.Paused ?: return
        runCatching { player?.start() }
        _state.value = QuranAudioState.Playing(current.reciterId, current.surah, current.ayah)
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
        token = null
        queue = emptyList()
        ayahNumbers = emptyList()
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
