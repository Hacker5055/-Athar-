package com.athar.athar.notification

import com.athar.athar.prayer.PrayerSlot
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/// لكل نوع قناة إشعارات مستقلة على أندرويد (الأذان لا يُكتم كبقية
/// التنبيهات)، ولذلك يُميَّز النوع داخل المعرّف.
enum class AlertKind(val indexValue: Int) {
    /// أذان الصلاة في وقتها
    ATHAN(0),

    /// تذكير قبل الصلاة بدقائق
    REMINDER(1),

    /// تذكير الجمعة — أسبوعي متكرر
    JUMUAH(2),

    /// تذكير أذكار الصباح — ⚠️ مرتبط **بالفجر** لا بساعة ثابتة
    MORNING_ADHKAR(3),

    /// تذكير أذكار المساء — ⚠️ مرتبط **بالعصر** لا بساعة ثابتة
    EVENING_ADHKAR(4),
}

data class DecodedAlert(
    val kind: AlertKind,
    val date: LocalDate,
    val prayer: PrayerSlot?,
    val dayEpoch: Int,
    val prayerSlot: Int,
)

/// معرّف إشعار **حتمي** (deterministic).
///
/// ⚠️ لماذا ليس رقمًا عشوائيًا؟ لأننا نحتاج إلغاء إشعار بعينه أو
/// تحديثه دون الاحتفاظ بخريطة في التخزين: نفس المدخلات = نفس المعرّف
/// دائمًا. وبلا ذلك يتراكم لدينا إشعار قديم لا نعرف كيف نلغيه.
///
/// الصيغة: `kind | dayEpoch | prayerSlot`
/// الأقصى ≈ ٣ ملايين — بعيد جدًا عن حدّ `Int` (٢٫١ مليار).
object AlertId {

    /// أساس العدّ: ٢٠٠٠-٠١-٠١ (يكفي حتى سنة ٢٢٧٣)
    private val EPOCH: LocalDate = LocalDate.of(2000, 1, 1)
    private const val PRAYER_SLOTS = 6

    fun dayEpoch(date: LocalDate): Int = ChronoUnit.DAYS.between(EPOCH, date).toInt()

    fun encode(kind: AlertKind, date: LocalDate, prayer: PrayerSlot?): Int {
        val day = dayEpoch(date)
        val slot = if (prayer == null) 0 else prayer.ordinal + 1
        return kind.indexValue * 1_000_000 + day * 10 + slot
    }

    /// فكّ الترميز — للاختبارات ولتشخيص الأخطاء.
    /// `prayer == null` يعني «بلا صلاة مرتبطة» (الخانة = ٠).
    fun decode(id: Int): DecodedAlert {
        val kindIndex = id / 1_000_000
        val rest = id % 1_000_000
        val day = rest / 10
        val slot = rest % 10
        return DecodedAlert(
            kind = AlertKind.entries.getOrNull(kindIndex) ?: AlertKind.ATHAN,
            date = EPOCH.plusDays(day.toLong()),
            prayer = if (slot in 1..PRAYER_SLOTS) PrayerSlot.entries[slot - 1] else null,
            dayEpoch = day,
            prayerSlot = slot,
        )
    }
}
