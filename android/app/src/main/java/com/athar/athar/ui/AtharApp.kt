package com.athar.athar.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.athar.athar.i18n.AppLocale
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.i18n.LocaleStore
import com.athar.athar.i18n.ProvideStrings
import com.athar.athar.notification.NotificationScheduler
import com.athar.athar.prayer.PrayerSettingsStore
import com.athar.athar.ui.adhkar.AdhkarScreen
import com.athar.athar.ui.prayer.PrayerScreen
import com.athar.athar.ui.qibla.QiblaScreen
import com.athar.athar.ui.quran.QuranScreen
import com.athar.athar.ui.settings.SettingsScreen

private val kTabIcons: List<ImageVector> = listOf(
    Icons.Filled.Home,
    Icons.Filled.MenuBook,
    Icons.Filled.Star,
    Icons.Filled.Explore,
    Icons.Filled.MoreHoriz,
)

/// الهيكل: خمس تبويبات. المواقيت على «الرئيسية» لأنها أكثر ما يُفتح
/// يوميًا (قرار docs/02).
@Composable
fun AtharApp() {
    val context = LocalContext.current
    var locale by remember { mutableStateOf(LocaleStore.load(context)) }

    ProvideStrings(locale) {
        AppShell(
            locale = locale,
            onLocaleChange = { next ->
                locale = next
                LocaleStore.save(context, next)
                // ⚠️ نصوص التنبيهات المجدولة مخزّنة في النظام، فتبقى
                // بلغتها القديمة إن لم نُجدولها من جديد
                NotificationScheduler.rescheduleFromStored(context)
            },
        )
    }
}

@Composable
private fun AppShell(
    locale: AppLocale,
    onLocaleChange: (AppLocale) -> Unit,
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var settings by remember { mutableStateOf(PrayerSettingsStore.load(context)) }

    /// ⚠️ إعادة الجدولة عند كل فتح: النظام ينسى التنبيهات بعد الإقلاع،
    /// والمواقيت تتغيّر يوميًا، والمستخدم قد يغيّر مدينته.
    LaunchedEffect(Unit) { NotificationScheduler.rescheduleFromStored(context) }

    val labels = listOf(s.tabHome, s.tabQuran, s.tabAdhkar, s.tabQibla, s.tabMore)

    Scaffold(
        bottomBar = {
            NavigationBar {
                labels.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = index == selected,
                        onClick = { selected = index },
                        icon = { Icon(kTabIcons[index], contentDescription = label) },
                        label = { Text(label) },
                        alwaysShowLabel = true,
                    )
                }
            }
        },
    ) { innerPadding ->
        when (selected) {
            0 -> PrayerScreen(
                settings = settings,
                onSettingsChange = { newSettings ->
                    settings = newSettings
                    PrayerSettingsStore.save(context, newSettings)
                    NotificationScheduler.rescheduleFromStored(context)
                },
                modifier = Modifier.padding(innerPadding),
            )
            1 -> QuranScreen(modifier = Modifier.padding(innerPadding))
            2 -> AdhkarScreen(modifier = Modifier.padding(innerPadding))
            3 -> QiblaScreen(
                latitude = settings.latitude,
                longitude = settings.longitude,
                modifier = Modifier.padding(innerPadding),
            )
            4 -> SettingsScreen(
                locale = locale,
                onLocaleChange = onLocaleChange,
                modifier = Modifier.padding(innerPadding),
            )
            else -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = labels[selected])
            }
        }
    }
}
