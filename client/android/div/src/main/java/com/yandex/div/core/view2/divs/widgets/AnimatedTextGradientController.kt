package com.yandex.div.core.view2.divs.widgets

import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Shader
import android.view.Choreographer
import android.view.View
import com.yandex.div.core.util.AnimatedTextGradientMath
import com.yandex.div.core.view2.divs.availableWidth
import com.yandex.div.internal.drawable.LinearGradientDrawable
import com.yandex.div.internal.drawable.RadialGradientDrawable
import kotlin.math.ceil

internal class AnimatedTextGradientController(
    private val view: DivLineHeightTextView,
    private val choreographer: Choreographer = Choreographer.getInstance(),
) : Choreographer.FrameCallback {

    private val shaderMatrix = Matrix()
    private val translation = PointF()
    private var gradientData: AnimatedTextGradientData? = null
    private var shader: Shader? = null
    private var shaderWidth = 0
    private var shaderHeight = 0
    private var textWidth = 0
    private var textHeight = 0
    private var textLeft = 0f
    private var animationsEnabled = true
    private var isRunning = false
    private var lastPhase = STATIC_PHASE

    fun update(gradientData: AnimatedTextGradientData, animationsEnabled: Boolean) {
        val shouldRebuildShader = this.gradientData?.gradient != gradientData.gradient
        this.gradientData = gradientData
        this.animationsEnabled = animationsEnabled
        if (shouldRebuildShader) {
            shader = null
        }
        updateTextBounds()
        updateShader(STATIC_PHASE)
        updateRunningState()
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        if (animationsEnabled == enabled) {
            return
        }
        animationsEnabled = enabled
        if (!enabled) {
            stop()
            updateShader(STATIC_PHASE)
        } else {
            updateRunningState()
        }
        view.invalidate()
    }

    fun invalidate() {
        shader = null
        updateTextBounds()
        updateShader(lastPhase)
    }

    fun onAttachedToWindow() {
        updateTextBounds()
        if (gradientData != null) {
            updateShader(STATIC_PHASE)
        }
        updateRunningState()
    }

    fun onVisibilityChanged() {
        updateRunningState()
    }

    fun onDetachedFromWindow() {
        stop()
        shader = null
        clearShader()
    }

    fun release() {
        stop()
        gradientData = null
        shader = null
        clearShader()
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!isRunning) {
            return
        }
        val gradientData = gradientData
        if (gradientData == null || !canRunAnimations()) {
            stop()
            return
        }

        val phase = AnimatedTextGradientMath.phase(frameTimeNanos / NANOS_IN_MILLISECOND, gradientData.duration)
        updateShader(phase)
        view.postInvalidateOnAnimation()
        choreographer.postFrameCallback(this)
    }

    private fun updateRunningState() {
        if (gradientData != null && canRunAnimations()) {
            start()
        } else {
            stop()
        }
    }

    private fun canRunAnimations(): Boolean {
        return animationsEnabled && (gradientData?.duration ?: 0L) > 0L
            && view.isShown && view.windowVisibility == View.VISIBLE
    }

    private fun start() {
        if (isRunning) {
            return
        }
        isRunning = true
        choreographer.postFrameCallback(this)
    }

    private fun stop() {
        if (!isRunning) {
            return
        }
        isRunning = false
        choreographer.removeFrameCallback(this)
    }

    private fun updateShader(phase: Float) {
        val gradientData = gradientData ?: return
        lastPhase = phase
        val width = textWidth
        val height = textHeight
        if (width <= 0 || height <= 0) {
            view.paint.shader = null
            return
        }

        ensureShader(gradientData.gradient, width, height)
        val shader = shader ?: return
        shaderMatrix.reset()
        if (phase != STATIC_PHASE && animationsEnabled) {
            when (val gradient = gradientData.gradient) {
                is AnimatedTextGradientData.Gradient.Linear -> {
                    AnimatedTextGradientMath.linearTranslation(
                        angleDegrees = gradient.angle,
                        width = width.toFloat(),
                        height = height.toFloat(),
                        phase = phase,
                        result = translation,
                    )
                    shaderMatrix.setTranslate(translation.x, translation.y)
                }
                is AnimatedTextGradientData.Gradient.Radial -> {
                    shaderMatrix.setTranslate(
                        AnimatedTextGradientMath.radialTranslation(width.toFloat(), phase),
                        0f,
                    )
                }
            }
        }
        shaderMatrix.postTranslate(textLeft, 0f)
        shader.setLocalMatrix(shaderMatrix)
        view.paint.shader = shader
        view.invalidate()
    }

    private fun ensureShader(
        gradient: AnimatedTextGradientData.Gradient,
        width: Int,
        height: Int,
    ) {
        if (shader != null && shaderWidth == width && shaderHeight == height) {
            return
        }

        shaderWidth = width
        shaderHeight = height
        shader = when (gradient) {
            is AnimatedTextGradientData.Gradient.Linear -> LinearGradientDrawable.createLinearGradient(
                angle = gradient.angle,
                colors = gradient.colormap.colors,
                positions = gradient.colormap.positions,
                width = width,
                height = height,
            )
            is AnimatedTextGradientData.Gradient.Radial -> RadialGradientDrawable.createRadialGradient(
                radius = gradient.radius,
                centerX = gradient.centerX,
                centerY = gradient.centerY,
                colors = gradient.colormap.colors,
                positions = gradient.colormap.positions,
                width = width,
                height = height,
            )
        }
    }

    private fun clearShader() {
        view.paint.shader = null
        shaderMatrix.reset()
        view.invalidate()
    }

    private fun updateTextBounds() {
        textHeight = view.height - view.paddingTop - view.paddingBottom
        val layout = view.layout
        if (layout == null) {
            textLeft = 0f
            textWidth = minOf(view.availableWidth, ceil(view.paint.measureText(view.text.toString())).toInt())
            return
        }
        if (layout.lineCount == 0) {
            textLeft = 0f
            textWidth = 0
            return
        }

        textLeft = layout.getLineLeft(0)
        var right = layout.getLineRight(0)
        for (line in 1 until layout.lineCount) {
            textLeft = minOf(textLeft, layout.getLineLeft(line))
            right = maxOf(right, layout.getLineRight(line))
        }
        textWidth = minOf(view.availableWidth, ceil(right - textLeft).toInt())
    }

    private companion object {
        const val NANOS_IN_MILLISECOND = 1_000_000L
        const val STATIC_PHASE = -1f
    }
}
