package com.athar.athar.quran

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/// تخزين وجلب قائمة التلاوات المسحوبة.
///
/// ⚠️ التخزين المحلي **واجب لا تحسين**: لو قرأنا من الشبكة وحدها
/// لظهرت التلاوة المسحوبة من جديد كلما فتح المستخدم التطبيق بلا
/// اتصال، ثم اختفت حين يتصل. القائمة المخزّنة تثبت القرار.
object ReciterWithdrawalStore {

    private const val PREFS_NAME = "athar.settings.v1"
    private const val KEY_IDS = "withdrawn_ids"
    private const val KEY_AT = "withdrawn_at"

    /// ⚠️ مرة كل ساعة لا مع كل فتح: القائمة شبه ثابتة، والطلب مع كل
    /// فتح شاشة يهدر بيانات المستخدم ولا يزيد أمانًا.
    private const val MIN_INTERVAL_MS = 60L * 60L * 1000L

    private const val TIMEOUT_MS = 8000

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /// آخر ما عرفناه من مسحوبات — بلا شبكة.
    fun load(context: Context): Set<String> {
        val raw = prefs(context).getString(KEY_IDS, null) ?: return emptySet()
        return raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    /// يجلب القائمة ويخزّنها، ويُعيد ما صار معروفًا الآن.
    ///
    /// ⚠️ **يفتح عند الفشل**: تعذّر الوصول إلى الملف لا يعني أن
    /// التلاوات حلال، بل يعني أننا لا نعرف. وحين لا نعرف نُبقي
    /// آخر ما عرفناه — فحذف تلاوة حيّة لأن الشبكة تعطّلت ضرر أكبر
    /// من تأخّر سحب تلاوة مطلوبة.
    suspend fun refresh(context: Context, force: Boolean = false): Set<String> =
        withContext(Dispatchers.IO) {
            val p = prefs(context)
            val last = p.getLong(KEY_AT, 0L)
            if (!force && System.currentTimeMillis() - last < MIN_INTERVAL_MS) {
                return@withContext load(context)
            }

            val fetched = runCatching { fetch() }.getOrNull()
            if (fetched == null) {
                // شبكة مقطوعة: نبقي المخزّن ولا نُحدّث الوقت، فنحاول
                // من جديد في الفتح القريب لا بعد ساعة.
                return@withContext load(context)
            }

            p.edit {
                putString(KEY_IDS, fetched.joinToString(","))
                putLong(KEY_AT, System.currentTimeMillis())
            }
            fetched
        }

    /// ⚠️ `HttpURLConnection` المدمج لا مكتبة خارجية: طلب بسيط لملف
    /// صغير، وكل اعتماد جديد خطر على بناء لا نترجمه محليًّا.
    private fun fetch(): Set<String> {
        val connection = URL(ReciterWithdrawals.SOURCE_URL).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = TIMEOUT_MS
            connection.readTimeout = TIMEOUT_MS
            connection.instanceFollowRedirects = true
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return emptySet()
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            // ⚠️ سقف للحجم: ملف نصوص يُفترض أن يكون بايتات معدودة.
            // لو استُبدل بشيء ضخم فنحن نقرأ صفحة خطأ لا قائمة.
            if (body.length > 64 * 1024) return emptySet()
            return ReciterWithdrawals.parse(body)
        } finally {
            connection.disconnect()
        }
    }
}
