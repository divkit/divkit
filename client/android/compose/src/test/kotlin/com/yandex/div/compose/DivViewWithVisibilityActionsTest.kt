package com.yandex.div.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.disappearAction
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.visibilityAction
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivVariable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewWithVisibilityActionsTest {

    @get:Rule
    val rule = createComposeRule().apply {
        mainClock.autoAdvance = false
    }

    private val counter = Variable.IntegerVariable("counter", 0)
    private val visibility = Variable.StringVariable("visibility", "visible")
    val viewHeight = Variable.IntegerVariable("view_height", 0)

    private val variableController = DivVariableController().apply {
        declare(counter, visibility, viewHeight)
    }

    private val configuration = DivComposeConfiguration(
        reporter = TestReporter(),
        variableController = variableController
    )

    private val testScope = TestScope()

    private val debugConfiguration = DivDebugConfiguration(
        coroutineScope = testScope
    )

    @Test
    fun `visibility action is triggered after delay`() {
        setContent(
            text(
                id = "counter",
                text = expression("counter = @{counter}"),
                visibilityActions = listOf(
                    visibilityAction(delayMs = 500, typed = incrementCounterAction())
                )
            )
        )

        advanceTimeBy(250)

        assertEquals(0L, counter.getValue())

        advanceTimeBy(250)

        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `visibility action is triggered when visibility changes`() {
        setContent(
            text(
                id = "counter",
                text = expression("counter = @{counter}"),
                visibility = visibilityExpression("@{visibility}"),
                visibilityActions = listOf(
                    visibilityAction(delayMs = 500, limit = 0, typed = incrementCounterAction())
                )
            )
        )

        advanceTimeBy(500)

        assertEquals(1L, counter.getValue())

        hideCounter()
        showCounter()

        assertEquals(1L, counter.getValue())

        advanceTimeBy(500)

        assertEquals(2L, counter.getValue())
    }

    @Test
    fun `visibility action is cancelled when view disappears`() {
        setContent(
            text(
                id = "counter",
                text = expression("counter = @{counter}"),
                visibility = visibilityExpression("@{visibility}"),
                visibilityActions = listOf(
                    visibilityAction(delayMs = 500, typed = incrementCounterAction())
                )
            )
        )

        advanceTimeBy(400)

        hideCounter()
        showCounter()

        advanceTimeBy(250)

        assertEquals(0L, counter.getValue())

        advanceTimeBy(250)

        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `visibility action is not triggered when limit is reached`() {
        setContent(
            text(
                id = "counter",
                text = expression("counter = @{counter}"),
                visibility = visibilityExpression("@{visibility}"),
                visibilityActions = listOf(
                    visibilityAction(delayMs = 500, limit = 3, typed = incrementCounterAction())
                )
            )
        )

        repeat(5) {
            assertEquals(min(it, 3).toLong(), counter.getValue())
            hideCounter()
            showCounter()
            advanceTimeBy(500)
        }
    }

    @Test
    fun `visibility action is triggered every time if limit is 0`() {
        setContent(
            text(
                id = "counter",
                text = expression("counter = @{counter}"),
                visibility = visibilityExpression("@{visibility}"),
                visibilityActions = listOf(
                    visibilityAction(delayMs = 500, limit = 0, typed = incrementCounterAction())
                )
            )
        )

        repeat(5) {
            assertEquals(it.toLong(), counter.getValue())
            hideCounter()
            showCounter()
            advanceTimeBy(500)
        }
    }

    @Test
    fun `disappear action is triggered when visibility changes`() {
        setContent(
            text(
                id = "counter",
                text = expression("counter = @{counter}"),
                visibility = visibilityExpression("@{visibility}"),
                disappearActions = listOf(
                    disappearAction(delayMs = 500, typed = incrementCounterAction())
                )
            )
        )

        advanceTimeBy(500)

        hideCounter()

        assertEquals(0L, counter.getValue())

        advanceTimeBy(500)

        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `disappear action is not triggered when limit is reached`() {
        setContent(
            text(
                id = "counter",
                text = expression("counter = @{counter}"),
                visibility = visibilityExpression("@{visibility}"),
                disappearActions = listOf(
                    disappearAction(delayMs = 500, limit = 3, typed = incrementCounterAction())
                )
            )
        )

        repeat(5) {
            rule.onNodeWithTag("counter").assertTextEquals("counter = ${min(it, 3)}")
            hideCounter()
            advanceTimeBy(500)
            showCounter()
        }
    }

    @Test
    fun `disappear action is triggered every time when limit is 0`() {
        setContent(
            text(
                id = "counter",
                text = expression("counter = @{counter}"),
                visibility = visibilityExpression("@{visibility}"),
                disappearActions = listOf(
                    disappearAction(delayMs = 500, limit = 0, typed = incrementCounterAction())
                )
            )
        )

        repeat(5) {
            rule.onNodeWithTag("counter").assertTextEquals("counter = $it")
            hideCounter()
            advanceTimeBy(500)
            showCounter()
        }
    }

    @Test
    fun `disappear action is cancelled when view becomes visible`() {
        setContent(
            text(
                id = "counter",
                text = expression("counter = @{counter}"),
                visibility = visibilityExpression("@{visibility}"),
                disappearActions = listOf(
                    disappearAction(delayMs = 500, typed = incrementCounterAction())
                )
            )
        )

        hideCounter()

        advanceTimeBy(400)

        showCounter()
        hideCounter()

        advanceTimeBy(250)

        assertEquals(0L, counter.getValue())

        advanceTimeBy(250)

        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `visibility action with percentage is not triggered when view appears below threshold`() {
        setContent(
            container(
                id = "viewport",
                height = fixed(
                    value = constant(100),
                ),
                items = listOf(
                    text(
                        id = "counter",
                        text = expression("counter = @{counter}"),
                        height = fixed(
                            value = constant(200),
                        ),
                        visibilityActions = listOf(
                            visibilityAction(
                                delayMs = 500,
                                percentage = 50,
                                typed = incrementCounterAction()
                            )
                        )
                    )
                )
            )
        )

        advanceTimeBy(500)

        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `visibility action with percentage is triggered when view crosses threshold`() {
        viewHeight.set(200)

        setContent(
            container(
                id = "viewport",
                height = fixed(
                    value = constant(100),
                ),
                items = listOf(
                    text(
                        id = "counter",
                        text = expression("counter = @{counter}"),
                        height = fixed(
                            value = intExpression("@{view_height}"),
                        ),
                        visibilityActions = listOf(
                            visibilityAction(
                                delayMs = 500,
                                percentage = 60,
                                limit = 0,
                                typed = incrementCounterAction()
                            )
                        )
                    )
                )
            )
        )

        advanceTimeBy(500)
        assertEquals(0L, counter.getValue())

        updateHeight(70)

        advanceTimeBy(500)
        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `visibility action with percentage is cancelled when view drops below threshold`() {
        viewHeight.set(100)

        setContent(
            container(
                id = "viewport",
                height = fixed(
                    value = constant(100),
                ),
                items = listOf(
                    text(
                        id = "counter",
                        text = expression("counter = @{counter}"),
                        height = fixed(
                            value = intExpression("@{view_height}"),
                        ),
                        visibilityActions = listOf(
                            visibilityAction(
                                delayMs = 500,
                                percentage = 60,
                                typed = incrementCounterAction()
                            )
                        )
                    )
                )
            )
        )

        advanceTimeBy(300)

        updateHeight(200)

        advanceTimeBy(200)
        assertEquals(0L, counter.getValue())

        updateHeight(100)

        advanceTimeBy(500)
        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `disappear action with percentage is not triggered when view stays above threshold`() {
        setContent(
            container(
                id = "viewport",
                height = fixed(
                    value = constant(100),
                ),
                items = listOf(
                    text(
                        id = "counter",
                        text = expression("counter = @{counter}"),
                        height = fixed(
                            value = constant(200),
                        ),
                        disappearActions = listOf(
                            disappearAction(
                                delayMs = 500,
                                percentage = 40,
                                typed = incrementCounterAction()
                            )
                        )
                    )
                )
            )
        )

        advanceTimeBy(500)
        assertEquals(0L, counter.getValue())
    }

    @Test
    fun `disappear action with percentage is triggered when view crosses threshold`() {
        viewHeight.set(100)

        setContent(
            container(
                id = "viewport",
                height = fixed(
                    value = constant(100),
                ),
                items = listOf(
                    text(
                        id = "counter",
                        text = expression("counter = @{counter}"),
                        height = fixed(
                            value = intExpression("@{view_height}"),
                        ),
                        disappearActions = listOf(
                            disappearAction(
                                delayMs = 500,
                                percentage = 40,
                                limit = 0,
                                typed = incrementCounterAction()
                            )
                        )
                    )
                )
            )
        )

        advanceTimeBy(500)
        assertEquals(0L, counter.getValue())

        updateHeight(300)

        advanceTimeBy(500)
        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `disappear action with percentage is cancelled when view goes back above threshold`() {
        viewHeight.set(100)

        setContent(
            container(
                id = "viewport",
                height = fixed(
                    value = constant(100),
                ),
                items = listOf(
                    text(
                        id = "counter",
                        text = expression("counter = @{counter}"),
                        height = fixed(
                            value = intExpression("@{view_height}"),
                        ),
                        disappearActions = listOf(
                            disappearAction(
                                delayMs = 500,
                                percentage = 51,
                                typed = incrementCounterAction()
                            )
                        )
                    )
                )
            )
        )

        updateHeight(200)

        advanceTimeBy(300)

        updateHeight(100)

        advanceTimeBy(200)
        assertEquals(0L, counter.getValue())

        updateHeight(200)

        advanceTimeBy(500)
        assertEquals(1L, counter.getValue())
    }

    private fun setContent(
        content: Div,
        variables: List<DivVariable>? = null
    ) {
        rule.setContent(
            configuration = configuration,
            debugConfiguration = debugConfiguration,
            data = data(content, variables = variables)
        )
    }

    private fun showCounter() {
        withAutoAdvance {
            visibility.set("visible")
            rule.onNodeWithTag("counter").assertIsDisplayed()
        }
    }

    private fun hideCounter() {
        withAutoAdvance {
            visibility.set("gone")
            rule.onNodeWithTag("counter").assertDoesNotExist()
        }
    }

    private fun updateHeight(height: Int) {
        withAutoAdvance {
            viewHeight.set(height.toLong())
            rule.onNodeWithTag("counter").assertIsDisplayed()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun advanceTimeBy(duration: Long) {
        testScope.testScheduler.advanceTimeBy(duration)
        testScope.testScheduler.runCurrent()
        rule.mainClock.advanceTimeBy(duration)
        rule.mainClock.advanceTimeByFrame()
    }

    private fun withAutoAdvance(block: () -> Unit) {
        rule.mainClock.autoAdvance = true
        block()
        rule.mainClock.autoAdvance = false
    }
}

private fun incrementCounterAction(): DivActionTyped {
    return setVariableAction("counter", typedValue(intExpression("@{counter + 1}")))
}
