package com.yandex.div.compose.actions

import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.internal.variables.toVariable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.color
import com.yandex.div.test.data.property
import com.yandex.div.test.data.throwingErrorLogger
import com.yandex.div.test.data.typedColorValue
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.typedUriValue
import com.yandex.div.test.data.typedValue
import com.yandex.div2.DivAction
import com.yandex.div2.DivEvaluableType
import org.json.JSONArray
import org.json.JSONObject
import org.junit.runner.RunWith
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class SetVariableActionHandlerWithPropertiesTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()

    private val reporter: TestReporter
        get() = actionHandlerEnvironment.reporter

    private val variableController: DivVariableController
        get() = actionHandlerEnvironment.variableController

    @BeforeTest
    fun setUp() {
        actionHandlerEnvironment.init(
            setVariableActionHandler = SetVariableActionHandler(
                reporter = reporter
            )
        )
    }

    @Test
    fun `set string property`() {
        val variable = Variable.StringVariable("var", "value")
        variableController.declare(variable)
        declareProperty(DivEvaluableType.STRING)

        handle(
            action(typed = setVariableAction("property", typedValue("new value")))
        )

        assertEquals("new value", variable.getValue())
    }

    @Test
    fun `set boolean property`() {
        val variable = Variable.BooleanVariable("var", false)
        variableController.declare(variable)
        declareProperty(DivEvaluableType.BOOLEAN)

        handle(
            action(typed = setVariableAction("property", typedValue(true)))
        )

        assertEquals(true, variable.getValue())
    }

    @Test
    fun `set integer property`() {
        val variable = Variable.IntegerVariable("var", 1)
        variableController.declare(variable)
        declareProperty(DivEvaluableType.INTEGER)

        handle(
            action(typed = setVariableAction("property", typedValue(2)))
        )

        assertEquals(2L, variable.getValue())
    }

    @Test
    fun `set number property`() {
        val variable = Variable.DoubleVariable("var", 1.5)
        variableController.declare(variable)
        declareProperty(DivEvaluableType.NUMBER)

        handle(
            action(typed = setVariableAction("property", typedValue(2.5)))
        )

        assertEquals(2.5, variable.getValue())
    }

    @Test
    fun `set color property`() {
        val variable = Variable.ColorVariable("var", 0xFFAABBCC.toInt())
        variableController.declare(variable)
        declareProperty(DivEvaluableType.COLOR)

        handle(
            action(typed = setVariableAction("property", typedColorValue(0xFFBBCCDD)))
        )

        assertEquals(color(0xFFBBCCDD), variable.getValue())
    }

    @Test
    fun `set url property`() {
        val variable = Variable.UrlVariable("var", "https://example.com/initial".toUri())
        variableController.declare(variable)
        declareProperty(DivEvaluableType.URL)

        handle(
            action(
                typed = setVariableAction(
                    name = "property",
                    value = typedUriValue("https://example.com/updated"),
                )
            )
        )

        assertEquals("https://example.com/updated".toUri(), variable.getValue())
    }

    @Test
    fun `set array property`() {
        val variable = Variable.ArrayVariable("var", JSONArray())
        variableController.declare(variable)
        declareProperty(DivEvaluableType.ARRAY)

        handle(
            action(
                typed = setVariableAction(
                    name = "property",
                    value = typedValue(JSONArray("[1, {\"title\": \"updated\"}]")),
                )
            )
        )

        assertEquals("[1,{\"title\":\"updated\"}]", variable.getValue().toString())
    }

    @Test
    fun `set dict property`() {
        val variable = Variable.DictVariable("var", JSONObject())
        variableController.declare(variable)
        declareProperty(DivEvaluableType.DICT)

        handle(
            action(
                typed = setVariableAction(
                    name = "property",
                    value = typedValue(JSONObject("{\"items\": [1, 2]}")),
                )
            )
        )

        assertEquals("{\"items\":[1,2]}", variable.getValue().toString())
    }

    @Test
    fun `set property without setter reports error and keeps value`() {
        reporter.failOnError = false

        val variable = Variable.StringVariable("var", "initial value")
        variableController.declare(variable)
        declareProperty(type = DivEvaluableType.STRING, set = null)

        handle(
            action(typed = setVariableAction("property", typedValue("new value")))
        )

        assertEquals("initial value", variable.getValue())
        assertEquals(
            listOf("Cannot set property. No setters provided. Name: 'property'"),
            reporter.errors
        )
    }

    private fun declareProperty(
        type: DivEvaluableType,
        set: List<DivAction>? = listOf(
            action(url = uriExpression("div-action://set_variable?name=var&value=@{new_value}"))
        ),
    ) {
        val property = property(
            name = "property",
            valueType = type,
            get = "@{var}",
            set = set,
        ).toVariable(
            resolver = actionHandlerEnvironment.expressionResolver,
            propertyVariableExecutor = actionHandlerEnvironment.propertyVariableExecutor,
            logger = throwingErrorLogger,
        )
        variableController.declare(property!!)
    }

    private fun handle(action: DivAction) = actionHandlerEnvironment.handle(action)
}
