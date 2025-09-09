package com.yandex.div.core.view2.spannable

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Spanned
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.internal.spannable.PositionAwareReplacementSpan
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

private const val RADIUS_JITTER_MIN  = 0.7f
private const val RADIUS_JITTER_SPAN = 0.8f
private const val MIN_PARTICLES = 1
private const val MAX_PARTICLES = 800

private const val SPEED_MIN_DP = 2f
private const val SPEED_MAX_DP = 9f
private const val OMEGA_MAX = 0.25f

internal class MaskSpan(
    private val mask: MaskData,
    private val hostView: DivLineHeightTextView?
) : PositionAwareReplacementSpan() {

    private val particles = mutableListOf<Particle>()
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private var lastWidth = -1f
    private var lastHeight = -1f
    private var active = false
    private var randomSeed: Long = 0

    private data class Particle(
        var cx: Float,
        var cy: Float,
        var radius: Float,
        var vx: Float,
        var vy: Float,
        var angularVel: Float,
        var lifetimeMs: Float,
        var ageMs: Float
    )

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

        when (val m = mask) {
            is MaskData.Solid -> {
                fillPaint.color = m.color
                canvas.drawRect(x, top.toFloat(), x + spanWidth, bottom.toFloat(), fillPaint)
                active = false
            }

            is MaskData.Particles -> {
                if (spanWidth != lastWidth || spanHeight != lastHeight || particles.isEmpty()) {
                    initParticles(spanWidth, spanHeight, start, end, m)
                }

                fillPaint.color = m.color
                for (p in particles) {
                    val cx = p.cx.coerceIn(p.radius, spanWidth - p.radius)
                    val cy = p.cy.coerceIn(p.radius, spanHeight - p.radius)
                    canvas.drawCircle(x + cx, top + cy, p.radius, fillPaint)
                }
                if (!m.isAnimated) {
                    active = false
                    hostView?.getParticlesTicker()?.untrack(this)
                    return
                }
                active = true
                hostView?.getParticlesTicker()?.track(this)
            }
        }
    }

    private fun initParticles(
        w: Float,
        h: Float,
        start: Int,
        end: Int,
        particlesData: MaskData.Particles
    ) {
        lastWidth = w
        lastHeight = h
        particles.clear()

        val densityKey = (particlesData.density * 1000f).roundToInt()
        randomSeed = (start * 73_856_093L) xor (end * 19_349_663L) xor densityKey.toLong()
        val rnd = java.util.Random(randomSeed)

        val baseRadius = max(1f, particlesData.particleSize)

        val rJMin = RADIUS_JITTER_MIN
        val rJMax = RADIUS_JITTER_MIN + RADIUS_JITTER_SPAN
        val expectedF2 = (rJMax.pow(3) - rJMin.pow(3)) / (3f * RADIUS_JITTER_SPAN)
        val avgParticleArea = (PI.toFloat() * baseRadius.pow(2) * expectedF2)

        val tiles = if (avgParticleArea > 0f) (w * h) / avgParticleArea else 0f

        val density = when {
            particlesData.density >= 1f -> 0.99f
            particlesData.density <= 0f -> 0f
            else -> particlesData.density
        }
        val targetCount = (tiles * density).roundToInt().coerceIn(MIN_PARTICLES, MAX_PARTICLES)

        val densityPx = hostView?.resources?.displayMetrics?.density ?: 1f
        val speedMinPx = SPEED_MIN_DP * densityPx
        val speedMaxPx = SPEED_MAX_DP * densityPx
        val speedSpan = (speedMaxPx - speedMinPx)

        repeat(targetCount) {
            val jitter = rJMin + rnd.nextFloat() * (rJMax - rJMin)
            val radius = baseRadius * jitter

            val angle = (rnd.nextFloat() * (2 * PI)).toFloat() - PI.toFloat()
            val speed = speedMinPx + rnd.nextFloat() * speedSpan
            val vx = cos(angle) * speed
            val vy = sin(angle) * speed

            val omega = (rnd.nextFloat() - 0.5f) * (OMEGA_MAX * 2f)
            val lifetime = 6000f + rnd.nextFloat() * 6000f
            val startAge = rnd.nextFloat() * lifetime * 0.5f

            particles += Particle(
                cx = rnd.nextFloat() * w,
                cy = rnd.nextFloat() * h,
                radius = radius,
                vx = vx,
                vy = vy,
                angularVel = omega,
                lifetimeMs = lifetime,
                ageMs = startAge
            )
        }
    }

    internal fun onFrame(dt: Float): Boolean {
        val particlesData = mask as? MaskData.Particles ?: return false
        if (!active || !particlesData.isEnabled || !particlesData.isAnimated) return false
        if (dt <= 0f) return true

        val width = lastWidth
        val height = lastHeight

        for (p in particles) {
            val rot = p.angularVel * dt
            if (rot != 0f) {
                val c = cos(rot)
                val s = sin(rot)
                val nvx = p.vx * c - p.vy * s
                val nvy = p.vx * s + p.vy * c
                p.vx = nvx
                p.vy = nvy
            }

            p.cx += p.vx * dt
            p.cy += p.vy * dt

            val r = p.radius
            if (p.cx < -r) p.cx += (width + 2f * r)
            if (p.cx > width + r) p.cx -= (width + 2f * r)
            if (p.cy < -r) p.cy += (height + 2f * r)
            if (p.cy > height + r) p.cy -= (height + 2f * r)

            p.ageMs += dt * 1000f
            if (p.ageMs >= p.lifetimeMs) {
                reinitParticle(p, width, height, particlesData)
            }
        }
        return true
    }

    private fun reinitParticle(p: Particle, w: Float, h: Float, cfg: MaskData.Particles) {
        val rnd = java.util.Random(randomSeed + p.hashCode())

        val baseRadius = max(1f, cfg.particleSize)
        val rJMin = RADIUS_JITTER_MIN
        val rJMax = RADIUS_JITTER_MIN + RADIUS_JITTER_SPAN
        val jitter = rJMin + rnd.nextFloat() * (rJMax - rJMin)
        p.radius = baseRadius * jitter

        p.cx = rnd.nextFloat() * w
        p.cy = rnd.nextFloat() * h

        val densityPx = hostView?.resources?.displayMetrics?.density ?: 1f
        val speedMinPx = SPEED_MIN_DP * densityPx
        val speedMaxPx = SPEED_MAX_DP * densityPx
        val speedSpan = (speedMaxPx - speedMinPx)

        val angle = (rnd.nextFloat() * (2 * PI)).toFloat() - PI.toFloat()
        val speed = speedMinPx + rnd.nextFloat() * speedSpan
        p.vx = cos(angle) * speed
        p.vy = sin(angle) * speed

        p.angularVel = (rnd.nextFloat() - 0.5f) * (OMEGA_MAX * 2f)
        p.lifetimeMs = 6000f + rnd.nextFloat() * 6000f
        p.ageMs = 0f
    }

    internal fun isAlive(): Boolean {
        val sp = hostView?.text as? Spanned ?: return false
        return sp.getSpanStart(this) != -1
    }
}
