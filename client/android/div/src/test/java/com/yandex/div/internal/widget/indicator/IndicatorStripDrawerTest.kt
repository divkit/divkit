package com.yandex.div.internal.widget.indicator

import com.yandex.div.core.view2.divs.createRoundedRectangle
import com.yandex.div.internal.KAssert.assertEquals
import com.yandex.div.internal.widget.indicator.animations.ScaleIndicatorAnimator
import com.yandex.div.internal.widget.indicator.forms.RoundedRect
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IndicatorStripDrawerTest {

    @Test
    fun `test max visible items don't change if width is enough`() {
        val style = IndicatorParams.Style(
            activeShape = createRoundedRectangle(
                color = 0,
                width = 10f,
                height = 10f,
                cornerRadius = 5f
            ),
            inactiveShape = createRoundedRectangle(
                color = 0,
                width = 10f,
                height = 10f,
                cornerRadius = 5f
            ),
            minimumShape = createRoundedRectangle(
                color = 0,
                width = 10f,
                height = 10f,
                cornerRadius = 5f
            ),
            itemsPlacement = IndicatorParams.ItemPlacement.Default(
                spaceBetweenCenters = 15f
            ),
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
            activeShape = createRoundedRectangle(
                color = 0,
                width = 10f,
                height = 10f,
                cornerRadius = 10f
            ),
            inactiveShape = createRoundedRectangle(
                color = 0,
                width = 10f,
                height = 10f,
                cornerRadius = 10f
            ),
            minimumShape = createRoundedRectangle(
                color = 0,
                width = 10f,
                height = 10f,
                cornerRadius = 10f
            ),
            itemsPlacement = IndicatorParams.ItemPlacement.Default(
                spaceBetweenCenters = 15f
            ),
            animation = IndicatorParams.Animation.SCALE,
        )

        val indicatorsStripDrawer = IndicatorsStripDrawer(style, RoundedRect(style), ScaleIndicatorAnimator(style))
        indicatorsStripDrawer.setItemsCount(10)
        indicatorsStripDrawer.calculateMaximumVisibleItems(100, 100)

        assertEquals(6, indicatorsStripDrawer.getMaxVisibleItems())
    }
}
