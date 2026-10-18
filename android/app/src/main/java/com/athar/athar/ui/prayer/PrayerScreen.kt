package com.athar.athar.ui.prayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.prayer.PrayerMethod
import com.athar.athar.prayer.PrayerSettings
import com.athar.athar.prayer.PrayerSettingsStore
import com.athar.athar.prayer.PrayerTimesCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormat: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

@Composable
fun PrayerScreen(
    settings: PrayerSettings,
    onSettingsChange: (PrayerSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val today = LocalDate.now()
    var pickingCity by remember { mutableStateOf(false) }

    if (pickingCity) {
        CityPickerScreen(
            onCityChosen = { city ->
                onSettingsChange(
                    settings.copy(
                        latitude = city.latitude,
                        longitude = city.longitude,
                        cityId = city.id,
                        placeName = "",
                    ),
                )
                pickingCity = false
            },
            onCoordinatesChosen = { lat, lng ->
                onSettingsChange(
                    settings.copy(
                        latitude = lat,
                        longitude = lng,
                        cityId = null,
                        placeName = "",
                    ),
                )
                pickingCity = false
            },
            onDismiss = { pickingCity = false },
            modifier = modifier,
        )
        return
    }

    /// ⚠️ اسم المدينة يُقرأ باللغة الحالية — «القاهرة» داخل تطبيق
    /// فرنسي بالكامل عيب لا يراه من يختبر بالعربية فقط.
    val placeLabel = remember(settings, s) {
        val city = settings.cityId?.let { id ->
            com.athar.athar.prayer.Cities.all.firstOrNull { it.id == id }
        }
        city?.nameFor(com.athar.athar.i18n.LocalAppLocale.current)
            ?: settings.placeName.ifBlank { s.prayerMyLocation }
    }

    val day = remember(settings, today) { PrayerTimesCalculator.calculate(settings, today) }
    val next = remember(day) { day.nextAfter(Instant.now()) }

    Column(modifier = modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = next?.let { "${s.prayerUpcoming}: ${s.t(it.first.stringKey)}" }
                        ?: s.prayerNoneLeft,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (next != null) {
                    Text(
                        text = timeFormat.format(next.second),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = placeLabel, style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { pickingCity = true }) {
                        Icon(Icons.Filled.EditLocation, contentDescription = null)
                        Text("  " + s.change)
                    }
                }
            }
        }

        LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            items(PrayerMethod.entries) { method ->
                TextButton(onClick = { onSettingsChange(settings.copy(method = method)) }) {
                    Text(
                        text = method.arabicName,
                        color = if (method == settings.method) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (method == settings.method) FontWeight.Bold else null,
                    )
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            items(day.ordered) { (slot, instant) ->
                val isNext = next?.first == slot
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = s.t(slot.stringKey),
                        fontWeight = if (isNext) FontWeight.Bold else null,
                        color = if (isNext) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = timeFormat.format(instant),
                        fontSize = 18.sp,
                        fontWeight = if (isNext) FontWeight.Bold else null,
                        color = if (isNext) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}
