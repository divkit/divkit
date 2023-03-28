@file:Suppress("DEPRECATION")

package com.yandex.div.core.view2.divs.widgets

import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import androidx.core.view.doOnLayout
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.internal.util.dpToPx
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBlur
import com.yandex.div2.DivFilter
import kotlin.math.max

private const val RADIUS_MAX_VALUE_PX = 25

internal fun Bitmap.applyFilters(
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

    target.doOnLayout {
        val scale = max(target.height / height.toFloat(), target.width / width.toFloat())
        var bitmap = Bitmap.createScaledBitmap(
            this,
            (scale * width).toInt(),
            (scale * height).toInt(),
            false
        )
        for (filter in filters) {
            when (filter) {
                is DivFilter.Blur -> bitmap = bitmap.getBlurredBitmap(filter.value, component, resolver)
            }
        }
        actionAfterFilters(bitmap)
    }
}

internal fun Bitmap.getBlurredBitmap(blur: DivBlur, component: Div2Component, resolver: ExpressionResolver): Bitmap {
    var radius = blur.radius.evaluate(resolver).toIntSafely()
    if (radius == 0) {
        return this
    }

    radius = radius.dpToPx()
    var sampling = 1f
    if (radius > RADIUS_MAX_VALUE_PX) {
        sampling = radius * 1f / RADIUS_MAX_VALUE_PX
        radius = RADIUS_MAX_VALUE_PX
    }

    val bitmap = if (sampling == 1f) {
        this
    } else {
        Bitmap.createScaledBitmap(this, (width / sampling).toInt(), (height / sampling).toInt(), false)
    }

    val rs = component.renderScript
    val input = Allocation.createFromBitmap(rs, bitmap)
    val output = Allocation.createTyped(rs, input.type)
    ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)).apply {
        setRadius(radius.toFloat())
        setInput(input)
        forEach(output)
    }
    output.copyTo(bitmap)
    return bitmap
}
