package com.yandex.div.compose

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textImage
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivText
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals

// Truncation is driven by real text measurement, which the legacy Robolectric graphics
// pipeline does not provide: without NATIVE the text always reports as fitting.
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(AndroidJUnit4::class)
class DivTextEllipsisTest {

    @get:Rule
    val rule = createComposeRule()

    private val reporter = TestReporter()
    private val variableController = DivVariableController()

    private val configuration = DivConfiguration(
        reporter = reporter,
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
                width = fixed(constant(100))
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
                width = fixed(constant(100))
            )
        )

        rule.onNodeWithTag("text").assertTextEquals("Short")
    }

    @Test
    fun `ellipsis is not applied without max_lines`() {
        setContent(
            text(
                id = "text",
                text = constant("A very long text that does not fit into a single line"),
                ellipsis = ellipsis(constant("… more")),
                width = fixed(constant(100))
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
                width = fixed(constant(100))
            )
        )

        rule.onNodeWithTag("text").assert(textEndsWith("… more"))

        variable.set("… ещё")

        rule.onNodeWithTag("text").assert(textEndsWith("… ещё"))
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
    fun `unsupported ellipsis properties are reported`() {
        reporter.failOnError = false

        setContent(
            text(
                id = "text",
                text = constant("A very long text that does not fit into a single line"),
                maxLines = 1,
                ellipsis = DivText.Ellipsis(
                    text = constant("… more"),
                    images = listOf(textImage(url = "https://divkit.tech/image.png"))
                ),
                width = fixed(constant(100))
            )
        )

        assertEquals(listOf("Text ellipsis property not supported: images"), reporter.errors)
    }

    private fun ellipsis(text: Expression<String>) = DivText.Ellipsis(text = text)

    private fun intrinsicWidthContainer(id: String, text: Div) = container(
        id = id,
        width = wrapContent(),
        items = listOf(text(text = constant("S"), width = matchParent()), text)
    )

    private fun nodeWidth(id: String) = rule.onNodeWithTag(id).fetchSemanticsNode().size.width

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
