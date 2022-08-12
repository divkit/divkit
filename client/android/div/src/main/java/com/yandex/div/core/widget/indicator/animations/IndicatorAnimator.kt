package com.yandex.div.core.widget.indicator.animations

import android.graphics.RectF

interface IndicatorAnimator {

    fun getItemWidthAt(position: Int): Float
    fun getItemHeightAt(position: Int): Float
    fun getItemCornerRadiusAt(position: Int): Float
    fun getColorAt(position: Int): Int
    fun onPageScrolled(position: Int, positionOffset: Float)
    fun onPageSelected(position: Int)
    fun setItemsCount(count: Int)
    fun getSelectedItemRect(xOffset: Float, yOffset: Float): RectF?

}
