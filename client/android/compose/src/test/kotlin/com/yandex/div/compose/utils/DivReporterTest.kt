package com.yandex.div.compose.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.mockLocalComponent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivReporterTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val reporter = TestReporter().apply {
        failOnError = false
    }

    private val localComponent = mockLocalComponent(
        reporter = reporter
    )

    @Test
    fun `reportError() reports error`() {
        setContent {
            reportError("Error!")
        }

        assertEquals("Error!", reporter.lastError)
    }

    @Test
    fun `multiple reportError() calls report multiple errors`() {
        setContent {
            reportError("Error!")
            reportError("Error!")
        }

        assertEquals(listOf("Error!", "Error!"), reporter.errors)
    }

    @Test
    fun `reportWarning() reports warning`() {
        setContent {
            reportWarning("Warning!")
        }

        assertEquals("Warning!", reporter.lastWarning)
    }

    @Test
    fun `multiple reportWarning() calls report multiple warnings`() {
        setContent {
            reportWarning("Warning!")
            reportWarning("Warning!")
        }

        assertEquals(listOf("Warning!", "Warning!"), reporter.warnings)
    }

    @Test
    fun `reportError() does not report the same error after recomposition`() {
        var text by mutableStateOf("Initial text")

        setContent {
            reportError("Error!")
            Text(text = text)
        }

        assertEquals(listOf("Error!"), reporter.popErrors())

        text = "New text"
        composeRule.waitForIdle()

        assertNull(reporter.lastError)
    }

    @Test
    fun `reportError() reports error after recomposition if message was changed`() {
        var error by mutableStateOf("Error!")

        setContent {
            reportError(error)
        }

        assertEquals(listOf("Error!"), reporter.popErrors())

        error = "New error!"
        composeRule.waitForIdle()

        assertEquals("New error!", reporter.lastError)
    }

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                content()
            }
        }
    }
}
