package com.yandex.div.core.view2.divs.gallery

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div2.DivGallery
import com.yandex.div2.DivSize

internal class DivGridLayoutManager(
    override val bindingContext: BindingContext,
    override val view: RecyclerView,
    override val div: DivGallery,
    @RecyclerView.Orientation orientation: Int = RecyclerView.HORIZONTAL
) : StaggeredGridLayoutManager (
        div.columnCount?.evaluate(bindingContext.expressionResolver)?.toIntSafely() ?: 1,
    orientation
), DivGalleryItemHelper {

    override val childrenToRelayout = HashSet<View>()

    override fun getItemDiv(position: Int) = (view.adapter as DivGalleryAdapter).visibleItems.getOrNull(position)

    private val itemSpacing
        get() = div.itemSpacing.evaluate(bindingContext.expressionResolver).dpToPx(view.resources.displayMetrics)

    val crossSpacing
        get() = div.crossSpacing?.evaluate(bindingContext.expressionResolver)?.dpToPx(view.resources.displayMetrics)
            ?: itemSpacing

    private fun spacingByOrientation(alongOrientation: Int) =
        if (alongOrientation == orientation) itemSpacing else crossSpacing

    override fun toLayoutManager() = this

    override fun getPaddingStart() = super.getPaddingStart() - itemSpacing / 2
    override fun getPaddingEnd() = super.getPaddingEnd() - itemSpacing / 2
    override fun getPaddingLeft() = super.getPaddingLeft() - spacingByOrientation(HORIZONTAL) / 2
    override fun getPaddingTop() = super.getPaddingTop() - spacingByOrientation(VERTICAL) / 2
    override fun getPaddingRight() = super.getPaddingRight() - spacingByOrientation(HORIZONTAL) / 2
    override fun getPaddingBottom() = super.getPaddingBottom() - spacingByOrientation(VERTICAL) / 2

    override fun layoutDecorated(child: View, left: Int, top: Int, right: Int, bottom: Int) {
        super.layoutDecorated(child, left, top, right, bottom)
        _layoutDecorated(child, left, top, right, bottom)
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        _onLayoutCompleted(state)
        super.onLayoutCompleted(state)
    }

    override fun calculateItemDecorationsForChild(child: View, outRect: Rect) {
        super.calculateItemDecorationsForChild(child, outRect)

        val position = _getPosition(child)
        if (position == RecyclerView.NO_POSITION) return
        val item = getItemDiv(position) ?: return

        val div = item.div.value()

        val isFixedHeight = div.height is DivSize.Fixed
        val isFixedWidth = div.width is DivSize.Fixed

        val isMultiSpan = spanCount > 1

        val verticalOffset = if (isFixedHeight && isMultiSpan) spacingByOrientation(VERTICAL) / 2 else 0
        val horizontalOffset = if (isFixedWidth && isMultiSpan) spacingByOrientation(HORIZONTAL) / 2 else 0

        outRect.apply {
            set(left - horizontalOffset, top - verticalOffset, right - horizontalOffset, bottom - verticalOffset)
        }
    }

    override fun layoutDecoratedWithMargins(child: View, left: Int, top: Int, right: Int, bottom: Int) {
        _layoutDecoratedWithMargins(child, left, top, right, bottom)
    }

    override fun removeAndRecycleAllViews(recycler: RecyclerView.Recycler) {
        _removeAndRecycleAllViews(recycler)
        super.removeAndRecycleAllViews(recycler)
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        _onAttachedToWindow(view)
    }

    override fun onDetachedFromWindow(view: RecyclerView, recycler: RecyclerView.Recycler) {
        super.onDetachedFromWindow(view, recycler)
        _onDetachedFromWindow(view, recycler)
    }

    override fun detachView(child: View) {
        super.detachView(child)
        _detachView(child)
    }

    override fun detachViewAt(index: Int) {
        super.detachViewAt(index)
        _detachViewAt(index)
    }

    override fun removeView(child: View) {
        super.removeView(child)
        _removeView(child)
    }

    override fun removeViewAt(index: Int) {
        super.removeViewAt(index)
        _removeViewAt(index)
    }

    override fun superLayoutDecoratedWithMargins(
        child: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.layoutDecoratedWithMargins(child, left, top, right, bottom)
    }

    override fun firstCompletelyVisibleItemPosition(): Int {
        val indexes = IntArray(itemCount.coerceAtLeast(spanCount))
        findFirstCompletelyVisibleItemPositions(indexes)
        return indexes.first()
    }

    override fun lastCompletelyVisibleItemPosition(): Int {
        val indexes = IntArray(itemCount.coerceAtLeast(spanCount))
        findLastCompletelyVisibleItemPositions(indexes)
        return indexes.last()
    }

    override fun firstVisibleItemPosition(): Int {
        val indexes = IntArray(itemCount.coerceAtLeast(spanCount))
        findFirstVisibleItemPositions(indexes)
        return indexes.first()
    }

    override fun lastVisibleItemPosition(): Int {
        val indexes = IntArray(itemCount.coerceAtLeast(spanCount))
        findLastVisibleItemPositions(indexes)
        return indexes.last()
    }

    override fun _getChildAt(index: Int): View? = getChildAt(index)

    override fun _getPosition(child: View): Int = getPosition(child)

    override fun width(): Int = width

    override fun getLayoutManagerOrientation(): Int = orientation

    override fun instantScrollToPosition(
        position: Int,
        scrollPosition: ScrollPosition
    ) {
        instantScroll(position, scrollPosition)
    }

    override fun instantScrollToPositionWithOffset(
        position: Int,
        offset: Int,
        scrollPosition: ScrollPosition
    ) {
        instantScroll(position, scrollPosition, offset)
    }
}
