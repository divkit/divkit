package com.yandex.div.internal.actions

import android.net.Uri
import com.yandex.div.data.StoredValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivUntypedActionTest {

    @Test
    fun `non div-action uri`() {
        assertNull(parse("non-div-action://"))
    }

    @Test
    fun `set_state action`() {
        assertEquals(
            DivUntypedAction.SetState(id = "state1", isTemporary = true),
            parse("div-action://set_state?state_id=state1")
        )
    }

    @Test
    fun `set_state action with temporary=false`() {
        assertEquals(
            DivUntypedAction.SetState(id = "state1", isTemporary = false),
            parse("div-action://set_state?state_id=state1&temporary=false")
        )
    }

    @Test
    fun `set_state action without id fails`() {
        assertNull(parse("div-action://set_state"))
    }

    @Test
    fun `set_stored_value action`() {
        assertEquals(
            DivUntypedAction.SetStoredValue(
                name = "boolean_value",
                value = "true",
                type = StoredValue.Type.BOOLEAN,
                lifetime = 1000L
            ),
            parse("div-action://set_stored_value?name=boolean_value&value=true&type=boolean&lifetime=1000")
        )
    }

    @Test
    fun `set_stored_value with invalid type fails`() {
        assertNull(
            parse("div-action://set_stored_value?name=boolean_value&value=true&type=invalid&lifetime=1000")
        )
    }

    @Test
    fun `set_variable action`() {
        assertEquals(
            DivUntypedAction.SetVariable(name = "var", value = "new value"),
            parse("div-action://set_variable?name=var&value=new value")
        )
    }

    @Test
    fun `set_variable action without value fails`() {
        assertNull(parse("div-action://set_variable?name=var"))
    }

    @Test
    fun `hide_tooltip action`() {
        assertEquals(
            DivUntypedAction.HideTooltip(id = "tooltip_id"),
            parse("div-action://hide_tooltip?id=tooltip_id")
        )
    }

    @Test
    fun `show_tooltip action`() {
        assertEquals(
            DivUntypedAction.ShowTooltip(id = "tooltip_id", isMultiple = false),
            parse("div-action://show_tooltip?id=tooltip_id")
        )
    }

    @Test
    fun `timer action`() {
        assertEquals(
            DivUntypedAction.Timer(id = "timer_id", action = "start"),
            parse("div-action://timer?id=timer_id&action=start")
        )
    }

    @Test
    fun `timer action without action fails`() {
        assertNull(parse("div-action://timer?id=timer_id"))
    }

    @Test
    fun `video action`() {
        assertEquals(
            DivUntypedAction.Video(id = "video_id", action = "start"),
            parse("div-action://video?id=video_id&action=start")
        )
    }

    @Test
    fun `video action without action fails`() {
        assertNull(parse("div-action://video?id=video_id"))
    }
}

private fun parse(uri: String): DivUntypedAction? {
    return DivUntypedAction.parse(Uri.parse(uri))
}
