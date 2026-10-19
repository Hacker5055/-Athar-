package com.athar.athar.adhkar

import com.athar.athar.prayer.CitySearch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AdhkarParserTest {

    private fun realJson(): String {
        val file = File("src/main/assets/adhkar/hisn.json")
        assertTrue("ملف الأذكار غير موجود: ${file.absolutePath}", file.exists())
        return file.readText(Charsets.UTF_8)
    }

    @Test fun parsesSectionShape() {
        val json = """
        {"باب": {"Audio": "http://example.com/a.mp3", "Adhkar": [
          {"Text": "سبحان الله", "Count": 33, "Reference": "مسلم 4/2090"},
          {"Text": "الله أكبر", "Count": 34, "Reference": ""}
        ]}}
        """.trimIndent()
        val sections = AdhkarParser.parse(json)
        assertEquals(1, sections.size)
        assertEquals("باب", sections[0].title)
        val items = sections[0].items
        assertEquals(2, items.size)
        assertEquals(33, items[0].count)
        assertEquals("مسلم 4/2090", items[0].reference)
        assertEquals(34, items[1].count)
        assertEquals("", items[1].reference)
    }

    /// ⚠️ عدد صفر أو سالب أو غائب = عدّاد لا ينتهي أبدًا
    @Test fun coercesBadCountToOne() {
        val json = """{"باب": {"Adhkar": [
            {"Text": "ذكر", "Count": 0},
            {"Text": "ذكر", "Count": -3},
            {"Text": "ذكر"}
        ]}}"""
        val items = AdhkarParser.parse(json)[0].items
        assertEquals(3, items.size)
        items.forEach { assertEquals(1, it.count) }
    }

    @Test fun skipsEmptyText() {
        val json = """{"باب": {"Adhkar": [
            {"Text": "", "Count": 3},
            {"Text": "   ", "Count": 3},
            {"Text": "ذكر", "Count": 1}
        ]}}"""
        val items = AdhkarParser.parse(json)[0].items
        assertEquals(1, items.size)
        assertEquals("ذكر", items[0].text)
    }

    @Test fun realDataIsComplete() {
        val sections = AdhkarParser.parse(realJson())
        assertEquals(133, sections.size)
        // ⚠️ العنوان مفتاح الحفظ في AdhkarProgressStore — تكراره يعني
        // عدّادين يتشاركان مفتاحًا ويفسد أحدهما الآخر
        assertEquals("عناوين مكررة", sections.size, sections.map { it.title }.toSet().size)

        var total = 0
        sections.forEach { section ->
            assertTrue("${section.title}: قسم فارغ", section.items.isNotEmpty())
            section.items.forEach { dhikr ->
                assertTrue("نصّ فارغ في ${section.title}", dhikr.text.isNotBlank())
                assertTrue("عدد غير موجب في ${section.title}", dhikr.count >= 1)
                assertTrue("بلا تخريج في ${section.title}", dhikr.reference.isNotBlank())
                total++
            }
        }
        assertEquals(302, total)
    }

    /// الأقسام التي يطلبها docs/02 صراحةً (C1 صباح/مساء، C2 بعد الصلاة)
    @Test fun hasCoreSections() {
        val titles = AdhkarParser.parse(realJson()).map { CitySearch.normalize(it.title) }
        fun has(section: String) =
            titles.any { CitySearch.normalize(section) in it }
        assertTrue("بلا أذكار الصباح", has("الصباح"))
        assertTrue("بلا أذكار المساء", has("المساء"))
        assertTrue("بلا أذكار بعد الصلاة", has("الصلاة"))
        assertTrue("بلا أذكار النوم", has("النوم"))
    }
}
