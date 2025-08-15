package com.yandex.div.core.view2

import android.os.Handler
import androidx.annotation.AnyThread
import com.yandex.div2.DivSightAction
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Utility wrapper for MutableList<MutableMap<CompositeLogId, DivSightAction>>.
 * @property tokens is list of MutableMap<CompositeLogId, DivSightAction>,
 * which entries is used as token for [Handler.postDelayed], so amount of entries can be changed
 * without cancelling whole message.
 */
@AnyThread
internal class DivVisibilityTokenHolder {

    private val tokens = ConcurrentLinkedQueue<MutableMap<CompositeLogId, DivSightAction>>()

    fun remove(logId: CompositeLogId,
               emptyTokenCallback: (Map<CompositeLogId, DivSightAction>) -> Unit) {
        val log = tokens.find {
            return@find it.remove(logId) != null
        } ?: return
        if (log.isEmpty()) {
            emptyTokenCallback(log)
            tokens.remove(log)
        }
    }

    fun add(logIds: MutableMap<CompositeLogId, DivSightAction>) = tokens.add(logIds)

    fun getLogId(logId: CompositeLogId): CompositeLogId? {
        val log = tokens.find { logIds: MutableMap<CompositeLogId, DivSightAction> ->
            logIds.containsKey(logId)
        }
        return log?.keys?.toTypedArray()?.find { it == logId }
    }
}
