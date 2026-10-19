package com.athar.athar.tasbih

/// قالب تسبيح: نصّه وعدده المعتاد.
data class TasbihPhrase(
    val id: String,
    /// ⚠️ النصّ **عربي ثابت** لا يُترجم: التسبيح يُقال بالعربية في كل
    /// لغة، وترجمة «سبحان الله» إلى الفرنسية تسبيح آخر لا ترجمة له.
    val arabic: String,
    val defaultTarget: Int,
)

/// قوالب التسبيح (E2 من docs/02).
///
/// ⚖️ هذه صيغ التسبيح المعروفة المتواترة، لا «أذكار» بمعنى الأدعية
/// المرويّة — ولذا لا تُعرض بتخريج. ولو أُضيف إليها دعاء مرويّ يومًا
/// وجب أن يُعرض بتخريجه كما في قسم الأذكار، لا أن يُلحق هنا.
object TasbihPhrases {

    val SUBHAN = TasbihPhrase("subhan", "سبحان الله", 33)
    val HAMD = TasbihPhrase("hamd", "الحمد لله", 33)
    val AKBAR = TasbihPhrase("akbar", "الله أكبر", 33)
    val SHAHADA = TasbihPhrase("shahada", "لا إله إلا الله", 100)
    val ISTIGHFAR = TasbihPhrase("istighfar", "أستغفر الله", 100)
    val SALAWAT = TasbihPhrase("salawat", "اللهم صلِّ على محمد", 100)

    val ALL: List<TasbihPhrase> = listOf(SUBHAN, HAMD, AKBAR, SHAHADA, ISTIGHFAR, SALAWAT)

    /// الأهداف المعروضة (E3).
    val TARGETS: List<Int> = listOf(33, 66, 99, 100, 1000)

    fun byId(id: String?): TasbihPhrase = ALL.firstOrNull { it.id == id } ?: SUBHAN
}
