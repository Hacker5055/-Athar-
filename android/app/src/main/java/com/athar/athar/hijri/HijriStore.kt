package com.athar.athar.hijri

import android.content.Context
import androidx.core.content.edit

/// مخزن إزاحة التقويم الهجري (G2).
object HijriStore {

    private const val PREFS_NAME = "athar.settings.v1"
    private const val KEY = "hijri_offset"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /// ⚠️ `coerceIn` لا `getInt` كما هو: قيمة خارج النطاق من نسخة
    /// أقدم تُقصّ إلى حدّها، لا تُطبّق كما هي فتُخرج تواريخ باطلة.
    fun load(context: Context): Int =
        runCatching { prefs(context).getInt(KEY, 0) }
            .getOrDefault(0)
            .coerceIn(-HijriCalendar.MAX_OFFSET, HijriCalendar.MAX_OFFSET)

    fun save(context: Context, offset: Int) {
        prefs(context).edit {
            putInt(KEY, offset.coerceIn(-HijriCalendar.MAX_OFFSET, HijriCalendar.MAX_OFFSET))
        }
    }
}
