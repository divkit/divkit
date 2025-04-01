package com.yandex.div.shine

import android.graphics.drawable.Drawable
import android.view.View
import com.yandex.div.core.widget.DivViewDelegate
import java.lang.ref.WeakReference


internal class ShineAnimationController(
    private val transformer: ShineImageTransformer,
    private val viewReference: WeakReference<View>,
) : DivViewDelegate, PauseShineObserver {

    private var isPausedByObserver = false
    private var isPausedByDelegate = false

    internal val isPaused: Boolean
        get() = isPausedByObserver || isPausedByDelegate

    private fun invalidateAnimationPause() {
        if (isPaused) {
            transformer.pauseAnimation()
        } else {
            transformer.resumeAnimation()
        }
    }

    private fun invalidateAnimationPauseByDelegate() {
        val view = viewReference.get()
        if (view == null) {
            transformer.clear()
            return
        }

        if (isPausedByDelegate && view.isShown && view.isAttachedToWindow) {
            isPausedByDelegate = false
        } else if (!isPausedByDelegate && (!view.isShown || !view.isAttachedToWindow)) {
            isPausedByDelegate = true
        }
        invalidateAnimationPause()
    }

    override fun invalidateDrawable(dr: Drawable): Drawable = dr

    override fun onVisibilityChanged(changedView: View, visibility: Int): Boolean {
        invalidateAnimationPauseByDelegate()
        return true
    }

    override fun onAttachedToWindow() = invalidateAnimationPauseByDelegate()
    override fun onDetachedFromWindow() = invalidateAnimationPauseByDelegate()
    override fun buildDrawingCache(autoScale: Boolean) = Unit
    override fun unscheduleDrawable(who: Drawable?) = Unit

    override fun onPause() {
        isPausedByObserver = true
        invalidateAnimationPause()
    }

    override fun onResume() {
        isPausedByObserver = false
        invalidateAnimationPause()
    }
}
