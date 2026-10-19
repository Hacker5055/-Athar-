package com.athar.athar.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.athar.athar.i18n.AppLocale
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.i18n.LocaleStore
import com.athar.athar.i18n.ProvideStrings
import com.athar.athar.notification.NotificationScheduler
import com.athar.athar.prayer.PrayerSettingsStore
import com.athar.athar.ui.adhkar.AdhkarScreen
import com.athar.athar.ui.prayer.PrayerScreen
import com.athar.athar.ui.qibla.QiblaScreen
import com.athar.athar.ui.tasbih.TasbihScreen
import com.athar.athar.ui.quran.QuranScreen
import com.athar.athar.ui.settings.MoreScreen
import com.athar.athar.ui.theme.Appearance
import com.athar.athar.ui.theme.AppearanceStore
import com.athar.athar.ui.theme.AtharTheme

private val kTabIcons: List<ImageVector> = listOf(
    Icons.Filled.Home,
    Icons.Filled.MenuBook,
    Icons.Filled.Star,
    // ⚠️ السبحة **تبويب** لا بند في «المزيد»: E1–E4 كلها P0، ومن
    // يسبّح بعد كل صلاة لا يفتح تبويبين ليصل إلى عدّاده.
    // ⚠️ والأيقونة من أيقونات **مستخدمة أصلًا** في ملف آخر: أسماء
    // الحزمة `material-icons-extended` لا تُحصى، واسم يبدو بديهيًّا
    // قد لا يكون فيها — فالتخمين يكلّف دورة بناء.
    Icons.Filled.RestartAlt,
    Icons.Filled.Explore,
    Icons.Filled.MoreHoriz,
)

/// الهيكل: خمس تبويبات. المواقيت على «الرئيسية» لأنها أكثر ما يُفتح
/// يوميًا (قرار docs/02).
@Composable
fun AtharApp() {
    val context = LocalContext.current
    var locale by remember { mutableStateOf(LocaleStore.load(context)) }
    var appearance by remember { mutableStateOf(AppearanceStore.load(context)) }

    ProvideStrings(locale) {
        // ⚠️ الثيم هنا لا في MainActivity: اللغة والمظهر يُقرآن من
        // التخزين عند التشغيل لا مرة واحدة عند إنشاء النشاط، وحتى
        // يتغيّرا فورًا بلا إعادة إنشاء.
        AtharTheme(appearance) {
            // ⚠️ نضاعف `fontScale` لا نستبدله: إعداد الخط في أندرويد
            // نفسه يبقى محترمًا، ومقاس التطبيق يُضاف إليه. من ضبط
            // خطّ النظام كبيرًا ثم اختار «صغير» في أثر يرى نصًّا
            // كبيرًا قليلًا لا صغيرًا مطلقًا.
            val density = LocalDensity.current
            val scaled = remember(density, appearance.fontScale) {
                Density(density.density, density.fontScale * appearance.fontScale.scale)
            }
            CompositionLocalProvider(LocalDensity provides scaled) {
                AppShell(
                    locale = locale,
                    appearance = appearance,
                    onLocaleChange = { next ->
                        locale = next
                        LocaleStore.save(context, next)
                        // ⚠️ نصوص التنبيهات المجدولة مخزّنة في النظام، فتبقى
                        // بلغتها القديمة إن لم نُجدولها من جديد
                        NotificationScheduler.rescheduleFromStored(context)
                    },
                    onAppearanceChange = { next ->
                        appearance = next
                        AppearanceStore.save(context, next)
                    },
                )
            }
        }
    }
}

@Composable
private fun AppShell(
    locale: AppLocale,
    appearance: Appearance,
    onLocaleChange: (AppLocale) -> Unit,
    onAppearanceChange: (Appearance) -> Unit,
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var settings by remember { mutableStateOf(PrayerSettingsStore.load(context)) }

    /// ⚠️ إعادة الجدولة عند كل فتح: النظام ينسى التنبيهات بعد الإقلاع،
    /// والمواقيت تتغيّر يوميًا، والمستخدم قد يغيّر مدينته.
    LaunchedEffect(Unit) { NotificationScheduler.rescheduleFromStored(context) }

    val labels = listOf(
        s.tabHome, s.tabQuran, s.tabAdhkar, s.tabTasbih, s.tabQibla, s.tabMore,
    )

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
            1 -> QuranScreen(
                readerTheme = appearance.readerTheme,
                modifier = Modifier.padding(innerPadding),
            )
            2 -> AdhkarScreen(modifier = Modifier.padding(innerPadding))
            3 -> TasbihScreen(modifier = Modifier.padding(innerPadding))
            4 -> QiblaScreen(
                latitude = settings.latitude,
                longitude = settings.longitude,
                modifier = Modifier.padding(innerPadding),
            )
            5 -> MoreScreen(
                locale = locale,
                appearance = appearance,
                onLocaleChange = onLocaleChange,
                onAppearanceChange = onAppearanceChange,
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
