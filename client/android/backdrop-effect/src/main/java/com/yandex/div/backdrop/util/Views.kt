package com.yandex.div.backdrop.util

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Size

/**
 * Collects the visible views painted after [this] anywhere between it and the window root, i.e.
 * everything that covers the view instead of being covered by it.
 *
 * At every level up the hierarchy the children are ordered the way [ViewGroup] draws them: through
 * [ViewGroup.getChildDrawingOrder] first — so a custom order set by [ViewGroup] subclasses such as
 * `RecyclerView` or `ViewPager` is respected — and then stably by [View.getZ]. Siblings that end up
 * after the branch holding [this] are the ones painted above it.
 *
 * Content that a [ViewGroup] draws outside of its children is invisible to this walk and cannot be
 * hidden by the caller: the [android.view.ViewOverlay] of any ancestor, transient views added by
 * [ViewGroup.addTransientView][android.view.ViewGroup], and children of another window. Neither can
 * a sibling that runs a legacy [android.view.animation.Animation] — [ViewGroup] keeps drawing it
 * regardless of its visibility.
 */
internal fun View.collectViewsAbove(): List<View> {
    val viewsAbove = mutableListOf<View>()

    var child: View = this
    var parent = child.parent
    while (parent is ViewGroup) {
        parent.collectChildrenAbove(child, viewsAbove)
        child = parent
        parent = child.parent
    }

    return viewsAbove
}

private fun ViewGroup.collectChildrenAbove(child: View, result: MutableList<View>) {
    if (childCount <= 1) {
        return
    }

    val drawOrder = childrenInDrawOrder()
    val childPosition = drawOrder.indexOf(child)
    if (childPosition < 0) {
        return
    }

    for (position in childPosition + 1 until drawOrder.size) {
        val sibling = drawOrder[position]
        if (sibling.visibility == View.VISIBLE) {
            result += sibling
        }
    }
}

/**
 * Reproduces the order [ViewGroup.dispatchDraw] draws the children in: the drawing order of the
 * group, then a stable sort by [View.getZ].
 */
private fun ViewGroup.childrenInDrawOrder(): List<View> {
    val children = ArrayList<View>(childCount)
    for (position in 0 until childCount) {
        children.add(childAtDrawingPosition(position) ?: continue)
    }

    return children.sortedBy { it.z }
}

private fun ViewGroup.childAtDrawingPosition(position: Int): View? {
    val index = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getChildDrawingOrder(position)
    } else {
        position
    }

    return getChildAt(if (index in 0 until childCount) index else position)
}

@Size(2)
internal fun View.getCoordinateOffset(other: View): IntArray {
    val viewLocation = IntArray(2)
    val otherLocation = IntArray(2)
    getLocationInWindow(viewLocation)
    other.getLocationInWindow(otherLocation)

    return intArrayOf(
        viewLocation[0] - otherLocation[0],
        viewLocation[1] - otherLocation[1]
    )
}
