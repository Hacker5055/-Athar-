package com.athar.athar.athary

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/// منقولة من `app/test/features/athary/tracker_state_test.dart`.
class TrackerStateTest {

    private val today: LocalDate = LocalDate.of(2026, 10, 18)

    private fun d(y: Int, m: Int, day: Int) = LocalDate.of(y, m, day)

    /// يبني سلسلة من `n` يوم منتهية في `end`، كلها بعبادة واحدة.
    private fun streakOf(end: LocalDate, n: Int, habit: HabitId = HabitId.FAJR): TrackerState {
        var state = TrackerState.EMPTY
        var cursor = end
        repeat(n) { state = state.toggle(cursor, habit); cursor = previousDay(cursor) }
        return state
    }

    // ── اليوم ─────────────────────────────────────────────

    @Test fun toggleAddsThenRemoves() {
        var record = DailyRecord(d(2026, 10, 18))
        assertEquals(0, record.count)
        record = record.toggle(HabitId.FAJR)
        assertEquals(1, record.count)
        assertTrue(record.has(HabitId.FAJR))
        record = record.toggle(HabitId.FAJR)
        assertEquals(0, record.count)
        assertFalse(record.has(HabitId.FAJR))
    }

    @Test fun densityLevels() {
        assertEquals(DayLevel.NONE, DailyRecord.levelFor(0))
        assertEquals(DayLevel.LOW, DailyRecord.levelFor(1))
        assertEquals(DayLevel.LOW, DailyRecord.levelFor(2))
        assertEquals(DayLevel.MID, DailyRecord.levelFor(3))
        assertEquals(DayLevel.MID, DailyRecord.levelFor(4))
        assertEquals(DayLevel.HIGH, DailyRecord.levelFor(5))
        assertEquals(DayLevel.HIGH, DailyRecord.levelFor(6))
        assertEquals(DayLevel.FULL, DailyRecord.levelFor(7))
        assertEquals(DayLevel.FULL, DailyRecord.levelFor(8))
        // ⚠️ أكثر من عدد العبادات: دفاعًا ضد إضافة عبادة بلا تحديث الحدود
        assertEquals(DayLevel.FULL, DailyRecord.levelFor(99))
    }

    @Test fun dayKeyIsZeroPadded() {
        assertEquals("2026-01-05", dayKey(d(2026, 1, 5)))
        assertEquals("2026-12-31", dayKey(d(2026, 12, 31)))
    }

    @Test fun previousDayCrossesMonthAndYear() {
        assertEquals(d(2025, 12, 31), previousDay(d(2026, 1, 1)))
        assertEquals(d(2026, 2, 28), previousDay(d(2026, 3, 1)))
        assertEquals(d(2024, 2, 29), previousDay(d(2024, 3, 1))) // سنة كبيسة
        assertEquals(d(2026, 10, 17), previousDay(d(2026, 10, 18)))
    }

    @Test fun dayFromKeyRoundTrips() {
        for (key in listOf("2026-01-01", "2026-10-18", "2024-02-29")) {
            assertEquals(key, dayKey(dayFromKey(key)!!))
        }
    }

    /// ⚠️ ٢٠٢٦-٠٢-٣٠ يمرّ فحص النطاق وهو تاريخ غير موجود
    @Test fun dayFromKeyRejectsBadInput() {
        assertNull(dayFromKey(""))
        assertNull(dayFromKey("2026"))
        assertNull(dayFromKey("2026-10"))
        assertNull(dayFromKey("2026-13-01"))
        assertNull(dayFromKey("2026-00-01"))
        assertNull(dayFromKey("س-ص-ش"))
        assertNull(dayFromKey("2026-02-30"))
    }

    // ── الحالة ────────────────────────────────────────────

    @Test fun emptyDayIsNotStored() {
        var state = TrackerState.EMPTY.toggle(d(2026, 10, 18), HabitId.FAJR)
        assertEquals(1, state.records.size)
        state = state.toggle(d(2026, 10, 18), HabitId.FAJR)
        assertTrue(state.records.isEmpty())
    }

