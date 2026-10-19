package com.athar.athar.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.ui.theme.Appearance
import com.athar.athar.ui.theme.FontScaleLevel
import com.athar.athar.ui.theme.ReaderPalette
import com.athar.athar.ui.theme.ReaderTheme
import com.athar.athar.ui.theme.ThemeMode
import com.athar.athar.ui.theme.AmiriFamily
import com.athar.athar.ui.components.SubHeading

/// شاشة المظهر: الثيم · حجم الخط · ثيم القراءة.
///
/// ⚠️ كل قسم يرى أثره فورًا — لا زر «حفظ» ولا «تطبيق». من يكبّر
/// الخط يريد أن يقرأ النموذج كبيرًا **الآن** ليقرّر، لا أن يخمّن
/// ثم يعود.
@Composable
fun AppearanceScreen(
    appearance: Appearance,
    onChange: (Appearance) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // ⚠️ **لا عنوانَ في «المزيد»**: اسمُ القسمِ معروضٌ في
        // شريطِ الأقسامِ فوقَه.

        // ── الثيم ────────────────────────────────────────
        SubHeading(s.themeMode)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = appearance.themeMode == mode,
                    onClick = { onChange(appearance.copy(themeMode = mode)) },
                    label = { Text(s.t(keyFor(mode))) },
                )
            }
        }

        // ── حجم الخط ─────────────────────────────────────
        Column {
            SubHeading(s.fontSize)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FontScaleLevel.entries.forEach { level ->
                    FilterChip(
                        selected = appearance.fontScale == level,
                        onClick = { onChange(appearance.copy(fontScale = level)) },
                        label = { Text(s.t(keyFor(level))) },
                    )
                }
            }
            // ⚠️ نموذج يُقرأ بالمقاس المختار: الاختيار بلا معاينة
            // تخمين، والعودة إلى الشاشة لتجربة مقاس آخر ثمنها أربع
            // نقرات كل مرة.
            Text(
                text = s.fontPreview,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 10.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // ── ثيم القراءة ──────────────────────────────────
        Column {
            SubHeading(s.readerTheme)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReaderTheme.entries.forEach { theme ->
                    FilterChip(
                        selected = appearance.readerTheme == theme,
                        onClick = { onChange(appearance.copy(readerTheme = theme)) },
                        label = { Text(s.t(keyFor(theme))) },
                    )
                }
            }
            ReaderPreview(theme = appearance.readerTheme, sample = s.fontPreview)
        }
    }
}

/// معاينة ثيم القراءة بلونه الحقيقي لا بوصفه.
///
/// ⚠️ وصف «ليلي» لا يغني عن رؤيته: اللون هو القرار كله، والكلمة
/// تحتمل أسودَ تامًّا أو رماديًّا خفيفًا وكلاهما «ليلي».
@Composable
private fun ReaderPreview(theme: ReaderTheme, sample: String) {
    // ⚠️ `MaterialTheme.colorScheme` استدعاء تركيبي: يُقرأ هنا داخل
    // دالة @Composable لا في دالة عادية تُبنى منها المعدّلات.
    val palette = ReaderPalette.of(theme)
    val background = if (palette.isCustom) {
        palette.background
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val foreground = if (palette.isCustom) {
        palette.onBackground
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = "﴿ $sample ﴾",
        fontSize = 22.sp,
        // ⚠️ **المعاينةُ بخطّها الحقيقي**: أميري هو الذي سيُقرأ
        // به النصّ، ومعاينةٌ بخطٍّ آخر تُدخل في الذهن صورةً
        // خاطئةً عن شكل القراءة.
        fontFamily = AmiriFamily,
        textAlign = TextAlign.Center,
        color = if (foreground == Color.Unspecified) Color.Unspecified else foreground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .background(background, MaterialTheme.shapes.medium)
            .padding(16.dp),
    )
}

private fun keyFor(mode: ThemeMode) = when (mode) {
    ThemeMode.SYSTEM -> "theme_system"
    ThemeMode.LIGHT -> "theme_light"
    ThemeMode.DARK -> "theme_dark"
}

private fun keyFor(level: FontScaleLevel) = when (level) {
    FontScaleLevel.SMALL -> "font_small"
    FontScaleLevel.NORMAL -> "font_normal"
    FontScaleLevel.LARGE -> "font_large"
    FontScaleLevel.HUGE -> "font_huge"
}

private fun keyFor(theme: ReaderTheme) = when (theme) {
    ReaderTheme.NORMAL -> "reader_normal"
    ReaderTheme.NIGHT -> "reader_night"
    ReaderTheme.SEPIA -> "reader_sepia"
}
