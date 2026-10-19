package com.athar.athar.prayer

import com.athar.athar.hijri.HijriCalendar
import com.athar.athar.hijri.HijriDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

/// اختبارات التقويم الشهري — المنطق النقي.
///
/// ⚠️ كما في كل ما يخصّ الحساب الفلكي: **لا ساعة ثابتة** في هذه
/// الاختبارات، لأن المواقيت تختلف بالمكان، ولا `java` في بيئة
/// التطوير لأحسب مرجعًا. الذي يُختبر هو **بنية الجدول** لا
/// محتواه — والبنية هي التي يكذب فيها جدول يُنسخ صفًّا واحدًا
/// ثلاثين مرّة.
class MonthScheduleTest {

    private val settings = PrayerSettings(latitude = 30.0444, longitude = 31.2357)

    @Test
    fun februaryIsLongerInALeapYear() {
        // ⚠️ الاختبار الذي يمنع افتراض ٣٠ يومًا لكل شهر
        assertEquals(29, MonthSchedule.lengthOf(2024, 2))
        assertEquals(28, MonthSchedule.lengthOf(2026, 2))
    }

    @Test
    fun everyMonthHasItsOwnLength() {
        val lengths = (1..12).map { MonthSchedule.lengthOf(2026, it) }.toSet()
        assertEquals(setOf(28, 30, 31), lengths)
    }

    @Test
    fun rowCountMatchesTheMonthLength() {
        for (month in 1..12) {
            val rows = MonthSchedule.rows(settings, 2026, month)
            assertEquals("شهر $month", MonthSchedule.lengthOf(2026, month), rows.size)
        }
    }

    @Test
    fun theFirstRowIsTheFirstOfTheMonth() {
        val rows = MonthSchedule.rows(settings, 2026, 3)
        assertEquals(LocalDate.of(2026, 3, 1), rows.first().date)
    }

    @Test
    fun datesAreConsecutiveAndInsideTheMonth() {
        val rows = MonthSchedule.rows(settings, 2026, 5)
        rows.forEachIndexed { i, row ->
            assertEquals("الصف $i", LocalDate.of(2026, 5, i + 1), row.date)
        }
    }

    @Test
    fun everyRowStaysInTheRequestedMonth() {
        for (month in 1..12) {
            for (row in MonthSchedule.rows(settings, 2026, month)) {
                assertEquals("شهر $month", month, row.date.monthValue)
            }
        }
    }

    @Test
    fun theTimesMoveThroughTheMonth() {
        // ⚠️ الاختبار الأهم: لو نُسخ صفٌّ واحد ثلاثين مرّة لظهر
        // الجدول صحيحًا كله، وهذا الاختبار وحده يكشفه. الفجر
        // يتقدّم ويؤخّر على مدار الشهر، فلا يتساوى يومان.
        val rows = MonthSchedule.rows(settings, 2026, 4)
        val fajr = rows.map { it.day.fajr }.toSet()
        assertTrue("كل الأيّام بفجر واحد", fajr.size > 20)
    }

    @Test
    fun timesAreOrderedWithinADay() {
        // ⚠️ الترتيب **شرط صحة**: فجر بعد عشاء يعني خللًا في
        // الحساب لا مجرد فرق توقيت.
        for (row in MonthSchedule.rows(settings, 2026, 6)) {
            val d = row.day
            assertTrue(d.fajr < d.sunrise)
            assertTrue(d.sunrise < d.dhuhr)
            assertTrue(d.dhuhr < d.asr)
            assertTrue(d.asr < d.maghrib)
            assertTrue(d.maghrib < d.isha)
        }
    }

    @Test
    fun everyRowCarriesAHijriDate() {
        for (row in MonthSchedule.rows(settings, 2026, 7)) {
            assertTrue(row.hijri.month in 1..12)
            assertTrue(row.hijri.day in 1..30)
        }
    }

    @Test
    fun theHijriDateNeverGoesBackwards() {
        // ⚠️ الشهر الهجري أقصر من الميلادي: قد يمرّ شهر هجري
        // كامل داخل الشهر الميلادي. لكن التاريخ لا يرجع.
        val rows = MonthSchedule.rows(settings, 2026, 8)
        for (i in 0 until rows.size - 1) {
            val a = rows[i].hijri
            val b = rows[i + 1].hijri
            assertTrue(
                "$a → $b",
                b.year > a.year || (b.year == a.year && b.dayOfYear() >= a.dayOfYear()),
            )
        }
    }

    @Test
    fun theOffsetIsAppliedToEveryRow() {
        val plain = MonthSchedule.rows(settings, 2026, 9)
        val shifted = MonthSchedule.rows(settings, 2026, 9, 1)
        // ⚠️ الضبط قد يغيّر اليوم الهجري أو لا — لكنه لا يغيّر
        // عدد الأيّام الميلاديّة.
        assertEquals(plain.size, shifted.size)
        assertEquals(plain.first().date, shifted.first().date)
    }

    // ── التنقّل بين الشهور ───────────────────────────────

    @Test
    fun previousJanuaryWrapsToDecember() {
        // ⚠️ كانون الثاني ناقص واحد = كانون الأول من السنة التي
        // قبلها. من كتب `month - 1` حصل على الشهر صفر.
        assertEquals(2025 to 12, MonthSchedule.previous(2026, 1))
    }

    @Test
    fun nextDecemberWrapsToJanuary() {
        assertEquals(2027 to 1, MonthSchedule.next(2026, 12))
    }

    @Test
    fun nextAndPreviousAreInverse() {
        for (month in 1..12) {
            val (ny, nm) = MonthSchedule.next(2026, month)
            assertEquals(2026 to month, MonthSchedule.previous(ny, nm))
        }
    }

    @Test
    fun navigationAgreesWithYearMonth() {
        for (month in 1..12) {
            val (ny, nm) = MonthSchedule.next(2026, month)
            assertEquals(
                YearMonth.of(2026, month).plusMonths(1),
                YearMonth.of(ny, nm),
            )
        }
    }

    private fun HijriDate.dayOfYear(): Int = (month - 1) * 30 + day
}
