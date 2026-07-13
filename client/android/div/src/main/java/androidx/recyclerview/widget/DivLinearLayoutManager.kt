package androidx.recyclerview.widget

import android.view.View
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.divs.gallery.DivGalleryItemHelper
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div2.DivGallery.ContentAlignment

internal class DivLinearLayoutManager(
    override val bindingContext: BindingContext,
    override val view: DivRecyclerView,
    @RecyclerView.Orientation orientation: Int,
    override val crossContentAlignment: ContentAlignment,
) : LinearLayoutManager(view.context, orientation, false), DivGalleryItemHelper {

    private var anchor = 0

    override val childrenToRelayout = HashSet<View>()

    override fun toLayoutManager() = this

    override fun layoutDecorated(child: View, left: Int, top: Int, right: Int, bottom: Int) {
        super.layoutDecorated(child, left, top, right, bottom)
        _layoutDecorated(child, left, top, right, bottom)
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        _onLayoutCompleted(state)
        super.onLayoutCompleted(state)
        anchor = view.snapHelper.getAnchor()
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

    override fun findFirstCompletelyVisibleItemPosition() = findCompletelyVisibleItemPosition(0, itemCount - 1)

    override fun lastCompletelyVisibleItemPosition(): Int = findLastCompletelyVisibleItemPosition()

    override fun findLastCompletelyVisibleItemPosition() = findCompletelyVisibleItemPosition(itemCount - 1, 0)

    private fun findCompletelyVisibleItemPosition(fromIndex: Int, toIndex: Int): Int {
        val parentSize = size()
        val range = if (fromIndex <= toIndex) fromIndex .. toIndex else fromIndex downTo toIndex
        for (i in range) {
            val child = getChildAt(i) ?: continue
            val childStart = if (isHorizontal) child.left else child.top
            val childEnd = if (isHorizontal) child.right else child.bottom
            if (childStart >= 0 && childEnd <= parentSize) return getPosition(child)
        }
        return RecyclerView.NO_POSITION
    }

    override fun firstVisibleItemPosition(): Int = findFirstVisibleItemPosition()

    override fun lastVisibleItemPosition(): Int = findLastVisibleItemPosition()

    /**
     * @param direction physical scroll direction
     */
    override fun getNearestItemPosition(direction: Int): Int {
        val reversed = isHorizontal && view.isLayoutRtl()
        val range = if ((direction >= 0) != reversed) 0 .. itemCount else itemCount downTo 0
        var lastFoundPosition = RecyclerView.NO_POSITION
        for (i in range) {
            val child = view.findViewHolderForAdapterPosition(i)?.itemView ?: continue
            lastFoundPosition = i
            val offset = getIndexOffsetIfIsNearest(child.getAnchor(), direction, reversed) ?: continue
            return i + offset
        }
        return if (reversed) lastFoundPosition - 1 else lastFoundPosition + 1
    }

    private fun View.getAnchor(): Int {
        return when (view.snapHelper.alignment) {
            ContentAlignment.START -> {
                when {
                    !isHorizontal -> top
                    view.isLayoutRtl() -> right
                    else -> left
                }
            }

            ContentAlignment.CENTER -> if (isHorizontal) (left + right) / 2 else (top + bottom) / 2

            ContentAlignment.END -> {
                when {
                    !isHorizontal -> bottom
                    view.isLayoutRtl() -> left
                    else -> right
                }
            }
        }
    }

    private fun getIndexOffsetIfIsNearest(childAnchor: Int, direction: Int, reversed: Boolean): Int? {
        return when {
            childAnchor > anchor -> if (direction >= 0) 0 else null
            childAnchor < anchor -> if (direction < 0) 0 else null
            direction >= 0 -> if (reversed) -1 else 1
            reversed -> 1
            else -> -1
        }
    }

    override fun _getChildAt(index: Int): View? = getChildAt(index)

    override fun _getPosition(child: View): Int = getPosition(child)

    override fun getLayoutManagerOrientation(): Int = orientation

    override fun instantScrollToPosition(position: Int, offset: Int) = instantScroll(position, offset)
}
