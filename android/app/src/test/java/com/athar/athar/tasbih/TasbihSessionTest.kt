package com.athar.athar.tasbih

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات سجلّ جلسات التسبيح (E6).
///
/// ⚠️ الفحص على **العلاقة** بين الجلسة والجار: جلسةٌ تستمرّ
/// متّصلة، وأخرى تُفتح بانقطاع الأثر. والمعنى: **دفعاتٌ** لا
/// أيام.
class TasbihSessionTest {

    private val start = 1_763_000_000_000L
    private val minute = 60_000L

    // ── فتح الجلسة والاستمرار ────────────────────────────

    @Test
    fun theFirstTapOpensASession() {
        val session = TasbihSessions.onTap(null, start, 33)
        assertEquals(1, session.count)
        assertEquals(start, session.startedAtMillis)
        assertEquals(start, session.lastTapAtMillis)
        assertEquals(33, session.target)
    }

    @Test
    fun closeTapsStayInOneSession() {
        var session = TasbihSessions.onTap(null, start, 33)
        session = TasbihSessions.onTap(session, start + minute, 33)
        session = TasbihSessions.onTap(session, start + 2 * minute, 33)
        assertEquals(3, session.count)
        assertEquals(start, session.startedAtMillis)
    }

    @Test
    fun aLongPauseOpensANewSession() {
        // ⚠️ **انقطاع الأثر** يفصل: فمن غاب بين
        // الشهادتين فهما جلستان.
        val first = TasbihSessions.onTap(null, start, 33)
        val gap = TasbihSessions.GAP_MILLIS + 1
        val second = TasbihSessions.onTap(first, start + gap, 33)
        assertEquals(1, second.count)
        assertEquals(start + gap, second.startedAtMillis)
    }

    @Test
    fun theGapIsTwoMinutes() {
        assertEquals(120_000L, TasbihSessions.GAP_MILLIS)
    }

    @Test
    fun aSessionIsNotClosedWhileItIsActive() {
        val session = TasbihSessions.onTap(null, start, 33)
        assertNull(TasbihSessions.closeIfStale(session, start + minute))
    }

    @Test
    fun aSessionIsClosedAfterTheGap() {
        val session = TasbihSessions.onTap(null, start, 33)
        val closed = TasbihSessions.closeIfStale(session, start + TasbihSessions.GAP_MILLIS + 1)
        assertNotNull(closed)
        assertEquals(1, closed!!.count)
    }

    @Test
    fun thereIsNothingToCloseWithoutASession() {
        assertNull(TasbihSessions.closeIfStale(null, start))
    }

    @Test
    fun theDurationIsNeverNegative() {
        val session = TasbihSessions.onTap(null, start, 33)
        assertTrue(session.durationMillis(start - 10_000L) >= 0)
        assertEquals(0L, session.durationMillis(start))
    }

    // ── السجلّ ───────────────────────────────────────────

    @Test
    fun theNewestSessionComesFirst() {
        val first = TasbihSessions.onTap(null, start, 33)
        val second = TasbihSessions.onTap(null, start + 10 * minute, 33)
        val sessions = TasbihSessions.add(
            TasbihSessions.add(emptyList(), first),
            second,
        )
        assertEquals(2, sessions.size)
        assertEquals(second.startedAtMillis, sessions[0].startedAtMillis)
    }

    @Test
    fun theLogIsCapped() {
        // ⚠️ السقف: ملفٌّ بلا حدٍّّ ينموّ حتى يثقل التخزين.
        var sessions = emptyList<TasbihSession>()
        for (index in 0 until 150) {
            sessions = TasbihSessions.add(
                sessions,
                TasbihSessions.onTap(null, start + index * minute, 33),
            )
        }
        assertEquals(TasbihSessions.MAX_SESSIONS, sessions.size)
    }

    @Test
    fun theSummaryCountsTheRangeAndTheTotal() {
        val today = TasbihSessions.onTap(null, start, 33)
        val old = TasbihSessions.onTap(null, start - 10 * 24 * 60 * minute, 33)
        val sessions = TasbihSessions.add(
            TasbihSessions.add(emptyList(), today),
            old,
        )
        val summary = TasbihSessions.summary(sessions, start - minute, start + minute)
        assertEquals(1, summary.rangeCount)
        assertEquals(1, summary.rangeSessions)
        assertEquals(2, summary.totalCount)
    }

    @Test
    fun anEmptyLogHasAnEmptySummary() {
        val summary = TasbihSessions.summary(emptyList(), start, start + 1000)
        assertEquals(0, summary.rangeCount)
        assertEquals(0, summary.rangeSessions)
        assertEquals(0, summary.totalCount)
        assertEquals(0, summary.bestSession)
    }

    @Test
    fun theBestSessionIsTheLongestOne() {
        val small = TasbihSessions.onTap(null, start, 33)
        var big = TasbihSessions.onTap(null, start + minute, 33)
        for (index in 0 until 10) {
            big = TasbihSessions.onTap(big, start + minute + index * 1000L, 33)
        }
        val sessions = TasbihSessions.add(
            TasbihSessions.add(emptyList(), small),
            big,
        )
        assertEquals(big.count, TasbihSessions.summary(sessions, 0, Long.MAX_VALUE).bestSession)
    }

    @Test
    fun theDayBoundariesAreHalfOpen() {
        // ⚠️ **من ضمن** البداية إلى ما **قبل** النهاية: فلا
        // تُحسب جلسةُ منتصف الليل مرّتين.
        val session = TasbihSessions.onTap(null, start, 33)
        val summary = TasbihSessions.summary(listOf(session), start, start + 1)
        assertEquals(1, summary.rangeCount)
        val outside = TasbihSessions.summary(listOf(session), start + 1, start + 2)
        assertEquals(0, outside.rangeCount)
    }
}
