package com.athar.athar.backup

import com.athar.athar.adhkar.CustomDhikr
import com.athar.athar.prayer.HighLatitude
import com.athar.athar.prayer.PrayerMethod
import com.athar.athar.quran.KhatmahStore
import com.athar.athar.quran.LastRead
import com.athar.athar.quran.QuranMeta
import com.athar.athar.tasbih.TasbihState
import com.batoulapps.adhan.Madhab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات النسخة الاحتياطية.
///
/// ⚠️ الأخطر في الاستعادة ليس أن تفشل — بل أن **تنجح ناقصة**:
/// ملف نُصف محتواه مفهوم فيُستورد الباقي، ويُمحى ما لم يُفهم
/// بصمت. فمعظم الاختبارات هنا على ما يُرفض لا على ما يُقبل.
class AtharBackupTest {

    private val sample = AtharBackup(
        createdAtEpochMillis = 1_763_000_000_000L,
        prayer = com.athar.athar.prayer.PrayerSettings(
            method = PrayerMethod.UMM_AL_QURA,
            madhab = Madhab.HANAFI,
            latitude = 48.8566,
            longitude = 2.3522,
            cityId = null,
            placeName = "باريس",
            highLatitude = HighLatitude.SEVENTH,
        ),
        lastRead = LastRead(surah = 18, surahName = "الكهف", ayah = 10),
        bookmarks = setOf("2:255", "36:1"),
        khatmah = KhatmahStore.Plan(days = 60, startEpochDay = 20_000L, doneUpTo = 4312),
        tasbih = TasbihState(count = 7, rounds = 2, lifetime = 3300),
        athary = mapOf("2026-10-19" to setOf("fajr", "quran")),
        customAdhkar = listOf(CustomDhikr(id = "a1", text = "سبحان الله", target = 33)),
        hijriOffset = 1,
        localeCode = "fr",
    )

    // ── الدورة الكاملة ───────────────────────────────────

    @Test
    fun roundTripKeepsEverything() {
        val back = BackupCodec.read(BackupCodec.write(sample))!!
        assertEquals(sample.version, back.version)
        assertEquals(sample.createdAtEpochMillis, back.createdAtEpochMillis)
        assertEquals(PrayerMethod.UMM_AL_QURA, back.prayer.method)
        assertEquals(Madhab.HANAFI, back.prayer.madhab)
        assertEquals(48.8566, back.prayer.latitude, 1e-9)
        assertEquals(2.3522, back.prayer.longitude, 1e-9)
        assertNull(back.prayer.cityId)
        assertEquals("باريس", back.prayer.placeName)
        assertEquals(HighLatitude.SEVENTH, back.prayer.highLatitude)
        assertEquals(LastRead(18, "الكهف", 10), back.lastRead)
        assertEquals(setOf("2:255", "36:1"), back.bookmarks)
        assertEquals(sample.khatmah, back.khatmah)
        assertEquals(sample.tasbih, back.tasbih)
        assertEquals(mapOf("2026-10-19" to setOf("fajr", "quran")), back.athary)
        assertEquals(listOf(CustomDhikr("a1", "سبحان الله", 33)), back.customAdhkar)
        assertEquals(1, back.hijriOffset)
        assertEquals("fr", back.localeCode)
    }

    @Test
    fun arabicSurvivesTheFile() {
        // ⚠️ النص العربي في JSON ليس مضمونًا: ترميز خاطئ يُخرج
        // «?????»، والأذكار المخصّصة كلها عربية.
        val back = BackupCodec.read(BackupCodec.write(sample))!!
        assertEquals("سبحان الله", back.customAdhkar.first().text)
        assertEquals("الكهف", back.lastRead!!.surahName)
    }

    @Test
    fun anEmptyBackupRoundTrips() {
        val text = BackupCodec.write(AtharBackup())
        val back = BackupCodec.read(text)!!
        assertNull(back.lastRead)
        assertTrue(back.bookmarks.isEmpty())
        assertNull(back.khatmah)
        assertTrue(back.athary.isEmpty())
        assertTrue(back.customAdhkar.isEmpty())
    }

