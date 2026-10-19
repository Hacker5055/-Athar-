package com.athar.athar.quran

import com.athar.athar.core.ArabicText

/// البحث في نصّ القرآن — **محض بالكامل** ويُختبر على JVM.
object QuranSearch {

    /// الألفُ وحده: ما يُهمَلُ في المقارنةِ المرنة.
    private const val ALEF = '\u0627'

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
        val hits = scan(needle, surahs, ayahsOf, limit)
        if (hits.isNotEmpty()) return hits
        // ⚠️ **مطابقةٌ مرنةٌ مرّةً ثانية**: الرسمُ العثمانيُّ يكتبُ
        // «ٱلۡعَٰلَمِينَ» ألفًا خنجريةً في محلِّ الألف، فمن كتب
        // «العالمين» لم يجدْ شيئًا — والخطأُ في التطبيقِ لا في
        // المستخدم. فإذا لم يجدِ البحثُ الصارمُ شيئًا بُحثَ مرّةً
        // أخرى بلا ألفات.
        // ⚠️ **والمرونةُ في الألفاتِ فقط**: لسائرِ الحروفِ أثرٌ
        // في المعنى، فتساهلُها يفتحُ بابَ نتائجَ لا يريدها
        // المستخدم. والحرفُ الواحدُ أو الاثنان لا يُبحثُ بهما
        // مرنًا، وإلّا لَفَ كلُّ لامٍ في القرآن.
        val loose = withoutAlef(needle)
        if (loose.length < 3) return emptyList()
        return scan(loose, surahs, ayahsOf, limit, loose = true)
    }

    /// مسحُ القرآن بالمُطابقة.
    ///
    /// ⚠️ **قاعدةٌ واحدة**: `ayahsOf` تُستدعى مرّةً لكلِّ سورةٍ لا
    /// لكلِّ آية، فالتحليلُ الكسولُ للمستودع لا يتكرّر.
    private fun scan(
        needle: String,
        surahs: List<Surah>,
        ayahsOf: (Int) -> List<Ayah>,
        limit: Int,
        loose: Boolean = false,
    ): List<Hit> {
        val names = surahs.associate { it.id to it.nameAr }
        val hits = mutableListOf<Hit>()
        for (surah in surahs) {
            for (ayah in ayahsOf(surah.id)) {
                val text = normalize(ayah.text)
                val comparable = if (loose) withoutAlef(text) else text
                if (needle in comparable) {
                    hits += Hit(ayah, names[ayah.surah].orEmpty())
                    if (hits.size >= limit) return hits
                }
            }
        }
        return hits
    }

    /// بلا ألفات: المقارنةُ المرنةُ تتجاهلُ الألفَ في الطرفين.
    private fun withoutAlef(text: String): String = text.filter { it != ALEF }

    /// ⚠️ **للمطابقة فقط، لا للتخزين ولا للعرض.**
    ///
    /// النص العثماني مشكول بالكامل، فمن يكتب «الحمد لله» بلا تشكيل لن
    /// يجد «ٱلۡحَمۡدُ لِلَّهِ» إلا بتطبيع الطرفين. لكن الرخصة (CC BY 3.0)
    /// **تمنع تعديل النص**، فنُطبّع نسخة للمقارنة ونعرض الأصل كما هو
    /// حرفيًا — ولا نلمس `ayah.text` أبدًا.
    private fun normalize(text: String): String = ArabicText.normalize(text)
}
