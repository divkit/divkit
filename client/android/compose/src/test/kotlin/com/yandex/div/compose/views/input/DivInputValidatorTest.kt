package com.yandex.div.compose.views.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.DivModelInternalApi
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivInputValidator
import com.yandex.div2.DivInputValidatorExpression
import com.yandex.div2.DivInputValidatorRegex
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(DivModelInternalApi::class)
@RunWith(AndroidJUnit4::class)
class DivInputValidatorTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val reporter = TestReporter()
    private val variableController = DivVariableController()
    private val localComponent = mockLocalComponent(
        reporter = reporter,
        variableController = variableController,
    )

    @Test
    fun `regex validator sets true for matching text`() {
        val resultVar = declareBoolean("is_valid", false)

        validate(listOf(regexValidator("[a-z]+", "is_valid")), "hello")

        assertEquals(true, resultVar.getValue())
    }

    @Test
    fun `regex validator sets false for non-matching text`() {
        val resultVar = declareBoolean("is_valid", true)

        validate(listOf(regexValidator("[a-z]+", "is_valid")), "123")

        assertEquals(false, resultVar.getValue())
    }

    @Test
    fun `regex validator allows empty when allowEmpty is true`() {
        val resultVar = declareBoolean("is_valid", false)

        validate(listOf(regexValidator("[a-z]+", "is_valid", allowEmpty = true)), "")

        assertEquals(true, resultVar.getValue())
    }

    @Test
    fun `regex validator rejects empty when allowEmpty is false`() {
        val resultVar = declareBoolean("is_valid", true)

        validate(listOf(regexValidator("[a-z]+", "is_valid", allowEmpty = false)), "")

        assertEquals(false, resultVar.getValue())
    }

    @Test
    fun `expression validator sets true when condition is true`() {
        val resultVar = declareBoolean("is_valid", false)

        validate(listOf(expressionValidator(condition = true, "is_valid")), "anything")

        assertEquals(true, resultVar.getValue())
    }

    @Test
    fun `expression validator sets false when condition is false`() {
        val resultVar = declareBoolean("is_valid", true)

        validate(listOf(expressionValidator(condition = false, "is_valid")), "anything")

        assertEquals(false, resultVar.getValue())
    }

    @Test
    fun `expression validator allows empty when allowEmpty is true`() {
        val resultVar = declareBoolean("is_valid", false)

        validate(listOf(expressionValidator(condition = false, "is_valid", allowEmpty = true)), "")

        assertEquals(true, resultVar.getValue())
    }

    @Test
    fun `expression validator with allowEmpty false uses condition for empty input`() {
        val resultVar = declareBoolean("is_valid", true)

        validate(listOf(expressionValidator(condition = false, "is_valid", allowEmpty = false)), "")

        assertEquals(false, resultVar.getValue())
    }

    @Test
    fun `expression validator with allowEmpty false accepts empty when condition is true`() {
        val resultVar = declareBoolean("is_valid", false)

        validate(listOf(expressionValidator(condition = true, "is_valid", allowEmpty = false)), "")

        assertEquals(true, resultVar.getValue())
    }

    @Test
    fun `multiple validators write to separate variables`() {
        val regexResult = declareBoolean("regex_valid", false)
        val exprResult = declareBoolean("expr_valid", false)

        validate(
            listOf(
                regexValidator("[a-z]+", "regex_valid"),
                expressionValidator(condition = true, "expr_valid"),
            ),
            "hello"
        )

        assertEquals(true, regexResult.getValue())
        assertEquals(true, exprResult.getValue())
    }

    @Test
    fun `validator updates when text changes`() {
        val resultVar = declareBoolean("is_valid", false)
        val validators = listOf(regexValidator("[a-z]+", "is_valid"))
        val text = mutableStateOf("hello")

        setContent { validators.validate(text.value) }
        assertEquals(true, resultVar.getValue())

        text.value = "123"
        composeRule.waitForIdle()
        assertEquals(false, resultVar.getValue())

        text.value = "abc"
        composeRule.waitForIdle()
        assertEquals(true, resultVar.getValue())
    }

    @Test
    fun `invalid regex reports error and defaults to true`() {
        reporter.failOnError = false
        val resultVar = declareBoolean("is_valid", false)

        validate(listOf(regexValidator("[invalid", "is_valid")), "anything")

        assertEquals(true, resultVar.getValue())
        assertEquals("Invalid regex pattern '[invalid'", reporter.lastError)
    }

    @Test
    fun `validator silently skips when variable is missing and reports a single error`() {
        reporter.failOnError = false

        validate(listOf(regexValidator("[a-z]+", "missing_var")), "hello")

        assertEquals(listOf("variable [missing_var] not found"), reporter.errors)
    }

    @Test
    fun `validator silently skips when variable is not boolean and reports a single error`() {
        reporter.failOnError = false
        variableController.declare(Variable.StringVariable("is_valid", "true"))

        validate(listOf(regexValidator("[a-z]+", "is_valid")), "hello")

        assertEquals(listOf("variable [is_valid] is not a boolean variable"), reporter.errors)
    }

    private fun validate(validators: List<DivInputValidator>, text: String) {
        setContent { validators.validate(text) }
    }

    private fun declareBoolean(name: String, defaultValue: Boolean): Variable.BooleanVariable {
        val variable = Variable.BooleanVariable(name, defaultValue)
        variableController.declare(variable)
        return variable
    }

    private fun regexValidator(
        pattern: String,
        variable: String,
        allowEmpty: Boolean = false,
    ) = DivInputValidator.Regex(
        DivInputValidatorRegex(
            pattern = Expression.constant(pattern),
            variable = variable,
            labelId = Expression.constant("label"),
            allowEmpty = Expression.constant(allowEmpty),
        )
    )

    private fun expressionValidator(
        condition: Boolean,
        variable: String,
        allowEmpty: Boolean = false,
    ) = DivInputValidator.Expression(
        DivInputValidatorExpression(
            condition = Expression.constant(condition),
            variable = variable,
            labelId = Expression.constant("label"),
            allowEmpty = Expression.constant(allowEmpty),
        )
    )

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                content()
            }
        }
    }
}
