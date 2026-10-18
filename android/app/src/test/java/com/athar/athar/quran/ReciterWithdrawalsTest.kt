package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات سحب التلاوة — القاعدة ٣ من `docs/10`.
///
/// ⚖️ هذه ليست ميزة تُختبَر للاطمئنان: هي **الشرط الذي يبيح لنا
/// النشر**. إن لم تعمل، فنحن ننشر ما لا حقّ لنا فيه ولا نملك
/// إيقافه. ومن هنا كثرة الاختبارات على منطقٍ في أصله سطران.
class ReciterWithdrawalsTest {

    private val allIds = Reciters.ALL.map { it.id }

    // ── قراءة القائمة ────────────────────────────────────

    @Test
    fun parsesWithdrawnIds() {
        val ids = ReciterWithdrawals.parse("""{"withdrawn": ["ar.husary", "ar.minshawi"]}""")
        assertEquals(setOf("ar.husary", "ar.minshawi"), ids)
    }

    @Test
    fun emptyListWithdrawsNothing() {
        assertEquals(emptySet<String>(), ReciterWithdrawals.parse("""{"withdrawn": []}"""))
    }

    @Test
    fun blankOrNullInputWithdrawsNothing() {
        // ⚠️ شبكة مقطوعة أو ملف فارغ: لا نعرف، فلا نسحب شيئًا
        assertEquals(emptySet<String>(), ReciterWithdrawals.parse(null))
        assertEquals(emptySet<String>(), ReciterWithdrawals.parse(""))
        assertEquals(emptySet<String>(), ReciterWithdrawals.parse("   "))
    }

    @Test
    fun corruptJsonNeverThrows() {
        // ⚠️ الأهم: ملف مكسور يجب ألّا يُسكت التلاوة كلها. حذف تلاوة
        // حيّة بسبب جسون تالف ضرر أكبر من تأخّر سحب تلاوة مطلوبة.
        assertEquals(emptySet<String>(), ReciterWithdrawals.parse("ليس جسون"))
        assertEquals(emptySet<String>(), ReciterWithdrawals.parse("{"))
        assertEquals(emptySet<String>(), ReciterWithdrawals.parse("[]"))
        assertEquals(emptySet<String>(), ReciterWithdrawals.parse("""{"withdrawn": "ar.husary"}"""))
    }

    @Test
    fun ignoresEntriesThatAreNotReciterIds() {
        // ⚠️ الملف مكتوب باليد: تعليق أو مسافة زائدة أو مُعرّف قديم
        // يجب أن يمرّ بلا أثر ولا يُسقط قراءة الباقي.
        val ids = ReciterWithdrawals.parse(
            """{"withdrawn": ["ar.husary", "", "  ", "تعليق", "http://x/y", "ar.minshawi"]}""",
        )
        assertEquals(setOf("ar.husary", "ar.minshawi"), ids)
    }

    // ── التطبيق على الكتالوج ─────────────────────────────

    @Test
    fun applyRemovesWithdrawnReciters() {
        val result = ReciterWithdrawals.apply(Reciters.ALL, setOf("ar.husary"))
        assertTrue("القارئ المسحوب ما زال ظاهرًا", result.none { it.id == "ar.husary" })
        assertEquals(Reciters.ALL.size - 1, result.size)
    }

    @Test
    fun applyWithEmptySetChangesNothing() {
        assertEquals(Reciters.ALL, ReciterWithdrawals.apply(Reciters.ALL, emptySet()))
    }

    @Test
    fun applyIgnoresUnknownIds() {
        // معرّف غير معروف = قارئ حذفناه أصلًا؛ لا معنى للخطأ فيه
        assertEquals(Reciters.ALL, ReciterWithdrawals.apply(Reciters.ALL, setOf("ar.ghayrMawjud")))
    }

    @Test
    fun withdrawingEverythingEmptiesTheCatalog() {
        // ⚖️ لو سُحب كل القرّاء فلا تلاوة أصلًا. الواجهة تُخفي المشغّل،
        // ولا يجوز أن يبقى زرّ تشغيل بلا شيء يعمل خلفه.
        assertTrue(ReciterWithdrawals.apply(Reciters.ALL, allIds.toSet()).isEmpty())
    }

    // ── تصحيح الاختيار ───────────────────────────────────

    @Test
    fun fallbackMovesSelectionOffAWithdrawnReciter() {
        val others = Reciters.ALL.filter { it.id != "ar.husary" }
        val picked = ReciterWithdrawals.fallback(others, Reciters.byId("ar.husary")!!, setOf("ar.husary"))
        assertTrue("ما زال القارئ المسحوب مختارًا", picked.id != "ar.husary")
    }

    @Test
    fun fallbackKeepsAValidSelection() {
        val husary = Reciters.byId("ar.husary")!!
        assertEquals(husary, ReciterWithdrawals.fallback(Reciters.ALL, husary, setOf("ar.minshawi")))
    }

    @Test
    fun fallbackWithNoWithdrawalsIsIdentity() {
        assertEquals(
            Reciters.byId("ar.hudhaify")!!,
            ReciterWithdrawals.fallback(Reciters.ALL, Reciters.byId("ar.hudhaify")!!, emptySet()),
        )
    }
}
