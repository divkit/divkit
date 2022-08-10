package com.yandex.div.core.view2

import android.app.Activity
import android.view.View
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.TestComponent
import com.yandex.div.core.TestViewComponentBuilder
import com.yandex.div.core.childrenToFlatList
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.Assert
import com.yandex.div.core.view2.animations.DIV_STATE_DIR
import com.yandex.div.core.view2.divs.UnitTestData
import com.yandex.div.core.view2.divs.widgets.ReleaseViewVisitor
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Div2ViewReleaseChildrenTest {
    private val testData = UnitTestData(DIV_STATE_DIR, "state_tree.json")
    private val testOtherData = UnitTestData(DIV_STATE_DIR, "state_list.json")
    private val tag = DivDataTag("tag")

    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val backingContext = Div2Context(
        baseContext = activity,
        configuration = DivConfiguration.Builder(mock()).build()
    )
    private val viewBinder = mock<DivBinder>()
    private val divExtensionController = DivExtensionController(emptyList())
    private val releaseViewVisitor = spy(ReleaseViewVisitor(mock(), null, divExtensionController))
    private val component: TestComponent = TestComponent(
        wrapped = backingContext.div2Component,
        divBinder = viewBinder,
        viewComponentBuilder = TestViewComponentBuilder(
            wrapped = backingContext.div2Component.viewComponent(),
            releaseViewVisitor = releaseViewVisitor
        )
    )
    private val div2Context = spy(backingContext) {
        on { div2Component } doReturn component
    }
    private val divView = Div2View(div2Context).apply {
        divData = testData.data
    }

    @Test
    fun `set data not release anything`() {
        divView.setData(testOtherData.data, tag)

        // cleanup called, but there's no children
        verify(releaseViewVisitor, never()).release(any())
    }

    @Test
    fun `force switch state releases children`() {
        val state = DivStatePath.parse("0/state_container/first/container_item_one/two")

        divView.switchToState(state, false)

        verify(releaseViewVisitor, never()).release(any())

        val children: List<View> = divView.childrenToFlatList()
        divView.switchToState(0)

        assertChildrenReleased(children)
    }

    @Test
    fun `cleanup release children`() {
        val state = DivStatePath.parse("0/state_container/first/container_item_one/two")
        divView.switchToState(state, false)
        val children: List<View> = divView.childrenToFlatList()

        divView.cleanup()

        assertChildrenReleased(children)
    }

    private fun assertChildrenReleased(children: List<View>) {
        Assert.assertTrue(children.isNotEmpty())
        verify(releaseViewVisitor, times(children.size)).release(any())
        children.forEach { view: View ->
            verify(releaseViewVisitor).release(view)
        }
    }
}
