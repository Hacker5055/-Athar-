package com.athar.athar.ui.qibla

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Qibla

/// اتجاه القبلة.
///
/// ⚠️ هذه الزاوية من **الشمال الجغرافي**، ولا تعني شيئًا بلا بوصلة.
/// العرض الحالي زاوية ثابتة حتى نضيف مستشعر الاتجاه — وهو ليس تقديرًا
/// للقبلة بل مجرد قراءة حسابية.
@Composable
fun QiblaScreen(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier,
) {
    val direction = remember(latitude, longitude) {
        Qibla(Coordinates(latitude, longitude)).direction
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Navigation,
                contentDescription = "اتجاه القبلة",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp).rotate(direction.toFloat()),
            )
            Text(
                text = "${direction.toInt()}°",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = "من الشمال الجغرافي",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
