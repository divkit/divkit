package com.yandex.div.compose.views.text

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestImageLoaderConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.doubleExpression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textImage
import com.yandex.div.test.data.textRange
import com.yandex.div.test.data.throwingErrorLogger
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivText
import com.yandex.div2.DivTextAlignmentVertical
import com.yandex.div2.DivTextAlignmentVertical.BASELINE
import com.yandex.div2.DivTextAlignmentVertical.BOTTOM
import com.yandex.div2.DivTextAlignmentVertical.TOP
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(AndroidJUnit4::class)
class DivTextRangeMetricsTest {

    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()
    private val configuration = DivConfiguration(
        imageLoaderConfiguration = TestImageLoaderConfiguration(),
        reporter = TestReporter(),
        variableController = variableController,
    )

    @Test
    fun `range line height follows its expression`() {
        val lineHeight = Variable.IntegerVariable("line_height", 20)
        variableController.declare(lineHeight)
        setContent(textWithRanges(textRange(lineHeight = intExpression("@{line_height}"))))

        lineHeight.set(40)

        rule.onNodeWithTag("text").assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun `range top offset follows its expression`() {
        val topOffset = Variable.IntegerVariable("top_offset", 0)
        variableController.declare(topOffset)
        setContent(
            textWithRanges(textRange(lineHeight = constant(20), topOffset = intExpression("@{top_offset}")))
        )

        topOffset.set(10)

        rule.onNodeWithTag("text").assertHeightIsEqualTo(30.dp)
    }

    @Test
    fun `range baseline offset raises a line without line height`() {
        val baselineOffset = Variable.DoubleVariable("baseline_offset", 0.0)
        variableController.declare(baselineOffset)
        setContent(textWithRanges(textRange(baselineOffset = doubleExpression("@{baseline_offset}"))))
        val height = rule.onNodeWithTag("text").getUnclippedBoundsInRoot().height

        baselineOffset.set(8.0)

        rule.onNodeWithTag("text").assertHeightIsEqualTo(height + 8.dp)
    }

    @Test
    fun `range alignment follows its expression`() {
        val alignment = Variable.StringVariable("alignment", "baseline")
        variableController.declare(alignment)
        setContent(
            textWithRanges(
                textRange(fontSize = 8, alignmentVertical = alignmentExpression("@{alignment}")),
                lineHeight = 30,
            )
        )
        assertTrue(renderedText().spanStyles.none { it.item.baselineShift != null })

        alignment.set("top")

        assertTrue(renderedText().spanStyles.any { it.item.baselineShift != null })
    }

    @Test
    fun `later range overrides line height where ranges overlap`() {
        setContent(textWithRanges(textRange(lineHeight = constant(40)), textRange(lineHeight = constant(20))))

        rule.onNodeWithTag("text").assertHeightIsEqualTo(20.dp)
    }

    @Test
    fun `range top offset applies only to the line where the range starts`() {
        setContent(
            textWithRanges(
                textRange(lineHeight = constant(20), topOffset = constant(10)),
                textRange(start = 6, lineHeight = constant(20)),
                text = "First\nSecond",
            )
        )

        rule.onNodeWithTag("text").assertHeightIsEqualTo(50.dp)
    }

    @Test
    fun `range baseline offset disables vertical alignment`() {
        val aligned = textRange(fontSize = 6, alignmentVertical = constant(DivTextAlignmentVertical.TOP))
        setContent(
            container(
                items = listOf(
                    textWithRanges(aligned, id = "aligned"),
                    textWithRanges(aligned, textRange(baselineOffset = constant(4.0)), id = "shifted"),
                )
            )
        )
        val height = rule.onNodeWithTag("aligned").getUnclippedBoundsInRoot().height

        rule.onNodeWithTag("shifted").assertHeightIsEqualTo(height + 4.dp)
    }

    @Test
    fun `last visible line takes the rest of a truncated text`() {
        val text = "Text with several words that wrap onto more than two lines"
        setContent(
            textWithRanges(
                textRange(end = 4, fontSize = 16),
                textRange(start = 34, end = 38, fontSize = 16),
                text = text,
                lineHeight = 20,
                maxLines = 2,
                width = 100,
            )
        )

        assertEquals(text.length, textLayout().getLineEnd(1))
    }

    @Test
    fun `range line height on the visible end of a start truncated line sets its height`() {
        setContent(
            text(
                id = "text",
                text = constant("Text with several words that wrap onto more than two lines"),
                maxLines = 1,
                truncate = DivText.Truncate.START,
                width = fixed(100),
                ranges = listOf(textRange(start = 53, lineHeight = constant(60))),
            )
        )

        rule.onNodeWithTag("text").assertHeightIsEqualTo(60.dp)
    }

    @Test
    fun `range line height on the visible end of a middle truncated line sets its height`() {
        setContent(
            text(
                id = "text",
                text = constant("Text with several words that wrap onto more than two lines"),
                maxLines = 1,
                truncate = DivText.Truncate.MIDDLE,
                width = fixed(100),
                ranges = listOf(textRange(start = 53, lineHeight = constant(60))),
            )
        )

        rule.onNodeWithTag("text").assertHeightIsEqualTo(60.dp)
    }

    @Test
    fun `ranges on lines hidden by max lines do not change the last visible line`() {
        val text = "First\nSecond\nThird"
        setContent(
            container(
                items = listOf(
                    textWithRanges(textRange(end = 5, lineHeight = constant(20)), text = text, id = "plain", maxLines = 2),
                    textWithRanges(
                        textRange(end = 5, lineHeight = constant(20)),
                        textRange(start = 13, lineHeight = constant(60), topOffset = constant(30)),
                        text = text,
                        id = "hidden",
                        maxLines = 2,
                    ),
                )
            )
        )
        val height = rule.onNodeWithTag("plain").getUnclippedBoundsInRoot().height

        rule.onNodeWithTag("hidden").assertHeightIsEqualTo(height)
    }

    @Test
    fun `empty line after a trailing line feed keeps the text line height without the top offset`() {
        val range = textRange(end = 4, topOffset = constant(10))
        setContent(
            container(
                items = listOf(
                    textWithRanges(range, text = "Text\n", id = "limited", lineHeight = 30, maxLines = 2),
                    textWithRanges(range, text = "Text\n", id = "unlimited", lineHeight = 30),
                )
            )
        )

        rule.onNodeWithTag("limited").assertHeightIsEqualTo(70.dp)
        rule.onNodeWithTag("unlimited").assertHeightIsEqualTo(70.dp)
    }

    @Test
    fun `range top offset is not repeated on hyphenated lines`() {
        val text = "Rent\u00ADgeno\u00ADelectro\u00ADcardio\u00ADgraphic"
        setContent(
            container(
                items = listOf(
                    textWithRanges(text = text, id = "plain", width = 60),
                    textWithRanges(textRange(end = 4, topOffset = constant(10)), text = text, id = "offset", width = 60),
                )
            )
        )
        val height = rule.onNodeWithTag("plain").getUnclippedBoundsInRoot().height

        rule.onNodeWithTag("offset").assertHeightIsEqualTo(height + 10.dp)
        assertTrue(VISIBLE_HYPHEN in renderedText("offset").text)
    }

    @Test
    fun `trailing spaces before a line feed hang like in plain text`() {
        setContent(
            textWithRanges(
                textRange(end = 1, fontSize = 12),
                text = "a" + " ".repeat(40) + "\nb",
                lineHeight = 16,
                width = 100,
            )
        )

        rule.onNodeWithTag("text").assertHeightIsEqualTo(32.dp)
    }

    @Test
    fun `lines split at a line feed keep the wrap content width of plain text`() {
        val text = "Widest line  \nSecond"
        setContent(
            container(
                items = listOf(
                    text(id = "plain", text = constant(text), width = wrapContent()),
                    text(
                        id = "split",
                        text = constant(text),
                        width = wrapContent(),
                        ranges = listOf(textRange(end = 1, lineHeight = constant(30))),
                    ),
                )
            )
        )
        val width = rule.onNodeWithTag("plain").getUnclippedBoundsInRoot().width

        rule.onNodeWithTag("split").assertWidthIsEqualTo(width)
    }

    @Test
    fun `images taller than the text line height align within the box of their line`() {
        setContent(
            text(
                id = "text",
                text = constant("Line\nText"),
                lineHeight = 16,
                images = listOf(
                    textImage(url = IMAGE_URL, start = 6, height = 24, alignmentVertical = TOP, description = "top"),
                    textImage(url = IMAGE_URL, start = 7, height = 24, alignmentVertical = BASELINE, description = "baseline"),
                    textImage(url = IMAGE_URL, start = 8, height = 24, alignmentVertical = BOTTOM, description = "bottom"),
                ),
                ranges = listOf(textRange(start = 5, lineHeight = constant(40), topOffset = constant(10))),
            )
        )
        val top = rule.onNodeWithTag("text").getUnclippedBoundsInRoot().top
        val layout = textLayout()

        assertPosition(top, layout.getLineTop(1), imageBounds("top").top)
        assertPosition(top, layout.getLineBaseline(1), imageBounds("baseline").bottom)
        assertPosition(top, layout.getLineBottom(1), imageBounds("bottom").bottom)
    }

    @Test
    fun `lines with images or font ranges keep the text line height before a line feed`() {
        setContent(
            text(
                id = "text",
                text = constant("First\nSecond"),
                lineHeight = 16,
                images = listOf(
                    textImage(url = IMAGE_URL, start = 2, height = 24),
                    textImage(url = IMAGE_URL, start = 8, height = 10, alignmentVertical = TOP),
                ),
                ranges = listOf(textRange(end = 1, fontSize = 12)),
            )
        )
        val layout = textLayout()

        rule.onNodeWithTag("text").assertHeightIsEqualTo(32.dp)
        assertEquals(layout.getLineBaseline(0) - layout.getLineTop(0), layout.getLineBaseline(1) - layout.getLineTop(1))
    }

    @Test
    fun `last visible line with an image keeps the text line height under max lines`() {
        setContent(
            text(
                id = "text",
                text = constant("First\nSecond"),
                lineHeight = 16,
                maxLines = 1,
                images = listOf(textImage(url = IMAGE_URL, start = 2, height = 24)),
            )
        )

        rule.onNodeWithTag("text").assertHeightIsEqualTo(16.dp)
    }

    @Test
    fun `text split at a line feed keeps the line feed for accessibility`() {
        setContent(textWithRanges(textRange(end = 5, lineHeight = constant(40)), text = "First\nSecond"))

        rule.onNodeWithTag("text").assertContentDescriptionEquals("First\nSecond")
    }

    private companion object {
        const val IMAGE_URL = "https://image"
    }

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }

