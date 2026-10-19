package com.athar.athar.ui.prayer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.prayer.Cities
import com.athar.athar.prayer.City
import com.athar.athar.prayer.CitySearch
import com.athar.athar.ui.components.AtharTopBar
import com.athar.athar.ui.components.AtharSearchField

@Composable
fun CityPickerScreen(
    onCityChosen: (City) -> Unit,
    onCoordinatesChosen: (Double, Double) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var notice by remember { mutableStateOf<String?>(null) }
    val results = remember(query) { CitySearch.search(query, Cities.all) }

    /// ⚠️ نستخدم `getLastKnownLocation` لا خدمة بثّ مستمر: نحتاج
    /// إحداثيات مرة واحدة لحساب المواقيت، لا تتبّعًا دائمًا. فلا
    /// نستهلك البطارية ولا نطلب الموقع في الخلفية أبدًا.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (!granted) {
            notice = s.prayerLocationDenied
            return@rememberLauncherForActivityResult
        }
        val coords = lastKnownCoordinates(context)
        if (coords != null) {
            onCoordinatesChosen(coords.first, coords.second)
        } else {
            notice = s.prayerLocationTimeout
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        AtharTopBar(
            title = s.prayerChooseCity,
            onBack = onDismiss,
            divider = true,
        )

        Button(
            onClick = {
                when (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    PackageManager.PERMISSION_GRANTED -> {
                        val coords = lastKnownCoordinates(context)
                        if (coords != null) onCoordinatesChosen(coords.first, coords.second)
                        else notice = s.prayerLocationTimeout
                    }
                    else -> permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = null)
            Text("  " + s.prayerUseMyLocation)
        }

        if (notice != null) {
            Text(
                text = notice!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        AtharSearchField(
            value = query,
            onValueChange = { query = it },
            label = s.citySearchHint,
        )

        // ⚠️ **الوزنُ لا الامتلاءُ**: الامتلاءُ يأخذُ ارتفاعَ
        // الشاشةِ كلَّه فوقَ الشريطِ فيُقصَّ آخرُ المدينة.
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(results) { city ->
                TextButton(
                    onClick = { onCityChosen(city) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(city.nameAr, fontSize = 17.sp)
                            Text(
                                "${city.nameEn} · ${city.countryCode}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

/// آخر موقع معروف — `null` إن لم يتوفر. لا نطلب تحديثًا ولا نستهلك
/// بطارية: المواقيت لا تحتاج أكثر من إحداثيات تقريبية.
private fun lastKnownCoordinates(context: Context): Pair<Double, Double>? {
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        ?: return null
    for (provider in listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)) {
        val location = runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
        if (location != null) return location.latitude to location.longitude
    }
    return null
}
