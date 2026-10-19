package com.athar.athar.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.athar.athar.core.ArabicDigits

/// أرقامٌ **حسب لغة الواجهة**.
///
/// ⚠️ لماذا ليست `ArabicDigits.of` دائمًا؟ لأنّ الأرقامَ العربيةَ
/// الشرقيةَ في واجهةٍ فرنسيةٍ خطأٌ ظاهر، والعكسُ كذلك: أرقامٌ
/// لاتينيةُ في واجهةٍ عربيةٍ تُقرأ كأنّها من جهازٍ آخر.
///
/// ⚠️ والوحدةُ واحدة: التحويلُ في مكانٍ واحد، فلا يختلف رقمٌ عن
/// جاره في نفس الشاشة.
///
/// @return دالةٌ تحوّل العددَ إلى نصّه بلغة الواجهة.
@Composable
fun localDigits(): (Int) -> String {
    val locale = LocalAppLocale.current
    return remember(locale) {
        { value -> digitsOf(locale, value) }
    }
}

/// الأرقامُ بلغةٍ معيّنة — نسخةٌ بلا تركيبٍ للمَن لا يملك
/// `@Composable`: الدوالُّ المحليةُ ونصوصُ البطاقات.
///
/// ⚠️ **الوحدةُ واحدة**: التحويلُ في مكانٍ واحد، فلا يختلف رقمٌ
/// عن جاره في نفس الشاشة.
fun digitsOf(locale: AppLocale, value: Int): String =
    if (locale.isRtl) ArabicDigits.of(value) else value.toString()
