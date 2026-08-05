package com.yandex.div.backdrop

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver.OnDrawListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.ViewTreeObserver.OnScrollChangedListener
import androidx.core.view.isVisible
import com.yandex.div.backdrop.graphics.CanvasBackdropLayer
import com.yandex.div.backdrop.graphics.PlainRimHighlightLayer
import com.yandex.div.backdrop.graphics.ReflectionRimHighlightLayer
import com.yandex.div.backdrop.graphics.RenderNodeBackdropLayer
import com.yandex.div.core.Disposable
import com.yandex.div.internal.view.onPreDrawListener

internal class BackdropEffectDrawable(
    private val view: View,
    private val backdropViewProvider: BackdropViewProvider,
    private val backdropWatcher: BackdropWatcher,
) : Drawable(), Disposable {

    val density: Float
        get() = view.resources.displayMetrics.density

    private val backdropLayer = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        CanvasBackdropLayer(view)
    } else {
        RenderNodeBackdropLayer(view)
    }

    private var isBackdropValid = false
    private var isBackdropCaptured = false

    private val backdropOnPreDrawListener = onPreDrawListener {
        if (isBackdropCaptured) {
            return@onPreDrawListener
        }

        val backdropView = backdropViewProvider.backdropView ?: return@onPreDrawListener
        isBackdropValid = !backdropWatcher.isBackdropInvalidated(backdropView) && isBackdropValid

        if (isBackdropValid) {
            isBackdropValid = false
            return@onPreDrawListener
        }

        if (view.isVisible) {
            view.visibility = INVISIBLE
            try {
                backdropLayer.capture(backdropView)
            } finally {
                view.visibility = VISIBLE
                isBackdropValid = true
                isBackdropCaptured = true
            }
        }
    }

    private val viewUpdateListener = object : OnGlobalLayoutListener, OnScrollChangedListener, OnDrawListener {

        override fun onGlobalLayout() {
            isBackdropValid = false
        }

        override fun onScrollChanged() {
            isBackdropValid = false
        }

        override fun onDraw() {
            isBackdropCaptured = false
        }
    }

    private val rimHighlightLayer = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        PlainRimHighlightLayer()
    } else {
        ReflectionRimHighlightLayer()
    }

    private val backdropPaint = Paint()

    fun setCornerRadius(radius: Float) {
        backdropLayer.setCornerRadius(radius)
        rimHighlightLayer.setCornerRadius(radius)
        invalidateSelf()
    }

    fun setCornerRadii(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float) {
        backdropLayer.setCornerRadii(topLeft, topRight, bottomRight, bottomLeft)
        rimHighlightLayer.setCornerRadii(topLeft, topRight, bottomRight, bottomLeft)
        invalidateSelf()
    }

    fun setBlurEffect(radius: Float) {
        backdropLayer.setBlurEffect(radius)
        invalidateSelf()
    }

    fun setRefractionEffect(height: Float, strength: Float, chromaticAberration: Boolean) {
        backdropLayer.setRefractionEffect(height, strength, chromaticAberration)
        invalidateSelf()
    }

    fun setColorAdjustment(brightness: Float, contrast: Float, saturation: Float) {
        backdropLayer.setColorAdjustment(brightness, contrast, saturation)
        invalidateSelf()
    }

    fun setRimHighlightEffect(width: Float, alpha: Float, color: Int, angle: Float, falloff: Float) {
        rimHighlightLayer.setRimHighlightEffect(width, alpha, color, angle, falloff)
        invalidateSelf()
    }

    fun setRimHighlightEffect(width: Float, alpha: Float, intensity: Float, angle: Float) {
        rimHighlightLayer.setRimHighlightEffect(width, alpha, intensity, angle)
        invalidateSelf()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        rimHighlightLayer.setSize(bounds.width().toFloat(), bounds.height().toFloat())
    }

    override fun draw(canvas: Canvas) {
        backdropLayer.draw(canvas, backdropPaint)
        rimHighlightLayer.draw(canvas, backdropPaint)
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {
        backdropPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        backdropPaint.colorFilter = colorFilter
    }

    fun attachToViews() {
        val backdropView = backdropViewProvider.backdropView
        backdropView?.viewTreeObserver?.apply {
            addOnPreDrawListener(backdropOnPreDrawListener)
            addOnGlobalLayoutListener(viewUpdateListener)
            addOnScrollChangedListener(viewUpdateListener)
            addOnDrawListener(viewUpdateListener)
        }
        view.viewTreeObserver.apply {
            addOnGlobalLayoutListener(viewUpdateListener)
            addOnScrollChangedListener(viewUpdateListener)
            addOnDrawListener(viewUpdateListener)
        }
    }

    fun detachFromViews() {
        val backdropView = backdropViewProvider.backdropView
        backdropView?.viewTreeObserver?.apply {
            removeOnPreDrawListener(backdropOnPreDrawListener)
            removeOnGlobalLayoutListener(viewUpdateListener)
            removeOnScrollChangedListener(viewUpdateListener)
            removeOnDrawListener(viewUpdateListener)
        }
        view.viewTreeObserver.apply {
            removeOnGlobalLayoutListener(viewUpdateListener)
            removeOnScrollChangedListener(viewUpdateListener)
            removeOnDrawListener(viewUpdateListener)
        }
        backdropLayer.recycle()
    }

    override fun close() {
        detachFromViews()
        backdropLayer.close()
    }
}
