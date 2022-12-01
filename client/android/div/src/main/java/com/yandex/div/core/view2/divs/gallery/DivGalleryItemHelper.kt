package com.yandex.div.core.view2.divs.gallery

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.forEach
import androidx.core.view.marginStart
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.R
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivGallery

internal interface DivGalleryItemHelper {
    val divView: Div2View
    val view: RecyclerView
    val div: DivGallery
    val divItems: List<Div>

    val childrenToRelayout: MutableSet<View>

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
        val parentHeight = view.measuredHeight
        val div = try {
            divItems[child.getTag(R.id.div_gallery_item_index) as Int]
        } catch (e: Exception) {
            null
        }
        val newTop = when (getVerticalAlignment(div)) {
            DivAlignmentVertical.CENTER -> (parentHeight - child.measuredHeight) / 2
            DivAlignmentVertical.BOTTOM -> parentHeight - child.measuredHeight
            else -> 0
        }
        if (newTop < 0) {
            superLayoutDecoratedWithMargins(child, left, top, right, bottom)
            if (!isRelayoutingChildren) {
                childrenToRelayout.add(child)
            }
        } else {
            superLayoutDecoratedWithMargins(child, left, top + newTop, right, bottom + newTop)
            trackVisibilityAction(child)
            if (!isRelayoutingChildren) {
                childrenToRelayout.remove(child)
            }
        }
    }

    fun firstVisibleItemPosition(): Int
    fun lastVisibleItemPosition(): Int
    fun width(): Int
    fun instantScrollToPosition(position: Int)
    fun instantScrollToPositionWithOffset(position: Int, offset: Int)
    fun getLayoutManagerOrientation(): Int

    fun instantScroll(position: Int, offset: Int = 0) {
        view.doOnLayout {
            if (position == 0) {
                view.scrollBy(-offset, -offset)
                return@doOnLayout
            }

            view.scrollBy(-view.scrollX, -view.scrollY)

            var targetView: View? = view.layoutManager?.findViewByPosition(position)

            val orientationHelper = OrientationHelper
                .createOrientationHelper(view.layoutManager, getLayoutManagerOrientation())

            while (
                (targetView == null) &&
                (view.canScrollVertically(1) || view.canScrollHorizontally(1))
            ) {
                view.layoutManager?.requestLayout()
                targetView = view.layoutManager?.findViewByPosition(position)
                if (targetView != null) break
                view.scrollBy(view.width, view.height)
            }

            if (targetView == null) return@doOnLayout

            val startGap = orientationHelper.getDecoratedStart(targetView) -
                    orientationHelper.startAfterPadding -
                    offset +
                    targetView.marginStart

            view.scrollBy(startGap, startGap)
        }
    }

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
        val div = divItems[position]

        if (clear) {
            divView.div2Component.visibilityActionTracker.trackVisibilityActionsOf(divView, null, div)
            divView.unbindViewFromDiv(itemView)
        } else {
            divView.div2Component.visibilityActionTracker.trackVisibilityActionsOf(divView, itemView, div)
            divView.bindViewToDiv(itemView, div)
        }
    }

    fun getVerticalAlignment(child: Div?): DivAlignmentVertical {
        val resolver = divView.expressionResolver
        child?.value()?.alignmentVertical?.let {
            return it.evaluate(resolver)
        }
        return when (div.crossContentAlignment.evaluate(resolver)) {
            DivGallery.CrossContentAlignment.CENTER -> DivAlignmentVertical.CENTER
            DivGallery.CrossContentAlignment.END -> DivAlignmentVertical.BOTTOM
            else -> DivAlignmentVertical.TOP
        }
    }
}
