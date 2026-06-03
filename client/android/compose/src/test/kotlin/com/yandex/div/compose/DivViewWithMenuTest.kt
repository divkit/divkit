package com.yandex.div.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.action
import com.yandex.div.test.data.data
import com.yandex.div.test.data.menuItem
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewWithMenuTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val reporter = TestReporter()

    private val configuration = DivComposeConfiguration(
        actionHandler = actionHandler,
        reporter = reporter
    )

    @Test
    fun `menu appears when element with menu action is clicked`() {
        setContent(
            menuItem(
                action = action(),
                text = "Item 1"
            ),
            menuItem(
                action = action(),
                text = "Item 2"
            )
        )

        rule.onNodeWithText("Item 1").assertIsNotDisplayed()
        rule.onNodeWithText("Item 2").assertIsNotDisplayed()

        rule.onNodeWithText("button").performClick()

        rule.onNodeWithText("Item 1").assertIsDisplayed()
        rule.onNodeWithText("Item 2").assertIsDisplayed()
    }

    @Test
    fun `menu appears for element with long tap action`() {
        setContent(
            text(
                longTapActions = listOf(
                    action(
                        menuItems = listOf(
                            menuItem(
                                action = action(),
                                text = "Menu Item"
                            )
                        )
                    )
                ),
                text = "button"
            )
        )

        rule.onNodeWithText("button").performClick()

        rule.onNodeWithText("Menu Item").assertIsNotDisplayed()

        rule.onNodeWithText("button").performTouchInput { longClick() }

        rule.onNodeWithText("Menu Item").assertIsDisplayed()
    }

    @Test
    fun `menu appears for element with double tap action`() {
        setContent(
            text(
                doubleTapActions = listOf(
                    action(
                        menuItems = listOf(
                            menuItem(
                                action = action(),
                                text = "Menu Item"
                            )
                        )
                    )
                ),
                text = "button"
            )
        )

        rule.onNodeWithText("button").performClick()

        rule.onNodeWithText("Menu Item").assertIsNotDisplayed()

        rule.onNodeWithText("button").performTouchInput { doubleClick() }

        rule.onNodeWithText("Menu Item").assertIsDisplayed()
    }

    @Test
    fun `menu item with disabled actions is ignored`() {
        setContent(
            menuItem(
                action = action(),
                text = "Enabled Item"
            ),
            menuItem(
                action = action(isEnabled = false),
                text = "Disabled Item"
            )
        )

        rule.onNodeWithText("button").performClick()

        rule.onNodeWithText("Enabled Item").assertIsDisplayed()
        rule.onNodeWithText("Disabled Item").assertIsNotDisplayed()
    }

    @Test
    fun `action is triggered when menu item is clicked`() {
        setContent(
            menuItem(
                action = action(id = "menu_item"),
                text = "Menu Item"
            )
        )

        rule.onNodeWithText("button").performClick()
        actionHandler.reset()

        rule.onNodeWithText("Menu Item").apply {
            assertIsDisplayed()
            performClick()
            assertIsNotDisplayed()
        }

        assertEquals(actionData(id = "menu_item"), actionHandler.handledAction)
    }

    @Test
    fun `menu appears and action is handled for an action that contains both menuItems and url`() {
        setContent(
            text(
                action = action(
                    id = "action_with_menu",
                    menuItems = listOf(
                        menuItem(
                            action = action(),
                            text = "Menu Item"
                        )
                    ),
                    url = "custom://url"
                ),
                text = "button"
            )
        )

        rule.onNodeWithText("button").performClick()

        rule.onNodeWithText("Menu Item").assertIsDisplayed()

        assertEquals(
            actionData(id = "action_with_menu", url = "custom://url"),
            actionHandler.handledAction
        )
    }

    private fun setContent(vararg menuItems: DivAction.MenuItem) {
        setContent(
            text(
                action = action(menuItems = menuItems.toList()),
                text = "button"
            )
        )
    }

    private fun setContent(content: Div) {
        rule.setContent(
            configuration = configuration,
            data = data(content)
        )
    }
}
