package com.yandex.div.core.timer

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Timer
import kotlin.math.abs

/**
 * Tests for [Ticker].
 */
@RunWith(RobolectricTestRunner::class)
class TickerTest {
    private val parentTimer = Timer()

    private val defaultTimerName = "test_timer"
    private val defaultDuration = 3000L
    private val defaultInterval = 1000L
    private val defaultDeviation = 15L
    private val retryCount = 3

    private val defaultValue = -1L

    @Test
    fun `run multiple timers`() = retryOnFail {
        val firstTimerDuration = 300L
        val firstTimerInterval = 100L

        val secondTimerDuration = 350L
        val secondTimerInterval = 150L

        val expectedTimerValues = listOf(100L, 150L, 200L, 300L, 300L)
        val actualTimerValues = mutableListOf<Long>()

        val firstTimer = getTicker(
            firstTimerDuration,
            firstTimerInterval,
            "first"
        ) { time, _, _ ->
            actualTimerValues += time
        }

        val secondTimer = getTicker(
            secondTimerDuration,
            secondTimerInterval,
            "second"
        ) { time, _, _ ->
            actualTimerValues += time
        }

        firstTimer.start()
        secondTimer.start()

        Thread.sleep(400L)

        val deviated = expectedTimerValues
            .zip(actualTimerValues) { left, right -> abs(right - left) }
            .all { it in 0 until defaultDeviation }

        Assert.assertTrue("expected: $expectedTimerValues, actual: $actualTimerValues", deviated)
    }

    @Test
    fun `correct tick count without extra time`() {
        val expectedTicks = 3
        var actualTicks = 0

        val ticker = getInstantTicker(
            onTick = { _, _, _ ->
                actualTicks++
            }
        )

        ticker.start()

        Assert.assertEquals(expectedTicks, actualTicks)
    }

    @Test
    fun `correct tick count with extra time`() {
        val duration = 3500L

        val expectedTicks = 3
        var actualTicks = 0

        val ticker = getInstantTicker(
            duration = duration,
            onTick = { _, _, _ ->
                actualTicks++
            }
        )

        ticker.start()

        Assert.assertEquals(expectedTicks, actualTicks)
    }

    @Test
    fun `correct duration`() = retryOnDeviation {
        val duration = 500L
        val interval = 100L

        var startTime: Long = defaultValue
        var delta: Long = defaultValue

        val ticker = getTicker(
            duration = duration,
            interval = interval,
            onStart = { _, _, _ ->
                startTime = currentTime
            },
            onEnd = { _, _, _ ->
                delta = currentTime - startTime
            }
        )

        ticker.start()

        Thread.sleep(duration - interval / 2)
        Assert.assertEquals(defaultValue, delta)
        Thread.sleep(interval)

        delta - duration
    }

    @Test
    fun `endless timer not ends`() {
        val interval = 100L
        val duration = 0L

        val expectedTicks = 30
        var actualTicks = 0

        val ticker = getInstantTicker(
            duration = duration,
            interval = interval,
            onTick = { _, _, _ ->
                actualTicks++

                if (actualTicks >= expectedTicks) {
                    this.stop()
                }
            }
        )

        ticker.start()

        Assert.assertEquals(expectedTicks, actualTicks)
    }

    @Test
    fun `end calls onEnd`() {
        var wasCalled = false

        val ticker = getInstantTicker(
            onEnd = { _, _, _ ->
                wasCalled = true
            }
        )

        ticker.start()

        Assert.assertTrue(wasCalled)
    }

    @Test
    fun `tick calls onTick`() {
        var wasCalled = false

        val ticker = getInstantTicker(
            onTick = { _, _, _ ->
                wasCalled = true
            }
        )

        ticker.start()

        Assert.assertTrue(wasCalled)
    }

    @Test
    fun `countdown timer not calls onTick`() {
        val duration = 100L
        val interval = null

        var wasCalled = false

        val ticker = getInstantTicker(
            duration = duration,
            interval = interval,
            onTick = { _, _, _ ->
                wasCalled = true
            }
        )

        ticker.start()

        Assert.assertFalse(wasCalled)
    }

    @Test
    fun `onEnd and last onTick calls in the same time`() {
        var lastOnEndTime: Long = 0
        var lastOnTickTime: Long = 1

        val ticker = getInstantTicker(
            onEnd = { time, _, _ ->
                lastOnEndTime = time
            },
            onTick = { time, _, _ ->
                lastOnTickTime = time
            }
        )

        ticker.start()

        Assert.assertEquals(lastOnEndTime, lastOnTickTime)
    }

    @Test
    fun `updates not applies while timer working if restart wasn't called`() {
        val duration = 300L
        val interval = 150L

        val newDuration = 500L
        val newInterval = 100L

        var wasUpdated = false

        var expectedTicksTimeIndex = 0
        val expectedTicksTime = listOf(150L, 300L)

        val ticker = getInstantTicker(
            duration = duration,
            interval = interval,
            onEnd = { time, currentDuration, _ ->
                Assert.assertEquals(currentDuration, time)
            },
            onTick = { time, _, _ ->
                Assert.assertEquals(expectedTicksTime[expectedTicksTimeIndex++], time)

                if (!wasUpdated) {
                    wasUpdated = true

                    update(newDuration, newInterval)
                }
            }
        )

        ticker.start()
    }

