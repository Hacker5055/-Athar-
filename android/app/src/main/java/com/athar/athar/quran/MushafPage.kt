package com.athar.athar.quran

/// صفحات المصحف (B2 من docs/02) — منطق نقي بلا واجهة ولا سياق.
///
/// ⚠️ **لماذا نصٌّ لا صور؟** وضع المصحف في الأصل صورٌ لـ ٦٠٤
/// صفحة، ولكلّها رخصة لم نجدها. فالبديل المنفَّذ صفحةٌ **نصّية**
/// حدودها من `quran-meta` (MIT) ونصّها من تانزيل (CC BY 3.0):
/// تُقرأ كما تُقرأ في المصحف الورقي — آيةً آية — لكنّها ليست صورة
/// المطبوع. وهذا وحده أبقى من ادّعاء المطابقة.
///
/// ⚠️ والصفحة **ليست سورة**: صفحة تبدأ في وسط سورة وتنتهي في
/// وسطها. فمن ظنّ الصفحة سورةً أضاع أكثرَ ممّن يبدو.
object MushafPage {

    /// عدد صفحات المصحف المدني برواية حفص.
    const val COUNT = QuranMeta.PAGE_COUNT

    fun isValid(page: Int): Boolean = page in 1..COUNT

    /// أول آية في الصفحة — **فهرس عالمي** (١..٦٢٣٦).
    fun firstVerseId(page: Int): Int = QuranMeta.firstIdOfPage(page)

    /// آخر آية في الصفحة — فهرس عالمي.
    fun lastVerseId(page: Int): Int = QuranMeta.lastIdOfPage(page)

    /// أول سورة تمسّها الصفحة.
    fun firstSurah(page: Int): Int =
        if (isValid(page)) QuranMeta.surahAyahOf(firstVerseId(page)).first else 0

    /// آخر سورة تمسّها الصفحة.
    fun lastSurah(page: Int): Int =
        if (isValid(page)) QuranMeta.surahAyahOf(lastVerseId(page)).first else 0

    /// السور التي تمسّها الصفحة — **مدى متّصل**، لأنّ الآيات
    /// متسلسلة والسور لا تتداخل في الصفحة.
    fun surahsOnPage(page: Int): IntRange =
        if (isValid(page)) firstSurah(page)..lastSurah(page) else IntRange.EMPTY

    /// هل تبدأ سورة **في هذه الصفحة** (لا أنّ الصفحة تبدأ في
    /// وسط سورة بدأت قبلها)؟
    ///
    /// ⚠️ الفرق مهم: صفحة تبدأ في آخر سورة البقرة فيها سورة
    /// آل عمران في آخرها — هذه صفحة **تكمِل** سورة لا صفحة
    /// **تبدأ** سورة. والفرق في العنوان: «سورة البقرة» أو
    /// «البقرة · آل عمران».
    fun startsSurah(page: Int): Boolean {
        if (!isValid(page)) return false
        val (surah, ayah) = QuranMeta.surahAyahOf(firstVerseId(page))
        return ayah == 1
    }

    /// الآيات **بترتيبها** على الصفحة.
    fun verseIds(page: Int): IntRange {
        if (!isValid(page)) return IntRange.EMPTY
        return firstVerseId(page)..lastVerseId(page)
    }

    /// عدد آيات الصفحة.
    fun verseCount(page: Int): Int =
        if (isValid(page)) lastVerseId(page) - firstVerseId(page) + 1 else 0
}
