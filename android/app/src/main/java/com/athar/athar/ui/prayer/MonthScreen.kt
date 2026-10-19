package com.athar.athar.ui.prayer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.athar.athar.i18n.AppLocale
import com.athar.athar.i18n.LocalStrings
import com.athar.athar.prayer.Cities
import com.athar.athar.prayer.MonthRow
import com.athar.athar.prayer.MonthSchedule
import com.athar.athar.prayer.PrayerSettings
import com.athar.athar.prayer.PrayerSlot
import com.athar.athar.share.MonthCards
import com.athar.athar.share.MonthImage
import com.athar.athar.share.ShareImage
import com.athar.athar.i18n.localDigits
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.max

private const val DAY_W = 46
private const val TIME_W = 58

/// التقويم الشهري — جدول الشهر كلّه: صفّ لكل يوم، وعمود لكل صلاة.
@Composable
fun MonthScreen(
    settings: PrayerSettings,
    locale: AppLocale,
    hijriOffset: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val base = remember { LocalDate.now() }
    var year by remember { mutableIntStateOf(base.year) }
    var month by remember { mutableIntStateOf(base.monthValue) }
    var shareMessage by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    val rows = remember(settings, year, month, hijriOffset) {
        MonthSchedule.rows(settings, year, month, hijriOffset)
    }

    /// ⚠️ **اسمُ المكان**: جدولٌ بلا مكانٍ يُنسب لكلِّ من قرأه —
    /// ومواقيتُ القاهرة غيرُ مواقيتِ جدة.
    val placeName = remember(settings, locale) {
        settings.cityId
            ?.let { id -> Cities.all.firstOrNull { it.id == id }?.nameFor(locale) }
            ?.takeIf { it.isNotBlank() }
            ?: settings.placeName
    }

    /// ⚠️ **الرسمُ خارجَ الخيط الرئيسي**: صورةٌ بآلافِ البكسلات
    /// تُجمّد الشاشة إن رُسمت فيها.
    fun share() {
        if (busy) return
        busy = true
        shareMessage = null
        scope.launch {
            val data = MonthCards.dataOf(
                rows = rows,
                title = s.appName,
                subtitle = monthText(year, month, locale),
                headers = PrayerSlot.entries.map { s.t(it.stringKey) },
                // ⚠️ بلا اسمِ مكانٍ لا مكانَ في التذييل: «—  — أثر».
                footer = if (placeName.isBlank()) {
                    s.appName
                } else {
                    s.tArgs("month_share_footer", "place" to placeName)
                },
                rtl = locale.isRtl,
            )
            val uri = withContext(Dispatchers.Default) {
                runCatching {
                    ShareImage.save(context, MonthImage.render(data))
                }.getOrNull()
            }
            busy = false
            if (uri == null) {
                shareMessage = s.shareFailed
            } else {
                val started = runCatching {
                    context.startActivity(
                        ShareImage.shareIntent(uri, s.monthShareText),
                    )
                }.isSuccess
                if (!started) shareMessage = s.shareFailed
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back)
            }
            Text(
                text = s.monthlyCalendar,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            // ⚠️ **مشاركةُ الجدول صورةً**: جدولٌ يُعلَّق على الحائط
            // أنفع من جدولٍ يُقرأ ويُنسى.
            IconButton(onClick = { share() }, enabled = !busy) {
                Icon(Icons.Filled.Share, contentDescription = s.monthShare)
            }
        }

        shareMessage?.let { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }

        // ⚠️ التنقّل بـ [MonthSchedule] لا بـ `month ± 1`: كانون
        // الثاني ناقص واحد هو كانون الأول من السنة التي قبلها، ومن
        // كتب الطرح وحده حصل على الشهر صفر.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {
                MonthSchedule.previous(year, month).let { (y, m) -> year = y; month = m }
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = s.monthPrev,
                )
            }
            Text(
                text = monthText(year, month, locale),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            IconButton(onClick = {
                MonthSchedule.next(year, month).let { (y, m) -> year = y; month = m }
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = s.monthNext,
                )
            }
        }

        HorizontalDivider()

        // ⚠️ الترويسة **خارج** الجدول وتمريرها معه أفقيًّا: رأس
        // ثابت فوق أعمدة تتحرّك يجعل العمود يقرأ صلاة غير التي
        // تحتَه.
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
        ) {
            Text("", modifier = Modifier.width(DAY_W.dp))
            for (slot in PrayerSlot.entries) {
                Text(
                    text = s.t(slot.stringKey),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(TIME_W.dp),
                )
            }
        }

        HorizontalDivider()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(rows) { row ->
                MonthRowView(row = row, locale = locale, today = base)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            }
        }
    }
}

@Composable
private fun MonthRowView(row: MonthRow, locale: AppLocale, today: LocalDate) {
    val digits = localDigits()
    val s = LocalStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // ⚠️ اليوم والميلادي والهجري **معًا**: التقويم الذي يُظهر
        // ميلاديًّا فقط يفقد معناه عند من يعرف الشهر برمضان، والذي
        // يُظهر هجريًّا فقط يفقده عند من يعرفه بأكتوبر.
        Column(
            modifier = Modifier.width(DAY_W.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = digits(row.date.dayOfMonth),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (row.date == today) FontWeight.Bold else null,
                color = if (row.date == today) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
            Text(
                text = digits(row.hijri.day),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        for ((slot, instant) in row.day.ordered) {
            Text(
                text = timeText(instant, locale),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TIME_W.dp),
            )
        }
    }
}

/// ⚠️ الشهر والسنة بلغة الواجهة: «أكتوبر ٢٠٢٦» و«October 2026»
/// و«octobre 2026».
private fun monthText(year: Int, month: Int, locale: AppLocale): String =
    runCatching {
        DateTimeFormatter
            .ofLocalizedDate(FormatStyle.FULL)
            .withLocale(locale.javaLocale())
            .format(YearMonth.of(year, month).atDay(1))
            .let { it.substring(max(0, it.length - 12)) }
    }.getOrDefault("$year/$month")

/// ⚠️ **٢٤ ساعةً دائمًا**: الجدولُ يوافقُ صورةَ التصدير (HH:mm)،
/// و«ص/م» في الجدولِ بلا مكانٍ له في الصورة.
private fun timeText(instant: java.time.Instant, locale: AppLocale): String =
    runCatching {
        DateTimeFormatter
            .ofPattern("HH:mm", locale.javaLocale())
            .withZone(java.time.ZoneId.systemDefault())
            .format(instant)
    }.getOrDefault("--:--")
