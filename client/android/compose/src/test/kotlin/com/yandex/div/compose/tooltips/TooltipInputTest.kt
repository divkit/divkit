package com.yandex.div.compose.tooltips

import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.internal.tooltip.TouchEventDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TooltipInputTest {

    @get:Rule
    val rule = createComposeRule()

    private val events = mutableListOf<Int>()
    private val positions = mutableListOf<Offset>()
    private val visible = mutableStateOf(true)
    private val target = object : View(getApplicationContext()) {
        override fun getLocationOnScreen(outLocation: IntArray) {
            outLocation[0] = 10
            outLocation[1] = 20
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            events += event.actionMasked
            positions += Offset(event.x, event.y)
            return true
        }
    }
    private val dispatcher = TouchEventDispatcher { target }::dispatch

    @Test
    fun `complete gesture reaches underlying window in its coordinates`() {
        setContent()

        rule.onNodeWithTag("popup").performTouchInput {
            down(Offset(50f, 60f))
            moveTo(Offset(80f, 90f))
            up()
        }

        assertEquals(listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP), events)
        assertEquals(listOf(Offset(40f, 40f), Offset(70f, 70f), Offset(70f, 70f)), positions)
    }

    @Test
    fun `removing popup cancels gesture in underlying window`() {
        setContent()

        rule.onNodeWithTag("popup").performTouchInput { down(Offset(50f, 60f)) }
        rule.runOnIdle { visible.value = false }
        rule.waitForIdle()

        assertEquals(listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_CANCEL), events)
    }

    @Test
    fun `scroll inside popup cancels forwarding to underlying window`() {
        val scroll = ScrollState(0)
        rule.setContent {
            CompositionLocalProvider(LocalTooltipTouchDispatcher provides dispatcher) {
                Box(Modifier.fillMaxSize().tooltipInput().verticalScroll(scroll).testTag("popup")) {
                    Box(Modifier.height(2000.dp))
                }
            }
        }

        rule.onNodeWithTag("popup").performTouchInput {
            down(Offset(50f, 100f))
            moveTo(Offset(50f, 98f))
            moveTo(Offset(50f, 20f))
            up()
        }

        assertTrue(scroll.value > 0)
        assertEquals(listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL), events)
    }

    private fun setContent() {
        rule.setContent {
            CompositionLocalProvider(LocalTooltipTouchDispatcher provides dispatcher) {
                if (visible.value) {
                    Box(Modifier.fillMaxSize().tooltipInput().testTag("popup"))
                }
            }
        }
    }
}
