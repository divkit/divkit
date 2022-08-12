package com.yandex.div.core.widget.indicator.animations

import android.graphics.RectF
import com.yandex.div.core.widget.indicator.IndicatorParams

class SliderIndicatorAnimator(private val style: IndicatorParams.Style): IndicatorAnimator {

    private var selectedPosition: Int = 0
    private var selectedPositionOffset: Float = 0f
    private var itemsCount: Int = 0
    private val itemRect = RectF()
    private val spaceBetweenCenters = style.spaceBetweenCenters

    override fun getItemWidthAt(position: Int) = style.normalWidth

    override fun getItemHeightAt(position: Int) = style.normalHeight

    override fun getItemCornerRadiusAt(position: Int): Float = style.cornerRadius

    override fun getColorAt(position: Int) = style.color

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

    override fun getSelectedItemRect(xOffset: Float, yOffset: Float): RectF {
        itemRect.left = xOffset + (spaceBetweenCenters * selectedPositionOffset).coerceAtLeast(0f) - style.selectedWidth / 2f
        itemRect.top = yOffset - style.selectedHeight / 2f
        itemRect.right = xOffset + (spaceBetweenCenters * selectedPositionOffset).coerceAtMost(spaceBetweenCenters) + style.selectedWidth / 2f
        itemRect.bottom = yOffset + style.selectedHeight / 2f
        return itemRect
    }
}
