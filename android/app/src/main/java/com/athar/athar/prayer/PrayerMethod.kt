package com.athar.athar.prayer

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
enum class PrayerMethod(val arabicName: String) {
    EGYPTIAN("الهيئة المصرية للمساحة"),
    UMM_AL_QURA("أم القرى — مكة"),
    MUSLIM_WORLD_LEAGUE("رابطة العالم الإسلامي"),
    NORTH_AMERICA("ISNA — أمريكا الشمالية"),
    FRANCE_15("فرنسا ١٥°"),
    OTHER("مخصّص"),
    ;

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
