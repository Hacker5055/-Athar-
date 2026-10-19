package com.athar.athar

import android.content.Context
import android.widget.RemoteViews
import es.antonborri.home_widget.HomeWidgetProvider

/**
 * ودجت أثر على الشاشة الرئيسية.
 *
 * ⚠️ هذه نسخة «الكود الأصلي» — `home_widget` لا يرسم الودجت، هو فقط
 * يوفّر قناة البيانات (SharedPreferences) + طلب التحديث.
 *
 * البيانات يكتبها `HomeWidgetService` في Dart بنفس المفاتيح الموجودة
 * في `lib/features/prayer/data/home_widget_service.dart`:
 *   next_name · next_time · next_in · times · place · updated_at
 *
 * الشكل: `res/layout/athar_widget.xml` — صفّان (الصلاة القادمة + شبكة مختصرة).
 */
class AtharWidgetProvider : HomeWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray,
        widgetData: android.content.SharedPreferences,
    ) {
        appWidgetIds.forEach { widgetId ->
            val views = RemoteViews(context.packageName, R.layout.athar_widget).apply {

                // ── الصلاة القادمة ──
                val nextName = widgetData.getString("next_name", null) ?: "—"
                val nextTime = widgetData.getString("next_time", null) ?: ""
                val nextIn = widgetData.getString("next_in", null) ?: ""

                setTextViewText(R.id.widget_next_name, nextName)
                setTextViewText(R.id.widget_next_time, nextTime)
                setTextViewText(R.id.widget_next_in, nextIn)

                // ── المكان ──
                val place = widgetData.getString("place", null) ?: ""
                setTextViewText(R.id.widget_place, place)

                // ── المواقيت (سطر واحد: fajr|05:32;dhuhr|12:40;…) ──
                val times = widgetData.getString("times", null)
                if (times.isNullOrEmpty()) {
                    setTextViewText(R.id.widget_times, "")
                } else {
                    // نعرض أول أربعة أوقات فقط — الودجت صغير.
                    val parts = times.split(";")
                    val shown = parts.take(4).joinToString("   ") { part ->
                        val bits = part.split("|")
                        if (bits.size == 2) "${bits[0]} ${bits[1]}" else part
                    }
                    setTextViewText(R.id.widget_times, shown)
                }
            }

            // فتح التطبيق عند النقر على الودجت
            val intent = context.packageManager
                .getLaunchIntentForPackage(context.packageName)
                ?.apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                        android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            if (intent != null) {
                views.setOnClickPendingIntent(
                    R.id.widget_root,
                    android.app.PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                            android.app.PendingIntent.FLAG_IMMUTABLE,
                    ),
                )
            }

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}
