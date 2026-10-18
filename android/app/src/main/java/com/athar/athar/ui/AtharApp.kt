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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

private data class TabSpec(val icon: ImageVector, val label: String)

private val kTabs = listOf(
    TabSpec(Icons.Filled.Home, "الرئيسية"),
    TabSpec(Icons.Filled.MenuBook, "القرآن"),
    TabSpec(Icons.Filled.Star, "الأذكار"),
    TabSpec(Icons.Filled.Explore, "القبلة"),
    TabSpec(Icons.Filled.MoreHoriz, "المزيد"),
)

/// الهيكل الأساسي: خمس تبويبات (docs/04) — المحتوى يأتي تدريجيًا.
///
/// ⚠️ هذه الشاشة قصدُها أن تكون **أول APK قابل للتثبيت**، لا الواجهة
/// النهائية. الغرض أن نملك بناءً أخضر من اليوم الأول بدل سبعة أسابيع
/// بلا مترجم.
@Composable
fun AtharApp() {
    var selected by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                kTabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = index == selected,
                        onClick = { selected = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        alwaysShowLabel = true,
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = kTabs[selected].label)
        }
    }
}
