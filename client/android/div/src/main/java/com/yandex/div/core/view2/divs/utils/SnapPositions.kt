package com.yandex.div.core.view2.divs.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Item positions a snap helper needs to pick a target in the direction of a gesture.
 *
 * Every position is named, so a call site can not mix up first and last or visible and completely
 * visible: an adapter maps each one explicitly instead of passing four same-typed getters in order.
 */
internal interface SnapPositions {
    val itemCount: Int
    val isLinearLayout: Boolean
    val firstCompletelyVisible: Int
    val lastCompletelyVisible: Int
    val firstVisible: Int
    val lastVisible: Int
}

internal fun isForwardScroll(
    isVertical: Boolean,
    velocityX: Int,
    velocityY: Int,
    isRtl: Boolean,
): Boolean {
    val velocity = when {
        isVertical -> velocityY
        isRtl -> -velocityX
        else -> velocityX
    }
    return velocity >= 0
}

internal fun findDirectionalSnapPosition(forward: Boolean, positions: SnapPositions): Int {
    val visible = if (forward) positions.lastVisible else positions.firstVisible
    // Snap to a partially visible edge item instead of moving away from the linear layout edge.
    val isEdgeItem = if (forward) {
        positions.itemCount > 0 && visible == positions.itemCount - 1
    } else {
        visible == 0
    }
    if (positions.isLinearLayout && isEdgeItem) return visible

    val completelyVisible = if (forward) positions.lastCompletelyVisible else positions.firstCompletelyVisible
    if (completelyVisible != RecyclerView.NO_POSITION) return completelyVisible

    return if (visible != RecyclerView.NO_POSITION) visible else 0
}

internal fun LinearLayoutManager.snapPositions(): SnapPositions {
    val manager = this
    return object : SnapPositions {
        override val itemCount get() = manager.itemCount
        override val isLinearLayout get() = true
        override val firstCompletelyVisible get() = manager.findFirstCompletelyVisibleItemPosition()
        override val lastCompletelyVisible get() = manager.findLastCompletelyVisibleItemPosition()
        override val firstVisible get() = manager.findFirstVisibleItemPosition()
        override val lastVisible get() = manager.findLastVisibleItemPosition()
    }
}
