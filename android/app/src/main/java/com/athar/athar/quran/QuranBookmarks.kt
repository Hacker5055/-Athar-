package com.athar.athar.quran

/// علامات الآيات (المفضلة) — المنطق النقي، بلا `Context` وبلا قرص.
///
/// ⚖️ لماذا ملفّ منفصل عن المخزن؟ لأن القرار — أي مفتاح يصحّ وأيّها
/// يُرفض — هو ما نريد اختباره. والمخزن مجرد نقل لا يُختبر بلا جهاز.
object QuranBookmarks {

    private val PATTERN = Regex("""(\d{1,3}):(\d{1,3})""")

    /// مفتاح آية: «٢:٢٥٥».
    ///
    /// ⚠️ السورة والآية معًا: علامة على آية في البقرة ليست علامة على
    /// آية برقمها في آل عمران.
    fun key(surah: Int, ayah: Int): String = "$surah:$ayah"

    fun contains(keys: Set<String>, surah: Int, ayah: Int): Boolean =
        key(surah, ayah) in keys

    /// يضيف العلامة إن كانت غائبة ويحذفها إن كانت حاضرة.
    fun toggle(keys: Set<String>, surah: Int, ayah: Int): Set<String> {
        val k = key(surah, ayah)
        return if (k in keys) keys - k else keys + k
    }

    /// فكّ الترميز — متسامح عموديًا.
    ///
    /// ⚠️ التخزين قديم أو مكتوب بيد أو من نسخة أقدم؛ وكل احتمال يجب أن
    /// ينتهي إلى **مجموعة** لا إلى استثناء. ومفتاح لا يوافق مقامًا
    /// معروفًا يُسقَط: علامة على آية ٩٩٩ لا معنى لها، والإبقاء عليها
    /// يُنبت سطرًا فارغًا في قائمة المفضلة لا يُحذف.
    fun decode(raw: String?): Set<String> {
        if (raw.isNullOrBlank()) return emptySet()
        val out = LinkedHashSet<String>()
        for (m in PATTERN.findAll(raw)) {
            val surah = m.groupValues[1].toIntOrNull() ?: continue
            val ayah = m.groupValues[2].toIntOrNull() ?: continue
            if (surah !in 1..114) continue
            if (ayah < 1 || ayah > QuranMeta.ayahCount(surah)) continue
            out.add(key(surah, ayah))
        }
        return out
    }

    fun encode(keys: Set<String>): String =
        keys.mapNotNull { key ->
            val parts = key.split(':')
            val surah = parts.getOrNull(0)?.toIntOrNull() ?: return@mapNotNull null
            val ayah = parts.getOrNull(1)?.toIntOrNull() ?: return@mapNotNull null
            if (surah !in 1..114) return@mapNotNull null
            if (ayah < 1 || ayah > QuranMeta.ayahCount(surah)) return@mapNotNull null
            key(surah, ayah)
        }
            .sortedWith(compareBy({ it.substringBefore(':').toInt() }, { it.substringAfter(':').toInt() }))
            .joinToString(",")

    /// تجميع العلامات بسورها — لقراءة نصّ كل سورة **مرة واحدة** لا
    /// مرّة لكل علامة.
    fun bySurah(keys: Set<String>): Map<Int, List<Int>> =
        keys.mapNotNull { key ->
            val surah = key.substringBefore(':').toIntOrNull() ?: return@mapNotNull null
            val ayah = key.substringAfter(':').toIntOrNull() ?: return@mapNotNull null
            surah to ayah
        }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, ayahs) -> ayahs.sorted() }
            .toSortedMap()
}
