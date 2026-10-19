package com.athar.athar.prayer

import com.athar.athar.core.ArabicDigits
import com.athar.athar.i18n.AppLocale
import java.time.Duration
import java.time.Instant

/// العدّ التنازلي حتى الصلاة القادمة (A7 من docs/02).
///
/// ⚠️ **العدّ وصف لا دعاء**: «بقيت دقيقة على المغرب» أصدق من
/// «اقتربت الصلاة»، ولا يَعِد بشيءٍ لا يعلمه.
data class Countdown(
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
) {
    val totalSeconds: Long get() = hours * 3600 + minutes * 60 + seconds

    /// هل انقضى الوقت؟
    val isElapsed: Boolean get() = totalSeconds <= 0
}

object PrayerCountdown {

    /// المتبقّي حتى [target] ابتداءً من [now].
    ///
    /// ⚠️ **الثواني لا تُقرَّب**: العدّ ينقص واحدة واحدة، فالتقريب
    /// يجعله يقفز ويضيع معناه. والاقتطاع إلى الصفر عند الفوات
    /// مقصود: المتبقّي **لا يكون سالبًا**، فالسالب يقول إنّ
    /// الصلاة تأخّرت، وهذا حكمٌ على الله.
    fun until(target: Instant, now: Instant): Countdown {
        val seconds = Duration.between(now, target).seconds
        if (seconds <= 0) return Countdown(0, 0, 0)
        return Countdown(
            hours = seconds / 3600,
            minutes = (seconds % 3600) / 60,
            seconds = seconds % 60,
        )
    }

    /// نصّ العدّ: `س:د:ث` أو `د:ث` إن لم يبقَ إلا دقائق.
    ///
    /// ⚠️ **الأرقام بلغة الواجهة**: العربية الشرقية في العربية
    /// واللاتينية في الإنجليزية والفرنسية.
    fun text(countdown: Countdown, locale: AppLocale): String {
        val hours = digits(countdown.hours, locale, pad = false)
        val minutes = digits(countdown.minutes, locale, pad = countdown.hours > 0)
        val seconds = digits(countdown.seconds, locale, pad = true)
        return if (countdown.hours > 0) "$hours:$minutes:$seconds" else "$minutes:$seconds"
    }

    /// ⚠️ **التصفير**: «٠:٠٥» لا «٠:٥».
    ///
    /// السبب في القراءة لا في الشكل: عدٌّ يتحرّك كلّ ثانية، فالثانية
    /// العارية «٥» تقفز في موضعها فتُقرأ خطأً، والمُثبَّت «٠٥» لا
    /// يتزحزح. والدقائق تُصفَّر **فقط** مع الساعات، وإلا صارت
    /// «٠٢:٠٥» تُقرأ كساعةٍ لا دقيقتين.
    private fun digits(value: Long, locale: AppLocale, pad: Boolean): String {
        val text = if (pad && value < 10) "0$value" else value.toString()
        return if (locale.isRtl) ArabicDigits.ofText(text) else text
    }
}
