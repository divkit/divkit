package com.yandex.div.core.view2

import android.view.View
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div2.Div
import com.yandex.div2.DivBase

/**
 * Interface for binding div views
 * @param <TData> - Div data class
 * @param <TDataValue> - DivBase class corresponding to TData
 * @param <TView> - binding view
 */
internal abstract class DivViewBinder<TData : Div, TDataValue : DivBase, TView : View>(
    private val baseBinder: DivBaseBinder
) {

    @Suppress("UNCHECKED_CAST")
    open fun bindView(context: BindingContext, view: TView, div: TData, path: DivStatePath) {
        val oldDiv = (view as DivHolderView<TData>).div
        if (div === oldDiv) return

        baseBinder.bindView(context, view, div, oldDiv, path)
        view.bind(context, div.value() as TDataValue, oldDiv?.value() as TDataValue?, path)
    }

    protected open fun TView.bind(bindingContext: BindingContext, div: TDataValue, oldDiv: TDataValue?) = Unit

    protected open fun TView.bind(
        bindingContext: BindingContext,
        div: TDataValue,
        oldDiv: TDataValue?,
        path: DivStatePath,
    ) = bind(bindingContext, div, oldDiv)
}
