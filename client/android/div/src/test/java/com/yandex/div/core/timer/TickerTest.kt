package com.yandex.div.core.timer

import android.os.SystemClock
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Tests for [Ticker].
 */
@RunWith(RobolectricTestRunner::class)
class TickerTest {
    private val defaultTimerName = "test_timer"
    private val defaultDuration = 3000L
    private val defaultInterval = 1000L
    private val defaultTickCount = (defaultDuration / defaultInterval).toInt()
    private val defaultDeviation = 15L

    @Test
    fun `run multiple timers`() {
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

        advanceUntilIdle()

        Assert.assertEquals(expectedTimerValues.size, actualTimerValues.size)
        val deviated = expectedTimerValues
            .zip(actualTimerValues) { left, right -> abs(right - left) }
            .all { it in 0 until defaultDeviation }

        Assert.assertTrue("expected: $expectedTimerValues, actual: $actualTimerValues", deviated)
    }

    @Test
    fun `correct tick count without extra time`() {
        var actualTicks = 0

        val ticker = createTicker(
            onTick = { _, _, _ ->
                actualTicks++
            }
        )

        ticker.startAndAdvance()

        Assert.assertEquals(defaultTickCount, actualTicks)
    }

    @Test
    fun `correct tick count with extra time`() {
        val duration = 3500L

        var actualTicks = 0

        val ticker = createTicker(
            duration = duration,
            onTick = { _, _, _ ->
                actualTicks++
            }
        )

        ticker.startAndAdvance()

        Assert.assertEquals(defaultTickCount, actualTicks)
    }

    @Test
    fun `correct duration`() {
        val duration = 500L
        val interval = 100L

        var startTime: Long = -1
        var delta: Long = -1

        val ticker = getTicker(
            duration = duration,
            interval = interval,
            onStart = { _, _, _ ->
                startTime = SystemClock.elapsedRealtime()
            },
            onEnd = { _, _, _ ->
                delta = SystemClock.elapsedRealtime() - startTime
            }
        )

        ticker.startAndAdvance()

        Assert.assertEquals(duration, delta)
    }

    @Test
    fun `endless timer not ends`() {
        val interval = 100L
        val duration = 0L

        val expectedTicks = 30
        var actualTicks = 0

        val ticker = createTicker(
            duration = duration,
            interval = interval,
            onTick = { _, _, _ ->
                actualTicks++

                if (actualTicks >= expectedTicks) {
                    this.stop()
                }
            }
        )

        ticker.startAndAdvance()

        Assert.assertEquals(expectedTicks, actualTicks)
    }

    @Test
    fun `end calls onEnd`() {
        var wasCalled = false

        val ticker = createTicker(
            onEnd = { _, _, _ ->
                wasCalled = true
            }
        )

        ticker.startAndAdvance()

        Assert.assertTrue(wasCalled)
    }

    @Test
    fun `tick calls onTick`() {
        var wasCalled = false

        val ticker = createTicker(
            onTick = { _, _, _ ->
                wasCalled = true
            }
        )

        ticker.startAndAdvance()

        Assert.assertTrue(wasCalled)
    }

    @Test
    fun `countdown timer not calls onTick`() {
        val duration = 100L
        val interval = null

        var wasCalled = false

        val ticker = createTicker(
            duration = duration,
            interval = interval,
            onTick = { _, _, _ ->
                wasCalled = true
            }
        )

        ticker.startAndAdvance()

        Assert.assertFalse(wasCalled)
    }

    @Test
    fun `onEnd and last onTick calls in the same time`() {
        var lastOnEndTime: Long = 0
        var lastOnTickTime: Long = 0

        val ticker = createTicker(
            onEnd = { time, _, _ ->
                lastOnEndTime = time
            },
            onTick = { time, _, _ ->
                lastOnTickTime = time
            }
        )

        ticker.startAndAdvance()

        Assert.assertEquals(lastOnEndTime, lastOnTickTime)
    }

    @Test
    fun `restore after saving state`() {
        var tickCounter = 0
        val ticker = createTicker(
            onTick = { _, _, _ ->
                tickCounter++
            }
        )

        ticker.start()

        ShadowLooper.idleMainLooper(defaultInterval, TimeUnit.MILLISECONDS)
        ticker.saveState()
        Assert.assertEquals(1, tickCounter)

        ShadowLooper.idleMainLooper(defaultInterval / 2, TimeUnit.MILLISECONDS)
        Assert.assertEquals(1, tickCounter)

        ticker.restoreState(true)
        advanceUntilIdle()
        Assert.assertEquals(3, tickCounter)
    }

