package com.athar.athar.quran

import android.content.Context
import androidx.core.content.edit

/// تخزين وضع الحفظ.
///
/// ⚠️ التكرارات تُحفظ **منفصلة عن العلامات**: العلامة مشتقّة من
/// التكرار (`Hifz.isMemorized`)، ولو حُفظت الاثنتان معًا لأمكن أن
/// تقول إحداهما «محفوظة» والأخرى «تكرار واحد» — وتناقضٌ كهذا لا
/// يُكتشف إلا بعد أن يثق الحافظ بما لا يصحّ.
object HifzStore {

    private const val PREFS_NAME = "athar.quran.hifz.v1"
    private const val KEY_FROM_S = "from_s"
    private const val KEY_FROM_A = "from_a"
    private const val KEY_TO_S = "to_s"
    private const val KEY_TO_A = "to_a"
    private const val KEY_REPEATS = "repeats"

    data class Plan(
        val fromSurah: Int,
        val fromAyah: Int,
        val toSurah: Int,
        val toAyah: Int,
    )

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /// ⚠️ `null` = لا نطاق محدّد، وهو **حالة صحيحة** لا خطأ:
    /// الشاشة تعرض عندها منتقي النطاق.
    fun loadPlan(context: Context): Plan? = runCatching {
        val p = prefs(context)
        if (!p.contains(KEY_FROM_S)) return null
        Plan(
            fromSurah = p.getInt(KEY_FROM_S, 1),
            fromAyah = p.getInt(KEY_FROM_A, 1),
            toSurah = p.getInt(KEY_TO_S, 1),
            toAyah = p.getInt(KEY_TO_A, 7),
        )
    }.getOrNull()

    fun savePlan(context: Context, plan: Plan) {
        prefs(context).edit {
            putInt(KEY_FROM_S, plan.fromSurah)
            putInt(KEY_FROM_A, plan.fromAyah)
            putInt(KEY_TO_S, plan.toSurah)
            putInt(KEY_TO_A, plan.toAyah)
        }
    }

    fun clearPlan(context: Context) {
        prefs(context).edit {
            remove(KEY_FROM_S)
            remove(KEY_FROM_A)
            remove(KEY_TO_S)
            remove(KEY_TO_A)
        }
    }

    fun loadRepeats(context: Context): Map<Int, Int> = runCatching {
        val raw = prefs(context).getString(KEY_REPEATS, null).orEmpty()
        if (raw.isBlank()) return emptyMap()
        raw.split(',').mapNotNull { part ->
            val bits = part.split(':')
            val id = bits.getOrNull(0)?.toIntOrNull() ?: return@mapNotNull null
            val count = bits.getOrNull(1)?.toIntOrNull() ?: return@mapNotNull null
            if (id <= 0 || count <= 0) null else id to count
        }.toMap()
    }.getOrDefault(emptyMap())

    private fun saveRepeats(context: Context, repeats: Map<Int, Int>) {
        // ⚠️ الفاصلة والنقطتان **آمنتان** هنا لأن المحتوى أعداد
        // لا نصّ — بخلاف الأذكار المخصّصة حيث استخدمنا فواصل
        // تحكّم (U+241E).
        prefs(context).edit {
            putString(KEY_REPEATS, repeats.entries.joinToString(",") { "${it.key}:${it.value}" })
        }
    }

    /// يزيد تكرار آية واحدة ويعيد عددها الجديد.
    fun repeat(context: Context, verseId: Int): Int {
        val current = loadRepeats(context)
        val next = (current[verseId] ?: 0) + 1
        saveRepeats(context, current + (verseId to next))
        return next
    }

    /// يُصفّر تكرار آية — تراجع عن وسمها محفوظة.
    fun reset(context: Context, verseId: Int) {
        val current = loadRepeats(context).toMutableMap()
        current.remove(verseId)
        saveRepeats(context, current)
    }

    fun clearAll(context: Context) {
        prefs(context).edit { clear() }
    }
}
