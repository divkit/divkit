package com.yandex.div.core.view2.divs

import android.view.View
import com.yandex.div.core.childrenToFlatList
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.downloader.DivPatchManager
import com.yandex.div.core.expression.variables.TwoWayStringVariableBinder
import com.yandex.div.core.state.DivPathUtils.findStateLayout
import com.yandex.div.core.state.DivStateManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

private const val STATE_DIR = "div-state"

@RunWith(RobolectricTestRunner::class)
class DivStateBinderReleaseViewTest: DivBinderTest() {

    private val divOne = UnitTestData(STATE_DIR, "state_tree.json")
    private val divTwo = UnitTestData(STATE_DIR, "state_list.json")

    private val viewBinder = mock<DivBinder>()
    private val stateManager = mock<DivStateManager>()
    private val divVisibilityActionTracker = mock<DivVisibilityActionTracker>()
    private val errorCollectors = mock<ErrorCollectors>()
    private val divPatchManager = mock<DivPatchManager>()
    private val divPatchCache = mock<DivPatchCache>()
    private val variableBinder = mock<TwoWayStringVariableBinder>()

    private val stateLayout = (viewCreator.create(divOne.div, ExpressionResolver.EMPTY) as DivStateLayout).apply {
        layoutParams = defaultLayoutParams()
    }
    private val rootPath = DivStatePath.fromRootDiv(0, divOne.div)

    private val stateBinder = DivStateBinder(
        baseBinder = baseBinder,
        viewCreator = viewCreator,
        viewBinder = { viewBinder },
        stateManager = stateManager,
        actionPerformer = actionPerformer,
        divPatchManager = divPatchManager,
        divPatchCache = divPatchCache,
        divVisibilityActionTracker = divVisibilityActionTracker,
        errorCollectors = errorCollectors,
        variableBinder = variableBinder,
        runtimeVisitor = mock()
    )

    @Test
    fun `initial bind do not call release`() {
        stateBinder.bindView(bindingContext, stateLayout, divOne.asDivState, rootPath)

        verify(visitor, never()).release(any())
    }

    @Test
    fun `rebind do call release on old views`() {
        stateBinder.bindView(bindingContext, stateLayout, divOne.asDivState, rootPath)

        val allChildren = stateLayout.childrenToFlatList()

        stateBinder.bindView(bindingContext, stateLayout, divTwo.asDivState, rootPath)

        verifyAllChildrenReleased(allChildren)
    }

    @Test
    fun `change state release old views`() {
        stateBinder.bindView(bindingContext, stateLayout, divOne.asDivState, rootPath)
        whenever(stateManager.getState(divOne.asDivState.value, divView, resolver, path = "0/state_container"))
            .thenReturn("second")
        val stateToBeSwitched: DivStateLayout = stateLayout
            .findStateLayout(DivStatePath.parse("0/state_container/first"))
            ?: throw AssertionError("failed to find state")
        val allChildren: List<View> = stateToBeSwitched.childrenToFlatList()

        stateBinder.bindView(bindingContext, stateLayout, divOne.asDivState, rootPath)

        verifyAllChildrenReleased(allChildren)
    }

    private fun verifyAllChildrenReleased(allChildren: List<View>) {
        Assert.assertTrue(allChildren.isNotEmpty())
        allChildren.forEach { view: View ->
            verify(visitor).release(view)
        }
        verify(visitor, times(allChildren.size)).release(any())
    }
}

private val UnitTestData.asDivState get() = div as Div.State
