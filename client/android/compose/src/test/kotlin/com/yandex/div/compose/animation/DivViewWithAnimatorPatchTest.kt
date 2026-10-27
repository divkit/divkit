package com.yandex.div.compose.animation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.test.data.action
import com.yandex.div.test.data.animatorStartAction
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.numberAnimator
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivViewWithAnimatorPatchTest {
    @get:Rule
    val rule = createComposeRule()

    private val divContext = DivContext(
        getApplicationContext(),
        DivConfiguration(),
    )

    @BeforeTest
    fun setUp() {
        rule.mainClock.autoAdvance = false
    }

    @Test
    fun `sibling patch keeps animator and its local variable`() {
        val data = data(container(items = listOf(
            target(endValue = 10.0, initialValue = 1L),
            text(id = SIBLING_ID, text = "Before"),
        )))
        setContent(data)

        assertTrue(applyPatch(data, DivPatch(changes = listOf(
            DivPatch.Change(SIBLING_ID, listOf(text(id = SIBLING_ID, text = "After")))
        ))))
        rule.onNodeWithTag(SIBLING_ID).assertTextEquals("After")
        rule.onNodeWithTag(TARGET_ID).performClick()
        finishAnimation()

        rule.onNodeWithTag(TARGET_ID).assertTextEquals("10")
    }

    @Test
    fun `target patch uses new animator and local variable`() {
        val data = data(target(endValue = 10.0, initialValue = 1L))
        setContent(data)

        assertTrue(applyPatch(data, DivPatch(changes = listOf(
            DivPatch.Change(TARGET_ID, listOf(target(endValue = 20.0, initialValue = 2L)))
        ))))
        rule.onNodeWithTag(TARGET_ID).assertTextEquals("2")
        rule.onNodeWithTag(TARGET_ID).performClick()
        finishAnimation()

        rule.onNodeWithTag(TARGET_ID).assertTextEquals("20")
    }

    private fun applyPatch(data: DivData, patch: DivPatch): Boolean {
        val result = rule.runOnIdle {
            runBlocking { divContext.patcher.applyPatch(data, patch) }
        }
        repeat(2) {
            rule.mainClock.advanceTimeByFrame()
            rule.waitForIdle()
        }
        return result
    }

    private fun finishAnimation() {
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
        rule.mainClock.advanceTimeBy(ANIMATION_DURATION)
        rule.waitForIdle()
    }

    private fun setContent(data: DivData) {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data)
            }
        }
    }

    private fun target(endValue: Double, initialValue: Long): Div = text(
        action = action(
            scopeId = TARGET_ID,
            typed = animatorStartAction(ANIMATOR_ID),
        ),
        animators = listOf(numberAnimator(
            duration = ANIMATION_DURATION,
            endValue = endValue,
            id = ANIMATOR_ID,
            variableName = VALUE,
        )),
        id = TARGET_ID,
        text = expression("@{$VALUE}"),
        variables = listOf(variable(VALUE, initialValue)),
    )

    private companion object {
        const val ANIMATION_DURATION = 160L
        const val ANIMATOR_ID = "animator"
        const val SIBLING_ID = "sibling"
        const val TARGET_ID = "target"
        const val VALUE = "value"
    }
}
