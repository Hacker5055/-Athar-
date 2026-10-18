package com.athar.athar

import com.athar.athar.prayer.Cities
import com.athar.athar.prayer.CitySearch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CitySearchTest {

    @Test fun findsByArabic() {
        assertTrue(CitySearch.search("القاهرة").any { it.id == "cairo" })
    }

    @Test fun findsByLatin() {
        assertTrue(CitySearch.search("cairo").any { it.id == "cairo" })
        assertTrue(CitySearch.search("Le Caire").any { it.id == "cairo" })
    }

    /// ⚠️ «القاهره» بالهاء المفتوحة — البحث بلا تطبيع يجعلها غير موجودة
    @Test fun findsWithLooseSpelling() {
        assertTrue(CitySearch.search("القاهره").any { it.id == "cairo" })
        assertTrue(CitySearch.search("القاهره").isNotEmpty())
    }

    @Test fun ignoresDiacritics() {
        assertTrue(CitySearch.search("القَاهِرَة").any { it.id == "cairo" })
    }

    @Test fun unifiesAlefVariants() {
        assertEquals("السلام", CitySearch.normalize("السًلام".replace("ً", "")))
        assertEquals("ا", CitySearch.normalize("أ"))
        assertEquals("ا", CitySearch.normalize("إ"))
        assertEquals("ا", CitySearch.normalize("آ"))
    }

    @Test fun emptyQueryReturnsEverything() {
        assertEquals(Cities.all.size, CitySearch.search("").size)
        assertEquals(Cities.all.size, CitySearch.search("   ").size)
    }

    /// ⚠️ بيانات القائمة نفسها: معرّف مكرّر يعني مدينة تختفي من الفهرس
    @Test fun cityDataIsSane() {
        assertTrue("القائمة فارغة", Cities.all.isNotEmpty())
        assertEquals(Cities.all.size, Cities.all.map { it.id }.toSet().size)
        Cities.all.forEach { city ->
            assertTrue("${city.id}: خط العرض", city.latitude in -90.0..90.0)
            assertTrue("${city.id}: خط الطول", city.longitude in -180.0..180.0)
            assertTrue("${city.id}: المنطقة الزمنية", city.timeZone.contains("/"))
            assertTrue("${city.id}: الاسم العربي", city.nameAr.isNotBlank())
        }
    }
}
