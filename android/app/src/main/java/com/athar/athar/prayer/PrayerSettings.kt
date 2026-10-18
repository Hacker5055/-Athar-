package com.athar.athar.prayer

import com.batoulapps.adhan.Madhab

data class PrayerSettings(
    val method: PrayerMethod = PrayerMethod.EGYPTIAN,
    /// المذهب: [Madhab.SHAFI] أو [Madhab.HANAFI] — يغيّر وقت العصر فقط.
    val madhab: Madhab = Madhab.SHAFI,
    val latitude: Double = 30.0444,
    val longitude: Double = 31.2357,
    val placeName: String = "القاهرة",
)
