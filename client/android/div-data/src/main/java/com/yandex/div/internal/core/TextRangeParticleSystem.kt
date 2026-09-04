package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

private const val MIN_PARTICLES = 1
private const val MAX_PARTICLES = 800
private const val RADIUS_JITTER_MIN = 0.7f
private const val RADIUS_JITTER_SPAN = 0.8f
private const val OMEGA_MAX = 0.25f
private const val SPEED_MIN_DP = 2f
private const val SPEED_MAX_DP = 9f
private const val RESPAWN_SEED_STEP = 0x9E3779B9L

@InternalApi
class TextRangeParticleSystem(
    bounds: List<Bounds>,
    start: Int,
    end: Int,
    density: Float,
    particleSizePx: Float,
    displayDensity: Float,
) {
    data class Bounds(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
    ) {
        val width: Float get() = right - left
        val height: Float get() = bottom - top
    }

    class Particle internal constructor(
        val bounds: Bounds,
        internal val index: Int,
        radius: Float,
        x: Float,
        y: Float,
        velocityX: Float,
        velocityY: Float,
        angularVelocity: Float,
        lifetimeMs: Float,
        ageMs: Float,
    ) {
        internal var respawnCount = 0L
        var radius: Float = radius
            internal set
        var x: Float = x
            internal set
        var y: Float = y
            internal set
        var velocityX: Float = velocityX
            internal set
        var velocityY: Float = velocityY
            internal set
        var angularVelocity: Float = angularVelocity
            internal set
        var lifetimeMs: Float = lifetimeMs
            internal set
        var ageMs: Float = ageMs
            internal set

        val constrainedX: Float
            get() = bounds.left + x.coerceParticleCenter(radius, bounds.width)
        val constrainedY: Float
            get() = bounds.top + y.coerceParticleCenter(radius, bounds.height)
    }

    private val randomSeed = (start * 73_856_093L) xor (end * 19_349_663L) xor
        (density * 1_000f).roundToInt().toLong()
    private val baseRadius = max(1f, particleSizePx)
    private val speedMinPx = SPEED_MIN_DP * displayDensity
    private val speedMaxPx = SPEED_MAX_DP * displayDensity

    val particles: List<Particle> = createParticles(bounds, density)

    fun advance(elapsedSeconds: Float) {
        if (elapsedSeconds <= 0f) return

        particles.forEach { particle ->
            val rotation = particle.angularVelocity * elapsedSeconds
            if (rotation != 0f) {
                val cosine = cos(rotation)
                val sine = sin(rotation)
                val velocityX = particle.velocityX * cosine - particle.velocityY * sine
                val velocityY = particle.velocityX * sine + particle.velocityY * cosine
                particle.velocityX = velocityX
                particle.velocityY = velocityY
            }
            particle.x += particle.velocityX * elapsedSeconds
            particle.y += particle.velocityY * elapsedSeconds

            val radius = particle.radius
            val width = particle.bounds.width
            val height = particle.bounds.height
            if (particle.x < -radius) particle.x += width + 2f * radius
            if (particle.x > width + radius) particle.x -= width + 2f * radius
            if (particle.y < -radius) particle.y += height + 2f * radius
            if (particle.y > height + radius) particle.y -= height + 2f * radius

            particle.ageMs += elapsedSeconds * 1_000f
            if (particle.ageMs >= particle.lifetimeMs) reinitialize(particle)
        }
    }

    private fun createParticles(bounds: List<Bounds>, density: Float): List<Particle> {
        require(bounds.isNotEmpty())
        val radiusJitterMax = RADIUS_JITTER_MIN + RADIUS_JITTER_SPAN
        val expectedRadiusSquared = (radiusJitterMax.pow(3) - RADIUS_JITTER_MIN.pow(3)) /
            (3f * RADIUS_JITTER_SPAN)
        val averageParticleArea = PI.toFloat() * baseRadius.pow(2) * expectedRadiusSquared
        val area = bounds.sumOf { (it.width * it.height).toDouble() }.toFloat()
        val tiles = if (averageParticleArea > 0f) area / averageParticleArea else 0f
        val effectiveDensity = when {
            density >= 1f -> 0.99f
            density <= 0f -> 0f
            else -> density
        }
        val count = (tiles * effectiveDensity).roundToInt().coerceIn(MIN_PARTICLES, MAX_PARTICLES)
        val random = JavaRandom(randomSeed)
        val totalArea = area.coerceAtLeast(1f)

        return List(count) { index ->
            val jitter = RADIUS_JITTER_MIN + random.nextFloat() * RADIUS_JITTER_SPAN
            val angle = (random.nextFloat() * (2 * PI)).toFloat() - PI.toFloat()
            val speed = speedMinPx + random.nextFloat() * (speedMaxPx - speedMinPx)
            val angularVelocity = (random.nextFloat() - 0.5f) * (OMEGA_MAX * 2f)
            val lifetimeMs = 6_000f + random.nextFloat() * 6_000f
            val ageMs = random.nextFloat() * lifetimeMs * 0.5f
            val particleBounds = if (bounds.size == 1) {
                bounds[0]
            } else {
                bounds.pickByArea(random.nextFloat() * totalArea)
            }
            Particle(
                bounds = particleBounds,
                index = index,
                radius = baseRadius * jitter,
                x = random.nextFloat() * particleBounds.width,
                y = random.nextFloat() * particleBounds.height,
                velocityX = cos(angle) * speed,
                velocityY = sin(angle) * speed,
                angularVelocity = angularVelocity,
                lifetimeMs = lifetimeMs,
                ageMs = ageMs,
            )
        }
    }

    private fun reinitialize(particle: Particle) {
        particle.respawnCount++
        val random = JavaRandom(randomSeed + particle.index + particle.respawnCount * RESPAWN_SEED_STEP)
        particle.radius = baseRadius * (RADIUS_JITTER_MIN + random.nextFloat() * RADIUS_JITTER_SPAN)
        particle.x = random.nextFloat() * particle.bounds.width
        particle.y = random.nextFloat() * particle.bounds.height

        val angle = (random.nextFloat() * (2 * PI)).toFloat() - PI.toFloat()
        val speed = speedMinPx + random.nextFloat() * (speedMaxPx - speedMinPx)
        particle.velocityX = cos(angle) * speed
        particle.velocityY = sin(angle) * speed
        particle.angularVelocity = (random.nextFloat() - 0.5f) * (OMEGA_MAX * 2f)
        particle.lifetimeMs = 6_000f + random.nextFloat() * 6_000f
        particle.ageMs = 0f
    }
}

private fun List<TextRangeParticleSystem.Bounds>.pickByArea(position: Float): TextRangeParticleSystem.Bounds {
    var remaining = position
    forEach { bounds ->
        remaining -= bounds.width * bounds.height
        if (remaining <= 0f) return bounds
    }
    return last()
}

private fun Float.coerceParticleCenter(radius: Float, dimension: Float): Float {
    val maximum = max(radius, dimension - radius)
    return coerceIn(radius, maximum)
}

private class JavaRandom(seed: Long) {
    private var seed = (seed xor MULTIPLIER) and MASK

    fun nextFloat(): Float = next(24) / (1 shl 24).toFloat()

    private fun next(bits: Int): Int {
        seed = (seed * MULTIPLIER + ADDEND) and MASK
        return (seed ushr (48 - bits)).toInt()
    }

    private companion object {
        const val MULTIPLIER = 0x5DEECE66DL
        const val ADDEND = 0xBL
        const val MASK = (1L shl 48) - 1
    }
}
