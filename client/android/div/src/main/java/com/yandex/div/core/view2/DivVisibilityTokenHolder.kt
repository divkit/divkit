package com.yandex.div.core.view2

import android.os.Handler
import com.yandex.div2.DivVisibilityAction

/**
 * Utility wrapper for MutableList<MutableMap<CompositeLogId, DivVisibilityAction>>.
 * @property tokens is list of MutableMap<CompositeLogId, DivVisibilityAction>, which entries is used as token
 * for [Handler.postDelayed], so amount of entries can be changed without cancelling whole message.
 */
internal class DivVisibilityTokenHolder {

    private val tokens: MutableList<MutableMap<CompositeLogId, DivVisibilityAction>> = mutableListOf()

    fun remove(logId: CompositeLogId) {
        val log = tokens.find {
            return@find it.remove(logId) != null
        } ?: return
        if (log.isEmpty()) {
            tokens.remove(log)
        }
    }

    fun remove(logIds: MutableMap<CompositeLogId, DivVisibilityAction>?) = tokens.remove(logIds)

    fun add(logIds: MutableMap<CompositeLogId, DivVisibilityAction>) = tokens.add(logIds)

    fun getTokenByLogId(logId: CompositeLogId) = tokens.find { it.contains(logId) }

    fun getLogId(logId: CompositeLogId): CompositeLogId? {
        tokens.forEach { logIds: MutableMap<CompositeLogId, DivVisibilityAction> ->
            return logIds.keys.getOrNull(logId) ?: return@forEach
        }
        return null
    }
}

private fun MutableSet<CompositeLogId>.getOrNull(logId: CompositeLogId): CompositeLogId? {
    val index = indexOf(logId)
    if (index >= 0) {
        return elementAt(index)
    }
    return null
}
