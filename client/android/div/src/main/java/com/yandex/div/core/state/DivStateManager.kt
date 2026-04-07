package com.yandex.div.core.state

import androidx.annotation.AnyThread
import androidx.collection.ArrayMap
import com.yandex.div.DivDataTag
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivPathUtils.statePath
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.internal.Assert
import com.yandex.div.internal.core.DivTreeVisitor
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.state.DivStateCache
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivState
import java.lang.ref.WeakReference
import javax.inject.Inject

private typealias CardVariables = MutableMap<String, StateVariableHolder>
private typealias VariableUpdater = (String) -> Unit

/**
 * Manipulates application's div data state change and retrieval.
 */
@DivScope
@AnyThread
internal class DivStateManager @Inject constructor(
    private val cache: DivStateCache,
    private val temporaryCache: TemporaryDivStateCache
) {

    private val states = ArrayMap<DivDataTag, DivViewState>()
    private val variables = mutableMapOf<String, CardVariables>()

    fun collectStateVariables(tag: DivDataTag, data: DivData, context: BindingContext) {
        val cardVariables = variables.getOrPut(tag.id) { mutableMapOf() }
        StateVariableCollector(cardVariables).collectStateVariables(data, context)
    }

    fun getState(tag: DivDataTag): DivViewState? = synchronized(states) {
        var state = states[tag]
        if (state == null) {
            state = cache.getRootState(tag.id)?.toLong()?.let { DivViewState(it) }
            states[tag] = state
        }
        return state
    }

    fun getState(
        div: DivState,
        divView: Div2View,
        resolver: ExpressionResolver,
        path: String,
    ): String? {
        val cardId = divView.divTag.id
        return div.stateIdVariable?.let { resolver.getVariable(it)?.getValue()?.toString() }
            ?: temporaryCache.getState(cardId, path)
            ?: cache.getState(cardId, path)
            ?: div.defaultStateId?.evaluate(resolver)
            ?: div.states.firstOrNull()?.stateId
    }

    fun updateState(tag: DivDataTag, stateId: Long, temporary: Boolean) {
        if (DivDataTag.INVALID == tag) return

        synchronized(states) {
            val state = getState(tag)
            states[tag] = state?.let { DivViewState(stateId, it.blockStates) } ?: DivViewState(stateId)
            temporaryCache.putRootState(tag.id, stateId.toString())
            if (!temporary) {
                cache.putRootState(tag.id, stateId.toString())
            }
        }
    }

    fun updateStates(cardId: String, divStatePath: DivStatePath, temporary: Boolean) {
        val path = divStatePath.pathToLastState
        val stateId = divStatePath.lastStateId
        if (path == null || stateId == null) return

        synchronized(states) {
            temporaryCache.putState(cardId, path, stateId)
            if (!temporary) {
                cache.putState(cardId, path, stateId)
            }
            variables[cardId]?.get(path)?.setValue(stateId)
        }
    }

    fun bindVariable(cardId: String, divStatePath: DivStatePath, variableUpdater: VariableUpdater) {
        val cardVariables = variables[cardId] ?: return Assert.fail("State variables weren't collected before binding.")
        cardVariables[divStatePath.statePath]?.variableUpdater = WeakReference(variableUpdater)
    }

    fun reset(tags: List<DivDataTag>) {
        if (tags.isEmpty()) {
            states.clear()
            cache.clear()
            temporaryCache.clear()
            variables.clear()
        } else {
            tags.forEach { tag ->
                states.remove(tag)
                cache.resetCard(tag.id)
                temporaryCache.resetCard(tag.id)
                variables.remove(tag.id)
            }
        }
    }
}

private class StateVariableHolder(val variable: Variable) {

    var variableUpdater: WeakReference<VariableUpdater>? = null

    fun setValue(value: String) {
        variableUpdater?.get()?.invoke(value) ?: variable.set(value)
    }
}

private class StateVariableCollector(
    private val variables: CardVariables
) : DivTreeVisitor<Unit>() {

    fun collectStateVariables(data: DivData, context: BindingContext) = visit(data, context)

    override fun defaultVisit(data: Div, context: BindingContext, path: DivStatePath) = Unit

    override fun visit(data: Div.State, context: BindingContext, path: DivStatePath) {
        val variableName = data.value.stateIdVariable ?: return
        val variable = context.expressionResolver.getVariable(variableName) ?: return
        variables.getOrPut(path.statePath) { StateVariableHolder(variable) }
        super.visit(data, context, path)
    }
}
