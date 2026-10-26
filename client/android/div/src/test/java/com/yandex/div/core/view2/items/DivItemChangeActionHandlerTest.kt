package com.yandex.div.core.view2.items

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.util.EnableAssertsRule
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@Config(shadows = [DivItemChangeActionHandlerTest.ShadowDivViewWithItemsControllerCompanion::class])
class DivItemChangeActionHandlerTest {

    private val view = mock<DivViewFacade>()

    @get:Rule
    val disableAsserts = EnableAssertsRule(enable = false)

    @BeforeTest
    fun `setup mock`() {
        controller = mock<DivViewWithItemsController>()
    }

    @AfterTest
    fun `cleanup mock`() {
        controller = null
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

    @Test
    fun `scroll to start preserves animation flag`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://scroll_to_start?id=$ID&animated=false"), SCOPE_ID, view,
        )

        assertTrue(result)
        verify(controller)?.scrollToStart(false)
    }

    @Test
    fun `scroll to end is animated by default`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://scroll_to_end?id=$ID"), SCOPE_ID, view,
        )

        assertTrue(result)
        verify(controller)?.scrollToEnd(true)
    }

    @Test
    fun `scroll to item id decodes URL parameter`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://scroll_to_item_id?id=$ID&item_id=second%20item&animated=false"), SCOPE_ID, view,
        )

        assertTrue(result)
        verify(controller)?.scrollToItemId("second item", false)
    }

    @Test
    fun `scroll to position preserves offset and animation`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://scroll_to_position?id=$ID&step=30&animated=false"),
            SCOPE_ID,
            view,
        )

        assertTrue(result)
        verify(requireNotNull(controller)).scrollTo(offset = 30, animated = false)
    }

    @Test
    fun `scroll to position defaults to one dp`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://scroll_to_position?id=$ID"), SCOPE_ID, view,
        )

        assertTrue(result)
        verify(requireNotNull(controller)).scrollTo(offset = 1, animated = true)
    }

    @Test
    fun `scroll to position uses default when step is malformed`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://scroll_to_position?id=$ID&step=invalid"), SCOPE_ID, view,
        )

        assertTrue(result)
        verify(requireNotNull(controller)).scrollTo(offset = 1, animated = true)
    }

    @Test
    fun `not handled when item id is missing`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://scroll_to_item_id?id=$ID"), SCOPE_ID, view,
        )

        assertFalse(result)
    }

    @Test
    fun `not handled when item index overflows Int`() {
        val result = DivItemChangeActionHandler.handleAction(
            Uri.parse("div-action://set_current_item?id=$ID&item=4294967297"), SCOPE_ID, view,
        )

        assertFalse(result)
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
