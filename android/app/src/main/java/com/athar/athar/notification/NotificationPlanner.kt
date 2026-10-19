package com.athar.athar.notification

import com.athar.athar.prayer.PrayerDay
import com.athar.athar.prayer.PrayerSlot
import java.time.DayOfWeek
import java.time.Instant
import java.time.temporal.ChronoUnit

/// تنبيه مجدول — **متى** فقط.
///
/// ⚠️ فصلنا «متى» (هنا، محض وقابل للاختبار بالكامل) عن «ماذا» (نصّ
/// الإشعار وصوته، يبنيهما المجدول وقت التنفيذ). الفصل هو ما يجعل
/// أصعب ما في الميزة — المنطق الزمني — قابلًا للاختبار على JVM.
data class ScheduledAlert(
    val id: Int,
    val kind: AlertKind,
    val at: Instant,
    val prayer: PrayerSlot?,
    val weekly: Boolean = false,
    val reminderMinutes: Int = 0,
) {
    val isReminder: Boolean get() = kind == AlertKind.REMINDER
}

/// يبني خطة الجدولة من مواقيت الأيام القادمة + إعدادات المستخدم.
///
/// **محض تمامًا** — لا أندرويد ولا منصّة.
object NotificationPlanner {

    /// أسبوع كامل يكفي لأن إعادة الجدولة تحصل عند فتح التطبيق وعند أي
    /// تغيير في الإعدادات، وفي الوقت نفسه يُبقي عدد الإشعارات المعلّقة
    /// صغيرًا (النظام يحدّها بـ ٥٠٠).
    const val DEFAULT_AHEAD_DAYS = 7

    fun plan(
        days: List<PrayerDay>,
        settings: NotificationSettings,
        now: Instant,
    ): List<ScheduledAlert> {
        if (!settings.masterEnabled) return emptyList()

        val alerts = mutableListOf<ScheduledAlert>()

        for (day in days) {
            for ((slot, at) in day.ordered) {
                val mode = settings.modeFor(slot)

                // ١) أذان الصلاة — نتخطّى ما فات وقته
                if (mode.isEnabled && !at.isBefore(now)) {
                    alerts += ScheduledAlert(
                        id = AlertId.encode(AlertKind.ATHAN, day.date, slot),
                        kind = AlertKind.ATHAN,
                        at = at,
                        prayer = slot,
                    )
                }

                // ٢) تذكير قبلها — لا تذكير لصلاة أوقف المستخدم أذانها
                val reminder = settings.reminderFor(slot)
                if (reminder > 0 && mode.isEnabled) {
                    val reminderAt = at.minus(reminder.toLong(), ChronoUnit.MINUTES)
                    if (!reminderAt.isBefore(now)) {
                        alerts += ScheduledAlert(
                            id = AlertId.encode(AlertKind.REMINDER, day.date, slot),
                            kind = AlertKind.REMINDER,
                            at = reminderAt,
                            prayer = slot,
                            reminderMinutes = reminder,
                        )
                    }
                }
            }
        }

        // ⚠️ الأذكار **مرتبطة بمواقيت الصلاة لا بساعة ثابتة**: تفصيله
        // في تعليق adhkarAlert.
        alerts += adhkarAlerts(
            days, now, AlertKind.MORNING_ADHKAR,
            base = { it.fajr },
            ceiling = { it.sunrise },
            enabled = settings.morningAdhkar,
            offset = settings.morningAdhkarOffset,
        )

        alerts += adhkarAlerts(
            days, now, AlertKind.EVENING_ADHKAR,
            base = { it.asr },
            ceiling = { it.maghrib },
            enabled = settings.eveningAdhkar,
            offset = settings.eveningAdhkarOffset,
        )

        jumuahAlert(days, settings, now)?.let { alerts += it }
        return alerts
    }

    /// تذكير الأذكار — يوم واحد داخل النطاق، يومه الأول غير الفائت.
    ///
    /// ⚠️ **لماذا مرتبط بالفجر/العصر لا بساعة ثابتة؟** لأن باب أذكار
    /// الصباح من طلوع الفجر إلى طلوع الشمس، وباب المساء من العصر إلى
    /// المغرب. ساعة ثابتة (٦:٠٠ مثلًا) تُذكّر **قبل** الفجر صيفًا
    /// و**بعد** الشروق شتاءً — أي خارج الباب في الحالين.
    ///
    /// ⚠️ و[ceiling] يمنع تجاوز الباب: تذكير بعد الشروق ليس «صباحًا»،
    /// فنُقيده بسقف الباب بدل أن نلغيه — لأن التذكير المتأخر خير من
    /// تذكير لا يأتي.
    ///
    /// ⚠️ **منبّه لكل يوم، لا منبّه واحد**: الأذكار يومية، فمن يعود
    /// بعد أول يوم يُذكَّر مرة واحدة ثم لا شيء طول الأسبوع — وهو بالضبط
    /// ما فعله الإصدار الأول من هذه الدالة (كانت تُرجع أول يوم وتخرج).
    private fun adhkarAlerts(
        days: List<PrayerDay>,
        now: Instant,
        kind: AlertKind,
        base: (PrayerDay) -> Instant,
        ceiling: (PrayerDay) -> Instant,
        enabled: Boolean,
        offset: Int,
    ): List<ScheduledAlert> {
        if (!enabled) return emptyList()
        val out = mutableListOf<ScheduledAlert>()
        for (day in days) {
            var at = base(day).plus(offset.toLong(), ChronoUnit.MINUTES)
            val limit = ceiling(day)
            if (at.isAfter(limit)) at = limit
            if (at.isBefore(now)) continue
            out += ScheduledAlert(
                id = AlertId.encode(kind, day.date, null),
                kind = kind,
                at = at,
                prayer = null,
            )
        }
        return out
    }

    /// أول جمعة قادمة داخل النطاق.
    private fun jumuahAlert(
        days: List<PrayerDay>,
        settings: NotificationSettings,
        now: Instant,
    ): ScheduledAlert? {
        if (!settings.jumuahReminder) return null

        for (day in days) {
            if (day.date.dayOfWeek != DayOfWeek.FRIDAY) continue

            var at = day.dhuhr.minus(settings.jumuahMinutesBefore.toLong(), ChronoUnit.MINUTES)
            // ⚠️ لو فات معادها، التكرار الأسبوعي سيوافق الجمعة القادمة
            if (at.isBefore(now)) {
                at = at.plus(7, ChronoUnit.DAYS)
            }

            return ScheduledAlert(
                id = AlertId.encode(AlertKind.JUMUAH, day.date, PrayerSlot.DHUHR),
                kind = AlertKind.JUMUAH,
                at = at,
                prayer = PrayerSlot.DHUHR,
                weekly = true,
            )
        }
        return null
    }
}
