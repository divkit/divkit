package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.text
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(AndroidJUnit4::class)
class LocalDivContextTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `DivView reports a missing DivContext provider`() {
        val error = assertFailsWith<IllegalStateException> {
            rule.setContent {
                DivView(data(text(text = "Card")))
            }
        }

        assertEquals(
            "DivContext not provided. Supply it through LocalContext or use DivViewHost.",
            error.message,
        )
    }

    @Test
    fun `LocalDivContext takes precedence over LocalContext`() {
        rule.setContent {
            CompositionLocalProvider(
                LocalContext provides contextWithValue("LocalContext"),
                LocalDivContext provides contextWithValue("LocalDivContext"),
            ) {
                DivView(data(text(text = expression("@{value}"))))
            }
        }

        rule.onNodeWithText("LocalDivContext").assertIsDisplayed()
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
