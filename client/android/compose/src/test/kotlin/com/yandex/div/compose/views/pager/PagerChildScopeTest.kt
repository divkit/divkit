package com.yandex.div.compose.views.pager

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContent
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.action
import com.yandex.div.test.data.alignmentVerticalExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.text
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibility
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A pager filters out the gone pages, aligns a page and runs its `selected_actions` on its behalf,
 * so it has to resolve them in the page scope; the card declares the same names with other values.
 */
@RunWith(AndroidJUnit4::class)
class PagerChildScopeTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()

    @Test
    fun `page visible by its local variable is shown`() {
        setContent(
            page(
                variables = listOf(variable("hidden", false)),
                visibility = "@{hidden ? 'gone' : 'visible'}",
            ),
            variables = listOf(variable("hidden", true)),
        )

        rule.onNodeWithTag("page").assertIsDisplayed()
    }

    @Test
    fun `page cross-axis alignment from its local variable is applied`() {
        setContent(
            page(
                alignmentVertical = alignmentVerticalExpression("@{align}"),
                height = 50,
                variables = listOf(variable("align", "bottom")),
            ),
            variables = listOf(variable("align", "top")),
        )

        val page = rule.onNodeWithTag("page").fetchSemanticsNode().boundsInRoot
        val pager = rule.onNodeWithTag("pager").fetchSemanticsNode().boundsInRoot
        assertEquals(50f * rule.density.density, page.top - pager.top, absoluteTolerance = 0.5f)
    }

    @Test
    fun `selected actions of a page are resolved in the page scope`() {
        setContent(
            page(
                selectedActions = listOf(
                    action(url = uriExpression("divkit-test://select?name=@{name}"))
                ),
                variables = listOf(variable("name", "page")),
            ),
            variables = listOf(variable("name", "card")),
        )

        rule.runOnIdle {
            assertEquals(
                listOf("page"),
                actionHandler.handledActions
                    .filter { it.source == DivActionSource.SELECTION }
                    .map { it.url?.getQueryParameter("name") },
            )
        }
    }

    private fun setContent(page: Div, variables: List<DivVariable>) {
        rule.setContent(
            configuration = divConfiguration {
                actionHandler = this@PagerChildScopeTest.actionHandler
                reporter = TestReporter()
            },
            data = data(
                pager(
                    height = fixed(100),
                    id = "pager",
                    items = listOf(page),
                    width = fixed(300),
                ),
                variables = variables,
            ),
        )
    }
}

private fun page(
    alignmentVertical: Expression<DivAlignmentVertical>? = null,
    height: Int = 100,
    selectedActions: List<DivAction>? = null,
    variables: List<DivVariable>,
    visibility: String? = null,
): Div = text(
    alignmentVertical = alignmentVertical,
    height = fixed(height),
    id = "page",
    selectedActions = selectedActions,
    text = constant("page"),
    variables = variables,
    visibility = visibility?.let(::visibilityExpression) ?: constant(DivVisibility.VISIBLE),
    width = fixed(300),
)
