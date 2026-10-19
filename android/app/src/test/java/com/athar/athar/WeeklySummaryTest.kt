package com.athar.athar.athary

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/// اختبارات الملخّص الأسبوعي — المنطق النقي.
///
/// ⚠️ التواريخ هنا **نسبية**: كل اختبار يبني حالته من `today`، فلا
/// يعتمد على اليوم الذي يُشغَّل فيه. ملخّص أسبوعي بتاريخ ثابت
/// يصدق يوم كتابته ويكذب بعده.
class WeeklySummaryTest {

    private val today = LocalDate.of(2026, 10, 19)

    private fun state(vararg entries: Pair<LocalDate, Set<HabitId>>): TrackerState {
        var state = TrackerState.EMPTY
        entries.forEach { (day, habits) ->
            habits.forEach { state = state.toggle(day, it) }
        }
        return state
    }

    @Test
    fun theWindowEndsToday() {
        val summary = WeeklySummaries.of(TrackerState.EMPTY, today)
        assertEquals(today, summary.to)
        assertEquals(today.minusDays(6), summary.from)
    }

    @Test
    fun anEmptyTrackerGivesAnEmptySummary() {
        val s = WeeklySummaries.of(TrackerState.EMPTY, today)
        assertEquals(0, s.activeDays)
        assertEquals(0, s.totalActs)
        assertEquals(0, s.completion)
        assertNull(s.bestDay)
        assertEquals(0, s.streak)
    }

    @Test
    fun itOnlyCountsTheLastSevenDays() {
        // ⚠️ ما قبل الأسبوع **لا يُحصى**: ملخّص أسبوعي يضمّ الشهر
        // كله ليس ملخّصًا أسبوعيًّا.
        val old = today.minusDays(20)
        val s = WeeklySummaries.of(state(old to setOf(HabitId.FAJR)), today)
        assertEquals(0, s.totalActs)
    }

    @Test
    fun theBoundaryDayIsIncluded() {
        val edge = today.minusDays(6)
        val s = WeeklySummaries.of(state(edge to setOf(HabitId.FAJR)), today)
        assertEquals(1, s.totalActs)
        assertEquals(1, s.activeDays)
    }

    @Test
    fun theDayBeforeTheBoundaryIsNot() {
        val edge = today.minusDays(7)
        val s = WeeklySummaries.of(state(edge to setOf(HabitId.FAJR)), today)
        assertEquals(0, s.totalActs)
    }

    @Test
    fun activeDaysCountsDaysNotActs() {
        // ⚠️ يومٌ بثلاث عبادات **يوم واحد** لا ثلاثة
        val s = WeeklySummaries.of(state(today to setOf(HabitId.FAJR, HabitId.QURAN, HabitId.DUHA)), today)
        assertEquals(1, s.activeDays)
        assertEquals(3, s.totalActs)
    }

    @Test
    fun completionIsOutOfSevenFullDays() {
        val all = HabitId.entries.toSet()
        val full = (0..6).map { today.minusDays(it.toLong()) to all }.toTypedArray()
        val s = WeeklySummaries.of(state(*full), today)
        assertEquals(100, s.completion)
    }

    @Test
    fun twoFullDaysAreTwoSeventhsNotAHundred() {
        // ⚠️ المقام سبعة أيام كاملة لا أيام النشاط: من أتى بيومين
        // أراد أن يرى ٢٥٪ لا ١٠٠٪.
        val all = HabitId.entries.toSet()
        val s = WeeklySummaries.of(state(today to all, today.minusDays(1) to all), today)
        // ⚠️ ١٦ عبادة من ٥٦ ممكنة = ٢٨٪. وكان هذا الاختبار يقارن
        // ٢٨ بـ ٢٨ بلا نظر إلى `s` — تحصيل حاصل لا يفحص شيئًا.
        assertEquals(16, s.totalActs)
        assertEquals(28, s.completion)
    }

    @Test
    fun completionNeverExceedsAHundred() {
        val all = HabitId.entries.toSet()
        val entries = (0..30).map { today.minusDays(it.toLong()) to all }.toTypedArray()
        val s = WeeklySummaries.of(state(*entries), today)
        assertTrue(s.completion <= 100)
    }

    @Test
    fun theBestDayIsTheRichestOne() {
        val s = WeeklySummaries.of(
            state(
                today to setOf(HabitId.FAJR),
                today.minusDays(2) to setOf(HabitId.FAJR, HabitId.QURAN, HabitId.DUHA),
            ),
            today,
        )
        assertEquals(today.minusDays(2), s.bestDay!!.day)
        assertEquals(3, s.bestDay!!.count)
    }

    @Test
    fun perHabitCoversEveryHabit() {
        val s = WeeklySummaries.of(TrackerState.EMPTY, today)
        assertEquals(HabitId.entries.size, s.perHabit.size)
        assertEquals(HabitId.entries.toSet(), s.perHabit.map { it.first }.toSet())
    }

    @Test
    fun perHabitIsSortedByMostDone() {
        val s = WeeklySummaries.of(
            state(
                today to setOf(HabitId.FAJR),
                today.minusDays(1) to setOf(HabitId.FAJR),
                today.minusDays(2) to setOf(HabitId.FAJR),
                today.minusDays(3) to setOf(HabitId.QURAN),
            ),
            today,
        )
        assertEquals(HabitId.FAJR, s.perHabit.first().first)
        assertEquals(3, s.perHabit.first().second)
        assertEquals(1, s.perHabit.last { it.first == HabitId.QURAN }.second)
    }

    @Test
    fun thePreviousWeekIsTheSevenDaysBefore() {
        val prev = WeeklySummaries.previousOf(TrackerState.EMPTY, today)
        assertEquals(today.minusDays(7), prev.to)
        assertEquals(today.minusDays(13), prev.from)
    }

    @Test
    fun theWeeksDoNotOverlap() {
        // ⚠️ تداخل النافذتين يجعل المقارنة مكرّرة: اليوم نفسه
        // يُحصى مرّتين فيُظهر تحسّنًا كاذبًا.
        val all = HabitId.entries.toSet()
        val entries = (0..13).map { today.minusDays(it.toLong()) to all }.toTypedArray()
        val state = state(*entries)
        val current = WeeklySummaries.of(state, today)
        val previous = WeeklySummaries.previousOf(state, today)
        assertEquals(current.totalActs, previous.totalActs)
        assertFalse(WeeklySummaries.improved(current, previous))
    }

    @Test
    fun improvementIsMeasuredAgainstTheWeekBefore() {
        val current = WeeklySummaries.of(state(today to setOf(HabitId.FAJR, HabitId.QURAN)), today)
        val previous = WeeklySummaries.of(TrackerState.EMPTY, today.minusDays(7))
        assertTrue(WeeklySummaries.improved(current, previous))
        assertFalse(WeeklySummaries.improved(previous, current))
    }

    @Test
    fun theStreakIsCarriedFromTheTracker() {
        val entries = (0..5).map { today.minusDays(it.toLong()) to setOf(HabitId.FAJR) }.toTypedArray()
        val s = WeeklySummaries.of(state(*entries), today)
        assertEquals(6, s.streak)
    }
}
