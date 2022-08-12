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
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        super.captureEndValues(transitionValues)
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view

        transitionValues.values[PROPNAME_ALPHA] = view.alpha

        val position = IntArray(2)
        view.getLocationOnScreen(position)
        transitionValues.values[PROPNAME_SCREEN_POSITION] = position
    }

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View?,
        startValues: TransitionValues?,
        endValues: TransitionValues
    ): Animator? {
        if (view == null) return null

        val startAlpha = getCapturedAlpha(startValues, alpha)
        val endAlpha = getCapturedAlpha(endValues, 1.0f)

        val position = endValues.values[PROPNAME_SCREEN_POSITION] as IntArray

        val overlayView = createOrGetVisualCopy(view, sceneRoot, this, position)

        return createFadeAnimator(overlayView, startAlpha, endAlpha)
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (view == null) return null

        val startAlpha = getCapturedAlpha(startValues, 1.0f)
        val endAlpha = getCapturedAlpha(endValues, alpha)
        return createFadeAnimator(view, startAlpha, endAlpha)
    }

    private fun getCapturedAlpha(transitionValues: TransitionValues?, fallbackValue: Float): Float {
        return transitionValues?.values?.get(PROPNAME_ALPHA) as? Float ?: fallbackValue
    }

    private fun createFadeAnimator(view: View, startAlpha: Float, endAlpha: Float): Animator? {
        if (startAlpha == endAlpha) {
            return null
        }

        view.visibility = View.INVISIBLE
        return ObjectAnimator.ofFloat(view, View.ALPHA, startAlpha, endAlpha).apply {
            addListener(FadeAnimatorListener(view))
        }
    }

    private class FadeAnimatorListener(
        private val view: View
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
            if (isLayerTypeChanged) {
                view.setLayerType(View.LAYER_TYPE_NONE, null)
            }
            animation.removeListener(this)
        }
    }

    private companion object {
        private const val PROPNAME_ALPHA: String = "yandex:fade:alpha"

        private const val PROPNAME_SCREEN_POSITION = "yandex:slide:screenPosition"
    }
}
