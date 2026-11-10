package com.yandex.div.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div2.DivVisibility
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class LocalDivVisibilityModifierTest {
    @get:Rule
    val rule = createComposeRule()

    private val boundary = DivVisibilityState()
    private var observedVisibility: Boolean? = null

    @Test
    fun `attached boundary is initially visible`() {
        rule.setContent { Boundary() }

        rule.runOnIdle { assertTrue(requireNotNull(observedVisibility)) }
    }

    @Test
    fun `own invisible modifier hides boundary`() {
        rule.setContent { Boundary(Modifier.divVisibility(DivVisibility.INVISIBLE)) }

        rule.runOnIdle { assertFalse(requireNotNull(observedVisibility)) }
    }

    @Test
    fun `wrappers preserve hidden ancestor`() {
        rule.setContent {
            Box(Modifier.divVisibility(DivVisibility.INVISIBLE)) {
                Box { Box { Boundary() } }
            }
        }

        rule.runOnIdle { assertFalse(requireNotNull(observedVisibility)) }
    }

    @Test
    fun `adding hidden ancestry invalidates boundary`() {
        val visibility = mutableStateOf(DivVisibility.VISIBLE)
        rule.setContent {
            Box(Modifier.divVisibility(visibility.value)) { Boundary() }
        }

        rule.runOnIdle { visibility.value = DivVisibility.INVISIBLE }

        rule.runOnIdle { assertFalse(requireNotNull(observedVisibility)) }
    }

    @Test
    fun `removing hidden ancestry invalidates boundary`() {
        val visibility = mutableStateOf(DivVisibility.INVISIBLE)
        rule.setContent {
            Box(Modifier.divVisibility(visibility.value)) { Boundary() }
        }

        rule.runOnIdle { visibility.value = DivVisibility.VISIBLE }

        rule.runOnIdle { assertTrue(requireNotNull(observedVisibility)) }
    }

    @Test
    fun `detached boundary has unknown visibility`() {
        val attached = mutableStateOf(true)
        rule.setContent { if (attached.value) Boundary() }

        rule.runOnIdle { attached.value = false }

        rule.runOnIdle { assertNull(boundary.currentVisibility()) }
    }

    @Test
    fun `reattached boundary queries its new ancestry`() {
        val attached = mutableStateOf(true)
        rule.setContent {
            Box(Modifier.divVisibility(DivVisibility.INVISIBLE)) {
                if (attached.value) Boundary()
            }
        }
        rule.runOnIdle { attached.value = false }

        rule.runOnIdle { attached.value = true }

        rule.runOnIdle { assertFalse(requireNotNull(observedVisibility)) }
    }

    @Test
    fun `independent boundaries keep separate visibility`() {
        val otherBoundary = DivVisibilityState()
        rule.setContent {
            Box {
                Boundary(Modifier.divVisibility(DivVisibility.INVISIBLE))
                Box(Modifier.observeDivVisibility(otherBoundary))
            }
        }

        rule.runOnIdle {
            assertFalse(requireNotNull(boundary.currentVisibility()))
            assertTrue(requireNotNull(otherBoundary.currentVisibility()))
        }
    }

    @Composable
    private fun Boundary(modifier: Modifier = Modifier) {
        boundary.invalidation
        SideEffect { observedVisibility = boundary.currentVisibility() }
        Box(modifier.observeDivVisibility(boundary))
    }
}
