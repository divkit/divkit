package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.action
import com.yandex.div.test.data.clearFocusAction
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.focusElementAction
import com.yandex.div.test.data.input
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div2.DivAction
import com.yandex.div2.DivFocus
import com.yandex.div2.DivPatch
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class DivViewFocusElementTest {
    @get:Rule
    val rule = createComposeRule()

    private val actions = TestExternalActionHandler()
    private val reporter = TestReporter()
    private val focus = DivFocus(
        onFocus = listOf(action(id = "focus")),
        onBlur = listOf(action(id = "blur")),
    )
    private val card = data(
        container(items = listOf(
            input(id = "input", textVariable = "value", focus = focus),
            text(id = "text", text = "Text", focus = focus),
        )),
        variables = listOf(variable("value", "Input")),
    )
    private val divContext = DivContext(
        RuntimeEnvironment.getApplication(),
        DivConfiguration(actionHandler = actions, reporter = reporter),
    )

    @Test
    fun `focus action focuses an input`() {
        setContent()

        perform(action(typed = focusElementAction("input")))

        rule.onNode(hasSetTextAction()).assertIsFocused()
    }

    @Test
    fun `focus moves from input to text and dispatches callbacks`() {
        setContent()
        perform(action(typed = focusElementAction("input")))
        actions.reset()

        perform(action(typed = focusElementAction("text")))

        rule.onNodeWithTag("text").assertIsFocused()
        assertEquals(listOf("blur", "focus"), actions.handledActions.map { it.id })
    }

    @Test
    fun `clear focus clears the focused input`() {
        setContent()
        perform(action(typed = focusElementAction("input")))

        perform(action(typed = clearFocusAction()))

        rule.onNode(hasSetTextAction()).assertIsNotFocused()
    }

    @Test
    fun `focus action uses the replacement target after patch`() {
        setContent()
        patch(DivPatch.Change("input", listOf(input(id = "input", textVariable = "value"))))

        perform(action(typed = focusElementAction("input")))

        rule.onNode(hasSetTextAction()).assertIsFocused()
    }

    @Test
    fun `patch removal unregisters the old target`() {
        setContent()
        patch(DivPatch.Change("input"))
        reporter.failOnError = false

        perform(action(typed = focusElementAction("input")))

        assertEquals(listOf("Element with id 'input' not found for focus_element action"), reporter.errors)
    }

    private fun setContent() {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(card)
            }
        }
    }

    private fun perform(action: DivAction) {
        rule.runOnIdle { divContext.debugFeatures.performAction(card, action) }
    }

    private fun patch(change: DivPatch.Change) {
        rule.runOnIdle {
            runBlocking { assertTrue(divContext.patcher.applyPatch(card, DivPatch(listOf(change)))) }
        }
        rule.waitForIdle()
    }
}
