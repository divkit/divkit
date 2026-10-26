package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.TextRange
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.focusElementAction
import com.yandex.div.test.data.input
import com.yandex.div.test.data.setCursorPositionAction
import com.yandex.div2.Div
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivData
import com.yandex.div2.DivFixedLengthInputMask
import com.yandex.div2.DivFocus
import com.yandex.div2.DivInputMask
import com.yandex.div2.DivPatch
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import kotlin.test.Test
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivViewSetCursorPositionTest {
    @get:Rule
    val rule = createComposeRule()

    private val divContext = DivContext(
        RuntimeEnvironment.getApplication(),
        DivConfiguration(
            reporter = TestReporter(),
            variableController = DivVariableController().apply {
                declare(
                    Variable.StringVariable("value", "Sample input text"),
                    Variable.StringVariable("phone", ""),
                    Variable.StringVariable("phone_raw", "1234567890"),
                    Variable.StringVariable("short", "ab"),
                )
            },
        ),
    )
    private val editable = SemanticsMatcher.keyIsDefined(SemanticsProperties.EditableText)
    private val field get() = rule.onNode(editable)

    @Test
    fun `input is focused with the range selected`() {
        val card = setContent(input(id = "input", textVariable = "value"))

        perform(card, setCursorPositionAction("input", start = 7, end = 12))

        field.assertIsFocused()
        field.assertSelection(TextRange(7, 12))
    }

    @Test
    fun `masked input positions address the shown text`() {
        val card = setContent(input(id = "phone", mask = digitsMask(), textVariable = "phone"))
        field.assertTextEquals("(123) 456-7890")

        perform(card, setCursorPositionAction("phone", start = 6, end = 9))

        field.performTextInput("0")
        field.assertTextEquals("(123) 078-90")
    }

    @Test
    fun `masked input minus one is the end of the shown text`() {
        val card = setContent(input(id = "phone", mask = digitsMask(), textVariable = "phone"))

        perform(card, setCursorPositionAction("phone", start = 6, end = -1))

        field.performTextInput("0")
        field.assertTextEquals("(123) 0")
    }

    @Test
    fun `selection wins over select all on focus`() {
        val card = setContent(
            input(id = "input", selectAllOnFocus = constant(true), textVariable = "value")
        )

        perform(card, setCursorPositionAction("input", start = 7, end = 12))

        field.assertSelection(TextRange(7, 12))
    }

    @Test
    fun `select all on focus applies again to a cursor whose focus moved on`() {
        val card = setContent(container(items = listOf(
            input(id = "input", selectAllOnFocus = constant(true), textVariable = "value"),
            input(id = "other", textVariable = "phone"),
        )))
        rule.runOnIdle {
            divContext.debugFeatures.performAction(card, action(typed = setCursorPositionAction("input", start = 5)))
            divContext.debugFeatures.performAction(card, action(typed = focusElementAction("other")))
        }
        rule.waitForIdle()

        perform(card, focusElementAction("input"))

        rule.onAllNodes(editable).onFirst().assertSelection(TextRange(0, 17))
    }

    @Test
    fun `text written by a preceding action of the list is addressed`() {
        val card = setContent(input(id = "input", textVariable = "short"))

        rule.runOnIdle {
            divContext.debugFeatures.performAction(
                card, action(url = "div-action://set_variable?name=short&value=abcd")
            )
            divContext.debugFeatures.performAction(card, action(typed = setCursorPositionAction("input", start = 3)))
        }
        rule.waitForIdle()

        field.assertTextEquals("abcd")
        field.assertSelection(TextRange(3))
    }

    @Test
    fun `text written on focus is addressed`() {
        val card = setContent(input(
            id = "input",
            focus = DivFocus(
                onFocus = listOf(action(url = "div-action://set_variable?name=value&value=Replaced on focus")),
            ),
            textVariable = "value",
        ))

        perform(card, setCursorPositionAction("input", start = 9, end = -1))

        field.assertTextEquals("Replaced on focus")
        field.assertSelection(TextRange(9, 17))
    }

    @Test
    fun `range behind a text shortened on focus ends at its end`() {
        val card = setContent(input(
            id = "input",
            focus = DivFocus(onFocus = listOf(action(url = "div-action://set_variable?name=value&value=Short"))),
            textVariable = "value",
        ))

        perform(card, setCursorPositionAction("input", start = 7, end = 12))

        field.assertTextEquals("Short")
        field.assertSelection(TextRange(5))
    }

    @Test
    fun `disabled input is selected without focus`() {
        val card = setContent(
            input(id = "input", isEnabled = constant(false), textVariable = "value")
        )

        perform(card, setCursorPositionAction("input", start = 7, end = 12))

        field.assertSelection(TextRange(7, 12))
        field.assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Focused))
    }

    @Test
    fun `replacement input after patch is selected`() {
        val card = setContent(input(id = "input", textVariable = "value"))
        rule.runOnIdle {
            runBlocking {
                val change = DivPatch.Change("input", listOf(input(id = "input", textVariable = "value")))
                assertTrue(divContext.patcher.applyPatch(card, DivPatch(listOf(change))))
            }
        }
        rule.waitForIdle()

        perform(card, setCursorPositionAction("input", start = 7, end = 12))

        field.assertSelection(TextRange(7, 12))
    }

    private fun setContent(div: Div): DivData {
        val card = data(div)
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(card)
            }
        }
        return card
    }

    private fun perform(card: DivData, typed: DivActionTyped) {
        rule.runOnIdle { divContext.debugFeatures.performAction(card, action(typed = typed)) }
        rule.waitForIdle()
    }

    private fun digitsMask() = DivInputMask.FixedLength(
        DivFixedLengthInputMask(
            pattern = constant("(###) ###-####"),
            patternElements = listOf(
                DivFixedLengthInputMask.PatternElement(
                    key = constant("#"),
                    placeholder = constant("_"),
                    regex = constant("[0-9]"),
                ),
            ),
            rawTextVariable = "phone_raw",
        ),
    )
}

private fun SemanticsNodeInteraction.assertSelection(range: TextRange) =
    assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, range))
