package com.athar.athar.quran

import kotlin.math.ceil

/// وضع الحفظ (B14 من docs/02) — المنطق النقي.
///
/// ⚠️ نحفظ **معرّف الآية العام** (١…٦٢٣٦) لا (سورة، آية): الحفظ
/// يمتدّ عبر السور، ومفتاح واحد يجعل المقارنة والترتيب والتجميع
/// كلها عمليات على أعداد صحيحة.
object Hifz {

    /// كم تكرارًا قبل أن تُعدّ الآية محفوظة.
    ///
    /// ⚠️ خمسة لا واحد: من راجع آية مرّة واحدة نسيها غدًا. والرقم
    /// ليس رأيًا — هو أقلّ ما يجعل المراجعة مراجعة.
    const val REPEAT_TARGET = 5

    /// أعلى درجة إخفاء: ٠ = النص كاملًا، ٣ = النص كله مخفيًّا.
    const val MAX_LEVEL = 3

    private const val MASK = "◌◌◌"

    /// معرّفات الآيات من موضع إلى موضع، **شاملًا الطرفين**.
    ///
    /// ⚠️ قد يمتدّ النطاق عبر السور: من البقرة ٢٨٥ إلى آل عمران ٥
    /// نطاق واحد متّصل، ولو كان بالـ (سورة، آية) لصار حلقتين
    /// متداخلتين.
    fun ids(fromSurah: Int, fromAyah: Int, toSurah: Int, toAyah: Int): List<Int> {
        val start = QuranMeta.verseId(fromSurah, fromAyah)
        val end = QuranMeta.verseId(toSurah, toAyah)
        if (start <= 0 || end <= 0) return emptyList()
        // ⚠️ نطاق معكوس = لا شيء، لا `downTo`: من بدأ من آية متأخّرة
        // سهوًا يجب أن يرى فارغًا لا قائمة مقلوبة يحفظها بالخطأ.
        if (end < start) return emptyList()
        return (start..end).toList()
    }

    /// كم أُنجز من النطاق.
    ///
    /// ⚠️ **من النطاق** لا من المصحف: من حفظ نصف البقرة أراد أن
    /// يرى ٥٠٪ لا ٤٪.
    fun percent(memorized: Set<Int>, ids: List<Int>): Int {
        if (ids.isEmpty()) return 0
        val done = ids.count { it in memorized }
        return ((done * 100L) / ids.size).toInt().coerceIn(0, 100)
    }

    fun isComplete(memorized: Set<Int>, ids: List<Int>): Boolean =
        ids.isNotEmpty() && ids.all { it in memorized }

    /// أول آية لم تُحفظ بعد.
    ///
    /// ⚠️ **بترتيب المصحف** لا بترتيب المجموعة: `Set` غير مرتّب،
    /// وبدون المرور على `ids` يبدأ الحافظ من حيث اتفق.
    fun nextToMemorize(memorized: Set<Int>, ids: List<Int>): Int? =
        ids.firstOrNull { it !in memorized }

    /// آية للمراجعة: آخر ما حُفظ يُراجَع أولًا.
    ///
    /// ⚠️ **الأحدث أولًا**: الذي حفظته أمس أولى بالمراجعة ممّا
    /// حفظته العام الماضي، والقديم لم يَنْسَه صاحبه.
    fun nextToReview(memorized: Set<Int>, order: List<Int>): Int? =
        order.lastOrNull { it in memorized }

    /// درجة الإخفاء بعد [repeats] تكرارًا.
    ///
    /// ⚠️ الإخفاء **يشتدّ** مع التكرار: المرّة الأولى يقرأ الحافظ
    /// النص كاملًا ليثبّته، ثم يُسحب منه كلمة كلمة حتّى يسترجعه
    /// من ذاكرته وحدها.
    fun levelOf(repeats: Int): Int = when {
        repeats <= 1 -> 0
        repeats == 2 -> 1
        repeats == 3 -> 2
        else -> MAX_LEVEL
    }

    fun isMemorized(repeats: Int): Boolean = repeats >= REPEAT_TARGET

    /// يُخفي آخر [level]/[MAX_LEVEL] من كلمات الآية.
    ///
    /// ⚠️ **النص لا يُبدَّل حرفًا**: نضع قناعًا على الكلمات ونترك
    /// الباقي كما هو. النص العثماني CC BY 3.0 يمنع التعديل
    /// (`docs/09`)، وتطبيع حرف واحد تعديل.
    fun mask(text: String, level: Int): String {
        if (level <= 0 || text.isBlank()) return text
        val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.isEmpty()) return text
        val hidden = if (level >= MAX_LEVEL) words.size
        else ceil(words.size * level.toDouble() / MAX_LEVEL).toInt()
        val cut = (words.size - hidden).coerceAtLeast(0)
        return words.mapIndexed { i, w -> if (i < cut) w else MASK }.joinToString(" ")
    }
}
