package com.yandex.div.core.expression

import com.yandex.div.json.NUMBER_TO_DOUBLE
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.TYPE_HELPER_BOOLEAN
import com.yandex.div.json.TYPE_HELPER_DOUBLE
import com.yandex.div.json.TYPE_HELPER_INT
import com.yandex.div.json.TYPE_HELPER_STRING
import com.yandex.div.json.TypeHelper
import com.yandex.div.json.expressions.Expression
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.FunctionProvider
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

/**
 * Tests for [ExpressionResolverImpl].
 */
class ExpressionResolverImplTest {
    @get:Rule
    val rule = EnableAssertsRule(false)

    private val variables = mutableMapOf<String, Variable>().also { map ->
        listOf(
            Variable.StringVariable("host", "yandex.ru"),
            Variable.StringVariable("path", "mail"),
            Variable.IntegerVariable("hundred", 100),
            Variable.BooleanVariable("logical_true", true),
            Variable.BooleanVariable("logical_false", false),
        ).forEach {
            map[it.name] = it
        }
    }

    private val failFastLogger = ParsingErrorLogger { e -> throw e }
    private val silentLogger = ParsingErrorLogger { e -> e.printStackTrace() }
    private val externalVariables = mock<VariableController> {
        on { getMutableVariable(any()) } doAnswer {
            val name = it.getArgument<String>(0)
            variables[name]
        }
    }

    private val underTest = ExpressionResolverImpl(
        externalVariables,
        ExpressionEvaluatorFactory(FunctionProvider.STUB),
        mock(),
    )

    @Test
    fun `string with multiple variables`() {
        Assert.assertEquals(
            "http://yandex.ru/mail",
            mutableExpression<String>("http://@{host}/@{path}", TYPE_HELPER_STRING).evaluate(
                underTest
            )
        )
    }

    @Test
    fun `string with single variable at center`() {
        Assert.assertEquals(
            "http://yandex.ru/api?",
            mutableExpression<String>("http://@{host}/api?", TYPE_HELPER_STRING).evaluate(underTest)
        )
    }

    @Test
    fun `numeric variable accessed via getInt`() {
        val mutableExpression = mutableExpression<Int>(
            rawExpression = "@{hundred}",
            typeHelper = TYPE_HELPER_INT,
        )
        Assert.assertEquals(100, mutableExpression.evaluate(underTest))
    }

    @Test
    fun `int variable access`() {
        val intValue = mutableExpression<Int>("@{hundred}", TYPE_HELPER_INT).evaluate(underTest)
        Assert.assertEquals(100, intValue)
    }

    @Test
    fun `double variable accessed via numeric expression`() {
        val doubleExpression = Expression.MutableExpression(
            expressionKey = "some_key",
            rawExpression = "@{hundred}",
            converter = NUMBER_TO_DOUBLE,
            validator = { true },
            logger = failFastLogger,
            typeHelper = TYPE_HELPER_DOUBLE)
        Assert.assertEquals(100.0f.toDouble(), doubleExpression.evaluate(underTest), 0f.toDouble())
    }

    @Test
    fun `string expression can access non-string variables`() {
        val value = mutableExpression(
            rawExpression = "@{hundred}",
            typeHelper = TYPE_HELPER_STRING,
            logger = silentLogger
        ).evaluate(underTest)
        Assert.assertEquals("100", value)
    }

    @Test
    fun `string expression can access non-string variables with evaluable`() {
        val value = mutableExpression(
            rawExpression = "@{hundred}",
            typeHelper = TYPE_HELPER_STRING,
            logger = silentLogger
        ).evaluate(underTest)
        Assert.assertEquals("100", value)
    }

    @Test
    fun `string expression may use non-string variables for composing itself`() {
        val stringValue = mutableExpression<String>(
            "http://@{host}/api?int_param=@{hundred}",
            TYPE_HELPER_STRING
        ).evaluate(underTest)
        Assert.assertEquals("http://yandex.ru/api?int_param=100", stringValue)
    }

    @Test
    fun `boolean variable evaluates in boolean`() {
        Assert.assertTrue(
            mutableExpression<Boolean>(
                "@{logical_true}",
                TYPE_HELPER_BOOLEAN
            ).evaluate(underTest)
        )
    }

    @Test
    fun `boolean variable inside string evaluates to int boolean (true is 1)`() {
        Assert.assertEquals(
            "bool_int:true",
            mutableExpression<String>("bool_int:@{logical_true}", TYPE_HELPER_STRING).evaluate(
                underTest
            )
        )
    }

    @Test
    fun `boolean variable inside string evaluates to int boolean (false is 0)`() {
        Assert.assertEquals(
            "bool_int:false",
            mutableExpression<String>("bool_int:@{logical_false}", TYPE_HELPER_STRING).evaluate(
                underTest
            )
        )
    }

    @Test
    fun `property with variable-like string`() {
        Assert.assertEquals(
            "@{host$[path",
            mutableExpression<String>("\\@{host$[path", TYPE_HELPER_STRING).evaluate(underTest)
        )
    }

    @Test
    fun `non-variable properties handling`() {
        Assert.assertEquals(
            "5",
            mutableExpression<String>("5", TYPE_HELPER_STRING).evaluate(underTest)
        )
    }

    @Test(expected = ParsingException::class)
    fun `parsing fails when variable cannot be resolved`() {
        mutableExpression<String>("@{undefined}", TYPE_HELPER_STRING).evaluate(underTest)
    }

    @Test(expected = ParsingException::class)
    fun `parsing fails when variable cannot be resolved when evaluable enabled`() {
        mutableExpression("@{undefined}", TYPE_HELPER_STRING)
            .evaluate(underTest)
    }

    @Test
    fun `resolving numeric variable as string will return type default`() {
        val intValue = mutableExpression<Int>(
            rawExpression = "@{path}",
            typeHelper = TYPE_HELPER_INT,
            logger = silentLogger
        ).evaluate(underTest)
        Assert.assertEquals(0, intValue)
    }

    @Test
    fun `resolving numeric variable as string will return type default with evaluable`() {
        val intValue = mutableExpression<Int>(
            rawExpression = "@{path}",
            typeHelper = TYPE_HELPER_INT,
            logger = silentLogger
        ).evaluate(underTest)
        Assert.assertEquals(0, intValue)
    }

    @Test
    fun `unknown variables fallback to field type defaults`() {
        val expression = mutableExpression(
            "scheme://action?value=@{this.random_property}",
            TYPE_HELPER_STRING,
            logger = silentLogger)

        val uri = expression.evaluate(underTest)

        Assert.assertEquals("", uri)
    }

    @Test
    fun `unknown variables fallback to field type defaults when evaluable enabled`() {
        val expression = mutableExpression(
            "scheme://action?value=@{this.random_property}",
            TYPE_HELPER_STRING,
            logger = silentLogger)

        val uri = expression.evaluate(underTest)

        Assert.assertEquals("", uri)
    }

    private fun <T: Any> mutableExpression(
        rawExpression: String,
        typeHelper: TypeHelper<T>,
        logger: ParsingErrorLogger = failFastLogger,
        validator: (T) -> Boolean = { true },
    ) = Expression.MutableExpression<T,T>(
        expressionKey = "some_key",
        rawExpression = rawExpression,
        validator = validator,
        converter = { it },
        logger = logger,
        typeHelper = typeHelper,
    )
}
