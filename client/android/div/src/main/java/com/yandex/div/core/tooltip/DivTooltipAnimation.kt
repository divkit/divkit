package com.yandex.div.core.tooltip

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Build
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionSet
import android.transition.TransitionValues
import android.transition.Visibility
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import com.yandex.div.core.animation.SpringInterpolator
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.view2.divs.dpToPxF
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAnimation
import com.yandex.div2.DivTooltip
import android.view.animation.TranslateAnimation as AndroidTranslateAnimation

private const val DEFAULT_TRANSLATION = 10
private const val DEFAULT_DURATION = 300L

private const val DEFAULT_ALPHA_SHOW_VALUE = 1f
private const val DEFAULT_ALPHA_HIDE_VALUE = 0f

private val View.defaultTranslation
    get() = DEFAULT_TRANSLATION.dpToPxF(resources.displayMetrics)

internal fun animateEnter(
    divTooltip: DivTooltip,
    resolver: ExpressionResolver,
    tooltipView: View,
    substrateView: View,
) {
    val tooltipAnimation = createTooltipAnimation(
        animation = divTooltip.animationIn,
        position = divTooltip.position.evaluate(resolver),
        resolver = resolver,
        tooltipView = tooltipView,
        incoming = true,
    )

    val substrateAnimation = createDefaultAlphaAnimation(incoming = true).apply {
        duration = tooltipAnimation.duration
        interpolator = tooltipAnimation.interpolator
    }

    tooltipView.startAnimation(tooltipAnimation)
    substrateView.startAnimation(substrateAnimation)
}

internal fun animateExit(
    divTooltip: DivTooltip,
    resolver: ExpressionResolver,
    tooltipView: View,
    substrateView: View,
    onEnd: () -> Unit,
) {
    val tooltipAnimation = createTooltipAnimation(
        animation = divTooltip.animationOut,
        position = divTooltip.position.evaluate(resolver),
        resolver = resolver,
        tooltipView = tooltipView,
        incoming = false,
    ).apply { setAnimationListener(createAnimationListener(onEnd)) }

    val substrateAnimation = createDefaultAlphaAnimation(incoming = false).apply {
        duration = tooltipAnimation.duration
        interpolator = tooltipAnimation.interpolator
    }

    tooltipView.startAnimation(tooltipAnimation)
    substrateView.startAnimation(substrateAnimation)
}

internal fun PopupWindow.setupAnimation(divTooltip: DivTooltip, resolver: ExpressionResolver) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val animationIn = divTooltip.animationIn
        enterTransition = if (animationIn != null) {
            animationIn.toTransition(divTooltip.position.evaluate(resolver), true, resolver)
        } else {
            defaultTransition(divTooltip, resolver)
        }

        val animationOut = divTooltip.animationOut
        exitTransition = if (animationOut != null) {
            animationOut.toTransition(divTooltip.position.evaluate(resolver), false, resolver)
        } else {
            defaultTransition(divTooltip, resolver)
        }
    } else {
        animationStyle = android.R.style.Animation_Dialog
    }
}

internal fun PopupWindow.clearAnimation() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        removeTransition()
    } else {
        animationStyle = 0
    }
}

@RequiresApi(Build.VERSION_CODES.M)
private fun PopupWindow.removeTransition() {
    enterTransition = null
    exitTransition = null
}

private fun defaultTransition(divTooltip: DivTooltip, resolver: ExpressionResolver) = TransitionSet()
    .addTransition(Fade())
    .addTransition(TranslateAnimation(divTooltip.position.evaluate(resolver)))
    .setInterpolator(SpringInterpolator())

