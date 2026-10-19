package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات الأحزاب (B15 من docs/02).
///
/// ⚠️ هذه الاختبارات **لا تكرّر الأرقام** — تعيد قراءتها من
/// [QuranMeta] نفسه. فائدة الاختبار هنا ليست في تثبيت قيمة، بل في
/// التحقّق من **العلاقات**: أنّ كل حزب فردي يبدأ بداية جزء، وأنّ
/// الأحزاب تغطّي المصحف بلا فراغ ولا تداخل. العلاقة تكذب إن
/// فسدت البيانات، والرقم الثابت لا يكذب أبدًا مهما فسد.
class HizbTest {

    @Test
    fun thereAreSixtyHizbs() {
        assertEquals(60, QuranMeta.HIZB_COUNT)
    }

    @Test
    fun everyOddHizbStartsAJuz() {
        // ⚠️ البرهان لا التكرار: حزبان في جزء، فالفردي يبدأ جزءًا.
        // هذه العلاقة مستحيلة أن تصدق مصادفة على ستّين حزبًا.
        for (k in 0 until 30) {
            val hizb = 2 * k + 1
            val juz = k + 1
            assertEquals(
                "الحزب $hizb يجب أن يبدأ الجزء $juz",
                QuranMeta.startOfJuz(juz),
                QuranMeta.startOfHizb(hizb),
            )
        }
    }

    @Test
    fun startsAreStrictlyIncreasing() {
        var previous = 0
        for (hizb in 1..QuranMeta.HIZB_COUNT) {
            val (surah, ayah) = QuranMeta.startOfHizb(hizb)
            val id = QuranMeta.verseId(surah, ayah)
            assertTrue("الحزب $hizb: $id بعد $previous", id > previous)
            previous = id
        }
    }

    @Test
    fun theFirstHizbStartsAtTheFirstVerse() {
        assertEquals(1 to 1, QuranMeta.startOfHizb(1))
    }

    @Test
    fun theLastHizbEndsAtTheLastVerse() {
        // ⚠️ آخر حزب يجب أن **ينتهي** عند ٦٢٣٦، لا أن يبدأ قبلها
        // بآيات تُترك بلا حزب.
        val (surah, ayah) = QuranMeta.startOfHizb(60)
        val last = QuranMeta.verseId(114, 6)
        val start = QuranMeta.verseId(surah, ayah)
        assertTrue("يبدأ عند $start وآخر آية $last", start <= last)
    }

    @Test
    fun noVerseIsLeftOutsideTheHizbs() {
        // ⚠️ تغطية كاملة: كل آية في حزب واحد بالضبط
        var expected = 1
        for (hizb in 1..QuranMeta.HIZB_COUNT) {
            val (surah, ayah) = QuranMeta.startOfHizb(hizb)
            assertEquals("الحزب $hizb", expected, QuranMeta.verseId(surah, ayah))
            expected = QuranMeta.verseId(surah, ayah)
        }
        assertTrue(expected <= QuranMeta.TOTAL_VERSES)
    }

    @Test
    fun hizbOfFindsTheHizbOfAnyVerse() {
        for (hizb in 1..QuranMeta.HIZB_COUNT) {
            val (surah, ayah) = QuranMeta.startOfHizb(hizb)
            assertEquals("بداية الحزب $hizb", hizb, QuranMeta.hizbOf(surah, ayah))
        }
    }

    @Test
    fun theLastVerseIsInTheLastHizb() {
        assertEquals(60, QuranMeta.hizbOf(114, 6))
    }

    @Test
    fun theFirstVerseIsInTheFirstHizb() {
        assertEquals(1, QuranMeta.hizbOf(1, 1))
    }

    @Test
    fun hizbOfNeverJumpsAHizb() {
        // ⚠️ كل آية من ١ إلى ٦٢٣٦ في حزب، والأحزاب متصاعدة بلا
        // رجوع: فراغ أو تداخل يظهر هنا فورًا.
        var previous = 1
        for (id in 1..QuranMeta.TOTAL_VERSES) {
            val (surah, ayah) = QuranMeta.surahAyahOf(id)
            val hizb = QuranMeta.hizbOf(surah, ayah)
            assertTrue("الآية $id: $hizb بعد $previous", hizb >= previous)
            assertTrue("الآية $id: حزب $hizb", hizb in 1..QuranMeta.HIZB_COUNT)
            previous = hizb
        }
        assertEquals(60, previous)
    }

    @Test
    fun juzOfHizbGroupsThemInPairs() {
        assertEquals(1, QuranMeta.juzOfHizb(1))
        assertEquals(1, QuranMeta.juzOfHizb(2))
        assertEquals(2, QuranMeta.juzOfHizb(3))
        assertEquals(30, QuranMeta.juzOfHizb(59))
        assertEquals(30, QuranMeta.juzOfHizb(60))
    }

    @Test
    fun juzOfHizbRejectsWhatIsNotAHizb() {
        assertEquals(0, QuranMeta.juzOfHizb(0))
        assertEquals(0, QuranMeta.juzOfHizb(61))
        assertEquals(0, QuranMeta.juzOfHizb(-1))
    }

    @Test
    fun theSecondHalfIsTheEvenOne() {
        assertTrue(QuranMeta.isSecondHalf(2))
        assertTrue(!QuranMeta.isSecondHalf(1))
        assertTrue(!QuranMeta.isSecondHalf(0))
        assertTrue(!QuranMeta.isSecondHalf(61))
    }

    @Test
    fun anOutOfRangeHizbHasNoStart() {
        assertEquals(0 to 0, QuranMeta.startOfHizb(0))
        assertEquals(0 to 0, QuranMeta.startOfHizb(61))
    }

    @Test
    fun everyHizbStartIsARealVerse() {
        for (hizb in 1..QuranMeta.HIZB_COUNT) {
            val (surah, ayah) = QuranMeta.startOfHizb(hizb)
            assertTrue("الحزب $hizb: سورة $surah", surah in 1..114)
            assertTrue("الحزب $hizb: آية $ayah", ayah >= 1 && ayah <= QuranMeta.ayahCount(surah))
        }
    }
}
