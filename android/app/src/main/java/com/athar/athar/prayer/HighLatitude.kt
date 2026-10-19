package com.athar.athar.prayer

import com.batoulapps.adhan.HighLatitudeRule

/// قواعد خطوط العرض العالية (A5 من docs/02).
///
/// ⚠️ لماذا `valueOf` داخل `runCatching` لا ثوابت المكتبة مباشرة؟
/// لأن أسماء ثوابت [HighLatitudeRule] محفوظة — وقد تختلف باختلاف
/// إصدار المكتبة. والإحالة المباشرة تجعل الاختلاف **خطأ بناء** يقف
/// وحده، أمّا `valueOf` فيجعله `null` يُطبَّق معه الافتراضي، ويكشفه
/// اختبار `everyRuleResolves` صريحًا.
///
/// يعني ذلك: التطبيق يعمل في أوروبا وإن اختلفت المكتبة، والذي
/// ينكسر هو الاختبار لا المستخدم.
enum class HighLatitude(val nameKey: String, val ruleName: String) {
    MIDDLE_NIGHT("hl_middle", "MIDDLE_OF_THE_NIGHT"),
    SEVENTH("hl_seventh", "SEVENTH_OF_THE_NIGHT"),
    TWILIGHT("hl_twilight", "TWILIGHT_ANGLE"),
    ;

    companion object {
        /// ⚠️ الافتراضي **وسط الليل** لأنه افتراضي المكتبة نفسها:
        /// من لم يختر شيئًا يحصل على ما كانت تعطيه إيّاه قبل أن
        /// توجد هذه الشاشة أصلًا.
        val DEFAULT: HighLatitude = MIDDLE_NIGHT

        fun fromName(name: String?): HighLatitude =
            entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}

object HighLatitudeRules {
    fun resolve(rule: HighLatitude): HighLatitudeRule? =
        runCatching { HighLatitudeRule.valueOf(rule.ruleName) }.getOrNull()
}
