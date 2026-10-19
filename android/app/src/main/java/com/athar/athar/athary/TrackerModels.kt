package com.athar.athar.athary

import java.time.LocalDate

/// العبادات اليومية.
///
/// ⚠️ القائمة **ثابتة** عن قصد: التخصيص يفتح باب تصميم كبير (أهداف،
/// أيقونات، ترتيب) وهو مؤجّل لما بعد الإطلاق.
///
/// ⚠️ وزاد عليها **التسبيح** (E6): أولُ ما تعلّمه التطبيقُ نفسُه
/// أن يميّز — فدورةُ تسبيحٍ مكتملة تُعلَّم في «أثري» من غير أن
/// يضغط المستخدم شيئًا.
enum class HabitId(val stringKey: String, val id: String) {
    /// أصعب واحدة، وأكثر واحدة تكسر السلسلة
    FAJR("habit_fajr", "fajr"),
    FIVE_PRAYERS("habit_five_prayers", "five_prayers"),
    QURAN("habit_quran", "quran"),
    MORNING_ADHKAR("habit_morning_adhkar", "morning_adhkar"),
    EVENING_ADHKAR("habit_evening_adhkar", "evening_adhkar"),
    DUHA("habit_duha", "duha"),
    SADAQA("habit_sadaqa", "sadaqa"),
    FASTING("habit_fasting", "fasting"),
    /// دورةُ تسبيحٍ كاملة — تُعلَّم تلقائيًا عند إتمام الدورة.
    TASBIH("habit_tasbih", "tasbih"),
    ;

    companion object {
        /// ⚠️ `id` حقل منفصل عن `name`: لو كان `name` نفسه لضاع سجل
        /// المستخدم متى أُعيدت تسمية الثابت.
        ///
        /// ⚠️ ويقبل الاثنين بلا حساسية لحالة الحروف: بيانات كُتبت بأيّهما
        /// تبقى مقروءة، والمجهول يُتجاهل بدل أن يمحو اليوم كله.
        fun fromId(id: String?): HabitId? {
            if (id == null) return null
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) }
                ?: entries.firstOrNull { it.name.equals(id, ignoreCase = true) }
        }
    }
}

/// كثافة اليوم — لتلوين شبكة الثلاثين يومًا.
///
/// ⚠️ اللون **ليس** الإشارة الوحيدة أبدًا: الشبكة مصحوبة بعدّاد نصّي
/// دائمًا، احترامًا لإمكانية الوصول (`docs/04` §أ).
enum class DayLevel { NONE, LOW, MID, HIGH, FULL }

/// يوم واحد في المتتبع.
///
/// ⚠️ **اليوم هنا يوم مدني محلي** (منتصف الليل لمنتصف الليل بتوقيت
/// الجهاز)، لا اليوم العبادي الذي يبدأ من المغرب. تبسيط مقصود لـ v1:
/// اليوم العبادي يفتح أسئلة صعبة (صيام، قيام، فجر قبل المغرب أم بعده؟).
data class DailyRecord(
    val day: LocalDate,
    val done: Set<HabitId> = emptySet(),
) {
    val count: Int get() = done.size
    val isEmpty: Boolean get() = done.isEmpty()
    val key: String get() = dayKey(day)
    val level: DayLevel get() = levelFor(count)

    fun has(habit: HabitId): Boolean = habit in done

    fun toggle(habit: HabitId): DailyRecord =
        copy(done = if (habit in done) done - habit else done + habit)

    /// يُضيف عبادةً **دون أن يرفعها**: العلامةُ التلقائية (دورةُ
    /// تسبيح) تُضيف ولا تحذف — بخلاف `toggle` الذي يُنقص.
    fun mark(habit: HabitId): DailyRecord =
        if (habit in done) this else copy(done = done + habit)

    companion object {
        fun levelFor(count: Int): DayLevel = when (count) {
            0 -> DayLevel.NONE
            1, 2 -> DayLevel.LOW
            3, 4 -> DayLevel.MID
            5, 6 -> DayLevel.HIGH
            else -> DayLevel.FULL
        }
    }
}

/// مفتاح اليوم `YYYY-MM-DD` — ثابت الطول، ويُرتّب معجميًا زمنيًا.
fun dayKey(day: LocalDate): String = day.toString()

/// اليوم السابق.
///
/// ⚠️ `LocalDate.minusDays(1)` يوم مدني صحيح، بخلاف طرح ٢٤ ساعة الذي
/// يخطئ في أيام التوقيت الصيفي (٢٣ أو ٢٥ ساعة).
fun previousDay(day: LocalDate): LocalDate = day.minusDays(1)

/// فكّ `YYYY-MM-DD`. `null` إن كانت الصيغة خاطئة أو التاريخ غير موجود.
fun dayFromKey(key: String): LocalDate? {
    val parts = key.split('-')
    if (parts.size != 3) return null
    val year = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val dayOfMonth = parts[2].toIntOrNull() ?: return null
    if (month !in 1..12 || dayOfMonth !in 1..31) return null
    // ⚠️ ٢٠٢٦-٠٢-٣٠ يمرّ الفحص أعلاه وهو تاريخ غير موجود
    return runCatching { LocalDate.of(year, month, dayOfMonth) }.getOrNull()
}
