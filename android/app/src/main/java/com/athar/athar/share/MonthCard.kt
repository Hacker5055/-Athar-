package com.athar.athar.share

import com.athar.athar.core.ArabicDigits
import com.athar.athar.prayer.MonthRow
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/// صفٌّ واحد في بطاقة الشهر: اليوم الميلادي والهجري ومواقيتُه.
data class MonthCardRow(
    val day: String,
    val hijri: String,
    /// المواقيتُ الستّة **بنفس ترتيب `PrayerSlot`**.
    val times: List<String>,
)

/// بياناتُ بطاقة الشهر — **نقية** قبل الرسم.
///
/// ⚠️ `rtl` في البيانات لا في الرسم: الجدولُ العربيُّ يُقرأ من
/// اليمين، فالعمودُ الأول (الفجر) على اليمين — ومن رسمه من اليسار
/// قرأه معكوسًا.
data class MonthCardData(
    val title: String,
    val subtitle: String,
    val headers: List<String>,
    val rows: List<MonthCardRow>,
    val footer: String,
    val rtl: Boolean,
)

/// بناءُ بيانات بطاقة الشهر — **نقيّ**، بلا `Canvas` ولا `Context`.
object MonthCards {

    /// ⚠️ **٢٤ ساعة**: جدولُ مواقيتٍ يُطبع ويُعلَّق لا تُكتب فيه
    /// «ص/م» — والساعةُ في المواقيت معروفةٌ بلا لاحقة.
    private val TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun dataOf(
        rows: List<MonthRow>,
        title: String,
        subtitle: String,
        headers: List<String>,
        footer: String,
        rtl: Boolean,
        zone: ZoneId = ZoneId.systemDefault(),
    ): MonthCardData {
        fun digits(text: String): String =
            if (rtl) ArabicDigits.ofText(text) else text

        return MonthCardData(
            title = title,
            subtitle = subtitle,
            headers = headers,
            rows = rows.map { row ->
                MonthCardRow(
                    day = digits(row.date.dayOfMonth.toString()),
                    hijri = digits(row.hijri.day.toString()),
                    times = row.day.ordered.map { (_, at) ->
                        digits(TIME.withZone(zone).format(at))
                    },
                )
            },
            footer = footer,
            rtl = rtl,
        )
    }
}
