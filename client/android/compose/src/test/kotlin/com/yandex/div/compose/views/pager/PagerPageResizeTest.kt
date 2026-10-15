package com.yandex.div.compose.views.pager

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPageSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivPercentageSize
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class PagerPageResizeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val resizedPageWidth = Variable.IntegerVariable("resized_page_width", 100)
    private val itemSpacing = Variable.IntegerVariable("item_spacing", 0)
    private val startPadding = Variable.IntegerVariable("start_padding", 0)
    private val pagerWidth = Variable.IntegerVariable("pager_width", 300)

    @Test
    fun `resized current page returns to the snap position`() {
        setPagerContent(pageCount = 5, defaultItem = 2, resizedPage = 2)

        resizedPageWidth.set(160)
        composeRule.waitForIdle()

        assertEquals(0f, distanceToPagerCenter("item2"), absoluteTolerance = 0.5f)
    }

    @Test
    fun `resize that brings the next page closer to the snap position keeps the selected page`() {
        resizedPageWidth.set(200)
        setPagerContent(pageCount = 3, defaultItem = 0, resizedPage = 0)

        resizedPageWidth.set(100)
        composeRule.waitForIdle()

        assertTrue(distanceToPagerCenter("item1") < distanceToPagerCenter("item0"))
        assertEquals(listOf("0"), selectedPages())
    }

    @Test
    fun `scrolled current page stays centered and selected when item spacing changes`() {
        scrollToMiddlePage(DivPagerLayoutMode.PageContentSize(DivPageContentSize()))

        itemSpacing.set(120)
        composeRule.waitForIdle()

        assertEquals(0f, distanceToPagerCenter("item2"), absoluteTolerance = 0.5f)
        assertEquals(listOf("1", "2"), selectedPages())
    }

    @Test
    fun `spacing change before layout preserves the scrolled page and selected actions`() {
        val measurePager = mutableStateOf(true)
        scrollToMiddlePage(DivPagerLayoutMode.PageContentSize(DivPageContentSize()), measurePager)

        composeRule.runOnIdle { measurePager.value = false }
        composeRule.waitForIdle()
        composeRule.runOnIdle { itemSpacing.set(120) }
        composeRule.waitForIdle()
        composeRule.runOnIdle { measurePager.value = true }
        composeRule.waitForIdle()

        assertEquals(0f, distanceToPagerCenter("item2"), absoluteTolerance = 0.5f)
        assertEquals(listOf("1", "2"), selectedPages())
    }

    @Test
    fun `scrolled current page stays centered and selected when padding changes`() {
        scrollToMiddlePage()

        startPadding.set(120)
        composeRule.waitForIdle()

        assertEquals(0f, distanceToPagerCenter("item2"), absoluteTolerance = 0.5f)
        assertEquals(listOf("1", "2"), selectedPages())
    }

    @Test
    fun `scrolled current page stays centered and selected when pager size changes`() {
        scrollToMiddlePage()

        pagerWidth.set(200)
        composeRule.waitForIdle()

        assertEquals(0f, distanceToPagerCenter("item2"), absoluteTolerance = 0.5f)
        assertEquals(listOf("1", "2"), selectedPages())
    }

    private fun scrollToMiddlePage(
        layoutMode: DivPagerLayoutMode = DivPagerLayoutMode.PageSize(
            DivPageSize(pageWidth = DivPercentageSize(value = constant(50.0)))
        ),
        measurePager: State<Boolean>? = null,
    ) {
        setPagerContent(
            pageCount = 7,
            defaultItem = 1,
            resizedPage = 2,
            layoutMode = layoutMode,
            measurePager = measurePager,
        )
        composeRule.onNodeWithTag("pager").performTouchInput { swipeLeft() }
        composeRule.waitForIdle()
        assertEquals(listOf("1", "2"), selectedPages())
    }

    private fun setPagerContent(
        pageCount: Int,
        defaultItem: Int,
        resizedPage: Int,
        layoutMode: DivPagerLayoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
        measurePager: State<Boolean>? = null,
    ) {
        val data = data(
            Div.Pager(
                value = DivPager(
                    defaultItem = constant(defaultItem.toLong()),
                    height = fixed(100),
                    id = "pager",
                    itemSpacing = DivFixedSize(value = intExpression("@{item_spacing}")),
                    paddings = DivEdgeInsets(left = intExpression("@{start_padding}")),
                    items = List(pageCount) { index ->
                        text(
                            height = fixed(100),
                            id = "item$index",
                            selectedActions = listOf(action(url = "divkit-test://select?page=$index")),
                            text = constant(index.toString()),
                            width = if (layoutMode is DivPagerLayoutMode.PageSize) {
                                matchParent()
                            } else if (index == resizedPage) {
                                fixed(intExpression("@{resized_page_width}"))
                            } else {
                                fixed(100)
                            },
                        )
                    },
                    layoutMode = layoutMode,
                    scrollAxisAlignment = constant(DivPager.ItemAlignment.CENTER),
                    width = fixed(intExpression("@{pager_width}")),
                )
            )
        )
        val configuration = divConfiguration {
            actionHandler = this@PagerPageResizeTest.actionHandler
            reporter = TestReporter()
            variableController = DivVariableController().apply {
                declare(resizedPageWidth)
                declare(itemSpacing)
                declare(startPadding)
                declare(pagerWidth)
            }
        }
        composeRule.setContentWithDivContext(configuration) {
            if (measurePager == null) {
                DivView(data = data)
            } else {
                Layout(
                    modifier = Modifier.size(300.dp, 100.dp),
                    content = { DivView(data = data) },
                ) { measurables, constraints ->
                    val pager = if (measurePager.value) measurables.single().measure(constraints) else null
                    layout(constraints.maxWidth, constraints.maxHeight) {
                        pager?.placeRelative(0, 0)
                    }
                }
            }
        }
    }

    private fun distanceToPagerCenter(tag: String): Float {
        val pagerCenter = composeRule.onNodeWithTag("pager").fetchSemanticsNode().boundsInRoot.center.x
        return abs(composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.center.x - pagerCenter)
    }

    private fun selectedPages(): List<String?> = actionHandler.handledActions
        .filter { it.source == DivActionSource.SELECTION }
        .map { it.url?.getQueryParameter("page") }
}
