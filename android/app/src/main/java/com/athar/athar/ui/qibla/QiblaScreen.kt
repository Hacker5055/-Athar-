package com.athar.athar.ui.qibla

import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.qibla.DeviceCompass
import com.athar.athar.qibla.QiblaMath
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Qibla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/// ⚠️ نُدير **الوردة** عكس اتجاه الهاتف، لا المؤشّر: فتبقى علامة
/// «شمال» حيث الشمال فعلًا، ويبقى مؤشّر القبلة مثبتًا على الوردة عند
/// زاويته. حين يصعد المؤشّر إلى أعلى الشاشة فالهاتف يشير إلى القبلة.
@Composable
fun QiblaScreen(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier,
) {
    val bearing = remember(latitude, longitude) {
        Qibla(Coordinates(latitude, longitude)).direction
    }
    val compass = rememberDeviceCompass()
    LaunchedEffect(latitude, longitude) { compass.setLocation(latitude, longitude) }

    val azimuth by compass.azimuth.collectAsState()
    val accuracy by compass.accuracy.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when {
            !compass.isAvailable -> {
                Text(
                    text = "هذا الجهاز لا يحتوي على بوصلة",
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "زاوية القبلة من الشمال: ${bearing.toInt()}°",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            azimuth == null -> {
                CircularProgressIndicator()
                Text(
                    text = "جارٍ قراءة البوصلة…",
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
                        contentDescription = "اتجاه الهاتف",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(30.dp)
                            .rotate(180f),
                    )
                }

                Text(
                    text = when {
                        degrees <= 2 -> "أنت باتجاه القبلة ✓"
                        offset > 0 -> "أدر الهاتف يمينًا $degrees°"
                        else -> "أدر الهاتف يسارًا $degrees°"
                    },
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp),
                )
                Text(
                    text = "القبلة على ${bearing.toInt()}° من الشمال الجغرافي",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE ||
                    accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW
                ) {
                    Text(
                        text = "البوصلة تحتاج معايرة — حرّك الهاتف على شكل ٨",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }
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
    val ringColor = MaterialTheme.colorScheme.outline
    val tickColor = MaterialTheme.colorScheme.onSurfaceVariant
    val accent = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .rotate(-deviceAzimuth.toFloat()),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            val ring = 2.dp.toPx()
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
        Text("شمال", fontSize = 13.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopCenter).rotate(upright))
        Text("جنوب", fontSize = 13.sp,
            modifier = Modifier.align(Alignment.BottomCenter).rotate(upright))
        Text("شرق", fontSize = 13.sp,
            modifier = Modifier.align(Alignment.CenterEnd).rotate(upright))
        Text("غرب", fontSize = 13.sp,
            modifier = Modifier.align(Alignment.CenterStart).rotate(upright))
    }
}

private fun polar(radius: Float, radians: Double): Offset =
    Offset(cos(radians).toFloat() * radius, sin(radians).toFloat() * radius)
