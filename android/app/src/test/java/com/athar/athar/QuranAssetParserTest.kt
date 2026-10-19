package com.athar.athar

import com.athar.athar.quran.QuranAssetParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// يختبر مُحلّل النص على JVM — البنية موثّقة من الملف الأصلي.
class QuranAssetParserTest {

    @Test
    fun parsesChaptersSortedById() {
        val json = """
        {"chapters":[
          {"id":2,"name":"البقرة","transliteration":"Al-Baqara",
           "translation":"The Cow","type":"medinan","total_verses":286},
          {"id":1,"name":"الفاتحة","transliteration":"Al-Faatiha",
           "translation":"The Opening","type":"meccan","total_verses":7}
        ]}
        """.trimIndent()

        val surahs = QuranAssetParser.parseChapters(json)

        assertEquals(2, surahs.size)
        // ⚠️ الترتيب شرط في الفهرس لا صدفة من الملف
        assertEquals(1, surahs[0].id)
        assertEquals("الفاتحة", surahs[0].nameAr)
        assertEquals(7, surahs[0].ayahCount)
        assertTrue("الفاتحة مكية", surahs[0].isMeccan)
        assertEquals("البقرة", surahs[1].nameAr)
        assertTrue("البقرة مدنية", !surahs[1].isMeccan)
    }

    @Test
    fun parsesUthmaniAyahs() {
        val json = """
        {"1":[
          {"chapter":1,"verse":2,"text":"ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ"},
          {"chapter":1,"verse":1,"text":"بِسْمِ ٱللَّهِ"}
        ]}
        """.trimIndent()

        val map = QuranAssetParser.parseUthmani(json)
        val ayahs = map[1]!!

        assertEquals(2, ayahs.size)
        // مرتّبة برقم الآية لا بترتيب الملف
        assertEquals(1, ayahs[0].number)
        assertEquals("بِسْمِ ٱللَّهِ", ayahs[0].text)
        assertEquals(1, ayahs[0].surah)
    }

    /// ⚠️ النص العثماني يُعرض **كما هو** — الرخصة تمنع التعديل،
    /// فلا نطبّع حروفه ولا نعيد تشكيله.
    @Test
    fun preservesUthmaniTextVerbatim() {
        val text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"
        val json = """{"1":[{"chapter":1,"verse":1,"text":"$text"}]}"""
        assertEquals(text, QuranAssetParser.parseUthmani(json)[1]!!.single().text)
    }
}
