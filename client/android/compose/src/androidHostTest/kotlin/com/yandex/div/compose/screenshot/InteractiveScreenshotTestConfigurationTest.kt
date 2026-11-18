package com.yandex.div.compose.screenshot

import androidx.compose.ui.unit.LayoutDirection
import org.json.JSONObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class InteractiveScreenshotTestConfigurationTest {

    @Test
    fun `layoutDirection is parsed`() {
        val configuration = InteractiveScreenshotTestConfiguration(
            name = "test",
            json = JSONObject(
                """{"configuration":{"layout_direction":"rtl"},"div_data":{}}"""
            ),
        )

        assertEquals(LayoutDirection.Rtl, configuration.baseConfiguration.layoutDirection)
    }

    @Test
    fun `parseDivData() does not fail if fail_on_parsing_error is false`() {
        val configuration = InteractiveScreenshotTestConfiguration(
            name = "test",
            json = invalidDivDataJson().put(
                "configuration",
                JSONObject("""{ "fail_on_parsing_error": false }""")
            ),
        )

        assertNotNull(configuration.baseConfiguration.parseDivData())
    }

    @Test
    fun `parseDivData() fails by default`() {
        val configuration = InteractiveScreenshotTestConfiguration(
            name = "test",
            json = invalidDivDataJson(),
        )

        assertFailsWith<AssertionError> {
            configuration.baseConfiguration.parseDivData()
        }
    }

    private fun invalidDivDataJson() = JSONObject(
        """
        {
          "div_data": {
            "card": {
              "log_id": "test",
              "states": [
                {
                  "state_id": 0,
                  "div": {
                    "type": "container",
                    "items": [
                      { "type": "invalid" }
                    ]
                  }
                }
              ]
            }
          }
        }
        """.trimIndent()
    )
}
