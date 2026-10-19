package com.athar.athar.asma

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets

/// يقرأ الأسماء الحسنى من أصول التطبيق.
///
/// ⚠️ المصدر: `Names_Of_Allah_Json` (MIT) عن كتاب **سعيد بن علي بن وهف
/// القحطاني** — وهو المؤلف نفسه الذي نقلنا عنه حصن المسلم في `adhkar/`
/// فمرجع الأذكار ومرجع الأسماء واحد. الرخصة محفوظة في
/// `docs/third-party/names-of-allah-json-LICENSE.md`.
///
/// ⚠️ لماذا الأصول لا الشبكة؟ لأن أثر **أوفلاين أولًا** (`docs/05` §1).
///
/// ⚠️ احتفظنا بالاسم والمعنى فقط وأسقطنا الشرح: ٣٩٠ كيلوبايت من نصّ
/// الكتاب مقابل ١٤، وشاشة تعرضه تصميم قائم بذاته لم نُعدّه بعد.
class AsmaRepository(private val context: Context) {

    private val namesLazy: List<AsmaName> by lazy { parse(read(ASSET)) }

    fun names(): List<AsmaName> = namesLazy

    private fun read(path: String): String =
        context.assets.open(path).use { stream ->
            stream.readBytes().toString(StandardCharsets.UTF_8)
        }

    companion object {
        const val ASSET = "asma/names.json"

        fun parse(raw: String): List<AsmaName> {
            val root = runCatching { JSONArray(raw) }.getOrNull() ?: return emptyList()
            val out = mutableListOf<AsmaName>()
            for (i in 0 until root.length()) {
                val o: JSONObject = root.optJSONObject(i) ?: continue
                val name = o.optString("name").trim()
                val meaning = o.optString("meaning").trim()
                // ⚠️ اسم بلا معنى (أو العكس) لا يُعرض إطلاقًا
                if (name.isEmpty() || meaning.isEmpty()) continue
                val arabic = o.optString("nameArabic").trim().ifEmpty { name }
                out += AsmaName(o.optInt("id", i + 1), name, arabic, meaning)
            }
            return out
        }
    }
}
