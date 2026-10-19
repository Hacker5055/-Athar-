package com.athar.athar.ui.hijri

import androidx.compose.runtime.Composable
import com.athar.athar.core.ArabicDigits
import com.athar.athar.hijri.HijriCalendar
import com.athar.athar.i18n.LocalAppLocale
import com.athar.athar.i18n.LocalStrings
import java.time.LocalDate

/// نصّ التاريخ الهجري بلغة المستخدم.
///
/// ⚠️ الأرقام **عربية في العربية ولاتينية في غيرها**: الأرقام
/// الشرقية في واجهة فرنسية ليست ترجمة بل نقل بلا داع.
@Composable
fun hijriText(date: LocalDate, offset: Int = 0): String {
    val s = LocalStrings.current
    val locale = LocalAppLocale.current
    val h = HijriCalendar.from(date, offset)
    val day = if (locale.isRtl) ArabicDigits.of(h.day) else h.day.toString()
    val year = if (locale.isRtl) ArabicDigits.of(h.year) else h.year.toString()
    return "$day ${s.t(HijriCalendar.monthKey(h.month))} $year"
}
