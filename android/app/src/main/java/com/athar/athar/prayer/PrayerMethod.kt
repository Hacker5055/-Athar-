package com.athar.athar.prayer

import com.athar.athar.i18n.Strings
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.CalculationParameters

/// طريقة حساب المواقيت.
///
/// ⚠️ تم التحقق من [CalculationMethod] مقابل **مصدر المكتبة** لا من
/// الذاكرة. المكتبة تحمل: MUSLIM_WORLD_LEAGUE · EGYPTIAN · KARACHI ·
/// UMM_AL_QURA · DUBAI · MOON_SIGHTING_COMMITTEE · NORTH_AMERICA ·
/// KUWAIT · QATAR · SINGAPORE · OTHER.
///
/// ⚠️ [FRANCE_15] **غير موجودة** في المكتبة — بنيناها يدويًا بزاويتَي
/// ١٥/١٥. وكذلك [HighLatitudeRule] فيه ثلاث قيم فقط ولا يحتوي `NONE`.
enum class PrayerMethod(val nameKey: String) {
    EGYPTIAN("method_egyptian"),
    UMM_AL_QURA("method_umm_al_qura"),
    MUSLIM_WORLD_LEAGUE("method_mwl"),
    NORTH_AMERICA("method_isna"),
    FRANCE_15("method_france"),
    OTHER("method_custom"),
    ;

    /// ⚠️ الاسم يتبع لغة الواجهة. كان نصًّا عربيًا ثابتًا (`arabicName`)
    /// فظهر «الهيئة المصرية للمساحة» عربيًّا لمن يقرأ الفرنسية — وهو
    /// الخطأ نفسه الذي أصلحناه في أسماء الصلوات بـ `stringKey`.
    fun name(strings: Strings): String = strings.t(nameKey)

    fun parameters(): CalculationParameters = when (this) {
        EGYPTIAN -> CalculationMethod.EGYPTIAN.parameters
        UMM_AL_QURA -> CalculationMethod.UMM_AL_QURA.parameters
        MUSLIM_WORLD_LEAGUE -> CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        NORTH_AMERICA -> CalculationMethod.NORTH_AMERICA.parameters
        // ⚠️ ليست في المكتبة — زاوية فجر ١٥ وعشاء ١٥
        FRANCE_15 -> CalculationParameters(15.0, 15.0, CalculationMethod.OTHER)
        OTHER -> CalculationParameters(18.0, 17.0, CalculationMethod.OTHER)
    }
}
