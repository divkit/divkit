package com.yandex.div.render.common.gradient

import kotlin.test.Test
import kotlin.test.assertEquals

class AnimatedGradientMathTest {

    @Test
    fun `phase is synchronized by frame time`() {
        assertEquals(0.25f, AnimatedGradientMath.phase(400L, 1600L))
        assertEquals(AnimatedGradientMath.phase(400L, 1600L), AnimatedGradientMath.phase(2000L, 1600L))
    }

    @Test
    fun `phase clamps duration to one millisecond`() {
        assertEquals(0f, AnimatedGradientMath.phase(400L, 0L))
    }

    @Test
    fun `linear translation moves along gradient angle`() {
        val (x, y) = AnimatedGradientMath.linearTranslation(
            angleDegrees = 90f, width = 200f, height = 40f, phase = 0.25f,
        ) { x, y -> x to y }

        assertEquals(0f, x, absoluteTolerance = 0.01f)
        assertEquals(20f, y, absoluteTolerance = 0.01f)
    }

    @Test
    fun `linear translation covers whole width at zero angle`() {
        val start = AnimatedGradientMath.linearTranslation(0f, 200f, 40f, phase = 0f) { x, _ -> x }
        val end = AnimatedGradientMath.linearTranslation(0f, 200f, 40f, phase = 1f) { x, _ -> x }

        assertEquals(-200f, start, absoluteTolerance = 0.01f)
        assertEquals(200f, end, absoluteTolerance = 0.01f)
    }

    @Test
    fun `radial translation is symmetric around the middle phase`() {
        assertEquals(-100f, AnimatedGradientMath.radialTranslation(width = 100f, phase = 0f))
        assertEquals(0f, AnimatedGradientMath.radialTranslation(width = 100f, phase = 0.5f))
        assertEquals(100f, AnimatedGradientMath.radialTranslation(width = 100f, phase = 2f))
    }
}
