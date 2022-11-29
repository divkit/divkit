package com.yandex.div.core.view2.divs

import com.yandex.div.core.Div2Logger
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.downloader.DivPatchManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.TemporaryDivStateCache
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.state.InMemoryDivStateCache
import com.yandex.div2.Div
import com.yandex.div2.DivState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.times
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivStateBinderTest: DivBinderTest() {

    private val viewBinder = mock<DivBinder>()
    private val divActionBinder = mock<DivActionBinder>()
    private val stateCache = InMemoryDivStateCache()
    private val temporaryStateCache = TemporaryDivStateCache()
    private val div2Logger = mock<Div2Logger>()
    private val divVisibilityActionTracker = mock<DivVisibilityActionTracker>()
    private val errorCollectors = mock<ErrorCollectors>()
    private val divPatchManager = mock<DivPatchManager>()
    private val divPatchCache = mock<DivPatchCache>()

    private val div = UnitTestData(STATE_DIR, "state_list.json").div
    private val divState = div.value() as DivState
    private val stateLayout = (viewCreator.create(div, ExpressionResolver.EMPTY) as DivStateLayout).apply {
        layoutParams = defaultLayoutParams()
    }

    private val stateBinder = DivStateBinder(
        baseBinder = baseBinder(),
        viewCreator = viewCreator,
        viewBinder = { viewBinder },
        divStateCache = stateCache,
        temporaryStateCache = temporaryStateCache,
        divActionBinder = divActionBinder,
        divPatchManager = divPatchManager,
        divPatchCache = divPatchCache,
        div2Logger = div2Logger,
        divVisibilityActionTracker = divVisibilityActionTracker,
        errorCollectors = errorCollectors
    )

    @Test
    fun `first state path applied when no defaultStateId`() {
        stateBinder.bindView(stateLayout, divState, divView, rootPath())

        assertEquals(pathToState("first"), stateLayout.path)
    }

    @Test
    fun `default state path applied when has defaultStateId`() {
        val div = UnitTestData(STATE_DIR,"default_state.json").div
        val divState = div.value() as DivState
        val stateLayout = viewCreator.create(div, ExpressionResolver.EMPTY) as DivStateLayout
        stateLayout.layoutParams = defaultLayoutParams()

        stateBinder.bindView(stateLayout, divState, divView, rootPath())

        assertEquals(pathToState("default"), stateLayout.path)
    }

    @Test
    fun `selected state path applied`() {
        switchToState("second")
        stateBinder.bindView(stateLayout, divState, divView, rootPath())

        assertEquals(pathToState("second"), stateLayout.path)
    }

    @Test
    fun `empty state path applied`() {
        switchToState("empty")
        stateBinder.bindView(stateLayout, divState, divView, rootPath())

        assertEquals(pathToState("empty"), stateLayout.path)
    }

    @Test
    fun `default state bound`() {
        stateBinder.bindView(stateLayout, divState, divView, rootPath())

        val expectedStateDiv = divState.states[0].div!!
        assertStateBound(pathToState("first"), expectedStateDiv)
    }

    @Test
    fun `selected state bound`() {
        switchToState("second")
        stateBinder.bindView(stateLayout, divState, divView, rootPath())

        val expectedStateDiv = divState.states[1].div!!
        assertStateBound(pathToState("second"), expectedStateDiv)
    }

    @Test
    fun `selected temporary state bound`() {
        switchToState(stateId = "first", temporaryStateId = "second")
        stateBinder.bindView(stateLayout, divState, divView, rootPath())

        val expectedStateDiv = divState.states[1].div!!
        assertStateBound(pathToState("second"), expectedStateDiv)
    }

    @Test
    fun `empty state not bound`() {
        clearInvocations(viewCreator)

        switchToState("empty")
        stateBinder.bindView(stateLayout, divState, divView, rootPath())

        assertStateNotBound(pathToState("empty"))
    }

    @Test
    fun `the same state rebound`() {
        stateBinder.bindView(stateLayout, divState, divView, rootPath())
        clearInvocations(viewCreator, viewBinder)

        stateBinder.bindView(stateLayout, divState,  divView, rootPath())

        val expectedStateDiv = divState.states[0].div!!
        assertStateRebound(pathToState("first"), expectedStateDiv)
    }

    @Test
    fun `new state bound`() {
        stateBinder.bindView(stateLayout, divState, divView, rootPath())
        clearInvocations(viewCreator, viewBinder)

        switchToState("second")
        stateBinder.bindView(stateLayout, divState, divView, rootPath())

        val expectedStateDiv = divState.states[1].div!!
        assertStateBound(pathToState("second"), expectedStateDiv)
    }

    @Test
    fun `rebind adds swipes`() {
        val oldData = UnitTestData(STATE_DIR_AUTOANIMATIONS, "old_state_no_swipe_actions.json").asDivState
        val newData = UnitTestData(STATE_DIR_AUTOANIMATIONS, "old_state.json").asDivState
        stateBinder.bindView(stateLayout, oldData, divView, rootPath())

        stateBinder.bindView(stateLayout, newData, divView, rootPath())

        assertNotNull(stateLayout.swipeOutCallback)
    }

    @Test
    fun `rebind removes old view tracking`() {
        val oldData = UnitTestData(STATE_DIR_AUTOANIMATIONS, "old_state.json").asDivState
        val newData = UnitTestData(STATE_DIR_AUTOANIMATIONS, "new_state_incompatible.json").asDivState
        stateBinder.bindView(stateLayout, oldData, divView, rootPath())
        val child = stateLayout.getChildAt(0)

        stateBinder.bindView(stateLayout, newData, divView, rootPath())
        val inOrder = inOrder(divView)

        inOrder.verify(divView).unbindViewFromDiv(child)
        inOrder.verify(divView).bindViewToDiv(stateLayout.getChildAt(0), newData.states[0].div!!)
    }

    @Test
    fun `rebind to empty state clears everything`() {
        val oldData = UnitTestData(STATE_DIR_AUTOANIMATIONS, "old_state.json").asDivState
        val newData = UnitTestData(STATE_DIR_AUTOANIMATIONS, "new_state_empty_state.json").asDivState
        stateBinder.bindView(stateLayout, oldData, divView, rootPath())
        val child = stateLayout.getChildAt(0)

        stateBinder.bindView(stateLayout, newData, divView, rootPath())

        assertNull(stateLayout.swipeOutCallback)
        assertEquals(0, stateLayout.childCount)
        verify(divView).unbindViewFromDiv(child)
    }

    @Test
    fun `rebind from same state do not untrack visibility actions`() {
        val oldData = UnitTestData(STATE_DIR_AUTOANIMATIONS, "new_state_empty_state.json").asDivState
        stateBinder.bindView(stateLayout, oldData, divView, rootPath())
        stateBinder.bindView(stateLayout, oldData, divView, rootPath())

        verify(divVisibilityActionTracker, never()).trackVisibilityActionsOf(any(), eq(null), any(), any())
    }

    @Test
    fun `selecting state causes untracking`() {
        stateBinder.bindView(stateLayout, divState, divView, rootPath())
        switchToState("second")
        stateBinder.bindView(stateLayout, divState, divView, rootPath())

        verify(divVisibilityActionTracker, times(1)).trackVisibilityActionsOf(any(), eq(null), any(), any())
    }

    @Test
    fun `rebind from different state untrack visibility actions`() {
        val oldData = UnitTestData(STATE_DIR_AUTOANIMATIONS, "old_state.json").asDivState
        val newData = UnitTestData(STATE_DIR_AUTOANIMATIONS, "new_state_empty_state.json").asDivState
        stateBinder.bindView(stateLayout, oldData, divView, rootPath())
        stateBinder.bindView(stateLayout, newData, divView, rootPath())

        verify(divVisibilityActionTracker, times(1)).trackVisibilityActionsOf(any(), eq(null), any(), any())
    }

    @Test
    fun `rebind from empty state inits everything`() {
        val oldData = UnitTestData(STATE_DIR_AUTOANIMATIONS, "new_state_empty_state.json").asDivState
        val newData = UnitTestData(STATE_DIR_AUTOANIMATIONS, "new_state_incompatible.json").asDivState
        stateBinder.bindView(stateLayout, oldData, divView, rootPath())

        stateBinder.bindView(stateLayout, newData, divView, rootPath())

        assertNotNull(stateLayout.swipeOutCallback)
        assertEquals(1, stateLayout.childCount)
        verify(divView).bindViewToDiv(stateLayout.getChildAt(0), newData.states[0].div!!)
    }

    private fun switchToState(stateId: String, temporaryStateId: String = stateId) {
        stateCache.putState(cardId = CARD_ID, path = "0/state_container", state = stateId)
        temporaryStateCache.putState(cardId = CARD_ID, path = "0/state_container", stateId = temporaryStateId)
    }

    private fun pathToState(stateId: String): DivStatePath {
        return rootPath().append(divId = "state_container", stateId = stateId)
    }

    private fun assertStateNotBound(path: DivStatePath) {
        verify(viewCreator, never()).create(any(), any())
        verify(viewBinder, never()).bind(any(), any(), any(), eq(path))
    }

    private fun assertStateBound(path: DivStatePath, div: Div) {
        verify(viewCreator).create(eq(div), any())
        verify(viewBinder).bind(any(), eq(div), eq(divView), eq(path))
    }

    private fun assertStateRebound(path: DivStatePath, div: Div) {
        verify(viewCreator, never()).create(eq(div), any())
        verify(viewBinder).bind(any(), eq(div), eq(divView), eq(path))
    }

    private val UnitTestData.asDivState: DivState
        get() = this.div.value() as DivState

    companion object {
        private const val STATE_DIR = "div-state"
        private const val STATE_DIR_AUTOANIMATIONS = "div-state/autoanimations"
    }
}
