package com.athar.athar.quran

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/// اختبارات التلاوة المحفوظة — المنطق النقي منها فقط.
///
/// ⚠️ التنزيل نفسه لا يُختبر: هو `DownloadManager` نظامي وشبكة،
/// والاختبار يجب أن يمرّ بلا جهاز ولا اتصال. الذي نختبره هو
/// **القرار**: متى نسمع من الملف ومتى نبثّ من المصدر.
class QuranAudioOfflineTest {

    private val alafasy = Reciters.byId("ar.alafasy")!!
    private val husary = Reciters.byId("ar.husary")!!
    private val dir = File("/tmp/athar-test-audio")

    private fun saved(filesDir: File, name: String, bytes: Long): File {
        val file = File(filesDir, name)
        file.parentFile?.mkdirs()
        file.writeBytes(ByteArray(bytes.toInt()))
        return file
    }

    // ── المفتاح ──────────────────────────────────────────

    @Test
    fun keyCarriesBothReciterAndSurah() {
        // ⚠️ الاثنان معًا: تلاوة العفاسي ليست تلاوة الحصري، ومن حفظ
        // سورة بقارئ ثم بدّله يجب ألّا يسمع ملف غيره.
        assertEquals("ar.alafasy/1", QuranAudioOffline.key("ar.alafasy", 1))
        assertFalse(
            QuranAudioOffline.key("ar.alafasy", 1) == QuranAudioOffline.key("ar.husary", 1),
        )
        assertFalse(
            QuranAudioOffline.key("ar.alafasy", 1) == QuranAudioOffline.key("ar.alafasy", 2),
        )
    }

    // ── من يستطيع التنزيل ────────────────────────────────

    @Test
    fun onlySurahModeRecitersCanBeDownloaded() {
        assertTrue(QuranAudioOffline.canDownload(alafasy))
        // ⚠️ قارئ الآيات يحتاج ٢٨٦ ملفًا للبقرة وحدها — ثقيل على
        // الجهاز وعلى المصدر، فالتنزيل مقصور على ملفّ واحد.
        assertFalse(QuranAudioOffline.canDownload(husary))
    }

    @Test
    fun atLeastOneReciterIsDownloadable() {
        assertTrue(
            "لا قارئ قابل للتنزيل يعني ميزة ميتة",
            Reciters.ALL.any { QuranAudioOffline.canDownload(it) },
        )
    }

    // ── الاختيار بين الملف والبثّ ────────────────────────

    @Test
    fun prefersTheSavedFileWhenItIsReal() {
        val root = File(dir, "real")
        saved(QuranAudioOffline.directory(root), "ar.alafasy-1.mp3", 4096)
        val source = QuranAudioOffline.sourceFor(
            root, alafasy, 1, setOf(QuranAudioOffline.key("ar.alafasy", 1)),
        )
        assertTrue("يجب أن يسمع الملف المحفوظ", source != null && source.endsWith(".mp3"))
    }

    @Test
    fun ignoresAKeyThatWasNotDownloaded() {
        val root = File(dir, "unlisted")
        saved(QuranAudioOffline.directory(root), "ar.alafasy-1.mp3", 4096)
        // ⚠️ الملف موجود لكنه غير مسجّل: تنزيلٌ لم يكتمل أو بقايا
        // ملف قديم، ولا يجوز تشغيله لمجرّد وجوده على القرص.
        assertNull(QuranAudioOffline.sourceFor(root, alafasy, 1, emptySet()))
    }

    @Test
    fun ignoresAnEmptyFile() {
        val root = File(dir, "empty")
        saved(QuranAudioOffline.directory(root), "ar.alafasy-1.mp3", 0)
        // ⚠️ تنزيل منقطع يترك ملفًا صفرًا، وتشغيله صمتٌ يبدو كعطل
        assertNull(
            QuranAudioOffline.sourceFor(
                root, alafasy, 1, setOf(QuranAudioOffline.key("ar.alafasy", 1)),
            ),
        )
    }

    @Test
    fun ignoresAMissingFileEvenIfMarkedSaved() {
        val root = File(dir, "missing")
        assertNull(
            QuranAudioOffline.sourceFor(
                root, alafasy, 9, setOf(QuranAudioOffline.key("ar.alafasy", 9)),
            ),
        )
    }

    @Test
    fun anotherReciterDoesNotReuseMyFile() {
        val root = File(dir, "other")
        saved(QuranAudioOffline.directory(root), "ar.alafasy-1.mp3", 4096)
        assertNull(
            QuranAudioOffline.sourceFor(
                root, husary, 1, setOf(QuranAudioOffline.key("ar.husary", 1)),
            ),
        )
    }

    @Test
    fun resolveFallsBackToStreaming() {
        val root = File(dir, "fallback")
        val url = QuranAudioOffline.resolve(root, alafasy, 1, emptySet())
        assertEquals("https://cdn.islamic.network/quran/audio-surah/128/ar.alafasy/1.mp3", url)
    }

    // ── الحجم المقروء ────────────────────────────────────

    @Test
    fun humanSizeIsReadable() {
        assertEquals("512 بايت", QuranAudioOffline.humanSize(512))
        assertTrue(QuranAudioOffline.humanSize(2048).endsWith("كيلو"))
        assertTrue(QuranAudioOffline.humanSize(5 * 1024 * 1024).endsWith("ميجا"))
        assertTrue(QuranAudioOffline.humanSize(3L * 1024 * 1024 * 1024).endsWith("جيجا"))
    }
}
