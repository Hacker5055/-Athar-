package com.athar.athar.notification

import com.athar.athar.prayer.PrayerDay
import com.athar.athar.prayer.PrayerSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/// منقولة من النسخة المرجعية في `app/test` بعد تحويلها إلى Kotlin —
/// المنطق ذاته لأنه هو ما يُخطئ فيه الناس صامتين.
class NotificationPlannerTest {

    private val sunday: LocalDate = LocalDate.of(2026, 10, 18)   // كان أحد
    private val friday: LocalDate = LocalDate.of(2026, 10, 23)   // أول جمعة

    private fun midnight(date: LocalDate): Instant =
        date.atStartOfDay(ZoneOffset.UTC).toInstant()

    /// ست صلوات تفصل بينها [gapMinutes] وتبدأ ٠٤:٠٠ UTC.
    ///
    /// ⚠️ الفاصل قابل للضبط لأن **باب الأذكار** (الفجر←الشروق) لا يمكن
    /// تجاوزه بفاصل ساعتين وأقصى تأخير ٣٠د — فلا يُختبر التقييد إلا
    /// بيوم قصير النهار، وهو حال الشتاء فعلًا لا حالة اصطناعية.
    private fun day(date: LocalDate, startHours: Long = 4, gapMinutes: Long = 120): PrayerDay {
        fun at(index: Long) =
            midnight(date).plus(startHours * 60 + index * gapMinutes, ChronoUnit.MINUTES)
        return PrayerDay(
            date = date,
            fajr = at(0),
            sunrise = at(1),
            dhuhr = at(2),
            asr = at(3),
            maghrib = at(4),
            isha = at(5),
        )
    }

    private fun week(from: LocalDate): List<PrayerDay> =
        (0 until NotificationPlanner.DEFAULT_AHEAD_DAYS).map { day(from.plusDays(it.toLong())) }

    /// ⚠️ الأذكار **مُفعّلة افتراضيًا**، فاختبارات الصلاة تُوقفها
    /// صراحةً: بلا ذلك يتغيّر كل عدد في الملف بلا سبب مرئي، ويصير
    /// الفشل لغزًا بدل أن يكون دليلًا.
    private fun prayersOnly(): NotificationSettings = NotificationSettings(
        jumuahReminder = false,
        morningAdhkar = false,
        eveningAdhkar = false,
    )

    // ── الأساسيات ───────────────────────────────────────────

    @Test fun masterSwitchOffYieldsNothing() {
        val plan = NotificationPlanner.plan(
            days = week(sunday),
            settings = NotificationSettings(masterEnabled = false),
            now = midnight(sunday),
        )
        assertTrue(plan.isEmpty())
    }

