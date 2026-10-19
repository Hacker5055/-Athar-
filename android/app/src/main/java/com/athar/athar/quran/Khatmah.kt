package com.athar.athar.quran

/// خطة ختمة — تقسيم المصحف على أيام معلومة (B13 وF5 من docs/02).
///
/// ⚠️ **حسابي لا جدولي**: الخطة ٣٠ أو ٦٠ أو ٩٠ يومًا تُقسَّم
/// بالعدّ لا تُقرأ من جدول مثبَّت، لأن المستخدم قد يختار غيرها،
/// والجدول لا يغطّي ما لم يُكتب.
object Khatmah {

    /// الخطط المعروضة.
    val PLANS: List<Int> = listOf(30, 60, 90)

    const val DEFAULT_DAYS = 30

    /// ⚠️ بالتقريب **لأعلى**: ٦٢٣٦ لا تقبل القسمة على ٣٠، والتقريب
    /// لأسفل يترك آيات بلا يوم، فلا تُختم الختمة أصلًا.
    fun perDay(days: Int): Int {
        val d = days.coerceAtLeast(1)
        return (QuranMeta.TOTAL_VERSES + d - 1) / d
    }

    /// كم يومًا تحتاج الخطة فعلًا.
    ///
    /// ⚠️ قد يكون **أقلّ** من المطلوب حين لا يقبل القسمة: ٦٢٣٦ على
    /// ٩٠ تعطي ٧٠ آية لليوم، و٦٢٣٦ ÷ ٧٠ = ٨٩٫٠٩ ⇒ ٩٠ يومًا،
    /// آخرها ناقص. واليوم الناقص مقبول، والآية الضائعة ليست.
    fun actualDays(days: Int): Int {
        val step = perDay(days)
        return (QuranMeta.TOTAL_VERSES + step - 1) / step
    }

    /// أول آية في اليوم (فهرس عالمي ١..٦٢٣٦).
    fun startOf(day: Int, days: Int): Int {
        val d = day.coerceAtLeast(1)
        val step = perDay(days)
        return ((d - 1) * step + 1).coerceAtMost(QuranMeta.TOTAL_VERSES)
    }

    /// آخر آية في اليوم.
    fun endOf(day: Int, days: Int): Int {
        val step = perDay(days)
        return (day.coerceAtLeast(1) * step).coerceAtMost(QuranMeta.TOTAL_VERSES)
    }

    /// اليوم الذي تقع فيه الآية.
    fun dayOf(verseId: Int, days: Int): Int {
        if (verseId !in 1..QuranMeta.TOTAL_VERSES) return 1
        return ((verseId - 1) / perDay(days)) + 1
    }

    /// ورد اليوم بصيغة مقروءة: (البداية، النهاية) كـ (سورة، آية).
    fun portionOf(day: Int, days: Int): Pair<Pair<Int, Int>, Pair<Int, Int>> =
        QuranMeta.surahAyahOf(startOf(day, days)) to QuranMeta.surahAyahOf(endOf(day, days))

    /// كم أُنجز من المصحف بآيةٍ معيّنة — نسبة مئوية.
    fun percent(verseId: Int): Int {
        if (verseId <= 0) return 0
        return ((verseId * 100L) / QuranMeta.TOTAL_VERSES).toInt().coerceIn(0, 100)
    }

    /// كم يومًا مضى من الخطة منذ تاريخ بدايتها.
    ///
    /// ⚠️ ` ChronoUnit.DAYS` على **التاريخ المحلي** لا على `Instant`:
    /// اليوم عند المستخدم يبدأ بمنتصف ليله لا بـ UTC، وحساب
    /// بالثواني يقدّم اليوم أو يؤخّره ساعات بحسب بلده.
    fun daySince(startEpochDay: Long, todayEpochDay: Long): Int =
        (todayEpochDay - startEpochDay + 1).coerceAtLeast(1).toInt()
}
