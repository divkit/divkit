package com.yandex.div.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div.test.data.timer
import com.yandex.div.test.data.timerAction
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionTimer
import com.yandex.div2.DivTimer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@RunWith(AndroidJUnit4::class)
class DivViewWithTimersTest {

    @get:Rule
    val rule = createComposeRule()

    private val elapsedTime = Variable.IntegerVariable("counter", 0)

    private val variableController = DivVariableController().apply {
        declare(elapsedTime)
    }

    private val configuration = DivComposeConfiguration(
        reporter = TestReporter(),
        variableController = variableController
    )

    private val testScope = TestScope()

    private val debugConfiguration = DivDebugConfiguration(
        coroutineScope = testScope,
        timeSource = testScope.testScheduler.timeSource
    )

    @Test
    fun `start timer action starts timer`() {
        setContent(
            text(
                action = startTimerAction(),
                id = "start_button",
                text = "Start timer"
            ),
            timers = listOf(
                timer(
                    duration = 1000,
                    id = "timer1",
                    valueVariable = "counter"
                )
            )
        )

        rule.onNodeWithTag("start_button").performClick()
        advanceTimeBy(500)

        assertEquals(0L, elapsedTime.getValue())

        advanceTimeBy(500)

        assertEquals(1000L, elapsedTime.getValue())
    }

    @Test
    fun `timer is paused when view leaves composition`() {
        val data = data(
            text(
                action = startTimerAction(),
                id = "start_button",
                text = "Start timer"
            ),
            timers = listOf(
                timer(
                    id = "timer1",
                    tickInterval = 1000,
                    valueVariable = "counter"
                )
            )
        )

        var isVisible by mutableStateOf(true)

        rule.setContentWithDivContext(
            configuration = configuration,
            debugConfiguration = debugConfiguration
        ) {
            if (isVisible) {
                DivView(data = data)
            }
        }

        rule.onNodeWithTag("start_button").performClick()
        advanceTimeBy(1500)

        assertEquals(1000L, elapsedTime.getValue())

        isVisible = false
        rule.waitForIdle()

        assertEquals(1500L, elapsedTime.getValue())

        advanceTimeBy(1000)

        assertEquals(1500L, elapsedTime.getValue())
    }

    @Test
    fun `timer is resumed when view reenters composition`() {
        val data = data(
            text(
                action = startTimerAction(),
                id = "start_button",
                text = "Start timer"
            ),
            timers = listOf(
                timer(
                    id = "timer1",
                    tickInterval = 1000,
                    valueVariable = "counter"
                )
            )
        )

        var isVisible by mutableStateOf(true)

        rule.setContentWithDivContext(
            configuration = configuration,
            debugConfiguration = debugConfiguration
        ) {
            if (isVisible) {
                DivView(data = data)
            }
        }

        rule.onNodeWithTag("start_button").performClick()
        advanceTimeBy(1500)
        isVisible = false
        rule.waitForIdle()

        assertEquals(1500L, elapsedTime.getValue())

        isVisible = true
        rule.waitForIdle()

        assertEquals(1500L, elapsedTime.getValue())

        advanceTimeBy(500)

        assertEquals(2000L, elapsedTime.getValue())
    }

    private fun setContent(
        content: Div,
        timers: List<DivTimer>
    ) {
        rule.setContent(
            configuration = configuration,
            debugConfiguration = debugConfiguration,
            data = data(content, timers = timers)
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun advanceTimeBy(duration: Long) {
        testScope.testScheduler.advanceTimeBy(duration)
        testScope.testScheduler.runCurrent()
    }
}

private fun startTimerAction(): DivAction {
    return action(
        typed = timerAction(id = "timer1", action = DivActionTimer.Action.START)
    )
}
