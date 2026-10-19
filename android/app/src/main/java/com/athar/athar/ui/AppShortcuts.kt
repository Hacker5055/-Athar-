package com.athar.athar.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.athar.athar.MainActivity
import com.athar.athar.R
import com.athar.athar.i18n.Strings

/// اختصاراتُ التطبيق (docs/02 · H7): ضغطةٌ مطوّلة على أيقونة التطبيق.
///
/// ⚠️ **ديناميكيةٌ لا ساكنة**: عناوينُ الاختصارات تُقرأ من حزمة
/// الترجمة فيتبعها اختصارٌ بلغة التطبيق نفسها — لا لغة النظام. أمّا
/// الاختصارات الساكنة في `shortcuts.xml` فلا تُترجَم إلا بموارد
/// أندرويد، وهي لغةٌ ثانية في تطبيقٍ بثلاث.
object AppShortcuts {

    const val ACTION_QIBLA = "com.athar.athar.shortcut.QIBLA"
    const val ACTION_TASBIH = "com.athar.athar.shortcut.TASBIH"
    const val ACTION_QURAN = "com.athar.athar.shortcut.QURAN"

    /// أرقامُ التبويبات — نفسُ ترتيب `AppShell`.
    const val TAB_QURAN = 1
    const val TAB_TASBIH = 3
    /// ⚠️ القبلةُ **لم تعد بندًا**: صار الشريطُ خمسةَ بنودٍ، فنُقلت
    /// القبلةُ إلى «المزيد». واختصارُها يفتحُ «المزيد» على قسمِها.
    const val TAB_MORE = 4
    const val SECTION_QIBLA = 9

    fun apply(context: Context, strings: Strings) {
        val shortcuts = listOf(
            shortcut(
                context,
                "athar_shortcut_qibla",
                ACTION_QIBLA,
                TAB_MORE,
                strings.tabQibla,
                R.drawable.ic_shortcut_qibla,
                section = SECTION_QIBLA,
            ),
            shortcut(
                context,
                "athar_shortcut_tasbih",
                ACTION_TASBIH,
                TAB_TASBIH,
                strings.tabTasbih,
                R.drawable.ic_shortcut_tasbih,
            ),
            shortcut(
                context,
                "athar_shortcut_quran",
                ACTION_QURAN,
                TAB_QURAN,
                strings.tabQuran,
                R.drawable.ic_shortcut_quran,
            ),
        )
        // ⚠️ الاستبدالُ لا الإضافة: فمن أضاف اختصارًا في إصدارٍ
        // سابقٍ لا يبقى أثره. والإضافةُ تتراكم بلا حدّ.
        runCatching { ShortcutManagerCompat.setDynamicShortcuts(context, shortcuts) }
    }

    private fun shortcut(
        context: Context,
        id: String,
        action: String,
        tab: Int,
        label: String,
        icon: Int,
        section: Int = 0,
    ): ShortcutInfoCompat =
        ShortcutInfoCompat.Builder(context, id)
            .setShortLabel(label)
            .setLongLabel(label)
            .setIcon(IconCompat.createWithResource(context, icon))
            // ⚠️ **فِعلٌ لازم**: `ShortcutInfoCompat` يرمي استثناءً
            // إن كان الـ Intent بلا فعل، فلا اختصار بلا فعل.
            .setIntent(
                Intent(context, MainActivity::class.java)
                    .setAction(action)
                    .putExtra(MainActivity.EXTRA_TAB, tab)
                    .putExtra(MainActivity.EXTRA_SECTION, section),
            )
            .build()
}
