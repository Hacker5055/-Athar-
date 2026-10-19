package com.athar.athar.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/// ⚠️ لماذا `compositionLocalOf` لا متغيّر عام؟
/// لأن تغيير اللغة يجب أن **يعيد تركيب** كل ما يقرأ النصوص. متغيّر
/// عام يجعل الشاشة تُترجم نصفها وتترك نصفها حتى تُعاد زيارتها.
val LocalStrings = compositionLocalOf { Strings(AppLocale.AR) }

val LocalAppLocale = compositionLocalOf { AppLocale.AR }

@Composable
fun ProvideStrings(
    locale: AppLocale,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalStrings provides Strings(locale),
        LocalAppLocale provides locale,
        // ⚠️ الاتجاه جزء من الترجمة لا شيء منفصل عنها: واجهة فرنسية
        // بترتيب عربي تبدو معطوبة ولو كانت كل كلمة صحيحة
        LocalLayoutDirection provides if (locale.isRtl) {
            LayoutDirection.Rtl
        } else {
            LayoutDirection.Ltr
        },
        content = content,
    )
}
