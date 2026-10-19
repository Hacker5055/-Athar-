package com.athar.athar.tasbih

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/// اختبارات مدَيات اليوم والأسبوع (E6).
///
/// ⚠️ الفحص على **الحدود**: مدًى نصف مفتوح، فلو كانت النهاية
/// مشمولة لتُحسب جلسةُ منتصف الليل مرّتين.
class TasbihRangesTest {

    private val zone: ZoneId = ZoneId.of("UTC")
    private val day: LocalDate = LocalDate.of(2026, 10, 19)

    private fun millisOf(date: LocalDate): Long =
        date.atStartOfDay(zone).toInstant().toEpochMilli()

    @Test
    fun theDayStartsAtMidnight() {
        assertEquals(millisOf(day), TasbihRanges.day(day, zone).first)
    }

    @Test
    fun theDayEndsAtTheNextMidnight() {
        assertEquals(millisOf(day.plusDays(1)), TasbihRanges.day(day, zone).last + 1)
    }

    @Test
    fun theDayIsHalfOpen() {
        val range = TasbihRanges.day(day, zone)
        assertTrue(range.contains(range.first))
        assertFalse(range.contains(range.last + 1))
    }

    @Test
    fun theWeekCoversSevenDays() {
        val week = TasbihRanges.week(day, zone)
        val from = LocalDate.ofInstant(Instant.ofEpochMilli(week.first), zone)
        val to = LocalDate.ofInstant(Instant.ofEpochMilli(week.last), zone)
        assertEquals(6, ChronoUnit.DAYS.between(from, to))
    }

    @Test
    fun theWeekStartsSixDaysBeforeToday() {
        // ⚠️ **ينتهي اليوم** كـ«أثري» (F4): لا «آخر سبعة أيام
        // مخزّنة».
        assertEquals(millisOf(day.minusDays(6)), TasbihRanges.week(day, zone).first)
    }

    @Test
    fun theWeekIncludesToday() {
        val week = TasbihRanges.week(day, zone)
        assertTrue(week.contains(millisOf(day)))
        assertFalse(week.contains(millisOf(day.plusDays(1))))
    }

    @Test
    fun aSessionOfTodayIsInBothRanges() {
        val now = millisOf(day) + 12 * 60 * 60 * 1000L
        val dayRange = TasbihRanges.day(day, zone)
        val weekRange = TasbihRanges.week(day, zone)
        assertTrue(dayRange.contains(now))
        assertTrue(weekRange.contains(now))
    }

    @Test
    fun theRangeFollowsTheDeviceZone() {
        // ⚠️ **منتصف الليل محليٌّ**: في تورونتو يبدأ اليوم قبل
        // منتصف ليل UTC بساعات.
        val toronto = ZoneId.of("America/Toronto")
        val start = TasbihRanges.startOfDay(day, toronto)
        assertEquals(day.atStartOfDay(toronto).toInstant().toEpochMilli(), start)
    }
}
