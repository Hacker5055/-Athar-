package com.athar.athar.adhkar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/// ⚠️ عيّنة تحاكي **أسماء الأقسام الحقيقية** لا نصًّا من عندنا: الخطأ
/// هنا كان تكريس قسم داخل الصلاة مكان قسم بعدها، ولا يُكشف ذلك إلا
/// بعنوانين متجاورين من الملف نفسه.
class AdhkarSectionsTest {

    private val beforeTasleem =
        AdhkarSection("الدعاء بعد التشهد الأخير وقبل السلام", listOf(Dhikr("دعاء", 1, "متفق عليه")))
    private val afterPrayer =
        AdhkarSection("الأَذْكَارُ بَعْدَ السَّلَامِ مِنَ الصَّلَاةِ", listOf(Dhikr("أستغفر الله", 3, "مسلم")))
    private val morning =
        AdhkarSection("أَذْكَارُ الصَّبَاحِ", listOf(Dhikr("ذكر", 1, "أبو داود")))
    private val evening =
        AdhkarSection("أَذْكَارُ المَسَاءِ", listOf(Dhikr("ذكر", 1, "أبو داود")))
    private val sleep =
        AdhkarSection("أَذْكَارُ النَّوْمِ", listOf(Dhikr("ذكر", 1, "البخاري")))
    private val istikharah =
        AdhkarSection("دُعَاءُ صَلَاةِ الاسْتِخَارَةِ", listOf(Dhikr("دعاء", 1, "البخاري")))
    private val travel =
        AdhkarSection("دُعَاءُ السَّفَرِ", listOf(Dhikr("دعاء", 1, "أبو داود")))

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

    /// ⚠️ أذكارُ اليومِ تُنتقى **بترتيبِها** لا بترتيبِ المصدر:
    /// الصباحُ أوّلًا لأنها أكثرُها طلبًا.
    @Test fun dailyKeepsTheDeclaredOrder() {
        val daily = AdhkarSections.daily(listOf(travel, sleep, evening, morning, istikharah))
        assertEquals(listOf(morning, evening, sleep, istikharah, travel), daily)
    }

    /// ⚠️ العنوانُ المشكولُ في المصدر لا يَمنعُ لقاعَه.
    @Test fun dailyMatchesVocalisedTitles() {
        assertEquals(5, AdhkarSections.daily(listOf(morning, evening, sleep, istikharah, travel)).size)
    }

    /// ⚠️ وما لا يُوجدُ يُسقَط: لا عنوانَ فارغًا ولا موضعًا غلط.
    @Test fun dailySkipsAbsentSections() {
        assertEquals(listOf(morning), AdhkarSections.daily(listOf(morning, evening)))
        assertTrue(AdhkarSections.daily(emptyList()).isEmpty())
    }

    /// ⚠️ «أذكار الصباح» لا «أذكار المساء» — فالمطابقةُ تامّة.
    @Test fun dailyDoesNotMatchAnotherSection() {
        assertTrue(AdhkarSections.daily(listOf(evening)).isEmpty())
    }
}
