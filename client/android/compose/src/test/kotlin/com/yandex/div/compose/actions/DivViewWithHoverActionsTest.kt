package com.yandex.div.compose.actions

import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.setContent
import com.yandex.div.test.data.action
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewWithHoverActionsTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `hover resumes after press end only for a hovering stylus released inside`() {
        val actionHandler = TestExternalActionHandler()
        rule.setContent(
            configuration = DivConfiguration(actionHandler = actionHandler, reporter = TestReporter()),
            data = data(text(
                id = "button",
                text = "Button",
                hoverStartActions = listOf(action(url = "test://start")),
                hoverEndActions = listOf(action(url = "test://end")),
                pressStartActions = listOf(action(url = "test://press-start")),
                pressEndActions = listOf(action(url = "test://press-end")),
                doubleTapActions = listOf(action(url = "test://double-tap")),
            )),
        )

        val node = rule.onNodeWithTag("button").fetchSemanticsNode()
        val view = (node.root as ViewRootForTest).view
        val position = node.boundsInRoot.center
        val outside = Offset(position.x, node.boundsInRoot.bottom + 10f)
        val doubleTapTimeout = ViewConfiguration.getDoubleTapTimeout().toLong()
        rule.runOnIdle {
            view.sendStylusEvent(MotionEvent.ACTION_HOVER_ENTER, position)
            view.sendStylusEvent(MotionEvent.ACTION_HOVER_EXIT, position)
            view.sendStylusEvent(MotionEvent.ACTION_DOWN, position)
            view.sendStylusEvent(MotionEvent.ACTION_UP, position)
            view.sendStylusEvent(MotionEvent.ACTION_HOVER_ENTER, position)
        }
        rule.mainClock.advanceTimeBy(doubleTapTimeout)

        rule.runOnIdle {
            view.sendStylusEvent(MotionEvent.ACTION_HOVER_EXIT, position)
            view.sendStylusEvent(MotionEvent.ACTION_DOWN, position)
            view.sendStylusEvent(MotionEvent.ACTION_MOVE, outside)
            view.sendStylusEvent(MotionEvent.ACTION_UP, outside)
        }
        rule.mainClock.advanceTimeBy(doubleTapTimeout)

        rule.runOnIdle {
            view.sendStylusEvent(MotionEvent.ACTION_DOWN, position)
            view.sendStylusEvent(MotionEvent.ACTION_UP, position)
        }
        rule.mainClock.advanceTimeBy(doubleTapTimeout)

        assertEquals(
            listOf(
                actionData(source = DivActionSource.GESTURE, url = "test://start"),
                actionData(source = DivActionSource.GESTURE, url = "test://end"),
                actionData(source = DivActionSource.GESTURE, url = "test://press-start"),
                actionData(source = DivActionSource.GESTURE, url = "test://press-end"),
                actionData(source = DivActionSource.GESTURE, url = "test://start"),
                actionData(source = DivActionSource.GESTURE, url = "test://end"),
                actionData(source = DivActionSource.GESTURE, url = "test://press-start"),
                actionData(source = DivActionSource.GESTURE, url = "test://press-end"),
                actionData(source = DivActionSource.GESTURE, url = "test://press-start"),
                actionData(source = DivActionSource.GESTURE, url = "test://press-end"),
            ),
            actionHandler.handledActions,
        )
    }

    private fun View.sendStylusEvent(action: Int, position: Offset) {
        val event = MotionEvent.obtain(
            0, rule.mainClock.currentTime, action, 1,
            arrayOf(MotionEvent.PointerProperties().apply {
                id = 0
                toolType = MotionEvent.TOOL_TYPE_STYLUS
            }),
            arrayOf(MotionEvent.PointerCoords().apply {
                x = position.x
                y = position.y
            }),
            0, 0, 1f, 1f, 0, 0, InputDevice.SOURCE_STYLUS, 0,
        )
        try {
            when (action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> dispatchTouchEvent(event)
                else -> dispatchGenericMotionEvent(event)
            }
        } finally {
            event.recycle()
        }
    }
}
