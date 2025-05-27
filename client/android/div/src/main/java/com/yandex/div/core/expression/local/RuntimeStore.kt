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

private const val ERROR_UNKNOWN_RESOLVER =
    "ExpressionResolverImpl didn't call RuntimeStore#putRuntime on create."
private const val WARNING_LOCAL_USING_LOCAL_VARIABLES =
    "You are using local variables. Please ensure that all elements that use local variables " +
    "and all of their parents recursively have an 'id' attribute."

internal class RuntimeStore(
    data: DivData,
    private val runtimeProvider: ExpressionsRuntimeProvider,
    private val errorCollector: ErrorCollector,
) {
    private var warningShown = false
    private val resolverToRuntime = mutableMapOf<ExpressionResolver, ExpressionsRuntime?>()
    private val allRuntimes = ObserverList<ExpressionsRuntime>()
    internal val tree = RuntimeTree()

    private val onCreateCallback by lazy {
        ExpressionResolverImpl.OnCreateCallback { resolver ->
            ExpressionsRuntime(resolver, null).also {
                /**
                 * we cannot provide path here, otherwise descendants of ExpressionResolver will
                 * receive the same callback and override runtime for provided path.
                 */
                putRuntime(runtime = it)
            }
        }
    }

    val rootRuntime = runtimeProvider.createRootRuntime(data, errorCollector, this, onCreateCallback).also {
        putRuntime(it, "", null)
    }

    internal fun showWarningIfNeeded(child: DivBase) {
        if (!warningShown && child.variables != null) {
            warningShown = true
            errorCollector.logWarning(Throwable(WARNING_LOCAL_USING_LOCAL_VARIABLES))
        }
    }

    /**
     * Returns runtime if it have been stored before, otherwise creates new runtime using
     * @param parentResolver
     */
    internal fun getOrCreateRuntime(
        path: String,
        div: Div,
        parentResolver: ExpressionResolver,
    ): ExpressionsRuntime {
        path.runtime?.let { return it }

        val parentRuntime = getRuntimeWithOrNull(parentResolver)
        val runtime = parentRuntime ?: rootRuntime

        return getRuntimeOrCreateChild(path, div, runtime, parentRuntime)
    }

    private val String.runtime get() = tree.getNode(this)?.runtime

    internal fun getRuntimeWithOrNull(resolver: ExpressionResolver) = resolverToRuntime[resolver]

    private fun putRuntime(runtime: ExpressionsRuntime) {
        resolverToRuntime[runtime.expressionResolver] = runtime
        allRuntimes.addObserver(runtime)
    }

    internal fun putRuntime(
        runtime: ExpressionsRuntime,
        path: String,
        parentRuntime: ExpressionsRuntime?,
    ) {
        putRuntime(runtime)

        tree.storeRuntime(runtime, parentRuntime, path)
        runtime.updateSubscriptions()
    }

    internal fun resolveRuntimeWith(
        divView: DivViewFacade?,
        path: String,
        div: Div?,
        resolver: ExpressionResolver,
        parentResolver: ExpressionResolver,
    ): ExpressionsRuntime? {
        val runtimeForPath = tree.getNode(path)?.runtime
        if (resolver == runtimeForPath?.expressionResolver) return runtimeForPath

        val existingRuntime = getRuntimeWithOrNull(resolver) ?: run {
            reportUnknownResolverError()
            return null
        }

        runtimeForPath?.let { tree.removeRuntimeAndCleanup(divView, it, path) }
        return getRuntimeOrCreateChild(path, div, existingRuntime, getRuntimeWithOrNull(parentResolver))
    }

    internal fun cleanup(divView: DivViewFacade) {
        warningShown = false
        allRuntimes.forEach { it.cleanup(divView) }
    }

    internal fun updateSubscriptions() = allRuntimes.forEach { it.updateSubscriptions() }

    internal fun clearBindings(divView: DivViewFacade) = allRuntimes.forEach { it.clearBinding(divView) }

    internal fun getUniquePathsAndRuntimes() = tree.getPathToRuntimes()

    private fun reportUnknownResolverError() {
        KAssert.fail { ERROR_UNKNOWN_RESOLVER }
        errorCollector.logError(AssertionError(ERROR_UNKNOWN_RESOLVER))
    }

    private fun getRuntimeOrCreateChild(
        path: String,
        div: Div?,
        runtime: ExpressionsRuntime,
        parentRuntime: ExpressionsRuntime?,
    ): ExpressionsRuntime {
        if (div != null && div.needLocalRuntime) {
            return runtimeProvider.createChildRuntime(
                path,
                div.value(),
                runtime.expressionResolver,
                errorCollector,
                onCreateCallback
            ).also { putRuntime(it, path, parentRuntime) }
        }

        tree.storeRuntime(runtime, parentRuntime, path)
        runtime.updateSubscriptions()
        return runtime
    }
}
