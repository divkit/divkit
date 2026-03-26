package com.yandex.div.core.expression

import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.expression.variables.VariableControllerImpl
import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.EvaluableType
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.Function
import com.yandex.div.evaluable.FunctionProvider
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div.internal.parser.Converter
import com.yandex.div.internal.parser.NUMBER_TO_DOUBLE
import com.yandex.div.internal.parser.TYPE_HELPER_BOOLEAN
import com.yandex.div.internal.parser.TYPE_HELPER_DOUBLE
import com.yandex.div.internal.parser.TYPE_HELPER_INT
import com.yandex.div.internal.parser.TYPE_HELPER_STRING
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.intExpression
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import java.util.TimeZone

/**
 * Tests for [ExpressionResolverImpl].
 */
@RunWith(RobolectricTestRunner::class)
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
            Variable.IntegerVariable("some_number", 42),
            Variable.IntegerVariable("another_number", 954),
            Variable.IntegerVariable("timer", 2000)
        ).forEach {
            map[it.name] = it
        }
    }

    private val globalVariables = mutableMapOf<String, Variable>().also { map ->
        listOf(
            Variable.IntegerVariable("global_number", 777)
        ).forEach {
            map[it.name] = it
        }
    }

    private val failFastLogger = ParsingErrorLogger { e -> throw e }
    private val silentLogger = ParsingErrorLogger { e -> e.printStackTrace() }
    private val variableController = DivVariableController().apply {
        declare(*globalVariables.values.toTypedArray())
    }
    private val externalVariables = VariableControllerImpl(
        viewProvider = { mock() }
    ).apply {
        variables.values.forEach {
            declare(it)
        }
        addSource(variableController.variableSource)
    }

    private val evaluationContext = EvaluationContext(
        variableProvider = { externalVariables.getMutableVariable(it)?.getValue() },
        storedValueProvider = mock(),
        functionProvider = GeneratedBuiltinFunctionProvider,
        warningSender = { _, _ -> }
    )

    private val underTest = ExpressionResolverImpl(
        "",
        mock(),
        externalVariables,
        Evaluator(evaluationContext),
        mock()
    )

    private val withFuncGetCallback = { callback: () -> Unit ->
        ExpressionResolverImpl(
            "",
            mock(),
            VariableControllerImpl(mock()),
            Evaluator(
                EvaluationContext(
                    variableProvider = evaluationContext.variableProvider,
                    storedValueProvider = evaluationContext.storedValueProvider,
                    functionProvider = FunctionProviderDecorator(object : FunctionProvider {
                        override fun get(name: String, args: List<EvaluableType>): Function {
                            callback()
                            return evaluationContext.functionProvider.get(name, args)
                        }

                        override fun getMethod(name: String, args: List<EvaluableType>): Function {
                            callback()
                            return evaluationContext.functionProvider.getMethod(name, args)
                        }
                    }),
                    warningSender = evaluationContext.warningSender
                )
            ),
            mock()
        )
    }

    @Before
    fun updateSubscriptions() {
        underTest.subscribeOnVariables()
    }

    @Test
    fun `string with multiple variables`() {
        assertEquals(
            "http://yandex.ru/mail",
            expression("http://@{host}/@{path}").evaluate(underTest)
        )
    }

    @Test
    fun `string with single variable at center`() {
        assertEquals(
            "http://yandex.ru/api?",
            expression("http://@{host}/api?").evaluate(underTest)
        )
    }

    @Test
    fun `int variable access`() {
        assertEquals(100, intExpression("@{hundred}").evaluate(underTest))
    }

    @Test
    fun `double variable accessed via numeric expression`() {
        val expression = mutableExpressionWithConverter(
            rawExpression = "@{hundred}",
            converter = NUMBER_TO_DOUBLE,
            typeHelper = TYPE_HELPER_DOUBLE
        )
        assertEquals(100.0f.toDouble(), expression.evaluate(underTest), 0f.toDouble())
    }

    @Test
    fun `string expression can access non-string variables`() {
        assertEquals("100", expression("@{hundred}").evaluate(underTest))
    }

    @Test
    fun `string expression may use non-string variables for composing itself`() {
        assertEquals(
            "http://yandex.ru/api?int_param=100",
            expression("http://@{host}/api?int_param=@{hundred}").evaluate(underTest)
        )
    }

    @Test
    fun `boolean variable evaluates in boolean`() {
        assertTrue(
            mutableExpression("@{logical_true}", TYPE_HELPER_BOOLEAN).evaluate(underTest)
        )
    }

    @Test
    fun `boolean variable inside string evaluates to int boolean (true is 1)`() {
        assertEquals(
            "bool_int:true",
            expression("bool_int:@{logical_true}").evaluate(underTest)
        )
    }

    @Test
    fun `boolean variable inside string evaluates to int boolean (false is 0)`() {
        assertEquals(
            "bool_int:false",
            expression("bool_int:@{logical_false}").evaluate(underTest)
        )
    }

    @Test
    fun `property with variable-like string`() {
        assertEquals(
            "@{host$[path",
            expression("\\@{host$[path").evaluate(underTest)
        )
    }

    @Test
    fun `non-variable properties handling`() {
        assertEquals("5", expression("5").evaluate(underTest))
    }

    @Test(expected = ParsingException::class)
    fun `parsing fails when variable cannot be resolved when evaluable enabled`() {
        mutableExpression("@{undefined}", TYPE_HELPER_STRING).evaluate(underTest)
    }

    @Test
    fun `resolving string variable as integer will return type default`() {
        val expression = mutableExpression(
            rawExpression = "@{path}",
            typeHelper = TYPE_HELPER_INT,
            logger = silentLogger
        )
        assertEquals(0, expression.evaluate(underTest))
    }

    @Test
    fun `unknown variables fallback to field type defaults`() {
        val expression = mutableExpression(
            "scheme://action?value=@{this.random_property}",
            TYPE_HELPER_STRING,
            logger = silentLogger
        )
        assertEquals("", expression.evaluate(underTest))
    }

    @Test
    fun `cache entry is deleted after the local variable is changed`() {
        val expression = intExpression("@{some_number}")

        assertEquals(42, expression.evaluate(underTest))

        variables["some_number"]?.set("12")

        assertEquals(12, expression.evaluate(underTest))
    }

    @Test
    fun `cache entry is deleted after the global variable is changed`() {
        val expression = intExpression("@{global_number}")

        assertEquals(777, expression.evaluate(underTest))

        globalVariables["global_number"]?.set("120")

        assertEquals(120, expression.evaluate(underTest))
    }

    @Test
    fun `pure expression saved in cache`() {
        var counter = 0
        val expression = mutableExpression(
            rawExpression = "@{sum(2,2)}",
            typeHelper = TYPE_HELPER_INT
        )
        val increaseCounter: () -> Unit = {
            counter++
        }
        val expressionResolverImpl = withFuncGetCallback(increaseCounter)
        expression.evaluate(expressionResolverImpl)
        expression.evaluate(expressionResolverImpl)
        assertEquals(1, counter)
    }

    @Test
    fun `non pure expression not saved in cache`() {
        var counter = 0
        val expression = mutableExpression(
            rawExpression = "@{nowLocal()}",
            typeHelper = TypeHelper.from(DateTime(0, TimeZone.getDefault())) {
                it is DateTime
            }
        )
        val increaseCounter: () -> Unit = {
            counter++
        }
        val expressionResolverImpl = withFuncGetCallback(increaseCounter)
        expression.evaluate(expressionResolverImpl)
        expression.evaluate(expressionResolverImpl)
        assertEquals(2, counter)
    }

    @Test
    fun `test subscribe to expression`() {
        var callbackCalled = false
        underTest.subscribeToExpression(
            "@{some_number+another_number}",
            listOf("some_number", "another_number")
        ) {
            callbackCalled = true
        }
        variables["another_number"]?.set("111")
        assertTrue(callbackCalled)
    }

    @Test
    fun `test nested subscribe to expression`() {
        var callbackCalled = false
        underTest.subscribeToExpression(
            "@{some_number}",
            listOf("some_number")
        ) {
            underTest.subscribeToExpression(
                "@{some_number+${Any().hashCode()}}",
                listOf("some_number")
            ) {
                callbackCalled = true
            }
        }
        variables["some_number"]?.set("111")
        variables["some_number"]?.set("112")
        assert(callbackCalled)
    }

    @Test
    fun `cache work with converter correctly`() {
        val expression = intExpression("@{timer}")

        assertEquals(2000, expression.evaluate(underTest))

        val expressionWithConverter = mutableExpressionWithConverter<Int, String>(
            rawExpression = "@{timer}",
            typeHelper = TYPE_HELPER_STRING,
            converter = { it.toString() }
        )
        assertEquals("2000", expressionWithConverter.evaluate(underTest))
    }

    @Test(expected = ParsingException::class)
    fun `cache work with validator correctly`() {
        val expression = intExpression("@{timer}")

        assertEquals(2000, expression.evaluate(underTest))

        val expressionWithValidator = mutableExpression(
            rawExpression = "@{timer}",
            typeHelper = TYPE_HELPER_INT,
            validator = { false }
        )
        expressionWithValidator.evaluate(underTest)
    }

    @Test
    fun `on expression changed callback called when variable declared`() {
        val expression = mutableExpression(
            rawExpression = "@{none_var}",
            typeHelper = TYPE_HELPER_INT
        )

        var declaredValue = 0L
        expression.observeAndGet(underTest) {
            declaredValue = it
        }

        variableController.declare(Variable.IntegerVariable("none_var", 56))

        assertEquals(56, declaredValue)
    }

    private fun <T : Any> mutableExpression(
        rawExpression: String,
        typeHelper: TypeHelper<T>,
        logger: ParsingErrorLogger = failFastLogger,
        validator: (T) -> Boolean = { true },
    ) = Expression.MutableExpression<T, T>(
        expressionKey = "some_key",
        rawExpression = rawExpression,
        validator = validator,
        converter = { it },
        logger = logger,
        typeHelper = typeHelper,
    )

    private fun <R : Any, T : Any> mutableExpressionWithConverter(
        rawExpression: String,
        typeHelper: TypeHelper<T>,
        converter: Converter<R, T>,
        logger: ParsingErrorLogger = failFastLogger,
        validator: (T) -> Boolean = { true },
    ) = Expression.MutableExpression(
        expressionKey = "some_key",
        rawExpression = rawExpression,
        validator = validator,
        converter = converter,
        logger = logger,
        typeHelper = typeHelper,
    )
}
