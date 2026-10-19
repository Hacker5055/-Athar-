package com.athar.athar.tasbih

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/// نطق العدد (C10) — غلافٌ حول `TextToSpeech`.
///
/// ⚠️ **عربيّ أو صمت**: إن لم يكن في الجهاز صوتٌ عربيّ، نكتفي
/// بصوت النظام بلغته — ولا نُنطق إن تعذّر كلّ شيء. فالنطق
/// المعطوب أسوأ من صمت.
///
/// ⚠️ و`QUEUE_FLUSH`: آخرُ عددٍ هو المهمّ، فلا تتراكم الأعداد
/// في طابورٍ ينطق «ثلاثة… اثنان… واحد» بعد انقضاء العدّ.
class Speaker(context: Context) {

    private var engine: TextToSpeech? = null
    private var ready = false
    private var failed = false

    init {
        val appContext = context.applicationContext
        engine = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val tts = engine
                if (tts != null) {
                    val arabic = tts.setLanguage(Locale("ar"))
                    if (arabic == TextToSpeech.LANG_MISSING_DATA ||
                        arabic == TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        // ⚠️ لا صوت عربيّ: صوتُ النظام بلغته، ولا شيءَ إن
                        // لم يوجد صوتٌ أصلًا.
                        tts.setLanguage(Locale.getDefault())
                    }
                    ready = true
                }
            } else {
                failed = true
            }
        }
    }

    /// ينطق نصًّا — إن كان الصوت جاهزًا.
    fun speak(text: String) {
        if (!ready || failed) return
        val tts = engine ?: return
        runCatching {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
        }
    }

    /// يُسكِت النطق الجاري.
    fun stop() {
        val tts = engine ?: return
        runCatching { tts.stop() }
    }

    /// يُحرّر الصوت — **واجب**: محرّك النطق موردٌ ثقيل، وتركه
    /// معلّقًا يُبقي العملية حيّة بلا داعٍ.
    fun shutdown() {
        ready = false
        val tts = engine
        engine = null
        runCatching {
            tts?.stop()
            tts?.shutdown()
        }
    }

    private companion object {
        const val UTTERANCE_ID = "athar-tasbih"
    }
}
