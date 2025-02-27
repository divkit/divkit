package com.yandex.div.core.view2.divs.gallery

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

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
            val velocity = if (getLayoutManagerOrientation() == LinearLayoutManager.HORIZONTAL) {
                if (manager.layoutDirection == View.LAYOUT_DIRECTION_LTR) velocityX else -velocityX
            } else {
                velocityY
            }

            val firstCompletelyVisibleItemPosition = if (velocity < 0) {
                firstCompletelyVisibleItemPosition()
            } else {
                lastCompletelyVisibleItemPosition()
            }
            if (firstCompletelyVisibleItemPosition != RecyclerView.NO_POSITION) {
                return firstCompletelyVisibleItemPosition
            }

            val firstVisibleItemPosition = if (velocity < 0) {
                firstVisibleItemPosition()
            } else {
                lastVisibleItemPosition()
            }

            val lastVisibleItemPosition = if (velocity < 0) {
                lastVisibleItemPosition()
            } else {
                firstVisibleItemPosition()
            }

            // workaround for first/last position
            if (lastVisibleItemPosition == firstVisibleItemPosition) {
                return if (lastVisibleItemPosition != RecyclerView.NO_POSITION) lastVisibleItemPosition else 0
            }

            // have 2 items on screen and choose by direction
            return if (velocity >= 0) {
                lastVisibleItemPosition
            } else {
                lastVisibleItemPosition - 1
            }
        }
    }

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray {
        val array = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            array[0] = distanceToCenter(layoutManager, targetView, getHorizontalHelper(layoutManager))
        } else if (layoutManager.canScrollVertically()) {
            array[1] = distanceToCenter(layoutManager, targetView, getVerticalHelper(layoutManager))
        }
        return array
    }

    private fun distanceToCenter(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View,
        helper: OrientationHelper
    ): Int {
        val childCenter = if (layoutManager.canScrollHorizontally()) {
            (targetView.x + targetView.width / 2).toInt()
        } else {
            (targetView.y + targetView.height / 2).toInt()
        }
        val containerCenter = if (layoutManager.clipToPadding) {
            helper.startAfterPadding + helper.totalSpace / 2
        } else {
            helper.end / 2
        }
        return childCenter - containerCenter
    }
}
