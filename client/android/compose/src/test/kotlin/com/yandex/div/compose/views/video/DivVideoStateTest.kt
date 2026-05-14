package com.yandex.div.compose.views.video

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivVideoStateTest {

    @get:Rule
    val composeRule = createComposeRule().apply {
        mainClock.autoAdvance = false
    }

    private val seekHistory = mutableListOf<Long>()

    @Test
    fun `setPosition writes once per distinct value and skips repeats`() {
        val variable = CountingMutableState(initial = 0L)
        val state = DivVideoState(variable)

        repeat(3) { state.setPosition(1_000L) }
        state.setPosition(2_000L)
        state.setPosition(2_000L)

        assertEquals(2, variable.writeCount)
        assertEquals(2_000L, variable.value)
    }

    @Test
    fun `setPosition return value reflects whether the value changed`() {
        val state = DivVideoState(mutableStateOf(0L))

        assertTrue(state.setPosition(1_000L))
        assertFalse(state.setPosition(1_000L))
        assertTrue(state.setPosition(2_000L))
        assertFalse(state.setPosition(2_000L))
    }

    @Test
    fun `setPosition tracks state without a bound variable`() {
        val state = DivVideoState(elapsedTimeVariable = null)

        assertTrue(state.setPosition(1_000L))
        assertFalse(state.setPosition(1_000L))
        assertTrue(state.setPosition(2_000L))
    }

    @Test
    fun `polls position into variable while playing`() {
        val variable = mutableStateOf(0L)
        val state = DivVideoState(variable).apply { isPlaying = true }
        var currentPosition = 100L

        setContent(state, getCurrentPosition = { currentPosition }, intervalMs = 100L)
        flush()
        assertEquals(100L, variable.value)

        currentPosition = 250L
        advanceBy(100L)
        assertEquals(250L, variable.value)

        currentPosition = 400L
        advanceBy(100L)
        assertEquals(400L, variable.value)
    }

    @Test
    fun `stops polling when paused`() {
        val variable = mutableStateOf(0L)
        val state = DivVideoState(variable).apply { isPlaying = true }
        var currentPosition = 100L

        setContent(state, getCurrentPosition = { currentPosition }, intervalMs = 100L)
        flush()
        assertEquals(100L, variable.value)

        state.isPlaying = false
        flush()

        currentPosition = 999L
        advanceBy(500L)

        assertEquals(100L, variable.value)
    }

    @Test
    fun `seeks player when variable changes externally`() {
        val variable = mutableStateOf(0L)
        val state = DivVideoState(variable)

        setContent(state, getCurrentPosition = { 0L })
        flush()
        assertEquals(emptyList<Long>(), seekHistory)

        variable.value = 5_000L
        flush()

        assertEquals(listOf(5_000L), seekHistory)
    }

    @Test
    fun `does not seek when polled position is written back to variable`() {
        val variable = mutableStateOf(0L)
        val state = DivVideoState(variable).apply { isPlaying = true }

        setContent(state, getCurrentPosition = { 1_000L }, intervalMs = 100L)
        flush()

        assertEquals(1_000L, variable.value)
        assertEquals(emptyList<Long>(), seekHistory)
    }

    private fun flush() {
        Snapshot.sendApplyNotifications()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
    }

    private fun advanceBy(ms: Long) {
        Snapshot.sendApplyNotifications()
        composeRule.mainClock.advanceTimeBy(ms)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
    }

    private fun setContent(
        state: DivVideoState,
        getCurrentPosition: () -> Long,
        intervalMs: Long = 1_000L,
    ) {
        composeRule.setContent {
            state.SyncWithPlayer(
                getCurrentPosition = getCurrentPosition,
                seekTo = { seekHistory += it },
                intervalMs = intervalMs,
            )
        }
    }

    private class CountingMutableState(initial: Long) : MutableState<Long> {
        var writeCount = 0
            private set

        private var _value = initial

        override var value: Long
            get() = _value
            set(newValue) {
                writeCount++
                _value = newValue
            }

        override fun component1(): Long = value
        override fun component2(): (Long) -> Unit = { value = it }
    }
}
