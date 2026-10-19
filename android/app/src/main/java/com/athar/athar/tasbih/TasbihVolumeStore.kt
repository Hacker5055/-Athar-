package com.athar.athar.tasbih

import android.content.Context
import androidx.core.content.edit

/// مفتاح العدّ بأزرار الصوت (E5).
///
/// ⚠️ **مُطفأ افتراضيًا**: أزرار الصوت أداةٌ في الهاتف، وأخذها
/// من المستخدم بلا إذنه يخطف منه تغييرَ الصوت — وهو ما يحتاجه
/// كلُّ من يسبّح ويعدّل الصوت في نفس الوقت. فمن أراد العدّ
/// بها فعّلها، ومن لا يريد يبقى كما هو.
object TasbihVolumeStore {

    private const val PREFS_NAME = "athar.tasbih.volume.v1"
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
