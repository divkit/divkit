package com.yandex.div.core.view2.divs

import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.divs.gallery.DivGalleryItemHelper

internal class PagerSnapStartHelper(var itemSpacing: Int) : PagerSnapHelper() {
    private var _verticalHelper: OrientationHelper? = null
    private var _horizontalHelper: OrientationHelper? = null

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        return _verticalHelper?.takeUnless { it.layoutManager != layoutManager }
            ?: OrientationHelper.createVerticalHelper(layoutManager).also { _verticalHelper = it }
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        return _horizontalHelper?.takeUnless { it.layoutManager != layoutManager }
            ?: OrientationHelper.createHorizontalHelper(layoutManager).also { _horizontalHelper = it }
    }

    override fun findTargetSnapPosition(manager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
        (manager as DivGalleryItemHelper).run {
            val firstCompletelyVisibleItemPosition = firstCompletelyVisibleItemPosition()
            if (firstCompletelyVisibleItemPosition != RecyclerView.NO_POSITION) {
                return firstCompletelyVisibleItemPosition
            }
            val lastVisibleItemPosition = lastVisibleItemPosition()
            // workaround for first/last position
            if (lastVisibleItemPosition == firstVisibleItemPosition()) {
                return if (lastVisibleItemPosition != RecyclerView.NO_POSITION) lastVisibleItemPosition else 0
            }
            val velocity =
                if (getLayoutManagerOrientation() == LinearLayoutManager.HORIZONTAL) velocityX else velocityY
            val isRtl = manager.layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL
            // have 2 items on screen and choose by direction
            return if ((velocity >= 0 && !isRtl) || (isRtl && velocity < 0)) {
                lastVisibleItemPosition
            } else {
                lastVisibleItemPosition - 1
            }
        }
    }

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray {
        val array = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            array[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager))
        } else if (layoutManager.canScrollVertically()) {
            array[1] = distanceToStart(targetView, getVerticalHelper(layoutManager))
        }
        return array
    }

    private fun distanceToStart(view: View, helper: OrientationHelper): Int {
        helper.run {
            return if (view.isLayoutRtl()) {
                getDecoratedEnd(view) - if (layoutManager.getPosition(view) == 0) endAfterPadding else (layoutManager.width + itemSpacing / 2)
            } else {
                getDecoratedStart(view) - if (layoutManager.getPosition(view) == 0) startAfterPadding else itemSpacing / 2
            }
        }
    }
}
