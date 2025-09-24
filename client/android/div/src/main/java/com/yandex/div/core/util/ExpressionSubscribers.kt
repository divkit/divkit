package com.yandex.div.core.util

import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAbsoluteEdgeInsets
import com.yandex.div2.DivBackground
import com.yandex.div2.DivCircleShape
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFilter
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivPivot
import com.yandex.div2.DivRadialGradient
import com.yandex.div2.DivRadialGradientCenter
import com.yandex.div2.DivRadialGradientRadius
import com.yandex.div2.DivRoundedRectangleShape
import com.yandex.div2.DivShape
import com.yandex.div2.DivSize
import com.yandex.div2.DivStroke
import com.yandex.div2.DivTransform

internal fun ExpressionSubscriber.observeSize(
    size: DivSize?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    when (size) {
        null -> Unit

        is DivSize.Fixed -> {
            val fixedSize = size.value
            addSubscription(fixedSize.value.observe(resolver, callback))
            addSubscription(fixedSize.unit.observe(resolver, callback))
        }

        is DivSize.MatchParent -> {
            val matchParentSize = size.value
            addSubscription(matchParentSize.weight?.observe(resolver, callback))
            addSubscription(matchParentSize.minSize?.value?.observe(resolver, callback))
            addSubscription(matchParentSize.minSize?.unit?.observe(resolver, callback))
            addSubscription(matchParentSize.maxSize?.value?.observe(resolver, callback))
            addSubscription(matchParentSize.maxSize?.unit?.observe(resolver, callback))
        }

        is DivSize.WrapContent -> {
            val wrapContentSize = size.value
            addSubscription(wrapContentSize.constrained?.observe(resolver, callback))
            addSubscription(wrapContentSize.minSize?.value?.observe(resolver, callback))
            addSubscription(wrapContentSize.minSize?.unit?.observe(resolver, callback))
            addSubscription(wrapContentSize.maxSize?.value?.observe(resolver, callback))
            addSubscription(wrapContentSize.maxSize?.unit?.observe(resolver, callback))
        }
    }
}

internal fun ExpressionSubscriber.observeFixedSize(
    fixedSize: DivFixedSize?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    if (fixedSize == null) {
        return
    }

    addSubscription(fixedSize.value.observe(resolver, callback))
    addSubscription(fixedSize.unit.observe(resolver, callback))
}

internal fun ExpressionSubscriber.observeEdgeInsets(
    insets: DivEdgeInsets?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    if (insets == null) {
        return
    }

    addSubscription(insets.top.observe(resolver, callback))
    addSubscription(insets.bottom.observe(resolver, callback))
    if (insets.start != null || insets.end != null) {
        addSubscription(insets.start?.observe(resolver, callback))
        addSubscription(insets.end?.observe(resolver, callback))
    } else {
        addSubscription(insets.left.observe(resolver, callback))
        addSubscription(insets.right.observe(resolver, callback))
    }
}

internal fun ExpressionSubscriber.observeAbsoluteEdgeInsets(
    insets: DivAbsoluteEdgeInsets?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    if (insets == null) {
        return
    }

    addSubscription(insets.left.observe(resolver, callback))
    addSubscription(insets.top.observe(resolver, callback))
    addSubscription(insets.right.observe(resolver, callback))
    addSubscription(insets.bottom.observe(resolver, callback))
}

internal fun ExpressionSubscriber.observeTransform(
    transform: DivTransform?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    if (transform == null) {
        return
    }

    addSubscription(transform.rotation?.observe(resolver, callback))
    observePivot(transform.pivotX, resolver, callback)
    observePivot(transform.pivotY, resolver, callback)
}

internal fun ExpressionSubscriber.observePivot(
    pivot: DivPivot?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    when (pivot) {
        null -> Unit

        is DivPivot.Fixed -> {
            val fixedPivot = pivot.value
            addSubscription(fixedPivot.value?.observe(resolver, callback))
            addSubscription(fixedPivot.unit.observe(resolver, callback))
        }

        is DivPivot.Percentage -> {
            val percentagePivot = pivot.value
            addSubscription(percentagePivot.value.observe(resolver, callback))
        }
    }
}

internal fun ExpressionSubscriber.observeFilter(
    filter: DivFilter?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    when (filter) {
        null -> Unit
        is DivFilter.RtlMirror -> Unit
        is DivFilter.Blur -> addSubscription(filter.value.radius.observe(resolver, callback))
    }
}

internal fun ExpressionSubscriber.observeDrawable(
    drawable: DivDrawable?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    when (drawable) {
        null -> Unit

        is DivDrawable.Shape -> {
            val shapeDrawable = drawable.value
            addSubscription(shapeDrawable.color.observe(resolver, callback))
            observeShape(shapeDrawable.shape, resolver, callback)
            observeStroke(shapeDrawable.stroke, resolver, callback)
        }
    }
}

internal fun ExpressionSubscriber.observeShape(
    shape: DivShape?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    when (shape) {
        null -> Unit
        is DivShape.RoundedRectangle -> observeRoundedRectangleShape(shape.value, resolver, callback)
        is DivShape.Circle -> observeCircleShape(shape.value, resolver, callback)
    }
}

