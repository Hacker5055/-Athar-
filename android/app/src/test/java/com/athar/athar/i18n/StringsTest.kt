package com.athar.athar.i18n

import com.athar.athar.prayer.PrayerMethod
import com.athar.athar.prayer.PrayerSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/// ⚠️ لماذا هذه الاختبارات أهمّ من شكلها؟
/// لأن الترجمة الناقصة **لا تُسقط البناء ولا تُظهر خطأً**: المفتاح
/// الناقص يرجع إلى العربية بصمت، فيرى المستخدم العربي كل شيء سليمًا،
/// ولا يكتشف الفرنسي النقص إلا إن فتح التطبيق بالفرنسية. ولا أحد
/// يفتح التطبيق بثلاث لغات ليتأكد.
class StringsTest {

    private val ar = Strings(AppLocale.AR)
    private val en = Strings(AppLocale.EN)
    private val fr = Strings(AppLocale.FR)

    @Test fun arabicIsReferenceAndNotEmpty() {
        assertTrue("العربية فارغة", Strings.kStringsAr.isNotEmpty())
        Strings.kStringsAr.forEach { (key, value) ->
            assertTrue("مفتاح فارغ", key.isNotBlank())
            assertTrue("نص فارغ للمفتاح $key", value.isNotBlank())
        }
    }

    /// ⚠️ أهم اختبار في الملف: مفتاح في لغة دون أخرى يعني شاشة
    /// نصفها مترجم
    @Test fun allLocalesHaveTheSameKeys() {
        val reference = Strings.kStringsAr.keys
        for ((name, bundle) in listOf(
            "en" to Strings.kStringsEn,
            "fr" to Strings.kStringsFr,
        )) {
            val missing = reference - bundle.keys
            val extra = bundle.keys - reference
            assertTrue("$name يفتقد: $missing", missing.isEmpty())
            assertTrue("$name يزيد: $extra", extra.isEmpty())
        }
    }

    @Test fun everyValueIsTranslatedNotCopiedFromArabic() {
        // ⚠️ استثناء: المفاتيح التي **يجب** أن تتطابق (أسماء أعلام،
        // أرقام، رموز) تتطابق حقًّا في كل اللغات.
        val identicalByDesign = setOf("app_name", "notif_channel_athan")
        val untranslated = mutableListOf<String>()
        Strings.kStringsAr.forEach { (key, arabic) ->
            if (key in identicalByDesign) return@forEach
            for (locale in listOf(AppLocale.EN, AppLocale.FR)) {
                val value = Strings(locale).t(key)
                // نص عربي في لغة لاتينية = مفتاح لم يُترجم
                if (value == arabic && arabic.any { it in '؀'..'ۿ' }) {
                    untranslated += "$key/$locale"
                }
            }
        }
        assertTrue("مفاتيح لم تُترجم: $untranslated", untranslated.isEmpty())
    }

    @Test fun eachLocaleReturnsItsOwnText() {
        assertEquals("الرئيسية", ar.t("tab_home"))
        assertEquals("Home", en.t("tab_home"))
        assertEquals("Accueil", fr.t("tab_home"))
        assertEquals("Qibla direction", en.t("qibla_title"))
        assertEquals("Direction de la Qibla", fr.t("qibla_title"))
    }

    @Test fun unknownKeyReturnsTheKeyNotBlank() {
        // ⚠️ المفتاح الظاهر للمستخدم أفضل من نصّ فارغ يكسر التخطيط —
        // وهو أيضًا ما يجعل النقص **مرئيًا** بدل أن يُخفى
        assertEquals("nope", en.t("nope"))
        assertEquals("nope", fr.t("nope"))
    }

    @Test fun tArgsSubstitutesPlaceholders() {
        val text = ar.tArgs("notif_reminder_body", "n" to 15)
        assertTrue("«$text» لا يحوي ١٥", text.contains("15"))
        assertTrue("«$text» ما زال يحوي {n}", !text.contains("{n}"))
    }

    /// ⚠️ الواجهة تترجم أسماء الصلوات بـ `stringKey` لا بـ `arabicName`
    @Test fun everyPrayerSlotHasAKeyInEveryLocale() {
        for (slot in PrayerSlot.entries) {
            for (strings in listOf(ar, en, fr)) {
                val value = strings.t(slot.stringKey)
                assertNotEquals(
                    "${slot.name} بلا ترجمة في ${strings.locale}",
                    slot.stringKey,
                    value,
                )
            }
        }
    }

    /// ⚠️ طرق الحساب كانت **آخر نصّ عربي ثابت** في الواجهة. و`t()` يرجع
    /// المفتاح نفسه عند النقص، فالتوقّع «القيمة ≠ المفتاح» يكشف أي مفتاح
    /// مخترع — وهو بالضبط خطأ `s.sunriseNoAdhan` الذي لم يمسكه مترجم.
    @Test fun everyPrayerMethodHasAKeyInEveryLocale() {
        for (method in PrayerMethod.entries) {
            for (strings in listOf(ar, en, fr)) {
                assertNotEquals(
                    "${method.name} بلا ترجمة في ${strings.locale}",
                    method.nameKey,
                    strings.t(method.nameKey),
                )
            }
        }
    }

    @Test fun localeDetectionIsForgiving() {
        assertEquals(AppLocale.AR, AppLocale.fromCode("ar"))
        assertEquals(AppLocale.FR, AppLocale.fromCode("fr"))
        assertEquals(AppLocale.AR, AppLocale.fromCode(null))
        assertEquals(AppLocale.AR, AppLocale.fromCode("zz"))
        // ⚠️ القائمة مرتّبة حسب **تفضيل المستخدم**: الأولى هي المفضّلة.
        // كتبتُ هذا التوقّع أول مرة مقلوبًا (ظننتها ترجع الإنجليزية من
        // ["fr-CA","en-US"]) ففشل البناء — وكان الكود على صواب وأنا
        // المخطئ: من يضع الفرنسية أولًا يريد الفرنسية فعلًا.
        assertEquals(AppLocale.FR, AppLocale.fromTags(listOf("fr-CA", "en-US")))
        assertEquals(AppLocale.EN, AppLocale.fromTags(listOf("en-US", "fr-CA")))
        assertEquals(AppLocale.FR, AppLocale.fromTags(listOf("fr-FR")))
        assertEquals(AppLocale.AR, AppLocale.fromTags(emptyList()))
        assertEquals(AppLocale.AR, AppLocale.fromTags(listOf("de-DE", "zh-CN")))
    }

    @Test fun onlyArabicIsRightToLeft() {
        assertTrue(AppLocale.AR.isRtl)
        assertTrue(!AppLocale.EN.isRtl)
        assertTrue(!AppLocale.FR.isRtl)
    }
}
