package com.athar.athar.quran

import android.content.Context
import org.json.JSONObject
import java.nio.charset.StandardCharsets

/// يقرأ ترجمة معاني القرآن من أصول التطبيق.
///
/// ⚠️ أوفلاين أولًا (`docs/05` §1): الترجمة أصل لا طلب شبكة، فتعمل في
/// الطائرة وفي المسجد بلا اتصال.
class QuranTranslationRepository(private val context: Context) {

    private val cache = mutableMapOf<QuranTranslation, Map<String, String>>()

    /// خريطة «سورة-آية» → نصّها المترجم.
    fun textFor(translation: QuranTranslation): Map<String, String> {
        if (translation == QuranTranslation.NONE) return emptyMap()
        cache[translation]?.let { return it }
        val raw = runCatching { read(translation.asset) }.getOrNull() ?: return emptyMap()
        // ⚠️ أصل تالف ليس سببًا لانهيار القارئ — نعرض العربية وحدها
        return parse(raw).also { cache[translation] = it }
    }

    private fun read(path: String): String =
        context.assets.open(path).use { stream ->
            stream.readBytes().toString(StandardCharsets.UTF_8)
        }

    companion object {
        /// عدد آيات القرآن — يُتحقَّق منه في الاختبارات لا في التشغيل
        const val EXPECTED_VERSES = 6236

        /// ⚠️ المفتاح «سورة-آية» **لا ترتيب المصفوفة**: لو اعتمدنا
        /// الفهرس لانزلق كل شيء آية واحدة عند أول اختلاف في العدّ
        /// بين مصدرَين، فيُعرض معنى آية مكان أخرى.
        fun key(surah: Int, ayah: Int): String = "$surah-$ayah"

        fun parse(raw: String): Map<String, String> {
            val root = runCatching { JSONObject(raw) }.getOrNull() ?: return emptyMap()
            val out = mutableMapOf<String, String>()
            val chapters = root.keys()
            while (chapters.hasNext()) {
                val chapterKey = chapters.next()
                val surah = chapterKey.toIntOrNull() ?: continue
                val list = root.optJSONArray(chapterKey) ?: continue
                for (i in 0 until list.length()) {
                    val entry = list.optJSONObject(i) ?: continue
                    val verse = entry.optInt("verse", -1)
                    val text = entry.optString("text").trim()
                    if (verse <= 0 || text.isEmpty()) continue
                    out[key(surah, verse)] = text
                }
            }
            return out
        }
    }
}
