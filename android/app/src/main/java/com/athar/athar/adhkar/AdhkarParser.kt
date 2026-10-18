package com.athar.athar.adhkar

import org.json.JSONArray
import org.json.JSONObject

/// يحوّل JSON الكتاب إلى نماذج — **محض بالكامل** ويُختبر على JVM.
object AdhkarParser {

    fun parse(json: String): List<AdhkarSection> {
        val root = JSONObject(json)
        val sections = mutableListOf<AdhkarSection>()
        val titles = root.keys()
        while (titles.hasNext()) {
            val title = titles.next()
            val node = root.optJSONObject(title) ?: continue
            sections += AdhkarSection(title.trim(), parseItems(node.optJSONArray("Adhkar")))
        }
        return sections
    }

    private fun parseItems(array: JSONArray?): List<Dhikr> {
        array ?: return emptyList()
        val items = mutableListOf<Dhikr>()
        for (i in 0 until array.length()) {
            val node = array.optJSONObject(i) ?: continue
            val text = node.optString("Text").trim()
            // ⚠️ ذكر بلا نصّ لا معنى لعرضه — نتخطّاه بدل بطاقة فارغة
            if (text.isEmpty()) continue
            items += Dhikr(
                text = text,
                // ⚠️ عدد صفر أو سالب في البيانات يعني عدّادًا لا ينتهي
                count = node.optInt("Count", 1).coerceAtLeast(1),
                reference = node.optString("Reference").trim(),
            )
        }
        return items
    }
}
