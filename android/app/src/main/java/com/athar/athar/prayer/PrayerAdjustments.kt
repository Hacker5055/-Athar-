package com.athar.athar.prayer

import java.time.Duration
import java.time.Instant

/// التعديل اليدوي للمواقيت (A4 من docs/02) — منطق نقي.
///
/// ⚠️ لماذا يُبنى أصلًا؟ لأنّ **حسابًا فلكيًّا لا يطابق مسجدك**.
/// الأذان في بلدك يتبع توقيتًا رسميًّا أو رؤية محلّيّة، والفرق
/// دقائق معدودة — لكنها الدقائق التي يفطر عليها الصائم ويصلي
/// عليها الجماعة. من لا يملك تعديلها يملك تطبيقًا يقول له وقتًا
/// لا يعمل به أحد حوله.
object PrayerAdjustments {

    /// ⚠️ ثلاثون دقيقة سقفًا **كافية ومانعة**: لا فرق مشروعًا
    /// يتجاوزها، وبلا سقف يصير التطبيق آلة حاسبة لا مواقيت.
    const val MIN = -30
    const val MAX = 30

    fun clamp(minutes: Int): Int = minutes.coerceIn(MIN, MAX)

    fun minutesOf(settings: PrayerSettings, slot: PrayerSlot): Int =
        settings.adjustments[slot] ?: 0

    fun isAdjusted(settings: PrayerSettings): Boolean =
        settings.adjustments.any { it.value != 0 }

    /// تعديل صلاة واحدة، مع إبقاء الباقي كما هو.
    ///
    /// ⚠️ الصفر **يحذف المدخل** ولا يبقينه: خريطة فيها أصفار
    /// تظنّ المستخدم أنه عدّل شيئًا وهو لم يفعل.
    fun withAdjustment(
        settings: PrayerSettings,
        slot: PrayerSlot,
        minutes: Int,
    ): PrayerSettings {
        val clamped = clamp(minutes)
        val map = settings.adjustments.toMutableMap()
        if (clamped == 0) map.remove(slot) else map[slot] = clamped
        return settings.copy(adjustments = map)
    }

    /// إزاحة موقّعة: «+٧» و«−٧»، لا «٧» وحدها — فرق بين
    /// «متأخّر سبع» و«متقدّم سبع» لا يبيّنه الرقم المجرّد.
    fun signed(minutes: Int): String = if (minutes > 0) "+$minutes" else "$minutes"

    /// يطبّق التعديل على يوم كامل.
    ///
    /// ⚠️ التعديل **على الوقت المحسوب** لا على المصدر: نحسب أوّلًا
    /// ثم نزيح. ولو أزحنا داخل الحساب لاختلطت الإزاحة على مكتبة
    /// الفلك فأعادت ترتيب الشروق والفجر.
    fun applyTo(day: PrayerDay, settings: PrayerSettings): PrayerDay = PrayerDay(
        date = day.date,
        fajr = shift(day.fajr, minutesOf(settings, PrayerSlot.FAJR)),
        sunrise = shift(day.sunrise, minutesOf(settings, PrayerSlot.SUNRISE)),
        dhuhr = shift(day.dhuhr, minutesOf(settings, PrayerSlot.DHUHR)),
        asr = shift(day.asr, minutesOf(settings, PrayerSlot.ASR)),
        maghrib = shift(day.maghrib, minutesOf(settings, PrayerSlot.MAGHRIB)),
        isha = shift(day.isha, minutesOf(settings, PrayerSlot.ISHA)),
    )

    private fun shift(instant: Instant, minutes: Int): Instant =
        if (minutes == 0) instant else instant.plus(Duration.ofMinutes(minutes.toLong()))
}
