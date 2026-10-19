package com.athar.athar.tasbih

import android.content.Context
import androidx.core.content.edit

/// مخزن سجلّ جلسات التسبيح (E6).
///
/// ⚠️ **ملفٌّ منفصل** عن حالة السبحة: الحالة عدّادٌ حيّّ يُكتب
/// مع كل نقرة، والسجلّ تاريخٌ لا يُكتب إلا حين تُغلق جلسة. ولو
/// دُمجا لالتحق بالحفظ ما كان يُكتب على كل نقرة.
object TasbihSessionStore {

    private const val PREFS_NAME = "athar.tasbih.sessions.v1"
    private const val KEY_SESSIONS = "sessions"
    private const val KEY_CURRENT = "current"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(context: Context): List<TasbihSession> = runCatching {
        TasbihSessionCodec.decode(prefs(context).getString(KEY_SESSIONS, "[]") ?: "[]")
    }.getOrDefault(emptyList())

    fun save(context: Context, sessions: List<TasbihSession>) {
        prefs(context).edit {
            putString(KEY_SESSIONS, TasbihSessionCodec.encode(sessions))
        }
    }

    /// الجلسة **القائمة** — التي تُسبَّح الآن.
    ///
    /// ⚠️ تُحفظ في ملفٍّ مستقلّ عن السجلّ: الجلسة القائمة تغيّر
    /// مع كل نقرة، والسجلّ لا يُكتب إلا عند الإغلاق. ولو
    /// جمعتُهما لازدحم الملفّ بكتابةٍ في كل نقرة.
    fun loadCurrent(context: Context): TasbihSession? {
        val text = runCatching { prefs(context).getString(KEY_CURRENT, null) }.getOrNull()
            ?: return null
        return runCatching { TasbihSessionCodec.decodeOne(text) }.getOrNull()
    }

    fun saveCurrent(context: Context, session: TasbihSession?) {
        prefs(context).edit {
            if (session == null) {
                remove(KEY_CURRENT)
            } else {
                putString(KEY_CURRENT, TasbihSessionCodec.encodeOne(session))
            }
        }
    }

    fun clear(context: Context) {
        prefs(context).edit { remove(KEY_SESSIONS) }
    }
}
