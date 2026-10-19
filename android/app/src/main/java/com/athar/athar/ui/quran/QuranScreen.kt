package com.athar.athar.ui.quran

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.draw.clip
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
import com.athar.athar.quran.SurahSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.athar.athar.quran.QuranTranslationRepository
import com.athar.athar.quran.QuranTranslation
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Card
import androidx.compose.foundation.lazy.rememberLazyListState
import com.athar.athar.quran.QuranMeta
import com.athar.athar.quran.QuranAudioController
import com.athar.athar.quran.QuranPlaybackService
import com.athar.athar.quran.QuranAudioState
import com.athar.athar.quran.Reciter
import com.athar.athar.quran.ReciterWithdrawalStore
import com.athar.athar.quran.ReciterWithdrawals
import com.athar.athar.quran.QuranAudioOffline
import com.athar.athar.quran.LastRead
import com.athar.athar.quran.QuranBookmarkStore
import com.athar.athar.quran.QuranProgressStore
import com.athar.athar.quran.QuranShare
import com.athar.athar.quran.QuranBookmarks
import com.athar.athar.quran.QuranDownloader
import com.athar.athar.quran.Reciters
import com.athar.athar.ui.theme.ReaderPalette
import com.athar.athar.ui.theme.ReaderTheme
import android.content.Intent
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Search
import com.athar.athar.ui.theme.AmiriFamily
import com.athar.athar.i18n.localDigits
import com.athar.athar.ui.components.AtharTopBar
import com.athar.athar.ui.components.EmptyState
import com.athar.athar.ui.components.AtharSearchField
import com.athar.athar.ui.components.AtharPageTitle
import com.athar.athar.ui.theme.AtharPageMargin
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.key

