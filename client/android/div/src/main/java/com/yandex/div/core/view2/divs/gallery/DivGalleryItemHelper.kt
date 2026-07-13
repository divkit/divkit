package com.yandex.div.core.view2.divs.gallery

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.doOnNextLayout
import androidx.core.view.forEach
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.R
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivGallery

@Suppress("FunctionName")
internal interface DivGalleryItemHelper {
    val bindingContext: BindingContext
    val view: DivRecyclerView
    val crossContentAlignment: DivGallery.ContentAlignment

    val childrenToRelayout: MutableSet<View>

    val isHorizontal get() = getLayoutManagerOrientation() == RecyclerView.HORIZONTAL

    fun getItemDiv(position: Int): DivItemBuilderResult? =
        (view.adapter as? DivGalleryAdapter)?.visibleItems?.getOrNull(position)

    fun toLayoutManager(): RecyclerView.LayoutManager

    fun _layoutDecorated(child: View, left: Int, top: Int, right: Int, bottom: Int) {
        trackVisibilityAction(child)
    }

    fun _onLayoutCompleted(state: RecyclerView.State?) {
        for (child in childrenToRelayout) {
            _layoutDecoratedWithMargins(child, child.left, child.top, child.right, child.bottom,
                isRelayoutingChildren = true)
        }
        childrenToRelayout.clear()
    }

    fun _layoutDecoratedWithMargins(
        child: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        isRelayoutingChildren: Boolean = false
    ) {
        val orientation = getLayoutManagerOrientation()
        val actualContentWidth = view.measuredWidth - view.paddingLeft - view.paddingRight
        val actualContentHeight = view.measuredHeight - view.paddingTop - view.paddingBottom
        val shouldRelayout = when (orientation) {
            RecyclerView.VERTICAL -> view.measuredWidth == 0 || actualContentWidth == 0 || child.measuredWidth == 0
            RecyclerView.HORIZONTAL -> view.measuredHeight == 0 || actualContentHeight == 0 || child.measuredHeight == 0
            else -> false
        }

        if (shouldRelayout) {
            superLayoutDecoratedWithMargins(child, left, top, right, bottom)
            if (!isRelayoutingChildren) {
                childrenToRelayout.add(child)
            }
            return
        }

        val childItem = (child.getTag(R.id.div_gallery_item_index) as Int?)?.let {
            getItemDiv(it)
        } ?: return finishLayoutDecoratedWithMargins(child, left, top, right, bottom, isRelayoutingChildren)

        val childDiv = childItem.div.value()
        val resolver = childItem.expressionResolver
        val horizontalOffset = if (!isHorizontal) {
            val alignment =
                childDiv.alignmentHorizontal?.evaluate(resolver)?.asCrossContentAlignment() ?: crossContentAlignment
            calculateOffset(
                actualContentWidth,
                right - left,
                alignment
            )
        } else {
            0
        }

        val verticalOffset = if (isHorizontal) {
            val alignment =
                childDiv.alignmentVertical?.evaluate(resolver)?.asCrossContentAlignment() ?: crossContentAlignment
            calculateOffset(
                actualContentHeight,
                bottom - top,
                alignment
            )
        } else {
            0
        }

        finishLayoutDecoratedWithMargins(
            child,
            left + horizontalOffset,
            top + verticalOffset,
            right + horizontalOffset,
            bottom + verticalOffset,
            isRelayoutingChildren
        )
    }

    private fun finishLayoutDecoratedWithMargins(
        child: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        isRelayoutingChildren: Boolean
    ) {
        superLayoutDecoratedWithMargins(child, left, top, right, bottom)
        trackVisibilityAction(child)
        if (!isRelayoutingChildren) {
            childrenToRelayout.remove(child)
        }
    }

    fun firstCompletelyVisibleItemPosition(): Int
    fun lastCompletelyVisibleItemPosition(): Int
    fun firstVisibleItemPosition(): Int
    fun lastVisibleItemPosition(): Int
    fun getNearestItemPosition(direction: Int): Int
    fun size(): Int = toLayoutManager().run { if (isHorizontal) width else height }
    fun instantScrollToPosition(position: Int, offset: Int)
    fun getLayoutManagerOrientation(): Int

    fun instantScroll(position: Int, offset: Int = 0) {
        if (trySnapToPosition(position, offset)) return

        view.doOnNextLayout {
            trySnapToPosition(position, offset)
        }
        view.scrollToPosition(position)
    }

    fun trySnapToPosition(position: Int, offset: Int): Boolean {
        val layoutManager = toLayoutManager()
        val target = layoutManager.findViewByPosition(position) ?: return false

        trySnapToPosition(target, offset)
        return true
    }

