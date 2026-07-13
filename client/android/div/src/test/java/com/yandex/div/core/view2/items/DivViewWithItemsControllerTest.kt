package com.yandex.div.core.view2.items

import android.app.Activity
import android.content.res.Resources
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class DivViewWithItemsControllerTest {

    private val divItemsView = mock<DivViewWithItems> {
        on { getNearestItem(any()) } doReturn NEAREST_ITEM
        on { itemCount } doReturn ITEM_COUNT
        on { metrics } doReturn mock()
    }
    private val underTest = DivViewWithItemsController(divItemsView)

    @Test
    fun `create controller when view is found`() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val context = Div2Context(activity, DivConfiguration.Builder(mock()).build())
        val res = mock<Resources> {
            on { displayMetrics } doReturn mock()
        }
        val targetView = mock<DivRecyclerView> {
            on { tag } doReturn "id"
            on { resources } doReturn res
        }
        val divView = Div2View(context).apply { addView(targetView) }

        val controller = DivViewWithItemsController.create("id", null, divView, "")

        Assert.assertNotNull(controller)
    }

    @Test
    fun `not create controller when view is not found`() {
        val divView = mock<Div2View>()
        val controller = DivViewWithItemsController.create("id", null, divView, "")
        Assert.assertNull(controller)
    }

    @Test
    fun `handle set current item`() {
        underTest.setCurrentItem(3, true)
        verify(divItemsView).setCurrentItem(3, true)
    }

    @Test
    fun `handle set next item`() {
        underTest.changeCurrentItemByStep(null, 1, true)
        verify(divItemsView).setCurrentItem(NEAREST_ITEM, true)
    }

    @Test
    fun `handle set previous item`() {
        underTest.changeCurrentItemByStep(null, -1, true)
        verify(divItemsView).setCurrentItem(NEAREST_ITEM, true)
    }

    @Test
    fun `handle set next item with overflow ring`() {
        whenever(divItemsView.itemCount).thenReturn(NEAREST_ITEM)
        underTest.changeCurrentItemByStep("ring", 1, true)
        verify(divItemsView).setCurrentItem(0, true)
    }

    @Test
    fun `handle set previous item with overflow ring`() {
        whenever(divItemsView.getNearestItem(any())).thenReturn(-1)
        underTest.changeCurrentItemByStep("ring", -1, true)
        verify(divItemsView).setCurrentItem(ITEM_COUNT - 1, true)
    }

    companion object {
        const val NEAREST_ITEM = 2
        const val ITEM_COUNT = 4
    }
}
