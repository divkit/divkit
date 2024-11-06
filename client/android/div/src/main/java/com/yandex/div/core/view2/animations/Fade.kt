package com.yandex.div.core.view2.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.core.view.ViewCompat
import androidx.transition.TransitionValues

internal class Fade(
    @FloatRange(from = 0.0, to = 1.0) val alpha: Float = 0.0f
) : OutlineAwareVisibility() {
    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
        when (mode) {
            MODE_IN -> {
                transitionValues.values[PROPNAME_ALPHA] = alpha
            }
            MODE_OUT -> {
                transitionValues.values[PROPNAME_ALPHA] = transitionValues.view.alpha
            }
        }

        capturePosition(transitionValues) { position ->
            transitionValues.values[PROPNAME_SCREEN_POSITION] = position
        }
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        super.captureEndValues(transitionValues)

        when (mode) {
            MODE_IN -> {
                transitionValues.values[PROPNAME_ALPHA] = transitionValues.view.alpha
            }
            MODE_OUT -> {
                transitionValues.values[PROPNAME_ALPHA] = alpha
            }
        }

        capturePosition(transitionValues) { position ->
            transitionValues.values[PROPNAME_SCREEN_POSITION] = position
        }
    }

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        endValues ?: return null

        val startAlpha = getCapturedAlpha(startValues, alpha)
        val endAlpha = getCapturedAlpha(endValues, 1.0f)

        val position = endValues.values[PROPNAME_SCREEN_POSITION] as IntArray

        val overlayView = createOrGetVisualCopy(view, sceneRoot, this, position)

        return createFadeAnimator(overlayView, startAlpha, endAlpha)
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        startValues ?: return null

        val startAlpha = getCapturedAlpha(startValues, 1.0f)
        val endAlpha = getCapturedAlpha(endValues, alpha)

        val viewForAnimate = getViewForAnimate(view, sceneRoot, startValues, PROPNAME_SCREEN_POSITION)

        return createFadeAnimator(viewForAnimate, startAlpha, endAlpha)
    }

    private fun getCapturedAlpha(transitionValues: TransitionValues?, fallbackValue: Float): Float {
        return transitionValues?.values?.get(PROPNAME_ALPHA) as? Float ?: fallbackValue
    }

    private fun createFadeAnimator(view: View, startAlpha: Float, endAlpha: Float): Animator? {
        if (startAlpha == endAlpha) {
            return null
        }

        view.alpha = startAlpha
        return ObjectAnimator.ofFloat(view, View.ALPHA, startAlpha, endAlpha).apply {
            addListener(FadeAnimatorListener(view, view.alpha))
        }
    }

    private class FadeAnimatorListener(
        private val view: View,
        private val nonTransitionAlpha: Float
    ) : AnimatorListenerAdapter() {

        private var isLayerTypeChanged = false

        override fun onAnimationStart(animation: Animator) {
            view.visibility = View.VISIBLE
            if (ViewCompat.hasOverlappingRendering(view) && view.layerType == View.LAYER_TYPE_NONE) {
                isLayerTypeChanged = true
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            }
        }

        override fun onAnimationEnd(animation: Animator) {
            view.alpha = nonTransitionAlpha

            if (isLayerTypeChanged) {
                view.setLayerType(View.LAYER_TYPE_NONE, null)
            }
            animation.removeListener(this)
        }
    }

    private companion object {
        private const val PROPNAME_SCREEN_POSITION = "yandex:fade:screenPosition"

        private const val PROPNAME_ALPHA: String = "yandex:fade:alpha"
    }
}
