package com.yandex.div.core.view2

import android.app.Activity
import com.google.common.collect.Iterators
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.state.DivPathUtils.findStateLayout
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.CONTAINER_DIR
import com.yandex.div.core.view2.divs.UnitTestData
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.viewEquals
import com.yandex.div.internal.Assert
import com.yandex.div2.DivData
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Div2RebindTest {
    private val divImageLoader = mock<DivImageLoader>()
    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val testData = UnitTestData("div-state", "state_tree.json")
    private val div2Context = Div2Context(
        activity,
        DivConfiguration.Builder(divImageLoader).build()
    )

    private val div2View = Div2View(div2Context)
    private val div2ReferenceView = Div2View(div2Context)
    private val tag = DivDataTag("tag")

    @Test
    fun `rebind to incompatible state`() {
        val oldData = UnitTestData(CONTAINER_DIR, "before_rebind.json").data
        val newData = UnitTestData(CONTAINER_DIR, "item_with_action.json").data
        div2View.setData(oldData, tag)

        div2View.setData(newData, oldData, tag)

        assertEqualsDivViews(newData)
    }

    @Test
    fun `rebind to replaceable view`() {
        val oldData = UnitTestData(CONTAINER_DIR, "before_rebind.json").data
        val newData = UnitTestData(CONTAINER_DIR, "after_rebind.json").data
        div2View.setData(oldData, tag)

        div2View.setData(newData, oldData, tag)
        div2View.layout(0, 0, 100, 100)

        assertEqualsDivViews(newData)
    }

    @Test
    fun `find view by path works correctly`() {
        val divView = Div2View(div2Context)
        divView.setData(testData.data, DivDataTag("tag"))
        divView.bindOnAttachRunnable?.onAttach()
        val path = DivStatePath.parse("0/state_container/first")

        val divStateLayout: DivStateLayout? = divView.findStateLayout(path)

        assertNotNull(divStateLayout)
        assertEquals("state_container", divStateLayout?.divState?.divId)
    }

    @Test
    fun `find view by path works correctly for deep created stateLayouts`() {
        val divView = Div2View(div2Context)
        divView.setData(testData.data, DivDataTag("tag"))
        divView.bindOnAttachRunnable?.onAttach()
        val firstStateContainers = listOf("container_item_one", "container_item_two", "container_item_three")

        firstStateContainers.forEach { containerStateId: String ->
            val path = DivStatePath.parse("0/state_container/first/$containerStateId/two")

            val divStateLayout: DivStateLayout? = divView.findStateLayout(path)

            assertNotNull(divStateLayout)
            assertEquals(containerStateId, divStateLayout?.divState?.divId)
        }
    }

    @Test
    fun `find view by path works correctly for created stateLayouts but with differ from path selected state`() {
        val divView = Div2View(div2Context)
        divView.setData(testData.data, DivDataTag("tag"))
        divView.bindOnAttachRunnable?.onAttach()
        val notActiveState = DivStatePath.parse("0/state_container/second")

        val divStateLayout: DivStateLayout? = divView.findStateLayout(notActiveState)

        // default state is 0/state_container/first/{futher_path} but state_container is created, so we still get it.
        assertNotNull(divStateLayout)
        assertEquals("state_container", divStateLayout?.divState?.divId)
    }

    @Test
    fun `find view by path fails to receive not created state`() {
        val divView = Div2View(div2Context)
        divView.setData(testData.data, DivDataTag("tag"))
        divView.bindOnAttachRunnable?.onAttach()
        val notActiveState = DivStatePath.parse("0/state_container/second/second_state/hidden")

        val divStateLayout: DivStateLayout? = divView.findStateLayout(notActiveState)

        // default state is 0/state_container/first/{futher_path}, so second_state not created yet.
        assertNull(divStateLayout)
    }

    private fun assertEqualsDivViews(newData: DivData) {
        div2ReferenceView.setData(newData, tag)
        div2ReferenceView.bindOnAttachRunnable?.onAttach()

        Assert.assertTrue(div2View.viewEquals(div2ReferenceView))
    }

    @Test
    fun `clear warnings after rebind`() {
        val oldData = UnitTestData(CONTAINER_DIR, "horizontal_wrap_content_width_match_parent_item.json").data
        val newData = UnitTestData(CONTAINER_DIR, "horizontal_wrap_content_width_wrap_content_constrained_item.json").data
        val errorCollectors = div2View.viewComponent.errorCollectors
        
        div2View.setData(oldData, tag)
        assertEquals(1, Iterators.size(errorCollectors.getOrCreate(tag, oldData).getWarnings()))

        div2View.setData(newData, oldData, tag)
        assertEquals(0, Iterators.size(errorCollectors.getOrCreate(tag, newData).getWarnings()))
    }
}
