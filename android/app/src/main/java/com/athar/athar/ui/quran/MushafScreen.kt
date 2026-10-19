package com.athar.athar.ui.quran

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.quran.Ayah
import com.athar.athar.quran.MushafPage
import com.athar.athar.quran.QuranMeta
import com.athar.athar.quran.QuranRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/// وضع المصحف (B2 من docs/02) — قراءةً صفحةً صفحة.
///
/// ⚠️ **حدود الصفحة من المصحف المدني** (حفص) من `quran-meta`
/// (MIT)، والنصّ من تانزيل (CC BY 3.0) **كما هو**: لا يُعاد
/// تشكيله ولا يُنقّط، فالتنقيط يغيّر النصّ فيخرج عن الرخصة،
/// والأرقام تُضاف حوله لا داخله.
@Composable
fun MushafScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var page by remember { mutableIntStateOf(1) }
    var jumping by remember { mutableStateOf(false) }

    // ⚠️ الصفحة **تُحمَّل وحدها** لا القرآن كلّه: من يفتح
    // المصحف يريد صفحة، لا ستّة آلاف آية في الذاكرة.
    val content by produceState<PageContent?>(null, page, context) {
        value = withContext(Dispatchers.IO) { loadPage(context, page) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = s.tArgs("mushaf_page", "n" to page.toString()),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${s.t("mushaf_of")} · ${s.quranJuz} ${content?.juz ?: 0}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = { jumping = true }) {
                Icon(Icons.Filled.Search, contentDescription = s.mushafJump)
            }
        }
        HorizontalDivider()

        val loaded = content
        if (loaded == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(s.loading, style = MaterialTheme.typography.bodySmall)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                item {
                    // ⚠️ عنوان السورة **فقط إن بدأت في هذه الصفحة**:
                    // صفحة تبدأ في آخر البقرة لا تُسمّى «البقرة»
                    // وحدها، فيظنّ القارئ أن ما بعد آخر آية منها
                    // لسورة البقرة.
                    if (MushafPage.startsSurah(page) && loaded.surahName != null) {
                        Text(
                            text = loaded.surahName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                items(loaded.verses) { verse ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        // ⚠️ رقم الآية **خارج النصّ**: الترقيم جزء من
                        // نسخ المصحف لا من النصّ القرآني، وإدخاله
                        // يغيّر النصّ.
                        Text(
                            text = verse.number.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(
                            text = verse.text,
                            fontSize = 19.sp,
                            lineHeight = 32.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                item {
                    Text(
                        text = s.mushafNote,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }
            }
        }

        // ⚠️ أزرار الصفحة **نصٌّ لا سهم**: السهم في العربية يشير
        // اتجاهًا قد يوهم، والكلمة لا تحتمل.
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                onClick = { if (page > 1) page-- },
                enabled = page > 1,
            ) { Text(s.mushafPrev) }
            TextButton(
                onClick = { if (page < MushafPage.COUNT) page++ },
                enabled = page < MushafPage.COUNT,
            ) { Text(s.mushafNext) }
        }
    }

    if (jumping) {
        JumpDialog(
            current = page,
            onGo = { target ->
                page = target
                jumping = false
            },
            onDismiss = { jumping = false },
        )
    }
}

/// محتوى صفحة واحدة — يُحمَّل في الخلفية.
private data class PageContent(
    val verses: List<Ayah>,
    val surahName: String?,
    val juz: Int,
)

/// ⚠️ القراءة **بالسور لا بالصفحة**: صفحة واحدة قد تمسّ سورتين،
/// ولا نملك في الأصل إلا فهرس سور. فنطلب سور الصفحة ونقصّها.
private fun loadPage(context: Context, page: Int): PageContent? {
    if (!MushafPage.isValid(page)) return null
    val first = MushafPage.firstVerseId(page)
    val last = MushafPage.lastVerseId(page)
    val repository = QuranRepository(context)
    val verses = ArrayList<Ayah>()
    for (surahId in MushafPage.surahsOnPage(page)) {
        for (ayah in repository.ayahsOf(surahId)) {
            val id = QuranMeta.verseId(ayah.surah, ayah.number)
            if (id in first..last) verses.add(ayah)
        }
    }
    val firstSurah = MushafPage.firstSurah(page)
    val name = repository.surahs().firstOrNull { it.id == firstSurah }?.nameAr
    return PageContent(
        verses = verses,
        surahName = name,
        juz = QuranMeta.juzOfId(first),
    )
}

/// نافذة القفز إلى صفحة.
@Composable
private fun JumpDialog(
    current: Int,
    onGo: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val s = LocalStrings.current
    var text by remember { mutableStateOf(current.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(s.mushafJump) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it.filter { c -> c.isDigit() }.take(4) },
                singleLine = true,
                label = { Text(s.mushafJump) },
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val target = text.toIntOrNull()
                    if (target != null && MushafPage.isValid(target)) onGo(target)
                },
            ) { Text(s.ok) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(s.cancel) }
        },
    )
}
