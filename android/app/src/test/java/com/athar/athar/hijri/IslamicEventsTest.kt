package com.athar.athar.hijri

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/// اختبارات المناسبات — المنطق النقي.
///
/// ⚠️ كما في التقويم الهجري: **لا تاريخ ميلادي ثابت** في هذه
/// الاختبارات، لأن المناسبة تتحرّك ١٠–١١ يومًا كل سنة ميلاديّة،
/// وأي رقم أكتبه يصدق عامًا ويكذب أعوامًا. الذي يُختبر هو الثوابت.
class IslamicEventsTest {

    private val today = LocalDate.of(2026, 10, 19)

    @Test
    fun everyEventIsARealHijriDate() {
        for (e in IslamicEvents.ALL) {
            assertTrue("${e.id}: الشهر ${e.month}", e.month in 1..12)
            assertTrue("${e.id}: اليوم ${e.day}", e.day in 1..30)
        }
    }

    @Test
    fun idsAreUnique() {
        val ids = IslamicEvents.ALL.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun theKnownEventsAreAllPresent() {
        val ids = IslamicEvents.ALL.map { it.id }.toSet()
        for (needed in listOf(
            "hijri_new_year", "ashura", "mawlid", "isra",
            "ramadan", "qadr", "eid_fitr", "arafah", "eid_adha",
        )) {
            assertTrue("مناسبة ناقصة: $needed", needed in ids)
        }
    }

    @Test
    fun ramadanPrecedesEidAlFitr() {
        // ⚠️ ترتيب المستحيل: العيد قبل رمضان يعني تقويمًا معكوسًا
        val ramadan = IslamicEvents.ALL.first { it.id == "ramadan" }
        val eid = IslamicEvents.ALL.first { it.id == "eid_fitr" }
        assertTrue(ramadan.month < eid.month)
    }

    @Test
    fun arafahPrecedesEidAlAdha() {
        val arafah = IslamicEvents.ALL.first { it.id == "arafah" }
        val adha = IslamicEvents.ALL.first { it.id == "eid_adha" }
        assertTrue(arafah.month == adha.month && arafah.day < adha.day)
    }

    @Test
    fun laylatAlQadrIsInTheLastTenNightsOfRamadan() {
        val qadr = IslamicEvents.ALL.first { it.id == "qadr" }
        assertEquals("ليلة القدر في رمضان", 9, qadr.month)
        assertTrue("في العشر الأواخر", qadr.day >= 21)
    }

    // ── الوقوع القادم ────────────────────────────────────

    @Test
    fun nextOccurrenceIsNeverInThePast() {
        for (e in IslamicEvents.ALL) {
            val at = IslamicEvents.nextOccurrence(e, today)
            assertTrue("${e.id} وقعت في الماضي: $at", at == null || !at.isBefore(today))
        }
    }

    @Test
    fun nextOccurrenceIsWithinOneHijriYear() {
        // ⚠️ السنة الهجريّة ٣٥٤ يومًا: ما جاوز ٣٦٠ فهو خطأ في
        // اختيار السنة، لا مناسبة بعيدة.
        for (e in IslamicEvents.ALL) {
            val at = IslamicEvents.nextOccurrence(e, today) ?: continue
            val days = ChronoUnit.DAYS.between(today, at)
            assertTrue("${e.id}: $days يومًا", days in 0..360)
        }
    }

    @Test
    fun everyEventHasAnOccurrence() {
        for (e in IslamicEvents.ALL) {
            assertTrue("${e.id} بلا وقوع", IslamicEvents.nextOccurrence(e, today) != null)
        }
    }

    @Test
    fun laterDatesNeverPushAnEventEarlier() {
        // ⚠️ تقدّم الزمن لا يجعل المناسبة أقرب بلا حدّ: إن مرّت
        // انتقلت إلى العام الهجري التالي، فقفز العدّ إلى ~٣٥٤.
        for (e in IslamicEvents.ALL) {
            val before = IslamicEvents.daysUntil(e, today) ?: continue
            val after = IslamicEvents.daysUntil(e, today.plusDays(30)) ?: continue
            assertTrue(
                "${e.id}: $before → $after",
                after >= before - 30,
            )
        }
    }

    // ── العدّاد ──────────────────────────────────────────

    @Test
    fun daysUntilIsNeverNegative() {
        for (e in IslamicEvents.ALL) {
            val d = IslamicEvents.daysUntil(e, today)
            assertTrue("${e.id}: $d", d == null || d >= 0)
        }
    }

    @Test
    fun daysUntilMatchesTheOccurrence() {
        for (e in IslamicEvents.ALL) {
            val at = IslamicEvents.nextOccurrence(e, today) ?: continue
            assertEquals(at, today.plusDays(IslamicEvents.daysUntil(e, today)!!.toLong()))
        }
    }

    // ── القائمة ──────────────────────────────────────────

    @Test
    fun upcomingIsSortedByDate() {
        val list = IslamicEvents.upcoming(today)
        for (i in 0 until list.size - 1) {
            assertTrue("غير مرتّبة", !list[i + 1].first.isBefore(list[i].first))
        }
    }

    @Test
    fun upcomingRespectsTheLimit() {
        assertTrue(IslamicEvents.upcoming(today, limit = 2).size <= 2)
        assertTrue(IslamicEvents.upcoming(today, limit = 100).size <= IslamicEvents.ALL.size)
    }

    @Test
    fun upcomingIsNotEmpty() {
        assertTrue(IslamicEvents.upcoming(today).isNotEmpty())
    }
}
