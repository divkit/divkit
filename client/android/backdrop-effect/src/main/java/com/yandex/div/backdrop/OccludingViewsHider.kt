package com.yandex.div.backdrop

import android.view.View

/**
 * Hides the views painted above a decorated element for the duration of a backdrop capture, so that
 * they do not bleed into the element's own backdrop, and puts them back afterwards.
 *
 * A view is dropped for good once hiding it makes it — or its parent — request a layout: a layout
 * pass invalidates the capture, which schedules the next one, which hides the view again, an
 * endless loop at the refresh rate. [android.view.SurfaceView] requests a layout on every
 * visibility change, and a parent running a [android.animation.LayoutTransition] requests one on
 * the view's behalf. Such a view keeps bleeding into the backdrop, which is the lesser of the two
 * evils.
 */
internal class OccludingViewsHider {

    private val hiddenViews = mutableListOf<View>()
    private val restoredVisibilities = mutableListOf<Int>()
    private val hadPendingLayout = mutableListOf<Boolean>()
    private val hadParentPendingLayout = mutableListOf<Boolean>()
    private val nonHidableViews = mutableSetOf<View>()

    /**
     * Hides [views], remembering their current visibility. Every call has to be paired with
     * [restore], including when hiding itself fails midway.
     */
    fun hide(views: List<View>) {
        views.forEach { view ->
            if (view in nonHidableViews) {
                return@forEach
            }

            hiddenViews += view
            restoredVisibilities += view.visibility
            hadPendingLayout += view.isLayoutRequested
            hadParentPendingLayout += view.isParentLayoutRequested

            view.visibility = View.INVISIBLE
        }
    }

    /**
     * Restores the visibility the views had when they were hidden. A view whose visibility no
     * longer is [View.INVISIBLE] is left alone: a capture draws the hierarchy and therefore runs
     * arbitrary host code, which may have taken the view over in the meantime.
     */
    fun restore() {
        hiddenViews.forEachIndexed { index, view ->
            if (view.visibility == View.INVISIBLE) {
                view.visibility = restoredVisibilities[index]
            }

            val requestedLayout = !hadPendingLayout[index] && view.isLayoutRequested
            val parentRequestedLayout = !hadParentPendingLayout[index] && view.isParentLayoutRequested
            if (requestedLayout || parentRequestedLayout) {
                nonHidableViews += view
            }
        }

        clearHiddenViews()
    }

    fun release() {
        clearHiddenViews()
        nonHidableViews.clear()
    }

    private fun clearHiddenViews() {
        hiddenViews.clear()
        restoredVisibilities.clear()
        hadPendingLayout.clear()
        hadParentPendingLayout.clear()
    }
}

/**
 * Whether the parent awaits a layout pass. Checked alongside [View.isLayoutRequested] because a
 * parent may request the layout on behalf of the view — that is what
 * [android.animation.LayoutTransition] does when a child is hidden.
 */
private val View.isParentLayoutRequested: Boolean
    get() = (parent as? View)?.isLayoutRequested ?: false
