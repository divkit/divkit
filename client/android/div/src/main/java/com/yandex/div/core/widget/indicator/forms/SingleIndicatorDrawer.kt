package com.yandex.div.core.widget.indicator.forms

import android.graphics.Canvas
import android.graphics.RectF
import com.yandex.div.core.widget.indicator.IndicatorParams
import com.yandex.div.core.widget.indicator.animations.IndicatorAnimator
import com.yandex.div.core.widget.indicator.animations.ScaleIndicatorAnimator
import com.yandex.div.core.widget.indicator.animations.SliderIndicatorAnimator
import com.yandex.div.core.widget.indicator.animations.WormIndicatorAnimator

interface SingleIndicatorDrawer {

    fun draw(canvas: Canvas, x: Float, y: Float, itemSize: IndicatorParams.ItemSize, color: Int)

    fun drawSelected(canvas: Canvas, rect: RectF)

}

fun getIndicatorDrawer(style: IndicatorParams.Style): SingleIndicatorDrawer = when (style.shape) {
    is IndicatorParams.Shape.RoundedRect -> RoundedRect(style)
    is IndicatorParams.Shape.Circle -> Circle(style)
}
