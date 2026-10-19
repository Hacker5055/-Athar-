package com.athar.athar.ui.quran

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.athar.athar.quran.Hifz
import com.athar.athar.quran.HifzStore
import com.athar.athar.quran.QuranMeta
import com.athar.athar.quran.QuranRepository
import com.athar.athar.ui.theme.AmiriFamily
import com.athar.athar.ui.components.AtharTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/// وضع الحفظ (B14 من docs/02).
///
/// ⚠️ الشاشة **لا تعدّل النصّ**: القناع واجهة فقط، والنص العثماني
/// يُقرأ كما هو من الأصول. الرخصة تمنع التعديل (`docs/09`)،
/// وتطبيع حرف واحد تعديل.
@Composable
fun HifzScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val context = LocalContext.current

    var plan by remember { mutableStateOf(HifzStore.loadPlan(context)) }
    var repeats by remember { mutableStateOf(HifzStore.loadRepeats(context)) }

    val ids = remember(plan) { plan?.let { Hifz.ids(it.fromSurah, it.fromAyah, it.toSurah, it.toAyah) } ?: emptyList() }

    if (plan == null) {
        PlanPicker(
            onChosen = { next ->
                HifzStore.savePlan(context, next)
                plan = next
            },
            onBack = onBack,
            modifier = modifier,
        )
        return
    }

    // ⚠️ النص يُقرأ **من الأصول** بلا إنترنت: أوفلاين أولًا
    // (`docs/05` §1). وحمّلنا السور التي يمتدّ عليها النطاق وحدها.
    val texts by produceState(initialValue = emptyMap<Int, String>(), plan) {
        value = withContext(Dispatchers.IO) {
            val repo = QuranRepository(context)
            val out = LinkedHashMap<Int, String>()
            val firstSurah = QuranMeta.surahAyahOf(ids.first()).first
            val lastSurah = QuranMeta.surahAyahOf(ids.last()).first
            for (surah in firstSurah..lastSurah) {
                for (ayah in repo.ayahsOf(surah)) {
                    out[QuranMeta.verseId(ayah.surah, ayah.number)] = ayah.text
                }
            }
            out
        }
    }

    var current by remember(ids) { mutableIntStateOf(Hifz.nextToMemorize(repeats.map { it.key }.toSet(), ids) ?: ids.firstOrNull() ?: 0) }
    var revealed by remember(current) { mutableStateOf(false) }

    // ⚠️ التقدّم **يعيد قراءة التكرارات** بعد كل زيادة: العرض
    // المبني على نسخة قديمة يجعل الحافظ يظنّ آية غير محفوظة
    // محفوظة.
    fun refresh() {
        repeats = HifzStore.loadRepeats(context)
        revealed = false
    }

    Column(modifier = modifier.fillMaxSize()) {
        AtharTopBar(
            title = s.hifzTitle,
            onBack = onBack,
        ) {
            TextButtonSmall(s.hifzChange) {
                HifzStore.clearPlan(context)
                plan = null
            }
        }

        val memorized = remember(repeats) { repeats.filter { Hifz.isMemorized(it.value) }.keys }
        val percent = Hifz.percent(memorized, ids)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(s.tArgs("hifz_progress", "a" to memorized.size, "b" to ids.size))
            LinearProgressIndicator(
                progress = { percent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )
        }
        HorizontalDivider()

        if (Hifz.isComplete(memorized, ids)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    // ⚠️ **تهنئة لا شرط نجاح أخلاقي**: نقول ما حدث
                    // ولا نزعم أنّ الحفظ ثبت في الصدر — ذلك لا
                    // يعرفه إلا الله.
                    s.hifzFinished,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // بطاقة الآية الحالية
        val count = repeats[current] ?: 0
        val level = Hifz.levelOf(count)
        val text = texts[current].orEmpty()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (Hifz.isMemorized(count)) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val (surah, ayah) = remember(current) { QuranMeta.surahAyahOf(current) }
                Text(
                    "﴿$ayah﴾",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = if (revealed || text.isEmpty()) text else Hifz.mask(text, level),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 22.sp,
                    lineHeight = 38.sp,
                    // ⚠️ **نسخٌ للحفظ**: من يحفظ يقرأ النصّ بنَفَسِ
                    // المصحف، فخطُّ الشاشة لا يمثّله.
                    fontFamily = AmiriFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                )
                Text(
                    s.tArgs("hifz_repeats_of", "a" to count.coerceAtMost(Hifz.REPEAT_TARGET), "b" to Hifz.REPEAT_TARGET),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = { revealed = !revealed },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(if (revealed) s.hifzHide else s.hifzReveal)
                    }
                    Button(
                        onClick = {
                            HifzStore.repeat(context, current)
                            refresh()
                            // ⚠️ بعد إتمام آية **نتقدّم تلقائيًّا**: من
                            // أتمّها يريد التالية لا أن يبحث عنها.
                            val next = Hifz.nextToMemorize(
                                HifzStore.loadRepeats(context).filter { Hifz.isMemorized(it.value) }.keys,
                                ids,
                            )
                            if (next != null) current = next
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(s.hifzRepeat)
                    }
                }
            }
        }

        HorizontalDivider()

        // ⚠️ **الوزنُ لا الامتلاءُ**: القائمةُ تأخذُ ما بقيَ من الشاشةِ
        // بعدَ العناوين، لا الشاشةَ كلَّها.
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(ids) { id ->
                VerseRow(
                    verseId = id,
                    text = texts[id].orEmpty(),
                    repeats = repeats[id] ?: 0,
                    isCurrent = id == current,
                    onSelect = { current = id; revealed = false },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            }
        }
    }
}

