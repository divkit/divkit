package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.tooltips.TooltipStateStorage
import com.yandex.div.test.data.action
import com.yandex.div.test.data.hideTooltipAction
import com.yandex.div.test.data.showTooltipAction
import com.yandex.div2.DivAction
import org.junit.runner.RunWith
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class TooltipActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()
    private val tooltipStateStorage = TooltipStateStorage()

    @BeforeTest
    fun setUp() {
        actionHandlerEnvironment.init(
            tooltipActionHandler = TooltipActionHandler(
                tooltipStateStorage = tooltipStateStorage
            )
        )
    }

    @Test
    fun `show tooltip action shows tooltip`() {
        handle(
            action(typed = showTooltipAction(id = "tooltip1"))
        )

        assertEquals(listOf("tooltip1"), tooltipStateStorage.shownTooltipIds())
    }

    @Test
    fun `hide tooltip action hides tooltip`() {
        tooltipStateStorage.show("tooltip1")
        tooltipStateStorage.show("tooltip2")

        handle(
            action(typed = hideTooltipAction(id = "tooltip1"))
        )

        assertEquals(listOf("tooltip2"), tooltipStateStorage.shownTooltipIds())
    }

    @Test
    fun `show_tooltip div-action shows tooltip`() {
        handle(
            action(url = "div-action://show_tooltip?id=tooltip1")
        )

        assertEquals(listOf("tooltip1"), tooltipStateStorage.shownTooltipIds())
    }

    @Test
    fun `hide_tooltip div-action hides tooltip`() {
        tooltipStateStorage.show("tooltip1")
        tooltipStateStorage.show("tooltip2")

        handle(
            action(url = "div-action://hide_tooltip?id=tooltip1")
        )

        assertEquals(listOf("tooltip2"), tooltipStateStorage.shownTooltipIds())
    }

    private fun handle(action: DivAction) = actionHandlerEnvironment.handle(action)
}
