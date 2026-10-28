package com.yandex.div.compose.tooltips

import com.yandex.div.test.data.constant
import com.yandex.div.test.data.text
import com.yandex.div2.DivTooltip
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TooltipStateStorageTest {
    private val storage = TooltipStateStorage()

    @Test
    fun `showing another tooltip keeps the first requested`() {
        storage.show(TOOLTIP_ID)
        storage.onShown(TOOLTIP_ID)

        storage.show("second")

        assertEquals(setOf(TOOLTIP_ID, "second"), storage.shownTooltipIds().toSet())
    }

    @Test
    fun `cancelling an unshown request does not consume its first show`() {
        storage.show(TOOLTIP_ID)
        storage.cancelAll()

        assertTrue(storage.show(TOOLTIP_ID))
    }

    @Test
    fun `unregister keeps replacement entry`() {
        val original = entry()
        val replacement = entry()
        storage.register(original)
        storage.register(replacement)

        storage.unregister(original)

        assertSame(replacement, storage.getAnchor(TOOLTIP_ID))
    }

    private fun entry() = TooltipAnchorEntry(
        tooltip = DivTooltip(
            id = TOOLTIP_ID,
            position = constant(DivTooltip.Position.CENTER),
            div = text(text = "Tooltip"),
        ),
    )

    private companion object {
        const val TOOLTIP_ID = "tooltip"
    }
}
