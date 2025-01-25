package com.yandex.div.core.view2.local

import android.app.Activity
import android.net.Uri
import android.widget.EditText
import android.widget.TextView
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
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

private const val LOCAL_VARIABLE_NAME = "text_variable"
private const val LOCAL_INPUT_INITIAL_VALUE_SECOND_STATE = "initial value 2"
private const val CARD_INPUT_INITIAL_VALUE = "card initial value"
private const val LOCAL_INPUT_MODIFIED_VALUE = "modified value"

@RunWith(RobolectricTestRunner::class)
class LocalVariablesTest {
    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val div2Context = Div2Context(
        baseContext = activity,
        lifecycleOwner = null,
        configuration = DivConfiguration.Builder(mock()).build()
    )
    private val div2View = Div2View(div2Context)

    private val state get() = div2View.getChildAt(0) as DivStateLayout
    private val container get() = state.getChildAt(0) as DivLinearLayout
    private val inputWithLocalVariable get() = container.getChildAt(0) as EditText
    private val inputWithCardVariable get() = container.getChildAt(1) as EditText

    private fun setDivView(json: String) {
        val parsingEnvironment = DivParsingEnvironment({ e -> throw AssertionError(e) })
        val divData = DivData(parsingEnvironment, JSONObject(json))
        div2View.setData(divData, DivDataTag("tag"))
    }

    @Test
    fun `elements with defined local variable can use card variables`() {
        setDivView(testJsonWithTwoStates)

        Assert.assertEquals(
            CARD_INPUT_INITIAL_VALUE, inputWithCardVariable.text.toString()
        )
    }

    @Test
    fun `local variable value can be updated by path`() {
        setDivView(testJsonWithTwoStates)
        setVariable(LOCAL_VARIABLE_NAME, LOCAL_INPUT_MODIFIED_VALUE, "0/label/state_1")

        Assert.assertEquals(LOCAL_INPUT_MODIFIED_VALUE, inputWithLocalVariable.text.toString())
    }

    @Test
    fun `changing local variable will not effect other states`() {
        setDivView(testJsonWithTwoStates)
        setVariable(LOCAL_VARIABLE_NAME, LOCAL_INPUT_MODIFIED_VALUE, "0/label/state_1")

        setState(2)
        Assert.assertEquals(
            LOCAL_INPUT_INITIAL_VALUE_SECOND_STATE,
            inputWithLocalVariable.text.toString()
        )
    }

    @Test
    fun `local variable restore it's value on changing state to another and returning to original state`() {
        setDivView(testJsonWithTwoStates)
        setVariable(LOCAL_VARIABLE_NAME, LOCAL_INPUT_MODIFIED_VALUE, "0/label/state_1")

        setState(2)
        setState(1)

        Assert.assertEquals(LOCAL_INPUT_MODIFIED_VALUE, inputWithLocalVariable.text.toString())
    }

    @Test
    fun `child can reach local variables of his parent`() {
        setDivView(testJsonWithContainerWithVariablesAndChild)

        val text = container.getChildAt(0) as TextView
        Assert.assertEquals("container_variable, text_variable", text.text)
    }

    @Test
    fun `changing variable in container when child has variable with the same name will not effect child`() {
        setDivView(testJsonWithContainerAndChildBothWithDefinedVariables)

        val textWithLocalVariable = container.getChildAt(0) as TextView
        val textWithParentVariable = container.getChildAt(1) as TextView
        Assert.assertEquals("text", textWithLocalVariable.text)
        Assert.assertEquals("text", textWithParentVariable.text)

        setVariable("text", "changed text", "0/label/state_4")
        Assert.assertEquals("text", textWithLocalVariable.text)
        Assert.assertEquals("changed text", textWithParentVariable.text)
    }

    private fun setState(stateNum: Int) {
        handleAction(Uri.parse("div-action://set_state?state_id=0/label/state_$stateNum"))
    }

    private fun handleAction(uri: Uri) {
        div2View.handleAction(DivAction(
            logId = Expression.constant("id"),
            url = Expression.constant(uri))
        )
    }

    private fun setVariable(name: String, value: String, path: String) {
        val variableController = div2View.expressionsRuntime
            ?.runtimeStore?.getOrCreateRuntime(path, null, null, null, null)?.variableController
        val variable = variableController?.getMutableVariable(name) ?: return
        variable.set(value)
    }
}
private val testJsonWithTwoStates = """
{
  "log_id": "local_variables",
  "variables": [
    {
      "name": "card_text_variable",
      "type": "string",
      "value": "card initial value"
    }
  ],
  "states": [
    {
      "state_id": 0,
      "div": {
        "type": "state",
        "id": "label",
        "states": [
          {
            "state_id": "state_1",
            "div": {
              "type": "container",
              "variables": [
                {
                  "name": "text_variable",
                  "type": "string",
                  "value": "initial value 1"
                }
              ],
              "orientation": "vertical",
              "items": [
                {
                  "type": "input",
                  "text_variable": "text_variable"
                },
                {
                  "type": "input",
                  "text_variable": "card_text_variable"
                }
              ]
            }
          },
          {
            "state_id": "state_2",
            "div": {
              "type": "container",
              "variables": [
                {
                  "name": "text_variable",
                  "type": "string",
                  "value": "initial value 2"
                }
              ],
              "orientation": "vertical",
              "items": [
                {
                  "type": "input",
                  "text_variable": "text_variable"
                },
                {
                  "type": "input",
                  "text_variable": "card_text_variable"
                }
              ]
            }
          }
        ]
      }
    }
  ]
}
""".trimIndent()

val testJsonWithContainerWithVariablesAndChild = """
  {
    "log_id": "local_variables",
    "variables": [
      {
        "name": "card_text_variable",
        "type": "string",
        "value": "card initial value"
      }
    ],
    "states": [
      {
        "state_id": 0,
        "div": {
          "type": "state",
          "id": "label",
          "states": [
            {
              "state_id": "state_3",
              "div": {
                "type": "container",
                "variables": [
                  {
                    "name": "container",
                    "type": "string",
                    "value": "container_variable"
                  }
                ],
                "orientation": "vertical",
                "items": [
                  {
                    "variables": [
                      {
                        "name": "text",
                        "type": "string",
                        "value": "text_variable"
                      }
                    ],
                    "type": "text",
                    "text": "@{container + ', ' + text}"
                  }
                ]
              }
            }
          ]
        }
      }
    ]
  }
""".trimIndent()

val testJsonWithContainerAndChildBothWithDefinedVariables = """
    {
      "log_id": "local_variables",
      "variables": [
        {
          "name": "card_text_variable",
          "type": "string",
          "value": "card initial value"
        }
      ],
      "states": [
        {
          "state_id": 0,
          "div": {
            "type": "state",
            "id": "label",
            "states": [
              {
                "state_id": "state_4",
                "div": {
                  "type": "container",
                  "variables": [
                    {
                      "name": "text",
                      "type": "string",
                      "value": "text"
                    }
                  ],
                  "orientation": "vertical",
                  "items": [
                    {
                      "variables": [
                        {
                          "name": "text",
                          "type": "string",
                           "value": "text"
                        }
                      ],
                      "type": "text",
                      "text": "@{text}"
                    },
                    {
                      "type": "text",
                      "text": "@{text}"
                    }
                  ]
                }
              }
            ]
          }
        }
      ]
    }
""".trimIndent()
