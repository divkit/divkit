package com.yandex.div.lottie

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.View.LAYER_TYPE_HARDWARE
import android.view.View.LAYER_TYPE_SOFTWARE
import androidx.annotation.FloatRange
import androidx.annotation.MainThread
import com.airbnb.lottie.*
import com.yandex.div.core.widget.DivViewDelegate
import com.yandex.div.core.widget.LoadableImageView

/**
 * LottieAnimationView implements only happy flow scenario, so this is custom view
 * derived both [LottieAnimationView] lottie animation rendering implementation and [DivGifImageView]
 * aspect, scale, image stub/preview logic.
 */
internal class LottieController(
    private val gifImageView: LoadableImageView,
) : DivViewDelegate {

    // LottieAnimationView fields
    private val lottieDrawable = LottieDrawable()
    private var composition: LottieComposition? = null
    private var isInitialized: Boolean
    private var renderMode = RenderMode.AUTOMATIC
    private var ignoreUnschedule: Boolean = false
    private var playAnimationWhenShown = false
    private var wasAnimatingWhenNotShown = false
    private var wasAnimatingWhenDetached = false
    private var autoPlay = false
    private var buildDrawingCacheDepth = 0

    // Div Lottie Extension fields
    var data: LottieData? = null

    private var onEndListener: (() -> Unit)? = null

    private val animatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) = Unit
        override fun onAnimationEnd(animation: Animator) {
            onEndListener?.invoke()
        }
        override fun onAnimationCancel(animation: Animator) = Unit
        override fun onAnimationRepeat(animation: Animator) = Unit
    }

    // From here goes LottieAnimationView implementations. Logic that do not serve LottieComposition drawing
    // purposes was cut off, everything else left untouched.
    init {
        enableMergePathsForKitKatAndAbove(true)
        enableOrDisableHardwareLayer()
        isInitialized = true
    }

    override fun unscheduleDrawable(who: Drawable?) {
        if (!ignoreUnschedule && who === lottieDrawable && lottieDrawable.isAnimating) {
            pauseAnimation()
        } else if (!ignoreUnschedule && who is LottieDrawable && who.isAnimating) {
            who.pauseAnimation()
        }
    }

    override fun invalidateDrawable(dr: Drawable): Drawable {
        return if (gifImageView.drawable === lottieDrawable) lottieDrawable else dr
    }

    fun setEndListener(listener: (() -> Unit)?) {
        onEndListener = listener
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int): Boolean {
        if (!isInitialized) {
            return true
        }
        if (gifImageView.isShown) {
            if (wasAnimatingWhenNotShown) {
                resumeAnimation()
            } else if (playAnimationWhenShown) {
                playAnimation()
            }
            wasAnimatingWhenNotShown = false
            playAnimationWhenShown = false
        } else {
            if (isAnimating()) {
                pauseAnimation()
                wasAnimatingWhenNotShown = true
            }
        }
        return true
    }

    override fun onAttachedToWindow() {
        lottieDrawable.addAnimatorListener(animatorListener)
        if (!gifImageView.isInEditMode && (autoPlay || wasAnimatingWhenDetached)) {
            playAnimation()
            autoPlay = false
            wasAnimatingWhenDetached = false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onVisibilityChanged(gifImageView, gifImageView.visibility)
        }
    }

    override fun onDetachedFromWindow() {
        lottieDrawable.removeAnimatorListener(animatorListener)
        if (isAnimating()) {
            cancelAnimation()
            wasAnimatingWhenDetached = true
        }
    }

    override fun buildDrawingCache(autoScale: Boolean) {
        buildDrawingCacheDepth++
        if (buildDrawingCacheDepth == 1
            && gifImageView.width > 0
            && gifImageView.height > 0
            && gifImageView.layerType == LAYER_TYPE_SOFTWARE
            && gifImageView.getDrawingCache(autoScale) == null
        ) {
            setRenderMode(RenderMode.HARDWARE)
        }
        buildDrawingCacheDepth--
    }

    fun setIgnoreDisabledSystemAnimations(ignore: Boolean) {
        lottieDrawable.setIgnoreDisabledSystemAnimations(ignore)
    }

    fun enableMergePathsForKitKatAndAbove(enable: Boolean) {
        lottieDrawable.enableMergePathsForKitKatAndAbove(enable)
    }

    fun setImageAssetsFolder(imageAssetsFolder: String?) {
        lottieDrawable.setImagesAssetsFolder(imageAssetsFolder)
    }

    fun setComposition(composition: LottieComposition) {
        lottieDrawable.callback = gifImageView
        gifImageView.externalImage = lottieDrawable
        this.composition = composition
        ignoreUnschedule = true
        val isNewComposition = lottieDrawable.setComposition(composition)
        ignoreUnschedule = false
        enableOrDisableHardwareLayer()
        if (gifImageView.drawable === lottieDrawable && !isNewComposition) {
            return
        } else if (!isNewComposition) {
            setLottieDrawable()
        }
        onVisibilityChanged(gifImageView, gifImageView.visibility)
        gifImageView.requestLayout()
    }

    fun clearComposition() {
        composition = null
        lottieDrawable.clearComposition()
        gifImageView.externalImage = null
        gifImageView.setImageDrawable(null)
    }

    @MainThread
    fun playAnimation() {
        if (gifImageView.isShown) {
            lottieDrawable.playAnimation()
            enableOrDisableHardwareLayer()
        } else {
            playAnimationWhenShown = true
        }
    }

    @MainThread
    fun resumeAnimation() {
        if (gifImageView.isShown) {
            lottieDrawable.resumeAnimation()
            enableOrDisableHardwareLayer()
        } else {
            playAnimationWhenShown = false
            wasAnimatingWhenNotShown = true
        }
    }

    @FloatRange(from = 0.0, to = 1.0)
    fun getProgress(): Float = lottieDrawable.progress

    fun setRepeatMode(@LottieDrawable.RepeatMode mode: Int) {
        lottieDrawable.repeatMode = mode
    }

    fun setMinFrame(minFrame: Int) {
        lottieDrawable.setMinFrame(minFrame)
    }

    fun setMaxFrame(maxFrame: Int) {
        lottieDrawable.setMaxFrame(maxFrame)
    }

    @LottieDrawable.RepeatMode
    fun getRepeatMode(): Int {
        return lottieDrawable.repeatMode
    }

    fun setRepeatCount(count: Int) {
        lottieDrawable.repeatCount = count
    }

    fun getRepeatCount(): Int {
        return lottieDrawable.repeatCount
    }

    fun isAnimating(): Boolean = lottieDrawable.isAnimating

    @MainThread
    fun cancelAnimation() {
        wasAnimatingWhenDetached = false
        wasAnimatingWhenNotShown = false
        playAnimationWhenShown = false
        lottieDrawable.cancelAnimation()
        enableOrDisableHardwareLayer()
    }

    @MainThread
    fun pauseAnimation() {
        autoPlay = false
        wasAnimatingWhenDetached = false
        wasAnimatingWhenNotShown = false
        playAnimationWhenShown = false
        lottieDrawable.pauseAnimation()
        enableOrDisableHardwareLayer()
    }

    fun setSafeMode(safeMode: Boolean) {
        lottieDrawable.setSafeMode(safeMode)
    }

    fun setRenderMode(renderMode: RenderMode) {
        this.renderMode = renderMode
        enableOrDisableHardwareLayer()
    }

    fun setApplyingOpacityToLayersEnabled(isApplyingOpacityToLayersEnabled: Boolean) {
        lottieDrawable.isApplyingOpacityToLayersEnabled = isApplyingOpacityToLayersEnabled
    }

    @SuppressLint("RestrictedApi")
    private fun enableOrDisableHardwareLayer() {
        var layerType = LAYER_TYPE_SOFTWARE
        val composition = this.composition
        when (renderMode) {
            RenderMode.HARDWARE -> layerType = LAYER_TYPE_HARDWARE
            RenderMode.SOFTWARE -> layerType = LAYER_TYPE_SOFTWARE
            RenderMode.AUTOMATIC -> {
                var useHardwareLayer = true
                if (composition != null && composition.hasDashPattern() && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    useHardwareLayer = false
                } else if (composition != null && composition.maskAndMatteCount > 4) {
                    useHardwareLayer = false
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N || Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                    useHardwareLayer = false
                }
                layerType = if (useHardwareLayer) LAYER_TYPE_HARDWARE else LAYER_TYPE_SOFTWARE
            }
        }
        if (layerType != gifImageView.layerType) {
            gifImageView.setLayerType(layerType, null)
        }
    }

    private fun setLottieDrawable() {
        val wasAnimating = isAnimating()
        // Set the drawable to null first because the underlying LottieDrawable's intrinsic bounds can change
        // if the composition changes.
        gifImageView.setImageDrawable(null)
        gifImageView.setImageDrawable(lottieDrawable)
        gifImageView.imageLoaded()
        if (wasAnimating) {
            // This is necessary because lottieDrawable will get unscheduled and canceled when the drawable is set to null.
            lottieDrawable.resumeAnimation()
        }
    }
}
