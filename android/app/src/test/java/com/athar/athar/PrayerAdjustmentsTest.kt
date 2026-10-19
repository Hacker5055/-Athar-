package com.athar.athar.prayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import com.athar.athar.prayer.PrayerTimesCalculator

/// اختبارات التعديل اليدوي (A4 من docs/02) — المنطق النقي.
///
/// ⚠️ هذه الميزة **تُدخل المستخدم في الحساب**: بعدها لا يعود
/// الوقت نتيجةً فلكيّة محضة، بل فلكًا موافقًا لمسجده. والخطر أن
/// يصير الحساب لاعبًا لا مرجعًا — فالسقف والحدود أهمّ هنا من
/// الحساب نفسه.
class PrayerAdjustmentsTest {

    private val base = Instant.parse("2026-10-19T04:00:00Z")
    private val day = PrayerDay(
        date = LocalDate.of(2026, 10, 19),
        fajr = base,
        sunrise = base.plusSeconds(3600),
        dhuhr = base.plusSeconds(7200),
        asr = base.plusSeconds(10800),
        maghrib = base.plusSeconds(14400),
        isha = base.plusSeconds(18000),
    )

    private fun settings(vararg pairs: Pair<PrayerSlot, Int>) =
        PrayerSettings(adjustments = mapOf(*pairs))

    // ── الحدود ───────────────────────────────────────────

    @Test
    fun theRangeIsThirtyMinutes() {
        assertEquals(-30, PrayerAdjustments.MIN)
        assertEquals(30, PrayerAdjustments.MAX)
    }

    @Test
    fun anythingBeyondTheRangeIsClamped() {
        // ⚠️ بلا سقف يصير التطبيق آلة حاسبة لا مواقيت
        assertEquals(30, PrayerAdjustments.clamp(500))
        assertEquals(-30, PrayerAdjustments.clamp(-500))
        assertEquals(30, PrayerAdjustments.clamp(Int.MAX_VALUE))
        assertEquals(-30, PrayerAdjustments.clamp(Int.MIN_VALUE))
    }

    @Test
    fun whatIsInsideTheRangeSurvives() {
        for (m in -30..30) assertEquals(m, PrayerAdjustments.clamp(m))
    }

    // ── التطبيق ──────────────────────────────────────────

    @Test
    fun noSettingsMeansNoChange() {
        val out = PrayerAdjustments.applyTo(day, PrayerSettings())
        assertEquals(day.fajr, out.fajr)
        assertEquals(day.isha, out.isha)
        assertEquals(day.date, out.date)
    }

    @Test
    fun aZeroIsTheSameAsNothing() {
        val out = PrayerAdjustments.applyTo(day, settings(PrayerSlot.FAJR to 0))
        assertEquals(day.fajr, out.fajr)
    }

    @Test
    fun oneSlotMovesAndTheOthersDoNot() {
        // ⚠️ تعديل الفجر **لا يجرّ** العشاء: من صلّى الفجر متأخّرًا
        // لم يتأخّر عشاؤه.
        val out = PrayerAdjustments.applyTo(day, settings(PrayerSlot.FAJR to 10))
        assertEquals(day.fajr.plus(Duration.ofMinutes(10)), out.fajr)
        assertEquals(day.isha, out.isha)
        assertEquals(day.maghrib, out.maghrib)
    }

    @Test
    fun positiveDelaysAndNegativeAdvances() {
        val later = PrayerAdjustments.applyTo(day, settings(PrayerSlot.MAGHRIB to 7))
        val earlier = PrayerAdjustments.applyTo(day, settings(PrayerSlot.MAGHRIB to -7))
        assertTrue(later.maghrib.isAfter(day.maghrib))
        assertTrue(earlier.maghrib.isBefore(day.maghrib))
        assertEquals(14, Duration.between(earlier.maghrib, later.maghrib).toMinutes())
    }

    @Test
    fun everySlotCanBeAdjustedAlone() {
        for (slot in PrayerSlot.entries) {
            val out = PrayerAdjustments.applyTo(day, settings(slot to 5))
            val (_, original) = day.ordered.first { it.first == slot }
            val (_, shifted) = out.ordered.first { it.first == slot }
            assertEquals("$slot", Duration.ofMinutes(5), Duration.between(original, shifted))
        }
    }

