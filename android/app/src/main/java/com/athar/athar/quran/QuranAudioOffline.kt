package com.athar.athar.quran

import java.io.File

/// التلاوة المحفوظة على الجهاز.
///
/// ⚖️ لماذا التنزيل اختياري لا تلقائي: رخصة المصدر تجيز إعادة
/// التوزيع غير التجاري وتبقي حق التلاوة لقارئها. التنزيل نسخة على
/// جهاز المستخدم لاستعماله، وهو داخل حدّ الرخصة — لكننا لا نملأ
/// جهازه بمئات الميجابايت دون أن يطلب.
///
/// ⚠️ لماذا `DownloadManager` لا خدمة من عندنا: هو خدمة نظام مجانية
/// تتولّى إعادة المحاولة، والإشعار، والتحميل بعد إغلاق التطبيق. كل
/// اعتماد جديد وكل خدمة نكتبها بأنفسنا خطر على بناء لا نترجمه
/// محليًّا، والمنفعة من كتابتها معدومة.
object QuranAudioOffline {

    /// مجلّد التلاوات داخل ملفات التطبيق — لا يحتاج أيّ إذن.
    fun directory(filesDir: File): File = File(filesDir, "quran-audio")

    /// مفتاح التلاوة المحفوظة: القارئ والسورة معًا.
    ///
    /// ⚠️ الاثنان معًا لا السورة وحدها: تلاوة العفاسي ليست تلاوة
    /// الحصري، ومن حفظ سورة بقارئ ثم بدّل القارئ يجب ألّا يسمع
    /// الملف المحفوظ لغيره.
    fun key(reciterId: String, surah: Int): String = "$reciterId/$surah"

    /// هل يمكن تنزيل هذا القارئ؟
    ///
    /// ⚠️ قارئ السورة الكاملة يُنزَّل بملف **واحد**؛ وقارئ الآيات
    /// يحتاج ٢٨٦ ملفًا للبقرة وحدها. تنزيل ذلك ثقيل على الجهاز
    /// وعلى المصدر، فالتنزيل متاح لمن يبثّ السورة كاملة فقط — وهو
    /// قيد نذكره للمستخدم لا نخفيه عنه.
    fun canDownload(reciter: Reciter): Boolean = reciter.mode == ReciterMode.SURAH

    /// مسار الملف المحفوظ، سواء وُجد أم لا.
    fun file(filesDir: File, reciterId: String, surah: Int): File =
        File(directory(filesDir), "$reciterId-$surah.mp3")

    /// ما يُشغَّل لهذه السورة: ملف محفوظ إن وُجد وصَلُح، وإلا الرابط.
    ///
    /// ⚠️ نتحقق أن الملف **موجود وغير فارغ**: تنزيلٌ انقطع يترك ملفًا
    /// صفرًا، وتشغيله يعطي صمتًا يبدو كأن التطبيق معطوب.
    fun sourceFor(
        filesDir: File,
        reciter: Reciter,
        surah: Int,
        downloaded: Set<String>,
    ): String? {
        if (key(reciter.id, surah) !in downloaded) return null
        val file = file(filesDir, reciter.id, surah)
        if (!file.exists() || file.length() <= 0L) return null
        return file.absolutePath
    }

    /// يختار بين المحفوظ والبثّ.
    fun resolve(
        filesDir: File,
        reciter: Reciter,
        surah: Int,
        downloaded: Set<String>,
    ): String {
        return sourceFor(filesDir, reciter, surah, downloaded)
            ?: QuranAudio.surahUrl(reciter, surah)
            ?: ""
    }

    /// حجم مقروء للمستخدم.
    ///
    /// ⚠️ مقروء لا دقيق: المستخدم يريد أن يعرف «كم سيأخذ من جهازي»،
    /// و«٣٤٫٢ ميجا» أنفع له من «٣٥٨٤١٢٢٤ بايت».
    fun humanSize(bytes: Long): String = when {
        bytes < 1024 -> "$bytes بايت"
        bytes < 1024 * 1024 -> "%.1f كيلو".format(bytes / 1024.0)
        bytes < 1024 * 1024 * 1024 -> "%.1f ميجا".format(bytes / (1024.0 * 1024.0))
        else -> "%.2f جيجا".format(bytes / (1024.0 * 1024.0 * 1024.0))
    }
}
