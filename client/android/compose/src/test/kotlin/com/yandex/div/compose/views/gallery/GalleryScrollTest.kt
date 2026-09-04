package com.yandex.div.compose.views.gallery

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeWithVelocity
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivGallery
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class GalleryScrollTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val restorationTester = StateRestorationTester(composeRule)
    private val alignment = Variable.StringVariable("alignment", "center")
    private val visibleItems = Variable.IntegerVariable("visible_items", 16)
    private val defaultItem = Variable.IntegerVariable("default_item", 8)
    private val startPadding = Variable.IntegerVariable("start_padding", 0)
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = DivVariableController().apply {
            declare(alignment)
            declare(visibleItems)
            declare(defaultItem)
            declare(startPadding)
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

    private fun setGalleryContent(
        columnCount: Long = 1,
        scrollMode: DivGallery.ScrollMode = DivGallery.ScrollMode.DEFAULT,
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    ) {
        val galleryData = data(
            gallery(
                columnCount = constant(columnCount),
                defaultItem = intExpression("@{default_item}"),
                height = fixed(constant(108L)),
                id = "gallery",
                items = List(16) { index ->
                    text(
                        height = fixed(constant(50L)),
                        id = "item$index",
                        text = constant(index.toString()),
                        visibility = visibilityExpression("@{visible_items > $index ? 'visible' : 'gone'}"),
                        width = fixed(constant(100L)),
                    )
                },
                paddings = DivEdgeInsets(start = intExpression("@{start_padding}")),
                scrollContentAlignment = Expression.MutableExpression(
                    expressionKey = "test",
                    rawExpression = "@{alignment}",
                    converter = DivGallery.ContentAlignment::fromString,
                    validator = { true },
                    logger = { fail(it.message) },
                    typeHelper = TypeHelper.from(default = DivGallery.ContentAlignment.CENTER) {
                        it is DivGallery.ContentAlignment
                    },
                ),
                scrollMode = constant(scrollMode),
                width = fixed(constant(300L)),
            )
        )
        restorationTester.setContent {
            val divContext = DivContext(
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
