package com.yandex.div.core.view2.divs.gallery

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div2.DivGallery
import com.yandex.div2.DivSize

internal class DivGridLayoutManager(
    override val divView: Div2View,
    override val view: RecyclerView,
    override val div: DivGallery,
    @RecyclerView.Orientation orientation: Int = RecyclerView.HORIZONTAL
) : StaggeredGridLayoutManager (
        div.columnCount?.evaluate(divView.expressionResolver)?.toIntSafely() ?: 1,
    orientation
), DivGalleryItemHelper {

    override val childrenToRelayout = HashSet<View>()

    override val divItems
        get() = (view.adapter as? DivGalleryBinder.GalleryAdapter)?.items ?: div.nonNullItems

    private val midPadding
        get() = div.itemSpacing.evaluate(divView.expressionResolver).dpToPx(view.resources.displayMetrics)

    override fun toLayoutManager() = this

    override fun getPaddingStart() = super.getPaddingStart() - midPadding / 2
    override fun getPaddingEnd() = super.getPaddingEnd() - midPadding / 2
    override fun getPaddingLeft() = super.getPaddingLeft() - midPadding / 2
    override fun getPaddingTop() = super.getPaddingTop() - midPadding / 2
    override fun getPaddingRight() = super.getPaddingRight() - midPadding / 2
    override fun getPaddingBottom() = super.getPaddingBottom() - midPadding / 2

    override fun layoutDecorated(child: View, left: Int, top: Int, right: Int, bottom: Int) {
        super.layoutDecorated(child, left, top, right, bottom)
        _layoutDecorated(child, left, top, right, bottom)
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        _onLayoutCompleted(state)
        super.onLayoutCompleted(state)
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
        val indexes = IntArray(itemCount)
        findFirstCompletelyVisibleItemPositions(indexes)
        return indexes.first()
    }

    override fun firstVisibleItemPosition(): Int {
        val indexes = IntArray(itemCount)
        findFirstVisibleItemPositions(indexes)
        return indexes.first()
    }

    override fun lastVisibleItemPosition(): Int {
        val indexes = IntArray(itemCount)
        findLastVisibleItemPositions(indexes)
        return indexes.last()
    }

    override fun _getChildAt(index: Int): View? = getChildAt(index)

    override fun _getPosition(child: View): Int = getPosition(child)

    override fun getDecoratedMeasuredWidth(child: View): Int {
        val position = _getPosition(child)
        val item = div.nonNullItems[position].value()

        val isFixedWidth = item.width is DivSize.Fixed
        val isMultiSpan = spanCount > 1

        return super.getDecoratedMeasuredWidth(child) + if (isFixedWidth && isMultiSpan) midPadding else 0
    }

    override fun getDecoratedMeasuredHeight(child: View): Int {
        val position = _getPosition(child)
        val item = div.nonNullItems[position].value()

        val isFixedHeight = item.height is DivSize.Fixed
        val isMultiSpan = spanCount > 1

        return super.getDecoratedMeasuredHeight(child) + if (isFixedHeight && isMultiSpan) midPadding else 0
    }

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
