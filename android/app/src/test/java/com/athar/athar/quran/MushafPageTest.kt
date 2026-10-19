package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات صفحات المصحف (B2) — **علاقات لا أرقام**.
///
/// ⚠️ القاعدة التي التزمتُها في الأحزاب: لا ننسخ الأرقام
/// المتوقّعة من خارج التطبيق (فتصير نسخةً من خطأٍ آخر)، بل
/// نفحص **العلاقة** التي يجب أن تصمد. الأرقام هنا مشتقة من
/// `PAGE_STARTS` نفسها، فنسخُها لا يثبت شيئًا.
class MushafPageTest {

    @Test
    fun thereAreSixHundredAndFourPages() {
        assertEquals(604, MushafPage.COUNT)
        assertEquals(QuranMeta.PAGE_COUNT, MushafPage.COUNT)
    }

    // ── الحدود ───────────────────────────────────────────

    @Test
    fun theFirstPageStartsWithTheFirstVerse() {
        assertEquals(1, MushafPage.firstVerseId(1))
    }

    @Test
    fun theLastPageEndsWithTheLastVerse() {
        assertEquals(QuranMeta.TOTAL_VERSES, MushafPage.lastVerseId(MushafPage.COUNT))
    }

    @Test
    fun pagesAreContiguousAndNeverOverlap() {
        // ⚠️ هذا هو الفحص الحاسم: لو تداخلت صفحتان لظهر آية
        // مرّتين، ولو انقطع بينها لظهر فراغ. والشرط **بين
        // كل صفحتين متتاليتين** لا في مواضع مختارة.
        for (page in 1 until MushafPage.COUNT) {
            assertEquals(
                "page $page",
                MushafPage.lastVerseId(page) + 1,
                MushafPage.firstVerseId(page + 1),
            )
        }
    }

    @Test
    fun everyVerseOfTheQuranBelongsToExactlyOnePage() {
        // ⚠️ تغطية كاملة: كل آية من ١ إلى ٦٢٣٦ في صفحة، ولا
        // صفحة فيها آية من خارجها.
        val seen = HashSet<Int>()
        for (page in 1..MushafPage.COUNT) {
            for (id in MushafPage.verseIds(page)) {
                assertTrue("verse $id duplicated", seen.add(id))
                assertEquals("verse $id on page $page", page, QuranMeta.pageOfId(id))
            }
        }
        assertEquals(QuranMeta.TOTAL_VERSES, seen.size)
    }

    @Test
    fun thePageOfAVerseAgreesWithThePageThatContainsIt() {
        for (page in 1..MushafPage.COUNT) {
            val (surah, ayah) = QuranMeta.surahAyahOf(MushafPage.firstVerseId(page))
            assertEquals("page $page", page, QuranMeta.pageOf(surah, ayah))
        }
    }

    @Test
    fun theVerseCountOfAPageIsNeverEmpty() {
        for (page in 1..MushafPage.COUNT) {
            assertTrue("page $page", MushafPage.verseCount(page) > 0)
        }
    }

    // ── السور على الصفحة ─────────────────────────────────

    @Test
    fun aPageNeverSpansMoreThanTwoSurahs() {
        // ⚠️ في المصحف المدينة: صفحة واحدة تعبر **حدّ سورة**
        // واحدة على الأكثر. ولو عبرت ثلاثًا لكانت الصفحة
        // أطول من صفحة المصحف، والمعطيات غلط.
        for (page in 1..MushafPage.COUNT) {
            val span = MushafPage.surahsOnPage(page)
            assertTrue("page $page spans ${span.first}..${span.last}", span.last - span.first <= 1)
        }
    }

    @Test
    fun aPageThatStartsASurahBeginsWithItsFirstAyah() {
        for (page in 1..MushafPage.COUNT) {
            if (!MushafPage.startsSurah(page)) continue
            val (surah, ayah) = QuranMeta.surahAyahOf(MushafPage.firstVerseId(page))
            assertEquals(1, ayah)
            assertEquals(surah, MushafPage.firstSurah(page))
        }
    }

    @Test
    fun aPageThatDoesNotStartASurahBeginsInsideOne() {
        for (page in 1..MushafPage.COUNT) {
            if (MushafPage.startsSurah(page)) continue
            val (_, ayah) = QuranMeta.surahAyahOf(MushafPage.firstVerseId(page))
            assertTrue("page $page starts at ayah $ayah", ayah > 1)
        }
    }

    @Test
    fun theFirstPageStartsASurahAndTheFirstSurahIsAlFatihah() {
        assertTrue(MushafPage.startsSurah(1))
        assertEquals(1, MushafPage.firstSurah(1))
    }

    @Test
    fun somePagesStartASurahAndSomeDoNot() {
        // ⚠️ فحصٌ ضدّ الفحص: لو كانت كل الصفحات تبدأ سورةً
        // لكانت الصفحة سورةً مقنّعة، ولو لم تبدأ أيّها لكانت
        // البيانات خاطئة. والصحيح أن الاثنين وارد.
        val starting = (1..MushafPage.COUNT).count { MushafPage.startsSurah(it) }
        assertTrue("starting pages: $starting", starting > 0)
        assertTrue("starting pages: $starting", starting < MushafPage.COUNT)
    }

    @Test
    fun theSurahsOnAPageAreConsecutive() {
        for (page in 1..MushafPage.COUNT) {
            val span = MushafPage.surahsOnPage(page)
            for (surah in span) {
                assertTrue("surah $surah on page $page", surah in 1..114)
            }
        }
    }

    // ── الحدود اللامعتبرة ────────────────────────────────

    @Test
    fun anImpossiblePageIsEmptyNotAnException() {
        // ⚠️ قيمة خارج النطاق **لا ترمي**: المتصل يريد أن
        // يتحقّق، والاستثناء يوقف القراءة كلّها.
        for (page in listOf(0, -1, 605, 1000, Int.MAX_VALUE, Int.MIN_VALUE)) {
            assertFalse(MushafPage.isValid(page))
            assertEquals(0, MushafPage.firstVerseId(page))
            assertEquals(0, MushafPage.lastVerseId(page))
            assertEquals(0, MushafPage.firstSurah(page))
            assertEquals(0, MushafPage.verseCount(page))
            assertFalse(MushafPage.startsSurah(page))
            assertTrue(MushafPage.verseIds(page).isEmpty())
        }
    }

    @Test
    fun theNeighboursOfAPageAreOneAway() {
        // ⚠️ التحقّق من **الحدود** لا من أن ١ ناقص ١ يساوي ١.
        for (page in 2 until MushafPage.COUNT) {
            assertTrue(MushafPage.isValid(page - 1))
            assertTrue(MushafPage.isValid(page + 1))
            assertFalse(MushafPage.isValid(page - 2))
            assertFalse(MushafPage.isValid(page + 2))
        }
    }
}
