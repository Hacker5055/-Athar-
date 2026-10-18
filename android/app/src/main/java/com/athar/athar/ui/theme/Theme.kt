package com.athar.athar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = AtharPrimary,
    onPrimary = AtharOnPrimary,
    primaryContainer = AtharPrimaryContainer,
    secondary = AtharAccent,
    background = AtharBackground,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7FD3C0),
    onPrimary = Color(0xFF00382F),
    secondary = AtharAccent,
)

/// ألوان شاشة القراءة وحدها.
///
/// ⚠️ `NORMAL` يترك كل شيء كما هو (ألوان شفّافة): ثيم القراءة العادي
/// **هو** ثيم التطبيق، فلا نفرض عليه لونًا يخالف ما اختاره المستخدم
/// من فاتح أو داكن.
data class ReaderPalette(
    val background: Color,
    val onBackground: Color,
    val divider: Color,
) {
    val isCustom: Boolean get() = background != Color.Unspecified

    companion object {
        val NORMAL = ReaderPalette(Color.Unspecified, Color.Unspecified, Color.Unspecified)

        /// ⚠️ ليلي: أسود قريب لا أسود خالص. الأسود الخالص على شاشة
        /// OLED يجعل الحروف «تحترق» بتباين حادّ يؤذي العين في الظلام.
        val NIGHT = ReaderPalette(
            background = Color(0xFF121417),
            onBackground = Color(0xFFE4E1DA),
            divider = Color(0xFF2A2E33),
        )

        /// ⚠️ سيبيا: ورق قديم. التباين فيه مقصود أن يكون أقلّ من
        /// الأبيض والأسود، فهو أوفر للعين في القراءة الطويلة.
        val SEPIA = ReaderPalette(
            background = Color(0xFFF4E4C1),
            onBackground = Color(0xFF3E2C17),
            divider = Color(0xFFDCCBA4),
        )

        fun of(theme: ReaderTheme) = when (theme) {
            ReaderTheme.NORMAL -> NORMAL
            ReaderTheme.NIGHT -> NIGHT
            ReaderTheme.SEPIA -> SEPIA
        }
    }
}

@Composable
fun AtharTheme(
    appearance: Appearance = Appearance(),
    content: @Composable () -> Unit,
) {
    val darkTheme = when (appearance.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
