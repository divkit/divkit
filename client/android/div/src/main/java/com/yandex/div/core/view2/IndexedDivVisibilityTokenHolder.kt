// Copyright 2026 Yandex LLC. All rights reserved.

package com.yandex.div.core.view2

import android.os.Handler
import androidx.annotation.AnyThread
import com.yandex.div2.DivSightAction
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Indexes actions in maps used as tokens for [Handler.postDelayed].
 * Log IDs are unique among registered tokens.
 * Only this holder mutates registered maps, so individual actions can be cancelled
 * without cancelling the rest of the message.
 */
@AnyThread
internal class IndexedDivVisibilityTokenHolder : DivVisibilityTokenHolder {

    private val actions = ConcurrentHashMap<CompositeLogId, TrackedAction>()

    override fun remove(
        logId: CompositeLogId,
        emptyTokenCallback: (Map<CompositeLogId, DivSightAction>) -> Unit
    ) {
        val action = actions.remove(logId) ?: return
        action.token.remove(logId)
        if (action.remainingActions.decrementAndGet() == 0) {
            emptyTokenCallback(action.token)
        }
    }

    override fun add(logIds: MutableMap<CompositeLogId, DivSightAction>) {
        // Removals can change the token as soon as its first key is published.
        val keys = logIds.keys.toTypedArray()
        val remainingActions = AtomicInteger(keys.size)
        for (logId in keys) {
            actions[logId] = TrackedAction(logId, logIds, remainingActions)
        }
    }

    override fun getLogId(logId: CompositeLogId): CompositeLogId? = actions[logId]?.logId

    private class TrackedAction(
        val logId: CompositeLogId,
        val token: MutableMap<CompositeLogId, DivSightAction>,
        val remainingActions: AtomicInteger
    )
}
