package com.athar.athar.prayer

/// بحث المدن — **محض بالكامل** ويُختبر على JVM.
///
/// ⚠️ لماذا التطبيع؟ لأن المستخدم يكتب «القاهره» أو «القَاهِرَة» أو
/// «elqahira» ويقصد القاهرة. البحث دون تطبيع يجعل المدينة «غير موجودة»
/// وهي موجودة — أسوأ تجربة في شاشة اختيار المدينة.
object CitySearch {

    fun normalize(input: String): String {
        val out = StringBuilder(input.length)
        for (ch in input) {
            when (ch) {
                // التشكيل العربي والتطويل — تُحذف
                in 'ً'..'ْ', 'ـ' -> Unit
                'أ', 'إ', 'آ', 'ٱ' -> out.append('ا')
                'ى' -> out.append('ي')
                'ة' -> out.append('ه')
                'ؤ' -> out.append('و')
                'ئ' -> out.append('ي')
                else -> out.append(ch.lowercaseChar())
            }
        }
        return out.toString()
    }

    /// يبحث بالعربية والإنجليزية والفرنسية — لأن أثر ثلاثي اللغة.
    fun search(query: String, cities: List<City> = Cities.all): List<City> {
        val q = normalize(query.trim())
        if (q.isEmpty()) return cities
        return cities.filter { city ->
            normalize(city.nameAr).contains(q) ||
                normalize(city.nameEn).contains(q) ||
                normalize(city.nameFr).contains(q)
        }
    }
}
