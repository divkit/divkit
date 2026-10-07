package com.yandex.div.core.view2.divs

import com.yandex.div.core.view2.Releasable
import com.yandex.div.internal.core.DivBlock

/** An unfinished holder must outlive its queued main actions, even after invalidation. */
internal class PreparedCollectionItem<H : DivCollectionViewHolder>(
    private val holder: H,
    private val block: DivBlock,
    private val isOutdated: () -> Boolean,
    private val onBound: () -> Unit,
    private val onRelease: () -> Unit,
) : Releasable {
    private enum class State { PREPARING, INVALIDATED, READY, HANDED_OUT, CONSUMED, RELEASED }

    private var state = State.PREPARING

    fun ready() {
        val release = synchronized(this) {
            if (state == State.RELEASED) return
            if (state == State.INVALIDATED || isOutdated()) {
                state = State.RELEASED
                true
            } else {
                check(state == State.PREPARING)
                state = State.READY
                false
            }
        }
        if (release) onRelease()
    }

    /** Returns the prepared holder once; failed requests discard it automatically. */
    fun onCreateViewHolder(): H? {
        val result = synchronized(this) {
            if (state == State.READY && !isOutdated()) {
                state = State.HANDED_OUT
                holder
            } else {
                null
            }
        }
        if (result == null) invalidate()
        return result
    }

    /** Transfers a matching binding to RecyclerView; failed requests discard it automatically. */
    fun onBindViewHolder(candidate: H, currentBlock: DivBlock): Boolean {
        val matchesPreparedItem = candidate === holder && currentBlock === block
        val bound = synchronized(this) {
            if (state == State.HANDED_OUT && matchesPreparedItem && !isOutdated()) {
                state = State.CONSUMED
                true
            } else {
                false
            }
        }
        if (bound) onBound() else invalidate()
        return bound
    }

    fun invalidate() {
        val release = synchronized(this) {
            when (state) {
                State.PREPARING -> {
                    state = State.INVALIDATED
                    false
                }
                State.READY, State.HANDED_OUT -> {
                    state = State.RELEASED
                    true
                }
                State.INVALIDATED, State.CONSUMED, State.RELEASED -> false
            }
        }
        if (release) onRelease()
    }

    override fun release() {
        synchronized(this) {
            if (state == State.RELEASED || state == State.CONSUMED) return
            state = State.RELEASED
        }
        onRelease()
    }
}
