package com.yandex.div.core.view2.divs.widgets

import com.yandex.div.internal.core.DivItemBuilderResult

internal interface DivCollectionHolder {
    var items: List<DivItemBuilderResult>?
}

internal class DivCollectionHolderMixin : DivCollectionHolder {
    override var items: List<DivItemBuilderResult>? = null
}
