package com.athar.athar.prayer

import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import java.time.Instant
import java.time.LocalDate

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
        }

        val times = PrayerTimes(coordinates, dateComponents, parameters)
        return PrayerDay(
            date = date,
            fajr = times.fajr.toInstant(),
            sunrise = times.sunrise.toInstant(),
            dhuhr = times.dhuhr.toInstant(),
            asr = times.asr.toInstant(),
            maghrib = times.maghrib.toInstant(),
            isha = times.isha.toInstant(),
        )
    }
}
