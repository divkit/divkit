package com.yandex.div.core.expression.local

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.ObserverList
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.internal.KAssert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivData

internal const val ERROR_PARENT_RUNTIME_NOT_STORED = "Parent runtime for path '%s' is not stored."
private const val WARNING_LOCAL_USING_LOCAL_VARIABLES =
    "You are using local variables. Please ensure that all elements that use local variables " +
    "and all of their parents recursively have an 'id' attribute."

internal class RuntimeStoreImpl(
    data: DivData,
    private val runtimeProvider: ExpressionsRuntimeProvider,
    private val errorCollector: ErrorCollector,
) : RuntimeStore {
    private var warningShown = false
    private val resolverToRuntime = mutableMapOf<ExpressionResolver, ExpressionsRuntime>()
    private val pathToRuntime = mutableMapOf<String, ExpressionsRuntime>()
    private val allRuntimes = ObserverList<ExpressionsRuntime>()
    private val tree = RuntimeTree()
    private val itemBuilderResolvers = mutableMapOf<String, ExpressionResolver>()

    override val rootRuntime = runtimeProvider.createRootRuntime(data, errorCollector, this).also {
        putRuntime(it, "", null)
    }

    override fun showWarningIfNeeded(child: DivBase) {
        if (!warningShown && child.variables != null) {
            warningShown = true
            errorCollector.logWarning(Throwable(WARNING_LOCAL_USING_LOCAL_VARIABLES))
        }
    }

    /**
     * Returns runtime if it have been stored before, otherwise creates new runtime using
     * @param parentResolver
     */
    override fun getOrCreateRuntime(
        path: String,
        div: Div,
        parentResolver: ExpressionResolver,
    ): ExpressionsRuntime {
        pathToRuntime[path]?.let { return it }

        if (parentResolver !is ExpressionResolverImpl) return rootRuntime

        val parentRuntime = getRuntimeWithOrNull(parentResolver) ?: run {
            reportParentRuntimeError(path)
            return rootRuntime
        }

        if (!div.needLocalRuntime) {
            pathToRuntime[path] = parentRuntime
            return parentRuntime
        }

        return runtimeProvider.createChildRuntime(path, div.value(), parentResolver, errorCollector).also {
            putRuntime(it, path, parentRuntime)
        }
    }

    override fun getRuntimeWithOrNull(resolver: ExpressionResolver) = resolverToRuntime[resolver]

    internal fun putRuntime(
        runtime: ExpressionsRuntime,
        path: String,
        parentRuntime: ExpressionsRuntime?,
    ) {
        pathToRuntime[path] = runtime
        resolverToRuntime[runtime.expressionResolver] = runtime
        allRuntimes.addObserver(runtime)
        tree.storeRuntime(runtime, parentRuntime, path)
        runtime.updateSubscriptions()
    }

    override fun resolveRuntimeWith(
        divView: DivViewFacade?,
        path: String,
        div: Div,
        resolver: ExpressionResolver,
        parentResolver: ExpressionResolver,
    ): ExpressionsRuntime? {
        val runtimeForPath = pathToRuntime[path]
        if (runtimeForPath?.let { resolver == it.expressionResolver || div.needLocalRuntime } == true) {
            return runtimeForPath
        }

        runtimeForPath?.let { tree.removeRuntimeAndCleanup(divView, it, path) }

        if (resolver !is ExpressionResolverImpl) return null

        val parentRuntime = getRuntimeWithOrNull(parentResolver) ?: run {
            reportParentRuntimeError(path)
            return null
        }

        return when {
            div.needLocalRuntime -> {
                runtimeProvider.createChildRuntime(path, div.value(), resolver, errorCollector).also {
                    putRuntime(it, path, parentRuntime)
                }
            }
            resolver != parentResolver -> {
                ExpressionsRuntime(resolver, null).also {
                    putRuntime(it, path, parentRuntime)
                }
            }
            else -> {
                parentRuntime.also {
                    pathToRuntime[path] = parentRuntime
                }
            }
        }
    }

    override fun cleanupRuntimes(divView: DivViewFacade) {
        warningShown = false
        allRuntimes.forEach { it.cleanup(divView) }
    }

    override fun updateSubscriptions() = allRuntimes.forEach { it.updateSubscriptions() }

    override fun clearBindings(divView: DivViewFacade) = allRuntimes.forEach { it.clearBinding(divView) }

    override fun onDetachedFromWindow(divView: DivViewFacade) = allRuntimes.forEach {
        it.onDetachedFromWindow(divView)
    }

    override fun traverseFrom(runtime: ExpressionsRuntime, path: String, callback: (ExpressionsRuntime) -> Unit) {
        tree.invokeRecursively(runtime, path) { node ->
            callback(node.runtime)
        }
    }

    override fun getUniquePathsAndRuntimes() = tree.getPathToRuntimes()

    private fun reportParentRuntimeError(path: String) {
        val message = String.format(ERROR_PARENT_RUNTIME_NOT_STORED, path)
        KAssert.fail { message }
        errorCollector.logError(AssertionError(message))
    }

    override fun getOrPutItemBuilderResolver(
        path: String,
        parentResolver: ExpressionResolver,
        createResolver: () -> ExpressionResolver
    ): ExpressionResolver {
        return itemBuilderResolvers.getOrPut(path) {
            val resolver = createResolver()
            getRuntimeWithOrNull(parentResolver)?.let { resolverToRuntime[resolver] = it }
            resolver
        }
    }

    private val Div.needLocalRuntime get() =
        !value().run { variables.isNullOrEmpty() && variableTriggers.isNullOrEmpty() && functions.isNullOrEmpty() }
}
