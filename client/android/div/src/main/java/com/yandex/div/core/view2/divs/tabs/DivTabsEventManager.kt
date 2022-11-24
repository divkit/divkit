package com.yandex.div.core.view2.divs.tabs

import androidx.viewpager.widget.ViewPager
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.util.KLog
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.internal.widget.tabs.BaseDivTabbedCardUi
import com.yandex.div.internal.widget.tabs.TabsLayout
import com.yandex.div2.DivAction
import com.yandex.div2.DivTabs

internal class DivTabsEventManager(
    private val div2View: Div2View,
    private val actionBinder: DivActionBinder,
    private val div2Logger: Div2Logger,
    private val visibilityActionTracker: DivVisibilityActionTracker,
    private val tabLayout: TabsLayout,
    var div: DivTabs
) : ViewPager.OnPageChangeListener,
    BaseDivTabbedCardUi.ActiveTabClickListener<DivAction> {

    private val viewPager: ViewPager
        get() = tabLayout.viewPager

    private var currentPagePosition: Int = NO_POSITION

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) {
        div2Logger.logTabPageChanged(div2View, position)
        onPageDisplayed(position)
    }

    override fun onPageScrollStateChanged(state: Int) = Unit

    fun onPageDisplayed(position: Int) {
        if (position == currentPagePosition) return

        // Since there is no way to get itemView from ViewPager for given position
        // we are using ViewPager instance itself to compute visibility.
        // This assumption is safe as long as we display only one page in ViewPager.
        if (currentPagePosition != NO_POSITION) {
            val previousTab = div.items[currentPagePosition]
            visibilityActionTracker.trackVisibilityActionsOf(div2View, null, previousTab.div)
            div2View.unbindViewFromDiv(viewPager)
        }

        val selectedTab = div.items[position]
        visibilityActionTracker.trackVisibilityActionsOf(div2View, viewPager, selectedTab.div)
        div2View.bindViewToDiv(viewPager, selectedTab.div)

        currentPagePosition = position
    }

    override fun onActiveTabClicked(action: DivAction, tabPosition: Int) {
        if (action.menuItems != null) {
            // TODO(MORDAANDROID-90): handle case with menuItems != null
            KLog.w(TAG) { "non-null menuItems ignored in title click action" }
        }
        div2Logger.logActiveTabTitleClick(div2View, tabPosition, action)
        actionBinder.handleAction(div2View, action)
    }

    private companion object {
        private const val TAG = "DivTabsEventManager"
        private const val NO_POSITION = -1
    }
}
