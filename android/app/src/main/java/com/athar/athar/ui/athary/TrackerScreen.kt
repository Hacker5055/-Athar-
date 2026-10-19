package com.athar.athar.ui.athary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.athar.athary.DayLevel
import com.athar.athar.athary.HabitId
import com.athar.athar.athary.TrackerState
import com.athar.athar.athary.TrackerStore
import com.athar.athar.i18n.AppLocale
import com.athar.athar.i18n.LocalAppLocale
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.athary.WeeklySummaries
import com.athar.athar.share.ShareCards
import com.athar.athar.share.ShareImage
import com.athar.athar.i18n.localDigits
import com.athar.athar.ui.components.SectionTitle
import com.athar.athar.ui.components.SubHeading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Locale

@Composable
fun TrackerScreen(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    val context = LocalContext.current
    var state by remember { mutableStateOf(TrackerStore.load(context)) }
    val today = LocalDate.now()

    fun toggle(habit: HabitId) {
        val next = state.toggle(today, habit).pruned(today)
        state = next
        TrackerStore.save(context, next)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            SectionTitle(
                title = s.trackerTitle,
                subtitle = s.trackerSub,
            )
        }

        item {
            // ⚠️ الملخّص **قبل الإحصاءات اليومية**: الإحصاءات تقول
            // أين أنت الآن، والملخّص يقول إلى أين تمشي. ومن يرى
            // الرقم قبل المسار يحسب اليوم الواحد نتيجة.
            WeeklyCard(state = state, today = today)
        }

        item {
            // ⚠️ **المشاركةُ بعد الملخّص**: فمن يريد أن يشارك
            // أسبوعه يريد أن يرى أوّلًا ما سيُشارك.
            ShareWeekCard(state = state, today = today)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                val digits = localDigits()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Statistic(value = digits(state.currentStreak(today)), label = s.streakDays)
                    Statistic(value = digits(state.bestStreak()), label = s.atharyBest)
                    Statistic(
                        value = "${digits(state.countFor(today))}/${digits(HabitId.entries.size)}",
                        label = s.today,
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(s.last30, fontWeight = FontWeight.Bold)
                    // ⚠️ العدّاد النصّي **دائمًا** إلى جانب الشبكة: اللون
                    // وحده لا يكفي لمن لا يميّز الألوان (docs/04 §أ)
                    Text(
                        "${state.totalInWindow(today)} ${s.trackerDays} · ${s.last30}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    ContributionGrid(state = state, today = today)
                }
            }
        }

        item { SubHeading(s.habits) }

        items(HabitId.entries) { habit ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { toggle(habit) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(s.t(habit.stringKey), modifier = Modifier.weight(1f), fontSize = 16.sp)
                    Checkbox(
                        checked = state.has(today, habit),
                        onCheckedChange = { toggle(habit) },
                    )
                }
            }
        }

        item {
            Text(
                s.atharyFutureNote,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        }
    }
}

/// بطاقةُ مشاركةِ الأسبوع (F6).
///
/// ⚠️ **الرسمُ في الخلفية**: صورةٌ ١٠٨٠×١٢٠٠ تُرسم على الخيط
/// الرئيسيّ تتجمّد الشاشة ثانيةً كاملة. و`startActivity` **بعد**
/// الرسم لا داخله — النيةُ تُرسل من خيط الواجهة.
@Composable
private fun ShareWeekCard(state: TrackerState, today: LocalDate) {
    val digits = localDigits()
    val s = LocalStrings.current
    val context = LocalContext.current
    val locale = LocalAppLocale.current
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = {
                    if (busy) return@Button
                    busy = true
                    message = null
                    scope.launch {
                        val data = ShareCards.dataOf(
                            state = state,
                            today = today,
                            locale = Locale.forLanguageTag(locale.code),
                            title = s.appName,
                            subtitle = s.shareWeek,
                            footer = s.shareFooter,
                            activeLabel = s.shareActiveDays,
                            actsLabel = s.shareActs,
                            streakLabel = s.shareStreak,
                        )
                        val uri = withContext(Dispatchers.Default) {
                            runCatching {
                                ShareImage.save(context, ShareImage.render(data, digits))
                            }.getOrNull()
                        }
                        busy = false
                        if (uri == null) {
                            message = s.shareFailed
                        } else {
                            val started = runCatching {
                                context.startActivity(
                                    ShareImage.shareIntent(uri, s.shareText),
                                )
                            }.isSuccess
                            if (!started) message = s.shareFailed
                        }
                    }
                },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(s.atharyShare)
            }
            message?.let { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

/// ⚠️ **الأرقامُ بحسب اللغة**: العربيةُ شرقية، وما دونها لاتينية.
/// وصورةٌ تُشارَك بالإنجليزية وأرقامُها عربيةٌ شرقية تبدو خطأً.
@Composable
private fun Statistic(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ContributionGrid(state: TrackerState, today: LocalDate) {
    val days = remember(state, today) { state.window(today) }
    val shape = RoundedCornerShape(3.dp)
    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outlineVariant

    // ⚠️ ٣٠/٥ = ٦ صفوف × ٥ أعمدة، تُقرأ كالأسبوع لا كشريط طويل
    Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        repeat(5) { column ->
            Column(modifier = Modifier.weight(1f)) {
                repeat(6) { row ->
                    val record = days.getOrNull(column * 6 + row) ?: return@repeat
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .aspectRatio(1f)
                            .clip(shape)
                            .background(levelColor(record.level, primary))
                            .border(1.dp, outline, shape),
                    )
                }
            }
        }
    }
}

private fun levelColor(level: DayLevel, primary: androidx.compose.ui.graphics.Color) =
    when (level) {
        DayLevel.NONE -> primary.copy(alpha = 0.06f)
        DayLevel.LOW -> primary.copy(alpha = 0.28f)
        DayLevel.MID -> primary.copy(alpha = 0.52f)
        DayLevel.HIGH -> primary.copy(alpha = 0.76f)
        DayLevel.FULL -> primary
    }

/// ملخّص الأسبوع — وصف لا حكم.
///
/// ⚠️ ولا نقول «أحسنت» ولا «قصّرت»: عبادة تُقاس برقم يراها صاحبها
/// غاية لا وسيلة، والتطبيق لا يعرف عنك ما يعرفه ربّك.
@Composable
private fun WeeklyCard(state: TrackerState, today: java.time.LocalDate) {
    val s = LocalStrings.current
    val current = remember(state, today) { WeeklySummaries.of(state, today) }
    val previous = remember(state, today) { WeeklySummaries.previousOf(state, today) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(s.weeklyTitle, fontWeight = FontWeight.Bold)
            Text(
                s.tArgs("weekly_acts", "n" to current.totalActs, "d" to current.activeDays),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                s.tArgs("weekly_completion", "n" to current.completion),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                // ⚠️ المقارنة **بالأسبوع الذي قبله مباشرة** لا
                // بمتوسط ولا بمثالية: من يقارن نفسه بالمثالية
                // يرى نفسه مقصّرًا دائمًا، ومن يقارنها بنفسه يرى
                // جهده.
                when {
                    WeeklySummaries.improved(current, previous) -> s.weeklyBetter
                    WeeklySummaries.improved(previous, current) -> s.weeklyWorse
                    else -> s.weeklySame
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            val top = current.perHabit.firstOrNull { it.second > 0 }
            if (top != null) {
                Text(
                    "${s.weeklyTop}: ${s.t(top.first.stringKey)} (${top.second})",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                s.tArgs("weekly_streak", "n" to current.streak),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                s.tArgs("weekly_best_streak", "n" to current.bestStreak),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
