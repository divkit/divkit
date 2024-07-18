package com.yandex.div.core

import android.view.View
import androidx.annotation.AnyThread
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import javax.inject.Inject

/**
 * DivCustomContainerChildFactory used to create div views in [DivCustomContainerViewAdapter]
 */
class DivCustomContainerChildFactory @Inject internal constructor (){

    /**
     * Call to create child div view. And then call ViewGroup.addView by yourself!
     */
    @JvmOverloads
    fun createChildView(
        div: Div,
        divStatePath: DivStatePath,
        divView: Div2View,
        expressionResolver: ExpressionResolver = divView.expressionResolver
    ): View {

        return divView.div2Component.div2Builder
            .buildView(div, divView.bindingContext.getFor(expressionResolver), divStatePath)
    }

    /**
     * Call to create child div view. And then call bindChildView and ViewGroup.addView by yourself!
     */
    @AnyThread
    @JvmOverloads
    fun createUnboundChildView(
        div: Div,
        divStatePath: DivStatePath,
        divView: Div2View,
        expressionResolver: ExpressionResolver = divView.expressionResolver
    ): View {
        return divView.div2Component.div2Builder
            .createView(div, divView.bindingContext.getFor(expressionResolver), divStatePath)
    }

    /**
     * Call to bind child div view.
     */
    fun bindChildView(
        childView: View,
        div: Div,
        divStatePath: DivStatePath,
        divView: Div2View,
        expressionResolver: ExpressionResolver,
    ) {
        divView.div2Component.divBinder
            .bind(divView.bindingContext.getFor(expressionResolver), childView, div, divStatePath)
    }
}
