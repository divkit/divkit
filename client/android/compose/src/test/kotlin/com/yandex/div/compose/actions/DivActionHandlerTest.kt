package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.actionData
import com.yandex.div.compose.scroll.ScrollTarget
import com.yandex.div.compose.scroll.ScrollTargetStorage
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.data.Variable
import com.yandex.div.internal.actions.DivUntypedAction
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.customAction
import com.yandex.div.test.data.disappearAction
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.scrollByAction
import com.yandex.div.test.data.scrollToAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityAction
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionScrollBy
import com.yandex.div2.DivActionScrollDestination
import com.yandex.div2.IndexDestination
import com.yandex.div2.OffsetDestination
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.json.JSONObject
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class DivActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()
    private val externalActionHandler = TestExternalActionHandler()
    private val setStateActionHandler = mock<SetStateActionHandler>()
    private val animationsEnabledProvider = mock<DivAnimationsEnabledProvider> {
        on { isAnimationsEnabled() } doReturn true
    }
    private val scrollTo = mock<(Int, Boolean, Boolean, Int?) -> Unit>()
    private val scrollToOffset = mock<(Int, Boolean, Int?) -> Unit>()
    private val scrollBy = mock<(Int, Int, DivActionScrollBy.Overflow, Boolean, Int?) -> Unit>()
    private val scrollTarget = ScrollTarget(
        items = listOf(text(text = "First"), text(id = "second item", text = "Second")),
        scrollTo = scrollTo,
        scrollToOffset = scrollToOffset,
        scrollBy = scrollBy,
    )
    private val targetStorage = ScrollTargetStorage()
    private val scrollActionHandler = ScrollActionHandler(
        animationsEnabledProvider = animationsEnabledProvider,
        reporter = actionHandlerEnvironment.reporter,
        targetStorage = targetStorage,
    )

    @BeforeTest
    fun setUp() {
        actionHandlerEnvironment.init(
            externalActionHandler = externalActionHandler,
            scrollActionHandler = scrollActionHandler,
            setStateActionHandler = setStateActionHandler,
        )
        targetStorage.register("gallery", scrollTarget)
    }

    @Test
    fun `not enabled action is not handled`() {
        handle(
            action(
                isEnabled = false,
                url = "custom://url"
            )
        )

        assertEquals(emptyList(), externalActionHandler.handledActions)
    }

    @Test
    fun `empty action is passed to the external action handler`() {
        handle(
            action(id = "test"),
            source = DivActionSource.TAP
        )

        assertEquals(
            actionData(
                id = "test",
                source = DivActionSource.TAP
            ),
            externalActionHandler.handledAction
        )
    }

    @Test
    fun `unhandled action is passed to the external action handler`() {
        val payload = JSONObject(mapOf("key" to "value"))

        handle(
            action(
                id = "test",
                payload = payload,
                url = "custom://url"
            ),
            source = DivActionSource.TAP
        )

        assertEquals(
            actionData(
                id = "test",
                payload = payload,
                source = DivActionSource.TAP,
                url = "custom://url"
            ),
            externalActionHandler.handledAction
        )
    }

    @Test
    fun `custom action is passed to the external action handler`() {
        val payload = JSONObject(mapOf("key" to "value"))

        handle(
            action(
                id = "test",
                payload = payload,
                typed = customAction()
            ),
            source = DivActionSource.TRIGGER
        )

        assertEquals(
            DivCustomActionData(
                id = "test",
                payload = payload,
                source = DivActionSource.TRIGGER
            ),
            externalActionHandler.handledCustomAction
        )
    }

    @Test
    fun `unhandled visibility action is passed to the external action handler`() {
        actionHandlerEnvironment.handle(
            visibilityAction(
                id = "test",
                url = "custom://url"
            )
        )

        assertEquals(
            actionData(
                id = "test",
                source = DivActionSource.VISIBILITY,
                url = "custom://url"
            ),
            externalActionHandler.handledAction
        )
    }

    @Test
    fun `unhandled disappear action is passed to the external action handler`() {
        actionHandlerEnvironment.handle(
            disappearAction(
                id = "test",
                url = "custom://url"
            )
        )

        assertEquals(
            actionData(
                id = "test",
                source = DivActionSource.DISAPPEAR,
                url = "custom://url"
            ),
            externalActionHandler.handledAction
        )
    }

    @Test
    fun `set_current_item scrolls to index with animation by default`() {
        handle(action(url = "div-action://set_current_item?id=gallery&item=1"))

        verify(scrollTo).invoke(1, true, false, null)
    }

    @Test
    fun `set_next_item action`() {
        handle(action(url = "div-action://set_next_item?id=gallery"))

        verify(scrollBy).invoke(1, 0, DivActionScrollBy.Overflow.CLAMP, true, null)
    }

    @Test
    fun `non div-action URI is not converted`() {
        handle(action(url = "custom://set_next_item?id=gallery"))

        assertEquals(
            actionData(
                source = DivActionSource.EXTERNAL,
                url = "custom://set_next_item?id=gallery"
            ),
            externalActionHandler.handledAction
        )
        verifyNoInteractions(scrollTo, scrollToOffset, scrollBy)
    }

    @Test
    fun `non scroll action is not converted`() {
        handle(action(url = "div-action://set_state?state_id=state1"))

        verify(setStateActionHandler).handle(DivUntypedAction.SetState("state1", true))
        verifyNoInteractions(scrollTo, scrollToOffset, scrollBy)
    }

    @Test
    fun `scroll_to_item_id decodes the item id`() {
        handle(action(url = "div-action://scroll_to_item_id?id=gallery&item_id=second%20item&animated=false"))

        verify(scrollTo).invoke(1, false, false, null)
    }

    @Test
    fun `scroll_to_start scrolls to the first item`() {
        handle(action(url = "div-action://scroll_to_start?id=gallery&animated=false"))

        verify(scrollTo).invoke(0, false, false, null)
    }

    @Test
    fun `scroll_to_end scrolls to the content end`() {
        handle(action(url = "div-action://scroll_to_end?id=gallery&animated=false"))

        verify(scrollTo).invoke(1, false, true, null)
    }

    @Test
    fun `URL animation flag follows View boolean parsing`() {
        handle(action(url = "div-action://set_current_item?id=gallery&item=1&animated=1"))

        verify(scrollTo).invoke(1, false, false, null)
    }

    @Test
    fun `missing gallery id is reported`() {
        actionHandlerEnvironment.reporter.failOnError = false

        handle(action(url = "div-action://scroll_to_end"))

        assertEquals("id param is required to set item", actionHandlerEnvironment.reporter.lastError)
        verifyNoInteractions(scrollTo)
    }

    @Test
    fun `missing item index is reported`() {
        actionHandlerEnvironment.reporter.failOnError = false

        handle(action(url = "div-action://set_current_item?id=gallery"))

        assertEquals("item is required to set current item", actionHandlerEnvironment.reporter.lastError)
        verifyNoInteractions(scrollTo)
    }

    @Test
    fun `malformed item index is reported`() {
        actionHandlerEnvironment.reporter.failOnError = false

        handle(action(url = "div-action://set_current_item?id=gallery&item=invalid"))

        assertEquals("invalid is not a number", actionHandlerEnvironment.reporter.lastError)
        verifyNoInteractions(scrollTo)
    }

    @Test
    fun `item index outside Int range is rejected without wrapping`() {
        actionHandlerEnvironment.reporter.failOnError = false

        handle(action(url = "div-action://set_current_item?id=gallery&item=4294967297"))

        assertEquals("4294967297 is not a number", actionHandlerEnvironment.reporter.lastError)
        verifyNoInteractions(scrollTo)
    }

    @Test
    fun `missing item id is reported`() {
        actionHandlerEnvironment.reporter.failOnError = false

        handle(action(url = "div-action://scroll_to_item_id?id=gallery"))

        assertEquals("item_id is required to scroll to item with id", actionHandlerEnvironment.reporter.lastError)
        verifyNoInteractions(scrollTo)
    }

    @Test
    fun `URL scroll respects disabled host animations`() {
        whenever(animationsEnabledProvider.isAnimationsEnabled()).thenReturn(false)

        handle(action(url = "div-action://set_current_item?id=gallery&item=1"))

        verify(scrollTo).invoke(1, false, false, null)
    }

    @Test
    fun `disabled URL action does not scroll`() {
        handle(action(isEnabled = false, url = "div-action://scroll_to_end?id=gallery"))

        verifyNoInteractions(scrollTo)
    }

    @Test
    fun `typed action takes precedence over scroll URL`() {
        handle(action(typed = customAction(), url = "div-action://scroll_to_end?id=gallery"))

        verifyNoInteractions(scrollTo)
    }

    @Test
    fun `next item URL scrolls one item by default`() {
        handle(action(url = "div-action://set_next_item?id=gallery&animated=false"))

        verify(scrollBy).invoke(1, 0, DivActionScrollBy.Overflow.CLAMP, false, null)
    }

    @Test
    fun `previous item URL reverses step and preserves overflow`() {
        handle(action(url = "div-action://set_previous_item?id=gallery&step=3&overflow=ring"))

        verify(scrollBy).invoke(-3, 0, DivActionScrollBy.Overflow.RING, true, null)
    }

    @Test
    fun `scroll forward URL forwards offset in dp`() {
        handle(action(url = "div-action://scroll_forward?id=gallery&step=30"))

        verify(scrollBy).invoke(0, 30, DivActionScrollBy.Overflow.CLAMP, true, null)
    }

    @Test
    fun `scroll backward URL reverses offset and preserves overflow`() {
        handle(action(url = "div-action://scroll_backward?id=gallery&step=60&overflow=ring&animated=false"))

        verify(scrollBy).invoke(0, -60, DivActionScrollBy.Overflow.RING, false, null)
    }

    @Test
    fun `scroll to position URL forwards absolute offset in dp`() {
        handle(action(url = "div-action://scroll_to_position?id=gallery&step=30"))

        verify(scrollToOffset).invoke(30, true, null)
    }

    @Test
    fun `relative URL without id reports an error without scrolling`() {
        actionHandlerEnvironment.reporter.failOnError = false

        handle(action(url = "div-action://set_next_item"))

        assertEquals("id param is required to set item", actionHandlerEnvironment.reporter.lastError)
        verifyNoInteractions(scrollBy)
    }

    @Test
    fun `malformed relative URL step reports an error and uses View default`() {
        actionHandlerEnvironment.reporter.failOnError = false

        handle(action(url = "div-action://scroll_backward?id=gallery&step=invalid"))

        assertEquals("invalid is not a number", actionHandlerEnvironment.reporter.lastError)
        verify(scrollBy).invoke(0, -1, DivActionScrollBy.Overflow.CLAMP, true, null)
    }

    @Test
    fun `unknown URL overflow reports an error and clamps like View`() {
        actionHandlerEnvironment.reporter.failOnError = false

        handle(action(url = "div-action://set_next_item?id=gallery&overflow=unknown"))

        assertEquals("Unsupported overflow unknown", actionHandlerEnvironment.reporter.lastError)
        verify(scrollBy).invoke(1, 0, DivActionScrollBy.Overflow.CLAMP, true, null)
    }

    @Test
    fun `zero URL step ignores overflow and does not scroll like View`() {
        handle(action(url = "div-action://set_next_item?id=gallery&step=0&overflow=unknown"))

        verifyNoInteractions(scrollBy)
    }

    @Test
    fun `typed scroll_by forwards evaluated parameters`() {
        handle(action(typed = scrollByAction(
            "gallery", itemCount = -2, offset = 54, overflow = DivActionScrollBy.Overflow.RING, animated = false,
        )))

        verify(scrollBy).invoke(-2, 54, DivActionScrollBy.Overflow.RING, false, null)
    }

    @Test
    fun `typed offset forwards its dp value`() {
        handle(action(typed = scrollToAction(
            "gallery", DivActionScrollDestination.Offset(OffsetDestination(constant(54L))), animated = false,
        )))

        verify(scrollToOffset).invoke(54, false, null)
    }

    @Test
    fun `typed scroll_to resolves index expression`() {
        val index = Variable.IntegerVariable("index", 1)
        actionHandlerEnvironment.variableController.declare(index)
        handle(action(typed = scrollToAction(
            "gallery", DivActionScrollDestination.Index(IndexDestination(intExpression("@{index}"))),
        )))

        verify(scrollTo).invoke(1, true, false, null)
    }

    @Test
    fun `index outside visible items is reported without scrolling`() {
        actionHandlerEnvironment.reporter.failOnError = false

        handle(action(url = "div-action://set_current_item?id=gallery&item=2"))

        assertEquals("Scroll index 2 is not in range [0, 2) for 'gallery'", actionHandlerEnvironment.reporter.lastError)
        verifyNoInteractions(scrollTo)
    }

    @Test
    fun `unknown item id is reported without scrolling`() {
        actionHandlerEnvironment.reporter.failOnError = false

        handle(action(url = "div-action://scroll_to_item_id?id=gallery&item_id=unknown"))

        assertEquals("No item with id 'unknown' found in 'gallery' for scroll_to action", actionHandlerEnvironment.reporter.lastError)
        verifyNoInteractions(scrollTo)
    }

    @Test
    fun `ambiguous gallery id is reported without scrolling`() {
        actionHandlerEnvironment.reporter.failOnError = false
        targetStorage.register("gallery", ScrollTarget(
            items = listOf(text(id = "other", text = "Other")),
            scrollTo = scrollTo,
            scrollToOffset = scrollToOffset,
            scrollBy = scrollBy,
        ))

        handle(action(url = "div-action://set_current_item?id=gallery&item=1"))

        assertEquals("Several scrollable elements with id 'gallery' found for scroll_to action", actionHandlerEnvironment.reporter.lastError)
        verifyNoInteractions(scrollTo)
    }

    @Test
    fun `duplicate item id is reported without scrolling`() {
        actionHandlerEnvironment.reporter.failOnError = false
        targetStorage.register("duplicate", ScrollTarget(
            items = listOf(text(id = "repeated", text = "First"), text(id = "repeated", text = "Second")),
            scrollTo = scrollTo,
            scrollToOffset = scrollToOffset,
            scrollBy = scrollBy,
        ))

        handle(action(url = "div-action://scroll_to_item_id?id=duplicate&item_id=repeated"))

        assertEquals("Several items with id 'repeated' found in 'duplicate' for scroll_to action", actionHandlerEnvironment.reporter.lastError)
        verifyNoInteractions(scrollTo)
    }

    @Test
    fun `missing gallery for relative scroll names the action in the error`() {
        actionHandlerEnvironment.reporter.failOnError = false

        handle(action(typed = scrollByAction("missing", itemCount = 1)))

        assertEquals("No scrollable element with id 'missing' found for scroll_by action", actionHandlerEnvironment.reporter.lastError)
        verifyNoInteractions(scrollBy)
    }

    @Test
    fun `typed scroll_to forwards duration`() {
        handle(action(typed = scrollToAction(
            "gallery", DivActionScrollDestination.Index(IndexDestination(constant(1L))), duration = constant(2_000L),
        )))

        verify(scrollTo).invoke(1, true, false, 2_000)
    }

    @Test
    fun `typed scroll_by forwards duration`() {
        handle(action(typed = scrollByAction("gallery", itemCount = 1, offset = 20, duration = constant(500L))))

        verify(scrollBy).invoke(1, 20, DivActionScrollBy.Overflow.CLAMP, true, 500)
    }

    @Test
    fun `zero duration disables scroll_to animation`() {
        handle(action(typed = scrollToAction(
            "gallery", DivActionScrollDestination.Index(IndexDestination(constant(1L))), duration = constant(0L),
        )))

        verify(scrollTo).invoke(1, false, false, 0)
    }

    @Test
    fun `zero duration disables scroll_by animation`() {
        handle(action(typed = scrollByAction("gallery", itemCount = 1, duration = constant(0L))))

        verify(scrollBy).invoke(1, 0, DivActionScrollBy.Overflow.CLAMP, false, 0)
    }

    @Test
    fun `duration respects disabled host animations`() {
        whenever(animationsEnabledProvider.isAnimationsEnabled()).thenReturn(false)

        handle(action(typed = scrollToAction(
            "gallery", DivActionScrollDestination.Index(IndexDestination(constant(1L))), duration = constant(2_000L),
        )))

        verify(scrollTo).invoke(1, false, false, 2_000)
    }

    @Test
    fun `duration respects animated false`() {
        handle(action(typed = scrollByAction("gallery", itemCount = 1, animated = false, duration = constant(2_000L))))

        verify(scrollBy).invoke(1, 0, DivActionScrollBy.Overflow.CLAMP, false, 2_000)
    }

    private fun handle(action: DivAction, source: DivActionSource = DivActionSource.EXTERNAL) =
        actionHandlerEnvironment.handle(action, source)
}
