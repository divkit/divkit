// Copyright 2024 Yandex LLC. All rights reserved.

package com.yandex.div.shine

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.abs
import kotlin.math.tan

/**
 * Drawable that draws animated [LinearGradient].
 */
class ShineDrawable(
    private var sourceBitmap: Bitmap? = null,
    initialConfig: Config
) : Drawable(), Animatable {
    var config: Config = initialConfig
        set(value) {
            field = value
            updateShader()
            updateValueAnimator()
            invalidateSelf()
        }

    private val updateListener = AnimatorUpdateListener {
        if (callback == null) {
            valueAnimator.cancel()
        }
        invalidateSelf()
    }

    private val animatorListener = object: Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {
            config.onCycleActionPerformer()
        }
        override fun onAnimationRepeat(p0: Animator) {
            config.onCycleActionPerformer()
        }
        override fun onAnimationEnd(p0: Animator) = Unit
        override fun onAnimationCancel(p0: Animator) = Unit
    }

    private val gradientPaint = Paint().apply {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            xfermode = PorterDuffXfermode(
                PorterDuff.Mode.SRC_ATOP
            )
        }
        isAntiAlias = true
        updateShader()
    }
    private val regularPaint = Paint()
    private val drawRect = RectF()
    private val shaderMatrix = Matrix()
    private var valueAnimator: ValueAnimator = createValueAnimator()

    override fun start() {
        if (!config.enabled) return
        if (valueAnimator.isStarted) return

        valueAnimator.start()
    }

    override fun stop() {
        valueAnimator.removeAllUpdateListeners()
        valueAnimator.removeAllListeners()
        valueAnimator.cancel()
    }

    override fun isRunning() = valueAnimator.isRunning

    public override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        drawRect.set(bounds)
        updateShader()
    }

    override fun draw(canvas: Canvas) {
        val gradientShader = gradientPaint.shader ?: return
        var rotate = config.angle

        fun translateHeight(): Float {
            val tan = tan(Math.toRadians(rotate)).toFloat()
            return drawRect.height() + abs(tan) * drawRect.width()
        }

        fun translateWidth(): Float {
            val tan = tan(Math.toRadians(rotate)).toFloat()
            return drawRect.width() + abs(tan) * drawRect.height()
        }

        fun offsetSymmetric(start: Float, percent: Float): Float {
            val end = -start
            return start + (end - start) * percent
        }

        val animatedValue = valueAnimator.animatedValue as Float

        val dx: Float
        val dy: Float

        when (normalizeAngle(config.angle.toInt())) {
            in 45 until 135 -> {
                rotate -= 90
                dx = 0f
                dy = offsetSymmetric(-translateHeight(), animatedValue)
            }
            in 225 until 315 -> {
                rotate -= 270
                dx = 0f
                dy = offsetSymmetric(translateHeight(), animatedValue)
            }
            in 135 until 225 -> {
                rotate -= 180
                dx = offsetSymmetric(translateWidth(), animatedValue)
                dy = 0f
            }
            else -> { // 315 until 45
                dx = offsetSymmetric(-translateWidth(), animatedValue)
                dy = 0f
            }
        }

        shaderMatrix.reset()
        shaderMatrix.setRotate(rotate.toFloat(), drawRect.width() / 2f, drawRect.height() / 2f)
        shaderMatrix.preTranslate(dx, dy)
        gradientShader.setLocalMatrix(shaderMatrix)

        sourceBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, null, drawRect, regularPaint)
        }
        config.tintColor?.let { color ->
            canvas.drawColor(color, PorterDuff.Mode.SRC_ATOP)
        }
        canvas.drawRect(drawRect, gradientPaint)
    }

    override fun setAlpha(alpha: Int) {
        // No-op
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // No-op
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    fun updateSourceBitmap(bitmap: Bitmap){
        sourceBitmap = bitmap
    }

    private fun updateValueAnimator() {
        stop()
        valueAnimator = createValueAnimator()
        start()
    }

    private fun updateShader() {
        val bounds = bounds
        val boundsWidth = bounds.width()
        val boundsHeight = bounds.height()
        if (boundsWidth == 0 || boundsHeight == 0) {
            return
        }
        if (config.colors.isEmpty() || config.locations.isEmpty()) return
        if (config.colors.size != config.locations.size) return
        val normalizedAngle = normalizeAngle(config.angle.toInt())
        val vertical = normalizedAngle in 45 until 135 || normalizedAngle in 225 until 315
        val endX = if (vertical) 0f else boundsWidth.toFloat()
        val endY = if (vertical) boundsHeight.toFloat() else 0f
        gradientPaint.shader = LinearGradient(
            0f,
            0f,
            endX,
            endY,
            config.colors,
            config.locations,
            Shader.TileMode.CLAMP
        )
    }

    private fun createValueAnimator(): ValueAnimator {
        val endValue = 1f + (config.repeatDelay / config.duration).toFloat()
        return ValueAnimator.ofFloat(0f, endValue).apply {
            interpolator = AccelerateDecelerateInterpolator()
            repeatMode = ValueAnimator.RESTART
            startDelay = config.startDelay
            duration = config.duration + config.repeatDelay
            repeatCount = config.repeatCount
            addUpdateListener(updateListener)
            addListener(animatorListener)
        }
    }

    private fun normalizeAngle(angle: Int): Int {
        var normailzed = angle
        if (normailzed < 0) {
            normailzed += 360
        }
        return normailzed % 360
    }

    /**
     * Configuration class for [ShineDrawable]
     */
    class Config(
        val enabled: Boolean,
        val colors: IntArray,
        val locations: FloatArray,
        val angle: Double,
        val duration: Long,
        val repeatDelay: Long,
        val startDelay: Long = 0,
        val repeatCount: Int = ValueAnimator.INFINITE,
        val tintColor: Int? = null,
        val onCycleActionPerformer: () -> Unit,
    )
}
