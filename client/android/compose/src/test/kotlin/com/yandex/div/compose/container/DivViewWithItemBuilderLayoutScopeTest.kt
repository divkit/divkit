package com.yandex.div.compose.container

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivSize
import com.yandex.div2.DivVariable
import com.yandex.div2.DivWrapContentSize
import org.json.JSONObject
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.test.Test

/**
 * A container reads the visibility and the margins of a built item on the item's behalf, so it
 * has to resolve them in the item scope; the card declares the same names with other values.
 * The margins of a visible match_parent item limit a constrained wrap_content sibling.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
class DivViewWithItemBuilderLayoutScopeTest(private val case: Case) {
    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController().apply {
        declare(
            Variable.DictVariable("it", jsonObject("hidden" to true, "margin" to 40)),
            Variable.BooleanVariable("hidden", true),
            Variable.IntegerVariable("margin", 40)
        )
    }

    @Test
    fun `container reads item visibility and margins in the item scope`() {
        rule.setContent(
            configuration = DivConfiguration(
                reporter = TestReporter(),
                variableController = variableController
            ),
            data = data(case.orientation.container(case.source))
        )

        rule.onNodeWithTag("item").assertExists()
        // 200 dp minus the 16 dp item margin; the card values would give 160 dp or 200 dp.
        case.orientation.assertMainAxisSize(rule.onNodeWithTag("sibling"), 184)
    }

    class Case(val orientation: Orientation, val source: Source) {
        override fun toString() = "$orientation, $source"
    }

    enum class Orientation {
        HORIZONTAL {
            override fun container(source: Source) = container(
                orientation = DivContainer.Orientation.HORIZONTAL,
                width = fixed(200),
                itemBuilder = source.itemBuilder(
                    sibling = container(
                        width = constrainedWrapContent(),
                        items = listOf(text(text = constant("sibling"), width = fixed(300)))
                    ),
                    item = text(
                        text = constant("item"),
                        margins = DivEdgeInsets(start = intExpression(source.margin)),
                        variables = source.variables,
                        visibility = visibilityExpression(source.visibility),
                        width = matchParent()
                    )
                )
            )

            override fun assertMainAxisSize(node: SemanticsNodeInteraction, size: Int) {
                node.assertWidthIsEqualTo(size.dp)
            }
        },
        VERTICAL {
            override fun container(source: Source) = container(
                orientation = DivContainer.Orientation.VERTICAL,
                height = fixed(200),
                itemBuilder = source.itemBuilder(
                    sibling = container(
                        height = constrainedWrapContent(),
                        items = listOf(text(text = constant("sibling"), height = fixed(300)))
                    ),
                    item = text(
                        text = constant("item"),
                        height = matchParent(),
                        margins = DivEdgeInsets(top = intExpression(source.margin)),
                        variables = source.variables,
                        visibility = visibilityExpression(source.visibility)
                    )
                )
            )

            override fun assertMainAxisSize(node: SemanticsNodeInteraction, size: Int) {
                node.assertHeightIsEqualTo(size.dp)
            }
        };

        abstract fun container(source: Source): Div

        abstract fun assertMainAxisSize(node: SemanticsNodeInteraction, size: Int)
    }

    enum class Source(
        val element: JSONObject,
        val margin: String,
        val visibility: String,
        val variables: List<DivVariable>?,
    ) {
        DATA_ELEMENT(
            element = jsonObject("hidden" to false, "margin" to 16),
            margin = "@{it.getInteger('margin')}",
            visibility = "@{it.getBoolean('hidden') ? 'gone' : 'visible'}",
            variables = null
        ),
        LOCAL_VARIABLES(
            element = jsonObject(),
            margin = "@{margin}",
            visibility = "@{hidden ? 'gone' : 'visible'}",
            variables = listOf(variable("hidden", false), variable("margin", 16L))
        );

        /** The sibling is built first, so it is measured before the match_parent item. */
        fun itemBuilder(sibling: Div, item: Div): DivCollectionItemBuilder = itemBuilder(
            data = jsonArray(jsonObject(), element),
            prototypes = listOf(
                prototype(
                    div = sibling,
                    id = constant("sibling"),
                    selector = booleanExpression("@{index == 0}")
                ),
                prototype(div = item, id = constant("item"))
            )
        )
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases(): List<Case> = cases

        // The runner calls the provider once per case, so the list is built once.
        private val cases: List<Case> = Orientation.entries.flatMap { orientation ->
            Source.entries.map { source -> Case(orientation, source) }
        }
    }
}

private fun constrainedWrapContent() =
    DivSize.WrapContent(DivWrapContentSize(constrained = constant(true)))
