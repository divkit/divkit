package com.yandex.div.core.util

import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFilter
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivPivot
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
