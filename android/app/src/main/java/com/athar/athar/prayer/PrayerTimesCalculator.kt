package com.athar.athar.prayer

import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/// حساب المواقيت — **محض بالكامل**، ويُختبر على JVM بلا جهاز.
///
/// لا نعيد تنفيذ علم الفلك: مكتبة Adhan الرسمية محقَّقة المصدر
/// ومعهودة. عملنا هو التغليف بواجهة عربية واضحة فقط.
object PrayerTimesCalculator {

    fun calculate(
        settings: PrayerSettings,
        date: LocalDate,
    ): PrayerDay {
        val coordinates = Coordinates(settings.latitude, settings.longitude)
        val dateComponents = DateComponents(date.year, date.monthValue, date.dayOfMonth)
        val parameters = settings.method.parameters().apply {
            madhab = settings.madhab
            // ⚠️ `?.let` لا إسناد مباشر: اسم ثابت المكتبة قد يختلف
            // بين إصداراتها، والفشل يعود إلى افتراضي المكتبة لا
            // يوقف الحساب.
            HighLatitudeRules.resolve(settings.highLatitude)?.let { highLatitudeRule = it }
        }

        val times = PrayerTimes(coordinates, dateComponents, parameters)
        // ⚠️ التعديل **بعد** الحساب لا داخله: نحسب ثم نزيح، فلا
        // تختلط الإزاحة على مكتبة الفلك.
        return PrayerAdjustments.applyTo(
            PrayerDay(
                date = date,
                fajr = minute(times.fajr.toInstant()),
                sunrise = minute(times.sunrise.toInstant()),
                dhuhr = minute(times.dhuhr.toInstant()),
                asr = minute(times.asr.toInstant()),
                maghrib = minute(times.maghrib.toInstant()),
                isha = minute(times.isha.toInstant()),
            ),
            settings,
        )
    }

    /// ⚠️ **قطع إلى الدقيقة** لا إلى الثانية.
    ///
    /// السبب في المكتبة: `CalendarUtil.resolveTime` يبني التاريخ من
    /// `GregorianCalendar.getInstance()` — أي من **ساعة الحائط** — ثم
    /// يضبط السنة والشهر واليوم والساعة والدقيقة والثانية، **ولا
    /// يمسّ المللي ثانية**. فيخرج الوقت محمَّلًا بمللي ثانية اللحظة
    /// التي حُسب فيها.
    ///
    /// وأثرها خفيّ في الاستخدام (العرض بالدقيقة) لكنّه **يفسد
    /// المقارنة**: حسابان يفصل بينهما مللي ثانية واحدة يظهران
    /// مختلفين، فتنهار كلّ مقارنة دقيقة. ولذلك نقطعها هنا مرةً
    /// واحدة، فيصير الوقت **محاذيًا للدقيقة** كما هو في المصحف.
    private fun minute(instant: Instant): Instant =
        instant.truncatedTo(ChronoUnit.MINUTES)
}
