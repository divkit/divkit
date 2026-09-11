package com.yandex.div.compose.views.gallery

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.text
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.DivGallery
import com.yandex.div2.DivSize
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class GalleryGridViewTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val configuration = DivConfiguration(reporter = TestReporter())

    @Test
    fun `horizontal gallery puts next item in shortest lane when axes are bounded`() {
        setGalleryContent(
            height = fixed(108),
            itemSizes = listOf(100L to 50L, 50L to 50L, 50L to 50L, 50L to 50L),
            orientation = DivGallery.Orientation.HORIZONTAL,
        )

        val bounds = List(4) { itemBounds(it) }
        val laneStarts = listOf(bounds[0].top, bounds[1].top)

        assertEquals(listOf(0, 1, 1, 0), bounds.map { laneStarts.indexOf(it.top) })
    }

    @Test
    fun `vertical wrap content gallery puts next item in shortest lane`() {
        setGalleryContent(
            height = wrapContent(),
            itemSizes = listOf(50L to 100L, 50L to 50L, 50L to 50L, 50L to 50L),
            orientation = DivGallery.Orientation.VERTICAL,
        )

        val bounds = List(4) { itemBounds(it) }
        val laneStarts = listOf(bounds[0].left, bounds[1].left)

        assertEquals(listOf(0, 1, 1, 0), bounds.map { laneStarts.indexOf(it.left) })
    }

    @Test
    fun `match parent item fills resolved lane in wrap content gallery`() {
        composeRule.setContent(
            configuration = configuration,
            data = data(
                gallery(
                    columnCount = constant(2L),
                    height = fixed(100),
                    items = listOf(
                        text(
                            height = fixed(50),
                            id = "fixedItem",
                            text = constant("fixed"),
                            width = fixed(100),
                        ),
                        text(
                            height = fixed(50),
                            id = "matchParentItem",
                            text = constant(""),
                            width = matchParent(),
                        ),
                    ),
                    orientation = constant(DivGallery.Orientation.VERTICAL),
                    width = wrapContent(),
                )
            ),
        )

        assertEquals(itemBounds("fixedItem").width, itemBounds("matchParentItem").width)
    }

    @Test
    fun `default item keeps lanes assigned from the first item`() {
        setGalleryContent(
            defaultItem = 5L,
            height = fixed(108),
            itemSizes = listOf(
                300L to 50L,
                400L to 50L,
                200L to 50L,
                300L to 50L,
                400L to 50L,
                200L to 50L,
                300L to 50L,
            ),
            orientation = DivGallery.Orientation.HORIZONTAL,
        )

        assertTrue(itemBounds(4).top < itemBounds(5).top)
    }

    @Test
    fun `wrap content grid supports nested gallery items`() {
        composeRule.setContent(
            configuration = configuration,
            data = data(
                gallery(
                    columnCount = constant(2L),
                    height = wrapContent(),
                    items = listOf(
                        gallery(
                            height = wrapContent(),
                            items = listOf(
                                text(
                                    height = fixed(50),
                                    id = "nestedItem",
                                    text = constant("nested"),
                                    width = fixed(100),
                                )
                            ),
                            width = fixed(100),
                        ),
                        text(
                            height = fixed(30),
                            text = constant("second lane"),
                            width = fixed(50),
                        ),
                    ),
                    width = fixed(300),
                )
            ),
        )

        assertTrue(itemBounds("nestedItem").height > 0f)
    }

    @Test
    fun `empty wrap content grid does not reserve lane spacing`() {
        composeRule.setContent(
            configuration = configuration,
            data = data(
                gallery(
                    columnCount = constant(2L),
                    height = wrapContent(),
                    id = "emptyGallery",
                    itemSpacing = constant(16L),
                    width = fixed(300),
                )
            ),
        )

        assertEquals(0f, itemBounds("emptyGallery").height)
    }

    @Test
    fun `vertical wrap content gallery places first lane on the right in rtl`() {
        setGalleryContent(
            crossSpacing = 8L,
            height = fixed(200),
            itemSizes = listOf(40L to 50L, 40L to 50L),
            layoutDirection = LayoutDirection.Rtl,
            orientation = DivGallery.Orientation.VERTICAL,
            width = wrapContent(),
        )

        assertEquals(itemBounds("gallery").right, itemBounds(0).right)
        assertTrue(itemBounds(1).right < itemBounds(0).left)
    }

    @Test
    fun `horizontal wrap content gallery lays out each lane from the right in rtl`() {
        setGalleryContent(
            height = wrapContent(),
            itemSizes = listOf(50L to 40L, 50L to 40L, 50L to 40L),
            layoutDirection = LayoutDirection.Rtl,
            orientation = DivGallery.Orientation.HORIZONTAL,
            width = wrapContent(),
        )

        assertEquals(itemBounds("gallery").right, itemBounds(0).right)
        assertTrue(itemBounds(2).right < itemBounds(0).left)
    }

    @Test
    fun `vertical wrap content gallery reserves all configured lanes`() {
        setGalleryContent(
            columnCount = 3L,
            crossSpacing = 8L,
            height = fixed(200),
            itemSizes = listOf(40L to 50L, 40L to 50L),
            orientation = DivGallery.Orientation.VERTICAL,
            width = wrapContent(),
        )

        val expectedWidth = with(composeRule.density) { 3 * 40.dp.roundToPx() + 2 * 8.dp.roundToPx() }
        assertEquals(expectedWidth.toFloat(), itemBounds("gallery").width)
    }

    @Test
    fun `horizontal wrap content gallery reserves all configured lanes`() {
        setGalleryContent(
            columnCount = 3L,
            crossSpacing = 8L,
            height = wrapContent(),
            itemSizes = listOf(50L to 40L, 50L to 40L),
            orientation = DivGallery.Orientation.HORIZONTAL,
        )

        val expectedHeight = with(composeRule.density) { 3 * 40.dp.roundToPx() + 2 * 8.dp.roundToPx() }
        assertEquals(expectedHeight.toFloat(), itemBounds("gallery").height)
    }

    private fun setGalleryContent(
        columnCount: Long = 2L,
        crossSpacing: Long? = null,
        defaultItem: Long = 0L,
        height: DivSize,
        itemSizes: List<Pair<Long, Long>>,
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
        orientation: DivGallery.Orientation,
        width: DivSize = fixed(300),
    ) {
        val galleryData = data(
            gallery(
                columnCount = constant(columnCount),
                crossSpacing = crossSpacing?.let(::constant),
                defaultItem = constant(defaultItem),
                height = height,
                id = "gallery",
                items = itemSizes.mapIndexed { index, (width, itemHeight) ->
                    text(
                        height = fixed(constant(itemHeight)),
                        id = "item$index",
                        text = constant(index.toString()),
                        width = fixed(constant(width)),
                    )
                },
                orientation = constant(orientation),
                width = width,
            )
        )
        composeRule.setContentWithDivContext(configuration = configuration) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                DivView(data = galleryData)
            }
        }
    }

    private fun itemBounds(index: Int) =
        itemBounds("item$index")

    private fun itemBounds(tag: String) =
        composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
}
