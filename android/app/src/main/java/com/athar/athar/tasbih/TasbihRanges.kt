package com.athar.athar.tasbih

import java.time.LocalDate
import java.time.ZoneId

/// مدَيات اليوم والأسبوع (E6) — نقيّة، بلا جهاز وبلا `Context`.
///
/// ⚠️ **نصف مفتوح** `[البداية، النهاية)`: فلو كانت النهاية
/// مشمولة، لتُحسب جلسةُ منتصف الليل مرّتين — في يومها وفي
/// الغد.
object TasbihRanges {

    /// ⚠️ **سبعة أيام تنتهي اليوم** — كـ«أثري» (F4). لا «آخر سبعة
    /// أيام مخزّنة»: من فتح التطبيق الأربعاء يريد هذا الأسبوع
    /// لا نافذةً تنزلق بلا مرجع.
    const val WEEK_DAYS = 7

    fun startOfDay(day: LocalDate, zone: ZoneId): Long =
        day.atStartOfDay(zone).toInstant().toEpochMilli()

    fun endOfDay(day: LocalDate, zone: ZoneId): Long =
        day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

    /// اليوم civil: من منتصف ليله إلى منتصف ليل الغد.
    fun day(day: LocalDate, zone: ZoneId): LongRange =
        startOfDay(day, zone) until endOfDay(day, zone)

    /// آخر سبعة أيام **ينتهي اليوم**.
    fun week(today: LocalDate, zone: ZoneId): LongRange {
        val from = today.minusDays((WEEK_DAYS - 1).toLong())
        return startOfDay(from, zone) until endOfDay(today, zone)
    }
}
