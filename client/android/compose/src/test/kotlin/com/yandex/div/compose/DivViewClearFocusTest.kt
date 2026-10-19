package com.yandex.div.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.test.data.action
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.input
import com.yandex.div.test.data.slider
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.variable
import com.yandex.div2.DivActionClearFocus
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivData
import com.yandex.div2.DivFocus
import com.yandex.div2.DivTrigger
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.RuntimeEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivViewClearFocusTest {
    @get:Rule
    val rule = createComposeRule()

    private val actions = TestExternalActionHandler()
    private val reporter = TestReporter()
    private val keyboard = mock<SoftwareKeyboardController>()
    private val visible = mutableStateOf(true)
    private val currentKeyboard = mutableStateOf<SoftwareKeyboardController?>(keyboard)
    private val currentFocusManager = mutableStateOf<FocusManager?>(null)
    private val focus = DivFocus(
        onFocus = listOf(action(id = "focus")),
        onBlur = listOf(action(id = "blur")),
    )
    private val card = data(
        input(id = "input", textVariable = "input", focus = focus),
        variables = listOf(variable("input", "Text")),
    )
    private val otherCard = data(
        input(id = "other", textVariable = "input", focus = focus),
        variables = listOf(variable("input", "Other")),
    )
    private val divContext by lazy {
        DivContext(
            baseContext = RuntimeEnvironment.getApplication(),
            configuration = DivConfiguration(actionHandler = actions, reporter = reporter),
        )
    }
    private val clearAction = action(typed = DivActionTyped.ClearFocus(DivActionClearFocus()))

    @Test
    fun `clear focus blurs the focused input once`() {
        setContent()
        requestFocus()

        clearFocus()

        inputNode("input").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
    }

    @Test
    fun `repeated clear focus does not dispatch extra blur`() {
        setContent()
        requestFocus()
        clearFocus()
        actions.reset()

        clearFocus()

        assertEquals(emptyList(), actions.handledActions)
    }

    @Test
    fun `clear focus without focused input does not dispatch blur`() {
        setContent()

        clearFocus()

        assertEquals(emptyList(), actions.handledActions)
    }

    @Test
    fun `disabled clear focus preserves focus`() {
        setContent()
        requestFocus()
        clearInvocations(keyboard)

        rule.runOnIdle {
            divContext.debugFeatures.performAction(card,
                action(isEnabled = false, typed = DivActionTyped.ClearFocus(DivActionClearFocus())))
        }

        inputNode("input").assertIsFocused()
        verifyNoInteractions(keyboard)
        assertEquals(emptyList(), actions.handledActions)
    }

    @Test
    fun `clear focus releases captured input focus`() {
        val requester = FocusRequester()
        setContent(Modifier.focusRequester(requester))
        requestFocus()
        rule.runOnIdle { assertTrue(requester.captureFocus()) }

        clearFocus()

        inputNode("input").assertIsNotFocused()
    }

    @Test
    fun `clear focus hides the software keyboard`() {
        setContent()
        requestFocus()

        clearFocus()

        verify(keyboard).hide()
    }

    @Test
    fun `clear focus hides keyboard even without focused input`() {
        setContent()

        clearFocus()

        verify(keyboard).hide()
    }

    @Test
    fun `clear focus does not blur another card`() {
        setContent()
        requestFocus("other")

        clearFocus()

        inputNode("other").assertIsFocused()
        assertEquals(emptyList(), actions.handledActions)
    }

    @Test
    fun `clear focus on second card does not blur first card`() {
        setContent()
        requestFocus()

        rule.runOnIdle { divContext.debugFeatures.performAction(otherCard, clearAction) }

        inputNode("input").assertIsFocused()
        assertEquals(emptyList(), actions.handledActions)
    }

    @Test
    fun `clear focus preserves host input outside the card`() {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                Column {
                    BasicTextField(value = "Host", onValueChange = {}, modifier = Modifier.testTag("host"))
                    DivView(card)
                }
            }
        }
        rule.onNodeWithTag("host").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNodeWithTag("host").assertIsFocused()

        clearFocus()

        rule.onNodeWithTag("host").assertIsFocused()
        assertEquals(emptyList(), actions.handledActions)
    }

    @Test
    fun `clear focus blurs focusable text once`() {
        val textCard = data(text(id = "text", text = "Text", focus = focus))
        setContent(data = textCard)
        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNodeWithTag("text").assertIsFocused()
        actions.reset()

        rule.runOnIdle { divContext.debugFeatures.performAction(textCard, clearAction) }

        rule.onNodeWithTag("text").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
    }

    @Test
    fun `clear focus blurs slider once`() {
        val sliderCard = data(slider(id = "slider", focus = focus))
        setContent(data = sliderCard)
        rule.onNodeWithTag("slider").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNodeWithTag("slider").assertIsFocused()
        actions.reset()

        rule.runOnIdle { divContext.debugFeatures.performAction(sliderCard, clearAction) }

        rule.onNodeWithTag("slider").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
    }

    @Test
    fun `clear focus after leaving composition reports error and preserves other card`() {
        reporter.failOnError = false
        setContent()
        requestFocus()
        rule.runOnIdle { visible.value = false }
        requestFocus("other")

        clearFocus()

        inputNode("other").assertIsFocused()
        assertEquals(listOf("No focus manager is bound for clear_focus action"), reporter.errors)
        assertEquals(emptyList(), actions.handledActions)
    }

    @Test
    fun `clear focus works after reentering composition`() {
        setContent()
        rule.runOnIdle { visible.value = false }
        rule.runOnIdle { visible.value = true }
        requestFocus()

        clearFocus()

        inputNode("input").assertIsNotFocused()
    }

    @Test
    fun `clear focus works for a nested input after a focusable sibling`() {
        val nestedCard = data(container(items = listOf(
            input(id = "first", textVariable = "input", focus = focus),
            container(items = listOf(input(id = "input", textVariable = "input", focus = focus))),
        )), variables = listOf(variable("input", "Text")))
        setContent(data = nestedCard)
        requestFocus()

        rule.runOnIdle { divContext.debugFeatures.performAction(nestedCard, clearAction) }

        inputNode("input").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
    }

    @Test
    fun `clear focus invoked from on focus callback blurs the input`() {
        val callbackCard = data(input(
            id = "input", textVariable = "input",
            focus = DivFocus(onFocus = listOf(clearAction), onBlur = listOf(action(id = "blur"))),
        ), variables = listOf(variable("input", "Text")))
        setContent(data = callbackCard)

        inputNode("input").performSemanticsAction(SemanticsActions.RequestFocus)

        inputNode("input").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
    }

    @Test
    fun `clear focus invoked again from blur callback does not refocus input`() {
        val callbackCard = data(input(
            id = "input", textVariable = "input",
            focus = DivFocus(onBlur = listOf(clearAction, action(id = "blur"))),
        ), variables = listOf(variable("input", "Text")))
        setContent(data = callbackCard)
        requestFocus()

        rule.runOnIdle { divContext.debugFeatures.performAction(callbackCard, clearAction) }

        inputNode("input").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
    }

    @Test
    fun `clear focus uses replacement software keyboard controller`() {
        setContent()
        requestFocus()
        clearInvocations(keyboard)
        val replacement = mock<SoftwareKeyboardController>()
        rule.runOnIdle { currentKeyboard.value = replacement }

        clearFocus()

        inputNode("input").assertIsNotFocused()
        verify(replacement).hide()
        verifyNoInteractions(keyboard)
    }

    @Test
    fun `clear focus uses replacement focus manager`() {
        setContent()
        requestFocus()
        val replacement = mock<FocusManager>()
        rule.runOnIdle { currentFocusManager.value = replacement }

        clearFocus()

        verify(replacement).clearFocus(force = true)
    }

    @Test
    fun `tap action clears input focus without focusing the button`() {
        val actionCard = data(container(items = listOf(
            input(id = "input", textVariable = "input", focus = focus),
            text(id = "clear", text = "Clear focus", action = clearAction),
        )), variables = listOf(variable("input", "Text")))
        setContent(data = actionCard)
        requestFocus()

        rule.onNodeWithTag("clear").performClick()

        inputNode("input").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
    }

    @Test
    fun `clear focus blurs first occurrence of shared data once`() {
        setSharedContent()
        requestFocus("first")

        clearFocus()

        inputNode("first").assertIsNotFocused()
        inputNode("second").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
        verify(keyboard).hide()
    }

    @Test
    fun `clear focus blurs second occurrence of shared data once`() {
        setSharedContent()
        requestFocus("second")

        clearFocus()

        inputNode("first").assertIsNotFocused()
        inputNode("second").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
        verify(keyboard).hide()
    }

    @Test
    fun `clear focus survives removal of another occurrence of the same data`() {
        setSharedContent()
        rule.runOnIdle { visible.value = false }
        requestFocus("second")

        clearFocus()

        inputNode("second").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
    }

    @Test
    fun `initial clear focus trigger runs without unavailable service error`() {
        reporter.failOnError = false
        val triggerCard = card.copy(variableTriggers = listOf(trigger(clearAction, "@{true}")))

        setContent(data = triggerCard)

        rule.waitForIdle()
        assertEquals(emptyList(), reporter.errors)
        verify(keyboard).hide()
    }

    @Test
    fun `clear focus trigger runs when card reenters composition`() {
        reporter.failOnError = false
        val triggerCard = card.copy(variableTriggers = listOf(
            trigger(clearAction, "@{true}", DivTrigger.Mode.ON_VARIABLE),
        ))
        setContent(data = triggerCard)
        rule.waitForIdle()
        assertEquals(emptyList(), reporter.errors)
        reset(keyboard)
        rule.runOnIdle { visible.value = false }

        rule.runOnIdle { visible.value = true }

        rule.waitForIdle()
        assertEquals(emptyList(), reporter.errors)
        verify(keyboard).hide()
    }

    @Test
    fun `clear focus after replacing focused card data`() {
        val replacement = card.copy(variables = listOf(variable("input", "Replacement")))
        val currentData = mutableStateOf(card)
        rule.setContent {
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalSoftwareKeyboardController provides keyboard,
            ) {
                DivView(currentData.value)
            }
        }
        requestFocus()

        rule.runOnIdle { currentData.value = replacement }
        inputNode("input").assertIsFocused()
        actions.reset()
        rule.runOnIdle { divContext.debugFeatures.performAction(replacement, clearAction) }

        inputNode("input").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
    }

    @Test
    fun `initial clear focus trigger blurs retained input after card replacement`() {
        reporter.failOnError = false
        val replacement = card.copy(
            variables = listOf(variable("input", "Replacement")),
            variableTriggers = listOf(trigger(clearAction, "@{true}")),
        )
        val currentData = mutableStateOf(card)
        rule.setContent {
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalSoftwareKeyboardController provides keyboard,
            ) {
                DivView(currentData.value)
            }
        }
        requestFocus()

        rule.runOnIdle { currentData.value = replacement }

        inputNode("input").assertIsNotFocused()
        assertEquals(listOf(actionData(id = "blur", source = DivActionSource.BLUR)), actions.handledActions)
        assertEquals(emptyList(), reporter.errors)
        verify(keyboard).hide()
    }

    private fun setContent(modifier: Modifier = Modifier, data: DivData = card) {
        rule.setContent {
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalSoftwareKeyboardController provides currentKeyboard.value,
                LocalFocusManager provides (currentFocusManager.value ?: LocalFocusManager.current),
            ) {
                Column {
                    if (visible.value) DivView(data, modifier)
                    DivView(otherCard)
                }
            }
        }
    }

    private fun setSharedContent() {
        reporter.failOnError = false
        rule.setContent {
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalSoftwareKeyboardController provides keyboard,
            ) {
                Column {
                    if (visible.value) DivView(card, Modifier.testTag("first"))
                    DivView(card, Modifier.testTag("second"))
                }
            }
        }
        rule.waitForIdle()
        reporter.failOnError = true
        assertEquals(
            listOf("DivViewContext is already used in another composition. This may lead to undefined behavior."),
            reporter.popErrors(),
        )
    }

    private fun requestFocus(id: String = "input") {
        inputNode(id).performTouchInput { click() }
        inputNode(id).assertIsFocused()
        actions.reset()
    }

    private fun inputNode(id: String) =
        rule.onNode(hasSetTextAction() and hasAnyAncestor(hasTestTag(id)))

    private fun clearFocus() {
        rule.runOnIdle { divContext.debugFeatures.performAction(card, clearAction) }
    }
}
