package com.athar.athar.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.i18n.AppLocale
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.ui.asma.AsmaScreen
import com.athar.athar.ui.athary.TrackerScreen
import com.athar.athar.ui.components.AtharPageTitle
import com.athar.athar.ui.components.SectionTitle
import com.athar.athar.ui.qibla.QiblaScreen
import com.athar.athar.ui.ruqya.RuqyaScreen
import com.athar.athar.ui.theme.Appearance
import com.athar.athar.ui.theme.AtharPageMargin
import com.athar.athar.ui.zakah.ZakahScreen

/// أقسامُ «المزيد» — ترتيبُها هو ترتيبُ أشرطة الأقسام.
private const val SECTION_TRACKER = 0
private const val SECTION_NOTIFICATIONS = 1
private const val SECTION_ASMA = 2
private const val SECTION_APPEARANCE = 3
private const val SECTION_SOURCES = 4
private const val SECTION_WIDGET = 5
private const val SECTION_BACKUP = 6
private const val SECTION_ZAKAH = 7
private const val SECTION_RUQYA = 8
private const val SECTION_QIBLA = 9

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
    qiblaLatitude: Double,
    qiblaLongitude: Double,
    qiblaPlaceName: String,
    initialSection: Int = 0,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    var section by rememberSaveable { mutableIntStateOf(initialSection) }
    // ⚠️ **اختصارُ القبلةِ قد يأتي والتطبيقُ مفتوح**: فالقسمُ
    // الجديدُ يُتَّبعُ لا القيمةُ الأولى. بلا هذا يفتح الاختصارُ
    // «المزيد» على قسمِه الأول — وهو قسمُ المتتبّع — لا على القبلة.
    LaunchedEffect(initialSection) {
        if (initialSection != 0) section = initialSection
    }

    Column(modifier = modifier.fillMaxSize()) {
        // ⚠️ **عنوانُ الفصلّ**: المزيدُ كان يبدأُ بشريطِ الأقسام
        // بلا عنوان، فكان نصفُ التطبيقِ بلا بابٍ.
        AtharPageTitle(
            title = s.tabMore,
            modifier = Modifier.padding(horizontal = AtharPageMargin),
        )

        // ⚠️ **أربعُ بلاطاتٍ في أوّلِ الصفحة**: «المزيد» محطّةُ
        // أدواتٍ لا فصلٌ في كتاب، فمن يريدُ القبلةَ أو الزكاةَ
        // الآن لا ينبغي أن يمرَّ على عشرةِ أقسامٍ حتى يجدها.
        // والبلاطةُ ضغطةٌ سريعةٌ إلى أكثرِ ما يُفتح.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AtharPageMargin, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MoreToolTile(
                icon = Icons.Filled.Explore,
                label = s.tabQibla,
                onClick = { section = SECTION_QIBLA },
            )
            MoreToolTile(
                icon = Icons.Filled.School,
                label = s.moreNames,
                onClick = { section = SECTION_ASMA },
            )
            MoreToolTile(
                icon = Icons.Filled.Calculate,
                label = s.moreZakat,
                onClick = { section = SECTION_ZAKAH },
            )
            MoreToolTile(
                icon = Icons.Filled.Insights,
                label = s.trackerTitle,
                onClick = { section = SECTION_TRACKER },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AtharPageMargin, vertical = 10.dp),
        ) {
            SectionTitle(
                title = s.language,
                subtitle = s.chooseLanguage,
            )
            Row(
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

        // ⚠️ **أشرطةُ أقسامٍ لا شرائحَ عائمة**: عشرةُ أقسامٍ
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
                s.tabQibla,
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
                SECTION_TRACKER -> TrackerScreen()
                SECTION_NOTIFICATIONS -> SettingsScreen()
                SECTION_ASMA -> AsmaScreen()
                SECTION_APPEARANCE -> AppearanceScreen(
                    appearance = appearance,
                    onChange = onAppearanceChange,
                )
                SECTION_SOURCES -> LicensesScreen()
                SECTION_WIDGET -> WidgetScreen()
                SECTION_BACKUP -> BackupScreen()
                SECTION_ZAKAH -> ZakahScreen()
                SECTION_RUQYA -> RuqyaScreen()
                else -> QiblaScreen(
                    latitude = qiblaLatitude,
                    longitude = qiblaLongitude,
                    placeName = qiblaPlaceName,
                    // ⚠️ بلا عنوانٍ خاصٍّ: شريطُ الأقسامِ فوقَه يقول
                    // «القبلة» فيكفي، وتكرارُ العنوانِ عنوانين.
                    showTitle = false,
                )
            }
        }
    }
}

/// بلاطةُ أداةٍ سريعة: أيقونةٌ في قرصٍ ملوّنٍ واسمُها تحته.
@Composable
private fun RowScope.MoreToolTile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.weight(1f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.padding(top = 6.dp),
            )
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
            fontSize = 17.sp,
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
