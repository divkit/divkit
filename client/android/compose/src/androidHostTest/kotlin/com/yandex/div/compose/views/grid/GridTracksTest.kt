package com.yandex.div.compose.views.grid

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.doubleExpression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.grid
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.separator
import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class GridTracksTest {

    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = variableController,
    )

    @Test
    fun `weighted tracks react to expression changes on both axes`() {
        val weight = Variable.DoubleVariable("weight", 1.0)
        variableController.declare(weight)
        val observedWeight = doubleExpression("@{weight}")

        setContent(
            grid(
                columnCount = constant(2L),
                width = fixed(120),
                height = fixed(120),
                items = listOf(
                    weightedSeparator("top-left", observedWeight, observedWeight),
                    weightedSeparator("top-right", constant(1.0), observedWeight),
                    weightedSeparator("bottom-left", observedWeight, constant(1.0)),
                    weightedSeparator("bottom-right", constant(1.0), constant(1.0)),
                ),
            )
        )

        assertNodeSize("top-left", width = 60, height = 60)
        assertNodeSize("bottom-right", width = 60, height = 60)

        weight.set(3.0)
        rule.waitForIdle()

        assertNodeSize("top-left", width = 90, height = 90)
        assertNodeSize("bottom-right", width = 30, height = 30)
    }

    @Test
    fun `match parent margins set the weighted track base size`() {
        val itemMargins = insets(start = 10, end = 10)
        setContent(
            grid(
                columnCount = constant(2L),
                width = fixed(10),
                height = fixed(10),
                items = listOf(
                    separator(
                        width = matchParent(constant(1.0)),
                        height = fixed(5),
                        margins = itemMargins,
                    ),
                    separator(
                        width = matchParent(constant(1.0)),
                        height = fixed(5),
                        margins = itemMargins,
                    ),
                    separator(
                        id = "first-marker",
                        width = fixed(1),
                        height = fixed(5),
                    ),
                    separator(
                        id = "second-marker",
                        width = fixed(1),
                        height = fixed(5),
                    ),
                ),
            )
        )

        val firstLeft = rule.onNodeWithTag("first-marker").fetchSemanticsNode().boundsInRoot.left
        val secondLeft = rule.onNodeWithTag("second-marker").fetchSemanticsNode().boundsInRoot.left

        assertEquals(20f, secondLeft - firstLeft)
    }

    @Test
    fun `resolved base size is applied to a non weighted track`() {
        val itemMargins = insets(start = 10, end = 10)
        setContent(
            grid(
                columnCount = constant(3L),
                width = fixed(40),
                height = fixed(15),
                items = listOf(
                    separator(width = matchParent(constant(1.0)), height = fixed(5)),
                    separator(width = fixed(5), height = fixed(5)),
                    separator(width = fixed(5), height = fixed(5)),
                    separator(width = fixed(1), height = fixed(5)),
                    separator(
                        width = matchParent(),
                        height = fixed(5),
                        margins = itemMargins,
                    ),
                    separator(width = fixed(1), height = fixed(5)),
                    separator(width = fixed(1), height = fixed(5)),
                    separator(
                        id = "second-marker",
                        width = fixed(1),
                        height = fixed(5),
                    ),
                    separator(
                        id = "third-marker",
                        width = fixed(1),
                        height = fixed(5),
                    ),
                ),
            )
        )

        val secondLeft = rule.onNodeWithTag("second-marker").fetchSemanticsNode().boundsInRoot.left
        val thirdLeft = rule.onNodeWithTag("third-marker").fetchSemanticsNode().boundsInRoot.left

        assertEquals(20f, thirdLeft - secondLeft)
    }

    private fun setContent(content: Div) {
        rule.setContentWithDivContext(configuration) {
            CompositionLocalProvider(LocalDensity provides Density(1f)) {
                DivView(data = data(content))
            }
        }
    }

    private fun assertNodeSize(tag: String, width: Int, height: Int) {
        val size = rule.onNodeWithTag(tag).fetchSemanticsNode().size
        assertEquals(width, size.width)
        assertEquals(height, size.height)
    }

    private fun weightedSeparator(
        id: String,
        widthWeight: Expression<Double>,
        heightWeight: Expression<Double>,
        margins: DivEdgeInsets? = null,
    ): Div = separator(
        id = id,
        width = matchParent(widthWeight),
        height = matchParent(heightWeight),
        margins = margins,
    )
}
