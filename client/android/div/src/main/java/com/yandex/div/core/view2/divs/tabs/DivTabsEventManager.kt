package com.yandex.div.core.view2.divs.tabs

import androidx.viewpager.widget.ViewPager
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div.internal.KLog
import com.yandex.div.internal.widget.tabs.BaseDivTabbedCardUi
import com.yandex.div2.DivAction
import com.yandex.div2.DivTabs

internal class DivTabsEventManager(
    private val context: BindingContext,
    private val actionBinder: DivActionBinder,
    private val div2Logger: Div2Logger,
    private val visibilityActionTracker: DivVisibilityActionTracker,
    private val tabLayout: DivTabsLayout,
    var div: DivTabs
) : ViewPager.OnPageChangeListener,
    BaseDivTabbedCardUi.ActiveTabClickListener<DivAction> {

    private var currentPagePosition: Int = NO_POSITION

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) {
        div2Logger.logTabPageChanged(context.divView, position)
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
            visibilityActionTracker.cancelTrackingViewsHierarchy(context, tabLayout, previousTab.div)
            context.divView.unbindViewFromDiv(tabLayout)
        }

        val selectedTab = div.items[position]
        visibilityActionTracker.startTrackingViewsHierarchy(context, tabLayout, selectedTab.div)
        context.divView.bindViewToDiv(tabLayout, selectedTab.div)

        currentPagePosition = position
    }

    override fun onActiveTabClicked(action: DivAction, tabPosition: Int) {
        if (action.menuItems != null) {
            // TODO(MORDAANDROID-90): handle case with menuItems != null
            KLog.w(TAG) { "non-null menuItems ignored in title click action" }
        }
        div2Logger.logActiveTabTitleClick(context.divView, context.expressionResolver, tabPosition, action)
        actionBinder.handleAction(context.divView, context.expressionResolver, action, DivActionReason.CLICK)
    }

    private companion object {
        private const val TAG = "DivTabsEventManager"
        private const val NO_POSITION = -1
    }
}
