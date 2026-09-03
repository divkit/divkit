package com.yandex.div.compose

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.doubleExpression
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.text
import com.yandex.div.test.data.uriExpression
import com.yandex.div2.Div
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivText
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class DivTextInlineImagesTest {

    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()
    private val imageLoaderConfiguration = TestImageLoaderConfiguration()
    private val configuration = DivConfiguration(
        imageLoaderConfiguration = imageLoaderConfiguration,
        reporter = TestReporter(),
        variableController = variableController,
    )

    @Test
    fun `image position changes when expression value changes`() {
        val position = Variable.IntegerVariable("image_position", 0)
        variableController.declare(position)
        rule.setContent(
            configuration = configuration,
            data = data(
                text(
                    id = "text",
                    text = "abc",
                    images = listOf(inlineImage(intExpression("@{image_position}"))),
                )
            ),
        )

        assertEquals("\u2060abc", renderedText())

        position.set(3)

        assertEquals("abc\u2060\u2060", renderedText())
    }

    @Test
    fun `indexing direction changes image position`() {
        val isReversed = Variable.BooleanVariable("is_reversed", false)
        variableController.declare(isReversed)
        rule.setContent(
            configuration = configuration,
            data = data(
                text(
                    id = "text",
                    text = "abc",
                    images = listOf(
                        inlineImage(
                            start = constant(1L),
                            indexingDirection = indexingDirectionExpression(
                                "@{is_reversed ? 'reversed' : 'normal'}"
                            ),
                        )
                    ),
                )
            ),
        )

        assertEquals("a\u2060\u2060bc", renderedText())

        isReversed.set(true)

        assertEquals("ab\u2060\u2060c", renderedText())
    }

    @Test
    fun `image size changes layout height`() {
        val imageSize = Variable.IntegerVariable("image_size", 8)
        variableController.declare(imageSize)
        val size = DivFixedSize(value = intExpression("@{image_size}"))
        rule.setContent(
            configuration = configuration,
            data = data(
                text(
                    id = "text",
                    text = "abc",
                    images = listOf(
                        inlineImage(
                            start = constant(1L),
                            height = size,
                            width = size,
                        )
                    ),
                )
            ),
        )

        val smallImageHeight = renderedHeight()

        imageSize.set(40)

        assertTrue(renderedHeight() > smallImageHeight)
    }

    @Test
    fun `image request changes only when url changes`() {
        val position = Variable.IntegerVariable("image_position", 0)
        val urlIndex = Variable.IntegerVariable("image_url_index", 0)
        variableController.declare(position)
        variableController.declare(urlIndex)
        val isVisible = mutableStateOf(true)
        val data = data(
            text(
                id = "text",
                text = "abc",
                images = listOf(
                    inlineImage(
                        start = intExpression("@{image_position}"),
                        url = uriExpression(
                            "@{image_url_index == 0 ? " +
                                "'https://divkit.tech/first.png' : " +
                                "'https://divkit.tech/second.png'}"
                        ),
                    )
                ),
            )
        )
        rule.setContentWithDivContext(configuration) {
            if (isVisible.value) {
                DivView(data)
            }
        }

        rule.waitUntil { imageLoaderConfiguration.capturedRequests.size == 1 }

        position.set(3)
        rule.waitForIdle()
        assertEquals(1, imageLoaderConfiguration.capturedRequests.size)

        urlIndex.set(1)
        rule.waitForIdle()
        rule.waitUntil { imageLoaderConfiguration.capturedRequests.size == 2 }

        rule.runOnIdle { isVisible.value = false }
        rule.waitForIdle()
        urlIndex.set(0)
        rule.waitForIdle()
        assertEquals(2, imageLoaderConfiguration.capturedRequests.size)
        assertEquals(
            listOf(
                "https://divkit.tech/first.png",
                "https://divkit.tech/second.png",
            ),
            imageLoaderConfiguration.capturedRequests.map { it.data.toString() },
        )
    }

    @Test
    fun `accessibility type none hides inline image from text semantics`() {
        rule.setContent(
            configuration = configuration,
            data = data(
                text(
                    id = "text",
                    text = "abc",
                    images = listOf(
                        inlineImage(
                            start = constant(0L),
                            url = constant("https://divkit.tech/image.png".toUri()),
                            accessibility = DivText.Image.Accessibility(
                                type = DivText.Image.Accessibility.Type.NONE,
                            ),
                        )
                    ),
                )
            ),
        )

        assertEquals("\u2060abc", renderedText())
        rule.onNodeWithTag("text").assert(!keyIsDefined(SemanticsProperties.Role))
    }

    @Test
    fun `accessibility description and type are applied to inline image`() {
        rule.setContent(
            configuration = configuration,
            data = data(
                text(
                    id = "text",
                    text = "abcde",
                    images = DivText.Image.Accessibility.Type.entries.mapIndexed { index, type ->
                        inlineImage(
                            start = constant(index.toLong()),
                            url = constant("https://divkit.tech/$index.png".toUri()),
                            accessibility = DivText.Image.Accessibility(
                                description = constant(type.name),
                                type = type,
                            ),
                        )
                    },
                )
            ),
        )

        rule.onNodeWithContentDescription("NONE", useUnmergedTree = true)
            .assert(!keyIsDefined(SemanticsProperties.Role))
        rule.onNodeWithContentDescription("TEXT", useUnmergedTree = true)
            .assert(!keyIsDefined(SemanticsProperties.Role))
        rule.onNodeWithContentDescription("BUTTON", useUnmergedTree = true)
            .assert(hasRole(Role.Button))
        rule.onNodeWithContentDescription("IMAGE", useUnmergedTree = true)
            .assert(hasRole(Role.Image))
        rule.onNodeWithContentDescription("AUTO", useUnmergedTree = true)
            .assert(hasRole(Role.Image))
    }

    @Test
    fun `accessibility description reacts without an image request`() {
        val description = Variable.StringVariable("image_description", "First")
        variableController.declare(description)
        rule.setContent(
            configuration = configuration,
            data = data(
                text(
                    id = "text",
                    text = "abc",
                    images = listOf(
                        inlineImage(
                            start = constant(0L),
                            accessibility = DivText.Image.Accessibility(
                                description = expression("@{image_description}"),
                            ),
                        )
                    ),
                )
            ),
        )

        rule.onNodeWithContentDescription("First", useUnmergedTree = true)
            .assert(hasRole(Role.Image))

        description.set("Second")

        rule.onNodeWithContentDescription("Second", useUnmergedTree = true)
            .assert(hasRole(Role.Image))
        assertEquals(emptyList(), imageLoaderConfiguration.capturedRequests)
    }

    @Test
    fun `baseline offset reserves shifted image bounds`() {
        val offset = Variable.DoubleVariable("baseline_offset", 0.0)
        variableController.declare(offset)
        rule.setContent(
            configuration = configuration,
            data = data(textWithImage(baselineOffset = doubleExpression("@{baseline_offset}"))),
        )

        val zeroOffsetHeight = renderedHeight()

        offset.set(10.0)
        val positiveOffsetHeight = renderedHeight()

        offset.set(-15.0)
        val negativeOffsetHeight = renderedHeight()

        assertTrue(positiveOffsetHeight > zeroOffsetHeight)
        assertTrue(negativeOffsetHeight > zeroOffsetHeight)
    }

    @Test
    fun `line height constrains baseline offset`() {
        val offset = Variable.DoubleVariable("baseline_offset", 0.0)
        variableController.declare(offset)
        rule.setContent(
            configuration = configuration,
            data = data(
                textWithImage(
                    baselineOffset = doubleExpression("@{baseline_offset}"),
                    lineHeight = 20,
                )
            ),
        )

        val zeroOffsetHeight = renderedHeight()

        offset.set(10.0)
        val positiveOffsetHeight = renderedHeight()

        offset.set(-15.0)
        val negativeOffsetHeight = renderedHeight()

        assertEquals(zeroOffsetHeight, positiveOffsetHeight)
        assertEquals(zeroOffsetHeight, negativeOffsetHeight)
    }

    private fun renderedText(): String {
        rule.waitForIdle()
        return rule.onNodeWithTag("text")
            .fetchSemanticsNode()
            .config[SemanticsProperties.Text]
            .single()
            .text
    }

    private fun renderedHeight(): Int {
        rule.waitForIdle()
        return rule.onNodeWithTag("text").fetchSemanticsNode().size.height
    }

    private fun inlineImage(
        start: Expression<Long>,
        accessibility: DivText.Image.Accessibility? = null,
        url: Expression<android.net.Uri> = constant("empty://".toUri()),
        baselineOffset: Expression<Double>? = null,
        height: DivFixedSize = DivFixedSize(value = constant(20L)),
        indexingDirection: Expression<DivText.Image.IndexingDirection> =
            constant(DivText.Image.IndexingDirection.NORMAL),
        width: DivFixedSize = DivFixedSize(value = constant(20L)),
    ) = DivText.Image(
        accessibility = accessibility,
        baselineOffset = baselineOffset,
        height = height,
        indexingDirection = indexingDirection,
        start = start,
        url = url,
        width = width,
    )

    private fun indexingDirectionExpression(
        expression: String,
    ): Expression<DivText.Image.IndexingDirection> {
        return Expression.MutableExpression(
            expressionKey = "test",
            rawExpression = expression,
            converter = DivText.Image.IndexingDirection::fromString,
            validator = { true },
            logger = { fail(it.message) },
            typeHelper = TypeHelper.from(default = DivText.Image.IndexingDirection.NORMAL) {
                it is DivText.Image.IndexingDirection
            },
        )
    }

    private fun textWithImage(
        baselineOffset: Expression<Double>,
        lineHeight: Long? = null,
    ): Div {
        val text = text(
            fontSize = 16,
            id = "text",
            images = listOf(
                inlineImage(
                    start = constant(1L),
                    baselineOffset = baselineOffset,
                )
            ),
            text = constant("abc"),
        ) as Div.Text
        return Div.Text(
            text.value.copy(lineHeight = lineHeight?.let(::constant))
        )
    }
}

private fun hasRole(role: Role) =
    SemanticsMatcher.expectValue(SemanticsProperties.Role, role)
