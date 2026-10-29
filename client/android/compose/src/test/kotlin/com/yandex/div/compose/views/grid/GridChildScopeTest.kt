package com.yandex.div.compose.views.grid

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.doubleExpression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.grid
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivSize
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibility
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A grid reads the visibility, the size, the margins and the spans of a child on the child's
 * behalf, so it has to resolve them in the child scope; the card declares the same names with
 * other values.
 */
@RunWith(AndroidJUnit4::class)
class GridChildScopeTest {

    @get:Rule
    val rule = createComposeRule()

    private val configuration = divConfiguration { reporter = TestReporter() }

    @Test
    fun `child visible by its local variable is shown`() {
        setContent(
            grid(
                columnCount = constant(2L),
                items = listOf(
                    cell(
                        id = "item",
                        variables = listOf(variable("hidden", false)),
                        visibility = HIDDEN_VISIBILITY,
                    ),
                ),
            ),
            variables = listOf(variable("hidden", true)),
        )

        rule.onNodeWithTag("item").assertIsDisplayed()
    }

    @Test
    fun `child gone by its local variable does not size a track`() {
        setContent(
            grid(
                columnCount = constant(2L),
                id = "grid",
                items = listOf(
                    cell(
                        id = "item",
                        variables = listOf(variable("hidden", true)),
                        visibility = HIDDEN_VISIBILITY,
                        width = fixed(30),
                    ),
                    cell(id = "first"),
                    cell(id = "second"),
                ),
            ),
            variables = listOf(variable("hidden", false)),
        )

        assertEquals(10f, left("second") - left("grid"))
    }

    @Test
    fun `size and margins of a child from its local variables set the track size`() {
        setContent(
            grid(
                columnCount = constant(2L),
                id = "grid",
                items = listOf(
                    cell(
                        id = "item",
                        margins = DivEdgeInsets(start = intExpression("@{margin}")),
                        variables = listOf(variable("margin", 20L), variable("size", 10L)),
                        width = fixed(intExpression("@{size}")),
                    ),
                    cell(id = "next"),
                ),
            ),
            variables = listOf(variable("margin", 50L), variable("size", 40L)),
        )

        assertEquals(30f, left("next") - left("grid"))
    }

    @Test
    fun `weight of a child from its local variable sets the track share`() {
        setContent(
            grid(
                columnCount = constant(2L),
                items = listOf(
                    cell(
                        id = "item",
                        variables = listOf(variable("weight", 3.0)),
                        width = matchParent(doubleExpression("@{weight}")),
                    ),
                    cell(id = "next", width = matchParent(constant(1.0))),
                ),
                width = fixed(120),
            ),
            variables = listOf(variable("weight", 1.0)),
        )

        assertEquals(90, rule.onNodeWithTag("item").fetchSemanticsNode().size.width)
    }

    @Test
    fun `column span of a child from its local variable moves the next child to the next row`() {
        setContent(
            grid(
                columnCount = constant(2L),
                id = "grid",
                items = listOf(
                    cell(
                        columnSpan = intExpression("@{span}"),
                        id = "item",
                        variables = listOf(variable("span", 2L)),
                    ),
                    cell(id = "next"),
                ),
            ),
            variables = listOf(variable("span", 1L)),
        )

        assertEquals(10f, top("next") - top("grid"))
    }

    private fun setContent(content: Div, variables: List<DivVariable>) {
        rule.setContentWithDivContext(configuration) {
            CompositionLocalProvider(LocalDensity provides Density(1f)) {
                DivView(data = data(content, variables = variables))
            }
        }
    }

    private fun left(tag: String) =
        rule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.left

    private fun top(tag: String) =
        rule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.top
}

private fun cell(
    id: String,
    columnSpan: Expression<Long>? = null,
    margins: DivEdgeInsets? = null,
    variables: List<DivVariable>? = null,
    visibility: String? = null,
    width: DivSize = fixed(10),
): Div = text(
    columnSpan = columnSpan,
    height = fixed(10),
    id = id,
    margins = margins,
    text = constant(id),
    variables = variables,
    visibility = visibility?.let(::visibilityExpression) ?: constant(DivVisibility.VISIBLE),
    width = width,
)

private const val HIDDEN_VISIBILITY = "@{hidden ? 'gone' : 'visible'}"
