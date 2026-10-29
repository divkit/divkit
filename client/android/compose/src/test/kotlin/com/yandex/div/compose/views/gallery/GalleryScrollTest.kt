package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeWithVelocity
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.views.gallery.scroll.GalleryScrollDestination
import com.yandex.div.compose.views.gallery.scroll.galleryScrollLayout
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.scrollByAction
import com.yandex.div.test.data.scrollToAction
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.throwingErrorLogger
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionScrollBy
import com.yandex.div2.DivActionScrollDestination
import com.yandex.div2.DivData
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivGallery
import com.yandex.div2.DivMatchParentSize
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnitValue
import com.yandex.div2.EndDestination
import com.yandex.div2.IndexDestination
import com.yandex.div2.OffsetDestination
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.runner.RunWith

private const val MAX_SCROLL_START_FRAMES = 200

@RunWith(AndroidJUnit4::class)
class GalleryScrollTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val restorationTester = StateRestorationTester(composeRule)
    private lateinit var divContext: DivContext
    private lateinit var galleryData: DivData
    private lateinit var lazyGridState: LazyStaggeredGridState
    private lateinit var scrollScope: CoroutineScope
    private val measuredItems = mutableSetOf<Int>()
    private val alignment = Variable.StringVariable("alignment", "center")
    private val visibleItems = Variable.IntegerVariable("visible_items", 16)
    private val defaultItem = Variable.IntegerVariable("default_item", 8)
    private val paging = Variable.StringVariable("scroll_mode", "default")
    private val startPadding = Variable.IntegerVariable("start_padding", 0)
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = DivVariableController().apply {
            declare(alignment)
            declare(visibleItems)
            declare(defaultItem)
            declare(startPadding)
            declare(paging)
        },
    )

    @Test
    fun `list keeps user scroll position when alignment changes`() {
        setGalleryContent()
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(8)
        val itemStart = itemBounds(8).left

        alignment.set("end")
        composeRule.waitForIdle()

        assertEquals(itemStart, itemBounds(8).left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `grid keeps user scroll position when alignment changes`() {
        setGalleryContent(columnCount = 2)
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(8)
        val itemStart = itemBounds(8).left

        alignment.set("end")
        composeRule.waitForIdle()

        assertEquals(itemStart, itemBounds(8).left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `list keeps user scroll offset when padding changes`() {
        setGalleryContent()
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(8)

        startPadding.set(32)
        composeRule.waitForIdle()

        assertEquals(
            32f * composeRule.density.density,
            itemBounds(8).left - galleryBounds().left,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `grid keeps user scroll offset when padding changes`() {
        setGalleryContent(columnCount = 2)
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(8)

        startPadding.set(32)
        composeRule.waitForIdle()

        assertEquals(
            32f * composeRule.density.density,
            itemBounds(8).left - galleryBounds().left,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `list applies original default when initially empty items appear`() {
        visibleItems.set(0)
        setGalleryContent()
        defaultItem.set(2)
        composeRule.waitForIdle()

        visibleItems.set(16)
        composeRule.waitForIdle()

        assertDefaultItemCentered()
    }

    @Test
    fun `grid applies original default when initially empty items appear`() {
        visibleItems.set(0)
        setGalleryContent(columnCount = 2)
        defaultItem.set(2)
        composeRule.waitForIdle()

        visibleItems.set(16)
        composeRule.waitForIdle()

        assertDefaultItemCentered()
    }

    @Test
    fun `list reclamps original default when more items appear`() {
        visibleItems.set(2)
        setGalleryContent()

        visibleItems.set(16)
        composeRule.waitForIdle()

        assertDefaultItemCentered()
    }

    @Test
    fun `grid reclamps original default when more items appear`() {
        visibleItems.set(2)
        setGalleryContent(columnCount = 2)

        visibleItems.set(16)
        composeRule.waitForIdle()

        assertDefaultItemCentered()
    }

    @Test
    fun `list does not reapply default after user scroll and items arrive`() {
        visibleItems.set(6)
        setGalleryContent()
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(0)

        visibleItems.set(16)
        composeRule.waitForIdle()

        assertEquals(galleryBounds().left, itemBounds(0).left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `grid does not reapply default after user scroll and items arrive`() {
        visibleItems.set(6)
        setGalleryContent(columnCount = 2)
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(0)

        visibleItems.set(16)
        composeRule.waitForIdle()

        assertEquals(galleryBounds().left, itemBounds(0).left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `list keeps restored user scroll position when more items arrive`() {
        visibleItems.set(6)
        setGalleryContent()
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(0)

        restorationTester.emulateSavedInstanceStateRestore()
        assertEquals(galleryBounds().left, itemBounds(0).left, absoluteTolerance = 0.5f)

        visibleItems.set(16)
        composeRule.waitForIdle()

        assertEquals(galleryBounds().left, itemBounds(0).left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `grid keeps restored user scroll position when more items arrive`() {
        visibleItems.set(8)
        setGalleryContent(columnCount = 2)
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(0)

        restorationTester.emulateSavedInstanceStateRestore()
        assertEquals(galleryBounds().left, itemBounds(0).left, absoluteTolerance = 0.5f)

        visibleItems.set(16)
        composeRule.waitForIdle()

        assertEquals(galleryBounds().left, itemBounds(0).left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `paging fling centers an item in the full viewport with asymmetric padding`() {
        startPadding.set(32)
        setGalleryContent(scrollMode = DivGallery.ScrollMode.PAGING)

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(start = center, end = centerLeft, endVelocity = 0f)
        }
        composeRule.waitForIdle()

        assertSnappedItemCentered()
    }

    @Test
    fun `rtl paging fling centers an item in the full viewport with asymmetric padding`() {
        startPadding.set(32)
        setGalleryContent(
            scrollMode = DivGallery.ScrollMode.PAGING,
            layoutDirection = LayoutDirection.Rtl,
        )

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(start = center, end = centerRight, endVelocity = 0f)
        }
        composeRule.waitForIdle()

        assertSnappedItemCentered()
    }

    @Test
    fun `grid paging drag centers an item with asymmetric padding`() {
        startPadding.set(32)
        setGalleryContent(columnCount = 2, scrollMode = DivGallery.ScrollMode.PAGING)

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(start = center, end = centerLeft, endVelocity = 0f)
        }

        assertSnappedItemCentered()
    }

    @Test
    fun `rtl grid paging drag centers an item with asymmetric padding`() {
        startPadding.set(32)
        setGalleryContent(
            columnCount = 2,
            scrollMode = DivGallery.ScrollMode.PAGING,
            layoutDirection = LayoutDirection.Rtl,
        )

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(start = center, end = centerRight, endVelocity = 0f)
        }

        assertSnappedItemCentered()
    }

    @Test
    fun `vertical grid paging drag centers an item with asymmetric padding`() {
        startPadding.set(32)
        setGalleryContent(
            columnCount = 2,
            scrollMode = DivGallery.ScrollMode.PAGING,
            orientation = DivGallery.Orientation.VERTICAL,
        )

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(start = center, end = topCenter, endVelocity = 0f)
        }

        assertEquals(
            galleryBounds().center.y,
            itemBounds(10).center.y,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `grid paging drag uses updated start alignment`() {
        startPadding.set(32)
        setGalleryContent(columnCount = 2, scrollMode = DivGallery.ScrollMode.PAGING)
        alignment.set("start")
        composeRule.waitForIdle()

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(start = center, end = centerLeft, endVelocity = 0f)
        }

        assertEquals(
            galleryBounds().left + 32f * composeRule.density.density,
            itemBounds(10).left,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `grid paging drag uses updated end alignment`() {
        setGalleryContent(columnCount = 2, scrollMode = DivGallery.ScrollMode.PAGING)
        alignment.set("end")
        composeRule.waitForIdle()

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(start = center, end = centerLeft, endVelocity = 0f)
        }

        assertEquals(galleryBounds().right, itemBounds(10).right, absoluteTolerance = 0.5f)
    }

    @Test
    fun `grid forward fling centers the last fully visible item`() {
        setGalleryContent(columnCount = 2, scrollMode = DivGallery.ScrollMode.PAGING)

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(start = center, end = centerLeft, endVelocity = 1000f)
        }

        assertEquals(galleryBounds().center.x, itemBounds(12).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `grid backward fling centers the first fully visible item`() {
        setGalleryContent(columnCount = 2, scrollMode = DivGallery.ScrollMode.PAGING)

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(start = center, end = centerRight, endVelocity = 1000f)
        }

        assertEquals(galleryBounds().center.x, itemBounds(4).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `grid starts snapping when scroll mode changes to paging`() {
        setGalleryContent(columnCount = 2)
        paging.set("paging")
        composeRule.waitForIdle()

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(start = center, end = centerLeft, endVelocity = 0f)
        }

        assertSnappedItemCentered()
    }

    @Test
    fun `grid stops snapping when scroll mode changes to default`() {
        setGalleryContent(columnCount = 2, scrollMode = DivGallery.ScrollMode.PAGING)
        paging.set("default")
        composeRule.waitForIdle()

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput {
            swipeWithVelocity(start = center, end = centerLeft, endVelocity = 0f)
        }

        assertTrue(abs(itemBounds(10).center.x - galleryBounds().center.x) > 1f)
    }

    @Test
    fun `match parent item fills horizontal gallery content viewport`() {
        defaultItem.set(0)
        startPadding.set(32)
        setGalleryContent(matchParentScrollAxis = true)

        assertEquals(
            galleryBounds().width - 32f * composeRule.density.density,
            itemBounds(0).width,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `match parent item fills vertical gallery content viewport`() {
        defaultItem.set(0)
        startPadding.set(32)
        setGalleryContent(orientation = DivGallery.Orientation.VERTICAL, matchParentScrollAxis = true)

        assertEquals(
            galleryBounds().height - 32f * composeRule.density.density,
            itemBounds(0).height,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `min size with margins may exceed horizontal gallery viewport`() {
        defaultItem.set(0)
        setGalleryContent(matchParentScrollAxis = true, itemMinSize = 400, itemMargins = DivEdgeInsets(
            left = constant(10L), right = constant(10L),
        ))

        assertEquals(
            400f * composeRule.density.density,
            composeRule.onNodeWithTag("item0").fetchSemanticsNode().size.width.toFloat(),
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `min size with margins may exceed vertical gallery viewport`() {
        defaultItem.set(0)
        setGalleryContent(
            orientation = DivGallery.Orientation.VERTICAL,
            matchParentScrollAxis = true,
            itemMinSize = 400,
            itemMargins = DivEdgeInsets(top = constant(10L), bottom = constant(10L)),
        )

        assertEquals(
            400f * composeRule.density.density,
            composeRule.onNodeWithTag("item0").fetchSemanticsNode().size.height.toFloat(),
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `match parent item excludes margins from horizontal gallery viewport`() {
        defaultItem.set(0)
        setGalleryContent(matchParentScrollAxis = true, itemMargins = DivEdgeInsets(
            left = constant(10L), right = constant(10L),
        ))

        assertEquals(
            galleryBounds().width - 20f * composeRule.density.density,
            itemBounds(0).width,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `match parent item excludes margins from vertical gallery viewport`() {
        defaultItem.set(0)
        setGalleryContent(
            orientation = DivGallery.Orientation.VERTICAL,
            matchParentScrollAxis = true,
            itemMargins = DivEdgeInsets(top = constant(10L), bottom = constant(10L)),
        )

        assertEquals(
            galleryBounds().height - 20f * composeRule.density.density,
            itemBounds(0).height,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `match parent item fills horizontal grid gallery content viewport`() {
        defaultItem.set(0)
        startPadding.set(32)
        setGalleryContent(columnCount = 2, matchParentScrollAxis = true)

        assertEquals(
            galleryBounds().width - 32f * composeRule.density.density,
            itemBounds(0).width,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `match parent item fills vertical grid gallery content viewport`() {
        defaultItem.set(0)
        startPadding.set(32)
        setGalleryContent(columnCount = 2, orientation = DivGallery.Orientation.VERTICAL, matchParentScrollAxis = true)

        assertEquals(
            galleryBounds().height - 32f * composeRule.density.density,
            itemBounds(0).height,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `min size may exceed horizontal grid gallery viewport`() {
        defaultItem.set(0)
        setGalleryContent(columnCount = 2, matchParentScrollAxis = true, itemMinSize = 400)

        assertEquals(
            400f * composeRule.density.density,
            composeRule.onNodeWithTag("item0").fetchSemanticsNode().size.width.toFloat(),
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `horizontal min size uses the child variable scope`() {
        galleryData = data(
            gallery(
                id = "gallery",
                width = fixed(300),
                height = fixed(108),
                items = listOf(text(
                    id = "item0",
                    text = constant("0"),
                    width = DivSize.MatchParent(DivMatchParentSize(
                        minSize = DivSizeUnitValue(value = intExpression("@{size}")),
                    )),
                    height = fixed(50),
                    variables = listOf(variable("size", 400)),
                )),
            ),
            variables = listOf(variable("size", 100)),
        )

        setContent()

        assertEquals(
            400f * composeRule.density.density,
            composeRule.onNodeWithTag("item0").fetchSemanticsNode().size.width.toFloat(),
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `vertical max size uses the child variable scope`() {
        galleryData = data(
            gallery(
                id = "gallery",
                orientation = constant(DivGallery.Orientation.VERTICAL),
                width = fixed(108),
                height = fixed(300),
                items = listOf(text(
                    id = "item0",
                    text = constant("0"),
                    width = fixed(50),
                    height = DivSize.MatchParent(DivMatchParentSize(
                        maxSize = DivSizeUnitValue(value = intExpression("@{size}")),
                    )),
                    variables = listOf(variable("size", 200)),
                )),
            ),
            variables = listOf(variable("size", 100)),
        )

        setContent()

        assertEquals(
            200f * composeRule.density.density,
            composeRule.onNodeWithTag("item0").fetchSemanticsNode().size.height.toFloat(),
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `item visible by its local variable is shown`() {
        galleryData = data(
            scopedItemGallery(
                text(
                    height = fixed(50),
                    id = "item0",
                    text = constant("0"),
                    variables = listOf(variable("hidden", false)),
                    visibility = visibilityExpression("@{hidden ? 'gone' : 'visible'}"),
                    width = fixed(100),
                )
            ),
            variables = listOf(variable("hidden", true)),
        )

        setContent()

        composeRule.onNodeWithTag("item0").assertIsDisplayed()
    }

    @Test
    fun `trigger of a scoped item keeps firing while the item is scrolled out and back`() {
        val condition = Variable.BooleanVariable("condition", false)
        val firings = Variable.IntegerVariable("firings", 0)
        configuration.variableController.declare(condition, firings)
        galleryData = data(scopedItemGallery(
            text(
                height = fixed(50),
                id = "item0",
                text = constant("0"),
                triggers = listOf(trigger(
                    action = action(
                        typed = setVariableAction(
                            name = "firings",
                            value = typedValue(intExpression("@{firings + 1}")),
                        )
                    ),
                    condition = "@{condition}",
                )),
                width = fixed(100),
            )
        ))
        setContent()
        val gallery = composeRule.onNode(hasScrollToIndexAction())

        gallery.performScrollToIndex(15)
        composeRule.onNodeWithTag("item0").assertDoesNotExist()
        composeRule.runOnIdle { condition.set(true) }
        composeRule.runOnIdle { condition.set(false) }
        gallery.performScrollToIndex(0)
        composeRule.runOnIdle { condition.set(true) }

        composeRule.runOnIdle { assertEquals(2L, firings.getValue()) }
    }

    @Test
    fun `scoped item keeps its local variable after it is scrolled out and back`() {
        galleryData = data(scopedItemGallery(
            text(
                actions = listOf(action(typed = setVariableAction("label", typedValue("changed")))),
                height = fixed(50),
                id = "item0",
                text = expression("@{label}"),
                variables = listOf(variable("label", "initial")),
                width = fixed(100),
            )
        ))
        setContent()
        val gallery = composeRule.onNode(hasScrollToIndexAction())
        composeRule.onNodeWithTag("item0").performClick()

        gallery.performScrollToIndex(15)
        gallery.performScrollToIndex(0)

        composeRule.onNodeWithTag("item0").assertTextEquals("changed")
    }

    @Test
    fun `offset scrolls list to an absolute position in dp`() {
        defaultItem.set(0)
        setGalleryContent()

        perform(offsetAction(54))

        assertEquals(54f * composeRule.density.density, itemBounds(1).left - galleryBounds().left, 0.5f)
    }

    @Test
    fun `offset scrolls rtl list from its logical start`() {
        defaultItem.set(0)
        setGalleryContent(layoutDirection = LayoutDirection.Rtl)

        perform(offsetAction(54))

        assertEquals(54f * composeRule.density.density, galleryBounds().right - itemBounds(1).right, 0.5f)
    }

    @Test
    fun `offset scrolls vertical grid in dp`() {
        defaultItem.set(0)
        setGalleryContent(columnCount = 2, orientation = DivGallery.Orientation.VERTICAL)

        perform(offsetAction(54))

        assertEquals(54f * composeRule.density.density, itemBounds(2).top - galleryBounds().top, 0.5f)
    }

    @Test
    fun `offset does not move paging gallery`() {
        setGalleryContent(scrollMode = DivGallery.ScrollMode.PAGING)
        val previousStart = itemBounds(8).left

        perform(offsetAction(54))

        assertEquals(previousStart, itemBounds(8).left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `relative item count advances from the aligned item`() {
        setGalleryContent()

        perform(action(typed = scrollByAction("gallery", itemCount = 2, animated = false)))

        assertEquals(galleryBounds().center.x, itemBounds(10).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `relative item count moves backwards in rtl`() {
        setGalleryContent(layoutDirection = LayoutDirection.Rtl)

        perform(action(typed = scrollByAction("gallery", itemCount = -2, animated = false)))

        assertEquals(galleryBounds().center.x, itemBounds(6).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `grid relative item count advances from the aligned item`() {
        setGalleryContent(columnCount = 2)

        perform(action(typed = scrollByAction("gallery", itemCount = 2, animated = false)))

        assertEquals(galleryBounds().center.x, itemBounds(10).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `vertical grid relative item count moves backwards from the aligned item`() {
        setGalleryContent(columnCount = 2, orientation = DivGallery.Orientation.VERTICAL)

        perform(action(typed = scrollByAction("gallery", itemCount = -2, animated = false)))

        assertEquals(galleryBounds().center.y, itemBounds(7).center.y, absoluteTolerance = 0.5f)
    }

    @Test
    fun `grid ring wraps relative item scrolling from the current item`() {
        setGalleryContent(columnCount = 2)

        perform(action(typed = scrollByAction(
            "gallery", itemCount = 10, overflow = DivActionScrollBy.Overflow.RING, animated = false,
        )))

        assertEquals(galleryBounds().center.x, itemBounds(2).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `rtl grid ring wraps backwards from the current item`() {
        setGalleryContent(columnCount = 2, layoutDirection = LayoutDirection.Rtl)

        perform(action(typed = scrollByAction(
            "gallery", itemCount = -12, overflow = DivActionScrollBy.Overflow.RING, animated = false,
        )))

        assertEquals(galleryBounds().center.x, itemBounds(13).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `rtl relative item count advances after an intermediate offset`() {
        setGalleryContent(layoutDirection = LayoutDirection.Rtl)
        perform(action(typed = scrollByAction("gallery", offset = 54, animated = false)))

        perform(action(typed = scrollByAction("gallery", itemCount = 2, animated = false)))

        assertEquals(galleryBounds().center.x, itemBounds(10).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `rtl relative item count moves backwards after an intermediate offset`() {
        setGalleryContent(layoutDirection = LayoutDirection.Rtl)
        perform(action(typed = scrollByAction("gallery", offset = 54, animated = false)))

        perform(action(typed = scrollByAction("gallery", itemCount = -2, animated = false)))

        assertEquals(galleryBounds().center.x, itemBounds(7).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `rtl relative item count reaches an offscreen next item`() {
        defaultItem.set(0)
        alignment.set("start")
        setGalleryContent(
            layoutDirection = LayoutDirection.Rtl,
            matchParentScrollAxis = true,
            itemMinSize = 400,
        )
        perform(offsetAction(54))

        perform(action(typed = scrollByAction("gallery", itemCount = 1, animated = false)))

        assertEquals(galleryBounds().right, itemBounds(1).right, absoluteTolerance = 0.5f)
    }

    @Test
    fun `relative item count reaches an offscreen previous item`() {
        alignment.set("end")
        setGalleryContent(matchParentScrollAxis = true, itemMinSize = 400)
        perform(action(typed = scrollByAction("gallery", offset = -54, animated = false)))

        perform(action(typed = scrollByAction("gallery", itemCount = -1, animated = false)))

        assertEquals(galleryBounds().right, itemBounds(7).right, absoluteTolerance = 0.5f)
    }

    @Test
    fun `clamp stops relative item scrolling at the start`() {
        alignment.set("start")
        setGalleryContent()

        perform(action(typed = scrollByAction("gallery", itemCount = -100, animated = false)))

        assertEquals(galleryBounds().left, itemBounds(0).left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `ring wraps relative item scrolling to the end`() {
        defaultItem.set(0)
        alignment.set("start")
        setGalleryContent()

        perform(action(typed = scrollByAction(
            "gallery", itemCount = -1, overflow = DivActionScrollBy.Overflow.RING, animated = false,
        )))

        assertEquals(galleryBounds().right, itemBounds(15).right, absoluteTolerance = 0.5f)
    }

    @Test
    fun `relative offset scrolls in dp from the current position`() {
        defaultItem.set(0)
        setGalleryContent()

        perform(action(typed = scrollByAction("gallery", offset = 54, animated = false)))

        assertEquals(54f * composeRule.density.density, itemBounds(1).left - galleryBounds().left, 0.5f)
    }

    @Test
    fun `ring offset is a no op when content fits in the viewport`() {
        defaultItem.set(0)
        visibleItems.set(2)
        alignment.set("start")
        setGalleryContent()

        perform(action(typed = scrollByAction(
            "gallery", offset = 54, overflow = DivActionScrollBy.Overflow.RING, animated = false,
        )))

        assertEquals(galleryBounds().left, itemBounds(0).left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `new command cancels an animated grid scroll`() {
        defaultItem.set(0)
        setGalleryContent(columnCount = 2)
        composeRule.mainClock.autoAdvance = false
        perform(action(url = "div-action://set_current_item?id=gallery&item=14"))
        composeRule.mainClock.advanceTimeBy(64)

        perform(action(url = "div-action://set_current_item?id=gallery&item=4&animated=false"))
        composeRule.mainClock.advanceTimeBy(2_000)

        assertEquals(galleryBounds().center.x, itemBounds(4).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `distant grid scroll skips intermediate items`() {
        setLazyGridContent()

        scrollGridTo(index = 900, animated = false)

        assertEquals(galleryBounds().center.x, itemBounds(900).center.x, absoluteTolerance = 0.5f)
        assertTrue(measuredItems.size < 100, "Measured ${measuredItems.size} items")
    }

    @Test
    fun `backward grid scroll does not traverse items from the start`() {
        setLazyGridContent(initialIndex = 900)

        scrollGridTo(index = 890, animated = false)

        assertEquals(galleryBounds().center.x, itemBounds(890).center.x, absoluteTolerance = 0.5f)
        assertTrue(measuredItems.size < 100, "Measured ${measuredItems.size} items")
    }

    @Test
    fun `distant backward grid animation completes within a bounded time`() {
        setLazyGridContent(initialIndex = 900)
        composeRule.mainClock.autoAdvance = false

        scrollGridTo(index = 100, animated = true)
        composeRule.mainClock.advanceTimeBy(5_000)

        assertEquals(galleryBounds().center.x, itemBounds(100).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `scroll action centers a distant rtl grid item`() {
        defaultItem.set(0)
        visibleItems.set(1_000)
        setGalleryContent(columnCount = 2, itemsCount = 1_000, layoutDirection = LayoutDirection.Rtl)

        perform(action(url = "div-action://set_current_item?id=gallery&item=900&animated=false"))

        assertEquals(galleryBounds().center.x, itemBounds(900).center.x, absoluteTolerance = 0.5f)
    }

    @Test
    fun `list end animation finishes for an oversized last item`() {
        lateinit var state: LazyListState
        composeRule.setContent {
            state = rememberLazyListState()
            scrollScope = rememberCoroutineScope()
            LazyRow(Modifier.size(300.dp, 108.dp), state = state) {
                item { Box(Modifier.size(10_000.dp, 50.dp)) }
            }
        }
        composeRule.waitForIdle()
        composeRule.mainClock.autoAdvance = false

        composeRule.runOnIdle {
            scrollScope.launch {
                state.galleryScrollLayout(composeRule.density).scrollToItem(
                    GalleryScrollDestination(index = 0, toEnd = true),
                    animated = true,
                    alignment = DivGallery.ContentAlignment.CENTER,
                    duration = null,
                )
            }
        }
        composeRule.mainClock.advanceTimeBy(5_000)

        composeRule.runOnIdle { assertFalse(state.canScrollForward) }
    }

    @Test
    fun `vertical grid end action includes padding after the longest lane`() {
        galleryData = data(gallery(
            id = "gallery",
            width = fixed(108),
            height = fixed(300),
            orientation = constant(DivGallery.Orientation.VERTICAL),
            columnCount = constant(2L),
            paddings = DivEdgeInsets(bottom = constant(32L)),
            items = listOf(
                text(id = "item0", text = constant("0"), width = fixed(50), height = fixed(400)),
                text(id = "item1", text = constant("1"), width = fixed(50), height = fixed(100)),
            ),
        ))
        setContent()

        perform(action(url = "div-action://scroll_to_end?id=gallery&animated=false"))

        assertEquals(
            galleryBounds().bottom - 32f * composeRule.density.density,
            itemBounds(0).bottom,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `rtl list end action includes end padding`() {
        galleryData = data(gallery(
            id = "gallery",
            width = fixed(300),
            height = fixed(108),
            paddings = DivEdgeInsets(start = constant(16L), end = constant(32L)),
            items = listOf(text(
                id = "item0", text = constant("0"), width = fixed(1_000), height = fixed(50),
            )),
        ))
        setContent(LayoutDirection.Rtl)

        perform(action(url = "div-action://scroll_to_end?id=gallery&animated=false"))

        assertEquals(
            galleryBounds().left + 32f * composeRule.density.density,
            itemBounds(0).left,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `distant grid end action reaches the end of a long earlier lane`() {
        setLazyGridContent(itemWidths = listOf(10_000) + List(80) { 100 })

        scrollGridTo(index = 80, animated = false, toEnd = true)

        val item = composeRule.onNodeWithTag("item0").fetchSemanticsNode()
        assertEquals(
            galleryBounds().right,
            item.positionInRoot.x + item.size.width,
            absoluteTolerance = 0.5f,
        )
    }

    @Test
    fun `distant grid end animation completes within a bounded time`() {
        setLazyGridContent()
        composeRule.mainClock.autoAdvance = false

        scrollGridTo(index = 999, animated = true, toEnd = true)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.runOnIdle { assertTrue(lazyGridState.canScrollForward) }
        composeRule.mainClock.advanceTimeBy(5_000)

        composeRule.runOnIdle { assertFalse(lazyGridState.canScrollForward) }
    }

    @Test
    fun `scroll with duration reaches the item at the deadline`() {
        defaultItem.set(0)
        setGalleryContent()

        performAnimated(action(typed = scrollToAction(
            "gallery", DivActionScrollDestination.Index(IndexDestination(constant(4L))), duration = constant(10_000L),
        )))
        awaitScrollStart(observedIndex = 1)
        composeRule.mainClock.advanceTimeBy(5_000)

        assertFalse(isItemCentered(4))
        composeRule.mainClock.advanceTimeBy(5_500)
        assertTrue(isItemCentered(4))
    }

    @Test
    fun `scroll without duration animates at a distance based pace`() {
        defaultItem.set(0)
        setGalleryContent()

        performAnimated(action(url = "div-action://set_current_item?id=gallery&item=4"))
        val elapsed = awaitScrollEnd(observedIndex = 1)

        assertTrue(isItemCentered(4))
        // 300 dp at 50 ms per inch, extended by the deceleration phase: about 280 ms.
        assertTrue(elapsed in 100..1_000, "Scrolled in $elapsed ms")
    }

    @Test
    fun `grid end scroll with duration reaches the end at the deadline`() {
        setGalleryContent(columnCount = 2)

        performAnimated(action(typed = scrollToAction(
            "gallery", DivActionScrollDestination.End(EndDestination()), duration = constant(10_000L),
        )))
        awaitScrollStart(observedIndex = 8)
        composeRule.mainClock.advanceTimeBy(5_000)

        assertFalse(isItemEndAligned(15))
        composeRule.mainClock.advanceTimeBy(5_500)
        assertTrue(isItemEndAligned(15))
    }

    @Test
    fun `scroll action aligns an item in a wrap content grid`() {
        setLanesGalleryContent()

        perform(action(url = "div-action://set_current_item?id=gallery&item=5&animated=false"))

        assertEquals(galleryBounds().left, itemBounds(5).left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `relative item scroll advances in a wrap content grid`() {
        setLanesGalleryContent()

        perform(action(typed = scrollByAction("gallery", itemCount = 3, animated = false)))

        assertEquals(galleryBounds().left, itemBounds(3).left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `end action reaches the end of a wrap content grid`() {
        setLanesGalleryContent()

        perform(action(url = "div-action://scroll_to_end?id=gallery&animated=false"))

        assertEquals(galleryBounds().right, itemBounds(11).right, absoluteTolerance = 0.5f)
    }

    @Test
    fun `animated scroll in a wrap content grid reaches the item`() {
        setLanesGalleryContent()

        performAnimated(action(url = "div-action://set_current_item?id=gallery&item=5"))
        val elapsed = awaitScrollEnd(observedIndex = 5)

        assertEquals(galleryBounds().left, itemBounds(5).left, absoluteTolerance = 0.5f)
        assertTrue(elapsed in 50..1_000, "Scrolled in $elapsed ms")
    }

    private fun setLanesGalleryContent(itemsCount: Int = 12) {
        galleryData = data(gallery(
            id = "gallery",
            columnCount = constant(2L),
            width = fixed(300),
            height = wrapContent(),
            items = List(itemsCount) { index ->
                text(id = "item$index", text = constant(index.toString()), width = fixed(100), height = fixed(50))
            },
        ))
        setContent()
    }

    private fun performAnimated(action: DivAction) {
        composeRule.mainClock.autoAdvance = false
        perform(action)
    }

    // The observed item must stay fully visible: clipped bounds hide the movement of an edge item.
    private fun awaitScrollStart(observedIndex: Int) {
        val start = itemBounds(observedIndex).left
        repeat(MAX_SCROLL_START_FRAMES) {
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.waitForIdle()
            if (abs(itemBounds(observedIndex).left - start) >= 1f) return
        }
        fail("The scroll has not started")
    }

    // Returns the animation time in milliseconds after the scroll has started.
    private fun awaitScrollEnd(observedIndex: Int): Long {
        awaitScrollStart(observedIndex)
        val startedAt = composeRule.mainClock.currentTime
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()
        return composeRule.mainClock.currentTime - startedAt
    }

    private fun isItemEndAligned(index: Int): Boolean {
        val galleryRight = galleryBounds().right
        val itemWidth = 100f * composeRule.density.density
        return composeRule.onAllNodesWithTag("item$index").fetchSemanticsNodes().any {
            abs(it.boundsInRoot.right - galleryRight) <= 0.5f && it.boundsInRoot.width >= itemWidth - 0.5f
        }
    }

    private fun isItemCentered(index: Int): Boolean {
        val galleryCenter = galleryBounds().center.x
        return composeRule.onAllNodesWithTag("item$index").fetchSemanticsNodes().any {
            abs(it.boundsInRoot.center.x - galleryCenter) <= 0.5f
        }
    }

    private fun setLazyGridContent(
        initialIndex: Int = 0,
        itemWidths: List<Int> = List(1_000) { if (it % 2 == 0) 100 else 150 },
    ) {
        composeRule.setContent {
            lazyGridState = rememberLazyStaggeredGridState(initialFirstVisibleItemIndex = initialIndex)
            scrollScope = rememberCoroutineScope()
            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.Fixed(2),
                modifier = Modifier.size(300.dp, 108.dp).testTag("gallery"),
                state = lazyGridState,
            ) {
                items(itemWidths.size) { index ->
                    Box(
                        Modifier
                            .size(itemWidths[index].dp, 50.dp)
                            .testTag("item$index")
                            .layout { measurable, constraints ->
                                measuredItems.add(index)
                                val placeable = measurable.measure(constraints)
                                layout(placeable.width, placeable.height) {
                                    placeable.placeRelative(0, 0)
                                }
                            }
                    )
                }
            }
        }
        composeRule.waitForIdle()
        measuredItems.clear()
    }

    private fun scrollGridTo(index: Int, animated: Boolean, toEnd: Boolean = false) {
        composeRule.runOnIdle {
            scrollScope.launch {
                lazyGridState.galleryScrollLayout(composeRule.density).scrollToItem(
                    GalleryScrollDestination(index, toEnd),
                    animated = animated,
                    alignment = DivGallery.ContentAlignment.CENTER,
                    duration = null,
                )
            }
        }
    }

    private fun perform(action: DivAction) {
        composeRule.runOnIdle {
            divContext.debugFeatures.performAction(galleryData, action)
        }
    }

    private fun offsetAction(offset: Long) = action(typed = scrollToAction(
        "gallery", DivActionScrollDestination.Offset(OffsetDestination(constant(offset))), animated = false,
    ))

    private fun setGalleryContent(
        columnCount: Long = 1,
        scrollMode: DivGallery.ScrollMode = DivGallery.ScrollMode.DEFAULT,
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
        orientation: DivGallery.Orientation = DivGallery.Orientation.HORIZONTAL,
        matchParentScrollAxis: Boolean = false,
        itemMinSize: Int? = null,
        itemMargins: DivEdgeInsets? = null,
        itemsCount: Int = 16,
    ) {
        paging.set(scrollMode.name.lowercase())
        val isHorizontal = orientation == DivGallery.Orientation.HORIZONTAL
        galleryData = data(
            gallery(
                columnCount = constant(columnCount),
                defaultItem = intExpression("@{default_item}"),
                height = fixed(if (isHorizontal) 108 else 300),
                id = "gallery",
                items = List(itemsCount) { index ->
                    text(
                        height = if (!isHorizontal && matchParentScrollAxis) matchParent(minSize = itemMinSize) else fixed(if (isHorizontal) 50 else 100),
                        id = "item$index",
                        margins = itemMargins,
                        text = constant(index.toString()),
                        visibility = visibilityExpression("@{visible_items > $index ? 'visible' : 'gone'}"),
                        width = if (isHorizontal && matchParentScrollAxis) matchParent(minSize = itemMinSize) else fixed(if (isHorizontal) 100 else 50),
                    )
                },
                orientation = constant(orientation),
                paddings = if (isHorizontal) {
                    DivEdgeInsets(start = intExpression("@{start_padding}"))
                } else {
                    DivEdgeInsets(top = intExpression("@{start_padding}"))
                },
                scrollContentAlignment = Expression.MutableExpression(
                    expressionKey = "test",
                    rawExpression = "@{alignment}",
                    converter = DivGallery.ContentAlignment::fromString,
                    validator = { true },
                    logger = throwingErrorLogger,
                    typeHelper = TypeHelper.from(default = DivGallery.ContentAlignment.CENTER) {
                        it is DivGallery.ContentAlignment
                    },
                ),
                scrollMode = Expression.MutableExpression(
                    expressionKey = "test",
                    rawExpression = "@{scroll_mode}",
                    converter = DivGallery.ScrollMode::fromString,
                    validator = { true },
                    logger = throwingErrorLogger,
                    typeHelper = TypeHelper.from(default = DivGallery.ScrollMode.DEFAULT) {
                        it is DivGallery.ScrollMode
                    },
                ),
                width = fixed(if (isHorizontal) 300 else 108),
            ),
        )
        setContent(layoutDirection)
    }

    private fun scopedItemGallery(scopedItem: Div): Div = gallery(
        height = fixed(108),
        id = "gallery",
        items = listOf(scopedItem) + List(15) { index ->
            text(
                height = fixed(50),
                id = "item${index + 1}",
                text = constant("${index + 1}"),
                width = fixed(100),
            )
        },
    )

    private fun setContent(layoutDirection: LayoutDirection = LayoutDirection.Ltr) {
        restorationTester.setContent {
            divContext = DivContext(
                baseContext = LocalContext.current,
                configuration = configuration,
            )
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalLayoutDirection provides layoutDirection,
            ) {
                DivView(data = galleryData)
            }
        }
        composeRule.waitForIdle()
    }

    private fun assertDefaultItemCentered() {
        assertEquals(galleryBounds().center.x, itemBounds(8).center.x, absoluteTolerance = 0.5f)
    }

    private fun assertSnappedItemCentered() {
        val galleryCenter = galleryBounds().center.x
        val closestDistance = composeRule.onAllNodes(
            SemanticsMatcher("gallery item") {
                it.config.getOrNull(SemanticsProperties.TestTag)?.startsWith("item") == true
            }
        ).fetchSemanticsNodes().minOf { abs(it.boundsInRoot.center.x - galleryCenter) }

        assertEquals(0f, closestDistance, absoluteTolerance = 0.5f)
    }

    private fun galleryBounds() = composeRule.onNodeWithTag("gallery").fetchSemanticsNode().boundsInRoot

    private fun itemBounds(index: Int) = composeRule.onNodeWithTag("item$index").fetchSemanticsNode().boundsInRoot
}
