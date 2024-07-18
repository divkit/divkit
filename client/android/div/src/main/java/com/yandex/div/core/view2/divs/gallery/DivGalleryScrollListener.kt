package com.yandex.div.core.view2.divs.gallery

import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.ScrollDirection
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div2.DivGallery
import kotlin.math.abs

internal class DivGalleryScrollListener(
    private val bindingContext: BindingContext,
    private val recycler: DivRecyclerView,
    private val galleryItemHelper: DivGalleryItemHelper,
    private val galleryDiv: DivGallery,
) : RecyclerView.OnScrollListener() {

    private val divView = bindingContext.divView
    private val minimumSignificantDx = divView.config.logCardScrollSignificantThreshold

    private var totalDelta = 0
    private var alreadyLogged = false
    var direction = ScrollDirection.NEXT

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        // New dragging event resets logged state
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            alreadyLogged = false
        }
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            divView.div2Component.div2Logger.logGalleryCompleteScroll(
                divView,
                bindingContext.expressionResolver,
                galleryDiv,
                galleryItemHelper.firstVisibleItemPosition(),
                galleryItemHelper.lastVisibleItemPosition(),
                direction
            )
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val minimumDelta = when (minimumSignificantDx > 0) {
            true -> minimumSignificantDx
            else -> galleryItemHelper.width() / 20
        }
        totalDelta += abs(dx) + abs(dy)  // because in galleries we used only one scroll direction at a time
        if (totalDelta > minimumDelta) {
            totalDelta = 0
            if (!alreadyLogged) {
                alreadyLogged = true
                divView.div2Component.div2Logger.logGalleryScroll(divView)
                direction = if (dx > 0 || dy > 0) ScrollDirection.NEXT else ScrollDirection.BACK
            }
            trackViews()
        }
    }

    private fun trackViews() {
        val visibilityActionTracker = divView.div2Component.visibilityActionTracker
        visibilityActionTracker.updateVisibleViews(recycler.children.toList())

        recycler.children.forEach { child ->
            val position = recycler.getChildAdapterPosition(child)
            if (position == RecyclerView.NO_POSITION) return@forEach

            val div = (recycler.adapter as DivGalleryAdapter).visibleItems[position].div
            visibilityActionTracker.startTrackingViewsHierarchy(bindingContext, child, div)
        }

        // Find and track recycled views containing DisappearActions that are waiting for disappear
        with(visibilityActionTracker) {
            getDivWithWaitingDisappearActions().filter { it.key !in recycler.children }.forEach { (view, div) ->
                trackDetachedView(bindingContext, view, div)
            }
        }
    }
}
