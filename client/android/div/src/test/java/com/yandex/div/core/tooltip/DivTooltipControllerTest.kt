package com.yandex.div.core.tooltip

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.R
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.DivTooltipRestrictor
import com.yandex.div.core.asExpression
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
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
import org.robolectric.shadows.ShadowLooper

@RunWith(AndroidJUnit4::class)
class DivTooltipControllerTest {

    private val div = Div.Text(DivText(text = "test".asExpression()))
    private val divTooltips = mutableListOf<DivTooltip>()
    private val anchorBlock = DivBlock.Text(
        div,
        ExpressionResolver.EMPTY,
        DivStatePath.fromState(0),
    )
    private val anchorViewTreeObserver = mock<ViewTreeObserver> {
        on { isAlive } doReturn true
    }
    private val anchorLayoutListener = argumentCaptor<View.OnLayoutChangeListener>()
    private val anchor = mock<DivLineHeightTextView> {
        on { getTag(R.id.div_tooltips_tag) } doReturn divTooltips
        on { isAttachedToWindow } doReturn true
        on { isLayoutRequested } doReturn false
        on { width } doReturn 300
        on { height } doReturn 100
        on { divBlock } doReturn anchorBlock
        on { viewTreeObserver } doReturn anchorViewTreeObserver
        on { addOnLayoutChangeListener(anchorLayoutListener.capture()) } doAnswer { }
    }
    private val rootView = mock<View>()
    private val runtimeStore = mock<RuntimeStore> {
        on { getOrCreateRuntime(any(), any(), any()) } doReturn ExpressionsRuntime(mock())
    }
    private val divView = mock<Div2View> {
        on { getChildAt(0) } doReturn anchor
        on { childCount } doReturn 1
        on { runtimeStore } doReturn runtimeStore
        on { rootView } doReturn rootView
    }
    private val shownCallback = mock<DivTooltipRestrictor.DivTooltipShownCallback>()
    private val tooltipRestrictor = mock<DivTooltipRestrictor> {
        on { canShowTooltip(any(), any(), any(), any(), anyOrNull()) } doReturn true
        on { tooltipShownCallback } doReturn shownCallback
    }
    private val preloadCallback = argumentCaptor<DivPreloader.Callback>()
    private val divPreloader = mock<DivPreloader> {
        on { preload(any<Div>(), any(), preloadCallback.capture()) } doReturn mock()
    }
    private val createdViews = mutableListOf<DivTooltipView>()
    private val shownCallbacks = mutableListOf<() -> Unit>()
    private val dismissedCallback = argumentCaptor<(DivTooltipView) -> Unit>()
    private val touchOutsideCallback = argumentCaptor<() -> Unit>()
    private var completeViewShowImmediately = true
    private var completeViewDismissImmediately = true
    private val viewFactory = mock<DivTooltipViewFactory> {
        on { create(any(), touchOutsideCallback.capture(), dismissedCallback.capture()) } doAnswer {
            val onDismissed = dismissedCallback.lastValue
            val view = mock<DivTooltipView>()
            doAnswer { showInvocation ->
                val onShown = showInvocation.getArgument<() -> Unit>(0)
                shownCallbacks += onShown
                if (completeViewShowImmediately) {
                    onShown()
                }
            }.whenever(view).show(any())
            doAnswer {
                if (completeViewDismissImmediately) {
                    onDismissed(view)
                }
            }.whenever(view).dismiss()
            createdViews += view
            view
        }
    }
    private val underTest = DivTooltipController(
        activeTooltipFactory = ActiveTooltipFactory(
            tooltipRestrictor = tooltipRestrictor,
            divPreloader = divPreloader,
            viewFactory = viewFactory,
        ),
    )

    @AfterTest
    fun tearDown() {
        underTest.clear()
    }

    @Test
    fun `successful preload shows tooltip and reports it once`() {
        showTooltip()

        verify(createdViews.single()).show(any())
        verify(shownCallback).onDivTooltipShown(divView, anchor, divTooltips.single())
    }

    @Test
    fun `shown callback and duration wait for actual tooltip layout`() {
        completeViewShowImmediately = false
        showTooltip(duration = 1_000L)

        verify(shownCallback, never()).onDivTooltipShown(any(), any(), any())
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        verify(createdViews.single(), never()).dismiss()

        shownCallbacks.single().invoke()

        verify(shownCallback).onDivTooltipShown(divView, anchor, divTooltips.single())
    }

