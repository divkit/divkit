// Copyright 2026 Yandex LLC. All rights reserved.

package com.yandex.div.core.view2

import androidx.annotation.AnyThread
import com.yandex.div2.DivSightAction
import java.util.concurrent.ConcurrentLinkedQueue

@AnyThread
internal class LinearDivVisibilityTokenHolder : DivVisibilityTokenHolder {
    private val tokens = ConcurrentLinkedQueue<MutableMap<CompositeLogId, DivSightAction>>()

    override fun remove(
        logId: CompositeLogId,
        emptyTokenCallback: (Map<CompositeLogId, DivSightAction>) -> Unit
    ) {
        val log = tokens.find {
            return@find it.remove(logId) != null
        } ?: return
        if (log.isEmpty()) {
            emptyTokenCallback(log)
            tokens.remove(log)
        }
    }

    override fun add(logIds: MutableMap<CompositeLogId, DivSightAction>) {
        tokens.add(logIds)
    }

    override fun getLogId(logId: CompositeLogId): CompositeLogId? {
        val log = tokens.find { logIds: MutableMap<CompositeLogId, DivSightAction> ->
            logIds.containsKey(logId)
        }
        return log?.keys?.toTypedArray()?.find { it == logId }
    }
}
