package com.yandex.div.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div2.Div
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewWithCustomTest {

    @get:Rule
    val rule = createComposeRule()

    private val reporter = TestReporter()

    private val configuration = DivComposeConfiguration(
        customViewFactories = mapOf("custom_text" to CustomTextViewFactory()),
        reporter = reporter
    )

    @Test
    fun `error is reported if factory not found`() {
        reporter.failOnError = false

        setContent(
            custom(type = "unknown")
        )

        assertEquals("No custom view factory for custom_type: unknown", reporter.lastError)
    }

    @Test
    fun `custom is composed`() {
        setContent(
            custom(
                type = "custom_text",
                customProps = JSONObject(mapOf("text" to "Hello!"))
            )
        )

        rule.onNodeWithText("Hello!").assertIsDisplayed()
    }

    private fun setContent(content: Div) {
        rule.setContent(
            configuration = configuration,
            data = data(content)
        )
    }
}

private class CustomTextViewFactory : DivCustomViewFactory {
    @Composable
    override fun Content(environment: DivCustomEnvironment) {
        Text(text = environment.data.customProps?.getString("text") ?: "")
    }
}
