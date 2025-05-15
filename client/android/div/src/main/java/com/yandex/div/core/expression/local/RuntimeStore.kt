package com.yandex.div.core.expression.local

import com.yandex.div.core.Div2Logger
import com.yandex.div.core.ObserverList
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.triggers.TriggersController
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.expression.variables.VariableControllerImpl
import com.yandex.div.core.expression.variables.toVariable
import com.yandex.div.core.util.toLocalFunctions
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivTrigger

private const val ERROR_UNKNOWN_RESOLVER =
    "ExpressionResolverImpl didn't call RuntimeStore#putRuntime on create."
internal const val ERROR_ROOT_RUNTIME_NOT_SPECIFIED = "Root runtime is not specified."
private const val WARNING_LOCAL_USING_LOCAL_VARIABLES =
    "You are using local variables. Please ensure that all elements that use local variables " +
    "and all of their parents recursively have an 'id' attribute."


internal class RuntimeStore(
    private val evaluator: Evaluator,
    private val errorCollector: ErrorCollector,
    private val div2Logger: Div2Logger,
    private val divActionBinder: DivActionBinder,
) {
    private var warningShown = false
    private val resolverToRuntime = mutableMapOf<ExpressionResolver, ExpressionsRuntime?>()
    private val allRuntimes = ObserverList<ExpressionsRuntime>()
    internal val tree = RuntimeTree()

    internal var rootRuntime: ExpressionsRuntime? = null
        set(value) {
            field = value
            field?.let { putRuntime(it, "", null) }
        }

    private val onCreateCallback by lazy {
        ExpressionResolverImpl.OnCreateCallback { resolver, variableController, functionProvider ->
            ExpressionsRuntime(resolver, variableController, null, functionProvider, this).also {
                /**
                 * we cannot provide path here, otherwise descendants of ExpressionResolver will
                 * receive the same callback and override runtime for provided path.
                 */
                putRuntime(runtime = it)
            }
        }
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
    ): ExpressionsRuntime? {
        path.runtime?.let { return it }

        val parentRuntime = getRuntimeWithOrNull(parentResolver)
        val runtime = parentRuntime ?: rootRuntime ?: run {
            reportError(ERROR_ROOT_RUNTIME_NOT_SPECIFIED)
            return null
        }

        return getRuntimeOrCreateChild(path, div, runtime, parentRuntime)
    }

    /**
     * Returns runtime if it have been stored before, otherwise creates new runtime using
     * @param parentRuntime
     */
    internal fun getOrCreateRuntime(path: String, div: Div, parentRuntime: ExpressionsRuntime) =
        path.runtime ?: getRuntimeOrCreateChild(path, div, parentRuntime, parentRuntime)

    private val String.runtime get() = tree.getNode(this)?.runtime

    internal fun getRuntimeWithOrNull(resolver: ExpressionResolver) = resolverToRuntime[resolver]

    internal fun putRuntime(runtime: ExpressionsRuntime) {
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
        path: String,
        div: Div?,
        resolver: ExpressionResolver,
        parentResolver: ExpressionResolver,
    ): ExpressionsRuntime? {
        val runtimeForPath = tree.getNode(path)?.runtime
        if (resolver == runtimeForPath?.expressionResolver) return runtimeForPath

        val existingRuntime = getRuntimeWithOrNull(resolver) ?: run {
            reportError(ERROR_UNKNOWN_RESOLVER)
            return null
        }

        runtimeForPath?.let { tree.removeRuntimeAndCleanup(it, path) }
        return getRuntimeOrCreateChild(path, div, existingRuntime, getRuntimeWithOrNull(parentResolver))
    }

    internal fun cleanup() {
        warningShown = false
        allRuntimes.forEach { it.cleanup() }
    }

    internal fun updateSubscriptions() = allRuntimes.forEach { it.updateSubscriptions() }

    internal fun clearBindings() = allRuntimes.forEach { it.clearBinding() }

    internal fun getUniquePathsAndRuntimes() = tree.getPathToRuntimes()

    private fun reportError(message: String) {
        Assert.fail(message)
        errorCollector.logError(AssertionError(message))
    }

    private fun createChildRuntime(
        baseRuntime: ExpressionsRuntime,
        parentRuntime: ExpressionsRuntime?,
        path: String,
        div: DivBase,
    ): ExpressionsRuntime {
        val localVariableController = VariableControllerImpl(baseRuntime.variableController)

        val functions = div.functions
        var functionProvider = baseRuntime.functionProvider
        if (!functions.isNullOrEmpty()) {
            functionProvider += functions.toLocalFunctions()
        }

        val evaluationContext = EvaluationContext(
            variableProvider = localVariableController,
            storedValueProvider = evaluator.evaluationContext.storedValueProvider,
            functionProvider = functionProvider,
            warningSender = evaluator.evaluationContext.warningSender
        )

        val evaluator = Evaluator(evaluationContext)
        val resolver = ExpressionResolverImpl(
            path = path + "/" + (baseRuntime.expressionResolver as? ExpressionResolverImpl)?.path,
            runtimeStore = this,
            variableController = localVariableController,
            evaluator = evaluator,
            errorCollector = errorCollector,
            onCreateCallback = onCreateCallback,
        )

        div.variables?.forEach {
            localVariableController.declare(it.toVariable(resolver))
        }

        val triggerController = div.variableTriggers.toTriggersController(localVariableController, resolver, evaluator)

        return ExpressionsRuntime(resolver, localVariableController, triggerController, functionProvider, this).also {
            putRuntime(it, path, parentRuntime)
        }
    }

    private fun List<DivTrigger>?.toTriggersController(
        variableController: VariableController,
        resolver: ExpressionResolver,
        evaluator: Evaluator,
    ): TriggersController? {
        if (isNullOrEmpty()) return null
        val controller =
            TriggersController(variableController, resolver, evaluator, errorCollector, div2Logger, divActionBinder)
        controller.ensureTriggersSynced(this)
        return controller
    }

    private fun getRuntimeOrCreateChild(
        path: String,
        div: Div?,
        runtime: ExpressionsRuntime,
        parentRuntime: ExpressionsRuntime?,
    ): ExpressionsRuntime {
        if (div != null && div.needLocalRuntime) {
            return createChildRuntime(runtime, parentRuntime, path, div.value())
        }

        tree.storeRuntime(runtime, parentRuntime, path)
        runtime.updateSubscriptions()
        return runtime
    }
}