    @Test
    fun `preload failure discards tooltip without dismissed callback`() {
        showTooltip(completePreload = false)

        preloadCallback.lastValue.finish(true)

        assertTrue(underTest.captureCurrentTooltips().isEmpty())
        verify(createdViews.single()).dismiss()
        verify(shownCallback, never()).onDivTooltipDismissed(any(), any(), any())
    }

    @Test
    fun `synchronous preload failure does not retain tooltip`() {
        whenever(divPreloader.preload(any<Div>(), any(), any())).doAnswer { invocation ->
            invocation.getArgument<DivPreloader.Callback>(2).finish(true)
            mock<DivPreloader.Ticket>()
        }

        showTooltip(completePreload = false)

        assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `successful callback from canceled preload does not show replacement tooltip`() {
        showTooltip(completePreload = false)
        underTest.hideTooltip("tooltip_id")
        underTest.showTooltip("tooltip_id", divView)
        val replacementView = createdViews.last()

        preloadCallback.firstValue.finish(false)

        assertEquals(1, underTest.captureCurrentTooltips().size)
        verify(replacementView, never()).show(any())

        preloadCallback.lastValue.finish(false)

        verify(replacementView).show(any())
    }

    @Test
    fun `failed callback from canceled preload does not discard replacement tooltip`() {
        showTooltip(completePreload = false)
        underTest.hideTooltip("tooltip_id")
        underTest.showTooltip("tooltip_id", divView)
        val replacementView = createdViews.last()

        preloadCallback.firstValue.finish(true)

        assertEquals(1, underTest.captureCurrentTooltips().size)
        verify(replacementView, never()).dismiss()
    }

    @Test
    fun `late touch outside from disposed tooltip leaves replacement active`() {
        showTooltip()
        underTest.cancelTooltips(divView)
        showTooltip()
        val replacementData = underTest.captureCurrentTooltips().single()

        touchOutsideCallback.firstValue.invoke()

        assertSame(replacementData, underTest.captureCurrentTooltips().single())
        verify(createdViews.last(), never()).dismiss()
    }

    @Test
    fun `late platform dismiss from disposed tooltip leaves replacement active`() {
        showTooltip()
        val disposedTooltip = divTooltips.single()
        underTest.cancelTooltips(divView)
        showTooltip()
        val replacementData = underTest.captureCurrentTooltips().single()

        dismissedCallback.firstValue.invoke(createdViews.first())

        assertSame(replacementData, underTest.captureCurrentTooltips().single())
        verify(shownCallback).onDivTooltipDismissed(divView, anchor, disposedTooltip)
    }

    @Test
    fun `late layout from disposed tooltip does not report it shown`() {
        completeViewShowImmediately = false
        showTooltip()
        underTest.cancelTooltips(divView)
        showTooltip()

        shownCallbacks.first().invoke()

        verify(shownCallback, never()).onDivTooltipShown(any(), any(), any())
    }

    @Test
    fun `synchronous successful preload releases ticket after showing`() {
        val ticket = mock<DivPreloader.Ticket>()
        whenever(divPreloader.preload(any<Div>(), any(), any())).doAnswer { invocation ->
            invocation.getArgument<DivPreloader.Callback>(2).finish(false)
            ticket
        }

        showTooltip(completePreload = false)

        verify(ticket).cancel()
        verify(shownCallback).onDivTooltipShown(divView, anchor, divTooltips.single())
    }

    @Test
    fun `failed preload cancels the owned ticket`() {
        val ticket = mock<DivPreloader.Ticket>()
        whenever(divPreloader.preload(any<Div>(), any(), preloadCallback.capture())).doReturn(ticket)
        showTooltip(completePreload = false)

        preloadCallback.lastValue.finish(true)

        verify(ticket).cancel()
    }

    @Test
    fun `synchronous platform dismiss removes ownership`() {
        showTooltip()

        underTest.hideTooltip("tooltip_id")

        assertTrue(underTest.captureCurrentTooltips().isEmpty())
        verify(shownCallback).onDivTooltipDismissed(divView, anchor, divTooltips.single())
    }

    @Test
    fun `hide pending tooltip reports neither shown nor dismissed`() {
        showTooltip(completePreload = false)

        underTest.hideTooltip("tooltip_id")

        verify(shownCallback, never()).onDivTooltipShown(any(), any(), any())
        verify(shownCallback, never()).onDivTooltipDismissed(any(), any(), any())
    }

    @Test
    fun `clear synchronously removes shown tooltip and cancels duration`() {
        completeViewDismissImmediately = false
        showTooltip(duration = 1_000L)
        val view = createdViews.single()

        underTest.clear()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        assertTrue(underTest.captureCurrentTooltips().isEmpty())
        verify(view).dismissImmediately()
        verify(view, never()).dismiss()
        verify(shownCallback).onDivTooltipDismissed(divView, anchor, divTooltips.single())
    }

    @Test
    fun `shown callback may synchronously hide without scheduling stale duration`() {
        whenever(shownCallback.onDivTooltipShown(any(), any(), any())).doAnswer {
            underTest.hideTooltip("tooltip_id")
        }

        showTooltip(duration = 1_000L)
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        verify(createdViews.single()).dismiss()
        verify(shownCallback).onDivTooltipDismissed(divView, anchor, divTooltips.single())
    }

    @Test
    fun `async platform dismiss retains ownership until animation completes`() {
        completeViewDismissImmediately = false
        showTooltip()

        underTest.hideTooltip("tooltip_id")
        underTest.showTooltip("tooltip_id", divView)

        assertEquals(1, underTest.captureCurrentTooltips().size)
        verify(viewFactory).create(any(), any(), any())
        verify(shownCallback, never()).onDivTooltipDismissed(any(), any(), any())

        dismissedCallback.lastValue.invoke(createdViews.single())

        assertTrue(underTest.captureCurrentTooltips().isEmpty())
        verify(shownCallback).onDivTooltipDismissed(divView, anchor, divTooltips.single())
    }

    @Test
    fun `platform dismiss removes matching tooltip and reports it`() {
        showTooltip()

        dismissedCallback.lastValue.invoke(createdViews.single())

        assertTrue(underTest.captureCurrentTooltips().isEmpty())
        verify(shownCallback).onDivTooltipDismissed(divView, anchor, divTooltips.single())
    }

    @Test
    fun `stale platform dismiss does not remove current tooltip`() {
        showTooltip()

        dismissedCallback.lastValue.invoke(mock())

        assertEquals(1, underTest.captureCurrentTooltips().size)
        verify(shownCallback, never()).onDivTooltipDismissed(any(), any(), any())
    }

    @Test
    fun `touch outside dismisses matching tooltip`() {
        showTooltip()

        touchOutsideCallback.lastValue.invoke()

        assertTrue(underTest.captureCurrentTooltips().isEmpty())
        verify(createdViews.single()).dismiss()
    }

    @Test
    fun `duration dismisses tooltip once`() {
        showTooltip(duration = 1_000L)

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        assertTrue(underTest.captureCurrentTooltips().isEmpty())
        verify(createdViews.single()).dismiss()
        verify(shownCallback).onDivTooltipDismissed(divView, anchor, divTooltips.single())
    }

    @Test
    fun `configuration changes recompute current layout`() {
        showTooltip()

        underTest.handleConfigurationChange(divView)
        underTest.handleConfigurationChange(divView)

        verify(createdViews.single(), times(2)).recomputePosition()
    }

    @Test
    fun `configuration change for another div view leaves tooltip unchanged`() {
        showTooltip()

        underTest.handleConfigurationChange(mock())

        verify(createdViews.single(), never()).recomputePosition()
    }

    @Test
    fun `clear cancels pending preload and removes ownership`() {
        val ticket = mock<DivPreloader.Ticket>()
        whenever(divPreloader.preload(any<Div>(), any(), any())).doReturn(ticket)
        showTooltip(completePreload = false)

        underTest.clear()

        verify(ticket).cancel()
        assertTrue(underTest.captureCurrentTooltips().isEmpty())
        verify(createdViews.single()).dismissImmediately()
    }

    @Test
    fun `duplicate id and scope creates one runtime`() {
        showTooltip()

        underTest.showTooltip("tooltip_id", divView)

        verify(viewFactory).create(any(), any(), any())
        assertEquals(1, underTest.captureCurrentTooltips().size)
    }

    @Test
    fun `duplicate requests before anchor layout create one runtime`() {
        whenever(anchor.width).doReturn(0)
        whenever(anchor.height).doReturn(0)
        whenever(anchor.isLayoutRequested).doReturn(true)
        divTooltips += createDivTooltip()

        underTest.showTooltip("tooltip_id", divView)
        underTest.showTooltip("tooltip_id", divView)
        anchorLayoutListener.allValues.forEach { listener ->
            listener.onLayoutChange(anchor, 0, 0, 300, 100, 0, 0, 0, 0)
        }

        verify(viewFactory).create(any(), any(), any())
        verify(divPreloader).preload(any<Div>(), any(), any())
        assertEquals(1, underTest.captureCurrentTooltips().size)
    }

    @Test
    fun `same id in different scopes creates independent runtimes`() {
        val scopedAnchors = prepareScopedTooltips()

        underTest.showTooltip("tooltip_id", divView, scopeId = "scope_a")
        preloadCallback.lastValue.finish(false)
        underTest.showTooltip("tooltip_id", divView, scopeId = "scope_b")
        preloadCallback.lastValue.finish(false)

        assertEquals(
            setOf("scope_a", "scope_b"),
            underTest.captureCurrentTooltips().map { it.scopeId }.toSet(),
        )
        verify(tooltipRestrictor, atLeastOnce())
            .canShowTooltip(any(), eq(scopedAnchors.first), any(), any(), eq("scope_a"))
        verify(tooltipRestrictor, atLeastOnce())
            .canShowTooltip(any(), eq(scopedAnchors.second), any(), any(), eq("scope_b"))
    }

    @Test
    fun `cancel all reports whether anything was dismissed`() {
        completeViewDismissImmediately = false
        assertFalse(underTest.cancelAllTooltips())
        showTooltip()

        assertTrue(underTest.cancelAllTooltips())

        assertTrue(underTest.captureCurrentTooltips().isEmpty())
        verify(createdViews.single()).dismissImmediately()
        verify(shownCallback).onDivTooltipDismissed(divView, anchor, divTooltips.single())
    }

    @Test
    fun `cancel matching tooltips synchronously removes ownership`() {
        completeViewDismissImmediately = false
        showTooltip()

        underTest.cancelTooltips(divView)

        assertTrue(underTest.captureCurrentTooltips().isEmpty())
        verify(createdViews.single()).dismissImmediately()
        verify(shownCallback).onDivTooltipDismissed(divView, anchor, divTooltips.single())
    }

    @Test
    fun `map tooltip stores tooltip tag`() {
        val view = mock<View>()
        val mappedTooltips = listOf(createDivTooltip(id = "mapped"))

        underTest.mapTooltip(view, mappedTooltips)

        verify(view).setTag(R.id.div_tooltips_tag, mappedTooltips)
    }

    @Test
    fun `show restriction prevents runtime creation`() {
        whenever(tooltipRestrictor.canShowTooltip(any(), any(), any(), any(), anyOrNull()))
            .doReturn(false)

        showTooltip(completePreload = false)

        verify(viewFactory, never()).create(any(), any(), any())
        assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `show restriction prevents creating tooltip runtime state`() {
        whenever(tooltipRestrictor.canShowTooltip(any(), any(), any(), any(), anyOrNull()))
            .doReturn(false)

        showTooltip(completePreload = false)

        verify(runtimeStore, never()).getOrCreateRuntime(any(), any(), any())
    }

    @Test
    fun `anchor detached during preload prevents showing tooltip`() {
        showTooltip(completePreload = false)
        whenever(anchor.isAttachedToWindow).doReturn(false)

        preloadCallback.lastValue.finish(false)

        verify(createdViews.single(), never()).show(any())
        assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `restriction changed during preload prevents showing tooltip`() {
        showTooltip(completePreload = false)
        whenever(tooltipRestrictor.canShowTooltip(any(), any(), any(), any(), anyOrNull()))
            .doReturn(false)

        preloadCallback.lastValue.finish(false)

        verify(createdViews.single(), never()).show(any())
        assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    @Test
    fun `hide affects only the requested scope`() {
        showScopedTooltips()

        underTest.hideTooltip("tooltip_id", scopeId = "scope_a")

        assertEquals(listOf("scope_b"), underTest.captureCurrentTooltips().map { it.scopeId })
        verify(createdViews.first()).dismiss()
        verify(createdViews.last(), never()).dismiss()
    }

    @Test
    fun `find view returns content from the requested scope`() {
        showScopedTooltips()
        val firstView = mock<View>()
        val secondView = mock<View>()
        whenever(createdViews.first().findViewWithTag("content")).doReturn(firstView)
        whenever(createdViews.last().findViewWithTag("content")).doReturn(secondView)

        val foundView = underTest.findViewWithTag("content", scopeId = "scope_b")

        assertSame(secondView, foundView)
    }

    @Test
    fun `cancel for another div view leaves the tooltip active`() {
        showTooltip()

        underTest.cancelTooltips(mock())

        assertSame(divView, underTest.captureCurrentTooltips().single().divView)
        verify(createdViews.single(), never()).dismissImmediately()
    }

    @Test
    fun `multiple flag is checked before and after preload`() {
        divTooltips += createDivTooltip()

        underTest.showTooltip("tooltip_id", divView, multiple = true)
        preloadCallback.lastValue.finish(false)

        verify(tooltipRestrictor, times(2))
            .canShowTooltip(divView, anchor, divTooltips.single(), true, null)
        verify(createdViews.single()).show(any())
    }

    @Test
    fun `zero duration keeps shown tooltip active after delayed tasks`() {
        showTooltip(duration = 0L)

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        verify(createdViews.single(), never()).dismiss()
        assertSame(divView, underTest.captureCurrentTooltips().single().divView)
    }

    private fun showScopedTooltips() {
        prepareScopedTooltips()
        underTest.showTooltip("tooltip_id", divView, scopeId = "scope_a")
        preloadCallback.lastValue.finish(false)
        underTest.showTooltip("tooltip_id", divView, scopeId = "scope_b")
        preloadCallback.lastValue.finish(false)
    }

    private fun showTooltip(
        duration: Long = 0L,
        completePreload: Boolean = true,
    ) {
        divTooltips.clear()
        divTooltips += createDivTooltip(duration = duration)
        underTest.showTooltip("tooltip_id", divView)
        if (completePreload) {
            preloadCallback.lastValue.finish(false)
        }
    }

    private fun createDivTooltip(
        id: String = "tooltip_id",
        duration: Long = 0L,
    ): DivTooltip {
        return DivTooltip(
            div = div,
            id = id,
            duration = duration.asExpression(),
            position = DivTooltip.Position.RIGHT.asExpression(),
        )
    }

    private fun prepareScopedTooltips(): ScopedAnchors {
        val firstAnchor = createScopedAnchor()
        val secondAnchor = createScopedAnchor()
        val firstScope = mockScope("scope_a", firstAnchor)
        val secondScope = mockScope("scope_b", secondAnchor)
        whenever(divView.childCount).doReturn(2)
        whenever(divView.getChildAt(0)).doReturn(firstScope)
        whenever(divView.getChildAt(1)).doReturn(secondScope)
        return ScopedAnchors(
            first = firstAnchor,
            second = secondAnchor,
        )
    }

    private fun createScopedAnchor(): DivLineHeightTextView {
        val observer = mock<ViewTreeObserver> {
            on { isAlive } doReturn true
        }
        return mock {
            on { getTag(R.id.div_tooltips_tag) } doReturn listOf(createDivTooltip())
            on { isAttachedToWindow } doReturn true
            on { isLayoutRequested } doReturn false
            on { width } doReturn 300
            on { height } doReturn 100
            on { divBlock } doReturn anchorBlock
            on { viewTreeObserver } doReturn observer
        }
    }

    private fun mockScope(scopeId: String, child: View): ViewGroup {
        return mock {
            on { tag } doReturn scopeId
            on { childCount } doReturn 1
            on { getChildAt(0) } doReturn child
            on { getTag(R.id.div_tooltips_tag) } doReturn null
        }
    }

    private data class ScopedAnchors(
        val first: View,
        val second: View,
    )
}
