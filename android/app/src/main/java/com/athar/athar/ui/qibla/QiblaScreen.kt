package com.athar.athar.ui.qibla

import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.qibla.DeviceCompass
import com.athar.athar.qibla.QiblaMath
import com.athar.athar.qibla.SunQibla
import com.athar.athar.ui.theme.AtharCream
import com.athar.athar.ui.theme.AtharGold
import com.athar.athar.ui.theme.AtharHeroBottom
import com.athar.athar.ui.theme.AtharPrimaryContainer
import com.athar.athar.i18n.localDigits
import com.batoulapps.adhan.Coordinates
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.batoulapps.adhan.Qibla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import com.athar.athar.ui.components.AtharPageTitle
import com.athar.athar.ui.theme.AtharPageMargin

/// ⚠️ نُدير **الوردة** عكس اتجاه الهاتف، لا المؤشّر: فتبقى علامة
/// «شمال» حيث الشمال فعلًا، ويبقى مؤشّر القبلة مثبتًا على الوردة عند
/// زاويته. حين يصعد المؤشّر إلى أعلى الشاشة فالهاتف يشير إلى القبلة.
@Composable
fun QiblaScreen(
    latitude: Double,
    longitude: Double,
    /// اسمُ المدينةِ التي تُحسبُ لها القبلة — وإلا صارت الأرقامُ
    /// غامضةً: اتجاهٌ من أين؟
    placeName: String = "",
    modifier: Modifier = Modifier,
) {
    val bearing = remember(latitude, longitude) {
        Qibla(Coordinates(latitude, longitude)).direction
    }
    val s = LocalStrings.current
    val compass = rememberDeviceCompass()
    LaunchedEffect(latitude, longitude) { compass.setLocation(latitude, longitude) }

    val azimuth by compass.azimuth.collectAsState()
    val accuracy by compass.accuracy.collectAsState()

    Column(
        // ⚠️ **قابل للتمرير**: البوصلة ٢٨٠ بكسل والبطاقة تحتها،
        // فلا يظهر إلا نصفُها على شاشةٍ صغيرة.
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AtharPageMargin, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ⚠️ **عنوانُ الفصلّ**: القبلةُ كانت تبدأُ بالبوصلة
        // بلا عنوان، فكان نصفُ التطبيقِ بلا بابٍ.
        AtharPageTitle(
            title = s.tabQibla,
            subtitle = placeName.takeIf { it.isNotBlank() },
            modifier = Modifier.fillMaxWidth(),
        )
        when {
            !compass.isAvailable -> {
                Text(
                    text = s.qiblaNoSensor,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = s.tArgs("qibla_bearing_from_north", "deg" to bearing.toInt()),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            azimuth == null -> {
                CircularProgressIndicator()
                Text(
                    text = s.loading,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            else -> {
                val heading = azimuth ?: 0f
                val offset = QiblaMath.offsetTo(bearing, heading.toDouble())
                val degrees = abs(offset).toInt()

                Box(contentAlignment = Alignment.Center) {
                    CompassRose(
                        qiblaBearing = bearing,
                        deviceAzimuth = heading.toDouble(),
                        modifier = Modifier.size(280.dp),
                    )
                    // سهم ثابت: هذا ما يشير إليه أعلى الهاتف
                    Icon(
                        imageVector = Icons.Filled.Navigation,
                        contentDescription = s.qiblaDeviceDirection,
                        tint = AtharGold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(30.dp)
                            .rotate(180f),
                    )
                }

                Text(
                    text = when {
                        degrees <= 2 -> s.qiblaAligned
                        offset > 0 -> s.tArgs("qibla_turn_right", "deg" to degrees)
                        else -> s.tArgs("qibla_turn_left", "deg" to degrees)
                    },
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp),
                )
                Text(
                    text = s.tArgs("qibla_bearing_geo", "deg" to bearing.toInt()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE ||
                    accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW
                ) {
                    Text(
                        text = s.calibrate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }
        }

        // ⚠️ القبلة الشمسية **أسفل البوصلة** لا فوقها: هي بديلٌ
        // عنها لا منافسة، ومن لا يملك مستشعرًا يجدها هناك.
        Spacer(Modifier.height(20.dp))
        SunQiblaCard(latitude = latitude, longitude = longitude)
    }
}

/// بطاقة القبلة الشمسية (D3).
///
/// ⚠️ **الوصف لا الوعد**: إن لم يكن في هذه السنة يومٌ تشرق فيه
/// الشمس في اتجاه القبلة، فالتطبيق يقول ذلك. والاختراع أضرّ من
/// الصدق.
@Composable
private fun SunQiblaCard(latitude: Double, longitude: Double) {
    val s = LocalStrings.current
    val digits = localDigits()
    val zone = remember { ZoneId.systemDefault() }
    val today = remember { LocalDate.now() }
    val year = today.year

    val days = remember(latitude, longitude, year, zone) {
        SunQibla.qiblaSunDays(year, latitude, longitude, zone)
    }
    val moment = remember(latitude, longitude, today, zone) {
        SunQibla.shadowMoment(today, latitude, longitude, zone)
    }
    val now = remember { Instant.now() }
    val sun = remember(latitude, longitude, zone) {
        SunQibla.position(now, latitude, longitude, zone.rules.getOffset(now).totalSeconds)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = s.qiblaSunTitle,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = s.qiblaSunBody,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            if (days.isEmpty()) {
                Text(
                    text = s.qiblaSunNone,
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                Text(
                    text = s.qiblaSunDays,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                for (day in days) {
                    Text(
                        // ⚠️ `day` تاريخٌ لا رقمٌ: والصوابُ
                        // رقمُ اليومِ من اليوم.
                        text = digits(day.dayOfMonth),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = s.qiblaSunShadow,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = if (moment == null) {
                    s.qiblaSunShadowNone
                } else {
                    String.format(
                        java.util.Locale.US,
                        "%02d:%02d",
                        moment.minutesOfDay / 60,
                        moment.minutesOfDay % 60,
                    )
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = s.tArgs(
                    "qibla_sun_now",
                    "alt" to sun.altitude.toInt(),
                    "az" to sun.azimuth.toInt(),
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun rememberDeviceCompass(): DeviceCompass {
    val context = LocalContext.current
    val compass = remember { DeviceCompass(context) }
    DisposableEffect(compass) {
        compass.start()
        onDispose { compass.stop() }
    }
    return compass
}

@Composable
private fun CompassRose(
    qiblaBearing: Double,
    deviceAzimuth: Double,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    // ⚠️ **البوصلةُ قرصٌ داكنٌ وحروفٌ ذهبية**: البوصلةُ الحقيقية
    // كذلك — قرصٌ داكنٌ وإبرةٌ مضيئة. والرسمُ الخطّي وحده على
    // خلفيةٍ فاتحة يبدو مخططًا لا بوصلة.
    val discColor = AtharHeroBottom
    val ringColor = AtharGold
    val tickColor = AtharPrimaryContainer.copy(alpha = 0.45f)
    val accent = AtharGold

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .rotate(-deviceAzimuth.toFloat()),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            val ring = 2.dp.toPx()
            // ⚠️ **القرصُ أولًا** ثم الحلقةُ فوقه: ما رُسم بعدَه
            // يغطّي ما قبلَه، فالترتيبُ هو كلُّ شيء.
            drawCircle(
                color = discColor,
                radius = radius - ring,
                style = Fill,
            )
            drawCircle(
                color = ringColor,
                radius = radius - ring,
                style = Stroke(width = ring),
            )
            for (i in 0 until 24) {
                val radians = Math.toRadians(QiblaMath.canvasAngle(i * 15.0))
                val inner = if (i % 6 == 0) radius * 0.78f else radius * 0.87f
                drawLine(
                    color = tickColor,
                    start = center + polar(inner, radians),
                    end = center + polar(radius - ring * 2, radians),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            val qiblaRadians = Math.toRadians(QiblaMath.canvasAngle(qiblaBearing))
            val tip = center + polar(radius * 0.62f, qiblaRadians)
            drawLine(
                color = accent,
                start = center,
                end = tip,
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawCircle(color = accent, radius = 9.dp.toPx(), center = tip)
        }

        // ⚠️ ندير كل لقبلة بعكس دوران الوردة ليبقى النصّ معتدلًا
        val upright = deviceAzimuth.toFloat()
        Text(s.north, fontSize = 13.sp, fontWeight = FontWeight.Bold,
            color = AtharCream,
            modifier = Modifier.align(Alignment.TopCenter).rotate(upright))
        Text(s.south, fontSize = 13.sp, color = AtharCream,
            modifier = Modifier.align(Alignment.BottomCenter).rotate(upright))
        Text(s.east, fontSize = 13.sp, color = AtharCream,
            modifier = Modifier.align(Alignment.CenterEnd).rotate(upright))
        Text(s.west, fontSize = 13.sp, color = AtharCream,
            modifier = Modifier.align(Alignment.CenterStart).rotate(upright))
    }
}

private fun polar(radius: Float, radians: Double): Offset =
    Offset(cos(radians).toFloat() * radius, sin(radians).toFloat() * radius)
