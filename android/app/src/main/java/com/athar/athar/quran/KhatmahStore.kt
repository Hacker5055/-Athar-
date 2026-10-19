package com.athar.athar.quran

import android.content.Context
import androidx.core.content.edit

/// مخزن خطة الختمة.
object KhatmahStore {

    private const val PREFS_NAME = "athar.quran.khatmah.v1"
    private const val KEY_DAYS = "days"
    private const val KEY_START = "start"
    private const val KEY_DONE = "done"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /// خطة نشِطة، أو `null` إن لم يبدأ المستخدم واحدة.
    data class Plan(val days: Int, val startEpochDay: Long, val doneUpTo: Int)

    fun load(context: Context): Plan? = runCatching {
        val p = prefs(context)
        val days = p.getInt(KEY_DAYS, 0)
        if (days <= 0) return null
        Plan(
            days = days,
            startEpochDay = p.getLong(KEY_START, 0L),
            doneUpTo = p.getInt(KEY_DONE, 0).coerceIn(0, QuranMeta.TOTAL_VERSES),
        )
    }.getOrNull()

    fun save(context: Context, plan: Plan) {
        prefs(context).edit {
            putInt(KEY_DAYS, plan.days)
            putLong(KEY_START, plan.startEpochDay)
            putInt(KEY_DONE, plan.doneUpTo.coerceIn(0, QuranMeta.TOTAL_VERSES))
        }
    }

    fun clear(context: Context) {
        prefs(context).edit { clear() }
    }
}
