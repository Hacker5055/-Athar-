package com.athar.athar.ui.quran

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.athar.athar.core.ArabicDigits
import com.athar.athar.i18n.LocalAppLocale
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.quran.Khatmah
import com.athar.athar.quran.KhatmahStore
import com.athar.athar.quran.QuranMeta
import com.athar.athar.quran.QuranRepository
import com.athar.athar.ui.theme.AmiriFamily
import com.athar.athar.ui.components.AtharTopBar
import java.time.LocalDate

/// الختمة المنظّمة (B13 وF5 من docs/02).
///
/// ⚠️ الشاشة **داخل تدفّق القرآن** لا في «المزيد»: الورد يُقرأ في
/// القارئ، ووضعها هناك يعني أن المستخدم ينتقل بين تبويبين ليقرأ
/// ما اقترحناه عليه.
@Composable
fun KhatmahScreen(
    onBack: () -> Unit,
    onRead: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    val locale = LocalAppLocale.current

    var plan by remember { mutableStateOf(KhatmahStore.load(context)) }
    var draft by remember { mutableIntStateOf(Khatmah.DEFAULT_DAYS) }

    fun num(value: Int): String =
        if (locale.isRtl) ArabicDigits.of(value) else value.toString()

    // ⚠️ **أسماءُ السور**: «٢:٢٥٥» رقمٌ لا يقول شيئًا، و«البقرة
    // ٢٥٥» يقول. تُقرأ من الفهرس مرّةً واحدة على خيطٍ آخر.
    var surahNames by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    LaunchedEffect(Unit) {
        val names = runCatching {
            withContext(Dispatchers.IO) {
                QuranRepository(context).surahs().associate { it.id to it.nameAr }
            }
        }.getOrDefault(emptyMap())
        surahNames = names
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        AtharTopBar(
            title = s.khatmahTitle,
            onBack = onBack,
        )

        val active = plan
        if (active == null) {
            // ⚠️ **بطاقةٌ واحدة**: الخطةُ ليست نصوصًا متناثرة على
            // الشاشة، بل عرضٌ واحدٌ يُقرأ في نظرة.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = s.khatmahPlan,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Khatmah.PLANS.forEach { days ->
                            FilterChip(
                                selected = draft == days,
                                onClick = { draft = days },
                                label = { Text(s.tArgs("khatmah_days", "n" to num(days))) },
                            )
                        }
                    }
                    Text(
                        // ⚠️ كم آية كل يوم **مذكور قبل البدء**: من
                        // يلتزم بخطة لا يعرف ثقلها يتركها في اليوم
                        // الثالث.
                        text = s.tArgs("khatmah_per_day", "n" to num(Khatmah.perDay(draft))),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(
                        onClick = {
                            val fresh = KhatmahStore.Plan(
                                days = draft,
                                startEpochDay = LocalDate.now().toEpochDay(),
                                doneUpTo = 0,
                            )
                            KhatmahStore.save(context, fresh)
                            plan = fresh
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                    ) {
                        Text(s.khatmahStart)
                    }
                }
            }
            return@Column
        }

        // ── خطة نشِطة ────────────────────────────────────
        val total = Khatmah.actualDays(active.days)
        val todayNum = Khatmah.daySince(
            active.startEpochDay,
            LocalDate.now().toEpochDay(),
        ).coerceAtMost(total)

        Column(modifier = Modifier.padding(16.dp)) {
            LinearProgressIndicator(
                progress = { active.doneUpTo.toFloat() / QuranMeta.TOTAL_VERSES.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = s.tArgs(
                    "khatmah_progress",
                    "n" to num(active.doneUpTo),
                    "m" to num(QuranMeta.TOTAL_VERSES),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )

            if (active.doneUpTo >= QuranMeta.TOTAL_VERSES) {
                Text(
                    text = s.khatmahFinished,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp),
                )
                Text(
                    text = s.khatmahFinishedBody,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                TextButton(
                    onClick = {
                        KhatmahStore.clear(context)
                        plan = null
                    },
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    Text(s.khatmahRestart)
                }
                return@Column
            }

            // ⚠️ **الجزءُ اليومي في بطاقة**: هو قلبُ الخطة، فيُعطى
            // وزنَه. واسمُ السورة بالنسخ لا رقمًا: «البقرة ٢٥٥».
            val (from, to) = Khatmah.portionOf(todayNum, active.days)
            val (fromSurah, fromAyah) = from
            val (toSurah, toAyah) = to
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = s.khatmahToday,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.khatmahFrom,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Text(
                                text = "${surahNames[fromSurah] ?: num(fromSurah)} · ${num(fromAyah)}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AmiriFamily,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = s.khatmahTo,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Text(
                                text = "${surahNames[toSurah] ?: num(toSurah)} · ${num(toAyah)}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AmiriFamily,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                    Text(
                        text = s.tArgs(
                            "khatmah_per_day",
                            "n" to num(Khatmah.perDay(active.days)),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = { onRead(fromSurah, fromAyah) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(s.khatmahRead)
                }
                TextButton(
                    onClick = {
                        // ⚠️ الإنجاز إلى **نهاية اليوم** لا بزيادة
                        // واحدة: من قرأ الورد كاملًا ضغطة واحدة تكفي،
                        // ولا نجعله يضغط ٢٠٨ مرّات.
                        val next = KhatmahStore.Plan(
                            days = active.days,
                            startEpochDay = active.startEpochDay,
                            doneUpTo = Khatmah.endOf(todayNum, active.days),
                        )
                        KhatmahStore.save(context, next)
                        plan = next
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(s.khatmahDone)
                }
            }

            Row(modifier = Modifier.padding(top = 20.dp)) {
                TextButton(onClick = {
                    KhatmahStore.clear(context)
                    plan = null
                }) {
                    Text(s.khatmahStop)
                }
            }
        }
    }
}
