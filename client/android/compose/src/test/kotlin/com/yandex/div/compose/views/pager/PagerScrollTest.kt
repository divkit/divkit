package com.yandex.div.compose.views.pager

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toSize
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.divConfiguration
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.scrollToAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionScrollDestination
import com.yandex.div2.DivData
import com.yandex.div2.DivPager
import com.yandex.div2.IndexDestination
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail
import org.junit.Rule
import org.junit.runner.RunWith

private const val PAGER_SIZE = 300
private const val TOLERANCE = 0.5f
private const val MAX_SCROLL_START_FRAMES = 200

@RunWith(AndroidJUnit4::class)
class PagerScrollTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val reporter = TestReporter()
    private val hideAll = Variable.BooleanVariable("hide_all", false)
    private val hideSecond = Variable.BooleanVariable("hide_second", false)
    private lateinit var divContext: DivContext
    private lateinit var pagerData: DivData

    @Test
    fun `set_current_item moves to the page`() {
        setPagerContent()

        perform(action(url = "div-action://set_current_item?id=pager&item=2&animated=false"))

        assertPageAtStart(2)
    }

    @Test
    fun `set_current_item animates to the page`() {
        setPagerContent()

        performAnimated(action(url = "div-action://set_current_item?id=pager&item=2"))
        awaitScrollEnd(observedIndex = 0)

        assertPageAtStart(2)
    }

    @Test
    fun `set_next_item counts from the pending instant target`() {
        setPagerContent()

        perform(
            action(url = "div-action://set_current_item?id=pager&item=2&animated=false"),
            action(url = "div-action://set_next_item?id=pager&animated=false"),
        )

        assertPageAtStart(3)
    }

    @Test
    fun `set_next_item counts from the target of a pending animated action`() {
        setPagerContent()

        perform(
            action(url = "div-action://set_current_item?id=pager&item=2"),
            action(url = "div-action://set_next_item?id=pager&animated=false"),
        )

        assertPageAtStart(3)
    }

    @Test
    fun `set_next_item counts from the target of a running animation`() {
        setPagerContent()

        performAnimated(action(url = "div-action://set_current_item?id=pager&item=2"))
        awaitScrollStart(observedIndex = 0)
        perform(action(url = "div-action://set_next_item?id=pager&animated=false"))
        finishAnimations()

        assertPageAtStart(3)
    }

    @Test
    fun `interrupted scroll with duration selects only the page it reaches`() {
        setPagerContent(itemCount = 10)

        performAnimated(action(typed = scrollToAction(
            "pager", DivActionScrollDestination.Index(IndexDestination(constant(7L))), duration = constant(10_000L),
        )))
        awaitScrollStart(observedIndex = 0)
        perform(action(url = "div-action://set_previous_item?id=pager&step=6&animated=false"))
        finishAnimations()

        assertPageAtStart(1)
        assertEquals(listOf("0", "1"), selectedPages())
    }

    @Test
    fun `second action replaces a running animation`() {
        setPagerContent()

        performAnimated(action(url = "div-action://set_current_item?id=pager&item=4"))
        awaitScrollStart(observedIndex = 0)
        perform(action(url = "div-action://set_current_item?id=pager&item=1&animated=false"))
        finishAnimations()

        assertPageAtStart(1)
    }

    @Test
    fun `set_previous_item on the first page stays with clamp`() {
        setPagerContent()

        perform(action(url = "div-action://set_previous_item?id=pager&animated=false"))

        assertPageAtStart(0)
    }

    @Test
    fun `set_previous_item on the first page wraps with ring`() {
        setPagerContent()

        perform(action(url = "div-action://set_previous_item?id=pager&overflow=ring&animated=false"))

        assertPageAtStart(4)
    }

    @Test
    fun `set_next_item with a step clamps at the last page`() {
        setPagerContent()

        perform(
            action(url = "div-action://set_current_item?id=pager&item=4&animated=false"),
            action(url = "div-action://set_next_item?id=pager&step=2&animated=false"),
        )

        assertPageAtStart(4)
    }

    @Test
    fun `set_next_item with a step wraps with ring`() {
        setPagerContent()

        perform(
            action(url = "div-action://set_current_item?id=pager&item=4&animated=false"),
            action(url = "div-action://set_next_item?id=pager&step=2&overflow=ring&animated=false"),
        )

        assertPageAtStart(1)
    }

    @Test
    fun `scroll_to_end moves to the last page`() {
        setPagerContent()

        perform(action(url = "div-action://scroll_to_end?id=pager&animated=false"))

        assertPageAtStart(4)
    }

    @Test
    fun `scroll_to_start returns to the first page`() {
        setPagerContent()

        perform(
            action(url = "div-action://scroll_to_end?id=pager&animated=false"),
            action(url = "div-action://scroll_to_start?id=pager&animated=false"),
        )

        assertPageAtStart(0)
    }

    @Test
    fun `scroll_to_item_id moves to the page with the id`() {
        setPagerContent()

        perform(action(url = "div-action://scroll_to_item_id?id=pager&item_id=item3&animated=false"))

        assertPageAtStart(3)
    }

    @Test
    fun `offset destination keeps the page and warns`() {
        setPagerContent()

        perform(action(url = "div-action://scroll_to_position?id=pager&step=100&animated=false"))

        assertPageAtStart(0)
        assertEquals("Pager 'pager' scrolls by pages, offset 100 is ignored", reporter.lastWarning)
    }

    @Test
    fun `scroll_forward keeps the page and warns`() {
        setPagerContent()

        perform(action(url = "div-action://scroll_forward?id=pager&step=100&animated=false"))

        assertPageAtStart(0)
        assertEquals("Pager 'pager' scrolls by pages, offset 100 is ignored", reporter.lastWarning)
    }

    @Test
    fun `infinite scroll set_previous_item on the first page shows the last page`() {
        setPagerContent(infiniteScroll = true)

        perform(action(url = "div-action://set_previous_item?id=pager&animated=false"))

        assertPageAtStart(4)
    }

    @Test
    fun `infinite scroll set_current_item reaches the nearest copy of the page`() {
        setPagerContent(infiniteScroll = true)

        performAnimated(action(url = "div-action://set_current_item?id=pager&item=4"))
        awaitScrollStart(observedIndex = 0)

        // The copy before the first page is one page away, so the content moves towards the end.
        assertTrue(pageBounds(0).left > pagerBounds().left, "The first page moves towards the end")
        finishAnimations()
        assertPageAtStart(4)
    }

    @Test
    fun `vertical pager set_current_item moves to the page`() {
        setPagerContent(orientation = DivPager.Orientation.VERTICAL)

        perform(action(url = "div-action://set_current_item?id=pager&item=2&animated=false"))

        assertEquals(pagerBounds().top, pageBounds(2).top, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun `rtl pager set_current_item moves to the page`() {
        setPagerContent(layoutDirection = LayoutDirection.Rtl)

        perform(action(url = "div-action://set_current_item?id=pager&item=2&animated=false"))

        assertEquals(pagerBounds().right, pageBounds(2).right, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun `center aligned pager centers the page`() {
        setPagerContent(pageSize = 100, alignment = DivPager.ItemAlignment.CENTER)

        perform(action(url = "div-action://set_current_item?id=pager&item=2&animated=false"))

        assertEquals(pagerBounds().center.x, pageBounds(2).center.x, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun `programmatic scroll selects the target page`() {
        setPagerContent()

        perform(action(url = "div-action://set_current_item?id=pager&item=2&animated=false"))

        assertEquals(listOf("0", "2"), selectedPages())
    }

    @Test
    fun `interrupted animation selects both target pages in order`() {
        setPagerContent()

        performAnimated(action(url = "div-action://set_current_item?id=pager&item=2"))
        awaitScrollStart(observedIndex = 0)
        perform(action(url = "div-action://set_current_item?id=pager&item=3&animated=false"))
        finishAnimations()

        assertEquals(listOf("0", "2", "3"), selectedPages())
    }

    @Test
    fun `scroll with duration reaches the page at the deadline`() {
        setPagerContent()

        performAnimated(action(typed = scrollToAction(
            "pager", DivActionScrollDestination.Index(IndexDestination(constant(2L))), duration = constant(10_000L),
        )))
        awaitScrollStart(observedIndex = 0)
        composeRule.mainClock.advanceTimeBy(5_000)

        assertFalse(isPageAtStart(2))
        composeRule.mainClock.advanceTimeBy(5_500)
        assertTrue(isPageAtStart(2))
    }

    @Test
    fun `scroll with duration covers the whole distance to a distant page`() {
        setPagerContent(itemCount = 10)

        performAnimated(action(typed = scrollToAction(
            "pager", DivActionScrollDestination.Index(IndexDestination(constant(9L))), duration = constant(10_000L),
        )))
        awaitScrollStart(observedIndex = 0)

        assertTrue(pageStarts(0).isNotEmpty(), "The first page is dragged away instead of being skipped")
        finishAnimations()
        assertPageAtStart(9)
    }

    @Test
    fun `scroll without duration animates to a distant page`() {
        setPagerContent(itemCount = 10)

        performAnimated(action(url = "div-action://set_current_item?id=pager&item=9"))
        awaitScrollEnd(observedIndex = 0)

        assertPageAtStart(9)
    }

    @Test
    fun `scroll_to_item_id addresses the pages left after one is hidden`() {
        setPagerContent()
        hideSecond.set(true)
        composeRule.waitForIdle()

        perform(action(url = "div-action://scroll_to_item_id?id=pager&item_id=item3&animated=false"))

        assertPageAtStart(3)
    }

    @Test
    fun `pager shown after all pages were hidden accepts actions`() {
        hideAll.set(true)
        setPagerContent()
        hideAll.set(false)
        composeRule.waitForIdle()

        perform(action(url = "div-action://set_current_item?id=pager&item=2&animated=false"))

        assertPageAtStart(2)
    }

    @Test
    fun `pager without visible pages reports the index`() {
        reporter.failOnError = false
        hideAll.set(true)
        setPagerContent()

        perform(action(url = "div-action://set_current_item?id=pager&item=0&animated=false"))

        assertEquals("Scroll index 0 is not in range [0, 0) for 'pager'", reporter.lastError)
    }

    private fun setPagerContent(
        itemCount: Int = 5,
        pageSize: Int = PAGER_SIZE,
        infiniteScroll: Boolean = false,
        orientation: DivPager.Orientation = DivPager.Orientation.HORIZONTAL,
        alignment: DivPager.ItemAlignment = DivPager.ItemAlignment.START,
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    ) {
        val isHorizontal = orientation == DivPager.Orientation.HORIZONTAL
        pagerData = data(pager(
            id = "pager",
            items = List(itemCount) { index ->
                text(
                    height = fixed(if (isHorizontal) 100 else pageSize),
                    id = "item$index",
                    selectedActions = listOf(action(url = "divkit-test://select?page=$index")),
                    text = constant(index.toString()),
                    visibility = visibilityExpression(
                        if (index == 1) "@{hide_all || hide_second ? 'gone' : 'visible'}" else "@{hide_all ? 'gone' : 'visible'}"
                    ),
                    width = fixed(if (isHorizontal) pageSize else 100),
                )
            },
            height = fixed(if (isHorizontal) 100 else PAGER_SIZE),
            width = fixed(if (isHorizontal) PAGER_SIZE else 100),
            infiniteScroll = infiniteScroll,
            orientation = orientation,
            scrollAxisAlignment = alignment,
        ))
        val configuration = divConfiguration {
            actionHandler = this@PagerScrollTest.actionHandler
            reporter = this@PagerScrollTest.reporter
            variableController = DivVariableController().apply {
                declare(hideAll)
                declare(hideSecond)
            }
        }
        composeRule.setContent {
            divContext = DivContext(baseContext = LocalContext.current, configuration = configuration)
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalLayoutDirection provides layoutDirection,
            ) {
                DivView(data = pagerData)
            }
        }
        composeRule.waitForIdle()
    }

    private fun perform(vararg actions: DivAction) {
        composeRule.runOnIdle {
            actions.forEach { divContext.debugFeatures.performAction(pagerData, it) }
        }
        composeRule.waitForIdle()
    }

    private fun performAnimated(action: DivAction) {
        composeRule.mainClock.autoAdvance = false
        perform(action)
    }

    private fun awaitScrollStart(observedIndex: Int) {
        val start = pageStarts(observedIndex)
        repeat(MAX_SCROLL_START_FRAMES) {
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.waitForIdle()
            if (pageStarts(observedIndex) != start) return
        }
        fail("The scroll has not started")
    }

    // Returns the animation time in milliseconds after the scroll has started.
    private fun awaitScrollEnd(observedIndex: Int): Long {
        awaitScrollStart(observedIndex)
        val startedAt = composeRule.mainClock.currentTime
        finishAnimations()
        return composeRule.mainClock.currentTime - startedAt
    }

    private fun finishAnimations() {
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()
    }

    private fun selectedPages(): List<String?> = actionHandler.handledActions
        .filter { it.source == DivActionSource.SELECTION }
        .map { it.url?.getQueryParameter("page") }

    private fun assertPageAtStart(index: Int) {
        assertTrue(
            isPageAtStart(index),
            "Page $index starts at ${pageStarts(index)}, the pager at ${pagerBounds().left}",
        )
    }

    private fun isPageAtStart(index: Int): Boolean =
        pageStarts(index).any { abs(it - pagerBounds().left) <= TOLERANCE }

    private fun pagerBounds(): Rect = composeRule.onNodeWithTag("pager").fetchSemanticsNode().boundsInRoot

    /** Unlike boundsInRoot, these bounds are not clipped by the pager. */
    private fun pageBounds(index: Int): Rect {
        val node = composeRule.onNodeWithTag("item$index", useUnmergedTree = true).fetchSemanticsNode()
        return Rect(node.positionInRoot, node.size.toSize())
    }

    /** Start positions of every laid out copy of the page: a cyclic pager composes several. */
    private fun pageStarts(index: Int): List<Float> =
        composeRule.onAllNodesWithTag("item$index", useUnmergedTree = true).fetchSemanticsNodes()
            .map { it.positionInRoot.x }
}
