package com.athar.athar.adhkar

import android.content.Context
import androidx.core.content.edit

/// عدّاد الذكر **محفوظ** — لو أغلق المستخدم التطبيق في منتصف
/// التسبيح يعود ليجد عدّه كما تركه.
object AdhkarProgressStore {

    private const val PREFS_NAME = "athar.adhkar.v1"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(context: Context, sectionTitle: String, size: Int): List<Int> {
        val raw = prefs(context).getString(sectionTitle, null) ?: return List(size) { 0 }
        val parts = raw.split(",")
        return List(size) { index ->
            parts.getOrNull(index)?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        }
    }

    fun save(context: Context, sectionTitle: String, counts: List<Int>) {
        prefs(context).edit { putString(sectionTitle, counts.joinToString(",")) }
    }

    fun reset(context: Context, sectionTitle: String) {
        prefs(context).edit { remove(sectionTitle) }
    }
}
