package com.athar.athar.notification

import android.content.Context
import androidx.core.content.edit
import com.athar.athar.prayer.PrayerSlot
import org.json.JSONObject

object NotificationSettingsStore {

    private const val PREFS_NAME = "athar.notifications.v1"
    private const val KEY = "settings"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(context: Context): NotificationSettings {
        val raw = prefs(context).getString(KEY, null) ?: return NotificationSettings()
        // ⚠️ تلف التخزين ليس سببًا لانهيار التطبيق — نرجع للافتراضي
        return runCatching { decode(raw) }.getOrDefault(NotificationSettings())
    }

    fun save(context: Context, settings: NotificationSettings) {
        prefs(context).edit { putString(KEY, encode(settings)) }
    }

    fun encode(settings: NotificationSettings): String = JSONObject().apply {
        put("on", settings.masterEnabled)
        put("modes", JSONObject().apply {
            settings.modes.forEach { (slot, mode) -> put(slot.name, mode.name) }
        })
        put("rem", JSONObject().apply {
            settings.reminderMinutes.forEach { (slot, minutes) ->
                if (minutes > 0) put(slot.name, minutes)
            }
        })
        put("jum", settings.jumuahReminder)
        put("jumMin", settings.jumuahMinutesBefore)
        put("mor", settings.morningAdhkar)
        put("morOff", settings.morningAdhkarOffset)
        put("eve", settings.eveningAdhkar)
        put("eveOff", settings.eveningAdhkarOffset)
    }.toString()

    /// ⚠️ قيمة محفوظة خارج الخيارات تُستبدل بالافتراضي لا تُحذف: ملف
    /// كتبه إصدار أقدم لا يجوز أن يُوقف التذكير بصمت.
    private fun adhkarOffset(root: JSONObject, key: String): Int {
        val value = root.optInt(key, NotificationSettings.DEFAULT_ADHKAR_OFFSET)
        return if (value in NotificationSettings.ADHKAR_OFFSET_CHOICES) {
            value
        } else {
            NotificationSettings.DEFAULT_ADHKAR_OFFSET
        }
    }

    fun decode(raw: String): NotificationSettings {
        val root = JSONObject(raw)

        val modes = mutableMapOf<PrayerSlot, PrayerAlertMode>()
        root.optJSONObject("modes")?.let { node ->
            val keys = node.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val slot = runCatching { PrayerSlot.valueOf(key) }.getOrNull() ?: continue
                val mode = runCatching {
                    PrayerAlertMode.valueOf(node.optString(key))
                }.getOrNull() ?: continue
                modes[slot] = mode
            }
        }

        val reminders = mutableMapOf<PrayerSlot, Int>()
        root.optJSONObject("rem")?.let { node ->
            val keys = node.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val slot = runCatching { PrayerSlot.valueOf(key) }.getOrNull() ?: continue
                val minutes = node.optInt(key, 0)
                if (minutes in NotificationSettings.REMINDER_CHOICES) {
                    reminders[slot] = minutes
                }
            }
        }

        return NotificationSettings(
            masterEnabled = root.optBoolean("on", true),
            modes = modes,
            reminderMinutes = reminders,
            jumuahReminder = root.optBoolean("jum", true),
            jumuahMinutesBefore = root.optInt("jumMin", 90)
                .coerceIn(NotificationSettings.JUMUAH_MIN, NotificationSettings.JUMUAH_MAX),
            morningAdhkar = root.optBoolean("mor", true),
            morningAdhkarOffset = adhkarOffset(root, "morOff"),
            eveningAdhkar = root.optBoolean("eve", true),
            eveningAdhkarOffset = adhkarOffset(root, "eveOff"),
        )
    }
}
