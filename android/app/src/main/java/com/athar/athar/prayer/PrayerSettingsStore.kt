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
            cityId = p.getString("cityId", "cairo"),
            placeName = p.getString("place", "") ?: "",
            // ⚠️ بالاسم لا بالترتيب: ترتيب التعداد قد يتغيّر،
            // والاسم ثابت.
            highLatitude = HighLatitude.fromName(p.getString("highLatitude", null)),
            adjustments = readAdjustments(p.getString("adjust", null)),
        )
    }

    fun save(context: Context, settings: PrayerSettings) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("method", settings.method.name)
            .putString("madhab", settings.madhab.name)
            .putFloat("lat", settings.latitude.toFloat())
            .putFloat("lng", settings.longitude.toFloat())
            .putString("cityId", settings.cityId)
            .putString("place", settings.placeName)
            .putString("highLatitude", settings.highLatitude.name)
            .putString("adjust", writeAdjustments(settings.adjustments))
            .apply()
    }
}

    /// ⚠️ المفتاح **اسم التعداد** لا ترتيبه: الترتيب يتغيّر إن
    /// أُعيد ترتيب الثوابت، والاسم يبقى.
    private fun writeAdjustments(map: Map<PrayerSlot, Int>): String =
        map.entries
            .filter { it.value != 0 }
            .joinToString(",") { "${it.key.name}:${PrayerAdjustments.clamp(it.value)}" }

    private fun readAdjustments(raw: String?): Map<PrayerSlot, Int> {
        if (raw.isNullOrBlank()) return emptyMap()
        return raw.split(',').mapNotNull { part ->
            val bits = part.split(':')
            val slot = bits.getOrNull(0)
                ?.let { name -> runCatching { PrayerSlot.valueOf(name) }.getOrNull() }
                ?: return@mapNotNull null
            val minutes = bits.getOrNull(1)?.toIntOrNull() ?: return@mapNotNull null
            slot to PrayerAdjustments.clamp(minutes)
        }.toMap()
    }
