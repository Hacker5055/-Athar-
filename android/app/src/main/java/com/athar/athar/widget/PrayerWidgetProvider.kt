package com.athar.athar.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.athar.athar.MainActivity
import com.athar.athar.R
import com.athar.athar.i18n.LocaleStore
import com.athar.athar.i18n.Strings
import com.athar.athar.prayer.PrayerSettingsStore
import com.athar.athar.prayer.PrayerSlot
import com.athar.athar.prayer.PrayerTimesCalculator
import java.time.Instant
import java.time.LocalDate

/// ودجت المواقيت على الشاشة الرئيسية.
///
/// ⚠️ **RemoteViews لا Glance**: Glance تبدو أحدث، لكنها تُبنى على
/// إصدار محدّد من Compose runtime، والتعارض بينها وبين نسختنا يظهر
/// `NoSuchMethodError` **على الجهاز لا في البناء** — أي أن البناء
/// يمرّ أخضر والودجت ينهار عند المستخدم. ولا مترجم هنا ولا جهاز،
/// فاخترنا الطريق الذي يُرى سلامته من الكود: اعتماد جديد صفر.
class PrayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val views = render(context)
        for (id in appWidgetIds) {
            appWidgetManager.updateAppWidget(id, views)
        }
    }

    companion object {

        /// ⚠️ تُستدعى من `MainActivity` عند كل فتح، لا انتظارًا
        /// لدورة النظام: المستخدم الذي فتح التطبيق يرى ودجتًا مطابقًا
        /// لما بالداخل، لا ودجتًا مضى عليه نصف ساعة.
        fun refresh(context: Context) {
            val manager = AppWidgetManager.getInstance(context) ?: return
            val ids = runCatching {
                manager.getAppWidgetIds(
                    android.content.ComponentName(context, PrayerWidgetProvider::class.java),
                )
            }.getOrDefault(intArrayOf())
            if (ids.isEmpty()) return
            val views = render(context)
            for (id in ids) {
                runCatching { manager.updateAppWidget(id, views) }
            }
        }

        private fun render(context: Context): RemoteViews {
            val s = Strings(LocaleStore.load(context))
            val views = RemoteViews(context.packageName, R.layout.widget_prayer)

            // ⚠️ الضغطة تفتح التطبيق: ودجت لا يفتح شيئًا ودجت ميت.
            // والـ FLAG_IMMUTABLE واجب على API 31+.
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            views.setOnClickPendingIntent(
                R.id.widget_root,
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                ),
            )

            val settings = PrayerSettingsStore.load(context)
            val today = LocalDate.now()
            val day = PrayerTimesCalculator.calculate(settings, today)
            val tomorrow = PrayerTimesCalculator.calculate(settings, today.plusDays(1))
            val now = Instant.now()

            views.setTextViewText(
                R.id.widget_place,
                settings.placeName.ifBlank { s.t("app_name") },
            )

            val next = PrayerWidgetPlan.nextOf(day, tomorrow, now)
            if (next == null) {
                // ⚠️ لا يحدث (الفجر دائمًا قادم)، لكن الودجت الفارغ
                // أسوأ من ودجت ناقص، والفرع يضمن نصًّا لا فراغًا.
                views.setTextViewText(R.id.widget_next_name, s.t("widget_no_data"))
                views.setTextViewText(R.id.widget_next_time, "")
                views.setTextViewText(R.id.widget_remaining, "")
                views.setTextViewText(R.id.widget_next_label, "")
            } else {
                val (slot, at) = next
                views.setTextViewText(R.id.widget_next_label, s.t("widget_next_prayer"))
                views.setTextViewText(R.id.widget_next_name, s.t(slot.stringKey))
                views.setTextViewText(R.id.widget_next_time, PrayerWidgetPlan.clock(at))
                views.setTextViewText(
                    R.id.widget_remaining,
                    "${s.t("widget_in")} ${PrayerWidgetPlan.remaining(now, at)}",
                )
            }

            fill(views, s, PrayerSlot.FAJR, day.fajr, R.id.widget_fajr_name, R.id.widget_fajr_time)
            fill(views, s, PrayerSlot.DHUHR, day.dhuhr, R.id.widget_dhuhr_name, R.id.widget_dhuhr_time)
            fill(views, s, PrayerSlot.ASR, day.asr, R.id.widget_asr_name, R.id.widget_asr_time)
            fill(
                views, s, PrayerSlot.MAGHRIB, day.maghrib,
                R.id.widget_maghrib_name, R.id.widget_maghrib_time,
            )
            fill(views, s, PrayerSlot.ISHA, day.isha, R.id.widget_isha_name, R.id.widget_isha_time)
            return views
        }

        private fun fill(
            views: RemoteViews,
            s: Strings,
            slot: PrayerSlot,
            at: Instant,
            nameId: Int,
            timeId: Int,
        ) {
            views.setTextViewText(nameId, s.t(slot.stringKey))
            views.setTextViewText(timeId, PrayerWidgetPlan.clock(at))
        }
    }
}
