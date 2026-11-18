package com.yandex.div.compose.views.tabs

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    fun `selected_tab change with the same clamped index selects its page`() {
        selectedTab.set(2)
        setContent()
        rule.onNodeWithText("Tab 1").performClick()
        rule.waitForIdle()

        selectedTab.set(5)
        rule.waitForIdle()

        onPage("Page 3").assertIsDisplayed()
    }

    @Test
    fun `selected_tab change to the same target finishes an ongoing page scroll`() {
        setContent()
        val range = rule.onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.PageRight))
            .fetchSemanticsNode().config[SemanticsProperties.HorizontalScrollAxisRange]
        rule.mainClock.autoAdvance = false
        selectedTab.set(2)
        var progress = 0f
        for (frame in 0 until 120) {
            rule.mainClock.advanceTimeByFrame()
            rule.waitForIdle()
            progress = range.value() / range.maxValue()
            if (progress in 0.80f..0.98f) break
        }
        assertTrue(progress in 0.80f..0.98f, "Page scroll progress: $progress")

        selectedTab.set(5)
        rule.mainClock.autoAdvance = true
        rule.waitForIdle()

        assertEquals(range.maxValue(), range.value(), absoluteTolerance = 0.5f)
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
    fun `fewer updated items keep user selection when selected_tab is unchanged`() {
        selectedTab.set(2)
        setContent()
        rule.onNodeWithText("Tab 1").performClick()
        rule.waitForIdle()

        data.value = tabsData(pages = 2)
        rule.waitForIdle()

        onPage("Page 1").assertIsDisplayed()
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
