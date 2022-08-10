package com.yandex.div.core.view2

import com.yandex.div2.DivVisibilityAction

internal class CompositeLogId(
    private val scopeLogId: String,
    private val dataTag: String,
    private val actionLogId: String
) {

    private val compositeLogId by lazy { "$scopeLogId#$dataTag#$actionLogId" }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CompositeLogId

        if (scopeLogId != other.scopeLogId) return false
        if (actionLogId != other.actionLogId) return false
        if (dataTag != other.dataTag) return false

        return true
    }

    override fun hashCode(): Int {
        var result = scopeLogId.hashCode()
        result = 31 * result + actionLogId.hashCode()
        result = 31 * result + dataTag.hashCode()
        return result
    }

    override fun toString() = compositeLogId
}

internal fun compositeLogIdOf(scope: Div2View, action: DivVisibilityAction): CompositeLogId {
    return CompositeLogId(scopeLogId = scope.logId, actionLogId = action.logId, dataTag = scope.dataTag.id)
}
