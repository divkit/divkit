package com.yandex.div.core.view2

import android.util.Size
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.widget.makeAtMostSpec
import com.yandex.div.core.widget.makeExactSpec
import com.yandex.div.core.widget.makeUnspecifiedSpec
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.wrapContent
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.Div
import com.yandex.div2.DivAspect
import com.yandex.div2.DivContainer
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivNeighbourPageSize
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPageSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivPercentageSize
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivWrapContentSize
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivPagerMeasurementTest {
    private val underTest = Div2View(
        Div2Context(testContextThemeWrapper(), DivConfiguration.Builder(mock()).build())
    )
    private val pager = DivPager(
        id = "pager",
        height = wrapContent(),
        layoutMode = percentage(80.0),
        items = listOf(aspectPage("first"), aspectPage("second"))
    )

    @Test
    fun `first measurement uses percentage page width for aspect height`() {
        bind(pager)

        measure()

        assertEquals(120, underTest.measuredHeight)
    }

    @Test
    fun `first measurement reserves neighbour width before calculating aspect height`() {
        bind(pager.copy(layoutMode = DivPagerLayoutMode.NeighbourPageSize(
            DivNeighbourPageSize(neighbourPageWidth = DivFixedSize(
                value = constant(30L), unit = constant(DivSizeUnit.PX)
            ))
        )))

        measure()

        assertEquals(120, underTest.measuredHeight)
    }

    @Test
    fun `first measurement uses new page percentage when rebound at the same viewport size`() {
        bind(pager)
        measure()
        layout()

        bind(pager.copy(layoutMode = percentage(60.0)))
        measure()

        assertEquals(90, underTest.measuredHeight)
    }

    @Test
    fun `first measurement uses new viewport width before layout catches up`() {
        bind(pager)
        measure()
        layout()

        measure(width = 400)

        assertEquals(160, underTest.measuredHeight)
    }

    @Test
    fun `height includes a taller neighbour when viewport size is unchanged after layout`() {
        bind(pager.copy(
            defaultItem = constant(1L),
            items = listOf(container(height = fixedPx(70)), container(height = fixedPx(50)))
        ))
        measure()
        layout()

        pagerView().viewPager.viewTreeObserver.dispatchOnPreDraw()
        measure()

        assertEquals(70, underTest.measuredHeight)
    }

    @Test
    fun `width includes a wider neighbour when vertical viewport size is unchanged after layout`() {
        bind(pager.copy(
            orientation = constant(DivPager.Orientation.VERTICAL),
            width = wrapContent(),
            height = fixedPx(300),
            defaultItem = constant(1L),
            items = listOf(container(width = fixedPx(200)), container(width = fixedPx(120)))
        ))
        underTest.measure(makeUnspecifiedSpec(), makeExactSpec(300))
        layout()

        pagerView().viewPager.viewTreeObserver.dispatchOnPreDraw()
        underTest.measure(makeUnspecifiedSpec(), makeExactSpec(300))

        assertEquals(200, underTest.measuredWidth)
    }

    @Test
    fun `first measurement preserves intrinsic width of content sized page`() {
        bind(pager.copy(
            layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
            items = listOf(container(
                id = "first",
                width = DivSize.WrapContent(DivWrapContentSize(constrained = constant(true))),
                items = listOf(container(width = fixedPx(120), height = fixedPx(100)))
            ))
        ))

        measure()

        assertEquals(120, underTest.findViewWithTag<View>("first").measuredWidth)
    }

    @Test
    fun `page width is updated during measurement when fixed height pager has bounded width`() {
        bind(pager.copy(
            height = fixedPx(180),
            items = listOf(container(
                id = "first",
                items = listOf(container(width = fixedPx(300), height = fixedPx(1)))
            ))
        ))

        pagerView().measure(makeAtMostSpec(300), makeExactSpec(180))

        assertEquals(240, underTest.findViewWithTag<View>("first").measuredWidth)
    }

    @Test
    fun `page has new dimensions on first layout when exact sized pager is rebound`() {
        val fixedPager = pager.copy(height = fixedPx(180))
        bind(fixedPager)
        measure()
        layout()

        bind(fixedPager.copy(layoutMode = percentage(60.0)))
        measure()
        layout()

        val page = underTest.findViewWithTag<View>("first")
        assertEquals(Size(180, 90), Size(page.width, page.height))
    }

    private fun bind(model: DivPager) {
        underTest.setData(data(Div.Pager(model)), DivDataTag("pager"))
    }

    private fun measure(width: Int = 300) {
        underTest.measure(makeExactSpec(width), makeUnspecifiedSpec())
    }

    private fun layout() {
        underTest.layout(0, 0, underTest.measuredWidth, underTest.measuredHeight)
    }

    private fun pagerView(): DivPagerView = underTest.findViewWithTag("pager")

    private fun percentage(value: Double) = DivPagerLayoutMode.PageSize(
        DivPageSize(pageWidth = DivPercentageSize(value = constant(value)))
    )

    private fun fixedPx(value: Int) = DivSize.Fixed(
        DivFixedSize(value = constant(value.toLong()), unit = constant(DivSizeUnit.PX))
    )

    private fun aspectPage(id: String) = Div.Container(DivContainer(
        id = id,
        height = wrapContent(),
        aspect = DivAspect(ratio = constant(2.0)),
        items = emptyList()
    ))
}
