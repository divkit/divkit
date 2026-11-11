package com.yandex.div.compose.views.container

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.assertWidthIsEqualTo
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
import com.yandex.div.test.data.data
import com.yandex.div.test.data.doubleExpression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivGallery
import com.yandex.div2.DivVisibility
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivContainerAspectTest {

    @get:Rule
    val rule = createComposeRule()

    private val ratio = Variable.DoubleVariable("ratio", 2.0)
    private val invalidRatio = Variable.DoubleVariable("invalid_ratio", 0.0)

    private val configuration = divConfiguration {
        reporter = TestReporter()
        variableController = DivVariableController().apply {
            declare(ratio)
            declare(invalidRatio)
        }
    }

    @Test
    fun `wrap_content width is the content width and the height follows the ratio`() {
        setContent(
            container(
                id = "aspect",
                width = wrapContent(),
                aspect = aspect(2.0),
                items = listOf(fixedBox(200, 40), fixedBox(150, 60)),
            )
        )

        rule.onNodeWithTag("aspect").assertWidthIsEqualTo(200.dp)
        rule.onNodeWithTag("aspect").assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun `match_parent child takes the height its siblings leave`() {
        setContent(
            container(
                width = wrapContent(),
                aspect = aspect(2.0),
                items = listOf(
                    fixedBox(300, 40),
                    container(id = "stretched", width = fixed(300), height = matchParent()),
                    fixedBox(150, 60),
                ),
            )
        )

        rule.onNodeWithTag("stretched").assertHeightIsEqualTo(50.dp)
    }

    @Test
    fun `constrained child shrinks to the height its siblings leave`() {
        setContent(
            container(
                width = wrapContent(),
                aspect = aspect(2.0),
                items = listOf(
                    fixedBox(200, 40),
                    container(
                        id = "shrunk",
                        width = fixed(200),
                        height = wrapContent(constrained = true),
                        items = listOf(fixedBox(200, 50)),
                    ),
                    fixedBox(150, 30),
                ),
            )
        )

        rule.onNodeWithTag("shrunk").assertHeightIsEqualTo(30.dp)
    }

    @Test
    fun `match_parent child keeps its max_size below its share`() {
        setContent(
            container(
                width = wrapContent(),
                aspect = aspect(2.0),
                items = listOf(
                    fixedBox(200, 20),
                    container(id = "bounded", width = fixed(200), height = matchParent(maxSize = 30)),
                ),
            )
        )

        rule.onNodeWithTag("bounded").assertHeightIsEqualTo(30.dp)
    }

    @Test
    fun `overlap match_parent child does not define the wrap_content width`() {
        setContent(
            container(
                id = "aspect",
                width = wrapContent(),
                aspect = aspect(2.0),
                orientation = DivContainer.Orientation.OVERLAP,
                items = listOf(
                    fixedBox(100, 40),
                    container(width = matchParent(), items = listOf(fixedBox(300, 10))),
                ),
            )
        )

        rule.onNodeWithTag("aspect").assertWidthIsEqualTo(100.dp)
    }

    @Test
    fun `overlap match_parent child defines the wrap_content width when its sibling is gone`() {
        setContent(
            container(
                id = "aspect",
                width = wrapContent(),
                aspect = aspect(2.0),
                orientation = DivContainer.Orientation.OVERLAP,
                items = listOf(
                    container(width = fixed(100), height = fixed(40), visibility = constant(DivVisibility.GONE)),
                    container(width = matchParent(), items = listOf(fixedBox(300, 10))),
                ),
            )
        )

        rule.onNodeWithTag("aspect").assertWidthIsEqualTo(300.dp)
    }

    @Test
    fun `tabs child is measured without intrinsic queries`() {
        setContent(
            container(
                id = "aspect",
                width = wrapContent(),
                aspect = aspect(2.0),
                items = listOf(
                    tabs(width = wrapContent(), items = listOf(tabItem(title = "Tab", div = fixedBox(120, 40)))),
                ),
            )
        )

        rule.onNodeWithTag("aspect").assertWidthIsEqualTo(120.dp)
        rule.onNodeWithTag("aspect").assertHeightIsEqualTo(60.dp)
    }

    @Test
    fun `overlap match_parent child does not define the wrap_content width next to an aspect sibling`() {
        setContent(
            container(
                id = "aspect",
                width = wrapContent(),
                aspect = aspect(2.0),
                orientation = DivContainer.Orientation.OVERLAP,
                items = listOf(
                    container(width = fixed(100), aspect = aspect(1.0)),
                    container(width = matchParent(), items = listOf(fixedBox(300, 10))),
                ),
            )
        )

        rule.onNodeWithTag("aspect").assertWidthIsEqualTo(100.dp)
    }

    @Test
    fun `ratio change resizes the container`() {
        setContent(
            container(
                id = "aspect",
                width = wrapContent(),
                aspect = aspect(doubleExpression("@{ratio}")),
                items = listOf(fixedBox(200, 40)),
            )
        )

        ratio.set(4.0)
        rule.waitForIdle()

        rule.onNodeWithTag("aspect").assertHeightIsEqualTo(50.dp)
    }

    @Test
    fun `vertical gallery keeps its scroll position when the ratio changes`() {
        setContent(galleryInAspectContainer())
        rule.onNode(hasScrollToIndexAction()).performScrollToIndex(3)
        rule.waitForIdle()

        ratio.set(4.0)
        rule.waitForIdle()

        assertItemAtGalleryTop("item3")
    }

    @Test
    fun `vertical gallery with default_item keeps the user scroll position when the ratio changes`() {
        setContent(galleryInAspectContainer(defaultItem = 1))
        rule.waitForIdle()
        assertItemAtGalleryTop("item1")
        rule.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
        rule.waitForIdle()

        ratio.set(1.0)
        rule.waitForIdle()

        assertItemAtGalleryTop("item0")
    }

    @Test
    fun `multi-column gallery with default_item keeps the user scroll position when the ratio changes`() {
        setContent(galleryInAspectContainer(defaultItem = 6, columnCount = 2))
        rule.waitForIdle()
        assertItemAtGalleryTop("item6")
        rule.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
        rule.waitForIdle()

        ratio.set(1.0)
        rule.waitForIdle()

        assertItemAtGalleryTop("item0")
    }

    @Test
    fun `non-positive ratio lays the container out without aspect until the ratio turns valid`() {
        setContent(
            container(
                id = "aspect",
                width = wrapContent(),
                aspect = aspect(doubleExpression("@{invalid_ratio}")),
                items = listOf(
                    fixedBox(200, 40),
                    container(id = "child", width = fixed(200), height = matchParent(), items = listOf(fixedBox(200, 30))),
                ),
            )
        )

        rule.onNodeWithTag("aspect").assertHeightIsEqualTo(70.dp)
        rule.onNodeWithTag("child").assertHeightIsEqualTo(30.dp)

        invalidRatio.set(2.0)
        rule.waitForIdle()

        rule.onNodeWithTag("aspect").assertHeightIsEqualTo(100.dp)
        rule.onNodeWithTag("child").assertHeightIsEqualTo(60.dp)
    }

    @Test
    fun `aspect container defines the height of a match_parent sibling in a row`() {
        setContent(
            container(
                width = wrapContent(),
                orientation = DivContainer.Orientation.HORIZONTAL,
                items = listOf(
                    container(width = wrapContent(), aspect = aspect(2.0), items = listOf(fixedBox(200, 40))),
                    container(id = "sibling", width = fixed(50), height = matchParent()),
                ),
            )
        )

        rule.onNodeWithTag("sibling").assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun `aspect container with min_size width defines the height of a match_parent sibling in a row`() {
        setContent(
            container(
                width = wrapContent(),
                orientation = DivContainer.Orientation.HORIZONTAL,
                items = listOf(
                    container(
                        id = "aspect",
                        width = wrapContent(minSize = 200),
                        aspect = aspect(2.0),
                        items = listOf(fixedBox(100, 20)),
                    ),
                    container(id = "sibling", width = fixed(50), height = matchParent()),
                ),
            )
        )

        rule.onNodeWithTag("aspect").assertHeightIsEqualTo(100.dp)
        rule.onNodeWithTag("sibling").assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun `nested aspect container wraps its own content`() {
        setContent(
            container(
                id = "outer",
                width = wrapContent(),
                aspect = aspect(2.0),
                items = listOf(
                    container(
                        id = "inner",
                        width = wrapContent(),
                        aspect = aspect(4.0),
                        items = listOf(fixedBox(100, 10)),
                    ),
                ),
            )
        )

        rule.onNodeWithTag("outer").assertHeightIsEqualTo(50.dp)
        rule.onNodeWithTag("inner").assertHeightIsEqualTo(25.dp)
    }

    private fun galleryInAspectContainer(defaultItem: Long = 0, columnCount: Long? = null): Div =
        container(
            width = wrapContent(),
            orientation = DivContainer.Orientation.HORIZONTAL,
            aspect = aspect(doubleExpression("@{ratio}")),
            items = listOf(
                gallery(
                    id = "gallery",
                    width = fixed(200),
                    height = matchParent(),
                    orientation = constant(DivGallery.Orientation.VERTICAL),
                    columnCount = columnCount?.let { constant(it) },
                    defaultItem = constant(defaultItem),
                    items = List(10) { container(id = "item$it", width = fixed(200), height = fixed(60)) },
                ),
            ),
        )

    private fun assertItemAtGalleryTop(tag: String) {
        val galleryTop = rule.onNodeWithTag("gallery").getUnclippedBoundsInRoot().top
        assertEquals(galleryTop, rule.onNodeWithTag(tag).getUnclippedBoundsInRoot().top)
    }

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }

    private fun fixedBox(width: Int, height: Int): Div =
        container(width = fixed(width), height = fixed(height))
}
