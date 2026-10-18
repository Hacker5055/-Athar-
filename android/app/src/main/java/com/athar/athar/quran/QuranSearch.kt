package com.athar.athar.quran

import com.athar.athar.prayer.CitySearch

/// البحث في نصّ القرآن — **محض بالكامل** ويُختبر على JVM.
object QuranSearch {

    data class Hit(val ayah: Ayah, val surahNameAr: String)

    /// أقصى عدد النتائج المعروضة.
    ///
    /// ⚠️ بلا حدٍّ قد يُطابق حرف شائع آلاف الآيات، فيتجمّد البناء على
    /// جهاز ضعيف والقرآن كله ٦٢٣٦ آية. الحدّ يجعل زمن البحث محدودًا
    /// مهما كتب المستخدم.
    const val DEFAULT_LIMIT = 40

    fun search(
        query: String,
        surahs: List<Surah>,
        ayahsOf: (Int) -> List<Ayah>,
        limit: Int = DEFAULT_LIMIT,
    ): List<Hit> {
        val needle = normalize(query.trim())
        if (needle.isEmpty()) return emptyList()

        val names = surahs.associate { it.id to it.nameAr }
        val hits = mutableListOf<Hit>()
        for (surah in surahs) {
            for (ayah in ayahsOf(surah.id)) {
                if (needle in normalize(ayah.text)) {
                    hits += Hit(ayah, names[ayah.surah].orEmpty())
                    if (hits.size >= limit) return hits
                }
            }
        }
        return hits
    }

    /// ⚠️ **للمطابقة فقط، لا للتخزين ولا للعرض.**
    ///
    /// النص العثماني مشكول بالكامل، فمن يكتب «الحمد لله» بلا تشكيل لن
    /// يجد «ٱلۡحَمۡدُ لِلَّهِ» إلا بتطبيع الطرفين. لكن الرخصة (CC BY 3.0)
    /// **تمنع تعديل النص**، فنُطبّع نسخة للمقارنة ونعرض الأصل كما هو
    /// حرفيًا — ولا نلمس `ayah.text` أبدًا.
    private fun normalize(text: String): String = CitySearch.normalize(text)
}
