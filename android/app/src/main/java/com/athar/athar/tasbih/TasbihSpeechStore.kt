package com.athar.athar.tasbih

import android.content.Context
import androidx.core.content.edit

/// مفتاح نطق العدد (C10).
///
/// ⚠️ **مُطفأ افتراضيًا**: النطق يقطع التسبيح بصوتٍ عالٍ، فمن
/// يسبّح في المسجد أو في الليل قد يفضّل الصمت. فالتشغيل على
/// المستخدم أن يختار — والتطبيق لا يفرض صوته على أحد.
object TasbihSpeechStore {

    private const val PREFS_NAME = "athar.tasbih.speech.v1"
    private const val KEY_ENABLED = "enabled"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isEnabled(context: Context): Boolean = runCatching {
        prefs(context).getBoolean(KEY_ENABLED, false)
    }.getOrDefault(false)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit { putBoolean(KEY_ENABLED, enabled) }
    }
}
