package com.yandex.div.compose.views.tabs

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import com.yandex.div.test.data.text
import com.yandex.div2.DivData
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class DivTabsSelectionTest {

    @get:Rule
    val rule = createComposeRule()

    private val selectedTab = Variable.IntegerVariable("selected_tab", 0)
    private val swipeEnabled = Variable.BooleanVariable("swipe_enabled", true)
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = DivVariableController().apply {
            declare(selectedTab)
            declare(swipeEnabled)
        },
    )
    private val data = mutableStateOf(tabsData(pages = 3))

    @Test
    fun `selected_tab expression change selects its page`() {
        setContent()
        onPage("Page 1").assertIsDisplayed()

        selectedTab.set(1)
        rule.waitForIdle()

        onPage("Page 2").assertIsDisplayed()
        onPage("Page 1").assertDoesNotExist()
    }

    @Test
    fun `selected_tab above the range selects the last tab`() {
        selectedTab.set(5)

        setContent()

        onPage("Page 3").assertIsDisplayed()
    }

    @Test
    fun `content swipe switches the page`() {
        setContent()

        swipePageLeft("Page 1")

        onPage("Page 2").assertIsDisplayed()
    }

    @Test
    fun `disabled content swipe keeps the page while a title tap still switches it`() {
        swipeEnabled.set(false)
        setContent()

        swipePageLeft("Page 1")

        onPage("Page 1").assertIsDisplayed()
        onPage("Page 2").assertDoesNotExist()

        rule.onNodeWithText("Tab 2").performClick()

        onPage("Page 2").assertIsDisplayed()
    }

    @Test
    fun `updated items show the new page content and keep the selected tab`() {
        setContent()
        rule.onNodeWithText("Tab 2").performClick()
        onPage("Page 2").assertIsDisplayed()

        data.value = tabsData(pages = 3, pageSuffix = " updated")
        rule.waitForIdle()

        onPage("Page 2 updated").assertIsDisplayed()
    }

    @Test
    fun `fewer updated items clamp the selected tab to the last one`() {
        setContent()
        rule.onNodeWithText("Tab 3").performClick()
        onPage("Page 3").assertIsDisplayed()

        data.value = tabsData(pages = 2)
        rule.waitForIdle()

        onPage("Page 2").assertIsDisplayed()
        rule.onNodeWithText("Tab 3").assertDoesNotExist()
    }

    @Test
    fun `more updated items make the added tab selectable`() {
        data.value = tabsData(pages = 2)
        setContent()

        data.value = tabsData(pages = 3)
        rule.waitForIdle()
        rule.onNodeWithText("Tab 3").performClick()

        onPage("Page 3").assertIsDisplayed()
    }

    private fun setContent() {
        rule.setContentWithDivContext(configuration) {
            DivView(data = data.value)
        }
    }

    private fun swipePageLeft(pageText: String) {
        onPage(pageText).performTouchInput { swipeLeft() }
        rule.waitForIdle()
    }

    private fun onPage(text: String) = rule.onNode(hasText(text) and isPlaced())

    private fun tabsData(pages: Int, pageSuffix: String = ""): DivData = data(
        tabs(
            items = List(pages) { index ->
                tabItem(
                    div = text(
                        height = fixed(200),
                        text = constant("Page ${index + 1}$pageSuffix"),
                    ),
                    title = "Tab ${index + 1}",
                )
            },
            selectedTab = intExpression("@{selected_tab}"),
            switchTabsByContentSwipeEnabled = booleanExpression("@{swipe_enabled}"),
        )
    )
}

// Measurement can compose an unplaced copy of a page.
private fun isPlaced() = SemanticsMatcher("is placed") { it.layoutInfo.isPlaced }
