package com.yandex.div.compose.views.transition

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.text
import com.yandex.div2.DivVisibility
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class StateChangeTransitionContentTest {
    @get:Rule
    val rule = createComposeRule()

    private val data = text(text = "Content", id = "content")
    private var token: Any? = null
    private var disposals = 0
    private var transitionModifier: StateChangeTransitionModifier? = null

    @Test
    fun `null scope supplies neutral transition and preserves context`() {
        val context = StateChangeTransitionContext(
            contentVisible = false,
            snapshotParentId = "parent",
        )
        var providedContext: StateChangeTransitionContext? = null
        rule.setContent {
            CompositionLocalProvider(LocalStateChangeTransitionContext provides context) {
                WithStateChangeTransition(data, DivVisibility.INVISIBLE) {
                    val currentContext = LocalStateChangeTransitionContext.current
                    SideEffect {
                        transitionModifier = it
                        providedContext = currentContext
                    }
                    Box(it.modifier)
                }
            }
        }

        assertEquals(StateChangeTransitionModifier(), transitionModifier)
        assertSame(context, providedContext)
    }

    @Test
    fun `becoming invisible preserves content lifetime`() {
        val visibility = mutableStateOf(DivVisibility.VISIBLE)
        rule.setContent { Content(visibility.value) }
        val initialToken = token

        rule.runOnIdle { visibility.value = DivVisibility.INVISIBLE }

        rule.runOnIdle {
            assertSame(initialToken, token)
            assertEquals(0, disposals)
        }
    }

    @Test
    fun `becoming visible preserves content lifetime`() {
        val visibility = mutableStateOf(DivVisibility.INVISIBLE)
        rule.setContent { Content(visibility.value) }
        val initialToken = token

        rule.runOnIdle { visibility.value = DivVisibility.VISIBLE }

        rule.runOnIdle {
            assertSame(initialToken, token)
            assertEquals(0, disposals)
        }
    }

    @Test
    fun `nested null scopes share neutral transition`() {
        var outer: StateChangeTransitionModifier? = null
        rule.setContent {
            WithStateChangeTransition(data, DivVisibility.INVISIBLE) {
                SideEffect { outer = it }
                Box { Content(DivVisibility.VISIBLE) }
            }
        }

        assertSame(outer, transitionModifier)
    }

    @Composable
    private fun Content(visibility: DivVisibility) {
        WithStateChangeTransition(data, visibility) {
            val remembered = remember { Any() }
            SideEffect {
                token = remembered
                transitionModifier = it
            }
            DisposableEffect(Unit) {
                onDispose { disposals++ }
            }
            Box(it.modifier)
        }
    }
}
