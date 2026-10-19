package com.athar.athar.ruqya

import com.athar.athar.quran.Ayah
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات مقتطفات الرقية (C9).
///
/// ⚠️ الفحص على **الحدود**: مقتطفٌ يخرج عن سورته يُظهر فراغًا في
/// التطبيق، ومقتطفٌ ناقصٌ في الرقية نقصٌ في المقتطف لا في
/// التطبيق.
class RuqyaSelectionsTest {

    @Test
    fun everySelectionIsInsideASurah() {
        for (selection in RuqyaSelections.ALL) {
            assertTrue(selection.surah in 1..114)
            assertTrue(selection.fromAyah in 1..286)
            assertTrue(selection.toAyah >= selection.fromAyah)
            assertTrue(selection.toAyah <= 286)
        }
    }

    @Test
    fun theIdsAreUnique() {
        val ids = RuqyaSelections.ALL.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun theTitleKeysAreUnique() {
        val keys = RuqyaSelections.ALL.map { it.titleKey }
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test
    fun theKursiIsOneAyah() {
        val kursi = RuqyaSelections.ALL.first { it.id == "kursi" }
        assertEquals(1, kursi.ayahCount)
        assertEquals(255, kursi.fromAyah)
        assertEquals(255, kursi.toAyah)
    }

    @Test
    fun theLastTwoVersesOfBaqarah() {
        val last = RuqyaSelections.ALL.first { it.id == "baqarah_last" }
        assertEquals(2, last.ayahCount)
        assertEquals(285, last.fromAyah)
        assertEquals(286, last.toAyah)
    }

    @Test
    fun theThreeQulsAreComplete() {
        // ⚠️ **الثلاثةُ كاملة**: سورةٌ ناقصة في الرقية نقصٌ في
        // المقتطف لا في التطبيق.
        val expected = mapOf("ikhlas" to 4, "falaq" to 5, "nas" to 6)
        for ((id, count) in expected) {
            val selection = RuqyaSelections.ALL.first { it.id == id }
            assertEquals(1, selection.fromAyah)
            assertEquals(count, selection.ayahCount)
            assertEquals(count, selection.toAyah)
        }
    }

    @Test
    fun theVersesComeInOrder() {
        // ⚠️ **الترتيبُ من المصدر**: آيةٌ ٢٨٦ قبل ٢٨٥ تعني نصًّا
        // مقلوبًا في الرقية.
        val selection = RuqyaSelections.ALL.first { it.id == "baqarah_last" }
        val source = listOf(Ayah(2, 286, "الثانية"), Ayah(2, 285, "الأولى"))
        val picked = RuqyaSelections.verses(selection, source)
        assertEquals(listOf(285, 286), picked.map { it.number })
    }

    @Test
    fun whatIsNotInTheSourceIsNotInvented() {
        // ⚠️ **لا نخترع آية**: ما غاب من المصدر غاب من المقتطف.
        val selection = RuqyaSelections.ALL.first { it.id == "nas" }
        val picked = RuqyaSelections.verses(selection, listOf(Ayah(114, 1, "واحد")))
        assertEquals(1, picked.size)
    }

    @Test
    fun theVersesOutsideTheRangeAreExcluded() {
        val selection = RuqyaSelections.ALL.first { it.id == "ikhlas" }
        val source = listOf(Ayah(112, 1, "أ"), Ayah(112, 5, "خارج"))
        val picked = RuqyaSelections.verses(selection, source)
        assertEquals(1, picked.size)
        assertEquals(1, picked[0].number)
    }
}
