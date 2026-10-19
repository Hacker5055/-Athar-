package com.athar.athar.adhkar

/// ذكر واحد: نصّه، وعدد مراته، وتخريجه.
///
/// ⚠️ `reference` (التخريج) ليس زينة: docs/02 يشترطه (C1)، وهو ما
/// يفرق بين ذكر ثابت عن النبي ﷺ وبين نصّ لا أصل له.
data class Dhikr(
    val text: String,
    val count: Int,
    val reference: String,
)

/// قسم من أقسام الكتاب (باب): عنوانه وأذكاره.
data class AdhkarSection(
    val title: String,
    val items: List<Dhikr>,
)

/// مصدر النص — الإسناد واجب، لا تفضّل (docs/09).
object AdhkarSources {
    const val BOOK = "حصن المسلم — سعيد بن علي بن وهف القحطاني"
    const val DATASET = "نسخة JSON: asellam/HisnElMuslim (MIT)"
    const val ATTRIBUTION = "$BOOK\n$DATASET"
}
