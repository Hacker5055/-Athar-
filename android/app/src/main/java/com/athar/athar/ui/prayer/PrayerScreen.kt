package com.athar.athar.ui.prayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.prayer.PrayerMethod
import com.athar.athar.prayer.PrayerSettings
import com.athar.athar.prayer.PrayerSettingsStore
import com.athar.athar.prayer.PrayerTimesCalculator
import com.athar.athar.prayer.HighLatitude
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.mutableIntStateOf
import com.athar.athar.hijri.HijriStore
import com.athar.athar.i18n.AppLocale
import com.athar.athar.ui.hijri.hijriText
import java.time.format.FormatStyle
import com.athar.athar.quran.QuranProgress
import com.athar.athar.quran.QuranProgressStore
import com.athar.athar.hijri.IslamicEvents
import com.athar.athar.prayer.PrayerDay
import com.athar.athar.prayer.PrayerAdjustments
import com.athar.athar.prayer.PrayerSlot
import com.athar.athar.ramadan.Ramadan
import com.athar.athar.hijri.HijriCalendar
import java.time.temporal.ChronoUnit

private val timeFormat: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

@Composable
fun PrayerScreen(
    settings: PrayerSettings,
    /// فتح القرآن عند (سورة، آية) — يُمرَّر إلى الأب لأن اختيار
    /// التبويب ليس بيد هذه الشاشة.
    onContinueReading: (Int, Int) -> Unit = { _, _ -> },
    onSettingsChange: (PrayerSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    val today = LocalDate.now()
    var pickingCity by remember { mutableStateOf(false) }
    var showMonth by remember { mutableStateOf(false) }
    var showAdjust by remember { mutableStateOf(false) }

    if (showMonth) {
        MonthScreen(
            settings = settings,
            locale = com.athar.athar.i18n.LocalAppLocale.current,
            hijriOffset = HijriStore.load(context),
            onBack = { showMonth = false },
            modifier = modifier,
        )
        return
    }

    if (pickingCity) {
        CityPickerScreen(
            onCityChosen = { city ->
                onSettingsChange(
                    settings.copy(
                        latitude = city.latitude,
                        longitude = city.longitude,
                        cityId = city.id,
                        placeName = "",
                    ),
                )
                pickingCity = false
            },
            onCoordinatesChosen = { lat, lng ->
                onSettingsChange(
                    settings.copy(
                        latitude = lat,
                        longitude = lng,
                        cityId = null,
                        placeName = "",
                    ),
                )
                pickingCity = false
            },
            onDismiss = { pickingCity = false },
            modifier = modifier,
        )
        return
    }

    if (showAdjust) {
        AdjustmentDialog(
            settings = settings,
            onChange = onSettingsChange,
            onDismiss = { showAdjust = false },
        )
    }

    /// ⚠️ اسم المدينة يُقرأ باللغة الحالية — «القاهرة» داخل تطبيق
    /// فرنسي بالكامل عيب لا يراه من يختبر بالعربية فقط.
    val locale = com.athar.athar.i18n.LocalAppLocale.current
    val placeLabel = remember(settings, s, locale) {
        val city = settings.cityId?.let { id ->
            com.athar.athar.prayer.Cities.all.firstOrNull { it.id == id }
        }
        city?.nameFor(locale)
            ?: settings.placeName.ifBlank { s.prayerMyLocation }
    }

    var hijriOffset by remember { mutableIntStateOf(HijriStore.load(context)) }

    val day = remember(settings, today) { PrayerTimesCalculator.calculate(settings, today) }
    val next = remember(day) { day.nextAfter(Instant.now()) }

    Column(modifier = modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = next?.let { "${s.prayerUpcoming}: ${s.t(it.first.stringKey)}" }
                        ?: s.prayerNoneLeft,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (next != null) {
                    Text(
                        text = timeFormat.format(next.second),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = placeLabel, style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { pickingCity = true }) {
                        Icon(Icons.Filled.EditLocation, contentDescription = null)
                        Text("  " + s.change)
                    }
                }
                // ⚠️ التاريخ الهجري **مع الميلادي** لا بديلًا عنه:
                // المستخدم يعيش بالتقويمين معًا، وربط المناسبات
                // الدينية يحتاج الهجري وحده.
                Text(
                    text = "${gregorianText(today, locale)} · ${hijriText(today, hijriOffset)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // ⚠️ بطاقة رمضان **تحت البطاقة الرئيسية** لا داخلها:
        // هي فرع على المواقيت لا بديلًا عنها — ومن لم يكن صائمًا
        // لا يفقد شيئًا، فهي لا تظهر إلا في رمضان.
        val hijriDate = remember(today, hijriOffset) { HijriCalendar.from(today, hijriOffset) }
        val ramadanDay = Ramadan.dayOf(hijriDate)
        if (ramadanDay != null) {
            RamadanCard(
                day = day,
                ramadanDay = ramadanDay,
                length = Ramadan.lengthOf(hijriDate.year),
            )
        }

        // ⚠️ «تابع القراءة» **تحت البطاقة مباشرة** لا في قسم: أكثر
        // ما يُفتح التطبيق لأجله بعد المواقيت هو العودة إلى حيث
        // توقّف القارئ. وهي تظهر بلا مفتاح إن لم يُقرأ شيء —
        // اختصار إلى لا شيء ليس اختصارًا.
        val last = remember(hijriOffset) { QuranProgressStore.load(context) }
        if (last != null) {
            Card(
                onClick = { onContinueReading(last.surah, last.ayah) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = s.continueReading,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "${last.surahName} · ${s.quranAyah} ${last.ayah}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = "${QuranProgress.percent(last.surah, last.ayah)}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        // ⚠️ المناسبات **بعد التاريخ الهجري** لا قبله: العدّاد
        // التنازلي لا يُفهم إلا بجانب التاريخ الذي يُحسب منه.
        val events = remember(hijriOffset, today) {
            IslamicEvents.upcoming(today, hijriOffset, limit = 2)
        }
        if (events.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                events.forEach { (at, event) ->
                    val days = ChronoUnit.DAYS.between(today, at).toInt()
                    Column {
                        Text(
                            text = s.t(event.nameKey),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            // ⚠️ «اليوم» و«غدًا» لا «٠ يومًا» و«١
                            // يومًا»: العدّاد الذي يقول «باقي ٠
                            // يومًا» على مناسبة اليوم نفسه غباء.
                            text = when (days) {
                                0 -> s.eventToday
                                1 -> s.eventTomorrow
                                else -> s.tArgs("event_days_left", "n" to days)
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            items(PrayerMethod.entries) { method ->
                TextButton(onClick = { onSettingsChange(settings.copy(method = method)) }) {
                    Text(
                        text = method.name(s),
                        color = if (method == settings.method) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (method == settings.method) FontWeight.Bold else null,
                    )
                }
            }
        }

        // ⚠️ القاعدة **تحت طريقة الحساب** لا داخلها: هي تعديل
        // عليها، ومن رآها وحدها ظنّها طريقة ثانية فيختار ما لا
        // يفهمه. القرب يبيّن أنها فرع لا نظير.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = s.hlTitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp),
            )
            HighLatitude.entries.forEach { rule ->
                TextButton(
                    onClick = { onSettingsChange(settings.copy(highLatitude = rule)) },
                ) {
                    Text(
                        text = s.t(rule.nameKey),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (rule == settings.highLatitude) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (rule == settings.highLatitude) FontWeight.Bold else null,
                    )
                }
            }
        }

        // ⚠️ زر التعديل **تحت طريقة الحساب** لا كزر مستقلّ: هو
        // تعديل على الحساب، ومن رآه فوقها ظنّه طريقة حساب ثالثة.
        TextButton(
            onClick = { showAdjust = true },
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            Text(s.adjustOpen)
        }

        TextButton(
            onClick = { showMonth = true },
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            Text(s.monthlyCalendar)
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            items(day.ordered) { (slot, instant) ->
                val isNext = next?.first == slot
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = s.t(slot.stringKey),
                        fontWeight = if (isNext) FontWeight.Bold else null,
                        color = if (isNext) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = timeFormat.format(instant),
                            fontSize = 18.sp,
                            fontWeight = if (isNext) FontWeight.Bold else null,
                            color = if (isNext) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                        )
                        // ⚠️ علامة «معدّل» **دائمًا** لا عند الغفلة:
                        // من يرى وقتًا مخالفًا لمسجده ولا يعلم أنه
                        // معدّل سيظنّ التطبيق مخطئًا.
                        val delta = PrayerAdjustments.minutesOf(settings, slot)
                        if (delta != 0) {
                            Text(
                                text = s.tArgs(
                                    "adjust_minutes",
                                    "n" to PrayerAdjustments.signed(delta),
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                        }
                    }
                }
            }
        }
    }

}

/// التاريخ الميلادي بلغة المستخدم.
///
/// ⚠️ `DateTimeFormatter` بلغة الواجهة لا بنمط ثابت: «١٩ أكتوبر»
/// و«19 octobre» و«October 19» كلها صحيحة في موضعها، ونمط واحد
/// يفرض ترتيب لغة على أخرى.
private fun gregorianText(date: LocalDate, locale: AppLocale): String =
    runCatching {
        DateTimeFormatter
            .ofLocalizedDate(FormatStyle.LONG)
            .withLocale(java.util.Locale.forLanguageTag(locale.code))
            .format(date)
    }.getOrDefault(date.toString())

/// بطاقة رمضان: الإفطار وآخر وقت السحور.
///
/// ⚠️ **وصف لا حكم**: البطاقة تعرض الوقت، ولا تهنّئ ولا تعاتب.
/// ومن كان مسافرًا أو مريضًا فعذره عنده، والتطبيق الذي يزعم أنه
/// يعرف حالك أكبر من أن يصدق.
@Composable
private fun RamadanCard(
    day: PrayerDay,
    ramadanDay: Int,
    length: Int,
) {
    val s = LocalStrings.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                s.tArgs("ramadan_day", "a" to ramadanDay),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            // ⚠️ العشر الأواخر تُذكر **بعد اليوم الحادي والعشرين**
            // لا بـ «باقي عشرة أيام»: العدّ التنازلي يبدأ في سنة
            // ٢٩ يومًا من اليوم ٢٠، واليوم العشرون ليس من الأواخر.
            if (Ramadan.isLastTen(ramadanDay)) {
                Text(
                    s.ramadanLastTen,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(s.ramadanIftar, style = MaterialTheme.typography.labelSmall)
                    Text(
                        timeFormat.format(day.maghrib),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column {
                    Text(s.ramadanSuhur, style = MaterialTheme.typography.labelSmall)
                    Text(
                        timeFormat.format(day.fajr),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            // ⚠️ عدد الأيام من **السنة الهجريّة نفسها**: الشهر قمريّ،
            // وعدّته ٢٩ أو ٣٠ بحسب الرؤية.
            Text(
                "$ramadanDay / $length",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

/// نافذة التعديل اليدوي للمواقيت (A4).
///
/// ⚠️ **الثلاثون سقفًا** معروضة في النصّ لا في السلوك وحده: من
/// يعدّل مئة دقيقة يريد أن يعرف أن التطبيق لم يطعه.
///
/// ⚠️ والتعديل **لكل صلاة على حدة**: من تأخّر فجره في مسجده
/// لا يلزمه أن يتأخّر عصره، ومن عدّل المغرب لا يلمس العشاء.
@Composable
private fun AdjustmentDialog(
    settings: PrayerSettings,
    onChange: (PrayerSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    val s = LocalStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(s.adjustTitle) },
        text = {
            Column {
                Text(
                    text = s.adjustBody,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                PrayerSlot.entries.forEach { slot ->
                    val minutes = PrayerAdjustments.minutesOf(settings, slot)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = s.t(slot.stringKey),
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(
                            onClick = {
                                onChange(
                                    PrayerAdjustments.withAdjustment(
                                        settings, slot, minutes - 1,
                                    ),
                                )
                            },
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = null)
                        }
                        Text(
                            text = s.tArgs(
                                "adjust_minutes",
                                "n" to PrayerAdjustments.signed(minutes),
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (minutes != 0) FontWeight.Bold else null,
                            color = if (minutes != 0) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                        IconButton(
                            onClick = {
                                onChange(
                                    PrayerAdjustments.withAdjustment(
                                        settings, slot, minutes + 1,
                                    ),
                                )
                            },
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(s.ok) }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onChange(settings.copy(adjustments = emptyMap()))
                    onDismiss()
                },
            ) { Text(s.adjustReset) }
        },
    )
}
