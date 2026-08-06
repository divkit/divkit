package com.yandex.div.core.view2

import android.view.View
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.internal.core.DivBlock

/**
 * Interface for binding div views
 * @param <TBlock> - DivBlock subclass
 * @param <TView> - binding view
 */
internal abstract class DivViewBinder<TBlock : DivBlock, TView : View>(
    private val baseBinder: DivBaseBinder
) {

    @Suppress("UNCHECKED_CAST")
    open fun bindView(view: TView, divBlock: TBlock, divView: Div2View) {
        val oldDivBlock = (view as DivHolderView<TBlock>).divBlock
        if (divBlock.div === oldDivBlock?.div) return

        baseBinder.bindView(view, divBlock, oldDivBlock, divView)
        view.bind(divBlock, oldDivBlock, divView)
    }

    protected open fun TView.bind(
        divBlock: TBlock,
        oldDivBlock: TBlock?,
        divView: Div2View
    ) = Unit
}
