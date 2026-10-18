package com.athar.athar.quran

import org.json.JSONArray
import org.json.JSONObject

/// يقرأ أصول القرآن — **محض بالكامل** ويُختبر على JVM.
///
/// ⚠️ البنية موثّقة من الملف الأصلي لا مُخمّنة:
///   chapters.json → {"chapters":[{"id":1,"name":"الفاتحة",
///                    "transliteration":"…","translation":"…",
///                    "type":"meccan","total_verses":7}, …]}
///   uthmani.json  → {"1":[{"chapter":1,"verse":1,"text":"…"}, …], …}
object QuranAssetParser {

    fun parseChapters(json: String): List<Surah> {
        val root = JSONObject(json)
        val array: JSONArray = root.getJSONArray("chapters")
        val result = ArrayList<Surah>(array.length())
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            result.add(
                Surah(
                    id = o.getInt("id"),
                    nameAr = o.getString("name"),
                    nameEn = o.getString("transliteration"),
                    ayahCount = o.getInt("total_verses"),
                    isMeccan = o.optString("type") == "meccan",
                ),
            )
        }
        // ⚠️ المصدر مرتّب أصلًا، لكن الترتيب شرط في الفهرس لا صدفة
        return result.sortedBy { it.id }
    }

    fun parseUthmani(json: String): Map<Int, List<Ayah>> {
        val root = JSONObject(json)
        val result = HashMap<Int, List<Ayah>>(114)
        val keys = root.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val array = root.getJSONArray(key)
            val list = ArrayList<Ayah>(array.length())
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                list.add(
                    Ayah(
                        surah = o.getInt("chapter"),
                        number = o.getInt("verse"),
                        text = o.getString("text"),
                    ),
                )
            }
            list.sortBy { it.number }
            result[key.toInt()] = list
        }
        return result
    }
}
