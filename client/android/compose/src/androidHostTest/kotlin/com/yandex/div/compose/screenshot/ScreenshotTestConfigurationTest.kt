package com.yandex.div.compose.screenshot

import org.json.JSONObject
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ScreenshotTestConfigurationTest {

    @Test
    fun `failOnParsingError default value is true`() {
        val configuration = ScreenshotTestConfiguration("test", JSONObject())

        assertTrue(configuration.failOnParsingError)
    }

    @Test
    fun `with fail_on_parsing_error`() {
        val json = JSONObject("""{"configuration":{"fail_on_parsing_error":false}}""")
        val configuration = ScreenshotTestConfiguration("test", json)

        assertFalse(configuration.failOnParsingError)
    }

    @Test
    fun `parseDivData() does not fail when fail_on_parsing_error is false`() {
        val json = invalidDivDataJson(failOnParsingError = false)
        val configuration = ScreenshotTestConfiguration("test", json)

        assertNotNull(configuration.parseDivData())
    }

    @Test
    fun `parseDivData() fails when fail_on_parsing_error is true`() {
        val json = invalidDivDataJson(failOnParsingError = true)
        val configuration = ScreenshotTestConfiguration("test", json)

        assertFailsWith<AssertionError> {
            configuration.parseDivData()
        }
    }

    private fun invalidDivDataJson(failOnParsingError: Boolean): JSONObject {
        return JSONObject(
            """
            {
              "configuration": { "fail_on_parsing_error": $failOnParsingError },
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
            """.trimIndent()
        )
    }
}
