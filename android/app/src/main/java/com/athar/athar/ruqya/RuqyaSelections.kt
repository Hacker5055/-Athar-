package com.athar.athar.ruqya

import com.athar.athar.quran.Ayah

/// مقتطفٌ قرآنيّ للرقية (C9) — **مرجعُه** لا نصُّه.
///
/// ⚠️ لماذا المرجع لا النص؟ لأنّ النصَّ يأتي من **أصول التطبيق
/// نفسِها** (المصحف العثماني) فلا يُكتب من الذاكرة ولا يُنسخ
/// خطأً. والاختيارُ هنا اختيارٌ مشهور، والنصُّ من المصدر.
data class RuqyaAyah(
    val id: String,
    /// مفتاحُ العنوان في الحزمة اللغوية — لا نصٌّ مكتوب هنا.
    val titleKey: String,
    val surah: Int,
    val fromAyah: Int,
    val toAyah: Int,
) {
    val ayahCount: Int get() = toAyah - fromAyah + 1
}

/// مقتطفاتُ الرقية القرآنية — القِسَم (ثلاثُ قُلَ) وآيةُ الكرسي
/// وآخرُ آيتي البقرة.
///
/// ⚠️ وهي **مختارةٌ لا كاملة**: الرقيةُ في السنة قائمةٌ على
/// التلاوة والذكر، لا على قائمةٍ مغلقة. والتطبيق يجمع المشهور
/// ولا يدّعي أنه كامل.
object RuqyaSelections {

    val ALL: List<RuqyaAyah> = listOf(
        RuqyaAyah("kursi", "ruqya_kursi", surah = 2, fromAyah = 255, toAyah = 255),
        RuqyaAyah("baqarah_last", "ruqya_baqarah_last", surah = 2, fromAyah = 285, toAyah = 286),
        RuqyaAyah("ikhlas", "ruqya_ikhlas", surah = 112, fromAyah = 1, toAyah = 4),
        RuqyaAyah("falaq", "ruqya_falaq", surah = 113, fromAyah = 1, toAyah = 5),
        RuqyaAyah("nas", "ruqya_nas", surah = 114, fromAyah = 1, toAyah = 6),
    )

    /// آياتُ المقتطف من **الأصول**.
    ///
    /// ⚠️ **ما ليس في السورة ليس في المقتطف**: إن غاب آيةٌ من
    /// المصدر فلا نخترع لها ولا نُظهر فراغًا.
    fun verses(selection: RuqyaAyah, ayahs: List<Ayah>): List<Ayah> =
        ayahs.filter { it.number in selection.fromAyah..selection.toAyah }
            .sortedBy { it.number }
}
