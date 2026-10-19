package com.athar.athar.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.athar.athar.prayer.PrayerSettingsStore
import com.athar.athar.prayer.PrayerSlot
import com.athar.athar.prayer.PrayerTimesCalculator
import java.time.Instant
import java.time.LocalDate

/// يحوّل نوافذ الصمت إلى منبّهاتٍ في النظام.
///
/// ⚠️ **ترجمةٌ فقط**: كلُّ القرار الزمني في `SilencePlanner` (محض
/// ومُختبَر). وهنا لا منطقَ زمنيَّ البتّة.
object SilenceScheduler {

    fun reschedule(context: Context) {
        cancel(context)

        val settings = SilenceStore.load(context)
        if (!settings.enabled) return
        // ⚠️ بلا إذنِ «عدم الإزعاج» لا جدولةَ نافذة: منبّهٌ يُطلق
        // صمتًا لا يستطيع التطبيقُ إيقافه أخطأُ من صمتٍ لا يقع.
        if (!SilenceController.hasAccess(context)) return

        val manager = context.getSystemService(AlarmManager::class.java) ?: return
        val prayerSettings = PrayerSettingsStore.load(context)
        val today = LocalDate.now()
        val days = (0 until SilencePlanner.AHEAD_DAYS).map { offset ->
            PrayerTimesCalculator.calculate(
                prayerSettings,
                today.plusDays(offset.toLong()),
            )
        }

        for (window in SilencePlanner.plan(days, settings, Instant.now())) {
            schedule(
                manager,
                context,
                window.start,
                SilenceReceiver.ACTION_ON,
                onId(window),
            )
            schedule(
                manager,
                context,
                window.end,
                SilenceReceiver.ACTION_OFF,
                offId(window),
            )
        }
    }

    private fun schedule(
        manager: AlarmManager,
        context: Context,
        at: Instant,
        action: String,
        id: Int,
    ) {
        val pending = pendingFor(context, id, action)
        val trigger = at.toEpochMilli()
        if (canScheduleExact(manager)) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
        } else {
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
        }
    }

    private fun canScheduleExact(manager: AlarmManager): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            manager.canScheduleExactAlarms()
        } else {
            true
        }

    /// ⚠️ المعرّفاتُ **حتمية**: نفسُ النافذة = نفسُ المعرّف دائمًا،
    /// فيلغي الجدولةُ القديمةَ نفسَها بلا قائمةٍ في الذاكرة.
    private fun onId(window: SilenceWindow): Int = baseId(window) * 2
    private fun offId(window: SilenceWindow): Int = baseId(window) * 2 + 1

    private fun baseId(window: SilenceWindow): Int =
        (window.date.toEpochDay().toInt() * 10 + window.prayer.ordinal)

    fun cancel(context: Context) {
        val manager = context.getSystemService(AlarmManager::class.java) ?: return
        val today = LocalDate.now()
        for (offset in -1..SilencePlanner.AHEAD_DAYS) {
            val date = today.plusDays(offset.toLong())
            for (slot in PrayerSlot.entries) {
                val base = (date.toEpochDay().toInt() * 10 + slot.ordinal) * 2
                manager.cancel(pendingFor(context, base, SilenceReceiver.ACTION_ON))
                manager.cancel(pendingFor(context, base + 1, SilenceReceiver.ACTION_OFF))
            }
        }
    }

    /// ⚠️ **نفسُ الـ Intent** يلزم للإلغاء: intentٌ بفعلٍ مختلف
    /// غيرُه، فيلغي `PendingIntent` غيرَ الذي ضُبط.
    private fun pendingFor(context: Context, id: Int, action: String): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            id,
            Intent(context, SilenceReceiver::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}
