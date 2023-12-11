package com.yandex.div.core.view2

import com.yandex.div2.DivSightAction

internal data class CompositeLogId(
    val dataTag: String,
    val scopeLogId: String,
    val actionLogId: String
) {

    private val compositeLogId by lazy { formatCompositeLogId() }

    private fun formatCompositeLogId(): String {
        return dataTag + (if (scopeLogId.isNotEmpty()) "#$scopeLogId" else "") + "#$actionLogId"
    }

    override fun toString() = compositeLogId
}

internal fun compositeLogIdOf(scope: Div2View, action: DivSightAction): CompositeLogId {
    return CompositeLogId(dataTag = scope.dataTag.id, scopeLogId = scope.logId, actionLogId = action.logId)
}
