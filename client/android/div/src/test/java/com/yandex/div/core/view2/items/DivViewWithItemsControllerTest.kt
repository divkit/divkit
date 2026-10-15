package com.yandex.div.core.view2.items

import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.test.testContextThemeWrapper
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
internal class DivViewWithItemsControllerTest {

    private val divItemsView = mock<DivViewWithItems.Gallery> {
        on { getNearestItem(any()) } doReturn NEAREST_ITEM
        on { itemCount } doReturn ITEM_COUNT
        on { metrics } doReturn mock()
    }
    private val underTest = DivViewWithItemsController(divItemsView, animationsEnabled = true)

    @Test
    fun `create controller when view is found`() {
        val context = Div2Context(testContextThemeWrapper(), DivConfiguration.Builder(mock()).build())
        val res = mock<Resources> {
            on { displayMetrics } doReturn mock()
        }
        val targetView = mock<DivRecyclerView> {
            on { tag } doReturn "id"
            on { resources } doReturn res
        }
        val divView = Div2View(context).apply { addView(targetView) }

        val controller = DivViewWithItemsController.create("id", null, divView, "")

        assertNotNull(controller)
    }

    @Test
    fun `not create controller when view is not found`() {
        val divView = mock<Div2View>()
        val controller = DivViewWithItemsController.create("id", null, divView, "")
        assertNull(controller)
    }

    @Test
    fun `handle set current item`() {
        underTest.setCurrentItem(3, true)
        verify(divItemsView).setCurrentItem(3, true)
    }

    @Test
    fun `set current item without animation when animations disabled`() {
        val controller = DivViewWithItemsController(divItemsView, animationsEnabled = false)
        controller.setCurrentItem(3, true)
        verify(divItemsView).setCurrentItem(3, false)
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

    @Test
    fun `ring offset stays at zero when view has no scroll range`() {
        whenever(divItemsView.metrics).thenReturn(DisplayMetrics().apply { density = 2f })

        underTest.scrollByOffset("ring", 54, true)

        verify(divItemsView).scrollTo(0, true)
    }

    companion object {
        const val NEAREST_ITEM = 2
        const val ITEM_COUNT = 4
    }
}
