package com.yandex.div.core.view2.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Rect
import android.util.Property
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.core.view.ViewCompat
import androidx.transition.TransitionValues
import androidx.transition.Visibility

internal class VerticalTranslation(
    private val translatedValue: Float = DEFAULT_TRANSLATED_VALUE,
    private val stableValue: Float = DEFAULT_STABLE_VALUE
) : OutlineAwareVisibility() {
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
        endValues ?: return null
        val height = view.height

        val startY = translatedValue * height
        val endY = stableValue * height

        val position = endValues.values[PROPNAME_SCREEN_POSITION] as IntArray

        val overlayView = createOrGetVisualCopy(view, sceneRoot, this, position)

        overlayView.translationY = startY
        val clipBoundsProperty = TranslationYClipBounds(overlayView)
        clipBoundsProperty.set(overlayView, translatedValue)

        return ObjectAnimator.ofPropertyValuesHolder(
            overlayView,
            PropertyValuesHolder.ofFloat(
                View.TRANSLATION_Y,
                startY,
                endY
            ),
            PropertyValuesHolder.ofFloat(
                clipBoundsProperty,
                translatedValue,
                stableValue
            )
        ).apply {
            addListener(AnimationEndListener(view))
        }
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        startValues ?: return null
        val startY = stableValue
        val endY = translatedValue * view.height.toFloat()

        val viewForAnimate = getViewForAnimate(view, sceneRoot, startValues, PROPNAME_SCREEN_POSITION)

        return ObjectAnimator.ofPropertyValuesHolder(
            viewForAnimate,
            PropertyValuesHolder.ofFloat(
                View.TRANSLATION_Y,
                startY,
                endY
            ),
            PropertyValuesHolder.ofFloat(
                TranslationYClipBounds(view),
                stableValue,
                translatedValue
            )
        ).apply {
            addListener(AnimationEndListener(view))
        }
    }

    @VisibleForTesting
    internal class TranslationYClipBounds(view: View) : Property<View, Float>(Float::class.java, "ClipBoundsTop") {

        private val clipBounds = Rect(0, 0, view.width, view.height)
        private var clipFactor = 0f

        override fun get(view: View): Float {
            return clipFactor
        }

        override fun set(view: View, value: Float) {
            clipFactor = value
            when {
                clipFactor < 0f -> clipBounds.set(0,
                                                  (-clipFactor * (view.height - 1)).toInt(),
                                                  view.width,
                                                  view.height)
                clipFactor > 0f -> clipBounds.set(0,
                                                  0,
                                                  view.width,
                                                  ((1 - clipFactor) * view.height + 1).toInt())
                else -> clipBounds.set(0, 0, view.width, view.height)
            }
            ViewCompat.setClipBounds(view, clipBounds)
        }
    }

    private class AnimationEndListener(private val view: View) : AnimatorListenerAdapter() {

        override fun onAnimationEnd(animation: Animator) {
            view.translationY = 0.0f
            ViewCompat.setClipBounds(view, null)
        }
    }

    companion object {
        const val DEFAULT_TRANSLATED_VALUE = -1f
        const val DEFAULT_STABLE_VALUE = 0f

        private const val PROPNAME_SCREEN_POSITION = "yandex:verticalTranslation:screenPosition"
    }
}
