package com.yandex.div.compose.views.gallery

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContent
import com.yandex.div.test.data.alignmentHorizontalExpression
import com.yandex.div.test.data.alignmentVerticalExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivGallery
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Every layout path of a gallery reads the cross-axis alignment of an item on the item's behalf,
 * so it has to resolve it in the item scope; the card declares the same name with another value.
 * The 50 dp item is aligned to the far side of its 100 dp lane.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
class GalleryChildScopeTest(private val layout: Layout) {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `item cross-axis alignment from its local variable is applied`() {
        rule.setContent(
            configuration = divConfiguration { reporter = TestReporter() },
            data = data(
                layout.gallery(listOf(layout.item(), filler())),
                variables = listOf(variable("align", layout.cardAlignment)),
            ),
        )

        val item = bounds("item")
        val gallery = bounds("gallery")
        val offset = if (layout.isHorizontal) item.top - gallery.top else item.left - gallery.left
        assertEquals(50f * rule.density.density, offset, absoluteTolerance = 0.5f)
    }

    private fun bounds(tag: String) = rule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot

    enum class Layout(val isHorizontal: Boolean) {
        LIST(isHorizontal = true) {
            override fun gallery(items: List<Div>) = gallery(
                height = fixed(100),
                id = "gallery",
                items = items,
                width = fixed(300),
            )
        },
        ROW(isHorizontal = true) {
            override fun gallery(items: List<Div>) = gallery(
                height = fixed(100),
                id = "gallery",
                items = items,
                width = wrapContent(),
            )
        },
        COLUMN(isHorizontal = false) {
            override fun gallery(items: List<Div>) = gallery(
                height = wrapContent(),
                id = "gallery",
                items = items,
                orientation = constant(DivGallery.Orientation.VERTICAL),
                width = fixed(100),
            )
        },
        HORIZONTAL_GRID(isHorizontal = true) {
            override fun gallery(items: List<Div>) = gallery(
                columnCount = constant(2L),
                crossSpacing = constant(0L),
                height = fixed(200),
                id = "gallery",
                items = items,
                width = fixed(300),
            )
        },
        VERTICAL_GRID(isHorizontal = false) {
            override fun gallery(items: List<Div>) = gallery(
                columnCount = constant(2L),
                crossSpacing = constant(0L),
                height = fixed(300),
                id = "gallery",
                items = items,
                orientation = constant(DivGallery.Orientation.VERTICAL),
                width = fixed(200),
            )
        },
        MEASURED_LANES(isHorizontal = true) {
            override fun gallery(items: List<Div>) = gallery(
                columnCount = constant(2L),
                crossSpacing = constant(0L),
                height = wrapContent(),
                id = "gallery",
                items = items,
                width = fixed(300),
            )
        };

        val cardAlignment: String
            get() = if (isHorizontal) "top" else "left"

        abstract fun gallery(items: List<Div>): Div

        fun item(): Div = text(
            alignmentHorizontal = if (isHorizontal) null else alignmentHorizontalExpression(ALIGN),
            alignmentVertical = if (isHorizontal) alignmentVerticalExpression(ALIGN) else null,
            height = fixed(50),
            id = "item",
            text = constant("item"),
            variables = listOf(variable("align", if (isHorizontal) "bottom" else "right")),
            width = fixed(50),
        )
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases(): List<Layout> = cases

        // The runner calls the provider once per case, so the list is built once.
        private val cases: List<Layout> = Layout.entries
    }
}

private const val ALIGN = "@{align}"

/**
 * Sets the 100 dp lane size where the lane size comes from the content.
 */
private fun filler(): Div = text(
    height = fixed(100),
    text = constant("filler"),
    width = fixed(100),
)
