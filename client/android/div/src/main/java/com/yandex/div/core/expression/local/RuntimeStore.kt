package com.yandex.div.core.expression.local

import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.variables.VariableControllerImpl
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.internal.Assert
import com.yandex.div.internal.Log
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase

internal const val ROOT_RUNTIME_PATH = "root_runtime_path"
private const val ERROR_UNKNOWN_RESOLVER =
    "ExpressionResolverImpl didn't call RuntimeStore#putRuntime on create."
private const val ERROR_ROOT_RUNTIME_NOT_SPECIFIED = "Root runtime is not specified."
private const val WARNING_LOCAL_USING_LOCAL_VARIABLES =
    "You are using local variables. Please ensure that all elements that use local variables " +
    "and all of their parents recursively have an 'id' attribute."


internal class RuntimeStore(
    private val evaluator: Evaluator,
    private val errorCollector: ErrorCollector,
) {
    private var warningShown = false
    private val pathToRuntimes = mutableMapOf<String, ExpressionsRuntime?>()
    private val resolverToRuntime = mutableMapOf<ExpressionResolver, ExpressionsRuntime?>()
    private val allRuntimes = mutableSetOf<ExpressionsRuntime>()

    private val onCreateCallback by lazy {
        ExpressionResolverImpl.OnCreateCallback { resolver, variableController ->
            ExpressionsRuntime(resolver, variableController, null, this).also {
                /**
                 * we cannot provide path here, otherwise descendants of ExpressionResolver will
                 * receive the same callback and override runtime for provided path.
                 */
                putRuntime(runtime = it)
                it.updateSubscriptions()
            }
        }
    }

    internal fun showWarningIfNeeded(child: DivBase) {
        if (!warningShown && child.variables != null) {
            warningShown = true
            errorCollector.logWarning(Throwable(WARNING_LOCAL_USING_LOCAL_VARIABLES))
        }
    }

    internal fun getOrCreateRuntime(path: String, parentPath: String?, variables: List<Variable>?) =
         pathToRuntimes[path] ?: getRuntimeOrCreateChild(path, parentPath, variables)

    internal fun getRuntimeWithOrNull(resolver: ExpressionResolver) = resolverToRuntime[resolver]

    internal fun putRuntime(runtime: ExpressionsRuntime, path: String? = null) {
        resolverToRuntime[runtime.expressionResolver] = runtime
        allRuntimes.add(runtime)

        if (path != null) pathToRuntimes[path] = runtime
    }

    internal fun setPathToRuntimeWith(
        path: String,
        parentPath: String?,
        variables: List<Variable>?,
        resolver: ExpressionResolver,
    ) {
        if (resolver == pathToRuntimes[path]?.expressionResolver) return
        val existingRuntime = getRuntimeWithOrNull(resolver) ?: run {
            reportError(ERROR_UNKNOWN_RESOLVER)
            return
        }

        removeRuntimesFor(path)
        getRuntimeOrCreateChild(path, parentPath, variables, existingRuntime)
    }

    internal fun cleanup() {
        warningShown = false
        allRuntimes.forEach { it.cleanup() }
    }

    internal fun updateSubscriptions() =
        pathToRuntimes.values.toSet().forEach { it?.updateSubscriptions() }

    private fun reportError(message: String) {
        Assert.fail(message)
        errorCollector.logError(AssertionError(message))
    }

    private fun createChildRuntime(
        parentRuntime: ExpressionsRuntime,
        path: String,
        variables: List<Variable>
    ): ExpressionsRuntime {
        val localVariableController = VariableControllerImpl(parentRuntime.variableController)
        variables.forEach { localVariableController.declare(it) }

        val evaluationContext = EvaluationContext(
            variableProvider = localVariableController,
            storedValueProvider = evaluator.evaluationContext.storedValueProvider,
            functionProvider = evaluator.evaluationContext.functionProvider,
            warningSender = evaluator.evaluationContext.warningSender
        )

        val resolver = ExpressionResolverImpl(
            variableController = localVariableController,
            evaluator = Evaluator(evaluationContext),
            errorCollector = errorCollector,
            onCreateCallback = onCreateCallback,
        )

        return ExpressionsRuntime(
            resolver, localVariableController, null, parentRuntime.runtimeStore
        ).also {
            putRuntime(it, path)
        }
    }

    private fun removeRuntimesFor(path: String) {
        pathToRuntimes[path]?.let {
            pathToRuntimes.entries.filter { (key, _) -> key.contains(path) }.forEach { (key, value) ->
                pathToRuntimes.remove(key)
                resolverToRuntime.remove(value?.expressionResolver)
            }
        }
    }

    private fun getRuntimeOrCreateChild(
        path: String,
        parentPath: String?,
        variables: List<Variable>?,
        existingRuntime: ExpressionsRuntime? = null,
    ): ExpressionsRuntime? {
        val runtime = (existingRuntime ?: parentPath?.let { pathToRuntimes[it] })
            ?: pathToRuntimes[ROOT_RUNTIME_PATH]
            ?: run {
                reportError(ERROR_ROOT_RUNTIME_NOT_SPECIFIED)
                return null
            }

        return if (variables.isNullOrEmpty()) {
            runtime.also { pathToRuntimes[path] = it }
        } else {
            createChildRuntime(runtime, path, variables)
        }
    }
}
