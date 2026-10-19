package com.athar.athar.quran
import com.athar.athar.i18n.AppLocale

/// سورة — بيانات الفهرس فقط.
data class Surah(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val ayahCount: Int,
    val isMeccan: Boolean,
)

/// آية واحدة بالنص العثماني.
/// اسمُ السورةِ بلغةِ الواجهة.
///
/// ⚠️ **العربيةُ في العربية واللاتينيةُ في غيرها**: تطبيقُ بثلاثِ
/// لغاتٍ يعرضُ أسماءَ السورِ بالعربيةِ دائمًا — فيرى Francophone
/// قائمةً لا يعرف اسمًا واحدًا منها. واسمُ السورةِ اللاتينيُّ
/// («Al-Baqarah») هو المتَّبعُ في كلِّ مصاحفِ العالم.
fun Surah.displayName(locale: AppLocale): String =
    if (locale.isRtl) nameAr else nameEn

/// اسمُ القارئِ بلغةِ الواجهة — كالسورةِ تمامًا.
fun Reciter.displayName(locale: AppLocale): String =
    if (locale.isRtl) nameAr else nameEn

data class Ayah(
    val surah: Int,
    val number: Int,
    val text: String,
)

/// ⚖️ نسبة المصدر — شرط الرخصة لا ميزة.
///
/// النص العثماني من Tanzania بموجب CC BY 3.0، وهي تجيز إعادة النشر
/// مع النسبة وتمنع **تعديل النص**. لذا نعرض النص كما هو حرفيًا،
/// ولا نعيد تشكيله ولا نطبّع حروفه للتخزين.
object QuranSources {
    const val TEXT_SOURCE = "tanzil (tanzil.net) — CC BY 3.0"
    const val TEXT_SOURCE_URL = "https://tanzil.net/"
    const val REPO_MIRROR = "risan/quran-json"
}
