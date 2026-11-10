package com.yandex.div.compose.views.text

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestImageLoaderConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.animatedTextGradient
import com.yandex.div.test.data.arrayExpression
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.separator
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textImage
import com.yandex.div.test.data.textRange
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivSolidBackground
import com.yandex.div2.DivText
import com.yandex.div2.DivTextRangeBackground
import org.json.JSONArray
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Truncation is driven by real text measurement, which the legacy Robolectric graphics
// pipeline does not provide: without NATIVE the text always reports as fitting.
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(AndroidJUnit4::class)
class DivTextEllipsisTest {

    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()

    private val configuration = DivConfiguration(
        imageLoaderConfiguration = TestImageLoaderConfiguration(),
        reporter = TestReporter(),
        variableController = variableController
    )

    @Test
    fun `custom ellipsis is appended to the truncated text`() {
        setContent(
            text(
                id = "text",
                text = constant("A very long text that does not fit into a single line"),
                maxLines = 1,
                ellipsis = ellipsis(constant("… more")),
                width = fixed(100)
            )
        )

        rule.onNodeWithTag("text")
            .assert(textStartsWith("A very"))
            .assert(textEndsWith("… more"))
    }

    @Test
    fun `custom ellipsis is not applied when the text fits`() {
        setContent(
            text(
                id = "text",
                text = constant("Short"),
                maxLines = 1,
                ellipsis = ellipsis(constant("… more")),
                width = fixed(100)
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("Short")
    }

    @Test
    fun `custom ellipsis wider than line renders empty text`() {
        setContent(
            text(
                id = "text",
                text = constant("Text"),
                maxLines = 1,
                ellipsis = ellipsis(constant("… more")),
                width = fixed(1)
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("")
    }

    @Test
    fun `custom ellipsis renders empty text when height allows zero visible lines`() {
        setContent(
            text(
                id = "text",
                text = constant("Text"),
                maxLines = 1,
                autoEllipsize = constant(true),
                ellipsis = ellipsis(constant("… more")),
                height = fixed(0),
                width = fixed(100),
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("")
    }

    @Test
    fun `custom ellipsis does not split an inline image at the truncation boundary`() {
        // Arrange
        val content = text(
            id = "text",
            text = constant("AAAAAA"),
            fontSize = 20,
            maxLines = 1,
            ellipsis = ellipsis(constant("...")),
            images = listOf(
                textImage(
                    url = "https://divkit.tech/image.png",
                    start = 2,
                )
            ),
            width = fixed(70),
        )

        // Act
        setContent(content)

        // Assert
        val rendered = rule.onNodeWithTag("text")
            .fetchSemanticsNode()
            .config[SemanticsProperties.Text]
            .single()
        val imageAnnotations = rendered.getStringAnnotations(0, rendered.length)
            .filter { annotation -> annotation.item == "inline-image-0" }
        assertTrue(rendered.text.startsWith("AA"))
        assertTrue(rendered.text.endsWith("..."))
        assertTrue(
            imageAnnotations.isEmpty() || imageAnnotations.single().let { annotation ->
                annotation.end - annotation.start == 1 &&
                    rendered.text.substring(annotation.start, annotation.end) == "\u2060"
            }
        )
    }

    @Test
    fun `standard ellipsis keeps native rendering before emoji grapheme`() {
        // Arrange
        val sourceText = "prefix 👨‍👩‍👧‍👦 suffix"

        // Act
        setContent(
            text(
                id = "text",
                text = constant(sourceText),
                fontSize = 20,
                maxLines = 1,
                width = fixed(75)
            )
        )

        // Assert
        rule.onNodeWithTag("text").assertTextEquals(sourceText)
    }

    @Test
    fun `standard word ellipsis renders the configured suffix`() {
        setContent(
            text(
                id = "text",
                text = constant("A long text that does not fit"),
                truncatePolicy = DivText.TruncatePolicy.WORD,
                maxLines = 1,
                width = fixed(100)
            )
        )

        rule.onNodeWithTag("text")
            .assert(textEndsWith("…"))
    }

    @Test
    fun `word ellipsis exposes full source text to accessibility`() {
        val sourceText = "A long text that does not fit"
        setContent(
            text(
                id = "text",
                text = constant(sourceText),
                truncatePolicy = DivText.TruncatePolicy.WORD,
                maxLines = 1,
                width = fixed(100),
            )
        )

        rule.onNodeWithTag("text").assertContentDescriptionEquals(sourceText)
    }

    @Test
    fun `ellipsis is not applied without max_lines`() {
        setContent(
            text(
                id = "text",
                text = constant("A very long text that does not fit into a single line"),
                ellipsis = ellipsis(constant("… more")),
                width = fixed(100)
            )
        )

        rule.onNodeWithTag("text")
            .assertTextEquals("A very long text that does not fit into a single line")
    }

    @Test
    fun `ellipsis changes when its variable changes`() {
        val variable = Variable.StringVariable("suffix", "… more")
        variableController.declare(variable)

        setContent(
            text(
                id = "text",
                text = constant("A very long text that does not fit into a single line"),
                maxLines = 1,
                ellipsis = ellipsis(expression("@{suffix}")),
                width = fixed(100)
            )
        )

        rule.onNodeWithTag("text").assert(textEndsWith("… more"))

        variable.set("… ещё")

        rule.onNodeWithTag("text").assert(textEndsWith("… ещё"))
    }

    @Test
    fun `standard ellipsis switches to custom rendering when its variable changes`() {
        val variable = Variable.StringVariable("suffix", "…")
        variableController.declare(variable)
        setContent(text(
            id = "text",
            text = constant("A very long text that does not fit into a single line"),
            maxLines = 1,
            ellipsis = ellipsis(expression("@{suffix}")),
            width = fixed(100),
        ))
        rule.waitForIdle()

        variable.set("… more")

        rule.onNodeWithTag("text").assert(textEndsWith("… more"))
    }

    @Test
    fun `custom ellipsis switches to native rendering when its variable changes`() {
        val variable = Variable.StringVariable("suffix", "… more")
        variableController.declare(variable)
        val sourceText = "A very long text that does not fit into a single line"
        setContent(text(
            id = "text",
            text = constant(sourceText),
            maxLines = 1,
            ellipsis = ellipsis(expression("@{suffix}")),
            width = fixed(100),
        ))
        rule.waitForIdle()

        variable.set("…")

        rule.onNodeWithTag("text").assertTextEquals(sourceText)
    }

    @Test
    fun `ellipsized text keeps the intrinsic width of the untruncated text`() {
        setContent(
            container(
                items = listOf(
                    intrinsicWidthContainer(
                        id = "ellipsized",
                        text = text(
                            text = constant("short\nsecond line that is much longer"),
                            maxLines = 1,
                            ellipsis = ellipsis(constant("… more"))
                        )
                    ),
                    intrinsicWidthContainer(
                        id = "reference",
                        text = text(text = constant("short\nsecond line that is much longer"))
                    )
                )
            )
        )

        assertEquals(nodeWidth("reference"), nodeWidth("ellipsized"))
    }

    @Test
    fun `wrap_content text is ellipsized after the last visible line`() {
        setContent(
            text(
                id = "text",
                text = constant("short\nsecond line that is much longer"),
                maxLines = 1,
                ellipsis = ellipsis(constant("… more")),
                width = wrapContent()
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("short… more")
    }

    @Test
    fun `custom ellipsis keeps text before hard line break`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                fontSize = 20,
                maxLines = 2,
                ellipsis = ellipsis(constant("… more")),
                width = fixed(200),
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("first\nsecond… more")
    }

    @Test
    fun `custom ellipsis shares animated gradient brush`() {
        // Arrange
        rule.mainClock.autoAdvance = false
        val content = text(
            id = "text",
            text = constant("A very long text that does not fit into a single line"),
            maxLines = 1,
            ellipsis = ellipsis(constant("… more")),
            width = fixed(constant(100)),
            textGradient = animatedTextGradient(),
        )

        // Act
        setContent(content)

        // Assert
        val rendered = rule.onNodeWithTag("text")
            .fetchSemanticsNode()
            .config[SemanticsProperties.Text]
            .single()
        val gradientSpan = rendered.spanStyles.single { span -> span.item.brush != null }
        assertEquals(0, gradientSpan.start)
        assertEquals(rendered.length, gradientSpan.end)
    }

    @Test
    fun `auto_ellipsize ellipsizes the text at the container height`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                fontSize = 20,
                autoEllipsize = constant(true),
                height = fixed(30),
            )
        )

        val layout = textLayout()
        assertEquals(1, layout.lineCount)
        assertTrue(layout.isLineEllipsized(0))
    }

    @Test
    fun `text with max_lines is not ellipsized at the container height without auto_ellipsize`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                fontSize = 20,
                maxLines = 3,
                height = fixed(30),
            )
        )

        val layout = textLayout()
        assertEquals(3, layout.lineCount)
        assertFalse(layout.isLineEllipsized(2))
    }

    @Test
    fun `text is ellipsized when auto_ellipsize changes`() {
        val variable = Variable.BooleanVariable("auto_ellipsize", false)
        variableController.declare(variable)
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                fontSize = 20,
                autoEllipsize = booleanExpression("@{auto_ellipsize}"),
                height = fixed(30),
            )
        )
        assertFalse(textLayout().isLineEllipsized(0))

