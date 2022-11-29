package com.yandex.div.core.view2

import android.os.Handler
import androidx.annotation.AnyThread
import com.yandex.div.internal.util.SynchronizedList
import com.yandex.div2.DivVisibilityAction

/**
 * Utility wrapper for MutableList<MutableMap<CompositeLogId, DivVisibilityAction>>.
 * @property tokens is list of MutableMap<CompositeLogId, DivVisibilityAction>, which entries is used as token
 * for [Handler.postDelayed], so amount of entries can be changed without cancelling whole message.
 */
@AnyThread
internal class DivVisibilityTokenHolder {

    private val tokens = SynchronizedList<MutableMap<CompositeLogId, DivVisibilityAction>>()

    fun remove(logId: CompositeLogId,
               emptyTokenCallback: (Map<CompositeLogId, DivVisibilityAction>) -> Unit) {
        val log = tokens.find {
            return@find it.remove(logId) != null
        } ?: return
        if (log.isEmpty()) {
            emptyTokenCallback(log)
            tokens.remove(log)
        }
    }

    fun add(logIds: MutableMap<CompositeLogId, DivVisibilityAction>) = tokens.add(logIds)

    fun getLogId(logId: CompositeLogId): CompositeLogId? {
        val log = tokens.find { logIds: MutableMap<CompositeLogId, DivVisibilityAction> ->
            logIds.containsKey(logId)
        }
        return log?.keys?.toTypedArray()?.find { it == logId }
    }
}
