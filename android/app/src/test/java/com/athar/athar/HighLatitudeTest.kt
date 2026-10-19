package com.athar.athar.prayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات قواعد خطوط العرض العالية.
///
/// ⚠️ الاختبار الأهم هنا `everyRuleResolves`: أسماء ثوابت المكتبة
/// محفوظة لا مُتحقَّق منها عند الترجمة (استخدمنا `valueOf` داخل
/// `runCatching`). فالاختبار وحده هو ما يكشف اسمًا تغيّر في إصدار
/// جديد — قبل أن يصل إلى مستخدم في أوروبا بلا فجر صحيح.
class HighLatitudeTest {

    @Test
    fun everyRuleResolves() {
        for (rule in HighLatitude.entries) {
            assertNotNull("${rule.ruleName} غير موجود في المكتبة", HighLatitudeRules.resolve(rule))
        }
    }

    @Test
    fun ruleNamesAreDistinct() {
        val names = HighLatitude.entries.map { it.ruleName }
        assertEquals(names.size, names.toSet().size)
    }

    @Test
    fun theDefaultResolvesToo() {
        assertNotNull(HighLatitudeRules.resolve(HighLatitude.DEFAULT))
    }

    @Test
    fun unknownNameFallsBackToDefault() {
        // ⚠️ اسم من تخزين قديم لا يُسقط الشاشة
        assertEquals(HighLatitude.DEFAULT, HighLatitude.fromName("غير موجود"))
        assertEquals(HighLatitude.DEFAULT, HighLatitude.fromName(null))
    }

    @Test
    fun knownNamesRoundTrip() {
        for (rule in HighLatitude.entries) {
            assertEquals(rule, HighLatitude.fromName(rule.name))
        }
    }

    @Test
    fun thereAreThreeRules() {
        // ⚠️ المكتبة تحمل ثلاثًا لا أقلّ ولا أكثر
        assertEquals(3, HighLatitude.entries.size)
    }

    @Test
    fun everyRuleHasATranslationKey() {
        assertTrue(HighLatitude.entries.all { it.nameKey.startsWith("hl_") })
    }
}
