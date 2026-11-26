package com.yandex.div.compose.utils.gradient

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.yandex.div.compose.utils.gradient.RadialGradientBrush.Center
import com.yandex.div.compose.utils.gradient.RadialGradientBrush.Radius
import com.yandex.div.render.common.gradient.GradientGeometry.RelativeRadius
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class RadialGradientBrushTest {

    @Test
    fun `fixed center and radius retain fractional pixel values`() {
        val brush = brush(centerX = Center.Fixed(-1.5f), centerY = Center.Fixed(4.25f))

        brush.withGeometry(BOUNDS) { center, radius ->
            assertEquals(Offset(-1.5f, 4.25f), center)
            assertEquals(2.75f, radius)
        }
    }

    @Test
    fun `relative center scales along each axis`() {
        brush().withGeometry(BOUNDS) { center, _ ->
            assertEquals(Offset(3f, 2f), center)
        }
    }

    @Test
    fun `nearest corner radius reaches the closest corner`() {
        assertRelativeRadius(RelativeRadius.NEAREST_CORNER, sqrt(13f))
    }

    @Test
    fun `farthest corner radius reaches the most distant corner`() {
        assertRelativeRadius(RelativeRadius.FARTHEST_CORNER, sqrt(117f))
    }

    @Test
    fun `nearest side radius reaches the closest edge`() {
        assertRelativeRadius(RelativeRadius.NEAREST_SIDE, 2f)
    }

    @Test
    fun `farthest side radius reaches the most distant edge`() {
        assertRelativeRadius(RelativeRadius.FARTHEST_SIDE, 9f)
    }

    @Test
    fun `zero fixed radius uses the minimum shader radius`() {
        brush(radius = Radius.Fixed(0f)).withGeometry(BOUNDS) { _, radius ->
            assertEquals(0.01f, radius)
        }
    }

    @Test
    fun `center on an edge gives the minimum nearest side radius`() {
        val brush = brush(centerY = Center.Relative(0f), radius = Radius.Relative(RelativeRadius.NEAREST_SIDE))

        brush.withGeometry(BOUNDS) { _, radius ->
            assertEquals(0.01f, radius)
        }
    }

    @Test
    fun `animation translates the center horizontally and retains the radius`() {
        val brush = brush(radius = Radius.Relative(RelativeRadius.FARTHEST_SIDE), animationPhase = 0.25f)

        brush.withGeometry(BOUNDS) { center, radius ->
            assertEquals(Offset(-3f, 2f), center)
            assertEquals(9f, radius)
        }
    }

    private fun assertRelativeRadius(type: RelativeRadius, expectedRadius: Float) {
        brush(radius = Radius.Relative(type)).withGeometry(BOUNDS) { _, radius ->
            assertEquals(expectedRadius, radius)
        }
    }

    private fun brush(
        centerX: Center = Center.Relative(0.25f),
        centerY: Center = Center.Relative(0.25f),
        radius: Radius = Radius.Fixed(2.75f),
        animationPhase: Float? = null,
    ) = RadialGradientBrush(centerX, centerY, radius, listOf(Color.Red, Color.Blue), null, animationPhase)

    private companion object {
        val BOUNDS = Size(12f, 8f)
    }
}
