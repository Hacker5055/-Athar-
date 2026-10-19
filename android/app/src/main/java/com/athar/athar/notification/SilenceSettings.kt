package com.athar.athar.notification

/// إعدادات «وضع الصمت» (docs/02 · A11).
///
/// ⚠️ **معطّلٌ افتراضيًا**: كتمُ أصواتِ الهاتف قرارٌ يخصّ المستخدم
/// لا التطبيق، فلا يبدأ قبل موافقته.
data class SilenceSettings(
    val enabled: Boolean = false,
    val minutes: Int = DEFAULT_MINUTES,
) {
    companion object {
        val MINUTE_CHOICES = listOf(10, 15, 20, 30)
        const val DEFAULT_MINUTES = 15

        /// ⚠️ الصمتُ يبدأ **بعد الأذان** بدقيقتين: إن بدأ معه لكتم
        /// «عدم الإزعاج» صوتَ الأذان نفسه — فيصمت الأذانُ وهو يُسمَع.
        const val DELAY_MINUTES = 2

        /// قيمةٌ خارج الخيارات تُعاد إلى الافتراضي: لا نُبقي دقيقةً
        /// غريبةً كتبها إصدارٌ أقدم.
        fun clamp(minutes: Int): Int =
            if (minutes in MINUTE_CHOICES) minutes else DEFAULT_MINUTES
    }
}
