package com.athar.athar.share

import com.athar.athar.athary.DailyRecord
import com.athar.athar.athary.DayLevel
import com.athar.athar.athary.HabitId
import com.athar.athar.athary.TrackerState
import com.athar.athar.athary.dayKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.util.Locale

/// اختبارات بيانات بطاقة المشاركة (F6).
///
/// ⚠️ الفحص على **الترتيب**: الأحدثُ أوّلًا (يسارًا في العربية)،
/// فترتيبٌ معكوسٌ يجعل «اليوم» أقدمَ يومٍ في الأسبوع — والاسم
/// تحت الخانة يقول غير ما تقوله الخانة.
class ShareCardsTest {

    private val today: LocalDate = LocalDate.of(2026, 10, 19)

    /// أرقامٌ بالترتيب: **الأحدثُ أوّلًا**.
    private fun state(vararg counts: Int): TrackerState {
        val records = LinkedHashMap<String, DailyRecord>()
        counts.forEachIndexed { index, count ->
            val day = today.minusDays(index.toLong())
            records[dayKey(day)] = DailyRecord(
                day = day,
                done = HabitId.entries.take(count).toSet(),
            )
        }
        return TrackerState(records)
    }

    @Test
    fun theNewestDayComesFirst() {
        val days = ShareCards.daysNewestFirst(state(3, 0, 5), today)
        assertEquals(3, days[0])
        assertEquals(0, days[1])
        assertEquals(5, days[2])
    }

    @Test
    fun theWeekIsSevenDays() {
        // ⚠️ **أسبوعٌ لا أسبوعٌ_arbitrary**: سبعة أيام بالضبط،
        // فثمانيةٌ تُحسب يومًا زائدًا.
        assertEquals(7, ShareCards.daysNewestFirst(state(), today).size)
        assertEquals(7, ShareCards.DAYS)
    }

    @Test
    fun theOldestDayIsSixDaysBack() {
        val days = ShareCards.daysNewestFirst(state(1, 1, 1, 1, 1, 1, 1), today)
        assertEquals(7, days.size)
        assertTrue(days.all { it == 1 })
    }

    @Test
    fun theActiveDaysAreCounted() {
        // ⚠️ **يومٌ فيه عبادةٌ واحدة نشطٌ**: لا نصفُ يوم.
        val data = ShareCards.dataOf(
            state = state(3, 0, 5, 0),
            today = today,
            locale = Locale("ar"),
            title = "أثر",
            subtitle = "هذا الأسبوع",
            footer = "خلّي أثرك باقي",
            activeLabel = "أيام",
            actsLabel = "عبادة",
            streakLabel = "سلسلة",
        )
        assertEquals(2, data.activeDays)
        assertEquals(8, data.totalActs)
    }

    @Test
    fun theStreakIsTheCurrentOne() {
        val data = ShareCards.dataOf(
            state = state(2, 2, 2),
            today = today,
            locale = Locale("ar"),
            title = "أثر",
            subtitle = "هذا الأسبوع",
            footer = "خلّي أثرك باقي",
            activeLabel = "أيام",
            actsLabel = "عبادة",
            streakLabel = "سلسلة",
        )
        assertEquals(3, data.streak)
    }

    @Test
    fun theLevelsFollowTheCounts() {
        // ⚠️ **نفسُ دلالة «أثري»**: الخانةُ الملوّنة تقرأ كاليوم
        // نفسه، لا كمقياسٍ آخر.
        val data = ShareCards.dataOf(
            state = state(0, 3, 9),
            today = today,
            locale = Locale("ar"),
            title = "أثر",
            subtitle = "هذا الأسبوع",
            footer = "خلّي أثرك باقي",
            activeLabel = "أيام",
            actsLabel = "عبادة",
            streakLabel = "سلسلة",
        )
        assertEquals(DayLevel.NONE, data.levels[0])
        assertEquals(DayLevel.MID, data.levels[1])
        assertEquals(DayLevel.FULL, data.levels[2])
    }

    @Test
    fun thereAreSevenDayNames() {
        val names = ShareCards.dayNames(today, Locale("ar"))
        assertEquals(7, names.size)
        assertTrue(names.none { it.isBlank() })
    }

    @Test
    fun theDayNamesStartWithToday() {
        val names = ShareCards.dayNames(today, Locale("en"))
        val expected = today.dayOfWeek.getDisplayName(
            java.time.format.TextStyle.SHORT,
            Locale("en"),
        )
        assertEquals(expected, names[0])
    }
}
