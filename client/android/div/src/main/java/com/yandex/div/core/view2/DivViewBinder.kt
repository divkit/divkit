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
    fun bindView(view: TView, div: TData, divView: Div2View) = Unit

    fun bindView(view: TView, div: TData, divView: Div2View, path: DivStatePath) {
        bindView(view, div, divView)
    }
}
