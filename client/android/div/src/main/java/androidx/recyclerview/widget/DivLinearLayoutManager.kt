package androidx.recyclerview.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.gallery.DivGalleryBinder
import com.yandex.div.core.view2.divs.gallery.DivGalleryItemHelper
import com.yandex.div.core.view2.divs.gallery.ScrollPosition
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div2.DivGallery

internal class DivLinearLayoutManager(
    override val divView: Div2View,
    override val view: RecyclerView,
    override val div: DivGallery,
    @RecyclerView.Orientation orientation: Int = RecyclerView.HORIZONTAL
) : LinearLayoutManager(view.context, orientation, false), DivGalleryItemHelper {

    override val childrenToRelayout = HashSet<View>()

    override val divItems
        get() = (view.adapter as? DivGalleryBinder.GalleryAdapter)?.items ?: div.items

    override fun measureChild(child: View, widthUsed: Int, heightUsed: Int) {
        val lp = child.layoutParams as DivRecyclerViewLayoutParams
        val insets = view.getItemDecorInsetsForChild(child)
        val widthSpec = getChildMeasureSpec(
            width,
            widthMode,
            paddingLeft + paddingRight + widthUsed + insets.left + insets.right,
            lp.width,
            lp.maxWidth,
            canScrollHorizontally()
        )
        val heightSpec = getChildMeasureSpec(
            height,
            heightMode,
            paddingTop + paddingBottom + heightUsed + insets.top + insets.bottom,
            lp.height,
            lp.maxHeight,
            canScrollVertically()
        )
        if (shouldMeasureChild(child, widthSpec, heightSpec, lp)) {
            child.measure(widthSpec, heightSpec)
        }
    }

    override fun measureChildWithMargins(child: View, widthUsed: Int, heightUsed: Int) {
        val lp = child.layoutParams as DivRecyclerViewLayoutParams
        val insets = view.getItemDecorInsetsForChild(child)
        val widthSpec = getChildMeasureSpec(
            width,
            widthMode,
            paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin + widthUsed + insets.left + insets.right,
            lp.width,
            lp.maxWidth,
            canScrollHorizontally()
        )
        val heightSpec = getChildMeasureSpec(
            height,
            heightMode,
            paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin + heightUsed + insets.top + insets.bottom,
            lp.height,
            lp.maxHeight,
            canScrollVertically()
        )
        if (shouldMeasureChild(child, widthSpec, heightSpec, lp)) {
            child.measure(widthSpec, heightSpec)
        }
    }

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

    override fun firstCompletelyVisibleItemPosition(): Int = findFirstCompletelyVisibleItemPosition()

    override fun firstVisibleItemPosition(): Int = findFirstVisibleItemPosition()

    override fun lastVisibleItemPosition(): Int = findLastVisibleItemPosition()

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

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?) = lp is DivRecyclerViewLayoutParams

    override fun generateLayoutParams(c: Context?, attrs: AttributeSet?): RecyclerView.LayoutParams =
        DivRecyclerViewLayoutParams(c, attrs)

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): RecyclerView.LayoutParams = when (lp) {
        is DivRecyclerViewLayoutParams -> DivRecyclerViewLayoutParams(lp)
        is RecyclerView.LayoutParams -> DivRecyclerViewLayoutParams(lp)
        is DivLayoutParams -> DivRecyclerViewLayoutParams(lp)
        is MarginLayoutParams -> DivRecyclerViewLayoutParams(lp)
        else -> DivRecyclerViewLayoutParams(lp)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        DivRecyclerViewLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    class DivRecyclerViewLayoutParams: RecyclerView.LayoutParams {

        var maxHeight = Integer.MAX_VALUE
        var maxWidth = Integer.MAX_VALUE

        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs)

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: MarginLayoutParams?) : super(source)

        constructor(source: ViewGroup.LayoutParams?) : super(source)

        constructor(source: RecyclerView.LayoutParams?) : super(source)

        constructor(source: DivRecyclerViewLayoutParams) : super(source) {
            maxHeight = source.maxHeight
            maxWidth = source.maxWidth
        }

        constructor(source: DivLayoutParams) : super(source) {
            maxHeight = source.maxHeight
            maxWidth = source.maxWidth
        }
    }
}
