package com.yandex.div.core.util

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.yandex.div.core.animation.EaseInInterpolator
import com.yandex.div.core.animation.EaseInOutInterpolator
import com.yandex.div.core.animation.EaseInterpolator
import com.yandex.div.core.animation.EaseOutInterpolator
import com.yandex.div.core.animation.SpringInterpolator
import com.yandex.div.core.animation.reversed
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.core.view2.divs.toPxF
import com.yandex.div.core.view2.divs.unitToPxF
import com.yandex.div.core.widget.AspectView
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.internal.drawable.CircleDrawable
import com.yandex.div.internal.drawable.RoundedRectDrawable
import com.yandex.div.internal.widget.AspectImageView
import com.yandex.div.internal.widget.DivGravity
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivAnimationDirection
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivBase
import com.yandex.div2.DivBlendMode
import com.yandex.div2.DivBorder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical
import com.yandex.div2.DivCustom
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivImageScale
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivInput
import com.yandex.div2.DivPager
import com.yandex.div2.DivSelect
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivShape
import com.yandex.div2.DivShapeDrawable
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivSize
import com.yandex.div2.DivSlider
import com.yandex.div2.DivState
import com.yandex.div2.DivStroke
import com.yandex.div2.DivSwitch
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVisibilityAction
import java.util.Collections.min

internal val Div.type: String
    get() {
        return when (this) {
            is Div.Text -> DivText.TYPE
            is Div.Image -> DivImage.TYPE
            is Div.GifImage -> DivGifImage.TYPE
            is Div.Separator -> DivSeparator.TYPE
            is Div.Indicator -> DivIndicator.TYPE
            is Div.Slider -> DivSlider.TYPE
            is Div.Input -> DivInput.TYPE
            is Div.Video -> DivVideo.TYPE
            is Div.Container -> DivContainer.TYPE
            is Div.Grid -> DivGrid.TYPE
            is Div.State -> DivState.TYPE
            is Div.Gallery -> DivGallery.TYPE
            is Div.Pager -> DivPager.TYPE
            is Div.Tabs -> DivTabs.TYPE
            is Div.Custom -> DivCustom.TYPE
            is Div.Select -> DivSelect.TYPE
            is Div.Switch -> DivSwitch.TYPE
        }
    }

internal fun Div.canBeReused(other: Div, resolver: ExpressionResolver): Boolean {
    if (this.type != other.type) {
        return false
    }

    val div = this.value()
    val otherDiv = other.value()

    if (div is DivImage && otherDiv is DivImage) {
        return div.imageUrl.evaluate(resolver) == otherDiv.imageUrl.evaluate(resolver)
    }
    return div.background === otherDiv.background
}

internal val DivAnimationInterpolator.androidInterpolator: Interpolator
    get() = when (this) {
        DivAnimationInterpolator.LINEAR -> LinearInterpolator()
        DivAnimationInterpolator.EASE -> EaseInterpolator()
        DivAnimationInterpolator.EASE_IN -> EaseInInterpolator()
        DivAnimationInterpolator.EASE_OUT -> EaseOutInterpolator()
        DivAnimationInterpolator.EASE_IN_OUT -> EaseInOutInterpolator()
        DivAnimationInterpolator.SPRING -> SpringInterpolator()
    }

internal fun DivAnimationInterpolator.androidInterpolator(reverse: Boolean): Interpolator {
    return if (reverse) {
        androidInterpolator.reversed()
    } else {
        androidInterpolator
    }
}

internal val DivAnimationDirection.isReversed: Boolean
    get() {
        return when (this) {
            DivAnimationDirection.REVERSE, DivAnimationDirection.ALTERNATE_REVERSE -> true
            else -> false
        }
    }

internal val DivAnimationDirection.isAlternated: Boolean
    get() {
        return when (this) {
            DivAnimationDirection.ALTERNATE, DivAnimationDirection.ALTERNATE_REVERSE -> true
            else -> false
        }
    }

