package com.yandex.div.core.view2.divs.tabs

import androidx.viewpager.widget.ViewPager
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.expression.local.DivRuntimeVisitor
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.TabsStateCache
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.internal.widget.tabs.BaseDivTabbedCardUi
import com.yandex.div2.DivAction
import com.yandex.div2.DivTabs

internal class DivTabsActiveStateTracker(
    private val context: BindingContext,
    private val path: DivStatePath,
    private val div2Logger: Div2Logger,
    private val tabsStateCache: TabsStateCache,
    private val runtimeVisitor: DivRuntimeVisitor,
    var div: DivTabs
) : ViewPager.OnPageChangeListener,
    BaseDivTabbedCardUi.ActiveTabClickListener<DivAction> {

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) {
        div2Logger.logTabPageChanged(context.divView, position)
        tabsStateCache.putSelectedTab(context.divView.dataTag.id, path.fullPath, position)
        runtimeVisitor.createAndAttachRuntimesToTabs(context.divView, div, path, context.expressionResolver)
    }

    override fun onPageScrollStateChanged(state: Int) = Unit

    override fun onActiveTabClicked(action: DivAction, tabPosition: Int) = Unit
}
