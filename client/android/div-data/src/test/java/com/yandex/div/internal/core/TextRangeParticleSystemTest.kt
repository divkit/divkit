package com.yandex.div.internal.core

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class TextRangeParticleSystemTest {

    @Test
    fun `advance rotates velocity without changing speed`() {
        val system = system()
        val particle = system.particles.first()
        val initialVelocityX = particle.velocityX
        val initialVelocityY = particle.velocityY
        val initialSpeedSquared = initialVelocityX * initialVelocityX + initialVelocityY * initialVelocityY

        system.advance(1f)

        val updatedSpeedSquared = particle.velocityX * particle.velocityX + particle.velocityY * particle.velocityY
        assertNotEquals(initialVelocityX, particle.velocityX)
        assertNotEquals(initialVelocityY, particle.velocityY)
        assertTrue(abs(initialSpeedSquared - updatedSpeedSquared) < 0.001f)
    }

    @Test
    fun `advance wraps particles with their radius`() {
        val system = system()
        val particle = system.particles.first()
        particle.angularVelocity = 0f
        particle.velocityX = 0f
        particle.velocityY = 0f
        particle.x = particle.bounds.width + particle.radius + 1f
        particle.y = -particle.radius - 1f

        system.advance(0.001f)

        assertTrue(particle.x <= particle.bounds.width + particle.radius)
        assertTrue(particle.y >= -particle.radius)
    }

    @Test
    fun `expired particle is deterministically reinitialized`() {
        val first = system()
        val second = system()
        val firstParticle = first.particles.first()
        val secondParticle = second.particles.first()
        firstParticle.ageMs = firstParticle.lifetimeMs
        secondParticle.ageMs = secondParticle.lifetimeMs

        first.advance(0.001f)
        second.advance(0.001f)

        assertEquals(0f, firstParticle.ageMs)
        assertEquals(firstParticle.radius, secondParticle.radius)
        assertEquals(firstParticle.x, secondParticle.x)
        assertEquals(firstParticle.y, secondParticle.y)
        assertEquals(firstParticle.velocityX, secondParticle.velocityX)
        assertEquals(firstParticle.velocityY, secondParticle.velocityY)
    }

    @Test
    fun `successive particle lifetimes start at different positions`() {
        val system = system()
        val particle = system.particles.first()
        particle.ageMs = particle.lifetimeMs
        system.advance(0.001f)
        val previousPosition = particle.x to particle.y

        particle.ageMs = particle.lifetimeMs
        system.advance(0.001f)

        assertNotEquals(previousPosition, particle.x to particle.y)
    }

    @Test
    fun `density below one is not clamped to point nine nine`() {
        val lowerDensity = system(density = 0.99f)

        val higherDensity = system(density = 0.995f)

        assertTrue(higherDensity.particles.size > lowerDensity.particles.size)
    }

    @Test
    fun `initial velocity preserves the View renderer angle precision`() {
        val particle = system().particles[1]

        assertEquals(4.8361325f to -19.246098f, particle.velocityX to particle.velocityY)
    }

    private fun system(density: Float = 0.4f) = TextRangeParticleSystem(
        bounds = listOf(TextRangeParticleSystem.Bounds(0f, 0f, 100f, 40f)),
        start = 3,
        end = 8,
        density = density,
        particleSizePx = 2f,
        displayDensity = 3f,
    )

}
