package com.yandex.div.internal.widget.indicator.animations

import android.graphics.RectF
import com.yandex.div.internal.widget.indicator.IndicatorParams

internal class WormIndicatorAnimator(private val styleParams: IndicatorParams.Style): IndicatorAnimator {

    private var selectedPosition: Int = 0
    private var selectedPositionOffset: Float = 0f
    private var itemsCount: Int = 0
    private val itemRect = RectF()
    private var spaceBetweenCenters: Float = 0f
    private var itemWidthOverride: Float = 0f

    override fun getColorAt(position: Int): Int = styleParams.inactiveShape.color

    override fun onPageScrolled(position: Int, positionOffset: Float) {
        selectedPosition = position
        selectedPositionOffset = positionOffset
    }

    override fun onPageSelected(position: Int) {
        selectedPosition = position
    }

    override fun updateSpaceBetweenCenters(spaceBetweenCenters: Float) {
        this.spaceBetweenCenters = spaceBetweenCenters
    }

    override fun overrideItemWidth(width: Float) {
        itemWidthOverride = width
    }

    override fun setItemsCount(count: Int) {
        itemsCount = count
    }

    override fun getSelectedItemRect(xOffset: Float, yOffset: Float): RectF {
        val itemWidth = if (itemWidthOverride == 0f) styleParams.activeShape.itemSize.width else itemWidthOverride
        itemRect.top = yOffset - styleParams.activeShape.itemSize.height / 2f
        itemRect.right = xOffset + (spaceBetweenCenters * selectedPositionOffset * 2f).coerceAtMost(spaceBetweenCenters) + itemWidth / 2f
        itemRect.bottom = yOffset + styleParams.activeShape.itemSize.height / 2f
        itemRect.left = xOffset + (spaceBetweenCenters * (selectedPositionOffset - 0.5f) * 2f).coerceAtLeast(0f) - itemWidth / 2f
        return itemRect
    }

    override fun getItemSizeAt(position: Int): IndicatorParams.ItemSize = styleParams.inactiveShape.itemSize
}
