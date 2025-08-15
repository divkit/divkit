package com.yandex.div.core.view2

import android.view.View
import android.view.ViewGroup.LayoutParams
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.local.DivRuntimeVisitor
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div2.Div
import javax.inject.Inject

/**
 * Base class for build div view
 */
@DivScope
@Mockable
internal class Div2Builder @Inject constructor(
    private val viewCreator: DivViewCreator,
    private val viewBinder: DivBinder,
    private val runtimeVisitor: DivRuntimeVisitor,
) {

    fun buildView(data: Div, context: BindingContext, path: DivStatePath): View {
        return createView(data, context, path).also {
            viewBinder.bind(context, it, data, path)
        }
    }

    fun createView(data: Div, context: BindingContext, path: DivStatePath): View {
        val resolver = context.expressionResolver

        runtimeVisitor.createAndAttachRuntimes(data, path, context.divView)
        val view = viewCreator.create(data, resolver).apply {
            layoutParams = DivLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        return view
    }
}
