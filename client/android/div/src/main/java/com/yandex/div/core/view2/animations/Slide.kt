package com.yandex.div.core.view2.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.yandex.div.R
import kotlin.math.roundToInt

internal class Slide(
    val distance: Int = DISTANCE_TO_EDGE,
    val slideEdge: Int = Gravity.BOTTOM
) : OutlineAwareVisibility() {

    private val slideCalculator: SlideCalculator = when (slideEdge) {
        Gravity.LEFT -> calculatorLeft
        Gravity.TOP -> calculatorTop
        Gravity.RIGHT -> calculatorRight
        else -> calculatorBottom
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
        capturePosition(transitionValues) { position ->
            transitionValues.values[PROPNAME_SCREEN_POSITION] = position
        }
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        super.captureEndValues(transitionValues)
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
        if (endValues == null) return null

        val position = endValues.values[PROPNAME_SCREEN_POSITION] as IntArray
        val startX: Float = slideCalculator.getGoneX(sceneRoot, view, distance)
        val startY: Float = slideCalculator.getGoneY(sceneRoot, view, distance)
        val endX = view.translationX
        val endY = view.translationY

        val overlayView = createOrGetVisualCopy(view, sceneRoot, this, position)

        return createTranslateAnimator(
            overlayView, this, endValues,
            position[0], position[1],
            startX, startY,
            endX, endY,
            interpolator
        )
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null) return null

        val position = startValues.values[PROPNAME_SCREEN_POSITION] as IntArray
        val startX = view.translationX
        val startY = view.translationY
        val endX: Float = slideCalculator.getGoneX(sceneRoot, view, distance)
        val endY: Float = slideCalculator.getGoneY(sceneRoot, view, distance)

        val viewForAnimate = getViewForAnimate(view, sceneRoot, startValues, PROPNAME_SCREEN_POSITION)

        return createTranslateAnimator(
            viewForAnimate, this, startValues,
            position[0], position[1],
            startX, startY,
            endX, endY,
            interpolator
        )
    }

    private fun createTranslateAnimator(
        view: View,
        transition: Transition,
        values: TransitionValues,
        viewPositionX: Int,
        viewPositionY: Int,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        interpolator: TimeInterpolator?,
    ): Animator? {
        var startX = startX
        var startY = startY
        val terminalX = view.translationX
        val terminalY = view.translationY
        val startPosition = values.view.getTag(R.id.div_transition_position) as? IntArray
        if (startPosition != null) {
            startX = startPosition[0] - viewPositionX + terminalX
            startY = startPosition[1] - viewPositionY + terminalY
        }

        // Initial position is at translation startX, startY, so position is offset by that amount
        val startPositionX = viewPositionX + (startX - terminalX).roundToInt()
        val startPositionY = viewPositionY + (startY - terminalY).roundToInt()
        view.translationX = startX
        view.translationY = startY
        if (startX == endX && startY == endY) {
            return null
        }

        val animator = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, startX, endX),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, startY, endY)
        )

        val listener = TransitionPositionListener(
            values.view, view,
            startPositionX, startPositionY, terminalX, terminalY
        )
        transition.addListener(listener)
        animator.addListener(listener)
        animator.addPauseListener(listener)

        animator.interpolator = interpolator
        return animator
    }

    private class TransitionPositionListener constructor(
        private val originalView: View,
        private val movingView: View,
        startX: Int,
        startY: Int,
        val terminalX: Float,
        val terminalY: Float
    ) : AnimatorListenerAdapter(), TransitionListener {

        private val startX: Int = startX - movingView.translationX.roundToInt()
        private val startY: Int = startY - movingView.translationY.roundToInt()

        private var transitionPosition = originalView.getTag(R.id.div_transition_position) as? IntArray
        private var pausedX = 0.0f
        private var pausedY = 0.0f

        init {
            if (transitionPosition != null) {
                originalView.setTag(R.id.div_transition_position, null)
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            if (transitionPosition == null) {
                transitionPosition = intArrayOf(
                    startX + movingView.translationX.roundToInt(),
                    startY + movingView.translationY.roundToInt()
                )
            }
            originalView.setTag(R.id.div_transition_position, transitionPosition)
        }

        override fun onAnimationPause(animator: Animator) {
            pausedX = movingView.translationX
            pausedY = movingView.translationY
            movingView.translationX = terminalX
            movingView.translationY = terminalY
        }

        override fun onAnimationResume(animator: Animator) {
            movingView.translationX = pausedX
            movingView.translationY = pausedY
        }

        override fun onTransitionStart(transition: Transition) = Unit

        override fun onTransitionEnd(transition: Transition) {
            movingView.translationX = terminalX
            movingView.translationY = terminalY
            transition.removeListener(this)
        }

        override fun onTransitionCancel(transition: Transition) = Unit

        override fun onTransitionPause(transition: Transition) = Unit

        override fun onTransitionResume(transition: Transition) = Unit
    }

    private interface SlideCalculator {

        /** @return the translation value for view when it goes out of the scene. */
        fun getGoneX(sceneRoot: ViewGroup, view: View, distance: Int): Float

        /** @return the translation value for view when it goes out of the scene. */
        fun getGoneY(sceneRoot: ViewGroup, view: View, distance: Int): Float
    }

    private abstract class HorizontalSlideCalculator : SlideCalculator {
        override fun getGoneY(sceneRoot: ViewGroup, view: View, distance: Int) = view.translationY
    }

    private abstract class VerticalSlideCalculator : SlideCalculator {
        override fun getGoneX(sceneRoot: ViewGroup, view: View, distance: Int) = view.translationX
    }

    companion object {

        const val DISTANCE_TO_EDGE = -1

        private const val PROPNAME_SCREEN_POSITION = "yandex:slide:screenPosition"

        private fun Int.exactValueBy(edgeDistance: Int): Int {
            return if (this == DISTANCE_TO_EDGE) edgeDistance else this
        }

        private val calculatorLeft = object : HorizontalSlideCalculator() {
            override fun getGoneX(sceneRoot: ViewGroup, view: View, distance: Int): Float {
                return view.translationX - distance.exactValueBy(view.right)
            }
        }

        private val calculatorTop = object : VerticalSlideCalculator() {
            override fun getGoneY(sceneRoot: ViewGroup, view: View, distance: Int): Float {
                return view.translationY - distance.exactValueBy(view.bottom)
            }
        }

        private val calculatorRight = object : HorizontalSlideCalculator() {
            override fun getGoneX(sceneRoot: ViewGroup, view: View, distance: Int): Float {
                return view.translationX + distance.exactValueBy(sceneRoot.width - view.left)
            }
        }

        private val calculatorBottom = object : VerticalSlideCalculator() {
            override fun getGoneY(sceneRoot: ViewGroup, view: View, distance: Int): Float {
                return view.translationY + distance.exactValueBy(sceneRoot.height - view.top)
            }
        }
    }
}