internal fun DivBorder.getCornerRadii(
    widthPx: Float,
    heightPx: Float,
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): FloatArray {

    var topLeft = (cornersRadius?.topLeft ?: cornerRadius)?.evaluate(resolver).dpToPx(metrics).toFloat()
    var topRight = (cornersRadius?.topRight ?: cornerRadius)?.evaluate(resolver).dpToPx(metrics).toFloat()
    var bottomLeft = (cornersRadius?.bottomLeft ?: cornerRadius)?.evaluate(resolver).dpToPx(metrics).toFloat()
    var bottomRight = (cornersRadius?.bottomRight ?: cornerRadius)?.evaluate(resolver).dpToPx(metrics).toFloat()

    val top = topLeft + topRight
    val bottom = bottomLeft + bottomRight
    val left = topLeft + bottomLeft
    val right = topRight + bottomRight

    val f = min(listOf(widthPx / top, widthPx / bottom, heightPx / left, heightPx / right))

    if (f > 0 && f < 1) {
        topLeft *= f
        topRight *= f
        bottomLeft *= f
        bottomRight *= f
    }

    return floatArrayOf(
        topLeft, topLeft,
        topRight, topRight,
        bottomRight, bottomRight,
        bottomLeft, bottomLeft
    )
}

internal fun Div.containsStateInnerTransitions(resolver: ExpressionResolver): Boolean {
    with(value()) {
        if (transitionIn != null || transitionChange != null || transitionOut != null) {
            return true
        }
    }
    return when (this) {
        is Div.Container -> value.buildItems(resolver)
            .any { it.div.containsStateInnerTransitions(it.expressionResolver) }
        is Div.Grid -> value.nonNullItems.any { it.containsStateInnerTransitions(resolver) }
        is Div.Text -> false
        is Div.Image -> false
        is Div.GifImage -> false
        is Div.Separator -> false
        is Div.Indicator -> false
        is Div.State -> false  // state binder should resolve by itself which animation description to use.
        is Div.Gallery -> false
        is Div.Pager -> false
        is Div.Tabs -> false
        is Div.Custom -> false
        is Div.Select -> false
        is Div.Slider -> false
        is Div.Video -> false
        is Div.Input -> false
        is Div.Switch -> false
    }
}

internal fun DivState.getDefaultState(resolver: ExpressionResolver): DivState.State? {
    return defaultStateId?.let { defaultStateId ->
        states.find { it.stateId == defaultStateId.evaluate(resolver) }
    } ?: states.firstOrNull()
}

internal val Div.isBranch: Boolean
    get() = when (this) {
        is Div.Text -> false
        is Div.Image -> false
        is Div.GifImage -> false
        is Div.Separator -> false
        is Div.Indicator -> false
        is Div.Slider -> false
        is Div.Input -> false
        is Div.Custom -> false
        is Div.Select -> false
        is Div.Video -> false
        is Div.Switch -> false
        is Div.Container -> true
        is Div.Grid -> true
        is Div.Gallery -> true
        is Div.Pager -> true
        is Div.Tabs -> true
        is Div.State -> true
    }

internal val Div.isLeaf: Boolean
    get() = !isBranch

internal fun DivContainer.isHorizontal(resolver: ExpressionResolver) =
    orientation.evaluate(resolver) == DivContainer.Orientation.HORIZONTAL

internal fun DivContainer.isWrapContainer(resolver: ExpressionResolver) = when {
    layoutMode.evaluate(resolver) != DivContainer.LayoutMode.WRAP -> false
    orientation.evaluate(resolver) == DivContainer.Orientation.OVERLAP -> false
    isHorizontal(resolver) -> width.canWrap(resolver)
    height.canWrap(resolver) -> true
    else -> aspect?.let { it.ratio.evaluate(resolver).toFloat() != AspectView.DEFAULT_ASPECT_RATIO } ?: false
}

private fun DivSize.canWrap(resolver: ExpressionResolver) =
    this !is DivSize.WrapContent || value.constrained?.evaluate(resolver) == true

