package com.yandex.div.compose

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.action
import com.yandex.div.test.data.data
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import com.yandex.div.test.data.text
import com.yandex.div2.DivAction
import com.yandex.div2.DivTabs
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(AndroidJUnit4::class)
class DivTabsActionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val reporter = TestReporter()

    private val configuration = DivConfiguration(
        actionHandler = actionHandler,
        reporter = reporter
    )

    @Test
    fun `tap on the active tab title triggers its title_click_action`() {
        setContent(
            firstTitleClickAction = action(url = "test://first"),
            secondTitleClickAction = action(url = "test://second")
        )

        rule.onNodeWithText("First").performClick()

        assertEquals(actionData(url = "test://first"), actionHandler.handledAction)
    }

    @Test
    fun `tap on the active tab title without title_click_action does nothing`() {
        setContent()

        rule.onNodeWithText("First").performClick()

        assertEquals(emptyList(), actionHandler.handledActions)
        onPageWithText("First page").assertIsDisplayed()
    }

    @Test
    fun `tap on an inactive tab title selects it instead of triggering its title_click_action`() {
        setContent(
            firstTitleClickAction = action(url = "test://first"),
            secondTitleClickAction = action(url = "test://second")
        )

        rule.onNodeWithText("Second").performClick()

        assertEquals(emptyList(), actionHandler.handledActions)
        onPageWithText("Second page").assertIsDisplayed()

        rule.onNodeWithText("Second").performClick()

        assertEquals(actionData(url = "test://second"), actionHandler.handledAction)
    }

    @Test
    fun `disabled title_click_action is ignored`() {
        setContent(firstTitleClickAction = action(isEnabled = false, url = "test://first"))

        rule.onNodeWithText("First").performClick()

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `tap on the active tab title scrolls it to the center of the title row`() {
        setScrollableTitlesContent()
        rule.onNodeWithText("Tab 2").performClick()
        titleRow().performScrollToIndex(0)
        assertNotEquals(titleRow().centerX(), rule.onNodeWithText("Tab 2").centerX(), absoluteTolerance = 1f)

        rule.onNodeWithText("Tab 2").performClick()

        assertEquals(titleRow().centerX(), rule.onNodeWithText("Tab 2").centerX(), absoluteTolerance = 1f)
    }

    private fun titleRow() = rule.onNode(hasScrollToIndexAction() and hasAnyDescendant(hasText("Tab 2")))

    private fun SemanticsNodeInteraction.centerX() = fetchSemanticsNode().boundsInRoot.center.x

    private fun onPageWithText(text: String) = rule.onNode(hasText(text) and isPlaced())

    private fun setContent(
        firstTitleClickAction: DivAction? = null,
        secondTitleClickAction: DivAction? = null
    ) {
        rule.setContent(
            configuration = configuration,
            data = data(
                tabs(
                    items = listOf(
                        tabItem(
                            div = text(text = "First page"),
                            title = "First",
                            titleClickAction = firstTitleClickAction
                        ),
                        tabItem(
                            div = text(text = "Second page"),
                            title = "Second",
                            titleClickAction = secondTitleClickAction
                        )
                    )
                )
            )
        )
    }

    private fun setScrollableTitlesContent() {
        rule.setContent(
            configuration = configuration,
            data = data(
                tabs(
                    items = List(6) { index ->
                        tabItem(div = text(text = "Page $index"), title = "Tab $index")
                    },
                    tabTitleStyle = DivTabs.TabTitleStyle(paddings = insets(start = 40, end = 40))
                )
            )
        )
    }
}

// TabsContent's measure slot composes every page a second time without placing it.
private fun isPlaced() = SemanticsMatcher("is placed") { it.layoutInfo.isPlaced }
