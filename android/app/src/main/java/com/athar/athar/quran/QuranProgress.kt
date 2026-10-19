package com.athar.athar.quran

/// آخر موضع قراءة (B9 من docs/02) — منطق نقي.
///
/// ⚠️ الاسم يُحفظ **مع** الموضع لا يُسترجع من المصحف: شاشة المواقيت
/// لا تريد أن تحمّل ميجابايت من الأصول لتكتب اسم سورة.
data class LastRead(
    val surah: Int,
    val surahName: String,
    val ayah: Int,
)

object QuranProgress {

    private val MIN = 1

    /// ⚠️ التحقّق قبل الحفظ: موضع من نسخة أقدم قد يشير إلى سورة
    /// لم تعد موجودة، و«تابع القراءة» الذي يفتح فارغًا أسوأ من
    /// غيابه.
    fun isValid(surah: Int, ayah: Int): Boolean =
        surah in MIN..114 && ayah in MIN..QuranMeta.ayahCount(surah)

    /// كم أُنقص من المصحف — من أيّ موضع.
    ///
    /// ⚠️ بالنسبة لا بالآيات: نسبة مئوية من كتاب كامل أنسب لمتابعة
    /// ختمة من «قرأت ٤٣١٢ آية».
    fun percent(surah: Int, ayah: Int): Int {
        val done = QuranMeta.verseId(surah, ayah)
        if (done <= 0) return 0
        return ((done * 100L) / QuranMeta.TOTAL_VERSES).toInt().coerceIn(0, 100)
    }
}