private fun DivAnimation.toTransition(position: DivTooltip.Position, incoming: Boolean,
                                      resolver: ExpressionResolver): Transition? {
    return when (name.evaluate(resolver)) {
        DivAnimation.Name.FADE -> Fade()
        DivAnimation.Name.TRANSLATE -> TranslateAnimation(
            position,
            percentage = (if (incoming) this.startValue else this.endValue)
                ?.evaluate(resolver)?.toFloat()
        )
        DivAnimation.Name.SCALE -> {
            val scaleFactor = if (incoming) {
                this.startValue
            } else {
                this.endValue
            }
            Scale(scaleFactor?.evaluate(resolver)?.toFloat() ?: 1f)
        }
        DivAnimation.Name.SET -> {
            val set = TransitionSet()
            items?.forEach {
                set.addTransition(it.toTransition(position, incoming, resolver))
            }
            set
        }
        DivAnimation.Name.NATIVE,
        DivAnimation.Name.NO_ANIMATION -> null
    }
        ?.setDuration(duration.evaluate(resolver).toLong())
        ?.setInterpolator(interpolator.evaluate(resolver).androidInterpolator)
}

private class TranslateAnimation(
    private val position: DivTooltip.Position,
    private val percentage: Float? = null,
) : Visibility() {

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues,
        endValues: TransitionValues
    ): Animator {
        val directionX = initialDirectionX(position)
        val directionY = initialDirectionY(position)

        view.translationX =
            directionX * if (percentage != null) view.width * percentage else view.defaultTranslation
        view.translationY =
            directionY * if (percentage != null) view.height * percentage else view.defaultTranslation

        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(
                View.TRANSLATION_X,
                view.translationX,
                0f
            ),
            PropertyValuesHolder.ofFloat(
                View.TRANSLATION_Y,
                view.translationY,
                0f
            )
        )
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues,
        endValues: TransitionValues
    ): Animator {

        val directionX = initialDirectionX(position)
        val directionY = initialDirectionY(position)

        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(
                View.TRANSLATION_X,
                0f,
                directionX * if (percentage != null) view.width * percentage else view.defaultTranslation
            ),
            PropertyValuesHolder.ofFloat(
                View.TRANSLATION_Y,
                0f,
                directionY * if (percentage != null) view.height * percentage else view.defaultTranslation
            )
        )
    }
}

private class Scale(
    private val scaleFactor: Float
) : Visibility() {
    override fun onAppear(
        sceneRoot: ViewGroup?,
        view: View,
        startValues: TransitionValues,
        endValues: TransitionValues
    ): Animator {
        return ObjectAnimator.ofPropertyValuesHolder(
            view,

            PropertyValuesHolder.ofFloat(
                View.SCALE_X,
                scaleFactor,
                view.scaleX
            ),
            PropertyValuesHolder.ofFloat(
                View.SCALE_Y,
                scaleFactor,
                view.scaleY
            )
        )
    }

    override fun onDisappear(
        sceneRoot: ViewGroup?,
        view: View,
        startValues: TransitionValues,
        endValues: TransitionValues
    ): Animator {
        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(
                View.SCALE_X,
                view.scaleX,
                scaleFactor
            ),
            PropertyValuesHolder.ofFloat(
                View.SCALE_Y,
                view.scaleY,
                scaleFactor
            )
        )
    }
}

private fun createTooltipAnimation(
    animation: DivAnimation?,
    position: DivTooltip.Position,
    resolver: ExpressionResolver,
    tooltipView: View,
    incoming: Boolean,
) = animation?.toAnimation(
    view = tooltipView,
    position = position,
    incoming = incoming,
    resolver = resolver,
) ?: defaultAnimation(
    view = tooltipView,
    position = position,
    incoming = incoming,
)

