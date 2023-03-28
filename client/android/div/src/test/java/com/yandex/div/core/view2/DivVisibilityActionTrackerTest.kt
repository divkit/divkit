
package com.yandex.div.core.view2

import android.view.View
import com.yandex.div.DivDataTag
import com.yandex.div.core.asExpression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivText
import com.yandex.div2.DivVisibilityAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class DivVisibilityActionTrackerTest {
    private val visibleDivsChangedCaptor = argumentCaptor<Map<View, Div>>()
    private val viewVisibilityCalculator = mock<ViewVisibilityCalculator>()
    private val resolver = mock<ExpressionResolver>()

    private val visibilityActionDispatcher = mock<DivVisibilityActionDispatcher> {
        on { dispatchVisibleViewsChanged(visibleDivsChangedCaptor.capture()) } doAnswer { }
    }

    private val scope = mock<Div2View> {
        on { logId } doReturn "div"
        on { dataTag } doReturn DivDataTag("test")
        on { expressionResolver } doReturn resolver
    }

    private val view1 = mockView()
    private val view2 = mockView()
    private val view3 = mockView()
    private val view4 = mockView()
    private val action1 = DivVisibilityAction(logId = "visibility_action")
    private val action2 = DivVisibilityAction(logId = "visibility_action2")
    private val action3 = DivVisibilityAction(logId = "visibility_action3")
    private val lottaActions = listOf(action1, action2, action3)
    private val actionsWithThreeDifferentDelays = Array(6) {
        val delay: Long = when {
            it < 2 -> 100
            it < 4 -> 200
            else -> 300
        }
        return@Array DivVisibilityAction(logId = "visibility_action_$it", visibilityDuration = delay.asExpression())
    }.toList()
    private val divBase1 = DivText(text = "test1".asExpression(), visibilityActions = listOf(action1))
    private val divBase2 = DivText(text = "test2".asExpression(), visibilityActions = listOf(action2))
    private val divBase3 = DivText(text = "test3".asExpression(), visibilityActions = lottaActions)
    private val divBase4 = DivText(text = "test4".asExpression(), visibilityActions = actionsWithThreeDifferentDelays)
    private val div1 = Div.Text(divBase1)
    private val div2 = Div.Text(divBase2)
    private val div3 = Div.Text(divBase3)
    private val div4 = Div.Text(divBase4)

    private val visibilityActionTracker = DivVisibilityActionTracker(
        viewVisibilityCalculator,
        visibilityActionDispatcher
    )

    @Test
    fun `visibility action is not dispatched for invisible view`() {
        trackVisibilityAction(view1, div1, 40)
        Robolectric.flushForegroundThreadScheduler()

        verify(visibilityActionDispatcher, never()).dispatchActions(
            eq(scope),
            eq(view1),
            argThat { this.contains(action1) && this.size == 1 }
        )
    }

    @Test
    fun `visibility action is dispatched for visible view`() {
        trackVisibilityAction(view1, div1, 100)
        Robolectric.flushForegroundThreadScheduler()

        verify(visibilityActionDispatcher).dispatchActions(
            eq(scope),
            eq(view1),
            argThat { this.contains(action1) && this.size == 1 }
        )
    }

    @Test
    fun `visibility action is not dispatched when view become invisible before deadline`() {
        trackVisibilityAction(view1, div1, 100)
        trackVisibilityAction(view1, div1, 40)
        Robolectric.flushForegroundThreadScheduler()

        verify(visibilityActionDispatcher, never()).dispatchActions(
            eq(scope),
            eq(view1),
            argThat { this.contains(action1) && this.size == 1 }
        )
    }

    @Test
    fun `visibility action is not dispatched when view become null before deadline`() {
        trackVisibilityAction(view1, div1, 100)
        updateViewVisibility(view1, visibilityPercentage = 40)
        visibilityActionTracker.trackVisibilityActionsOf(scope, null, div1)
        Robolectric.flushForegroundThreadScheduler()

        verify(visibilityActionDispatcher, never()).dispatchActions(
            eq(scope),
            eq(view1),
            argThat { this.contains(action1) && this.size == 1 }
        )
    }

    @Test
    fun `repeated visibility action is not dispatched before deadline`() {
        trackVisibilityAction(view1, div1, 100)
        trackVisibilityAction(view1, div1, 100)
        Robolectric.flushForegroundThreadScheduler()

        verify(visibilityActionDispatcher, times(1)).dispatchActions(
            eq(scope),
            eq(view1),
            argThat { this.contains(action1) && this.size == 1 }
        )
    }

    @Test
    fun `repeated visibility action is not dispatched when view stays visible`() {
        trackVisibilityAction(view1, div1, 100)
        Robolectric.flushForegroundThreadScheduler()

        trackVisibilityAction(view1, div1, 100)
        Robolectric.flushForegroundThreadScheduler()
        verify(visibilityActionDispatcher, times(1)).dispatchActions(
            eq(scope),
            eq(view1),
            argThat { this.contains(action1) && this.size == 1 }
        )
    }

    @Test
    fun `repeated visibility action is dispatched when view changes visibility there and back`() {
        trackVisibilityAction(view1, div1, 100)
        Robolectric.flushForegroundThreadScheduler()

        trackVisibilityAction(view1, div1, 40)
        Robolectric.flushForegroundThreadScheduler()

        trackVisibilityAction(view1, div1, 100)
        Robolectric.flushForegroundThreadScheduler()

        verify(visibilityActionDispatcher, times(2)).dispatchActions(eq(scope), eq(view1), any())
    }

    @Test
    fun `changed visible divs dispatched`() {
        trackVisibilityAction(view1, div1, 100)

        Robolectric.flushForegroundThreadScheduler()
        assertEquals(visibleDivsChangedCaptor.lastValue[view1], div1)
    }

    @Test
    fun `twice tracked view dispatched visibility changing twice`() {
        trackVisibilityAction(view1, div1, 100)
        Robolectric.flushForegroundThreadScheduler()
        trackVisibilityAction(view1, div1, 80)
        Robolectric.flushForegroundThreadScheduler()
        verify(visibilityActionDispatcher, times(2)).dispatchVisibleViewsChanged(any())
    }

    @Test
    fun `after adding new visible action visibility div view dispatched again`() {
        trackVisibilityAction(view1, div1, 100)
        Robolectric.flushForegroundThreadScheduler()
        trackVisibilityAction(view2, div2, 100)
        Robolectric.flushForegroundThreadScheduler()
        trackVisibilityAction(view3, div3, 100)
        Robolectric.flushForegroundThreadScheduler()
        verify(visibilityActionDispatcher, times(3)).dispatchVisibleViewsChanged(any())
    }

    @Test
    fun `both views with divs dispatched after they became visible`() {
        trackVisibilityAction(view1, div1, 100)
        trackVisibilityAction(view2, div2, 80)
        Robolectric.flushForegroundThreadScheduler()

        val result = visibleDivsChangedCaptor.lastValue
        assertTrue(result.size == 2)
        assertEquals(result[view1], div1)
        assertEquals(result[view2], div2)
    }

    @Test
    fun `view with actions deleted from VisibilityDivsChanged actions when view became invisible`() {
        trackVisibilityAction(view1, div1, 50)
        trackVisibilityAction(view1, div1, 0)
        Robolectric.flushForegroundThreadScheduler()
        assertTrue(visibleDivsChangedCaptor.lastValue.isEmpty())
    }

    @Test
    fun `multiple visibility actions call dispatch once`() {
        trackVisibilityAction(view3, div3, 100)
        Robolectric.flushForegroundThreadScheduler()

        verify(visibilityActionDispatcher, times(1)).dispatchActions(
            eq(scope),
            eq(view3),
            argThat { this.toList().containsAll(lottaActions) && this.size == lottaActions.size }
        )
    }

    @Test
    fun `repeated visibility action is not dispatched when view stays visible for multiple actions`() {
        trackVisibilityAction(view3, div3, 100)
        Robolectric.flushForegroundThreadScheduler()

        trackVisibilityAction(view3, div3, 100)
        Robolectric.flushForegroundThreadScheduler()
        verify(visibilityActionDispatcher, times(1)).dispatchActions(
            eq(scope),
            eq(view3),
            argThat { this.toList().containsAll(lottaActions) && this.size == lottaActions.size }
        )
    }

    @Test
    fun `actions w different delays grouped for group dispatching`() {
        trackVisibilityAction(view4, div4, 100)
        Robolectric.getForegroundThreadScheduler().advanceBy(101L, TimeUnit.MILLISECONDS)
        verify(visibilityActionDispatcher, times(1)).dispatchActions(
            eq(scope),
            eq(view4),
            argThat { this.size == 2 }
        )
        Robolectric.getForegroundThreadScheduler().advanceBy(101L, TimeUnit.MILLISECONDS)
        verify(visibilityActionDispatcher, times(2)).dispatchActions(
            eq(scope),
            eq(view4),
            argThat { this.size == 2 }
        )
        Robolectric.getForegroundThreadScheduler().advanceBy(101L, TimeUnit.MILLISECONDS)
        verify(visibilityActionDispatcher, times(3)).dispatchActions(
            eq(scope),
            eq(view4),
            argThat { this.size == 2 }
        )
    }

    @Test
    fun `actions w different delays correctly canceled`() {
        trackVisibilityAction(view4, div4, 100)
        Robolectric.getForegroundThreadScheduler().advanceBy(101L, TimeUnit.MILLISECONDS)
        verify(visibilityActionDispatcher, times(1)).dispatchActions(
            eq(scope),
            eq(view4),
            argThat { this.size == 2 }
        )
        trackVisibilityAction(view4, div4, 0)
        Robolectric.flushForegroundThreadScheduler()
        verify(visibilityActionDispatcher, times(1)).dispatchActions(eq(scope), eq(view4), any())
    }

    private fun trackVisibilityAction(view: View, div: Div, visibilityPercentage: Int) {
        updateViewVisibility(view, visibilityPercentage)
        visibilityActionTracker.trackVisibilityActionsOf(scope, view, div)
    }

    private fun updateViewVisibility(view: View, visibilityPercentage: Int) {
        whenever(viewVisibilityCalculator.calculateVisibilityPercentage(eq(view))) doReturn visibilityPercentage
    }

    companion object {
        private fun mockView() = mock<View> {
            on { isLaidOut } doReturn true
        }
    }
}
