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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import com.athar.athar.ui.ruqya.RuqyaScreen
import com.athar.athar.ui.athary.TrackerScreen
import com.athar.athar.ui.theme.Appearance
import com.athar.athar.ui.asma.AsmaScreen
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Widgets
import com.athar.athar.ui.zakah.ZakahScreen
import com.athar.athar.ui.components.SectionTitle
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import com.athar.athar.ui.components.AtharPageTitle
import com.athar.athar.ui.theme.AtharPageMargin

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
        // ⚠️ **عنوانُ الفصلّ**: المزيدُ كان يبدأُ بشريطِ الأقسام
        // بلا عنوان، فكان نصفُ التطبيقِ بلا بابٍ.
        AtharPageTitle(
            title = s.tabMore,
            modifier = Modifier.padding(horizontal = AtharPageMargin),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AtharPageMargin, vertical = 10.dp),
        ) {
            SectionTitle(
                title = s.language,
                subtitle = s.chooseLanguage,
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

        // ⚠️ **أشرطةُ أقسامٍ لا شرائحَ عائمة**: تسعةُ أقسامٍ
        // في «المزيد» — والشرائحُ العائمةُ تتراكمُ حتى يصير
        // الصفُّ جدارًا من البطاقات. وشريطُ الأقسامِ يبيّن: كم
        // قسمًا، وأين أنا.
        ScrollableTabRow(
            selectedTabIndex = section,
            edgePadding = 8.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            // ⚠️ حاسبة الزكاة **قسمٌ لا زرّ**: ليست ابنًا لصفحة
            // أخرى، بل أداةٌ في «المزيد».
            listOf(
                s.trackerTitle,
                s.notifications,
                s.moreAsma,
                s.appearance,
                s.sources,
                s.widgetAdd,
                s.backupTitle,
                s.zakahTitle,
                s.ruqyaTitle,
            ).forEachIndexed { index, label ->
                Tab(
                    selected = index == section,
                    onClick = { section = index },
                    text = { Text(label, maxLines = 1) },
                )
            }
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
                7 -> ZakahScreen()
                else -> RuqyaScreen()
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
