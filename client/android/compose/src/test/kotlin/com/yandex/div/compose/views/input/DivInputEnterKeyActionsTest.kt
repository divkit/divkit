package com.yandex.div.compose.views.input

import android.view.inputmethod.EditorInfo
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.PlatformTextInputMethodRequest
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.DivView
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.input
import com.yandex.div2.DivAction
import com.yandex.div2.DivInput
import kotlinx.coroutines.awaitCancellation
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class DivInputEnterKeyActionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val configuration = DivConfiguration(
        actionHandler = actionHandler,
        reporter = TestReporter(),
        variableController = DivVariableController().apply { declare(Variable.StringVariable("text", "")) },
    )
    private val field get() = rule.onNode(hasSetTextAction())
    private var inputRequest: PlatformTextInputMethodRequest? = null

    @Test
    fun `enter key actions are handled with enter source`() {
        setContent(
            enterKeyActions = listOf(action(id = "enter")),
            enterKeyType = constant(DivInput.EnterKeyType.DONE),
            keyboardType = constant(DivInput.KeyboardType.SINGLE_LINE_TEXT),
        )

        field.performImeAction()

        assertEquals(actionData(id = "enter", source = DivActionSource.KEYBOARD), actionHandler.handledAction)
    }

    @Test
    fun `done button of single line input with default enter key performs enter key actions`() {
        setContent(
            enterKeyActions = listOf(action(id = "enter")),
            keyboardType = constant(DivInput.KeyboardType.SINGLE_LINE_TEXT),
        )

        rule.runOnIdle {
            checkNotNull(inputRequest).createInputConnection(EditorInfo()).performEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        rule.runOnIdle {
            assertEquals(actionData(id = "enter", source = DivActionSource.KEYBOARD), actionHandler.handledAction)
        }
    }

    @Test
    fun `all enter key actions are handled in order`() {
        setContent(
            enterKeyActions = listOf(action(id = "first"), action(id = "second")),
            enterKeyType = constant(DivInput.EnterKeyType.SEND),
            keyboardType = constant(DivInput.KeyboardType.SINGLE_LINE_TEXT),
        )

        field.performImeAction()

        assertEquals(
            listOf(
                actionData(id = "first", source = DivActionSource.KEYBOARD),
                actionData(id = "second", source = DivActionSource.KEYBOARD),
            ),
            actionHandler.handledActions,
        )
    }

    @Test
    fun `disabled enter key actions are skipped`() {
        setContent(
            enterKeyActions = listOf(action(id = "enter", isEnabled = false)),
            enterKeyType = constant(DivInput.EnterKeyType.DONE),
            keyboardType = constant(DivInput.KeyboardType.SINGLE_LINE_TEXT),
        )

        field.performImeAction()

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `multi line input performs enter key actions with its enter key`() {
        setContent(
            enterKeyActions = listOf(action(id = "enter")),
            enterKeyType = constant(DivInput.EnterKeyType.SEND),
        )

        field.performImeAction()

        assertEquals(actionData(id = "enter", source = DivActionSource.KEYBOARD), actionHandler.handledAction)
    }

    @Test
    fun `enter key of multi line input with specific enter key performs enter key actions`() {
        setContent(
            enterKeyActions = listOf(action(id = "enter")),
            enterKeyType = constant(DivInput.EnterKeyType.SEND),
        )
        field.performTextInput("a")

        field.performKeyInput { pressKey(Key.Enter) }

        field.assertTextEquals("a")
        assertEquals(actionData(id = "enter", source = DivActionSource.KEYBOARD), actionHandler.handledAction)
    }

    @Test
    fun `keyboard enter of multi line input with specific enter key performs enter key actions`() {
        setContent(
            enterKeyActions = listOf(action(id = "enter")),
            enterKeyType = constant(DivInput.EnterKeyType.SEND),
        )
        field.performTextInput("a")

        rule.runOnIdle {
            checkNotNull(inputRequest).createInputConnection(EditorInfo()).commitText("\n", 1)
        }

        field.assertTextEquals("a")
        assertEquals(actionData(id = "enter", source = DivActionSource.KEYBOARD), actionHandler.handledAction)
    }

    @Test
    fun `enter key of multi line input with default enter key performs enter key actions`() {
        setContent(enterKeyActions = listOf(action(id = "enter")))
        field.performTextInput("a")

        field.performKeyInput { pressKey(Key.Enter) }

        field.assertTextEquals("a")
        assertEquals(actionData(id = "enter", source = DivActionSource.KEYBOARD), actionHandler.handledAction)
    }

    @Test
    fun `keyboard enter of multi line input with default enter key performs enter key actions`() {
        setContent(enterKeyActions = listOf(action(id = "enter")))
        field.performTextInput("a")

        rule.runOnIdle {
            checkNotNull(inputRequest).createInputConnection(EditorInfo()).commitText("\n", 1)
        }

        field.assertTextEquals("a")
        assertEquals(actionData(id = "enter", source = DivActionSource.KEYBOARD), actionHandler.handledAction)
    }

    @Test
    fun `default enter key of multi line input without actions inserts newline`() {
        setContent(enterKeyActions = emptyList())
        field.performTextInput("a")

        field.performKeyInput { pressKey(Key.Enter) }

        field.assertTextEquals("a\n")
    }

    @Test
    fun `default enter key of multi line input with disabled actions inserts newline`() {
        setContent(enterKeyActions = listOf(action(id = "enter", isEnabled = false)))
        field.performTextInput("a")

        field.performKeyInput { pressKey(Key.Enter) }

        field.assertTextEquals("a\n")
        assertEquals(emptyList(), actionHandler.handledActions)
    }

    private fun setContent(
        enterKeyActions: List<DivAction>,
        enterKeyType: Expression<DivInput.EnterKeyType> = constant(DivInput.EnterKeyType.DEFAULT),
        keyboardType: Expression<DivInput.KeyboardType> = constant(DivInput.KeyboardType.MULTI_LINE_TEXT),
    ) {
        val divData = data(
            input(
                enterKeyActions = enterKeyActions,
                enterKeyType = enterKeyType,
                keyboardType = keyboardType,
                textVariable = "text",
            )
        )
        rule.setContentWithDivContext(configuration) {
            InterceptPlatformTextInput(
                interceptor = { request, _ ->
                    inputRequest = request
                    awaitCancellation()
                }
            ) {
                DivView(divData)
            }
        }
        field.performClick()
    }
}
