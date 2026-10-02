package com.yandex.div.compose.timers

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.timer
import com.yandex.div2.DivAction
import com.yandex.div2.DivTimer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
@RunWith(AndroidJUnit4::class)
class TimerStorageTest {
    private val reporter = TestReporter()
    private val testScope = TestScope()
    private val elapsedTime = Variable.IntegerVariable("counter", 0)

    private val variableController = DivVariableController().apply {
        declare(elapsedTime)
    }

    private val actionHandler = mock<DivActionHandler> {
        on { handle(context = any(), actions = any(), source = any()) } doAnswer {
            assertEquals(DivActionSource.TIMER, it.arguments[2])
            handledActions.addAll(it.arguments[1] as Collection<DivAction>)
            Unit
        }
    }

    private lateinit var timerStorage: TimerStorage

    private val handledActions = mutableListOf<DivAction>()

    @Test
    fun `init() reports an error when adding timers with non unique ids`() {
        reporter.failOnError = false

        init(timer(id = "timer1"), timer(id = "timer1"))

        assertEquals("Timer with the same id is already exist: timer1", reporter.lastError)
    }

    @Test
    fun `init() reports an error when adding timers with unknown variable`() {
        reporter.failOnError = false

        init(timer(id = "timer1", valueVariable = "unknown"))

        assertEquals("Timer variable is not declared: unknown", reporter.lastError)
    }

    @Test
    fun `init() reports an error when adding timers with invalid variable`() {
        reporter.failOnError = false

        variableController.putOrUpdate(
            Variable.StringVariable(name = "string_var", defaultValue = "value")
        )

        init(timer(id = "timer1", valueVariable = "string_var"))

        assertEquals("Timer variable must be an integer variable: string_var", reporter.lastError)
    }

    @Test
    fun `start() reports an error if timer with the given id not found`() {
        reporter.failOnError = false

        init(timer(id = "timer1"))

        timerStorage.start("unknown")

        assertEquals("Timer does not exist: unknown", reporter.lastError)
    }

    @Test
    fun `start() reports an error if timer is paused`() {
        init(
            timer(
                duration = 5000,
                id = "timer1"
            )
        )

        timerStorage.start("timer1")
        timerStorage.pause("timer1")

        reporter.failOnError = false
        timerStorage.start("timer1")

        assertEquals("Timer is already started: timer1", reporter.lastError)
    }

    @Test
    fun `start() is ignored if isEnabled=false`() {
        val actions = listOf(action(url = "action://end"))
        init(
            timer(
                duration = 1000,
                endActions = actions,
                id = "timer1",
                valueVariable = "counter"
            )
        )

        timerStorage.isEnabled = false
        timerStorage.start("timer1")
        advanceTimeBy(1000)

        assertEquals(0L, elapsedTime.getValue())
        verifyHandledActions(emptyList())
    }

