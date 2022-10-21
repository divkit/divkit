package com.yandex.div.core.widget.indicator

import com.yandex.div.core.util.KAssert.assertEquals
import com.yandex.div.core.widget.indicator.animations.ScaleIndicatorAnimator
import com.yandex.div.core.widget.indicator.forms.RoundedRect
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IndicatorStripDrawerTest {

    @Test
    fun `test max visible items don't change if width is enough`() {
        val style = IndicatorParams.Style(
            color = 0,
            selectedColor = 0,
            shape = IndicatorParams.Shape.RoundedRect(
                normalWidth = 10f,
                selectedWidth = 10f,
                minimumWidth = 10f,
                normalHeight = 10f,
                selectedHeight = 10f,
                minimumHeight = 10f,
                cornerRadius = 5f,
                selectedCornerRadius = 5f,
                minimumCornerRadius = 5f,
            ),
            spaceBetweenCenters = 15f,
            animation = IndicatorParams.Animation.SCALE,
        )

        val indicatorsStripDrawer = IndicatorsStripDrawer(style, RoundedRect(style), ScaleIndicatorAnimator(style))
        indicatorsStripDrawer.setItemsCount(10)
        indicatorsStripDrawer.calculateMaximumVisibleItems(300, 300)

        assertEquals(10, indicatorsStripDrawer.getMaxVisibleItems())
    }

    @Test
    fun `test max visible changed`() {
        val style = IndicatorParams.Style(
            color = 0,
            selectedColor = 0,
            shape = IndicatorParams.Shape.RoundedRect(
                normalWidth = 10f,
                selectedWidth = 10f,
                minimumWidth = 10f,
                normalHeight = 10f,
                selectedHeight = 10f,
                minimumHeight = 10f,
                cornerRadius = 10f,
                selectedCornerRadius = 10f,
                minimumCornerRadius = 10f,
            ),
            spaceBetweenCenters = 15f,
            animation = IndicatorParams.Animation.SCALE,
        )

        val indicatorsStripDrawer = IndicatorsStripDrawer(style, RoundedRect(style), ScaleIndicatorAnimator(style))
        indicatorsStripDrawer.setItemsCount(10)
        indicatorsStripDrawer.calculateMaximumVisibleItems(100, 100)

        assertEquals(6, indicatorsStripDrawer.getMaxVisibleItems())
    }
}
