package com.athar.athar.prayer

import com.athar.athar.core.ArabicText

/// بحث المدن — **محض بالكامل** ويُختبر على JVM.
///
/// ⚠️ لماذا التطبيع؟ لأن المستخدم يكتب «القاهره» أو «القَاهِرَة» أو
/// «elqahira» ويقصد القاهرة. البحث دون تطبيع يجعل المدينة «غير موجودة»
/// وهي موجودة — أسوأ تجربة في شاشة اختيار المدينة.
object CitySearch {

    fun normalize(input: String): String = ArabicText.normalize(input)

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
