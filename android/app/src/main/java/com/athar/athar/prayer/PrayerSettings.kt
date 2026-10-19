package com.athar.athar.prayer

import com.batoulapps.adhan.Madhab

data class PrayerSettings(
    val method: PrayerMethod = PrayerMethod.EGYPTIAN,
    /// المذهب: [Madhab.SHAFI] أو [Madhab.HANAFI] — يغيّر وقت العصر فقط.
    val madhab: Madhab = Madhab.SHAFI,
    val latitude: Double = 30.0444,
    val longitude: Double = 31.2357,
    /// ⚠️ نحفظ **المعرّف** لا الاسم: الاسم يُترجم حسب اللغة، والمعرّف ثابت.
    /// `null` = إحداثيات يدوية أو تحديد تلقائي بالموقع.
    val cityId: String? = "cairo",
    val placeName: String = "",
    /// ⚠️ قاعدة خطوط العرض العالية: بلا تطبيقها لا يُحسب فجر ولا
    /// عشاء في أوروبا شتاءً — إمّا أن يغيبا أو يتطرّفا.
    val highLatitude: HighLatitude = HighLatitude.DEFAULT,
    /// ⚠️ فروق **بالدقائق** لكل صلاة (A4): الحساب الفلكي لا يطابق
    /// توقيت المسجد، وفرق الدقائق هو الذي يفطر عليه الصائم.
    ///
    /// ⚠️ والمفتاح **ثابت التعداد** لا اسمه المعرَّض للترجمة.
    val adjustments: Map<PrayerSlot, Int> = emptyMap(),
    /// ⚠️ **هل اختار المستخدم مكانًا؟**: الموقعُ الافتراضيُّ القاهرة،
    /// فمن لم يختار مكانه يرى مواقيتَ القاهرة وهو يظنّها مواقيتَ
    /// مدينته — فالسؤالُ يُسأل في الصفحة الرئيسية حتى يختار.
    val locationChosen: Boolean = false,
)
