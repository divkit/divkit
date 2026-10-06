package com.yandex.div.compose.views.pager

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toSize
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.doubleExpression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPageTransformation
import com.yandex.div2.DivPageTransformationOverlap
import com.yandex.div2.DivPageTransformationSlide
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val PAGER_SIZE = 300
private const val TOLERANCE = 0.5f

@RunWith(AndroidJUnit4::class)
class PagerPageTransformationTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val nextScale = Variable.DoubleVariable("next_scale", 0.5)

    @Test
    fun `neighbour pages are scaled and follow scale expressions`() {
        setPagerContent(
            pageSize = PAGER_SIZE / 3,
            alignment = DivPager.ItemAlignment.CENTER,
            defaultItem = 1,
            transformation = DivPageTransformation.Slide(
                DivPageTransformationSlide(
                    nextPageScale = doubleExpression("@{next_scale}"),
                    previousPageScale = constant(0.8),
                )
            ),
        )
        val pageSize = pagerBounds().width / 3

        assertEquals(pageSize * 0.8f, visibleBounds(0).width, absoluteTolerance = TOLERANCE)
        assertEquals(pageSize, visibleBounds(1).width, absoluteTolerance = TOLERANCE)
        assertEquals(pageSize * 0.5f, visibleBounds(2).width, absoluteTolerance = TOLERANCE)

        nextScale.set(0.7)
        composeRule.waitForIdle()

        assertEquals(pageSize * 0.7f, visibleBounds(2).width, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun `overlapped page stays in place while the next page slides over it`() {
        setPagerContent(transformation = overlap(reversedStackingOrder = false))
        val pager = pagerBounds()

        drag(Offset(-pager.width / 5, 0f))
        drag(Offset(-pager.width / 3, 0f))

        assertEquals(pager.left, itemBounds(0).left, absoluteTolerance = TOLERANCE)
        assertTrue(itemBounds(1).left > pager.left + 1f && itemBounds(1).left < pager.right - 1f)
        release()
    }

    @Test
    fun `overlapped page pinned outside the viewport stays in place`() {
        // The wide spacing keeps the visible pages during the drag,
        // so LazyList scrolls without remeasuring the pinned previous page.
        setPagerContent(
            pageSize = PAGER_SIZE / 3,
            itemSpacing = PAGER_SIZE / 3L,
            defaultItem = 1,
            transformation = overlap(reversedStackingOrder = false),
        )
        val pager = pagerBounds()

        drag(Offset(-pager.width / 5, 0f))

        assertEquals(pager.left, itemBounds(0).left, absoluteTolerance = TOLERANCE)
        assertEquals(pager.left, itemBounds(1).left, absoluteTolerance = TOLERANCE)
        release()
    }

    @Test
    fun `reversed overlap keeps the next page in place while the selected page slides away`() {
        setPagerContent(transformation = overlap(reversedStackingOrder = true))
        val pager = pagerBounds()

        drag(Offset(-pager.width / 5, 0f))
        drag(Offset(-pager.width / 3, 0f))

        assertEquals(pager.left, itemBounds(1).left, absoluteTolerance = TOLERANCE)
        assertTrue(itemBounds(0).right > pager.left + 1f && itemBounds(0).right < pager.right - 1f)
        release()
    }

    @Test
    fun `overlapped page stays in place in a right-to-left pager`() {
        setPagerContent(transformation = overlap(reversedStackingOrder = false), layoutDirection = LayoutDirection.Rtl)
        val pager = pagerBounds()

        drag(Offset(pager.width / 5, 0f))
        drag(Offset(pager.width / 3, 0f))

        assertEquals(pager.left, itemBounds(0).left, absoluteTolerance = TOLERANCE)
        assertTrue(itemBounds(1).right > pager.left + 1f && itemBounds(1).right < pager.right - 1f)
        release()
    }

    @Test
    fun `overlapped page stays in place in a vertical pager`() {
        setPagerContent(transformation = overlap(reversedStackingOrder = false), orientation = DivPager.Orientation.VERTICAL)
        val pager = pagerBounds()

        drag(Offset(0f, -pager.height / 5))
        drag(Offset(0f, -pager.height / 3))

        assertEquals(pager.top, itemBounds(0).top, absoluteTolerance = TOLERANCE)
        assertTrue(itemBounds(1).top > pager.top + 1f && itemBounds(1).top < pager.bottom - 1f)
        release()
    }

    private fun setPagerContent(
        pageSize: Int = PAGER_SIZE,
        itemSpacing: Long = 0,
        alignment: DivPager.ItemAlignment = DivPager.ItemAlignment.START,
        defaultItem: Long = 0,
        orientation: DivPager.Orientation = DivPager.Orientation.HORIZONTAL,
        transformation: DivPageTransformation,
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    ) {
        val isHorizontal = orientation == DivPager.Orientation.HORIZONTAL
        val data = data(
            Div.Pager(
                value = DivPager(
                    defaultItem = constant(defaultItem),
                    height = fixed(if (isHorizontal) PAGER_SIZE / 3 else PAGER_SIZE),
                    id = "pager",
                    itemSpacing = DivFixedSize(value = constant(itemSpacing)),
                    items = List(5) { index ->
                        text(
                            height = fixed(if (isHorizontal) PAGER_SIZE / 3 else pageSize),
                            id = "item$index",
                            text = constant(index.toString()),
                            width = fixed(if (isHorizontal) pageSize else PAGER_SIZE / 3),
                        )
                    },
                    layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
                    orientation = constant(orientation),
                    pageTransformation = transformation,
                    scrollAxisAlignment = constant(alignment),
                    width = fixed(if (isHorizontal) PAGER_SIZE else PAGER_SIZE / 3),
                )
            )
        )
        val configuration = divConfiguration {
            reporter = TestReporter()
            variableController = DivVariableController().apply { declare(nextScale) }
        }
        composeRule.setContentWithDivContext(configuration) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                DivView(data = data)
            }
        }
    }

    private fun overlap(reversedStackingOrder: Boolean) = DivPageTransformation.Overlap(
        DivPageTransformationOverlap(reversedStackingOrder = Expression.constant(reversedStackingOrder))
    )

    private fun drag(offset: Offset) {
        composeRule.onNodeWithTag("pager").performTouchInput {
            if (currentPosition() == null) down(center)
            moveBy(offset)
        }
    }

    private fun release() {
        composeRule.onNodeWithTag("pager").performTouchInput { up() }
        composeRule.waitForIdle()
    }

    private fun pagerBounds(): Rect = composeRule.onNodeWithTag("pager").fetchSemanticsNode().boundsInRoot

    /** Unlike [visibleBounds], these bounds are not clipped by the pager and ignore the page scale. */
    private fun itemBounds(index: Int): Rect {
        val node = composeRule.onNodeWithTag("item$index", useUnmergedTree = true).fetchSemanticsNode()
        return Rect(node.positionInRoot, node.size.toSize())
    }

    private fun visibleBounds(index: Int): Rect =
        composeRule.onNodeWithTag("item$index", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
}
