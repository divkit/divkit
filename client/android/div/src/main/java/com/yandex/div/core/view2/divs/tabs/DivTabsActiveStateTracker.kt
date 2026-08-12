package com.yandex.div.core.view2.divs.tabs

import androidx.viewpager.widget.ViewPager
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.tabs.BaseDivTabbedCardUi
import com.yandex.div2.DivAction

internal class DivTabsActiveStateTracker(
    var divBlock: DivBlock.Tabs,
    private val divView: Div2View,
    private val div2Logger: Div2Logger,
) : ViewPager.OnPageChangeListener,
    BaseDivTabbedCardUi.ActiveTabClickListener<DivAction> {

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) {
        div2Logger.logTabPageChanged(divView, position)
        divView.dataComponent.tabsStateCache.putSelectedTab(divBlock.path.fullPath, position)
        divView.dataComponent.runtimeVisitor
            .createAndAttachRuntimesToTabs(divView, divBlock.divValue, divBlock.path, divBlock.expressionResolver)
    }

    override fun onPageScrollStateChanged(state: Int) = Unit

    override fun onActiveTabClicked(action: DivAction, tabPosition: Int) = Unit
}
