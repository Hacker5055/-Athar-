package com.athar.athar.notification

import com.athar.athar.prayer.PrayerSlot

/// وضع التنبيه لكل صلاة — كل صلاة مستقلة عن الباقي (docs/02 · A8).
enum class PrayerAlertMode {
    /// أذان كامل — ⚠️ يسقط تلقائيًا إلى نغمة النظام ما لم يُرفق الملف
    ADHAN,

    /// أختصر الأذان (أوله فقط)
    SHORT_ADHAN,

    /// نغمة تنبيه قصيرة
    BEEP,

    /// اهتزاز فقط — بلا صوت
    VIBRATE,

    /// إشعار صامت على الشاشة فقط
    SILENT,

    /// لا إشعار إطلاقًا
    OFF,
    ;

    val isEnabled: Boolean get() = this != OFF
}

/// إعدادات الإشعارات — تُحفظ تحت `athar.notifications.v1`.
data class NotificationSettings(
    /// مفتاح رئيسي — يوقف الكل دفعة واحدة (سفر، صمت)
    val masterEnabled: Boolean = true,
    val modes: Map<PrayerSlot, PrayerAlertMode> = emptyMap(),
    val reminderMinutes: Map<PrayerSlot, Int> = emptyMap(),
    /// تذكير الجمعة: الكهف · الصلاة على النبي · الغُسل (docs/02 · A13)
    val jumuahReminder: Boolean = true,
    val jumuahMinutesBefore: Int = 90,
) {
    companion object {
        val REMINDER_CHOICES = listOf(0, 5, 10, 15, 20, 30)
        const val JUMUAH_MIN = 30
        const val JUMUAH_MAX = 240
    }

    /// ⚠️ الشروق **ليس صلاة** فلا أذان له — حتى لو طلبه المستخدم
    /// صراحةً. القيد هنا لا في الواجهة، حتى يستحيل تجاوزه.
    fun modeFor(slot: PrayerSlot): PrayerAlertMode =
        if (slot == PrayerSlot.SUNRISE) {
            PrayerAlertMode.OFF
        } else {
            modes[slot] ?: PrayerAlertMode.ADHAN
        }

    fun reminderFor(slot: PrayerSlot): Int = reminderMinutes[slot] ?: 0

    fun withMode(slot: PrayerSlot, mode: PrayerAlertMode): NotificationSettings =
        copy(modes = modes + (slot to mode))

    /// ⚠️ قيمة ليست من الخيارات تُعتبر صفرًا: نفضّل «بلا تذكير» على
    /// تذكير يظهر قبل الفجر بسبع دقائق لأن المستخدم كتب 7
    fun withReminder(slot: PrayerSlot, minutes: Int): NotificationSettings {
        val clamped = if (minutes in REMINDER_CHOICES) minutes else 0
        return copy(
            reminderMinutes = if (clamped == 0) {
                reminderMinutes - slot
            } else {
                reminderMinutes + (slot to clamped)
            },
        )
    }
}
