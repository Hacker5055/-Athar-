package com.athar.athar.ruqya

import com.athar.athar.adhkar.AdhkarSection
import com.athar.athar.core.ArabicText

/// أبوابُ الرقية من حصن المسلم (C9).
///
/// ⚠️ **المطابقةُ بعد التطبيع** كما في `AdhkarSections`: عناوينُ
/// الأبواب مشكولةٌ كاملة («دُعَاءُ طَرْدِ الشَّيْطَانِ وَوَسَاوِسِهِ»)،
/// فأي مقارنةٍ حرفية تنكسر مع أوّل اختلافِ تشكيل.
///
/// ⚠️ والعلامات **مكتوبةٌ بالشكل المجرّد** لأنّ التطبيع يحوّل
/// «ة» إلى «ه» و«ئ» إلى «ي» — فـ«الدعاء على الوسوسة» تُكتب
/// «الوسوسه» بعد التطبيع.
object RuqyaSections {

    /// العلامات — **بعد التطبيع**.
    private val MARKERS: List<String> = listOf(
        "دعاء طرد الشيطان ووساوسه",
        "دعاء الوسوسه في الصلاه والقراءه",
        "دعاء من خشي ان يصيب شييا بعينه",
        "دعاء من راي مبتلي",
        "ما يعصم الله به من الدجال",
    )

    /// الأبوابُ الخمسة، **بترتيب الكتاب** لا بترتيب العلامات:
    /// فترتيبُ القائمة هو ترتيبُ الناس للرقية.
    fun sections(all: List<AdhkarSection>): List<AdhkarSection> =
        all.filter { section ->
            val title = ArabicText.normalize(section.title)
            MARKERS.any { marker -> marker in title }
        }

    /// هل البابُ من أبواب الرقية؟
    fun isRuqya(section: AdhkarSection): Boolean {
        val title = ArabicText.normalize(section.title)
        return MARKERS.any { marker -> marker in title }
    }
}
