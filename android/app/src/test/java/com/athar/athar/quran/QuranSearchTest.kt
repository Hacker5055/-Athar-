package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// ⚠️ عيّنة صغيرة لا القرآن كله: يُختبر **منطق** البحث لا بياناته،
/// وحجم الأصل (١٫٧ ميجابايت) لا يجعله مناسبًا لاختبار وحدة.
class QuranSearchTest {

    private val data = mapOf(
        1 to listOf(
            Ayah(1, 1, "بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ"),
            Ayah(1, 2, "ٱلۡحَمۡدُ لِلَّهِ رَبِّ ٱلۡعَٰلَمِينَ"),
        ),
        112 to listOf(
            Ayah(112, 1, "قُلۡ هُوَ ٱللَّهُ أَحَدٌ"),
            Ayah(112, 2, "ٱللَّهُ ٱلصَّمَدُ"),
        ),
    )
    private val surahs = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", 7, true),
        Surah(112, "الإخلاص", "Al-Ikhlas", 4, false),
    )
    private fun ayahsOf(id: Int) = data[id].orEmpty()

    /// ⚠️ هذا جوهر الميزة: المستخدم يكتب «الحمد لله» بلا تشكيل
    @Test fun findsWithoutDiacritics() {
        val hits = QuranSearch.search("الحمد لله", surahs, ::ayahsOf)
        assertEquals(1, hits.size)
        assertEquals(1, hits[0].ayah.surah)
        assertEquals(2, hits[0].ayah.number)
    }

    @Test fun findsWithDiacritics() {
        val hits = QuranSearch.search("ٱلۡحَمۡدُ", surahs, ::ayahsOf)
        assertEquals(1, hits.size)
        assertEquals(2, hits[0].ayah.number)
    }

    @Test fun searchesAcrossSurahs() {
        val hits = QuranSearch.search("الله", surahs, ::ayahsOf)
        assertEquals(2, hits.map { it.ayah.surah }.toSet().size)
    }

    /// ⚠️ شرط الرخصة (CC BY 3.0 تمنع التعديل): النص يبقى كما هو حرفيًا
    @Test fun returnedTextIsUntouched() {
        val hits = QuranSearch.search("الحمد", surahs, ::ayahsOf)
        assertEquals("ٱلۡحَمۡدُ لِلَّهِ رَبِّ ٱلۡعَٰلَمِينَ", hits[0].ayah.text)
    }

    @Test fun emptyQueryYieldsNothing() {
        assertTrue(QuranSearch.search("", surahs, ::ayahsOf).isEmpty())
        assertTrue(QuranSearch.search("   ", surahs, ::ayahsOf).isEmpty())
    }

    @Test fun noMatchYieldsNothing() {
        assertTrue(QuranSearch.search("كلمة-لا-توجد", surahs, ::ayahsOf).isEmpty())
    }

    /// ⚠️ بلا حدٍّ يتجمّد البناء على جهاز ضعيف
    @Test fun respectsTheLimit() {
        assertEquals(1, QuranSearch.search("الله", surahs, ::ayahsOf, limit = 1).size)
    }

    @Test fun carriesSurahName() {
        assertEquals("الإخلاص", QuranSearch.search("الصمد", surahs, ::ayahsOf)[0].surahNameAr)
    }
}
