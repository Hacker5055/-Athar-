package com.athar.athar.widget

import com.athar.athar.core.ArabicDigits
import com.athar.athar.prayer.PrayerDay
import com.athar.athar.prayer.PrayerSlot
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/// ما يعرضه الودجت — المنطق النقي، بلا `Context` وبلا Glance.
///
/// ⚠️ الفصل بين «ما يُعرض» و«كيف يُعرض» ليس ترتيبًا: الودجت يُبنى في
/// عملية أخرى ولا نراه يعمل، فالذي نستطيع التأكد منه هو القرار وحده.
object PrayerWidgetPlan {

    /// أرقام عربية — الودجت عربي أولًا.
    ///
    /// ⚠️ تُفوَّض إلى [ArabicDigits] لا تُنسخ هنا: نسختان للتحويل
    /// تختلفان يومًا في السالب أو في الصفر.
    fun arabicDigits(value: Int): String = ArabicDigits.of(value)

    private val CLOCK = DateTimeFormatter.ofPattern("HH:mm")

    fun clock(at: Instant, zone: ZoneId = ZoneId.systemDefault()): String =
        CLOCK.format(at.atZone(zone))

    /// الصلاة القادمة.
    ///
    /// ⚠️ بعد العشاء لا توجد صلاة قادمة **اليوم**، والودجت الفارغ أسوأ
    /// من لا ودجت: ننتقل إلى فجر الغد. و`nextAfter` تُقصي الشروق لأنه
    /// ليس صلاة (قرار الأسبوع ٢).
    fun nextOf(today: PrayerDay, tomorrow: PrayerDay, now: Instant): Pair<PrayerSlot, Instant>? =
        today.nextAfter(now) ?: tomorrow.nextAfter(now)

    /// كم بقي — «٢:١٥» أو «١٥ د».
    ///
    /// ⚠️ الدقائق وحدها تحت الساعة: «٤٥ د» أصدق من «٠:٤٥» حين ينظر
    /// المستخدم نظرة واحدة.
    fun remaining(now: Instant, at: Instant): String {
        val seconds = (at.epochSecond - now.epochSecond).coerceAtLeast(0)
        val hours = (seconds / 3600).toInt()
        val minutes = ((seconds % 3600) / 60).toInt()
        return if (hours > 0) {
            "${arabicDigits(hours)}:${arabicDigits(minutes).padStart(2, '٠')}"
        } else {
            "${arabicDigits(minutes)} د"
        }
    }
}
