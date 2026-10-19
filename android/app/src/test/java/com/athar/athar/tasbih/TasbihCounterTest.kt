package com.athar.athar.tasbih

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات عدّاد السبحة — المنطق النقي.
///
/// ⚠️ العدّاد هو الميزة كلها: رقم خاطئ في سبحة تطبيق إسلامي ليس خللًا
/// في العرض، بل كذب على المستخدم في عبادته.
class TasbihCounterTest {

    private val start = TasbihState()

    @Test
    fun oneTapIsOne() {
        assertEquals(1, TasbihCounter.tap(start, 33).state.count)
    }

    @Test
    fun lifetimeCountsEveryTap() {
        var s = start
        repeat(5) { s = TasbihCounter.tap(s, 33).state }
        assertEquals(5, s.lifetime)
    }

    // ── الوصول إلى الهدف ──────────────────────────────────

    @Test
    fun reachingTargetCompletesARoundAndContinues() {
        // ⚠️ عدّاد يتجمّد عند الهدف يجعل التسبيح ينتهي قبل أن ينتهي
        var s = start
        var completed = false
        repeat(33) {
            val t = TasbihCounter.tap(s, 33)
            s = t.state
            completed = t.completed
        }
        assertEquals(1, s.rounds)
        assertEquals(0, s.count)
        assertEquals(33, s.lifetime)
        assertTrue("الاهتزاز عند الإتمام", completed)
    }

    @Test
    fun completedIsFalseBeforeTheTarget() {
        assertFalse(TasbihCounter.tap(start, 33).completed)
        assertFalse(TasbihCounter.tap(TasbihState(count = 31), 33).completed)
    }

    @Test
    fun targetIsNeverZeroOrNegative() {
        // ⚠️ هدف صفر يقسم العدّ أو يكمله في النقرة الأولى
        val t = TasbihCounter.tap(start, 0)
        assertEquals(1, t.state.rounds)
        assertEquals(0, t.state.count)
    }

    @Test
    fun hundredTargetCountsAHundred() {
        var s = start
        var done = false
        repeat(100) {
            val t = TasbihCounter.tap(s, 100)
            s = t.state
            done = t.completed
        }
        assertTrue(done)
        assertEquals(1, s.rounds)
        assertEquals(100, s.lifetime)
    }

    // ── الرجوع ────────────────────────────────────────────

    @Test
    fun undoStepsBack() {
        val s = TasbihCounter.tap(TasbihCounter.tap(start, 33).state, 33).state
        assertEquals(1, TasbihCounter.undo(s, 33).count)
    }

    @Test
    fun undoCrossesTheRoundBoundary() {
        // ⚠️ العدّاد على صفر ودورة مكتملة يعود إلى «٣٢» لا إلى صفر؛
        // ومن لا يعبر يرى الرجوع لا يعمل في أهم موضع.
        var s = start
        repeat(33) { s = TasbihCounter.tap(s, 33).state }
        val back = TasbihCounter.undo(s, 33)
        assertEquals(32, back.count)
        assertEquals(0, back.rounds)
        assertEquals(32, back.lifetime)
    }

    @Test
    fun undoNeverGoesBelowZero() {
        val back = TasbihCounter.undo(start, 33)
        assertEquals(0, back.count)
        assertEquals(0, back.lifetime)
        assertEquals(0, back.rounds)
    }

    @Test
    fun undoKeepsLifetimeConsistent() {
        var s = start
        repeat(40) { s = TasbihCounter.tap(s, 33).state }
        val up = s.lifetime
        assertEquals(up - 1, TasbihCounter.undo(s, 33).lifetime)
    }

    // ── التصفير ───────────────────────────────────────────

    @Test
    fun resetClearsTheRoundButNeverTheLifetime() {
        // ⚠️ التصفير يُنهي جلسة، ومحو الإجمالي يمحو أثرًا لا يُستعاد
        var s = start
        repeat(70) { s = TasbihCounter.tap(s, 33).state }
        val cleared = TasbihCounter.reset(s)
        assertEquals(0, cleared.count)
        assertEquals(0, cleared.rounds)
        assertEquals(70, cleared.lifetime)
    }

    @Test
    fun resetOfAFreshStateIsEmpty() {
        assertTrue(TasbihCounter.reset(start).isEmpty)
    }

    // ── القوالب ───────────────────────────────────────────

    @Test
    fun phrasesAreUniqueAndPositive() {
        val ids = TasbihPhrases.ALL.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        assertTrue(TasbihPhrases.ALL.all { it.defaultTarget > 0 })
        assertTrue(TasbihPhrases.ALL.all { it.arabic.isNotBlank() })
    }

    @Test
    fun unknownPhraseFallsBackNotCrashes() {
        // ⚠️ معرّف من تخزين قديم قد لا يكون موجودًا
        assertEquals(TasbihPhrases.SUBHAN, TasbihPhrases.byId("غير موجود"))
        assertEquals(TasbihPhrases.SUBHAN, TasbihPhrases.byId(null))
    }

    @Test
    fun targetsIncludeTheKnownCounts() {
        assertTrue(TasbihPhrases.TARGETS.containsAll(listOf(33, 66, 99, 100, 1000)))
    }
}
