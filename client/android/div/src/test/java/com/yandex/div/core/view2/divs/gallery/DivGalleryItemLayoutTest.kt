package com.yandex.div.core.view2.divs.gallery

import android.content.Context
import android.os.Handler
import android.os.Looper.getMainLooper
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.internal.widget.DivLayoutParams
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowViewGroup

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [DivGalleryItemLayoutTest.ShadowDivRecyclerView::class])
class DivGalleryItemLayoutTest {

    private val context = RuntimeEnvironment.application

    @Test
    fun `request horizontal gallery remeasure when item exceeds content height`() {
        val recyclerView = createRecyclerView(width = 100, height = 60).apply {
            setPadding(0, 10, 0, 10)
            setCrossAxisMeasureSpec(unspecifiedSpec(), RecyclerView.HORIZONTAL)
        }
        val item = createItem(recyclerView, RecyclerView.HORIZONTAL, width = 50, height = 50)
        recyclerView.shadow().requestLayoutCalls = 0

        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()

        assertEquals(1, recyclerView.shadow().requestLayoutCalls)
    }

    @Test
    fun `request vertical gallery remeasure when grid item exceeds content width`() {
        val recyclerView = createRecyclerView(width = 100, height = 100).apply {
            setCrossAxisMeasureSpec(unspecifiedSpec(), RecyclerView.VERTICAL)
        }
        val item = createItem(recyclerView, RecyclerView.VERTICAL, width = 60, height = 50).apply {
            columnCount = { 2 }
            crossSpacing = { 4f }
        }
        recyclerView.shadow().requestLayoutCalls = 0

        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()

        assertEquals(1, recyclerView.shadow().requestLayoutCalls)
    }

    @Test
    fun `request bounded gallery remeasure when item exceeds content height`() {
        val recyclerView = createRecyclerView(width = 100, height = 60).apply {
            setCrossAxisMeasureSpec(atMostSpec(100), RecyclerView.HORIZONTAL)
        }
        val item = createItem(recyclerView, RecyclerView.HORIZONTAL, width = 50, height = 80)
        recyclerView.shadow().requestLayoutCalls = 0

        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()

        assertEquals(1, recyclerView.shadow().requestLayoutCalls)
    }

    @Test
    fun `do not request bounded gallery remeasure when limit is reached`() {
        val recyclerView = createRecyclerView(width = 100, height = 60).apply {
            setCrossAxisMeasureSpec(atMostSpec(60), RecyclerView.HORIZONTAL)
        }
        val item = createItem(recyclerView, RecyclerView.HORIZONTAL, width = 50, height = 80)
        recyclerView.shadow().requestLayoutCalls = 0

        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()

        assertEquals(0, recyclerView.shadow().requestLayoutCalls)
    }

    @Test
    fun `use decorated odd cross spacing when requesting grid gallery remeasure`() {
        val recyclerView = createRecyclerView(width = 158, height = 100).apply {
            setCrossAxisMeasureSpec(unspecifiedSpec(), RecyclerView.VERTICAL)
        }
        val item = createItem(recyclerView, RecyclerView.VERTICAL, width = 50, height = 50).apply {
            columnCount = { 3 }
            crossSpacing = { 5f }
        }
        recyclerView.shadow().requestLayoutCalls = 0

        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()

        assertEquals(0, recyclerView.shadow().requestLayoutCalls)
    }

    @Test
    fun `use decorated odd cross spacing when measuring grid item`() {
        val recyclerView = createRecyclerView(width = 160, height = 100)
        val item = createItem(recyclerView, RecyclerView.VERTICAL, width = MATCH_PARENT, height = 50).apply {
            columnCount = { 3 }
            crossSpacing = { 5f }
        }

        item.measure(unspecifiedSpec(), unspecifiedSpec())

        assertEquals(50, item.measuredWidth)
    }

    @Test
    fun `request gallery remeasure after larger item becomes visible`() {
        val recyclerView = createRecyclerView().apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = TestAdapter(context, itemWidth = 200, itemHeights = intArrayOf(50, 80))
        }

        recyclerView.measureAndLayout(exactSpec(100), atMostSpec(100))
        drainMainLoop()
        assertEquals(50, recyclerView.measuredHeight)

        recyclerView.scrollBy(200, 0)
        recyclerView.shadow().requestLayoutCalls = 0
        drainMainLoop()

