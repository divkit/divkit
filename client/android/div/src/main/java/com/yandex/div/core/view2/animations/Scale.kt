package com.yandex.div.core.view2.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.transition.TransitionValues

internal class Scale(
    @FloatRange(from = 0.0) private val scaleFactor: Float,
    @FloatRange(from = 0.0, to = 1.0) private val pivotX: Float = PIVOT_CENTER,
    @FloatRange(from = 0.0, to = 1.0) private val pivotY: Float = PIVOT_CENTER
) : OutlineAwareVisibility() {

    override fun captureStartValues(transitionValues: TransitionValues) {
        transitionValues.withNoScale {
            super.captureStartValues(transitionValues)
        }
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        transitionValues.withNoScale {
            super.captureEndValues(transitionValues)
        }
        captureValues(transitionValues)
    }

    private inline fun TransitionValues.withNoScale(block: (TransitionValues) -> Unit) {
        val scaleX = view.scaleX
        val scaleY = view.scaleY
        view.scaleX = NO_SCALE
        view.scaleY = NO_SCALE

        block(this)

        view.scaleX = scaleX
        view.scaleY = scaleY
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        transitionValues.values[PROPNAME_SCALE_X] = view.scaleX
        transitionValues.values[PROPNAME_SCALE_Y] = view.scaleY

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

        val startScaleX = getCapturedScaleX(startValues, scaleFactor)
        val startScaleY = getCapturedScaleY(startValues, scaleFactor)
        val endScaleX = getCapturedScaleX(endValues, NO_SCALE)
        val endScaleY = getCapturedScaleY(endValues, NO_SCALE)

        val position = endValues.values[PROPNAME_SCREEN_POSITION] as IntArray

        val overlayView = createOrGetVisualCopy(view, sceneRoot, this, position)

        return createScaleAnimator(overlayView, startScaleX, startScaleY, endScaleX, endScaleY)
    }

    override fun onDisappear(
        sceneRoot: ViewGroup?,
        view: View?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (view == null) return null

        val startScaleX = getCapturedScaleX(startValues, NO_SCALE)
        val startScaleY = getCapturedScaleY(startValues, NO_SCALE)
        val endScaleX = getCapturedScaleX(endValues, scaleFactor)
        val endScaleY = getCapturedScaleY(endValues, scaleFactor)
        return createScaleAnimator(view, startScaleX, startScaleY, endScaleX, endScaleY)
    }

    private fun getCapturedScaleX(transitionValues: TransitionValues?, fallbackValue: Float): Float {
        return transitionValues?.values?.get(PROPNAME_SCALE_X) as? Float ?: fallbackValue
    }

    private fun getCapturedScaleY(transitionValues: TransitionValues?, fallbackValue: Float): Float {
        return transitionValues?.values?.get(PROPNAME_SCALE_Y) as? Float ?: fallbackValue
    }

    private fun createScaleAnimator(
        view: View,
        startScaleX: Float,
        startScaleY: Float,
        endScaleX: Float,
        endScaleY: Float
    ): Animator? {
        if (startScaleX == endScaleX && startScaleY == endScaleY) {
            return null
        }

        view.visibility = View.INVISIBLE
        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, startScaleX, endScaleX),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, startScaleY, endScaleY)
        ).apply {
            addListener(ScaleAnimatorListener(view))
        }
    }

    private inner class ScaleAnimatorListener(
        private val view: View
    ) : AnimatorListenerAdapter() {

        private var isPivotSet = false

        override fun onAnimationStart(animation: Animator) {
            view.visibility = View.VISIBLE
            if (pivotX != PIVOT_CENTER || pivotY != PIVOT_CENTER) {
                isPivotSet = true
                view.pivotX = view.width * pivotX
                view.pivotY = view.height * pivotY
            }
        }

        override fun onAnimationEnd(animation: Animator) {
            if (isPivotSet) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    view.resetPivot()
                } else {
                    view.pivotX = view.width * PIVOT_CENTER
                    view.pivotY = view.height * PIVOT_CENTER
                }
            }
            animation.removeListener(this)
        }
    }

    private companion object {
        private const val PROPNAME_SCREEN_POSITION = "yandex:slide:screenPosition"

        const val NO_SCALE = 1.0f
        const val PIVOT_CENTER = 0.5f
        const val PROPNAME_SCALE_X = "yandex:scale:scaleX"
        const val PROPNAME_SCALE_Y = "yandex:scale:scaleY"
    }
}
