package com.yandex.div.core.view2.divs.tabs

import androidx.viewpager.widget.ViewPager
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionPerformer
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div.internal.KLog
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.tabs.BaseDivTabbedCardUi
import com.yandex.div2.DivAction

internal class DivTabsEventManager(
    var divBlock: DivBlock.Tabs,
    private val actionPerformer: DivActionPerformer,
    private val div2Logger: Div2Logger,
    private val visibilityActionTracker: DivVisibilityActionTracker,
    private val tabLayout: DivTabsLayout,
    private val divView: Div2View,
) : ViewPager.OnPageChangeListener,
    BaseDivTabbedCardUi.ActiveTabClickListener<DivAction> {

    private var currentPagePosition: Int = NO_POSITION

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) {
        div2Logger.logTabPageChanged(divView, position)
        onPageDisplayed(position)
    }

    override fun onPageScrollStateChanged(state: Int) = Unit

    fun onPageDisplayed(position: Int) {
        if (position == currentPagePosition) return

        // Since there is no way to get itemView from ViewPager for given position
        // we are using ViewPager instance itself to compute visibility.
        // This assumption is safe as long as we display only one page in ViewPager.
        if (currentPagePosition != NO_POSITION) {
            val previousTab = divBlock.divValue.items[currentPagePosition]
            visibilityActionTracker.
            cancelTrackingViewsHierarchy(tabLayout, previousTab.div, divBlock.expressionResolver, divView)
            divView.unbindViewFromDiv(tabLayout)
        }

        val selectedTab = divBlock.divValue.items[position]
        visibilityActionTracker
            .startTrackingViewsHierarchy(tabLayout, selectedTab.div, divBlock.expressionResolver, divView)
        divView.bindViewToDiv(tabLayout, selectedTab.div)

        currentPagePosition = position
    }

    override fun onActiveTabClicked(action: DivAction, tabPosition: Int) {
        if (action.menuItems != null) {
            // TODO(MORDAANDROID-90): handle case with menuItems != null
            KLog.w(TAG) { "non-null menuItems ignored in title click action" }
        }
        actionPerformer.performTabTitleClick(divView, divBlock.expressionResolver, action, tabPosition)
    }

    private companion object {
        private const val TAG = "DivTabsEventManager"
        private const val NO_POSITION = -1
    }
}
