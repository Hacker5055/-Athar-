package com.athar.athar.backup

import com.athar.athar.adhkar.CustomDhikr
import com.athar.athar.athary.HabitId
import com.athar.athar.prayer.HighLatitude
import com.athar.athar.prayer.PrayerMethod
import com.athar.athar.prayer.PrayerAdjustments
import com.athar.athar.prayer.PrayerSettings
import com.athar.athar.prayer.PrayerSlot
import com.athar.athar.quran.Khatmah
import com.athar.athar.quran.KhatmahStore
import com.athar.athar.quran.LastRead
import com.athar.athar.quran.QuranBookmarks
import com.athar.athar.quran.QuranMeta
import com.athar.athar.quran.QuranProgress
import com.athar.athar.tasbih.TasbihSession
import com.athar.athar.tasbih.TasbihSessionCodec
import com.athar.athar.tasbih.TasbihState
import com.batoulapps.adhan.Madhab
import org.json.JSONArray
import org.json.JSONObject

/// لقطة من كل ما يملكه المستخدم في أثر.
///
/// ⚠️ **بيانات المستخدم وحدها** — لا نصّ قرآن ولا أذكار مضمّنة:
/// تلك تُحمَّل من الأصول عند الحاجة، ووضعها في النسخة يجعلها
/// ميجابايت بلا فائدة (ويعرّض نصًّا مرخّصًا للتوزيع بلا إسناد).
data class AtharBackup(
    val version: Int = BackupCodec.VERSION,
    val createdAtEpochMillis: Long = 0L,
    val prayer: PrayerSettings = PrayerSettings(),
    val lastRead: LastRead? = null,
    val bookmarks: Set<String> = emptySet(),
    val khatmah: KhatmahStore.Plan? = null,
    val tasbih: TasbihState = TasbihState(),
    /// ⚠️ **سجلّ الجلسات** جزءٌ من الأثر لا من العدّاد: من نسخ
    /// ملفّه ثم استعادته على جهازٍ جديد وجد تاريخه intactًا.
    val tasbihSessions: List<TasbihSession> = emptyList(),
    /// ⚠️ مفتاح اليوم ← معرّفات العادات: **بالمعرّف** لا بالاسم،
    /// لأن الاسم يُترجم والمعرّف ثابت.
    val athary: Map<String, Set<String>> = emptyMap(),
    val customAdhkar: List<CustomDhikr> = emptyList(),
    val hijriOffset: Int = 0,
    val localeCode: String? = null,
)

/// ترميز النسخة الاحتياطية وفكّها (F6 وF7 من docs/02).
///
/// ⚠️ `read` **دالّة تامّة**: لا ترمي أبدًا، وتُعيد `null` عند أي
/// ملف لا تفهمه. استيراد ملف تالف يجب أن يُظهر رسالة، لا أن
/// يُسقط التطبيق — ومن يفقد بياناته مرّة لا يجرب الاستعادة ثانية.
object BackupCodec {

    const val VERSION = 1
    /// ⚠️ الذي يميّز ملفنا: بغيره يُستورد أي JSON في العالم
    /// فيُصفَّر كل شيء بصمت.
    const val MAGIC = "athar-backup"
    private const val K_MAGIC = "app"
    private const val K_VERSION = "version"
    private const val K_CREATED = "createdAt"

    fun write(backup: AtharBackup): String = JSONObject().apply {
        put(K_MAGIC, MAGIC)
        put(K_VERSION, backup.version)
        put(K_CREATED, backup.createdAtEpochMillis)
        put("prayer", jsonOf(backup.prayer))
        backup.lastRead?.let { put("lastRead", jsonOf(it)) }
        put("bookmarks", JSONArray(backup.bookmarks.toList()))
        backup.khatmah?.let { put("khatmah", jsonOf(it)) }
        put("tasbih", jsonOf(backup.tasbih))
        put("tasbihSessions", JSONArray(TasbihSessionCodec.encode(backup.tasbihSessions)))
        put("athary", jsonOf(backup.athary))
        put("customAdhkar", JSONArray().apply {
            backup.customAdhkar.forEach { put(jsonOf(it)) }
        })
        put("hijriOffset", backup.hijriOffset)
        backup.localeCode?.let { put("locale", it) }
    }.toString()

