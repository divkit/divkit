package com.yandex.div.compose

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.text
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDivContextTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `DivView keeps the platform context when DivContext is provided`() {
        val platformContext = ContextThemeWrapper(getApplicationContext(), android.R.style.Theme_Material)
        var renderedContext: Context? = null
        val factory = object : DivCustomViewFactory {
            @Composable
            override fun Content(modifier: Modifier, environment: DivCustomEnvironment) {
                renderedContext = LocalContext.current
                Text("Card", modifier)
            }
        }
        val divContext = DivContext(platformContext, divConfiguration {
            reporter = TestReporter()
            customViewFactories = mapOf("context" to factory)
        })

        rule.setContent {
            CompositionLocalProvider(
                LocalContext provides platformContext,
                LocalDivContext provides divContext,
            ) {
                DivView(data(custom(type = "context")))
            }
        }

        rule.onNodeWithText("Card").assertIsDisplayed()
        assertSame(platformContext, renderedContext)
    }

    @Test
    fun `DivView reports a missing DivContext provider`() {
        val error = assertFailsWith<IllegalStateException> {
            rule.setContent { DivView(data(text(text = "Card"))) }
        }

        assertEquals(
            "DivContext not provided. Supply it through LocalContext or use DivViewHost.",
            error.message,
        )
    }

    @Test
    fun `nested DivViews use the variables of their own context`() {
        val outer = contextWithValue("Outer")
        val inner = contextWithValue("Inner")
        val outerCard = data(text(text = expression("@{value}")), logId = "outer")
        val innerCard = data(text(text = expression("@{value}")), logId = "inner")

        rule.setContent {
            CompositionLocalProvider(LocalDivContext provides outer) {
                Column {
                    DivView(outerCard)
                    CompositionLocalProvider(LocalDivContext provides inner) {
                        DivView(innerCard)
                    }
                }
            }
        }

        rule.onNodeWithText("Outer").assertIsDisplayed()
        rule.onNodeWithText("Inner").assertIsDisplayed()
    }

    @Test
    fun `explicit DivContext takes precedence over the legacy Android context`() {
        val legacyContext = contextWithValue("Legacy")
        val explicitContext = contextWithValue("Explicit")
        val card = data(text(text = expression("@{value}")))

        rule.setContent {
            CompositionLocalProvider(
                LocalContext provides legacyContext,
                LocalDivContext provides explicitContext,
            ) {
                DivView(card)
            }
        }

        rule.onNodeWithText("Explicit").assertIsDisplayed()
    }

    private fun contextWithValue(value: String) = DivContext(
        getApplicationContext(),
        divConfiguration {
            reporter = TestReporter()
            variableController = DivVariableController().apply {
                declare(Variable.StringVariable("value", value))
            }
        },
    )
}
