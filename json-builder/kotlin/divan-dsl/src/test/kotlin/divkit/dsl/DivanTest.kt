package divkit.dsl

import divkit.dsl.core.expression
import kotlin.test.Test

class DivanTest {

    @Test
    fun `simple card`() {
        val card = divan {
            data(
                logId = "test",
                div = text(
                    text = "Hello!"
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

        assertEquals(expectedJson, card)
    }

    @Test
    fun `card with expression`() {
        val card = divan {
            data(
                logId = "test",
                div = text().evaluate(
                    text = expression("@{hello_text}")
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

        assertEquals(expectedJson, card)
    }

    @Test
    fun `card with boolean property`() {
        val card = divan {
            data(
                logId = "test",
                div = text(
                    text = "Hello!",
                    autoEllipsize = true
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

        assertEquals(expectedJson, card)
    }

    @Test
    fun `json array variable`() {
        val card = divan {
            val array = arrayVariable(
                name = "array_variable",
                value = listOf(
                    "outer_string",
                    123,
                    true,
                    arrayVariable(
                        value = listOf(
                            "inner_string",
                            456,
                            false
                        )
                    )
                )
            )
            data(
                logId = "test",
                div = text(),
                variables = listOf(array)
            )
        }

        assertEquals(readResource("/json/array_variable.json"), card)
    }
}
