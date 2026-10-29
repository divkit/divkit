package com.yandex.div.compose.views.transition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.click
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.compose.setContent
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.compose.views.state.RetainedStateInteractionBarrier
import com.yandex.div.compose.views.text.DivTextView
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.state
import com.yandex.div.test.data.switch
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textRange
import com.yandex.div.test.data.typedValue
import com.yandex.div2.Div
import com.yandex.div2.DivExtension
import com.yandex.div2.DivTransitionSelector
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivStateChangeInteractionTest : StateChangeTransitionTestBase() {
    @Test
    fun `retained content clears semantics`() {
        composeRule.setContent {
            Box(
                Modifier
                    .size(100.dp)
                    .disableRetainedStateInteractions()
            ) {
                Box(
                    Modifier
                        .size(100.dp)
                        .semantics { contentDescription = OUTGOING }
                )
            }
        }

        composeRule.onNodeWithContentDescription(OUTGOING).assertDoesNotExist()
    }

    @Test
    fun `retained barrier blocks content and allows pointer input to reach parent`() {
        var parentClicked = false
        var retainedClicked = false
        composeRule.setContent {
            Box(
                Modifier
                    .size(100.dp)
                    .clickable { parentClicked = true }
            ) {
                Box(
                    Modifier.size(100.dp)
                ) {
                    Box(
                        Modifier
                            .size(100.dp)
                            .clickable { retainedClicked = true }
                    )
                    RetainedStateInteractionBarrier()
                }
            }
        }

        composeRule.onRoot().performTouchInput { click(Offset(50f, 50f)) }
        assertFalse(retainedClicked)
        assertTrue(parentClicked)
    }

    @Test
    fun `retained custom does not receive pointer input`() {
        var clicked = false
        val factory = object : DivCustomViewFactory {
            @Composable
            override fun Content(modifier: Modifier, environment: DivCustomEnvironment) {
                Box(modifier.clickable { clicked = true })
            }
        }
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            customViewFactories = mapOf("clickable" to factory),
            outgoing = custom(
                type = "clickable",
                height = fixed(100),
                id = OUTGOING_ID,
                transitionOut = fade(duration = TRANSITION_DURATION_MS),
                width = fixed(100),
            ),
            incoming = text(
                text = constant(INCOMING),
                height = fixed(100),
                id = INCOMING_ID,
                width = fixed(100),
            ),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onRoot().performTouchInput { click(Offset(50f, 50f)) }

        assertFalse(clicked)
    }

    @Test
    fun `retained switch does not toggle and allows pointer input to reach parent`() {
        val checked = Variable.BooleanVariable(CLICKED_VARIABLE, false)
        val composedIds = mutableSetOf<String>()
        val handler = TrackingExtensionHandler(composedIds)
        var parentClicked = false
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = switch(
                extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
                id = OUTGOING_ID,
                isOnVariable = CLICKED_VARIABLE,
                transitionOut = fade(duration = TRANSITION_DURATION_MS),
                width = fixed(100),
                height = fixed(100),
            ),
            incoming = text(text = constant(INCOMING), width = fixed(100), height = fixed(100)),
            extraVariables = listOf(checked),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
            modifier = Modifier.size(100.dp).clickable { parentClicked = true },
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.runOnIdle {
            assertEquals(emptySet(), composedIds)
            assertFalse(handler.transitionStateOf(OUTGOING_ID).snapshots.exiting.single().layer.isReleased)
        }

        composeRule.onRoot().performTouchInput { click(Offset(50f, 50f)) }
        assertEquals(false, checked.getValue())
        assertTrue(parentClicked)
    }

    @Test
    fun `retained text range link does not block active pointer input`() {
        val outgoingClicked = Variable.BooleanVariable(OUTGOING_CLICKED_VARIABLE, false)
        val variableController = DivVariableController().apply { declare(outgoingClicked) }
        val component = mockLocalComponent(
            reporter = reporter,
            variableController = variableController,
        )
        val textData = (text(
            text = constant(OUTGOING),
            ranges = listOf(
                textRange(
                    start = 0,
                    end = OUTGOING.length.toLong(),
                    actions = listOf(
                        action(
                            typed = setVariableAction(
                                name = OUTGOING_CLICKED_VARIABLE,
                                value = typedValue(true),
                            )
                        )
                    )
                )
            ),
        ) as Div.Text).value
        var parentClicked = false
        val configuration = divConfiguration {
            reporter = this@DivStateChangeInteractionTest.reporter
            this.variableController = variableController
        }
        composeRule.setContentWithDivContext(configuration) {
            CompositionLocalProvider(
                LocalComponent provides component,
                LocalStateChangeTransitionContext provides StateChangeTransitionContext(
                    contentActive = false,
                ),
            ) {
                Box(
                    Modifier
                        .size(100.dp)
                        .clickable { parentClicked = true }
                ) {
                    DivTextView(
                        modifier = Modifier
                            .size(100.dp)
                            .disableRetainedStateInteractions(),
                        data = textData,
                        focusState = null,
                    )
                }
            }
        }

        composeRule.onRoot().performTouchInput { click(Offset(50f, 50f)) }

        assertEquals(false, outgoingClicked.getValue())
        assertTrue(parentClicked)
    }

    @Test
    fun `retained selectable text does not block active long tap`() {
        val textData = (text(
            text = constant(OUTGOING),
            selectable = true,
        ) as Div.Text).value
        var parentLongClicked = false
        val component = mockLocalComponent(reporter = reporter)
        val configuration = divConfiguration {
            reporter = this@DivStateChangeInteractionTest.reporter
        }
        composeRule.setContentWithDivContext(configuration) {
            CompositionLocalProvider(
                LocalComponent provides component,
                LocalStateChangeTransitionContext provides StateChangeTransitionContext(
                    contentActive = false,
                ),
            ) {
                Box(
                    Modifier
                        .size(100.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { parentLongClicked = true },
                        )
                ) {
                    DivTextView(
                        modifier = Modifier
                            .size(100.dp)
                            .disableRetainedStateInteractions(),
                        data = textData,
                        focusState = null,
                    )
                }
            }
        }

        composeRule.onRoot().performTouchInput { longClick(Offset(50f, 50f)) }
        assertTrue(parentLongClicked)
    }
}
