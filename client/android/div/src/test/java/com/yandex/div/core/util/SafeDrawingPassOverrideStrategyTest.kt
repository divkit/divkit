package com.yandex.div.core.util

import com.yandex.div.core.Div2Logger
import com.yandex.div.core.view2.Div2View
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.startsWith
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SafeDrawingPassOverrideStrategyTest {

    private val divView = mock<Div2View>()
    private val logger = mock<Div2Logger>()

    private val overrideStrategy = SafeDrawingPassOverrideStrategy(divView, logger)

    @Test
    fun `no frame cancellation`() {
        overrideStrategy.frameCancelLimit = 10

        repeat(times = 4) {
            overrideStrategy.overrideDrawingPass(mock(), proceed = true)
        }

        verify(logger, never()).logFrameCancelled(any(), any())
        verify(logger, never()).logFrameCancelLimitExceeded(any(), any())
    }

    @Test
    fun `frame cancellation within limit - no override`() {
        overrideStrategy.frameCancelLimit = 10

        var overridden = false
        repeat(times = 4) {
            assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), false)
        }
    }

    @Test
    fun `frame cancellation within limit - no limit exceed logging`() {
        overrideStrategy.frameCancelLimit = 10

        var overridden = false
        repeat(times = 4) {
            overrideStrategy.overrideDrawingPass(mock(), proceed = false)
        }

        verify(logger, times(4)).logFrameCancelled(any(), startsWith("Frame cancelled by "))
        verify(logger, never()).logFrameCancelLimitExceeded(any(), any())
    }

    @Test
    fun `frame proceeding resets limit counter - no result override`() {
        overrideStrategy.frameCancelLimit = 2

        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), false)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = true), true)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), false)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), false)
    }

    @Test
    fun `frame proceeding resets limit counter - no limit exceed logging`() {
        overrideStrategy.frameCancelLimit = 2

        overrideStrategy.overrideDrawingPass(mock(), proceed = false)
        overrideStrategy.overrideDrawingPass(mock(), proceed = true)
        overrideStrategy.overrideDrawingPass(mock(), proceed = false)
        overrideStrategy.overrideDrawingPass(mock(), proceed = false)

        verify(logger, never()).logFrameCancelLimitExceeded(any(), any())
    }

    @Test
    fun `frame cancellation over limit - result overridden`() {
        overrideStrategy.frameCancelLimit = 3

        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), false)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), false)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), false)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), true)
    }

    @Test
    fun `frame cancellation over limit - limit exceed logged`() {
        overrideStrategy.frameCancelLimit = 3

        overrideStrategy.overrideDrawingPass(mock(), proceed = false)
        overrideStrategy.overrideDrawingPass(mock(), proceed = false)
        overrideStrategy.overrideDrawingPass(mock(), proceed = false)
        overrideStrategy.overrideDrawingPass(mock(), proceed = false)

        verify(logger).logFrameCancelLimitExceeded(any(), startsWith("Frame cancellation limit exceeded by "))
    }

    @Test
    fun `sequential frame cancellation over limit - result remains overridden`() {
        overrideStrategy.frameCancelLimit = 1

        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), false)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), true)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), true)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), true)
    }

    @Test
    fun `frame proceeding resets limit counter when limit has exceeded`() {
        overrideStrategy.frameCancelLimit = 1

        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), false)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), true)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = true), true)
        assertEquals(overrideStrategy.overrideDrawingPass(mock(), proceed = false), false)
    }
}
