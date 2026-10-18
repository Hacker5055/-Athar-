package com.athar.athar.qibla

import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Qibla
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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

    /// النقيضان يختلفان ١٨٠ درجة بالضبط
    @Test fun antipodeDiffersByHalfTurn() {
        val a = qibla(30.0, 31.0)
        val b = qibla(-30.0, -149.0)
        val delta = kotlin.math.abs(QiblaMath.normalize(a - b) - 180.0)
        assertTrue("الفرق عن ١٨٠ كان $delta", delta < 1.0)
    }
}
