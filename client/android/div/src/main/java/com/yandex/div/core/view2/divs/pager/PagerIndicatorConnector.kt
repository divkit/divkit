package com.yandex.div.core.view2.divs.pager

import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.divs.widgets.DivPagerIndicatorView
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div2.DivPager
import javax.inject.Inject

@DivScope
internal class PagerIndicatorConnector @Inject constructor() {
    private data class IndicatorData(
        val indicator: DivPagerIndicatorView,
        val pagerDiv: DivPager,
    )

    private val pagers = mutableMapOf<DivPager, DivPagerView>()
    private val indicators = mutableListOf<IndicatorData>()

    internal fun submitPager(pagerView: DivPagerView, pagerDiv: DivPager) {
        pagers[pagerDiv] = pagerView
    }

    internal fun submitIndicator(indicatorView: DivPagerIndicatorView, pagerDiv: DivPager) {
        val indicatorData = IndicatorData(indicatorView, pagerDiv)
        indicators.add(indicatorData)
    }

    internal fun attach() {
        pagers.forEach {
            it.value.clearChangePageCallbackForIndicators()
        }
        indicators.forEach { indicator ->
            val pager = pagers[indicator.pagerDiv] ?: return@forEach
            indicator.indicator.attachPager(pager)
        }
        pagers.clear()
        indicators.clear()
    }
}