    fun read(text: String): AtharBackup? = runCatching {
        val root = JSONObject(text)
        if (root.optString(K_MAGIC) != MAGIC) return null
        // ⚠️ نسخة **أحدث** مرفوضة لا مُجهَّضة: استيراد نصف
        // الحقول يمحو ما لم يُفهم بصمت، والضياع الصامت أسوأ من
        // رسالة الرفض.
        val version = root.optInt(K_VERSION, 0)
        if (version > VERSION) return null

        AtharBackup(
            version = version,
            createdAtEpochMillis = root.optLong(K_CREATED, 0L),
            prayer = prayerOf(root.optJSONObject("prayer")),
            lastRead = root.optJSONObject("lastRead")?.let(::lastReadOf),
            bookmarks = bookmarksOf(root),
            khatmah = root.optJSONObject("khatmah")?.let(::khatmahOf),
            tasbih = root.optJSONObject("tasbih")?.let(::tasbihOf) ?: TasbihState(),
            tasbihSessions = root.optJSONArray("tasbihSessions")
                ?.let { TasbihSessionCodec.decode(it.toString()) }
                ?: emptyList(),
            athary = atharyOf(root.optJSONObject("athary")),
            customAdhkar = customAdhkarOf(root.optJSONArray("customAdhkar")),
            hijriOffset = root.optInt("hijriOffset", 0),
            localeCode = root.optString("locale", "").takeIf { it.isNotBlank() },
        )
    }.getOrNull()

    fun isBackup(text: String): Boolean =
        runCatching { JSONObject(text).optString(K_MAGIC) == MAGIC }.getOrDefault(false)

    /// ⚠️ كل حقل **مُتحقَّق منه قبل القبول**: إحداثيات من نسخة
    /// أقدم قد تكون خارج النطاق، واسم طريقة غير موجود يرمي عند
    /// الاستخدام لا عند القراءة.
    private fun prayerOf(o: JSONObject?): PrayerSettings {
        if (o == null) return PrayerSettings()
        val lat = o.optDouble("lat", 30.0444)
        val lng = o.optDouble("lng", 31.2357)
        val cityId = o.optString("cityId", "").takeIf { it.isNotBlank() }
        return PrayerSettings(
            method = runCatching { PrayerMethod.valueOf(o.optString("method")) }
                .getOrDefault(PrayerMethod.EGYPTIAN),
            madhab = runCatching { Madhab.valueOf(o.optString("madhab")) }
                .getOrDefault(Madhab.SHAFI),
            latitude = if (lat in -90.0..90.0) lat else 30.0444,
            longitude = if (lng in -180.0..180.0) lng else 31.2357,
            cityId = cityId,
            placeName = o.optString("placeName", ""),
            highLatitude = HighLatitude.fromName(o.optString("highLatitude", null)),
            adjustments = adjustmentsOf(o.optJSONObject("adjust")),
            // ⚠️ **«اختار مكانَه» تُنتقلُ معه**: فمن استعاد نسخته
            // لا يُعاد إليه سؤالُ «حدّد مدينتك».
            locationChosen = o.optBoolean("chosen", false),
        )
    }

    /// ⚠️ التعديلات **تُقصّ عند القراءة**: ملف معدَّل بـ ٩٠٠ دقيقة
    /// لا يجعل الفجر بعد الظهر.
    private fun adjustmentsOf(o: JSONObject?): Map<PrayerSlot, Int> {
        if (o == null) return emptyMap()
        val out = LinkedHashMap<PrayerSlot, Int>()
        for (name in o.keys()) {
            val slot = runCatching { PrayerSlot.valueOf(name) }.getOrNull() ?: continue
            val minutes = o.optInt(name, 0)
            if (minutes != 0) out[slot] = PrayerAdjustments.clamp(minutes)
        }
        return out
    }

    /// ⚠️ الموضع **يُتحقَّق منه**: سورة من نسخة أقدم قد لا تكون
    /// موجودة، و«تابع القراءة» الذي يفتح فارغًا أسوأ من غيابه.
    private fun lastReadOf(o: JSONObject): LastRead? {
        val surah = o.optInt("surah", 0)
        val ayah = o.optInt("ayah", 0)
        if (!QuranProgress.isValid(surah, ayah)) return null
        return LastRead(
            surah = surah,
            surahName = o.optString("surahName", "").ifBlank { "$surah" },
            ayah = ayah,
        )
    }

    /// ⚠️ المفتاح يُعاد **ترميزه** لا يُؤخذ كما هو: ملف معدَّل
    /// يدويًّا قد يحمل «9:999»، والعلامة التي لا تُفتح علامة بلا
    /// معنى.
    private fun bookmarksOf(root: JSONObject): Set<String> {
        val arr = root.optJSONArray("bookmarks") ?: return emptySet()
        val out = LinkedHashSet<String>()
        for (i in 0 until arr.length()) {
            val raw = arr.optString(i, "")
            val parts = raw.split(':')
            val surah = parts.getOrNull(0)?.toIntOrNull() ?: continue
            val ayah = parts.getOrNull(1)?.toIntOrNull() ?: continue
            if (QuranProgress.isValid(surah, ayah)) out.add(QuranBookmarks.key(surah, ayah))
        }
        return out
    }