    @Test fun defaultAdhanForEveryPrayerExceptSunrise() {
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = prayersOnly(),
            now = midnight(sunday),
        )
        val athan = plan.filter { it.kind == AlertKind.ATHAN }
        assertEquals(5, athan.size)
        assertFalse("الشروق ليست صلاة", plan.any { it.prayer == PrayerSlot.SUNRISE })
    }

    /// ⚠️ القيد في `modeFor` لا في الواجهة: يستحيل تجاوزه من شاشة
    @Test fun sunriseStaysOffEvenIfExplicitlyRequested() {
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = prayersOnly().withMode(PrayerSlot.SUNRISE, PrayerAlertMode.ADHAN),
            now = midnight(sunday),
        )
        assertFalse(plan.any { it.prayer == PrayerSlot.SUNRISE })
    }

    @Test fun pastDueIsNotScheduled() {
        // «الآن» = ٠٧:٠٠ — بعد الفجر (٠٤:٠٠) والشروق (٠٦:٠٠)
        val now = midnight(sunday).plus(7, ChronoUnit.HOURS)
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = prayersOnly(),
            now = now,
        )
        assertFalse("الفجر فات", plan.any { it.prayer == PrayerSlot.FAJR })
        assertTrue("الظهر ٠٨:٠٠ لسه جاي", plan.any {
            it.kind == AlertKind.ATHAN && it.prayer == PrayerSlot.DHUHR
        })
    }

    @Test fun offModeCancelsOnlyThatPrayer() {
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = prayersOnly().withMode(PrayerSlot.ASR, PrayerAlertMode.OFF),
            now = midnight(sunday),
        )
        assertFalse(plan.any { it.prayer == PrayerSlot.ASR })
        assertEquals(4, plan.size)
    }

    // ── التذكير ─────────────────────────────────────────────

    @Test fun reminderIsScheduledBeforeAthn() {
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = prayersOnly().withReminder(PrayerSlot.FAJR, 15),
            now = midnight(sunday),
        )
        val reminder = plan.single { it.isReminder }
        assertEquals(PrayerSlot.FAJR, reminder.prayer)
        assertEquals(15, reminder.reminderMinutes)
        assertEquals(
            midnight(sunday).plus(4, ChronoUnit.HOURS).minus(15, ChronoUnit.MINUTES),
            reminder.at,
        )
    }

    @Test fun zeroMinutesMeansNoReminder() {
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = prayersOnly(),
            now = midnight(sunday),
        )
        assertFalse(plan.any { it.isReminder })
    }

    @Test fun pastDueReminderIsDroppedButAthanStays() {
        // now = ٠٣:٤٥: تذكير الفجر (٠٣:٣٠) فات، والأذان (٠٤:٠٠) جاي
        val now = midnight(sunday).plus(3, ChronoUnit.HOURS).plus(45, ChronoUnit.MINUTES)
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = prayersOnly().withReminder(PrayerSlot.FAJR, 30),
            now = now,
        )
        assertFalse(plan.any { it.isReminder })
        assertTrue(plan.any {
            it.kind == AlertKind.ATHAN && it.prayer == PrayerSlot.FAJR
        })
    }

    @Test fun switchedOffPrayerHasNoReminder() {
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = prayersOnly()
                .withMode(PrayerSlot.ASR, PrayerAlertMode.OFF)
                .withReminder(PrayerSlot.ASR, 10),
            now = midnight(sunday),
        )
        assertFalse(plan.any { it.isReminder })
    }

    /// ⚠️ الصامت إشعار قائم، لا موقوف — يحتفظ بتذكيره
    @Test fun silentPrayerKeepsItsReminder() {
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = prayersOnly()
                .withMode(PrayerSlot.ASR, PrayerAlertMode.SILENT)
                .withReminder(PrayerSlot.ASR, 10),
            now = midnight(sunday),
        )
        val reminder = plan.single { it.isReminder }
        assertEquals(PrayerSlot.ASR, reminder.prayer)
    }

    @Test fun reminderAndAthanNeverShareAnId() {
        val settings = NotificationSettings(jumuahReminder = false).let { base ->
            PrayerSlot.entries.fold(base) { acc, slot -> acc.withReminder(slot, 10) }
        }
        val plan = NotificationPlanner.plan(week(sunday), settings, midnight(sunday))
        assertEquals("معرّف مكرّر", plan.size, plan.map { it.id }.toSet().size)
    }

    // ── تذكير الجمعة ────────────────────────────────────────

    @Test fun jumuahIsWeeklyBeforeFridayDhuhr() {
        val plan = NotificationPlanner.plan(
            days = week(sunday),
            settings = NotificationSettings(jumuahMinutesBefore = 90),
            now = midnight(sunday),
        )
        val jumuah = plan.single { it.kind == AlertKind.JUMUAH }
        assertTrue(jumuah.weekly)
        assertEquals(PrayerSlot.DHUHR, jumuah.prayer)
        // ظهر الجمعة ٠٨:٠٠ − ٩٠ دقيقة = ٠٦:٣٠
        assertEquals(
            midnight(friday).plus(6, ChronoUnit.HOURS).plus(30, ChronoUnit.MINUTES),
            jumuah.at,
        )
    }

    /// ⚠️ لو فات معاد الجمعة نلفّ أسبوعًا — لا نجدول في الماضي فيتكرر فورًا
    @Test fun jumuahWrapsAWeekWhenMissed() {
        val now = midnight(friday).plus(9, ChronoUnit.HOURS)
        val plan = NotificationPlanner.plan(
            days = week(sunday),
            settings = NotificationSettings(jumuahMinutesBefore = 60),
            now = now,
        )
        val jumuah = plan.single { it.kind == AlertKind.JUMUAH }
        assertEquals(
            midnight(friday.plusDays(7)).plus(7, ChronoUnit.HOURS),
            jumuah.at,
        )
    }

    @Test fun jumuahOffYieldsNone() {
        val plan = NotificationPlanner.plan(
            days = week(sunday),
            settings = prayersOnly(),
            now = midnight(sunday),
        )
        assertFalse(plan.any { it.kind == AlertKind.JUMUAH })
    }

    @Test fun noFridayInRangeIsSilentlyIgnored() {
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = NotificationSettings(),
            now = midnight(sunday),
        )
        assertNull(plan.firstOrNull { it.kind == AlertKind.JUMUAH })
    }

    // ── شروط السلامة ────────────────────────────────────────

    @Test fun planIsOrderedWithinEachKind() {
        val settings = PrayerSlot.entries.fold(prayersOnly()) { acc, slot ->
            acc.withReminder(slot, 10)
        }
        val plan = NotificationPlanner.plan(week(sunday), settings, midnight(sunday))
        for (kind in AlertKind.entries) {
            val times = plan.filter { it.kind == kind }.map { it.at }
            for (i in 1 until times.size) {
                assertTrue("$kind غير مرتّب عند $i", !times[i].isBefore(times[i - 1]))
            }
        }
    }

    @Test fun everyIdIsDecodable() {
        val settings = PrayerSlot.entries.fold(prayersOnly()) { acc, slot ->
            acc.withReminder(slot, 20)
        }
        val plan = NotificationPlanner.plan(week(sunday), settings, midnight(sunday))
        assertTrue("خطة فارغة", plan.isNotEmpty())
        for (alert in plan) {
            val decoded = AlertId.decode(alert.id)
            assertEquals("فكّ ${alert.id}", alert.kind, decoded.kind)
        }
    }

    // ── أذكار الصباح والمساء ───────────────────────────────

    /// ⚠️ الجوهري: التذكير **يتبع الفجر** لا ساعة ثابتة. ساعة ثابتة
    /// تُذكّر قبل الفجر صيفًا وبعد الشروق شتاءً — خارج الباب في الحالين.
    @Test fun morningAdhkarFollowsFajr() {
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = prayersOnly().copy(morningAdhkar = true, morningAdhkarOffset = 15),
            now = midnight(sunday),
        )
        val alert = plan.single { it.kind == AlertKind.MORNING_ADHKAR }
        assertEquals(
            "الفجر ٠٤:٠٠ + ١٥د",
            midnight(sunday).plus(4, ChronoUnit.HOURS).plus(15, ChronoUnit.MINUTES),
            alert.at,
        )
    }

    @Test fun eveningAdhkarFollowsAsr() {
        val plan = NotificationPlanner.plan(
            days = listOf(day(sunday)),
            settings = prayersOnly().copy(eveningAdhkar = true, eveningAdhkarOffset = 10),
            now = midnight(sunday),
        )
        val alert = plan.single { it.kind == AlertKind.EVENING_ADHKAR }
        assertEquals(
            "العصر ١٠:٠٠ + ١٠د",
            midnight(sunday).plus(10, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES),
            alert.at,
        )
    }

    /// ⚠️ سقف الباب: تذكير بعد الشروق ليس «صباحًا». نُقيّده بالسقف
    /// بدل إلغائه، لأن تذكيرًا متأخرًا خير من تذكير لا يأتي.
    @Test fun morningAdhkarIsCappedAtSunrise() {
        // يوم فاصلُه ١٥د: الفجر ٠٤:٠٠، الشروق ٠٤:١٥
        val short = day(sunday, gapMinutes = 15)
        val plan = NotificationPlanner.plan(
            days = listOf(short),
            settings = prayersOnly().copy(morningAdhkar = true, morningAdhkarOffset = 30),
            now = midnight(sunday),
        )
        val alert = plan.single { it.kind == AlertKind.MORNING_ADHKAR }
        // ٠٤:٠٠ + ٣٠د = ٠٤:٣٠، يُقيَّد إلى الشروق ٠٤:١٥
        assertEquals(short.sunrise, alert.at)
    }

    @Test fun eveningAdhkarIsCappedAtMaghrib() {
        // يوم فاصلُه ١٥د: العصر ٠٤:٤٥، المغرب ٠٥:٠٠
        val short = day(sunday, gapMinutes = 15)
        val plan = NotificationPlanner.plan(
            days = listOf(short),
            settings = prayersOnly().copy(eveningAdhkar = true, eveningAdhkarOffset = 30),
            now = midnight(sunday),
        )
        val alert = plan.single { it.kind == AlertKind.EVENING_ADHKAR }
        // ٠٤:٤٥ + ٣٠د = ٠٥:١٥، يُقيَّد إلى المغرب ٠٥:٠٠
        assertEquals(short.maghrib, alert.at)
    }

    @Test fun adhkarOffYieldsNone() {
        val plan = NotificationPlanner.plan(
            days = week(sunday),
            settings = prayersOnly(),
            now = midnight(sunday),
        )
        assertEquals(0, plan.count { it.kind == AlertKind.MORNING_ADHKAR })
        assertEquals(0, plan.count { it.kind == AlertKind.EVENING_ADHKAR })
    }

    /// ⚠️ بلا صلاة مرتبطة = الخانة ٠، وهو ما يجعل `cancelAll` بحاجة إلى
    /// `null` في دورته — وإلا بقيت منبّهات ميتة لا تُوقف أبدًا.
    @Test fun adhkarAlertsCarryNoPrayerSlot() {
        val plan = NotificationPlanner.plan(
            days = week(sunday),
            settings = prayersOnly().copy(morningAdhkar = true, eveningAdhkar = true),
            now = midnight(sunday),
        )
        val adhkar = plan.filter {
            it.kind == AlertKind.MORNING_ADHKAR || it.kind == AlertKind.EVENING_ADHKAR
        }
        assertEquals(2, adhkar.size)
        for (alert in adhkar) {
            assertNull(alert.prayer)
            assertEquals(0, AlertId.decode(alert.id).prayerSlot)
        }
    }

    /// ⚠️ الأذكار يومية: تُجدول كل يوم في النطاق لا مرة واحدة
    @Test fun adhkarAlertIsScheduledOncePerDay() {
        val plan = NotificationPlanner.plan(
            days = week(sunday),
            settings = prayersOnly().copy(morningAdhkar = true),
            now = midnight(sunday),
        )
        val morning = plan.filter { it.kind == AlertKind.MORNING_ADHKAR }
        assertEquals(NotificationPlanner.DEFAULT_AHEAD_DAYS, morning.size)
        assertEquals(morning.size, morning.map { it.id }.toSet().size)
    }

    @Test fun emptyDaysYieldEmptyPlan() {
        assertTrue(
            NotificationPlanner.plan(emptyList(), NotificationSettings(), midnight(sunday))
                .isEmpty(),
        )
    }
}