        variable.set(true)

        assertTrue(textLayout().isLineEllipsized(0))
    }

    @Test
    fun `custom ellipsis is not appended at the container height without auto_ellipsize`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                fontSize = 20,
                maxLines = 3,
                ellipsis = ellipsis(constant("… more")),
                height = fixed(30),
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("first\nsecond\nthird")
    }

    @Test
    fun `word ellipsis is not appended at the container height without auto_ellipsize`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                fontSize = 20,
                maxLines = 3,
                truncatePolicy = DivText.TruncatePolicy.WORD,
                height = fixed(30),
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("first\nsecond\nthird")
    }

    @Test
    fun `word ellipsis is appended at the container height with auto_ellipsize`() {
        setContent(
            text(
                id = "text",
                text = constant("alpha beta gamma delta"),
                fontSize = 20,
                autoEllipsize = constant(true),
                truncatePolicy = DivText.TruncatePolicy.WORD,
                width = fixed(150),
                height = fixed(30),
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("alpha beta…")
    }

    @Test
    fun `text is clipped without ellipsis at the container height when truncate is none`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                fontSize = 20,
                autoEllipsize = constant(true),
                truncate = DivText.Truncate.NONE,
                height = fixed(30),
            )
        )

        assertFalse(textLayout().isLineEllipsized(0))
    }

    @Test
    fun `custom ellipsis is not appended at the container height when truncate is none`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                fontSize = 20,
                autoEllipsize = constant(true),
                truncate = DivText.Truncate.NONE,
                ellipsis = ellipsis(constant("… more")),
                height = fixed(30),
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("first\nsecond\nthird")
    }

    @Test
    fun `start ellipsis is drawn on the only line that fits the container with auto_ellipsize`() {
        setContent(
            text(
                id = "text",
                text = constant("alpha beta gamma delta epsilon zeta eta theta"),
                fontSize = 20,
                autoEllipsize = constant(true),
                truncate = DivText.Truncate.START,
                width = fixed(150),
                height = fixed(30),
            )
        )

        val layout = textLayout()
        assertEquals(1, layout.lineCount)
        assertTrue(layout.isLineEllipsized(0))
    }

    @Test
    fun `middle ellipsis is drawn on the only line that fits the container with auto_ellipsize`() {
        setContent(
            text(
                id = "text",
                text = constant("alpha beta gamma delta epsilon zeta eta theta"),
                fontSize = 20,
                autoEllipsize = constant(true),
                truncate = DivText.Truncate.MIDDLE,
                width = fixed(150),
                height = fixed(30),
            )
        )

        val layout = textLayout()
        assertEquals(1, layout.lineCount)
        assertTrue(layout.isLineEllipsized(0))
    }

    @Test
    fun `middle ellipsis is drawn on the only line that fits the container in text with range color`() {
        setContent(
            text(
                id = "text",
                text = constant("alpha beta gamma delta epsilon zeta eta theta"),
                fontSize = 20,
                autoEllipsize = constant(true),
                truncate = DivText.Truncate.MIDDLE,
                ranges = listOf(textRange(end = 5, textColor = 0xFFFF0000.toInt())),
                width = fixed(150),
                height = fixed(30),
            )
        )

        val layout = textLayout()
        assertEquals(1, layout.lineCount)
        assertTrue(layout.isLineEllipsized(0))
    }

    @Test
    fun `middle ellipsis is drawn on the only line that fits the container in text with range background`() {
        setContent(
            text(
                id = "text",
                text = constant("alpha beta gamma delta epsilon zeta eta theta"),
                fontSize = 20,
                autoEllipsize = constant(true),
                truncate = DivText.Truncate.MIDDLE,
                ranges = listOf(
                    textRange(
                        end = 5,
                        background = DivTextRangeBackground.Solid(DivSolidBackground(color = constant(0xFFFF0000.toInt()))),
                    )
                ),
                width = fixed(150),
                height = fixed(30),
            )
        )

        val layout = textLayout()
        assertEquals(1, layout.lineCount)
        assertTrue(layout.isLineEllipsized(0))
    }

    @Test
    fun `middle ellipsis is not limited to one line when two lines fit the container`() {
        setContent(
            text(
                id = "text",
                text = constant("alpha beta gamma delta epsilon zeta eta theta"),
                fontSize = 20,
                autoEllipsize = constant(true),
                truncate = DivText.Truncate.MIDDLE,
                width = fixed(150),
                height = fixed(55),
            )
        )

        val layout = textLayout()
        assertTrue(layout.lineCount > 1)
        assertFalse(layout.isLineEllipsized(0))
    }

    @Test
    fun `middle ellipsis is drawn when min_hidden_lines crops the text to one line`() {
        setContent(
            text(
                id = "text",
                text = constant("alpha beta gamma delta epsilon zeta eta theta"),
                fontSize = 20,
                maxLines = 1,
                minHiddenLines = constant(1),
                truncate = DivText.Truncate.MIDDLE,
                width = fixed(150),
            )
        )

        val layout = textLayout()
        assertEquals(1, layout.lineCount)
        assertTrue(layout.isLineEllipsized(0))
    }

    @Test
    fun `auto_ellipsize ellipsizes the text that min_hidden_lines does not crop`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird\nfourth"),
                fontSize = 20,
                maxLines = 2,
                minHiddenLines = constant(2),
                autoEllipsize = constant(true),
                height = fixed(75),
            )
        )

        val layout = textLayout()
        assertEquals(3, layout.lineCount)
        assertTrue(layout.isLineEllipsized(2))
    }

    @Test
    fun `custom ellipsis is appended at the container height with auto_ellipsize`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                fontSize = 20,
                autoEllipsize = constant(true),
                ellipsis = ellipsis(constant("… more")),
                height = fixed(30),
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("first… more")
    }

    @Test
    fun `text is not cropped when it hides no more lines than min_hidden_lines`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                maxLines = 1,
                minHiddenLines = constant(2),
            )
        )

        assertEquals(3, textLayout().lineCount)
    }

    @Test
    fun `text is cropped to max_lines when it hides more lines than min_hidden_lines`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird\nfourth"),
                maxLines = 1,
                minHiddenLines = constant(2),
            )
        )

        val layout = textLayout()
        assertEquals(1, layout.lineCount)
        assertTrue(layout.isLineEllipsized(0))
    }

    @Test
    fun `text is cropped when min_hidden_lines changes`() {
        val variable = Variable.IntegerVariable("min_hidden_lines", 3)
        variableController.declare(variable)
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird\nfourth"),
                maxLines = 1,
                minHiddenLines = intExpression("@{min_hidden_lines}"),
            )
        )
        assertEquals(4, textLayout().lineCount)

        variable.set(2)

        assertEquals(1, textLayout().lineCount)
    }

    @Test
    fun `text is not cropped when min_hidden_lines exceeds the integer range`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird\nfourth"),
                maxLines = 1,
                minHiddenLines = constant(4294967298),
            )
        )

        assertEquals(4, textLayout().lineCount)
    }

    @Test
    fun `custom ellipsis is appended when min_hidden_lines crops the text`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird\nfourth"),
                maxLines = 1,
                minHiddenLines = constant(2),
                ellipsis = ellipsis(constant("… more")),
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("first… more")
    }

    @Test
    fun `text cropped by min_hidden_lines fits a taller custom ellipsis`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird\nfourth"),
                fontSize = 12,
                maxLines = 1,
                minHiddenLines = constant(2),
                ellipsis = DivText.Ellipsis(
                    text = constant("… more"),
                    ranges = listOf(textRange(fontSize = 48)),
                ),
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("first… more")
        val layout = textLayout()
        assertEquals(1, layout.lineCount)
        assertTrue(nodeHeight("text") >= layout.getLineBottom(0))
    }

    @Test
    fun `custom ellipsis is not appended when min_hidden_lines keeps all lines`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                maxLines = 1,
                minHiddenLines = constant(2),
                ellipsis = ellipsis(constant("… more")),
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("first\nsecond\nthird")
    }

    @Test
    fun `text cropped by min_hidden_lines has the intrinsic height of max_lines lines`() {
        setContent(
            container(
                items = listOf(
                    intrinsicHeightContainer(
                        id = "cropped",
                        text = text(
                            text = constant("first\nsecond\nthird\nfourth"),
                            maxLines = 1,
                            minHiddenLines = constant(2),
                        )
                    ),
                    intrinsicHeightContainer(
                        id = "reference",
                        text = text(text = constant("first"))
                    )
                )
            )
        )

        assertEquals(nodeHeight("reference"), nodeHeight("cropped"))
    }

    @Test
    fun `ellipsis image is placed in the custom ellipsis`() {
        setContent(
            longText(
                DivText.Ellipsis(
                    text = constant("… more"),
                    images = listOf(textImage(url = "https://divkit.tech/image.png", start = 1)),
                )
            )
        )

        rule.onNodeWithTag("text").assert(textEndsWith("…\u2060\u2060 more"))
    }

    @Test
    fun `ellipsis image does not replace the text image with the same index`() {
        setContent(
            longText(
                ellipsis = DivText.Ellipsis(
                    text = constant("… more"),
                    images = listOf(textImage(url = "https://divkit.tech/ellipsis.png", description = "Ellipsis")),
                ),
                images = listOf(textImage(url = "https://divkit.tech/text.png", description = "Text")),
            )
        )

        rule.onNodeWithContentDescription("Text", useUnmergedTree = true).assertExists()
        rule.onNodeWithContentDescription("Ellipsis", useUnmergedTree = true).assertExists()
    }

    @Test
    fun `ellipsis image builder builds images from data`() {
        setContent(
            longText(
                DivText.Ellipsis(
                    text = constant("… more"),
                    imageBuilder = imageBuilder(constant(jsonArray(jsonObject("pos" to 1)))),
                )
            )
        )

        rule.onNodeWithTag("text").assert(textEndsWith("…\u2060\u2060 more"))
    }

    @Test
    fun `ellipsis image builder replaces static ellipsis images`() {
        setContent(
            longText(
                DivText.Ellipsis(
                    text = constant("… more"),
                    imageBuilder = imageBuilder(constant(jsonArray(jsonObject("pos" to 1)))),
                    images = listOf(textImage(url = "https://divkit.tech/image.png", start = 6)),
                )
            )
        )

        rule.onNodeWithTag("text").assert(textEndsWith("…\u2060\u2060 more"))
    }

    @Test
    fun `ellipsis images follow builder data`() {
        val source = Variable.ArrayVariable("source", jsonArray(jsonObject("pos" to 1)))
        variableController.declare(source)
        setContent(
            longText(
                DivText.Ellipsis(
                    text = constant("… more"),
                    imageBuilder = imageBuilder(arrayExpression("@{source}")),
                )
            )
        )

        source.set(jsonArray(jsonObject("pos" to 6)))

        rule.onNodeWithTag("text").assert(textEndsWith("… more\u2060\u2060"))
    }

    @Test
    fun `ellipsis range builder builds ranges from data`() {
        setContent(
            longText(
                DivText.Ellipsis(
                    text = constant("… more"),
                    rangeBuilder = rangeBuilder(constant(jsonArray(jsonObject("start" to 2, "end" to 6)))),
                )
            )
        )

        assertEquals(listOf("more"), styledTexts())
    }

    @Test
    fun `ellipsis range builder replaces static ellipsis ranges`() {
        setContent(
            longText(
                DivText.Ellipsis(
                    text = constant("… more"),
                    rangeBuilder = rangeBuilder(constant(jsonArray(jsonObject("start" to 2, "end" to 6)))),
                    ranges = listOf(textRange(start = 0, end = 1, fontSize = 20)),
                )
            )
        )

        assertEquals(listOf("more"), styledTexts())
    }

    @Test
    fun `ellipsis ranges follow builder data`() {
        val source = Variable.ArrayVariable("source", jsonArray(jsonObject("start" to 2, "end" to 6)))
        variableController.declare(source)
        setContent(
            longText(
                DivText.Ellipsis(
                    text = constant("… more"),
                    rangeBuilder = rangeBuilder(arrayExpression("@{source}")),
                )
            )
        )

        source.set(jsonArray(jsonObject("start" to 2, "end" to 4)))

        assertEquals(listOf("mo"), styledTexts())
    }

    private fun ellipsis(text: Expression<String>) = DivText.Ellipsis(text = text)

    private fun longText(ellipsis: DivText.Ellipsis, images: List<DivText.Image>? = null) = text(
        id = "text",
        text = constant("A very long text that does not fit into a single line"),
        images = images,
        maxLines = 1,
        ellipsis = ellipsis,
        width = fixed(100),
    )

    private fun imageBuilder(data: Expression<JSONArray>) = DivText.ImageBuilder(
        data = data,
        prototypes = listOf(
            DivText.ImageBuilder.Prototype(
                image = DivText.Image(
                    start = intExpression("@{it.getInteger('pos')}"),
                    url = uriExpression("https://divkit.tech/image.png"),
                ),
            )
        ),
    )

    private fun rangeBuilder(data: Expression<JSONArray>) = DivText.RangeBuilder(
        data = data,
        prototypes = listOf(
            DivText.RangeBuilder.Prototype(
                range = DivText.Range(
                    end = intExpression("@{it.getInteger('end')}"),
                    fontWeight = constant(DivFontWeight.BOLD),
                    start = intExpression("@{it.getInteger('start')}"),
                ),
            )
        ),
    )

    private fun styledTexts(): List<String> {
        rule.waitForIdle()
        val text = rule.onNodeWithTag("text").fetchSemanticsNode().config[SemanticsProperties.Text].single()
        return text.spanStyles.map { text.text.substring(it.start, it.end) }
    }

    private fun intrinsicWidthContainer(id: String, text: Div) = container(
        id = id,
        width = wrapContent(),
        items = listOf(text(text = constant("S"), width = matchParent()), text)
    )

    private fun intrinsicHeightContainer(id: String, text: Div) = container(
        id = id,
        orientation = DivContainer.Orientation.HORIZONTAL,
        items = listOf(text, separator(height = matchParent()))
    )

    private fun nodeWidth(id: String) = rule.onNodeWithTag(id).fetchSemanticsNode().size.width

    private fun nodeHeight(id: String) = rule.onNodeWithTag(id).fetchSemanticsNode().size.height

    private fun textLayout(): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        return layouts.single()
    }

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }
}

private fun textStartsWith(prefix: String) = SemanticsMatcher("text starts with '$prefix'") {
    it.textValue.startsWith(prefix)
}

private fun textEndsWith(suffix: String) = SemanticsMatcher("text ends with '$suffix'") {
    it.textValue.endsWith(suffix)
}

private val SemanticsNode.textValue: String
    get() = config.getOrNull(SemanticsProperties.Text).orEmpty().joinToString(separator = "") { it.text }
