package com.athar.athar.athary

import java.time.LocalDate

/// ملخّص أسبوع «أثري» (F4 من docs/02) — منطق نقي على [TrackerState].
///
/// ⚠️ **وصف لا حكم**: الملخّص يقول ماذا حدث. ولا يقول «أحسنت» ولا
/// «قصّرت»، لأنّ التطبيق لا يعرف عنك ما يعرفه ربّك — وعبادة تُقاس
/// برقم يراها صاحبها غاية لا وسيلة.
data class WeeklySummary(
    val from: LocalDate,
    val to: LocalDate,
    /// أيامٌ فيها عبادة واحدة على الأقل.
    val activeDays: Int,
    /// مجموع العبادات كلّها في الأسبوع.
    val totalActs: Int,
    /// أكثر يومٍ حصيلةً.
    val bestDay: DailyRecord?,
    /// كل عبادة وكم مرّة — **مرتّبة بالأكثر**.
    val perHabit: List<Pair<HabitId, Int>>,
    val streak: Int,
    val bestStreak: Int,
) {
    /// نسبة الإتمام: ما أُنجز ممّا يمكن إنجازه في سبعة أيام.
    ///
    /// ⚠️ **المقام ثابت** (`٧ × عدد العبادات`) لا عدد أيام النشاط:
    /// من أتى بيومين كاملين أراد أن يرى ٢٥٪ لا ١٠٠٪.
    val completion: Int get() {
        val possible = 7 * HabitId.entries.size
        return ((totalActs * 100L) / possible).toInt().coerceIn(0, 100)
    }
}

/// ⚠️ الأسبوع **سبعة أيام تنتهي اليوم** لا «آخر سبعة أيام مخزّنة»:
/// من فتح التطبيق الأربعاء أراد ملخّص الأربعاء الذي مضى، لا نافذة
/// تنزلق بلا مرجع يعرفه.
object WeeklySummaries {

    const val DAYS = 7

    fun of(state: TrackerState, today: LocalDate): WeeklySummary {
        val days = state.window(today, DAYS)
        val counts = LinkedHashMap<HabitId, Int>()
        for (habit in HabitId.entries) counts[habit] = 0
        for (day in days) {
            for (habit in day.done) counts[habit] = (counts[habit] ?: 0) + 1
        }
        return WeeklySummary(
            from = today.minusDays((DAYS - 1).toLong()),
            to = today,
            activeDays = days.count { !it.isEmpty },
            totalActs = days.sumOf { it.count },
            bestDay = days.filter { !it.isEmpty }.maxByOrNull { it.count },
            // ⚠️ **الأكثر أولًا**: الذي يفعله صاحبه كثيرًا لا يحتاج
            // تذكيرًا، والذي يفعله نادرًا هو ما يستحق النظر.
            perHabit = counts.entries
                .sortedWith(compareByDescending<Map.Entry<HabitId, Int>> { it.value }
                    .thenBy { it.key.ordinal })
                .map { it.key to it.value },
            streak = state.currentStreak(today),
            bestStreak = state.bestStreak(),
        )
    }

    /// الأسبوع الذي قبله مباشرة — للمقارنة.
    fun previousOf(state: TrackerState, today: LocalDate): WeeklySummary =
        of(state, today.minusDays(DAYS.toLong()))

    /// هل تحسّن الأسبوع عن الذي قبله؟
    fun improved(current: WeeklySummary, previous: WeeklySummary): Boolean =
        current.totalActs > previous.totalActs
}
