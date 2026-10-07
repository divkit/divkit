package com.yandex.div.core.view2.divs

/** Owns detached collection holders until their binding actions run or are cancelled. */
internal class PreparedCollectionViewHoldersPool {
    private val pending = mutableMapOf<PreparedCollectionItem<*>, BindingBatch>()
    private var currentBatch: BindingBatch? = null

    val isCollecting: Boolean get() = synchronized(this) { currentBatch != null }
    val pendingHolderCount: Int get() = synchronized(this) { pending.size }

    @Synchronized
    fun register(holder: PreparedCollectionItem<*>) {
        val batch = checkNotNull(currentBatch)
        pending[holder] = batch
        batch.register(holder)
    }

    @Synchronized
    fun unregister(holder: PreparedCollectionItem<*>) {
        pending.remove(holder)?.unregister(holder)
    }

    fun invalidate(onError: (Throwable) -> Unit) {
        val snapshot = synchronized(this) { pending.keys.toList() }
        snapshot.forEach { holder -> runCatching(holder::invalidate).onFailure(onError) }
    }

    fun <T> collect(batch: BindingBatch, block: () -> T): T {
        synchronized(this) {
            check(currentBatch == null) {
                "Cannot collect overlapping binding batches: active=$currentBatch, incoming=$batch"
            }
            currentBatch = batch
        }
        try {
            return block()
        } finally {
            synchronized(this) { currentBatch = null }
        }
    }

    class BindingBatch(
        private val collectionId: Int,
        private val generation: Int,
    ) {
        private val holders = mutableSetOf<PreparedCollectionItem<*>>()

        override fun toString(): String = "BindingBatch@${System.identityHashCode(this)}(" +
            "collectionId=$collectionId, generation=$generation)"

        @Synchronized
        fun register(holder: PreparedCollectionItem<*>) {
            holders.add(holder)
        }

        @Synchronized
        fun unregister(holder: PreparedCollectionItem<*>) {
            holders.remove(holder)
        }

        fun release(onError: (Throwable) -> Unit) {
            val snapshot = synchronized(this) { holders.toList().also { holders.clear() } }
            // A failing host extension must not leave the other prepared players or images alive.
            snapshot.forEach { holder -> runCatching(holder::release).onFailure(onError) }
        }
    }
}
