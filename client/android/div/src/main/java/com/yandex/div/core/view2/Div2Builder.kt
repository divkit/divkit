package com.yandex.div.core.view2

import android.view.View
import android.view.ViewGroup.LayoutParams
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.local.DivRuntimeVisitor
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import javax.inject.Inject

/**
 * Base class for build div view
 */
@DivScope
internal class Div2Builder @Inject constructor(
    private val viewCreator: DivViewCreator,
    private val viewBinder: DivBinder,
    private val runtimeVisitor: DivRuntimeVisitor,
) {

    fun buildView(divBlock: DivBlock, divView: Div2View): View {
        return createView(divBlock.div, divBlock.expressionResolver, divBlock.path, divView).also {
            viewBinder.bind(it, divBlock, divView)
        }
    }

    fun createView(data: Div, resolver: ExpressionResolver, path: DivStatePath, divView: Div2View): View {
        runtimeVisitor.createAndAttachRuntimes(data, path, divView)
        val view = viewCreator.create(data, resolver).apply {
            layoutParams = DivLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        return view
    }
}
