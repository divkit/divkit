package com.yandex.div.compose.views.container

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContent
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.aspect
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.containerSeparator
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivData
import com.yandex.div2.DivGallery
import com.yandex.div2.DivPatch
import com.yandex.div2.DivSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivContainerMatchParentTest {

    @get:Rule
    val rule = createComposeRule()

    private val size = Variable.IntegerVariable("size", 20L)
    private val visibility = Variable.StringVariable("visibility", "visible")

    private val testReporter = TestReporter()

    private val configuration = divConfiguration {
        reporter = testReporter
        variableController = DivVariableController().apply {
            declare(size)
            declare(visibility)
        }
    }

    @Test
    fun `spaced children that overflow the container keep their places instead of piling up at its end`() {
        setContent(
            container(
                height = fixed(100),
                itemSpacing = 10,
                items = listOf(
                    verticalChild("first", matchParent(minSize = 80)),
                    verticalChild("second", matchParent(minSize = 80)),
                ),
            )
        )

        rule.onNodeWithTag("first").assertTopPositionInRootIsEqualTo(0.dp)
        rule.onNodeWithTag("second").assertTopPositionInRootIsEqualTo(90.dp)
    }

    @Test
    fun `overlap match_parent child keeps its min_size in the wrap_content width its sibling defines`() {
        setContent(
            container(
                id = "overlap",
                width = wrapContent(),
                orientation = DivContainer.Orientation.OVERLAP,
                items = listOf(
                    fixedBox(100, 40),
                    container(id = "minimum", width = matchParent(minSize = 160), items = listOf(fixedBox(300, 10))),
                ),
            )
        )

        rule.onNodeWithTag("overlap").assertWidthIsEqualTo(160.dp)
        rule.onNodeWithTag("minimum").assertWidthIsEqualTo(160.dp)
    }

    @Test
    fun `horizontal match_parent children keep distinct minima beside a constrained sibling`() {
        setContent(
            container(
                width = fixed(120),
                orientation = DivContainer.Orientation.HORIZONTAL,
                items = listOf(
                    container(
                        id = "constrained",
                        width = wrapContent(constrained = true),
                        items = listOf(fixedBox(200, 20)),
                    ),
                    container(id = "larger", width = matchParent(minSize = 60)),
                    container(id = "smaller", width = matchParent(minSize = 20)),
                ),
            )
        )

        rule.onNodeWithTag("constrained").assertWidthIsEqualTo(40.dp)
        rule.onNodeWithTag("larger").assertWidthIsEqualTo(60.dp)
        rule.onNodeWithTag("smaller").assertWidthIsEqualTo(20.dp)
    }

    @Test
    fun `horizontal children share surplus by weight after applying minimum bounds`() {
        setContent(
            container(
                width = fixed(120),
                orientation = DivContainer.Orientation.HORIZONTAL,
                items = listOf(
                    container(
                        id = "constrained",
                        width = wrapContent(constrained = true),
                        items = listOf(fixedBox(30, 20)),
                    ),
                    container(id = "larger", width = matchParent(minSize = 60)),
                    container(id = "smaller", width = matchParent(minSize = 20)),
                ),
            )
        )

        rule.onNodeWithTag("constrained").assertWidthIsEqualTo(30.dp)
        rule.onNodeWithTag("larger").assertWidthIsEqualTo(60.dp)
        rule.onNodeWithTag("smaller").assertWidthIsEqualTo(30.dp)
    }

    @Test
    fun `horizontal minimum resolves in the child scope`() {
        setContent(
            container(
                width = fixed(100),
                orientation = DivContainer.Orientation.HORIZONTAL,
                items = listOf(
                    container(
                        id = "constrained",
                        width = wrapContent(constrained = true),
                        items = listOf(fixedBox(200, 20)),
                    ),
                    container(
                        id = "minimum",
                        width = matchParent(minSize = intExpression("@{size}")),
                        variables = listOf(variable("size", 60L)),
                    ),
                ),
            )
        )

        rule.onNodeWithTag("constrained").assertWidthIsEqualTo(40.dp)
        rule.onNodeWithTag("minimum").assertWidthIsEqualTo(60.dp)
    }

    @Test
    fun `horizontal constrained child leaves room for the minimum and end separator`() {
        setContent(
            container(
                width = fixed(100),
                orientation = DivContainer.Orientation.HORIZONTAL,
                separator = containerSeparator(constant(10), showBetween = false, showAtEnd = true),
                items = listOf(
                    container(
                        id = "constrained",
                        width = wrapContent(constrained = true),
                        items = listOf(fixedBox(200, 20)),
                    ),
                    container(id = "minimum", width = matchParent(minSize = 40)),
                ),
            )
        )

        rule.onNodeWithTag("constrained").assertWidthIsEqualTo(50.dp)
        rule.onNodeWithTag("minimum").assertWidthIsEqualTo(40.dp)
    }

    @Test
    fun `vertical constrained child leaves room for the minimum and end separator`() {
        setContent(
            container(
                height = fixed(100),
                separator = containerSeparator(constant(10), height = 10, showBetween = false, showAtEnd = true),
                items = listOf(
                    container(
                        id = "constrained",
                        height = wrapContent(constrained = true),
                        items = listOf(fixedBox(200, 200)),
                    ),
                    verticalChild("minimum", matchParent(minSize = 40)),
                ),
            )
        )

        rule.onNodeWithTag("constrained").assertHeightIsEqualTo(50.dp)
        rule.onNodeWithTag("minimum").assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun `horizontal constrained sibling yields to match_parent minimum`() {
        setContent(
            container(
                width = fixed(320),
                orientation = DivContainer.Orientation.HORIZONTAL,
                paddings = insets(start = 16, end = 16),
                itemSpacing = 4,
                items = listOf(
                    container(
                        id = "constrained",
                        width = wrapContent(constrained = true),
                        items = listOf(fixedBox(400, 20)),
                    ),
                    container(id = "minimum", width = matchParent(minSize = 90)),
                ),
            )
        )

        rule.onNodeWithTag("constrained").assertWidthIsEqualTo(194.dp)
        rule.onNodeWithTag("minimum").assertWidthIsEqualTo(90.dp)
    }

    @Test
    fun `vertical constrained sibling yields to match_parent minimum`() {
        setContent(
            container(
                height = fixed(120),
                paddings = insets(top = 16, bottom = 16),
                itemSpacing = 4,
                items = listOf(
                    container(
                        id = "constrained",
                        height = wrapContent(constrained = true),
                        items = listOf(fixedBox(200, 200)),
                    ),
                    verticalChild("minimum", matchParent(minSize = 40)),
                ),
            )
        )

        rule.onNodeWithTag("constrained").assertHeightIsEqualTo(44.dp)
        rule.onNodeWithTag("minimum").assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun `constrained siblings shrink when match_parent minimum grows`() {
        setContent(
            container(
                width = fixed(100),
                items = listOf(
                    container(
                        orientation = DivContainer.Orientation.HORIZONTAL,
                        items = listOf(
                            container(
                                id = "horizontalConstrained",
                                width = wrapContent(constrained = true),
                                items = listOf(fixedBox(200, 20)),
                            ),
                            container(
                                id = "horizontalMinimum",
                                width = matchParent(minSize = intExpression("@{size}")),
                            ),
                        ),
                    ),
                    container(
                        height = fixed(100),
                        items = listOf(
                            container(
                                id = "verticalConstrained",
                                height = wrapContent(constrained = true),
                                items = listOf(fixedBox(100, 200)),
                            ),
                            verticalChild("verticalMinimum", matchParent(minSize = intExpression("@{size}"))),
                        ),
                    ),
                ),
            )
        )

        size.set(70L)
        rule.waitForIdle()

        rule.onNodeWithTag("horizontalConstrained").assertWidthIsEqualTo(30.dp)
        rule.onNodeWithTag("horizontalMinimum").assertWidthIsEqualTo(70.dp)
        rule.onNodeWithTag("verticalConstrained").assertHeightIsEqualTo(30.dp)
        rule.onNodeWithTag("verticalMinimum").assertHeightIsEqualTo(70.dp)
    }

    @Test
    fun `space a max_size leaves goes to the other match_parent children of an aspect container`() {
        setContent(
            container(
                width = wrapContent(),
                aspect = aspect(2.0),
                items = listOf(
                    verticalChild("bounded", matchParent(maxSize = 20)),
                    verticalChild("rest", matchParent()),
                ),
            )
        )

        rule.onNodeWithTag("bounded").assertHeightIsEqualTo(20.dp)
        rule.onNodeWithTag("rest").assertHeightIsEqualTo(80.dp)
    }

    @Test
    fun `space a max_size leaves goes to the other match_parent children of a fixed container`() {
        setContent(
            column(
                verticalChild("bounded", matchParent(maxSize = 20)),
                verticalChild("rest", matchParent()),
            )
        )

        rule.onNodeWithTag("bounded").assertHeightIsEqualTo(20.dp)
        rule.onNodeWithTag("rest").assertHeightIsEqualTo(80.dp)
    }

    @Test
    fun `min_size of a match_parent child takes space from its siblings`() {
        setContent(
            column(
                verticalChild("bounded", matchParent(minSize = 70)),
                verticalChild("rest", matchParent()),
            )
        )

        rule.onNodeWithTag("bounded").assertHeightIsEqualTo(70.dp)
        rule.onNodeWithTag("rest").assertHeightIsEqualTo(30.dp)
    }

    @Test
    fun `weights share the space the other children leave`() {
        setContent(
            column(
                fixedBox(200, 20),
                verticalChild("light", matchParent(weight = constant(1.0))),
                verticalChild("heavy", matchParent(weight = constant(3.0))),
            )
        )

        rule.onNodeWithTag("light").assertHeightIsEqualTo(20.dp)
        rule.onNodeWithTag("heavy").assertHeightIsEqualTo(60.dp)
    }

    @Test
    fun `constrained sibling keeps its content and the match_parent child takes the rest`() {
        setContent(
            column(
                container(
                    id = "constrained",
                    width = fixed(200),
                    height = wrapContent(constrained = true),
                    items = listOf(fixedBox(200, 60)),
                ),
                verticalChild("rest", matchParent()),
            )
        )

        rule.onNodeWithTag("constrained").assertHeightIsEqualTo(60.dp)
        rule.onNodeWithTag("rest").assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun `margins of a match_parent child stay outside its share`() {
        setContent(
            column(
                container(width = fixed(200), height = fixed(30)),
                container(id = "rest", width = fixed(200), height = matchParent(), margins = insets(top = 10, bottom = 20)),
            )
        )

        rule.onNodeWithTag("rest").assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun `item_spacing is not added around the margins of a match_parent child`() {
        setContent(
            container(
                width = fixed(200),
                orientation = DivContainer.Orientation.HORIZONTAL,
                itemSpacing = 4,
                items = listOf(
                    fixedBox(50, 20),
                    container(id = "rest", width = matchParent(), margins = insets(start = 10, end = 10)),
                    fixedBox(50, 20),
                ),
            )
        )

        rule.onNodeWithTag("rest").assertLeftPositionInRootIsEqualTo(64.dp)
        rule.onNodeWithTag("rest").assertWidthIsEqualTo(72.dp)
    }

    @Test
    fun `margins of a match_parent child are reserved once for a constrained sibling`() {
        setContent(
            column(
                container(id = "rest", width = fixed(200), height = matchParent(), margins = insets(top = 10, bottom = 10)),
                container(
                    id = "constrained",
                    width = fixed(200),
                    height = wrapContent(constrained = true),
                    items = listOf(fixedBox(200, 100)),
                ),
            )
        )

        rule.onNodeWithTag("constrained").assertHeightIsEqualTo(80.dp)
        rule.onNodeWithTag("rest").assertHeightIsEqualTo(0.dp)
    }

    @Test
    fun `min_size of one child and max_size of another hold together when they fit`() {
        setContent(
            column(
                verticalChild("min", matchParent(minSize = 80)),
                verticalChild("max", matchParent(maxSize = 40)),
            )
        )

        rule.onNodeWithTag("min").assertHeightIsEqualTo(80.dp)
        rule.onNodeWithTag("max").assertHeightIsEqualTo(20.dp)
    }

    @Test
    fun `rounding leaves a pixel free instead of exceeding max_size`() {
        setContent(
            column(
                verticalChild("first", matchParent(maxSize = 50)),
                verticalChild("second", matchParent(maxSize = 50)),
                height = 101,
            )
        )

        rule.onNodeWithTag("first").assertHeightIsEqualTo(50.dp)
        rule.onNodeWithTag("second").assertHeightIsEqualTo(50.dp)
    }

    @Test
    fun `rounding overflows by a pixel instead of shrinking below min_size`() {
        setContent(
            column(
                verticalChild("first", matchParent(minSize = 50)),
                verticalChild("second", matchParent(minSize = 51)),
            )
        )

        rule.onNodeWithTag("first").assertHeightIsEqualTo(50.dp)
        rule.onNodeWithTag("second").assertHeightIsEqualTo(51.dp)
    }

    @Test
    fun `unknown custom child is reported without crashing its fixed height parent`() {
        testReporter.failOnError = false

        setContent(
            container(
                id = "column",
                width = fixed(200),
                height = fixed(100),
                items = listOf(custom(type = "unknown", height = matchParent())),
            )
        )

        rule.onNodeWithTag("column").assertHeightIsEqualTo(100.dp)
        assertEquals("No custom view factory for custom_type: unknown", testReporter.lastError)
    }

    @Test
    fun `sibling size change reallocates the match_parent child`() {
        setContent(
            column(
                container(width = fixed(200), height = fixed(intExpression("@{size}"))),
                verticalChild("rest", matchParent()),
            )
        )
        rule.onNodeWithTag("rest").assertHeightIsEqualTo(80.dp)

        size.set(50L)
        rule.waitForIdle()

        rule.onNodeWithTag("rest").assertHeightIsEqualTo(50.dp)
    }

    @Test
    fun `gone sibling returns its space to the match_parent child`() {
        setContent(
            column(
                container(width = fixed(200), height = fixed(30), visibility = visibilityExpression("@{visibility}")),
                verticalChild("rest", matchParent()),
            )
        )
        rule.onNodeWithTag("rest").assertHeightIsEqualTo(70.dp)

        visibility.set("gone")
        rule.waitForIdle()

        rule.onNodeWithTag("rest").assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun `gallery keeps its scroll position when its match_parent sibling becomes gone`() {
        setContent(
            column(
                gallery(
                    id = "gallery",
                    width = fixed(200),
                    height = fixed(100),
                    orientation = constant(DivGallery.Orientation.VERTICAL),
                    items = List(10) { container(id = "item$it", width = fixed(200), height = fixed(60)) },
                ),
                container(width = fixed(200), height = matchParent(), visibility = visibilityExpression("@{visibility}")),
                height = 200,
            )
        )
        rule.onNode(hasScrollToIndexAction()).performScrollToIndex(3)
        rule.waitForIdle()

        visibility.set("gone")
        rule.waitForIdle()

        val galleryTop = rule.onNodeWithTag("gallery").getUnclippedBoundsInRoot().top
        assertEquals(galleryTop, rule.onNodeWithTag("item3").getUnclippedBoundsInRoot().top)
    }

    @Test
    fun `constrained sibling of a match_parent minimum keeps its content in an unbounded row`() {
        setContent(
            container(
                width = wrapContent(),
                orientation = DivContainer.Orientation.HORIZONTAL,
                items = listOf(
                    container(
                        width = matchParent(),
                        orientation = DivContainer.Orientation.HORIZONTAL,
                        items = listOf(
                            container(
                                id = "constrained",
                                width = wrapContent(constrained = true),
                                items = listOf(fixedBox(400, 20)),
                            ),
                            container(width = matchParent(minSize = 90)),
                        ),
                    ),
                ),
            )
        )

        rule.onNodeWithTag("constrained").assertWidthIsEqualTo(400.dp)
    }

    @Test
    fun `weighted gallery keeps its scroll when its constrained sibling becomes gone`() {
        setContent(
            container(
                width = fixed(400),
                height = fixed(100),
                orientation = DivContainer.Orientation.HORIZONTAL,
                items = listOf(
                    container(
                        width = wrapContent(constrained = true),
                        visibility = visibilityExpression("@{visibility}"),
                        items = listOf(fixedBox(300, 20)),
                    ),
                    gallery(
                        id = "gallery",
                        width = matchParent(minSize = 40),
                        height = fixed(100),
                        items = List(10) { container(id = "item$it", width = fixed(60), height = fixed(100)) },
                    ),
                ),
            )
        )
        rule.onNodeWithTag("gallery").assertWidthIsEqualTo(100.dp)
        rule.onNode(hasScrollToIndexAction()).performScrollToIndex(3)
        rule.waitForIdle()

        visibility.set("gone")
        rule.waitForIdle()

        rule.onNodeWithTag("gallery").assertWidthIsEqualTo(400.dp)
        val galleryLeft = rule.onNodeWithTag("gallery").getUnclippedBoundsInRoot().left
        assertEquals(galleryLeft, rule.onNodeWithTag("item3").getUnclippedBoundsInRoot().left)
    }

    @Test
    fun `gallery keeps its scroll when a patch adds a sibling minimum`() {
        val card = data(horizontalGalleryWithMinimum(minimum = null))
        val divContext = DivContext(getApplicationContext(), configuration)
        rule.setContent {
            CompositionLocalProvider(LocalDivContext provides divContext) { DivView(card) }
        }
        rule.onNodeWithTag("minimum").assertWidthIsEqualTo(0.dp)
        rule.onNode(hasScrollToIndexAction()).performScrollToIndex(3)
        rule.waitForIdle()
        val galleryLeft = rule.onNodeWithTag("gallery").getUnclippedBoundsInRoot().left

        patchMinimum(divContext, card, container(id = "minimum", width = matchParent(minSize = 40)))

        rule.onNodeWithTag("minimum").assertWidthIsEqualTo(40.dp)
        assertEquals(galleryLeft, rule.onNodeWithTag("item3").getUnclippedBoundsInRoot().left)
    }

    @Test
    fun `gallery keeps its scroll when a patch removes a sibling minimum`() {
        val card = data(horizontalGalleryWithMinimum(minimum = 40))
        val divContext = DivContext(getApplicationContext(), configuration)
        rule.setContent {
            CompositionLocalProvider(LocalDivContext provides divContext) { DivView(card) }
        }
        rule.onNodeWithTag("minimum").assertWidthIsEqualTo(40.dp)
        rule.onNode(hasScrollToIndexAction()).performScrollToIndex(3)
        rule.waitForIdle()
        val galleryLeft = rule.onNodeWithTag("gallery").getUnclippedBoundsInRoot().left

        patchMinimum(divContext, card, container(id = "minimum", width = matchParent()))

        rule.onNodeWithTag("minimum").assertWidthIsEqualTo(0.dp)
        assertEquals(galleryLeft, rule.onNodeWithTag("item3").getUnclippedBoundsInRoot().left)
    }

    @Test
    fun `minimum sizes account for separately rounded margins`() {
        val card = data(container(
            width = fixed(100),
            items = listOf(
                container(
                    height = fixed(20),
                    orientation = DivContainer.Orientation.HORIZONTAL,
                    items = listOf(
                        container(
                            id = "horizontalConstrained",
                            width = wrapContent(constrained = true),
                            items = listOf(fixedBox(200, 20)),
                        ),
                        container(
                            id = "horizontalMinimum",
                            width = matchParent(minSize = 40),
                            margins = insets(start = 1, end = 1),
                        ),
                    ),
                ),
                container(
                    height = fixed(100),
                    items = listOf(
                        container(
                            id = "verticalConstrained",
                            height = wrapContent(constrained = true),
                            items = listOf(fixedBox(100, 200)),
                        ),
                        container(
                            id = "verticalMinimum",
                            height = matchParent(minSize = 40),
                            margins = insets(top = 1, bottom = 1),
                        ),
                    ),
                ),
            ),
        ))
        rule.setContentWithDivContext(configuration) {
            CompositionLocalProvider(LocalDensity provides Density(1.5f)) { DivView(card) }
        }

        assertEquals(60, rule.onNodeWithTag("horizontalMinimum").fetchSemanticsNode().size.width)
        assertEquals(86, rule.onNodeWithTag("horizontalConstrained").fetchSemanticsNode().size.width)
        assertEquals(60, rule.onNodeWithTag("verticalMinimum").fetchSemanticsNode().size.height)
        assertEquals(86, rule.onNodeWithTag("verticalConstrained").fetchSemanticsNode().size.height)
    }

    private fun horizontalGalleryWithMinimum(minimum: Int?): Div = container(
        width = fixed(400),
        height = fixed(100),
        orientation = DivContainer.Orientation.HORIZONTAL,
        items = listOf(
            gallery(
                id = "gallery",
                width = fixed(200),
                height = fixed(100),
                items = List(10) { container(id = "item$it", width = fixed(60), height = fixed(100)) },
            ),
            container(
                width = wrapContent(constrained = true),
                items = listOf(fixedBox(300, 20)),
            ),
            container(id = "minimum", width = matchParent(minSize = minimum)),
        ),
    )

    private fun patchMinimum(divContext: DivContext, card: DivData, replacement: Div) {
        rule.runOnIdle {
            assertTrue(runBlocking {
                divContext.patcher.applyPatch(card, DivPatch(
                    changes = listOf(DivPatch.Change("minimum", listOf(replacement))),
                ))
            })
        }
        rule.waitForIdle()
    }

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }

    private fun column(vararg items: Div, height: Int = 100): Div =
        container(width = fixed(200), height = fixed(height), items = items.toList())

    private fun verticalChild(id: String, height: DivSize): Div =
        container(id = id, width = fixed(200), height = height)

    private fun fixedBox(width: Int, height: Int): Div =
        container(width = fixed(width), height = fixed(height))
}
