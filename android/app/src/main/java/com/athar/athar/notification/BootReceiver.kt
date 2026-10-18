package com.athar.athar.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/// ⚠️ `AlarmManager` ينسى كل الجدولة بعد إعادة التشغيل. بلا هذا
/// المستقبل يصمت التطبيق تمامًا بعد أول إعادة تشغيل — عطل لا يظهر
/// في الاختبارات، بل صباح يوم ينتظر فيه المستخدم أذان الفجر.
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        NotificationScheduler.rescheduleFromStored(context)
    }
}
