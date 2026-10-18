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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.quran.QuranSources
import com.athar.athar.quran.QuranSearch
import com.athar.athar.quran.Surah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
com.athar.athar.quran.QuranTranslationRepository
com.athar.athar.quran.QuranTranslation
androidx.compose.material3.FilterChip

@Composable
fun QuranScreen(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var surahs by remember { mutableStateOf<List<Surah>?>(null) }
    var opened by remember { mutableStateOf<Surah?>(null) }
    var ayahs by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<QuranSearch.Hit>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    var translation by remember { mutableStateOf(QuranTranslation.NONE) }
    var translated by remember { mutableStateOf(emptyMap<String, String>()) }

    // ⚠️ نسخة واحدة: التخزين المؤقت داخلها، ولو أُنشئت مع كل إعادة تركيب
    // أُعيد تحليل ميجابايت عند كل حرف
    val translationRepo = remember { QuranTranslationRepository(context) }

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

    // ⚠️ البحث على `Dispatchers.IO`: ٦٢٣٦ آية، والنصّ ١٫٧ ميجابايت
    // ⚠️ على IO لا على الواجهة: ميجابايت من الجسون يجمّد الإطار
    LaunchedEffect(translation) {
        translated = withContext(Dispatchers.IO) { translationRepo.textFor(translation) }
    }

    LaunchedEffect(query, surahs) {
        val list = surahs ?: return@LaunchedEffect
        val needle = query.trim()
        if (needle.isEmpty()) {
            results = emptyList()
            searching = false
            return@LaunchedEffect
        }
        searching = true
        // ⚠️ مرة واحدة خارجه: لو أُنشئ داخل ayahsOf أعيد تحليل ١٫٧ ميجابايت
        // مع كل سورة (١١٤ مرة) في كل حرف يكتبه المستخدم.
        val repo = QuranRepository(context)
        val found = runCatching {
            withContext(Dispatchers.IO) {
                QuranSearch.search(needle, list, { id -> repo.ayahsOf(id) })
            }
        }.getOrDefault(emptyList())
        results = found
        searching = false
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            error != null -> Text(
                text = "${s.quranLoadFailed}: $error",
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                textAlign = TextAlign.Center,
            )
            opened != null -> ReaderView(
                surah = opened!!,
                ayahs = ayahs,
                onBack = { opened = null },
                translation = translation,
                translated = translated,
                onTranslationChange = { translation = it },
            )
            surahs == null -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            else -> Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text(s.quranSearchHint) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    singleLine = true,
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isBlank()) {
                        SurahList(surahs!!) { opened = it }
                    } else {
                        SearchResults(
                            hits = results,
                            searching = searching,
                        ) { hit -> opened = surahs!!.firstOrNull { it.id == hit.ayah.surah } }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    hits: List<QuranSearch.Hit>,
    searching: Boolean,
    onOpen: (QuranSearch.Hit) -> Unit,
) {
    val s = LocalStrings.current
    if (searching) {
        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
        return
    }
    if (hits.isEmpty()) {
        Text(
            text = s.quranSearchEmpty,
            modifier = Modifier.padding(24.dp),
            textAlign = TextAlign.Center,
        )
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = s.tArgs("quran_results_count", "n" to hits.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        items(hits) { hit ->
            TextButton(onClick = { onOpen(hit) }, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(hit.surahNameAr, style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${hit.ayah.surah}:${hit.ayah.number}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text(
                        // ⚠️ النص **كما هو** بلا أي تعديل — شرط الرخصة
                        text = hit.ayah.text,
                        fontSize = 18.sp,
                        lineHeight = 30.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
            HorizontalDivider()
        }
    }
}

@Composable
private fun SurahList(surahs: List<Surah>, onOpen: (Surah) -> Unit) {
    val s = LocalStrings.current
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
                            text = "${surah.ayahCount} ${s.verses} · ${if (surah.isMeccan) s.meccan else s.medinan}",
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
                text = s.tArgs("quran_uthmani_source", "source" to QuranSources.TEXT_SOURCE),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ReaderView(
    surah: Surah,
    ayahs: List<Ayah>,
    onBack: () -> Unit,
    translation: QuranTranslation,
    translated: Map<String, String>,
    onTranslationChange: (QuranTranslation) -> Unit,
) {
    val s = LocalStrings.current
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = s.back)
            }
            Text(text = surah.nameAr, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // ⚠️ أفقي قابل للتمرير: ثلاث شرائح بثلاث لغات تتجاوز عرض الشاشة
        // على الأجهزة الصغيرة، والقصّ الصامت أسوأ من التمرير.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            QuranTranslation.entries.forEach { option ->
                FilterChip(
                    selected = option == translation,
                    onClick = { onTranslationChange(option) },
                    label = { Text(s.t(option.nameKey)) },
                )
            }
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
                // ⚠️ الترجمة **تحت** آيتها لا في صفحة أخرى: القارئ يريد
                // المعنى أمام النصّ، لا رحلة بين شاشتين.
                translated[QuranTranslationRepository.key(ayah.surah, ayah.number)]
                    ?.let { meaning ->
                        Text(
                            text = meaning,
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                        )
                    }
                HorizontalDivider()
            }
            if (translation != QuranTranslation.NONE) {
                item {
                    // ⚠️ الإسناد واجب (docs/09) وشرط الرخصة — لا «من مصادر عامة»
                    Text(
                        text = s.t(translation.creditKey),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                    )
                }
            }
        }
    }
}
