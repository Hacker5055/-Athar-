package com.athar.athar.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.athar.athar.i18n.LocaleStore
import com.athar.athar.i18n.Strings

/// قنوات الإشعارات — على API ٢٦+ لا يظهر إشعار بلا قناة.
///
/// ⚠️ لماذا ثلاث قنوات لا واحدة؟ لأن النظام يتيح للمستخدم كتم قناة
/// دون أخرى: من يريد إيقاف التذكيرات مع إبقاء الأذان يقدر على ذلك.
/// قناة واحدة تعني خيارًا واحدًا: كل شيء أو لا شيء.
object NotificationChannels {

    const val ATHAN = "athan"
    const val REMINDER = "reminder"
    const val JUMUAH = "jumuah"
    const val ADHKAR = "adhkar"
    /// ⚠️ التلاوة قناة منفصلة: هي إشعار **جارٍ** لا تنبيه، ومن يكتم
    /// التنبيهات لا يعني أنه لا يريد رؤية ما يعمل الآن.
    const val PLAYBACK = "playback"

    /// ⚠️ أسماء القنوات **مترجمة**: تظهر في إعدادات النظام نفسها، فعربي
    /// ثابت يعني أن مستخدم الفرنسية يرى «الأذان» في قائمة بلغته. وكانت
    /// المفاتيح معرَّفة في الحزم من قبل **ولا يستعملها أحد** — عكس خطأ
    /// `s.sunriseNoAdhan` (مفتاح مُخترع)، فهنا مفتاح موجود ومُهمَل.
    ///
    /// ⚠️ و`strings` له قيمة افتراضية لأن `AlertReceiver` يستقبل البثّ
    /// بلا `Strings`، فيقرأ اللغة من المخزن كما يفعل المجدول.
    fun ensure(context: Context, strings: Strings = Strings(LocaleStore.load(context))) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return

        create(
            manager, ATHAN, strings.notifChannelAthan, strings.notifChannelAthanDesc,
            NotificationManager.IMPORTANCE_HIGH,
        )
        create(
            manager, REMINDER, strings.notifChannelReminder, strings.notifChannelReminderDesc,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        create(
            manager, JUMUAH, strings.notifChannelJumuah, strings.notifChannelJumuahDesc,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        create(
            manager, ADHKAR, strings.notifChannelAdhkar, strings.notifChannelAdhkarDesc,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        // ⚠️ IMPORTANCE_LOW لا DEFAULT: إشعار التشغيل الجاري يظهر في
        // الشريط بلا صوت ولا اهتزاز — لو كان DEFAULT لرنّ مع كل آية
        // تبدأ في سورة تُشغَّل آية آية.
        create(
            manager, PLAYBACK, strings.notifChannelPlayback,
            strings.notifChannelPlaybackDesc, NotificationManager.IMPORTANCE_LOW,
        )
    }

    private fun create(
        manager: NotificationManager,
        id: String,
        name: String,
        description: String,
        importance: Int,
    ) {
        manager.createNotificationChannel(
            NotificationChannel(id, name, importance).apply { this.description = description },
        )
    }

    /// ⚠️ القناة تُحدَّد من نوع التنبيه لا من الصلاة — نوع التنبيه هو
    /// ما يريد المستخدم التحكّم فيه منفصلًا.
    fun forKind(kind: AlertKind): String = when (kind) {
        AlertKind.ATHAN -> ATHAN
        AlertKind.REMINDER -> REMINDER
        AlertKind.JUMUAH -> JUMUAH
        AlertKind.MORNING_ADHKAR, AlertKind.EVENING_ADHKAR -> ADHKAR
    }
}
