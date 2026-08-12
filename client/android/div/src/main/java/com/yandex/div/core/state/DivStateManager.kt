package com.yandex.div.core.state

import androidx.annotation.AnyThread
import com.yandex.div.core.dagger.DivDataScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.state.DivPathUtils.statePath
import com.yandex.div.data.Variable
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.DivTreeVisitor
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.state.DivStateCache
import com.yandex.div2.DivData
import com.yandex.div2.DivState
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Named

private typealias VariableUpdater = (String) -> Unit

/**
 * Manipulates application's div data state change and retrieval.
 */
@AnyThread
@DivDataScope
internal class DivStateManager @Inject constructor(
    @param:Named(Names.DATA_TAG) private val dataTag: String,
    private val cache: DivStateCache,
) {

    private var _state: DivViewState? = null
    private val temporaryCache = mutableMapOf<String, String>()
    private val variables = mutableMapOf<String, StateVariableHolder>()

    fun collectStateVariables(data: DivData, resolver: ExpressionResolver) {
        StateVariableCollector(variables).collectStateVariables(data, resolver)
    }

    val state: DivViewState?
        get() {
            return _state
                ?: cache.getRootState(dataTag)?.toLong()?.let { DivViewState(it) }
                    .also { _state = it }
        }

    fun getState(path: String) = temporaryCache[path]

    fun getState(
        div: DivState,
        resolver: ExpressionResolver,
        path: String,
    ): String? {
        return div.stateIdVariable?.let { resolver.getVariable(it)?.getValue()?.toString() }
            ?: temporaryCache[path]
            ?: cache.getState(dataTag, path)
            ?: div.defaultStateId?.evaluate(resolver)
            ?: div.states.firstOrNull()?.stateId
    }

    fun updateState(stateId: Long, temporary: Boolean) {
        _state = state?.let { DivViewState(stateId, it.blockStates) } ?: DivViewState(stateId)
        temporaryCache["/"] = stateId.toString()
        if (!temporary) {
            cache.putRootState(dataTag, stateId.toString())
        }
    }

    fun updateStates(divStatePath: DivStatePath, temporary: Boolean) {
        val path = divStatePath.pathToLastState ?: return
        val stateId = divStatePath.lastStateId ?: return

        temporaryCache[path] = stateId
        if (!temporary) {
            cache.putState(dataTag, path, stateId)
        }
        variables[path]?.setValue(stateId)
    }

    fun bindVariable(divStatePath: DivStatePath, variableUpdater: VariableUpdater) {
        variables[divStatePath.statePath]?.variableUpdater = WeakReference(variableUpdater)
    }

    fun reset() {
        _state = null
        cache.resetCard(dataTag)
        temporaryCache.clear()
        variables.clear()
    }
}

private class StateVariableHolder(val variable: Variable) {

    var variableUpdater: WeakReference<VariableUpdater>? = null

    fun setValue(value: String) {
        variableUpdater?.get()?.invoke(value) ?: variable.set(value)
    }
}

private class StateVariableCollector(
    private val variables: MutableMap<String, StateVariableHolder>
) : DivTreeVisitor<Unit>() {

    fun collectStateVariables(data: DivData, resolver: ExpressionResolver) = visit(data, resolver)

    override fun defaultVisit(divBlock: DivBlock) = Unit

    override fun visitState(block: DivBlock.State) {
        val variableName = block.divValue.stateIdVariable ?: return
        val variable = block.expressionResolver.getVariable(variableName) ?: return
        variables.getOrPut(block.path.statePath) { StateVariableHolder(variable) }
        super.visitState(block)
    }
}
