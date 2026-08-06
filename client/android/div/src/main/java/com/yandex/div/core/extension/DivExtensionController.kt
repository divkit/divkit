package com.yandex.div.core.extension

import android.view.View
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.util.UiThreadHandler.Companion.executeOnMainThreadBlocking
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.isConstant
import com.yandex.div2.DivBase
import javax.inject.Inject

@DivScope
internal class DivExtensionController @Inject constructor(
    private val extensionHandlers: List<DivExtensionHandler>,
) {

    fun preprocessExtensions(
        divBlock: DivBlock,
        downloadCallback: DivPreloader.DownloadCallback,
    ) {
        if (!hasExtensions(divBlock)) {
            return
        }
        onExtensionHandlers(divBlock) { div, resolver ->
            preprocess(div, resolver, downloadCallback)
        }
    }

    fun beforeBindView(view: View, divBlock: DivBlock, divView: Div2View) {
        if (!hasEnabledExtensions(divBlock)) {
            return
        }
        onExtensionHandlers(divBlock) { div, resolver ->
            beforeBindView(divView, resolver, view, div)
        }
    }

    fun bindView(view: View, divBlock: DivBlock, divView: Div2View) {
        if (!hasExtensions(divBlock)) {
            return
        }
        observeIsEnabled(view, divBlock, divView)
        applyExtensions(view, divBlock, divView)
    }

    private fun observeIsEnabled(view: View, divBlock: DivBlock, divView: Div2View) {
        val subscriber = view.expressionSubscriber
        divBlock.div.value().extensions?.forEach { extension ->
            if (extension.isEnabled.isConstant()) {
                return@forEach
            }
            subscriber.addSubscription(
                extension.isEnabled.observe(divBlock.expressionResolver) {
                    applyExtensions(view, divBlock, divView)
                }
            )
        }
    }

    private fun applyExtensions(view: View, divBlock: DivBlock, divView: Div2View) {
        val enabled = hasEnabledExtensions(divBlock)
        onExtensionHandlers(divBlock) { div, resolver ->
            executeOnMainThreadBlocking {
                if (enabled) {
                    bindView(divView, resolver, view, div)
                } else {
                    unbindView(divView, resolver, view, div)
                }
            }
        }
    }

    fun unbindView(view: View, divBlock: DivBlock, divView: Div2View) {
        if (!hasExtensions(divBlock)) {
            return
        }
        onExtensionHandlers(divBlock) { div, resolver ->
            executeOnMainThreadBlocking {
                unbindView(divView, resolver, view, div)
            }
        }
    }

    fun loadMedia(view: View, divBlock: DivBlock, divView: Div2View) {
        if (!hasEnabledExtensions(divBlock)) return
        onExtensionHandlers(divBlock) { div, resolver ->
            loadMedia(divView, resolver, view, div)
        }
    }

    fun releaseMedia(view: View, divBlock: DivBlock, divView: Div2View) {
        if (!hasExtensions(divBlock)) return
        onExtensionHandlers(divBlock) { div, resolver ->
            releaseMedia(divView, resolver, view, div)
        }
    }

    private fun hasExtensions(divBlock: DivBlock): Boolean {
        return !divBlock.div.value().extensions.isNullOrEmpty() && extensionHandlers.isNotEmpty()
    }

    private fun hasEnabledExtensions(divBlock: DivBlock): Boolean {
        if (extensionHandlers.isEmpty()) {
            return false
        }
        return divBlock.div.value().extensions?.any { it.isEnabled.evaluate(divBlock.expressionResolver) } ?: false
    }

    private fun onExtensionHandlers(
        divBlock: DivBlock,
        action: DivExtensionHandler.(DivBase, ExpressionResolver) -> Unit
    ) {
        val div = divBlock.div.value()
        extensionHandlers.forEach { handler ->
            if (handler.matches(div)) {
                handler.action(div, divBlock.expressionResolver)
            }
        }
    }
}
