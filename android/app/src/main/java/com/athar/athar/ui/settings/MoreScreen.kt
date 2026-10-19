package com.athar.athar.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.i18n.AppLocale
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.ui.athary.TrackerScreen
import com.athar.athar.ui.theme.Appearance
import com.athar.athar.ui.asma.AsmaScreen
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Widgets
import com.athar.athar.ui.zakah.ZakahScreen
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size

/// تبويب «المزيد» — المدخل إلى ما لا يحتاج تبويبًا خاصًا به
/// (`docs/03`: المتتبع · الإعدادات · عن التطبيق).
///
/// ⚠️ اللغة في **الرأس الدائم** لا داخل قسم: من يفتح التطبيق بلغة
/// لا يقرأها يحتاجها في وجهه فورًا، لا بعد بحث في الأقسام.
@Composable
fun MoreScreen(
    locale: AppLocale,
    appearance: Appearance,
    onLocaleChange: (AppLocale) -> Unit,
    onAppearanceChange: (Appearance) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    var section by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text(s.language, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(
                s.chooseLanguage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppLocale.entries.forEach { option ->
                    FilterChip(
                        selected = locale == option,
                        onClick = { onLocaleChange(option) },
                        label = { Text(option.nativeName) },
                    )
                }
            }
        }
        HorizontalDivider()

        androidx.compose.foundation.layout.Row(
            // ⚠️ قابل للتمرير أفقيًا: خمس شرائح بثلاث لغات تتجاوز عرض
            // الشاشة على الأجهزة الصغيرة، والقصّ الصامت أسوأ من التمرير.
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = section == 0,
                onClick = { section = 0 },
                label = { Text(s.trackerTitle) },
            )
            FilterChip(
                selected = section == 1,
                onClick = { section = 1 },
                label = { Text(s.notifications) },
            )
            FilterChip(
                selected = section == 2,
                onClick = { section = 2 },
                label = { Text(s.moreAsma) },
            )
            FilterChip(
                selected = section == 3,
                onClick = { section = 3 },
                label = { Text(s.appearance) },
            )
            FilterChip(
                selected = section == 4,
                onClick = { section = 4 },
                label = { Text(s.sources) },
            )
            FilterChip(
                selected = section == 5,
                onClick = { section = 5 },
                label = { Text(s.widgetAdd) },
            )
            FilterChip(
                selected = section == 6,
                onClick = { section = 6 },
                label = { Text(s.backupTitle) },
            )
            // ⚠️ حاسبة الزكاة **قسمٌ لا زرّ**: ليست ابنًا لصفحة
            // أخرى، بل أداةٌ في «المزيد».
            FilterChip(
                selected = section == 7,
                onClick = { section = 7 },
                label = { Text(s.zakahTitle) },
            )
        }

        // ⚠️ `weight` يعطي الشاشة الداخلية ارتفاعًا محدودًا فتعمل
        // LazyColumn الخاصة بها — بلا ذلك يتعطل القياس
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when (section) {
                0 -> TrackerScreen()
                1 -> SettingsScreen()
                2 -> AsmaScreen()
                3 -> AppearanceScreen(
                    appearance = appearance,
                    onChange = onAppearanceChange,
                )
                4 -> LicensesScreen()
                5 -> WidgetScreen()
                6 -> BackupScreen()
                else -> ZakahScreen()
            }
        }
    }
}

/// كيف يُضاف الودجت.
///
/// ⚠️ الودجت لا يُضاف من داخل التطبيق — أندرويد لا يسمح بذلك. وأفضل
/// ما نملكه أن نشرح الطريق، لا أن نترك ميزة موجودة لا أحد يعرفها.
@Composable
private fun WidgetScreen(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Filled.Widgets,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = s.widgetAdd,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = s.widgetAddHint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