    // ── الرفض ────────────────────────────────────────────

    @Test
    fun aFutureVersionIsRefusedNotHalfImported() {
        val future = org.json.JSONObject(BackupCodec.write(sample)).apply {
            put("version", BackupCodec.VERSION + 1)
        }.toString()
        assertNull("استيراد نسخة أحدث يمحو ما لا نفهمه", BackupCodec.read(future))
    }

    @Test
    fun foreignJsonIsRefused() {
        assertNull(BackupCodec.read("""{"hello":"world"}"""))
        assertNull(BackupCodec.read("""[1,2,3]"""))
        assertNull(BackupCodec.read(""))
        assertNull(BackupCodec.read("ليس json"))
    }

    @Test
    fun isBackupOnlyAcceptsOurOwn() {
        assertTrue(BackupCodec.isBackup(BackupCodec.write(sample)))
        assertFalse(BackupCodec.isBackup("""{"hello":"world"}"""))
        assertFalse(BackupCodec.isBackup(""))
    }

    // ── التحقّق عند القراءة ──────────────────────────────

    @Test
    fun anImpossibleReadingPositionIsDropped() {
        val bad = org.json.JSONObject(BackupCodec.write(sample)).apply {
            put("lastRead", org.json.JSONObject().put("surah", 900).put("ayah", 900))
        }.toString()
        assertNull(BackupCodec.read(bad)!!.lastRead)
    }

    @Test
    fun anUnknownKhatmahLengthIsDropped() {
        // ⚠️ ٧٠ ليس من خططنا: تقسيم المصحف على مدّة مخترعة قد
        // يترك آيات بلا يوم فلا تُختم الختمة.
        val bad = org.json.JSONObject(BackupCodec.write(sample)).apply {
            put("khatmah", org.json.JSONObject()
                .put("days", 70).put("start", 1L).put("doneUpTo", 5))
        }.toString()
        assertNull(BackupCodec.read(bad)!!.khatmah)
    }

    @Test
    fun anOutOfRangeKhatmahProgressIsClamped() {
        val bad = org.json.JSONObject(BackupCodec.write(sample)).apply {
            put("khatmah", org.json.JSONObject()
                .put("days", 30).put("start", 1L).put("doneUpTo", 999_999))
        }.toString()
        assertEquals(QuranMeta.TOTAL_VERSES, BackupCodec.read(bad)!!.khatmah!!.doneUpTo)
    }

    @Test
    fun anInvalidBookmarkIsDroppedTheValidOneSurvives() {
        val bad = org.json.JSONObject(BackupCodec.write(sample)).apply {
            put("bookmarks", org.json.JSONArray().put("9:999").put("36:1"))
        }.toString()
        assertEquals(setOf("36:1"), BackupCodec.read(bad)!!.bookmarks)
    }

    @Test
    fun negativeCountersAreRejected() {
        val bad = org.json.JSONObject(BackupCodec.write(sample)).apply {
            put("tasbih", org.json.JSONObject()
                .put("count", -5).put("rounds", -1).put("lifetime", -9))
        }.toString()
        val t = BackupCodec.read(bad)!!.tasbih
        assertTrue(t.count >= 0 && t.rounds >= 0 && t.lifetime >= 0)
    }

    @Test
    fun unknownHabitIdsAreFiltered() {
        val bad = org.json.JSONObject(BackupCodec.write(sample)).apply {
            put("athary", org.json.JSONObject().put(
                "2026-10-19", org.json.JSONArray().put("fajr").put("عادة-لا-توجد"),
            ))
        }.toString()
        assertEquals(mapOf("2026-10-19" to setOf("fajr")), BackupCodec.read(bad)!!.athary)
    }

