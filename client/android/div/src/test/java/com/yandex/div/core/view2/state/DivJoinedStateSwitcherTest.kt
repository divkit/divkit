package com.yandex.div.core.view2.state

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.path
import com.yandex.div.core.state.DivPathUtils.findDivState
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.UnitTestData
import com.yandex.div2.DivData
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivJoinedStateSwitcherTest {

    private val testData = UnitTestData("div-state", "state_tree.json")
    private val rootDiv = testData.div
    private val divDataState = DivData.State(rootDiv, 0)

    private val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
    private val viewBinder = mock<DivBinder>()
    private val div2Context = Div2Context(
        baseContext = activity,
        configuration = DivConfiguration.Builder(mock(), mock()).build()
    )
    private val div2View = Div2View(div2Context).apply {
        setData(
            DivData(logId = "id", states = listOf(divDataState)),
            DivDataTag("tag")
        )
        bindOnAttachRunnable?.onAttach()
    }
    private val resolver = div2View.expressionResolver

    private val stateSwitcher = DivJoinedStateSwitcher(div2View, viewBinder)

    @Test
    fun `change to not active state bind first active div`() {
        // default state is 0/state_container/first/container_item_one/one
        val notActiveState = "0/state_container/second/second_state/hidden".path
        val div = rootDiv.findDivState(notActiveState.parentState(), resolver)!!

        stateSwitcher.switchStates(divDataState, listOf(notActiveState), resolver)

        verify(viewBinder).bind(any(), eq(div), any(), eq(DivStatePath.fromState(0)))
    }

    @Test
    fun `change to active state bind state_container div`() {
        val activeState = "0/state_container/first/container_item_one/two".path
        val div = rootDiv.findDivState(activeState, resolver)!!

        stateSwitcher.switchStates(divDataState, listOf(activeState), resolver)

        verify(viewBinder).bind(any(), eq(div), any(), eq(activeState.parentState()))
    }

    @Test
    fun `change to deep not active state bind deep active state div`() {
        val activeState = "0/state_container/first/container_item_two/two".path
        val div = rootDiv.findDivState(activeState, resolver)!!

        stateSwitcher.switchStates(divDataState, listOf(activeState), resolver)

        verify(viewBinder).bind(any(), eq(div), any(), eq(activeState.parentState()))
    }

    @Test
    fun `change to double active states bind lowestCommonAncestor div`() {
        val firstPath = "0/state_container/first/container_item_one/two".path
        val secondPath = "0/state_container/first/container_item_two/three".path
        val paths = listOf(firstPath, secondPath)
        val commonPath = DivStatePath.lowestCommonAncestor(firstPath, secondPath)!!
        val div = rootDiv.findDivState(commonPath, resolver)!!

        stateSwitcher.switchStates(divDataState, paths, resolver)

        verify(viewBinder).bind(any(), eq(div), any(), eq(commonPath.parentState()))
    }

    @Test
    fun `change to multiple active states bind lowestCommonAncestor div`() {
        val firstPath = "0/state_container/first/container_item_one/two".path
        val secondPath = "0/state_container/first/container_item_two/three".path
        val thirdPath = "0/state_container/first/container_item_three/one".path
        val paths = listOf(firstPath, secondPath, thirdPath)
        val commonPath = paths.reduce { acc, path -> DivStatePath.lowestCommonAncestor(acc, path)!! }
        val div = rootDiv.findDivState(commonPath, resolver)!!

        stateSwitcher.switchStates(divDataState, paths, resolver)

        verify(viewBinder).bind(any(), eq(div), any(), eq(commonPath.parentState()))
    }

    @Test
    fun `change both to active and not active states bind root div`() {
        val firstPath = "0/state_container/first/container_item_one/two".path
        val secondPath = "0/state_container/second/second_state/hidden".path
        val paths = listOf(firstPath, secondPath)
        val commonPath = DivStatePath.fromState(0)

        stateSwitcher.switchStates(divDataState, paths, resolver)

        verify(viewBinder).bind(any(), eq(rootDiv), any(), eq(commonPath))
    }
}
