package com.athar.athar.tasbih

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject

/// ترميز جلسات التسبيح — **نقيّ** ليُختبر بلا جهاز.
///
/// ⚠️ الملفّ **JSON لا نصًّا**: الحقول تُسمّى بأسمائها، فيبقى
/// الملفّ مقروءًا، والقيمُ التالفة تُتجاهل بدل أن يُسقط
/// السجلّ كلّه.
object TasbihSessionCodec {

    fun encode(sessions: List<TasbihSession>): String {
        val array = JSONArray()
        for (session in sessions) {
            array.put(jsonOf(session))
        }
        return array.toString()
    }

    /// ⚠️ القراءة **لا تُسقط السجلّ**: جلسةٌ تالفة تُتجاهل،
    /// والجلسات الباقية تُقرأ. فملفٌّ فيه خلل واحد لا يُمحى
    /// تاريخُ المستخدم كاملًا.
    fun decode(text: String): List<TasbihSession> {
        val array = runCatching { JSONArray(text) }.getOrNull() ?: return emptyList()
        val out = ArrayList<TasbihSession>()
        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val session = sessionOf(item) ?: continue
            out.add(session)
        }
        return out
    }

    /// جلسةٌ واحدة — للجلسة **القائمة** لا للسجلّ.
    fun encodeOne(session: TasbihSession): String = jsonOf(session).toString()

    fun decodeOne(text: String): TasbihSession? {
        val obj = runCatching { JSONObject(text) }.getOrNull() ?: return null
        return sessionOf(obj)
    }

    private fun jsonOf(session: TasbihSession): JSONObject = JSONObject().apply {
        put("start", session.startedAtMillis)
        put("last", session.lastTapAtMillis)
        put("count", session.count)
        put("target", session.target)
    }

    private fun sessionOf(item: JSONObject): TasbihSession? {
        val start = item.optLong("start", -1L)
        val last = item.optLong("last", -1L)
        if (start < 0 || last < 0) return null
        return TasbihSession(
            startedAtMillis = start,
            lastTapAtMillis = last,
            count = item.optInt("count", 0).coerceAtLeast(0),
            target = item.optInt("target", 0).coerceAtLeast(0),
        )
    }
}
