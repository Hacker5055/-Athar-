package com.athar.athar.athary

import android.content.Context
import androidx.core.content.edit

object TrackerStore {
    private const val PREFS_NAME = "athar.tracker.v1"
    private const val KEY = "state"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(context: Context): TrackerState =
        runCatching { TrackerState.decode(prefs(context).getString(KEY, null)) }
            .getOrDefault(TrackerState.EMPTY)

    fun save(context: Context, state: TrackerState) {
        prefs(context).edit { putString(KEY, state.encode()) }
    }
}