@Composable
private fun VerseRow(
    verseId: Int,
    text: String,
    repeats: Int,
    isCurrent: Boolean,
    onSelect: () -> Unit,
) {
    val s = LocalStrings.current
    val memorized = Hifz.isMemorized(repeats)
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "﴿${QuranMeta.surahAyahOf(verseId).second}﴾",
            style = MaterialTheme.typography.labelSmall,
            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(
                // ⚠️ في القائمة **نُظهر الآية كاملة**: الإخفاء
                // تمرين، والقائمة فهرس. من أخفى الفهرس جعل
                // المراجعة أعمى.
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
            )
            if (repeats > 0) {
                Text(
                    s.tArgs("hifz_repeats_of", "a" to repeats.coerceAtMost(Hifz.REPEAT_TARGET), "b" to Hifz.REPEAT_TARGET),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (memorized) {
            Text(
                s.hifzMemorized,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/// منتقي النطاق — أرقام لا قوائم منسدلة.
///
/// ⚠️ حقل رقم لا قائمة بـ ١١٤ سورة: القائمة المنسدلة تصلح لاختيار
/// وجبة، لا للوصول إلى «النساء ١٧٦» في لمسة. ومن يحفظ يعرف رقم
/// ما يريد.
@Composable
private fun PlanPicker(
    onChosen: (HifzStore.Plan) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    var fromSurah by remember { mutableStateOf("1") }
    var fromAyah by remember { mutableStateOf("1") }
    var toSurah by remember { mutableStateOf("1") }
    var toAyah by remember { mutableStateOf("7") }

    fun int(value: String) = value.toIntOrNull() ?: 0
    val ids = Hifz.ids(int(fromSurah), int(fromAyah), int(toSurah), int(toAyah))
    val valid = ids.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        AtharTopBar(
            title = s.hifzTitle,
            onBack = onBack,
        )

        Text(
            s.hifzBody,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            s.hifzFrom,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = fromSurah,
                onValueChange = { fromSurah = it.filter(Char::isDigit).take(3) },
                label = { Text(s.hifzSurah) },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                value = fromAyah,
                onValueChange = { fromAyah = it.filter(Char::isDigit).take(3) },
                label = { Text(s.hifzAyah) },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        Text(
            s.hifzTo,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = toSurah,
                onValueChange = { toSurah = it.filter(Char::isDigit).take(3) },
                label = { Text(s.hifzSurah) },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                value = toAyah,
                onValueChange = { toAyah = it.filter(Char::isDigit).take(3) },
                label = { Text(s.hifzAyah) },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        Text(
            s.tArgs("hifz_count", "n" to ids.size),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp),
        )

        Button(
            onClick = {
                onChosen(HifzStore.Plan(int(fromSurah), int(fromAyah), int(toSurah), int(toAyah)))
            },
            enabled = valid,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(s.hifzStart)
        }
    }
}

/// زرّ نصّي صغير — لا يحتاج سطرًا كاملًا كـ [Button].
@Composable
private fun TextButtonSmall(text: String, onClick: () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick) {
        Text(text, style = MaterialTheme.typography.labelMedium)
    }
}
