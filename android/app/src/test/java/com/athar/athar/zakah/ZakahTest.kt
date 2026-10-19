package com.athar.athar.zakah

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات حاسبة الزكاة (G5).
///
/// ⚠️ الفحص على **علاقات**: أنّ ما دون النصاب لا زكاة فيه، وأنّ
/// الزكاة ربع العشر من *القيمة* لا من الوزن، وأنّ نصاب النقود
/// هو **الأقلّ** من الذهب والفضة.
class ZakahTest {

    private val tolerance = 0.0001

    // ── النِّسبة ─────────────────────────────────────────

    @Test
    fun theRateIsATwentyFifth() {
        assertEquals(2.5, Zakah.RATE_PERCENT, tolerance)
    }

    @Test
    fun theZakahIsATwentyFifthOfTheValue() {
        // ⚠️ من الوزن لا من القيمة: جرام الذهب قيمته تختلف
        // عن قيمته.
        val r = Zakah.gold(100.0, 50.0)
        assertEquals(5000.0, r.amount, tolerance)
        assertEquals(125.0, r.due, tolerance)
    }

    // ── النصاب ──────────────────────────────────────────

    @Test
    fun theNisabOfGoldIsEightyFiveGrams() {
        assertEquals(85.0, Zakah.NISAB_GOLD_GRAMS, tolerance)
    }

    @Test
    fun theNisabOfSilverIsFiveHundredAndNinetyFiveGrams() {
        assertEquals(595.0, Zakah.NISAB_SILVER_GRAMS, tolerance)
    }

    @Test
    fun goldBelowTheNisabIsNotLiable() {
        val r = Zakah.gold(84.9, 50.0)
        assertFalse(r.isLiable)
        assertEquals(0.0, r.due, tolerance)
    }

    @Test
    fun goldAtTheNisabIsLiable() {
        // ⚠️ **عند النصاب بالضبط** مدينٌ: النصاب «بلغ»=
        // reached. فمن ساوى عنده أقلّ موضعًا.
        val r = Zakah.gold(85.0, 50.0)
        assertTrue(r.isLiable)
        assertEquals(85.0 * 50.0 * 0.025, r.due, tolerance)
    }

    @Test
    fun silverBelowTheNisabIsNotLiable() {
        assertFalse(Zakah.silver(594.0, 1.0).isLiable)
        assertTrue(Zakah.silver(595.0, 1.0).isLiable)
    }

    @Test
    fun theNisabIsWorthTheNisabWeight() {
        val r = Zakah.gold(10.0, 50.0)
        assertEquals(85.0 * 50.0, r.nisab, tolerance)
    }

    // ── النقود ──────────────────────────────────────────

    @Test
    fun theCashNisabIsTheSmallerOfGoldAndSilver() {
        // ⚠️ **الأقلّ** أوسَع: من بلغ أحدهما بلغ النصاب. ولو
        // أخذنا الأكبر لأَخطأنا الناس.
        val gold = Zakah.nisabInCash(50.0, 1.0)
        val silver = Zakah.nisabInCash(50.0, 1.0)
        assertEquals(minOf(85.0 * 50.0, 595.0 * 1.0), gold, tolerance)
        assertEquals(gold, silver, tolerance)
    }

    @Test
    fun theCashNisabFollowsThePrices() {
        val cheap = Zakah.nisabInCash(10.0, 0.5)
        val dear = Zakah.nisabInCash(100.0, 5.0)
        assertTrue(cheap < dear)
    }

    @Test
    fun cashBelowTheNisabIsNotLiable() {
        val nisab = Zakah.nisabInCash(50.0, 1.0)
        val r = Zakah.cash(nisab - 1.0, 50.0, 1.0)
        assertFalse(r.isLiable)
        assertEquals(0.0, r.due, tolerance)
    }

    @Test
    fun cashAboveTheNisabIsLiable() {
        val nisab = Zakah.nisabInCash(50.0, 1.0)
        val r = Zakah.cash(nisab * 2, 50.0, 1.0)
        assertTrue(r.isLiable)
        assertEquals(nisab * 2 * 0.025, r.due, tolerance)
    }

    // ── زكاة الفطر ──────────────────────────────────────

    @Test
    fun theFitrQuantityIsTwoAndAHalfKilosPerPerson() {
        assertEquals(2.5, Zakah.FITR_KG_PER_PERSON, tolerance)
    }

    @Test
    fun theFitrIsTheValueOfTheFood() {
        // ⚠️ **قيمة القوت** لا نقدًا زائدًا: زكاة الفطر تُؤدّى
        // قوتًا، فحسابها مالٌ خطأ.
        val r = Zakah.fitr(4, 20.0)
        assertEquals(4 * 2.5 * 20.0, r.amount, tolerance)
        assertEquals(4 * 2.5 * 20.0, r.due, tolerance)
    }

    @Test
    fun noPeopleMeansNoFitr() {
        val r = Zakah.fitr(0, 20.0)
        assertFalse(r.isLiable)
        assertEquals(0.0, r.due, tolerance)
    }

    @Test
    fun onePersonIsLiable() {
        assertTrue(Zakah.fitr(1, 20.0).isLiable)
    }

    // ── الحالات الحدّية ─────────────────────────────────

    @Test
    fun zeroIsNeverLiable() {
        assertFalse(Zakah.gold(0.0, 50.0).isLiable)
        assertFalse(Zakah.silver(0.0, 1.0).isLiable)
        assertFalse(Zakah.cash(0.0, 50.0, 1.0).isLiable)
    }

    @Test
    fun aZeroPriceMeansNoNisabAndNoLiability() {
        // ⚠️ سعرٌ صفري يجعل النصاب صفرًا، فيصير كلّ مبلغ
        // «بلغ النصاب» — وهذا خطأٌ يُلخبط صاحب التطبيق.
        val r = Zakah.gold(100.0, 0.0)
        assertFalse(r.isLiable)
        assertEquals(0.0, r.due, tolerance)
    }

    @Test
    fun theAmountIsNeverNegative() {
        for (grams in listOf(0.0, 10.0, 85.0, 1000.0)) {
            val r = Zakah.gold(grams, 50.0)
            assertTrue("$grams", r.amount >= 0)
            assertTrue("$grams", r.due >= 0)
            assertTrue("$grams", r.nisab >= 0)
        }
    }
}
