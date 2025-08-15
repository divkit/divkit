package com.yandex.div.internal.widget.indicator.animations

import android.graphics.RectF
import com.yandex.div.internal.widget.indicator.IndicatorParams

internal class WormIndicatorAnimator(private val styleParams: IndicatorParams.Style): IndicatorAnimator {

    private var selectedPositionOffset: Float = 0f
    private val itemRect = RectF()
    private var spaceBetweenCenters: Float = 0f
    private var itemWidthOverride: Float = 0f
    private val inactiveItemSizeWithBorders = when (val shape = styleParams.inactiveShape) {
        is IndicatorParams.Shape.Circle -> shape.itemSize
        is IndicatorParams.Shape.RoundedRect -> shape.itemSize.copy(
                itemWidth = shape.itemSize.itemWidth + shape.strokeWidth,
                itemHeight = shape.itemSize.itemHeight + shape.strokeWidth,
        )
    }

    override fun getColorAt(position: Int): Int = styleParams.inactiveShape.color

    override fun onPageScrolled(position: Int, positionOffset: Float) {
        selectedPositionOffset = positionOffset
    }

    override fun updateSpaceBetweenCenters(spaceBetweenCenters: Float) {
        this.spaceBetweenCenters = spaceBetweenCenters
    }

    override fun overrideItemWidth(width: Float) {
        itemWidthOverride = width
    }

    override fun getSelectedItemRect(xOffset: Float, yOffset: Float, viewportWidth: Float, isLayoutRtl: Boolean): RectF {
        val itemWidth = if (itemWidthOverride == 0f) styleParams.activeShape.itemSize.width else itemWidthOverride
        itemRect.top = yOffset - styleParams.activeShape.itemSize.height / 2f
        if (isLayoutRtl) {
            itemRect.right = xOffset - (spaceBetweenCenters * (selectedPositionOffset - 0.5f) * 2f).coerceAtLeast(0f) + itemWidth / 2f
            itemRect.left = xOffset - (spaceBetweenCenters * selectedPositionOffset * 2f).coerceAtMost(spaceBetweenCenters) - itemWidth / 2f
        } else {
            itemRect.right = xOffset + (spaceBetweenCenters * selectedPositionOffset * 2f).coerceAtMost(spaceBetweenCenters) + itemWidth / 2f
            itemRect.left = xOffset + (spaceBetweenCenters * (selectedPositionOffset - 0.5f) * 2f).coerceAtLeast(0f) - itemWidth / 2f
        }
        itemRect.bottom = yOffset + styleParams.activeShape.itemSize.height / 2f
        if (itemRect.left < 0) {
            itemRect.offset(-itemRect.left, 0f)
        }
        if (itemRect.right > viewportWidth) {
            itemRect.offset(-(itemRect.right - viewportWidth), 0f)
        }

        return itemRect
    }

    override fun getItemSizeAt(position: Int): IndicatorParams.ItemSize = inactiveItemSizeWithBorders

    override fun getBorderColorAt(position: Int): Int = styleParams.inactiveShape.borderColor

    override fun getBorderWidthAt(position: Int): Float = styleParams.inactiveShape.borderWidth
}
