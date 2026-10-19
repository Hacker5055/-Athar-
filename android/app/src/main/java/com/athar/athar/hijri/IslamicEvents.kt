package com.athar.athar.hijri

import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoUnit

/// مناسبة إسلامية — ثابتة في التقويم الهجري.
data class IslamicEvent(
    val id: String,
    /// مفتاح ترجمة: اسم المناسبة بلغة المستخدم.
    val nameKey: String,
    val month: Int,
    val day: Int,
)

/// المناسبات والعدّاد التنازلي (G3 وG4 من docs/02) — منطق نقي.
///
/// ⚠️ التواريخ **حسابية** لا رؤية: تُقرأ من التقويم الهجري نفسه،
/// فهي بحدّه ودقّته. وضبط الهجري (±يوم/يومين) ينتقل إليها تلقائيًّا،
/// لأن المناسبة تتبع ما يراه أهل البلد لا ما نحسبه نحن.
object IslamicEvents {

    val ALL: List<IslamicEvent> = listOf(
        IslamicEvent("hijri_new_year", "event_hijri_new_year", 1, 1),
        IslamicEvent("ashura", "event_ashura", 1, 10),
        IslamicEvent("mawlid", "event_mawlid", 3, 12),
        IslamicEvent("isra", "event_isra", 7, 27),
        IslamicEvent("ramadan", "event_ramadan", 9, 1),
        IslamicEvent("qadr", "event_qadr", 9, 27),
        IslamicEvent("eid_fitr", "event_eid_fitr", 10, 1),
        IslamicEvent("arafah", "event_arafah", 12, 9),
        IslamicEvent("eid_adha", "event_eid_adha", 12, 10),
    )

    /// أقرب وقوع للمناسبة ابتداءً من [today].
    ///
    /// ⚠️ نجرّب **سنة الهجري الحالية ثم التي بعدها**: المناسبة قد
    /// تكون مرّت هذا العام. والسنة الهجريّة أقصر من الميلاديّة
    /// (٣٥٤ يومًا)، فلا يجوز تقدير الفرق بحساب ٣٦٥.
    fun nextOccurrence(event: IslamicEvent, today: LocalDate, offsetDays: Int = 0): LocalDate? {
        val now = today.plusDays(offsetDays.toLong())
        val year = HijriCalendar.from(now, 0).year
        return (year..year + 2).firstNotNullOfOrNull { y ->
            runCatching {
                val hijri = HijrahDate.of(y, event.month, event.day)
                LocalDate.from(hijri)
            }.getOrNull()?.takeIf { !it.isBefore(now) }
        }
    }

    /// كم يومًا بقي على المناسبة.
    fun daysUntil(event: IslamicEvent, today: LocalDate, offsetDays: Int = 0): Int? {
        val at = nextOccurrence(event, today, offsetDays) ?: return null
        return ChronoUnit.DAYS.between(today, at).toInt()
    }

    /// المناسبات القادمة مرتّبة بترتيب وقوعها.
    ///
    /// ⚠️ **مرتّبة** لا كما كُتبت في القائمة: «باقي على العيد ١٢
    /// يومًا» قبل «باقي على رمضان ٢٠٠ يوم»، ولو كان رمضان أولًا
    /// في القائمة.
    fun upcoming(
        today: LocalDate,
        offsetDays: Int = 0,
        limit: Int = 4,
    ): List<Pair<LocalDate, IslamicEvent>> =
        ALL.mapNotNull { event ->
            nextOccurrence(event, today, offsetDays)?.let { it to event }
        }
            .sortedBy { it.first }
            .take(limit)
}
