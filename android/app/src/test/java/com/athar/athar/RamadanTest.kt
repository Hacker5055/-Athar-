package com.athar.athar.ramadan

import com.athar.athar.hijri.HijriDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات وضع رمضان — المنطق النقي.
///
/// ⚠️ كما في كل ما يتبع التقويم الهجري: **لا تاريخ ميلادي ثابت**
/// في هذه الاختبارات. الذي يُختبر هو الثوابت والحدود.
class RamadanTest {

    private fun ramadan(day: Int) = HijriDate(day = day, month = 9, year = 1448)
    private fun shawwal(day: Int) = HijriDate(day = day, month = 10, year = 1448)

    @Test
    fun ramadanIsTheNinthMonth() {
        assertEquals(9, Ramadan.MONTH)
        assertTrue(Ramadan.isRamadan(ramadan(1)))
        assertFalse(Ramadan.isRamadan(shawwal(1)))
        assertFalse(Ramadan.isRamadan(HijriDate(1, 8, 1448)))
    }

    @Test
    fun theDayIsOnlyGivenInsideRamadan() {
        assertEquals(1, Ramadan.dayOf(ramadan(1)))
        assertEquals(27, Ramadan.dayOf(ramadan(27)))
        assertNull(Ramadan.dayOf(shawwal(1)))
        assertNull(Ramadan.dayOf(HijriDate(9, 8, 1448)))
    }

    @Test
    fun theLengthIsTwentyNineOrThirty() {
        // ⚠️ الشهر الهجري قمريّ: ٢٩ أو ٣٠، لا ثالث لهما
        for (year in 1440..1470) {
            val length = Ramadan.lengthOf(year)
            assertTrue("سنة $year: $length", length == 29 || length == 30)
        }
    }

    @Test
    fun bothLengthsActuallyOccur() {
        // ⚠️ لو كان الجواب ٣٠ دائمًا لكان الثابت مبرَّرًا. وهذا
        // الاختبار يبرهن أنّ الحساب ضروريّ لا تحصيل حاصل.
        val lengths = (1440..1470).map { Ramadan.lengthOf(it) }.toSet()
        assertTrue("لم نجد إلا $lengths", lengths.size == 2)
    }

    @Test
    fun theLastTenStartAtTheTwentyFirst() {
        // ⚠️ الحدّ أهمّ ما هنا: «آخر عشرة بالعدّ التنازلي» تبدأ من
        // ٢٠ في سنة ٢٩ يومًا — واليوم العشرون ليس من الأواخر.
        assertFalse(Ramadan.isLastTen(20))
        assertTrue(Ramadan.isLastTen(21))
        assertTrue(Ramadan.isLastTen(27))
        assertTrue(Ramadan.isLastTen(30))
    }

    @Test
    fun theLastTenIncludesLaylatAlQadr() {
        // ⚠️ ليلة القدر في العشر الأواخر — فإن لم يكن ٢٧ منها فالبيانات
        // متناقضة مع قائمة المناسبات.
        assertTrue(Ramadan.isLastTen(27))
    }

    @Test
    fun theFirstDayIsNotInTheLastTen() {
        assertFalse(Ramadan.isLastTen(1))
    }
}
