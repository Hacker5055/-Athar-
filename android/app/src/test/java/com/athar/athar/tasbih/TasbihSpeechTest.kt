package com.athar.athar.tasbih

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات نطق العدد (C10).
///
/// ⚠️ الفحص على **العدد المنطوق لا المعروض**: عند إتمام دورة
/// يعود العدّاد إلى صفر، ومن يسمع «صفر» يظنّ أن عدّه ضاع.
class TasbihSpeechTest {

    private fun tap(count: Int, completed: Boolean) = TasbihCounter.Tap(
        state = TasbihState(count = count, rounds = if (completed) 1 else 0, lifetime = 1),
        completed = completed,
    )

    @Test
    fun theSpokenTextIsArabicDigits() {
        // ⚠️ لا أرقام لاتينية: النطقُ لاتينيّ وسط التسبيح عربيّ.
        assertEquals("٣٣", TasbihSpeech.text(33))
        assertEquals("١٠٠", TasbihSpeech.text(100))
        assertEquals("٠", TasbihSpeech.text(0))
    }

    @Test
    fun nothingIsSpokenWhenItIsTurnedOff() {
        assertFalse(TasbihSpeech.shouldSpeak(enabled = false, count = 5))
    }

    @Test
    fun zeroIsNotSpoken() {
        // ⚠️ «صفر» لحظةُ فراغٍ لا ذكر.
        assertFalse(TasbihSpeech.shouldSpeak(enabled = true, count = 0))
        assertTrue(TasbihSpeech.shouldSpeak(enabled = true, count = 1))
    }

    @Test
    fun theCurrentCountIsSpoken() {
        val result = tap(count = 12, completed = false)
        assertEquals(12, TasbihSpeech.spokenCount(result, target = 33))
    }

    @Test
    fun theTargetIsSpokenWhenARoundCompletes() {
        // ⚠️ **الهدف لا الصفر**: العدّاد يعود إلى صفر عند الإتمام،
        // ومن يسمع «صفر» يظنّ أن عدّه ضاع.
        val result = tap(count = 0, completed = true)
        assertEquals(33, TasbihSpeech.spokenCount(result, target = 33))
    }

    @Test
    fun theSpokenCountIsNeverZeroAfterCompletion() {
        val result = tap(count = 0, completed = true)
        assertTrue(TasbihSpeech.spokenCount(result, target = 99) > 0)
    }
}
