package com.yandex.div.core.widget.indicator.animations

import android.graphics.RectF
import com.yandex.div.core.widget.indicator.IndicatorParams

class SliderIndicatorAnimator(
    private val styleParams: IndicatorParams.Style
) : IndicatorAnimator {

    private var selectedPosition: Int = 0
    private var selectedPositionOffset: Float = 0f
    private var itemsCount: Int = 0
    private val itemRect = RectF()
    private val spaceBetweenCenters: Float = styleParams.spaceBetweenCenters

    override fun getColorAt(position: Int): Int = styleParams.color

    override fun onPageScrolled(position: Int, positionOffset: Float) {
        selectedPosition = position
        selectedPositionOffset = positionOffset
    }

    override fun onPageSelected(position: Int) {
        selectedPosition = position
    }

    override fun setItemsCount(count: Int) {
        itemsCount = count
    }

    override fun getSelectedItemRect(xOffset: Float, yOffset: Float): RectF? {
        itemRect.left = xOffset + (spaceBetweenCenters * selectedPositionOffset).coerceAtLeast(0f) - styleParams.shape.width / 2f
        itemRect.top = yOffset - styleParams.shape.height / 2f
        itemRect.right = xOffset + (spaceBetweenCenters * selectedPositionOffset).coerceAtMost(spaceBetweenCenters) + styleParams.shape.width / 2f
        itemRect.bottom = yOffset + styleParams.shape.height / 2f
        return itemRect
    }

    override fun getItemSizeAt(position: Int): IndicatorParams.ItemSize = styleParams.shape.normalItemSize
}
