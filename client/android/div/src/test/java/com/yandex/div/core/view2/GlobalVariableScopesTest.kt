package com.yandex.div.core.view2

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.expression.variables.GlobalVariableController
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.view2.state.DivStateSwitcher
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.data.Variable
import com.yandex.div2.DivData
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner


private const val VARIABLE_A = "var_a"

private const val CARD_WITH_VARIABLE = """
{
    "log_id": "div2_sample_card",
    "states": [
        {
            "state_id": 0,
            "div": {
                "type": "text",
                "text": "@{${VARIABLE_A}}"
            }
        }
    ]
}
"""

@RunWith(RobolectricTestRunner::class)
class GlobalVariableScopesTest {
    private class ComponentHolder(variableController: GlobalVariableController? = null) {
        private val parsingEnvironment = DivParsingEnvironment({ e -> throw java.lang.AssertionError(e) })
        private val tag = DivDataTag("tag")

        fun setVariableViaDivView(variableName: String, value: String) {
            val divData = DivData(parsingEnvironment, JSONObject(CARD_WITH_VARIABLE))
            div2View.setData(divData, tag)
            Assert.assertEquals(null, div2View.setVariable(variableName, value))
        }

        private val divImageLoader = mock<DivImageLoader>()
        private val activity = Robolectric.buildActivity(Activity::class.java).get()
        private val div2Context = Div2Context(
                baseContext = activity,
                configuration = DivConfiguration.Builder(divImageLoader)
                        .globalVariableController(variableController)
                        .build()
        )
        private val stateSwitcher = mock<DivStateSwitcher>()
        private val component = TestComponent(
                wrapped = div2Context.div2Component,
                viewComponentBuilder = TestViewComponentBuilder(
                        wrapped = div2Context.div2Component.viewComponent(),
                        stateSwitcher = stateSwitcher
                )
        )

        private val div2View = Div2View(div2Context)

        val variableController = component.globalVariableController
    }

    @Test
    fun `global variables could be shared between contexts`() {
        val sharedVariableController = GlobalVariableController()

        val component1 = ComponentHolder(sharedVariableController)
        component1.variableController.putOrUpdate(Variable.StringVariable(VARIABLE_A, "default"))
        component1.setVariableViaDivView(VARIABLE_A, "context#1")
        Assert.assertEquals("context#1", sharedVariableController.get(VARIABLE_A)!!.getValue())

        val component2 = ComponentHolder(sharedVariableController)
        component2.setVariableViaDivView(VARIABLE_A, "context#2")
        Assert.assertEquals(
                component1.variableController.get(VARIABLE_A)!!.getValue(),
                component2.variableController.get(VARIABLE_A)!!.getValue())
    }

    @Test
    fun `global variables not shared between contexts by default`() {
        val component1 = ComponentHolder()
        component1.variableController.putOrUpdate(Variable.StringVariable(VARIABLE_A, "default"))
        component1.setVariableViaDivView(VARIABLE_A, "context#1")

        val component2 = ComponentHolder()
        component2.variableController.putOrUpdate(Variable.StringVariable(VARIABLE_A, "default"))
        component2.setVariableViaDivView(VARIABLE_A, "context#2")

        Assert.assertNotEquals(
                component1.variableController.get(VARIABLE_A)!!.getValue(),
                component2.variableController.get(VARIABLE_A)!!.getValue())
    }
}
