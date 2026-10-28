package com.yandex.div.compose

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.tooltips.DivTooltipController
import com.yandex.div.compose.tooltips.DivTooltipEvent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.showTooltipAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivTooltip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivTooltipControllerTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val activity: ComponentActivity
        get() = rule.activity

    private val eventScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @AfterTest
    fun tearDown() {
        eventScope.cancel()
    }

    @Test
    fun `controller is unavailable before card context exists`() {
        val divContext = DivContext(
            activity,
            divConfiguration { reporter = TestReporter() },
        )

        assertNull(divContext.getTooltipController(CARD_ID))
    }

    @Test
    fun `availability contains laid out anchor`() {
        val divContext = setControlledContent(data(anchor()))
        rule.onNodeWithText("Anchor").assertIsDisplayed()

        assertEquals(
            setOf(TOOLTIP_ID),
            controller(divContext).availableTooltips.value,
        )
    }

    @Test
    fun `availability is card local`() {
        val firstCard = data(
            anchor(
                label = "First anchor",
                tooltips = listOf(tooltip(FIRST_TOOLTIP_ID, "First tooltip")),
            ),
            logId = FIRST_CARD_ID,
        )
        val secondCard = data(
            anchor(
                label = "Second anchor",
                tooltips = listOf(tooltip(SECOND_TOOLTIP_ID, "Second tooltip")),
            ),
            logId = SECOND_CARD_ID,
        )
        val divContext = setControlledContent(firstCard, secondCard)
        rule.onNodeWithText("First anchor").assertIsDisplayed()
        rule.onNodeWithText("Second anchor").assertIsDisplayed()

        assertEquals(
            listOf(
                setOf(FIRST_TOOLTIP_ID),
                setOf(SECOND_TOOLTIP_ID),
            ),
            listOf(
                controller(divContext, FIRST_CARD_ID).availableTooltips.value,
                controller(divContext, SECOND_CARD_ID).availableTooltips.value,
            ),
        )
    }

    @Test
    fun `availability is removed after card disposal`() {
        val isComposed = mutableStateOf(true)
        val card = data(anchor())
        val divContext = setControlledContent {
            if (isComposed.value) {
                DivView(card)
            }
        }
        rule.onNodeWithText("Anchor").assertIsDisplayed()
        val controller = controller(divContext)

        rule.runOnIdle {
            isComposed.value = false
        }
        rule.onNodeWithText("Anchor").assertDoesNotExist()

        assertEquals(emptySet(), controller.availableTooltips.value)
    }

    @Test
    fun `availability removes anchor when it leaves composition`() {
        val variableController = DivVariableController()
        val visibility = Variable.StringVariable("visibility", "visible")
        variableController.declare(visibility)
        val divContext = DivContext(
            activity,
            divConfiguration {
                reporter = TestReporter()
                this.variableController = variableController
            },
        )
        activity.setContentView(
            ComposeView(divContext).apply {
                setContent {
                    DivView(
                        data(
                            text(
                                text = "Anchor",
                                visibility = visibilityExpression("@{visibility}"),
                                tooltips = listOf(tooltip(TOOLTIP_ID, "Tooltip text")),
                            )
                        )
                    )
                }
            }
        )
        rule.onNodeWithText("Anchor").assertIsDisplayed()
        assertEquals(
            setOf(TOOLTIP_ID),
            controller(divContext).availableTooltips.value,
        )

        visibility.set("gone")
        rule.onNodeWithText("Anchor").assertDoesNotExist()

        assertEquals(emptySet(), controller(divContext).availableTooltips.value)
    }

    @Test
    fun `pre-layout tooltip is unavailable for existing card`() {
        var result: List<Boolean>? = null
        val divContext = DivContext(activity, divConfiguration { reporter = TestReporter() })
        activity.setContentView(
            ComposeView(divContext).apply {
                setContent {
                    DivView(data(anchor()))
                    DisposableEffect(Unit) {
                        val controller = requireNotNull(divContext.getTooltipController(CARD_ID))
                        result = listOf(
                            controller.showTooltip(TOOLTIP_ID, multiple = false),
                            controller.cancelTooltips(),
                        )
                        onDispose { }
                    }
                }
            }
        )

        rule.runOnIdle {
            assertEquals(listOf(false, true), result)
        }
    }

    @Test
    fun `show result reflects anchor availability`() {
        val divContext = setControlledContent(data(anchor()))
        rule.onNodeWithText("Anchor").assertIsDisplayed()

        val controller = controller(divContext)
        assertTrue(controller.showTooltip(TOOLTIP_ID, multiple = false))
        assertFalse(controller.showTooltip("missing", multiple = false))
    }

    @Test
    fun `show accepts multiple true`() {
        val divContext = setControlledContent(data(anchor()))
        rule.onNodeWithText("Anchor").assertIsDisplayed()

        assertTrue(controller(divContext).showTooltip(TOOLTIP_ID, multiple = true))
    }

    @Test
    fun `show reports success when already requested`() {
        val divContext = setControlledContent(data(anchor()))
        rule.onNodeWithText("Anchor").assertIsDisplayed()

        val controller = controller(divContext)
        controller.showTooltip(TOOLTIP_ID, multiple = false)

        assertTrue(controller.showTooltip(TOOLTIP_ID, multiple = false))
    }

    @Test
    fun `multiple false rejects a tooltip after it was shown and hidden`() {
        val divContext = setControlledContent(data(anchor()))
        val controller = controller(divContext)
        showAndHideTooltip(controller)

        assertFalse(controller.showTooltip(TOOLTIP_ID, multiple = false))

        rule.onNodeWithText("Tooltip text").assertDoesNotExist()
    }

    @Test
    fun `multiple true shows a tooltip after it was shown and hidden`() {
        val divContext = setControlledContent(data(anchor()))
        val controller = controller(divContext)
        showAndHideTooltip(controller)

        assertTrue(controller.showTooltip(TOOLTIP_ID, multiple = true))

        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    @Test
    fun `multiple false rejects a tooltip previously shown with multiple true`() {
        val divContext = setControlledContent(data(anchor()))
        val controller = controller(divContext)
        showAndHideTooltip(controller, multiple = true)

        assertFalse(controller.showTooltip(TOOLTIP_ID, multiple = false))
    }

    @Test
    fun `showing an active one-time tooltip is accepted without duplication`() {
        val divContext = setControlledContent(data(anchor()))
        val controller = controller(divContext)
        controller.showTooltip(TOOLTIP_ID, multiple = false)
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()

        assertTrue(controller.showTooltip(TOOLTIP_ID, multiple = false))

        rule.onAllNodesWithText("Tooltip text").assertCountEquals(1)
    }

    @Test
    fun `cancel does not allow another one-time show`() {
        val divContext = setControlledContent(data(anchor()))
        val controller = controller(divContext)
        controller.showTooltip(TOOLTIP_ID, multiple = false)
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
        controller.cancelTooltips()
        rule.onNodeWithText("Tooltip text").assertDoesNotExist()

        assertFalse(controller.showTooltip(TOOLTIP_ID, multiple = false))
    }

    @Test
    fun `request hidden before display does not consume one-time show`() {
        val divContext = setControlledContent(data(anchor()))
        val controller = controller(divContext)
        rule.runOnIdle {
            controller.showTooltip(TOOLTIP_ID, multiple = false)
            controller.hideTooltip(TOOLTIP_ID)
        }
        rule.waitForIdle()

        assertTrue(controller.showTooltip(TOOLTIP_ID, multiple = false))

        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    @Test
    fun `one-time show history is card local`() {
        val firstCard = data(anchor(), logId = FIRST_CARD_ID)
        val secondCard = data(anchor(), logId = SECOND_CARD_ID)
        val divContext = setControlledContent(firstCard, secondCard)
        showAndHideTooltip(controller(divContext, FIRST_CARD_ID))

        assertTrue(controller(divContext, SECOND_CARD_ID).showTooltip(TOOLTIP_ID, multiple = false))

        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
    }

    @Test
    fun `multiple true keeps tooltip visible when requested during exit`() {
        val divContext = setControlledContent(data(anchor()))
        val controller = controller(divContext)
        controller.showTooltip(TOOLTIP_ID, multiple = false)
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
        rule.mainClock.autoAdvance = false
        controller.hideTooltip(TOOLTIP_ID)
        rule.mainClock.advanceTimeByFrame()

        controller.showTooltip(TOOLTIP_ID, multiple = true)
        rule.mainClock.advanceTimeBy(1000)
        rule.mainClock.autoAdvance = true

        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
        rule.onAllNodesWithText("Tooltip text").assertCountEquals(1)
    }

    @Test
    fun `one-time tooltip is not restored when card returns to composition`() {
        val isComposed = mutableStateOf(true)
        val card = data(anchor())
        val divContext = setControlledContent {
            if (isComposed.value) {
                DivView(card)
            }
        }
        controller(divContext).showTooltip(TOOLTIP_ID, multiple = false)
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
        rule.runOnIdle { isComposed.value = false }
        rule.onNodeWithText("Tooltip text").assertDoesNotExist()

        rule.runOnIdle { isComposed.value = true }

        rule.onNodeWithText("Anchor").assertIsDisplayed()
        rule.onNodeWithText("Tooltip text").assertDoesNotExist()
    }

    @Test
    fun `one-time tooltip is not restored when anchor becomes visible again`() {
        val variableController = DivVariableController()
        val visibility = Variable.StringVariable("visibility", "visible")
        variableController.declare(visibility)
        val divContext = DivContext(
            activity,
            divConfiguration {
                reporter = TestReporter()
                this.variableController = variableController
            },
        )
        val card = data(text(
            text = "Anchor",
            visibility = visibilityExpression("@{visibility}"),
            tooltips = listOf(tooltip(TOOLTIP_ID, "Tooltip text")),
        ))
        activity.setContentView(ComposeView(divContext).apply {
            setContent { DivView(card) }
        })
        rule.onNodeWithText("Anchor").assertIsDisplayed()
        controller(divContext).showTooltip(TOOLTIP_ID, multiple = false)
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
        visibility.set("gone")
        rule.onNodeWithText("Tooltip text").assertDoesNotExist()

        visibility.set("visible")

        rule.onNodeWithText("Anchor").assertIsDisplayed()
        rule.onNodeWithText("Tooltip text").assertDoesNotExist()
    }

    @Test
    fun `show targets matching card`() {
        val firstCard = data(
            anchor(
                label = "First anchor",
                tooltips = listOf(tooltip(TOOLTIP_ID, "First tooltip")),
            ),
            logId = FIRST_CARD_ID,
        )
        val secondCard = data(
            anchor(
                label = "Second anchor",
                tooltips = listOf(tooltip(TOOLTIP_ID, "Second tooltip")),
            ),
            logId = SECOND_CARD_ID,
        )
        val divContext = setControlledContent(firstCard, secondCard)
        rule.onNodeWithText("First anchor").assertIsDisplayed()
        rule.onNodeWithText("Second anchor").assertIsDisplayed()

        controller(divContext, FIRST_CARD_ID).showTooltip(TOOLTIP_ID, multiple = false)

        rule.onNodeWithText("First tooltip").assertIsDisplayed()
        rule.onNodeWithText("Second tooltip").assertDoesNotExist()
    }

    @Test
    fun `hide removes requested tooltip`() {
        val divContext = setControlledContent(data(anchor()))
        rule.onNodeWithText("Anchor").assertIsDisplayed()
        controller(divContext).showTooltip(TOOLTIP_ID, multiple = false)
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()

        controller(divContext).hideTooltip(TOOLTIP_ID)

        rule.onNodeWithText("Tooltip text").assertDoesNotExist()
    }

    @Test
    fun `hide reports whether request was removed`() {
        val divContext = setControlledContent(data(anchor()))
        rule.onNodeWithText("Anchor").assertIsDisplayed()

        val controller = controller(divContext)
        controller.showTooltip(TOOLTIP_ID, multiple = false)

        assertTrue(controller.hideTooltip(TOOLTIP_ID))
        assertFalse(controller.hideTooltip(TOOLTIP_ID))
    }

    @Test
    fun `cancel removes all requested tooltips`() {
        val divContext = setControlledContent(
            data(
                anchor(
                    tooltips = listOf(
                        tooltip("first", "First tooltip"),
                        tooltip("second", "Second tooltip"),
                    )
                )
            )
        )
        rule.onNodeWithText("Anchor").assertIsDisplayed()
        controller(divContext).showTooltip("first", multiple = true)
        controller(divContext).showTooltip("second", multiple = true)
        rule.onNodeWithText("First tooltip").assertIsDisplayed()
        rule.onNodeWithText("Second tooltip").assertIsDisplayed()

        controller(divContext).cancelTooltips()

        rule.onNodeWithText("First tooltip").assertDoesNotExist()
        rule.onNodeWithText("Second tooltip").assertDoesNotExist()
    }

    @Test
    fun `cancel is accepted for existing card controller`() {
        val divContext = setControlledContent(data(anchor()))
        rule.onNodeWithText("Anchor").assertIsDisplayed()

        assertTrue(controller(divContext).cancelTooltips())
    }

    @Test
    fun `show fails after card disposal`() {
        val isComposed = mutableStateOf(true)
        val card = data(anchor())
        val divContext = setControlledContent {
            if (isComposed.value) {
                DivView(card)
            }
        }
        rule.onNodeWithText("Anchor").assertIsDisplayed()
        val controller = controller(divContext)

        rule.runOnIdle {
            isComposed.value = false
        }
        rule.onNodeWithText("Anchor").assertDoesNotExist()

        assertFalse(controller.showTooltip(TOOLTIP_ID, multiple = false))
    }

    @Test
    fun `controller is removed and emptied after view context is cleared`() {
        val card = data(anchor())
        val divContext = setControlledContent(card)
        rule.onNodeWithText("Anchor").assertIsDisplayed()
        val controller = controller(divContext)

        divContext.clearViewContext(card)

        assertNull(divContext.getTooltipController(CARD_ID))
        assertEquals(emptySet(), controller.availableTooltips.value)
    }

    @Test
    fun `shown event is emitted for internal action`() {
        val divContext = setControlledContent(
            data(
                anchor(
                    label = "Show tooltip",
                    action = action(typed = showTooltipAction(id = TOOLTIP_ID)),
                )
            )
        )
        rule.onNodeWithText("Show tooltip").assertIsDisplayed()
        val controller = controller(divContext)
        val events = collectEvents(controller)

        rule.onNodeWithText("Show tooltip").performClick()
        rule.waitForIdle()

        assertEquals(listOf(DivTooltipEvent.Shown(CARD_ID, TOOLTIP_ID)), events)
    }

    @Test
    fun `dismissed event is emitted when controller hides tooltip`() {
        val divContext = setControlledContent(data(anchor()))
        rule.onNodeWithText("Anchor").assertIsDisplayed()
        val controller = controller(divContext)
        controller.showTooltip(TOOLTIP_ID, multiple = false)
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
        val events = collectEvents(controller)

        controller.hideTooltip(TOOLTIP_ID)
        rule.waitForIdle()

        assertEquals(listOf(DivTooltipEvent.Dismissed(CARD_ID, TOOLTIP_ID)), events)
    }

    private fun showAndHideTooltip(controller: DivTooltipController, multiple: Boolean = false) {
        controller.showTooltip(TOOLTIP_ID, multiple)
        rule.onNodeWithText("Tooltip text").assertIsDisplayed()
        controller.hideTooltip(TOOLTIP_ID)
        rule.onNodeWithText("Tooltip text").assertDoesNotExist()
    }

    private fun setControlledContent(vararg data: DivData): DivContext {
        return setControlledContent {
            Column {
                data.forEach { DivView(it) }
            }
        }
    }

    private fun setControlledContent(content: @Composable () -> Unit): DivContext {
        val divContext = DivContext(activity, divConfiguration { reporter = TestReporter() })
        activity.setContentView(
            ComposeView(divContext).apply {
                setContent(content)
            }
        )
        rule.waitForIdle()
        return divContext
    }

    private fun controller(
        divContext: DivContext,
        cardId: String = CARD_ID,
    ): DivTooltipController = requireNotNull(divContext.getTooltipController(cardId))

    private fun collectEvents(controller: DivTooltipController): List<DivTooltipEvent> {
        val events = mutableListOf<DivTooltipEvent>()
        rule.runOnIdle {
            eventScope.launch {
                controller.events.collect { events.add(it) }
            }
        }
        return events
    }

    private fun anchor(
        label: String = "Anchor",
        action: com.yandex.div2.DivAction? = null,
        tooltips: List<DivTooltip> = listOf(tooltip(TOOLTIP_ID, "Tooltip text")),
    ): Div = text(
        text = label,
        action = action,
        tooltips = tooltips,
    )

    private fun tooltip(id: String, label: String) = DivTooltip(
        id = id,
        position = constant(DivTooltip.Position.CENTER),
        div = text(text = label),
        duration = constant(0L),
    )

    private companion object {
        const val CARD_ID = "test"
        const val FIRST_CARD_ID = "first"
        const val FIRST_TOOLTIP_ID = "first_tooltip"
        const val SECOND_CARD_ID = "second"
        const val SECOND_TOOLTIP_ID = "second_tooltip"
        const val TOOLTIP_ID = "tooltip"
    }
}
