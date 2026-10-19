package com.athar.athar.ui.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
import com.athar.athar.prayer.PrayerCountdown
import com.athar.athar.prayer.PrayerSlot
import com.athar.athar.ramadan.Ramadan
import com.athar.athar.ui.theme.AmiriFamily
import com.athar.athar.ui.theme.AtharCream
import com.athar.athar.ui.theme.AtharGold
import com.athar.athar.ui.theme.AtharHeroBottom
import com.athar.athar.ui.theme.AtharHeroTop
import com.athar.athar.ui.theme.AtharPrimaryContainer
import com.athar.athar.hijri.HijriCalendar
import java.time.temporal.ChronoUnit
import com.athar.athar.ui.theme.AtharPageMargin

/// ⚠️ **الوقتُ بلغةِ التطبيق**: `ofPattern` بلا لغةٍ يتبع لغةَ
/// الجهاز، فيرى مستخدمُ العربية على هاتفٍ إنجليزيٍّ أوقاتَه
/// بأرقامٍ لاتينية. والوقتُ وقتُه: «٠٥:١٢» في العربية.
private fun timeFormat(locale: AppLocale): DateTimeFormatter =
    DateTimeFormatter
        .ofPattern("HH:mm", locale.javaLocale())
        .withZone(ZoneId.systemDefault())

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
                        // ⚠️ **المكانُ اختير**: فسؤالُ «حدّد مدينتك»
                        // لا يعود.
                        locationChosen = true,
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
                        locationChosen = true,
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
    val timeFmt = remember(locale) { timeFormat(locale) }
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // ⚠️ **سؤالُ المكانِ فوقَ البطل**: الموقعُ الافتراضيُّ
        // القاهرة، فمن لم يختار مكانه يرى مواقيتَ القاهرة ويظنّها
        // مواقيتَ مدينته — فلا يصحُّ أن يبدأ من البطاقةِ الصامتة.
        if (!settings.locationChosen) {
            Card(
                onClick = { pickingCity = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                // ⚠️ **النعناعيُّ لا الأزرق**: لونُ النسخة الثالثة
                // أزرقٌ رماديٌّ، وهو لا ينتمي إلى التطبيق. فالبطاقةُ
                // بلونِ الأساس.
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(
                            text = s.prayerPickLocation,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            text = s.prayerPickLocationBody,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }

        // ⚠️ **البطلُ في الأعلى**: الموعدُ والوقتُ أوّلُ ما يُفتح
        // التطبيقُ لأجله، فهما أولاً — لا تحت إعداداتِ الحساب ولا
        // تحت قائمةِ المواقيت.
        HeroCard(
            prayerName = next?.let { s.t(it.first.stringKey) } ?: s.prayerNoneLeft,
            time = next?.second?.let { timeFmt.format(it) } ?: "",
            place = placeLabel,
            date = "${gregorianText(today, locale)} · ${hijriText(today, hijriOffset)}",
            target = next?.second,
            onChangePlace = { pickingCity = true },
        )

        // ⚠️ **شريطُ المواقيت**: ستّ صلواتٍ في صفٍّ واحدٍ يُقرأ في
        // نظرة — ومن أراد تفاصيلِ اليوم فالقائمةُ تحته.
        PrayerStrip(day = day, next = next, settings = settings)

        // ⚠️ بطاقة رمضان **تحت البطل** لا داخله: هي فرعٌ على
        // المواقيت لا بديلٌ عنها — ومن لم يكن صائمًا لا يفقد شيئًا.
        val hijriDate = remember(today, hijriOffset) { HijriCalendar.from(today, hijriOffset) }
        val ramadanDay = Ramadan.dayOf(hijriDate)
        if (ramadanDay != null) {
            RamadanCard(
                day = day,
                ramadanDay = ramadanDay,
                length = Ramadan.lengthOf(hijriDate.year),
            )
        }

        // ⚠️ «تابع القراءة» **تحت المواقيت مباشرة**: أكثر ما يُفتح
        // التطبيقُ لأجله بعد المواقيت هو العودةُ إلى حيث توقّف
        // القارئ. وهي تظهر بلا مفتاح إن لم يُقرأ شيء — اختصارٌ إلى
        // لا شيء ليس اختصارًا.
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
                // ⚠️ **شريطُ التقدّمِ تحتِ السطر**: الرقمُ يقول «كم
                // بقي» والشريطُ يقول «من أين بدأ» — والاثنان
                // يُقرآن بنظرة.
                LinearProgressIndicator(
                    progress = { QuranProgress.percent(last.surah, last.ayah) / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
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

        // ⚠️ **الإعداداتُ في بطاقةٍ واحدة** في أسفل الصفحة: طريقةُ
        // الحساب وقاعدةُ خطوطِ العرض والتعديلُ والتقويمُ أدواتٌ
        // تُفتح مرّةً في الشهر، لا تُترك في مجرى الصفحة تُزيح
        // المواقيتَ تحتها.
        CalculationCard(
            settings = settings,
            onMethod = { method ->
                onSettingsChange(settings.copy(method = method))
            },
            onHighLatitude = { rule ->
                onSettingsChange(settings.copy(highLatitude = rule))
            },
            onAdjust = { showAdjust = true },
            onMonth = { showMonth = true },
        )

        Spacer(Modifier.height(16.dp))
    }
}

/// بطاقةُ البطل: الصلاة القادمة بتدرّجٍ أخضرَ ووقتٍ ذهبيّ.
///
/// ⚠️ **الاسمُ أكبرُ من الوقت**: الوقتُ رقمٌ يُقرأ في ثانية،
/// والاسمُ هو ما ينتظره المستخدم. والاسمُ بخطٍّ نسخيّ (أميري)
/// فيتّسع المجالُ ويصير للصلاة هيبة.
///
/// ⚠️ **والتدرّجُ للبطلِ وحده**: لو تدرّجت كلُّ البطاقاتِ صار
/// التطبيقُ شريطًا من الألوان، فقرأَه العينُ بلا ترتيب.
@Composable
private fun HeroCard(
    prayerName: String,
    time: String,
    place: String,
    date: String,
    target: Instant?,
    onChangePlace: () -> Unit,
) {
    val s = LocalStrings.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(AtharHeroTop, AtharHeroBottom)),
                ),
        ) {
            // ⚠️ **نجمةُ ثمانٍ في الركن**: مربّعانِ متقاطعان — وهي
            // زخرفةُ «الخاتم» في العمارة الإسلامية. خفيفةٌ إلى حدّ
            // أنها لا تُقرأ نصًّا، لكنها تقول إن هذا تطبيقٌ له هوية.
            val ltr = LocalLayoutDirection.current == LayoutDirection.Ltr
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = if (ltr) size.width * 0.86f else size.width * 0.14f
                val cy = size.height * 0.5f
                val r = size.minDimension * 0.62f
                val stroke = 1.2.dp.toPx()
                val line = AtharGold.copy(alpha = 0.18f)
                rotate(0f, Offset(cx, cy)) {
                    drawRect(
                        color = line,
                        topLeft = Offset(cx - r, cy - r),
                        size = Size(r * 2, r * 2),
                        style = Stroke(width = stroke),
                    )
                }
                rotate(45f, Offset(cx, cy)) {
                    drawRect(
                        color = line,
                        topLeft = Offset(cx - r, cy - r),
                        size = Size(r * 2, r * 2),
                        style = Stroke(width = stroke),
                    )
                }
            }
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = s.prayerNextLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = AtharPrimaryContainer,
                )
                Text(
                    text = prayerName,
                    style = MaterialTheme.typography.displayMedium,
                    color = AtharCream,
                    fontFamily = AmiriFamily,
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.headlineLarge,
                        color = AtharGold,
                        fontFamily = AmiriFamily,
                    )
                    if (target != null) {
                        Spacer(Modifier.width(14.dp))
                        CountdownText(target = target, color = AtharPrimaryContainer)
                    }
                }
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "$place · $date",
                        style = MaterialTheme.typography.bodySmall,
                        color = AtharPrimaryContainer,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onChangePlace) {
                        Icon(
                            Icons.Filled.EditLocation,
                            contentDescription = null,
                            tint = AtharPrimaryContainer,
                        )
                        Text("  " + s.change, color = AtharPrimaryContainer)
                    }
                }
            }
        }
    }
}

