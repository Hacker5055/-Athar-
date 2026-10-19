package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات وضع الحفظ — المنطق النقي.
///
/// ⚠️ الأخطر هنا أن يبدو التقدّم أكبر ممّا هو: من ظنّ آية محفوظة
/// وهي ليست، اكتشف ذلك في الصلاة لا في التطبيق.
class HifzTest {

    private val text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"

    // ── النطاق ───────────────────────────────────────────

    @Test
    fun aRangeIsInclusiveAtBothEnds() {
        val ids = Hifz.ids(1, 1, 1, 7)
        assertEquals(7, ids.size)
        assertEquals(1, ids.first())
        assertEquals(7, ids.last())
    }

    @Test
    fun aRangeMaySpanSurahs() {
        // ⚠️ من آخر البقرة إلى أول آل عمران نطاق **متّصل**
        val ids = Hifz.ids(2, 285, 3, 2)
        assertEquals(4, ids.size)
        assertEquals(QuranMeta.verseId(2, 285), ids.first())
        assertEquals(QuranMeta.verseId(3, 2), ids.last())
    }

    @Test
    fun aReversedRangeIsEmptyNotBackwards() {
        assertTrue(Hifz.ids(2, 200, 2, 5).isEmpty())
    }

    @Test
    fun anImpossibleRangeIsEmpty() {
        assertTrue("سورة ٩٠٠", Hifz.ids(900, 1, 900, 5).isEmpty())
        assertTrue("آية ٩٩٩", Hifz.ids(2, 999, 2, 1000).isEmpty())
    }

    @Test
    fun everyIdIsUniqueAndReal() {
        val ids = Hifz.ids(1, 1, 2, 10)
        assertEquals(ids.size, ids.toSet().size)
        assertTrue(ids.all { it in 1..QuranMeta.TOTAL_VERSES })
    }

    @Test
    fun aSingleVerseIsARangeOfOne() {
        assertEquals(listOf(QuranMeta.verseId(2, 255)), Hifz.ids(2, 255, 2, 255))
    }

    // ── النسبة ───────────────────────────────────────────

    @Test
    fun percentIsRelativeToTheRangeNotTheWholeQuran() {
        // ⚠️ من حفظ نصف الفاتحة أراد ٥٠٪ لا ٠٪
        val ids = Hifz.ids(1, 1, 1, 4)
        assertEquals(50, Hifz.percent(setOf(ids[0], ids[1]), ids))
    }

    @Test
    fun percentOfAnEmptyRangeIsZero() {
        assertEquals(0, Hifz.percent(setOf(1, 2, 3), emptyList()))
    }

    @Test
    fun percentNeverExceedsAHundred() {
        val ids = Hifz.ids(1, 1, 1, 7)
        assertEquals(100, Hifz.percent((1..6236).toSet(), ids))
    }

    @Test
    fun completionNeedsEveryVerse() {
        val ids = Hifz.ids(1, 1, 1, 7)
        assertFalse(Hifz.isComplete(setOf(1, 2, 3, 4, 5, 6), ids))
        assertTrue(Hifz.isComplete(setOf(1, 2, 3, 4, 5, 6, 7), ids))
    }

    @Test
    fun anEmptyRangeIsNeverComplete() {
        // ⚠️ `all` على قائمة فارغة تُعيد `true` — وهي كارثة هنا:
        // نطاق غير محدّد يظهر مكتملًا قبل أن يبدأ.
        assertFalse(Hifz.isComplete(emptySet(), emptyList()))
        assertFalse(Hifz.isComplete(setOf(1), emptyList()))
    }

    // ── التالية ──────────────────────────────────────────

    @Test
    fun theNextVerseFollowsTheMushafOrder() {
        val ids = Hifz.ids(1, 1, 1, 7)
        assertEquals(1, Hifz.nextToMemorize(emptySet(), ids))
        assertEquals(3, Hifz.nextToMemorize(setOf(1, 2), ids))
    }

