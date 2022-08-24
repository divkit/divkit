package com.yandex.div.core.view2

import android.view.View
import android.widget.FrameLayout
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.suppressExpressionErrors
import com.yandex.div.core.state.DivStatePath
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

    fun buildView(data: Div, divView: Div2View, path: DivStatePath): View {
        val view = createView(data, divView, path)

        suppressExpressionErrors {
            viewBinder.bind(view, data, divView, path)
        }

        return view
    }

    fun createView(data: Div, divView: Div2View, path: DivStatePath): View {
        val resolver = divView.expressionResolver
        val view = viewCreator.create(data, resolver).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }

        return view
    }
}
