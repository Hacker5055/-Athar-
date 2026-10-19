package com.athar.athar.quran

/// ترجمة معاني القرآن المتاحة في التطبيق.
///
/// ⚠️ **الإسناد واجب لا تفضّل** (docs/09): كل ترجمة تحمل رخصتها ونصّ
/// إسنادها، ويُعرض الإسناد **داخل الواجهة** لا في ملفّ بعيد لا يراه
/// أحد. ولذلك مفتاحا `nameKey` و`creditKey` من حزمة الترجمة: الإسناد
/// مترجم كأي نصّ آخر.
///
/// ⚠️ `ITANI` بترخيص **CC BY-ND 4.0**، و`-ND` تعني «لا مشتقّات»: النص
/// يُقرأ من الأصل ويُعرض **كما هو حرفيًا** بلا تعديل ولا تطبيع. وهي
/// القاعدة نفسها التي نلتزمها مع الرسم العثماني — نُطبّع نسخة
/// للمقارنة فقط، ولا نلمس المعروض.
enum class QuranTranslation(
    val asset: String,
    val nameKey: String,
    val creditKey: String,
) {
    /// النص العربي وحده — لا ترجمة
    NONE("", "quran_translation_none", ""),

    /// ⚠️ ملكية عامة: المتوفى ١٩٣٦، فخرج عن حق المؤلف في نطاق
    /// «حياة + ٧٠» منذ ٢٠٠٧.
    PICKTHALL(
        "quran/translations/english_pickthall.json",
        "quran_translation_pickthall",
        "quran_credit_pickthall",
    ),

    /// ⚠️ CC BY-ND 4.0 — ترجمة معاصرة بلغة سلسة
    ITANI(
        "quran/translations/english_itani.json",
        "quran_translation_itani",
        "quran_credit_itani",
    ),
    ;

    companion object {
        val selectable: List<QuranTranslation> get() = entries.filter { it != NONE }
    }
}
