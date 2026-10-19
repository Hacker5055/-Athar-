package com.athar.athar.hijri

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/// اختبارات التقويم الهجري.
///
/// ⚠️ **لا تاريخ ثابت واحد** في هذه الاختبارات عن قصد: لا مترجم
/// ولا `java` في بيئة التطوير لأحسب قيمة مرجعية أتحقق منها، والرقم
/// الذي أكتبه من الذاكرة قد يكون خطأً أُثبته اختبارًا. فالذي يُختبر
/// هو **الثوابت** — ما يجب أن يصدق على أي تحويل صحيح.
class HijriCalendarTest {

    private val start = LocalDate.of(2026, 1, 1)

    @Test
    fun monthIsAlwaysOneToTwelve() {
        var d = start
        repeat(800) {
            val h = HijriCalendar.from(d)
            assertTrue("الشهر ${h.month}", h.month in 1..12)
            d = d.plusDays(1)
        }
    }

    @Test
    fun dayIsNeverAboveThirty() {
        // ⚠️ الشهر الهجري ٢٩ أو ٣٠ — و٣١ يعني تحويلًا معطوبًا
        var d = start
        repeat(800) {
            val h = HijriCalendar.from(d)
            assertTrue("اليوم ${h.day}", h.day in 1..30)
            d = d.plusDays(1)
        }
    }

    @Test
    fun yearIsPlausible() {
        val h = HijriCalendar.from(start)
        assertTrue("السنة ${h.year}", h.year in 1300..1600)
    }

    @Test
    fun allTwelveMonthsAppearWithinAYear() {
        // ⚠️ السنة الهجريّة ~354 يومًا: لو لم تمرّ بكل الشهور في
        // ٤٠٠ يوم فالتحويل يتكرّر أو يقفز.
        val seen = HashSet<Int>()
        var d = start
        repeat(400) {
            seen.add(HijriCalendar.from(d).month)
            d = d.plusDays(1)
        }
        assertEquals((1..12).toSet(), seen)
    }

    @Test
    fun consecutiveGregorianDaysNeverGoBackwards() {
        // ⚠️ التحويل ترتيبي: يوم ميلادي بعد آخر لا يكون قبله هجريًّا
        var d = start
        var prev = HijriCalendar.from(d)
        repeat(800) {
            d = d.plusDays(1)
            val now = HijriCalendar.from(d)
            val before = listOf(prev.year, prev.month, prev.day)
            val after = listOf(now.year, now.month, now.day)
            assertTrue(
                "تراجع في $d: $before → $after",
                before <= after || (now.month < prev.month && now.year > prev.year),
            )
            prev = now
        }
    }

    // ── الإزاحة (G2) ─────────────────────────────────────

    @Test
    fun offsetIsClampedToTwoDays() {
        // ⚠️ إزاحة بلا حدّ تُخرج تواريخ لا معنى لها
        val base = HijriCalendar.from(start)
        assertEquals(HijriCalendar.from(start, 2), HijriCalendar.from(start, 5))
        assertEquals(HijriCalendar.from(start, -2), HijriCalendar.from(start, -5))
        // الصفر لا يغيّر شيئًا
        assertEquals(base, HijriCalendar.from(start, 0))
    }

    @Test
    fun aPositiveOffsetNeverMovesTheDateBackwards() {
        val base = HijriCalendar.from(start)
        val plus = HijriCalendar.from(start, 1)
        assertTrue(
            listOf(base.year, base.month, base.day) <= listOf(plus.year, plus.month, plus.day),
        )
    }

    @Test
    fun aTwoDayOffsetDiffersByAtMostTwoDays() {
        val base = HijriCalendar.from(start)
        val plus = HijriCalendar.from(start, 2)
        val delta = when {
            plus.year > base.year -> 30 // عبر حدّ السنة: نكتفي بعدم التراجع
            else -> plus.day - base.day
        }
        assertTrue("فرق $delta", delta <= 2 || plus.month > base.month)
    }

    // ── الأسماء ──────────────────────────────────────────

    @Test
    fun monthKeyIsStable() {
        assertEquals("hijri_month_1", HijriCalendar.monthKey(1))
        assertEquals("hijri_month_9", HijriCalendar.monthKey(9))
        assertEquals("hijri_month_12", HijriCalendar.monthKey(12))
    }

    @Test
    fun monthNamesCoverEveryMonth() {
        assertEquals(12, HijriCalendar.MONTHS_AR.size)
        assertEquals(12, HijriCalendar.MONTHS_AR.toSet().size)
        assertEquals("رمضان", HijriCalendar.monthNameAr(9))
        assertEquals("محرم", HijriCalendar.monthNameAr(1))
    }

    @Test
    fun outOfRangeMonthFallsBackNotCrashes() {
        // ⚠️ شهر من تخزين قديم قد يكون باطلًا
        assertEquals(HijriCalendar.MONTHS_AR.first(), HijriCalendar.monthNameAr(0))
        assertEquals(HijriCalendar.MONTHS_AR.first(), HijriCalendar.monthNameAr(99))
    }
}