    private fun textWithRanges(
        vararg ranges: DivText.Range,
        text: String = "Text",
        id: String = "text",
        lineHeight: Long? = null,
        maxLines: Long? = null,
        width: Int? = null,
    ): Div {
        return text(
            id = id,
            text = constant(text),
            ranges = ranges.toList(),
            lineHeight = lineHeight,
            maxLines = maxLines,
            width = width?.let { fixed(it) } ?: matchParent(),
        )
    }

    private fun renderedText(tag: String = "text") = rule.onNodeWithTag(tag)
        .fetchSemanticsNode().config[SemanticsProperties.Text].single()

    private fun imageBounds(description: String) =
        rule.onNodeWithContentDescription(description).getUnclippedBoundsInRoot()

    private fun assertPosition(textTop: Dp, linePx: Float, actual: Dp) {
        val expected = textTop + with(rule.density) { linePx.toDp() }
        assertEquals(expected.value, actual.value, absoluteTolerance = 0.5f)
    }

    private fun textLayout(): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        rule.onNode(
            hasAnyAncestor(hasTestTag("text")) and SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult),
            useUnmergedTree = true,
        ).fetchSemanticsNode().config[SemanticsActions.GetTextLayoutResult].action?.invoke(results)
        return results.single()
    }

    private fun alignmentExpression(expression: String): Expression<DivTextAlignmentVertical> {
        return Expression.MutableExpression(
            expressionKey = "test",
            rawExpression = expression,
            converter = DivTextAlignmentVertical::fromString,
            validator = { true },
            logger = throwingErrorLogger,
            typeHelper = TypeHelper.from(default = DivTextAlignmentVertical.BASELINE) {
                it is DivTextAlignmentVertical
            },
        )
    }
}
