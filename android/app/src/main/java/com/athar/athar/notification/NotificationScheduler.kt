package com.athar.athar.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.athar.athar.prayer.PrayerSettings
import com.athar.athar.prayer.PrayerSettingsStore
import com.athar.athar.prayer.PrayerSlot
import com.athar.athar.prayer.PrayerTimesCalculator
import java.time.Instant
import java.time.LocalDate

/// يحوّل خطة المنظّم إلى منبّهات فعلية في النظام.
///
/// ⚠️ كل المنطق الزمني في `NotificationPlanner` (محض ومُختبَر). هنا
/// **ترجمة فقط** إلى `AlarmManager` — لا قرار زمني واحد.
object NotificationScheduler {

    fun rescheduleFromStored(context: Context) {
        reschedule(
            context,
            PrayerSettingsStore.load(context),
            NotificationSettingsStore.load(context),
        )
    }

    fun reschedule(
        context: Context,
        prayerSettings: PrayerSettings,
        settings: NotificationSettings,
    ) {
        NotificationChannels.ensure(context)
        cancelAll(context)

        // ⚠️ الإذن قد يُسحب بعد منحه. الجدولة بلا إذن تعطي منبّهات
        // تصمت بلا أثر — أسوأ من لا شيء، لأنها توهم بالعمل.
        if (!settings.masterEnabled) return
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

        val manager = context.getSystemService(AlarmManager::class.java) ?: return
        val now = Instant.now()
        val today = LocalDate.now()
        val days = (0 until NotificationPlanner.DEFAULT_AHEAD_DAYS).map { offset ->
            PrayerTimesCalculator.calculate(prayerSettings, today.plusDays(offset.toLong()))
        }

        for (alert in NotificationPlanner.plan(days, settings, now)) {
            schedule(manager, context, alert)
        }
    }

    /// ⚠️ زر «اختبر» — لأن جدولة تعمل في الخلفية ولا تظهر في شاشة لا
    /// يمكن للمستخدم أن يتأكد من عملها إلا بأن تصله. بلا هذا الزر
    /// يبقى كل ما سبق ادّعاءً.
    fun scheduleTest(context: Context) {
        NotificationChannels.ensure(context)
        val manager = context.getSystemService(AlarmManager::class.java) ?: return
        schedule(
            manager,
            context,
            ScheduledAlert(
                id = 999_999,
                kind = AlertKind.ATHAN,
                at = Instant.now().plusSeconds(5),
                prayer = null,
            ),
        )
    }

    private fun schedule(manager: AlarmManager, context: Context, alert: ScheduledAlert) {
        val intent = alertIntent(context, alert)
        val pending = PendingIntent.getBroadcast(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val trigger = alert.at.toEpochMilli()
        if (canScheduleExact(manager)) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
        } else {
            // ⚠️ المستخدم سحب إذن التنبيه الدقيق: منبّه متأخر قليلًا
            // أفضل بكثير من لا منبّه
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
        }
    }

    private fun canScheduleExact(manager: AlarmManager): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            manager.canScheduleExactAlarms()
        } else {
            true
        }

    private fun alertIntent(context: Context, alert: ScheduledAlert): Intent =
        Intent(context, AlertReceiver::class.java)
            .putExtra(AlertReceiver.EXTRA_ID, alert.id)
            .putExtra(AlertReceiver.EXTRA_TITLE, alert.title())
            .putExtra(AlertReceiver.EXTRA_BODY, alert.body())
            .putExtra(AlertReceiver.EXTRA_CHANNEL, NotificationChannels.forKind(alert.kind))

    /// ⚠️ نُخبر النظام بكل ما عسانا نكون جدولناه — المعرّفات حتمية،
    /// فنبنيها من الأيام الماضية والقادمة بدل الاحتفاظ بقائمة.
    fun cancelAll(context: Context) {
        val manager = context.getSystemService(AlarmManager::class.java) ?: return
        val today = LocalDate.now()
        for (offset in -1..NotificationPlanner.DEFAULT_AHEAD_DAYS) {
            val date = today.plusDays(offset.toLong())
            for (kind in AlertKind.entries) {
                for (slot in PrayerSlot.entries) {
                    val id = AlertId.encode(kind, date, slot)
                    manager.cancel(pendingIntentFor(context, id))
                }
            }
        }
    }

    private fun pendingIntentFor(context: Context, id: Int): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            id,
            Intent(context, AlertReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}

private fun ScheduledAlert.title(): String = when (kind) {
    AlertKind.ATHAN -> "حان وقت ${prayer?.arabicName ?: "الصلاة"}"
    AlertKind.REMINDER -> "بقي $reminderMinutes دقيقة على ${prayer?.arabicName ?: "الصلاة"}"
    AlertKind.JUMUAH -> "الجمعة — سورة الكهف والصلاة على النبي"
}

private fun ScheduledAlert.body(): String = when (kind) {
    AlertKind.ATHAN -> "حيّ على الصلاة"
    AlertKind.REMINDER -> "تهيّأ للصلاة"
    AlertKind.JUMUAH -> "الغُسل، والكهف، والإكثار من الصلاة على النبي ﷺ"
}
