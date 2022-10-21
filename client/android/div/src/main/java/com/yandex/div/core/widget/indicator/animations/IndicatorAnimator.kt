package com.yandex.div.core.widget.indicator.animations

import android.graphics.RectF
import com.yandex.div.core.widget.indicator.IndicatorParams

interface IndicatorAnimator {

    fun getColorAt(position: Int): Int
    fun onPageScrolled(position: Int, positionOffset: Float)
    fun onPageSelected(position: Int)
    fun setItemsCount(count: Int)
    fun getSelectedItemRect(xOffset: Float, yOffset: Float): RectF?
    fun getItemSizeAt(position: Int): IndicatorParams.ItemSize
}

fun getIndicatorAnimator(style: IndicatorParams.Style): IndicatorAnimator = when (style.animation) {
    IndicatorParams.Animation.SCALE -> ScaleIndicatorAnimator(style)
    IndicatorParams.Animation.WORM -> WormIndicatorAnimator(style)
    IndicatorParams.Animation.SLIDER -> SliderIndicatorAnimator(style)
}
