package com.yandex.div.compose.views.text

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getFirstLinkBounds
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performFirstLinkClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.action
import com.yandex.div.test.data.arrayExpression
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.doubleExpression
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textRange
import com.yandex.div.test.data.uriExpression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivText
import com.yandex.div2.DivTextRangeMask
import com.yandex.div2.DivTextRangeMaskSolid
import org.json.JSONArray
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivTextRangeBuilderTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val variableController = DivVariableController()
    private val configuration = DivConfiguration(
        actionHandler = actionHandler,
        reporter = TestReporter(),
        variableController = variableController,
    )

    @Test
    fun `range is built from data element`() {
        setContent(
            textWithRangeBuilder(rangeBuilder(constant(jsonArray(jsonObject("start" to 2, "end" to 5))))),
        )

        val span = renderedText().spanStyles.single()
        assertEquals(2, span.start)
        assertEquals(5, span.end)
    }

    @Test
    fun `ranges follow builder data`() {
        val source = Variable.ArrayVariable("source", jsonArray(jsonObject("start" to 0, "end" to 3)))
        variableController.declare(source)
        setContent(textWithRangeBuilder(rangeBuilder(arrayExpression("@{source}"))))

        source.set(jsonArray(jsonObject("start" to 4, "end" to 7)))

        val span = renderedText().spanStyles.single()
        assertEquals(4, span.start)
        assertEquals(7, span.end)
    }

    @Test
    fun `builder ranges replace static ranges`() {
        setContent(
            textWithRangeBuilder(
                rangeBuilder = rangeBuilder(constant(jsonArray(jsonObject("start" to 4, "end" to 7)))),
                ranges = listOf(textRange(start = 0, end = 3, fontSize = 20)),
            )
        )

        val span = renderedText().spanStyles.single()
        assertEquals(4, span.start)
        assertEquals(7, span.end)
    }

    @Test
    fun `range style reads its data element`() {
        setContent(
            textWithRangeBuilder(
                rangeBuilder(
                    data = constant(jsonArray(jsonObject("start" to 0, "end" to 3, "size" to 20))),
                    range = builtRange(fontSize = intExpression("@{it.getInteger('size')}")),
                )
            )
        )

        assertEquals(20.sp, renderedText().spanStyles.single().item.fontSize)
    }

    @Test
    fun `first matching prototype builds the range`() {
        setContent(
            textWithRangeBuilder(
                DivText.RangeBuilder(
                    data = constant(jsonArray(jsonObject("start" to 0, "end" to 3))),
                    prototypes = listOf(
                        DivText.RangeBuilder.Prototype(range = builtRange(fontWeight = DivFontWeight.BOLD)),
                        DivText.RangeBuilder.Prototype(range = builtRange(fontWeight = DivFontWeight.LIGHT)),
                    ),
                )
            )
        )

        assertEquals(FontWeight.Bold, renderedText().spanStyles.single().item.fontWeight)
    }

    @Test
    fun `prototype after a false selector builds the range`() {
        setContent(
            textWithRangeBuilder(
                DivText.RangeBuilder(
                    data = constant(jsonArray(jsonObject("start" to 0, "end" to 3))),
                    prototypes = listOf(
                        DivText.RangeBuilder.Prototype(
                            range = builtRange(fontWeight = DivFontWeight.BOLD),
                            selector = constant(false),
                        ),
                        DivText.RangeBuilder.Prototype(range = builtRange(fontWeight = DivFontWeight.LIGHT)),
                    ),
                )
            )
        )

        assertEquals(FontWeight.Light, renderedText().spanStyles.single().item.fontWeight)
    }

    @Test
    fun `selector reacts to card variable`() {
        val isShown = Variable.BooleanVariable("is_shown", false)
        variableController.declare(isShown)
        setContent(
            textWithRangeBuilder(
                rangeBuilder(
                    data = constant(jsonArray(jsonObject("start" to 0, "end" to 3))),
                    selector = booleanExpression("@{is_shown}"),
                )
            )
        )

        isShown.set(true)

        assertEquals(1, renderedText().spanStyles.size)
    }

    @Test
    fun `range reads its data element under data element name`() {
        setContent(
            textWithRangeBuilder(
                DivText.RangeBuilder(
                    data = constant(jsonArray(jsonObject("start" to 4, "end" to 7))),
                    dataElementName = "row",
                    prototypes = listOf(
                        DivText.RangeBuilder.Prototype(
                            range = DivText.Range(
                                end = intExpression("@{row.getInteger('end')}"),
                                fontWeight = constant(DivFontWeight.BOLD),
                                start = intExpression("@{row.getInteger('start')}"),
                            ),
                        )
                    ),
                )
            )
        )

        val span = renderedText().spanStyles.single()
        assertEquals(4, span.start)
        assertEquals(7, span.end)
    }

    @Test
    fun `range reads index of its data element`() {
        setContent(
            textWithRangeBuilder(
                rangeBuilder(
                    data = constant(jsonArray(jsonObject(), jsonObject())),
                    range = DivText.Range(
                        end = intExpression("@{index * 4 + 3}"),
                        fontWeight = constant(DivFontWeight.BOLD),
                        start = intExpression("@{index * 4}"),
                    ),
                )
            )
        )

        assertEquals(listOf(0 to 3, 4 to 7), renderedText().spanStyles.map { it.start to it.end })
    }

    @Test
    fun `range mask reads its data element`() {
        setContent(
            textWithRangeBuilder(
                rangeBuilder(
                    data = constant(
                        jsonArray(
                            jsonObject("start" to 0, "end" to 3, "hidden" to true),
                            jsonObject("start" to 4, "end" to 7, "hidden" to false),
                        )
                    ),
                    range = builtRange(
                        mask = DivTextRangeMask.Solid(
                            DivTextRangeMaskSolid(
                                color = constant(0xFF000000.toInt()),
                                isEnabled = booleanExpression("@{it.getBoolean('hidden')}"),
                            )
                        ),
                    ),
                )
            )
        )

        val maskedSpans = renderedText().spanStyles.filter { it.item.shadow == Shadow.None }
        assertEquals(listOf(0 to 3), maskedSpans.map { it.start to it.end })
    }

    @Test
    fun `range baseline offset reads its data element`() {
        setContent(
            textWithRangeBuilder(
                rangeBuilder(
                    data = constant(
                        jsonArray(
                            jsonObject("start" to 0, "end" to 3, "offset" to 0.0),
                            jsonObject("start" to 4, "end" to 7, "offset" to 5.0),
                        )
                    ),
                    range = builtRange(baselineOffset = doubleExpression("@{it.getNumber('offset')}")),
                )
            )
        )

        val shiftedSpans = renderedText().spanStyles.filter { it.item.baselineShift != null }
        assertEquals(listOf(4 to 7), shiftedSpans.map { it.start to it.end })
    }

    @Test
    fun `data element without matching prototype adds no range`() {
        setContent(
            textWithRangeBuilder(
                rangeBuilder(
                    data = constant(
                        jsonArray(
                            jsonObject("start" to 0, "end" to 3, "kind" to "plain"),
                            jsonObject("start" to 4, "end" to 7, "kind" to "bold"),
                        )
                    ),
                    selector = booleanExpression("@{it.getString('kind') == 'bold'}"),
                )
            )
        )

        val span = renderedText().spanStyles.single()
        assertEquals(4, span.start)
        assertEquals(7, span.end)
    }

    @Test
    fun `tap on a built range triggers its actions with its data element`() {
        setContent(
            textWithRangeBuilder(
                rangeBuilder(
                    data = constant(jsonArray(jsonObject("start" to 0, "end" to 3, "id" to "first"))),
                    range = builtRange(actions = listOf(action(url = uriExpression("test://@{it.getString('id')}")))),
                )
            )
        )

        rule.onNodeWithTag("text").performFirstLinkClick()

        assertEquals(actionData(url = "test://first"), actionHandler.handledAction)
    }

    @Test
    fun `built range action keeps its data element after a preceding element is removed`() {
        val source = Variable.ArrayVariable(
            "source",
            jsonArray(
                jsonObject("start" to 0, "end" to 3, "id" to "first"),
                jsonObject("start" to 4, "end" to 7, "id" to "second"),
            ),
        )
        variableController.declare(source)
        setContent(
            textWithRangeBuilder(
                rangeBuilder(
                    data = arrayExpression("@{source}"),
                    range = builtRange(actions = listOf(action(url = uriExpression("test://@{it.getString('id')}")))),
                )
            )
        )

        source.set(jsonArray(jsonObject("start" to 4, "end" to 7, "id" to "second")))
        rule.onNodeWithTag("text").performFirstLinkClick()

        assertEquals(actionData(url = "test://second"), actionHandler.handledAction)
    }

    @Test
    fun `tap next to a built range triggers element actions`() {
        setContent(
            textWithRangeBuilder(
                rangeBuilder = rangeBuilder(
                    data = constant(jsonArray(jsonObject("start" to 0, "end" to 1))),
                    range = builtRange(actions = listOf(action(url = "test://range"))),
                ),
                action = action(url = "test://element"),
            )
        )
        val rangeBounds = requireNotNull(rule.onNodeWithTag("text").getFirstLinkBounds())

        rule.onNodeWithTag("text").performTouchInput {
            click(Offset(rangeBounds.right + 10f, rangeBounds.center.y))
        }

        assertEquals(actionData(url = "test://element"), actionHandler.handledAction)
    }

    private fun renderedText(): AnnotatedString {
        rule.waitForIdle()
        return rule.onNodeWithTag("text")
            .fetchSemanticsNode()
            .config[SemanticsProperties.Text]
            .single()
    }

    private fun builtRange(
        actions: List<DivAction>? = null,
        baselineOffset: Expression<Double> = constant(0.0),
        fontSize: Expression<Long>? = null,
        fontWeight: DivFontWeight = DivFontWeight.BOLD,
        mask: DivTextRangeMask? = null,
    ) = DivText.Range(
        actions = actions,
        baselineOffset = baselineOffset,
        end = intExpression("@{it.getInteger('end')}"),
        fontSize = fontSize,
        fontWeight = constant(fontWeight),
        mask = mask,
        start = intExpression("@{it.getInteger('start')}"),
    )

    private fun rangeBuilder(
        data: Expression<JSONArray>,
        selector: Expression<Boolean> = constant(true),
        range: DivText.Range = builtRange(),
    ) = DivText.RangeBuilder(
        data = data,
        prototypes = listOf(DivText.RangeBuilder.Prototype(range = range, selector = selector)),
    )

    private fun textWithRangeBuilder(
        rangeBuilder: DivText.RangeBuilder,
        action: DivAction? = null,
        ranges: List<DivText.Range>? = null,
    ) = text(
        id = "text",
        action = action,
        rangeBuilder = rangeBuilder,
        ranges = ranges,
        text = constant("abc defg hij"),
    )

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }
}
