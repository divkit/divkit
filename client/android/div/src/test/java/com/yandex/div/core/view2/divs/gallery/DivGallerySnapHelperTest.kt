package com.yandex.div.core.view2.divs.gallery

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DivLinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div2.DivGallery
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivGallerySnapHelperTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `forward fling targets visible last item`() {
        val fixture = fixture(RecyclerView.VERTICAL)
        fixture.stubPositions(lastCompletelyVisible = 7, lastVisible = 9)

        val target = fixture.snapHelper.findTargetSnapPosition(fixture.layoutManager, 0, FLING_VELOCITY)

        assertEquals(9, target)
    }

    @Test
    fun `backward fling targets visible first item`() {
        val fixture = fixture(RecyclerView.VERTICAL)
        fixture.stubPositions(firstCompletelyVisible = 2, firstVisible = 0)

        val target = fixture.snapHelper.findTargetSnapPosition(fixture.layoutManager, 0, -FLING_VELOCITY)

        assertEquals(0, target)
    }

    @Test
    fun `forward fling in middle targets completely visible item`() {
        val fixture = fixture(RecyclerView.VERTICAL, itemCount = 12)
        fixture.stubPositions(lastCompletelyVisible = 7, lastVisible = 9)

        val target = fixture.snapHelper.findTargetSnapPosition(fixture.layoutManager, 0, FLING_VELOCITY)

        assertEquals(7, target)
    }

    @Test
    fun `negative horizontal fling in rtl targets visible last item`() {
        val fixture = fixture(RecyclerView.HORIZONTAL, isRtl = true)
        fixture.stubPositions(lastCompletelyVisible = 7, lastVisible = 9)

        val target = fixture.snapHelper.findTargetSnapPosition(fixture.layoutManager, -FLING_VELOCITY, 0)

        assertEquals(9, target)
    }

    @Test
    fun `forward fling in staggered grid targets completely visible item at adapter end`() {
        val fixture = fixture(RecyclerView.VERTICAL, columnCount = 2)
        fixture.stubPositions(lastCompletelyVisible = 7, lastVisible = 9)

        val target = fixture.snapHelper.findTargetSnapPosition(fixture.layoutManager, 0, FLING_VELOCITY)

        assertEquals(7, target)
    }

    private fun fixture(
        orientation: Int,
        itemCount: Int = ITEM_COUNT,
        isRtl: Boolean = false,
        columnCount: Int = 1,
    ): Fixture {
        val view = spy(DivRecyclerView(context))
        doReturn(if (isRtl) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR)
            .whenever(view).layoutDirection
        val layoutManager = if (columnCount == 1) {
            spy(
                DivLinearLayoutManager(
                    view = view,
                    divView = mock<Div2View>(),
                    orientation = orientation,
                    crossContentAlignment = DivGallery.ContentAlignment.START,
                )
            )
        } else {
            spy(
                DivGridLayoutManager(
                    view = view,
                    divView = mock<Div2View>(),
                    orientation = orientation,
                    crossContentAlignment = DivGallery.ContentAlignment.START,
                    columnCount = columnCount,
                    itemSpacing = 0,
                    crossSpacing = 0,
                )
            )
        }
        view.layoutManager = layoutManager
        doReturn(itemCount).whenever(layoutManager).itemCount
        return Fixture(DivGallerySnapHelper(view), layoutManager, layoutManager as DivGalleryItemHelper)
    }

    private class Fixture(
        val snapHelper: DivGallerySnapHelper,
        val layoutManager: RecyclerView.LayoutManager,
        private val itemHelper: DivGalleryItemHelper,
    ) {
        fun stubPositions(
            firstCompletelyVisible: Int = 4,
            lastCompletelyVisible: Int = 7,
            firstVisible: Int = 3,
            lastVisible: Int = 8,
        ) {
            whenever(itemHelper.firstCompletelyVisibleItemPosition()).thenReturn(firstCompletelyVisible)
            whenever(itemHelper.lastCompletelyVisibleItemPosition()).thenReturn(lastCompletelyVisible)
            whenever(itemHelper.firstVisibleItemPosition()).thenReturn(firstVisible)
            whenever(itemHelper.lastVisibleItemPosition()).thenReturn(lastVisible)
        }
    }

    private companion object {
        const val ITEM_COUNT = 10
        const val FLING_VELOCITY = 1_000
    }
}
