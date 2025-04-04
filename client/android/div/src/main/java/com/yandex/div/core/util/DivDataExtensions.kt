package com.yandex.div.core.util

import com.yandex.div.core.downloader.DivPatchApply
import com.yandex.div.core.downloader.DivPatchMap
import com.yandex.div.internal.graphics.Colormap
import com.yandex.div.internal.util.compareWith
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.DivAbsoluteEdgeInsets
import com.yandex.div2.DivBackground
import com.yandex.div2.DivBorder
import com.yandex.div2.DivCornersRadius
import com.yandex.div2.DivData
import com.yandex.div2.DivDimension
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFilter
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivInput
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivPatch
import com.yandex.div2.DivPivot
import com.yandex.div2.DivPoint
import com.yandex.div2.DivRadialGradientCenter
import com.yandex.div2.DivRadialGradientRadius
import com.yandex.div2.DivShadow
import com.yandex.div2.DivShape
import com.yandex.div2.DivSize
import com.yandex.div2.DivStroke
import com.yandex.div2.DivTransform

internal fun DivSize?.equalsToConstant(other: DivSize?): Boolean {
     return when (this) {
        null -> other == null

        is DivSize.Fixed -> {
            other is DivSize.Fixed
                && value.value.equalsToConstant(other.value.value)
                && value.unit.equalsToConstant(other.value.unit)
        }

        is DivSize.MatchParent -> {
            other is DivSize.MatchParent
                && value.weight.equalsToConstant(other.value.weight)
        }

        is DivSize.WrapContent -> {
            other is DivSize.WrapContent
                && value.constrained.equalsToConstant(other.value.constrained)
                && value.minSize?.value.equalsToConstant(other.value.minSize?.value)
                && value.minSize?.unit.equalsToConstant(other.value.minSize?.unit)
                && value.maxSize?.value.equalsToConstant(other.value.maxSize?.value)
                && value.maxSize?.unit.equalsToConstant(other.value.maxSize?.unit)
        }
    }
}

internal fun DivSize?.isConstant(): Boolean {
    return when (this) {
        null -> true

        is DivSize.Fixed -> value.value.isConstant() && value.unit.isConstant()

        is DivSize.MatchParent -> value.weight.isConstantOrNull()

        is DivSize.WrapContent -> {
            value.constrained.isConstantOrNull()
                && value.minSize?.value.isConstantOrNull()
                && value.minSize?.unit.isConstantOrNull()
                && value.maxSize?.value.isConstantOrNull()
                && value.maxSize?.unit.isConstantOrNull()
        }
    }
}

internal fun DivFixedSize?.equalsToConstant(other: DivFixedSize?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.value.equalsToConstant(other?.value)
        && this?.unit.equalsToConstant(other?.unit)
}

internal fun DivFixedSize?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return value.isConstant()
        && unit.isConstant()
}

internal fun DivEdgeInsets?.equalsToConstant(other: DivEdgeInsets?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.left.equalsToConstant(other?.left)
        && this?.top.equalsToConstant(other?.top)
        && this?.right.equalsToConstant(other?.right)
        && this?.bottom.equalsToConstant(other?.bottom)
        && this?.start.equalsToConstant(other?.start)
        && this?.end.equalsToConstant(other?.end)
}

internal fun DivEdgeInsets?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return left.isConstant()
        && top.isConstant()
        && right.isConstant()
        && bottom.isConstant()
        && start.isConstantOrNull()
        && end.isConstantOrNull()
}

internal fun DivAbsoluteEdgeInsets?.equalsToConstant(other: DivAbsoluteEdgeInsets?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.left.equalsToConstant(other?.left)
        && this?.top.equalsToConstant(other?.top)
        && this?.right.equalsToConstant(other?.right)
        && this?.bottom.equalsToConstant(other?.bottom)
}

internal fun DivAbsoluteEdgeInsets?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return left.isConstant()
        && top.isConstant()
        && right.isConstant()
        && bottom.isConstant()
}

internal fun DivTransform?.equalsToConstant(other: DivTransform?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.rotation.equalsToConstant(other?.rotation)
        && this?.pivotX.equalsToConstant(other?.pivotX)
        && this?.pivotY.equalsToConstant(other?.pivotY)
}

internal fun DivTransform?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return rotation.isConstantOrNull()
        && pivotX.isConstant()
        && pivotY.isConstant()
}

internal fun DivPivot?.equalsToConstant(other: DivPivot?): Boolean {
    return when (this) {
        null -> other == null

        is DivPivot.Fixed ->  {
            other is DivPivot.Fixed
                && value.value.equalsToConstant(other.value.value)
                && value.unit.equalsToConstant(other.value.unit)
        }

        is DivPivot.Percentage -> {
            other is DivPivot.Percentage
                && value.value.equalsToConstant(other.value.value)
        }
    }
}

internal fun DivPivot?.isConstant(): Boolean {
    return when (this) {
        null -> true
        is DivPivot.Fixed -> value.value.isConstantOrNull() && value.value.isConstantOrNull()
        is DivPivot.Percentage -> value.value.isConstant()
    }
}

