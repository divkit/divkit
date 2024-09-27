package com.yandex.div.core.view2

import android.view.View
import android.view.ViewGroup.LayoutParams
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.local.DivRuntimeTree
import com.yandex.div.core.expression.suppressExpressionErrors
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
    private val viewBinder: DivBinder
) {

    fun buildView(data: Div, context: BindingContext, path: DivStatePath): View {
        val view = createView(data, context, path)

        suppressExpressionErrors {
            viewBinder.bind(context, view, data, path)
        }

        return view
    }

    fun createView(data: Div, context: BindingContext, path: DivStatePath): View {
        val resolver = context.expressionResolver

        context.runtimeStore?.let { runtimeStore ->
            DivRuntimeTree(data, path, runtimeStore).createRuntimes()
        }

        val view = viewCreator.create(data, resolver).apply {
            layoutParams = DivLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        return view
    }
}
