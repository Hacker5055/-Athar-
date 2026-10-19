package com.athar.athar.ui.ruqya

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.athar.athar.adhkar.AdhkarRepository
import com.athar.athar.adhkar.AdhkarSection
import com.athar.athar.adhkar.AdhkarSources
import com.athar.athar.adhkar.Dhikr
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.quran.Ayah
import com.athar.athar.quran.QuranAudioController
import com.athar.athar.quran.QuranAudioState
import com.athar.athar.quran.QuranRepository
import com.athar.athar.quran.Reciter
import com.athar.athar.quran.Reciters
import com.athar.athar.ruqya.RuqyaAyah
import com.athar.athar.ruqya.RuqyaSelections
import com.athar.athar.ruqya.RuqyaSections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.athar.athar.ui.theme.AmiriFamily
import com.athar.athar.ui.components.SectionTitle
import com.athar.athar.ui.components.SubHeading

/// الرقية الشرعية (C9 من docs/02).
///
/// ⚠️ **ما في هذا التطبيق دعاءٌ جديد**: هو جمعٌ لمقتطفاتٍ مشهورة
/// من حصن المسلم ومن القرآن، **بنصّها كما هو** ومن مصادر
/// التطبيق. فلا نصّ مكتوبٌ من الذاكرة، ولا ادّعاءٌ بالشفاء.
///
/// ⚠️ والجمعُ **من المصادر**: نصُّ القرآن من المصحف العثماني
/// في الأصول، وأذكارُ حصن المسلم من `hisn.json`. والإسناد
/// واجب (docs/09).
@Composable
fun RuqyaScreen(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    val context = LocalContext.current

    var surahNames by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var ayahs by remember { mutableStateOf<Map<String, List<Ayah>>?>(null) }
    var adhkar by remember { mutableStateOf<List<AdhkarSection>?>(null) }
    val audioState by QuranAudioController.state.collectAsState()

    LaunchedEffect(Unit) {
        // ⚠️ القراءة **في الخلفية**: `uthmani.json` ميجابايتان،
        // وقراءتُهما على الخيط الرئيسيّ تُجمّد الشاشة.
        val loaded = withContext(Dispatchers.IO) {
            val repo = QuranRepository(context)
            val names = repo.surahs().associate { it.id to it.nameAr }
            val texts = RuqyaSelections.ALL.associate { selection ->
                selection.id to repo.ayahsOf(selection.surah)
            }
            Triple(names, texts, AdhkarRepository.load(context))
        }
        surahNames = loaded.first
        ayahs = loaded.second
        adhkar = loaded.third
    }

    val texts = ayahs
    val sections = adhkar

    Column(modifier = modifier.fillMaxSize()) {
        SectionTitle(title = s.ruqyaTitle, modifier = Modifier.padding(horizontal = 16.dp))
        Text(
            text = s.ruqyaNote,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        if (texts == null || sections == null) {
            // ⚠️ **الوزنُ لا الامتلاءُ**: المؤشِّرُ في وسطِ ما
            // بقي من الشاشة، لا في وسطِ الشاشةِ كلِّها.
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        // ⚠️ **الوزنُ لا الامتلاءُ**: القائمةُ تأخذُ ما بقي بعد
        // العنوانِ والملاحظة، لا الشاشةَ كلَّها.
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                SubHeading(s.ruqyaQuranTitle, Modifier.padding(horizontal = 16.dp))
            }
            items(RuqyaSelections.ALL) { selection ->
                val verses = RuqyaSelections.verses(selection, texts[selection.id].orEmpty())
                AyahCard(
                    selection = selection,
                    verses = verses,
                    surahName = surahNames[selection.surah].orEmpty(),
                    state = audioState,
                )
            }

            item {
                SubHeading(s.ruqyaAdhkarTitle, Modifier.padding(horizontal = 16.dp))
            }
            items(RuqyaSections.sections(sections)) { section ->
                AdhkarCard(section = section)
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

/// مقتطفٌ قرآنيّ مع تلاوته.
@Composable
private fun AyahCard(
    selection: RuqyaAyah,
    verses: List<Ayah>,
    surahName: String,
    state: QuranAudioState,
) {
    val s = LocalStrings.current
    val reciter: Reciter = Reciters.DEFAULT
    val playing = state is QuranAudioState.Playing &&
        state.surahOrNull == selection.surah &&
        state.ayahOrNull == selection.fromAyah
    val active = state.isActive && state.surahOrNull == selection.surah

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = s.t(selection.titleKey),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "$surahName · ${selection.fromAyah}" +
                            (if (selection.toAyah != selection.fromAyah) {
                                "-${selection.toAyah}"
                            } else {
                                ""
                            }),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                // ⚠️ **الإيقافُ بإيقاعٍ واحد**: من يعمل الآن يوقفه،
                // ومن لا يعمل يبدأ. زرٌّان منفصلان يُربكان.
                IconButton(
                    onClick = {
                        QuranAudioController.toggleAyahs(
                            reciter = reciter,
                            surah = selection.surah,
                            fromAyah = selection.fromAyah,
                            toAyah = selection.toAyah,
                            title = "${s.t(selection.titleKey)} · ${reciter.nameAr}",
                        )
                    },
                ) {
                    Icon(
                        imageVector = if (active) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                        contentDescription = s.ruqyaPlay,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (verses.isEmpty()) {
                // ⚠️ **لا فراغ**: إن غاب المقتطف من المصدر فلا نكتب
                // شيئًا مكانه — ولا نصنع آيةً من الذاكرة.
                Text(
                    text = s.ruqyaMissing,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = verses.joinToString(" ") { verse ->
                        "${verse.text} ﴿${verse.number}﴾"
                    },
                    fontSize = 19.sp,
                    lineHeight = 34.sp,
                    fontFamily = AmiriFamily,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}

/// بابٌ من أبواب حصن المسلم.
@Composable
private fun AdhkarCard(section: AdhkarSection) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = section.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            section.items.forEach { dhikr -> DhikrText(dhikr) }
        }
    }
}

@Composable
private fun DhikrText(dhikr: Dhikr) {
    Column {
        Text(
            text = dhikr.text,
            fontSize = 18.sp,
            lineHeight = 30.sp,
            fontFamily = AmiriFamily,
            textAlign = TextAlign.Start,
        )
        if (dhikr.reference.isNotEmpty()) {
            // ⚠️ **التخريجُ واجب**: ذكرٌ بلا تخريج لا يجوز أن يبدو
            // كالباقي.
            Text(
                text = dhikr.reference,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
