package com.athar.athar.ui.theme

import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات المظهر — ما يُقاس منه: الترتيب، والتباين، والشفافية.
///
/// ⚠️ لماذا نختبر ألوانًا: ثيم القراءة يُقرأ ساعات، والتباين الضعيف
/// فيها إيذاء للعين يظهر بعد الاستعمال لا في مراجعة الكود. وما
/// يمكن قياسه لا يُترك للانطباع.
class AppearanceTest {

    // ── حجم الخط ─────────────────────────────────────────

    @Test
    fun fontScalesIncreaseMonotonically() {
        val scales = FontScaleLevel.entries.map { it.scale }
        assertEquals(scales, scales.sorted())
        assertTrue("مقاسان متساويان لا فرق بينهما", scales.toSet().size == scales.size)
    }

    @Test
    fun normalFontScaleChangesNothing() {
        // ⚠️ الأصل لا يُضاعف: من لم يغيّر الإعداد يرى التطبيق كما هو
        assertEquals(1.0f, FontScaleLevel.NORMAL.scale, 0.0001f)
    }

    @Test
    fun scalesStraddleTheNeutralPoint() {
        assertTrue(FontScaleLevel.SMALL.scale < 1.0f)
        assertTrue(FontScaleLevel.LARGE.scale > 1.0f)
        assertTrue(FontScaleLevel.HUGE.scale > FontScaleLevel.LARGE.scale)
    }

    @Test
    fun smallestScaleStaysReadable() {
        // ⚠️ «صغير» لا يعني غير مقروء: ٠٫٨٥ على خط ١٤sp يبقى ١٢sp،
        // وهو الحدّ الأدنى المعقول لا ما دونه.
        assertTrue(FontScaleLevel.SMALL.scale >= 0.8f)
    }

    // ── ثيم القراءة ──────────────────────────────────────

    @Test
    fun normalReaderThemeIsTransparent() {
        // ⚠️ «عادي» يعني اتباع ثيم التطبيق لا فرض لون: من اختار
        // الداكن يرى قراءة داكنة بلا أن يختار ذلك مرتين.
        assertFalse(ReaderPalette.NORMAL.isCustom)
        assertFalse(ReaderPalette.of(ReaderTheme.NORMAL).isCustom)
    }

    @Test
    fun nightAndSepiaAreCustomThemes() {
        assertTrue(ReaderPalette.of(ReaderTheme.NIGHT).isCustom)
        assertTrue(ReaderPalette.of(ReaderTheme.SEPIA).isCustom)
    }

    @Test
    fun readerBackgroundsAreDistinct() {
        val backgrounds = ReaderTheme.entries
            .map { ReaderPalette.of(it) }
            .filter { it.isCustom }
            .map { it.background }
        assertEquals("ثيمان بلون واحد ليسا ثيمين", backgrounds.toSet().size, backgrounds.size)
    }

    @Test
    fun customThemesHaveReadableContrast() {
        // ⚠️ التباين بين النصّ والخلفية: ما دون نصف المدى يبدأ في
        // إجهاد العين على نصّ طويل.
        for (theme in ReaderTheme.entries) {
            val palette = ReaderPalette.of(theme)
            if (!palette.isCustom) continue
            val gap = kotlin.math.abs(
                palette.onBackground.luminance() - palette.background.luminance(),
            )
            assertTrue("$theme: التباين $gap ضعيف", gap > 0.5f)
        }
    }

    @Test
    fun nightIsDarkerThanSepia() {
        val night = ReaderPalette.of(ReaderTheme.NIGHT)
        val sepia = ReaderPalette.of(ReaderTheme.SEPIA)
        assertTrue(
            "الليلي يجب أن يكون الأغمق",
            night.background.luminance() < sepia.background.luminance(),
        )
        assertTrue(
            "الليلي نصّ فاتح على خلفية غامقة",
            night.onBackground.luminance() > night.background.luminance(),
        )
        assertTrue(
            "السيبيا نصّ غامق على خلفية فاتحة",
            sepia.onBackground.luminance() < sepia.background.luminance(),
        )
    }

    @Test
    fun nightIsNotPureBlack() {
        // ⚠️ الأسود الخالص على OLED يحرق الحروف في الظلام؛ الليلي
        // عندنا رماديّ غامق قريب منه لا هو.
        assertTrue(ReaderPalette.NIGHT.background.luminance() > 0f)
    }

    // ── الثيم ────────────────────────────────────────────

    @Test
    fun themeModeOffersThreeChoices() {
        assertEquals(3, ThemeMode.entries.size)
        assertTrue(ThemeMode.entries.contains(ThemeMode.SYSTEM))
    }

    @Test
    fun defaultAppearanceIsNeutral() {
        // ⚠️ الأصل محايد: لا يفرض ثيمًا ولا يكبّر خطًّا على من لم يختر
        val appearance = Appearance()
        assertEquals(ThemeMode.SYSTEM, appearance.themeMode)
        assertEquals(FontScaleLevel.NORMAL, appearance.fontScale)
        assertEquals(ReaderTheme.NORMAL, appearance.readerTheme)
    }
}
