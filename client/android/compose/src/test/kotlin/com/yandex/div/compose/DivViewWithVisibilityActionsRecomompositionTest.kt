package com.yandex.div.compose

import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.visibilityAction
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.min

@RunWith(AndroidJUnit4::class)
class DivViewWithVisibilityActionsRecompositionTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>().apply {
        mainClock.autoAdvance = false
    }

    private val activity: ComponentActivity
        get() = rule.activity

    private val counter = Variable.IntegerVariable("counter", 0)
    private val visibility = Variable.StringVariable("visibility", "visible")

    private val variableController = DivVariableController().apply {
        declare(counter, visibility)
    }

    private val testScope = TestScope()

    private lateinit var divContext: DivContext

    @Before
    fun setUp() {
        divContext = DivContext(
            baseContext = activity,
            configuration = DivComposeConfiguration(
                reporter = TestReporter(),
                variableController = variableController
            ),
            debugConfiguration = DivDebugConfiguration(
                coroutineScope = testScope
            )
        )
    }

    @Test
    fun `visibility action limit does not reset when ComposeView is recreated with the same context`() {
        val data = data(
            content = text(
                id = "counter",
                text = expression("counter = @{counter}"),
                visibility = visibilityExpression("@{visibility}"),
                visibilityActions = listOf(
                    visibilityAction(delayMs = 500, limit = 2, typed = incrementCounterAction())
                )
            )
        )

        setContent(data)

        rule.onNodeWithTag("counter").assertTextEquals("counter = 0")
        advanceTimeBy(500)
        rule.onNodeWithTag("counter").assertTextEquals("counter = 1")

        activity.setContentView(View(activity))
        setContent(data)

        rule.onNodeWithTag("counter").assertTextEquals("counter = 1")
        advanceTimeBy(500)

        repeat(5) {
            rule.onNodeWithTag("counter").assertTextEquals("counter = 2")
            hideCounter()
            showCounter()
            advanceTimeBy(500)
        }
    }

    @Test
    fun `visibility action limit resets when the view context was cleared`() {
        val data = data(
            content = text(
                id = "counter",
                text = expression("counter = @{counter}"),
                visibility = visibilityExpression("@{visibility}"),
                visibilityActions = listOf(
                    visibilityAction(delayMs = 500, limit = 3, typed = incrementCounterAction())
                )
            )
        )

        setContent(data)

        rule.onNodeWithTag("counter").assertTextEquals("counter = 0")
        advanceTimeBy(500)
        rule.onNodeWithTag("counter").assertTextEquals("counter = 1")

        activity.setContentView(View(activity))
        divContext.clearViewContext(data)
        counter.set(0)
        setContent(data)

        repeat(5) {
            rule.onNodeWithTag("counter").assertTextEquals("counter = ${min(it, 3)}")
            hideCounter()
            showCounter()
            advanceTimeBy(500)
        }
    }

    private fun setContent(data: DivData) {
        activity.setContentView(
            ComposeView(divContext).apply {
                setContent {
                    DivView(data)
                }
            }
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
