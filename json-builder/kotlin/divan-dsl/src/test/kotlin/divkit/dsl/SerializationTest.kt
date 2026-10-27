package divkit.dsl

import com.fasterxml.jackson.databind.json.JsonMapper
import divkit.dsl.core.bind
import divkit.dsl.core.reference
import kotlin.test.Test
import kotlin.test.assertContains

class SerializationTest {

    @Test
    fun `type first then schema order`() {
        val card = divan {
            data(
                logId = "test",
                div = text(
                    text = "Hello",
                    alpha = 0.5,
                    autoEllipsize = true,
                )
            )
        }

        assertContains(
            JsonMapper.builder().build().writeValueAsString(card),
            """{"type":"text","text":"Hello","alpha":0.5,"auto_ellipsize":1}"""
        )
    }

    @Test
    fun `component type first then insertion order`() {
        val titleRef = reference<String>("title")
        val template = template("item") {
            text() + textRefs(text = titleRef)
        }
        val card = divan {
            data(
                logId = "test",
                div = render(template, titleRef bind "Hello") + textProps(maxLines = 2)
            )
        }

        assertContains(
            JsonMapper.builder().build().writeValueAsString(card),
            """{"type":"item","title":"Hello","max_lines":2}"""
        )
    }

    @Test
    fun `entity without properties`() {
        val card = divan {
            data(
                logId = "test",
                div = container(
                    border = border(
                        stroke = stroke(
                            color = color("#000000"),
                            style = strokeStyleSolid(),
                        )
                    )
                )
            )
        }

        val expectedJson = """
            {
              "templates": {},
              "card": {
                "log_id": "test",
                "states": [
                  {
                    "state_id": 0,
                    "div": {
                      "type": "container",
                      "border": {
                        "stroke": {
                          "color": "#000000",
                          "style": {
                            "type": "solid"
                          }
                        }
                      }
                    }
                  }
                ]
              }
            }
        """.trimIndent()

        assertEquals(expectedJson, card)
    }

    @Test
    fun `map property`() {
        val card = divan {
            data(
                logId = "test",
                div = text(
                    text = "Hello",
                    fontVariationSettings = mapOf(
                        "wght" to 700,
                        "color" to color("#FF0000"),
                        "style" to strokeStyleSolid(),
                        "flag" to true,
                        "list" to listOf(1, "a"),
                    )
                )
            )
        }

        val expectedJson = """
            {
              "templates": {},
              "card": {
                "log_id": "test",
                "states": [
                  {
                    "state_id": 0,
                    "div": {
                      "type": "text",
                      "text": "Hello",
                      "font_variation_settings": {
                        "wght": 700,
                        "color": "#FF0000",
                        "style": {
                          "type": "solid"
                        },
                        "flag": true,
                        "list": [1, "a"]
                      }
                    }
                  }
                ]
              }
            }
        """.trimIndent()

        assertEquals(expectedJson, card)
    }

    @Test
    fun `enum color and url`() {
        val card = divan {
            data(
                logId = "test",
                div = container(
                    items = listOf(
                        text(
                            text = "Hello",
                            fontWeight = bold,
                            textColor = color("#112233"),
                        ),
                        image(
                            imageUrl = url("https://ya.ru"),
                        ),
                    )
                )
            )
        }

        val expectedJson = """
            {
              "templates": {},
              "card": {
                "log_id": "test",
                "states": [
                  {
                    "state_id": 0,
                    "div": {
                      "type": "container",
                      "items": [
                        {
                          "type": "text",
                          "text": "Hello",
                          "font_weight": "bold",
                          "text_color": "#112233"
                        },
                        {
                          "type": "image",
                          "image_url": "https://ya.ru"
                        }
                      ]
                    }
                  }
                ]
              }
            }
        """.trimIndent()

        assertEquals(expectedJson, card)
    }

    @Test
    fun `int reference`() {
        val linesRef = reference<Int>("lines")
        val template = template("item") {
            text(text = "Hello") + textRefs(maxLines = linesRef)
        }
        val card = divan {
            data(
                logId = "test",
                div = render(template, linesRef bind 3)
            )
        }

        val expectedJson = $$"""
            {
              "templates": {
                "item": {
                  "type": "text",
                  "text": "Hello",
                  "$max_lines": "lines"
                }
              },
              "card": {
                "log_id": "test",
                "states": [
                  {
                    "state_id": 0,
                    "div": {
                      "type": "item",
                      "lines": 3
                    }
                  }
                ]
              }
            }
        """.trimIndent()

        assertEquals(expectedJson, card)
    }
}
