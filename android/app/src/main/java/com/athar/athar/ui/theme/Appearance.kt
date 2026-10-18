package com.athar.athar.ui.theme

import android.content.Context
import androidx.core.content.edit

/// كيف يختار المستخدم ثيم التطبيق.
///
/// ⚠️ «تلقائي» هو الأصل لا الفاتح: من ضبط تطبيقه على الداكن ليلًا ثم
/// فتح أثر نهارًا يرى شاشة بيضاء تؤلمه. اتباع النظام هو الأقل
/// مفاجأة، والخيار اليدوي لمن يريد غيره.
enum class ThemeMode { SYSTEM, LIGHT, DARK }

/// مقاس الخط العام.
///
/// ⚠️ القيم عوامل مضاعفة لا مقاسات: كل نص في التطبيق يقرأ بـ sp،
/// فيكبر ويصغر مع إعداد النظام نفسه. أمّا تبديل المقاسات الجاهزة
/// فيجمّد النص عند حدّ لا يتغيّر، وهو ما لا نريده.
enum class FontScaleLevel(val scale: Float) {
    SMALL(0.85f),
    NORMAL(1.0f),
    LARGE(1.15f),
    HUGE(1.30f),
}

/// ثيم القراءة — مستقل عن ثيم التطبيق.
///
/// ⚠️ لماذا مستقل: من يقرأ القرآن قبل النوم يريد شاشة سوداء وإن كان
/// تطبيقه كله فاتحًا، ومن يقرأ في النهار يريد ورقًا مريحًا وإن كان
/// تطبيقه داكنًا. القراءة طقس آخر لا تابع للإعداد العام.
enum class ReaderTheme { NORMAL, NIGHT, SEPIA }

/// إعدادات المظهر مجتمعة — تُقرأ مرة وتُمرَّر، فلا يُفاتح التخزين
/// في كل إعادة تركيب.
data class Appearance(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val fontScale: FontScaleLevel = FontScaleLevel.NORMAL,
    val readerTheme: ReaderTheme = ReaderTheme.NORMAL,
)

object AppearanceStore {

    private const val PREFS_NAME = "athar.settings.v1"
    private const val KEY_THEME = "theme_mode"
    private const val KEY_FONT = "font_scale"
    private const val KEY_READER = "reader_theme"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(context: Context): Appearance {
        val p = prefs(context)
        return Appearance(
            // ⚠️ قراءة متسامحة: قيمة مجهولة (من إصدار أقدم أو ملف
            // مكتوب باليد) تُرجع الأصل ولا تُسقط التطبيق.
            themeMode = runCatching {
                ThemeMode.valueOf(p.getString(KEY_THEME, null) ?: ThemeMode.SYSTEM.name)
            }.getOrDefault(ThemeMode.SYSTEM),
            fontScale = runCatching {
                FontScaleLevel.valueOf(p.getString(KEY_FONT, null) ?: FontScaleLevel.NORMAL.name)
            }.getOrDefault(FontScaleLevel.NORMAL),
            readerTheme = runCatching {
                ReaderTheme.valueOf(p.getString(KEY_READER, null) ?: ReaderTheme.NORMAL.name)
            }.getOrDefault(ReaderTheme.NORMAL),
        )
    }

    fun save(context: Context, appearance: Appearance) {
        prefs(context).edit {
            putString(KEY_THEME, appearance.themeMode.name)
            putString(KEY_FONT, appearance.fontScale.name)
            putString(KEY_READER, appearance.readerTheme.name)
        }
    }
}
