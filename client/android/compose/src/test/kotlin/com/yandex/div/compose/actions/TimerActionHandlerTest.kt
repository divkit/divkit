package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.timers.TimerStorage
import com.yandex.div.test.data.action
import com.yandex.div.test.data.timerAction
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionTimer
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class TimerActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()
    private val timerStorage = mock<TimerStorage>()

    private val reporter: TestReporter
        get() = actionHandlerEnvironment.reporter

    @BeforeTest
    fun setUp() {
        actionHandlerEnvironment.init(
            timerActionHandler = TimerActionHandler(
                reporter = reporter,
                timerStorage = timerStorage
            )
        )
    }

    @Test
    fun `start action starts timer`() {
        handle(
            action(
                typed = timerAction(id = "timer1", action = DivActionTimer.Action.START)
            )
        )

        verify(timerStorage).start("timer1")
    }

    @Test
    fun `stop action stops timer`() {
        handle(
            action(
                typed = timerAction(id = "timer1", action = DivActionTimer.Action.STOP)
            )
        )

        verify(timerStorage).stop("timer1")
    }

    @Test
    fun `pause action pauses timer`() {
        handle(
            action(
                typed = timerAction(id = "timer1", action = DivActionTimer.Action.PAUSE)
            )
        )

        verify(timerStorage).pause("timer1")
    }

    @Test
    fun `resume action resumes timer`() {
        handle(
            action(
                typed = timerAction(id = "timer1", action = DivActionTimer.Action.RESUME)
            )
        )

        verify(timerStorage).resume("timer1")
    }

    @Test
    fun `start div-action starts timer`() {
        handle(
            action(url = "div-action://timer?id=timer1&action=start")
        )

        verify(timerStorage).start("timer1")
    }

    @Test
    fun `error is reported for invalid div-action`() {
        reporter.failOnError = false

        handle(
            action(url = "div-action://timer?id=timer1&action=invalid")
        )

        assertEquals("Invalid timer action: invalid", reporter.lastError)
    }

    private fun handle(action: DivAction) = actionHandlerEnvironment.handle(action)
}
