package com.yandex.div.core.view2.divs.pager

import android.annotation.SuppressLint
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.ScrollDirection
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.hasSightActions
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div2.DivPager

internal class DivPagerPageChangeCallback(
    private val divPager: DivPager,
    private val items: List<DivItemBuilderResult>,
    private val bindingContext: BindingContext,
    private val recyclerView: RecyclerView,
    private val pagerView: DivPagerView,
) : ViewPager2.OnPageChangeCallback() {
    private var prevPosition = RecyclerView.NO_POSITION

    private val divView = bindingContext.divView
    private val minimumSignificantDx = divView.config.logCardScrollSignificantThreshold
    private var totalDelta = 0

    @SuppressLint("SwitchIntDef")
    override fun onPageScrollStateChanged(state: Int) {
        super.onPageScrollStateChanged(state)

        when (state) {
            ViewPager2.SCROLL_STATE_IDLE -> trackVisibleViews()
        }
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        trackVisibleViews()
        if (position == prevPosition) {
            return
        }
        if (prevPosition != RecyclerView.NO_POSITION) {
            divView.unbindViewFromDiv(pagerView)
        }

        if (position == RecyclerView.NO_POSITION) {
            prevPosition = position
            return
        }

        if (prevPosition != RecyclerView.NO_POSITION) {
            val direction =
                if (position > prevPosition) ScrollDirection.NEXT else ScrollDirection.BACK
            divView.div2Component.div2Logger.logPagerChangePage(
                divView,
                items[position].expressionResolver,
                divPager,
                position,
                direction
            )
        }
        val selectedPage = items[position].div

        if (selectedPage.value().hasSightActions) {
            divView.bindViewToDiv(pagerView, selectedPage)
        }
        prevPosition = position
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        val minimumDelta = if (minimumSignificantDx > 0) {
            minimumSignificantDx
        } else {
            val width = recyclerView.layoutManager?.width ?: 0
            width / 20
        }
        totalDelta += positionOffsetPixels
        if (totalDelta > minimumDelta) {
            totalDelta = 0
            trackVisibleViews()
        }
    }

    private fun trackVisibleViews() {
        if (recyclerView.children.count() > 0) {
            trackVisibleChildren()
        } else {
            recyclerView.doOnActualLayout { trackVisibleChildren() }
        }
    }

    private fun trackVisibleChildren() {
        recyclerView.children.forEach { child ->
            val childPosition = recyclerView.getChildAdapterPosition(child)
            if (childPosition == RecyclerView.NO_POSITION) return

            val item = items[childPosition]
            divView.div2Component.visibilityActionTracker
                .startTrackingViewsHierarchy(bindingContext.getFor(item.expressionResolver), child, item.div)
        }
    }
}
