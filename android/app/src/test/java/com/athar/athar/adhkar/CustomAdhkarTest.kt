package com.athar.athar.adhkar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات أذكار المستخدم — المنطق النقي.
///
/// ⚠️ المخزن لا يُختبر (SharedPreferences تحتاج جهازًا). الذي نختبره
/// هو **نصّ المستخدم**: يمكن أن يحتوي أي حرف، وأن يُلصق من أي مكان،
/// ويجب أن يخرج كما دخل.
class CustomAdhkarTest {

    private val a = CustomDhikr("a1", "سبحان الله", 33)
    private val b = CustomDhikr("b2", "الحمد لله", 33)

    @Test
    fun roundTripKeepsTextAndTarget() {
        val decoded = CustomAdhkar.decode(CustomAdhkar.encode(listOf(a, b)))
        assertEquals(listOf(a, b), decoded)
    }

    @Test
    fun emptyAndNullDecodeToEmpty() {
        assertTrue(CustomAdhkar.decode(null).isEmpty())
        assertTrue(CustomAdhkar.decode("").isEmpty())
        assertTrue(CustomAdhkar.decode("باطل").isEmpty())
    }

    @Test
    fun textWithCommasAndPipesSurvives() {
        // ⚠️ الفاصل لو كان `,` أو `|` لانقسم النصّ إلى سجلّين
        val odd = CustomDhikr("z9", "لا إله إلا الله، وحده | لا شريك له", 1)
        assertEquals(listOf(odd), CustomAdhkar.decode(CustomAdhkar.encode(listOf(odd))))
    }

    @Test
    fun separatorsInUserTextAreStrippedNotFatal() {
        // ⚠️ الحذف لا الرفض: المستخدم لا يعرف ما فاصل التحكّم، ورفض
        // ذكره كلّه لأنه لصق نصًا فيه رمز نادر عقاب على خطأ لا يراه.
        val cleaned = CustomAdhkar.sanitize("قبل␟بعد")
        assertTrue("الفاصل يُسقط من النصّ", '␟' !in cleaned)
        assertTrue(cleaned.contains("قبل"))
    }

    @Test
    fun separatorIsPrintableNotAControlChar() {
        // ⚠️ SharedPreferences تُخزَّن في XML، ومحارف التحكّم غير
        // قانونية فيه فتُسقط بصمت — فيضيع الذكر.
        val raw = CustomAdhkar.encode(listOf(a))
        assertTrue(raw.none { it.code < 0x20 })
    }

    @Test
    fun invalidRecordsAreDropped() {
        val mixed = listOf(
            CustomAdhkar.encode(listOf(a)),
            "باطل",
            "a1␟", // نصّ فارغ
        ).joinToString("␟")
        assertEquals(listOf(a), CustomAdhkar.decode(mixed))
    }

    @Test
    fun targetIsClamped() {
        val wild = CustomDhikr("c3", "ذكر", 999_999)
        val decoded = CustomAdhkar.decode(CustomAdhkar.encode(listOf(wild)))
        assertTrue(decoded[0].target <= 10_000)
    }

    @Test
    fun blankItemsAreNotEncoded() {
        assertEquals("", CustomAdhkar.encode(listOf(CustomDhikr("d4", "   ", 3))))
    }

    // ── العدّ ────────────────────────────────────────────

    @Test
    fun doneIsKeyedByIdNotByPosition() {
        // ⚠️ الحذف من المنتصف كان ينقل العدّاد: يُسقط تسبيحًا كاملاً
        // على ذكر لم يسبّحه المستخدم.
        val done = mapOf("a1" to 10, "b2" to 3)
        assertEquals(done, CustomAdhkar.decodeDone(CustomAdhkar.encodeDone(done)))
    }

    @Test
    fun zeroCountsAreDropped() {
        assertEquals("", CustomAdhkar.encodeDone(mapOf("a1" to 0)))
    }

    @Test
    fun doneOfEmptyIsEmpty() {
        assertTrue(CustomAdhkar.decodeDone(null).isEmpty())
        assertTrue(CustomAdhkar.decodeDone("باطل").isEmpty())
    }

    @Test
    fun doneSurvivesDeletingTheItemAboveIt() {
        // حذف `a1` لا يغيّر عدّ `b2`
        val before = CustomAdhkar.decodeDone(CustomAdhkar.encodeDone(mapOf("a1" to 10, "b2" to 3)))
        assertEquals(3, before["b2"])
        val after = CustomAdhkar.decodeDone(
            CustomAdhkar.encodeDone(before - "a1"),
        )
        assertEquals(3, after["b2"])
        assertEquals(null, after["a1"])
    }
}
