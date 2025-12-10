package com.yandex.div.core.view2.state

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.path
import com.yandex.div.core.state.DivPathUtils.findDivState
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.UnitTestData
import com.yandex.div2.DivData
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
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
    private val pathCaptor = argumentCaptor<DivStatePath>()
    private val resolver = divView.expressionResolver
    private val bindingContext = BindingContext(divView, resolver)

    private val stateSwitcher = DivMultipleStateSwitcher(divView, viewBinder)

    @Test
    fun `switch to single state binds its state layout`() {
        val activeState = "0/state_container/second".path
        val div = rootDiv.findDivState(activeState, resolver)!!

        stateSwitcher.switchStates(bindingContext, divDataState, listOf(activeState))

        verify(viewBinder).bind(any(), any(), eq(div), eq(activeState.parentState()))
    }

    @Test
    fun `switch to single inner state binds its state layout`() {
        val activeState = "0/state_container/first/container_item_two/two".path
        val div = rootDiv.findDivState(activeState, resolver)!!

        stateSwitcher.switchStates(bindingContext, divDataState, listOf(activeState))

        verify(viewBinder).bind(any(), any(), eq(div), pathCaptor.capture())
        Assert.assertEquals(activeState.parentState().statesString, pathCaptor.firstValue.statesString)
    }

    @Test
    fun `switch to single state of missing state layout binds root div`() {
        val inactiveState = "0/state_container/second/second_state/hidden".path
        stateSwitcher.switchStates(bindingContext, divDataState, listOf(inactiveState))
        verify(viewBinder).bind(any(), any(), eq(rootDiv), eq(DivStatePath.fromState(divDataState)))
    }

    @Test
    fun `switch to multiple states binds corresponding state layouts`() {
        val firstPath = "0/state_container/first/container_item_one/two".path
        val secondPath = "0/state_container/first/container_item_two/two".path
        val paths = listOf(firstPath, secondPath)
        val firstDiv = rootDiv.findDivState(firstPath, resolver)!!
        val secondDiv = rootDiv.findDivState(secondPath, resolver)!!

        stateSwitcher.switchStates(bindingContext, divDataState, paths)

        verify(viewBinder).bind(any(), any(), eq(firstDiv), pathCaptor.capture())
        Assert.assertEquals(firstPath.parentState().statesString, pathCaptor.firstValue.statesString)

        verify(viewBinder).bind(any(), any(), eq(secondDiv), pathCaptor.capture())
        Assert.assertEquals(secondPath.parentState().statesString, pathCaptor.firstValue.statesString)
    }

    @Test
    fun `switch to multiple nested states binds outer state layout`() {
        val firstPath = "0/state_container/first".path
        val secondPath = "0/state_container/first/container_item_one/two".path
        val paths = listOf(firstPath, secondPath)
        val firstDiv = rootDiv.findDivState(firstPath, resolver)!!

        stateSwitcher.switchStates(bindingContext, divDataState, paths)

        verify(viewBinder).bind(any(), any(), eq(firstDiv), eq(firstPath.parentState()))
    }

    @Test
    fun `switch to conflicting states binds its state layout once`() {
        val firstPath = "0/state_container/first/container_item_one/two".path
        val secondPath = "0/state_container/first/container_item_one/three".path
        val paths = listOf(firstPath, secondPath)
        val firstDiv = rootDiv.findDivState(firstPath, resolver)!!

        stateSwitcher.switchStates(bindingContext, divDataState, paths)

        verify(viewBinder).bind(any(), any(), eq(firstDiv), pathCaptor.capture())
        Assert.assertEquals(firstPath.parentState().statesString, pathCaptor.firstValue.statesString)
    }

    @Test
    fun `switch to multiple states ignores missing state layouts`() {
        val firstPath = "0/state_container/first/container_item_one/two".path
        val secondPath = "0/state_container/second/second_state/hidden".path
        val paths = listOf(firstPath, secondPath)
        val firstDiv = rootDiv.findDivState(firstPath, resolver)!!
        val secondDiv = rootDiv.findDivState(secondPath, resolver)!!

        stateSwitcher.switchStates(bindingContext, divDataState, paths)

        verify(viewBinder).bind(any(), any(), eq(firstDiv), pathCaptor.capture())
        Assert.assertEquals(firstPath.parentState().statesString, pathCaptor.firstValue.statesString)
        verify(viewBinder, never()).bind(any(), any(), eq(secondDiv), any())
    }

    @Test
    fun `switch to multiple states of missing state layouts binds root div`() {
        val firstPath = "0/state_container/second/second_state/hidden".path
        val secondPath = "0/state_container/second/second_state/clicked".path
        val paths = listOf(firstPath, secondPath)

        stateSwitcher.switchStates(bindingContext, divDataState, paths)

        verify(viewBinder).bind(any(), any(), eq(rootDiv), eq(DivStatePath.fromState(divDataState)))
    }
}
