package com.athar.athar

import com.athar.athar.prayer.PrayerDay
import com.athar.athar.prayer.PrayerMethod
import com.athar.athar.prayer.PrayerSettings
import com.athar.athar.prayer.PrayerSlot
import com.athar.athar.prayer.PrayerTimesCalculator
import com.batoulapps.adhan.Madhab
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/// ⚠️ هذه الاختبارات تعمل على **JVM بلا محاكي**: مكتبة Adhan مكتبة
/// Java نقية. هذا أول تحقّق آلي من صحة منطق أثر، وهو ما افتقدناه
/// سبعة أسابيع في نسخة فليتر.
class PrayerTimesTest {

    private val utcFmt: DateTimeFormatter =
        DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC)

    /// ⚠️ مكتبة Adhan تصفّر الثواني في `CalendarUtil.roundedMinute`
    /// ولا تصفّر المللي ثانية، فتترك ضجيجًا تحت الثانية (رأيناه ١٨ms
    /// بين نداءين متطابقين). المواقيت دقّةُها الدقيقة، فنقارن على
    /// الدقيقة لا على النانو ثانية.
    private fun Instant.toMinute(): Instant = truncatedTo(java.time.temporal.ChronoUnit.MINUTES)

    private fun minutesOf(day: PrayerDay): List<Int> =
        day.ordered.map { (_, time) ->
            val z = time.atZone(ZoneOffset.UTC)
            z.hour * 60 + z.minute
        }

    private fun utcList(day: PrayerDay): List<String> =
        day.ordered.map { (_, time) -> utcFmt.format(time) }

    /// القيم المرجعية من نسخة فليتر (أسبوع ٢) بالتوقيت العالمي،
    /// بتفاوت ±٣ دقائق مسموح به بين منقولات الخوارزمية.
    @Test
    fun cairo_egyptian_shafi_2026_10_18() {
        val day = PrayerTimesCalculator.calculate(
            PrayerSettings(
                method = PrayerMethod.EGYPTIAN,
                madhab = Madhab.SHAFI,
                latitude = 30.0444,
                longitude = 31.2357,
            ),
            LocalDate.of(2026, 10, 18),
        )

        val expected = listOf(2 * 60 + 32, 3 * 60 + 59, 9 * 60 + 40, 12 * 60 + 54, 15 * 60 + 21, 16 * 60 + 38)
        val actualMinutes = minutesOf(day)
        val actualUtc = utcList(day)

        actualMinutes.forEachIndexed { i, minutes ->
            val diff = abs(minutes - expected[i])
            assertTrue(
                "الفرق ${diff}د في ${day.ordered[i].first.arabicName}: " +
                    "المتوقع ${expected[i] / 60}:${(expected[i] % 60).toString().padStart(2, '0')} " +
                    "والفعلي ${actualUtc[i]} (كل الأوقات UTC: $actualUtc)",
                diff <= 3,
            )
        }
    }

    /// ترتيب الصلوات شرط لا ميزة: فجر ← شروق ← ظهر ← عصر ← مغرب ← عشاء
    @Test
    fun timesAreStrictlyIncreasing() {
        val day = PrayerTimesCalculator.calculate(
            PrayerSettings(method = PrayerMethod.UMM_AL_QURA),
            LocalDate.of(2026, 6, 21),
        )
        val times = day.ordered.map { it.second }
        times.zipWithNext().forEach { (a, b) ->
            assertTrue("$a يجب أن يكون قبل $b", a.isBefore(b))
        }
    }

    /// ⚠️ الشروق ليس صلاة — فلا يظهر في «الصلاة القادمة» (قرار أسبوع ٢)
    @Test
    fun nextPrayerSkipsSunrise() {
        val day = PrayerDay(
            date = LocalDate.of(2026, 10, 18),
            fajr = Instant.parse("2026-10-18T02:32:00Z"),
            sunrise = Instant.parse("2026-10-18T03:59:00Z"),
            dhuhr = Instant.parse("2026-10-18T09:40:00Z"),
            asr = Instant.parse("2026-10-18T12:54:00Z"),
            maghrib = Instant.parse("2026-10-18T15:21:00Z"),
            isha = Instant.parse("2026-10-18T16:38:00Z"),
        )

        // بين الفجر والشروق → القادمة الظهر لا الشروق
        val between = day.nextAfter(Instant.parse("2026-10-18T03:00:00Z"))
        assertEquals(PrayerSlot.DHUHR, between?.first)

        // بعد العشاء → لا شيء قادم هذا اليوم
        assertNull(day.nextAfter(Instant.parse("2026-10-18T20:00:00Z")))
    }

    /// المذهب يغيّر العصر فقط — إن غيّر غيره فثمة خلل في الربط
    @Test
    fun madhabAffectsOnlyAsr() {
        val date = LocalDate.of(2026, 10, 18)
        val shafi = PrayerTimesCalculator.calculate(
            PrayerSettings(madhab = Madhab.SHAFI), date,
        )
        val hanafi = PrayerTimesCalculator.calculate(
            PrayerSettings(madhab = Madhab.HANAFI), date,
        )

        assertEquals(shafi.fajr.toMinute(), hanafi.fajr.toMinute())
        assertEquals(shafi.dhuhr.toMinute(), hanafi.dhuhr.toMinute())
        assertEquals(shafi.maghrib.toMinute(), hanafi.maghrib.toMinute())
        assertEquals(shafi.isha.toMinute(), hanafi.isha.toMinute())
        assertTrue(
            "العصر الحنفي يتأخر عن الشافعي (${shafi.asr} مقابل ${hanafi.asr})",
            hanafi.asr.toMinute().isAfter(shafi.asr.toMinute()),
        )
    }
}
