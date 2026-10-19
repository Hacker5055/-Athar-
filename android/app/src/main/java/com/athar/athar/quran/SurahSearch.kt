package com.athar.athar.quran

import com.athar.athar.core.ArabicText

/// البحث عن سورة **باسمها** (B12) — لا بنصّها.
///
/// ⚠️ البحث في النصّ وحده يفوّت من كتب «الكهف» يريد السورة
/// لا موضع الآية. والاسم **يُنقَّى كالنصّ**: «البقره» و«البقرة»
/// اسمٌ واحد، ومن كتب «ابقره» يريد «البقرة».
object SurahSearch {

    /// السور التي يطابقها الاسم — **أسماؤها العربية والإنجليزية**.
    fun byName(query: String, surahs: List<Surah>): List<Surah> {
        val needle = ArabicText.normalize(query.trim())
        if (needle.isEmpty()) return emptyList()
        return surahs.filter { surah ->
            ArabicText.normalize(surah.nameAr).contains(needle) ||
                surah.nameEn.lowercase().contains(needle)
        }
    }

    /// تصفية السور بمكان النزول.
    ///
    /// ⚠️ `null` = الكلّ. و«مكية» ليست درجةً في التنزّل: أكثر
    /// السور مكّية نزولًا في مكة، وبعضها نزولًا في مكة ثم
    /// قصّفت في المدينة. فالفصل **تقريبيّ** ومقصود.
    fun byOrigin(surahs: List<Surah>, meccanOnly: Boolean?, medinanOnly: Boolean?): List<Surah> =
        surahs.filter { surah ->
            val meccan = surah.isMeccan
            when {
                meccanOnly == true -> meccan
                medinanOnly == true -> !meccan
                else -> true
            }
        }
}
