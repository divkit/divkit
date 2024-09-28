package com.yandex.div.core.view2

import android.app.Activity
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableMutationException
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.DivData
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

private const val VARIABLE_NAME = "variable"
private const val INITIAL_VALUE = "initial"
private const val UPDATED_VALUE = "updated"
private const val UNDEFINED_VALUE = "undefined"

private const val CARD_WITH_GLOBAL_VARIABLE = """
{
    "log_id": "test_card",
    "states": [
      {
        "state_id": 0,
        "div": {
          "type": "text",
          "text": "@{$VARIABLE_NAME !: '$UNDEFINED_VALUE'}"
        }
      }
    ]
}
"""

private const val CARD_WITH_LOCAL_VARIABLE = """
{
    "log_id": "test_card",
    "variables": [
      {
        "name": "$VARIABLE_NAME",
        "type": "string",
        "value": "$INITIAL_VALUE"
      }
    ],
    "states": [
      {
        "state_id": 0,
        "div": {
          "type": "text",
          "text": "@{$VARIABLE_NAME}"
        }
      }
    ]
}
"""

@RunWith(RobolectricTestRunner::class)
class VariableUpdatesTest {

    private val parsingEnvironment = DivParsingEnvironment(ParsingErrorLogger.LOG)
    private val divImageLoader = mock<DivImageLoader>()
    private val div2Context = Div2Context(
        baseContext = Robolectric.buildActivity(Activity::class.java).get(),
        configuration = DivConfiguration.Builder(divImageLoader).build(),
        lifecycleOwner = null
    )
    private val variableController = div2Context.divVariableController

    @Test
    fun `DivData receives a global variable which was created before DivData was created`() {
        setGlobalVariable(VARIABLE_NAME, INITIAL_VALUE)

        val divView = Div2View(div2Context)
        setDataToDivView(CARD_WITH_GLOBAL_VARIABLE, divView)

        divView.assertVariableShown(INITIAL_VALUE)
    }

    @Test
    fun `DivData receives a global variable which was created after DivData was created`() {
        val divView = Div2View(div2Context)
        setDataToDivView(CARD_WITH_GLOBAL_VARIABLE, divView)

        setGlobalVariable(VARIABLE_NAME, INITIAL_VALUE)

        divView.assertVariableShown(INITIAL_VALUE)
    }

    @Test
    fun `DivData receives global variable updates after the destruction of its life cycle and its recreation`() {
        val lifecycleOwner = createLifecycleOwner()
        var divView = Div2View(div2Context.childContext(lifecycleOwner))
        setDataToDivView(CARD_WITH_GLOBAL_VARIABLE, divView)
        setGlobalVariable(VARIABLE_NAME, INITIAL_VALUE)

        (lifecycleOwner.lifecycle as TestLifecycle).destroy()

        divView = Div2View(div2Context.childContext(createLifecycleOwner()))
        setDataToDivView(CARD_WITH_GLOBAL_VARIABLE, divView)
        setGlobalVariable(VARIABLE_NAME, UPDATED_VALUE)

        divView.assertVariableShown(UPDATED_VALUE)
    }

    @Test
    fun `DivData receives global variables updates happened when DivData was destroyed`() {
        val lifecycleOwner = createLifecycleOwner()
        var divView = Div2View(div2Context.childContext(lifecycleOwner))
        setDataToDivView(CARD_WITH_GLOBAL_VARIABLE, divView)
        setGlobalVariable(VARIABLE_NAME, INITIAL_VALUE)

        (lifecycleOwner.lifecycle as TestLifecycle).destroy()
        setGlobalVariable(VARIABLE_NAME, UPDATED_VALUE)

        divView = Div2View(div2Context.childContext(createLifecycleOwner()))
        setDataToDivView(CARD_WITH_GLOBAL_VARIABLE, divView)

        divView.assertVariableShown(UPDATED_VALUE)
    }

    @Test
    fun `DivView receive new variable on DivVariableController#replaceAll`() {
        val divView = Div2View(div2Context)

        // set DivData without defining global variable, text will be 'undefined'
        setDataToDivView(CARD_WITH_GLOBAL_VARIABLE, divView)
        divView.assertVariableShown(UNDEFINED_VALUE)

        // setting global variable value to 'updated'
        val variable = Variable.StringVariable(VARIABLE_NAME, UPDATED_VALUE)
        variableController.replaceAll(variable)
        divView.assertVariableShown(UPDATED_VALUE)
    }

