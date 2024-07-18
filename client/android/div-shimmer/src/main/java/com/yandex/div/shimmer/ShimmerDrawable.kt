package com.yandex.div.shimmer

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.view.animation.LinearInterpolator
import kotlin.math.abs
import kotlin.math.tan

/**
 * Drawable that draws animated [LinearGradient]. Animation starts automatically,
 * but in can be stopped and resumed using [pauseShimmer] and [resumeShimmer] repectfuly.
 *
 * @param animationStartTime can be used to synchronize it with other animations.
 * It should be set to [SystemClock.uptimeMillis] of the start of the first animation.
 */
class ShimmerDrawable(
        initialConfig: Config,
        private val animationStartTime: Long
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
    private val shimmerPaint = Paint().apply {
        xfermode = PorterDuffXfermode(
                PorterDuff.Mode.SRC_IN
        )
        isAntiAlias = true
        updateShader()
    }
    private val drawRect = Rect()
    private val shaderMatrix = Matrix()
    private var valueAnimator: ValueAnimator = createValueAnimator()
    private var isStopped = false

    override fun start() {
        isStopped = false
        if (!valueAnimator.isStarted) {
            valueAnimator.start()
        }
    }

    override fun stop() {
        isStopped = true
        valueAnimator.cancel()
    }

    override fun isRunning() = valueAnimator.isRunning

    public override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        drawRect.set(bounds)
        updateShader()
    }

    override fun draw(canvas: Canvas) {
        val shimmerShader = shimmerPaint.shader ?: return
        var rotate = config.angle
        fun translateHeight(): Float {
            val tan = tan(Math.toRadians(rotate)).toFloat()
            return drawRect.height() + abs(tan) * drawRect.width()
        }

        fun translateWidth(): Float {
            val tan = tan(Math.toRadians(rotate)).toFloat()
            return drawRect.width() + abs(tan) * drawRect.height()
        }

        val animatedValue = valueAnimator.animatedValue as Float

        val dx: Float
        val dy: Float

        when (normalizeAngle(config.angle.toInt())) {
            in 45 until  135 -> {
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
        shimmerShader.setLocalMatrix(shaderMatrix)
        if (!isStopped) {
            start()
        }
        canvas.drawRect(drawRect, shimmerPaint)
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
        return PixelFormat.OPAQUE
    }

    private fun offsetSymmetric(start: Float, percent: Float): Float {
        val end = -start
        return start + (end - start) * percent
    }

    private fun updateValueAnimator() {
        valueAnimator.removeAllUpdateListeners()
        valueAnimator.cancel()
        valueAnimator = createValueAnimator()
    }

    private fun updateShader() {
        val bounds = bounds
        val boundsWidth = bounds.width()
        val boundsHeight = bounds.height()
        if (boundsWidth == 0 || boundsHeight == 0) {
            return
        }
        val normalizedAngle = normalizeAngle(config.angle.toInt())
        val vertical = normalizedAngle in 45 until 135 || normalizedAngle in 225 until 315
        val endX = if (vertical) 0f else boundsWidth.toFloat()
        val endY = if (vertical) boundsHeight.toFloat() else 0f
        shimmerPaint.shader = LinearGradient(
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
            interpolator = LinearInterpolator()
            repeatMode = ValueAnimator.RESTART
            startDelay = config.startDelay
            repeatCount = ValueAnimator.INFINITE
            duration = config.duration + config.repeatDelay
            currentPlayTime = SystemClock.uptimeMillis() - animationStartTime
            addUpdateListener(updateListener)
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
     * Configuration class for [ShimmerDrawable]
     */
    class Config constructor(
            val colors: IntArray,
            val locations: FloatArray,
            val angle: Double,
            val duration: Long,
            val repeatDelay: Long = 0,
            val startDelay: Long = 0,
    )
}
