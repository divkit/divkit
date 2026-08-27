package com.yandex.div.core.view2.divs

import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.view2.animations.DEFAULT_CLICK_ANIMATION
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.action
import com.yandex.div2.DivAction
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.robolectric.Shadows.shadowOf
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertFalse

@RunWith(AndroidJUnit4::class)
class DivActionBinderSetTapListenerTest : DivBinderTest() {

    private val view = View(context)
    private val actionDivView = divView()
    private val oldTapActions = listOf(action(url = "old_tap"))
    private val tapActions = listOf(action(url = "tap"))
    private val doubleTapActions = listOf(action(url = "double_tap"))
    private val underTest = DivActionBinder(
        actionPerformer = actionPerformer,
        logger = mock(),
        divActionBeaconSender = mock(),
        longtapActionsPassToChild = false,
        shouldIgnoreActionMenuItems = false,
    )

    @Test
    fun `single tap action is invoked through view click when double tap is absent`() {
        bind(tapActions = tapActions)

        view.performClick()

        verifyAction(tapActions, DivActionReason.CLICK)
    }

    @Test
    fun `rebind with double tap clears stale view click and invokes new tap once`() {
        bind(tapActions = oldTapActions)
        bind(tapActions = tapActions, doubleTapActions = doubleTapActions)

        view.performClick()
        dispatchTaps(0L)

        verifyAction(tapActions, DivActionReason.CLICK)
    }

    @Test
    fun `rebind without double tap replaces gesture callback with view click`() {
        bind(tapActions = oldTapActions, doubleTapActions = doubleTapActions)
        bind(tapActions = tapActions)

        view.performClick()
        dispatchTaps(0L)

        verifyAction(tapActions, DivActionReason.CLICK)
    }

    @Test
    fun `double tap invokes only double tap action`() {
        bind(tapActions = tapActions, doubleTapActions = doubleTapActions)

        dispatchTaps(0L, 100L)

        verifyAction(doubleTapActions, DivActionReason.DOUBLE_CLICK)
    }

    @Test
    fun `removing all actions on rebind makes view non clickable`() {
        bind(tapActions = tapActions)

        bind()

        assertFalse(view.isClickable)
    }

    private fun bind(
        tapActions: List<DivAction> = emptyList(),
        doubleTapActions: List<DivAction> = emptyList(),
    ) {
        underTest.bindDivActions(
            target = view,
            actions = tapActions,
            longTapActions = null,
            doubleTapActions = doubleTapActions,
            hoverStartActions = null,
            hoverEndActions = null,
            pressStartActions = null,
            pressEndActions = null,
            actionAnimation = DEFAULT_CLICK_ANIMATION,
            captureFocusOnAction = Expression.constant(false),
            resolver = resolver,
            divView = actionDivView,
        )
    }

    private fun verifyAction(actions: List<DivAction>, reason: String) {
        verify(actionPerformer, only()).performBulkActions(any(), eq(actions), any(), any(), eq(reason))
    }

    private fun dispatchTaps(vararg downTimes: Long) {
        downTimes.forEach { downTime ->
            listOf(MotionEvent.ACTION_DOWN to downTime, MotionEvent.ACTION_UP to downTime + 10L).forEach {
                MotionEvent.obtain(downTime, it.second, it.first, 0f, 0f, 0).run {
                    view.dispatchTouchEvent(this)
                    recycle()
                }
            }
        }
        shadowOf(Looper.getMainLooper()).idleFor(
            Duration.ofMillis(ViewConfiguration.getDoubleTapTimeout().toLong() + 1L),
        )
    }
}