    @Test
    fun `updates applies while timer working if pause and resume was called`() {
        val duration = 300L
        val interval = 150L

        val newDuration = 500L
        val newInterval = 100L

        var wasUpdated = false

        var expectedTicksTimeIndex = 0
        val expectedTicksTime = listOf(150L, 300L)

        val ticker = getInstantTicker(
            duration = duration,
            interval = interval,
            onEnd = { time, currentDuration, _ ->
                Assert.assertEquals(currentDuration, time)
            },
            onTick = { time, _, _ ->
                Assert.assertEquals(expectedTicksTime[expectedTicksTimeIndex++], time)

                if (!wasUpdated) {
                    wasUpdated = true

                    update(newDuration, newInterval)
                    pause()
                    resume()
                }
            }
        )

        ticker.start()
    }

    @Test
    fun `updates applies while timer working if reset was called`() {
        val duration = 300L
        val interval = 150L

        val newDuration = 500L
        val newInterval = 100L

        var wasUpdated = false

        var expectedTicksTimeIndex = 0
        val expectedTicksTime = listOf(150L, 100L, 200L, 300L, 400L, 500L)

        val ticker = getInstantTicker(
            duration = duration,
            interval = interval,
            onEnd = { time, _, _ ->
                Assert.assertEquals(newDuration, time)
            },
            onTick = { time, _, _ ->
                Assert.assertEquals(expectedTicksTime[expectedTicksTimeIndex++], time)

                if (!wasUpdated) {
                    wasUpdated = true

                    update(newDuration, newInterval)
                    reset()
                }
            }
        )

        ticker.start()
    }

    @Test
    fun `the timer does not work during the pause`() {
        val sleepDuration = 200L
        val duration = 300L
        val interval = 100L

        val expectedFirstTickAndEndDiff = duration - interval + sleepDuration

        var endTime = -1L
        var firstTickTime: Long = -1L

        val ticker = getTicker(
            duration = duration,
            interval = interval,
            onTick = { _, _, _ ->
                if (firstTickTime == -1L) firstTickTime = System.currentTimeMillis()
            },
            onEnd = { _, _, _ ->
                endTime = System.currentTimeMillis()
            }
        )

        ticker.start()

        Thread.sleep(150)
        ticker.pause()
        Thread.sleep(sleepDuration)
        ticker.resume()

        Thread.sleep(200L)

        Assert.assertTrue(endTime - firstTickTime - expectedFirstTickAndEndDiff <= defaultDeviation)
    }

    private fun getInstantTicker(
        duration: Long = defaultDuration,
        interval: Long? = defaultInterval,
        name: String = defaultTimerName,
        onStart: InstantTestTicker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> },
        onPause: InstantTestTicker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> },
        onEnd: InstantTestTicker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> },
        onTick: InstantTestTicker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> }
    ): InstantTestTicker {
        var ticker: InstantTestTicker? = null

        ticker = InstantTestTicker(
            name = name,
            onStart = { time -> ticker?.onStart(time, duration, interval) },
            onPause = { time -> ticker?.onPause(time, duration, interval) },
            onEnd = { time -> ticker?.onEnd(time, duration, interval) },
            onTick = { time -> ticker?.onTick(time, duration, interval) }
        )

        ticker.update(duration, interval)

        return ticker
    }

    private fun getTicker(
        duration: Long = defaultDuration,
        interval: Long? = defaultInterval,
        name: String = defaultTimerName,
        onStart: Ticker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> },
        onPause: Ticker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> },
        onEnd: Ticker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> },
        onTick: Ticker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> }
    ): Ticker {
        var ticker: Ticker? = null

        ticker = Ticker(
            name = name,
            onStart = { time -> ticker?.onStart(time, duration, interval) },
            onInterrupt = { time -> ticker?.onPause(time, duration, interval) },
            onEnd = { time -> ticker?.onEnd(time, duration, interval) },
            onTick = { time -> ticker?.onTick(time, duration, interval) },
            errorCollector = null
        )

        ticker.update(duration, interval)

        ticker.attachToTimer(parentTimer)

        return ticker
    }

    /**
     * Since we work with time, it is worth considering delays.
     * To avoid fails of tests, we add a small error in time and restart the tests several times
     */
    private fun retryOnDeviation(
        block: () -> Long
    ) {
        var retries = 0
        var wasSuccessful = false

        val deviations = MutableList(retryCount) { 0L }

        while (retries < retryCount && !wasSuccessful) {
            val deviation = block()
            wasSuccessful = deviation <= defaultDeviation

            deviations[retries] = deviation

            retries++
        }

        if (!wasSuccessful) {
            Assert.fail("Deviations are too big. All values $deviations are greater than $defaultDeviation")
        }
    }

    private fun retryOnFail(
        block: () -> Unit
    ) {
        val retries: MutableList<Throwable> = mutableListOf()

        while (retries.size < retryCount) {
            try {
                block()

                break
            } catch (e: Throwable) { retries += e }
        }

        if (retries.size >= retryCount) {
            Assert.fail("All ${retries.size} retries failed!\n\t${retries.map { it.message }.joinToString("\n\t")}")
        }
    }
}
