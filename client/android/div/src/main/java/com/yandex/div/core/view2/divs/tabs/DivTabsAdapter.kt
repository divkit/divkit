package com.yandex.div.core.view2.divs.tabs

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.toLayoutParamsSize
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.viewpool.ViewPool
import com.yandex.div.internal.widget.tabs.BaseDivTabbedCardUi
import com.yandex.div.internal.widget.tabs.HeightCalculatorFactory
import com.yandex.div.internal.widget.tabs.TabTextStyleProvider
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivSize

internal class DivTabsAdapter(
    viewPool: ViewPool,
    private val view: View,
    tabbedCardConfig: TabbedCardConfig,
    heightCalculatorFactory: HeightCalculatorFactory,
    val isDynamicHeight: Boolean,
    var resolver: ExpressionResolver,
    var divView: Div2View,
    textStyleProvider: TabTextStyleProvider,
    private val viewCreator: DivViewCreator,
    private val divBinder: DivBinder,
    val divTabsEventManager: DivTabsEventManager,
    val activeStateTracker: DivTabsActiveStateTracker,
) : BaseDivTabbedCardUi<DivSimpleTab, ViewGroup, DivAction>(
    viewPool,
    view,
    tabbedCardConfig,
    heightCalculatorFactory,
    textStyleProvider,
    divTabsEventManager,
    divTabsEventManager,
    activeStateTracker,
) {

    private val tabModels = mutableMapOf<ViewGroup, TabModel>()

    val tabDivs get() = tabModels.map { it.value.divBlock.div }

    var selectedTab = NO_POS
        get() = mPager.currentItem
        set(value) {
            if (field != value) {
                mPager.setCurrentItem(value, true)
            }
        }

    fun setData(data: Input<DivSimpleTab>) {
        super.setData(data, resolver, view.expressionSubscriber)
        tabModels.clear()
    }

    override fun bindTabData(tabView: ViewGroup, tab: DivSimpleTab): ViewGroup {
        tabView.releaseAndRemoveChildren(divView)

        val itemView = createItemView(tab)
        tabModels[tabView] = TabModel(tab.item, itemView)
        tabView.addView(itemView)

        return tabView
    }

    override fun unbindTabData(tabView: ViewGroup) {
        tabModels.remove(tabView)
        tabView.releaseAndRemoveChildren(divView)
    }

    override fun fillMeasuringTab(tabView: ViewGroup, tab: DivSimpleTab) {
        tabView.releaseAndRemoveChildren(divView)
        val itemView = createItemView(tab)
        tabView.addView(itemView)
    }

    private fun createItemView(tab: DivSimpleTab): View {
        val item = tab.item
        val itemView = viewCreator.create(item.div, item.expressionResolver).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        divBinder.bind(itemView, item, divView)

        return itemView
    }

    fun notifyStateChanged() {
        tabModels.forEach { (tabView, tabModel) ->
            divBinder.bind(tabModel.view, tabModel.divBlock, divView)
            // ... and a little bit of a magic
            tabView.requestLayout()
        }
    }
}

internal class DivSimpleTab(
    private val title: Expression<String>,
    private val titleClickAction: DivAction?,
    val item: DivBlock,
    private val displayMetrics: DisplayMetrics,
    val parentResolver: ExpressionResolver
) : BaseDivTabbedCardUi.Input.TabBase<DivAction> {

    override fun getTitle() = title.evaluate(parentResolver)

    override fun getActionable() = titleClickAction

    override fun getTabHeight(): Int? {
        val height = item.div.value().height as? DivSize.Fixed ?: return null
        return height.toLayoutParamsSize(displayMetrics, item.expressionResolver)
    }

    override fun getTabHeightLayoutParam() =
        item.div.value().height.toLayoutParamsSize(displayMetrics, item.expressionResolver)
}

private class TabModel(
    val divBlock: DivBlock,
    val view: View
)
