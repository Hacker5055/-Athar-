package com.athar.athar.quran

import android.content.Context

/// مخزن العلامات (المفضلة).
///
/// ⚠️ SharedPreferences لا قاعدة بيانات: العلامات عشرات في الغالب،
/// ولا تحتاج جدولًا ولا ترحيلًا. ولو صارت آلافًا فالملف لا يزال
/// أصغر من سورة واحدة.
object QuranBookmarkStore {

    private const val PREFS_NAME = "athar.bookmarks.v1"
    private const val KEY = "marks"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /// ⚠️ `runCatching`: ملف تالف يجب أن يُقرأ **كمجموعة فارغة** لا أن
    /// يُسقط الشاشة. فقدْ ضياع العلامات أهون من تطبيق لا يُفتح.
    fun load(context: Context): Set<String> =
        runCatching { QuranBookmarks.decode(prefs(context).getString(KEY, null)) }
            .getOrDefault(emptySet())

    fun save(context: Context, keys: Set<String>) {
        prefs(context).edit { putString(KEY, QuranBookmarks.encode(keys)) }
    }
}
