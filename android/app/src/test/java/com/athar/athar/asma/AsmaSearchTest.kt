package com.athar.athar.asma

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// ⚠️ عيّنة صغيرة: يُختبر **المنطق** لا البيانات، والأصل يُقرأ وقت
/// التشغيل من الأصول لا في اختبار وحدة.
class AsmaSearchTest {

    private val names = listOf(
        AsmaName(1, "الرحمن", "الرَّحْمَنُ", "الذي وسعت رحمته كل شيء"),
        AsmaName(2, "الملك", "الْمَلِكُ", "المتصرف في ملكه كيف يشاء"),
    )

    /// ⚠️ جوهر الميزة: المستخدم يكتب بلا تشكيل والنص مشكول
    @Test fun findsByUndiacritizedName() {
        assertEquals(listOf(1), AsmaSearch.search("الرحمن", names).map { it.id })
    }

    @Test fun findsByDiacritizedName() {
        assertEquals(listOf(1), AsmaSearch.search("الرَّحْمَنُ", names).map { it.id })
    }

    /// ⚠️ البحث بالمعنى مقصود: من لا يحفظ الاسم يبحث بصفة الله
    @Test fun findsByMeaning() {
        assertEquals(listOf(1), AsmaSearch.search("رحمته", names).map { it.id })
    }

    @Test fun emptyQueryReturnsAllNames() {
        assertEquals(names, AsmaSearch.search("", names))
        assertEquals(names, AsmaSearch.search("   ", names))
    }

    @Test fun noMatchYieldsNothing() {
        assertTrue(AsmaSearch.search("كلمة-لا-توجد", names).isEmpty())
    }

    @Test fun parseReadsTheSchema() {
        val parsed = AsmaRepository.parse(
            """[{"id":1,"name":"الأول","nameArabic":"الأوَّلُ","meaning":"الذي ليس قبله شيء"}]""",
        )
        assertEquals(1, parsed.size)
        assertEquals("الأول", parsed[0].name)
        assertEquals("الأوَّلُ", parsed[0].nameArabic)
        assertEquals("الذي ليس قبله شيء", parsed[0].meaning)
    }

    /// ⚠️ اسم بلا معنى لا يُعرض: الأصل يحتوي ٩٩ كاملًا، لكن أي تلف
    /// أو تعديل مستقبلي للملف لا يجوز أن يُظهر بطاقة ناقصة.
    @Test fun parseSkipsIncompleteEntries() {
        val parsed = AsmaRepository.parse(
            """[{"id":1,"name":"الأول","meaning":"الذي ليس قبله شيء"},"""
                + """{"id":2,"name":"","meaning":"بلا اسم"},"""
                + """{"id":3,"name":"الآخر","meaning":""}]""",
        )
        assertEquals(listOf(1), parsed.map { it.id })
    }

    @Test fun parseToleratesCorruptInput() {
        assertTrue(AsmaRepository.parse("{ليس-مصفوفة}").isEmpty())
        assertTrue(AsmaRepository.parse("").isEmpty())
    }

    /// ⚠️ لو أسقطنا حقل التشكيل لظهر الاسم ناقصًا لا مشكولًا
    @Test fun parseFallsBackWhenDiacritizedFieldIsMissing() {
        val parsed = AsmaRepository.parse("""[{"id":1,"name":"الأول","meaning":"معنى"}]""")
        assertEquals("الأول", parsed[0].nameArabic)
    }
}
