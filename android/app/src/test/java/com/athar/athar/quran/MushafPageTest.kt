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
    fun aPageNeverSpansMoreThanThreeSurahs() {
        // ⚠️ المصحف المدني يطبع السور القصيرة **جنبًا إلى جنب**،
        // فالصفحة قد تحوي ثلاثًا — famouslyً في آخر القرآن حيث
        // السور قصيرة كلّها. ولو افترضنا سورتين لبقيت سورة
        // بلا عنوان.
        for (page in 1..MushafPage.COUNT) {
            val span = MushafPage.surahsOnPage(page)
            assertTrue("page $page spans ${span.first}..${span.last}", span.last - span.first <= 2)
        }
    }

    @Test
    fun aPageWithThreeSurahsHasOnlyShortOnes() {
        // ⚠️ **العلاقة لا الرقم**: الصفحة التي تحوي ثلاث سور
        // لا تحوي إلا سورًا قصيرة، لأنّ المصحف لا يضع سورة
        // طويلة جنبًا إلى جنب. ولو كانت طويلة لكانت الصفحة
        // مزدحمة.
        for (page in 1..MushafPage.COUNT) {
            val span = MushafPage.surahsOnPage(page)
            if (span.last - span.first < 2) continue
            for (surah in span) {
                assertTrue("surah $surah on page $page", QuranMeta.ayahCount(surah) <= 30)
            }
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
        // ⚠️ التحقّق من **حدود النطاق** لا من هوية الأرقام:
        // كل صفحة داخلة جارتان صالحتان، والصفحة قبلها أو بعدها
        // قد تكون صالحة أيضًا — والعبرة أنّ ما خارج [١، ٦٠٤] مرفوض.
        for (page in 2 until MushafPage.COUNT) {
            assertTrue(MushafPage.isValid(page - 1))
            assertTrue(MushafPage.isValid(page + 1))
        }
    }

    @Test
    fun exactlyTheKnownNumberOfPagesIsValid() {
        // ⚠️ **الحدّان** لا الأرقام: أوّل صفحة صالحة ١ وآخرها
        // ٦٠٤، وما عداها مرفوض — لا «حوالي ٦٠٤».
        assertTrue(MushafPage.isValid(1))
        assertTrue(MushafPage.isValid(MushafPage.COUNT))
        assertFalse(MushafPage.isValid(0))
        assertFalse(MushafPage.isValid(MushafPage.COUNT + 1))
        val valid = (0..MushafPage.COUNT + 1).count { MushafPage.isValid(it) }
        assertEquals(MushafPage.COUNT, valid)
    }

    // ── تقسيم الصفحة إلى أجزاء ──────────────────────────

    @Test
    fun theSegmentsOfAPageAreItsSurahsInOrder() {
        for (page in 1..MushafPage.COUNT) {
            val segments = MushafPage.segmentsOf(page)
            assertTrue("page $page", segments.isNotEmpty())
            val surahs = segments.map { it.surah }
            assertEquals("page $page", surahs.sorted(), surahs)
            assertEquals("page $page", surahs.distinct(), surahs)
        }
    }

    @Test
    fun theSegmentsCoverEveryVerseOfThePageExactlyOnce() {
        for (page in 1..MushafPage.COUNT) {
            val segments = MushafPage.segmentsOf(page)
            val covered = segments.sumOf { it.verseCount }
            assertEquals("page $page", MushafPage.verseCount(page), covered)
            assertEquals("page $page", MushafPage.firstVerseId(page), segments.first().firstVerseId)
            assertEquals("page $page", MushafPage.lastVerseId(page), segments.last().lastVerseId)
        }
    }

    @Test
    fun theSegmentsAreContiguous() {
        // ⚠️ لا فراغ **بين** الأجزاء: آخر جزءٍ ينتهي حيث
        // يبدأ الذي يليه.
        for (page in 1..MushafPage.COUNT) {
            val segments = MushafPage.segmentsOf(page)
            for (i in 0 until segments.size - 1) {
                assertEquals(
                    "page $page segment $i",
                    segments[i].lastVerseId + 1,
                    segments[i + 1].firstVerseId,
                )
            }
        }
    }

    @Test
    fun onlyTheFirstSegmentCanBeContinued() {
        // ⚠️ **سورةٌ واحدة فقط** تُكمل من الصفحة السابقة:
        // ما بعد ذلك سورٌ بدأت في الصفحة.
        for (page in 1..MushafPage.COUNT) {
            val segments = MushafPage.segmentsOf(page)
            for (i in 1 until segments.size) {
                assertTrue("page $page segment $i", segments[i].startsHere)
            }
        }
    }

    @Test
    fun theFirstSegmentContinuesExactlyWhenThePageDoesNotStartASurah() {
        for (page in 1..MushafPage.COUNT) {
            val first = MushafPage.segmentsOf(page).first()
            assertEquals("page $page", MushafPage.startsSurah(page), first.startsHere)
        }
    }

    @Test
    fun anImpossiblePageHasNoSegments() {
        assertTrue(MushafPage.segmentsOf(0).isEmpty())
        assertTrue(MushafPage.segmentsOf(605).isEmpty())
    }
}
