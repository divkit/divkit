package com.yandex.div.render.common.gradient

import com.yandex.div.render.common.gradient.GradientGeometry.RelativeRadius
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class GradientGeometryTest {

    @Test
    fun `cardinal angles follow counterclockwise direction`() {
        assertEndpoints(0f, 100f, 50f, 0f, 25f, 100f, 25f)
        assertEndpoints(90f, 100f, 50f, 50f, 50f, 50f, 0f)
        assertEndpoints(180f, 100f, 50f, 100f, 25f, 0f, 25f)
        assertEndpoints(270f, 100f, 50f, 50f, 0f, 50f, 50f)
    }

    @Test
    fun `diagonal gradient covers rectangular bounds`() {
        assertEndpoints(45f, 100f, 50f, 12.5f, 62.5f, 87.5f, -12.5f)
    }

    @Test
    fun `negative angle preserves direction`() {
        assertEndpoints(-90f, 100f, 50f, 50f, 0f, 50f, 50f)
    }

    @Test
    fun `angles beyond full turn preserve direction`() {
        assertEndpoints(450f, 100f, 50f, 50f, 50f, 50f, 0f)
    }

    @Test
    fun `fractional bounds are not rounded`() {
        assertEndpoints(0f, 7.5f, 2.5f, 0f, 1.25f, 7.5f, 1.25f)
    }

    @Test
    fun `zero bounds have coincident endpoints`() {
        assertEndpoints(45f, 0f, 0f, 0f, 0f, 0f, 0f)
    }

    @Test
    fun `near zero delta is snapped to zero`() {
        GradientGeometry.linearEndpoints(0.00001, 100f, 50f) { _, startY, _, endY ->
            assertEquals(25f, startY)
            assertEquals(25f, endY)
        }
    }

    @Test
    fun `delta above snapping threshold is retained`() {
        GradientGeometry.linearEndpoints(0.001, 100f, 50f) { _, startY, _, endY ->
            assertEquals(25.000872f, startY, 0.000002f)
            assertEquals(24.999128f, endY, 0.000002f)
        }
    }

    @Test
    fun `fixed center remains outside bounds`() {
        assertEquals(-3.25f, GradientGeometry.absoluteCenter(-3.25f, false, 10.5f))
    }

    @Test
    fun `relative center preserves fractional dimensions`() {
        assertEquals(2.625f, GradientGeometry.absoluteCenter(0.25f, true, 10.5f))
    }

    @Test
    fun `relative center may be outside bounds`() {
        assertEquals(15.75f, GradientGeometry.absoluteCenter(1.5f, true, 10.5f))
    }

    @Test
    fun `relative center of zero size is zero`() {
        assertEquals(0f, GradientGeometry.absoluteCenter(0.5f, true, 0f))
    }

    @Test
    fun `nearest side uses shortest perpendicular distance`() {
        assertEquals(2f, radius(RelativeRadius.NEAREST_SIDE))
    }

    @Test
    fun `farthest side uses longest perpendicular distance`() {
        assertEquals(8f, radius(RelativeRadius.FARTHEST_SIDE))
    }

    @Test
    fun `nearest corner uses shortest euclidean distance`() {
        assertEquals(sqrt(13f), radius(RelativeRadius.NEAREST_CORNER))
    }

    @Test
    fun `farthest corner uses longest euclidean distance`() {
        assertEquals(sqrt(89f), radius(RelativeRadius.FARTHEST_CORNER))
    }

    @Test
    fun `outside center keeps unsigned side distances`() {
        assertEquals(3f, radius(RelativeRadius.NEAREST_SIDE, -3f, 12f))
        assertEquals(13f, radius(RelativeRadius.FARTHEST_SIDE, -3f, 12f))
    }

    @Test
    fun `outside center keeps corner distances`() {
        assertEquals(5f, radius(RelativeRadius.NEAREST_CORNER, -3f, 12f))
        assertEquals(sqrt(313f), radius(RelativeRadius.FARTHEST_CORNER, -3f, 12f))
    }

    @Test
    fun `radius remains zero when all bounds coincide`() {
        RelativeRadius.entries.forEach { type ->
            assertEquals(0f, GradientGeometry.relativeRadius(type, 0f, 0f, 0f, 0f))
        }
    }

    @Test
    fun `positive radius below renderer minimum is not clamped`() {
        assertEquals(
            0.001f,
            GradientGeometry.relativeRadius(RelativeRadius.NEAREST_SIDE, 0.001f, 1f, 10f, 8f),
        )
    }

    private fun radius(type: RelativeRadius, centerX: Float = 2f, centerY: Float = 3f): Float =
        GradientGeometry.relativeRadius(type, centerX, centerY, width = 10f, height = 8f)

    private fun assertEndpoints(
        angle: Float,
        width: Float,
        height: Float,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
    ) {
        GradientGeometry.linearEndpoints(angle.toDouble(), width, height) { x0, y0, x1, y1 ->
            assertEquals(startX, x0, 0.00002f)
            assertEquals(startY, y0, 0.00002f)
            assertEquals(endX, x1, 0.00002f)
            assertEquals(endY, y1, 0.00002f)
        }
    }
}
