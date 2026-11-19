package com.yandex.div.core.view2

import androidx.annotation.AnyThread
import com.yandex.div2.DivSightAction

@AnyThread
internal interface DivVisibilityTokenHolder {
    fun add(logIds: MutableMap<CompositeLogId, DivSightAction>)

    fun getLogId(logId: CompositeLogId): CompositeLogId?

    fun remove(
        logId: CompositeLogId,
        emptyTokenCallback: (Map<CompositeLogId, DivSightAction>) -> Unit
    )

    companion object {
        fun create(indexEnabled: Boolean): DivVisibilityTokenHolder =
            if (indexEnabled) IndexedDivVisibilityTokenHolder() else LinearDivVisibilityTokenHolder()
    }
}