    @Test
    fun `restore after saving state with missed interval`() {
        var tickCounter = 0
        val ticker = createTicker(
            onTick = { _, _, _ ->
                tickCounter++
            }
        )

        ticker.start()

        ShadowLooper.idleMainLooper(defaultInterval, TimeUnit.MILLISECONDS)
        ticker.saveState()
        Assert.assertEquals(1, tickCounter)

        ShadowLooper.idleMainLooper(defaultInterval + 100, TimeUnit.MILLISECONDS)
        Assert.assertEquals(1, tickCounter)

        ticker.restoreState(true)
        Assert.assertEquals(2, tickCounter)

        advanceUntilIdle()
        Assert.assertEquals(4, tickCounter)
    }

    @Test
    fun `restore countdown`() {
        var ended = false

        val ticker = createTicker(
            interval = null,
            onEnd = { _, _, _ ->
                ended = true
            }
        )
        ticker.start()
        ShadowLooper.idleMainLooper(1, TimeUnit.SECONDS)
        ticker.saveState()
        Assert.assertFalse(ended)

        ShadowLooper.idleMainLooper(10, TimeUnit.SECONDS)
        ticker.restoreState(true)
        advanceUntilIdle()
        Assert.assertTrue(ended)
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

        val ticker = createTicker(
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

        ticker.startAndAdvance()
        Assert.assertEquals(expectedTicksTime.size, expectedTicksTimeIndex)
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

        val ticker = createTicker(
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

        ticker.startAndAdvance()
        Assert.assertEquals(expectedTicksTime.size, expectedTicksTimeIndex)
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

        val ticker = createTicker(
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

        ticker.startAndAdvance()

        Assert.assertEquals(expectedTicksTime.size, expectedTicksTimeIndex)
    }

    @Test
    fun `the timer does not work during the pause`() {
        val sleepDuration = 200L
        val duration = 300L
        val interval = 100L

        val expectedFirstTickAndEndDiff = duration - interval + sleepDuration

        var endTime = -1L
        var firstTickTime: Long = -1L
        var tickCounter = 0

        val ticker = getTicker(
            duration = duration,
            interval = interval,
            onTick = { _, _, _ ->
                tickCounter++
                if (firstTickTime == -1L) firstTickTime = System.currentTimeMillis()
            },
            onEnd = { _, _, _ ->
                endTime = System.currentTimeMillis()
            }
        )

        ticker.start()

        ShadowLooper.idleMainLooper(150, TimeUnit.MILLISECONDS)
        Assert.assertEquals(1, tickCounter)
        ticker.pause()

        ShadowLooper.idleMainLooper(sleepDuration, TimeUnit.MILLISECONDS)
        Assert.assertEquals(1, tickCounter)
        ticker.resume()

        ShadowLooper.idleMainLooper(200, TimeUnit.MILLISECONDS)

        Assert.assertTrue(endTime - firstTickTime - expectedFirstTickAndEndDiff <= defaultDeviation)
        Assert.assertEquals(3, tickCounter)
    }

    private fun createTicker(
        duration: Long = defaultDuration,
        interval: Long? = defaultInterval,
        name: String = defaultTimerName,
        onStart: Ticker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> },
        onInterrupt: Ticker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> },
        onEnd: Ticker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> },
        onTick: Ticker.(time: Long, duration: Long, interval: Long?) -> Unit = { _, _, _ -> }
    ): Ticker {
        var ticker: Ticker? = null

        ticker = Ticker(
            name = name,
            onStart = { time -> ticker?.onStart(time, duration, interval) },
            onInterrupt = { time -> ticker?.onInterrupt(time, duration, interval) },
            onEnd = { time -> ticker?.onEnd(time, duration, interval) },
            onTick = { time -> ticker?.onTick(time, duration, interval) },
            errorCollector = null,
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

        return ticker
    }

    private fun Ticker.startAndAdvance() {
        start()
        advanceUntilIdle()
    }

    private fun advanceUntilIdle() {
        ShadowLooper.shadowMainLooper().idleFor(10, TimeUnit.SECONDS)
    }
}
