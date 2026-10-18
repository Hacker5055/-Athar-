package com.athar.athar.athary

import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/// قلب «أثري» — **محض بالكامل**، يُختبر على JVM بلا أي محاكاة.
data class TrackerState(val records: Map<String, DailyRecord> = emptyMap()) {

    fun recordFor(day: LocalDate): DailyRecord =
        records[dayKey(day)] ?: DailyRecord(day)

    fun countFor(day: LocalDate): Int = recordFor(day).count

    fun has(day: LocalDate, habit: HabitId): Boolean = recordFor(day).has(habit)

    fun toggle(day: LocalDate, habit: HabitId): TrackerState {
        val key = dayKey(day)
        val next = recordFor(day).toggle(habit)
        val map = records.toMutableMap()
        // ⚠️ لا نُخزّن أيامًا فارغة: أوضح في التصحيح وأصغر في التخزين
        if (next.isEmpty) map.remove(key) else map[key] = next
        return TrackerState(map)
    }

    /// يحذف ما هو أقدم من [RETENTION_DAYS] — وإلا نما ملف التخزين بلا حدّ.
    fun pruned(today: LocalDate): TrackerState {
        if (records.isEmpty()) return this
        val cutoff = dayKey(today.minusDays(RETENTION_DAYS.toLong()))
        return TrackerState(records.filterKeys { it >= cutoff })
    }

    /// عدد الأيام المتصلة التي فيها عبادة واحدة على الأقل.
    ///
    /// ⚠️ **قاعدة التسامح**: لو اليوم الحالي ما زال فارغًا نعدّ من
    /// الأمس. بدونها تظهر السلسلة «صفر» كل صباح قبل أي تأشير — وهو
    /// بالضبط السلوك الذي يجعل المستخدم يترك المتتبع.
    fun currentStreak(today: LocalDate): Int {
        var cursor = today
        if (countFor(cursor) == 0) cursor = previousDay(cursor)
        var streak = 0
        while (countFor(cursor) > 0) {
            streak++
            cursor = previousDay(cursor)
        }
        return streak
    }

    /// أطول سلسلة في التاريخ المخزّن.
    fun bestStreak(): Int {
        if (records.isEmpty()) return 0
        val days = records.keys.mapNotNull { dayFromKey(it) }.sorted()
        var best = 0
        var run = 0
        var previous: LocalDate? = null
        for (day in days) {
            run = if (previous != null && previous!!.plusDays(1) == day) run + 1 else 1
            if (run > best) best = run
            previous = day
        }
        return best
    }

    fun isComplete(today: LocalDate): Boolean =
        recordFor(today).count >= HabitId.entries.size

    fun ratioFor(today: LocalDate): Float =
        recordFor(today).count.toFloat() / HabitId.entries.size

    fun window(today: LocalDate, days: Int = WINDOW_DAYS): List<DailyRecord> {
        val result = mutableListOf<DailyRecord>()
        var cursor = today
        repeat(days) {
            result.add(0, recordFor(cursor))
            cursor = previousDay(cursor)
        }
        return result
    }

    fun totalInWindow(today: LocalDate, days: Int = WINDOW_DAYS): Int =
        window(today, days).sumOf { it.count }

    /// العبادة التالية المقترحة — «فِعل واحد» لا ثمانية (قاعدة `docs/03`).
    fun nextSuggestion(today: LocalDate): HabitId? {
        val record = recordFor(today)
        return HabitId.entries.firstOrNull { !record.has(it) }
    }

    fun encode(): String = JSONObject().apply {
        put("v", 1)
        put("days", JSONObject().apply {
            records.forEach { (key, record) ->
                put(key, JSONArray(record.done.map { it.id }.sorted()))
            }
        })
    }.toString()

    companion object {
        const val WINDOW_DAYS = 30
        const val RETENTION_DAYS = 400
        val EMPTY = TrackerState()

        fun decode(raw: String?): TrackerState {
            // ⚠️ تلف التخزين ليس سببًا لانهيار التطبيق
            if (raw.isNullOrEmpty()) return EMPTY
            val root = runCatching { JSONObject(raw) }.getOrNull() ?: return EMPTY
            val daysRaw = root.optJSONObject("days") ?: return EMPTY

            val records = mutableMapOf<String, DailyRecord>()
            val keys = daysRaw.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val record = recordFromKey(key, daysRaw.opt(key)) ?: continue
                if (!record.isEmpty) records[key] = record
            }
            return TrackerState(records)
        }

        /// يتجاهل المعرّفات غير المعروفة بدل أن يفشل: عبادة أُزيلت من
        /// إصدار أقدم لا يجوز أن تُمحو سجل المستخدم كله.
        private fun recordFromKey(key: String, raw: Any?): DailyRecord? {
            val day = dayFromKey(key) ?: return null
            if (raw !is JSONArray) return DailyRecord(day)
            val done = mutableSetOf<HabitId>()
            for (i in 0 until raw.length()) {
                HabitId.fromId(raw.optString(i, ""))?.let { done += it }
            }
            return DailyRecord(day, done)
        }
    }
}
