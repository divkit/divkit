package com.yandex.div.compose.views.tabs

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.scrollToAction
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import com.yandex.div.test.data.text
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionScrollDestination
import com.yandex.div2.DivData
import com.yandex.div2.IndexDestination
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(AndroidJUnit4::class)
class DivTabsScrollActionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val reporter = TestReporter()
    private lateinit var divContext: DivContext
    private lateinit var tabsData: DivData

    @Test
    fun `set_current_item selects the tab`() {
        setTabsContent()

        perform(action(url = "div-action://set_current_item?id=tabs&item=2"))

        onPage("Page 3").assertIsDisplayed()
    }

    @Test
    fun `set_current_item without animation selects the tab`() {
        setTabsContent()

        perform(action(url = "div-action://set_current_item?id=tabs&item=2&animated=false"))

        onPage("Page 3").assertIsDisplayed()
    }

    @Test
    fun `scroll_to with duration switches the page over the duration`() {
        setTabsContent()
        val pageStart = pageLeft("Page 1")
        rule.mainClock.autoAdvance = false

        perform(action(typed = scrollToAction(
            "tabs", DivActionScrollDestination.Index(IndexDestination(constant(2L))), duration = constant(10_000L),
        )))
        rule.mainClock.advanceTimeBy(5_000)

        assertNotEquals(pageStart, pageLeft("Page 3"), absoluteTolerance = 0.5f)
        rule.mainClock.advanceTimeBy(5_500)
        assertEquals(pageStart, pageLeft("Page 3"), absoluteTolerance = 0.5f)
    }

    @Test
    fun `set_next_item selects the following tab`() {
        setTabsContent()

        perform(action(url = "div-action://set_next_item?id=tabs"))

        onPage("Page 2").assertIsDisplayed()
    }

    @Test
    fun `set_next_item counts from the target of a pending action`() {
        setTabsContent()

        perform(
            action(url = "div-action://set_current_item?id=tabs&item=2"),
            action(url = "div-action://set_next_item?id=tabs"),
        )

        onPage("Page 4").assertIsDisplayed()
    }

    @Test
    fun `set_previous_item on the first tab stays with clamp`() {
        setTabsContent()

        perform(action(url = "div-action://set_previous_item?id=tabs"))

        onPage("Page 1").assertIsDisplayed()
    }

    @Test
    fun `set_previous_item on the first tab wraps with ring`() {
        setTabsContent()

        perform(action(url = "div-action://set_previous_item?id=tabs&overflow=ring"))

        onPage("Page 5").assertIsDisplayed()
    }

    @Test
    fun `set_next_item with a step clamps at the last tab`() {
        setTabsContent()

        perform(
            action(url = "div-action://set_current_item?id=tabs&item=4&animated=false"),
            action(url = "div-action://set_next_item?id=tabs&step=2"),
        )

        onPage("Page 5").assertIsDisplayed()
    }

    @Test
    fun `scroll_to_end selects the last tab`() {
        setTabsContent()

        perform(action(url = "div-action://scroll_to_end?id=tabs"))

        onPage("Page 5").assertIsDisplayed()
    }

    @Test
    fun `scroll_to_start returns to the first tab`() {
        setTabsContent()

        perform(
            action(url = "div-action://scroll_to_end?id=tabs&animated=false"),
            action(url = "div-action://scroll_to_start?id=tabs"),
        )

        onPage("Page 1").assertIsDisplayed()
    }

    @Test
    fun `scroll_to_item_id selects the tab with the id`() {
        setTabsContent()

        perform(action(url = "div-action://scroll_to_item_id?id=tabs&item_id=page4"))

        onPage("Page 4").assertIsDisplayed()
    }

    @Test
    fun `scroll_forward keeps the tab and warns`() {
        setTabsContent()

        perform(action(url = "div-action://scroll_forward?id=tabs&step=100"))

        onPage("Page 1").assertIsDisplayed()
        assertEquals("Tabs 'tabs' switch by tabs, offset 100 is ignored", reporter.lastWarning)
    }

    @Test
    fun `tabs without items report the index`() {
        reporter.failOnError = false
        setTabsContent(pages = 0)

        perform(action(url = "div-action://set_current_item?id=tabs&item=0"))

        assertEquals("Scroll index 0 is not in range [0, 0) for 'tabs'", reporter.lastError)
    }

    private fun setTabsContent(pages: Int = 5) {
        tabsData = data(
            tabs(
                id = "tabs",
                items = List(pages) { index ->
                    tabItem(
                        div = text(
                            height = fixed(200),
                            id = "page${index + 1}",
                            text = constant("Page ${index + 1}"),
                        ),
                        title = "Tab ${index + 1}",
                    )
                },
            )
        )
        val configuration = divConfiguration { reporter = this@DivTabsScrollActionsTest.reporter }
        rule.setContent {
            divContext = DivContext(baseContext = LocalContext.current, configuration = configuration)
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data = tabsData)
            }
        }
        rule.waitForIdle()
    }

    private fun perform(vararg actions: DivAction) {
        rule.runOnIdle {
            actions.forEach { divContext.debugFeatures.performAction(tabsData, it) }
        }
        rule.waitForIdle()
    }

    private fun onPage(text: String) = rule.onNode(hasText(text) and isPlaced())

    private fun pageLeft(text: String): Float = onPage(text).fetchSemanticsNode().positionInRoot.x
}

// Measurement can compose an unplaced copy of a page.
private fun isPlaced() = SemanticsMatcher("is placed") { it.layoutInfo.isPlaced }
