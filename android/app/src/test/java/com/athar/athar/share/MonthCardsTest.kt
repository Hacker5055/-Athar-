package com.athar.athar.share

import com.athar.athar.hijri.HijriCalendar
import com.athar.athar.prayer.MonthRow
import com.athar.athar.prayer.PrayerDay
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/// اختبارات بطاقة الشهر (A12 — تصدير التقويم الشهري).
///
/// ⚠️ الفحوص على **ما يُقرأ**: الجدولُ صورةٌ تُعلَّق، فلا تحتمل
/// «ص/م» ولا أرقامًا لاتينيةً في بطاقةٍ عربية.
class MonthCardsTest {

    private val headers = listOf("الفجر", "الشروق", "الظهر", "العصر", "المغرب", "العشاء")

    private fun row(day: Int): MonthRow {
        val date = java.time.LocalDate.of(2026, 10, day)
        val at = date.atStartOfDay(ZoneOffset.UTC).toInstant()
        return MonthRow(
            date = date,
            hijri = HijriCalendar.from(date, 0),
            day = PrayerDay(
                date = date,
                fajr = at.plus(4, ChronoUnit.HOURS),
                sunrise = at.plus(6, ChronoUnit.HOURS),
                dhuhr = at.plus(12, ChronoUnit.HOURS),
                asr = at.plus(15, ChronoUnit.HOURS),
                maghrib = at.plus(18, ChronoUnit.HOURS),
                isha = at.plus(20, ChronoUnit.HOURS),
            ),
        )
    }

    private fun dataOf(rows: List<MonthRow>, rtl: Boolean) = MonthCards.dataOf(
        rows = rows,
        title = "أثر",
        subtitle = "أكتوبر ٢٠٢٦",
        headers = headers,
        footer = "مواقيت الصلاة — القاهرة — أثر",
        rtl = rtl,
        zone = ZoneOffset.UTC,
    )

    @Test
    fun everyDayBecomesARow() {
        val rows = (1..31).map { row(it) }
        val data = dataOf(rows, rtl = true)
        assertEquals(31, data.rows.size)
    }

    @Test
    fun eachRowHasTheSixTimes() {
        val data = dataOf(listOf(row(1)), rtl = true)
        assertEquals(6, data.rows[0].times.size)
    }

    @Test
    fun theTimesAreInTwentyFourHours() {
        // ⚠️ **٢٤ ساعة**: جدولٌ يُعلَّق لا تُكتب فيه «ص/م».
        val data = dataOf(listOf(row(1)), rtl = false)
        assertEquals(
            listOf("04:00", "06:00", "12:00", "15:00", "18:00", "20:00"),
            data.rows[0].times,
        )
    }

    @Test
    fun theArabicCardUsesArabicDigits() {
        val data = dataOf(listOf(row(1)), rtl = true)
        assertEquals("٠٤:٠٠", data.rows[0].times[0])
        assertEquals("١", data.rows[0].day)
    }

    @Test
    fun theLatinCardKeepsLatinDigits() {
        val data = dataOf(listOf(row(1)), rtl = false)
        assertEquals("04:00", data.rows[0].times[0])
        assertEquals("1", data.rows[0].day)
    }

    @Test
    fun theDirectionIsCarriedWithTheData() {
        // ⚠️ **الاتجاهُ في البيانات**: الجدولُ يُرسم من اليمين في
        // العربية، ومن يقرأ البيانات بلا علمٍ بالرسم رسمه معكوسًا.
        assertEquals(true, dataOf(listOf(row(1)), rtl = true).rtl)
        assertEquals(false, dataOf(listOf(row(1)), rtl = false).rtl)
    }

    @Test
    fun theHijriDayIsThereToo() {
        // ⚠️ **الميلادي والهجري معًا**: التقويم الذي يُظهر ميلاديًّا
        // فقط يفقد معناه عند من يعرف الشهر برمضان.
        val data = dataOf(listOf(row(1)), rtl = false)
        assertEquals(
            HijriCalendar.from(java.time.LocalDate.of(2026, 10, 1), 0).day.toString(),
            data.rows[0].hijri,
        )
    }
}