    @Test
    fun theWholeDayCanMoveTogether() {
        val all = PrayerSlot.entries.map { it to 3 }.toTypedArray()
        val out = PrayerAdjustments.applyTo(day, settings(*all))
        for ((slot, original) in day.ordered) {
            val shifted = out.ordered.first { it.first == slot }.second
            assertEquals("$slot", Duration.ofMinutes(3), Duration.between(original, shifted))
        }
    }

    @Test
    fun theDateNeverChanges() {
        // ⚠️ تعديل بدقائق لا ينقل اليوم: من زاد ثلاثين دقيقة على
        // العشاء لم يستيقظ في الغد.
        val out = PrayerAdjustments.applyTo(day, settings(PrayerSlot.ISHA to 30))
        assertEquals(day.date, out.date)
    }

    // ── الحالة ───────────────────────────────────────────

    @Test
    fun isAdjustedOnlyWhenSomethingIsNonZero() {
        assertTrue(!PrayerAdjustments.isAdjusted(PrayerSettings()))
        assertTrue(!PrayerAdjustments.isAdjusted(settings(PrayerSlot.FAJR to 0)))
        assertTrue(PrayerAdjustments.isAdjusted(settings(PrayerSlot.FAJR to 1)))
        assertTrue(PrayerAdjustments.isAdjusted(settings(PrayerSlot.FAJR to -1)))
    }

    @Test
    fun minutesOfDefaultsToZero() {
        assertEquals(0, PrayerAdjustments.minutesOf(PrayerSettings(), PrayerSlot.ASR))
        assertEquals(9, PrayerAdjustments.minutesOf(settings(PrayerSlot.ASR to 9), PrayerSlot.ASR))
    }

    // ── مع الحساب الحقيقي ───────────────────────────────

    @Test
    fun theRealCalculationIsShifted() {
        // ⚠️ هذا هو الاختبار الذي يثبت أنّ التعديل **وصل**:
        // الباقي كلّه يختبر منطقًا منفصلًا عن الحساب.
        val date = LocalDate.of(2026, 10, 19)
        val plain = PrayerTimesCalculator.calculate(PrayerSettings(), date)
        val shifted = PrayerTimesCalculator.calculate(
            settings(PrayerSlot.FAJR to 12),
            date,
        )
        assertEquals(
            Duration.ofMinutes(12),
            Duration.between(plain.fajr, shifted.fajr),
        )
        // ⚠️ وبقية المواقيت **لم تتحرّك**: تعديل فجر لا يجرّ عشاء.
        assertEquals(plain.isha, shifted.isha)
        assertEquals(plain.maghrib, shifted.maghrib)
        assertEquals(plain.sunrise, shifted.sunrise)
    }

    @Test
    fun theRealCalculationIgnoresAZero() {
        val date = LocalDate.of(2026, 10, 19)
        val plain = PrayerTimesCalculator.calculate(PrayerSettings(), date)
        val zeroed = PrayerTimesCalculator.calculate(
            settings(PrayerSlot.FAJR to 0),
            date,
        )
        assertEquals(plain.fajr, zeroed.fajr)
    }

    @Test
    fun theDateOfTheRealCalculationIsUntouched() {
        val date = LocalDate.of(2026, 10, 19)
        val shifted = PrayerTimesCalculator.calculate(
            settings(PrayerSlot.ISHA to 30),
            date,
        )
        assertEquals(date, shifted.date)
    }

    @Test
    fun sunriseIsAdjustableToo() {
        // ⚠️ الشروق ليس صلاة، لكنه **يُعرض** ويُعتمد في الإمساك،
        // فتركه بلا تعديل يجعل نصف الشاشة غير قابلة للتصحيح.
        val out = PrayerAdjustments.applyTo(day, settings(PrayerSlot.SUNRISE to -4))
        assertEquals(day.sunrise.minus(Duration.ofMinutes(4)), out.sunrise)
    }
}
