package com.athar.athar.quran

import android.content.Context
import java.nio.charset.StandardCharsets

/// يقرأ القرآن من أصول التطبيق.
///
/// ⚠️ لماذا الأصول لا الشبكة؟ لأن أثر **أوفلاين أولًا** (`docs/05` §1).
/// النص يُنزَّل **وقت البناء** في CI ويوضع في `assets/quran/`، فلا
/// يوجد في git قط ( قرار `docs/09` §1 ) ويبقى متاحًا بلا إنترنت.
class QuranRepository(private val context: Context) {

    private val surahsLazy: List<Surah> by lazy {
        QuranAssetParser.parseChapters(read("quran/chapters.json"))
    }
    private val ayahsLazy: Map<Int, List<Ayah>> by lazy {
        QuranAssetParser.parseUthmani(read("quran/uthmani.json"))
    }

    fun surahs(): List<Surah> = surahsLazy

    fun ayahsOf(surahId: Int): List<Ayah> = ayahsLazy[surahId].orEmpty()

    private fun read(path: String): String =
        context.assets.open(path).use { stream ->
            stream.readBytes().toString(StandardCharsets.UTF_8)
        }
}
