package com.yandex.div.core.tooltip

import android.view.View
import android.view.ViewGroup
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.DivTooltipRestrictor
import com.yandex.div.core.asExpression
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
class DivTooltipControllerTest {

    private val div = Div.Text(DivText(text = "test1".asExpression()))
    private val tooltips = mutableListOf<DivTooltip>()
    private val anchorBlock = DivBlock.Text(mock<Div.Text>(), ExpressionResolver.EMPTY, DivStatePath.fromState(0))
    private val anchor = mock<DivLineHeightTextView> {
        on { getTag(R.id.div_tooltips_tag) } doReturn tooltips
        on { isAttachedToWindow } doReturn true
        on { isLayoutRequested } doReturn false
        on { width } doReturn 300
        on { height } doReturn 100
        on { divBlock } doReturn anchorBlock
    }

    private val runtimeStore = mock<RuntimeStore> {
        on { getOrCreateRuntime(any(), any(), any()) } doReturn ExpressionsRuntime(mock())
    }
    private val div2View = mock<Div2View> {
        on { getChildAt(0) } doReturn anchor
        on { childCount } doReturn 1
        on { runtimeStore } doReturn runtimeStore
    }

    private val tooltipShownCallback = mock<DivTooltipRestrictor.DivTooltipShownCallback>()
    private val tooltipRestrictor = mock<DivTooltipRestrictor> {
        on { canShowTooltip(any(), any(), any(), any(), anyOrNull()) } doReturn true
        on { tooltipShownCallback } doReturn tooltipShownCallback
    }

    private val preloadCallbackCaptor = argumentCaptor<DivPreloader.Callback>()
    private val divPreloader = mock<DivPreloader> {
        on { preload(any<Div>(), any(), preloadCallbackCaptor.capture()) } doReturn mock()
    }

    private val tooltipWrapper = mock<DivTooltipContainer> {
        on { tooltipView } doReturn mock()
    }

    private val onShownCaptor = argumentCaptor<() -> Unit>()

    private val viewController = mock<DivTooltipViewController>()
    private val visibilityController = mock<DivTooltipVisibilityController> {
        on { showTooltip(any(), onShownCaptor.capture()) } doAnswer { }
    }

    private val underTest = DivTooltipController(tooltipRestrictor, divPreloader, viewController, visibilityController)

    init {
        whenever(viewController.createPopupWindow(any(), any(), any())).doAnswer { inv ->
            val data = inv.arguments[0] as TooltipData
            data.popupWindow = mock<SafePopupWindow> {
                on { contentView } doReturn tooltipWrapper
                on { isShowing } doReturn true
            }
        }
    }

    @Test
    fun `tooltip is shown`() {
        showTooltip()
        verify(visibilityController).showTooltip(any(), any())
    }

    @Test
    fun `hideTooltip delegates to visibility controller`() {
        showTooltip()
        underTest.hideTooltip("tooltip_id")
        verify(visibilityController).hideTooltip(any())
    }

