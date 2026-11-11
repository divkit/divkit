package com.yandex.div.compose.views.container

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.aspect
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivGallery
import com.yandex.div2.DivSize
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

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
