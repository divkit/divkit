package com.yandex.div.core.view2.animations

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.view.children
import com.yandex.div.R

/**
 * Owns only the legacy animations started by one action binding.
 * Android can hit-test a hidden view while it retains an Animation, including after it has ended.
 */
internal class TouchAnimationBinding(
    private val view: View,
    private var directAnimation: Animation?,
    private var reverseAnimation: Animation?,
) : TouchAnimationListener, View.OnAttachStateChangeListener {

    private var active = false
    private var disposed = false
    private var ownedAnimation: Animation? = null

    init {
        view.setTag(R.id.div_touch_animation_binding, this)
        view.addOnAttachStateChangeListener(this)
    }

    override fun onTouch(target: View, event: MotionEvent) {
        if (disposed) {
            return
        }
        val canReceiveTouch = target.isAttachedToWindow && target.isShown && target.isEnabled
        val canHandleClick = target.isClickable && target.hasOnClickListeners()
        if (!canReceiveTouch || !canHandleClick) {
            cancel()
            return
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                active = true
                directAnimation?.let(::startAnimation)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (active) {
                    active = false
                    reverseAnimation?.let(::startAnimation)
                }
            }
        }
    }

    fun updateAnimations(directAnimation: Animation?, reverseAnimation: Animation?) {
        this.directAnimation = directAnimation
        this.reverseAnimation = reverseAnimation
    }

    private fun startAnimation(animation: Animation) {
        ownedAnimation = animation
        view.startAnimation(animation)
    }

    fun cancel() {
        active = false
        val animation = view.animation
        if (animation != null && animation === ownedAnimation) {
            view.clearAnimation()
        }
        ownedAnimation = null
    }

    fun dispose() {
        cancel()
        disposed = true
        view.removeOnAttachStateChangeListener(this)
        if (view.getTag(R.id.div_touch_animation_binding) === this) {
            view.setTag(R.id.div_touch_animation_binding, null)
        }
    }

    override fun onViewAttachedToWindow(view: View) = Unit

    override fun onViewDetachedFromWindow(view: View) = cancel()
}

internal fun View.disposeTouchAnimationBinding() {
    (getTag(R.id.div_touch_animation_binding) as? TouchAnimationBinding)?.dispose()
}

internal fun View.cancelTouchAnimations() {
    cancelTouchAnimation()
    if (this is ViewGroup) {
        children.forEach { it.cancelTouchAnimations() }
    }
}

internal fun View.cancelTouchAnimation() {
    (getTag(R.id.div_touch_animation_binding) as? TouchAnimationBinding)?.cancel()
}