    @Test
    fun `isEnabled=false pauses timer`() {
        val actions = listOf(action(url = "action://end"))
        init(
            timer(
                duration = 1000,
                endActions = actions,
                id = "timer1",
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(500)
        timerStorage.isEnabled = false
        advanceTimeBy(500)

        assertEquals(500L, elapsedTime.getValue())
        verifyHandledActions(emptyList())
    }

    @Test
    fun `isEnabled=true resumes timer`() {
        val actions = listOf(action(url = "action://end"))
        init(
            timer(
                duration = 1000,
                endActions = actions,
                id = "timer1",
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(500)
        timerStorage.isEnabled = false
        advanceTimeBy(500)
        timerStorage.isEnabled = true

        assertEquals(500L, elapsedTime.getValue())
        verifyHandledActions(emptyList())

        advanceTimeBy(500)

        assertEquals(1000L, elapsedTime.getValue())
        verifyHandledActions(actions)
    }

    @Test
    fun `endActions are triggered after duration`() {
        val actions = listOf(action(url = "action://end"))
        init(
            timer(
                duration = 1000,
                endActions = actions,
                id = "timer1"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(500)

        verifyHandledActions(emptyList())

        advanceTimeBy(500)

        verifyHandledActions(actions)

        advanceTimeBy(1000)

        verifyHandledActions(emptyList())
    }

    @Test
    fun `both endActions and tickActions are triggered after duration`() {
        val endActions = listOf(action(url = "action://end"))
        val tickActions = listOf(action(url = "action://tick"))
        init(
            timer(
                duration = 1000,
                endActions = endActions,
                id = "timer1",
                tickActions = tickActions,
                tickInterval = 1000
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(1000)

        verifyHandledActions(tickActions + endActions)
    }

    @Test
    fun `stop() triggers endActions`() {
        val actions = listOf(action(url = "action://tick"))
        init(
            timer(
                duration = 1000,
                id = "timer1",
                endActions = actions
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(500)

        verifyHandledActions(emptyList())

        timerStorage.stop("timer1")

        verifyHandledActions(actions)
    }

    @Test
    fun `stop() triggers endActions if timer is paused`() {
        val actions = listOf(action(url = "action://tick"))
        init(
            timer(
                duration = 1000,
                id = "timer1",
                endActions = actions
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(500)
        timerStorage.pause("timer1")

        verifyHandledActions(emptyList())

        timerStorage.stop("timer1")

        verifyHandledActions(actions)
    }

    @Test
    fun `tickActions are triggered after each interval for infinite timer`() {
        val actions = listOf(action(url = "action://tick"))
        init(
            timer(
                id = "timer1",
                tickActions = actions,
                tickInterval = 1000
            )
        )

        timerStorage.start("timer1")

        verifyHandledActions(emptyList())

        repeat(10) {
            advanceTimeBy(1000)
            verifyHandledActions(actions)
        }
    }

    @Test
    fun `tickActions are not triggered for the last diminished tick`() {
        val endActions = listOf(action(url = "action://end"))
        val tickActions = listOf(action(url = "action://tick"))
        init(
            timer(
                duration = 1500,
                endActions = endActions,
                id = "timer1",
                tickActions = tickActions,
                tickInterval = 1000
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(1000)

        verifyHandledActions(tickActions)

        advanceTimeBy(1000)

        verifyHandledActions(endActions)
    }

    @Test
    fun `tickActions does not trigger after duration`() {
        val actions = listOf(action(url = "action://tick"))
        init(
            timer(
                duration = 5000,
                id = "timer1",
                tickActions = actions,
                tickInterval = 1000
            )
        )

        timerStorage.start("timer1")

        verifyHandledActions(emptyList())

        repeat(5) {
            advanceTimeBy(1000)
            verifyHandledActions(actions)
        }

        repeat(5) {
            advanceTimeBy(1000)
            verifyHandledActions(emptyList())
        }
    }

    @Test
    fun `start() restarts timer after stop()`() {
        init(
            timer(
                id = "timer1",
                tickInterval = 1000,
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(1500)
        timerStorage.stop("timer1")

        assertEquals(1000L, elapsedTime.getValue())

        timerStorage.start("timer1")

        assertEquals(0L, elapsedTime.getValue())

        advanceTimeBy(500)

        assertEquals(0L, elapsedTime.getValue())

        advanceTimeBy(500)

        assertEquals(1000L, elapsedTime.getValue())
    }

    @Test
    fun `start() restarts timer after cancel()`() {
        val actions = listOf(action(url = "action://end"))
        init(
            timer(
                duration = 1000,
                id = "timer1",
                endActions = actions,
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(500)
        timerStorage.cancel("timer1")
        advanceTimeBy(500)

        assertEquals(0L, elapsedTime.getValue())
        verifyHandledActions(emptyList())

        timerStorage.start("timer1")
        advanceTimeBy(500)

        assertEquals(0L, elapsedTime.getValue())
        verifyHandledActions(emptyList())

        advanceTimeBy(500)

        assertEquals(1000L, elapsedTime.getValue())
        verifyHandledActions(actions)
    }

    @Test
    fun `reset() restarts timer`() {
        val actions = listOf(action(url = "action://end"))
        init(
            timer(
                duration = 1000,
                id = "timer1",
                endActions = actions,
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(500)
        timerStorage.reset("timer1")
        advanceTimeBy(500)

        assertEquals(0L, elapsedTime.getValue())
        verifyHandledActions(emptyList())

        advanceTimeBy(500)

        assertEquals(1000L, elapsedTime.getValue())
        verifyHandledActions(actions)
    }

    @Test
    fun `resume() does not reset timer duration`() {
        val actions = listOf(action(url = "action://tick"))
        init(
            timer(
                id = "timer1",
                tickActions = actions,
                tickInterval = 1000
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(500)
        timerStorage.pause("timer1")
        advanceTimeBy(500)

        verifyHandledActions(emptyList())

        timerStorage.resume("timer1")

        verifyHandledActions(emptyList())

        advanceTimeBy(500)

        verifyHandledActions(actions)
    }

    @Test
    fun `variable is updated every tick`() {
        init(
            timer(
                id = "timer1",
                tickInterval = 1000,
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(500)

        assertEquals(0L, elapsedTime.getValue())

        advanceTimeBy(500)

        repeat(10) {
            assertEquals((it + 1L) * 1000, elapsedTime.getValue())
            advanceTimeBy(1000)
        }
    }

    @Test
    fun `variable is updated after duration`() {
        init(
            timer(
                duration = 5000,
                id = "timer1",
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(2500)

        assertEquals(0L, elapsedTime.getValue())

        advanceTimeBy(2500)

        assertEquals(5000L, elapsedTime.getValue())
    }

    @Test
    fun `stop() does not update variable`() {
        init(
            timer(
                id = "timer1",
                tickInterval = 1000,
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(2500)

        assertEquals(2000L, elapsedTime.getValue())

        timerStorage.stop("timer1")

        assertEquals(2000L, elapsedTime.getValue())
    }

    @Test
    fun `pause() updates variable with current value`() {
        init(
            timer(
                duration = 5000,
                id = "timer1",
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(2500)

        assertEquals(0L, elapsedTime.getValue())

        timerStorage.pause("timer1")

        assertEquals(2500L, elapsedTime.getValue())
    }

    @Test
    fun `cancel() cancels started timer`() {
        init(
            timer(
                id = "timer1",
                tickInterval = 1000,
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(2000)

        assertEquals(2000L, elapsedTime.getValue())

        timerStorage.cancel("timer1")

        assertEquals(2000L, elapsedTime.getValue())

        advanceTimeBy(1000)

        assertEquals(2000L, elapsedTime.getValue())
    }

    @Test
    fun `cancel() cancels paused timer`() {
        init(
            timer(
                id = "timer1",
                tickInterval = 1000,
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        advanceTimeBy(2000)
        timerStorage.pause("timer1")
        timerStorage.cancel("timer1")

        assertEquals(2000L, elapsedTime.getValue())

        timerStorage.start("timer1")

        assertEquals(0L, elapsedTime.getValue())

        advanceTimeBy(1000)

        assertEquals(1000L, elapsedTime.getValue())
    }

    @Test
    fun `updated duration is applied after reset`() {
        val duration = Variable.IntegerVariable("duration", 5000)
        variableController.putOrUpdate(duration)

        init(
            timer(
                duration = intExpression("@{duration}"),
                id = "timer1",
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        duration.set(4000)
        advanceTimeBy(5000)

        assertEquals(5000L, elapsedTime.getValue())

        timerStorage.reset("timer1")
        advanceTimeBy(5000)

        assertEquals(4000L, elapsedTime.getValue())
    }

    @Test
    fun `updated tickInterval is applied after reset`() {
        val tickInterval = Variable.IntegerVariable("tick_interval", 1000)
        variableController.putOrUpdate(tickInterval)

        init(
            timer(
                id = "timer1",
                tickInterval = intExpression("@{tick_interval}"),
                valueVariable = "counter"
            )
        )

        timerStorage.start("timer1")
        tickInterval.set(500)
        advanceTimeBy(500)

        assertEquals(0L, elapsedTime.getValue())

        advanceTimeBy(500)

        assertEquals(1000L, elapsedTime.getValue())

        timerStorage.reset("timer1")
        advanceTimeBy(500)

        assertEquals(500L, elapsedTime.getValue())
    }

    private fun init(vararg timers: DivTimer) {
        timerStorage = TimerStorage(
            actionHandler = { actionHandler },
            coroutineScope = testScope,
            reporter = reporter,
            timeSource = testScope.testScheduler.timeSource
        ).apply {
            init(
                timers = timers.asList(),
                localComponent = mockLocalComponent(
                    variableController = variableController
                )
            )
        }
        timerStorage.isEnabled = true
    }

    private fun advanceTimeBy(duration: Long) {
        testScope.testScheduler.advanceTimeBy(duration)
        testScope.testScheduler.runCurrent()
    }

    private fun verifyHandledActions(actions: List<DivAction>) {
        assertEquals(actions, handledActions)
        handledActions.clear()
    }
}
