package com.yandex.div.core.view2.animations

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.core.content.ContextCompat
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.yandex.div.R
import com.yandex.div.core.animation.reversed
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.util.isActuallyLaidOut
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAnimation

private const val MIN_ALPHA_VALUE = 0.0f
private const val MAX_ALPHA_VALUE = 1.0f
private const val DEFAULT_ALPHA_START_VALUE = 1f
private const val DEFAULT_ALPHA_END_VALUE = 0.6f

private const val MIN_SCALE_VALUE = 0.0f
private const val DEFAULT_SCALE_START_VALUE = 1f
private const val DEFAULT_SCALE_END_VALUE = 0.95f
private const val SCALE_PIVOT_VALUE = 0.5f

internal val DEFAULT_CLICK_ANIMATION = DivAnimation(
    duration = Expression.constant(100),
    endValue = Expression.constant(0.6),
    name = Expression.constant(DivAnimation.Name.FADE),
    startValue = Expression.constant(1.0)
)

internal fun Transition.getViewForAnimate(
    view: View,
    sceneRoot: ViewGroup,
    values: TransitionValues,
    positionKey: String
): View {
    val startViewIsOverlayView = values.view == view
    /*
     * If start animation view is equals to overlay animation view, that means that
     * `androidx.transition.Visibility#onDisappear` expect to see real view as an overlay (cause
     * start view has no parents).
     *
     * If we get the copy and set it as `save_overlay_view` tag to get copy on next animation applying,
     * the tag will be anyway replaced by `androidx.transition.Visibility#onDisappear` with start view
     * so when applying second animation, we get our start view from `save_overlay_view` tag,
     * and so we will apply the first animation to the copied view and all future to the real view.
     */

    return if (!startViewIsOverlayView && view.isActuallyLaidOut) {
        createOrGetVisualCopy(view, sceneRoot, this, values.values[positionKey] as IntArray)
    } else {
        view
    }
}

internal fun capturePosition(transitionValues: TransitionValues, savePosition: (IntArray) -> Unit) {
    val view = transitionValues.view
    val position = IntArray(2)
    view.getLocationOnScreen(position)
    savePosition(position)
}

internal fun DivAnimation.asTouchListener(
    expressionResolver: ExpressionResolver,
    view: View
): ((View, MotionEvent) -> Unit)? {
    val directAnimation = toAnimation(expressionResolver, view = view)
    val reverseAnimation = toAnimation(expressionResolver, reverse = true)

    if (directAnimation == null && reverseAnimation == null) {
        return null
    }

    return { v, event ->
        if (v.isEnabled && v.isClickable && v.hasOnClickListeners()) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> directAnimation?.let { v.startAnimation(it) }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> reverseAnimation?.let {
                    v.startAnimation(it)
                }
            }
        }
    }
}

private fun DivAnimation.toAnimation(
    resolver: ExpressionResolver,
    reverse: Boolean = false,
    view: View? = null
): Animation? {
    val animationName = name.evaluate(resolver)
    val animation = when (animationName) {
        DivAnimation.Name.SET -> AnimationSet(false).apply {
            items?.forEach { divAnimation ->
                val animation = divAnimation.toAnimation(resolver, reverse, view)
                animation?.let { addAnimation(it) }
            }
        }
        DivAnimation.Name.SCALE ->
            if (reverse) {
                createScaleAnimation(
                    endValue?.evaluate(resolver).scaleValue() ?: DEFAULT_SCALE_END_VALUE,
                    startValue?.evaluate(resolver).scaleValue() ?: DEFAULT_SCALE_START_VALUE
                )
            } else {
                createScaleAnimation(
                    startValue?.evaluate(resolver).scaleValue() ?: DEFAULT_SCALE_START_VALUE,
                    endValue?.evaluate(resolver).scaleValue() ?: DEFAULT_SCALE_END_VALUE
                )
            }
        DivAnimation.Name.NATIVE -> {
            if (view != null) {
                val layers = view.background as? LayerDrawable
                val shouldAddAnimation = layers == null || (0 until layers.numberOfLayers).none {
                    layers.getId(it) == R.drawable.native_animation_background
                }

                if (shouldAddAnimation) {
                    val drawables = mutableListOf<Drawable>()

                    layers?.let {
                        for (i in 0 until it.numberOfLayers) {
                            drawables.add(it.getDrawable(i))
                        }
                    } ?: drawables.add(view.background)

                    val animation = ContextCompat.getDrawable(
                        view.context,
                        R.drawable.native_animation_background
                    )

                    animation?.let { drawables.add(it) }

                    val layerDrawable = LayerDrawable(drawables.toTypedArray())
                    layerDrawable.setId(
                        drawables.size - 1,
                        R.drawable.native_animation_background
                    ) //mark background has animation
                    view.background = layerDrawable
                }
            }
            null
        }
        DivAnimation.Name.NO_ANIMATION -> null
        else ->
            if (reverse) {
                AlphaAnimation(
                    endValue?.evaluate(resolver).alphaValue() ?: DEFAULT_ALPHA_END_VALUE,
                    startValue?.evaluate(resolver).alphaValue() ?: DEFAULT_ALPHA_START_VALUE
                )
            } else {
                AlphaAnimation(
                    startValue?.evaluate(resolver).alphaValue() ?: DEFAULT_ALPHA_START_VALUE,
                    endValue?.evaluate(resolver).alphaValue() ?: DEFAULT_ALPHA_END_VALUE
                )
            }
    }

    if (animationName != DivAnimation.Name.SET) {
        animation?.interpolator = if (reverse) {
            interpolator.evaluate(resolver).androidInterpolator.reversed()
        } else {
            interpolator.evaluate(resolver).androidInterpolator
        }
        animation?.duration = duration.evaluate(resolver)
    }

    animation?.startOffset = startDelay.evaluate(resolver)
    animation?.fillAfter = true

    return animation
}

private fun createScaleAnimation(startValue: Float, endValue: Float) =
    ScaleAnimation(
        startValue, endValue, startValue, endValue,
        Animation.RELATIVE_TO_SELF, SCALE_PIVOT_VALUE,
        Animation.RELATIVE_TO_SELF, SCALE_PIVOT_VALUE
    )

private fun Double?.alphaValue(): Float? {
    return this?.toFloat()?.coerceIn(MIN_ALPHA_VALUE, MAX_ALPHA_VALUE)
}

private fun Double?.scaleValue(): Float? {
    return this?.toFloat()?.coerceAtLeast(MIN_SCALE_VALUE)
}
