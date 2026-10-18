package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات علامات الآيات — المنطق النقي.
///
/// ⚠️ المخزن لا يُختبر (SharedPreferences تحتاج جهازًا). الذي نختبره
/// هو **أي مفتاح يصحّ**: التخزين يُقرأ من نسخ قديمة ومكتوبة باليد،
/// وكل احتمال يجب أن ينتهي إلى مجموعة لا إلى استثناء.
class QuranBookmarksTest {

    @Test
    fun keyCarriesBothSurahAndAyah() {
        assertEquals("2:255", QuranBookmarks.key(2, 255))
        // ⚠️ آية ٢ في البقرة ليست آية ٢ في آل عمران
        assertFalse(QuranBookmarks.key(2, 5) == QuranBookmarks.key(3, 5))
    }

    @Test
    fun toggleAddsThenRemoves() {
        var marks = emptySet<String>()
        marks = QuranBookmarks.toggle(marks, 2, 255)
        assertTrue(QuranBookmarks.contains(marks, 2, 255))
        marks = QuranBookmarks.toggle(marks, 2, 255)
        assertFalse("العلامة تُحذف بثانية ضغطة لا تبقى أبدًا", marks.contains("2:255"))
    }

    @Test
    fun toggleKeepsTheOthers() {
        var marks = QuranBookmarks.toggle(emptySet(), 1, 1)
        marks = QuranBookmarks.toggle(marks, 114, 6)
        assertEquals(setOf("1:1", "114:6"), marks)
    }

    // ── التسامح ──────────────────────────────────────────

    @Test
    fun nullAndBlankDecodeToEmpty() {
        assertTrue(QuranBookmarks.decode(null).isEmpty())
        assertTrue(QuranBookmarks.decode("").isEmpty())
        assertTrue(QuranBookmarks.decode("   ").isEmpty())
    }

    @Test
    fun garbageDecodesToEmptyNotException() {
        assertTrue(QuranBookmarks.decode("ليس مفتاحًا").isEmpty())
        assertTrue(QuranBookmarks.decode("{{{").isEmpty())
    }

    @Test
    fun outOfRangeAyahsAreDropped() {
        // ⚠️ علامة على آية ٩٩٩ في الفاتحة لا معنى لها، والإبقاء عليها
        // يُنبت سطرًا فارغًا في القائمة لا يُحذف.
        assertTrue(QuranBookmarks.decode("1:999").isEmpty())
        assertTrue(QuranBookmarks.decode("115:1").isEmpty())
        assertTrue(QuranBookmarks.decode("0:1").isEmpty())
        assertTrue(QuranBookmarks.decode("2:0").isEmpty())
    }

    @Test
    fun validAyahSurvivesBesideAnInvalidOne() {
        assertEquals(
            setOf("2:255", "114:6"),
            QuranBookmarks.decode("2:255,1:999,114:6,999:1"),
        )
    }

    @Test
    fun decodeIsIdempotent() {
        val once = QuranBookmarks.decode("2:255,1:1")
        assertEquals(once, QuranBookmarks.decode(QuranBookmarks.encode(once)))
    }

    // ── الترميز ──────────────────────────────────────────

    @Test
    fun encodeSortsBySurahThenAyah() {
        // ⚠️ الترتيب جزء من العرض: القائمة المبعثرة تبدو كأن التطبيق
        // نسي ما حفظه المستخدم.
        val raw = QuranBookmarks.encode(setOf("114:1", "2:255", "1:1", "2:3"))
        assertEquals("1:1,2:3,2:255,114:1", raw)
    }

    @Test
    fun encodeDropsInvalidKeys() {
        assertEquals("1:1", QuranBookmarks.encode(setOf("1:1", "1:999", "باطل")))
    }

    // ── التجميع ──────────────────────────────────────────

    @Test
    fun bySurahGroupsAndSorts() {
        val grouped = QuranBookmarks.bySurah(setOf("2:255", "1:1", "2:3"))
        assertEquals(listOf(1), grouped[1])
        assertEquals("آيات السورة مرتّبة", listOf(3, 255), grouped[2])
        assertEquals("سورة واحدة ⇒ قراءة واحدة للنصّ", listOf(1, 2), grouped.keys.toList())
    }

    @Test
    fun bySurahHandlesEmpty() {
        assertTrue(QuranBookmarks.bySurah(emptySet()).isEmpty())
    }
}
