package com.yandex.div.core.internal.type

import kotlin.test.Test
import kotlin.test.assertEquals

class ArgbInterpolationTest {

    @Test
    fun `rgb channels are interpolated in linear light`() {
        assertColor(0xFFBABABA, interpolateArgb(0.5f, 0xFF000000.toInt(), 0xFFFFFFFF.toInt()))
    }

    @Test
    fun `alpha is interpolated linearly and rounded`() {
        assertColor(0x40000000, interpolateArgb(0.25f, 0x00000000, 0xFF000000.toInt()))
    }

    @Test
    fun `all channels are interpolated independently`() {
        assertColor(0x401C2B3A, interpolateArgb(0.25f, 0x00102030, 0xFF304050.toInt()))
    }

    @Test
    fun `endpoints are exact`() {
        assertColor(0x00102030, interpolateArgb(0f, 0x00102030, 0xFF304050.toInt()))
        assertColor(0xFF304050, interpolateArgb(1f, 0x00102030, 0xFF304050.toInt()))
    }

    private fun assertColor(expected: Long, actual: Int) {
        assertEquals(expected.toInt().toUInt().toString(16), actual.toUInt().toString(16))
    }
}
