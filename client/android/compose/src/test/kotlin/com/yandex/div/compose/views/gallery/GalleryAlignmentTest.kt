package com.yandex.div.compose.views.gallery

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
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
import com.yandex.div2.DivGallery
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class GalleryAlignmentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `paging snap positions account for asymmetric content padding`() {
        val actual = DivGallery.ContentAlignment.entries.map { alignment ->
            alignment.toSnapPosition().position(
                layoutSize = 200,
                itemSize = 40,
                beforeContentPadding = 16,
                afterContentPadding = 24,
                itemIndex = 2,
                itemCount = 8,
            )
        }

        assertEquals(listOf(0, 64, 120), actual)
    }

    @Test
    fun `default scroll mode aligns content to start`() {
        assertEquals(
            DivGallery.ContentAlignment.START,
            DivGallery.ScrollMode.DEFAULT.defaultScrollContentAlignment(),
        )
    }

    @Test
    fun `paging scroll mode aligns content to center`() {
        assertEquals(
            DivGallery.ContentAlignment.CENTER,
            DivGallery.ScrollMode.PAGING.defaultScrollContentAlignment(),
        )
    }

    @Test
    fun `start alignment uses the padded viewport start`() {
        val offset = DivGallery.ContentAlignment.START.calculateDesiredScrollOffset(
            viewportSizePx = 200,
            itemSizePx = 40,
            startPaddingPx = 16,
            endPaddingPx = 24,
        )

        assertEquals(0, offset)
    }

    @Test
    fun `center alignment uses the viewport center`() {
        val offset = DivGallery.ContentAlignment.CENTER.calculateDesiredScrollOffset(
            viewportSizePx = 200,
            itemSizePx = 40,
            startPaddingPx = 16,
            endPaddingPx = 24,
        )

        assertEquals(64, offset)
    }

    @Test
    fun `end alignment uses the padded viewport end`() {
        val offset = DivGallery.ContentAlignment.END.calculateDesiredScrollOffset(
            viewportSizePx = 200,
            itemSizePx = 40,
            startPaddingPx = 16,
            endPaddingPx = 24,
        )

        assertEquals(120, offset)
    }

    @Test
    fun `list alignment is reactive while default item is not`() {
        val alignment = Variable.StringVariable("alignment", "center")
        val defaultItem = Variable.IntegerVariable("default_item", 2)
        val variableController = DivVariableController().apply {
            declare(alignment)
            declare(defaultItem)
        }
        val configuration = DivConfiguration(
            reporter = TestReporter(),
            variableController = variableController,
        )

        composeRule.setContent(
            configuration = configuration,
            data = data(
                gallery(
                    defaultItem = intExpression("@{default_item}"),
                    height = fixed(constant(100L)),
                    id = "gallery",
                    itemSpacing = constant(8L),
                    items = List(5) { index ->
                        text(
                            height = fixed(constant(100L)),
                            id = "item$index",
                            text = constant(index.toString()),
                            width = fixed(constant(100L)),
                        )
                    },
                    scrollContentAlignment = contentAlignmentExpression("@{alignment}"),
                    scrollMode = constant(DivGallery.ScrollMode.PAGING),
                    width = fixed(constant(300L)),
                )
            ),
        )

        val gallery = composeRule.onNodeWithTag("gallery")
        val galleryBounds = gallery.fetchSemanticsNode().boundsInRoot
        val item = composeRule.onNodeWithTag("item2")
        val initialItemBounds = item.fetchSemanticsNode().boundsInRoot
        assertEquals(galleryBounds.center.x, initialItemBounds.center.x, absoluteTolerance = 0.5f)

        defaultItem.set(0)
        composeRule.waitForIdle()

        alignment.set("end")
        composeRule.waitForIdle()

        val endItemBounds = item.fetchSemanticsNode().boundsInRoot
        assertEquals(galleryBounds.right, endItemBounds.right, absoluteTolerance = 0.5f)

        alignment.set("start")
        composeRule.waitForIdle()

        val startItemBounds = item.fetchSemanticsNode().boundsInRoot
        assertEquals(galleryBounds.left, startItemBounds.left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `grid alignment is reactive while default item is not`() {
        val alignment = Variable.StringVariable("alignment", "center")
        val defaultItem = Variable.IntegerVariable("default_item", 4)
        val variableController = DivVariableController().apply {
            declare(alignment)
            declare(defaultItem)
        }
        val configuration = DivConfiguration(
            reporter = TestReporter(),
            variableController = variableController,
        )

        composeRule.setContent(
            configuration = configuration,
            data = data(
                gallery(
                    columnCount = constant(2L),
                    defaultItem = intExpression("@{default_item}"),
                    height = fixed(constant(108L)),
                    id = "gallery",
                    itemSpacing = constant(8L),
                    items = List(12) { index ->
                        text(
                            height = fixed(constant(50L)),
                            id = "item$index",
                            text = constant(index.toString()),
                            width = fixed(constant(100L)),
                        )
                    },
                    scrollContentAlignment = contentAlignmentExpression("@{alignment}"),
                    width = fixed(constant(300L)),
                )
            ),
        )

        val gallery = composeRule.onNodeWithTag("gallery")
        val galleryBounds = gallery.fetchSemanticsNode().boundsInRoot
        val item = composeRule.onNodeWithTag("item4")
        val initialItemBounds = item.fetchSemanticsNode().boundsInRoot
        assertEquals(galleryBounds.center.x, initialItemBounds.center.x, absoluteTolerance = 0.5f)

        defaultItem.set(0)
        composeRule.waitForIdle()

        alignment.set("end")
        composeRule.waitForIdle()

        val endItemBounds = item.fetchSemanticsNode().boundsInRoot
        assertEquals(galleryBounds.right, endItemBounds.right, absoluteTolerance = 0.5f)

        alignment.set("start")
        composeRule.waitForIdle()

        val startItemBounds = item.fetchSemanticsNode().boundsInRoot
        assertEquals(galleryBounds.left, startItemBounds.left, absoluteTolerance = 0.5f)
    }

    @Test
    fun `list default item remains zero after expression change`() {
        assertInitialZeroDefaultItemIsNotReactive(columnCount = null, nextDefaultItem = 2)
    }

    @Test
    fun `grid default item remains zero after expression change`() {
        assertInitialZeroDefaultItemIsNotReactive(columnCount = constant(2L), nextDefaultItem = 4)
    }

    private fun assertInitialZeroDefaultItemIsNotReactive(
        columnCount: Expression<Long>?,
        nextDefaultItem: Int,
    ) {
        val defaultItem = Variable.IntegerVariable("default_item", 0)
        val variableController = DivVariableController().apply { declare(defaultItem) }
        val configuration = DivConfiguration(
            reporter = TestReporter(),
            variableController = variableController,
        )

        composeRule.setContent(
            configuration = configuration,
            data = data(
                gallery(
                    columnCount = columnCount,
                    defaultItem = intExpression("@{default_item}"),
                    height = fixed(constant(108L)),
                    id = "gallery",
                    items = List(8) { index ->
                        text(
                            height = fixed(constant(50L)),
                            id = "item$index",
                            text = constant(index.toString()),
                            width = fixed(constant(100L)),
                        )
                    },
                    scrollContentAlignment = constant(DivGallery.ContentAlignment.START),
                    width = fixed(constant(300L)),
                )
            ),
        )

        val galleryBounds = composeRule.onNodeWithTag("gallery").fetchSemanticsNode().boundsInRoot
        val firstItem = composeRule.onNodeWithTag("item0")
        assertEquals(
            galleryBounds.left,
            firstItem.fetchSemanticsNode().boundsInRoot.left,
            absoluteTolerance = 0.5f,
        )

        defaultItem.set(nextDefaultItem.toLong())
        composeRule.waitForIdle()

        assertEquals(
            galleryBounds.left,
            firstItem.fetchSemanticsNode().boundsInRoot.left,
            absoluteTolerance = 0.5f,
        )
    }

    private fun contentAlignmentExpression(
        expression: String,
    ): Expression<DivGallery.ContentAlignment> = Expression.MutableExpression(
        expressionKey = "test",
        rawExpression = expression,
        converter = DivGallery.ContentAlignment::fromString,
        validator = { true },
        logger = { fail(it.message) },
        typeHelper = TypeHelper.from(default = DivGallery.ContentAlignment.START) {
            it is DivGallery.ContentAlignment
        },
    )
}
