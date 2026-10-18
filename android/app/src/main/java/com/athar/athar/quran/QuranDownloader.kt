package com.athar.athar.quran

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import java.io.File

/// حالة التنزيلات الجارية — تُقرأ لتحديث الواجهة.
data class PendingDownload(val id: Long, val key: String)

/// تنزيل التلاوة بالـ `DownloadManager` المدمج في النظام.
///
/// ⚠️ لماذا خدمة النظام لا خدمة من عندنا: `DownloadManager` يتولّى
/// إعادة المحاولة عند انقطاع الشبكة، والإشعار، وإكمال التنزيل بعد
/// إغلاق التطبيق. كتابة ذلك بأنفسنا تعقيد بلا منفعة، وخطر على بناء
/// لا نترجمه محليًّا.
object QuranDownloader {

    private const val PREFS_NAME = "athar.settings.v1"
    private const val KEY_PENDING = "audio_pending"
    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun manager(context: Context) =
        context.getSystemService(DownloadManager::class.java)

    /// يُطلق تنزيل سورة بصوت قارئ.
    ///
    /// ⚠️ الوجهة ملفّات التطبيق لا الذاكرة الخارجية: بلا أيّ إذن،
    /// وبلا أثر يتركه المستخدم بعد حذف التطبيق.
    fun enqueue(context: Context, reciter: Reciter, surah: Int, title: String): Long? {
        if (!QuranAudioOffline.canDownload(reciter)) return null
        val url = QuranAudio.surahUrl(reciter, surah) ?: return null
        val destination = QuranAudioOffline.file(context.filesDir, reciter.id, surah)

        // ⚠️ نحذف القديم أولًا: تنزيل فوق ملف موجود يترك ملفًا مقطوعًا
        // إن فشل في منتصفه، والملف المقطوع أسوأ من الملف المفقود.
        if (destination.exists()) destination.delete()
        destination.parentFile?.mkdirs()

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("$title · ${reciter.nameAr}")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED,
            )
            .setDestinationUri(Uri.fromFile(destination))
            .setMimeType("audio/mpeg")

        val id = runCatching { manager(context)?.enqueue(request) }.getOrNull()
            ?: return null

        // ⚠️ نربط مُعرّف التنزيل بمفتاحه: لا سبيل لمعرفة ما اكتمل
        // إلا بهذا الربط.
        remember(context, id, QuranAudioOffline.key(reciter.id, surah))
        return id
    }

    /// يحذف تلاوة محفوظة وينسى أمرها.
    fun remove(context: Context, reciter: Reciter, surah: Int) {
        val key = QuranAudioOffline.key(reciter.id, surah)
        QuranAudioOffline.file(context.filesDir, reciter.id, surah).delete()
        prefs(context).edit {
            putStringSet(KEY_DOWNLOADED, loaded(context) - key)
        }
        forget(context, key)
    }

    /// التنزيلات التي لم تكتمل بعد.
    fun pending(context: Context): List<PendingDownload> {
        val raw = prefs(context).getString(KEY_PENDING, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split(';').mapNotNull { part ->
            val bits = part.split('=', limit = 2)
            if (bits.size != 2) return@mapNotNull null
            val id = bits[0].toLongOrNull() ?: return@mapNotNull null
            PendingDownload(id, bits[1])
        }
    }

    /// يستقرئ حال التنزيلات ويُثبت ما اكتمل منها.
    ///
    /// ⚠️ استقراء لا مستقبل بثّ: المستقبل يعني مكوّنًا جديدًا في
    /// البيان ودورة حياة أخرى لا نستطيع اختبارها. والاستقراء يكفي:
    /// إن اكتمل التنزيل والشاشة مغلقة، يُثبت عند العودة إليها.
    fun settle(context: Context): Set<String> {
        val dm = manager(context) ?: return loaded(context)
        val pending = pending(context)
        if (pending.isEmpty()) return loaded(context)

        val done = mutableListOf<PendingDownload>()
        for (item in pending) {
            val status = runCatching {
                val cursor = dm.query(DownloadManager.Query().setFilterById(item.id))
                if (cursor.moveToFirst()) {
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                } else {
                    null
                }.also { cursor.close() }
            }.getOrNull()

            when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> done.add(item)
                DownloadManager.STATUS_FAILED,
                null,
                -> forget(context, item.key)
            }
        }

        if (done.isNotEmpty()) {
            val keys = done.map { it.key }.toSet()
            prefs(context).edit {
                putStringSet(KEY_DOWNLOADED, loaded(context) + keys)
            }
            done.forEach { forget(context, it.key) }
        }
        return loaded(context)
    }

    /// المحفوظات المكتملة.
    fun loaded(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_DOWNLOADED, emptySet()) ?: emptySet()

    private const val KEY_DOWNLOADED = "audio_downloaded"

    private fun remember(context: Context, id: Long, key: String) {
        val current = pending(context).toMutableList()
        current.add(PendingDownload(id, key))
        prefs(context).edit {
            putString(KEY_PENDING, current.joinToString(";") { "${it.id}=${it.key}" })
        }
    }

    private fun forget(context: Context, key: String) {
        val current = pending(context).filterNot { it.key == key }
        prefs(context).edit {
            putString(KEY_PENDING, current.joinToString(";") { "${it.id}=${it.key}" })
        }
    }
}
