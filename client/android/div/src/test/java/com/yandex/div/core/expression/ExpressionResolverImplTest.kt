package com.yandex.div.core.expression

import com.yandex.div.core.expression.variables.GlobalVariableController
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.util.EnableAssertsRule
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.internal.parser.*
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.expressions.Expression
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import java.util.*

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
    private val globalVariableController = GlobalVariableController().apply {
        declare(*globalVariables.values.toTypedArray())
    }
    private val externalVariables = VariableController().apply {
        variables.values.forEach {
            declare(it)
        }
        addSource(globalVariableController.variableSource)
    }

    private val evaluationContext = EvaluationContext(
        variableProvider = { externalVariables.getMutableVariable(it)?.getValue() },
        storedValueProvider = mock(),
        functionProvider = GeneratedBuiltinFunctionProvider,
        warningSender = { _, _ -> }
    )

    private val underTest = ExpressionResolverImpl(
        externalVariables,
        Evaluator(evaluationContext),
        mock(),
    )

    private val withFuncGetCallback = { callback: () -> Unit ->
        ExpressionResolverImpl(
            VariableController(),
            Evaluator(
                EvaluationContext(
                    variableProvider = evaluationContext.variableProvider,
                    storedValueProvider = evaluationContext.storedValueProvider,
                    functionProvider = { name, args ->
                        callback()
                        evaluationContext.functionProvider.get(name, args)
                    },
                    warningSender = evaluationContext.warningSender
                )
            ),
            mock()
        )
    }

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
        val mutableExpression = mutableExpression<Long>(
            rawExpression = "@{hundred}",
            typeHelper = TYPE_HELPER_INT,
        )
        Assert.assertEquals(100, mutableExpression.evaluate(underTest))
    }

    @Test
    fun `int variable access`() {
        val intValue = mutableExpression<Long>("@{hundred}", TYPE_HELPER_INT).evaluate(underTest)
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
            typeHelper = TYPE_HELPER_DOUBLE
        )
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
        val intValue = mutableExpression<Long>(
            rawExpression = "@{path}",
            typeHelper = TYPE_HELPER_INT,
            logger = silentLogger
        ).evaluate(underTest)
        Assert.assertEquals(0, intValue)
    }

    @Test
    fun `resolving numeric variable as string will return type default with evaluable`() {
        val intValue = mutableExpression<Long>(
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
            logger = silentLogger
        )

        val uri = expression.evaluate(underTest)

        Assert.assertEquals("", uri)
    }

    @Test
    fun `unknown variables fallback to field type defaults when evaluable enabled`() {
        val expression = mutableExpression(
            "scheme://action?value=@{this.random_property}",
            TYPE_HELPER_STRING,
            logger = silentLogger
        )

        val uri = expression.evaluate(underTest)

        Assert.assertEquals("", uri)
    }

    @Test
    fun `cache entry is deleted after the local variable is changed`() {
        val mutableExpression = mutableExpression<Long>(
            rawExpression = "@{some_number}",
            typeHelper = TYPE_HELPER_INT,
        )
        Assert.assertEquals(42, mutableExpression.evaluate(underTest))
        variables["some_number"]?.set("12")
        Assert.assertEquals(12, mutableExpression.evaluate(underTest))
    }

    @Test
    fun `cache entry is deleted after the global variable is changed`() {
        val mutableExpression = mutableExpression<Long>(
            rawExpression = "@{global_number}",
            typeHelper = TYPE_HELPER_INT,
        )
        Assert.assertEquals(777, mutableExpression.evaluate(underTest))
        globalVariables["global_number"]?.set("120")
        Assert.assertEquals(120, mutableExpression.evaluate(underTest))
    }

    @Test
    fun `pure expression saved in cache`() {
        var counter = 0
        val mutableExpression = mutableExpression(
            rawExpression = "@{sum(2,2)}",
            typeHelper = TYPE_HELPER_INT
        )
        val increaseCounter = {
            counter++
        }
        val expressionResolverImpl = withFuncGetCallback(increaseCounter)
        mutableExpression.evaluate(expressionResolverImpl)
        mutableExpression.evaluate(expressionResolverImpl)
        assert(counter == 1)
    }

    @Test
    fun `non pure expression not saved in cache`() {
        var counter = 0
        val mutableExpression = mutableExpression(
            rawExpression = "@{nowLocal()}",
            typeHelper = TypeHelper.from(DateTime(0, TimeZone.getDefault())) {
                it is DateTime
            }
        )
        val increaseCounter = {
            counter++
        }
        val expressionResolverImpl = withFuncGetCallback(increaseCounter)
        mutableExpression.evaluate(expressionResolverImpl)
        mutableExpression.evaluate(expressionResolverImpl)
        assert(counter == 2)
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
        assert(callbackCalled)
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
        val mutableExpression = mutableExpression<Long>(
            rawExpression = "@{timer}",
            typeHelper = TYPE_HELPER_INT,
        )
        Assert.assertEquals(2000, mutableExpression.evaluate(underTest))
        val mutableExpressionWithConverter = mutableExpressionWithConverter<Int, String>(
            rawExpression = "@{timer}",
            typeHelper = TYPE_HELPER_STRING,
            converter = { it.toString() }
        )
        Assert.assertEquals("2000", mutableExpressionWithConverter.evaluate(underTest))
    }

    @Test(expected = ParsingException::class)
    fun `cache work with validator correctly`() {
        val mutableExpression = mutableExpression<Long>(
            rawExpression = "@{timer}",
            typeHelper = TYPE_HELPER_INT,
        )
        Assert.assertEquals(2000, mutableExpression.evaluate(underTest))
        val mutableExpressionWithValidator = mutableExpression<Long>(
            rawExpression = "@{timer}",
            typeHelper = TYPE_HELPER_INT,
            validator = { false }
        )
        mutableExpressionWithValidator.evaluate(underTest)
    }

    @Test
    fun `on expression changed callback called when variable declared`() {
        val mutableExpression = mutableExpression<Long>(
            rawExpression = "@{none_var}",
            typeHelper = TYPE_HELPER_INT,
        )
        var declaredValue = 0L
        mutableExpression.observeAndGet(underTest) {
            declaredValue = it
        }
        globalVariableController.declare(Variable.IntegerVariable("none_var", 56))
        Assert.assertEquals(56, declaredValue)
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
    ) = Expression.MutableExpression<R, T>(
        expressionKey = "some_key",
        rawExpression = rawExpression,
        validator = validator,
        converter = converter,
        logger = logger,
        typeHelper = typeHelper,
    )
}
