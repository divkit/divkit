package com.yandex.div.compose.utils.gradient

import android.graphics.Color
import android.graphics.Matrix
import androidx.compose.ui.geometry.Size
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div2.DivRadialGradientRelativeRadius
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class AnimatedGradientBrushTest {

    @Test
    fun `linear brush matrix has exact translation for animation phase`() {
        val phase = 0.25f
        val translation = LinearGradientBrush(
            angle = 0,
            colorMap = COLOR_MAP,
            animationPhase = phase,
        )
            .createShader(SIZE)
            .translationX()

        assertEquals(-100f, translation, TOLERANCE)
    }

    @Test
    fun `radial brush matrix has exact translation for animation phase`() {
        val phase = 0.25f
        val translation = RadialGradientBrush(
            centerX = CENTER,
            centerY = CENTER,
            radius = RADIUS,
            colorMap = COLOR_MAP,
            animationPhase = phase,
        )
            .createShader(SIZE)
            .translationX()

        assertEquals(-100f, translation, TOLERANCE)
    }

    private fun android.graphics.Shader.translationX(): Float {
        val matrix = Matrix()
        @Suppress("DEPRECATION")
        getLocalMatrix(matrix)
        return FloatArray(9).also(matrix::getValues)[Matrix.MTRANS_X]
    }

    private companion object {
        val SIZE = Size(200f, 40f)
        val COLOR_MAP = ColorMap(intArrayOf(Color.RED, Color.WHITE, Color.BLUE), null)
        val CENTER = RadialGradientBrush.Center.Relative(0.5f)
        val RADIUS = RadialGradientBrush.Radius.Relative(DivRadialGradientRelativeRadius.Value.FARTHEST_CORNER)
        const val TOLERANCE = 0.001f
    }
}
