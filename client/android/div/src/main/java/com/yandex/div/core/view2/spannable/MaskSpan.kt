package com.yandex.div.core.view2.spannable

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Spanned
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.internal.core.TextRangeParticleSystem
import com.yandex.div.internal.spannable.PositionAwareReplacementSpan

internal class MaskSpan(
    private val mask: MaskData,
    private val hostView: DivLineHeightTextView?
) : PositionAwareReplacementSpan() {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private var lastWidth = -1f
    private var lastHeight = -1f
    private var particleSystem: TextRangeParticleSystem? = null
    private var active = false

    override fun adjustSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int = paint.measureText(text, start, end).toInt()

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        textPaint: Paint
    ) {
        val spanWidth = textPaint.measureText(text, start, end)
        val spanHeight = (bottom - top).toFloat()

        when (val mask = mask) {
            is MaskData.Solid -> {
                fillPaint.color = mask.color
                canvas.drawRect(x, top.toFloat(), x + spanWidth, bottom.toFloat(), fillPaint)
                active = false
            }

            is MaskData.Particles -> {
                if (spanWidth != lastWidth || spanHeight != lastHeight || particleSystem == null) {
                    particleSystem = TextRangeParticleSystem(
                        bounds = listOf(TextRangeParticleSystem.Bounds(0f, 0f, spanWidth, spanHeight)),
                        start = start,
                        end = end,
                        density = mask.density,
                        particleSizePx = mask.particleSize,
                        displayDensity = hostView?.resources?.displayMetrics?.density ?: 1f,
                    )
                    lastWidth = spanWidth
                    lastHeight = spanHeight
                }

                fillPaint.color = mask.color
                particleSystem?.particles?.forEach { particle ->
                    canvas.drawCircle(
                        x + particle.constrainedX,
                        top + particle.constrainedY,
                        particle.radius,
                        fillPaint,
                    )
                }
                if (!mask.isAnimated) {
                    active = false
                    hostView?.getParticlesTicker()?.untrack(this)
                    return
                }
                active = true
                hostView?.getParticlesTicker()?.track(this)
            }
        }
    }

    internal fun onFrame(dt: Float): Boolean {
        val particlesData = mask as? MaskData.Particles ?: return false
        if (!active || !particlesData.isEnabled || !particlesData.isAnimated) return false
        particleSystem?.advance(dt)
        return true
    }

    internal fun isAlive(): Boolean {
        val spanned = hostView?.text as? Spanned ?: return false
        return spanned.getSpanStart(this) != -1
    }
}
