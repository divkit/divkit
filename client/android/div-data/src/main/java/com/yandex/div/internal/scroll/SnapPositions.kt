package com.yandex.div.internal.scroll

import com.yandex.div.core.annotations.InternalApi

/**
 * Item positions a snap helper needs to pick a target in the direction of a gesture.
 *
 * Every position is named, so a call site can not mix up first and last or visible and completely
 * visible: an adapter maps each one explicitly instead of passing four same-typed getters in order.
 */
@InternalApi
interface SnapPositions {
    val itemCount: Int
    val isLinearLayout: Boolean
    val firstCompletelyVisible: Int
    val lastCompletelyVisible: Int
    val firstVisible: Int
    val lastVisible: Int
}

@InternalApi
fun findDirectionalSnapPosition(forward: Boolean, positions: SnapPositions): Int {
    val visible = if (forward) positions.lastVisible else positions.firstVisible
    // Snap to a partially visible edge item instead of moving away from the linear layout edge.
    val isEdgeItem = if (forward) {
        positions.itemCount > 0 && visible == positions.itemCount - 1
    } else {
        visible == 0
    }
    if (positions.isLinearLayout && isEdgeItem) return visible

    val completelyVisible = if (forward) positions.lastCompletelyVisible else positions.firstCompletelyVisible
    if (completelyVisible != NO_POSITION) return completelyVisible

    return if (visible != NO_POSITION) visible else 0
}

private const val NO_POSITION = -1
