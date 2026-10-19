package com.athar.athar.qibla

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.abs

/// اختبارات القبلة الشمسية (D3).
///
/// ⚠️ **علاقات لا أرقام**: الفحص أنّ ميل الشمس يبلغ ذروته في
/// الاعتدالين، وأنّ زاوية القبلة معروفة، وأنّ **يوم القبلة
/// الشمسية تحقّقه الفلك**: فيها تشرق الشمس في اتجاه القبلة
/// بالضبط. ونسخُ التاريخ من مرجعٍ آخر يجعل الاختبار نسخةً من
/// خطئه.
class SunQiblaTest {

    private val cairo = 30.0444 to 31.2357
    private val paris = 48.8566 to 2.3522
    private val newYork = 40.7128 to -74.0060

    private fun at(text: String): Instant = Instant.parse(text)

    // ── الميل ────────────────────────────────────────────

    @Test
    fun theDeclinationIsZeroAtTheMarchEquinox() {
        // ⚠️ الاعتدال: الميل صفر، ورقمٌ اعتباطيّ لا يصلح.
        assertEquals(0.0, SunQibla.declination(at("2026-03-20T12:00:00Z")), 0.2)
    }

    @Test
    fun theDeclinationIsZeroAtTheSeptemberEquinox() {
        assertEquals(0.0, SunQibla.declination(at("2026-09-22T12:00:00Z")), 0.2)
    }

    @Test
    fun theDeclinationPeaksAtTheJuneSolstice() {
        // ⚠️ ذروة الميل **في الاعتدال** لا في غيره: ٢٣٫٤ درجة
        // هي ميل دائرة البروج.
        val june = SunQibla.declination(at("2026-06-21T12:00:00Z"))
        val december = SunQibla.declination(at("2026-12-21T12:00:00Z"))
        assertEquals(SunQibla.OBLIQUITY, june, 0.1)
        assertEquals(-SunQibla.OBLIQUITY, december, 0.1)
    }

    @Test
    fun theDeclinationNeverExceedsTheObliquity() {
        // ⚠️ الميل **لا يتجاوز** ميل دائرة البروج أبدًا —
        // قاعدةٌ فيزيائية لا تقدير.
        for (month in 1..12) {
            for (day in listOf(1, 11, 21)) {
                val value = SunQibla.declination(at("2026-%02d-%02dT12:00:00Z".format(month, day)))
                assertTrue("$month-$day → $value", abs(value) <= SunQibla.OBLIQUITY + 0.01)
            }
        }
    }

    @Test
    fun theDeclinationIsNegativeInDecemberAndPositiveInJune() {
        assertTrue(SunQibla.declination(at("2026-01-15T12:00:00Z")) < 0)
        assertTrue(SunQibla.declination(at("2026-07-15T12:00:00Z")) > 0)
    }

    // ── زاوية القبلة ─────────────────────────────────────

    @Test
    fun theQiblaBearingOfCairoIsKnown() {
        // ⚠️ زاوية القبلة من القاهرة **١٣٦ درجة** — قيمة
        // منشورة، ومقياس الفحص على دقّة نصف درجة.
        assertEquals(136.1, SunQibla.qiblaBearing(cairo.first, cairo.second), 0.6)
    }

    @Test
    fun theQiblaBearingOfNewYorkIsKnown() {
        // ⚠️ من نيويورك **٥٨ درجة** — في الشرق، فالمؤشّر يشير
        // إلى الرheast لا إلى الغرب.
        assertEquals(58.5, SunQibla.qiblaBearing(newYork.first, newYork.second), 0.6)
    }

    @Test
    fun theBearingIsAlwaysBetweenZeroAndThreeSixty() {
        for (lat in listOf(-60.0, -30.0, 0.0, 30.0, 51.5, 60.0)) {
            for (lon in listOf(-180.0, -90.0, 0.0, 31.2, 120.0, 180.0)) {
                val bearing = SunQibla.qiblaBearing(lat, lon)
                assertTrue("$lat,$lon → $bearing", bearing >= 0.0 && bearing < 360.0)
            }
        }
    }

    @Test
    fun theKaabaIsDueEastFromAPointJustWestOfIt() {
        // ⚠️ **من الكعبة نفسها** لا زاوية: الموضع معدوم. فنتحقّق
        // من نقطةٍ **غرب** الكعبة بقليل — فالكعبة شرقًا منها،
        // والزاوية تسعون درجة.
        val bearing = SunQibla.qiblaBearing(
            SunQibla.KAABA_LATITUDE,
            SunQibla.KAABA_LONGITUDE - 0.01,
        )
        assertEquals(90.0, bearing, 0.5)
    }

