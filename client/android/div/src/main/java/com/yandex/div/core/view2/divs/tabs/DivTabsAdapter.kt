package com.yandex.div.core.view2.divs.tabs

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.yandex.div.core.downloader.DivPatchApply
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.toLayoutParamsSize
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.internal.viewpool.ViewPool
import com.yandex.div.internal.widget.tabs.BaseDivTabbedCardUi
import com.yandex.div.internal.widget.tabs.HeightCalculatorFactory
import com.yandex.div.internal.widget.tabs.ScrollableViewPager
import com.yandex.div.internal.widget.tabs.TabTextStyleProvider
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivSize
import com.yandex.div2.DivTabs

internal class DivTabsAdapter(
    viewPool: ViewPool,
    view: View,
    tabbedCardConfig: TabbedCardConfig,
    heightCalculatorFactory: HeightCalculatorFactory,
    val isDynamicHeight: Boolean,
    private val bindingContext: BindingContext,
    textStyleProvider: TabTextStyleProvider,
    private val viewCreator: DivViewCreator,
    private val divBinder: DivBinder,
    val divTabsEventManager: DivTabsEventManager,
    var path: DivStatePath,
    private val divPatchCache: DivPatchCache,
) : BaseDivTabbedCardUi<DivSimpleTab, ViewGroup, DivAction>(
    viewPool,
    view,
    tabbedCardConfig,
    heightCalculatorFactory,
    textStyleProvider,
    divTabsEventManager,
    divTabsEventManager
) {

    private val tabModels = mutableMapOf<ViewGroup, TabModel>()

    fun setData(data: Input<DivSimpleTab>, selectedTab: Int) {
        super.setData(data, bindingContext.expressionResolver, bindingContext.divView.expressionSubscriber)
        tabModels.clear()
        mPager.setCurrentItem(selectedTab, true)
    }

    override fun bindTabData(
        tabView: ViewGroup,
        tab: DivSimpleTab,
        tabNumber: Int
    ): ViewGroup {
        tabView.releaseAndRemoveChildren(bindingContext.divView)

        val itemDiv = tab.item.div
        val itemView = createItemView(itemDiv, bindingContext.expressionResolver)
        tabModels[tabView] = TabModel(tabNumber, itemDiv, itemView)
        tabView.addView(itemView)

        return tabView
    }

    override fun unbindTabData(tabView: ViewGroup) {
        tabModels.remove(tabView)
        tabView.releaseAndRemoveChildren(bindingContext.divView)
    }

    override fun fillMeasuringTab(tabView: ViewGroup, tab: DivSimpleTab, tabNumber: Int) {
        tabView.releaseAndRemoveChildren(bindingContext.divView)
        val itemView = createItemView(tab.item.div, bindingContext.expressionResolver)
        tabView.addView(itemView)
    }

    private fun createItemView(div: Div, resolver: ExpressionResolver): View {
        val itemView = viewCreator.create(div, resolver).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        divBinder.bind(bindingContext, itemView, div, path)

        return itemView
    }

    fun notifyStateChanged() {
        tabModels.forEach { (tabView, tabModel) ->
            divBinder.bind(bindingContext, tabModel.view, tabModel.div, path)
            // ... and a little bit of a magic
            tabView.requestLayout()
        }
    }

    fun applyPatch(resolver: ExpressionResolver, div: DivTabs): DivTabs? {
        val patchMap = divPatchCache.getPatch(bindingContext.divView.dataTag) ?: return null
        val newTabs = DivPatchApply(patchMap).applyPatchForDiv(Div.Tabs(div), resolver)[0].value() as DivTabs
        val displayMetrics = bindingContext.divView.resources.displayMetrics
        val list = newTabs.items.map { DivSimpleTab(it, displayMetrics, resolver) }
        setData ({ list }, mPager.currentItem)
        return newTabs
    }

    val pager = PagerController(mPager)
}

internal class PagerController(private val scrollableViewPager: ScrollableViewPager) {
    fun smoothScrollTo(itemIndex: Int) {
        scrollableViewPager.setCurrentItem(itemIndex, true)
    }

    val currentItemIndex get() = scrollableViewPager.currentItem
}

internal class DivSimpleTab(
    private val item: DivTabs.Item,
    private val displayMetrics: DisplayMetrics,
    private val resolver: ExpressionResolver
) : BaseDivTabbedCardUi.Input.SimpleTab<DivTabs.Item, DivAction> {

    override fun getTitle(): String {
        return item.title.evaluate(resolver)
    }

    override fun getActionable(): DivAction? {
        return item.titleClickAction
    }

    override fun getTabHeight(): Int? {
        val height = item.div.value().height
        return if (height is DivSize.Fixed) {
            height.toLayoutParamsSize(displayMetrics, resolver)
        } else {
            null
        }
    }

    override fun getTabHeightLayoutParam() =
        item.div.value().height.toLayoutParamsSize(displayMetrics, resolver)

    override fun getItem(): DivTabs.Item {
        return item
    }
}

private class TabModel(
    val index: Int,
    val div: Div,
    val view: View
)
