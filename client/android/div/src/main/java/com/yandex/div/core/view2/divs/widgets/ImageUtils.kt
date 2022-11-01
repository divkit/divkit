package com.yandex.div.core.view2.divs.widgets

import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import androidx.core.view.doOnPreDraw
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.util.dpToPx
import com.yandex.div2.DivBlur
import com.yandex.div2.DivFilter
import kotlin.math.max

fun Bitmap.applyFilters(
    target: View,
    filters: List<DivFilter>?,
    component: Div2Component,
    resolver: ExpressionResolver,
    actionAfterFilters: (Bitmap) -> Unit
) {
    if (filters == null) {
        actionAfterFilters(this)
        return
    }

    target.doOnPreDraw {
        val scale = max(target.height / height.toFloat(), target.width / width.toFloat())
        val bitmap = Bitmap.createScaledBitmap(
            this,
            (scale * width).toInt(),
            (scale * height).toInt(),
            false
        )
        for (filter in filters) {
            when (filter) {
                is DivFilter.Blur -> bitmap.applyBlur(filter.value, component, resolver)
            }
        }
        actionAfterFilters(bitmap)
    }
}

private const val RADIUS_MAX_VALUE_PX = 25

fun Bitmap.applyBlur(blur: DivBlur, component: Div2Component, resolver: ExpressionResolver) {
    val radius = blur.radius.evaluate(resolver).dpToPx().let {
        if (it > RADIUS_MAX_VALUE_PX) RADIUS_MAX_VALUE_PX else it
    }.toFloat()

    val rs = component.renderScript
    val input = Allocation.createFromBitmap(rs, this)
    val output = Allocation.createTyped(rs, input.type)
    ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)).apply {
        setRadius(radius)
        setInput(input)
        forEach(output)
    }
    output.copyTo(this)
}