internal fun DivFilter?.equalsToConstant(other: DivFilter?): Boolean {
    return when (this) {
        null -> other == null
        is DivFilter.RtlMirror -> other is DivFilter.RtlMirror
        is DivFilter.Blur -> other is DivFilter.Blur && value.radius.equalsToConstant(other.value.radius)
    }
}

internal fun DivFilter?.isConstant(): Boolean {
    return when (this) {
        null -> true
        is DivFilter.RtlMirror -> true
        is DivFilter.Blur -> value.radius.isConstant()
    }
}

internal fun DivDrawable?.equalsToConstant(other: DivDrawable?): Boolean {
    return when (this) {
        null -> other == null

        is DivDrawable.Shape -> {
            other is DivDrawable.Shape
                && value.color.equalsToConstant(other.value.color)
                && value.shape.equalsToConstant(other.value.shape)
                && value.stroke.equalsToConstant(other.value.stroke)
        }
    }
}

internal fun DivDrawable?.isConstant(): Boolean {
    return when (this) {
        null -> true

        is DivDrawable.Shape -> {
            value.color.isConstant()
                && value.shape.isConstant()
                && value.stroke.isConstant()
        }
    }
}

internal fun DivShape?.equalsToConstant(other: DivShape?): Boolean {
    return when (this) {
        null -> other == null

        is DivShape.RoundedRectangle -> {
            other is DivShape.RoundedRectangle
                && value.backgroundColor.equalsToConstant(other.value.backgroundColor)
                && value.stroke.equalsToConstant(other.value.stroke)
                && value.itemWidth.equalsToConstant(other.value.itemWidth)
                && value.itemHeight.equalsToConstant(other.value.itemHeight)
                && value.cornerRadius.equalsToConstant(other.value.cornerRadius)
        }

        is DivShape.Circle -> {
            other is DivShape.Circle
                && value.backgroundColor.equalsToConstant(other.value.backgroundColor)
                && value.stroke.equalsToConstant(other.value.stroke)
                && value.radius.equalsToConstant(other.value.radius)
        }
    }
}

internal fun DivShape?.isConstant(): Boolean {
    return when (this) {
        null -> true

        is DivShape.RoundedRectangle -> {
            value.backgroundColor.isConstantOrNull()
                && value.stroke.isConstant()
                && value.itemWidth.isConstant()
                && value.itemHeight.isConstant()
                && value.cornerRadius.isConstant()
        }

        is DivShape.Circle -> {
            value.backgroundColor.isConstantOrNull()
                && value.stroke?.isConstant() != false
                && value.radius.isConstant()
        }
    }
}

internal fun DivStroke?.equalsToConstant(other: DivStroke?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.color.equalsToConstant(other?.color)
        && this?.width.equalsToConstant(other?.width)
        && this?.unit.equalsToConstant(other?.unit)
}

internal fun DivStroke?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return color.isConstant()
        && width.isConstant()
        && unit.isConstant()
}

internal fun DivBorder?.equalsToConstant(other: DivBorder?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.cornerRadius.equalsToConstant(other?.cornerRadius)
        && this?.cornersRadius.equalsToConstant(other?.cornersRadius)
        && this?.hasShadow.equalsToConstant(other?.hasShadow)
        && this?.shadow.equalsToConstant(other?.shadow)
        && this?.stroke.equalsToConstant(other?.stroke)
}

internal fun DivBorder?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return cornerRadius.isConstantOrNull()
        && cornersRadius.isConstant()
        && hasShadow.isConstant()
        && shadow.isConstant()
        && stroke.isConstant()
}

internal fun DivCornersRadius?.equalsToConstant(other: DivCornersRadius?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.topLeft.equalsToConstant(other?.topLeft)
        && this?.topRight.equalsToConstant(other?.topRight)
        && this?.bottomRight.equalsToConstant(other?.bottomRight)
        && this?.bottomLeft.equalsToConstant(other?.bottomLeft)
}

internal fun DivCornersRadius?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return topLeft.isConstantOrNull()
        && topRight.isConstantOrNull()
        && bottomRight.isConstantOrNull()
        && bottomLeft.isConstantOrNull()
}

internal fun DivShadow?.equalsToConstant(other: DivShadow?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.alpha.equalsToConstant(other?.alpha)
        && this?.blur.equalsToConstant(other?.blur)
        && this?.color.equalsToConstant(other?.color)
        && this?.offset.equalsToConstant(other?.offset)
}

internal fun DivShadow?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return alpha.isConstant()
        && blur.isConstant()
        && color.isConstant()
        && offset.isConstant()
}

internal fun DivPoint?.equalsToConstant(other: DivPoint?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.x.equalsToConstant(other?.x)
        && this?.y.equalsToConstant(other?.y)
}

internal fun DivPoint?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return x.isConstant()
        && y.isConstant()
}

internal fun DivDimension?.equalsToConstant(other: DivDimension?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.unit.equalsToConstant(other?.unit)
        && this?.value.equalsToConstant(other?.value)
}