private fun DivAnimation.toAnimation(
    view: View,
    position: DivTooltip.Position,
    incoming: Boolean,
    resolver: ExpressionResolver,
): Animation? = when (name.evaluate(resolver)) {
    DivAnimation.Name.FADE -> {
        val startValue = startValue?.evaluate(resolver)?.toFloat() ?: DEFAULT_ALPHA_SHOW_VALUE
        val endValue = endValue?.evaluate(resolver)?.toFloat() ?: DEFAULT_ALPHA_HIDE_VALUE
        AlphaAnimation(startValue, endValue)
    }

    DivAnimation.Name.TRANSLATE -> {
        val percentage = (if (incoming) this.startValue else this.endValue)
            ?.evaluate(resolver)?.toFloat()
        createTranslateAnimation(view, position, incoming, percentage)
    }

    DivAnimation.Name.SCALE -> {
        val fromScale = startValue?.evaluate(resolver)?.toFloat() ?: 0f
        val toScale = endValue?.evaluate(resolver)?.toFloat() ?: 1f
        ScaleAnimation(
            fromScale, toScale, fromScale, toScale,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
    }

    DivAnimation.Name.SET -> {
        AnimationSet(false).apply {
            items?.forEach { divAnimation ->
                val childAnimation =
                    divAnimation.toAnimation(view, position, incoming, resolver)
                addAnimation(childAnimation)
            }
        }
    }

    DivAnimation.Name.NATIVE,
    DivAnimation.Name.NO_ANIMATION -> null
}?.apply {
    duration = this@toAnimation.duration.evaluate(resolver)
    interpolator = this@toAnimation.interpolator.evaluate(resolver).androidInterpolator
}

private fun createTranslateAnimation(
    view: View,
    position: DivTooltip.Position,
    incoming: Boolean,
    percentage: Float?
): AndroidTranslateAnimation {
    val directionX = initialDirectionX(position)
    val directionY = initialDirectionY(position)

    if (incoming) {
        // From offset to 0
        val fromX = directionX * (percentage?.let { view.width * it } ?: view.defaultTranslation)
        val fromY = directionY * (percentage?.let { view.height * it } ?: view.defaultTranslation)
        return AndroidTranslateAnimation(fromX, 0f, fromY, 0f)
    } else {
        // From 0 to offset
        val toX = directionX * (percentage?.let { view.width * it } ?: view.defaultTranslation)
        val toY = directionY * (percentage?.let { view.height * it } ?: view.defaultTranslation)
        return AndroidTranslateAnimation(0f, toX, 0f, toY)
    }
}

private fun defaultAnimation(
    view: View,
    position: DivTooltip.Position,
    incoming: Boolean
): Animation = AnimationSet(true).apply {
    addAnimation(createDefaultAlphaAnimation(incoming))
    addAnimation(createTranslateAnimation(view, position, incoming, null))

    duration = DEFAULT_DURATION
    interpolator = SpringInterpolator()
}

private fun createDefaultAlphaAnimation(
    incoming: Boolean
) = AlphaAnimation(
    if (incoming) DEFAULT_ALPHA_HIDE_VALUE else DEFAULT_ALPHA_SHOW_VALUE,
    if (incoming) DEFAULT_ALPHA_SHOW_VALUE else DEFAULT_ALPHA_HIDE_VALUE,
)

private fun initialDirectionX(
    position: DivTooltip.Position
) = when (position) {
    DivTooltip.Position.TOP_LEFT, DivTooltip.Position.LEFT, DivTooltip.Position.BOTTOM_LEFT -> 1f
    DivTooltip.Position.TOP_RIGHT, DivTooltip.Position.RIGHT, DivTooltip.Position.BOTTOM_RIGHT -> -1f
    DivTooltip.Position.CENTER -> 0.5f
    DivTooltip.Position.TOP, DivTooltip.Position.BOTTOM -> 0f
}

private fun initialDirectionY(
    position: DivTooltip.Position
) = when (position) {
    DivTooltip.Position.TOP_LEFT, DivTooltip.Position.TOP, DivTooltip.Position.TOP_RIGHT -> 1f
    DivTooltip.Position.BOTTOM_LEFT, DivTooltip.Position.BOTTOM, DivTooltip.Position.BOTTOM_RIGHT -> -1f
    DivTooltip.Position.CENTER -> 0.5f
    DivTooltip.Position.LEFT, DivTooltip.Position.RIGHT -> 0f
}

private fun createAnimationListener(
    onEnd: (() -> Unit)? = null,
) = object : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation?) {}
    override fun onAnimationEnd(animation: Animation?) {
        onEnd?.invoke()
    }

    override fun onAnimationRepeat(animation: Animation?) {}
}
