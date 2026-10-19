package com.athar.athar.i18n

import android.content.Context
import androidx.core.content.edit

object LocaleStore {

    private const val PREFS_NAME = "athar.settings.v1"
    private const val KEY = "locale"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(context: Context): AppLocale = AppLocale.fromCode(prefs(context).getString(KEY, null))

    fun save(context: Context, locale: AppLocale) {
        prefs(context).edit { putString(KEY, locale.code) }
    }
}