internal fun DivDimension?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return unit.isConstant()
        && value.isConstant()
}

internal fun DivBackground?.equalsToConstant(other: DivBackground?): Boolean {
    return when (this) {
        null -> other == null

        is DivBackground.Solid -> {
            other is DivBackground.Solid
                && value.color.equalsToConstant(other.value.color)
        }

        is DivBackground.Image -> {
            val filters = value.filters ?: emptyList()
            other is DivBackground.Image
                && value.alpha.equalsToConstant(other.value.alpha)
                && value.contentAlignmentHorizontal.equalsToConstant(other.value.contentAlignmentHorizontal)
                && value.contentAlignmentVertical.equalsToConstant(other.value.contentAlignmentVertical)
                && filters.compareWith(other.value.filters ?: emptyList()) { left, right -> left.equalsToConstant(right) }
                && value.imageUrl.equalsToConstant(other.value.imageUrl)
                && value.preloadRequired.equalsToConstant(other.value.preloadRequired)
                && value.scale.equalsToConstant(other.value.scale)
        }

        is DivBackground.LinearGradient -> {
            val colormap = value.colorMap ?: emptyList()
            other is DivBackground.LinearGradient
                && value.angle.equalsToConstant(other.value.angle)
                && value.colors.equalsToConstant(other.value.colors)
                && colormap.compareWith(other.value.colorMap ?: emptyList()) { left, right -> left.equalsToConstant(right) }
        }

        is DivBackground.RadialGradient -> {
            other is DivBackground.RadialGradient
                && value.centerX.equalsToConstant(other.value.centerX)
                && value.centerY.equalsToConstant(other.value.centerY)
                && value.colors.equalsToConstant(other.value.colors)
                && value.radius.equalsToConstant(other.value.radius)
        }

        is DivBackground.NinePatch -> {
            other is DivBackground.NinePatch
                && value.imageUrl.equalsToConstant(other.value.imageUrl)
                && value.insets.equalsToConstant(other.value.insets)
        }
    }
}

internal fun DivBackground?.isConstant(): Boolean {
    return when (this) {
        null -> true

        is DivBackground.Solid -> {
            value.color.isConstant()
        }

        is DivBackground.Image -> {
            value.alpha.isConstant()
                && value.contentAlignmentHorizontal.isConstant()
                && value.contentAlignmentVertical.isConstant()
                && value.filters?.all { it.isConstant() } ?: true
                && value.imageUrl.isConstant()
                && value.preloadRequired.isConstant()
                && value.scale.isConstant()
        }

        is DivBackground.LinearGradient -> {
            value.angle.isConstant()
                && value.colors.isConstantOrNull()
        }

        is DivBackground.RadialGradient -> {
            value.centerX.isConstant()
                && value.centerY.isConstant()
                && value.colors.isConstantOrNull()
                && value.radius.isConstant()
        }

        is DivBackground.NinePatch -> {
            value.imageUrl.isConstant()
                && value.insets.isConstant()
        }
    }
}

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

    return color.isConstant()
        && position.isConstant()
}


internal fun DivLinearGradient.toColormap(resolver: ExpressionResolver): Colormap {
    val mappedColors = this.colorMap?.sortedBy { colorPoint -> colorPoint.position.evaluate(resolver) }
    val uniformColors = this.colors

    if (mappedColors != null) {
        val colors = IntArray(mappedColors.size)
        val positions = FloatArray(mappedColors.size)
        for (i in mappedColors.indices) {
            colors[i] = mappedColors[i].color.evaluate(resolver)
            positions[i] = mappedColors[i].position.evaluate(resolver).toFloat()
        }
        return Colormap(colors, positions)
    } else if (uniformColors != null) {
        return Colormap(uniformColors.evaluate(resolver).toIntArray())
    } else {
        return Colormap.EMPTY
    }
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

        is DivRadialGradientCenter.Fixed -> {
            value.unit.isConstant()
                && value.value.isConstant()
        }

        is DivRadialGradientCenter.Relative -> {
            value.value.isConstant()
        }
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

        is DivRadialGradientRadius.FixedSize -> {
            value.unit.isConstant()
                && value.value.isConstant()
        }

        is DivRadialGradientRadius.Relative -> {
            value.value.isConstant()
        }
    }
}

internal fun DivInput.NativeInterface?.equalsToConstant(other: DivInput.NativeInterface?): Boolean {
    if (this == null && other == null) {
        return true
    }

    return this?.color.equalsToConstant(other?.color)
}

internal fun DivInput.NativeInterface?.isConstant(): Boolean {
    if (this == null) {
        return true
    }

    return color.isConstant()
}

fun DivData.applyPatch(patch: DivPatch): DivData? {
    val patchMap = DivPatchMap(patch)
    val states = DivPatchApply(patchMap).applyPatch(states, ExpressionResolver.EMPTY) ?: return null
    return DivData(
        logId = logId,
        states = states,
        timers = timers,
        transitionAnimationSelector = transitionAnimationSelector,
        variableTriggers = variableTriggers,
        variables = variables,
        parsingErrors = parsingErrors,
    )
}
