package com.athar.athar.tasbih

import com.athar.athar.core.ArabicDigits

/// نطق العدد (C10) — **المنطق النقيّ**، بلا `TextToSpeech`.
///
/// ⚠️ لماذا نطقُ **أرقامًا عربية**؟ لأنّ النطق لاتينيّ في تطبيق
/// عربي يُقال «three» وسط التسبيح. والأرقام العربية يقرؤها
/// المحرّك العربيّ كما تُكتب.
object TasbihSpeech {

    /// نصٌّ يُنطق.
    fun text(count: Int): String = ArabicDigits.of(count)

    /// ⚠️ **العدد المنطوق ≠ العدّاد المعروض دائمًا**: حين تُتمّ
    /// دورة يعود العدّاد إلى صفر — ومن يسمع «صفر» يظنّ أن عدّه
    /// ضاع. فننطق **الهدف** في تلك اللحظة: «ثلاث وثلاثون».
    fun spokenCount(result: TasbihCounter.Tap, target: Int): Int =
        if (result.completed) target.coerceAtLeast(1) else result.state.count

    /// هل يُنطق أصلًا؟ المُطفأ لا ينطق، والعدّ صفرٌ لا يُنطق
    /// (لأنّ صفرًا يُقال «صفر» وهي لحظةُ فراغٍ لا ذكر).
    fun shouldSpeak(enabled: Boolean, count: Int): Boolean =
        enabled && count > 0
}