/// شريطُ المواقيت: ستّ صلواتٍ في صفٍّ واحد.
///
/// ⚠️ **الصلاةُ القادمةُ مُلوَّنةٌ وحدها**: من رآها ملوّنةً عرف
/// أين هو، ومن رآها كلها ملوّنةً لم يعرف.
///
/// ⚠️ **والماضيةُ باهتة**: ما فات لا يُعاد إلى التفصيل، والزمنُ
/// يمرّ. فلا تتساوى الصلواتُ في الوزن.
@Composable
private fun PrayerStrip(
    day: PrayerDay,
    next: Pair<PrayerSlot, Instant>?,
    settings: PrayerSettings,
) {
    val s = LocalStrings.current
    val locale = com.athar.athar.i18n.LocalAppLocale.current
    val timeFmt = remember(locale) { timeFormat(locale) }
    val now = remember(day) { Instant.now() }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AtharPageMargin, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(day.ordered) { (slot, instant) ->
            val isNext = next?.first == slot
            val isPast = instant.isBefore(now)
            Column(
                modifier = Modifier
                    .width(76.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isNext) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                    )
                    .then(
                        if (isNext) {
                            Modifier
                        } else {
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(18.dp),
                            )
                        },
                    )
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = s.t(slot.stringKey),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = timeFmt.format(instant),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isNext) FontWeight.Bold else null,
                    color = when {
                        isNext -> MaterialTheme.colorScheme.onPrimaryContainer
                        isPast -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
                // ⚠️ علامة «معدّل» **دائمًا** لا عند الغفلة: من
                // يرى وقتًا مخالفًا لمسجده ولا يعلم أنه معدّل
                // سيظنّ التطبيق مخطئًا.
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

/// بطاقةُ إعدادات المواقيت: طريقةُ الحساب وقاعدةُ خطوطِ العرض
/// والتعديلُ اليدويُّ والتقويمُ الشهريّ.
@Composable
private fun CalculationCard(
    settings: PrayerSettings,
    onMethod: (PrayerMethod) -> Unit,
    onHighLatitude: (HighLatitude) -> Unit,
    onAdjust: () -> Unit,
    onMonth: () -> Unit,
) {
    val s = LocalStrings.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = s.prayerCalcSettings,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )

            // ⚠️ طريقةُ الحساب **في صفٍّ يُمرَّر**: طرقُ الحساب
            // سبعٌ، والصفُّ أعرضُ من الشاشة.
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(PrayerMethod.entries) { method ->
                    FilterChip(
                        selected = method == settings.method,
                        onClick = { onMethod(method) },
                        label = { Text(method.name(s)) },
                    )
                }
            }

            // ⚠️ القاعدة **تحت طريقة الحساب** لا داخلها: هي تعديل
            // عليها، ومن رآها وحدها ظنّها طريقة ثانية فيختار ما
            // لا يفهمه. القرب يبيّن أنها فرعٌ لا نظير.
            Text(
                text = s.hlTitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                HighLatitude.entries.forEach { rule ->
                    FilterChip(
                        selected = rule == settings.highLatitude,
                        onClick = { onHighLatitude(rule) },
                        label = { Text(s.t(rule.nameKey)) },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // ⚠️ زر التعديل **تحت طريقة الحساب** لا كزرٍ مستقلّ:
                // هو تعديلٌ على الحساب.
                TextButton(onClick = onAdjust) { Text(s.adjustOpen) }
                TextButton(onClick = onMonth) { Text(s.monthlyCalendar) }
            }
        }
    }
}

