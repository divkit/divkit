package com.yandex.div.core.extension

import android.view.View
import com.yandex.div.R
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.suppressExpressionErrors
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.runMainThreadAction
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
        val dispatcher = divView.viewComponent.bindingDispatcher
        val generation = dispatcher.currentGeneration
        unbindView(view, divView)
        if (dispatcher.currentGeneration != generation) return
        if (!hasEnabledExtensions(divBlock)) {
            if (divBlock is DivBlock.Custom && hasExtensions(divBlock)) {
                createBinding(view, divBlock, divView)
            }
            return
        }
        val binding = createBinding(view, divBlock, divView)
        binding.handlers.forEachIndexed { index, handler ->
            if (getBindings(divView)?.get(view) !== binding || binding.releasing ||
                dispatcher.currentGeneration != generation) return
            binding.enteredHandlerCount = index + 1
            handler.beforeBindView(divView, divBlock.expressionResolver, view, divBlock.div.value())
        }
    }

    fun bindView(view: View, divBlock: DivBlock, divView: Div2View) {
        if (!hasExtensions(divBlock)) {
            return
        }
        observeIsEnabled(view, divBlock, divView)
        applyExtensions(view, divBlock, divView)
    }

    fun bindCustomView(view: View, preparedView: View, divBlock: DivBlock.Custom, divView: Div2View) {
        val bindings = getBindings(divView) ?: return
        val binding = bindings[preparedView] ?: return
        if (binding.divBlock !== divBlock || binding.releasing ||
            binding.generation != divView.viewComponent.bindingDispatcher.currentGeneration) return
        bindings.remove(preparedView)
        bindings[view] = binding
        bindView(view, divBlock, divView)
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
        val dispatcher = divView.viewComponent.bindingDispatcher
        val generation = dispatcher.currentGeneration
        if (!hasEnabledExtensions(divBlock)) {
            unbindView(view, divView)
            return
        }
        val previous = getBindings(divView)?.get(view)
        val binding = if (previous?.divBlock === divBlock && !previous.releasing) {
            previous
        } else {
            unbindView(view, divView)
            if (dispatcher.currentGeneration != generation) return
            createBinding(view, divBlock, divView)
        }
        val request = ++binding.request
        val bind = {
            for ((index, handler) in binding.handlers.withIndex()) {
                if (getBindings(divView)?.get(view) !== binding || binding.releasing || binding.request != request ||
                    dispatcher.currentGeneration != generation) {
                    break
                }
                binding.enteredHandlerCount = maxOf(binding.enteredHandlerCount, index + 1)
                handler.bindView(divView, divBlock.expressionResolver, view, divBlock.div.value())
            }
        }
        if (dispatcher.isCollectingMainThreadActions) {
            divView.runMainThreadAction {
                suppressExpressionErrors { bind() }
            }
        } else {
            executeOnMainThreadBlocking { bind() }
        }
    }

    fun unbindView(view: View, divView: Div2View) {
        val bindings = getBindings(divView) ?: return
        val binding = bindings[view] ?: return
        binding.releasing = true
        if (binding.enteredHandlerCount == 0) {
            bindings.remove(view)
            return
        }
        executeOnMainThreadBlocking {
            val divBlock = binding.divBlock
            try {
                while (binding.releasedHandlerCount < binding.enteredHandlerCount) {
                    val handler = binding.handlers[binding.releasedHandlerCount++]
                    handler.unbindView(divView, divBlock.expressionResolver, view, divBlock.div.value())
                }
            } finally {
                if (binding.releasedHandlerCount == binding.enteredHandlerCount && bindings[view] === binding) {
                    bindings.remove(view)
                }
            }
        }
    }

    fun releaseBindings(divView: Div2View) {
        val views = getBindings(divView)?.keys?.toList() ?: return
        views.forEach { unbindView(it, divView) }
    }

    private fun createBinding(view: View, divBlock: DivBlock, divView: Div2View): Binding {
        val bindings = getBindings(divView) ?: LinkedHashMap<View, Binding>().also {
            divView.setTag(R.id.div_extension_bindings, it)
        }
        val div = divBlock.div.value()
        return Binding(
            divBlock,
            extensionHandlers.filter { it.matches(div) },
            divView.viewComponent.bindingDispatcher.currentGeneration,
        ).also { bindings[view] = it }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getBindings(divView: Div2View): MutableMap<View, Binding>? =
        divView.getTag(R.id.div_extension_bindings) as? MutableMap<View, Binding>

    private class Binding(
        val divBlock: DivBlock,
        val handlers: List<DivExtensionHandler>,
        val generation: Int,
    ) {
        var enteredHandlerCount = 0
        var releasedHandlerCount = 0
        var request = 0
        var releasing = false
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