@Composable
fun QuranScreen(
    readerTheme: ReaderTheme = ReaderTheme.NORMAL,
    /// (سورة، آية) يُفتح من تبويب آخر — «تابع القراءة» من الرئيسية.
    target: Pair<Int, Int>? = null,
    onTargetConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var surahs by remember { mutableStateOf<List<Surah>?>(null) }
    var opened by remember { mutableStateOf<Surah?>(null) }
    var ayahs by remember { mutableStateOf<List<Ayah>>(emptyList()) }
    // ⚠️ **البسملةُ من المصدر لا من الذاكرة**: تُؤخذ من آيةِ الفاتحة
    // الأولى في نصّ تنزيل — فهي النصُّ المرخَّص، ولا نكتب نصًّا
    // دينيًّا من تلقائنا أبدًا.
    var basmala by remember { mutableStateOf<String?>(null) }
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

    // ⚠️ الهدف يُستهلك **مرة واحدة** ثم يُصفَّر: لو بقي لأعاد فتح
    // السورة مع كل إعادة تركيب، فيقفز المستخدم إلى موضعه كلما
    // غيّر لغة أو أدار الشاشة.
    LaunchedEffect(target, surahs) {
        val (surah, ayah) = target ?: return@LaunchedEffect
        val list = surahs ?: return@LaunchedEffect
        opened = list.firstOrNull { it.id == surah }
        openAyah = ayah
        onTargetConsumed()
    }

    var showKhatmah by remember { mutableStateOf(false) }
    var showHifz by remember { mutableStateOf(false) }
    var showMushaf by remember { mutableStateOf(false) }
    /// تصفية السور بمكان النزول: ٠ = الكلّ · ١ = مكية · ٢ = مدنية
    var originFilter by remember { mutableIntStateOf(0) }
    /// السور التي يطابقها **اسمها** (لا نصّها) — بحث سريع.
    var surahHits by remember { mutableStateOf<List<Surah>>(emptyList()) }

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
            withContext(Dispatchers.IO) {
                val repo = QuranRepository(context)
                // ⚠️ **نسخةٌ واحدة**: التحليلُ مرّةً واحدة، والبسملةُ
                // من نفس النسخة فلا يُعاد قراءةُ الملف.
                repo.surahs() to repo.ayahsOf(1).firstOrNull()?.text
            }
        }.onSuccess { (list, basm) ->
            surahs = list
            basmala = basm
        }.onFailure { error = it.message }
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
            surahHits = emptyList()
            searching = false
            return@LaunchedEffect
        }
        searching = true
        // ⚠️ **السور بالاسم قبل النصّ**: من كتب «الكهف» يريد
        // السورة لا موضع آية فيها، والبحث في النصّ وحده يُخفي
        // سورةً باسمها.
        surahHits = SurahSearch.byName(needle, list)
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
                // ⚠️ **السورةُ التاليةُ في ترتيبِ المصحف**: `id` + ١
                // هي التاليةُ دائمًا، فالسورُ مرقّمةٌ متّصلة.
                nextSurah = surahs?.firstOrNull { it.id == opened!!.id + 1 },
                onOpenNext = { next ->
                    opened = next
                    openAyah = null
                },
                basmala = basmala,
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
                onShare = { ayah ->
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            QuranShare.ayah(opened!!.nameAr, ayah.number, ayah.text),
                        )
                    }
                    // ⚠️ المشاركة بلا اسم التطبيق تجعل النصّ يبدو
                    // مرسلًا من النظام لا من أثر، والنسبة تضيع.
                    runCatching {
                        context.startActivity(Intent.createChooser(intent, s.share))
                    }
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
            showHifz -> HifzScreen(
                onBack = { showHifz = false },
                modifier = Modifier.fillMaxSize(),
            )
            // ⚠️ وضع المصحف **شاشة مستقلّة** لا تبويبًا داخل
            // القائمة: القارئ فيه يقرأ صفحةً صفحة، ومزاحمته
            // مع قائمة السور تُربكه.
            showMushaf -> MushafScreen(
                onBack = { showMushaf = false },
                modifier = Modifier.fillMaxSize(),
            )
            showKhatmah -> KhatmahScreen(
                onBack = { showKhatmah = false },
                onRead = { surahId, ayah ->
                    showKhatmah = false
                    opened = surahs?.firstOrNull { it.id == surahId }
                    openAyah = ayah
                },
                modifier = Modifier.fillMaxSize(),
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
                // ⚠️ **عنوانُ الفصلّ**: كلُّ تبويبٍ له عنوانُه،
                // والأذكارُ والسبحةُ والقبلةُ والمزيدُ تبدأُ بمحتواها
                // مباشرةً — فكان نصفُ التطبيقِ بلا بابٍ.
                AtharPageTitle(
                    title = s.tabQuran,
                    modifier = Modifier.padding(horizontal = AtharPageMargin),
                )
                // ⚠️ **حقلُ بحثٍ واحد**: أيقونةُ البحثِ في «القرآن»
                // و«الأذكار» و«الأسماء» و«المدينة» — حقلٌ واحدٌ
                // بأيقونةٍ واحدة، لا أربعةُ حقولٍ بأربعِ صور.
                AtharSearchField(
                    value = query,
                    onValueChange = { query = it },
                    label = s.quranSearchHint,
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isBlank()) {
                        // ⚠️ Box يرصّ أبناءه فوق بعضهم، فلا يكفي وضع
                        // الصفّ والقائمة فيه جنبًا إلى جنب — يحتاجان
                        // Column يوزّع الارتفاع بينهما.
                        Column(modifier = Modifier.fillMaxSize()) {
                            // ⚠️ **«تابع القراءة» أوّلُ ما في تبويبِ
                            // القرآن**: موضعُ القراءةِ كان معروفًا في
                            // الرئيسيةِ وحدَها، فمن فتح تبويبَ القرآن
                            // ليقرأ لم يجدْ موضعَه إلا برجوعٍ إلى
                            // التبويبِ الأوّل.
                            val last = remember { QuranProgressStore.load(context) }
                            ContinueReadingCard(
                                last = last,
                                onOpen = { surah, ayah ->
                                    opened = surahs?.firstOrNull { it.id == surah }
                                    openAyah = ayah
                                },
                                modifier = Modifier.padding(
                                    horizontal = AtharPageMargin,
                                    vertical = 4.dp,
                                ),
                            )
                            // ⚠️ **الأفعالُ الأربعةُ أيقوناتٌ لا روابط**:
                            // رابطٌ نصّيٌّ بين آلاف النصوصِ التي
                            // ستُقرأ ليس فعلًا، بل سطرٌ ضائع.
                            // ⚠️ المفضلة قبل الأجزاء: من حفظ آية يريدها
                            // في ضغطة، لا أن يمرّ بثلاثين جزءًا ليصلها.
                            // ⚠️ الختمة قبل المفضلة: ورد اليوم
                            // له وقت، والذي فات لا يُستدرك.
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = AtharPageMargin,
                                        vertical = 6.dp,
                                    ),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                QuranAction(
                                    icon = Icons.Filled.MenuBook,
                                    label = s.khatmahTitle,
                                    onClick = { showKhatmah = true },
                                )
                                // ⚠️ الحفظ بعد الختمة: الورد يُقرأ،
                                // والحفظ يُستظهَر — وكلاهما التزام
                                // يوميّ، فقُرِنا في موضع واحد.
                                QuranAction(
                                    icon = Icons.Filled.School,
                                    label = s.hifzTitle,
                                    onClick = { showHifz = true },
                                )
                                // ⚠️ وضع المصحف **بعد الحفظ**: من يقرأ
                                // المصحف ورقيًّا يتبع صفحاته، فمن يجد
                                // الصفحات متأخّرة بحثًا عنه تاه.
                                QuranAction(
                                    icon = Icons.Filled.Book,
                                    label = s.mushafOpen,
                                    onClick = { showMushaf = true },
                                )
                                if (bookmarks.isNotEmpty()) {
                                    QuranAction(
                                        icon = Icons.Filled.Bookmark,
                                        label = s.quranBookmarks,
                                        badge = bookmarks.size,
                                        onClick = { showBookmarks = true },
                                    )
                                }
                            }
                            // ⚠️ الأجزاء قبل القائمة: من يقرأ «جزء عمّ»
                            // لا يبحث عن سورة النبأ — يبحث عن الجزء نفسه.
                            JuzRow { juz ->
                                val (surah, ayah) = QuranMeta.startOfJuz(juz)
                                opened = surahs!!.firstOrNull { it.id == surah }
                                openAyah = ayah
                            }
                            // ⚠️ الأحزاب **بعد** الأجزاء لا قبلها:
                            // الجزء أعمّ والحزب أدقّ، ومن يحفظ
                            // ترتيب الأجزاء يصلها من الصفّ الأوّل،
                            // ومن يقرأ بالحزب يجد ما يريد تحته.
                            Text(
                                s.quranHizb,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    horizontal = AtharPageMargin,
                                    vertical = 2.dp,
                                ),
                            )
                            HizbRow { hizb ->
                                val (surah, ayah) = QuranMeta.startOfHizb(hizb)
                                opened = surahs!!.firstOrNull { it.id == surah }
                                openAyah = ayah
                            }
                            // ⚠️ التصفية **قبل** القائمة لا داخلها:
                            // قائمة من ١١٤ سورة بلا تصفية تفرض
                            // تمريرًا طويلًا للوصول إلى سورة واحدة.
                            OriginFilter(
                                selected = originFilter,
                                onSelect = { originFilter = it },
                            )
                            SurahList(
                                surahs = SurahSearch.byOrigin(
                                    surahs!!,
                                    meccanOnly = if (originFilter == 1) true else null,
                                    medinanOnly = if (originFilter == 2) true else null,
                                ),
                                onOpen = { opened = it },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        SearchResults(
                            hits = results,
                            searching = searching,
                            surahHits = surahHits,
                            onOpenSurah = { surah -> opened = surah },
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

/// ستّون حزبًا في صفّ يمرّر أفقيًا.
///
/// ⚠️ الشريحة تُظهر **رقم الحزب ورقم الجزء**: «الحزب ٣٧» وحده لا
/// يعني شيئًا لمن لا يحفظ ترتيب الستّين، أمّا «١٩-١» فيُقرأ فورًا:
/// الجزء التاسع عشر، نصفه الأول.
@Composable
private fun HizbRow(onPick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AtharPageMargin, vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        (1..QuranMeta.HIZB_COUNT).forEach { hizb ->
            FilterChip(
                selected = false,
                onClick = { onPick(hizb) },
                label = { Text("${QuranMeta.juzOfHizb(hizb)}-${if (QuranMeta.isSecondHalf(hizb)) 2 else 1}") },
            )
        }
    }
}

/// ثلاثون جزءًا في صفّ يمرّر أفقيًا.
@Composable
private fun JuzRow(onPick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AtharPageMargin, vertical = 4.dp)
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
    /// السور المطابقة **بالاسم** — تُعرض قبل النتائج النصّية.
    surahHits: List<Surah> = emptyList(),
    onOpenSurah: (Surah) -> Unit = {},
    onOpen: (QuranSearch.Hit) -> Unit,
) {
    val s = LocalStrings.current
    if (searching) {
        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
        return
    }
    // ⚠️ «لا نتائج» **لا تُقال** إن وُجدت سورة بالاسم: البحث
    // في نصّ القرآن لا يجد «الكهف» في اسمها، فيظنّ القارئ أنّ
    // التطبيق لا يعرف هذه السورة.
    if (hits.isEmpty() && surahHits.isEmpty()) {
        EmptyState(
            icon = Icons.Filled.Search,
            title = s.quranSearchEmpty,
            modifier = Modifier.fillMaxSize(),
        )
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (surahHits.isNotEmpty()) {
            item {
                Text(
                    text = s.surahSections,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            // ⚠️ **نتيجةُ البحثِ بطاقةٌ كالفهرس**: السورةُ التي
            // يبحث عنها الناسُ اسمًا لها، فلها شكلُ الفهرس نفسه —
            // وإلا بدا قسمان لشيءٍ واحد.
            items(surahHits) { surah ->
                val digits = localDigits()
                Card(
                    onClick = { onOpenSurah(surah) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AtharPageMargin, vertical = 3.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = surah.nameAr,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AmiriFamily,
                            )
                            Text(
                                text = "${surah.ayahCount} ${s.verses}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = digits(surah.id),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }
        if (hits.isNotEmpty()) {
            item {
                Text(
                    text = s.tArgs("quran_results_count", "n" to hits.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            // ⚠️ **نتيجةُ النصّ بطاقةٌ كذلك**: الآيةُ تُقرأ بنصّها
            // لا سطرًا، فسطرٌ عريضٌ بلا حدٍّ يجعلها سطرًا.
            items(hits) { hit ->
                Card(
                    onClick = { onOpen(hit) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AtharPageMargin, vertical = 3.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = hit.surahNameAr,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                "${hit.ayah.surah}:${hit.ayah.number}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            // ⚠️ النص **كما هو** بلا أي تعديل — شرط الرخصة
                            text = hit.ayah.text,
                            fontSize = 19.sp,
                            lineHeight = 32.sp,
                            fontFamily = AmiriFamily,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

/// شريط تصفية السور بمكان النزول.
@Composable
private fun OriginFilter(
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    val s = LocalStrings.current
    Row(
        // ⚠️ **الحاشيةُ واحدةٌ كسائرِ التبويب**: كان هذا الصفُّ
        // وحدَه على ثمانية — فكان تصفيةُ السورِ تبدأُ من حافةٍ
        // غيرِ حافةِ البطاقاتِ تحتها.
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AtharPageMargin),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val labels = listOf(s.surahFilterAll, s.surahFilterMeccan, s.surahFilterMedinan)
        labels.forEachIndexed { index, label ->
            TextButton(onClick = { onSelect(index) }) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (index == selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (index == selected) FontWeight.Bold else null,
                )
            }
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
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
    ) {
        items(surahs) { surah ->
            // ⚠️ **الفهرسُ كروتٌ**: سطرٌ عريضٌ بلا حدٍّ يبدو جدولًا،
            // والبطاقةُ تجعل «السورةُ اسمٌ يُقرأ» لا رقمًا في قائمة.
            // وهي نفسُ شكلِ نتيجةِ البحثِ فليس قسمان لشيءٍ واحد.
            Card(
                onClick = { onOpen(surah) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AtharPageMargin),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val digits = localDigits()
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                        Text(
                            text = surah.nameAr,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            // ⚠️ **نسخٌ لأسماء السور**: فهرسُ المصحف
                            // أسماءُ سورٍ تُكتبُ وتُنطق، فخطُّ الشاشات
                            // لا يليق بها.
                            fontFamily = AmiriFamily,
                        )
                        Text(
                            text = "${digits(surah.ayahCount)} ${s.verses} · " +
                                "${if (surah.isMeccan) s.meccan else s.medinan}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        // ⚠️ **أول** آية لا آيةٍ منتصفها: الجزء والصفحة
                        // يُذكران لبداية السورة، فمن أراد صفحةً
                        // داخلها وجدها في المصحف.
                        Text(
                            text = "${s.quranJuz} ${digits(QuranMeta.juzOf(surah.id, 1))} · " +
                                "${s.quranPage} ${digits(QuranMeta.pageOf(surah.id, 1))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    // ⚠️ **رقمُ السورةِ في دائر**: هو العلامةُ التي
                    // يعرفها القارئُ في المصحف، وقديمًا كان رقمُ السورة
                    // ونقطُها في الهامش.
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = digits(surah.id),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
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
    /// السورةُ التالية — تُعرضُ في آخرِ السورةِ ليمضيَ القارئُ
    /// دونَ رجوعٍ إلى الفهرس.
    nextSurah: Surah?,
    onOpenNext: (Surah) -> Unit,
    /// نصُّ البسملة — يُعرض رأسَ السورة إلا في الفاتحة والتوبة.
    basmala: String?,
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
    onShare: (Ayah) -> Unit,
    audioState: QuranAudioState,
    onPlayToggle: () -> Unit,
    onStop: () -> Unit,
) {
    val context = LocalContext.current
    val s = LocalStrings.current
    // ⚠️ **السورةُ تبدأُ من أوّلها**: حالةُ التمريرِ كانت تُحفَظ
    // بين السور، فمن انتهى من سورةٍ وفتح ما بعدها وجد نفسه في
    // آخرِ السورةِ الأولى — أو في فراغٍ أبيض. فحالةُ التمريرِ
    // مربوطةٌ بالسورة: كلُّ سورةٍ لها موضعُها.
    val listState = key(surah.id) { rememberLazyListState() }
    // ⚠️ ثيم القراءة يخصّ هذه الشاشة وحدها: النصّ يُقرأ طويلًا وفي
    // ظروف إضاءة غير بقية التطبيق، فلو ألزمناه بالثيم العام لأجبرنا
    // من يقرأ ليلًا على شاشة بيضاء أو العكس.
    val palette = ReaderPalette.of(readerTheme)

    // ⚠️ الانتقال إلى جزء يفتح السورة **ويمرّر** إلى آيته: فتح أولها
    // وحده يجعل «الانتقال إلى الجزء ٣٠» يبدأ من أول سورة النبأ فقط،
    // وهو صحيح مصادفةً لا دائمًا.
    // ⚠️ **البسملةُ تزيحُ الأرقام**: رأسُ السورة بندٌ في القائمة،
    // فالآيةُ الأولى رقمُها ١ في النصّ و٢ في القائمة.
    val basmalaOffset = if (basmala != null && surah.id != 1 && surah.id != 9) 1 else 0
    LaunchedEffect(openAyah) {
        val target = openAyah
        if (target != null && target in 1..ayahs.size) {
            listState.scrollToItem(target - 1 + basmalaOffset)
            onOpened()
        }
    }

    val firstVisible = listState.firstVisibleItemIndex

    // ⚠️ الحفظ مع كل تمرير لا عند الخروج: من أُغلق تطبيقه فجأة يجب
    // أن يعود إلى حيث كان، لا إلى أول السورة. و`apply` لا `commit`
    // فلا تعرقل الكتابة الإطار.
    LaunchedEffect(firstVisible, surah.id) {
        if (firstVisible < 0) return@LaunchedEffect
        // ⚠️ **البسملةُ تزيحُ الأرقام**: البسملةُ بندٌ في
        // القائمة، فرقمُ البندِ أكبرُ من رقمِ الآيةِ بواحد. فبين
        // السطورِ آيةٌ بعدَ البسملة.
        QuranProgressStore.save(
            context,
            LastRead(surah.id, surah.nameAr, maxOf(1, firstVisible + 1 - basmalaOffset)),
        )
    }
    // ⚠️ **رقمُ الآيةِ مع البسملة**: `ayahs` لا يسعُ البسملة،
    // والقائمةُ تسعُها — فالفرقُ واحدٌ في كلِّ سورةٍ لها بسملة.
    val currentAyah = ayahs.getOrNull(firstVisible - basmalaOffset)?.number ?: 1
    val juz = QuranMeta.juzOf(surah.id, currentAyah)
    val page = QuranMeta.pageOf(surah.id, currentAyah)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(if (palette.isCustom) Modifier.background(palette.background) else Modifier),
    ) {
        // ⚠️ **لونُ الرأسِ يتبعُ ثيمَ القراءة**: في الليليّ
        // والسيبيّا عنوانُ السورةِ لونُ الورق لا لونُ التطبيق.
        AtharTopBar(
            title = surah.nameAr,
            // ⚠️ الموضع مذكور دائمًا: بلا «الجزء / الصفحة» لا يعرف
            // القارئ أين هو في المصحف الورقي الذي يتابع عليه.
            subtitle = "${s.quranJuz} $juz · ${s.quranPage} $page",
            onBack = onBack,
            // ⚠️ **نسخٌ لاسم السورة**: عنوانُ المصحف.
            titleFont = AmiriFamily,
            titleSize = 22.sp,
            titleColor = if (palette.isCustom) palette.onBackground else MaterialTheme.colorScheme.onSurface,
            subtitleColor = if (palette.isCustom) {
                palette.onBackground.copy(alpha = 0.72f)
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )

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
            // ⚠️ **البسملةُ رأسَ السورة** كما في المصحف: لا رقمَ
            // لها ولا آية، تُكتبُ فوق النصّ. ولا تُعرض في الفاتحة
            // (فهي آيتُها الأولى) ولا في التوبة (فهي محفوظةٌ عند
            // المشركين، ولم يُحكم عليها).
            if (basmala != null && surah.id != 1 && surah.id != 9) {
                // ⚠️ **بلا مفتاح**: بنودُ الآياتِ في هذه القائمة بلا
                // مفاتيح، ومفتاحٌ واحدٌ مع بنودٍ بلا مفتاحٍ يُلخبط
                // القائمةَ فتُرمى استثناءً.
                item {
                    Text(
                        text = basmala,
                        fontSize = 22.sp,
                        lineHeight = 38.sp,
                        fontFamily = AmiriFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                    )
                }
            }
            items(ayahs) { ayah ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${ayah.text} ﴿${ayah.number}﴾",
                        // النص العثماني يحتاج مقاسًا أكبر وتباعدًا أوسع
                        fontSize = 24.sp,
                        lineHeight = 44.sp,
                        // ⚠️ **أميري**: الرسمُ العثماني بخطٍّ لا يعرف
                        // علاماتِه تتفرّق حركاتُه عن حروفها.
                        fontFamily = AmiriFamily,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                    )
                    // ⚠️ المشاركة على السطر نفسه لا في قائمة منبثقة:
                    // الآية أمام العين حين يقرّر مشاركتها.
                    IconButton(onClick = { onShare(ayah) }) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = s.share,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
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
            if (nextSurah != null) {
                item {
                    // ⚠️ **«السورة التالية» في آخرِ السورة**: القارئ
                    // الذي انتهى من سورةٍ يريد السورةَ التي بعدها، لا
                    // الفهرسَ ثمَّ السورة. فلمّا بلغت آخرَ السورة
                    // جُعلَ الطريقُ أمامَه مبسوطًا: بطاقةٌ تفتحُ
                    // السورةَ التاليةَ بنقرةٍ واحدة.
                    Card(
                        onClick = { onOpenNext(nextSurah) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = s.nextSurah,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = nextSurah.nameAr,
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                    // ⚠️ **نسخٌ لأسماء السور**:
                                    // اسمٌ يُقرأ لا رقمٌ يُكتب.
                                    fontFamily = AmiriFamily,
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
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
    val digits = localDigits()
    val ordered = bookmarks.mapNotNull { key ->
        val surah = key.substringBefore(':').toIntOrNull() ?: return@mapNotNull null
        val ayah = key.substringAfter(':').toIntOrNull() ?: return@mapNotNull null
        Triple(surah, ayah, key)
    }.sortedWith(compareBy({ it.first }, { it.second }))

    Column(modifier = Modifier.fillMaxSize()) {
        AtharTopBar(
            title = s.quranBookmarks,
            onBack = onBack,
        )
        if (ordered.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.BookmarkBorder,
                title = s.quranBookmarksEmpty,
                body = s.quranBookmarksEmptyBody,
                modifier = Modifier.weight(1f),
            )
        } else {
            // ⚠️ **الوزنُ لا الامتلاءُ**: الامتلاءُ يأخذُ ارتفاعَ
            // الشاشةِ كلَّه فوقَ الشريطِ فيُقصَّ آخرُ العلامات.
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(ordered) { (surah, ayah, key) ->
                    val name = surahs.firstOrNull { it.id == surah }?.nameAr ?: "$surah"
                    // ⚠️ **المحفوظاتُ بطاقاتٌ كنتائج البحث**: سطرٌ
                    // عريضٌ بلا حدٍّ يبدو جدولًا، والبطاقةُ تجعل
                    // ما اختاره المستخدمُ مجموعةً لا قائمة.
                    Card(
                        onClick = { onOpen(surah, ayah) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AtharPageMargin, vertical = 4.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AmiriFamily,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f),
                            )
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = digits(ayah),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                        // ⚠️ النص **كما جاء في المصحف** بلا اختصار
                        // ولا إعادة تشكيل: الرخصة تمنع تعديل النصّ.
                        Text(
                            text = texts[key].orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 19.sp,
                            lineHeight = 32.sp,
                            fontFamily = AmiriFamily,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        )
                    }
                }
            }
        }
    }
}

/// فعلٌ سريعٌ في تبويبِ القرآن: أيقونةٌ في دائرٍ واسمٌ تحتها.
///
/// ⚠️ **الدائرةُ ملوّنةٌ دائمًا**: الدائرةُ الفارغةُ تعني «معطّل»،
/// والمفعّلُ يُعرف بلونه.
@Composable
private fun RowScope.QuranAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    badge: Int? = null,
) {
    val digits = localDigits()
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            if (badge != null && badge > 0) {
                Text(
                    text = digits(badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 2.dp),
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            // ⚠️ **سطر واحد وقطعً بثلاث نقاط**: الأسماء تقول عند المعرب،
            // فلو اقتطً سطرين لما الأسماء يقطع نصفه، فبقي معًًا متققً.
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}
