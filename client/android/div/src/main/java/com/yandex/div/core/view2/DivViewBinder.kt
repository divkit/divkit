package com.yandex.div.core.view2

import android.view.View
import com.yandex.div.core.state.DivStatePath

/**
 * Interface for binding div views
 * @param <TData> - Div data class
 * @param <TView> - binding view
 */
internal interface DivViewBinder<TData, TView : View> {

    /**
     * Binds div's data to templated view
     */
    fun bindView(context: BindingContext, view: TView, div: TData) = Unit

    fun bindView(context: BindingContext, view: TView, div: TData, path: DivStatePath) {
        bindView(context, view, div)
    }
}
