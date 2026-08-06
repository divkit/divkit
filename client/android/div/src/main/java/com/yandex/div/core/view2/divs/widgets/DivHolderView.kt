package com.yandex.div.core.view2.divs.widgets

import com.yandex.div.core.Disposable
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin

internal interface DivHolderView<T : DivBlock> : DivBorderSupports,
    TransientView,
    ExpressionSubscriber {

    var divBlock: T?

    override fun release() {
        super.release()
        divBlock = null
        releaseBorderDrawer()
    }
}

internal class DivHolderViewMixin<T : DivBlock> : DivHolderView<T>,
    DivBorderSupports by DivBorderSupportsMixin(),
    TransientView by TransientViewMixin() {

    override var divBlock: T? = null

    override val subscriptions = mutableListOf<Disposable>()
}
