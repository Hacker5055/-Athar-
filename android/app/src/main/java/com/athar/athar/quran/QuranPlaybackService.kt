package com.athar.athar.quran

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.athar.athar.MainActivity
import com.athar.athar.R
import com.athar.athar.i18n.LocaleStore
import com.athar.athar.i18n.Strings
import com.athar.athar.notification.NotificationChannels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/// خدمة تشغيل التلاوة في الخلفية.
///
/// ⚠️ لماذا خدمة مقدَّمة: بلاها يقطع النظام التلاوة حين يخرج التطبيق
/// من الواجهة، ومن يفتح القرآن ليسمعه وهو يقود أو يعمل لا يريده أن
/// يصمت عند أول إشعار. والمقدَّمة وحدها تفرض إشعارًا ظاهرًا — وهو
/// حقّ للمستخدم: ما يعمل في الخلفية يجب أن يُرى.
///
/// ⚠️ الخدمة لا تملك الصوت: [QuranAudioController] يملكه. فلو تعذّرت
/// الخدمة (إذن إشعار مرفوض، نوع خدمة غير مدعوم) بقي التشغيل داخل
/// التطبيق يعمل ولم يمت كله.
class QuranPlaybackService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var title: String = ""

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.ensure(this)
        // ⚠️ الإشعار يتبع الحالة لا يثبت على صورة واحدة: زرّ «إيقاف»
        // على شيء يعمل أصلًا يكذب على المستخدم، وصورة الإشعار يجب أن
        // تتغيّر مع كل آية في سورة تُشغَّل آية آية.
        scope.launch {
            QuranAudioController.state.distinctUntilChanged().collect { state ->
                if (state.isActive) {
                    show(state)
                } else {
                    stopSelfGracefully()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val incoming = intent?.getStringExtra(EXTRA_TITLE)
        if (!incoming.isNullOrBlank()) title = incoming

        when (intent?.action) {
            ACTION_STOP -> {
                QuranAudioController.stop()
                stopSelfGracefully()
                return START_NOT_STICKY
            }
            ACTION_TOGGLE -> QuranAudioController.toggleLast()
        }

        // ⚠️ فوريًّا قبل أيّ شيء آخر: النظام يمنح خمس ثوانٍ فقط بين
        // startForegroundService و startForeground، وتجاوزها يقتل
        // التطبيق. فلا ننتظر تحميل الرابط ولا نجزّئ الأمر.
        startForegroundCompat(QuranAudioController.state.value)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun startForegroundCompat(state: QuranAudioState) {
        val notification = build(state)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            // ⚠️ لا نُسقط التطبيق: إن رفض النظام الخدمة المقدَّمة
            // (نوع غير مصرّح، أو قيد على الجهاز) يبقى التشغيل داخل
            // الشاشة يعمل ويصمت عند الخروج — وهو أقلّ من أن يموت
            // التطبيق كلّه عند الضغط على «اسمع».
            stopSelf()
        }
    }

    private fun show(state: QuranAudioState) {
        val manager = getSystemService(NotificationManager::class.java) ?: return
        manager.notify(NOTIFICATION_ID, build(state))
    }

    private fun build(state: QuranAudioState): android.app.Notification {
        val strings = Strings(LocaleStore.load(this))
        val playing = state is QuranAudioState.Playing

        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val toggleIntent = pending(ACTION_TOGGLE, 1)
        val stopIntent = pending(ACTION_STOP, 2)

        val subtitle = when (state) {
            is QuranAudioState.Loading -> strings.quranAudioLoading
            is QuranAudioState.Playing -> ayahLabel(state.ayah, strings)
            is QuranAudioState.Paused -> strings.quranResume
            is QuranAudioState.Error -> strings.quranAudioError
            else -> ""
        }

        return NotificationCompat.Builder(this, NotificationChannels.PLAYBACK)
            .setSmallIcon(R.drawable.ic_stat_playback)
            .setContentTitle(title.ifBlank { strings.quranPlay })
            .setContentText(subtitle)
            .setContentIntent(contentIntent)
            .setOngoing(playing)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                // ⚠️ الأيقونة والعنوان من الحالة الفعلية لا من وضع ثابت:
                // زرّ «تشغيل» على تلاوة تعمل يجعل المستخدم يضغط فيوقفها
                // وهو يريد العكس.
                if (playing) R.drawable.ic_stat_playback else R.drawable.ic_stat_playback,
                if (playing) strings.quranPause else strings.quranResume,
                toggleIntent,
            )
            .addAction(R.drawable.ic_stat_stop, strings.quranStop, stopIntent)
            .build()
    }

    private fun ayahLabel(ayah: Int?, strings: Strings): String =
        if (ayah != null) "${strings.quranPlay} · آية $ayah" else strings.quranPlay

    private fun pending(action: String, code: Int): PendingIntent = PendingIntent.getService(
        this, code,
        Intent(this, QuranPlaybackService::class.java).setAction(action),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    private fun stopSelfGracefully() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    companion object {
        private const val NOTIFICATION_ID = 4711
        private const val EXTRA_TITLE = "title"
        private const val ACTION_TOGGLE = "com.athar.athar.action.TOGGLE"
        private const val ACTION_STOP = "com.athar.athar.action.STOP"

        /// يشغّل الخدمة بعد بدء التشغيل — لا قبله.
        ///
        /// ⚠️ الترتيب مهم: [QuranAudioController.toggle] يُضبط الحالة
        /// أولًا، ثم تأتي الخدمة فتقرأ حالة جاهزة. لو بدأنا الخدمة
        /// أولًا لظهر الإشعار فارغًا ثم صار مضبوطًا — وميض يراه
        /// المستخدم في كل مرة يضغط «اسمع».
        fun attach(context: Context, title: String) {
            NotificationChannels.ensure(context)
            val intent = Intent(context, QuranPlaybackService::class.java)
                .putExtra(EXTRA_TITLE, title)
            runCatching { context.startForegroundService(intent) }
                .onFailure {
                    // ⚠️ النظام قد يمنع بدء خدمة مقدَّمة من الخلفية
                    // (API 31+). التلاوة تبقى تعمل داخل التطبيق؛ ولا
                    // نُسقط الشاشة على المستخدم من أجل إشعار.
                }
        }

        fun stop(context: Context) {
            runCatching { context.stopService(Intent(context, QuranPlaybackService::class.java)) }
        }
    }
}
