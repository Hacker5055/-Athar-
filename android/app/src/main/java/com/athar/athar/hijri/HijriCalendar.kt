package com.athar.athar.hijri

import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

/// تاريخ هجري: يوم وشهر وسنة.
data class HijriDate(
    val day: Int,
    /// ١ .. ١٢ — يُترجم إلى اسم باللغة عند العرض.
    val month: Int,
    val year: Int,
)

/// التقويم الهجري (G1 وG2 من docs/02) — منطق نقي.
///
/// ⚠️ `HijrahDate` من `java.time` لا حساب من عندنا: التقويم الهجري
/// تقويم رصدي لا يُحسب بمعادلة بسيطة، وأي حساب نكتبه يخطئ في سنوات
/// بعينها. والمكتبة في JDK متاحة من API ٢٦ = `minSdk` التطبيق.
///
/// ⚖️ والتقويم **أم القرى** الذي تعتمده المكتبة تقويم حسابي معلوم،
/// لا رؤية شرعية؛ ولذا G2 — إزاحة ±يوم/يومين — ليست تفضيلًا بل
/// ضرورة: الرؤية تختلف ببلد المستخدم، ولا نملك أن نفرض عليه
/// تقويم بلد آخر.
object HijriCalendar {

    /// أقصى إزاحة مسموحة.
    ///
    /// ⚠️ بحدّ لا مفتوح: إزاحة بلا حدّ تُخرج تواريخ لا معنى لها،
    /// ويومان يكفيان كل خلاف رؤية معروف.
    const val MAX_OFFSET = 2

    fun from(gregorian: LocalDate, offsetDays: Int = 0): HijriDate {
        val shifted = gregorian.plusDays(offsetDays.toLong().coerceIn(-MAX_OFFSET.toLong(), MAX_OFFSET.toLong()))
        val hijri = HijrahDate.from(shifted)
        return HijriDate(
            day = hijri.get(ChronoField.DAY_OF_MONTH),
            month = hijri.get(ChronoField.MONTH_OF_YEAR),
            year = hijri.get(ChronoField.YEAR),
        )
    }

    fun monthKey(month: Int): String = "hijri_month_$month"

    /// اسماء الشهور — للاستخدامات التي لا تملك لغة (التشخيص، الاختبارات).
    val MONTHS_AR: List<String> = listOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوال", "ذو القعدة", "ذو الحجة",
    )

    fun monthNameAr(month: Int): String =
        MONTHS_AR.getOrElse(month - 1) { MONTHS_AR.first() }
}
