package com.yandex.div.compose.tooltips

import com.yandex.div.test.data.constant
import com.yandex.div.test.data.text
import com.yandex.div2.DivTooltip
import kotlin.test.Test
import kotlin.test.assertSame

class TooltipStateStorageTest {

    @Test
    fun `unregister keeps replacement entry`() {
        val storage = TooltipStateStorage()
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
