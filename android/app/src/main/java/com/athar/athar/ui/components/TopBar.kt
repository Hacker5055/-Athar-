package com.athar.athar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.i18n.LocalStrings

/// شريطُ العنوانِ في الشاشاتِ الفرعية.
///
/// ⚠️ **شريطٌ واحدٌ لا عشرة**: كلُّ شاشةٍ كانت تبني رأسَها بنفسها
/// — بمسافةٍ مختلفةٍ ومقاسِ خطٍّ مختلفٍ وسهمِ رجوعٍ لا يتقلّب
/// دائمًا. فالنتيجةُ تطبيقٌ «مجمَّع» لا تطبيقٌ مصمَّم. والرأسُ
/// الواحدُ يجعلُ الانتقالَ بين الشاشاتِ متوحّدًا.
///
/// ⚠️ **السهمُ يتقلّب**: سهمُ الرجوعِ الثابتُ يشير إلى اليسار
/// دائمًا، وفي العربيةِ «الرجوعُ» إلى اليمين. فالرسمُ
/// `AutoMirrored` هو الذي ينقلبُ مع اللغة.
@Composable
fun AtharTopBar(
    title: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleFont: FontFamily? = null,
    titleSize: TextUnit = 21.sp,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    divider: Boolean = false,
    dividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
    action: @Composable RowScope.() -> Unit = {},
) {
    val s = LocalStrings.current
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = s.back,
                        tint = titleColor,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = titleFont,
                    color = titleColor,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = subtitleColor,
                    )
                }
            }
            action()
        }
        if (divider) {
            HorizontalDivider(color = dividerColor)
        }
    }
}
