package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DragScope
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity

/**
 * Keeps the gestures that start on a scrollable element away from its scrolling ancestors,
 * the `restrict_parent_scroll` semantics of the View renderer's `ParentScrollRestrictor`:
 * - drags that the element itself does not claim are claimed here, so an ancestor cannot start
 *   scrolling with them (the counterpart of `requestDisallowInterceptTouchEvent`);
 * - the element's own-axis scroll and fling leftovers are consumed instead of being handed
 *   to ancestors. Cross-axis leftovers of scrollables nested inside the element still pass through,
 *   as nested scrolling does in the View renderer.
 *
 * Apply to the scrollable itself: the element's own gesture handling runs first and keeps
 * priority on its axis, so the element scrolls exactly as without this modifier.
 */
internal fun Modifier.restrictParentScroll(isHorizontal: Boolean): Modifier {
    val mainAxis = if (isHorizontal) Orientation.Horizontal else Orientation.Vertical
    val crossAxis = if (isHorizontal) Orientation.Vertical else Orientation.Horizontal
    return nestedScroll(if (isHorizontal) HORIZONTAL_PARENT_SCROLL_RESTRICTOR else VERTICAL_PARENT_SCROLL_RESTRICTOR)
        .draggable(state = IgnoringDraggableState, orientation = mainAxis)
        // Nearest to the element, so the element resolves a cross-axis gesture conflict the same
        // way as with a scrolling parent: by giving it up.
        .draggable(state = IgnoringDraggableState, orientation = crossAxis)
}

private class ParentScrollRestrictor(private val isHorizontal: Boolean) : NestedScrollConnection {

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        return if (isHorizontal) Offset(available.x, 0f) else Offset(0f, available.y)
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        return if (isHorizontal) Velocity(available.x, 0f) else Velocity(0f, available.y)
    }
}

private val HORIZONTAL_PARENT_SCROLL_RESTRICTOR = ParentScrollRestrictor(isHorizontal = true)
private val VERTICAL_PARENT_SCROLL_RESTRICTOR = ParentScrollRestrictor(isHorizontal = false)

// Stateless on purpose: a shared MutatorMutex-backed state would let a drag on one element cancel
// the drag claimed by another.
private object IgnoringDraggableState : DraggableState, DragScope {

    override suspend fun drag(dragPriority: MutatePriority, block: suspend DragScope.() -> Unit) = block(this)

    override fun dispatchRawDelta(delta: Float) = Unit

    override fun dragBy(pixels: Float) = Unit
}