    @Test
    fun `onDismiss callback removes tooltip and notifies`() {
        val dismissCaptor = argumentCaptor<() -> Unit>()
        showTooltip()
        verify(viewController).createPopupWindow(any(), any(), dismissCaptor.capture())

        dismissCaptor.lastValue.invoke()

        verify(visibilityController).onDismiss(any())
        verify(tooltipShownCallback).onDivTooltipDismissed(div2View, anchor, tooltips[0])
        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `onTouchOutside callback hides tooltip`() {
        val touchOutsideCaptor = argumentCaptor<() -> Unit>()
        showTooltip()
        verify(viewController).createPopupWindow(any(), touchOutsideCaptor.capture(), any())

        touchOutsideCaptor.lastValue.invoke()

        verify(visibilityController).hideTooltip(any())
    }

    @Test
    fun `clear dismisses popup and clears registry`() {
        showTooltip()
        val popupWindow = underTest.captureCurrentTooltips().first().popupWindow as SafePopupWindow
        val tracking = mock<Disposable>()
        currentTooltip().anchorTrackingDisposable = tracking

        underTest.clear()

        verify(popupWindow).dismiss()
        verify(tracking).close()
        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `tooltip is dismissed after timeout`() {
        showTooltip(duration = 1000)
        onShownCaptor.lastValue.invoke()

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        verify(visibilityController).hideTooltip(any())
    }

    @Test
    fun `tooltip is not dismissed after timeout when duration is zero`() {
        showTooltip()
        onShownCaptor.lastValue.invoke()

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        verify(visibilityController, never()).hideTooltip(any())
    }

    @Test
    fun `auto hide is canceled on cleanup`() {
        showTooltip(duration = 1000)
        onShownCaptor.lastValue.invoke()
        underTest.clear()

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        verify(visibilityController, never()).hideTooltip(any())
    }

    @Test
    fun `onPopupShown is called when tooltip becomes visible`() {
        showTooltip()
        onShownCaptor.lastValue.invoke()
        verify(viewController).onPopupShown(any())
    }

    @Test
    fun `tooltip not present at shown tooltips before restriction-check`() {
        val wasEmptyOnRestrictorCheck = mutableListOf<Boolean>()
        whenever(tooltipRestrictor.canShowTooltip(any(), any(), any(), any(), anyOrNull())).doAnswer {
            wasEmptyOnRestrictorCheck += underTest.captureCurrentTooltips().isEmpty()
            true
        }
        showTooltip()

        onShownCaptor.lastValue.invoke()

        Assert.assertTrue(wasEmptyOnRestrictorCheck.first())
        verify(tooltipShownCallback).onDivTooltipShown(div2View, anchor, tooltips[0])
    }

    @Test
    fun `tooltip show restriction works`() {
        whenever(tooltipRestrictor.canShowTooltip(any(), any(), any(), any(), anyOrNull())).doReturn(false)
        showTooltip()
        verify(divPreloader, never()).preload(any<Div>(), any(), any())
    }

    @Test
    fun `when preload completes with failures tooltip is removed`() {
        showTooltip(completePreload = false)

        preloadCallbackCaptor.lastValue.finish(true)

        verify(visibilityController, never()).showTooltip(any(), any())
        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `handleConfigurationChange delegates anchor tracking to view controller`() {
        showTooltip()
        underTest.handleConfigurationChange(div2View)
        verify(viewController).startAnchorPositionTracking(eq(currentTooltip()), eq(div2View), any())
    }

    @Test
    fun `showTooltip logs error when tooltip view is not found`() {
        showTooltip("missing_tooltip")

        verify(div2View).logError(any())
        verify(visibilityController, never()).showTooltip(any(), any())
        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `showTooltip ignores duplicate id and scope`() {
        showTooltip()

        showTooltip()

        verify(viewController, times(1)).createPopupWindow(any(), any(), any())
        Assert.assertEquals(1, underTest.captureCurrentTooltips().size)
    }

    @Test
    fun `showTooltip allows same id in different scopes`() {
        val (anchorA, anchorB) = prepareScopedTooltips()

        showTooltip(scopeId = "scope_a")
        showTooltip(scopeId = "scope_b")

        val shown = underTest.captureCurrentTooltips().toList()
        Assert.assertEquals(2, shown.size)
        Assert.assertEquals(setOf("scope_a", "scope_b"), shown.map { it.scopeId }.toSet())
        verify(tooltipRestrictor, atLeastOnce())
            .canShowTooltip(any(), eq(anchorA), any(), any(), eq("scope_a"))
        verify(tooltipRestrictor, atLeastOnce())
            .canShowTooltip(any(), eq(anchorB), any(), any(), eq("scope_b"))
    }

    @Test
    fun `showTooltip passes multiple flag to restrictor`() {
        showTooltip(multiple = true)
        verify(tooltipRestrictor, atLeastOnce()).canShowTooltip(any(), any(), any(), eq(true), anyOrNull())
    }

    @Test
    fun `showTooltip requests layout when anchor is not laid out`() {
        whenever(anchor.width).doReturn(0)
        whenever(anchor.height).doReturn(0)
        whenever(anchor.isLayoutRequested).doReturn(false)

        showTooltip()

        verify(anchor).requestLayout()
    }

    @Test
    fun `hideTooltip with scope hides only matching tooltip`() {
        prepareScopedTooltips()
        val dismissCaptor = argumentCaptor<() -> Unit>()
        showTooltip(scopeId = "scope_a")
        verify(viewController).createPopupWindow(any(), any(), dismissCaptor.capture())
        showTooltip(scopeId = "scope_b")
        val hideCaptor = argumentCaptor<TooltipData>()

        underTest.hideTooltip("tooltip_id", scopeId = "scope_a")

        verify(visibilityController).hideTooltip(hideCaptor.capture())
        Assert.assertEquals("scope_a", hideCaptor.firstValue.scopeId)

        dismissCaptor.firstValue.invoke()

        Assert.assertEquals(listOf("scope_b"), underTest.captureCurrentTooltips().map { it.scopeId })
    }

    @Test
    fun `hideTooltip does nothing for unknown id`() {
        showTooltip()

        underTest.hideTooltip("other_id")

        verify(visibilityController, never()).hideTooltip(any())
        Assert.assertEquals(1, underTest.captureCurrentTooltips().size)
    }

    @Test
    fun `cancelTooltips dismisses tooltips of given divView`() {
        showTooltip()
        underTest.cancelTooltips(div2View)
        verify(visibilityController).dismissTooltip(any())
    }

    @Test
    fun `cancelTooltips ignores tooltips of other divViews`() {
        showTooltip()

        underTest.cancelTooltips(mock())

        verify(visibilityController, never()).dismissTooltip(any())
        Assert.assertEquals(1, underTest.captureCurrentTooltips().size)
    }

    @Test
    fun `cancelTooltips removes tooltip when dismissTooltip returns it`() {
        showTooltip()
        val tooltip = currentTooltip()
        whenever(visibilityController.dismissTooltip(tooltip)).doReturn(tooltip)

        underTest.cancelTooltips(div2View)

        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `cancelAllTooltips returns false when empty`() {
        Assert.assertFalse(underTest.cancelAllTooltips())
        verify(visibilityController, never()).dismissTooltip(any())
    }

    @Test
    fun `cancelAllTooltips dismisses all and returns true`() {
        showTooltip()

        Assert.assertTrue(underTest.cancelAllTooltips())
        verify(visibilityController).dismissTooltip(any())
        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `mapTooltip stores tooltips tag on view`() {
        val view = mock<View>()
        val mapped = listOf(createDivTooltip("mapped_id"))

        underTest.mapTooltip(view, mapped)

        verify(view).setTag(R.id.div_tooltips_tag, mapped)
    }

    @Test
    fun `findViewWithTag returns view from matching tooltip popup`() {
        val nested = mock<View>()
        whenever(tooltipWrapper.findViewWithTag<View>("nested_id")).doReturn(nested)
        showTooltip()

        Assert.assertSame(nested, underTest.findViewWithTag("nested_id", null))
    }

    @Test
    fun `findViewWithTag returns null for different scope`() {
        whenever(tooltipWrapper.findViewWithTag<View>("nested_id")).doReturn(mock())
        showTooltip()

        Assert.assertNull(underTest.findViewWithTag("nested_id", "other_scope"))
    }

    @Test
    fun `tooltip is not shown when dismissed before preload finishes`() {
        showTooltip(completePreload = false)
        underTest.captureCurrentTooltips().first().dismissed = true

        preloadCallbackCaptor.lastValue.finish(false)

        verify(visibilityController, never()).showTooltip(any(), any())
        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `tooltip is not shown when anchor detached before preload finishes`() {
        whenever(anchor.isAttachedToWindow).doReturn(false)

        showTooltip()

        verify(visibilityController, never()).showTooltip(any(), any())
        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `tooltip is not shown when restrictor rejects after preload`() {
        showTooltip(completePreload = false)
        whenever(tooltipRestrictor.canShowTooltip(any(), any(), any(), any(), anyOrNull())).doReturn(false)

        preloadCallbackCaptor.lastValue.finish(false)

        verify(visibilityController, never()).showTooltip(any(), any())
        Assert.assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `clear cancels preload ticket`() {
        val preloadTicket = mock<DivPreloader.Ticket>()
        whenever(divPreloader.preload(any<Div>(), any(), any())).doReturn(preloadTicket)
        showTooltip(completePreload = false)

        underTest.clear()

        verify(preloadTicket).cancel()
    }

    private fun showTooltip(
        id: String = "tooltip_id",
        duration: Long = 0,
        scopeId: String? = null,
        multiple: Boolean = false,
        completePreload: Boolean = true,
    ) {
        tooltips.add(createDivTooltip(duration = duration))
        underTest.showTooltip(id, div2View, multiple, scopeId)
        if (completePreload && preloadCallbackCaptor.allValues.isNotEmpty()) {
            preloadCallbackCaptor.lastValue.finish(false)
        }
    }

    private fun createDivTooltip(id: String = "tooltip_id", duration: Long = 0) = DivTooltip(
        div = div,
        id = id,
        duration = duration.asExpression(),
        position = DivTooltip.Position.RIGHT.asExpression(),
    )

    private fun currentTooltip() = underTest.captureCurrentTooltips().first()

    private fun prepareScopedTooltips(): Pair<View, View> {
        val tooltipA = mutableListOf(createDivTooltip())
        val tooltipB = mutableListOf(createDivTooltip())
        val anchorA = mockAnchor(tooltipA)
        val anchorB = mockAnchor(tooltipB)
        val scopeA = mockScope("scope_a", anchorA)
        val scopeB = mockScope("scope_b", anchorB)
        whenever(div2View.childCount).doReturn(2)
        whenever(div2View.getChildAt(0)).doReturn(scopeA)
        whenever(div2View.getChildAt(1)).doReturn(scopeB)
        return anchorA to anchorB
    }

    private fun mockAnchor(tooltipList: MutableList<DivTooltip>): DivLineHeightTextView = mock {
        on { getTag(R.id.div_tooltips_tag) } doReturn tooltipList
        on { isAttachedToWindow } doReturn true
        on { isLayoutRequested } doReturn false
        on { width } doReturn 300
        on { height } doReturn 100
        on { divBlock } doReturn anchorBlock
    }

    private fun mockScope(scopeId: String, child: View): ViewGroup = mock {
        on { tag } doReturn scopeId
        on { childCount } doReturn 1
        on { getChildAt(0) } doReturn child
        on { getTag(R.id.div_tooltips_tag) } doReturn null
    }
}
