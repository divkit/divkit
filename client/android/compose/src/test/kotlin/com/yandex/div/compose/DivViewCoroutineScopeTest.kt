package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewCoroutineScopeTest {

    @get:Rule
    val rule = createComposeRule()

    private val card = data(text(text = "Card"))
    private val isVisible = mutableStateOf(true)

    private val divContext = DivContext(
        baseContext = getApplicationContext(),
        configuration = divConfiguration {
            reporter = TestReporter()
        },
    )

    private val coroutineScopeProvider = divContext
        .getViewContext(card)
        .component
        .coroutineScopeProvider

    @Test
    fun `scope is unavailable before entering composition`() {
        assertNull(coroutineScopeProvider.get())
    }

    @Test
    fun `scope is available after entering composition`() {
        setContent()

        assertNotNull(coroutineScopeProvider.get())
    }

    @Test
    fun `scope is unavailable after leaving composition`() {
        setContent()
        hideCard()

        assertNull(coroutineScopeProvider.get())
    }

    @Test
    fun `leaving composition cancels in-flight work and unregisters scope`() {
        setContent()

        val scope = assertNotNull(coroutineScopeProvider.get())
        val job = scope.launch { awaitCancellation() }

        hideCard()

        assertTrue(job.isCancelled)
        assertTrue(job.isCompleted)
        assertFalse(scope.isActive)
    }

    @Test
    fun `new scope is created after the same card returns to composition`() {
        setContent()

        val previousScope = assertNotNull(coroutineScopeProvider.get())
        val previousJob = previousScope.launch { awaitCancellation() }

        hideCard()
        showCard()

        val scope = assertNotNull(coroutineScopeProvider.get())

        assertTrue(previousJob.isCancelled)
        assertNotSame(previousScope, scope)
    }

    @Test
    fun `recomposition preserves scope and in-flight work`() {
        val recompose = mutableStateOf(false)
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(
                    data = card,
                    modifier = Modifier.testTag(if (recompose.value) "After" else "Before"),
                )
            }
        }

        val scope = assertNotNull(coroutineScopeProvider.get())
        val job = scope.launch { awaitCancellation() }

        rule.runOnIdle { recompose.value = true }
        rule.waitForIdle()

        assertSame(scope, coroutineScopeProvider.get())
        assertTrue(job.isActive)
    }

    private fun setContent() {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                if (isVisible.value) {
                    DivView(card)
                }
            }
        }
        rule.waitForIdle()
    }

    private fun hideCard() {
        rule.runOnIdle { isVisible.value = false }
        rule.waitForIdle()
    }

    private fun showCard() {
        rule.runOnIdle { isVisible.value = true }
        rule.waitForIdle()
    }
}
