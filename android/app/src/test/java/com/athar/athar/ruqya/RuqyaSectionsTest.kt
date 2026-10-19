package com.athar.athar.ruqya

import com.athar.athar.adhkar.AdhkarSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات أبواب الرقية (C9).
///
/// ⚠️ العناوينُ هنا **نسخةٌ حرفية** من `hisn.json`: والمطابقةُ
/// بعد التطبيع، فلو تغيّرت العناوينُ في المصدراختفت الأبوابُ
/// كلها دون أن يُظهر التطبيقُ خطأً.
class RuqyaSectionsTest {

    private fun section(title: String) = AdhkarSection(title, emptyList())

    // ⚠️ العناوينُ من المصدر كما هي — مشكولةٌ كاملة.
    private val ruqyaTitles = listOf(
        "دُعَاءُ طَرْدِ الشَّيْطَانِ وَوَسَاوِسِهِ",
        "دُعَاءُ الوَسْوَسَةِ فِي الصَّلَاةِ وَالقِرَاءَةِ",
        "دُعَاءُ مَنْ خَشِيَ أَنْ يُصِيبَ شَيْئًا بِعَيْنِهِ",
        "دُعَاءُ مَنْ رَأَى مُبْتَلًى",
        "مَا يَعْصِمُ اللَّهُ بِهِ مِنَ الدَّجَّالِ",
    )

    @Test
    fun theFiveRuqyaChaptersAreFound() {
        val all = ruqyaTitles.map { section(it) } + section("أَذْكَارُ الصَّبَاحِ")
        val found = RuqyaSections.sections(all)
        assertEquals(5, found.size)
    }

    @Test
    fun theOrderOfTheBookIsKept() {
        // ⚠️ **ترتيبُ الكتاب هو ترتيبُ الناس للرقية**: فمن
        // رتّبناهم على markersٍ أخرى لاختلط الترتيب.
        val all = ruqyaTitles.map { section(it) } + section("أَذْكَارُ الصَّبَاحِ")
        val found = RuqyaSections.sections(all)
        assertEquals(ruqyaTitles, found.map { it.title })
    }

    @Test
    fun anUnrelatedChapterIsNotIncluded() {
        val all = listOf(section("أَذْكَارُ الصَّبَاحِ"), section("دُعَاءُ السَّفَرِ"))
        assertTrue(RuqyaSections.sections(all).isEmpty())
    }

    @Test
    fun theShapedTitleStillMatches() {
        // ⚠️ **التطبيع** هو ما يجعل العنوان المشكول يُطابَق:
        // «الدُّعَاءُ عَلَى الْوَسْوَسَةِ» و«الدعاء على الوسوسة»
        // عنوانٌ واحد.
        val shaped = section("دُعَاءُ الوَسْوَسَةِ فِي الصَّلَاةِ وَالقِرَاءَةِ")
        assertTrue(RuqyaSections.isRuqya(shaped))
        assertFalse(RuqyaSections.isRuqya(section("أَذْكَارُ الصَّبَاحِ")))
    }

    @Test
    fun theEmptyBookYieldsNothing() {
        assertTrue(RuqyaSections.sections(emptyList()).isEmpty())
    }
}
