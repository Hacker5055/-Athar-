package com.athar.athar

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.athar.athar.tasbih.VolumeCounter
import com.athar.athar.ui.AtharApp
import com.athar.athar.widget.PrayerWidgetProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // ⚠️ الودجت يُحدَّث عند **الفتح** لا انتظارًا لدورة النظام:
        // من فتح التطبيق يرى ودجتًا مطابقًا لما بالداخل، لا ودجتًا
        // مضى عليه نصف ساعة.
        PrayerWidgetProvider.refresh(this)
        setContent {
            AtharApp()
        }
    }

    /// ⚠️ أزرار الصوت **تُلتقط في شاشة السبحة وحدها** (E5): من
    /// يعدّ بزرّ الصوت يريد العدد لا تغييرَ الصوت. وفي كلّ شاشة
    /// أخرى يعمل الزرّ كالمعتاد.
    ///
    /// ⚠️ و`repeatCount == 0` فقط: الضغطُ المُطوَّل يُعيد المفتاح
    /// عشرات المرات في الثانية، فمن انزلق إصبعه على الزرّ جُمع
    /// له خمسون ذكرًا في ثانيتين. والنقرُ المتعمّد وحده يُعدّ.
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val isVolume = keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
            keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
        if (isVolume && event.repeatCount == 0 && VolumeCounter.handleKey()) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
