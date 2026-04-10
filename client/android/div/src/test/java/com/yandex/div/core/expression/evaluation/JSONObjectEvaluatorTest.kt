package com.yandex.div.core.expression.evaluation

import com.yandex.div.core.DivActionPerformer
import com.yandex.div.core.expression.ExpressionTestCaseUtils
import com.yandex.div.core.expression.local.ExpressionsRuntimeProvider
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.expression.storedvalues.StoredValuesController
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.evaluable.types.Url
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import com.yandex.div2.DivEvaluableType
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.TimeZone

/**
 * Tests for [JSONObjectEvaluator].
 */
class JSONObjectEvaluatorTest {
    private val mockDivVariableController = mock<DivVariableController> {
        on { variableSource } doReturn mock()
    }
    private val runtimeStore = mock<RuntimeStore> {
        on { viewProvider } doReturn mock()
    }
    private val errorCollector = mock<ErrorCollector>()

    init {
        UiThreadHandler.setInTestMode(enabled = true)
    }

    @After
    fun tearDown() {
        UiThreadHandler.setInTestMode(enabled = false)
    }

    @Test
    fun `nested object replaces string expression`() {
        val underTest = createSUT(
            payload = JSONObject().put("nested", JSONObject().put("title", "@{title_variable}")),
            variables = arrayOf(
                Variable.StringVariable(
                    "title_variable",
                    "variable_value"
                )
            ),
        )

        val result = underTest.get().getOrThrow()
        val nested = result["nested"] as Map<String, Any?>

        Assert.assertEquals("variable_value", nested["title"])
    }

    @Test
    fun `array of string expressions evaluated as string`() {
        val underTest = createSUT(
            payload = JSONObject().put("arr", JSONArray().put("@{a}").put("@{b}")),
            variables = arrayOf(
                Variable.StringVariable("a", "a variable value"),
                Variable.StringVariable("b", "b variable value"),
            ),
        )

        val arr = underTest.get().getOrThrow()["arr"] as List<*>

        Assert.assertEquals("a variable value", arr[0])
        Assert.assertEquals("b variable value", arr[1])
    }

    @Test
    fun `type resolver selects integer type`() {
        val underTest = createSUT(
            payload = JSONObject()
                .put("int_typed", "@{count}")
                .put("string_typed", "@{count}"),
            variables = arrayOf(
                Variable.IntegerVariable("count", 42),
            ),
        )

        val result = underTest.get(object : TypeResolver {
            override fun resolveExpressionType(key: String, valueProvider: (key: String) -> Any?): DivEvaluableType {
                return if (key == "int_typed") DivEvaluableType.INTEGER else DivEvaluableType.STRING
            }
        }).getOrThrow()

        Assert.assertEquals(42L, result["int_typed"])
        Assert.assertEquals("42", result["string_typed"])
    }

    @Test
    fun `type resolver selects integer type with help of nearby props`() {
        val underTest = createSUT(
            payload = JSONObject()
                .put("type", "integer")
                .put("value", "@{count}"),
            variables = arrayOf(
                Variable.IntegerVariable("count", 42),
            ),
        )

        val result = underTest.get(object : TypeResolver {
            override fun resolveExpressionType(key: String, valueProvider: (key: String) -> Any?): DivEvaluableType {
                return if (valueProvider("type") == "integer") DivEvaluableType.INTEGER else DivEvaluableType.STRING
            }
        }).getOrThrow()

        Assert.assertEquals(42L, result["value"])
    }

    @Test
    fun `plain string without at brace unchanged`() {
        val underTest = createSUT(
            payload = JSONObject().put("msg", "literal text"),
            variables = arrayOf(
                Variable.StringVariable("literal text", "variable value"),
            )
        )
        val result = underTest.get().getOrThrow()
        Assert.assertEquals("literal text", result["msg"])
    }

    @Test
    fun `type resolver throw yields failure`() {
        val underTest = createSUT(
            payload = JSONObject().put("bad", "@{x}"),
            variables = arrayOf(
                Variable.StringVariable("x", "v"),
            ),
        )

        val result = underTest.get(object : TypeResolver {
            override fun resolveExpressionType(key: String, valueProvider: (key: String) -> Any?): DivEvaluableType {
                if (key == "bad") {
                    error("resolver failed")
                }
                return DivEvaluableType.STRING
            }
        })

        Assert.assertTrue(result.isFailure)
    }

    @Test
    fun `type resolver selects datetime type`() {
        val underTest = createSUT(
            payload = JSONObject()
                .put("datetime_typed", "@{parseUnixTime(0)}"),
        )

        val result = underTest.get(object : TypeResolver {
            override fun resolveExpressionType(key: String, valueProvider: (key: String) -> Any?): DivEvaluableType {
                return DivEvaluableType.DATETIME
            }
        }).getOrThrow()

        val value = result["datetime_typed"]
        Assert.assertTrue(
            "Expecting Datetime type, instead got '${value?.javaClass?.simpleName}'",
            value is DateTime
        )
        Assert.assertEquals(DateTime(0L, TimeZone.getDefault()), value as DateTime)
    }

    @Test
    fun `type resolver selects url type`() {
        val underTest = createSUT(
            payload = JSONObject()
                .put("url_typed", "@{toUrl('https://wikipedia.org')}"),
        )

        val result = underTest.get(object : TypeResolver {
            override fun resolveExpressionType(key: String, valueProvider: (key: String) -> Any?): DivEvaluableType {
                return DivEvaluableType.URL
            }
        }).getOrThrow()

        val value = result["url_typed"]
        Assert.assertTrue(
            "Expecting Url type, instead got '${value?.javaClass?.simpleName}'",
            value is Url
        )
        Assert.assertEquals(Url("https://wikipedia.org"), value as Url)
    }

    private fun createSUT(
        payload: JSONObject,
        vararg variables: Variable,
    ): JSONObjectEvaluator {
        val data: DivData = ExpressionTestCaseUtils.createDivDataFromTestVars(
            variables.map { it.writeToJSON() }.toList(),
            emptyList(),
            ParsingErrorLogger.LOG
        )
        val runtimeProvider = ExpressionsRuntimeProvider(
            mockDivVariableController,
            mock<DivActionPerformer>(),
            mock<StoredValuesController>(),
        )
        return JSONObjectEvaluator(
            input = payload,
            resolver = runtimeProvider.createRootRuntime(
                data,
                errorCollector,
                runtimeStore
            ).expressionResolver,
            logger = ParsingErrorLogger.LOG,
        )
    }
}