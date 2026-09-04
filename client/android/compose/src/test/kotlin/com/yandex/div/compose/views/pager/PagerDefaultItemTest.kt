package com.yandex.div.compose.views.pager

import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class PagerDefaultItemTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val alignment = Variable.StringVariable("alignment", "center")
    private val defaultItem = Variable.IntegerVariable("default_item", 1)
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = DivVariableController().apply {
            declare(alignment)
            declare(defaultItem)
        },
    )

    @Test
    fun `default item expression is only used for initial position`() {
        setPagerContent()

        val pagerBounds = composeRule.onNodeWithTag("pager").fetchSemanticsNode().boundsInRoot
        val initialItem = composeRule.onNodeWithTag("item1")
        val initialItemBounds = initialItem.fetchSemanticsNode().boundsInRoot
        assertEquals(pagerBounds.center.x, initialItemBounds.center.x, absoluteTolerance = 0.5f)

        defaultItem.set(0)
        alignment.set("start")
        composeRule.waitForIdle()

        alignment.set("center")
        composeRule.waitForIdle()

        val unchangedItemBounds = initialItem.fetchSemanticsNode().boundsInRoot
        assertEquals(pagerBounds.center.x, unchangedItemBounds.center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `alignment changes do not return to an offscreen default item after user scroll`() {
        setPagerContent()
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(6)
        val itemStart = composeRule.onNodeWithTag("item6").fetchSemanticsNode().boundsInRoot.left

        alignment.set("start")
        composeRule.waitForIdle()

        alignment.set("center")
        composeRule.waitForIdle()

        assertEquals(
            itemStart,
            composeRule.onNodeWithTag("item6").fetchSemanticsNode().boundsInRoot.left,
            absoluteTolerance = 0.5f,
        )
    }

    private fun setPagerContent() {
        composeRule.setContent(
            configuration = configuration,
            data = data(
                Div.Pager(
                    value = DivPager(
                        defaultItem = intExpression("@{default_item}"),
                        height = fixed(constant(100L)),
                        id = "pager",
                        items = List(10) { index ->
                            text(
                                height = fixed(constant(100L)),
                                id = "item$index",
                                text = constant(index.toString()),
                                width = fixed(constant(100L)),
                            )
                        },
                        layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
                        scrollAxisAlignment = itemAlignmentExpression("@{alignment}"),
                        width = fixed(constant(300L)),
                    )
                )
            ),
        )
    }

    private fun itemAlignmentExpression(
        expression: String,
    ): Expression<DivPager.ItemAlignment> = Expression.MutableExpression(
        expressionKey = "test",
        rawExpression = expression,
        converter = DivPager.ItemAlignment::fromString,
        validator = { true },
        logger = { fail(it.message) },
        typeHelper = TypeHelper.from(default = DivPager.ItemAlignment.CENTER) {
            it is DivPager.ItemAlignment
        },
    )
}
