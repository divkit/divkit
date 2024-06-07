package com.yandex.div.core.view2.divs.widgets

import com.yandex.div.core.Disposable
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.internal.widget.TransientViewMixin
import com.yandex.div2.DivBase

internal interface DivHolderView<T: DivBase> : DivBorderSupports,
    TransientView,
    ExpressionSubscriber {

    var div: T?
    var bindingContext: BindingContext?
}

internal class DivHolderViewMixin<T: DivBase> : DivHolderView<T>,
    DivBorderSupports by DivBorderSupportsMixin(),
    TransientView by TransientViewMixin() {

    override var div: T? = null

    override var bindingContext: BindingContext? = null

    override val subscriptions = mutableListOf<Disposable>()

    override fun release() {
        super.release()
        div = null
        bindingContext = null
        releaseBorderDrawer()
    }
}
