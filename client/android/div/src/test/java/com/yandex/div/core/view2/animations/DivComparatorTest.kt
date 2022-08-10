package com.yandex.div.core.view2.animations

import android.net.Uri
import com.yandex.div.core.asExpression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivPageSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivPercentageSize
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivComparatorTest {

    @Test
    fun `divs of the same type are replaceable`() {
        val div1 = divText(text = "Text 01")
        val div2 = divText(text = "Text 02")

        assertTrue(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `the same instance of div is replaceable`() {
        val div = divText(text = "Text")

        assertTrue(DivComparator.areDivsReplaceable(div, div, ExpressionResolver.EMPTY))
    }

    @Test
    fun `nulls are replaceable`() {
        assertTrue(DivComparator.areDivsReplaceable(null as Div?, null as Div?, ExpressionResolver.EMPTY))
    }

    @Test
    fun `divs with different type are not replaceable`() {
        val div1 = divText(text = "Text 01")
        val div2 = divImage(imageUrl = "https://image")

        assertFalse(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `divs with different ids are not replaceable`() {
        val div1 = divText(id = "01", text = "Text 01")
        val div2 = divText(id = "02", text = "Text 02")

        assertFalse(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `div customs with different type are not replaceable`() {
        val div1 = divCustom(type = "type_a")
        val div2 = divCustom(type = "type_b")

        assertFalse(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `containers with similar hierarchy are replaceable`() {
        val div1 = divContainer(items = listOf(divText(text = "Text 01")))
        val div2 = divContainer(items = listOf(divText(text = "Text 02")))

        assertTrue(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `containers with different child count are not replaceable`() {
        val div1 = divContainer(items = listOf(divText(text = "Text 01"), divImage(imageUrl = "https://image")))
        val div2 = divContainer(items = listOf(divText(text = "Text 02")))

        assertFalse(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `containers with different child types are not replaceable`() {
        val div1 = divContainer(items = listOf(divImage(imageUrl = "https://image")))
        val div2 = divContainer(items = listOf(divText(text = "Text")))

        assertFalse(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `containers with different child order are not replaceable`() {
        val div1 = divContainer(items = listOf(divImage(imageUrl = "https://image"), divText(text = "Text 01")))
        val div2 = divContainer(items = listOf(divText(text = "Text 02"), divImage(imageUrl = "https://image")))

        assertFalse(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `containers with different child ids are not replaceable`() {
        val div1 = divContainer(items = listOf(divText(id = "01", text = "Text 01")))
        val div2 = divContainer(items = listOf(divText(id = "02", text = "Text 02")))

        assertFalse(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `grids with similar hierarchy are replaceable`() {
        val div1 = divGrid(items = listOf(divText(text = "Text 01")))
        val div2 = divGrid(items = listOf(divText(text = "Text 02")))

        assertTrue(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `grids with different child count are not replaceable`() {
        val div1 = divGrid(items = listOf(divText(text = "Text 01"), divImage(imageUrl = "https://image")))
        val div2 = divGrid(items = listOf(divText(text = "Text 02")))

        assertFalse(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `grids with different child types are not replaceable`() {
        val div1 = divGrid(items = listOf(divImage(imageUrl = "https://image")))
        val div2 = divGrid(items = listOf(divText(text = "Text")))

        assertFalse(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `grids with different child order are not replaceable`() {
        val div1 = divGrid(items = listOf(divImage(imageUrl = "https://image"), divText(text = "Text 01")))
        val div2 = divGrid(items = listOf(divText(text = "Text 02"), divImage(imageUrl = "https://image")))

        assertFalse(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `grids with different child ids are not replaceable`() {
        val div1 = divGrid(items = listOf(divText(id = "01", text = "Text 01")))
        val div2 = divGrid(items = listOf(divText(id = "02", text = "Text 02")))

        assertFalse(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `galleries with hierarchy differences are replaceable`() {
        val div1 = divGallery(items = listOf(divImage(imageUrl = "https://image")))
        val div2 = divGallery(items = listOf(divText(text = "Text")))

        assertTrue(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `pagers with hierarchy differences are replaceable`() {
        val div1 = divPager(items = listOf(divImage(imageUrl = "https://image")))
        val div2 = divPager(items = listOf(divText(text = "Text")))

        assertTrue(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `tabs with hierarchy differences are replaceable`() {
        val div1 = divTabs(items = listOf(divImage(imageUrl = "https://image")))
        val div2 = divTabs(items = listOf(divText(text = "Text")))

        assertTrue(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    @Test
    fun `states with hierarchy differences are replaceable`() {
        val div1 = divState(items = listOf(divImage(imageUrl = "https://image")))
        val div2 = divState(items = listOf(divText(text = "Text")))

        assertTrue(DivComparator.areDivsReplaceable(div1, div2, ExpressionResolver.EMPTY))
    }

    private fun divText(id: String? = null, text: String): Div {
        return Div.Text(DivText(id  = id, text = text.asExpression()))
    }

    private fun divImage(id: String? = null, imageUrl: String): Div {
        return Div.Image(DivImage(id  = id, imageUrl = Uri.parse(imageUrl).asExpression()))
    }

    private fun divCustom(type: String): Div {
        return Div.Custom(DivCustom(customType = type))
    }

    private fun divContainer(
        orientation: DivContainer.Orientation = DivContainer.Orientation.VERTICAL,
        items: List<Div>
    ): Div {
        return Div.Container(
            DivContainer(
                orientation = orientation.asExpression(),
                items = items
            )
        )
    }

    private fun divGrid(items: List<Div>): Div {
        return Div.Grid(DivGrid(columnCount = 1.asExpression(), items = items))
    }

    private fun divGallery(items: List<Div>): Div {
        return Div.Gallery(DivGallery(items = items))
    }

    private fun divPager(items: List<Div>): Div {
        return Div.Pager(
            DivPager(
                layoutMode = DivPagerLayoutMode.PageSize(value = DivPageSize(pageWidth = DivPercentageSize(80.0.asExpression()))),
                items = items
            )
        )
    }

    private fun divTabs(items: List<Div>): Div {
        val tabs = items.mapIndexed { index, div ->
            DivTabs.Item(title = "tab $index".asExpression(), div = div)
        }
        return Div.Tabs(DivTabs(items = tabs))
    }

    private fun divState(items: List<Div>): Div {
        val states = items.mapIndexed { index, div ->
            DivState.State(stateId = "state_$index", div = div)
        }
        return Div.State(DivState(divId = "state_container", states = states))
    }
}
