package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات التلاوة — روابط وكتالوج، بلا شبكة ولا جهاز.
///
/// ⚠️ القيم هنا من توثيق المصدر ومن المصحف، لا ممّا نحسبه نحن:
/// اختبارٌ يقارن الكود بنفسه يمرّ ولو كان الكود مخطئًا.
class QuranAudioTest {

    private val reciter = Reciters.byId("ar.alafasy")!!

    // ── مخطّط الروابط ──────────────────────────────────────

    @Test
    fun surahUrlUsesSurahPathAndReciterBitrate() {
        assertEquals(
            "https://cdn.islamic.network/quran/audio-surah/128/ar.alafasy/1.mp3",
            QuranAudio.surahUrl(reciter, 1),
        )
    }

    @Test
    fun surahUrlRejectsOutOfRangeSurah() {
        assertNull(QuranAudio.surahUrl(reciter, 0))
        assertNull(QuranAudio.surahUrl(reciter, 115))
        assertNull(QuranAudio.surahUrl(reciter, -3))
    }

    @Test
    fun surahUrlCoversEverySurah() {
        // ⚠️ 114 سورة كلها يجب أن تُبنى — سورة ناقصة تعني زرًّا صامتًا
        for (surah in 1..114) {
            assertNotNull("سورة $surah", QuranAudio.surahUrl(reciter, surah))
        }
    }

    @Test
    fun ayahUrlUsesAyahPath() {
        assertEquals(
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3",
            QuranAudio.ayahUrl(reciter, 1),
        )
    }

    /// ⚠️ هذه حقيقة من توثيق islamic.network نفسه: الآية الكونية ٢٦٢
    /// هي آية الكرسي ٢:٢٥٥. لو كان ترقيمنا كونيًّا خاطئًا ظهر هنا.
    @Test
    fun ayahUrlMapsSurahAyahToGlobalNumber() {
        assertEquals(
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/262.mp3",
            QuranAudio.ayahUrl(reciter, 2, 255),
        )
    }

    @Test
    fun ayahUrlRejectsAyahOutsideItsSurah() {
        // البقرة ٢٨٦ آية؛ ٢٨٧ ليست موجودة
        assertNull(QuranAudio.ayahUrl(reciter, 2, 287))
        assertNull(QuranAudio.ayahUrl(reciter, 115, 1))
        assertNull(QuranAudio.ayahUrl(reciter, 0, 1))
    }

    @Test
    fun ayahUrlRejectsOutOfRangeVerseId() {
        assertNull(QuranAudio.ayahUrl(reciter, 0))
        assertNull(QuranAudio.ayahUrl(reciter, QuranMeta.TOTAL_VERSES + 1))
        assertNotNull(QuranAudio.ayahUrl(reciter, QuranMeta.TOTAL_VERSES))
    }

    @Test
    fun differentRecitersGetTheirOwnBitrate() {
        // ⚖️ الجودة من بيان المصدر لكل قارئ: المنشاوي الممجود ٦٤ لا ١٢٨
        val mujawwad = Reciters.byId("ar.minshawimujawwad")!!
        assertEquals(64, mujawwad.bitrate)
        assertEquals(
            "https://cdn.islamic.network/quran/audio-surah/64/ar.minshawimujawwad/114.mp3",
            QuranAudio.surahUrl(mujawwad, 114),
        )
    }

    // ── الكتالوج ───────────────────────────────────────────

    @Test
    fun reciterIdsAreUnique() {
        val ids = Reciters.ALL.map { it.id }
        assertEquals("معرّف مكرّر يكسر اختيار القارئ", ids.size, ids.toSet().size)
    }

    @Test
    fun reciterIdsAreUrlSafe() {
        // ⚠️ المُعرّف يدخل الرابط كما هو: مسافة أو شرطة مائلة تكسره
        Reciters.ALL.forEach {
            assertTrue("مُعرّف غير آمن: ${it.id}", it.id.matches(Regex("ar\\.[a-z0-9]+")))
        }
    }

    @Test
    fun bitratesAreOnesTheCdnServes() {
        // الجودات التي يعلنها المصدر: 192, 128, 64, 48, 40, 32
        val allowed = setOf(192, 128, 64, 48, 40, 32)
        Reciters.ALL.forEach {
            assertTrue("جودة غير مدعومة: ${it.id} = ${it.bitrate}", it.bitrate in allowed)
        }
    }

    @Test
    fun defaultReciterIsInCatalog() {
        assertTrue(Reciters.ALL.contains(Reciters.DEFAULT))
    }

    @Test
    fun catalogHasMultipleStyles() {
        // القارئ يختار أسلوبه؛ كتالوج بأسلوب واحد ليس كتالوجًا
        assertTrue(Reciters.ALL.map { it.style }.toSet().size >= 3)
    }

    @Test
    fun lookingUpUnknownIdGivesNull() {
        assertNull(Reciters.byId("ar.doesnotexist"))
    }
}