    @Test
    fun unknownPrayerMethodFallsBack() {
        val bad = org.json.JSONObject(BackupCodec.write(sample)).apply {
            put("prayer", org.json.JSONObject()
                .put("method", "طريقة-لا-توجد").put("madhab", "مذهب-لا-يوجد"))
        }.toString()
        val p = BackupCodec.read(bad)!!.prayer
        assertEquals(PrayerMethod.EGYPTIAN, p.method)
        assertEquals(Madhab.SHAFI, p.madhab)
    }

    @Test
    fun outOfRangeCoordinatesFallBack() {
        val bad = org.json.JSONObject(BackupCodec.write(sample)).apply {
            put("prayer", org.json.JSONObject()
                .put("method", "EGYPTIAN").put("lat", 999.0).put("lng", -999.0))
        }.toString()
        val p = BackupCodec.read(bad)!!.prayer
        assertTrue(p.latitude in -90.0..90.0)
        assertTrue(p.longitude in -180.0..180.0)
    }

    @Test
    fun unknownKeysAreIgnored() {
        // ⚠️ التوافق مع نسخة **أحدث** تضيف حقولًا: يجب ألا
        // يُرفض ملفها، بل تُتجاهل ما لا نعرفه.
        val extra = org.json.JSONObject(BackupCodec.write(sample))
            .put("somethingFromTheFuture", org.json.JSONObject().put("x", 1))
            .toString()
        assertEquals(sample.lastRead, BackupCodec.read(extra)!!.lastRead)
    }

    // ── الثوابت ──────────────────────────────────────────

    @Test
    fun theMagicIsWrittenFirst() {
        // ⚠️ «أثر» نكرة في JSON يجب أن تكون مميّزة: بغير
        // `app` يُستورد أي ملف في world ويُصفَّر كل شيء.
        val text = BackupCodec.write(sample)
        assertTrue(text.contains("\"app\":\"athar-backup\""))
    }

    @Test
    fun thePrayerAdjustmentsSurvive() {
        val with = sample.prayer.copy(
            adjustments = mapOf(
                com.athar.athar.prayer.PrayerSlot.FAJR to 7,
                com.athar.athar.prayer.PrayerSlot.ISHA to -4,
            ),
        )
        val back = BackupCodec.read(BackupCodec.write(sample.copy(prayer = with)))!!
        assertEquals(7, back.prayer.adjustments[com.athar.athar.prayer.PrayerSlot.FAJR])
        assertEquals(-4, back.prayer.adjustments[com.athar.athar.prayer.PrayerSlot.ISHA])
        assertEquals(2, back.prayer.adjustments.size)
    }

    @Test
    fun adjustmentsAreClampedOnImport() {
        // ⚠️ ملف معدَّل بـ ٩٠٠ دقيقة لا يجعل الفجر بعد الظهر
        val bad = org.json.JSONObject(BackupCodec.write(sample)).apply {
            put("prayer", org.json.JSONObject()
                .put("method", "EGYPTIAN")
                .put("adjust", org.json.JSONObject().put("FAJR", 900)))
        }.toString()
        val minutes = BackupCodec.read(bad)!!.prayer.adjustments[com.athar.athar.prayer.PrayerSlot.FAJR]!!
        assertTrue(minutes <= com.athar.athar.prayer.PrayerAdjustments.MAX)
    }

    @Test
    fun anUnknownSlotNameIsDropped() {
        val bad = org.json.JSONObject(BackupCodec.write(sample)).apply {
            put("prayer", org.json.JSONObject()
                .put("method", "EGYPTIAN")
                .put("adjust", org.json.JSONObject().put("صلاة-لا-توجد", 5)))
        }.toString()
        assertTrue(BackupCodec.read(bad)!!.prayer.adjustments.isEmpty())
    }

    @Test
    fun theVersionIsWritten() {
        assertTrue(BackupCodec.write(sample).contains("\"version\":${BackupCodec.VERSION}"))
    }
}
