package com.yandex.div.core.view2.divs.widgets

import android.graphics.Color
import android.graphics.Paint
import android.view.View
import com.yandex.div.core.view2.divs.context
import com.yandex.div.core.view2.divs.divView
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBorder
import com.yandex.div2.DivDimension
import com.yandex.div2.DivPoint
import com.yandex.div2.DivShadow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivBorderDrawerShadowTest {

    private val context = context()
    private val divView = divView()
    private val resolver = ExpressionResolver.EMPTY

    @Test
    fun `opaque shadow color keeps full alpha`() {
        assertEquals(255, shadowPaintAlpha(color = Color.BLACK, shadowAlpha = 1.0))
    }

    @Test
    fun `color-embedded alpha is applied to the shadow`() {
        assertEquals(0x2F, shadowPaintAlpha(color = 0x2F000000, shadowAlpha = 1.0))
    }

    @Test
    fun `color-embedded alpha is multiplied by shadow alpha, not replaced`() {
        val opaque = shadowPaintAlpha(color = Color.BLACK, shadowAlpha = 1.0)
        val semiTransparent = shadowPaintAlpha(color = 0x2F000000, shadowAlpha = 1.0)

        assertTrue(
            "shadow color alpha must reduce paint alpha (was $semiTransparent, opaque $opaque)",
            semiTransparent < opaque
        )
    }

    @Test
    fun `color alpha and shadow alpha combine multiplicatively`() {
        val full = shadowPaintAlpha(color = 0x2F000000, shadowAlpha = 1.0)
        val half = shadowPaintAlpha(color = 0x2F000000, shadowAlpha = 0.5)

        assertEquals(full / 2, half)
    }

    @Test
    fun `shadow with default color renders as opaque black`() {
        assertEquals(
            shadowPaintAlpha(color = Color.BLACK, shadowAlpha = 1.0),
            defaultColorShadowPaintAlpha(shadowAlpha = 1.0)
        )
    }

    @Test
    fun `explicit fully transparent shadow color produces no shadow`() {
        assertEquals(0, shadowPaintAlpha(color = 0x00000000, shadowAlpha = 1.0))
    }

    private fun shadowPaintAlpha(color: Int, shadowAlpha: Double): Int =
        shadowPaintAlpha(shadowWith(Expression.constant(color), shadowAlpha))

    private fun defaultColorShadowPaintAlpha(shadowAlpha: Double): Int =
        shadowPaintAlpha(shadowWith(color = null, shadowAlpha = shadowAlpha))

    private fun shadowPaintAlpha(shadow: DivShadow): Int {
        val view = View(context)
        val drawer = DivBorderDrawer(divView, view)
        drawer.setBorder(DivBorder(hasShadow = Expression.constant(true), shadow = shadow), resolver)
        return shadowPaint(drawer).alpha
    }

    private fun shadowWith(color: Expression<Int>?, shadowAlpha: Double): DivShadow {
        val offset = DivPoint(
            DivDimension(value = Expression.constant(0.0)),
            DivDimension(value = Expression.constant(0.0)),
        )
        val base = DivShadow(
            alpha = Expression.constant(shadowAlpha),
            blur = Expression.constant(0L),
            offset = offset,
        )
        return if (color != null) base.copy(color = color) else base
    }

    private fun shadowPaint(drawer: DivBorderDrawer): Paint {
        val delegateField = DivBorderDrawer::class.java.declaredFields
            .first { it.name.contains("shadowParams") }
            .apply { isAccessible = true }
        val raw = delegateField.get(drawer)
        val shadowParams = if (raw is Lazy<*>) raw.value!! else raw!!
        val paintField = shadowParams.javaClass.getDeclaredField("paint").apply { isAccessible = true }
        return paintField.get(shadowParams) as Paint
    }
}
