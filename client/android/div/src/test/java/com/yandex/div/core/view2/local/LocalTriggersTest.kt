package com.yandex.div.core.view2.local

import android.app.Activity
import android.net.Uri
import android.widget.TextView
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import java.lang.AssertionError
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

private const val ACTIVE_TRIGGER_TEXT = "Trigger: activated"
private const val INACTIVE_TRIGGER_TEXT = "Trigger: unset"
private const val ACTIVE_TRIGGER_VARIABLE_VALUE = 110
private const val INACTIVE_TRIGGER_VARIABLE_VALUE = 10

@RunWith(RobolectricTestRunner::class)
class LocalTriggersTest {
    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val div2Context = Div2Context(
        baseContext = activity,
        lifecycleOwner = null,
        configuration = DivConfiguration.Builder(mock()).build()
    )
    private val div2View = Div2View(div2Context)

    private val container get() = div2View.getChildAt(0) as DivLinearLayout

    private val firstStateText get() = container.getChildAt(0) as TextView
    private val secondStateText get() = container.getChildAt(1) as TextView

    private fun setDivView(json: String, tag: String = "tag") {
        val parsingEnvironment = DivParsingEnvironment({ e -> throw AssertionError(e) })
        val divData = DivData(parsingEnvironment, JSONObject(json))
        div2View.setData(divData, DivDataTag(tag))
    }

    @Test
    fun `when variable changed, triggers only from active state are triggered`() {
        setDivView(testJsonWithTwoStates)

        setVariableValue(ACTIVE_TRIGGER_VARIABLE_VALUE)

        Assert.assertEquals(ACTIVE_TRIGGER_TEXT, firstStateText.text)
        Assert.assertEquals(INACTIVE_TRIGGER_TEXT, secondStateText.text)
    }

    @Test
    fun `when state changed, triggers from previous state is unbound`() {
        setDivView(testJsonWithTwoStates)

        setState(2)
        setVariableValue(ACTIVE_TRIGGER_VARIABLE_VALUE)

        Assert.assertEquals(INACTIVE_TRIGGER_TEXT, firstStateText.text)
        Assert.assertEquals(ACTIVE_TRIGGER_TEXT, secondStateText.text)
    }

    @Test
    fun `when variable_triggers created on state change, they will be invoked`() {
        setDivView(testJsonWithTwoStates)

        setVariableValue(ACTIVE_TRIGGER_VARIABLE_VALUE)
        setState(2)

        Assert.assertEquals(ACTIVE_TRIGGER_TEXT, firstStateText.text)
        Assert.assertEquals(ACTIVE_TRIGGER_TEXT, secondStateText.text)
    }

    @Test
    fun `when binding unbound view, triggers from inactive state won't be attached`() {
        setDivView(testJsonWithTwoStates)

        //ensure all runtimes are created
        setState(2)
        setState(1)

        setDivView(stubJson, "some other tag")
        setDivView(testJsonWithTwoStates)

        setVariableValue(ACTIVE_TRIGGER_VARIABLE_VALUE)

        // Created with state 1, only triggers from state 1 should be triggered
        Assert.assertEquals(ACTIVE_TRIGGER_TEXT, firstStateText.text)
        Assert.assertEquals(INACTIVE_TRIGGER_TEXT, secondStateText.text)
    }

    @Test
    fun `when binding unbound view, triggers from inactive state won't be attached 2`() {
        setDivView(testJsonWithTwoStates)

        //ensure all runtimes are created
        setState(2)

        setDivView(stubJson, "some other tag")
        setDivView(testJsonWithTwoStates)

        setVariableValue(ACTIVE_TRIGGER_VARIABLE_VALUE)

        // Created with state 2, only triggers from state 2 should be triggered
        Assert.assertEquals(INACTIVE_TRIGGER_TEXT, firstStateText.text)
        Assert.assertEquals(ACTIVE_TRIGGER_TEXT, secondStateText.text)
    }

    private fun setState(stateNum: Int) {
        when (stateNum) {
            1 -> handleAction(Uri.parse("div-action://set_state?state_id=0/sample/first"))
            2 -> handleAction(Uri.parse("div-action://set_state?state_id=0/sample/second"))
            else -> Unit
        }
    }

    private fun setVariableValue(value: Int) {
        handleAction(Uri.parse("div-action://set_variable?name=counter&value=$value"))
    }

    private fun handleAction(uri: Uri) {
        div2View.handleAction(DivAction(
            logId = Expression.constant("id"),
            url = Expression.constant(uri))
        )
    }
}
private val testJsonWithTwoStates = """
{
      "log_id": "sample_card",
      "variables": [
        {
          "name": "counter",
          "type": "integer",
          "value": $INACTIVE_TRIGGER_VARIABLE_VALUE
        },
        {
          "name": "trigger_state_1",
          "type": "string",
          "value": "unset"
        },
        {
          "name": "trigger_state_2",
          "type": "string",
          "value": "unset"
        }
      ],
      "states": [
        {
          "state_id": 0,
          "div": {
            "type": "container",
            "items": [
              {
                "type": "text",
                "text": "Trigger: @{trigger_state_1}"
              },
              {
                "type": "text",
                "text": "Trigger: @{trigger_state_2}"
              },
              {
                "type": "state",
                "id": "sample",
                "states": [
                  {
                    "state_id": "first",
                    "div": {
                      "type": "text",
                      "variable_triggers": [
                        {
                          "condition": "@{counter > 100}",
                          "actions": [
                            {
                              "log_id": "trigger_invoke_1",
                              "url": "div-action://set_variable?name=trigger_state_1&value=activated"
                            }
                          ]
                        }
                      ],
                      "text": "Local trigger on state 1"
                    }
                  },
                  {
                    "state_id": "second",
                    "div": {
                      "type": "text",
                      "variable_triggers": [
                        {
                          "condition": "@{counter > 100}",
                          "actions": [
                            {
                              "log_id": "trigger_invoke_2",
                              "url": "div-action://set_variable?name=trigger_state_2&value=activated"
                            }
                          ]
                        }
                      ],
                      "text": "Local trigger on state 2"
                    }
                  }
                ]
              }
            ]
          }
        }
      ]
    }
""".trimIndent()

private val stubJson = """
{
      "log_id": "sample_card",
      
      "states": [
        {
          "state_id": 0,
          "div": {
            "type": "text",
            "text": "empty"
          }
        }
      ]
    }
""".trimIndent()