    fun trySnapToPosition(target: View, offset: Int) {
        if (offset == 0) {
            val distance = view.snapHelper.calculateDistanceToFinalSnap(toLayoutManager(), target)
            view.scrollBy(distance[0], distance[1])
            return
        }

        val distance = if (isHorizontal && view.isLayoutRtl()) {
            offset - target.scrollOffset
        } else {
            target.scrollOffset - offset
        }
        view.scrollBy(distance, distance)
    }

    private val View.scrollOffset: Int get() {
        return if (!isHorizontal) {
            top - marginTop - view.paddingTop
        } else {
            val viewStart = if (isLayoutRtl()) view.width - right else left
            viewStart - marginStart - view.paddingStart
        }
    }

    fun calcScrollOffset(targetView: View) = targetView.scrollOffset

    fun superLayoutDecoratedWithMargins(child: View, left: Int, top: Int, right: Int, bottom: Int)

    fun _removeAndRecycleAllViews(recycler: RecyclerView.Recycler) {
        view.forEach { child ->
            trackVisibilityAction(child, clear = true)
        }
    }

    fun _onAttachedToWindow(view: RecyclerView) {
        view.forEach { child ->
            trackVisibilityAction(child)
        }
    }

    fun _onDetachedFromWindow(view: RecyclerView, recycler: RecyclerView.Recycler) {
        view.forEach { child ->
            trackVisibilityAction(child, clear = true)
        }
    }

    fun _detachView(child: View) {
        trackVisibilityAction(child, clear = true)
    }

    fun _detachViewAt(index: Int) {
        val child = _getChildAt(index) ?: return
        trackVisibilityAction(child, clear = true)
    }

    fun _removeView(child: View) {
        trackVisibilityAction(child, clear = true)
    }

    fun _removeViewAt(index: Int) {
        val child = _getChildAt(index) ?: return
        trackVisibilityAction(child, clear = true)
    }

    fun _getChildAt(index: Int): View?

    fun _getPosition(child: View): Int

    fun trackVisibilityAction(child: View, clear: Boolean = false) {
        val position = _getPosition(child)
        if (position == RecyclerView.NO_POSITION) return

        val container = (child as? ViewGroup) ?: return
        val itemView = container.children.firstOrNull() ?: return

        val divView = bindingContext.divView
        if (clear) {
            val div = divView.takeBindingDiv(itemView) ?: return
            val itemContext = (itemView as? DivHolderView<*>)?.bindingContext ?: return
            divView.div2Component.visibilityActionTracker.cancelTrackingViewsHierarchy(itemContext, itemView, div)
            divView.unbindViewFromDiv(itemView)
        } else {
            val item = getItemDiv(position) ?: return
            divView.div2Component.visibilityActionTracker
                .startTrackingViewsHierarchy(bindingContext.getFor(item.expressionResolver), itemView, item.div)
            divView.bindViewToDiv(itemView, item.div)
        }
    }

    companion object {
        private fun calculateOffset(
            totalSpace: Int,
            decoratedMeasurement: Int,
            crossContentAlignment: DivGallery.ContentAlignment
        ): Int {
            val availableSpace = totalSpace - decoratedMeasurement

            return when (crossContentAlignment) {
                DivGallery.ContentAlignment.START -> 0
                DivGallery.ContentAlignment.CENTER -> availableSpace / 2
                DivGallery.ContentAlignment.END -> availableSpace
            }
        }

        private fun DivAlignmentHorizontal.asCrossContentAlignment(): DivGallery.ContentAlignment {
            return when (this) {
                DivAlignmentHorizontal.LEFT -> DivGallery.ContentAlignment.START
                DivAlignmentHorizontal.CENTER -> DivGallery.ContentAlignment.CENTER
                DivAlignmentHorizontal.RIGHT -> DivGallery.ContentAlignment.END
                DivAlignmentHorizontal.START -> DivGallery.ContentAlignment.START
                DivAlignmentHorizontal.END -> DivGallery.ContentAlignment.END
            }
        }

        private fun DivAlignmentVertical.asCrossContentAlignment(): DivGallery.ContentAlignment {
            return when (this) {
                DivAlignmentVertical.TOP, DivAlignmentVertical.BASELINE -> DivGallery.ContentAlignment.START
                DivAlignmentVertical.CENTER -> DivGallery.ContentAlignment.CENTER
                DivAlignmentVertical.BOTTOM -> DivGallery.ContentAlignment.END
            }
        }
    }
}
