package com.athar.athar

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableIntStateOf
import com.athar.athar.tasbih.VolumeCounter
import com.athar.athar.ui.AtharApp
import com.athar.athar.widget.PrayerWidgetProvider

class MainActivity : ComponentActivity() {

    /// ⚠️ **حالةٌ مرصودة**: الاختصارُ يفتح التبويبَ، وقد يكون
    /// التطبيقُ مفتوحًا فيأتي الطلبُ من نيةٍ جديدة. والحالةُ تُقرأ
    /// في التركيب، فتدفع التحديث بلا إعادة بناء.
    private val shortcutTab = mutableIntStateOf(0)

    /// ⚠️ **قسمُ «المزيد»**: القبلةُ لم تعد بندًا في الشريط، فصار
    /// فتحُها فتحَ «المزيد» على قسمِها. فالاختصارُ بابٌ إليها
    /// كما كان، لا走着ٌ إلى تبويبٍ مات.
    private val shortcutSection = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // ⚠️ الودجت يُحدَّث عند **الفتح** لا انتظارًا لدورة النظام:
        // من فتح التطبيق يرى ودجتًا مطابقًا لما بالداخل، لا ودجتًا
        // مضى عليه نصف ساعة.
        PrayerWidgetProvider.refresh(this)
        shortcutTab.intValue = tabFrom(intent)
        shortcutSection.intValue = sectionFrom(intent)
        setContent {
            AtharApp(
                initialTab = shortcutTab.intValue,
                initialSection = shortcutSection.intValue,
            )
        }
    }

    /// ⚠️ التطبيقُ مفتوحٌ بالفعل فنستقبل نيةً جديدة لا إنشاءً
    /// جديدًا — ولولا هذا لبقي المستخدم حيث هو.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        shortcutTab.intValue = tabFrom(intent)
        shortcutSection.intValue = sectionFrom(intent)
    }

    /// ⚠️ **نقرأها مرّة**: لولا حذفُ الزيادة لبقيت في النية بعد كل
    /// تدويرٍ للشاشة، فيقفز التطبيقُ إلى تبويب الاختصار من أينما
    /// كان المستخدم.
    private fun tabFrom(intent: Intent?): Int {
        val tab = intent?.getIntExtra(EXTRA_TAB, 0) ?: 0
        intent?.removeExtra(EXTRA_TAB)
        return tab
    }

    /// ⚠️ القسمُ يُقرأ ويُحذفُ كما يُقرأ التبويب: إلا أن قُرِئ
    /// مرّة، لعلا يقفز التطبيقُ إلى قسمٍ من أينما كان.
    private fun sectionFrom(intent: Intent?): Int {
        val section = intent?.getIntExtra(EXTRA_SECTION, 0) ?: 0
        intent?.removeExtra(EXTRA_SECTION)
        return section
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

    companion object {
        /// رقمُ التبويب الذي يفتحه الاختصار.
        const val EXTRA_TAB = "athar.tab"

        /// رقمُ القسمِ داخل «المزيد» الذي يفتحه الاختصار.
        const val EXTRA_SECTION = "athar.section"
    }
}
