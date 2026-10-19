package com.athar.athar.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.athar.athar.R

/// يستقبل تنبيهًا مجدولًا ويعرضه.
///
/// ⚠️ لماذا نعيد الجدولة بعد كل تنبيه؟
/// لأن `AlarmManager` ينسى كل شيء بعد إعادة التشغيل، ولا يكرّر
/// التنبيه الأسبوعي بنفسه (`setRepeating` غير دقيق ومهمَل). الجدولة
/// الذاتية تجعل السلسلة تتجدّد ما دام الهاتف يُستخدم — ومع مستقبل
/// الإقلاع تكتمل الصورة.
class AlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(EXTRA_ID, -1)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        if (id < 0) return

        NotificationChannels.ensure(context)

        val channel = intent.getStringExtra(EXTRA_CHANNEL) ?: NotificationChannels.ATHAN
        val body = intent.getStringExtra(EXTRA_BODY).orEmpty()

        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.ic_stat_athan)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        runCatching {
            NotificationManagerCompat.from(context).notify(id, notification)
        }
        // ⚠️ إذن الإشعارات قد يُسحب في أي وقت بعد منحه، فيلقي
        // SecurityException — والانهيار هنا أسوأ من إشعار فائت

        NotificationScheduler.rescheduleFromStored(context)
    }

    companion object {
        const val EXTRA_ID = "athar.alert.id"
        const val EXTRA_TITLE = "athar.alert.title"
        const val EXTRA_BODY = "athar.alert.body"
        const val EXTRA_CHANNEL = "athar.alert.channel"
    }
}