internal val DivBase.hasSightActions: Boolean
    get() = visibilityAction != null || !visibilityActions.isNullOrEmpty() || !disappearActions.isNullOrEmpty()

internal val DivBase.allAppearActions: List<DivVisibilityAction>
    get() = visibilityActions ?: visibilityAction?.let { listOf(it) }.orEmpty()

internal val DivBase.allDisappearActions: List<DivDisappearAction>
    get() = disappearActions.orEmpty()

internal val DivBase.allSightActions: List<DivSightAction>
    get() = this.allDisappearActions + this.allAppearActions

internal fun DivSize?.toLayoutParamsSize(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver,
    lp: ViewGroup.LayoutParams? = null
): Int {
    return when (this) {
        null -> ViewGroup.LayoutParams.WRAP_CONTENT
        is DivSize.MatchParent -> ViewGroup.LayoutParams.MATCH_PARENT
        is DivSize.Fixed -> value.toPx(metrics, resolver)
        is DivSize.WrapContent -> when {
            value.constrained?.evaluate(resolver) != true -> ViewGroup.LayoutParams.WRAP_CONTENT
            lp is DivLayoutParams -> DivLayoutParams.WRAP_CONTENT_CONSTRAINED
            else -> ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }
}

internal fun evaluateGravity(horizontal: DivAlignmentHorizontal?, vertical: DivAlignmentVertical?): Int {
    val horizontalGravity = when (horizontal) {
        DivAlignmentHorizontal.LEFT -> Gravity.LEFT
        DivAlignmentHorizontal.CENTER -> Gravity.CENTER_HORIZONTAL
        DivAlignmentHorizontal.RIGHT -> Gravity.RIGHT
        DivAlignmentHorizontal.START -> Gravity.START
        DivAlignmentHorizontal.END -> Gravity.END
        else -> Gravity.START
    }

    val verticalGravity = when (vertical) {
        DivAlignmentVertical.TOP -> Gravity.TOP
        DivAlignmentVertical.CENTER -> Gravity.CENTER_VERTICAL
        DivAlignmentVertical.BOTTOM -> Gravity.BOTTOM
        else -> Gravity.TOP
    }

    return horizontalGravity or verticalGravity
}

internal fun evaluateGravity(horizontal: DivContentAlignmentHorizontal?, vertical: DivContentAlignmentVertical?): Int {
    val horizontalGravity = when (horizontal) {
        DivContentAlignmentHorizontal.LEFT -> Gravity.LEFT
        DivContentAlignmentHorizontal.CENTER -> Gravity.CENTER_HORIZONTAL
        DivContentAlignmentHorizontal.RIGHT -> Gravity.RIGHT
        DivContentAlignmentHorizontal.START -> Gravity.START
        DivContentAlignmentHorizontal.END -> Gravity.END
        DivContentAlignmentHorizontal.SPACE_AROUND -> DivGravity.SPACE_AROUND_HORIZONTAL
        DivContentAlignmentHorizontal.SPACE_BETWEEN -> DivGravity.SPACE_BETWEEN_HORIZONTAL
        DivContentAlignmentHorizontal.SPACE_EVENLY -> DivGravity.SPACE_EVENLY_HORIZONTAL
        else -> Gravity.START
    }

    val verticalGravity = when (vertical) {
        DivContentAlignmentVertical.TOP -> Gravity.TOP
        DivContentAlignmentVertical.CENTER -> Gravity.CENTER_VERTICAL
        DivContentAlignmentVertical.BOTTOM -> Gravity.BOTTOM
        DivContentAlignmentVertical.SPACE_AROUND -> DivGravity.SPACE_AROUND_VERTICAL
        DivContentAlignmentVertical.SPACE_BETWEEN -> DivGravity.SPACE_BETWEEN_VERTICAL
        DivContentAlignmentVertical.SPACE_EVENLY -> DivGravity.SPACE_EVENLY_VERTICAL
        else -> Gravity.TOP
    }

    return horizontalGravity or verticalGravity
}

internal fun DivBlendMode.toPorterDuffMode(): PorterDuff.Mode {
    return when (this) {
        DivBlendMode.SOURCE_IN -> PorterDuff.Mode.SRC_IN
        DivBlendMode.SOURCE_ATOP -> PorterDuff.Mode.SRC_ATOP
        DivBlendMode.DARKEN -> PorterDuff.Mode.DARKEN
        DivBlendMode.LIGHTEN -> PorterDuff.Mode.LIGHTEN
        DivBlendMode.MULTIPLY -> PorterDuff.Mode.MULTIPLY
        DivBlendMode.SCREEN -> PorterDuff.Mode.SCREEN
    }
}

internal fun DivImageScale.toImageScale(): AspectImageView.Scale {
    return when(this) {
        DivImageScale.NO_SCALE -> AspectImageView.Scale.NO_SCALE
        DivImageScale.FIT -> AspectImageView.Scale.FIT
        DivImageScale.FILL -> AspectImageView.Scale.FILL
        DivImageScale.STRETCH -> AspectImageView.Scale.STRETCH
    }
}

internal fun DivDrawable.toDrawable(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): Drawable? {
    return when (this) {
        is DivDrawable.Shape -> value.toDrawable(metrics, resolver)
    }
}

internal fun DivShapeDrawable.toDrawable(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): Drawable? {
    return when (val shape = this.shape) {
        is DivShape.RoundedRectangle -> {
            RoundedRectDrawable(
                RoundedRectDrawable.Params(
                    width = shape.value.itemWidth.toPxF(metrics, resolver),
                    height = shape.value.itemHeight.toPxF(metrics, resolver),
                    color = (shape.value.backgroundColor ?: color).evaluate(resolver),
                    radius = shape.value.cornerRadius.toPxF(metrics, resolver),
                    strokeColor = (shape.value.stroke ?: stroke)?.color?.evaluate(resolver),
                    strokeWidth = (shape.value.stroke ?: stroke)?.getWidthPxF(metrics, resolver)
                )
            )
        }
        is DivShape.Circle ->
            CircleDrawable(
                CircleDrawable.Params(
                    radius = shape.value.radius.toPxF(metrics, resolver),
                    color = (shape.value.backgroundColor ?: color).evaluate(resolver),
                    strokeColor = (shape.value.stroke ?: stroke)?.color?.evaluate(resolver),
                    strokeWidth = (shape.value.stroke ?: stroke)?.getWidthPxF(metrics, resolver)
                )
            )
        else -> null
    }
}

private fun DivStroke.getWidthPxF(metrics: DisplayMetrics, resolver: ExpressionResolver) =
    width.evaluate(resolver).unitToPxF(metrics, unit.evaluate(resolver))

internal fun DivContentAlignmentHorizontal.toAlignmentHorizontal(): DivAlignmentHorizontal = when (this) {
    DivContentAlignmentHorizontal.LEFT -> DivAlignmentHorizontal.LEFT
    DivContentAlignmentHorizontal.CENTER -> DivAlignmentHorizontal.CENTER
    DivContentAlignmentHorizontal.RIGHT -> DivAlignmentHorizontal.RIGHT
    DivContentAlignmentHorizontal.START -> DivAlignmentHorizontal.START
    DivContentAlignmentHorizontal.END -> DivAlignmentHorizontal.END
    else -> DivAlignmentHorizontal.START
}

internal fun DivContentAlignmentVertical.toAlignmentVertical(): DivAlignmentVertical = when (this) {
    DivContentAlignmentVertical.TOP -> DivAlignmentVertical.TOP
    DivContentAlignmentVertical.CENTER -> DivAlignmentVertical.CENTER
    DivContentAlignmentVertical.BOTTOM -> DivAlignmentVertical.BOTTOM
    DivContentAlignmentVertical.BASELINE -> DivAlignmentVertical.BASELINE
    else -> DivAlignmentVertical.TOP
}
