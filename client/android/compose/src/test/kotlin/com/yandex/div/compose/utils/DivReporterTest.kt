package com.yandex.div.compose.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.mockLocalComponent
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class DivReporterTest {

    private val reporter = TestReporter().apply {
        failOnError = false
    }

    private val localComponent = mockLocalComponent(
        reporter = reporter
    )

    @Test
    fun `reportError() reports error`() = runComposeUiTest {
        setContentWitLocalComponent {
            reportError("Error!")
        }

        assertEquals("Error!", reporter.lastError)
    }

    @Test
    fun `multiple reportError() calls report multiple errors`() = runComposeUiTest {
        setContentWitLocalComponent {
            reportError("Error!")
            reportError("Error!")
        }

        assertEquals(listOf("Error!", "Error!"), reporter.errors)
    }

    @Test
    fun `reportWarning() reports warning`() = runComposeUiTest {
        setContentWitLocalComponent {
            reportWarning("Warning!")
        }

        assertEquals("Warning!", reporter.lastWarning)
    }

    @Test
    fun `multiple reportWarning() calls report multiple warnings`() = runComposeUiTest {
        setContentWitLocalComponent {
            reportWarning("Warning!")
            reportWarning("Warning!")
        }

        assertEquals(listOf("Warning!", "Warning!"), reporter.warnings)
    }

    @Test
    fun `reportError() does not report the same error after recomposition`() = runComposeUiTest {
        var text by mutableStateOf("Initial text")

        setContentWitLocalComponent {
            reportError("Error!")
            Text(text = text)
        }

        assertEquals(listOf("Error!"), reporter.popErrors())

        text = "New text"
        waitForIdle()

        assertNull(reporter.lastError)
    }

    @Test
    fun `reportError() reports error after recomposition if message was changed`() = runComposeUiTest {
        var error by mutableStateOf("Error!")

        setContentWitLocalComponent {
            reportError(error)
        }

        assertEquals(listOf("Error!"), reporter.popErrors())

        error = "New error!"
        waitForIdle()

        assertEquals("New error!", reporter.lastError)
    }

    private fun ComposeUiTest.setContentWitLocalComponent(content: @Composable () -> Unit) {
        setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                content()
            }
        }
    }
}
