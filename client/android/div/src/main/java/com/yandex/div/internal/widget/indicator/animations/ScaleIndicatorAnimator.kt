package com.yandex.div.internal.widget.indicator.animations

import android.animation.ArgbEvaluator
import android.graphics.RectF
import android.util.SparseArray
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.yandex.div.internal.widget.indicator.IndicatorParams
import kotlin.math.abs

internal class ScaleIndicatorAnimator(private val styleParams: IndicatorParams.Style) : IndicatorAnimator {

    private val colorEvaluator: ArgbEvaluator = ArgbEvaluator()
    private val itemsScale = SparseArray<Float>()
    private var itemsCount: Int = 0

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

    override fun getItemSizeAt(position: Int): IndicatorParams.ItemSize {
        return when (val shape = styleParams.shape) {
            is IndicatorParams.Shape.Circle -> {
                IndicatorParams.ItemSize.Circle(
                    shape.normalRadius + (shape.selectedRadius - shape.normalRadius) * getScaleAt(position)
                )
            }
            is IndicatorParams.Shape.RoundedRect -> {
                IndicatorParams.ItemSize.RoundedRect(
                    shape.normalWidth + (shape.selectedWidth - shape.normalWidth) * getScaleAt(position),
                    shape.normalHeight + (shape.selectedHeight - shape.normalHeight) * getScaleAt(position),
                    shape.cornerRadius + (shape.selectedCornerRadius - shape.cornerRadius) * getScaleAt(position)
                )
            }
        }
    }

    private fun scaleIndicatorByOffset(position: Int, offset: Float) {
        if (offset == 0f) {
            itemsScale.remove(position)
        } else {
            itemsScale.put(position, abs(offset))
        }
    }

    private fun getScaleAt(position: Int): Float = itemsScale.get(position, 0f)
    @ColorInt
    private fun calculateColor(@FloatRange(from = 0.0, to = 1.0) scaleOffset: Float): Int {
        return colorEvaluator.evaluate(scaleOffset, styleParams.color, styleParams.selectedColor) as Int
    }

}
