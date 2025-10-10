package com.yandex.div.core.util

import android.util.DisplayMetrics
import com.yandex.div.core.view2.divs.toPxF
import com.yandex.div.internal.drawable.RadialGradientDrawable
import com.yandex.div.internal.graphics.Colormap
import com.yandex.div.internal.util.compareWith
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivRadialGradient
import com.yandex.div2.DivRadialGradientCenter
import com.yandex.div2.DivRadialGradientRadius
import com.yandex.div2.DivRadialGradientRelativeRadius

@JvmName("isLinearColorMapConstantOrNull")
internal fun List<DivLinearGradient.ColorPoint>?.isConstantOrNull(): Boolean =
    this?.all { it.isConstant() } ?: true

@JvmName("isRadialColorMapConstantOrNull")
internal fun List<DivRadialGradient.ColorPoint>?.isConstantOrNull(): Boolean =
    this?.all { it.isConstant() } ?: true

internal fun DivLinearGradient.ColorPoint?.equalsToConstant(other: DivLinearGradient.ColorPoint?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.color.equalsToConstant(other?.color)
        && this?.position.equalsToConstant(other?.position)
}

internal fun DivLinearGradient.ColorPoint?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return color.isConstant() && position.isConstant()
}

internal fun DivLinearGradient.colorsEqualToConstant(other: DivLinearGradient): Boolean {
    val lm = this.colorMap
    val rm = other.colorMap
    return if (!lm.isNullOrEmpty() || !rm.isNullOrEmpty()) {
        (lm ?: emptyList()).compareWith(rm ?: emptyList()) { l, r -> l.equalsToConstant(r) }
    } else {
        this.colors.equalsToConstant(other.colors)
    }
}

internal fun DivRadialGradient.colorsEqualToConstant(other: DivRadialGradient): Boolean {
    val lm = this.colorMap
    val rm = other.colorMap
    return if (!lm.isNullOrEmpty() || !rm.isNullOrEmpty()) {
        (lm ?: emptyList()).compareWith(rm ?: emptyList()) { l, r -> l.equalsToConstant(r) }
    } else {
        this.colors.equalsToConstant(other.colors)
    }
}

internal fun DivRadialGradient.ColorPoint?.equalsToConstant(other: DivRadialGradient.ColorPoint?): Boolean {
    if (this == null && other == null) {
        return true
    }
    return this?.color.equalsToConstant(other?.color)
        && this?.position.equalsToConstant(other?.position)
}

internal fun DivRadialGradient.ColorPoint?.isConstant(): Boolean {
    if (this == null) {
        return true
    }
    return color.isConstant() && position.isConstant()
}

private fun buildColormap(
    points: List<Pair<Int, Float>>?,
    colors: List<Int>?,
): Colormap = when {
    points != null -> {
        val sorted = points.sortedBy { it.second }
        val c = IntArray(sorted.size) { i -> sorted[i].first }
        val p = FloatArray(sorted.size) { i -> sorted[i].second }
        Colormap(c, p)
    }
    colors != null -> Colormap(colors.toIntArray())
    else -> Colormap.EMPTY
}

internal fun DivLinearGradient.toColormap(resolver: ExpressionResolver): Colormap {
    val points = colorMap
        ?.map { it.color.evaluate(resolver) to it.position.evaluate(resolver).toFloat() }
    val flat = colors?.evaluate(resolver)
    return buildColormap(points, flat)
}

internal fun DivRadialGradient.toColormap(resolver: ExpressionResolver): Colormap {
    val points = colorMap
        ?.map { it.color.evaluate(resolver) to it.position.evaluate(resolver).toFloat() }
    val flat = colors?.evaluate(resolver)
    return buildColormap(points, flat)
}

internal fun DivRadialGradientCenter?.equalsToConstant(other: DivRadialGradientCenter?): Boolean {
    return when (this) {
        null -> other == null

        is DivRadialGradientCenter.Fixed -> {
            other is DivRadialGradientCenter.Fixed
                && value.unit.equalsToConstant(other.value.unit)
                && value.value.equalsToConstant(other.value.value)
        }

        is DivRadialGradientCenter.Relative -> {
            other is DivRadialGradientCenter.Relative
                && value.value.equalsToConstant(other.value.value)
        }
    }
}

internal fun DivRadialGradientCenter?.isConstant(): Boolean {
    return when (this) {
        null -> true
        is DivRadialGradientCenter.Fixed -> value.unit.isConstant() && value.value.isConstant()
        is DivRadialGradientCenter.Relative -> value.value.isConstant()
    }
}

internal fun DivRadialGradientRadius?.equalsToConstant(other: DivRadialGradientRadius?): Boolean {
    return when (this) {
        null -> other == null

        is DivRadialGradientRadius.FixedSize -> {
            other is DivRadialGradientRadius.FixedSize
                && value.unit.equalsToConstant(other.value.unit)
                && value.value.equalsToConstant(other.value.value)
        }

        is DivRadialGradientRadius.Relative -> {
            other is DivRadialGradientRadius.Relative
                && value.value.equalsToConstant(other.value.value)
        }
    }
}

internal fun DivRadialGradientRadius?.isConstant(): Boolean {
    return when (this) {
        null -> true
        is DivRadialGradientRadius.FixedSize -> value.unit.isConstant() && value.value.isConstant()
        is DivRadialGradientRadius.Relative -> value.value.isConstant()
    }
}

internal fun DivRadialGradientRadius.toRadialGradientDrawableRadius(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): RadialGradientDrawable.Radius {
    return when (this) {
        is DivRadialGradientRadius.FixedSize -> RadialGradientDrawable.Radius.Fixed(value.toPxF(metrics, resolver))
        is DivRadialGradientRadius.Relative -> {
            RadialGradientDrawable.Radius.Relative(
                when (value.value.evaluate(resolver)) {
                    DivRadialGradientRelativeRadius.Value.FARTHEST_CORNER ->
                        RadialGradientDrawable.Radius.Relative.Type.FARTHEST_CORNER
                    DivRadialGradientRelativeRadius.Value.NEAREST_CORNER ->
                        RadialGradientDrawable.Radius.Relative.Type.NEAREST_CORNER
                    DivRadialGradientRelativeRadius.Value.FARTHEST_SIDE ->
                        RadialGradientDrawable.Radius.Relative.Type.FARTHEST_SIDE
                    DivRadialGradientRelativeRadius.Value.NEAREST_SIDE ->
                        RadialGradientDrawable.Radius.Relative.Type.NEAREST_SIDE
                }
            )
        }
    }
}

internal fun DivRadialGradientCenter.toRadialGradientDrawableCenter(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): RadialGradientDrawable.Center {
    return when (this) {
        is DivRadialGradientCenter.Fixed -> {
            RadialGradientDrawable.Center.Fixed(
                value.value.evaluate(resolver).toPxF(value.unit.evaluate(resolver), metrics)
            )
        }

        is DivRadialGradientCenter.Relative ->
            RadialGradientDrawable.Center.Relative(value.value.evaluate(resolver).toFloat())
    }
}
