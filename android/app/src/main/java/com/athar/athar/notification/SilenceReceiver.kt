package com.athar.athar.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/// يُطبّق الصمتَ في أول النافذة ويحرّره في آخرها.
class SilenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_ON -> SilenceController.apply(context)
            ACTION_OFF -> SilenceController.restore(context)
        }
    }

    companion object {
        const val ACTION_ON = "com.athar.athar.silence.ON"
        const val ACTION_OFF = "com.athar.athar.silence.OFF"
    }
}
