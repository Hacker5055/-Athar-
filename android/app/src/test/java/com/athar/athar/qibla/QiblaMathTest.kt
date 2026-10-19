package com.athar.athar.qibla

import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Qibla
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class QiblaMathTest {

    private fun qibla(lat: Double, lng: Double) = Qibla(Coordinates(lat, lng)).direction

    @Test fun normalizeWraps() {
        assertEquals(0.0, QiblaMath.normalize(0.0), 1e-9)
        assertEquals(10.0, QiblaMath.normalize(370.0), 1e-9)
        assertEquals(350.0, QiblaMath.normalize(-10.0), 1e-9)
        assertEquals(0.0, QiblaMath.normalize(720.0), 1e-9)
        assertEquals(359.5, QiblaMath.normalize(-0.5), 1e-9)
    }

    /// ⚠️ أخطر سطر في الشاشة: البوصلة مغناطيسية والقبلة جغرافية
    @Test fun declinationIsApplied() {
        assertEquals(10.0, QiblaMath.trueAzimuth(350.0, 20.0), 1e-9)
        assertEquals(350.0, QiblaMath.trueAzimuth(10.0, -20.0), 1e-9)
        assertEquals(0.0, QiblaMath.trueAzimuth(0.0, 0.0), 1e-9)
        // عبور الشمال: لا يجوز أن يقفز إلى ٣٦٠
        assertEquals(5.0, QiblaMath.trueAzimuth(355.0, 10.0), 1e-9)
    }

    /// ⚠️ بلا التطبيع يدور المؤشّر دورة كاملة عند عبور الشمال
    @Test fun offsetTakesShortestTurn() {
        assertEquals(135.0, QiblaMath.offsetTo(135.0, 0.0), 1e-9)
        assertEquals(-135.0, QiblaMath.offsetTo(0.0, 135.0), 1e-9)
        assertEquals(20.0, QiblaMath.offsetTo(10.0, 350.0), 1e-9)
        assertEquals(-20.0, QiblaMath.offsetTo(350.0, 10.0), 1e-9)
        assertEquals(0.0, QiblaMath.offsetTo(136.0, 136.0), 1e-9)
    }

    @Test fun canvasAngleOffsetsByNinety() {
        assertEquals(-90.0, QiblaMath.canvasAngle(0.0), 1e-9)
        assertEquals(0.0, QiblaMath.canvasAngle(90.0), 1e-9)
    }

    /// حقائق جغرافية **مؤكدة** لا قيم محفوظة: مكة جنوب شرق القاهرة،
    /// وشمال شرق نيويورك، وشمال غرب سيدني.
    @Test fun bearingPointsTheRightWay() {
        val cairo = qibla(30.0444, 31.2357)
        assertTrue("القاهرة: مكة جنوب شرقًا، فالزاوية بين ٩٠ و١٨٠ (كانت $cairo)",
            cairo > 90.0 && cairo < 180.0)

        val newYork = qibla(40.7128, -74.0060)
        assertTrue("نيويورك: مكة شمال شرقًا، فالزاوية بين ٠ و٩٠ (كانت $newYork)",
            newYork > 0.0 && newYork < 90.0)

        val london = qibla(51.5074, -0.1278)
        assertTrue("لندن: مكة جنوب شرقًا، فالزاوية بين ٩٠ و١٨٠ (كانت $london)",
            london > 90.0 && london < 180.0)

        // ⚠️ مكة في الشمال الغربي من سيدني، لا الشمال الشرقي — خطأ
        // شائع في حسابات نصف الكرة الجنوبي
        val sydney = qibla(-33.8688, 151.2093)
        assertTrue("سيدني: مكة شمال غربًا، فالزاوية بين ٢٧٠ و٣٦٠ (كانت $sydney)",
            sydney > 270.0 && sydney < 360.0)
    }

    /// ⚠️ كان هنا اختبار يزعم أن زاوية القبلة من نقطة ومن نقيضها تختلف
    /// ١٨٠ درجة بالضبط. بدا بديهيًا فكتبته — وهو **خاطئ**: الفرق الحقيقي
    /// ٨٩.٧ درجة، ولا علاقة بسيطة بينهما أصلًا. تركته لأنه «معقول» لا
    /// لأني تحقّقت منه، فأوقف البناء يومًا كاملًا بسبب وهم.
    ///
    /// البديل يقيس شيئًا صحيحًا ويهمّ فعلًا: أن زاوية Adhan تطابق حسابًا
    /// مستقلًا للدائرة الكبرى. هو يصطاد بالضبط صنف الخطأ الذي يهمّ هنا —
    /// تبديل خط العرض بالطول، أو الراديان بالدرجة — وهي أخطاء لا يظهرها
    /// اختبارٌ يقارن رقمًا محفوظًا برقم محفوظ.
    @Test fun agreesWithIndependentBearing() {
        val meccaLat = 21.4225
        val meccaLng = 39.8262
        for (point in listOf(
            "القاهرة" to (30.0444 to 31.2357),
            "نيويورك" to (40.7128 to -74.0060),
            "لندن" to (51.5074 to -0.1278),
            "سيدني" to (-33.8688 to 151.2093),
        )) {
            val (name, coords) = point
            val (lat, lng) = coords
            val fromAdhan = qibla(lat, lng)
            val independent = greatCircleBearing(lat, lng, meccaLat, meccaLng)
            val gap = QiblaMath.normalize(fromAdhan - independent)
            val delta = if (gap > 180.0) 360.0 - gap else gap
            assertTrue(
                "$name: Adhan=$fromAdhan مقابل حساب مستقل=$independent (الفرق $delta)",
                delta < 1.0,
            )
        }
    }

    /// حساب الدائرة الكبرى — مكتوب هنا قصدًا، مستقلًا عن Adhan، ليكون
    /// شاهدًا عليه لا صدًى له.
    private fun greatCircleBearing(
        fromLat: Double,
        fromLng: Double,
        toLat: Double,
        toLng: Double,
    ): Double {
        val phi1 = Math.toRadians(fromLat)
        val phi2 = Math.toRadians(toLat)
        val deltaLambda = Math.toRadians(toLng - fromLng)
        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        return QiblaMath.normalize(Math.toDegrees(atan2(y, x)))
    }
}
