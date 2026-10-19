package com.athar.athar.prayer

import com.athar.athar.hijri.HijriCalendar
import com.athar.athar.hijri.HijriDate
import java.time.LocalDate
import java.time.YearMonth

/// يوم واحد في التقويم الشهري: ميلادي + هجري + مواقيت.
data class MonthRow(
    val date: LocalDate,
    val hijri: HijriDate,
    val day: PrayerDay,
)

/// التقويم الشهري (A12 من docs/02) — جدول الشهر كلّه بمرّة واحدة.
///
/// ⚠️ منطق **نقي**: لا يقرأ مخزنًا ولا ساعة، فيُختبر على JVM بلا
/// جهاز. الحساب نفسه مفوَّض إلى [PrayerTimesCalculator] — لم نعد
/// نخترع علم الفلك مرّة ثانية.
object MonthSchedule {

    /// صفوف شهر ميلادي كامل.
    fun rows(
        settings: PrayerSettings,
        year: Int,
        month: Int,
        offsetDays: Int = 0,
    ): List<MonthRow> {
        val length = lengthOf(year, month)
        // ⚠️ الحساب **يومًا بيوم** لا بقصّ نتيجة يوم واحد: الشهر
        // يمتدّ ٣٠ يومًا، ومواقيت اليوم العشرين ليست مواقيت اليوم
        // الأوّل. من نسخ صفًّا واحدًا ثلاثين مرّة فقد أعطى جدولًا
        // يبدو صحيحًا وهو كاذب في تسعةٍ وعشرين منه.
        return (1..length).map { d ->
            val date = LocalDate.of(year, month, d)
            MonthRow(
                date = date,
                hijri = HijriCalendar.from(date, offsetDays),
                day = PrayerTimesCalculator.calculate(settings, date),
            )
        }
    }

    /// ⚠️ `YearMonth` لا رقم ثابت: شباط ٢٩ يومًا في السنة الكبيسة
    /// و٢٨ في غيرها، وأي جدول يفترض ٣٠ يومًا لكل شهر يكذب.
    fun lengthOf(year: Int, month: Int): Int =
        YearMonth.of(year, month).lengthOfMonth()

    /// الشهر السابق بترتيب صحيح عبر حدود السنة.
    fun previous(year: Int, month: Int): Pair<Int, Int> =
        YearMonth.of(year, month).minusMonths(1).let { it.year to it.monthValue }

    /// الشهر التالي بترتيب صحيح عبر حدود السنة.
    fun next(year: Int, month: Int): Pair<Int, Int> =
        YearMonth.of(year, month).plusMonths(1).let { it.year to it.monthValue }
}