    @Test
    fun theKaabaIsDueWestFromAPointJustEastOfIt() {
        val bearing = SunQibla.qiblaBearing(
            SunQibla.KAABA_LATITUDE,
            SunQibla.KAABA_LONGITUDE + 0.01,
        )
        assertEquals(270.0, bearing, 0.5)
    }

    // ── الشروق ───────────────────────────────────────────

    @Test
    fun theSunRisesDueEastAtTheEquinox() {
        // ⚠️ الاعتدال: الشروق **شرقًا تامًّا** — ٩٠ درجة.
        assertEquals(90.0, SunQibla.sunriseAzimuth(cairo.first, 0.0), 0.5)
    }

    @Test
    fun theSunRisesNorthOfEastInSummer() {
        // ⚠️ في الصيف **شمال الشرق**، فزاوية الشروق أصغر من ٩٠.
        val summer = SunQibla.sunriseAzimuth(cairo.first, 23.0)
        assertTrue("$summer", summer < 90.0 && summer > 60.0)
    }

    @Test
    fun theSunRisesSouthOfEastInWinter() {
        val winter = SunQibla.sunriseAzimuth(cairo.first, -23.0)
        assertTrue("$winter", winter > 90.0 && winter < 120.0)
    }

    // ── أيام القبلة الشمسية ──────────────────────────────

    @Test
    fun cairoHasNoQiblaSunDay() {
        // ⚠️ **القاهرة ليس فيها يومٌ كهذا**: |cos β · cos φ|
        // أكبر من ميل الشمس الأقصى. فالحقيقة أنّ القبلة الشمسية
        // **غير ممكنة** من هناك — لا "بعيدة".
        assertTrue(SunQibla.requiredDeclination(cairo.first, SunQibla.qiblaBearing(cairo.first, cairo.second)).isNaN())
        assertTrue(SunQibla.qiblaSunDays(2026, cairo.first, cairo.second).isEmpty())
    }

    @Test
    fun parisHasTwoQiblaSunDays() {
        // ⚠️ **يومان لا يوم**: الميل يعبر القيمة نفسها مرّتين.
        val days = SunQibla.qiblaSunDays(2026, paris.first, paris.second)
        assertEquals(2, days.size)
    }

    @Test
    fun onTheQiblaSunDayTheSunRisesExactlyAtTheQiblaBearing() {
        // ⚠️ **هذا هو الفحص الفيزيائيّ الحقيقي**: في اليوم
        // المعنيّ تشرق الشمس في اتجاه القبلة بالضبط. فلو ضاع
        // شيءٌ في الحساب لاختلّ.
        for (lat in listOf(paris, newYork)) {
            val bearing = SunQibla.qiblaBearing(lat.first, lat.second)
            for (day in SunQibla.qiblaSunDays(2026, lat.first, lat.second)) {
                val noon = day.atTime(12, 0).atZone(ZoneId.of("UTC")).toInstant()
                val declination = SunQibla.declination(noon)
                val sunrise = SunQibla.sunriseAzimuth(lat.first, declination)
                val difference = abs(sunrise - bearing)
                assertTrue("$day: $sunrise ≠ $bearing", minOf(difference, 360 - difference) < 0.5)
            }
        }
    }

    @Test
    fun theQiblaSunDaysAreDistinct() {
        val days = SunQibla.qiblaSunDays(2026, paris.first, paris.second)
        assertEquals(days.size, days.distinct().size)
        assertEquals(days.sorted(), days)
    }

    @Test
    fun theQiblaSunDaysAreInTheYear() {
        for (day in SunQibla.qiblaSunDays(2026, paris.first, paris.second)) {
            assertEquals(2026, day.year)
            assertTrue(day.monthValue in 1..12)
        }
    }

    // ── موضع الشمس ───────────────────────────────────────

    /// أعلى نقطةٍ للشمس في اليوم — **هي الظهر الشمسيّ**.
    ///
    /// ⚠️ الظهر الشمسيّ **ليس ١٢:٠٠ بالضبط**: خطّ الطول
    /// وفرق التوقيت يجعلانه قبلها أو بعدها بدقائق. فمن افترض
    /// الثانية تمامًا فشل. فنبحث عن **أعلى ارتفاعٍ** في اليوم،
    /// وهو التعريف الصحيح.
    private fun highestPoint(
        date: String,
        latitude: Double,
        longitude: Double,
        offsetSeconds: Int,
    ): SunQibla.Position {
        val start = at("${date}T00:00:00Z")
        var best = SunQibla.position(start, latitude, longitude, offsetSeconds)
        for (minute in 1 until 1440) {
            val position = SunQibla.position(
                start.plusSeconds(minute * 60L), latitude, longitude, offsetSeconds,
            )
            if (position.altitude > best.altitude) best = position
        }
        return best
    }

