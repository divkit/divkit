package com.yandex.div.compose.views.input

import android.text.InputType
import android.view.inputmethod.EditorInfo
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.PlatformTextInputMethodRequest
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.TextRange
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.input
import com.yandex.div2.DivCurrencyInputMask
import com.yandex.div2.DivFixedLengthInputMask
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputMask
import com.yandex.div2.DivPhoneInputMask
import kotlinx.coroutines.awaitCancellation
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class DivInputKeyboardTest {

    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()
    private val configuration = divConfiguration {
        reporter = TestReporter()
        variableController = this@DivInputKeyboardTest.variableController
    }
    private val source = Variable.StringVariable("text", "")
    private val field get() = rule.onNode(hasSetTextAction())
    private var inputRequest: PlatformTextInputMethodRequest? = null

    @Test
    fun `number keyboard supports signed decimals`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.NUMBER))

        assertEquals(
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED,
            editorInfo().inputType,
        )
    }

    @Test
    fun `length limited number keyboard supports signed decimals`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.NUMBER), maxLength = 10)

        assertEquals(
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED,
            editorInfo().inputType,
        )
    }

    @Test
    fun `single line keyboard does not request autocorrection`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.SINGLE_LINE_TEXT))

        assertEquals(InputType.TYPE_CLASS_TEXT, editorInfo().inputType)
    }

    @Test
    fun `length limited single line keyboard does not request autocorrection`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.SINGLE_LINE_TEXT), maxLength = 10)

        assertEquals(InputType.TYPE_CLASS_TEXT, editorInfo().inputType)
    }

    @Test
    fun `multiline keyboard keeps its input type without autocorrection`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.MULTI_LINE_TEXT))

        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE, editorInfo().inputType)
    }

    @Test
    fun `email keyboard keeps its input type without autocorrection`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.EMAIL))

        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, editorInfo().inputType)
    }

    @Test
    fun `URI keyboard keeps its input type without autocorrection`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.URI))

        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI, editorInfo().inputType)
    }

    @Test
    fun `phone keyboard keeps its input type without autocorrection`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.PHONE))

        assertEquals(InputType.TYPE_CLASS_PHONE, editorInfo().inputType)
    }

    @Test
    fun `password keyboard keeps its input type without autocorrection`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.PASSWORD))

        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD, editorInfo().inputType)
    }

    @Test
    fun `length limited password does not request autocorrection`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.PASSWORD), maxLength = 10)

        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD, editorInfo().inputType)
    }

    @Test
    fun `go enter key requests go IME action`() {
        setInput(enterKeyType = constant(DivInput.EnterKeyType.GO))

        assertEquals(EditorInfo.IME_ACTION_GO, editorInfo().imeOptions and EditorInfo.IME_MASK_ACTION)
    }

    @Test
    fun `search enter key requests search IME action`() {
        setInput(enterKeyType = constant(DivInput.EnterKeyType.SEARCH))

        assertEquals(EditorInfo.IME_ACTION_SEARCH, editorInfo().imeOptions and EditorInfo.IME_MASK_ACTION)
    }

    @Test
    fun `send enter key requests send IME action`() {
        setInput(enterKeyType = constant(DivInput.EnterKeyType.SEND))

        assertEquals(EditorInfo.IME_ACTION_SEND, editorInfo().imeOptions and EditorInfo.IME_MASK_ACTION)
    }

    @Test
    fun `done enter key requests done IME action`() {
        setInput(enterKeyType = constant(DivInput.EnterKeyType.DONE))

        assertEquals(EditorInfo.IME_ACTION_DONE, editorInfo().imeOptions and EditorInfo.IME_MASK_ACTION)
    }

    @Test
    fun `default enter key requests unspecified action for multiline input`() {
        setInput()

        assertEquals(EditorInfo.IME_ACTION_UNSPECIFIED, editorInfo().imeOptions and EditorInfo.IME_MASK_ACTION)
    }

    @Test
    fun `default enter key requests done for single line input`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.SINGLE_LINE_TEXT))

        assertEquals(EditorInfo.IME_ACTION_DONE, editorInfo().imeOptions and EditorInfo.IME_MASK_ACTION)
    }

    @Test
    fun `capitalization is retained without autocorrection`() {
        setInput(
            keyboardType = constant(DivInput.KeyboardType.SINGLE_LINE_TEXT),
            autocapitalization = constant(DivInput.Autocapitalization.SENTENCES),
        )

        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES, editorInfo().inputType)
    }

    @Test
    fun `switching from number to email removes numeric flags`() {
        val type = Variable.StringVariable("type", "number")
        variableController.declare(type)
        setInput(keyboardType = keyboardTypeExpression())

        type.set("email")

        assertEquals(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, editorInfo().inputType)
    }

    @Test
    fun `length limited keyboard switches from email to signed decimal`() {
        val type = Variable.StringVariable("type", "email")
        variableController.declare(type)
        setInput(keyboardType = keyboardTypeExpression(), maxLength = 10)

        type.set("number")

        assertEquals(
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED,
            editorInfo().inputType,
        )
    }

    @Test
    fun `changing keyboard preserves focus and selection`() {
        val type = Variable.StringVariable("type", "number")
        variableController.declare(type)
        source.set("1234")
        setInput(keyboardType = keyboardTypeExpression())
        field.performTextInputSelection(TextRange(1, 3))

        type.set("single_line_text")

        field.assertIsFocused()
        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(1, 3)))
    }

    @Test
    fun `changing enter key updates focused length limited input`() {
        val enterKeyType = Variable.StringVariable("enter", "go")
        variableController.declare(enterKeyType)
        setInput(
            maxLength = 10,
            enterKeyType = Expression.MutableExpression(
                expressionKey = "enter_key_type",
                rawExpression = "@{enter}",
                converter = DivInput.EnterKeyType::fromString,
                validator = { true },
                logger = { fail(it.message) },
                typeHelper = TypeHelper.from(default = DivInput.EnterKeyType.DEFAULT) { it is DivInput.EnterKeyType },
            ),
        )

        enterKeyType.set("search")

        assertEquals(EditorInfo.IME_ACTION_SEARCH, editorInfo().imeOptions and EditorInfo.IME_MASK_ACTION)
    }

    @Test
    fun `numeric input connection writes to the bound variable`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.NUMBER))

        rule.runOnIdle {
            checkNotNull(inputRequest).createInputConnection(EditorInfo()).commitText("-12.3", 1)
        }

        rule.runOnIdle { assertEquals("-12.3", source.getValue()) }
    }

    @Test
    fun `numeric input connection respects max length`() {
        setInput(keyboardType = constant(DivInput.KeyboardType.NUMBER), maxLength = 3)

        rule.runOnIdle {
            checkNotNull(inputRequest).createInputConnection(EditorInfo()).commitText("-12.3", 1)
        }

        rule.runOnIdle { assertEquals("-12", source.getValue()) }
    }

    @Test
    fun `currency mask keeps digits keyboard`() {
        variableController.declare(Variable.StringVariable("raw", ""))
        setInput(
            keyboardType = constant(DivInput.KeyboardType.NUMBER),
            mask = DivInputMask.Currency(DivCurrencyInputMask(rawTextVariable = "raw")),
        )

        assertEquals(InputType.TYPE_CLASS_NUMBER, editorInfo().inputType)
    }

    @Test
    fun `phone mask keeps digits keyboard`() {
        variableController.declare(Variable.StringVariable("raw", ""))
        setInput(
            keyboardType = constant(DivInput.KeyboardType.NUMBER),
            mask = DivInputMask.Phone(DivPhoneInputMask(rawTextVariable = "raw")),
        )

        assertEquals(InputType.TYPE_CLASS_NUMBER, editorInfo().inputType)
    }

    @Test
    fun `fixed length mask keeps signed decimal keyboard`() {
        variableController.declare(Variable.StringVariable("raw", ""))
        setInput(
            keyboardType = constant(DivInput.KeyboardType.NUMBER),
            mask = DivInputMask.FixedLength(DivFixedLengthInputMask(
                pattern = constant("###"),
                patternElements = listOf(DivFixedLengthInputMask.PatternElement(key = constant("#"))),
                rawTextVariable = "raw",
            )),
        )

        assertEquals(
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED,
            editorInfo().inputType,
        )
    }

    private fun keyboardTypeExpression(): Expression<DivInput.KeyboardType> = Expression.MutableExpression(
        expressionKey = "keyboard_type",
        rawExpression = "@{type}",
        converter = DivInput.KeyboardType::fromString,
        validator = { true },
        logger = { fail(it.message) },
        typeHelper = TypeHelper.from(default = DivInput.KeyboardType.MULTI_LINE_TEXT) { it is DivInput.KeyboardType },
    )

    private fun editorInfo(): EditorInfo = rule.runOnIdle {
        EditorInfo().also { checkNotNull(inputRequest).createInputConnection(it) }
    }

    private fun setInput(
        keyboardType: Expression<DivInput.KeyboardType> = constant(DivInput.KeyboardType.MULTI_LINE_TEXT),
        maxLength: Long? = null,
        mask: DivInputMask? = null,
        enterKeyType: Expression<DivInput.EnterKeyType> = constant(DivInput.EnterKeyType.DEFAULT),
        autocapitalization: Expression<DivInput.Autocapitalization> = constant(DivInput.Autocapitalization.AUTO),
    ) {
        variableController.declare(source)
        val divData = data(input(
            autocapitalization = autocapitalization,
            enterKeyType = enterKeyType,
            keyboardType = keyboardType,
            mask = mask,
            maxLength = maxLength?.let(::constant),
            textVariable = "text",
        ))
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
