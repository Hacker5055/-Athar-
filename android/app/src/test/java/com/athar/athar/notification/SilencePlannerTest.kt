package com.athar.athar.notification

import com.athar.athar.prayer.PrayerDay
import com.athar.athar.prayer.PrayerSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/// اختبارات نوافذ الصمت (docs/02 · A11).
///
/// ⚠️ الفحوص على **الحدود**: متى تبدأ النافذة؟ متى تنتهي؟ وماذا
/// يحدث حين تتداخل نافذتان — فكلُّ خطأٍ هنا صوتُ هاتفٍ لا يُطفأ.
class SilencePlannerTest {

    private val day: LocalDate = LocalDate.of(2026, 10, 19)

    private fun midnight(date: LocalDate): Instant =
        date.atStartOfDay(ZoneOffset.UTC).toInstant()

    /// ستّ صلوات تفصل بينها [gapMinutes] وتبدأ ٠٤:٠٠ UTC.
    private fun day(date: LocalDate, startHours: Long = 4, gapMinutes: Long = 120): PrayerDay {
        fun at(index: Long) =
            midnight(date).plus(startHours * 60 + index * gapMinutes, ChronoUnit.MINUTES)
        return PrayerDay(
            date = date,
            fajr = at(0),
            sunrise = at(1),
            dhuhr = at(2),
            asr = at(3),
            maghrib = at(4),
            isha = at(5),
        )
    }

    private val on = SilenceSettings(enabled = true, minutes = 15)

    // ── الأساسيات ───────────────────────────────────────────

    @Test
    fun theFeatureIsOffByDefault() {
        assertTrue(!SilenceSettings().enabled)
    }

    @Test
    fun nothingIsPlannedWhenTheFeatureIsOff() {
        val plan = SilencePlanner.plan(
            days = listOf(day(day)),
            settings = SilenceSettings(enabled = false),
            now = midnight(day),
        )
        assertTrue(plan.isEmpty())
    }

    @Test
    fun everyPrayerHasAWindow() {
        val plan = SilencePlanner.plan(
            days = listOf(day(day)),
            settings = on,
            now = midnight(day),
        )
        // ⚠️ **خمسُ نوافذ لا ستًّا**: الشروق ليس صلاة.
        assertEquals(5, plan.size)
    }

    @Test
    fun theSunriseHasNoWindow() {
        val plan = SilencePlanner.plan(
            days = listOf(day(day)),
            settings = on,
            now = midnight(day),
        )
        assertTrue(plan.none { it.prayer == PrayerSlot.SUNRISE })
    }

    @Test
    fun theWindowStartsAfterTheAdhan() {
        // ⚠️ **بعد الأذان بدقيقتين**: لو بدأ معه لكتم «عدم الإزعاج»
        // صوتَ الأذان نفسه.
        val plan = SilencePlanner.plan(
            days = listOf(day(day)),
            settings = on,
            now = midnight(day),
        )
        val fajr = plan.first { it.prayer == PrayerSlot.FAJR }
        val adhan = day(day).fajr
        assertEquals(adhan.plus(2, ChronoUnit.MINUTES), fajr.start)
    }

    @Test
    fun theWindowLastsTheChosenMinutes() {
        val plan = SilencePlanner.plan(
            days = listOf(day(day)),
            settings = SilenceSettings(enabled = true, minutes = 30),
            now = midnight(day),
        )
        for (window in plan) {
            assertEquals(30, window.end.epochSecond - window.start.epochSecond)
        }
    }

    @Test
    fun aWindowThatAlreadyEndedIsNotPlanned() {
        val plan = SilencePlanner.plan(
            days = listOf(day(day)),
            settings = on,
            // ⚠️ بعد آخر نافذة بيوم: لم يبقَ إلا ما هو آتٍ.
            now = midnight(day).plus(30, ChronoUnit.HOURS),
        )
        assertTrue(plan.isEmpty())
    }

    @Test
    fun aWindowInProgressIsStillPlanned() {
        // ⚠️ **النافذةُ الجارية تُعاد جدولتها**: بعد إعادة تشغيل
        // الجهاز تبدأ من جديد — فلا يبقى الهاتف مصمتًا بلا سبب.
        val plan = SilencePlanner.plan(
            days = listOf(day(day)),
            settings = on,
            now = midnight(day).plus(4, ChronoUnit.HOURS).plus(5, ChronoUnit.MINUTES),
        )
        assertEquals(5, plan.size)
    }

    // ── الإعداد ─────────────────────────────────────────────

    @Test
    fun anUnknownDurationFallsBackToTheDefault() {
        val plan = SilencePlanner.plan(
            days = listOf(day(day)),
            settings = SilenceSettings(enabled = true, minutes = 7),
            now = midnight(day),
        )
        val fajr = plan.first { it.prayer == PrayerSlot.FAJR }
        assertEquals(15, fajr.end.epochSecond - fajr.start.epochSecond)
    }

    // ── التداخل ─────────────────────────────────────────────

    @Test
    fun overlappingWindowsAreMerged() {
        // ⚠️ **النوافذُ المتداخلة تُدمج**: لولا الدمج لَرَدَّ
        // الإيقافُ الأولُ فلترَ الصمت والثانيةُ مفتوحة — فيصمت
        // الهاتف لحظةً ثم يعود الصوت في وسط الصلاة.
        val tight = PrayerDay(
            date = day,
            fajr = midnight(day).plus(4, ChronoUnit.HOURS),
            sunrise = midnight(day).plus(4, ChronoUnit.HOURS).plus(30, ChronoUnit.MINUTES),
            dhuhr = midnight(day).plus(5, ChronoUnit.HOURS),
            asr = midnight(day).plus(5, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES),
            maghrib = midnight(day).plus(6, ChronoUnit.HOURS),
            isha = midnight(day).plus(7, ChronoUnit.HOURS),
        )
        val plan = SilencePlanner.plan(
            days = listOf(tight),
            settings = SilenceSettings(enabled = true, minutes = 30),
            now = midnight(day),
        )
        // ⚠️ الظهرُ والعصرُ متداخلان (٠٥:٠٠+٢د إلى ٠٥:٣٠+٣٠د = ٠٥:٤٢
        // والعصرُ يبدأ ٠٥:١٢) فصارا نافذةً واحدة.
        assertEquals(4, plan.size)
        val merged = plan.first { it.prayer == PrayerSlot.DHUHR }
        // ⚠️ النافذةُ المدمجة **تحتفظُ بأول صلاةٍ فيها**، فلا
        // نبحث عن العصر فحسب.
        assertTrue(plan.none { it.prayer == PrayerSlot.ASR })
        assertEquals(
            midnight(day).plus(5, ChronoUnit.HOURS).plus(2, ChronoUnit.MINUTES),
            merged.start,
        )
        assertEquals(
            midnight(day).plus(5, ChronoUnit.HOURS).plus(42, ChronoUnit.MINUTES),
            merged.end,
        )
    }

    @Test
    fun theWindowsAreInOrder() {
        val plan = SilencePlanner.plan(
            days = listOf(day(day)),
            settings = on,
            now = midnight(day),
        )
        for (i in 1 until plan.size) {
            assertTrue(plan[i].start.isAfter(plan[i - 1].start))
        }
    }
}
