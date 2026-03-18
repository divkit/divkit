package com.yandex.div.core.extension

import android.view.View
import com.yandex.div.core.preload.CompositeResult
import com.yandex.div.core.preload.PreloadResult
import com.yandex.div.core.preload.PreloadingCompletion
import com.yandex.div.core.preload.PreloadingRegistry
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase

interface DivExtensionHandler {

    fun matches(div: DivBase): Boolean

    fun preprocess(div: DivBase, expressionResolver: ExpressionResolver) = Unit

    fun preprocess(
        div: DivBase,
        expressionResolver: ExpressionResolver,
        preloadingRegistry: PreloadingRegistry,
    ) {
        val completion = preloadingRegistry.registerPreloading(div.toString())
        preprocess(div, expressionResolver)
        completion.onCompleted(CompositeResult(emptyList()))
    }

    fun beforeBindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) = Unit

    fun bindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase)

    fun unbindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase)

    fun loadMedia(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) = Unit

    fun releaseMedia(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) = Unit
}
