package com.yandex.div.compose.views.pager

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.graphics.GraphicsLayerScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val TOLERANCE = 1e-4f

class PageTransformationTest {

    private val slide = PageTransformation(
        nextAlpha = 0.2f,
        previousAlpha = 0.6f,
        nextScale = 0.5f,
        previousScale = 0.8f,
        easing = LinearEasing,
    )

    @Test
    fun `selected page keeps its alpha and scale`() {
        assertAppearance(slide, position = 0f, alpha = 1f, scale = 1f)
    }

    @Test
    fun `next page moves towards the next page alpha and scale`() {
        assertAppearance(slide, position = 0.5f, alpha = 0.6f, scale = 0.75f)
    }

    @Test
    fun `previous page moves towards the previous page alpha and scale`() {
        assertAppearance(slide, position = -0.5f, alpha = 0.8f, scale = 0.9f)
    }

    @Test
    fun `pages beyond the neighbours keep the neighbour alpha and scale`() {
        assertAppearance(slide, position = 2.5f, alpha = 0.2f, scale = 0.5f)
        assertAppearance(slide, position = -3f, alpha = 0.6f, scale = 0.8f)
    }

    @Test
    fun `interpolator eases the progress`() {
        val easeIn = CubicBezierEasing(0.42f, 0f, 1f, 1f)
        val fraction = easeIn.transform(0.5f)

        assertAppearance(
            slide.copy(easing = easeIn),
            position = 0.5f,
            alpha = 1f + (0.2f - 1f) * fraction,
            scale = 1f + (0.5f - 1f) * fraction,
        )
    }

    @Test
    fun `slide moves every page`() {
        assertFalse(slide.isStationary(-0.5f))
        assertFalse(slide.isStationary(0f))
        assertFalse(slide.isStationary(0.5f))
    }

    @Test
    fun `overlap keeps the pages up to the selected one in place under the following pages`() {
        val overlap = slide.copy(overlap = true)

        assertTrue(overlap.isStationary(-0.5f))
        assertTrue(overlap.isStationary(0f))
        assertFalse(overlap.isStationary(0.5f))
        assertTrue(overlap.stackingOrder(0.5f) > overlap.stackingOrder(-0.5f))
    }

    @Test
    fun `reversed overlap keeps the pages from the selected one in place under the previous pages`() {
        val reversed = PageTransformation(
            nextAlpha = 0.2f,
            previousAlpha = 0.6f,
            nextScale = 0.5f,
            previousScale = 0.8f,
            easing = LinearEasing,
            overlap = true,
            reversedStackingOrder = true,
        )

        assertFalse(reversed.isStationary(-0.5f))
        assertTrue(reversed.isStationary(0f))
        assertTrue(reversed.isStationary(0.5f))
        assertTrue(reversed.stackingOrder(-0.5f) > reversed.stackingOrder(0.5f))
    }

    private fun assertAppearance(transformation: PageTransformation, position: Float, alpha: Float, scale: Float) {
        val layer = GraphicsLayerScope()
        transformation.applyAppearance(layer, position)
        assertEquals(alpha, layer.alpha, absoluteTolerance = TOLERANCE)
        assertEquals(scale, layer.scaleX, absoluteTolerance = TOLERANCE)
        assertEquals(scale, layer.scaleY, absoluteTolerance = TOLERANCE)
    }
}
