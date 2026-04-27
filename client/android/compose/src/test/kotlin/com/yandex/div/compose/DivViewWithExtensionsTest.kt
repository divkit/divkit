package com.yandex.div.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivExtension
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewWithExtensionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val reporter = TestReporter()

    private val configuration = DivComposeConfiguration(
        extensionHandlers = mapOf("wrapper" to WrapperExtensionHandler()),
        reporter = reporter
    )

    @Test
    fun `error is reported if handler not found`() {
        reporter.failOnError = false

        setContent(
            text(
                extensions = listOf(DivExtension(id = "unknown")),
                text = constant("Hello!")
            )
        )

        assertEquals("No handler for extension: unknown", reporter.lastError)
    }

    @Test
    fun `multiple wrapper extensions are applied`() {
        reporter.failOnError = false

        setContent(
            text(
                extensions = listOf(
                    DivExtension(id = "unknown"),
                    DivExtension(
                        id = "wrapper",
                        params = JSONObject(mapOf("id" to "wrapper1"))
                    )
                ),
                text = constant("Hello!")
            )
        )

        rule.onNodeWithTag("wrapper1").assertIsDisplayed()
    }

    @Test
    fun `other extensions are applied if handler not found`() {
        setContent(
            text(
                extensions = listOf(
                    DivExtension(
                        id = "wrapper",
                        params = JSONObject(mapOf("id" to "wrapper1"))
                    ),
                    DivExtension(
                        id = "wrapper",
                        params = JSONObject(mapOf("id" to "wrapper2"))
                    )
                ),
                text = constant("Hello!")
            )
        )

        rule.onNodeWithTag("wrapper1").apply {
            assertIsDisplayed()
            onChild().apply {
                assert(hasTestTag("wrapper2"))
                onChild().assertTextEquals("Hello!")
            }
        }
    }

    private fun setContent(content: Div) {
        rule.setContent(
            configuration = configuration,
            data = data(content)
        )
    }
}

private class WrapperExtensionHandler : DivExtensionHandler {

    @Composable
    override fun Content(
        environment: DivExtensionEnvironment,
        content: @Composable (modifier: Modifier) -> Unit
    ) {
        val id = environment.extension.params?.getString("id") ?: ""
        Box(modifier = environment.modifier.testTag(id)) {
            content(Modifier)
        }
    }
}
