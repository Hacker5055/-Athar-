package com.athar.athar.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/// قنوات الإشعارات — على API ٢٦+ لا يظهر إشعار بلا قناة.
///
/// ⚠️ لماذا ثلاث قنوات لا واحدة؟ لأن النظام يتيح للمستخدم كتم قناة
/// دون أخرى: من يريد إيقاف التذكيرات مع إبقاء الأذان يقدر على ذلك.
/// قناة واحدة تعني خيارًا واحدًا: كل شيء أو لا شيء.
object NotificationChannels {

    const val ATHAN = "athan"
    const val REMINDER = "reminder"
    const val JUMUAH = "jumuah"

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return

        manager.createNotificationChannel(
            NotificationChannel(ATHAN, "الأذان", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "تنبيه عند دخول وقت الصلاة"
            },
        )
        manager.createNotificationChannel(
            NotificationChannel(REMINDER, "التذكير قبل الصلاة", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "تنبيه قبل وقت الصلاة بدقائق"
            },
        )
        manager.createNotificationChannel(
            NotificationChannel(JUMUAH, "تذكير الجمعة", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "سورة الكهف · الصلاة على النبي · الغُسل"
            },
        )
    }

    /// ⚠️ القناة تُحدَّد من نوع التنبيه لا من الصلاة — نوع التنبيه هو
    /// ما يريد المستخدم التحكّم فيه منفصلًا.
    fun forKind(kind: AlertKind): String = when (kind) {
        AlertKind.ATHAN -> ATHAN
        AlertKind.REMINDER -> REMINDER
        AlertKind.JUMUAH -> JUMUAH
    }
}
