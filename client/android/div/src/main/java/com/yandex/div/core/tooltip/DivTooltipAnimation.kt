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
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import com.yandex.div.core.animation.SpringInterpolator
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.internal.util.dpToPx
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAnimation
import com.yandex.div2.DivTooltip

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

    private val defaultTranslation: Float = dpToPx(10f)

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues,
        endValues: TransitionValues
    ): Animator {
        val directionX = initialDirectionX(position)
        val directionY = initialDirectionY(position)

        view.translationX =
            directionX * if (percentage != null) view.width * percentage else defaultTranslation
        view.translationY =
            directionY * if (percentage != null) view.height * percentage else defaultTranslation

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
                directionX * if (percentage != null) view.width * percentage else defaultTranslation
            ),
            PropertyValuesHolder.ofFloat(
                View.TRANSLATION_Y,
                0f,
                directionY * if (percentage != null) view.height * percentage else defaultTranslation
            )
        )
    }

    private fun initialDirectionX(
        position: DivTooltip.Position
    ) = when (position) {
        DivTooltip.Position.TOP_LEFT, DivTooltip.Position.LEFT, DivTooltip.Position.BOTTOM_LEFT ->
            1
        DivTooltip.Position.TOP_RIGHT, DivTooltip.Position.RIGHT, DivTooltip.Position.BOTTOM_RIGHT ->
            -1
        DivTooltip.Position.TOP, DivTooltip.Position.BOTTOM -> 0
    }

    private fun initialDirectionY(
        position: DivTooltip.Position
    ) = when (position) {
        DivTooltip.Position.TOP_LEFT, DivTooltip.Position.TOP, DivTooltip.Position.TOP_RIGHT ->
            1
        DivTooltip.Position.BOTTOM_LEFT, DivTooltip.Position.BOTTOM, DivTooltip.Position.BOTTOM_RIGHT ->
            -1
        DivTooltip.Position.LEFT, DivTooltip.Position.RIGHT -> 0
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
