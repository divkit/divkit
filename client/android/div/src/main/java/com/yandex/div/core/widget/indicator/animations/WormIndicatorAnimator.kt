package com.yandex.div.core.widget.indicator.animations

import android.graphics.RectF
import com.yandex.div.core.widget.indicator.IndicatorParams

class WormIndicatorAnimator(private val style: IndicatorParams.Style): IndicatorAnimator {

    private var selectedPosition: Int = 0
    private var selectedPositionOffset: Float = 0f
    private var itemsCount: Int = 0
    private val itemRect = RectF()
    private val spaceBetweenCenters = style.spaceBetweenCenters

    override fun getItemWidthAt(position: Int) = style.normalWidth

    override fun getItemHeightAt(position: Int) = style.normalHeight

    override fun getItemCornerRadiusAt(position: Int) = style.cornerRadius

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
        itemRect.top = yOffset - style.normalHeight / 2f
        itemRect.right = xOffset + (spaceBetweenCenters * selectedPositionOffset * 2f).coerceAtMost(spaceBetweenCenters) + style.normalWidth / 2f
        itemRect.bottom = yOffset + style.normalHeight / 2f
        itemRect.left = xOffset + (spaceBetweenCenters * (selectedPositionOffset - 0.5f) * 2f).coerceAtLeast(0f) - style.normalWidth / 2f
        return itemRect
    }
}
