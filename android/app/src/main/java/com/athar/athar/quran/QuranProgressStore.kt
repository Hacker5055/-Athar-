package com.athar.athar.quran

import android.content.Context
import androidx.core.content.edit

/// مخزن آخر موضع قراءة.
object QuranProgressStore {

    private const val PREFS_NAME = "athar.quran.progress.v1"
    private const val KEY_SURAH = "surah"
    private const val KEY_NAME = "name"
    private const val KEY_AYAH = "ayah"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(context: Context): LastRead? = runCatching {
        val p = prefs(context)
        val surah = p.getInt(KEY_SURAH, 0)
        val ayah = p.getInt(KEY_AYAH, 0)
        val name = p.getString(KEY_NAME, null) ?: return null
        if (!QuranProgress.isValid(surah, ayah)) return null
        LastRead(surah, name, ayah)
    }.getOrNull()

    /// ⚠️ `apply` لا `commit`: الموضع يُحفظ مع كل تمرير، والكتابة
    /// المتزامنة على الواجهة تعرقل الإطار.
    fun save(context: Context, last: LastRead) {
        if (!QuranProgress.isValid(last.surah, last.ayah)) return
        prefs(context).edit(commit = false) {
            putInt(KEY_SURAH, last.surah)
            putString(KEY_NAME, last.surahName)
            putInt(KEY_AYAH, last.ayah)
        }
    }

    fun clear(context: Context) {
        prefs(context).edit(commit = false) { clear() }
    }
}
