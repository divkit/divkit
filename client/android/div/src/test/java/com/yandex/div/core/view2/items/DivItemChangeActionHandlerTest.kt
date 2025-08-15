package com.yandex.div.core.view2.items

import android.net.Uri
import android.view.View
import com.yandex.div.core.DivViewFacade
import com.yandex.div.json.expressions.ExpressionResolver
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

private typealias DivKitAssert = com.yandex.div.internal.Assert

@RunWith(RobolectricTestRunner::class)
class DivItemChangeActionHandlerTest {

    private val targetView = mock<View> {
        on { findViewWithTag<View>(ID) } doReturn mock
    }
    val resolver = mock<ExpressionResolver>()
    private val view = mock<DivViewFacade> {
        on { view } doReturn targetView
        on { expressionResolver } doReturn mock()
    }
    private val divItemsView = mock<DivViewWithItems> {
        on { currentItem } doReturn CURRENT_ITEM
        on { itemCount } doReturn ITEM_COUNT
        on { metrics } doReturn mock()
    }

    @Before
    fun `setup mock`() {
        DivKitAssert.setEnabled(false)
        DivViewWithItems.viewForTests = divItemsView
    }

    @After
    fun `cleanup mock`() {
        DivViewWithItems.viewForTests = null
        DivKitAssert.setEnabled(true)
    }

    @Test
    fun `can handle`() {
        assertTrue(DivItemChangeActionHandler.canHandle("set_current_item"))
        assertTrue(DivItemChangeActionHandler.canHandle("set_next_item"))
        assertTrue(DivItemChangeActionHandler.canHandle("set_previous_item"))
    }

    @Test
    fun `cannot handle`() {
        assertFalse(DivItemChangeActionHandler.canHandle("set_item"))
    }

    @Test
    fun `handle set current item`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_current_item?id=$ID&item=3"),
            view,
            resolver,
        )

        assertTrue(result)
        verify(divItemsView).currentItem = 3
    }

    @Test
    fun `handle set next item`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_next_item?id=$ID"),
            view,
            resolver,
        )

        assertTrue(result)
        verify(divItemsView).currentItem = CURRENT_ITEM + 1
    }

    @Test
    fun `handle set previous item`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_previous_item?id=$ID"),
            view,
            resolver,
        )

        assertTrue(result)
        verify(divItemsView).currentItem = CURRENT_ITEM - 1
    }

    @Test
    fun `not handled when id param missing`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_current_item"),
            view,
            resolver,
        )

        assertFalse(result)
    }

    @Test
    fun `not handled when view with id not found`() {
        whenever(targetView.findViewWithTag<View>(ID)).thenReturn(null)

        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_next_item?id=$ID"),
            view,
            resolver,
        )

        assertFalse(result)
    }

    @Test
    fun `not handled when div items view not created`() {
        DivViewWithItems.viewForTests = null

        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_next_item?id=$ID"),
            view,
            resolver,
        )

        assertFalse(result)
    }

    @Test
    fun `not handled when item param missing`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_current_item?id=$ID"),
            view,
            resolver,
        )

        assertFalse(result)
    }

    @Test
    fun `not handled when item param not a number`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_current_item?id=$ID&item=bar"),
            view,
            resolver,
        )

        assertFalse(result)
    }

    @Test
    fun `handle set next item with overflow ring`() {
        whenever(divItemsView.itemCount).thenReturn(CURRENT_ITEM + 1)

        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_next_item?id=$ID&overflow=ring"), view, resolver
        )

        assertTrue(result)
        verify(divItemsView).currentItem = 0
    }

    @Test
    fun `handle set previous item with overflow ring`() {
        whenever(divItemsView.currentItem).thenReturn(0)

        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_previous_item?id=$ID&overflow=ring"),
            view,
            resolver,
        )

        assertTrue(result)
        verify(divItemsView).currentItem = ITEM_COUNT - 1
    }

    private companion object {
        private const val ID = "foo"
        private const val CURRENT_ITEM = 2
        private const val ITEM_COUNT = 4
    }
}
