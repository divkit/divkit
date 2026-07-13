package com.yandex.div.core.view2.items

import android.net.Uri
import com.yandex.div.core.DivViewFacade
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

private typealias DivKitAssert = com.yandex.div.internal.Assert

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [DivItemChangeActionHandlerTest.ShadowDivViewWithItemsControllerCompanion::class])
class DivItemChangeActionHandlerTest {

    private val view = mock<DivViewFacade>()

    @Before
    fun `setup mock`() {
        DivKitAssert.isEnabled = false
        controller = mock<DivViewWithItemsController>()
    }

    @After
    fun `cleanup mock`() {
        DivKitAssert.isEnabled = true
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
            SCOPE_ID,
            view,
        )

        assertTrue(result)
        verify(controller)?.setCurrentItem(3, true)
    }

    @Test
    fun `handle set next item`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_next_item?id=$ID"),
            SCOPE_ID,
            view,
        )

        assertTrue(result)
        verify(controller)?.changeCurrentItemByStep(null, 1, true)
    }

    @Test
    fun `handle set previous item`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_previous_item?id=$ID"),
            SCOPE_ID,
            view,
        )

        assertTrue(result)
        verify(controller)?.changeCurrentItemByStep(null, -1, true)
    }

    @Test
    fun `not handled when id param missing`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_current_item"),
            SCOPE_ID,
            view,
        )

        assertFalse(result)
    }

    @Test
    fun `not handled when div items view not created`() {
        controller = null
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_next_item?id=$ID"),
            SCOPE_ID,
            view,
        )

        assertFalse(result)
    }

    @Test
    fun `not handled when item param missing`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_current_item?id=$ID"),
            SCOPE_ID,
            view,
        )

        assertFalse(result)
    }

    @Test
    fun `not handled when item param not a number`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_current_item?id=$ID&item=bar"),
            SCOPE_ID,
            view,
        )

        assertFalse(result)
    }

    @Test
    fun `handle set next item with overflow ring`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_next_item?id=$ID&overflow=ring"),
            SCOPE_ID,
            view,
        )

        assertTrue(result)
        verify(controller)?.changeCurrentItemByStep("ring", 1, true)
    }

    @Test
    fun `handle set previous item with overflow ring`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_previous_item?id=$ID&overflow=ring"),
            SCOPE_ID,
            view,
        )

        assertTrue(result)
        verify(controller)?.changeCurrentItemByStep("ring", -1, true)
    }

    @Implements(DivViewWithItemsController.Companion::class)
    internal class ShadowDivViewWithItemsControllerCompanion {

        @Implementation
        @Suppress("unused")
        fun create(
            id: String,
            scopeId: String?,
            view: DivViewFacade,
            actionType: String,
        ): DivViewWithItemsController? = controller
    }

    private companion object {
        const val ID = "foo"
        const val SCOPE_ID = "scope"

        var controller: DivViewWithItemsController? = null
    }
}