/// العدّ التنازلي حتى الصلاة القادمة.
///
/// ⚠️ **تأخيره الخاص** لا تأخير الشاشة: إعادة تركيب الشاشة
/// كلّ ثانية تُعيد بناء المواقيت كلّها وتُثقل الإطار، والعدّ
/// وحده هو ما يحتاج الثانية.
///
/// ⚠️ والحلقة **لا تتوقّف**: `delay` داخل `LaunchedEffect` تُلغى
/// تلقائيًّا حين يخرج التأليف من الشاشة.
@Composable
private fun CountdownText(
    target: Instant,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    val s = LocalStrings.current
    val locale = com.athar.athar.i18n.LocalAppLocale.current
    var now by remember { mutableStateOf(Instant.now()) }
    LaunchedEffect(target) {
        while (true) {
            now = Instant.now()
            delay(1000)
        }
    }
    val countdown = PrayerCountdown.until(target, now)
    Text(
        text = if (countdown.isElapsed) {
            s.prayerNow
        } else {
            "${s.prayerRemaining}: ${PrayerCountdown.text(countdown, locale)}"
        },
        style = MaterialTheme.typography.titleMedium,
        color = color,
    )
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
            .withLocale(locale.javaLocale())
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
    val locale = com.athar.athar.i18n.LocalAppLocale.current
    val timeFmt = remember(locale) { timeFormat(locale) }
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
                        timeFmt.format(day.maghrib),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column {
                    Text(s.ramadanSuhur, style = MaterialTheme.typography.labelSmall)
                    Text(
                        timeFmt.format(day.fajr),
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
