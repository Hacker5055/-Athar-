package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات الختمة — المنطق النقي.
///
/// ⚠️ الخطة وعد: من التزم بثلاثين يومًا ثم اكتشف في اليوم الأخير
/// أن التقسيم لم يغطّ المصحف، فقد صدّق تطبيقًا كذب عليه في عبادته.
class KhatmahTest {

    private val TOTAL = QuranMeta.TOTAL_VERSES // ٦٢٣٦

    @Test
    fun thePlanCoversTheWholeQuran() {
        // ⚠️ أهم اختبار: آخر يوم يجب أن ينتهي عند آخر آية
        for (days in Khatmah.PLANS) {
            assertEquals(
                "خطة $days لا تنتهي عند آخر آية",
                TOTAL,
                Khatmah.endOf(Khatmah.actualDays(days), days),
            )
        }
    }

    @Test
    fun noVerseIsLeftOutsideThePlan() {
        for (days in Khatmah.PLANS) {
            val last = Khatmah.actualDays(days)
            var expected = 1
            for (day in 1..last) {
                assertEquals("يوم $day", expected, Khatmah.startOf(day, days))
                val end = Khatmah.endOf(day, days)
                assertTrue(end >= expected)
                expected = end + 1
            }
            assertEquals("بقيت آيات بلا يوم", TOTAL + 1, expected)
        }
    }

    @Test
    fun everyDayHasAtLeastOneVerse() {
        for (days in Khatmah.PLANS) {
            for (day in 1..Khatmah.actualDays(days)) {
                assertTrue(
                    "يوم $day فارغ في خطة $days",
                    Khatmah.endOf(day, days) >= Khatmah.startOf(day, days),
                )
            }
        }
    }

    @Test
    fun perDayRoundsUpNotDown() {
        // ⚠️ التقريب لأسفل يترك آيات بلا يوم، فلا تُختم الختمة
        assertEquals(208, Khatmah.perDay(30)) // ٦٢٣٦ ÷ ٣٠ = ٢٠٧٫٨٧
        assertEquals(104, Khatmah.perDay(60))
        assertEquals(70, Khatmah.perDay(90)) // ٦٢٣٦ ÷ ٩٠ = ٦٩٫٢٩
    }

    @Test
    fun thirtyDaysNeedsExactlyThirty() {
        assertEquals(30, Khatmah.actualDays(30))
    }

    @Test
    fun ninetyDaysMayNeedFewer() {
        // ⚠️ ٦٢٣٦ ÷ ٧٠ = ٨٩٫٠٩ ⇒ ٩٠ يومًا. واليوم الأخير ناقص.
        val actual = Khatmah.actualDays(90)
        assertTrue("٩٠ تحتاج $actual يومًا", actual <= 90 && actual >= 89)
    }

    @Test
    fun dayOfIsTheInverseOfTheRange() {
        for (days in Khatmah.PLANS) {
            val last = Khatmah.actualDays(days)
            for (day in 1..last) {
                for (verse in listOf(
                    Khatmah.startOf(day, days),
                    (Khatmah.startOf(day, days) + Khatmah.endOf(day, days)) / 2,
                    Khatmah.endOf(day, days),
                )) {
                    assertEquals("آية $verse", day, Khatmah.dayOf(verse, days))
                }
            }
        }
    }

    @Test
    fun dayOfNeverExceedsThePlan() {
        for (days in Khatmah.PLANS) {
            assertTrue(Khatmah.dayOf(TOTAL, days) <= Khatmah.actualDays(days))
        }
    }

    @Test
    fun outOfRangeVersesAreClamped() {
        assertEquals(1, Khatmah.dayOf(0, 30))
        assertEquals(1, Khatmah.dayOf(-5, 30))
        assertTrue(Khatmah.dayOf(999_999, 30) <= 30)
    }

    @Test
    fun dayZeroIsTreatedAsDayOne() {
        // ⚠️ صفر ليس يومًا: بدء الخطة يجب أن يقف على اليوم الأول
        assertEquals(1, Khatmah.startOf(0, 30))
        assertEquals(Khatmah.startOf(1, 30), Khatmah.startOf(0, 30))
    }

    @Test
    fun percentStartsAtZeroAndEndsAtHundred() {
        assertEquals(0, Khatmah.percent(0))
        assertEquals(100, Khatmah.percent(TOTAL))
    }

    @Test
    fun percentIsClamped() {
        assertEquals(0, Khatmah.percent(-1))
        assertEquals(100, Khatmah.percent(TOTAL * 2))
    }

    @Test
    fun percentageNeverGoesBackwards() {
        var previous = -1
        var verse = 1
        while (verse <= TOTAL) {
            val p = Khatmah.percent(verse)
            assertTrue(p >= previous)
            previous = p
            verse += 97
        }
    }

    // ── ورد اليوم ────────────────────────────────────────

    @Test
    fun theFirstPortionStartsAtAlFatiha() {
        val (from, _) = Khatmah.portionOf(1, 30)
        assertEquals("البداية ١:١", 1 to 1, from)
    }

    @Test
    fun theLastPortionEndsAtTheLastVerse() {
        val (_, to) = Khatmah.portionOf(Khatmah.actualDays(30), 30)
        assertEquals("النهاية آخر آية", QuranMeta.surahAyahOf(TOTAL), to)
    }

    @Test
    fun portionsAreInIncreasingOrder() {
        for (days in Khatmah.PLANS) {
            for (day in 1 until Khatmah.actualDays(days)) {
                assertTrue(
                    "يوم $day لا يسبق الذي بعده",
                    Khatmah.startOf(day, days) < Khatmah.startOf(day + 1, days),
                )
            }
        }
    }

    // ── عدد الأيام المنقضية ──────────────────────────────

    @Test
    fun theFirstDayIsDayOneNotZero() {
        assertEquals(1, Khatmah.daySince(100, 100))
    }

    @Test
    fun dayCountGrowsByOneEachDay() {
        assertEquals(5, Khatmah.daySince(100, 104))
    }

    @Test
    fun aPastDateNeverGivesLessThanOne() {
        assertEquals(1, Khatmah.daySince(100, 99))
        assertEquals(1, Khatmah.daySince(100, 0))
    }
}
