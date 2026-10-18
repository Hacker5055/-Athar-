package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/// ⚠️ يُختبر **المحاذاة** قبل أي شيء: أسوأ عيب يمكن أن يصيب ترجمة
/// قرآن هو أن تُعرض آية مكان أخرى. صامت، ولا يبدو خطأً لقارئ لا يحفظ.
class QuranTranslationTest {

    private val schema = """{
        "1": [
            {"chapter": 1, "verse": 1, "text": "In the name of Allah"},
            {"chapter": 1, "verse": 2, "text": "Praise be to Allah"}
        ],
        "2": [
            {"chapter": 2, "verse": 1, "text": "Alif Lam Mim"}
        ]
    }"""

    @Test fun parsesTheSchema() {
        val map = QuranTranslationRepository.parse(schema)
        assertEquals(3, map.size)
        assertEquals("In the name of Allah", map["1-1"])
        assertEquals("Praise be to Allah", map["1-2"])
        assertEquals("Alif Lam Mim", map["2-1"])
    }

    /// ⚠️ المفتاح مركّب: «٢-١» ≠ «٢١-» ولا «١-٢»
    @Test fun keyDistinguishesSurahFromAyah() {
        assertEquals("2-1", QuranTranslationRepository.key(2, 1))
        assertEquals("114-6", QuranTranslationRepository.key(114, 6))
        assertTrue(QuranTranslationRepository.key(11, 46) != QuranTranslationRepository.key(114, 6))
    }

    /// ⚠️ نصّ فارغ يُسقط **المفتاح** لا أن يُخزَّن فارغًا: «ترجمة»
    /// تظهر فراغًا أسوأ من غيابها
    @Test fun skipsEmptyText() {
        val map = QuranTranslationRepository.parse(
            """{"1": [{"chapter":1,"verse":1,"text":"   "},{"chapter":1,"verse":2,"text":"نص"}]}""",
        )
        assertEquals(1, map.size)
        assertNull(map["1-1"])
        assertEquals("نص", map["1-2"])
    }

    @Test fun skipsInvalidVerseNumbers() {
        val map = QuranTranslationRepository.parse(
            """{"1": [{"chapter":1,"verse":0,"text":"صفر"},{"chapter":1,"verse":-3,"text":"سالب"}]}""",
        )
        assertTrue(map.isEmpty())
    }

    @Test fun toleratesCorruptInput() {
        assertTrue(QuranTranslationRepository.parse("ليس جسون").isEmpty())
        assertTrue(QuranTranslationRepository.parse("").isEmpty())
        assertTrue(QuranTranslationRepository.parse("[1,2,3]").isEmpty())
    }

    /// ⚠️ سورة بمفتاح غير رقمي (تلف في الملف) لا توقف قراءة الباقي
    @Test fun ignoresNonNumericChapterKeys() {
        val map = QuranTranslationRepository.parse(
            """{"بقرة": [{"chapter":2,"verse":1,"text":"تُتجاهل"}], """ +
                """"2": [{"chapter":2,"verse":2,"text":"تُقرأ"}]}""",
        )
        assertEquals(1, map.size)
        assertEquals("تُقرأ", map["2-2"])
    }

    @Test fun noneTranslationHasNothingToShow() {
        assertTrue(QuranTranslation.NONE.asset.isEmpty())
        assertTrue(QuranTranslation.NONE.creditKey.isEmpty())
        assertEquals(2, QuranTranslation.selectable.size)
    }
}
