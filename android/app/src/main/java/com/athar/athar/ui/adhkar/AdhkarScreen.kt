package com.athar.athar.ui.adhkar

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.athar.athar.adhkar.AdhkarProgressStore
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.adhkar.AdhkarRepository
import com.athar.athar.adhkar.AdhkarSection
import com.athar.athar.adhkar.AdhkarSources
import com.athar.athar.adhkar.Dhikr
import com.athar.athar.prayer.CitySearch

@Composable
fun AdhkarScreen(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var sections by remember { mutableStateOf<List<AdhkarSection>?>(null) }
    var query by remember { mutableStateOf("") }
    var openSection by remember { mutableStateOf<AdhkarSection?>(null) }

    LaunchedEffect(Unit) { sections = AdhkarRepository.load(context) }

    val current = openSection
    if (current != null) {
        SectionScreen(
            section = current,
            onBack = { openSection = null },
            modifier = modifier,
        )
        return
    }

    val list = sections
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = s.tabAdhkar,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp),
        )

        if (list == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(s.adhkarSearchHint) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
            )

            val q = CitySearch.normalize(query.trim())
            val filtered = remember(q, list) {
                if (q.isEmpty()) {
                    list
                } else {
                    list.filter { q in CitySearch.normalize(it.title) }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered) { section ->
                    TextButton(
                        onClick = { openSection = section },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                        ) {
                            Text(
                                text = section.title,
                                fontSize = 17.sp,
                                textAlign = TextAlign.Start,
                            )
                            Text(
                                text = s.tArgs("adhkar_items_count", "n" to section.items.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    HorizontalDivider()
                }
                item {
                    Text(
                        text = AdhkarSources.ATTRIBUTION,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionScreen(
    section: AdhkarSection,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var counts by remember(section.title) {
        mutableStateOf(AdhkarProgressStore.load(context, section.title, section.items.size))
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = s.back)
            }
            Text(
                text = section.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            if (counts.any { it > 0 }) {
                IconButton(onClick = {
                    counts = List(section.items.size) { 0 }
                    AdhkarProgressStore.reset(context, section.title)
                }) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = s.reset)
                }
            }
        }
        HorizontalDivider()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(section.items) { index, dhikr ->
                DhikrCard(
                    dhikr = dhikr,
                    done = counts.getOrElse(index) { 0 },
                    onTap = {
                        val before = counts.getOrElse(index) { 0 }
                        val next = (before + 1).coerceAtMost(dhikr.count)
                        if (next != before) {
                            // ⚠️ نوسّع القائمة بدل `it[index] =` المباشر: فهرس
                            // خارج المدى يعني انهيارًا، لا ذكرًا مفقودًا
                            val updated = counts.toMutableList()
                            while (updated.size <= index) updated.add(0)
                            updated[index] = next
                            counts = updated
                            AdhkarProgressStore.save(context, section.title, updated)
                            if (next == dhikr.count) buzz(context)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun DhikrCard(dhikr: Dhikr, done: Int, onTap: () -> Unit) {
    val finished = done >= dhikr.count
    Card(
        onClick = onTap,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (finished) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dhikr.text,
                    fontSize = 18.sp,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Start,
                )
                if (dhikr.reference.isNotEmpty()) {
                    Text(
                        text = dhikr.reference,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
            when {
                // ذكر يُقال مرة: علامة صح تكفي، ولا حلقة تقدّم
                dhikr.count == 1 && finished ->
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                dhikr.count > 1 -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(46.dp),
                ) {
                    CircularProgressIndicator(
                        progress = done.coerceAtLeast(0).toFloat() / dhikr.count.toFloat(),
                        modifier = Modifier.fillMaxSize(),
                    )
                    if (finished) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Text(
                            text = "$done",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

/// اهتزاز خفيف عند إتمام العدد (docs/02 · C4).
private fun buzz(context: Context) {
    val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java) ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        runCatching {
            vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
