package com.yandex.div.rive

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.MainThread
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.RiveArtboardRenderer
import app.rive.runtime.kotlin.core.Alignment
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.Loop

/**
 * Wrapper over RiveAnimationView.
 * RiveAnimationView looks like "one shot" view. After calling onDetachedFromWindow(), C++ references are
 * removed. These objects are accessed in onAttachedToWindow(), which causes a crash.
 * To work around this behavior, DivRiveContainer is used.
 */
internal class DivRiveContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {
    private var animationInfo: RiveAnimationInfo? = null
    private fun getRiveView() = getChildAt(0) as? InternalRiveView

    @MainThread
    fun setAnimationBytes(
        bytes: ByteArray,
        fit: Fit,
        alignment: Alignment,
        loop: Loop,
    ) {
        val info = RiveAnimationInfo(bytes, fit, alignment, loop)
        setAnimationInfo(info)
    }

    private fun setAnimationInfo(info: RiveAnimationInfo) {
        this.animationInfo = info
        val view = getRiveView() ?: InternalRiveView(context).also {
            addView(it, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        view.setAnimationBytes(info.animationBytes, info.fit, info.alignment, info.loop)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animationInfo?.let { setAnimationInfo(it) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        getRiveView()?.let { removeView(it) }
    }

    fun release() {
        animationInfo = null
        getRiveView()?.let { it.stop(); removeView(it) }
    }

    class RiveAnimationInfo(
        val animationBytes: ByteArray,
        val fit: Fit,
        val alignment: Alignment,
        val loop: Loop
    )
}

internal class InternalRiveView(context: Context) : RiveAnimationView(context) {
    override val renderer: RiveArtboardRenderer = InternalArtboardRenderer()

    @Deprecated("Deprecated in Java")
    override fun setBackgroundDrawable(background: Drawable?) {}

    @MainThread
    fun setAnimationBytes(
        bytes: ByteArray,
        fit: Fit,
        alignment: Alignment,
        loop: Loop,
    ) {
        setRiveBytes(
            bytes,
            fit = fit,
            alignment = alignment,
            loop = loop,
            autoplay = true
        )
        (renderer as? InternalArtboardRenderer)?.setHasAnimation(true)
        // handle 'wrap_content' size
        requestLayout()
    }

    internal class InternalArtboardRenderer : RiveArtboardRenderer() {

        private var hasAnimation = false

        @MainThread
        fun setHasAnimation(hasAnimation: Boolean) {
            this.hasAnimation = hasAnimation
        }

        // Sometimes advance() method is called from cpp library before any animation is set.
        // If there is no animation, this method will cause the Surface to be released.
        override fun advance(elapsed: Float) {
            if (!hasAnimation) return
            super.advance(elapsed)
        }
    }
}
