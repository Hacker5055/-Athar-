package com.athar.athar.tasbih

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/// اختبارات جسر أزرار الصوت (E5).
///
/// ⚠️ الفحص على **الإغلاق**: زرُّ الصوت الملتقط في شاشةٍ أخرى
/// يُبتلع الصوت من المستخدم بلا سبب — ومن لا يعرف لماذا لا
/// يتغيّر الصوت يفقد الثقة بالتطبيق كلّه.
class VolumeCounterTest {

    @After
    fun tearDown() {
        VolumeCounter.detach()
    }

    @Test
    fun nothingIsCapturedWithoutAHandler() {
        assertFalse(VolumeCounter.handleKey())
    }

    @Test
    fun theKeyIsCapturedWhenAHandlerIsAttached() {
        var counted = 0
        VolumeCounter.attach { counted++ }
        assertTrue(VolumeCounter.handleKey())
        assertEquals(1, counted)
    }

    @Test
    fun everyKeyCountsOnce() {
        var counted = 0
        VolumeCounter.attach { counted++ }
        repeat(33) { VolumeCounter.handleKey() }
        assertEquals(33, counted)
    }

    @Test
    fun detachingReleasesTheKey() {
        // ⚠️ **الفصلُ واجب**: من خرج من السبحة لا يجوز أن يبقى
        // زرُّه مُلتقطًا.
        var counted = 0
        VolumeCounter.attach { counted++ }
        VolumeCounter.detach()
        assertFalse(VolumeCounter.handleKey())
        assertEquals(0, counted)
    }

    @Test
    fun attachingReplacesTheOldHandler() {
        // ⚠️ لو تجمّع.handlers لأُضيفت مرّتين — ومن يعيد فتح
        // الشاشة يعدّ مرّةً واحدة لا مرّتين.
        var first = 0
        var second = 0
        VolumeCounter.attach { first++ }
        VolumeCounter.attach { second++ }
        VolumeCounter.handleKey()
        assertEquals(0, first)
        assertEquals(1, second)
    }
}
