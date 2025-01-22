package com.yandex.div.core.view2.divs.pager

internal interface DivPagerPageSizeProvider {
    fun getItemSize(position: Int): Float
}
