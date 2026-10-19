package com.athar.athar.ramadan

import com.athar.athar.hijri.HijriDate
import java.time.chrono.HijrahDate

/// وضع رمضان (A14 من docs/02) — المنطق النقي.
///
/// ⚠️ **لا نحسب الصيام ولا نفتي فيه**: هذا وضعٌ يعرض الوقت لا
/// حكمًا. من كان مسافرًا أو مريضًا فعذره عنده، والتطبيق الذي
/// يزعم أنه يعرف حالك أكبر من أن يصدق.
object Ramadan {

    /// رمضان هو الشهر التاسع.
    const val MONTH = 9

    fun isRamadan(hijri: HijriDate): Boolean = hijri.month == MONTH

    /// اليوم من رمضان (١…٣٠)، أو `null` إن لم نكن فيه.
    fun dayOf(hijri: HijriDate): Int? = hijri.day.takeIf { isRamadan(hijri) }

    /// كم يومًا في رمضان هذه السنة — ٢٩ أو ٣٠.
    ///
    /// ⚠️ من **التقويم الهجري نفسه** لا ثابتًا: الشهر الهجري قمريّ،
    /// وعدّته تختلف باختلاف الرؤية. ومن كتب «٣٠» ثابتًا جعل اليوم
    /// الثلاثين يوهم صاحبه بصيام يومٍ من شوّال.
    fun lengthOf(year: Int): Int = runCatching {
        HijrahDate.of(year, MONTH, 1).lengthOfMonth()
    }.getOrDefault(30).coerceIn(29, 30)

    /// ⚠️ العشر الأواخر **تبدأ من اليوم ٢١** لا من «آخر عشرة
    /// بالعدّ التنازلي»: لو قلنا «آخر عشرة» لبدأت في سنة ٢٩ يومًا
    /// من اليوم ٢٠ — واليوم العشرون ليس من الأواخر.
    fun isLastTen(day: Int): Boolean = day >= 21
}