        assertEquals(1, recyclerView.shadow().requestLayoutCalls)
    }

    @Test
    fun `request horizontal gallery remeasure when effective cross axis is exact`() {
        val recyclerView = createRecyclerView(width = 100, height = 60).apply {
            heightMeasureSpec = exactSpec(60)
            parentCrossAxisMeasureSpec = unspecifiedSpec()
        }
        val item = createItem(recyclerView, RecyclerView.HORIZONTAL, width = 50, height = 80)
        recyclerView.shadow().requestLayoutCalls = 0

        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()

        assertEquals(1, recyclerView.shadow().requestLayoutCalls)
    }

    @Test
    fun `request gallery remeasure again after cross axis remeasure reset`() {
        val recyclerView = createRecyclerView(width = 100, height = 60).apply {
            setCrossAxisMeasureSpec(unspecifiedSpec(), RecyclerView.HORIZONTAL)
        }
        val item = createItem(recyclerView, RecyclerView.HORIZONTAL, width = 50, height = 80)
        recyclerView.shadow().requestLayoutCalls = 0

        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()
        assertEquals(1, recyclerView.shadow().requestLayoutCalls)

        (item.getChildAt(0).layoutParams as DivLayoutParams).height = 70
        recyclerView.resetCrossAxisRemeasure()
        recyclerView.shadow().requestLayoutCalls = 0
        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()

        assertEquals(1, recyclerView.shadow().requestLayoutCalls)
    }

    @Test
    fun `remeasure again when gallery returns to a previous size`() {
        val recyclerView = createRecyclerView(width = 100, height = 60).apply {
            setCrossAxisMeasureSpec(unspecifiedSpec(), RecyclerView.HORIZONTAL)
        }
        val item = createItem(recyclerView, RecyclerView.HORIZONTAL, width = 50, height = 80)
        recyclerView.shadow().requestLayoutCalls = 0

        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()
        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()

        assertEquals(1, recyclerView.shadow().requestLayoutCalls)

        recyclerView.measureAndLayout(exactSpec(100), exactSpec(80))
        recyclerView.measureAndLayout(exactSpec(100), exactSpec(60))
        recyclerView.setCrossAxisMeasureSpec(unspecifiedSpec(), RecyclerView.HORIZONTAL)
        recyclerView.shadow().requestLayoutCalls = 0
        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()

        assertEquals(1, recyclerView.shadow().requestLayoutCalls)
    }

    @Test
    fun `do not request remeasure when gallery cross axis is fixed`() {
        val recyclerView = createRecyclerView(width = 100, height = 50)
        val item = createItem(recyclerView, RecyclerView.HORIZONTAL, width = 50, height = 100)
        recyclerView.shadow().requestLayoutCalls = 0

        item.measure(unspecifiedSpec(), unspecifiedSpec())
        drainMainLoop()

        assertEquals(0, recyclerView.shadow().requestLayoutCalls)
    }

    private fun createRecyclerView(): DivRecyclerView {
        return DivRecyclerView(context)
    }

    private fun createRecyclerView(width: Int, height: Int): DivRecyclerView {
        return createRecyclerView().apply {
            layoutManager = LinearLayoutManager(context)
            measure(exactSpec(width), exactSpec(height))
            layout(0, 0, width, height)
        }
    }

    private fun DivRecyclerView.setCrossAxisMeasureSpec(spec: Int, orientation: Int) {
        parentCrossAxisMeasureSpec = spec
        if (orientation == RecyclerView.HORIZONTAL) {
            heightMeasureSpec = spec
        } else {
            widthMeasureSpec = spec
        }
    }

    private fun createItem(
        recyclerView: DivRecyclerView,
        orientation: Int,
        width: Int,
        height: Int,
    ): DivGalleryItemLayout {
        return DivGalleryItemLayout(context).apply {
            this.orientation = { orientation }
            layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            recyclerView.addView(this)
            addView(View(context), DivLayoutParams(width, height))
        }
    }

    private fun exactSpec(size: Int) = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)

    private fun atMostSpec(size: Int) = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.AT_MOST)

    private fun unspecifiedSpec() = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

    private fun drainMainLoop() {
        shadowOf(getMainLooper()).idle()
    }

    private fun DivRecyclerView.shadow(): ShadowDivRecyclerView = Shadow.extract(this)

    private fun View.measureAndLayout(widthSpec: Int, heightSpec: Int) {
        measure(widthSpec, heightSpec)
        layout(0, 0, measuredWidth, measuredHeight)
    }

    private class TestAdapter(
        private val context: Context,
        private val itemWidth: Int,
        private val itemHeights: IntArray,
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemCount() = itemHeights.size

        override fun getItemViewType(position: Int) = position

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val item = DivGalleryItemLayout(context).apply {
                orientation = { RecyclerView.HORIZONTAL }
                layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                addView(View(context), DivLayoutParams(itemWidth, itemHeights[viewType]))
            }
            return object : RecyclerView.ViewHolder(item) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    }

    @Suppress("unused")
    @Implements(DivRecyclerView::class)
    class ShadowDivRecyclerView : ShadowViewGroup() {
        var requestLayoutCalls = 0

        @Implementation
        override fun requestLayout() {
            requestLayoutCalls++
            super.requestLayout()
        }

        @Implementation
        override fun post(action: Runnable): Boolean = Handler(getMainLooper()).post(action)
    }
}
