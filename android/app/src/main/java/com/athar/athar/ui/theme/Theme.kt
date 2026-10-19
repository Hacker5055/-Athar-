package com.athar.athar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/// ⚠️ **الخلفيةُ ليست بيضاء**: ورقٌ كريميّ (#F7F5F0) أرفقُّ من
/// الأبيض، خاصةً للنظر الطويل. وبياضُ الشاشةِ الخالصُ على
/// ورقٍ كريميّ يبدو كأنّه لم يُصبَغ.
private val LightColors = lightColorScheme(
    primary = AtharPrimary,
    onPrimary = AtharOnPrimary,
    primaryContainer = AtharPrimaryContainer,
    onPrimaryContainer = AtharOnPrimaryContainer,
    inversePrimary = Color(0xFF7FD3C0),
    secondary = AtharSecondary,
    onSecondary = AtharOnSecondary,
    secondaryContainer = AtharSecondaryContainer,
    onSecondaryContainer = AtharOnSecondaryContainer,
    tertiary = AtharTertiary,
    onTertiary = AtharOnTertiary,
    tertiaryContainer = AtharTertiaryContainer,
    onTertiaryContainer = AtharOnTertiaryContainer,
    background = AtharBackground,
    onBackground = AtharOnBackground,
    surface = AtharSurface,
    onSurface = AtharOnSurface,
    surfaceVariant = AtharSurfaceVariant,
    onSurfaceVariant = AtharOnSurfaceVariant,
    outline = AtharOutline,
    outlineVariant = AtharOutlineVariant,
    error = AtharError,
    onError = AtharOnError,
    errorContainer = AtharErrorContainer,
    onErrorContainer = AtharOnErrorContainer,
    inverseSurface = Color(0xFF2F312F),
    inverseOnSurface = Color(0xFFF1F1ED),
)

/// ⚠️ **داكنٌ أخضرُ لا أسودُ خالص**: الأسودُ على شاشاتِ OLED يجعل
/// الحروفَ «تحترق» بتباينٍ حادّ، والأخضرُ العميقُ يهدّئه.
private val DarkColors = darkColorScheme(
    primary = Color(0xFF7FD3C0),
    onPrimary = Color(0xFF00382F),
    primaryContainer = Color(0xFF005143),
    onPrimaryContainer = Color(0xFF9CF2DC),
    inversePrimary = AtharPrimary,
    secondary = Color(0xFFE0C48C),
    onSecondary = Color(0xFF3D2E05),
    secondaryContainer = Color(0xFF56451A),
    onSecondaryContainer = Color(0xFFFDE1A6),
    tertiary = Color(0xFFA9CBE0),
    onTertiary = Color(0xFF103544),
    tertiaryContainer = Color(0xFF274C5C),
    onTertiaryContainer = Color(0xFFC5E7FF),
    background = Color(0xFF0E1512),
    onBackground = Color(0xFFE2E3DF),
    surface = Color(0xFF0E1512),
    onSurface = Color(0xFFE2E3DF),
    surfaceVariant = Color(0xFF3F4946),
    onSurfaceVariant = Color(0xFFBFC9C5),
    outline = Color(0xFF899390),
    outlineVariant = Color(0xFF3F4946),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E3DF),
    inverseOnSurface = Color(0xFF2F312F),
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
        typography = AtharTypography,
        shapes = AtharShapes,
        content = content,
    )
}
