package com.athar.athar.zakah

/// حاسبة الزكاة (G5 من docs/02) — منطق نقي.
///
/// ⚠️ **لا سعر ثابت في التطبيق**: سعر الذهب والفضة يتغيّر كلّ
/// يوم، وسعرٌ مكتوبٌ في الكود يقدّم زكاةً خاطئة بثقة. فالسعر
/// **يدخله المستخدم**، والتطبيق يحسب عليه.
///
/// ⚠️ **النصاب بالوزن لا بالقيمة**: ٨٥ جرامًا ذهبًا
/// و٥٩٥ جرامًا فضة، وهذه أرقامٌ ثابتة في المذهب. لكنّ **النصاب
/// في النقود** يتغيّر مع السعر، فيُحسب عند المستخدم.
object Zakah {

    /// نسبة الزكاة: ٢٫٥٪ — ربع العشر.
    const val RATE_PERCENT = 2.5

    /// نصاب الذهب: ٨٥ جرامًا (٢٠ دينارًا).
    const val NISAB_GOLD_GRAMS = 85.0

    /// نصاب الفضة: ٥٩٥ جرامًا (٢٤٠ درهمًا).
    const val NISAB_SILVER_GRAMS = 595.0

    /// نصاب زكاة الفطر: ٢٫٥ كجم من القوت لكلّ فرد.
    const val FITR_KG_PER_PERSON = 2.5

    /// نتيجة الحساب: **المُدخَل** والواجب وهل بلغ النصاب.
    data class Result(
        val amount: Double,
        val nisab: Double,
        val due: Double,
        val isLiable: Boolean,
    )

    /// زكاة الذهب: الزكاة **٢٫٥٪ من قيمة ما بلغ النصاب**.
    ///
    /// ⚠️ الزكاة على **الجزء الزائد** فقط عند الشكّ في بلوغ
    /// النصاب قبل الحول، لا على الكلّ. ولمّا كان الحول شرطًا
    /// في المال المعدنيّ، فالحساب هنا على ما بلغ النصاب كاملًا.
    fun gold(grams: Double, pricePerGram: Double): Result {
        val value = grams * pricePerGram
        val nisab = NISAB_GOLD_GRAMS * pricePerGram
        return result(value, nisab)
    }

    /// زكاة الفضة: كالذهب، والفرق في وزن النصاب.
    fun silver(grams: Double, pricePerGram: Double): Result {
        val value = grams * pricePerGram
        val nisab = NISAB_SILVER_GRAMS * pricePerGram
        return result(value, nisab)
    }

    /// زكاة النقود: المقام **نصاب النقود** الذي يحدّده سعر
    /// الذهب والفضة.
    ///
    /// ⚠️ والنصاب **الأقلّ** من الذهب والفضة: من بلغ أحدهما
    /// بلغ النصاب، والأقلّ أوسَع.
    fun cash(amount: Double, goldPrice: Double, silverPrice: Double): Result {
        val nisab = nisabInCash(goldPrice, silverPrice)
        return result(amount, nisab)
    }

    /// نصاب النقود: **الأقلّ** من قيمة ٨٥ جرامًا ذهبًا ومن قيمة
    /// ٥٩٥ جرامًا فضة.
    fun nisabInCash(goldPrice: Double, silverPrice: Double): Double {
        val byGold = NISAB_GOLD_GRAMS * goldPrice
        val bySilver = NISAB_SILVER_GRAMS * silverPrice
        return minOf(byGold, bySilver)
    }

    /// زكاة الفطر: **قيمة القوت** لكلّ فرد، لا مالٌ زائد.
    ///
    /// ⚠️ والمقدار شرطٌ في المذهب، فلا يُزكى ما لم يبلغ ٢٫٥ كجم
    /// من قوت البلد. والقيمة **محسوبة** من سعر القوت لا من
    /// نقدٍ معدنيّ.
    fun fitr(people: Int, pricePerKg: Double): Result {
        val kg = people * FITR_KG_PER_PERSON
        val value = kg * pricePerKg
        return Result(
            amount = value,
            nisab = FITR_KG_PER_PERSON * pricePerKg,
            due = value,
            isLiable = people > 0,
        )
    }

    /// ⚠️ **الزكاة على ما بلغ النصاب**: ما دونه لا زكاة فيه.
    private fun result(amount: Double, nisab: Double): Result {
        val liable = amount >= nisab && nisab > 0
        val due = if (liable) amount * RATE_PERCENT / 100 else 0.0
        return Result(
            amount = amount,
            nisab = nisab,
            due = due,
            isLiable = liable,
        )
    }
}
