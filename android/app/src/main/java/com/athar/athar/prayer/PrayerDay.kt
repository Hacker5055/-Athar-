package com.athar.athar.prayer

import java.time.Instant
import java.time.LocalDate

/// أوقات الصلوات الست ليوم واحد.
data class PrayerDay(
    val date: LocalDate,
    val fajr: Instant,
    val sunrise: Instant,
    val dhuhr: Instant,
    val asr: Instant,
    val maghrib: Instant,
    val isha: Instant,
) {
    val ordered: List<Pair<PrayerSlot, Instant>> = listOf(
        PrayerSlot.FAJR to fajr,
        PrayerSlot.SUNRISE to sunrise,
        PrayerSlot.DHUHR to dhuhr,
        PrayerSlot.ASR to asr,
        PrayerSlot.MAGHRIB to maghrib,
        PrayerSlot.ISHA to isha,
    )

    /// الصلاة القادمة بعد [now].
    ///
    /// ⚠️ الشروق **ليس صلاة** — لا يُعدّ في «القادمة» (قرار الأسبوع ٢).
    fun nextAfter(now: Instant): Pair<PrayerSlot, Instant>? =
        ordered
            .filter { (slot, time) -> slot != PrayerSlot.SUNRISE && time.isAfter(now) }
            .minByOrNull { (_, time) -> time }
}

enum class PrayerSlot(val arabicName: String) {
    FAJR("الفجر"),
    SUNRISE("الشروق"),
    DHUHR("الظهر"),
    ASR("العصر"),
    MAGHRIB("المغرب"),
    ISHA("العشاء"),
}
