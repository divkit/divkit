package com.yandex.div.core.view2.divs

import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.divs.widgets.DivPagerIndicatorView
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import java.util.*
import javax.inject.Inject

@DivScope
internal class PagerIndicatorConnector @Inject constructor() {
    private val divPagers = WeakHashMap<String, DivPagerView>()
    private val divIndicators = WeakHashMap<String, MutableList<DivPagerIndicatorView>>()

    internal fun submitPager(pagerId: String, divPagerView: DivPagerView) {
        divPagers[pagerId] = divPagerView
    }

    internal fun submitIndicator(pagerId: String, divPagerIndicatorView: DivPagerIndicatorView) {
        divIndicators.getOrPut(pagerId, ::mutableListOf).add(divPagerIndicatorView)
    }

    internal fun attach() {
        divPagers.forEach { (pagerId, pager) ->
            divIndicators[pagerId]?.forEach {
                it.attachPager(pager)
            }
        }
        divPagers.clear()
        divIndicators.clear()
    }
}
