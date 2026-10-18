package com.athar.athar.ui.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.athar.athar.notification.NotificationScheduler
import com.athar.athar.notification.NotificationSettings
import com.athar.athar.notification.NotificationSettingsStore
import com.athar.athar.notification.PrayerAlertMode
import com.athar.athar.prayer.PrayerSlot

/// ⚠️ الشروق **مذكور** بصف «لا أذان — ليست صلاة» لا مخفيًا: إخفاؤه
/// يوهم المستخدم أن التطبيق نسيه، وشرح السبب أصدق من الصمت.
private val kPrayers = listOf(
    PrayerSlot.FAJR,
    PrayerSlot.DHUHR,
    PrayerSlot.ASR,
    PrayerSlot.MAGHRIB,
    PrayerSlot.ISHA,
)

private data class ModeOption(val mode: PrayerAlertMode, val label: String)

private val kModes = listOf(
    ModeOption(PrayerAlertMode.ADHAN, "أذان"),
    ModeOption(PrayerAlertMode.BEEP, "نغمة"),
    ModeOption(PrayerAlertMode.VIBRATE, "اهتزاز"),
    ModeOption(PrayerAlertMode.SILENT, "صامت"),
    ModeOption(PrayerAlertMode.OFF, "موقوف"),
)

@Composable
fun NotificationsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var settings by remember { mutableStateOf(NotificationSettingsStore.load(context)) }
    var needsPermission by remember { mutableStateOf(false) }
    var testSent by remember { mutableStateOf(false) }

    fun commit(next: NotificationSettings) {
        settings = next
        NotificationSettingsStore.save(context, next)
        NotificationScheduler.rescheduleFromStored(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        needsPermission = !granted
        if (granted) commit(settings)
    }

    LaunchedEffect(Unit) { needsPermission = !hasNotificationPermission(context) }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                "التنبيهات",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        if (needsPermission) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "يحتاج أثر إلى إذن الإشعارات ليوقظك على الصلاة",
                            fontWeight = FontWeight.Bold,
                        )
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(
                                        Manifest.permission.POST_NOTIFICATIONS,
                                    )
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp),
                        ) { Text("منح الإذن") }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("التنبيهات مفعّلة", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "المفتاح الرئيسي — أوقفه للسفر أو الصمت",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = settings.masterEnabled,
                    onCheckedChange = { commit(settings.copy(masterEnabled = it)) },
                )
            }
            HorizontalDivider()
            Text(
                "الشروق: لا أذان — ليست صلاة",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }

        items(kPrayers) { slot ->
            PrayerCard(
                slot = slot,
                settings = settings,
                onMode = { commit(settings.withMode(slot, it)) },
                onReminder = { commit(settings.withReminder(slot, it)) },
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("تذكير الجمعة", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "الكهف · الصلاة على النبي · الغُسل",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = settings.jumuahReminder,
                            onCheckedChange = { commit(settings.copy(jumuahReminder = it)) },
                        )
                    }
                    if (settings.jumuahReminder) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(30, 60, 90, 120).forEach { minutes ->
                                FilterChip(
                                    selected = settings.jumuahMinutesBefore == minutes,
                                    onClick = {
                                        commit(settings.copy(jumuahMinutesBefore = minutes))
                                    },
                                    label = { Text("$minutes د") },
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    NotificationScheduler.scheduleTest(context)
                    testSent = true
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Filled.NotificationsActive, contentDescription = null)
                Text("  أرسل إشعارًا تجريبيًا بعد ٥ ثوان")
            }
            if (testSent) {
                Text(
                    "إن لم يصل: الإذن مرفوض، أو النظام يقيّد عمل التطبيق في الخلفية",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun PrayerCard(
    slot: PrayerSlot,
    settings: NotificationSettings,
    onMode: (PrayerAlertMode) -> Unit,
    onReminder: (Int) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(slot.arabicName, fontSize = 17.sp, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                kModes.forEach { option ->
                    FilterChip(
                        selected = settings.modeFor(slot) == option.mode,
                        onClick = { onMode(option.mode) },
                        label = { Text(option.label) },
                    )
                }
            }

            val current = settings.reminderFor(slot)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NotificationSettings.REMINDER_CHOICES.forEach { minutes ->
                    FilterChip(
                        selected = current == minutes,
                        onClick = { onReminder(minutes) },
                        label = { Text(if (minutes == 0) "بلا" else "$minutes د") },
                    )
                }
            }
        }
    }
}

private fun hasNotificationPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS,
    ) == PackageManager.PERMISSION_GRANTED
}
