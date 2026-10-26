package com.yandex.div.core.extension

import android.view.View
import com.yandex.div.R
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.actions.actionTargetBlockLocator
import com.yandex.div.core.actions.logActionError
import com.yandex.div.core.actions.logWarning
import com.yandex.div.core.expression.evaluation.DictEvaluator
import com.yandex.div.core.expression.suppressExpressionErrors
import com.yandex.div.core.util.binding.BindingTaskType
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.runMainThreadAction
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.util.UiThreadHandler.Companion.executeOnMainThreadBlocking
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.isConstant
import com.yandex.div2.DivBase
import javax.inject.Inject
import javax.inject.Named

@DivScope
internal class DivExtensionController @Inject constructor(
    private val extensionHandlers: List<DivExtensionHandler>,
    @Named(Names.WILDCARD_EXTENSION_HANDLERS)
    private val wildcardExtensionHandlers: List<DivExtensionHandler>,
) {

    private val allExtensionHandlers = extensionHandlers + wildcardExtensionHandlers

    fun preprocessExtensions(
        divBlock: DivBlock,
        downloadCallback: DivPreloader.DownloadCallback,
    ) {
        val handlers = matchingHandlers(divBlock, hasExtensions(divBlock))
        onExtensionHandlers(divBlock, handlers) { div, resolver ->
            preprocess(div, resolver, downloadCallback)
        }
    }

    fun beforeBindView(view: View, divBlock: DivBlock, divView: Div2View) {
        val generation = divView.viewComponent.bindingDispatcher.currentGeneration
        unbindView(view, divView)
        if (beforeBindView(view, divBlock, divView, generation, wildcard = false)) {
            beforeBindView(view, divBlock, divView, generation, wildcard = true)
        }
    }

    private fun beforeBindView(
        view: View,
        divBlock: DivBlock,
        divView: Div2View,
        generation: Int,
        wildcard: Boolean,
    ): Boolean {
        if (divView.viewComponent.bindingDispatcher.currentGeneration != generation) return false
        val handlers = bindingHandlers(divBlock, wildcard)
        if (handlers.isEmpty()) return true
        if (!isEnabled(divBlock, wildcard)) {
            if (divBlock is DivBlock.Custom) {
                createBinding(view, divBlock, divView, handlers, wildcard)
            }
            return true
        }
        val binding = createBinding(view, divBlock, divView, handlers, wildcard)
        binding.handlers.forEachIndexed { index, handler ->
            if (!isCurrent(divView, view, binding, generation)) return false
            binding.enteredHandlerCount = index + 1
            divView.flushPendingVisibilityTracking()
            handler.beforeBindView(divView, divBlock.expressionResolver, view, divBlock.div.value())
        }
        return isCurrent(divView, view, binding, generation)
    }

    fun bindView(view: View, divBlock: DivBlock, divView: Div2View) {
        val hasExtensions = hasExtensions(divBlock)
        val hasWildcards = wildcardExtensionHandlers.isNotEmpty()
        if (!hasExtensions && !hasWildcards) {
            return
        }
        if (hasExtensions) observeIsEnabled(view, divBlock, divView)
        applyExtensions(view, divBlock, divView, declared = hasExtensions, wildcard = hasWildcards)
    }

    fun bindCustomView(view: View, preparedView: View, divBlock: DivBlock.Custom, divView: Div2View) {
        val declared = moveBinding(view, preparedView, divBlock, divView, wildcard = false)
        val wildcard = moveBinding(view, preparedView, divBlock, divView, wildcard = true)
        if (declared || wildcard) bindView(view, divBlock, divView)
    }

    private fun moveBinding(
        view: View,
        preparedView: View,
        divBlock: DivBlock.Custom,
        divView: Div2View,
        wildcard: Boolean,
    ): Boolean {
        val bindings = getBindings(divView, wildcard) ?: return false
        val binding = bindings[preparedView] ?: return false
        val generation = divView.viewComponent.bindingDispatcher.currentGeneration
        if (binding.divBlock !== divBlock || binding.releasing) return false
        if (binding.generation != generation) return false
        bindings.remove(preparedView)
        bindings[view] = binding
        return true
    }

    private fun observeIsEnabled(view: View, divBlock: DivBlock, divView: Div2View) {
        val subscriber = view.expressionSubscriber
        divBlock.div.value().extensions?.forEach { extension ->
            if (extension.isEnabled.isConstant()) {
                return@forEach
            }
            subscriber.addSubscription(
                extension.isEnabled.observe(divBlock.expressionResolver) {
                    applyExtensions(view, divBlock, divView, declared = true, wildcard = false)
                }
            )
        }
    }

    private fun applyExtensions(
        view: View,
        divBlock: DivBlock,
        divView: Div2View,
        declared: Boolean,
        wildcard: Boolean,
    ) {
        val dispatcher = divView.viewComponent.bindingDispatcher
        val generation = dispatcher.currentGeneration
        val bindings = buildList {
            if (declared) add(prepareBinding(view, divBlock, divView, generation, wildcard = false))
            if (wildcard) add(prepareBinding(view, divBlock, divView, generation, wildcard = true))
        }.filterNotNull().map { it to ++it.request }
        if (bindings.isEmpty()) return
        val bind = {
            bindings.forEach { (binding, request) ->
                bindExtensionHandlers(divView, view, binding, request, generation)
            }
        }
        if (dispatcher.isCollectingMainThreadActions) {
            divView.flushPendingVisibilityTracking()
            divView.runMainThreadAction(BindingTaskType.DATA) {
                suppressExpressionErrors { bind() }
            }
        } else {
            executeOnMainThreadBlocking { bind() }
        }
    }

    private fun prepareBinding(
        view: View,
        divBlock: DivBlock,
        divView: Div2View,
        generation: Int,
        wildcard: Boolean,
    ): Binding? {
        val handlers = bindingHandlers(divBlock, wildcard)
        if (handlers.isEmpty() || !isEnabled(divBlock, wildcard)) {
            unbindGroup(view, divView, wildcard)
            return null
        }
        val previous = getBindings(divView, wildcard)?.get(view)
        if (previous?.divBlock === divBlock && !previous.releasing) return previous
        unbindGroup(view, divView, wildcard)
        if (divView.viewComponent.bindingDispatcher.currentGeneration != generation) return null
        return createBinding(view, divBlock, divView, handlers, wildcard)
    }

    private fun bindExtensionHandlers(
        divView: Div2View,
        view: View,
        binding: Binding,
        request: Int,
        generation: Int,
    ) {
        val divBlock = binding.divBlock
        var index = 0
        while (index < binding.handlers.size && !shouldStopBinding(divView, view, binding, request, generation)) {
            val handler = binding.handlers[index]
            binding.enteredHandlerCount = maxOf(binding.enteredHandlerCount, index + 1)
            handler.bindView(divView, divBlock.expressionResolver, view, divBlock.div.value())
            // bindView may synchronously rebind or unbind the view and change this binding.
            if (!shouldStopBinding(divView, view, binding, request, generation)) {
                val actionHandler = handler.actionHandler
                if (actionHandler != null && handler !in binding.boundActionStateHandlers) {
                    binding.boundActionStateHandlers[handler] = actionHandler
                    actionHandler.onViewBind(divView, divBlock.path, view)
                }
            }
            index++
        }
    }

    fun unbindView(view: View, divView: Div2View, onError: ((Throwable) -> Unit)? = null) {
        val declared = getBindings(divView, wildcard = false)?.get(view)
        val wildcard = getBindings(divView, wildcard = true)?.get(view)
        declared?.let { unbindBinding(view, divView, it, onError) }
        wildcard?.let { unbindBinding(view, divView, it, onError) }
    }

    private fun unbindGroup(view: View, divView: Div2View, wildcard: Boolean) {
        val binding = getBindings(divView, wildcard)?.get(view) ?: return
        unbindBinding(view, divView, binding, onError = null)
    }

    private fun unbindBinding(
        view: View,
        divView: Div2View,
        binding: Binding,
        onError: ((Throwable) -> Unit)?,
    ) {
        val bindings = getBindings(divView, binding.wildcard) ?: return
        binding.releasing = true
        if (binding.enteredHandlerCount == 0) {
            if (bindings[view] === binding) bindings.remove(view)
            return
        }
        divView.flushPendingVisibilityTracking()
        executeOnMainThreadBlocking {
            val divBlock = binding.divBlock
            try {
                while (binding.releasedHandlerCount < binding.enteredHandlerCount) {
                    val handler = binding.handlers[binding.releasedHandlerCount++]
                    performUnbind(onError) {
                        binding.boundActionStateHandlers.remove(handler)
                            ?.onViewUnbind(divView, divBlock.path, view)
                    }
                    performUnbind(onError) {
                        handler.unbindView(divView, divBlock.expressionResolver, view, divBlock.div.value())
                    }
                }
            } finally {
                if (binding.releasedHandlerCount == binding.enteredHandlerCount && bindings[view] === binding) {
                    bindings.remove(view)
                }
            }
        }
    }

    private inline fun performUnbind(noinline onError: ((Throwable) -> Unit)?, action: () -> Unit) {
        if (onError == null) {
            action()
        } else {
            runCatching(action).onFailure(onError)
        }
    }

    fun releaseBindings(divView: Div2View) {
        val views = getBindings(divView, wildcard = false)?.keys.orEmpty() +
            getBindings(divView, wildcard = true)?.keys.orEmpty()
        views.forEach { unbindView(it, divView) }
    }

    private fun isCurrent(
        divView: Div2View,
        view: View,
        binding: Binding,
        generation: Int,
    ): Boolean {
        val bindings = getBindings(divView, binding.wildcard)
        return bindings?.get(view) === binding && !binding.releasing &&
            divView.viewComponent.bindingDispatcher.currentGeneration == generation
    }

    private fun shouldStopBinding(
        divView: Div2View,
        view: View,
        binding: Binding,
        request: Int,
        generation: Int,
    ): Boolean {
        return !isCurrent(divView, view, binding, generation) || binding.request != request
    }

    private fun createBinding(
        view: View,
        divBlock: DivBlock,
        divView: Div2View,
        handlers: List<DivExtensionHandler>,
        wildcard: Boolean,
    ): Binding {
        val bindings = getBindings(divView, wildcard) ?: LinkedHashMap<View, Binding>().also {
            divView.setTag(bindingsTag(wildcard), it)
        }
        return Binding(
            divBlock,
            handlers,
            divView.viewComponent.bindingDispatcher.currentGeneration,
            wildcard,
        ).also { bindings[view] = it }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getBindings(divView: Div2View, wildcard: Boolean): MutableMap<View, Binding>? =
        divView.getTag(bindingsTag(wildcard)) as? MutableMap<View, Binding>

    private fun bindingsTag(wildcard: Boolean): Int =
        if (wildcard) R.id.div_wildcard_extension_bindings else R.id.div_extension_bindings

    private class Binding(
        val divBlock: DivBlock,
        val handlers: List<DivExtensionHandler>,
        val generation: Int,
        val wildcard: Boolean,
    ) {
        var enteredHandlerCount = 0
        var releasedHandlerCount = 0
        var request = 0
        var releasing = false
        val boundActionStateHandlers = mutableMapOf<DivExtensionHandler, DivExtensionActionHandler>()
    }

    fun handleAction(
        divView: Div2View,
        extensionId: String,
        divId: String,
        scopeId: String?,
        payload: DictEvaluator?,
        resolver: ExpressionResolver,
    ): Boolean {
        val target = divView.actionTargetBlockLocator()?.findTarget(
            divId = divId,
            scopeId = scopeId,
            actionResolver = resolver,
            matches = { block ->
                block.div.value().extensions.orEmpty().any { it.id == extensionId }
            },
            reportWarning = divView::logWarning,
        )?.onFailure {
            divView.logActionError("extension_action", it)
        }?.getOrNull() ?: return false
        return extensionHandlers.asSequence()
            .filter { it.matches(target.div.value()) }
            .mapNotNull { it.actionHandler }
            .any { it.handleAction(divView, target.path, payload, resolver) }
    }

    fun loadMedia(view: View, divBlock: DivBlock, divView: Div2View) {
        val handlers = matchingHandlers(divBlock, hasEnabledExtensions(divBlock))
        onViewExtensionHandlers(divBlock, divView, handlers) { div, resolver ->
            loadMedia(divView, resolver, view, div)
        }
    }

    fun releaseMedia(view: View, divBlock: DivBlock, divView: Div2View) {
        val handlers = matchingHandlers(divBlock, hasExtensions(divBlock))
        onViewExtensionHandlers(divBlock, divView, handlers) { div, resolver ->
            releaseMedia(divView, resolver, view, div)
        }
    }

    private fun matchingHandlers(
        divBlock: DivBlock,
        hasExtensions: Boolean,
    ): List<DivExtensionHandler> {
        val handlers = if (hasExtensions) allExtensionHandlers else wildcardExtensionHandlers
        return handlers.matching(divBlock)
    }

    private fun bindingHandlers(divBlock: DivBlock, wildcard: Boolean): List<DivExtensionHandler> {
        return when {
            wildcard -> wildcardExtensionHandlers.matching(divBlock)
            hasExtensions(divBlock) -> extensionHandlers.matching(divBlock)
            else -> emptyList()
        }
    }

    private fun List<DivExtensionHandler>.matching(divBlock: DivBlock): List<DivExtensionHandler> {
        if (isEmpty()) return emptyList()
        val div = divBlock.div.value()
        return filter { it.matches(div) }
    }

    private fun isEnabled(divBlock: DivBlock, wildcard: Boolean): Boolean =
        wildcard || hasEnabledExtensions(divBlock)

    private fun hasExtensions(divBlock: DivBlock): Boolean {
        return !divBlock.div.value().extensions.isNullOrEmpty() && extensionHandlers.isNotEmpty()
    }

    private fun hasEnabledExtensions(divBlock: DivBlock): Boolean {
        if (extensionHandlers.isEmpty()) {
            return false
        }
        return divBlock.div.value().extensions?.any { it.isEnabled.evaluate(divBlock.expressionResolver) } ?: false
    }

    private fun onViewExtensionHandlers(
        divBlock: DivBlock,
        divView: Div2View,
        handlers: List<DivExtensionHandler>,
        action: DivExtensionHandler.(DivBase, ExpressionResolver) -> Unit,
    ) {
        if (handlers.isEmpty()) return
        divView.flushPendingVisibilityTracking()
        onExtensionHandlers(divBlock, handlers, action)
    }

    private fun onExtensionHandlers(
        divBlock: DivBlock,
        handlers: List<DivExtensionHandler>,
        action: DivExtensionHandler.(DivBase, ExpressionResolver) -> Unit
    ) {
        val div = divBlock.div.value()
        handlers.forEach { it.action(div, divBlock.expressionResolver) }
    }
}
