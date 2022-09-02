package com.yandex.div.core.view2.state

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.asExpression
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
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * State switcher that performs binding for each individual state path when it is possible.
 */
@RunWith(RobolectricTestRunner::class)
class DivMultipleStateSwitcherTest {

    private val testData = UnitTestData("div-state", "state_tree.json")
    private val rootDiv = testData.div
    private val divDataState = DivData.State(rootDiv, 0)

    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val div2Context = Div2Context(
        baseContext = activity,
        configuration = DivConfiguration.Builder(mock())
            .build()
    )
    private val divView = Div2View(div2Context).apply {
        setData(
            DivData(logId = "id", states = listOf(divDataState)),
            DivDataTag("tag")
        )
        bindOnAttachRunnable?.onAttach()
    }

    private val viewBinder = mock<DivBinder>()

    private val stateSwitcher = DivMultipleStateSwitcher(divView, viewBinder)

    @Test
    fun `switch to single state binds its state layout`() {
        val activeState = "0/state_container/second".path
        val div = rootDiv.findDivState(activeState)!!

        stateSwitcher.switchStates(divDataState, listOf(activeState))

        verify(viewBinder).bind(any(), eq(div), any(), eq(activeState.parentState()))
    }

    @Test
    fun `switch to single inner state binds its state layout`() {
        val activeState = "0/state_container/first/container_item_two/two".path
        val div = rootDiv.findDivState(activeState)!!

        stateSwitcher.switchStates(divDataState, listOf(activeState))

        verify(viewBinder).bind(any(), eq(div), any(), eq(activeState.parentState()))
    }

    @Test
    fun `switch to single state of missing state layout binds root div`() {
        val inactiveState = "0/state_container/second/second_state/hidden".path
        val div = rootDiv.findDivState(inactiveState.parentState())!!

        stateSwitcher.switchStates(divDataState, listOf(inactiveState))

        verify(viewBinder).bind(any(), eq(rootDiv), any(), eq(DivStatePath.fromState(0)))
    }

    @Test
    fun `switch to multiple states binds corresponding state layouts`() {
        val firstPath = "0/state_container/first/container_item_one/two".path
        val secondPath = "0/state_container/first/container_item_two/two".path
        val paths = listOf(firstPath, secondPath)
        val firstDiv = rootDiv.findDivState(firstPath)!!
        val secondDiv = rootDiv.findDivState(secondPath)!!

        stateSwitcher.switchStates(divDataState, paths)

        verify(viewBinder).bind(any(), eq(firstDiv), any(), eq(firstPath.parentState()))
        verify(viewBinder).bind(any(), eq(secondDiv), any(), eq(secondPath.parentState()))
    }

    @Test
    fun `switch to multiple nested states binds outer state layout`() {
        val firstPath = "0/state_container/first".path
        val secondPath = "0/state_container/first/container_item_one/two".path
        val paths = listOf(firstPath, secondPath)
        val firstDiv = rootDiv.findDivState(firstPath)!!

        stateSwitcher.switchStates(divDataState, paths)

        verify(viewBinder).bind(any(), eq(firstDiv), any(), eq(firstPath.parentState()))
    }

    @Test
    fun `switch to conflicting states binds its state layout once`() {
        val firstPath = "0/state_container/first/container_item_one/two".path
        val secondPath = "0/state_container/first/container_item_one/three".path
        val paths = listOf(firstPath, secondPath)
        val firstDiv = rootDiv.findDivState(firstPath)!!

        stateSwitcher.switchStates(divDataState, paths)

        verify(viewBinder).bind(any(), eq(firstDiv), any(), eq(firstPath.parentState()))
    }

    @Test
    fun `switch to multiple states ignores missing state layouts`() {
        val firstPath = "0/state_container/first/container_item_one/two".path
        val secondPath = "0/state_container/second/second_state/hidden".path
        val paths = listOf(firstPath, secondPath)
        val firstDiv = rootDiv.findDivState(firstPath)!!
        val secondDiv = rootDiv.findDivState(secondPath)!!

        stateSwitcher.switchStates(divDataState, paths)

        verify(viewBinder).bind(any(), eq(firstDiv), any(), eq(firstPath.parentState()))
        verify(viewBinder, never()).bind(any(), eq(secondDiv), any(), any())
    }

    @Test
    fun `switch to multiple states of missing state layouts binds root div`() {
        val firstPath = "0/state_container/second/second_state/hidden".path
        val secondPath = "0/state_container/second/second_state/clicked".path
        val paths = listOf(firstPath, secondPath)

        stateSwitcher.switchStates(divDataState, paths)

        verify(viewBinder).bind(any(), eq(rootDiv), any(), eq(DivStatePath.fromState(0)))
    }
}
