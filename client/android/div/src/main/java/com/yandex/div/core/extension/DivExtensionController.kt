package com.yandex.div.core.extension

import android.view.View
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import javax.inject.Inject

@DivScope
@Mockable
internal class DivExtensionController @Inject constructor(
    private val extensionHandlers: List<DivExtensionHandler>,
) {

    fun preprocessExtensions(div: DivBase, resolver: ExpressionResolver) {
        if (!hasExtensions(div)) {
            return
        }
        extensionHandlers.forEach { handler ->
            if (handler.matches(div)) {
                handler.preprocess(div, resolver)
            }
        }
    }

    fun beforeBindView(divView: Div2View, resolver: ExpressionResolver, view: View, div: DivBase) {
        if (!hasExtensions(div)) {
            return
        }
        extensionHandlers.forEach { handler ->
            if (handler.matches(div)) {
                handler.beforeBindView(divView, resolver, view, div)
            }
        }
    }

    fun bindView(divView: Div2View, resolver: ExpressionResolver, view: View, div: DivBase) {
        if (!hasExtensions(div)) {
            return
        }
        extensionHandlers.forEach { handler ->
            if (handler.matches(div)) {
                handler.bindView(divView, resolver, view, div)
            }
        }
    }

    fun unbindView(divView: Div2View, resolver: ExpressionResolver, view: View, div: DivBase) {
        if (!hasExtensions(div)) {
            return
        }
        extensionHandlers.forEach { handler ->
            if (handler.matches(div)) {
                handler.unbindView(divView, resolver, view, div)
            }
        }
    }

    fun loadMedia(divView: Div2View, resolver: ExpressionResolver, view: View, div: DivBase) {
        if (!hasExtensions(div)) return
        extensionHandlers.forEach { handler ->
            if (handler.matches(div)) {
                handler.loadMedia(divView, resolver, view, div)
            }
        }
    }

    fun releaseMedia(divView: Div2View, resolver: ExpressionResolver, view: View, div: DivBase) {
        if (!hasExtensions(div)) return
        extensionHandlers.forEach { handler ->
            if (handler.matches(div)) {
                handler.releaseMedia(divView, resolver, view, div)
            }
        }
    }

    private fun hasExtensions(div: DivBase): Boolean {
        return !div.extensions.isNullOrEmpty() && extensionHandlers.isNotEmpty()
    }
}
