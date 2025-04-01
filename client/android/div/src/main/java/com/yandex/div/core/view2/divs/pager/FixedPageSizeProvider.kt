package com.yandex.div.core.view2.divs.pager

internal interface FixedPageSizeProvider {
    val itemSize: Float
    val neighbourSize: Float
    val hasOffScreenPages: Boolean
}
