package com.yandex.div.core.widget.indicator.animations

import android.animation.ArgbEvaluator
import android.graphics.RectF
import android.util.SparseArray
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yandex.div.core.widget.indicator.IndicatorParams
import kotlin.math.abs

class ScaleIndicatorAnimator(
    private val styleParams: IndicatorParams.Style,
) : IndicatorAnimator {
    private val colorEvaluator: ArgbEvaluator = ArgbEvaluator()
    private val itemsScale = SparseArray<Float>()
    private var itemsCount: Int = 0

    override fun getItemWidthAt(position: Int): Float {
        return styleParams.normalWidth + (styleParams.selectedWidth - styleParams.normalWidth) * getScaleAt(position)
    }

    override fun getItemHeightAt(position: Int): Float {
        return styleParams.normalHeight + (styleParams.selectedHeight - styleParams.normalHeight) * getScaleAt(position)
    }

    override fun getItemCornerRadiusAt(position: Int): Float {
        return styleParams.cornerRadius + (styleParams.selectedCornerRadius - styleParams.cornerRadius) * getScaleAt(position)
    }

    override fun getColorAt(position: Int) = calculateColor(getScaleAt(position))

    override fun onPageScrolled(position: Int, positionOffset: Float) {
        scaleIndicatorByOffset(position, 1f - positionOffset)
        if (position < itemsCount - 1) {
            scaleIndicatorByOffset(position + 1, positionOffset)
        } else {
            scaleIndicatorByOffset(0, positionOffset)
        }
    }

    override fun onPageSelected(position: Int) {
        itemsScale.clear()
        itemsScale.put(position, 1f)
    }

    override fun setItemsCount(count: Int) {
        itemsCount = count
    }

    override fun getSelectedItemRect(xOffset: Float, yOffset: Float): RectF? = null

    private fun scaleIndicatorByOffset(position: Int, offset: Float) {
        if (offset == 0f) {
            itemsScale.remove(position)
        } else {
            itemsScale.put(position, abs(offset))
        }
    }

    private fun getScaleAt(position: Int) = itemsScale.get(position, 0f)
    @ColorInt
    private fun calculateColor(@FloatRange(from = 0.0, to = 1.0) scaleOffset: Float): Int {
        return colorEvaluator.evaluate(scaleOffset, styleParams.color, styleParams.selectedColor) as Int
    }

}