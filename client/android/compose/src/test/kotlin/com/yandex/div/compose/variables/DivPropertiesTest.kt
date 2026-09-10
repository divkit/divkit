package com.yandex.div.compose.variables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.property
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.stringProperty
import com.yandex.div.test.data.text
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.variable
import com.yandex.div2.DivData
import com.yandex.div2.DivEvaluableType
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class DivPropertiesTest {
    private val reporter = TestReporter()
    private val variableController = DivVariableController()

    private val configuration = DivConfiguration(
        reporter = reporter,
        variableController = variableController,
    )
    private val context = DivContext(
        baseContext = getApplicationContext(),
        configuration = configuration,
    )

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `property value changes after backing variable changes`() {
        val value = Variable.StringVariable("value", "initial value")
        variableController.declare(value)

        setContent(
            data(
                text(id = "title", text = expression("@{title}")),
                variables = listOf(
                    stringProperty(
                        name = "title",
                        get = "@{alias}",
                    ),
                    stringProperty(
                        name = "alias",
                        get = "@{value}",
                    )
                )
            )
        )

        rule.onNodeWithTag("title").assertTextEquals("initial value")

        rule.runOnIdle { value.set("new value") }

        rule.onNodeWithTag("title").assertTextEquals("new value")
    }

    @Test
    fun `property setter uses its scope`() {
        setContent(
            data(
                container(
                    items = listOf(
                        text(
                            id = "root",
                            text = expression("@{title}"),
                        ),
                        text(
                            id = "button",
                            text = expression("@{title}"),
                            variables = listOf(
                                variable(name = "title", value = "initial item value")
                            ),
                            action = action(
                                typed = setVariableAction(
                                    name = "title_property",
                                    value = typedValue("updated value")
                                )
                            ),
                        )
                    )
                ),
                variables = listOf(
                    variable(name = "title", value = "initial root value"),
                    stringProperty(
                        name = "title_property",
                        get = "@{title}",
                        set = listOf(
                            action(
                                url = uriExpression("div-action://set_variable?name=title&value=@{new_value}")
                            )
                        ),
                    )
                )
            )
        )

        rule.onNodeWithTag("button").performClick()

        rule.onNodeWithTag("root").assertTextEquals("updated value")
        rule.onNodeWithTag("button").assertTextEquals("initial item value")
    }

    @Test
    fun `boolean property state writes through setter`() {
        val variable = Variable.BooleanVariable("variable", false)
        variableController.declare(variable)

        var state: MutableState<Boolean>? = null
        setPropertyContent(type = DivEvaluableType.BOOLEAN) {
            state = mutableStateFromBooleanVariable("property")
        }

        rule.runOnIdle { assertNotNull(state).value = true }

        assertEquals(true, variable.getValue())
    }

    @Test
    fun `integer property state writes through setter`() {
        val variable = Variable.IntegerVariable("variable", 1)
        variableController.declare(variable)

        var state: MutableState<Long>? = null
        setPropertyContent(type = DivEvaluableType.INTEGER) {
            state = mutableStateFromIntegerVariable("property")
        }

        rule.runOnIdle { assertNotNull(state).value = 2L }

        assertEquals(2L, variable.getValue())
    }

    @Test
    fun `string binding rejects integer property`() {
        reporter.failOnError = false

        variableController.declare(Variable.IntegerVariable("variable", 1))

        var state: MutableState<String>? = null
        setPropertyContent(type = DivEvaluableType.INTEGER) {
            state = mutableStateFromStringVariable("property")
        }

        assertNull(state)
        assertEquals(listOf("variable [property] is not a string variable"), reporter.errors)
    }

    @Test
    fun `property state reads latest value after reentering composition`() {
        val variable = Variable.StringVariable("variable", "initial value")
        variableController.declare(variable)

        var shown by mutableStateOf(true)
        var state: MutableState<String>? = null
        setPropertyContent(type = DivEvaluableType.STRING) {
            if (shown) {
                state = mutableStateFromStringVariable("property")
            }
        }

        rule.runOnIdle { shown = false }
        rule.runOnIdle { variable.set("new value") }

        assertEquals("initial value", state?.value)

        rule.runOnIdle { shown = true }
        rule.waitForIdle()

        assertEquals("new value", state?.value)
    }

    private fun setPropertyContent(
        type: DivEvaluableType,
        content: @Composable () -> Unit
    ) {
        val card = data(
            text(text = "property"),
            variables = listOf(
                property(
                    name = "property",
                    valueType = type,
                    get = "@{variable}",
                    set = listOf(
                        action(url = uriExpression("div-action://set_variable?name=variable&value=@{new_value}"))
                    ),
                )
            )
        )
        rule.setContent {
            CompositionLocalProvider(
                LocalContext provides context,
                LocalComponent provides context.getViewContext(card).rootLocalComponent,
                content = content
            )
        }
    }

    private fun setContent(content: DivData) {
        rule.setContent(configuration = configuration, data = content)
    }
}
