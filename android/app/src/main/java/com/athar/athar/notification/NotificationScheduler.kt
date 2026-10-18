package com.athar.athar.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.athar.athar.i18n.LocaleStore
import com.athar.athar.i18n.Strings
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

    fun rescheduleFromStored(context: Context, strings: Strings? = null) {
        reschedule(
            context,
            PrayerSettingsStore.load(context),
            NotificationSettingsStore.load(context),
            // ⚠️ نصّ الإشعار يُبنى **مرة واحدة** وقت الجدولة ويُخزَّن في
            // النظام، فلا يكفي أن نقرأ اللغة وقت العرض. المستقبلات
            // (إقلاع، تنبيه) لا تملك `Strings` فتقرؤها من المخزن.
            strings ?: Strings(LocaleStore.load(context)),
        )
    }

    fun reschedule(
        context: Context,
        prayerSettings: PrayerSettings,
        settings: NotificationSettings,
        strings: Strings = Strings(LocaleStore.load(context)),
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

        val placeName = prayerSettings.cityId
            ?.let { id -> com.athar.athar.prayer.Cities.all.firstOrNull { it.id == id } }
            ?.nameFor(strings.locale)
            ?.takeIf { it.isNotBlank() }
            ?: prayerSettings.placeName

        for (alert in NotificationPlanner.plan(days, settings, now)) {
            schedule(manager, context, alert, strings, placeName)
        }
    }

    /// ⚠️ زر «اختبر» — لأن جدولة تعمل في الخلفية ولا تظهر في شاشة لا
    /// يمكن للمستخدم أن يتأكد من عملها إلا بأن تصله. بلا هذا الزر
    /// يبقى كل ما سبق ادّعاءً.
    fun scheduleTest(context: Context, strings: Strings? = null) {
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
            strings ?: Strings(LocaleStore.load(context)),
        )
    }

    private fun schedule(
        manager: AlarmManager,
        context: Context,
        alert: ScheduledAlert,
        strings: Strings,
        placeName: String = "",
    ) {
        val intent = alertIntent(context, alert, strings, placeName)
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

    private fun alertIntent(
        context: Context,
        alert: ScheduledAlert,
        strings: Strings,
        placeName: String,
    ): Intent =
        Intent(context, AlertReceiver::class.java)
            .putExtra(AlertReceiver.EXTRA_ID, alert.id)
            .putExtra(AlertReceiver.EXTRA_TITLE, alert.title(strings))
            .putExtra(AlertReceiver.EXTRA_BODY, alert.body(strings, placeName))
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

private fun ScheduledAlert.prayerName(strings: Strings): String =
    prayer?.let { strings.t(it.stringKey) } ?: ""

/// ⚠️ نصوص التنبيه من حزمة الترجمة لا م硬كودة — وإلا بقي الأذان
/// بالعربية داخل تطبيق فرنسي بالكامل.
private fun ScheduledAlert.title(strings: Strings): String = when (kind) {
    AlertKind.ATHAN -> strings.tArgs("notif_athan_title", "prayer" to prayerName(strings))
    AlertKind.REMINDER -> strings.tArgs("notif_reminder_title", "prayer" to prayerName(strings))
    AlertKind.JUMUAH -> strings.notifJumuahTitle
}

private fun ScheduledAlert.body(strings: Strings, placeName: String): String = when (kind) {
    AlertKind.JUMUAH -> strings.notifJumuahBody
    AlertKind.REMINDER -> strings.tArgs("notif_reminder_body", "n" to reminderMinutes)
    AlertKind.ATHAN -> {
        val time = TIME_FORMAT.format(at)
        strings.tArgs("notif_athan_body", "time" to time, "place" to placeName)
            .trim(' ', '—')
    }
}

private val TIME_FORMAT: java.time.format.DateTimeFormatter =
    java.time.format.DateTimeFormatter
        .ofPattern("HH:mm")
        .withZone(java.time.ZoneId.systemDefault())
