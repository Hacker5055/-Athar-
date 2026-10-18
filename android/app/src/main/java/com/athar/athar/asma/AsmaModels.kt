package com.athar.athar.asma

/// اسم من أسماء الله الحسنى.
///
/// ⚠️ `nameArabic` مشكول كما ورد في المصدر، و`name` أخفّ تشكيلًا
/// (للبحث). وكلاهما — مع `meaning` — **من المصدر المرخّص** لا من
/// تأليفنا: لا ننشئ نصًا دينيًا من الذاكرة أبدًا.
data class AsmaName(
    val id: Int,
    val name: String,
    val nameArabic: String,
    val meaning: String,
)
