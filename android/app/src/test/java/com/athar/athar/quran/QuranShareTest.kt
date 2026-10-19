package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات نصّ المشاركة.
///
/// ⚖️ الاختبار الأهم هنا ليس التنسيق بل **وجود النسبة**: مشاركة
/// بلا مصدر تجعل كل من يعيد النشر مخالفًا لرخصة CC BY 3.0 دون أن
/// يدري. فهي شرط لا زينة، وتُختبر كما تُختبر الوظيفة.
class QuranShareTest {

    private val text = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ"

    @Test
    fun theVerseTextIsIncludedVerbatim() {
        val out = QuranShare.ayah("البقرة", 255, text)
        assertTrue("النصّ كما هو بلا تعديل", out.contains(text))
    }

    @Test
    fun theAyahNumberAndSurahAreIncluded() {
        val out = QuranShare.ayah("البقرة", 255, text)
        assertTrue(out.contains("﴿255﴾"))
        assertTrue(out.contains("سورة البقرة"))
    }

    @Test
    fun theSourceIsAlwaysCredited() {
        // ⚖️ هذا أهم اختبار في الملف
        assertTrue(QuranShare.ayah("البقرة", 255, text).contains(QuranSources.TEXT_SOURCE))
        assertTrue(QuranShare.ayahCompact("البقرة", 255, text).contains(QuranSources.TEXT_SOURCE))
    }

    @Test
    fun creditIsNonBlank() {
        assertTrue(QuranShare.credit().isNotBlank())
        assertEquals(QuranShare.credit(), "المصدر: ${QuranSources.TEXT_SOURCE}")
    }

    @Test
    fun compactIsShorterButKeepsEverything() {
        val full = QuranShare.ayah("البقرة", 255, text)
        val compact = QuranShare.ayahCompact("البقرة", 255, text)
        assertTrue(compact.length < full.length)
        assertTrue(compact.contains(text))
        assertTrue(compact.contains("﴿255﴾"))
        assertTrue(compact.contains("سورة البقرة"))
    }

    @Test
    fun anEmptyVerseStillCarriesTheCredit() {
        // ⚠️ نصّ فارغ لا يُسقط الإسناد
        assertTrue(QuranShare.ayah("البقرة", 1, "").contains(QuranSources.TEXT_SOURCE))
    }

    @Test
    fun noBlankLineSplitsTheCompactForm() {
        val compact = QuranShare.ayahCompact("البقرة", 255, text)
        assertTrue(compact.lines().none { it.isBlank() })
    }
}
