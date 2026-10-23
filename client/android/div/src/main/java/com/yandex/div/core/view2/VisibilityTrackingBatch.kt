package com.yandex.div.core.view2

import com.yandex.div.DivDataTag
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div2.DivData

internal enum class VisibilityTrackingOperation {
    LOG_VISIBILITY,
    DISCARD,
    TRACK_CHILDREN,
}

internal class VisibilityTrackingBatch(
    val data: DivData?,
    val tag: DivDataTag,
) {
    var pending = false

    fun matchesData(view: Div2View): Boolean = data === view.divData && tag == view.dataTag
}

internal class WorkerVisibilityTrackingBatch(
    private val view: Div2View,
    private val dispatcher: BindingDispatcher,
    private val scan: () -> Unit,
) {
    private var active: Batch? = null

    fun <T> collect(block: () -> T): T {
        if (active?.isCurrent() == true) return block()
        val previous = active
        val batch = Batch(dispatcher.currentGeneration)
        active = batch
        try {
            val result = block()
            if (active === batch) flush()
            return result
        } finally {
            if (active === batch) active = previous?.takeIf { it.isCurrent() }
        }
    }

    fun tryDeferScan(): Boolean {
        val batch = active?.takeIf { it.isCurrent() } ?: return false
        if (batch.pending?.matchesData() != true) {
            batch.pending = ScanTarget(view.divData, view.dataTag)
        }
        return true
    }

    fun discard() {
        active?.let {
            it.pending = null
        }
    }

    /**
     * Finishes the pending worker scan before invoking or enqueuing a host callback.
     * Callback owners (observers, actions, extensions and custom content) preserve this ordering;
     * ordinary view mutations stay in the batch.
     */
    fun flush() {
        val batch = active ?: return
        val target = batch.pending ?: return
        // Publication queued by the scan also passes this barrier.
        batch.pending = null
        if (!batch.isCurrent() || !target.matchesData()) return
        scan()
    }

    private fun Batch.isCurrent(): Boolean = generation == dispatcher.currentGeneration

    private fun ScanTarget.matchesData(): Boolean = data === view.divData && tag == view.dataTag

    private class Batch(val generation: Int) {
        var pending: ScanTarget? = null
    }

    private class ScanTarget(val data: DivData?, val tag: DivDataTag)
}
