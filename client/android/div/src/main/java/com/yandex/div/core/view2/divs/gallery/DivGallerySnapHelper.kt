package com.yandex.div.core.view2.divs.gallery

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.divs.utils.isForwardScroll
import com.yandex.div.internal.scroll.SnapPositions
import com.yandex.div.internal.scroll.findDirectionalSnapPosition
import com.yandex.div2.DivGallery.ContentAlignment

internal class DivGallerySnapHelper(private val view: RecyclerView) : PagerSnapHelper() {

    var itemSpacing: Int = 0
    var alignment: ContentAlignment = ContentAlignment.CENTER

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

    private fun RecyclerView.LayoutManager.getOrientationHelper(): OrientationHelper =
        if (canScrollHorizontally()) getHorizontalHelper(this) else getVerticalHelper(this)

    override fun findTargetSnapPosition(manager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
        val helper = manager as DivGalleryItemHelper
        val isVertical = helper.getLayoutManagerOrientation() == RecyclerView.VERTICAL
        return findDirectionalSnapPosition(
            forward = isForwardScroll(isVertical, velocityX, velocityY, view.isLayoutRtl()),
            positions = helper.snapPositions(manager),
        )
    }

    private fun DivGalleryItemHelper.snapPositions(manager: RecyclerView.LayoutManager): SnapPositions {
        val helper = this
        return object : SnapPositions {
            override val itemCount get() = manager.itemCount
            override val isLinearLayout get() = manager is LinearLayoutManager
            override val firstCompletelyVisible get() = helper.firstCompletelyVisibleItemPosition()
            override val lastCompletelyVisible get() = helper.lastCompletelyVisibleItemPosition()
            override val firstVisible get() = helper.firstVisibleItemPosition()
            override val lastVisible get() = helper.lastVisibleItemPosition()
        }
    }

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray {
        val array = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            array[0] = distanceToItem(layoutManager, targetView, getHorizontalHelper(layoutManager))
        } else if (layoutManager.canScrollVertically()) {
            array[1] = distanceToItem(layoutManager, targetView, getVerticalHelper(layoutManager))
        }
        return array
    }

    fun distanceToItem(targetView: View): Int {
        val layoutManager = view.layoutManager ?: return 0
        val helper = layoutManager.getOrientationHelper()
        return -distanceToItem(layoutManager, targetView, helper)
    }

    fun getAnchor(): Int {
        val layoutManager = view.layoutManager ?: return 0
        val isHorizontal = layoutManager.canScrollHorizontally()
        val helper = layoutManager.getOrientationHelper()

        if (alignment == ContentAlignment.CENTER) return helper.end / 2

        return helper.getSideAnchor(
            isStart = alignment == ContentAlignment.START,
            reverse = isHorizontal && view.isLayoutRtl(),
            offset = layoutManager.gridGalleryOffset,
        )
    }

    private fun OrientationHelper.getSideAnchor(isStart: Boolean, reverse: Boolean, offset: Int) =
        if (isStart != reverse) startAfterPadding + offset else endAfterPadding - offset

    private fun distanceToItem(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View,
        helper: OrientationHelper
    ): Int {
        return when (alignment) {
            ContentAlignment.START -> distanceToStart(layoutManager, targetView, helper)
            ContentAlignment.CENTER -> distanceToCenter(layoutManager, targetView, helper)
            ContentAlignment.END -> distanceToEnd(layoutManager, targetView, helper)
        }
    }

    private fun distanceToStart(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View,
        helper: OrientationHelper
    ): Int {
        val offset = layoutManager.gridGalleryOffset
        return when {
            layoutManager.canScrollVertically() -> targetView.y.toInt() - helper.startAfterPadding - offset
            targetView.isLayoutRtl() -> targetView.x.toInt() + targetView.width - helper.endAfterPadding + offset
            else -> targetView.x.toInt() - helper.startAfterPadding - offset
        }
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

    private fun distanceToEnd(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View,
        helper: OrientationHelper
    ): Int {
        val offset = layoutManager.gridGalleryOffset
        return when {
            layoutManager.canScrollVertically() ->
                targetView.y.toInt() + targetView.height - helper.endAfterPadding + offset
            targetView.isLayoutRtl() -> targetView.x.toInt() - helper.startAfterPadding - offset
            else -> targetView.x.toInt() + targetView.width - helper.endAfterPadding + offset
        }
    }

    private val RecyclerView.LayoutManager.gridGalleryOffset get() =
        if (this is DivGridLayoutManager) itemSpacing / 2 else 0
}