    @Test fun toggleDoesNotAffectAnotherDay() {
        val state = TrackerState.EMPTY
            .toggle(d(2026, 10, 18), HabitId.FAJR)
            .toggle(d(2026, 10, 17), HabitId.QURAN)
        assertTrue(state.has(d(2026, 10, 18), HabitId.FAJR))
        assertFalse(state.has(d(2026, 10, 18), HabitId.QURAN))
        assertTrue(state.has(d(2026, 10, 17), HabitId.QURAN))
    }

    @Test fun recordForUnknownDayIsEmptyNotMissing() {
        assertEquals(0, TrackerState.EMPTY.countFor(d(2026, 10, 18)))
        assertTrue(TrackerState.EMPTY.recordFor(d(2026, 10, 18)).isEmpty)
    }

    // ── السلسلة ───────────────────────────────────────────

    @Test fun contiguousStreak() {
        assertEquals(5, streakOf(today, 5).currentStreak(today))
    }

    @Test fun emptyStateHasZeroStreak() {
        assertEquals(0, TrackerState.EMPTY.currentStreak(today))
    }

    /// ⚠️ بلا هذه القاعدة تظهر السلسلة «صفر» كل صباح قبل أي تأشير
    @Test fun toleranceRuleCountsFromYesterdayWhenTodayEmpty() {
        val state = streakOf(previousDay(today), 7)
        assertEquals(7, state.currentStreak(today))
    }

    @Test fun gapStopsTheCount() {
        var state = streakOf(today, 2)
        val third = previousDay(previousDay(today))
        state = state.toggle(third, HabitId.FAJR)
        assertEquals(3, state.currentStreak(today))

        val isolated = previousDay(previousDay(third))
        state = state.toggle(isolated, HabitId.FAJR)
        assertEquals(3, state.currentStreak(today))
    }

    @Test fun bestStreakRemembersThePast() {
        var state = streakOf(d(2026, 1, 10), 30)
        state = state.toggle(d(2026, 10, 18), HabitId.FAJR)
        assertEquals(30, state.bestStreak())
        assertEquals(1, state.currentStreak(d(2026, 10, 18)))
    }

    @Test fun bestStreakOfEmptyHistoryIsZero() {
        assertEquals(0, TrackerState.EMPTY.bestStreak())
    }

    @Test fun completeDay() {
        var state = TrackerState.EMPTY
        HabitId.entries.forEach { state = state.toggle(today, it) }
        assertTrue(state.isComplete(today))
        assertEquals(1f, state.ratioFor(today))
        assertNull(state.nextSuggestion(today))
    }

    @Test fun suggestionReturnsFirstMissingHabitInOrder() {
        val state = TrackerState.EMPTY.toggle(today, HabitId.FAJR)
        assertEquals(HabitId.FIVE_PRAYERS, state.nextSuggestion(today))
    }

    // ── الشبكة ────────────────────────────────────────────

    @Test fun windowHas30DaysEndingToday() {
        val window = TrackerState.EMPTY.window(today)
        assertEquals(TrackerState.WINDOW_DAYS, window.size)
        assertEquals(today, window.last().day)
        for (i in 1 until window.size) {
            assertTrue("غير مرتّب عند $i", window[i].day.isAfter(window[i - 1].day))
        }
    }

    /// ⚠️ الشبكة يجب أن تعبر حدود الشهر والسنة بلا فراغات
    @Test fun windowCrossesMonthAndYear() {
        val window = TrackerState.EMPTY.window(d(2026, 1, 3), days = 10)
        assertEquals(d(2025, 12, 25), window.first().day)
        assertEquals(d(2026, 1, 3), window.last().day)
    }

    @Test fun totalInWindow() {
        assertEquals(3, streakOf(today, 3).totalInWindow(today, days = 30))
    }

