package com.athar.athar.widget

import com.athar.athar.prayer.PrayerDay
import com.athar.athar.prayer.PrayerSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

/// اختبارات ودجت المواقيت — المنطق النقي.
///
/// ⚠️ الودجت نفسه يُبنى في عملية أخرى ولا نراه يعمل بلا جهاز. الذي
/// نختبره هو **القرار**: ما الذي يُعرض، ومتى ينتقل إلى الغد.
class PrayerWidgetPlanTest {

    private val day = LocalDate.of(2026, 1, 15)

    private fun dayAt(date: LocalDate): PrayerDay {
        fun slot(hour: Int) = Instant.parse(
            "%04d-%02d-%02dT%02d:00:00Z".format(
                date.year, date.monthValue, date.dayOfMonth, hour,
            ),
        )
        return PrayerDay(
            date = date,
            fajr = slot(4),
            sunrise = slot(6),
            dhuhr = slot(12),
            asr = slot(15),
            maghrib = slot(18),
            isha = slot(20),
        )
    }

    private fun at(hour: Int) =
        Instant.parse("2026-01-15T%02d:00:00Z".format(hour))

    @Test
    fun nextPrayerIsTheNextOne() {
        val next = PrayerWidgetPlan.nextOf(dayAt(day), dayAt(day.plusDays(1)), at(13))
        assertEquals(PrayerSlot.ASR, next?.first)
    }

    @Test
    fun sunriseIsNeverTheNextPrayer() {
        // ⚠️ الشروق ليس صلاة (قرار الأسبوع ٢)
        val next = PrayerWidgetPlan.nextOf(dayAt(day), dayAt(day.plusDays(1)), at(5))
        assertEquals(PrayerSlot.DHUHR, next?.first)
    }

    @Test
    fun afterIshaItRollsToTomorrowsFajr() {
        // ⚠️ بعد العشاء لا توجد صلاة قادمة اليوم، والودجت الفارغ أسوأ
        // من لا ودجت.
        val next = PrayerWidgetPlan.nextOf(dayAt(day), dayAt(day.plusDays(1)), at(23))
        assertEquals(PrayerSlot.FAJR, next?.first)
    }

    @Test
    fun beforeFajrShowsTodaysFajr() {
        val next = PrayerWidgetPlan.nextOf(dayAt(day), dayAt(day.plusDays(1)), at(1))
        assertEquals(PrayerSlot.FAJR, next?.first)
    }

    // ── الأرقام ──────────────────────────────────────────

    @Test
    fun digitsAreArabic() {
        assertEquals("٠", PrayerWidgetPlan.arabicDigits(0))
        assertEquals("٣٣", PrayerWidgetPlan.arabicDigits(33))
        assertEquals("١١٤", PrayerWidgetPlan.arabicDigits(114))
    }

    @Test
    fun remainingUnderAnHourIsMinutesOnly() {
        // ⚠️ «٤٥ د» أصدق من «٠:٤٥» في نظرة واحدة
        assertEquals("٤٥ د", PrayerWidgetPlan.remaining(at(12), at(12).plusSeconds(45 * 60)))
    }

    @Test
    fun remainingOverAnHourIsHoursAndMinutes() {
        assertEquals("٢:١٥", PrayerWidgetPlan.remaining(at(12), at(12).plusSeconds(2 * 3600 + 15 * 60)))
    }

    @Test
    fun remainingIsNeverNegative() {
        // ⚠️ ودجت لم يُحدَّث في وقته يعرض «-٠٥ د» إن لم نَجمعه
        val text = PrayerWidgetPlan.remaining(at(13), at(12))
        assertTrue("لا سالب في العرض: $text", '-' !in text)
    }

    @Test
    fun clockIsTwentyFourHour() {
        assertEquals("14:30", PrayerWidgetPlan.clock(
            Instant.parse("2026-01-15T14:30:00Z"), java.time.ZoneId.of("UTC"),
        ))
    }
}