    @Test
    fun theSunIsDueSouthAtSolarNoonInTheNorth() {
        // ⚠️ ظهيرةً شمسيًّا: الشمس **جنوبًا** في نصف الكرة
        // الشماليّ (إلا حين يفوق الميل عرضَ المكان).
        val noon = highestPoint("2026-10-19", cairo.first, cairo.second, 7200)
        assertEquals(180.0, noon.azimuth, 1.0)
    }

    @Test
    fun theSunIsDueNorthAtSolarNoonInTheSouth() {
        val noon = highestPoint("2026-10-19", -33.87, 151.21, 0)
        assertTrue("${noon.azimuth}", noon.azimuth < 1.0 || noon.azimuth > 359.0)
    }

    @Test
    fun theHighestPointOfTheDayIsInTheDaylight() {
        // ⚠️ أعلى نقطةٍ **Above الأفق** دائمًا — فمن حسب وقتًا
        // آخر وجد ارتفاعًا سالبًا.
        val noon = highestPoint("2026-10-19", cairo.first, cairo.second, 7200)
        assertTrue("${noon.altitude}", noon.altitude > 0)
    }

    @Test
    fun theSunIsHighestAroundNoonAndLowestAtNight() {
        // ⚠️ الارتفاع **يتصاعد ثم ينحدر** — علاقةٌ لا أرقام.
        val morning = SunQibla.position(at("2026-10-19T06:00:00Z"), cairo.first, cairo.second, 7200)
        val noon = SunQibla.position(at("2026-10-19T10:00:00Z"), cairo.first, cairo.second, 7200)
        val night = SunQibla.position(at("2026-10-19T22:00:00Z"), cairo.first, cairo.second, 7200)
        assertTrue(noon.altitude > morning.altitude)
        assertTrue(morning.altitude > night.altitude)
        assertTrue(night.altitude < 0)
    }

    @Test
    fun theAzimuthIsAlwaysNormalized() {
        for (hour in 0..23) {
            val instant = at("2026-10-19T%02d:00:00Z".format(hour))
            val position = SunQibla.position(instant, cairo.first, cairo.second, 7200)
            assertTrue("$hour → ${position.azimuth}", position.azimuth >= 0.0 && position.azimuth < 360.0)
        }
    }

    // ── الظلّ ────────────────────────────────────────────

    @Test
    fun theShadowMomentIsFoundOnTheDayItExists() {
        // ⚠️ **في اليوم الذي فيه** يظهر الظلّ متّجهًا إلى
        // القبلة، لا في يومٍ آخر. فنتحقّق أن اللحظة **تحقّق
        // الشرط** فعلًا: اتجاه الشمس = القبلة + ١٨٠.
        val moment = SunQibla.shadowMoment(
            LocalDate.of(2026, 6, 21),
            newYork.first,
            newYork.second,
            ZoneId.of("UTC"),
        )
        assertNotNull(moment)
        val bearing = SunQibla.qiblaBearing(newYork.first, newYork.second)
        val target = (bearing + 180.0) % 360.0
        var error = abs(moment!!.azimuth - target)
        if (error > 180.0) error = 360.0 - error
        assertTrue("error=$error", error < 1.0)
        assertTrue("altitude=${moment.altitude}", moment.altitude > 0)
    }

    @Test
    fun thereIsNoShadowMomentWhenTheSunNeverReachesThatDirection() {
        // ⚠️ فيماكن لا تبلغ الشمس ذلك الاتجاه نهارًا فلا
        // لحظة — والاختيار هنا: **لا نخترع لحظة**.
        val moment = SunQibla.shadowMoment(
            LocalDate.of(2026, 12, 21),
            cairo.first,
            cairo.second,
            ZoneId.of("UTC"),
        )
        assertNull(moment)
    }

    @Test
    fun theShadowPointsToTheQiblaRightNowWhenItDoes() {
        // ⚠️ **الاتّساق بين الفحصين**: ما يقوله `shadowMoment`
        // وما يقوله `shadowPointsToQibla` لا يختلفان.
        val date = LocalDate.of(2026, 6, 21)
        val moment = SunQibla.shadowMoment(date, newYork.first, newYork.second, ZoneId.of("UTC"))
        assertNotNull(moment)
        val instant = date.atStartOfDay(ZoneId.of("UTC")).plusSeconds(moment!!.minutesOfDay * 60L).toInstant()
        assertTrue(
            SunQibla.shadowPointsToQibla(
                instant, newYork.first, newYork.second, 0, toleranceDegrees = 2.0,
            ),
        )
    }

    @Test
    fun theShadowNeverPointsToTheQiblaAtNight() {
        // ⚠️ ليلًا **لا ظلّ أصلًا**، فالحكم لا يكون إيجابيًّا.
        assertTrue(
            !SunQibla.shadowPointsToQibla(
                at("2026-06-21T02:00:00Z"), newYork.first, newYork.second, 0,
            ),
        )
    }
}