    /// ⚠️ `days` يجب أن يكون **خطة معروفة**: خطة بمدّة اخترعها
    /// الملف تُقسّم المصحف على غير ما نعرف، وقد تترك آيات بلا يوم.
    private fun khatmahOf(o: JSONObject): KhatmahStore.Plan? {
        val days = o.optInt("days", 0)
        if (days !in Khatmah.PLANS) return null
        val done = o.optInt("doneUpTo", 0).coerceIn(0, QuranMeta.TOTAL_VERSES)
        return KhatmahStore.Plan(
            days = days,
            startEpochDay = o.optLong("start", 0L),
            doneUpTo = done,
        )
    }

    /// ⚠️ العدّادات **لا سالبة**: ملف معدَّل بـ ١٠٠ ألف تسبيحة
    /// يُفسد الشاشة كلها.
    private fun tasbihOf(o: JSONObject): TasbihState = TasbihState(
        count = o.optInt("count", 0).coerceAtLeast(0),
        rounds = o.optInt("rounds", 0).coerceAtLeast(0),
        lifetime = o.optInt("lifetime", 0).coerceAtLeast(0),
    )

    /// ⚠️ معرّفات العادات **تُفلتر**: معرّف من نسخة أقدم قد لا
    /// يكون موجودًا، وسجلّ يحتويه يُسقط الشاشة عند العرض.
    private fun atharyOf(o: JSONObject?): Map<String, Set<String>> {
        if (o == null) return emptyMap()
        val out = LinkedHashMap<String, Set<String>>()
        for (key in o.keys()) {
            val arr = o.optJSONArray(key) ?: continue
            val ids = LinkedHashSet<String>()
            for (i in 0 until arr.length()) {
                val id = arr.optString(i, "")
                if (HabitId.fromId(id) != null) ids.add(id)
            }
            if (ids.isNotEmpty()) out[key] = ids
        }
        return out
    }

    private fun customAdhkarOf(arr: JSONArray?): List<CustomDhikr> {
        if (arr == null) return emptyList()
        val out = ArrayList<CustomDhikr>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            val id = o.optString("id", "").takeIf { it.isNotBlank() } ?: continue
            val text = o.optString("text", "").takeIf { it.isNotBlank() } ?: continue
            out.add(CustomDhikr(id = id, text = text, target = o.optInt("target", 33).coerceAtLeast(1)))
        }
        return out
    }

    // ── الكتابة ──────────────────────────────────────────

    private fun jsonOf(p: PrayerSettings): JSONObject = JSONObject().apply {
        put("method", p.method.name)
        put("madhab", p.madhab.name)
        put("lat", p.latitude)
        put("lng", p.longitude)
        p.cityId?.let { put("cityId", it) }
        put("placeName", p.placeName)
        put("chosen", p.locationChosen)
        put("highLatitude", p.highLatitude.name)
        put("adjust", adjustmentsJson(p.adjustments))
    }

    /// ⚠️ **اسمها مختلف** عن `jsonOf(Map<String, Set<String>>)`:
    /// الخريطتان تُمحيان على JVM إلى `Map` فيصطدمان، وKotlin
    /// يقبل التزاحم في المصدر ولا يكتشفه إلا وقت البناء.
    ///
    /// ⚠️ وبالاسم لا بالترتيب: ترتيب التعداد قد يتغيّر، واسم
    /// الثابت يبقى.
    private fun adjustmentsJson(adjustments: Map<PrayerSlot, Int>): JSONObject = JSONObject().apply {
        adjustments.forEach { (slot, minutes) ->
            if (minutes != 0) put(slot.name, PrayerAdjustments.clamp(minutes))
        }
    }

    private fun jsonOf(r: LastRead): JSONObject = JSONObject().apply {
        put("surah", r.surah)
        put("surahName", r.surahName)
        put("ayah", r.ayah)
    }

    private fun jsonOf(p: KhatmahStore.Plan): JSONObject = JSONObject().apply {
        put("days", p.days)
        put("start", p.startEpochDay)
        put("doneUpTo", p.doneUpTo)
    }

    private fun jsonOf(t: TasbihState): JSONObject = JSONObject().apply {
        put("count", t.count)
        put("rounds", t.rounds)
        put("lifetime", t.lifetime)
    }

    private fun jsonOf(m: Map<String, Set<String>>): JSONObject = JSONObject().apply {
        m.forEach { (day, ids) ->
            put(day, JSONArray().apply { ids.forEach { put(it) } })
        }
    }

    private fun jsonOf(d: CustomDhikr): JSONObject = JSONObject().apply {
        put("id", d.id)
        put("text", d.text)
        put("target", d.target)
    }
}
