package com.athar.athar.prayer

import com.athar.athar.i18n.AppLocale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

/// اختبارات العدّ التنازلي (A7).
///
/// ⚠️ الفحص على **علاقات** لا على أرقام مكتوبة: ما يهمّ أنّ
/// العدّ ينقص ثانيةً كلّ ثانية، وأنّه لا يقرّب، وأنّ المتبقّي
/// لا يكون سالبًا.
class PrayerCountdownTest {

    private val now = Instant.parse("2026-10-19T12:00:00Z")

    private fun at(seconds: Long) = now.plusSeconds(seconds)

    // ── الحساب ───────────────────────────────────────────

    @Test
    fun anHourAndTwentyMinutesAndThirtySeconds() {
        val c = PrayerCountdown.until(at(3600 + 20 * 60 + 30), now)
        assertEquals(1L, c.hours)
        assertEquals(20L, c.minutes)
        assertEquals(30L, c.seconds)
        assertEquals(3630L, c.totalSeconds)
    }

    @Test
    fun lessThanAnHourHasNoHours() {
        val c = PrayerCountdown.until(at(20 * 60 + 30), now)
        assertEquals(0L, c.hours)
        assertEquals(20L, c.minutes)
        assertEquals(30L, c.seconds)
    }

    @Test
    fun exactlyAMinute() {
        val c = PrayerCountdown.until(at(60), now)
        assertEquals(0L, c.hours)
        assertEquals(1L, c.minutes)
        assertEquals(0L, c.seconds)
    }

    @Test
    fun exactlyAnHour() {
        val c = PrayerCountdown.until(at(3600), now)
        assertEquals(1L, c.hours)
        assertEquals(0L, c.minutes)
        assertEquals(0L, c.seconds)
    }

    @Test
    fun theCountdownLosesExactlyOneSecondEachSecond() {
        // ⚠️ العدّ **ينقص ثانيةً لا يتقفز**: قفزةٌ كهذه تعني
        // أنّه لا يُعرض حيًّا بل كلّ بضع ثوانٍ.
        val target = at(5000)
        var previous = PrayerCountdown.until(target, now).totalSeconds
        for (i in 1..10) {
            val current = PrayerCountdown.until(target, now.plusSeconds(i.toLong())).totalSeconds
            assertEquals(previous - 1, current)
            previous = current
        }
    }

    @Test
    fun theCountdownIsNotRounded() {
        // ⚠️ بلا تقريب: ٢٩.٩ ثانية تظلّ ٢٩ ثانية لا ٣٠. فمن
        // قرّب لأعلى صار يعدّ صفرًا قبل الأذان بدقيقة.
        val c = PrayerCountdown.until(now.plusMillis(29_900), now)
        assertEquals(29L, c.seconds)
        assertEquals(0L, c.minutes)
    }

    @Test
    fun theRemainingIsNeverNegative() {
        // ⚠️ السالب يقول إنّ الصلاة تأخّرت، وهذا حكمٌ على الله.
        val past = PrayerCountdown.until(at(-100), now)
        assertEquals(0L, past.hours)
        assertEquals(0L, past.minutes)
        assertEquals(0L, past.seconds)
        assertTrue(past.isElapsed)
    }

    @Test
    fun theMomentItselfIsNotElapsed() {
        val c = PrayerCountdown.until(now, now)
        assertEquals(0L, c.totalSeconds)
        assertTrue(c.isElapsed)
    }

    @Test
    fun theTotalIsTheSumOfItsParts() {
        for (seconds in listOf(0L, 1L, 59L, 60L, 3599L, 3600L, 86_399L)) {
            val c = PrayerCountdown.until(at(seconds), now)
            assertEquals("$seconds", seconds, c.totalSeconds)
        }
    }

    @Test
    fun aWholeDayIsTwentyFourHours() {
        val c = PrayerCountdown.until(at(86_400), now)
        assertEquals(24L, c.hours)
        assertEquals(0L, c.minutes)
        assertEquals(0L, c.seconds)
    }

    // ── العرض ────────────────────────────────────────────

    @Test
    fun arabicUsesEasternDigits() {
        val c = PrayerCountdown.until(at(3661), now)
        // ⚠️ ١:٠١:٠١ بالعربية الشرقية
        assertEquals("١:٠١:٠١", PrayerCountdown.text(c, AppLocale.AR))
    }

    @Test
    fun englishAndFrenchUseLatinDigits() {
        val c = PrayerCountdown.until(at(3661), now)
        assertEquals("1:01:01", PrayerCountdown.text(c, AppLocale.EN))
        assertEquals("1:01:01", PrayerCountdown.text(c, AppLocale.FR))
    }

    @Test
    fun withoutHoursOnlyMinutesAndSeconds() {
        val c = PrayerCountdown.until(at(125), now)
        assertEquals("٢:٠٥", PrayerCountdown.text(c, AppLocale.AR))
        assertEquals("2:05", PrayerCountdown.text(c, AppLocale.EN))
    }

    @Test
    fun theSecondsAreAlwaysTwoDigits() {
        // ⚠️ «٠:٥» تقرأ «صفر وخمسة»، و«٠:٠٥» تقرأ «صفر وخمس
        // ثوانٍ». الفرق في الالتباس.
        val c = PrayerCountdown.until(at(5), now)
        assertEquals("0:05", PrayerCountdown.text(c, AppLocale.EN))
        val withHours = PrayerCountdown.until(at(3605), now)
        assertEquals("1:00:05", PrayerCountdown.text(withHours, AppLocale.EN))
    }
}
