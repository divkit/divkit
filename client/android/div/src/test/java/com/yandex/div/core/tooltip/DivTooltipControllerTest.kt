package com.yandex.div.core.tooltip

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.R
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.DivTooltipRestrictor
import com.yandex.div.core.asExpression
import com.yandex.div.core.dagger.DivDataComponent
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.state.DivStateManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.PathFormatException
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivState
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
    private val stateManager = mock<DivStateManager>()
    private val dataComponent = mock<DivDataComponent> {
        on { stateManager } doReturn stateManager
    }
    private val divView = mock<Div2View> {
        on { getChildAt(0) } doReturn anchor
        on { childCount } doReturn 1
        on { runtimeStore } doReturn runtimeStore
        on { rootView } doReturn rootView
        on { dataComponent } doReturn dataComponent
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
    private var tooltipContentView: View? = null
    private var substrateContentView: View? = null
    private var onViewCreate: () -> Unit = {}
    private val viewFactory = mock<DivTooltipViewFactory> {
        on { create(any(), touchOutsideCallback.capture(), dismissedCallback.capture()) } doAnswer {
            val onDismissed = dismissedCallback.lastValue
            onViewCreate()
            var isPopupShowing = false
            val view = mock<DivTooltipView> {
                on { isShown } doAnswer { isPopupShowing }
                on { tooltipView } doReturn tooltipContentView
                on { substrateView } doReturn substrateContentView
            }
            doAnswer { showInvocation ->
                isPopupShowing = true
                val onShown = showInvocation.getArgument<() -> Unit>(0)
                shownCallbacks += onShown
                if (completeViewShowImmediately) {
                    onShown()
                }
            }.whenever(view).show(any())
            doAnswer {
                if (completeViewDismissImmediately) {
                    isPopupShowing = false
                    onDismissed(view)
                }
            }.whenever(view).dismiss()
            createdViews += view
            view
        }
    }
    private val divBinder = mock<DivBinder>()
    private val stateDiv = Div.State(
        DivState(
            id = "state",
            states = listOf(DivState.State(stateId = "first"), DivState.State(stateId = "second")),
        )
    )
    private val stateContainer = Div.Container(DivContainer(items = listOf(stateDiv)))
    private val nestedStateDiv = Div.State(
        DivState(
            id = "outer",
            states = listOf(
                DivState.State(stateId = "s1", div = div),
                DivState.State(stateId = "s2", div = stateContainer),
            ),
        )
    )
    private val tooltipStatePath = stateLayoutPath("tooltip_id")

    private val underTest = DivTooltipController(
        activeTooltipFactory = ActiveTooltipFactory(
            tooltipRestrictor = tooltipRestrictor,
            divPreloader = divPreloader,
            viewFactory = viewFactory,
        ),
        divBinder = { divBinder },
    )

    private val manager = DivTooltipManager(underTest)

    @Test
    fun `pending tooltip is not reported as shown`() {
        showTooltip(completePreload = false)

        assertTrue(manager.captureShownTooltips().isEmpty())
    }

    @Test
    fun `open tooltip waiting for layout is reported as shown`() {
        completeViewShowImmediately = false

        showTooltip()

        assertEquals(listOf("tooltip_id"), manager.captureShownTooltips().map { it.id })
    }

    @Test
    fun `shown tooltip is reported with its initiator`() {
        showTooltip()

        val metadata = manager.captureShownTooltips().single()
        assertEquals("tooltip_id", metadata.id)
        assertSame(divView, metadata.initiator)
    }

    @Test
    fun `tooltip remains reported during dismissal animation`() {
        completeViewDismissImmediately = false
        showTooltip()

        underTest.hideTooltip("tooltip_id")

        assertEquals(listOf("tooltip_id"), manager.captureShownTooltips().map { it.id })
    }

    @Test
    fun `dismissed tooltip is no longer reported`() {
        showTooltip()

        underTest.hideTooltip("tooltip_id")

        assertTrue(manager.captureShownTooltips().isEmpty())
    }

    @Test
    fun `synchronous preload is not blocked by its own pending tooltip`() {
        restrictToNoShownTooltips()
        whenever(divPreloader.preload(any<Div>(), any(), any())).doAnswer { invocation ->
            invocation.getArgument<DivPreloader.Callback>(2).finish(false)
            mock<DivPreloader.Ticket>()
        }

        showTooltip(completePreload = false)

        assertEquals(listOf("tooltip_id"), manager.captureShownTooltips().map { it.id })
        verify(shownCallback).onDivTooltipShown(divView, anchor, divTooltips.single())
    }

    @Test
    fun `asynchronous preload is not blocked by its own pending tooltip`() {
        restrictToNoShownTooltips()
        showTooltip(completePreload = false)

        preloadCallback.lastValue.finish(false)

        assertEquals(listOf("tooltip_id"), manager.captureShownTooltips().map { it.id })
        verify(shownCallback).onDivTooltipShown(divView, anchor, divTooltips.single())
    }

    @Test
    fun `another shown tooltip still prevents showing a tooltip`() {
        showTooltip()
        restrictToNoShownTooltips()
        divTooltips += createDivTooltip(id = "another_tooltip")

        underTest.showTooltip("another_tooltip", divView)

        verify(viewFactory).create(any(), any(), any())
        assertEquals(listOf("tooltip_id"), manager.captureShownTooltips().map { it.id })
        verify(shownCallback, never()).onDivTooltipShown(divView, anchor, divTooltips.last())
    }

    @Test
    fun `cancel all includes pending tooltips and ignores their late preload`() {
        showTooltip(completePreload = false)

        assertTrue(manager.cancelAllTooltips())
        preloadCallback.lastValue.finish(false)

        assertTrue(manager.captureShownTooltips().isEmpty())
        verify(shownCallback, never()).onDivTooltipShown(any(), any(), any())
    }

    @Test
    fun `pending tooltip survives configuration change without becoming shown`() {
        showTooltip(completePreload = false)

        underTest.handleConfigurationChange(divView)

        assertTrue(manager.captureShownTooltips().isEmpty())
        assertEquals("tooltip_id", underTest.captureCurrentTooltips().single().id)
    }

    @Test
    fun `pending tooltip can finish preload after configuration change`() {
        restrictToNoShownTooltips()
        showTooltip(completePreload = false)
        underTest.handleConfigurationChange(divView)

        preloadCallback.lastValue.finish(false)

        assertEquals(listOf("tooltip_id"), manager.captureShownTooltips().map { it.id })
    }

    @Test
    fun `shown callback observes the tooltip in the public list`() {
        val reportedIds = mutableListOf<String>()
        whenever(shownCallback.onDivTooltipShown(any(), any(), any())).doAnswer {
            reportedIds += manager.captureShownTooltips().map { it.id }
        }

        showTooltip()

        assertEquals(listOf("tooltip_id"), reportedIds)
    }

    @Test
    fun `dismissed callback observes an empty public list`() {
        val reportedLists = mutableListOf<List<String>>()
        whenever(shownCallback.onDivTooltipDismissed(any(), any(), any())).doAnswer {
            reportedLists += manager.captureShownTooltips().map { it.id }
        }
        showTooltip()

        underTest.hideTooltip("tooltip_id")

        assertEquals(listOf(emptyList()), reportedLists)
    }

    private fun restrictToNoShownTooltips() {
        whenever(tooltipRestrictor.canShowTooltip(any(), any(), any(), any(), anyOrNull()))
            .doAnswer { manager.captureShownTooltips().isEmpty() }
    }

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

    @Test
    fun `state variables of tooltip content are collected`() {
        showTooltip()

        verify(stateManager).collectStateVariables(underTest.captureCurrentTooltips().single().tooltipBlock)
    }

    @Test
    fun `state variables of substrate content are collected`() {
        showTooltip(substrateDiv = stateContainer)

        verify(stateManager).collectStateVariables(underTest.captureCurrentTooltips().single().substrateBlock!!)
    }

    @Test
    fun `tooltip state path rebinds matching state layout`() {
        val stateLayout = stateLayout(tooltipStatePath)
        tooltipContentView = frameWith(stateLayout)
        showTooltip()
        val path = tooltipStatePath.append("state", "second", "second")

        val handled = underTest.switchStates(divView, listOf(path))

        verify(divBinder).bind(stateLayout, stateLayout.divBlock!!, divView)
        verify(divBinder).attachIndicators(divView)
        assertEquals(listOf(path), handled)
    }

    @Test
    fun `substrate state path rebinds matching state layout`() {
        val stateLayout = stateLayout(stateLayoutPath("tooltip_id", prefix = "tooltip_substrate"))
        substrateContentView = frameWith(stateLayout)
        showTooltip(substrateDiv = stateContainer)
        val path = stateLayoutPath("tooltip_id", prefix = "tooltip_substrate").append("state", "second", "second")

        val handled = underTest.switchStates(divView, listOf(path))

        verify(divBinder).bind(stateLayout, stateLayout.divBlock!!, divView)
        assertEquals(listOf(path), handled)
    }

    @Test
    fun `tooltip state path rebinds tooltip root when layout is not found`() {
        val contentView = FrameLayout(testContextThemeWrapper())
        tooltipContentView = contentView
        showTooltip()

        underTest.switchStates(divView, listOf(tooltipStatePath.append("state", "second", "second")))

        verify(divBinder).bind(contentView, underTest.captureCurrentTooltips().single().tooltipBlock, divView)
    }

    @Test
    fun `card state path with tooltip-like ids is not handled by tooltip`() {
        showTooltip(div = stateContainer)

        val handled = underTest.switchStates(
            divView,
            listOf(
                DivStatePath.parse("0/tooltip#tooltip_id/global/state/second"),
                DivStatePath.parse("0/tooltip#10#tooltip_id/0/state/second"),
            ),
        )

        verify(divBinder, never()).bind(any(), any(), any())
        assertTrue(handled.isEmpty())
    }

    @Test
    fun `card state path is not handled by tooltip`() {
        showTooltip()

        val handled = underTest.switchStates(divView, listOf(DivStatePath.parse("0/state/second")))

        verify(divBinder, never()).bind(any(), any(), any())
        assertTrue(handled.isEmpty())
    }

    @Test
    fun `state paths of another div view are ignored`() {
        showTooltip()

        val handled = underTest.switchStates(
            mock(),
            listOf(tooltipStatePath.append("state", "second", "second")),
        )

        verify(divBinder, never()).bind(any(), any(), any())
        assertTrue(handled.isEmpty())
    }

    @Test
    fun `relative state path is resolved inside shown tooltip`() {
        showTooltip(div = stateContainer)

        assertResolved("tooltip#10#tooltip_id/0/state/second", "state/second")
    }

    @Test
    fun `relative state path is resolved when tooltip root is state`() {
        showTooltip(div = stateDiv)

        assertResolved("tooltip#10#tooltip_id/0/state/second", "state/second")
    }

    @Test
    fun `relative nested state path is resolved inside inactive state`() {
        showTooltip(div = nestedStateDiv)

        assertResolved("tooltip#10#tooltip_id/0/outer/s2/state/second", "outer/s2/state/second")
    }

    @Test
    fun `relative nested state path with wrong parent state throws`() {
        showTooltip(div = nestedStateDiv)

        assertFailsWith<PathFormatException> { underTest.resolveStatePath(divView, "outer/s1/state/second") }
    }

    @Test
    fun `relative state path is resolved inside substrate`() {
        showTooltip(substrateDiv = stateContainer)

        assertResolved("tooltip_substrate#10#tooltip_id/0/state/second", "state/second")
    }

    @Test
    fun `relative state path is resolved inside scoped tooltip`() {
        prepareScopedTooltips(div = stateContainer)
        underTest.showTooltip("tooltip_id", divView, scopeId = "scope_a")
        preloadCallback.lastValue.finish(false)

        assertResolved("tooltip#10#tooltip_id#7#scope_a/0/state/second", "state/second")
    }

    @Test
    fun `relative state path without matching tooltip throws`() {
        showTooltip()

        assertFailsWith<PathFormatException> { underTest.resolveStatePath(divView, "state/second") }
    }

    @Test
    fun `relative state path found twice in one tooltip throws`() {
        showTooltip(div = Div.Container(DivContainer(items = listOf(stateDiv, stateDiv))))

        assertFailsWith<PathFormatException> { underTest.resolveStatePath(divView, "state/second") }
    }

    @Test
    fun `relative state path found in two tooltips throws`() {
        showTooltips("first_tooltip", "second_tooltip")

        assertFailsWith<PathFormatException> { underTest.resolveStatePath(divView, "state/second") }
    }

    @Test
    fun `relative state path is resolved inside tooltip with slash in id`() {
        showTooltips("broken/id")

        assertResolved("tooltip#9#broken/id/0/state/second", "state/second")
    }

    @Test
    fun `relative state path with odd segments throws`() {
        showTooltip(div = stateContainer)

        assertFailsWith<PathFormatException> { underTest.resolveStatePath(divView, "state") }
    }

    @Test
    fun `resolved relative state path rebinds matching state layout`() {
        val stateLayout = stateLayout(tooltipStatePath)
        tooltipContentView = frameWith(stateLayout)
        showTooltip(div = stateContainer)

        underTest.switchStates(divView, listOf(underTest.resolveStatePath(divView, "state/second")))

        verify(divBinder).bind(stateLayout, stateLayout.divBlock!!, divView)
    }

    @Test
    fun `resolved relative state path rebinds state layout inside tooltip with slash in id`() {
        val stateLayout = stateLayout(stateLayoutPath("broken/id"))
        tooltipContentView = frameWith(stateLayout)
        showTooltips("broken/id")

        underTest.switchStates(divView, listOf(underTest.resolveStatePath(divView, "state/second")))

        verify(divBinder).bind(stateLayout, stateLayout.divBlock!!, divView)
    }

    @Test
    fun `relative state path is resolved while tooltip content is bound`() {
        var resolved: DivStatePath? = null
        onViewCreate = { resolved = underTest.resolveStatePath(divView, "state/second") }

        showTooltip(div = stateContainer)

        assertEquals("tooltip#10#tooltip_id/0/state/second", resolved?.statesString)
    }

    @Test
    fun `state path switched while tooltip content is bound is applied after build`() {
        val stateLayout = stateLayout(tooltipStatePath)
        tooltipContentView = frameWith(stateLayout)
        val path = tooltipStatePath.append("state", "second", "second")
        var handled = emptyList<DivStatePath>()
        onViewCreate = { handled = underTest.switchStates(divView, listOf(path)) }

        showTooltip(div = stateContainer)

        assertEquals(listOf(path), handled)
        verify(divBinder).bind(stateLayout, stateLayout.divBlock!!, divView)
    }

    @Test
    fun `tooltip cancelled while content is bound is not shown`() {
        var isCancelled = false
        onViewCreate = { isCancelled = underTest.cancelAllTooltips() }

        showTooltip(completePreload = false)

        assertTrue(isCancelled)
        assertTooltipCancelledWhileBound()
    }

    @Test
    fun `tooltip hidden while content is bound is not shown`() {
        onViewCreate = { underTest.hideTooltip("tooltip_id") }

        showTooltip(completePreload = false)

        assertTooltipCancelledWhileBound()
    }

    @Test
    fun `tooltip of cancelled div view while content is bound is not shown`() {
        onViewCreate = { underTest.cancelTooltips(divView) }

        showTooltip(completePreload = false)

        assertTooltipCancelledWhileBound()
    }

    @Test
    fun `tooltip cleared while content is bound is not shown`() {
        onViewCreate = { underTest.clear() }

        showTooltip(completePreload = false)

        assertTooltipCancelledWhileBound()
    }

    @Test
    fun `cancelled tooltip is not used for relative state paths while its content is bound`() {
        var result: Result<DivStatePath>? = null
        onViewCreate = {
            underTest.hideTooltip("tooltip_id")
            result = runCatching { underTest.resolveStatePath(divView, "state/second") }
        }

        showTooltip(div = stateContainer, completePreload = false)

        assertTrue(result?.exceptionOrNull() is PathFormatException)
    }

    @Test
    fun `cancelled tooltip does not claim state paths while its content is bound`() {
        var handled: List<DivStatePath>? = null
        onViewCreate = {
            underTest.hideTooltip("tooltip_id")
            handled = underTest.switchStates(divView, listOf(tooltipStatePath.append("state", "second", "second")))
        }

        showTooltip(div = stateContainer, completePreload = false)

        assertEquals(emptyList(), handled)
    }

    @Test
    fun `tooltip shown again while its content is bound is not duplicated`() {
        onViewCreate = {
            onViewCreate = {}
            underTest.showTooltip("tooltip_id", divView)
        }

        showTooltip()

        assertEquals(1, createdViews.size)
        assertEquals(1, underTest.captureCurrentTooltips().size)
    }

    @Test
    fun `tooltip of another div view cancelled while content is bound is shown`() {
        onViewCreate = { underTest.cancelTooltips(mock()) }

        showTooltip()

        verify(createdViews.single()).show(any())
        assertEquals(1, underTest.captureCurrentTooltips().size)
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
        div: Div = this.div,
        substrateDiv: Div? = null,
    ) {
        divTooltips.clear()
        divTooltips += createDivTooltip(duration = duration, div = div, substrateDiv = substrateDiv)
        underTest.showTooltip("tooltip_id", divView)
        if (completePreload) {
            preloadCallback.lastValue.finish(false)
        }
    }

    private fun showTooltips(vararg ids: String) {
        divTooltips.clear()
        ids.forEach { divTooltips += createDivTooltip(id = it, div = stateContainer) }
        ids.forEach {
            underTest.showTooltip(it, divView, multiple = true)
            preloadCallback.lastValue.finish(false)
        }
    }

    private fun createDivTooltip(
        id: String = "tooltip_id",
        duration: Long = 0L,
        div: Div = this.div,
        substrateDiv: Div? = null,
    ): DivTooltip {
        return DivTooltip(
            div = div,
            id = id,
            duration = duration.asExpression(),
            position = DivTooltip.Position.RIGHT.asExpression(),
            substrateDiv = substrateDiv,
        )
    }

    private fun assertResolved(expected: String, relativePath: String) =
        assertEquals(expected, underTest.resolveStatePath(divView, relativePath).statesString)

    private fun assertTooltipCancelledWhileBound() {
        verify(createdViews.single()).dismissImmediately()
        verify(createdViews.single(), never()).show(any())
        verify(divPreloader, never()).preload(any<Div>(), any(), any())
        assertTrue(underTest.captureCurrentTooltips().isEmpty())
    }

    private fun stateLayoutPath(
        tooltipId: String,
        scopeId: String? = null,
        prefix: String = "tooltip",
    ) = DivStatePath.fromState(0)
        .withNamespace("$prefix#${tooltipId.length}#$tooltipId" + scopeId?.let { "#${it.length}#$it" }.orEmpty())
        .appendDiv("$prefix#$tooltipId")
        .appendDiv("state")

    private fun stateLayout(path: DivStatePath) = DivStateLayout(testContextThemeWrapper()).apply {
        currentStatePath = path.append("state", "first", "first")
        divBlock = DivBlock.State(stateDiv, ExpressionResolver.EMPTY, path)
    }

    private fun frameWith(view: View) = FrameLayout(testContextThemeWrapper()).apply { addView(view) }

    private fun prepareScopedTooltips(div: Div = this.div): ScopedAnchors {
        val firstAnchor = createScopedAnchor(div)
        val secondAnchor = createScopedAnchor(div)
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

    private fun createScopedAnchor(div: Div = this.div): DivLineHeightTextView {
        val observer = mock<ViewTreeObserver> {
            on { isAlive } doReturn true
        }
        return mock {
            on { getTag(R.id.div_tooltips_tag) } doReturn listOf(createDivTooltip(div = div))
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
