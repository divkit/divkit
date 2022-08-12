package com.yandex.div.core

import android.view.View
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.Div
import javax.inject.Inject

/**
 * DivCustomContainerChildFactory used to create div views in [DivCustomViewAdapter]
 */
class DivCustomContainerChildFactory @Inject internal constructor (){

    /**
     * Call to create child div view. And then call ViewGroup.addView by yourself!
     */
    fun createChildView(
        div: Div,
        divStatePath: DivStatePath,
        divView: Div2View
    ): View {
        return divView.div2Component.div2Builder.buildView(div, divView, divStatePath)
    }

    /**
     * Call to bind child div view.
     */
    fun bindChildView(
        childView: View,
        div: Div,
        divStatePath: DivStatePath,
        divView: Div2View
    ) {
        divView.div2Component.divBinder.bind(childView, div, divView, divStatePath)
    }

}
