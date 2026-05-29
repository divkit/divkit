package com.yandex.div.compose

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.core.view.updateLayoutParams
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
import com.yandex.div2.Div
import com.yandex.div2.DivActionTyped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowLooper
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewHostWithVisibilityActionsTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>().apply {
        mainClock.autoAdvance = false
    }

    private val counter = Variable.IntegerVariable("counter", 0)
    private val contentHeight = Variable.IntegerVariable("content_height", 100)

    private val variableController = DivVariableController().apply {
        declare(counter, contentHeight)
    }

    private val configuration = DivComposeConfiguration(
        reporter = TestReporter(),
        variableController = variableController,
    )

    private val testScope = TestScope()

    private val debugConfiguration = DivDebugConfiguration(
        coroutineScope = testScope,
    )

    private lateinit var host: DivViewHost

    @Test
    fun `visibility action triggers when element is half visible in composeView`() {
        setClippedContent(
            counterWithVisibilityAction(elementHeightDp = 200, percentage = 50),
            composeViewHeightDp = 100,
        )

        advanceTimeBy(500)

        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `visibility action is not triggered below percentage threshold in clipped composeView`() {
        setClippedContent(
            counterWithVisibilityAction(elementHeightDp = 200, percentage = 60),
            composeViewHeightDp = 100,
        )

        advanceTimeBy(500)

        assertEquals(0L, counter.getValue())
    }

    @Test
    fun `visibility action triggers when composeView height increases`() {
        setClippedContent(
            counterWithVisibilityAction(elementHeightDp = 200, percentage = 60),
            composeViewHeightDp = 100,
        )

        advanceTimeBy(500)
        assertEquals(0L, counter.getValue())

        resizeComposeViewHeight(200)

        advanceTimeBy(500)
        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `visibility action is cancelled when composeView shrinks before delay completes`() {
        setClippedContent(
            counterWithVisibilityAction(elementHeightDp = 200, percentage = 60),
            composeViewHeightDp = 200,
        )

        advanceTimeBy(300)

        resizeComposeViewHeight(100)

        advanceTimeBy(500)
        assertEquals(0L, counter.getValue())

        resizeComposeViewHeight(200)

        advanceTimeBy(500)
        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `disappear action is not triggered while element stays above threshold in composeView`() {
        setClippedContent(
            counterWithDisappearAction(elementHeightDp = 200, percentage = 40),
            composeViewHeightDp = 200,
        )

        advanceTimeBy(500)

        assertEquals(0L, counter.getValue())
    }

    @Test
    fun `disappear action triggers when composeView height decreases`() {
        setClippedContent(
            counterWithDisappearAction(elementHeightDp = 200, percentage = 40),
            composeViewHeightDp = 100,
        )

        advanceTimeBy(500)
        assertEquals(0L, counter.getValue())

        resizeComposeViewHeight(50)

        advanceTimeBy(500)
        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `disappear action is cancelled when composeView height increases before delay completes`() {
        setClippedContent(
            counterWithDisappearAction(elementHeightDp = 200, percentage = 51),
            composeViewHeightDp = 200,
        )

        advanceTimeBy(500)
        assertEquals(0L, counter.getValue())

        resizeComposeViewHeight(100)
        advanceTimeBy(300)

        resizeComposeViewHeight(200)
        advanceTimeBy(200)
        assertEquals(0L, counter.getValue())

        resizeComposeViewHeight(100)
        advanceTimeBy(500)
        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `visibility action reacts to inner compose content resize with large composeView`() {
        contentHeight.set(100)

        setClippedContent(
            container(
                id = "viewport",
                height = fixed(value = intExpression("@{content_height}")),
                items = listOf(
                    counterWithVisibilityAction(
                        elementHeightDp = 200,
                        percentage = 60,
                    ),
                ),
            ),
            composeViewHeightDp = 400,
        )

        advanceTimeBy(500)
        assertEquals(0L, counter.getValue())

        updateContentHeight(160)

        advanceTimeBy(500)
        assertEquals(1L, counter.getValue())
    }

    private fun setClippedContent(
        content: Div,
        composeViewHeightDp: Int,
    ) {
        setContent(
            content = content,
            composeViewHeightDp = composeViewHeightDp,
        )
        composeRule.onNodeWithTag("counter").assertIsDisplayed()
    }

    private fun setContent(
        content: Div,
        composeViewHeightDp: Int,
    ) {
        val activity = composeRule.activity
        val divContext = DivContext(
            baseContext = activity,
            configuration = configuration,
            debugConfiguration = debugConfiguration,
        )
        host = DivViewHost(divContext)
        host.setContent {
            DivView(data = data(content))
        }

        activity.setContentView(
            FrameLayout(activity).apply {
                addView(
                    host.composeView,
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        viewportHeightPx(composeViewHeightDp),
                    ),
                )
            }
        )

        composeRule.waitForIdle()
    }

    private fun resizeComposeViewHeight(heightDp: Int) {
        host.composeView.updateLayoutParams {
            height = viewportHeightPx(heightDp)
        }
        (host.composeView.parent as? ViewGroup)?.requestLayout()
        ShadowLooper.idleMainLooper()
        composeRule.waitForIdle()
    }

    private fun updateContentHeight(heightDp: Int) {
        withAutoAdvance {
            contentHeight.set(heightDp.toLong())
            composeRule.onNodeWithTag("counter").assertIsDisplayed()
        }
    }

    private fun viewportHeightPx(dp: Int): Int {
        return (dp * composeRule.activity.resources.displayMetrics.density).toInt()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun advanceTimeBy(duration: Long) {
        testScope.testScheduler.advanceTimeBy(duration)
        testScope.testScheduler.runCurrent()
        composeRule.mainClock.advanceTimeBy(duration)
        composeRule.mainClock.advanceTimeByFrame()
    }

    private fun withAutoAdvance(block: () -> Unit) {
        composeRule.mainClock.autoAdvance = true
        block()
        composeRule.mainClock.autoAdvance = false
    }

    private fun counterWithVisibilityAction(
        elementHeightDp: Int,
        percentage: Int,
        delayMs: Int = 500,
    ): Div = text(
        id = "counter",
        text = expression("counter = @{counter}"),
        height = fixed(value = constant(elementHeightDp.toLong())),
        visibilityActions = listOf(
            visibilityAction(
                delayMs = delayMs.toLong(),
                percentage = percentage,
                typed = incrementCounterAction(),
            )
        ),
    )

    private fun counterWithDisappearAction(
        elementHeightDp: Int,
        percentage: Int,
        delayMs: Int = 500,
    ): Div = text(
        id = "counter",
        text = expression("counter = @{counter}"),
        height = fixed(value = constant(elementHeightDp.toLong())),
        disappearActions = listOf(
            disappearAction(
                delayMs = delayMs.toLong(),
                percentage = percentage,
                typed = incrementCounterAction(),
            )
        ),
    )

    private fun incrementCounterAction(): DivActionTyped {
        return setVariableAction("counter", typedValue(intExpression("@{counter + 1}")))
    }
}