    @Test
    fun nothingLeftToMemorize() {
        val ids = Hifz.ids(1, 1, 1, 7)
        assertEquals(null, Hifz.nextToMemorize(ids.toSet(), ids))
    }

    @Test
    fun reviewStartsFromTheMostRecent() {
        // ⚠️ الأحدث أولًا: ما حفظته أمس أولى ممّا حفظته العام الماضي
        val order = Hifz.ids(1, 1, 1, 7)
        assertEquals(5, Hifz.nextToReview(setOf(2, 5), order))
    }

    // ── درجات الإخفاء ────────────────────────────────────

    @Test
    fun theFirstReadingsAreVisible() {
        assertEquals(0, Hifz.levelOf(0))
        assertEquals(0, Hifz.levelOf(1))
    }

    @Test
    fun maskingHardensWithRepetition() {
        assertEquals(1, Hifz.levelOf(2))
        assertEquals(2, Hifz.levelOf(3))
        assertEquals(Hifz.MAX_LEVEL, Hifz.levelOf(4))
        assertEquals(Hifz.MAX_LEVEL, Hifz.levelOf(99))
    }

    @Test
    fun memorizationNeedsTheTarget() {
        assertFalse(Hifz.isMemorized(Hifz.REPEAT_TARGET - 1))
        assertTrue(Hifz.isMemorized(Hifz.REPEAT_TARGET))
        assertTrue(Hifz.isMemorized(Hifz.REPEAT_TARGET + 10))
    }

    // ── القناع ───────────────────────────────────────────

    @Test
    fun levelZeroKeepsTheTextAsItIs() {
        // ⚠️ النص **حرفيًّا**: الرخصة تمنع التعديل
        assertEquals(text, Hifz.mask(text, 0))
    }

    @Test
    fun theMaskHidesFromTheEnd() {
        val masked = Hifz.mask(text, 1)
        assertTrue("يبدأ بما يُعرف", masked.startsWith("بِسْمِ"))
        assertTrue("وينتهي بالقناع", masked.endsWith("◌◌◌"))
        assertFalse(masked.contains("ٱلرَّحِيمِ"))
    }

    @Test
    fun fullMaskingHidesEveryWord() {
        val masked = Hifz.mask(text, Hifz.MAX_LEVEL)
        assertFalse(masked.contains("بِسْمِ"))
        assertFalse(masked.contains("ٱللَّهِ"))
        assertEquals(4, masked.split("◌◌◌").size - 1)
    }

    @Test
    fun maskingKeepsTheWordCount() {
        // ⚠️ القناع **بديل كلمة** لا حذف: حذف كلمة يغيّر ترتيب
        // الآية فيبدو النص آية أخرى.
        val words = text.split(Regex("\\s+")).size
        for (level in 0..Hifz.MAX_LEVEL) {
            assertEquals("درجة $level", words, Hifz.mask(text, level).split(Regex("\\s+")).size)
        }
    }

    @Test
    fun maskingGrowsMonotonically() {
        var previous = 0
        for (level in 0..Hifz.MAX_LEVEL) {
            val hidden = Hifz.mask(text, level).split("◌◌◌").size - 1
            assertTrue("درجة $level", hidden >= previous)
            previous = hidden
        }
    }

    @Test
    fun maskingSurvivesBlankAndSingleWords() {
        assertEquals("", Hifz.mask("", 3))
        assertEquals("   ", Hifz.mask("   ", 3))
        assertEquals("◌◌◌", Hifz.mask("قُلْ", 3))
    }

    @Test
    fun maskingNeverHidesEverythingBelowTheTop() {
        // ⚠️ درجة دون العليا تُبقي شيئًا: إخفاء كل النص في
        // المرتبة الأولى يجعل التمرين أعمى لا تدريجيًّا.
        assertTrue(Hifz.mask(text, 1).contains("بِسْمِ"))
        assertTrue(Hifz.mask(text, 2).contains("بِسْمِ"))
    }
}
