package com.athar.athar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
}
