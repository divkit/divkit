package divkit.dsl

import com.fasterxml.jackson.databind.json.JsonMapper
import divkit.dsl.core.ArrayElement
import divkit.dsl.core.bind
import divkit.dsl.core.expressionArrayElement
import divkit.dsl.core.reference
import divkit.dsl.core.valueArrayElement
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

class ArrayElementsTest {

    @Test
    fun `literal array elements`() {
        val card = divan {
            data(
                logId = "test",
                div = container(
                    background = listOf(
                        radialGradient(
                            colors = listOf(
                                valueArrayElement(color("#000000")),
                                valueArrayElement(color("#FFFFFF"))
                            )
                        )
                    )
                )
            )
        }

        val expectedJson = """
            {
              "card": {
                "log_id": "test",
                "states": [
                  {
                    "state_id": 0,
                    "div": {
                      "type": "container",
                      "background": [
                        {
                          "type": "radial_gradient",
                          "colors": [
                            "#000000",
                            "#FFFFFF"
                          ]
                        }
                      ]
                    }
                  }
                ]
              },
              "templates": {}
            }
        """.trimIndent()

        JSONAssert.assertEquals(expectedJson, card.toJson(), JSONCompareMode.STRICT)
    }

    @Test
    fun `expression array elements`() {
        val card = divan {
            data(
                logId = "test",
                div = container(
                    background = listOf(
                        radialGradient(
                            colors = listOf(
                                expressionArrayElement("@{start_color}"),
                                expressionArrayElement("@{end_color}"),
                            )
                        )
                    )
                )
            )
        }

        val expectedJson = """
            {
              "card": {
                "log_id": "test",
                "states": [
                  {
                    "state_id": 0,
                    "div": {
                      "type": "container",
                      "background": [
                        {
                          "type": "radial_gradient",
                          "colors": [
                            "@{start_color}",
                            "@{end_color}"
                          ]
                        }
                      ]
                    }
                  }
                ]
              },
              "templates": {}
            }
        """.trimIndent()

        JSONAssert.assertEquals(expectedJson, card.toJson(), JSONCompareMode.STRICT)
    }

    @Test
    fun `mixed array elements`() {
        val card = divan {
            data(
                logId = "test",
                div = container(
                    background = listOf(
                        radialGradient(
                            colors = listOf(
                                valueArrayElement(color("#000000")),
                                expressionArrayElement("@{end_color}"),
                            )
                        )
                    )
                )
            )
        }

        val expectedJson = """
            {
              "card": {
                "log_id": "test",
                "states": [
                  {
                    "state_id": 0,
                    "div": {
                      "type": "container",
                      "background": [
                        {
                          "type": "radial_gradient",
                          "colors": [
                            "#000000",
                            "@{end_color}"
                          ]
                        }
                      ]
                    }
                  }
                ]
              },
              "templates": {}
            }
        """.trimIndent()

        JSONAssert.assertEquals(expectedJson, card.toJson(), JSONCompareMode.STRICT)
    }

    @Test
    fun `template array elements`() {
        val colorsRef = reference<List<ArrayElement<Color>>>("colors")
        val card = divan {
            data(
                logId = "test",
                div = render(
                    template("testTemplate") {
                        container(
                            background = listOf(
                                radialGradient().defer(
                                    colors = colorsRef,
                                )
                            )
                        )
                    },
                    colorsRef bind listOf(
                        valueArrayElement(color("#000000")),
                        expressionArrayElement("@{end_color}"),
                    )
                )
            )
        }

        val expectedJson = """
            {
              "card": {
                "log_id": "test",
                "states": [
                  {
                    "div": {
                      "colors": [
                        "#000000",
                        "@{end_color}"
                      ],
                      "type": "testTemplate"
                    },
                    "state_id": 0
                  }
                ]
              },
              "templates": {
                "testTemplate": {
                  "background": [
                    {
                      "${'$'}colors": "colors",
                      "type": "radial_gradient"
                    }
                  ],
                  "type": "container"
                }
              }
            }
        """.trimIndent()

        JSONAssert.assertEquals(expectedJson, card.toJson(), JSONCompareMode.STRICT)
    }
}

private fun Divan.toJson() = JsonMapper.builder().build().writeValueAsString(this)