internal fun ExpressionSubscriber.observeRoundedRectangleShape(
    roundedRectangle: DivRoundedRectangleShape?,
    resolver: ExpressionResolver, callback: (Any) -> Unit
) {
    if (roundedRectangle == null) {
        return
    }

    addSubscription(roundedRectangle.backgroundColor?.observe(resolver, callback))
    observeFixedSize(roundedRectangle.cornerRadius, resolver, callback)
    observeFixedSize(roundedRectangle.itemWidth, resolver, callback)
    observeFixedSize(roundedRectangle.itemHeight, resolver, callback)
    observeStroke(roundedRectangle.stroke, resolver, callback)
}

internal fun ExpressionSubscriber.observeCircleShape(
    circle: DivCircleShape?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    if (circle == null) {
        return
    }

    addSubscription(circle.backgroundColor?.observe(resolver, callback))
    observeFixedSize(circle.radius, resolver, callback)
    observeStroke(circle.stroke, resolver, callback)
}

internal fun ExpressionSubscriber.observeStroke (
    stroke: DivStroke?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    if (stroke == null) {
        return
    }

    addSubscription(stroke.color.observe(resolver, callback))
    addSubscription(stroke.width.observe(resolver, callback))
    addSubscription(stroke.unit.observe(resolver, callback))
}

internal fun ExpressionSubscriber.observeBackground (
    background: DivBackground?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    when (background) {
        null -> Unit

        is DivBackground.Solid -> {
            val solidBackground = background.value
            addSubscription(solidBackground.color.observe(resolver, callback))
        }

        is DivBackground.Image -> {
            val imageBackground = background.value
            addSubscription(imageBackground.alpha.observe(resolver, callback))
            addSubscription(imageBackground.imageUrl.observe(resolver, callback))
            addSubscription(imageBackground.contentAlignmentHorizontal.observe(resolver, callback))
            addSubscription(imageBackground.contentAlignmentVertical.observe(resolver, callback))
            addSubscription(imageBackground.preloadRequired.observe(resolver, callback))
            addSubscription(imageBackground.scale.observe(resolver, callback))
            imageBackground.filters?.forEach { filter -> observeFilter(filter, resolver, callback) }
        }

        is DivBackground.LinearGradient -> {
            val linearGradientBackground = background.value
            addSubscription(linearGradientBackground.angle.observe(resolver, callback))
            addSubscription(linearGradientBackground.colors?.observe(resolver, callback))
            linearGradientBackground.colorMap?.forEach { colorPoint -> observeColorPoint(colorPoint, resolver, callback) }
        }

        is DivBackground.RadialGradient -> {
            val radialGradientBackground = background.value
            addSubscription(radialGradientBackground.colors?.observe(resolver, callback))
            observeRadialGradientCenter(radialGradientBackground.centerX, resolver, callback)
            observeRadialGradientCenter(radialGradientBackground.centerY, resolver, callback)
            observeRadialGradientRadius(radialGradientBackground.radius, resolver, callback)
            background.value.colorMap?.forEach { observeColorPoint(it, resolver, callback) }
        }

        is DivBackground.NinePatch -> {
            val ninePatchBackground = background.value
            addSubscription(ninePatchBackground.imageUrl.observe(resolver, callback))
            observeAbsoluteEdgeInsets(ninePatchBackground.insets, resolver, callback)
        }
    }
}

internal fun ExpressionSubscriber.observeColorPoint(
    colorPoint: DivLinearGradient.ColorPoint?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    if (colorPoint == null) {
        return
    }

    addSubscription(colorPoint.color.observe(resolver, callback))
    addSubscription(colorPoint.position.observe(resolver, callback))
}

internal fun ExpressionSubscriber.observeColorPoint(
    colorPoint: DivRadialGradient.ColorPoint?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    if (colorPoint == null) {
        return
    }

    addSubscription(colorPoint.color.observe(resolver, callback))
    addSubscription(colorPoint.position.observe(resolver, callback))
}

internal fun ExpressionSubscriber.observeRadialGradientCenter (
    center: DivRadialGradientCenter?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    when (center) {
        null -> Unit

        is DivRadialGradientCenter.Fixed -> {
            addSubscription(center.value.unit.observe(resolver, callback))
            addSubscription(center.value.value.observe(resolver, callback))
        }

        is DivRadialGradientCenter.Relative -> {
            addSubscription(center.value.value.observe(resolver, callback))
        }
    }
}

internal fun ExpressionSubscriber.observeRadialGradientRadius (
    radius: DivRadialGradientRadius?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    when (radius) {
        null -> Unit

        is DivRadialGradientRadius.FixedSize -> {
            addSubscription(radius.value.unit.observe(resolver, callback))
            addSubscription(radius.value.value.observe(resolver, callback))
        }

        is DivRadialGradientRadius.Relative -> {
            addSubscription(radius.value.value.observe(resolver, callback))
        }
    }
}
