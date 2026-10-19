package com.athar.athar.backup

import android.content.Context
import com.athar.athar.athary.DailyRecord
import com.athar.athar.athary.HabitId
import com.athar.athar.athary.TrackerState
import com.athar.athar.athary.TrackerStore
import com.athar.athar.adhkar.CustomAdhkarStore
import com.athar.athar.hijri.HijriStore
import com.athar.athar.i18n.AppLocale
import com.athar.athar.i18n.LocaleStore
import com.athar.athar.prayer.PrayerSettingsStore
import com.athar.athar.quran.KhatmahStore
import com.athar.athar.quran.QuranBookmarkStore
import com.athar.athar.quran.QuranProgressStore
import com.athar.athar.tasbih.TasbihStore
import java.time.LocalDate

/// جمع بيانات المستخدم وتطبيقها (F6 وF7 من docs/02).
///
/// ⚠️ **كل الحالات ملفوفة بـ runCatching**: نسخة احتياطية نصفها
/// تالف يجب أن تستعيد ما يصلح، لا أن تفشل كلها. ومن يجرّب
/// الاستعادة مرّة وتخونه لا يجرّبها ثانية.
object BackupStore {

    fun collect(context: Context): AtharBackup = AtharBackup(
        createdAtEpochMillis = System.currentTimeMillis(),
        prayer = runCatching { PrayerSettingsStore.load(context) }
            .getOrDefault(com.athar.athar.prayer.PrayerSettings()),
        lastRead = runCatching { QuranProgressStore.load(context) }.getOrNull(),
        bookmarks = runCatching { QuranBookmarkStore.load(context) }.getOrDefault(emptySet()),
        khatmah = runCatching { KhatmahStore.load(context) }.getOrNull(),
        tasbih = runCatching { TasbihStore.loadState(context) }
            .getOrDefault(com.athar.athar.tasbih.TasbihState()),
        athary = runCatching { TrackerStore.load(context) }
            .getOrDefault(TrackerState())
            .records
            .mapValues { (_, record) -> record.done.map { it.id }.toSet() },
        customAdhkar = runCatching { CustomAdhkarStore.load(context) }.getOrDefault(emptyList()),
        hijriOffset = runCatching { HijriStore.load(context) }.getOrDefault(0),
        localeCode = runCatching { LocaleStore.load(context).code }.getOrNull(),
    )

    /// ⚠️ الاستعادة **تستبدل** ما عند المستخدم لا تدمجه. الدمج
    /// يبدو ألطف وهو أخطر: يومٌ مؤشَّر في النسخة وغير مؤشَّر الآن
    /// — أيهما الصحيح؟ الاستبدال قابل للفهم، والدمج لا.
    fun apply(context: Context, backup: AtharBackup) {
        runCatching { PrayerSettingsStore.save(context, backup.prayer) }
        runCatching { QuranBookmarkStore.save(context, backup.bookmarks) }
        backup.lastRead?.let { runCatching { QuranProgressStore.save(context, it) } }
        runCatching {
            if (backup.khatmah == null) KhatmahStore.clear(context)
            else KhatmahStore.save(context, backup.khatmah)
        }
        runCatching {
            TasbihStore.save(
                context,
                TasbihStore.loadPhraseId(context),
                TasbihStore.loadTarget(
                    context,
                    com.athar.athar.tasbih.TasbihPhrases.SUBHAN,
                ),
                backup.tasbih,
            )
        }
        runCatching { TrackerStore.save(context, trackerStateOf(backup.athary)) }
        runCatching { CustomAdhkarStore.save(context, backup.customAdhkar) }
        runCatching { HijriStore.save(context, backup.hijriOffset) }
        backup.localeCode?.let { code ->
            runCatching { LocaleStore.save(context, AppLocale.fromCode(code)) }
        }
    }

    /// ⚠️ السجل يُبنى **من المفاتيح** لا من تاريخ اليوم: مفتاح
    /// غير مفهوم يُتجاهل، ولا يُصنع له يوم مزيّف.
    private fun trackerStateOf(days: Map<String, Set<String>>): TrackerState {
        val records = LinkedHashMap<String, DailyRecord>()
        days.forEach { (key, ids) ->
            val day = runCatching { LocalDate.parse(key) }.getOrNull() ?: return@forEach
            val habits = ids.mapNotNull { HabitId.fromId(it) }.toSet()
            if (habits.isNotEmpty()) records[key] = DailyRecord(day = day, done = habits)
        }
        return TrackerState(records)
    }
}
