package com.yandex.div.internal.widget.slider

import android.animation.Animator

internal class SliderThumbAnimatorListener(
    private val onAnimationEnd: (Boolean) -> Unit
) : Animator.AnimatorListener {

    private var hasCanceled = false

    override fun onAnimationEnd(animation: Animator) = onAnimationEnd(hasCanceled)

    override fun onAnimationCancel(animation: Animator) {
        hasCanceled = true
    }

    override fun onAnimationStart(animation: Animator) {
        hasCanceled = false
    }

    override fun onAnimationRepeat(animation: Animator) = Unit
}
