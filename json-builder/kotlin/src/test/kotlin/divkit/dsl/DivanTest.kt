package divkit.dsl

import com.fasterxml.jackson.databind.json.JsonMapper
import divkit.dsl.core.bind
import divkit.dsl.core.expression
import divkit.dsl.core.reference
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import org.skyscreamer.jsonassert.JSONCompareMode

class DivanTest {

    @Test
    fun `simple card`() {
        val card = divan {
            data(
                logId = "test",
                states = singleRoot(
                    div = text(
                        text = "Hello!"
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
                      "text": "Hello!"
                    }
                  }
                ]
              }
            }
        """.trimIndent()

        assertEquals(expectedJson, card.toJson(), JSONCompareMode.STRICT)
    }

    @Test
    fun `card with expression`() {
        val card = divan {
            data(
                logId = "test",
                states = singleRoot(
                    div = text().evaluate(
                        text = expression("@{hello_text}")
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
                      "text": "@{hello_text}"
                    }
                  }
                ]
              }
            }
        """.trimIndent()

        assertEquals(expectedJson, card.toJson(), JSONCompareMode.STRICT)
    }

    @Test
    fun `card with boolean property`() {
        val card = divan {
            data(
                logId = "test",
                states = singleRoot(
                    div = text(
                        text = "Hello!",
                        autoEllipsize = true
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
                      "text": "Hello!",
                      "auto_ellipsize": 1
                    }
                  }
                ]
              }
            }
        """.trimIndent()

        assertEquals(expectedJson, card.toJson(), JSONCompareMode.STRICT)
    }

    @Test
    fun `card with template`() {
        val titleRef = reference<String>("title")

        val titleTemplate = template("title_text") {
            text(
                width = fixedSize(120, unit = dp)
            ) + textRefs(text = titleRef)
        }

        val card = divan {
            data(
                logId = "layout",
                states = singleRoot(
                    div = container(
                        width = fixedSize(320),
                        height = fixedSize(320),
                        orientation = vertical,
                        items = listOf(
                            render(
                                titleTemplate,
                                titleRef bind "Good news, everyone!"
                            ) + textProps(
                                width = matchParentSize()
                            ),
                            text(
                                width = wrapContentSize(),
                                height = wrapContentSize(),
                                text = "Hello, Everyone!",
                                actions = listOf(
                                    action(
                                        logId = "tap_action",
                                        url = url("https://yandex.ru")
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }

        assertEquals(
            readResource("/json/card_with_templates.json"),
            card.toJson(),
            JSONCompareMode.STRICT
        )
    }
}

private fun Divan.toJson() = JsonMapper.builder().build().writeValueAsString(this)
