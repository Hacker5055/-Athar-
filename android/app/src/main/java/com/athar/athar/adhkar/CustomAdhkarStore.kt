package com.athar.athar.adhkar

import android.content.Context
import androidx.core.content.edit

/// مخزن أذكار المستخدم.
///
/// ⚠️ `runCatching` في القراءة: ملف تالف يجب أن يُقرأ **كقائمة
/// فارغة** لا أن يُسقط الشاشة. فقْد أذكار المستخدم مؤلم، لكن تطبيق
/// لا يُفتح أسوأ.
object CustomAdhkarStore {

    private const val PREFS_NAME = "athar.adhkar.custom.v1"
    private const val KEY_ITEMS = "items"
    private const val KEY_DONE = "done"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(context: Context): List<CustomDhikr> =
        runCatching { CustomAdhkar.decode(prefs(context).getString(KEY_ITEMS, null)) }
            .getOrDefault(emptyList())

    fun save(context: Context, items: List<CustomDhikr>) {
        prefs(context).edit { putString(KEY_ITEMS, CustomAdhkar.encode(items)) }
    }

    fun done(context: Context): Map<String, Int> =
        runCatching { CustomAdhkar.decodeDone(prefs(context).getString(KEY_DONE, null)) }
            .getOrDefault(emptyMap())

    fun saveDone(context: Context, done: Map<String, Int>) {
        prefs(context).edit { putString(KEY_DONE, CustomAdhkar.encodeDone(done)) }
    }
}