    // ── الترميز ───────────────────────────────────────────

    @Test fun encodeDecodeRoundTrip() {
        val state = TrackerState.EMPTY
            .toggle(d(2026, 10, 18), HabitId.FAJR)
            .toggle(d(2026, 10, 18), HabitId.QURAN)
            .toggle(d(2026, 10, 17), HabitId.FASTING)
        val decoded = TrackerState.decode(state.encode())
        assertEquals(2, decoded.records.size)
        assertTrue(decoded.has(d(2026, 10, 18), HabitId.FAJR))
        assertTrue(decoded.has(d(2026, 10, 18), HabitId.QURAN))
        assertFalse(decoded.has(d(2026, 10, 18), HabitId.FASTING))
        assertTrue(decoded.has(d(2026, 10, 17), HabitId.FASTING))
    }

    /// ⚠️ تلف التخزين ليس سببًا لانهيار التطبيق
    @Test fun corruptedInputYieldsEmpty() {
        for (bad in listOf("", "not json", "[]", "null", "{\"days\": \"yes\"}")) {
            assertTrue("«$bad»", TrackerState.decode(bad).records.isEmpty())
        }
        assertTrue(TrackerState.decode(null).records.isEmpty())
    }

    @Test fun unknownIdsAndKeysAreDropped() {
        val decoded = TrackerState.decode(
            """{"v":1,"days":{"2026-10-18":["fajr","غلط",42],"""
                + """"تاريخ-غلط":["fajr"],"2026-13-40":["fajr"]}}""",
        )
        assertEquals(1, decoded.records.size)
        assertTrue(decoded.has(d(2026, 10, 18), HabitId.FAJR))
        assertEquals(1, decoded.countFor(d(2026, 10, 18)))
    }

    @Test fun emptyDaysAreDroppedAfterDecode() {
        assertTrue(TrackerState.decode("""{"days":{"2026-10-18":[]}}""").records.isEmpty())
    }

    // ── التقليم ───────────────────────────────────────────

    @Test fun pruneDropsOlderThanRetention() {
        val state = TrackerState.EMPTY
            .toggle(d(2026, 10, 18), HabitId.FAJR)
            .toggle(d(2024, 1, 1), HabitId.FAJR)
        val pruned = state.pruned(d(2026, 10, 18))
        assertTrue(pruned.has(d(2026, 10, 18), HabitId.FAJR))
        assertFalse(pruned.has(d(2024, 1, 1), HabitId.FAJR))
    }

    @Test fun pruningEmptyDoesNotCrash() {
        assertTrue(TrackerState.EMPTY.pruned(d(2026, 10, 18)).records.isEmpty())
    }

    // ── العبادات ──────────────────────────────────────────

    @Test fun eightHabitsWithUniqueIds() {
        assertEquals(8, HabitId.entries.size)
        assertEquals(8, HabitId.entries.map { it.id }.toSet().size)
        assertEquals(8, HabitId.entries.map { it.stringKey }.toSet().size)
    }

    /// ⚠️ حارس: لو كان `id` هو `name` نفسه لأضاع أي إعادة تسمية سجل
    /// المستخدم. الفصل بينهما ليس تحسينًا بل شرط بقاء للبيانات.
    @Test fun idIsDecoupledFromEnumName() {
        for (habit in HabitId.entries) {
            assertNotEquals("id لا يجوز أن يكون name: ${habit.name}", habit.name, habit.id)
        }
        assertEquals("fajr", HabitId.FAJR.id)
    }

    @Test fun habitFromIdToleratesUnknown() {
        assertEquals(HabitId.FAJR, HabitId.fromId("fajr"))
        assertEquals(HabitId.FIVE_PRAYERS, HabitId.fromId("FIVE_PRAYERS"))
        assertNull(HabitId.fromId("غير-موجود"))
        assertNull(HabitId.fromId(null))
    }
}
