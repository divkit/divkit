package com.yandex.div.core.view2.local

import android.app.Activity
import android.net.Uri
import android.widget.EditText
import android.widget.TextView
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.Assert
import com.yandex.div.internal.util.textString
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
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
    private val divBase = mock<DivBase>()
    private val div = mock<Div> {
        on { value() } doReturn divBase
    }
    private val div2View = Div2View(div2Context)

    private val state get() = div2View.getChildAt(0) as DivStateLayout
    private val container get() = state.getChildAt(0) as DivLinearLayout
    private val inputWithLocalVariable get() = container.getChildAt(0) as EditText
    private val inputWithCardVariable get() = container.getChildAt(1) as EditText
    private val path = DivStatePath.parse("0/label/state_1")

    private fun setDivView(json: String) {
        val parsingEnvironment = DivParsingEnvironment({ e -> throw AssertionError(e) })
        val divData = DivData(parsingEnvironment, JSONObject(json))
        div2View.setData(divData, DivDataTag("tag"))
    }

    @Test
    fun `elements with defined local variable can use card variables`() {
        setDivView(testJsonWithTwoStates)

        assertTextShown(CARD_INPUT_INITIAL_VALUE, inputWithCardVariable)
    }

    @Test
    fun `local variable value can be updated by path`() {
        setDivView(testJsonWithTwoStates)
        setVariable(LOCAL_VARIABLE_NAME, LOCAL_INPUT_MODIFIED_VALUE, path)

        assertTextShown(LOCAL_INPUT_MODIFIED_VALUE, inputWithLocalVariable)
    }

    @Test
    fun `changing local variable will not effect other states`() {
        setDivView(testJsonWithTwoStates)
        setVariable(LOCAL_VARIABLE_NAME, LOCAL_INPUT_MODIFIED_VALUE, path)

        setState(2)
        assertTextShown(LOCAL_INPUT_INITIAL_VALUE_SECOND_STATE, inputWithLocalVariable)
    }

    @Test
    fun `local variable restore it's value on changing state to another and returning to original state`() {
        setDivView(testJsonWithTwoStates)
        setVariable(LOCAL_VARIABLE_NAME, LOCAL_INPUT_MODIFIED_VALUE, path)

        setState(2)
        setState(1)

        assertTextShown(LOCAL_INPUT_MODIFIED_VALUE, inputWithLocalVariable)
    }

    @Test
    fun `child can reach local variables of his parent`() {
        setDivView(testJsonWithContainerWithVariablesAndChild)

        val text = container.getChildAt(0) as TextView
        assertTextShown("container_variable, text_variable", text)
    }

    @Test
    fun `changing variable in container when child has variable with the same name will not effect child`() {
        setDivView(testJsonWithContainerAndChildBothWithDefinedVariables)

        val textWithLocalVariable = container.getChildAt(0) as TextView
        val textWithParentVariable = container.getChildAt(1) as TextView
        assertTextShown("text", textWithLocalVariable)
        assertTextShown("text", textWithParentVariable)

        setVariable("text", "changed text", DivStatePath.parse("0/label/state_4"))
        assertTextShown("text", textWithLocalVariable)
        assertTextShown("changed text", textWithParentVariable)
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

    private fun setVariable(name: String, value: String, path: DivStatePath) {
        val variable = div2View.runtimeStore.getOrCreateRuntime(path, div, div2View.expressionResolver, div2View)
            .expressionResolver.variableController.getMutableVariable(name) ?: return
        variable.set(value)
    }

    private fun assertTextShown(expected: String, view: TextView) {
        Assert.assertEquals(expected, view.textString)
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
