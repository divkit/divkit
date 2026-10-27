package com.yandex.div.compose.views.input

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.missingVariable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.change
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.input
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivInputValidator
import com.yandex.div2.DivInputValidatorExpression
import com.yandex.div2.DivInputValidatorRegex
import com.yandex.div2.DivPatch
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivInputValidatorLabelTest {

    @get:Rule
    val rule = createComposeRule()

    private val reporter = TestReporter()
    private val textVariable = Variable.StringVariable("text", "")
    private val labelText = Variable.StringVariable("label_text", "Enter digits")
    private val labelId = Variable.StringVariable("label_id", "first")
    private val labelVisibility = Variable.StringVariable("label_visibility", "visible")
    private val divContext = DivContext(
        getApplicationContext(),
        DivConfiguration(
            reporter = reporter,
            variableController = DivVariableController().apply {
                declare(
                    textVariable,
                    Variable.BooleanVariable("digits", true),
                    Variable.BooleanVariable("condition", true),
                    labelText,
                    labelId,
                    labelVisibility,
                )
            },
        ),
    )
    private val field get() = rule.onNode(hasSetTextAction())

    @Test
    fun `failed validator exposes the label text as the error of the input`() {
        setContent(card(label(), input()))

        field.performTextInput("abc")

        field.assert(hasError("Enter digits"))
    }

    @Test
    fun `input invalid at the first composition exposes the error`() {
        textVariable.set("abc")

        setContent(card(label(), input()))

        field.assert(hasError("Enter digits"))
    }

    @Test
    fun `label that follows the input is found`() {
        setContent(card(input(), label()))

        field.performTextInput("abc")

        field.assert(hasError("Enter digits"))
    }

    @Test
    fun `passing validator leaves the input without an error`() {
        setContent(card(label(), input()))
        field.performTextInput("abc")
        field.assert(hasError("Enter digits"))

        field.performTextReplacement("123")

        field.assert(hasNoError())
    }

    @Test
    fun `validator without a variable still exposes its label`() {
        reporter.failOnError = false
        setContent(card(label(), input(validators = listOf(digitsValidator(variable = "unknown")))))

        field.performTextInput("abc")

        field.assert(hasError("Enter digits"))
        assertEquals(listOf(missingVariable("unknown").message), reporter.errors)
    }

    @Test
    fun `label text change updates the error`() {
        setContent(card(text(id = "first", text = expression("@{label_text}")), input()))
        field.performTextInput("abc")

        labelText.set("Digits only")

        field.assert(hasError("Digits only"))
    }

    @Test
    fun `errors of several failed validators are joined`() {
        setContent(
            card(
                label(),
                text(id = "second", text = "Condition failed"),
                input(validators = listOf(digitsValidator(), conditionValidator(false, labelId = "second"))),
            )
        )

        field.performTextInput("abc")

        field.assert(hasError("Enter digits. Condition failed"))
    }

    @Test
    fun `label id change switches the label`() {
        setContent(
            card(
                label(),
                text(id = "second", text = "Second label"),
                input(validators = listOf(digitsValidator(labelId = expression("@{label_id}")))),
            )
        )
        field.performTextInput("abc")

        labelId.set("second")

        field.assert(hasError("Second label"))
    }

    @Test
    fun `missing label is reported`() {
        reporter.failOnError = false

        setContent(card(input(validators = listOf(digitsValidator(labelId = constant("missing"))))))

        field.performTextInput("abc")

        field.assert(hasNoError())
        assertEquals(listOf("Can't find label with id 'missing'"), reporter.errors)
    }

    @Test
    fun `validators sharing a missing label report it once`() {
        reporter.failOnError = false
        val validators = listOf(digitsValidator(labelId = constant("missing")), conditionValidator(false, "missing"))
        setContent(card(input(validators = validators)))

        field.performTextInput("abc")

        assertEquals(listOf("Can't find label with id 'missing'"), reporter.errors)
    }

    @Test
    fun `initially gone label of a passing validator is not reported`() {
        labelVisibility.set("gone")

        setContent(card(text(id = "first", text = "Enter digits", visibility = visibilityExpression("@{label_visibility}")), input()))

        rule.waitForIdle()
        assertEquals(emptyList<String>(), reporter.errors)
    }

    @Test
    fun `gone label of a failed validator leaves the input without an error`() {
        labelVisibility.set("gone")
        setContent(card(text(id = "first", text = "Enter digits", visibility = visibilityExpression("@{label_visibility}")), input()))

        field.performTextInput("abc")

        field.assert(hasNoError())
    }

    @Test
    fun `gone label withdraws its text until it is visible again`() {
        val visibility = visibilityExpression("@{label_visibility}")
        setContent(card(text(id = "first", text = "Enter digits", visibility = visibility), input()))
        field.performTextInput("abc")
        field.assert(hasError("Enter digits"))

        labelVisibility.set("gone")
        field.assert(hasNoError())

        labelVisibility.set("visible")
        field.assert(hasError("Enter digits"))
    }

    @Test
    fun `label leaving the composition clears the error until it returns`() {
        val data = card(
            state(
                id = "labels",
                states = listOf(
                    state(stateId = "shown", div = label()),
                    state(stateId = "hidden", div = text(text = "No label")),
                ),
            ),
            input(),
        )
        setContent(data)
        field.performTextInput("abc")
        field.assert(hasError("Enter digits"))

        performAction(data, "div-action://set_state?state_id=0/labels/hidden")
        field.assert(hasNoError())

        performAction(data, "div-action://set_state?state_id=0/labels/shown")
        field.assert(hasError("Enter digits"))
    }

    @Test
    fun `label with a duplicate id leaving the composition hands over to the other one`() {
        val data = card(
            state(
                id = "labels",
                states = listOf(
                    state(stateId = "shown", div = label()),
                    state(stateId = "hidden", div = text(text = "No label")),
                ),
            ),
            text(id = "first", text = "Fallback"),
            input(),
        )
        setContent(data)
        field.performTextInput("abc")
        field.assert(hasError("Enter digits"))

        performAction(data, "div-action://set_state?state_id=0/labels/hidden")

        field.assert(hasError("Fallback"))
    }

    @Test
    fun `patched label and input keep the error`() {
        val data = card(label(), input())
        setContent(data)
        field.performTextInput("abc")

        assertTrue(applyPatch(data, patch("first", text(id = "first", text = "Patched"))))
        field.assert(hasError("Patched"))

        assertTrue(applyPatch(data, patch("input", input())))
        field.assert(hasError("Patched"))
    }

    private fun hasError(message: String) = SemanticsMatcher.expectValue(SemanticsProperties.Error, message)

    private fun hasNoError() = SemanticsMatcher.keyNotDefined(SemanticsProperties.Error)

    private fun card(vararg items: Div): DivData = data(container(items = items.toList()))

    private fun label() = text(id = "first", text = "Enter digits")

    private fun input(validators: List<DivInputValidator> = listOf(digitsValidator())) =
        input(id = "input", textVariable = "text", validators = validators)

    private fun digitsValidator(
        labelId: Expression<String> = constant("first"),
        variable: String = "digits",
    ) = DivInputValidator.Regex(
        DivInputValidatorRegex(labelId = labelId, pattern = constant("[0-9]*"), variable = variable)
    )

    private fun conditionValidator(condition: Boolean, labelId: String) = DivInputValidator.Expression(
        DivInputValidatorExpression(condition = constant(condition), labelId = constant(labelId), variable = "condition")
    )

    private fun patch(id: String, item: Div) = DivPatch(changes = listOf(change(id, listOf(item))))

    private fun setContent(data: DivData) {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data)
            }
        }
    }

    private fun performAction(data: DivData, url: String) = rule.runOnIdle {
        divContext.debugFeatures.performAction(data, action(url = url))
    }

    private fun applyPatch(data: DivData, patch: DivPatch): Boolean = rule.runOnIdle {
        runBlocking { divContext.patcher.applyPatch(data, patch) }
    }
}
