package com.yandex.div.compose.views.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ScrollWheel
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.observer.DivComponentObserver
import com.yandex.div.compose.observer.events.DivTabChangedEvent
import com.yandex.div.compose.observer.events.DivTabTitlesScrolledEvent
import com.yandex.div.compose.setContent
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import com.yandex.div.test.data.text
import com.yandex.div2.DivAction
import com.yandex.div2.DivTabs
import kotlin.test.Test
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions

@RunWith(AndroidJUnit4::class)
class DivTabsScrollObserverTest {

    @get:Rule
    val rule = createComposeRule()

    private val observer = mock<DivComponentObserver>()
    private val selectedTab = Variable.IntegerVariable("selected_tab", 0)
    private val configuration = divConfiguration {
        reporter = TestReporter()
        componentObserver = observer
        variableController = DivVariableController().apply { declare(selectedTab) }
    }

    @Test
    fun `tapping a title reports the selected index even without a tabs id`() {
        rule.setContent(configuration = configuration, data = tabsData(id = null))

        rule.onNodeWithText("Tab 1").performClick()
        rule.waitForIdle()

        verify(observer).onTabChanged(DivTabChangedEvent("card", null, 1))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `a scroll action reports only the destination tab`() {
        val selectLast = action(url = "div-action://set_current_item?id=tabs&item=5")
        rule.setContent(configuration = configuration, data = tabsData(pageAction = selectLast))

        page("Page 0").performClick()
        rule.waitForIdle()

        verify(observer).onTabChanged(DivTabChangedEvent("card", "tabs", 5))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `changing the selected tab expression reports its new value`() {
        rule.setContent(configuration = configuration, data = tabsData())

        selectedTab.set(3)
        rule.waitForIdle()

        verify(observer).onTabChanged(DivTabChangedEvent("card", "tabs", 3))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `tab selection is still reported after a gesture interrupts an expression animation`() {
        rule.setContent(configuration = configuration, data = tabsData())
        rule.mainClock.autoAdvance = false
        selectedTab.set(3)
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeByFrame()

        rule.onNode(hasScrollToIndexAction() and hasAnyDescendant(hasText("Page ", substring = true)))
            .performTouchInput {
                down(center)
                moveBy(Offset(-width / 4f, 0f))
                up()
            }
        rule.mainClock.autoAdvance = true
        rule.waitForIdle()
        titleRow().performScrollToIndex(5)
        rule.onNodeWithText("Tab 5").performClick()
        rule.waitForIdle()

        verify(observer).onTabChanged(DivTabChangedEvent("card", "tabs", 5))
    }

    @Test
    fun `changing the selected tab inside a visible tab reports only one event`() {
        val innerTabs = tabs(
            id = "inner",
            selectedTab = intExpression("@{selected_tab}"),
            items = listOf(
                tabItem(text(text = "First page"), title = "First"),
                tabItem(text(text = "Second page"), title = "Second"),
            ),
        )
        val outerTabs = tabs(items = listOf(tabItem(innerTabs, title = "Outer")))
        rule.setContent(configuration = configuration, data = data(outerTabs, logId = "card"))

        selectedTab.set(1)
        rule.waitForIdle()

        verify(observer).onTabChanged(DivTabChangedEvent("card", "inner", 1))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `a nested DivView inside a tab reports selection only from its visible copy`() {
        val nestedCard = tabsData(id = "inner")
        val outerObserver = mock<DivComponentObserver>()
        val factory = object : DivCustomViewFactory {
            @Composable
            override fun Content(modifier: Modifier, environment: DivCustomEnvironment) {
                val baseContext = LocalContext.current
                val context = remember(baseContext) { DivContext(baseContext, configuration) }
                CompositionLocalProvider(LocalDivContext provides context) {
                    DivView(nestedCard, modifier)
                }
            }
        }
        rule.setContent(
            configuration = divConfiguration {
                reporter = TestReporter()
                componentObserver = outerObserver
                customViewFactories = mapOf("nested" to factory)
            },
            data = data(
                tabs(items = listOf(tabItem(custom(type = "nested"), title = "Outer"))),
                logId = "outer",
            ),
        )

        selectedTab.set(1)
        rule.waitForIdle()

        verify(observer).onTabChanged(DivTabChangedEvent("card", "inner", 1))
        verifyNoMoreInteractions(observer)
        verifyNoInteractions(outerObserver)
    }

    @Test
    fun `a scroll action selects the visible nested tab and reports one event`() {
        val openSecond = action(url = "div-action://set_current_item?id=inner&item=1&animated=false")
        val innerTabs = tabs(
            id = "inner",
            height = fixed(160),
            items = listOf(
                tabItem(text(text = "Open second page", action = openSecond), title = "First"),
                tabItem(text(text = "Second page"), title = "Second"),
            ),
        )
        val outerTabs = tabs(items = listOf(tabItem(innerTabs, title = "Outer")))
        rule.setContent(configuration = divConfiguration {
            reporter = TestReporter()
            componentObserver = observer
        }, data = data(outerTabs, logId = "card"))

        page("Open second page").performClick()
        rule.waitForIdle()

        page("Second page").assertIsDisplayed()
        verify(observer).onTabChanged(DivTabChangedEvent("card", "inner", 1))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `title strip reports each drag once without changing tabs`() {
        rule.setContent(configuration = configuration, data = tabsData())
        val titleRow = titleRow()
        val event = DivTabTitlesScrolledEvent("card", "tabs", 0)

        titleRow.performTouchInput { swipeLeft() }
        rule.waitForIdle()
        verify(observer).onTabTitlesScrolled(event)
        titleRow.performTouchInput { swipeRight() }
        rule.waitForIdle()

        verify(observer, times(2)).onTabTitlesScrolled(event)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `scrolling tab titles reports the latest settled selection`() {
        rule.setContent(configuration = configuration, data = tabsData())
        selectedTab.set(2)
        rule.waitForIdle()

        titleRow().performTouchInput { swipeLeft() }
        rule.waitForIdle()

        val event = DivTabChangedEvent("card", "tabs", 2)
        verify(observer).onTabChanged(event)
        verify(observer).onTabTitlesScrolled(DivTabTitlesScrolledEvent("card", "tabs", 2))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `a title drag after a wheel scroll is reported separately`() {
        rule.setContent(configuration = configuration, data = tabsData())
        val titleRow = titleRow()
        val event = DivTabTitlesScrolledEvent("card", "tabs", 0)
        titleRow.performMouseInput {
            moveTo(center)
            scroll(1f, ScrollWheel.Horizontal)
        }
        rule.waitForIdle()
        verify(observer).onTabTitlesScrolled(event)

        titleRow.performTouchInput { swipeLeft() }
        rule.waitForIdle()

        verify(observer, times(2)).onTabTitlesScrolled(event)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `a tab selection after rebinding goes only to the new context observer`() {
        val newObserver = mock<DivComponentObserver>()
        val newConfiguration = divConfiguration {
            reporter = TestReporter()
            componentObserver = newObserver
            variableController = configuration.variableController
        }
        val oldCard = tabsData()
        val newCard = tabsData(pagePrefix = "Updated")
        val rebound = mutableStateOf(false)
        rule.setContent {
            val baseContext = LocalContext.current
            val activeConfiguration = if (rebound.value) newConfiguration else configuration
            val context = remember(baseContext, activeConfiguration) { DivContext(baseContext, activeConfiguration) }
            CompositionLocalProvider(LocalContext provides context) {
                DivView(if (rebound.value) newCard else oldCard)
            }
        }

        rebound.value = true
        rule.onNodeWithText("Tab 1").performClick()
        rule.waitForIdle()

        verifyNoInteractions(observer)
        verify(newObserver).onTabChanged(DivTabChangedEvent("card", "tabs", 1))
        verifyNoMoreInteractions(newObserver)
    }

    @Test
    fun `replacement card with the same ids does not report its initial selected tab`() {
        val current = mutableStateOf(cardWithSelectedTab("card", 0))
        rule.setContentWithDivContext(configuration) { DivView(current.value) }

        current.value = cardWithSelectedTab("card", 2)
        rule.waitForIdle()

        page("card page 2").assertIsDisplayed()
        verifyNoInteractions(observer)
    }

    @Test
    fun `replacement card with the same selected_tab keeps the user's selection`() {
        val current = mutableStateOf(cardWithSelectedTab("A", 0))
        rule.setContentWithDivContext(configuration) { DivView(current.value) }
        rule.onNodeWithText("Tab 1").performClick()
        rule.waitForIdle()

        current.value = cardWithSelectedTab("B", 0)

        page("B page 1").assertIsDisplayed()
        verify(observer).onTabChanged(DivTabChangedEvent("A", "tabs", 1))
        verifyNoMoreInteractions(observer)
    }

    private fun cardWithSelectedTab(cardId: String, selected: Long) = data(
        logId = cardId,
        content = tabs(
            id = "tabs",
            selectedTab = constant(selected),
            items = List(3) { index -> tabItem(text(text = "$cardId page $index"), title = "Tab $index") },
        ),
    )

    private fun page(text: String) = rule.onNode(
        hasText(text) and SemanticsMatcher("is placed") { it.layoutInfo.isPlaced }
    )

    private fun titleRow() =
        rule.onNode(hasScrollToIndexAction() and hasAnyDescendant(hasText("Tab ", substring = true)))

    private fun tabsData(id: String? = "tabs", pageAction: DivAction? = null, pagePrefix: String = "Page") = data(
        logId = "card",
        content = tabs(
            id = id,
            selectedTab = intExpression("@{selected_tab}"),
            tabTitleStyle = DivTabs.TabTitleStyle(paddings = insets(start = 40, end = 40)),
            items = List(6) { index ->
                tabItem(
                    title = "Tab $index",
                    div = text(height = fixed(160), text = constant("$pagePrefix $index"), action = pageAction),
                )
            },
        ),
    )
}