    @Test
    fun `DivView removes variable on DivVariableController#replaceAll`() {
        val divView = Div2View(div2Context)

        // setting global variable value to 'updated'
        setGlobalVariable(VARIABLE_NAME, UPDATED_VALUE)
        setDataToDivView(CARD_WITH_GLOBAL_VARIABLE, divView)
        divView.assertVariableShown(UPDATED_VALUE)

        // removing global variable, value should be updated to 'undefined'
        variableController.replaceAll()
        divView.assertVariableShown(UNDEFINED_VALUE)
    }

    @Test(expected = VariableMutationException::class)
    fun `DivVariableController#replaceAll cannot change variable type`() {
        val divView = Div2View(div2Context)

        // set DivData and define variable with type 'string'
        setGlobalVariable(VARIABLE_NAME, INITIAL_VALUE)
        setDataToDivView(CARD_WITH_GLOBAL_VARIABLE, divView)

        // trying to replace variable with 'double' one
        val variable = Variable.DoubleVariable(VARIABLE_NAME, 1.0)
        variableController.replaceAll(variable)
    }

    @Test
    fun `DivView removes variable on DivVariableController#removeAll`() {
        val divView = Div2View(div2Context)

        // setting global variable value to 'updated'
        setGlobalVariable(VARIABLE_NAME, UPDATED_VALUE)
        setDataToDivView(CARD_WITH_GLOBAL_VARIABLE, divView)
        divView.assertVariableShown(UPDATED_VALUE)

        // removing global variable, value should be updated to 'undefined'
        variableController.removeAll(VARIABLE_NAME)
        divView.assertVariableShown(UNDEFINED_VALUE)
    }

    @Test
    fun `DivData receives updates of local variable`() {
        val divView = Div2View(div2Context)
        setDataToDivView(CARD_WITH_LOCAL_VARIABLE, divView)
        divView.setVariable(VARIABLE_NAME, UPDATED_VALUE)

        divView.assertVariableShown(UPDATED_VALUE)
    }

    @Test
    fun `DivData save local variables value after recreate`() {
        val lifecycleOwner = createLifecycleOwner()
        var divView = Div2View(div2Context.childContext(lifecycleOwner))

        setDataToDivView(CARD_WITH_LOCAL_VARIABLE, divView)
        divView.setVariable(VARIABLE_NAME, UPDATED_VALUE)
        (lifecycleOwner.lifecycle as TestLifecycle).destroy()

        divView = Div2View(div2Context.childContext(null))
        setDataToDivView(CARD_WITH_LOCAL_VARIABLE, divView)

        divView.assertVariableShown(UPDATED_VALUE)
    }

    private fun Div2View.assertVariableShown(value: String) {
        Assert.assertEquals(value, (getChildAt(0) as TextView).text)
    }

    private fun setDataToDivView(name: String, divView: Div2View) {
        val divData = DivData(parsingEnvironment, JSONObject(name))
        divView.setData(divData, DivDataTag("tag"))
    }

    private fun setGlobalVariable(name: String, value: String) {
        variableController.putOrUpdate(Variable.StringVariable(name, value))
    }

    private fun createLifecycleOwner(): LifecycleOwner {
        val lifecycle = TestLifecycle()
        val owner = mock<LifecycleOwner> {
            on { getLifecycle() } doReturn lifecycle
        }
        lifecycle.owner = owner
        return owner
    }

    private class TestLifecycle: Lifecycle() {
        private val observers = mutableListOf<LifecycleObserver>()
        private var state: State = State.STARTED
        var owner: LifecycleOwner? = null

        override fun addObserver(observer: LifecycleObserver) {
            observers.add(observer)
        }

        override fun removeObserver(observer: LifecycleObserver) {
            observers.remove(observer)
        }

        override fun getCurrentState(): State {
            return state
        }

        fun destroy() {
            state = State.DESTROYED
            observers.forEach {
                if (it is LifecycleEventObserver) it.onStateChanged(owner!!, Event.ON_DESTROY)
            }
        }
    }
}
