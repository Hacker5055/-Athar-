package com.athar.athar.tasbih

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات ترميز الجلسات.
///
/// ⚠️ الفحص على **الصمود**: ملفٌّ فيه جلسةٌ تالفة **لا يُسقط
/// السجلّ كلّه**. فسجلُّ عشرين جلسةٍ لا يُمحى لأن واحدةً منها
/// فسدت.
class TasbihSessionCodecTest {

    private val start = 1_763_000_000_000L

    private fun session(count: Int, offset: Long = 0) = TasbihSession(
        startedAtMillis = start + offset,
        lastTapAtMillis = start + offset + 60_000L,
        count = count,
        target = 33,
    )

    @Test
    fun theRoundTripKeepsEverything() {
        val sessions = listOf(session(5), session(12, 60_000L), session(3, 120_000L))
        val back = TasbihSessionCodec.decode(TasbihSessionCodec.encode(sessions))
        assertEquals(3, back.size)
        assertEquals(sessions[0], back[0])
        assertEquals(sessions[1], back[1])
        assertEquals(sessions[2], back[2])
    }

    @Test
    fun anEmptyListEncodesToAnEmptyArray() {
        val back = TasbihSessionCodec.decode(TasbihSessionCodec.encode(emptyList()))
        assertTrue(back.isEmpty())
    }

    @Test
    fun garbageIsReadAsAnEmptyLog() {
        // ⚠️ ملفٌّ تالف **يُقرأ كحالة فارغة** لا أن يُسقط
        // الشاشة.
        assertTrue(TasbihSessionCodec.decode("ليس JSON").isEmpty())
        assertTrue(TasbihSessionCodec.decode("").isEmpty())
    }

    @Test
    fun aBrokenSessionDoesNotSinkTheOthers() {
        // ⚠️ **جلسةٌ تالفة لا تُسقط السجلّ**: فسجلُّ عشرين
        // جلسةٍ لا يضيع لأن واحدةً منها فسدت.
        val text = TasbihSessionCodec.encode(listOf(session(5), session(7, 60_000L)))
        val broken = text.replace("\"count\":5", "\"count\":\"ليس رقمًا\"")
        val back = TasbihSessionCodec.decode(broken)
        assertEquals(2, back.size)
        assertEquals(7, back[1].count)
        assertEquals(0, back[0].count)
    }

    @Test
    fun aSessionWithoutTimesIsSkipped() {
        val text = """[{"count":5,"target":33}]"""
        assertTrue(TasbihSessionCodec.decode(text).isEmpty())
    }

    @Test
    fun negativeCountsAreClampedToZero() {
        val text = """[{"start":1,"last":2,"count":-9,"target":33}]"""
        val back = TasbihSessionCodec.decode(text)
        assertEquals(1, back.size)
        assertEquals(0, back[0].count)
    }

    // ── الجلسة القائمة (لا السجلّ) ────────────────────────

    @Test
    fun theCurrentSessionRoundTrips() {
        val one = session(9)
        val back = TasbihSessionCodec.decodeOne(TasbihSessionCodec.encodeOne(one))
        assertNotNull(back)
        assertEquals(one, back)
    }

    @Test
    fun aBrokenCurrentSessionIsSimplyAbsent() {
        // ⚠️ جلسةٌ تالفة = **لا جلسة**: ولا جلسات
        assertNull(TasbihSessionCodec.decodeOne("ليس JSON"))
        assertNull(TasbihSessionCodec.decodeOne(""))
    }
}
