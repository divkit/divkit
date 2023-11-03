package com.yandex.div.core.util

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.core.view.children
import com.yandex.div.core.animation.EaseInInterpolator
import com.yandex.div.core.animation.EaseInOutInterpolator
import com.yandex.div.core.animation.EaseInterpolator
import com.yandex.div.core.animation.EaseOutInterpolator
import com.yandex.div.core.animation.SpringInterpolator
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.internal.core.buildItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivBorder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivInput
import com.yandex.div2.DivPager
import com.yandex.div2.DivSelect
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSlider
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import com.yandex.div2.DivVideo
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
    if (div.background?.equals(otherDiv.background) != true) {
        return false
    }
    return true
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

internal fun requestHierarchyLayout(v : View) {
    v.requestLayout()
    if (v is ViewGroup) {
        v.children.forEach { requestHierarchyLayout(it) }
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
        is Div.Container -> value.buildItems(resolver).map { it.containsStateInnerTransitions(resolver) }.contains(true)
        is Div.Grid -> value.items.map { it.containsStateInnerTransitions(resolver) }.contains(true)
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
    }
}

internal fun DivState.getDefaultState(resolver: ExpressionResolver): DivState.State {
        return defaultStateId?.let { defaultStateId ->
            states.find { it.stateId == defaultStateId.evaluate(resolver) }
        } ?: states[0]
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
        is Div.Container -> true
        is Div.Grid -> true
        is Div.Gallery -> true
        is Div.Pager -> true
        is Div.Tabs -> true
        is Div.State -> true
    }

internal val Div.isLeaf: Boolean
    get() = !isBranch
