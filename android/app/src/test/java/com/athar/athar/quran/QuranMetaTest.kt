package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// حدود الأجزاء والصفحات.
///
/// ⚠️ كل توقّع هنا **معلومة من المصحف** لا نتيجة تطبيقي: الجزء الثاني
/// يبدأ البقرة ١٤٢، والثلاثون يبدأ النبأ. لو كان التوقّع من حسابي
/// لكان الاختبار يكرّر افتراض الكود ولا يفحصه.
class QuranMetaTest {

    @Test fun juzOneStartsAtAlFatihah() {
        assertEquals(1 to 1, QuranMeta.startOfJuz(1))
        assertEquals(1, QuranMeta.juzOf(1, 1))
    }

    @Test fun juzTwoStartsAtBaqarahOneFortyTwo() {
        assertEquals(2 to 142, QuranMeta.startOfJuz(2))
        assertEquals(2, QuranMeta.juzOf(2, 142))
        assertEquals(1, QuranMeta.juzOf(2, 141))
    }

    @Test fun juzThirtyStartsAtAnNaba() {
        assertEquals(78 to 1, QuranMeta.startOfJuz(30))
        assertEquals(30, QuranMeta.juzOf(78, 1))
    }

    /// ⚠️ معلومة محفوظة: الجزء الأول الفاتحة (٧) + البقرة ١–١٤١
    @Test fun juzOneSpansOneHundredFortyEightVerses() {
        val first = QuranMeta.verseId(1, 1)
        val second = QuranMeta.startOfJuz(2).let { (su, ay) -> QuranMeta.verseId(su, ay) }
        assertEquals(148, second - first)
    }

    @Test fun pageOneIsAlFatihah() {
        assertEquals(1 to 1, QuranMeta.startOfPage(1))
        assertEquals(1, QuranMeta.pageOf(1, 7))
        assertEquals(2 to 1, QuranMeta.startOfPage(2))
        assertEquals(2, QuranMeta.pageOf(2, 1))
    }

    /// ⚠️ أهم اختبار: لا آية بلا جزء، ولا آية في جزأين. مجموع آيات
    /// الأجزاء يجب أن يساوي ٦٢٣٦ — إن نقص فثمّة آية ضائعة، وإن زاد
    /// فآية محسوبة مرتين.
    @Test fun everyVerseFallsInExactlyOneJuz() {
        var total = 0
        for (juz in 1..QuranMeta.JUZ_COUNT) {
            val start = QuranMeta.startOfJuz(juz).let { (su, ay) -> QuranMeta.verseId(su, ay) }
            val next = if (juz == QuranMeta.JUZ_COUNT) {
                QuranMeta.TOTAL_VERSES + 1
            } else {
                QuranMeta.startOfJuz(juz + 1).let { (su, ay) -> QuranMeta.verseId(su, ay) }
            }
            assertTrue("الجزء $juz يبدأ بصفر", start > 0)
            assertTrue("الجزء $juz فارغ أو معكوس", next > start)
            total += next - start
        }
        assertEquals(QuranMeta.TOTAL_VERSES, total)
    }

    @Test fun everyVerseFallsInExactlyOnePage() {
        var total = 0
        for (page in 1..QuranMeta.PAGE_COUNT) {
            val start = QuranMeta.startOfPage(page).let { (su, ay) -> QuranMeta.verseId(su, ay) }
            val next = if (page == QuranMeta.PAGE_COUNT) {
                QuranMeta.TOTAL_VERSES + 1
            } else {
                QuranMeta.startOfPage(page + 1).let { (su, ay) -> QuranMeta.verseId(su, ay) }
            }
            assertTrue("الصفحة $page تبدأ بصفر", start > 0)
            assertTrue("الصفحة $page فارغة أو معكوسة", next > start)
            total += next - start
        }
        assertEquals(QuranMeta.TOTAL_VERSES, total)
    }

    @Test fun lastVerseIsJuzThirtyPageSixOhFour() {
        assertEquals(30, QuranMeta.juzOf(114, 6))
        assertEquals(QuranMeta.PAGE_COUNT, QuranMeta.pageOf(114, 6))
    }

    /// ⚠️ دورة كاملة على القرآن كلّه: كل فهرس يعود إلى (سورة، آية)
    /// تعود إليه. هذا ما يضمن أن الانتقال إلى صفحة يفتح الموضع الصحيح.
    @Test fun verseIdRoundTripsForEveryVerse() {
        for (id in 1..QuranMeta.TOTAL_VERSES) {
            val (surah, ayah) = QuranMeta.surahAyahOf(id)
            assertEquals("الفهرس $id", id, QuranMeta.verseId(surah, ayah))
        }
    }

    @Test fun outOfRangeIsZeroNotAnException() {
        assertEquals(0, QuranMeta.verseId(0, 1))
        assertEquals(0, QuranMeta.verseId(115, 1))
        assertEquals(0, QuranMeta.verseId(2, 287))   // البقرة ٢٨٦ آية
        assertEquals(0 to 0, QuranMeta.surahAyahOf(0))
        assertEquals(0 to 0, QuranMeta.surahAyahOf(6237))
        assertEquals(0, QuranMeta.juzOf(2, 287))
        assertEquals(0, QuranMeta.pageOf(115, 1))
        assertEquals(0 to 0, QuranMeta.startOfJuz(31))
        assertEquals(0 to 0, QuranMeta.startOfPage(605))
    }

    /// ⚠️ الحدود **بنيوية لا حسابية**: ليست ٦٢٣٦ ÷ ٣٠، فالأجزاء متفاوتة
    @Test fun juzBoundariesAreUnequal() {
        val sizes = (1..QuranMeta.JUZ_COUNT).map { juz ->
            val start = QuranMeta.startOfJuz(juz).let { (su, ay) -> QuranMeta.verseId(su, ay) }
            val next = if (juz == QuranMeta.JUZ_COUNT) {
                QuranMeta.TOTAL_VERSES + 1
            } else {
                QuranMeta.startOfJuz(juz + 1).let { (su, ay) -> QuranMeta.verseId(su, ay) }
            }
            next - start
        }
        assertTrue("الأجزاء متساوية — الحدود محسوبة لا مقروءة", sizes.toSet().size > 10)
        assertTrue("جزء فارغ", sizes.all { it > 0 })
    }
}
