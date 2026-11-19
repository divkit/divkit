package com.yandex.div.compose.visibility

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class VisibilityHostTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `visibility responds to the replacement host and unsubscribes from the previous host`() {
        val firstHost = TestVisibilityHost()
        val secondHost = TestVisibilityHost()
        val currentHost = mutableStateOf<VisibilityHost>(firstHost)
        val visibilityChanges = mutableListOf<Boolean>()
        rule.setContent {
            CompositionLocalProvider(LocalVisibilityHost provides currentHost.value) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .onDivVisibilityChanged(minFractionVisible = 0.5f) {
                            visibilityChanges.add(it)
                        }
                )
            }
        }
        rule.runOnIdle { currentHost.value = secondHost }
        rule.runOnIdle { visibilityChanges.clear() }

        rule.runOnIdle {
            secondHost.visibleBounds = null
            secondHost.callbacks.toList().forEach { it.checkVisibility() }
        }

        assertEquals(listOf(false), visibilityChanges)
        assertTrue(firstHost.callbacks.isEmpty())
    }

    @Test
    fun `tracker receives unbind from replacement host and unsubscribes from the previous host`() {
        val firstHost = TestVisibilityHost()
        val secondHost = TestVisibilityHost()
        val currentHost = mutableStateOf<VisibilityHost>(firstHost)
        val tracker = mock<VisibilityActionTracker>()
        rule.setContent {
            CompositionLocalProvider(LocalVisibilityHost provides currentHost.value) {
                tracker.onComposition()
            }
        }
        rule.runOnIdle { currentHost.value = secondHost }
        rule.waitForIdle()

        rule.runOnIdle {
            secondHost.contentUnboundCallbacks.toList().forEach { it() }
        }

        verify(tracker).onContentUnbound()
        assertTrue(firstHost.contentUnboundCallbacks.isEmpty())
    }

    private class TestVisibilityHost : VisibilityHost {
        var visibleBounds: Rect? = Rect(0f, 0f, 10000f, 10000f)
        val callbacks = mutableSetOf<VisibilityHost.Callback>()
        val contentUnboundCallbacks = mutableSetOf<() -> Unit>()

        override fun getVisibleRectInWindow(): Rect? = visibleBounds

        override fun addCallback(callback: VisibilityHost.Callback) {
            callbacks.add(callback)
        }

        override fun removeCallback(callback: VisibilityHost.Callback) {
            callbacks.remove(callback)
        }

        override fun addContentUnboundCallback(callback: () -> Unit) {
            contentUnboundCallbacks.add(callback)
        }

        override fun removeContentUnboundCallback(callback: () -> Unit) {
            contentUnboundCallbacks.remove(callback)
        }
    }
}
