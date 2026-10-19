package com.athar.athar.share

import com.athar.athar.athary.DailyRecord
import com.athar.athar.athary.DayLevel
import com.athar.athar.athary.TrackerState
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/// بطاقةُ المشاركة (F6) — **بياناتٌ نقية** قبل الرسم.
///
/// ⚠️ **الأحدثُ أوّلًا**: تُقرأ العربية من اليمين إلى اليسار،
/// والأيام تُرسم من اليسار لليمين فيُقرأ ترتيبها معكوسًا. فالخانة
/// اليسرى **اليوم**، والأيمنى أقدمُ يومٍ في الأسبوع.
data class ShareCardData(
    val title: String,
    val subtitle: String,
    /// عددُ الأيام التي فيها عبادةٌ واحدة على الأقل.
    val activeDays: Int,
    /// مجموعُ العبادات في الأسبوع.
    val totalActs: Int,
    /// سلسلةُ الأيام المتصلة.
    val streak: Int,
    /// ٧ أعداد — **الأحدث أوّلًا** (ترتيب الرسم من اليسار).
    val days: List<Int>,
    /// أسماءُ الأيام بنفس ترتيب `days`.
    val dayNames: List<String>,
    val footer: String,
    /// أسماءُ الأرقام الثلاثة — من لغة الواجهة.
    val activeLabel: String,
    val actsLabel: String,
    val streakLabel: String,
) {
    /// كثافةُ كلّ يوم — نفسُ دلالة «أثري» (F2).
    ///
    /// ⚠️ `levelFor` في `DailyRecord` لا في `DayLevel`: الكثافةُ
    /// دالّةُ اليوم لا دالّةُ النوع.
    val levels: List<DayLevel> get() = days.map { DailyRecord.levelFor(it) }
}

/// بناءُ بيانات بطاقة المشاركة — **نقيّ**، بلا `Canvas` ولا `Context`.
object ShareCards {

    const val DAYS = 7

    /// ⚠️ **الأحدثُ أوّلًا**: نافذةُ «أثري» ترجع **الأقدمَ أوّلًا**
    /// (من قديمٍ إلى اليوم — `window.last()` هو اليوم) — فَنقلبها
    /// هنا. والرسمُ في العربية يبدأ من اليسار، واليسارُ هو اليوم.
    fun daysNewestFirst(state: TrackerState, today: LocalDate): List<Int> =
        state.window(today, DAYS).reversed().map { it.count }

    /// أسماءُ الأيام — بلغة الواجهة.
    ///
    /// ⚠️ وبـ`SHORT` لا `FULL`: «الثلاثاء» أعرض من «ثلاثاء»،
    /// والخانةُ ضيّقة.
    fun dayNames(today: LocalDate, locale: Locale): List<String> =
        (0 until DAYS).map { offset ->
            val day = today.minusDays(offset.toLong())
            day.dayOfWeek.getDisplayName(TextStyle.SHORT, locale).ifBlank { "—" }
        }

    fun dataOf(
        state: TrackerState,
        today: LocalDate,
        locale: Locale,
        title: String,
        subtitle: String,
        footer: String,
        activeLabel: String,
        actsLabel: String,
        streakLabel: String,
    ): ShareCardData {
        val counts = daysNewestFirst(state, today)
        return ShareCardData(
            title = title,
            subtitle = subtitle,
            activeDays = counts.count { it > 0 },
            totalActs = counts.sum(),
            streak = state.currentStreak(today),
            days = counts,
            dayNames = dayNames(today, locale),
            footer = footer,
            activeLabel = activeLabel,
            actsLabel = actsLabel,
            streakLabel = streakLabel,
        )
    }
}
