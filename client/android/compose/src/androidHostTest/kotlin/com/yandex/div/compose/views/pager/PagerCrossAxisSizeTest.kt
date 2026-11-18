package com.yandex.div.compose.views.pager

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.pageSize
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivPager
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(AndroidJUnit4::class)
class PagerCrossAxisSizeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val crossSize = Variable.IntegerVariable("cross_size", 100)
    private val contentSize = Variable.IntegerVariable("content_size", 0)
    private val firstContentSize = Variable.IntegerVariable("first_content_size", 0)
    private val tallPageVisibility = Variable.StringVariable("tall_page_visibility", "visible")

    @Test
    fun `wrap_content pager grows with its page`() {
        setContent(pagerWithMatchParentPage())

        crossSize.set(200)
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("pager").assertHeightIsEqualTo(200.dp)
    }

    @Test
    fun `wrap_content pager shrinks with its page`() {
        crossSize.set(200)
        setContent(pagerWithMatchParentPage())

        crossSize.set(100)
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("pager").assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun `match_parent page stretches to the largest page`() {
        setContent(pagerWithMatchParentPage())

        composeRule.onNodeWithTag("match_parent_page").assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun `match_parent page content larger than the other pages grows the pager`() {
        setContent(pagerWithMatchParentPage())

        contentSize.set(150)
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("pager").assertHeightIsEqualTo(150.dp)
    }

    @Test
    fun `match_parent page content shrinking after growth shrinks the pager`() {
        setContent(pagerWithMatchParentPage())
        contentSize.set(150)
        composeRule.waitForIdle()

        contentSize.set(50)
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("pager").assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun `match_parent pages that expanded in turn shrink the pager to their content`() {
        firstContentSize.set(50)
        contentSize.set(50)
        setContent(pagerWithTwoMatchParentPages())
        for (size in listOf(firstContentSize, contentSize)) {
            size.set(200)
            composeRule.waitForIdle()
            size.set(50)
            composeRule.waitForIdle()
        }

        crossSize.set(10)
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("pager").assertHeightIsEqualTo(50.dp)
    }

    @Test
    fun `expanding the text of the current page grows the pager`() {
        setContent(expandablePager())
        val collapsedHeight = heightOf("pager")

        composeRule.onNodeWithTag("expand_2").performClick()
        composeRule.waitForIdle()

        assertTrue(heightOf("pager") > collapsedHeight, "Pager height stays at $collapsedHeight px")
    }

    @Test
    fun `expanding the text of the current page grows the pager to fit the page`() {
        setContent(expandablePager())

        composeRule.onNodeWithTag("expand_2").performClick()
        composeRule.waitForIdle()

        val buttonBottom = unclippedBottomOf("collapse_2")
        val pagerBottom = unclippedBottomOf("pager")
        assertTrue(buttonBottom <= pagerBottom, "COLLAPSE ends at $buttonBottom px, below the pager bottom at $pagerBottom px")
    }

    @Test
    fun `collapsing the expanded text shrinks the pager back`() {
        setContent(expandablePager())
        val collapsedHeight = heightOf("pager")
        composeRule.onNodeWithTag("expand_2").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("collapse_2").performClick()
        composeRule.waitForIdle()

        assertEquals(collapsedHeight, heightOf("pager"))
    }

    @Test
    fun `expanding the text keeps the selected page`() {
        setContent(expandablePager())

        composeRule.onNodeWithTag("expand_2").performClick()
        composeRule.waitForIdle()

        assertEquals(listOf("2"), selectedPages())
    }

    @Test
    fun `wrap_content vertical pager grows with its page`() {
        setContent(verticalPagerWithMatchParentPage())

        crossSize.set(200)
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("pager").assertWidthIsEqualTo(200.dp)
    }

    @Test
    fun `match_parent page of a vertical pager stretches to the widest page`() {
        setContent(verticalPagerWithMatchParentPage())

        composeRule.onNodeWithTag("match_parent_page").assertWidthIsEqualTo(100.dp)
    }

    @Test
    fun `wrap_content pager shrinks when its tallest page is hidden`() {
        setContent(
            pager(
                id = "pager",
                items = listOf(
                    text(height = fixed(100), text = constant("0")),
                    text(
                        height = fixed(200),
                        text = constant("1"),
                        visibility = visibilityExpression("@{tall_page_visibility}"),
                    ),
                ),
            )
        )

        tallPageVisibility.set("gone")
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("pager").assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun `wrap_content pager counts the page next to the viewport`() {
        setContent(
            pager(
                id = "pager",
                items = listOf(100, 100, 300).mapIndexed { index, height ->
                    text(height = fixed(height), text = constant(index.toString()))
                },
                layoutMode = pageSize(80.0),
                scrollAxisAlignment = DivPager.ItemAlignment.START,
            )
        )

        composeRule.onNodeWithTag("pager").assertHeightIsEqualTo(300.dp)
    }

    @Test
    fun `page resize keeps the selected page`() {
        setContent(
            pager(
                defaultItem = 1,
                id = "pager",
                items = List(3) { index ->
                    text(
                        height = if (index == 1) fixed(intExpression("@{cross_size}")) else fixed(100),
                        selectedActions = listOf(action(url = "divkit-test://select?page=$index")),
                        text = constant(index.toString()),
                    )
                },
            )
        )

        crossSize.set(200)
        composeRule.waitForIdle()

        assertEquals(listOf("1"), selectedPages())
    }

    private fun pagerWithMatchParentPage(): Div = pager(
        id = "pager",
        items = listOf(
            text(height = fixed(intExpression("@{cross_size}")), text = constant("0")),
            container(
                height = matchParent(),
                id = "match_parent_page",
                items = listOf(
                    text(height = fixed(intExpression("@{content_size}")), text = constant("1")),
                ),
            ),
        ),
    )

    private fun pagerWithTwoMatchParentPages(): Div = pager(
        defaultItem = 1,
        id = "pager",
        items = listOf(
            text(height = fixed(intExpression("@{cross_size}")), text = constant("0")),
            matchParentPage(contentSize = "first_content_size"),
            matchParentPage(contentSize = "content_size"),
        ),
    )

    private fun matchParentPage(contentSize: String): Div = container(
        height = matchParent(),
        items = listOf(
            text(height = fixed(intExpression("@{$contentSize}")), text = constant(contentSize)),
        ),
    )

    private fun expandablePager(): Div = pager(
        defaultItem = 2,
        id = "pager",
        infiniteScroll = true,
        items = List(3) { index ->
            container(
                items = listOf(
                    state(
                        id = "content_$index",
                        states = listOf(
                            state(stateId = "collapsed", div = text(maxLines = 3, text = constant(PAGE_TEXT))),
                            state(stateId = "expanded", div = text(text = constant(PAGE_TEXT))),
                        ),
                    ),
                    text(
                        actions = listOf(action(url = "div-action://set_state?state_id=0/content_$index/expanded")),
                        id = "expand_$index",
                        text = constant("EXPAND"),
                    ),
                    text(
                        actions = listOf(action(url = "div-action://set_state?state_id=0/content_$index/collapsed")),
                        id = "collapse_$index",
                        text = constant("COLLAPSE"),
                    ),
                ),
                selectedActions = listOf(action(url = "divkit-test://select?page=$index")),
            )
        },
        layoutMode = pageSize(80.0),
    )

    private fun verticalPagerWithMatchParentPage(): Div = pager(
        height = fixed(300),
        id = "pager",
        items = listOf(
            text(height = fixed(120), text = constant("0"), width = fixed(intExpression("@{cross_size}"))),
            container(height = fixed(120), id = "match_parent_page"),
        ),
        orientation = DivPager.Orientation.VERTICAL,
        width = wrapContent(),
    )

    private fun setContent(pager: Div) {
        val configuration = divConfiguration {
            actionHandler = this@PagerCrossAxisSizeTest.actionHandler
            reporter = TestReporter()
            variableController = DivVariableController().apply {
                declare(crossSize)
                declare(contentSize)
                declare(firstContentSize)
                declare(tallPageVisibility)
            }
        }
        composeRule.setContentWithDivContext(configuration) {
            DivView(data = data(pager))
        }
    }

    private fun unclippedBottomOf(tag: String): Float {
        val node = composeRule.onNodeWithTag(tag).fetchSemanticsNode()
        return node.positionInRoot.y + node.size.height
    }

    private fun heightOf(tag: String): Int = composeRule.onNodeWithTag(tag).fetchSemanticsNode().size.height

    private fun selectedPages(): List<String?> = actionHandler.handledActions
        .filter { it.source == DivActionSource.SELECTION }
        .map { it.url?.getQueryParameter("page") }
}

private const val PAGE_TEXT = "The collapsed state shows three lines of this text and the expanded state shows all of it, " +
    "so the page and the pager around it grow when the text expands and shrink again when it collapses."
