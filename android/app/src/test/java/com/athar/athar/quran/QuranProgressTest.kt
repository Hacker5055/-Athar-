package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات آخر موضع قراءة — المنطق النقي.
class QuranProgressTest {

    @Test
    fun validPositionsAreAccepted() {
        assertTrue(QuranProgress.isValid(1, 1))
        assertTrue(QuranProgress.isValid(2, 286))
        assertTrue(QuranProgress.isValid(114, 6))
    }

    @Test
    fun outOfRangeSurahIsRejected() {
        // ⚠️ موضع من نسخة أقدم قد يشير إلى ما لم يعد موجودًا
        assertFalse(QuranProgress.isValid(0, 1))
        assertFalse(QuranProgress.isValid(115, 1))
    }

    @Test
    fun ayahBeyondTheSurahIsRejected() {
        // ⚠️ البقرة ٢٨٦ لا ٢٨٧، و«تابع القراءة» الذي يفتح فارغًا
        // أسوأ من غيابه
        assertFalse(QuranProgress.isValid(2, 287))
        assertFalse(QuranProgress.isValid(114, 7))
        assertFalse(QuranProgress.isValid(1, 0))
    }

    @Test
    fun percentStartsAtZeroAndEndsAtHundred() {
        assertEquals(0, QuranProgress.percent(1, 1))
        assertEquals(100, QuranProgress.percent(114, 6))
    }

    @Test
    fun percentIsBetweenZeroAndHundred() {
        for (surah in listOf(1, 2, 18, 78, 114)) {
            val p = QuranProgress.percent(surah, 1)
            assertTrue("سورة $surah: $p", p in 0..100)
        }
    }

    @Test
    fun percentNeverGoesBackwards() {
        var previous = -1
        for (surah in 1..114) {
            val p = QuranProgress.percent(surah, 1)
            assertTrue("تراجع في سورة $surah", p >= previous)
            previous = p
        }
    }

    @Test
    fun percentOfAnInvalidPositionIsZero() {
        // ⚠️ صفر لا سالب ولا استثناء: نسبة باطلة تُعرض كبداية،
        // ولا شيء يُسقط الشاشة.
        assertEquals(0, QuranProgress.percent(115, 1))
        assertEquals(0, QuranProgress.percent(2, 999))
    }

    @Test
    fun theEndIsAfterTheBeginning() {
        assertTrue(QuranProgress.percent(114, 6) > QuranProgress.percent(1, 1))
    }
}
