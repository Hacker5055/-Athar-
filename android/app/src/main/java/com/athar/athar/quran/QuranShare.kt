package com.athar.athar.quran

/// نصّ الآية للمشاركة (B11 من docs/02) — منطق نقي.
object QuranShare {

    /// ⚖️ النصّ **بنسبته دائمًا**: النصّ العثماني من Tanzania بموجب
    /// CC BY 3.0، وهي تجيز إعادة النشر مع النسبة وتمنع **تعديل
    /// النصّ**. والمشاركة نشر — فالنسبة فيها أوجب لا مستحبّة،
    /// وحذفُها يجعل كل من يعيد النشر مخالفًا للرخصة دون أن يدري.
    fun credit(): String = "المصدر: ${QuranSources.TEXT_SOURCE}"

    fun ayah(surahName: String, ayahNumber: Int, ayahText: String): String =
        "«$ayahText» ﴿$ayahNumber﴾\nسورة $surahName\n\n${credit()}"

    /// ⚠️ بلا سطر فارغ ولا كلمة «سورة»: نصّ يُلصق في رسالة لا يحتاج
    /// فقرة إسناد كاملة ولا لقبًا، لكن **الإسناد يبقى** — في سطر
    /// واحد أخير.
    ///
    /// ⚠️ والاسم «مختصرة» يصدق بالأحرف لا بالنية: حذفتُ السطر
    /// الفارغ وحده فكانت أطول من الكاملة بحرف، لأن « — » ثلاثة
    /// أحرف والسطر الفارغ حرفان. فحذفتُ معه كلمة «سورة».
    fun ayahCompact(surahName: String, ayahNumber: Int, ayahText: String): String =
        "«$ayahText» ﴿$ayahNumber﴾ — $surahName\n${credit()}"
}
