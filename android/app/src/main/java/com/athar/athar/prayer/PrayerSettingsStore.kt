package com.athar.athar.prayer

import android.content.Context
import com.batoulapps.adhan.Madhab

/// إعدادات المواقيت — SharedPreferences لا DataStore: بسيطة،
/// متزامنة، وبلا اعتماد إضافي.
///
/// ⚠️ لا نطلب صلاحية موقع بعد — الموقع الافتراضي ثابت (القاهرة)
/// ويختاره المستخدم من قائمة مدن لاحقًا.
object PrayerSettingsStore {
    private const val PREFS = "athar.prayer.v1"

    fun load(context: Context): PrayerSettings {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return PrayerSettings(
            method = runCatching { PrayerMethod.valueOf(p.getString("method", "")!!) }
                .getOrDefault(PrayerMethod.EGYPTIAN),
            madhab = runCatching { Madhab.valueOf(p.getString("madhab", "")!!) }
                .getOrDefault(Madhab.SHAFI),
            latitude = p.getFloat("lat", 30.0444f).toDouble(),
            longitude = p.getFloat("lng", 31.2357f).toDouble(),
            placeName = p.getString("place", "القاهرة") ?: "القاهرة",
        )
    }

    fun save(context: Context, settings: PrayerSettings) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("method", settings.method.name)
            .putString("madhab", settings.madhab.name)
            .putFloat("lat", settings.latitude.toFloat())
            .putFloat("lng", settings.longitude.toFloat())
            .putString("place", settings.placeName)
            .apply()
    }
}
