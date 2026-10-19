package com.athar.athar.core

import org.junit.Assert.assertEquals
import org.junit.Test

/// اختبارات الأرقام العربية — وحدة مشتركة بين الودجت والسبحة.
class ArabicDigitsTest {

    @Test
    fun zeroIsItsOwnCase() {
        assertEquals("٠", ArabicDigits.of(0))
    }

    @Test
    fun singleAndMultiDigit() {
        assertEquals("٧", ArabicDigits.of(7))
        assertEquals("٣٣", ArabicDigits.of(33))
        assertEquals("١١٤", ArabicDigits.of(114))
    }

    @Test
    fun largeValuesKeepEveryDigit() {
        assertEquals("١٠٠٠", ArabicDigits.of(1000))
        assertEquals("٦٢٣٦", ArabicDigits.of(6236))
    }

    @Test
    fun negativeKeepsTheSign() {
        // ⚠️ لا نخترع علامة سالب عربية: المستخدم يقرأ «-» كما في كل
        // تطبيق آخر، وابتكار رمز يجعله يبحث عن معناه.
        assertEquals("-٥", ArabicDigits.of(-5))
    }

    @Test
    fun longOverloadMatchesInt() {
        assertEquals(ArabicDigits.of(1234), ArabicDigits.of(1234L))
    }

    @Test
    fun noLatinDigitsLeak() {
        val all = (0..1000).joinToString("") { ArabicDigits.of(it) }
        assertEquals("لا حرف لاتيني في المخرَج", true, all.none { it in '0'..'9' })
    }
}
