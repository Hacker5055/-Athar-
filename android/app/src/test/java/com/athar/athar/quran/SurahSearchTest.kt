package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات البحث بالاسم وتصفية السور (B12).
///
/// ⚠️ الأسماء **مكتوبة هنا كما في الفهرس** (١١٤ سورة)، لا
/// منسوخة من ملف التطبيق: فمن نسخها أوّلًا لأخطأ مرّ. وما
/// نتحقّق منه هو **العلاقة**: أن يطابق «الكهف» سورته، وأن
/// «النمل» لا تطابق «الأنمل».
class SurahSearchTest {

    private val surahs = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", 7, true),
        Surah(2, "البقرة", "Al-Baqarah", 286, false),
        Surah(18, "الكهف", "Al-Kahf", 110, true),
        Surah(27, "النمل", "An-Naml", 93, true),
        Surah(36, "يس", "Ya-Sin", 83, true),
        Surah(112, "الإخلاص", "Al-Ikhlas", 4, false),
    )

    // ── البحث بالاسم ────────────────────────────────────

    @Test
    fun theNameFindsItsSurah() {
        val hits = SurahSearch.byName("الكهف", surahs)
        assertEquals(1, hits.size)
        assertEquals(18, hits[0].id)
    }

    @Test
    fun theNameFindsItsSurahInEnglishToo() {
        val hits = SurahSearch.byName("Kahf", surahs)
        assertEquals(1, hits.size)
        assertEquals(18, hits[0].id)
    }

    @Test
    fun theEnglishNameIsMatchedCaseInsensitively() {
        assertEquals(18, SurahSearch.byName("kahf", surahs)[0].id)
        assertEquals(18, SurahSearch.byName("AL-KAHF", surahs)[0].id)
    }

    @Test
    fun aPartialNameFindsItsSurah() {
        // ⚠️ من كتب «كهف» يريد «الكهف».
        assertEquals(18, SurahSearch.byName("كهف", surahs)[0].id)
    }

    @Test
    fun aNameWithoutDiacriticsFindsItsSurah() {
        // ⚠️ التطبيع **شرط**: من كتب «الفاتحة» بلا تشكيل يجب
        // أن يجدها.
        assertEquals(1, SurahSearch.byName("الفاتحة", surahs)[0].id)
    }

    @Test
    fun theAlifMaddaIsTheSameAlif() {
        // ⚠️ «البقره» و«البقرة» و«البقرة» اسمٌ واحد.
        assertEquals(2, SurahSearch.byName("البقره", surahs)[0].id)
    }

    @Test
    fun aDifferentNameDoesNotMatch() {
        // ⚠️ «النمل» **لا** تطابق «الأنمل»: حرف النون ثم
        // الهمزة. فلو طابقتا لَما وجد النمل سورته.
        assertTrue(SurahSearch.byName("الأنمل", surahs).isEmpty())
    }

    @Test
    fun aWordInsideTheNameMatches() {
        assertEquals(112, SurahSearch.byName("خلاص", surahs)[0].id)
    }

    @Test
    fun anEmptyQueryFindsNothing() {
        assertTrue(SurahSearch.byName("", surahs).isEmpty())
        assertTrue(SurahSearch.byName("   ", surahs).isEmpty())
    }

    @Test
    fun aQueryThatMatchesNothingFindsNothing() {
        assertTrue(SurahSearch.byName("زقزقة", surahs).isEmpty())
    }

    @Test
    fun theResultKeepsTheOrderOfTheIndex() {
        // ⚠️ الترتيب **كترتيب الفهرس** لا كترتيب المطابقة:
        // من يقفز إلى سورة يرى الباقي بترتيب المصحف.
        val hits = SurahSearch.byName("ا", surahs)
        val ids = hits.map { it.id }
        assertEquals(ids.sorted(), ids)
    }

    // ── التصفية بمكان النزول ─────────────────────────────

    @Test
    fun noFilterKeepsEverything() {
        assertEquals(surahs.size, SurahSearch.byOrigin(surahs, null, null).size)
    }

    @Test
    fun theMeccanFilterKeepsOnlyMeccan() {
        val meccan = SurahSearch.byOrigin(surahs, meccanOnly = true, medinanOnly = null)
        assertTrue(meccan.isNotEmpty())
        assertTrue(meccan.all { it.isMeccan })
    }

    @Test
    fun theMedinanFilterKeepsOnlyMedinan() {
        val medinan = SurahSearch.byOrigin(surahs, meccanOnly = null, medinanOnly = true)
        assertTrue(medinan.isNotEmpty())
        assertTrue(medinan.none { it.isMeccan })
    }

    @Test
    fun theTwoFiltersPartitionTheList() {
        // ⚠️ التصفيتان **تتقاسمان** القائمة: لا سورة ضائعة
        // ولا سورة مكرّرة.
        val meccan = SurahSearch.byOrigin(surahs, meccanOnly = true, medinanOnly = null)
        val medinan = SurahSearch.byOrigin(surahs, meccanOnly = null, medinanOnly = true)
        assertEquals(surahs.size, meccan.size + medinan.size)
    }

    @Test
    fun theFilterKeepsTheOrderOfTheIndex() {
        val meccan = SurahSearch.byOrigin(surahs, meccanOnly = true, medinanOnly = null)
        val ids = meccan.map { it.id }
        assertEquals(ids.sorted(), ids)
    }
}
