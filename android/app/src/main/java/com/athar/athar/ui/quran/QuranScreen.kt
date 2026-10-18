package com.athar.athar.ui.quran

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.athar.athar.quran.Ayah
import com.athar.athar.quran.QuranRepository
import com.athar.athar.quran.QuranSources
import com.athar.athar.quran.Surah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun QuranScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var surahs by remember { mutableStateOf<List<Surah>?>(null) }
    var opened by remember { mutableStateOf<Surah?>(null) }
    var ayahs by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        runCatching {
            withContext(Dispatchers.IO) { QuranRepository(context).surahs() }
        }.onSuccess { surahs = it }.onFailure { error = it.message }
    }

    LaunchedEffect(opened) {
        val target = opened ?: return@LaunchedEffect
        runCatching {
            withContext(Dispatchers.IO) { QuranRepository(context).ayahsOf(target.id) }
        }.onSuccess { ayahs = it }.onFailure { error = it.message }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            error != null -> Text(
                text = "تعذّر تحميل القرآن: $error",
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                textAlign = TextAlign.Center,
            )
            opened != null -> ReaderView(
                surah = opened!!,
                ayahs = ayahs,
                onBack = { opened = null },
            )
            surahs == null -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            else -> SurahList(surahs!!) { opened = it }
        }
    }
}

@Composable
private fun SurahList(surahs: List<Surah>, onOpen: (Surah) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(surahs) { surah ->
            TextButton(
                onClick = { onOpen(surah) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                        Text(text = surah.nameAr, fontSize = 19.sp, fontWeight = FontWeight.Medium)
                        Text(
                            text = "${surah.ayahCount} آية · ${if (surah.isMeccan) "مكية" else "مدنية"}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text(
                        text = surah.id.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 12.dp),
                    )
                }
            }
            HorizontalDivider()
        }
        item {
            // ⚖️ النسبة شرط الرخصة
            Text(
                text = "النص العثماني: ${QuranSources.TEXT_SOURCE}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ReaderView(surah: Surah, ayahs: List<Ayah>, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "رجوع")
            }
            Text(text = surah.nameAr, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
            items(ayahs) { ayah ->
                Text(
                    text = "${ayah.text} ﴿${ayah.number}﴾",
                    // النص العثماني يحتاج مقاسًا أكبر وتباعدًا أوسع
                    fontSize = 24.sp,
                    lineHeight = 44.sp,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                )
            }
        }
    }
}
