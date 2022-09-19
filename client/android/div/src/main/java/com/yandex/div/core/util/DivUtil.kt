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
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivBase
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
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSlider
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText

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
            is Div.Container -> DivContainer.TYPE
            is Div.Grid -> DivGrid.TYPE
            is Div.State -> DivState.TYPE
            is Div.Gallery -> DivGallery.TYPE
            is Div.Pager -> DivPager.TYPE
            is Div.Tabs -> DivTabs.TYPE
            is Div.Custom -> DivCustom.TYPE
        }
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
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): FloatArray {
    val topLeft = (cornersRadius?.topLeft ?: cornerRadius)?.evaluate(resolver).dpToPx(metrics).toFloat()
    val topRight = (cornersRadius?.topRight ?: cornerRadius)?.evaluate(resolver).dpToPx(metrics).toFloat()
    val bottomLeft = (cornersRadius?.bottomLeft ?: cornerRadius)?.evaluate(resolver).dpToPx(metrics).toFloat()
    val bottomRight = (cornersRadius?.bottomRight ?: cornerRadius)?.evaluate(resolver).dpToPx(metrics).toFloat()

    return floatArrayOf(
        topLeft, topLeft,
        topRight, topRight,
        bottomRight, bottomRight,
        bottomLeft, bottomLeft
    )
}

internal fun DivBase.containsStateInnerTransitions(): Boolean {
    if (transitionIn != null || transitionChange != null || transitionOut != null) {
        return true
    }
    return when (this) {
        is DivText -> false
        is DivImage -> false
        is DivGifImage -> false
        is DivSeparator -> false
        is DivIndicator -> false
        is DivContainer -> items.map { div -> div.value().containsStateInnerTransitions() }.contains(true)
        is DivGrid -> items.map { div -> div.value().containsStateInnerTransitions() }.contains(true)
        is DivState -> false  // state binder should resolve by itself which animation description to use.
        is DivGallery -> false
        is DivPager -> false
        is DivTabs -> false
        is DivCustom -> false
        else -> false
    }
}

internal fun DivState.getDefaultState(resolver: ExpressionResolver): DivState.State {
        return defaultStateId?.let { defaultStateId ->
            states.find { it.stateId == defaultStateId.evaluate(resolver) }
        } ?: states[0]
    }
