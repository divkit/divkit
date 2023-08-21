package com.yandex.div.internal.widget.indicator.animations

import android.graphics.RectF
import com.yandex.div.internal.widget.indicator.IndicatorParams

internal interface IndicatorAnimator {

    fun getColorAt(position: Int): Int
    fun onPageScrolled(position: Int, positionOffset: Float)
    fun onPageSelected(position: Int)
    fun updateSpaceBetweenCenters(spaceBetweenCenters: Float) = Unit
    fun overrideItemWidth(width: Float) = Unit
    fun setItemsCount(count: Int)
    fun getSelectedItemRect(xOffset: Float, yOffset: Float, viewportWidth: Float, isLayoutRtl: Boolean): RectF?
    fun getItemSizeAt(position: Int): IndicatorParams.ItemSize
    fun getBorderColorAt(position: Int): Int
    fun getBorderWidthAt(position: Int): Float
}

internal fun getIndicatorAnimator(style: IndicatorParams.Style): IndicatorAnimator = when (style.animation) {
    IndicatorParams.Animation.SCALE -> ScaleIndicatorAnimator(style)
    IndicatorParams.Animation.WORM -> WormIndicatorAnimator(style)
    IndicatorParams.Animation.SLIDER -> SliderIndicatorAnimator(style)
}
