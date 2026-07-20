package com.yandex.div.compose.state

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivState

/**
 * Resolves the active [DivState.State] using the same priority as [com.yandex.div.compose.views.state.DivStateView]:
 * 1. [DivState.stateIdVariable]
 * 2. value stored in [DivStateStorage]
 * 3. [DivState.defaultStateId]
 * 4. first item in [DivState.states]
 */
internal fun DivState.resolveActiveState(
    resolver: ExpressionResolver,
    stateStorage: DivStateStorage,
    parentPath: DivStatePath,
): DivState.State? = findState(resolveActiveStateId(resolver, stateStorage, parentPath))

internal fun DivState.resolveActiveStateId(
    resolver: ExpressionResolver,
    stateStorage: DivStateStorage,
    parentPath: DivStatePath,
): String? {
    return stateIdVariable
        ?.let { resolver.getVariable(it)?.getValue()?.toString() }
        ?: id?.let { stateStorage.getActiveState(parentPath, it) }
        ?: defaultStateId?.evaluate(resolver)
}

internal fun DivState.findState(stateId: String?): DivState.State? {
    return stateId?.let { id -> states.find { it.stateId == id } }
        ?: states.firstOrNull()
}
