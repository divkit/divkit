package com.yandex.div.core.view2

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.childrenToFlatList
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.path
import com.yandex.div.core.view2.animations.DIV_STATE_DIR
import com.yandex.div.core.view2.divs.UnitTestData
import com.yandex.div.core.view2.state.DivStateSwitcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Div2ViewTest {
    private val divImageLoader = mock<DivImageLoader>()
    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val backingContext = Div2Context(
        baseContext = activity,
        configuration = DivConfiguration.Builder(divImageLoader).build()
    )
    private val stateSwitcher = mock<DivStateSwitcher>()
    private val component = TestComponent(
        wrapped = backingContext.div2Component,
        viewComponentBuilder = TestViewComponentBuilder(
            wrapped = backingContext.div2Component.viewComponent(),
            stateSwitcher = stateSwitcher
        )
    )
    private val div2Context = spy(backingContext) {
        on { div2Component } doReturn component
    }

    private val div2View = Div2View(div2Context)
    private val tag = DivDataTag("tag")

    @Test
    fun `test switch different root states`() = disableAssertions {
        val divData = UnitTestData(DIV_STATE_DIR, "state_list.json").data
        div2View.setData(divData, tag)

        div2View.switchToMultipleStates(
            listOf("0/state_container/first".path, "1/state_container/first".path),
            temporary = true,
            withAnimations = false
        )
        assertEquals(0, div2View.stateId)
    }

    @Test
    fun `test switch zero states`() = disableAssertions {
        val divData = UnitTestData(DIV_STATE_DIR, "state_list.json").data
        div2View.setData(divData, tag)

        div2View.switchToMultipleStates(
            emptyList(),
            temporary = true,
            withAnimations = false
        )
        assertEquals(0, div2View.stateId)
    }

    @Test
    fun `test switch multiple states`() {
        val divData = UnitTestData(DIV_STATE_DIR, "state_list.json").data
        div2View.setData(divData, tag)

        div2View.switchToMultipleStates(
            listOf("0/state_container/first".path, "0/state_list/second".path),
            temporary = true,
            withAnimations = false
        )

        assertEquals("first", div2View.div2Component.temporaryDivStateCache.getState(tag.id, "0/state_container"))
        assertEquals("second", div2View.div2Component.temporaryDivStateCache.getState(tag.id, "0/state_list"))
    }

    @Test
    fun `multiple states are passed to state switcher`() {
        val divData = UnitTestData(DIV_STATE_DIR, "state_list.json").data
        div2View.setData(divData, tag)

        div2View.switchToMultipleStates(
            listOf("0/state_container/first".path, "0/state_list/second".path),
            temporary = true,
            withAnimations = false
        )

        verify(stateSwitcher).switchStates(
            bindingContext = any(),
            state = any(),
            paths = eq(listOf("0/state_container/first".path, "0/state_list/second".path)),
        )
    }

    @Test
    fun `bulked states are passed to state switcher`() {
        val divData = UnitTestData(DIV_STATE_DIR, "state_list.json").data
        div2View.setData(divData, tag)

        div2View.bulkActions {
            div2View.switchToState("0/state_container/first".path, false)
            div2View.switchToState("0/state_list/second".path, false)
        }

        verify(stateSwitcher).switchStates(
            bindingContext = any(),
            state = any(),
            paths = eq(listOf("0/state_container/first".path, "0/state_list/second".path)),
        )
    }

    @Test
    fun `current state exists after set data`() {
        val divData = UnitTestData(DIV_STATE_DIR, "state_list.json").data

        assertTrue(div2View.setData(divData, tag))

        assertEquals(0, div2View.currentState!!.currentDivStateId)
    }

    @Test
    fun `current state exists after switch to state`() {
        val divData = UnitTestData(DIV_STATE_DIR, "state_list.json").data
        assertTrue(div2View.setData(divData, tag))

        div2View.switchToState(0)

        assertEquals(0, div2View.currentState!!.currentDivStateId)
    }

    @Test
    fun `views are not recreated after state switch when it is not necessary`() {
        val divData = UnitTestData(DIV_STATE_DIR, "reusable_states.json").data
        assertTrue(div2View.setData(divData, tag))

        val initialViews = div2View.childrenToFlatList()
        div2View.switchToState(1)
        val viewsAfterStateSwitch = div2View.childrenToFlatList()

        assertEquals(initialViews, viewsAfterStateSwitch)
    }
}
