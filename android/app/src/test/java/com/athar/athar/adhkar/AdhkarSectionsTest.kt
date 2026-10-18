package com.athar.athar.adhkar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/// ⚠️ عيّنة تحاكي **أسماء الأقسام الحقيقية** لا نصًّا من عندنا: الخطأ
/// هنا كان تكريس قسم داخل الصلاة مكان قسم بعدها، ولا يُكشف ذلك إلا
/// بعنوانين متجاورين من الملف نفسه.
class AdhkarSectionsTest {

    private val beforeTasleem =
        AdhkarSection("الدعاء بعد التشهد الأخير وقبل السلام", listOf(Dhikr("دعاء", 1, "متفق عليه")))
    private val afterPrayer =
        AdhkarSection("الأَذْكَارُ بَعْدَ السَّلَامِ مِنَ الصَّلَاةِ", listOf(Dhikr("أستغفر الله", 3, "مسلم")))
    private val morning =
        AdhkarSection("أَذْكَارُ الصَّبَاحِ", listOf(Dhikr("ذكر", 1, "أبو داود")))

    @Test fun findsPostPrayerDespiteFullDiacritics() {
        assertEquals(afterPrayer, AdhkarSections.postPrayer(listOf(beforeTasleem, afterPrayer, morning)))
    }

    /// ⚠️ أهم اختبار في الملف: القسم المجاور «بعد التشهد … وقبل السلام»
    /// **داخل** الصلاة. قاعدة «بعد» + «سلام» تطابقه خطأً.
    @Test fun doesNotMatchTheSectionBeforeTasleem() {
        assertNull(AdhkarSections.postPrayer(listOf(beforeTasleem)))
    }

    @Test fun worksWhenSourceHasNoDiacritics() {
        val plain = AdhkarSection("الأذكار بعد السلام من الصلاة", listOf(Dhikr("أستغفر الله", 3, "مسلم")))
        assertNotNull(AdhkarSections.postPrayer(listOf(plain)))
    }

    @Test fun returnsNullWhenTheSectionIsAbsent() {
        assertNull(AdhkarSections.postPrayer(listOf(morning)))
        assertNull(AdhkarSections.postPrayer(emptyList()))
    }
}
