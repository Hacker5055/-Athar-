package com.athar.athar.notification

import com.athar.athar.prayer.PrayerDay
import com.athar.athar.prayer.PrayerSlot
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/// نافذةُ صمتٍ واحدة: من متى إلى متى.
data class SilenceWindow(
    val date: LocalDate,
    val prayer: PrayerSlot,
    val start: Instant,
    val end: Instant,
)

/// يبني نوافذ الصمت من مواقيت الصلاة — **محضٌّ تمامًا**.
///
/// ⚠️ كلُّ المنطق الزمني هنا: الأندرويد لا يعرف إلا «متى».
object SilencePlanner {

    const val AHEAD_DAYS = 7

    fun plan(
        days: List<PrayerDay>,
        settings: SilenceSettings,
        now: Instant,
    ): List<SilenceWindow> {
        if (!settings.enabled) return emptyList()
        val minutes = SilenceSettings.clamp(settings.minutes)

        val raw = mutableListOf<SilenceWindow>()
        for (day in days) {
            for ((slot, at) in day.ordered) {
                // ⚠️ الشروق **ليس صلاة** فلا صمتَ عليه.
                if (slot == PrayerSlot.SUNRISE) continue
                val start = at.plus(
                    SilenceSettings.DELAY_MINUTES.toLong(),
                    ChronoUnit.MINUTES,
                )
                val end = start.plus(minutes.toLong(), ChronoUnit.MINUTES)
                // ⚠️ ما انتهى لا يُجدول: منبّهٌ في الماضي يُطلق
                // فورًا، فيصمت الهاتف بلا سبب.
                if (!end.isAfter(now)) continue
                raw += SilenceWindow(
                    date = day.date,
                    prayer = slot,
                    start = start,
                    end = end,
                )
            }
        }
        raw.sortBy { it.start }
        return merge(raw)
    }

    /// ⚠️ **النوافذ المتداخلة تُدمج**: لو تداخلت نافذتان، لَرَدَّ
    /// الإيقافُ الأولُ فلترَ الصمت والثانيةُ ما تزال مفتوحة — فيصمت
    /// الهاتف لحظةً ثم يعود الصوت في منتصف صلاة.
    private fun merge(windows: List<SilenceWindow>): List<SilenceWindow> {
        val out = mutableListOf<SilenceWindow>()
        for (window in windows) {
            val last = out.lastOrNull()
            if (last != null && !window.start.isAfter(last.end)) {
                if (window.end.isAfter(last.end)) {
                    out[out.lastIndex] = last.copy(end = window.end)
                }
            } else {
                out += window
            }
        }
        return out
    }
}
