package com.yandex.div.core.view2.divs.widgets

import com.yandex.div.internal.core.DivBlock

internal interface DivCollectionHolder {
    var items: List<DivBlock>?
}

internal class DivCollectionHolderMixin : DivCollectionHolder {
    override var items: List<DivBlock>? = null
}
