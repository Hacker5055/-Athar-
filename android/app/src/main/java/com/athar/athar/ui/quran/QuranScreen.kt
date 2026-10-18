package com.athar.athar.ui.quran

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.athar.athar.quran.QuranTranslationRepository
import com.athar.athar.quran.QuranTranslation
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.lazy.rememberLazyListState
import com.athar.athar.quran.QuranMeta
import com.athar.athar.quran.QuranAudioController
import com.athar.athar.quran.QuranPlaybackService
import com.athar.athar.quran.QuranAudioState
import com.athar.athar.quran.Reciter
import com.athar.athar.quran.ReciterWithdrawalStore
import com.athar.athar.quran.ReciterWithdrawals
import com.athar.athar.quran.QuranAudioOffline
import com.athar.athar.quran.QuranBookmarkStore
import com.athar.athar.quran.QuranBookmarks
import com.athar.athar.quran.QuranDownloader
import com.athar.athar.quran.Reciters
import com.athar.athar.ui.theme.ReaderPalette
import com.athar.athar.ui.theme.ReaderTheme

@Composable
fun QuranScreen(
    readerTheme: ReaderTheme = ReaderTheme.NORMAL,
    modifier: Modifier = Modifier,
) {
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
    var openAyah by remember { mutableStateOf<Int?>(null) }
    var translated by remember { mutableStateOf(emptyMap<String, String>()) }
    var reciter by remember { mutableStateOf(Reciters.DEFAULT) }

    // ⚖️ سحب التلاوة (القاعدة ٣ من docs/10): إن اعترض قارئ على نشر
    // تلاوته يجب أن تتوقف فورًا لا في التحديث القادم. فالقائمة تُقرأ
    // من ملف بعيد عند كل فتح، لا من الكتالوج المثبَّت في الكود.
    // ⚖️ التنزيل اختياري لا تلقائي: لا نملأ جهاز المستخدم بمئات
    // الميجابايت دون أن يطلب.
    var downloaded by remember { mutableStateOf(QuranDownloader.loaded(context)) }
    var downloadTick by remember { mutableIntStateOf(0) }

    // ⚠️ استقراء حال التنزيل كل ثانيتين **ما دام هناك تنزيل جارٍ**
    // لا دائمًا: حلقة توقّف نفسها عند الانتهاء فلا تستهلك بطارية.
    LaunchedEffect(downloadTick) {
        downloaded = QuranDownloader.settle(context)
        while (QuranDownloader.pending(context).isNotEmpty()) {
            delay(2000)
            downloaded = QuranDownloader.settle(context)
        }
    }

    // ⚠️ المحفوظ على الجهاز أولًا: من نزّل سورة ليسمعها بلا اتصال
    // يجب أن يسمعها من ملفّه لا أن يفاجأ بصمت.
    LaunchedEffect(downloaded) {
        QuranAudioController.bindOffline(context.filesDir, downloaded)
    }

    // ⚖️ المفضلة اختيارية وبسيطة: علامة على آية، تُحفظ فورًا.
    var bookmarks by remember { mutableStateOf(QuranBookmarkStore.load(context)) }
    var showBookmarks by remember { mutableStateOf(false) }
    var bookmarkTexts by remember { mutableStateOf(emptyMap<String, String>()) }

    // ⚠️ الحفظ **فوري** لا عند الخروج: المستخدم قد يغلق التطبيق بعد
    // الضغطة، وضياع علامة ظنّها محفوظة أسوأ من لا مفضلة أصلًا.
    fun setBookmarks(next: Set<String>) {
        bookmarks = next
        QuranBookmarkStore.save(context, next)
    }

    // ⚠️ نصّ الآيات المحفوظة يُقرأ **مرة لكل سورة** لا مرة لكل علامة:
    // ١١٤ قراءة على الأكثر بدل مئات.
    LaunchedEffect(showBookmarks, bookmarks) {
        if (!showBookmarks) return@LaunchedEffect
        val grouped = QuranBookmarks.bySurah(bookmarks)
        bookmarkTexts = withContext(Dispatchers.IO) {
            val repo = QuranRepository(context)
            val out = LinkedHashMap<String, String>()
            for ((surah, ayahs) in grouped) {
                val list = runCatching { repo.ayahsOf(surah) }.getOrDefault(emptyList())
                for (a in ayahs) {
                    out[QuranBookmarks.key(surah, a)] = list.firstOrNull { it.number == a }?.text.orEmpty()
                }
            }
            out
        }
    }

    var withdrawn by remember { mutableStateOf(emptySet<String>()) }
    LaunchedEffect(Unit) { withdrawn = ReciterWithdrawalStore.refresh(context) }
    LaunchedEffect(withdrawn) {
        reciter = ReciterWithdrawals.fallback(
            ReciterWithdrawals.apply(Reciters.ALL, withdrawn), reciter, withdrawn,
        )
    }

    // ⚠️ المشغّل كائن وحيد لا يُنشأ هنا: لو أُنشئ داخل هذه الشاشة
    // لأُفلت معها عند تبديل التبويب، فتنقطع التلاوة كلما انتقل
    // المستخدم إلى المواقيت — وهو ما يُفتح التطبيق لأجله.
    val audioState by QuranAudioController.state.collectAsState()

    // ⚠️ إذن الإشعار **يُطلب** عند أول تشغيل لا يُفترض: الإذن كان
    // يُفحص في الإعدادات ولا يُطلب قطّ، فعل أندرويد ١٣+ يبقى التشغيل
    // في الخلفية بلا إشعار — أي بلا زرّ إيقاف يراه المستخدم.
    val requestNotification = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

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
                openAyah = openAyah,
                onOpened = { openAyah = null },
                reciter = reciter,
                onReciterChange = { reciter = it },
                availableReciters = ReciterWithdrawals.apply(Reciters.ALL, withdrawn),
                readerTheme = readerTheme,
                downloaded = downloaded,
                downloading = QuranDownloader.pending(context).any {
                    it.key == QuranAudioOffline.key(reciter.id, opened!!.id)
                },
                onDownloadToggle = {
                    opened?.let { surah ->
                        if (QuranAudioOffline.key(reciter.id, surah.id) in downloaded) {
                            QuranDownloader.remove(context, reciter, surah.id)
                            downloaded = QuranDownloader.loaded(context)
                        } else {
                            QuranDownloader.enqueue(context, reciter, surah.id, surah.nameAr)
                        }
                        downloadTick++
                    }
                },
                bookmarks = bookmarks,
                onBookmarkToggle = { ayah ->
                    setBookmarks(QuranBookmarks.toggle(bookmarks, opened!!.id, ayah))
                },
                audioState = audioState,
                onPlayToggle = {
                    opened?.let { surah ->
                        val title = "${surah.nameAr} · ${reciter.nameAr}"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                context, Manifest.permission.POST_NOTIFICATIONS,
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        QuranAudioController.toggle(reciter, surah.id, surah.ayahCount, title)
                        // ⚠️ بعد الضبط لا قبله: الخدمة تقرأ الحالة
                        // الجاهزة فلا يومض الإشعار فارغًا.
                        QuranPlaybackService.attach(context, title)
                    }
                },
                onStop = { QuranAudioController.stop() },
            )
            showBookmarks -> BookmarksView(
                bookmarks = bookmarks,
                texts = bookmarkTexts,
                surahs = surahs ?: emptyList(),
                onOpen = { surah, ayah ->
                    showBookmarks = false
                    opened = surahs?.firstOrNull { it.id == surah }
                    openAyah = ayah
                },
                onBack = { showBookmarks = false },
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
                        // ⚠️ Box يرصّ أبناءه فوق بعضهم، فلا يكفي وضع
                        // الصفّ والقائمة فيه جنبًا إلى جنب — يحتاجان
                        // Column يوزّع الارتفاع بينهما.
                        Column(modifier = Modifier.fillMaxSize()) {
                            // ⚠️ المفضلة قبل الأجزاء: من حفظ آية يريدها
                            // في ضغطة، لا أن يمرّ بثلاثين جزءًا ليصلها.
                            if (bookmarks.isNotEmpty()) {
                                TextButton(onClick = { showBookmarks = true }) {
                                    Text("${s.quranBookmarks} (${bookmarks.size})")
                                }
                            }
                            // ⚠️ الأجزاء قبل القائمة: من يقرأ «جزء عمّ»
                            // لا يبحث عن سورة النبأ — يبحث عن الجزء نفسه.
                            JuzRow { juz ->
                                val (surah, ayah) = QuranMeta.startOfJuz(juz)
                                opened = surahs!!.firstOrNull { it.id == surah }
                                openAyah = ayah
                            }
                            SurahList(
                                surahs = surahs!!,
                                onOpen = { opened = it },
                                modifier = Modifier.weight(1f),
                            )
                        }
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

/// اسم القارئ متبوعًا برقم الآية إن كان يبثّ آية آية.
///
/// ⚠️ الرقم يظهر للمستخدم: من يتابع على مصحف يريد أن يعرف أين وصل،
/// والتلاوة التي تبثّ آية آية بلا موضع تُفقده الصفحة.
private fun label(reciter: Reciter, prefix: String, ayah: Int?): String =
    if (ayah != null) "$prefix · آية $ayah" else prefix

/// ثلاثون جزءًا في صفّ يمرّر أفقيًا.
@Composable
private fun JuzRow(onPick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        (1..QuranMeta.JUZ_COUNT).forEach { juz ->
            FilterChip(
                selected = false,
                onClick = { onPick(juz) },
                label = { Text(juz.toString()) },
            )
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
private fun SurahList(
    surahs: List<Surah>,
    onOpen: (Surah) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    LazyColumn(modifier = modifier.fillMaxSize()) {
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
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
private fun ReaderView(
    surah: Surah,
    ayahs: List<Ayah>,
    onBack: () -> Unit,
    translation: QuranTranslation,
    translated: Map<String, String>,
    onTranslationChange: (QuranTranslation) -> Unit,
    openAyah: Int?,
    onOpened: () -> Unit,
    reciter: Reciter,
    onReciterChange: (Reciter) -> Unit,
    availableReciters: List<Reciter>,
    readerTheme: ReaderTheme,
    downloaded: Set<String>,
    downloading: Boolean,
    onDownloadToggle: () -> Unit,
    bookmarks: Set<String>,
    onBookmarkToggle: (Int) -> Unit,
    audioState: QuranAudioState,
    onPlayToggle: () -> Unit,
    onStop: () -> Unit,
) {
    val s = LocalStrings.current
    val listState = rememberLazyListState()
    // ⚠️ ثيم القراءة يخصّ هذه الشاشة وحدها: النصّ يُقرأ طويلًا وفي
    // ظروف إضاءة غير بقية التطبيق، فلو ألزمناه بالثيم العام لأجبرنا
    // من يقرأ ليلًا على شاشة بيضاء أو العكس.
    val palette = ReaderPalette.of(readerTheme)

    // ⚠️ الانتقال إلى جزء يفتح السورة **ويمرّر** إلى آيته: فتح أولها
    // وحده يجعل «الانتقال إلى الجزء ٣٠» يبدأ من أول سورة النبأ فقط،
    // وهو صحيح مصادفةً لا دائمًا.
    LaunchedEffect(openAyah) {
        val target = openAyah
        if (target != null && target in 1..ayahs.size) {
            listState.scrollToItem(target - 1)
            onOpened()
        }
    }

    val firstVisible = listState.firstVisibleItemIndex
    val currentAyah = ayahs.getOrNull(firstVisible)?.number ?: 1
    val juz = QuranMeta.juzOf(surah.id, currentAyah)
    val page = QuranMeta.pageOf(surah.id, currentAyah)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(if (palette.isCustom) Modifier.background(palette.background) else Modifier),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = s.back)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = surah.nameAr, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                // ⚠️ الموضع مذكور دائمًا: بلا «الجزء / الصفحة» لا يعرف
                // القارئ أين هو في المصحف الورقي الذي يتابع عليه.
                Text(
                    text = "${s.quranJuz} $juz · ${s.quranPage} $page",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (palette.isCustom) {
                        palette.onBackground.copy(alpha = 0.72f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
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

        // ⚠️ اختيار القارئ قبل التشغيل لا داخله: من يفتح التلاوة يريد
        // صوتًا محدّدًا، لا أن يكتشف بعد التشغيل أنه يسمع غيره.
        // ⚖️ إن سُحب كلّ القرّاء فلا تلاوة أصلًا، ولا يجوز أن يبقى
        // زرّ تشغيل بلا شيء يعمل خلفه.
        if (availableReciters.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                availableReciters.forEach { option ->
                    FilterChip(
                        selected = option.id == reciter.id,
                        onClick = { onReciterChange(option) },
                        label = { Text(option.nameAr) },
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val playing = audioState is QuranAudioState.Playing
                val loading = audioState is QuranAudioState.Loading
                val active = audioState.isActive
                val isSaved = QuranAudioOffline.key(reciter.id, surah.id) in downloaded

                IconButton(onClick = onPlayToggle) {
                    Icon(
                        // ⚠️ الأيقونة تعكس الحالة الفعلية: «تشغيل» على سورة
                        // تعمل الآن يجعل المستخدم يضغط فيتوقّف عكس ما أراد.
                        imageVector = if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (playing) s.quranPause else s.quranPlay,
                    )
                }
                if (active) {
                    IconButton(onClick = onStop) {
                        Icon(Icons.Filled.Stop, contentDescription = s.quranStop)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        // ⚠️ أثناء التشغيل نُظهر **اسم القارئ** لا «استماع
                        // السورة»: المستخدم يعرف أنه يستمع، المهم من يستمع.
                        // ومع قارئ يبثّ آية آية نُظهر رقمها، فيعرف أين هو
                        // في المصحف الذي يتابع عليه.
                        // ⚠️ كل فرع على حدة: دمج فرعين في `when` يمنع
                        // التحويل الذكي فلا تُقرأ `ayah` أصلاً.
                        // ⚠️ حين تكون السورة محفوظة يُذكر ذلك صريحًا:
                        // المستخدم نزّلها ليعرف أنها تعمل بلا اتصال،
                        // وإخفاء ذلك يُفقد التنزيل معناه.
                        text = when {
                            audioState is QuranAudioState.Loading -> s.quranAudioLoading
                            audioState is QuranAudioState.Error -> s.quranAudioError
                            audioState is QuranAudioState.Paused ->
                                label(reciter, s.quranResume, audioState.ayah)
                            audioState is QuranAudioState.Playing ->
                                label(reciter, reciter.nameAr, audioState.ayah)
                            isSaved -> "${reciter.nameAr} · ${s.quranOfflineReady}"
                            else -> reciter.nameAr
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (audioState is QuranAudioState.Error) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
                // ⚖️ التنزيل اختياري، ومتاح لمن يبثّ السورة كاملة
                // بملف واحد. والقيد **مذكور** لا مخفيّ: زرّ تنزيل
                // لا يعمل أسوأ من غيابه.
                if (QuranAudioOffline.canDownload(reciter)) {
                    if (downloading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                    IconButton(onClick = onDownloadToggle) {
                        Icon(
                            imageVector = when {
                                downloading -> Icons.Filled.Download
                                isSaved -> Icons.Filled.Delete
                                else -> Icons.Filled.DownloadDone
                            },
                            contentDescription = when {
                                downloading -> s.quranDownloading
                                isSaved -> s.quranDeleteDownload
                                else -> s.quranDownload
                            },
                            tint = if (isSaved) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                    }
                }
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
            }
        }
        HorizontalDivider(
            // ⚠️ نمرّر اللون صريحًا: الفاصل بلون الثيم العام على خلفية
            // سيبيا يظهر خطًّا رماديًّا غريبًا، وعلى الليلي يكاد يضيع.
            color = if (palette.isCustom) {
                palette.divider
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            items(ayahs) { ayah ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${ayah.text} ﴿${ayah.number}﴾",
                        // النص العثماني يحتاج مقاسًا أكبر وتباعدًا أوسع
                        fontSize = 24.sp,
                        lineHeight = 44.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                    )
                    // ⚠️ العلامة **على السطر نفسه** لا في قائمة منبثقة:
                    // ضغطة واحدة بدل ضغطتين، والآية أمام العين حين
                    // يقرّر حفظها لا بعد أن ينسى أي آية كانت.
                    val marked = QuranBookmarks.contains(bookmarks, ayah.surah, ayah.number)
                    IconButton(onClick = { onBookmarkToggle(ayah.number) }) {
                        Icon(
                            imageVector = if (marked) {
                                Icons.Filled.Bookmark
                            } else {
                                Icons.Filled.BookmarkBorder
                            },
                            contentDescription = if (marked) {
                                s.quranBookmarkRemove
                            } else {
                                s.quranBookmarkAdd
                            },
                            tint = if (marked) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }
                // ⚠️ الترجمة **تحت** آيتها لا في صفحة أخرى: القارئ يريد
                // المعنى أمام النصّ، لا رحلة بين شاشتين.
                translated[QuranTranslationRepository.key(ayah.surah, ayah.number)]
                    ?.let { meaning ->
                        Text(
                            text = meaning,
                            color = if (palette.isCustom) {
                                palette.onBackground.copy(alpha = 0.72f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                        )
                    }
                HorizontalDivider(
                    color = if (palette.isCustom) {
                        palette.divider
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                )
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
            item {
                // ⚖️ حق التلاوة لقارئها: النسبة واجبة كما في النصّ
                Text(
                    text = Reciters.CREDIT,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                )
            }
        }
    }
}

/// شاشة المفضلة — الآيات التي علّمها المستخدم.
///
/// ⚠️ القائمة **مرتّبة** بترتيب المصحف لا بترتيب الحفظ: من حفظ آية
/// من البقرة ثم آية من الفاتحة يتوقّعهما في ترتيب القرآن لا في
/// ترتيب ضغطاته.
@Composable
private fun BookmarksView(
    bookmarks: Set<String>,
    texts: Map<String, String>,
    surahs: List<Surah>,
    onOpen: (Int, Int) -> Unit,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val ordered = bookmarks.mapNotNull { key ->
        val surah = key.substringBefore(':').toIntOrNull() ?: return@mapNotNull null
        val ayah = key.substringAfter(':').toIntOrNull() ?: return@mapNotNull null
        Triple(surah, ayah, key)
    }.sortedWith(compareBy({ it.first }, { it.second }))

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = s.back)
            }
            Text(text = s.quranBookmarks, style = MaterialTheme.typography.titleMedium)
        }
        if (ordered.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = s.quranBookmarksEmpty, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = s.quranBookmarksEmptyBody,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(ordered) { (surah, ayah, key) ->
                    val name = surahs.firstOrNull { it.id == surah }?.nameAr ?: "$surah"
                    TextButton(
                        onClick = { onOpen(surah, ayah) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "$name — $ayah",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            // ⚠️ النص **كما جاء في المصحف** بلا اختصار
                            // ولا إعادة تشكيل: الرخصة تمنع تعديل النصّ.
                            Text(
                                text = texts[key].orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}
