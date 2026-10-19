package com.athar.athar.notification

import android.content.Context
import androidx.core.content.edit

/// تخزينُ إعدادات الصمت — ملفٌ مستقلّ عن إعدادات التنبيه.
///
/// ⚠️ **مستقلٌّ عمدًا**: إعداداتُ التنبيه تُمسح في مكانها، وهذه تخصّ
/// سلوكًا حسّاسًا (كتم الصوت)، فلا تختلط بمسحٍ عارض.
object SilenceStore {

    private const val PREFS_NAME = "athar.silence.v1"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_MINUTES = "minutes"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(context: Context): SilenceSettings {
        val store = prefs(context)
        return SilenceSettings(
            enabled = store.getBoolean(KEY_ENABLED, false),
            minutes = SilenceSettings.clamp(
                store.getInt(KEY_MINUTES, SilenceSettings.DEFAULT_MINUTES),
            ),
        )
    }

    fun save(context: Context, settings: SilenceSettings) {
        prefs(context).edit {
            putBoolean(KEY_ENABLED, settings.enabled)
            putInt(KEY_MINUTES, SilenceSettings.clamp(settings.minutes))
        }
    }
}
