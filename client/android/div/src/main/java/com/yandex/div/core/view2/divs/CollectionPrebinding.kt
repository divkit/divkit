package com.yandex.div.core.view2.divs

import android.view.ViewGroup
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemovePreparedChildren
import com.yandex.div.core.view2.runMainThreadAction
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.core.DivBlock

/** Decorates normal item binding with a bounded cache for the first layout. */
internal class CollectionPrebinding<H : DivCollectionViewHolder>(
    private val binding: CollectionItemBinding<H>,
    private val divView: Div2View,
    private val isAdapterCurrent: () -> Boolean,
    private val itemAt: (Int) -> DivBlock?,
    private val nativeViewTypes: () -> Set<Int>,
    private val onPreparedItemConsumed: (H, DivBlock) -> Unit = { _, _ -> },
) : CollectionItemBinding<H> {
    // Consumed entries keep their bounded type leases until clear, including ordinary recycling.
    private val prepared = mutableMapOf<Int, Entry<H>>()
    private var nextViewType = Int.MIN_VALUE

    override fun viewType(position: Int, originalType: Int): Int {
        val stale = synchronized(this) {
            prepared.values.any { it.viewType == originalType || !isGenerationCurrent(it.generation) }
        }
        if (stale) clear()
        return synchronized(this) { prepared[position]?.viewType } ?: originalType
    }

    fun prepare(parent: ViewGroup, positions: Iterable<Int>) {
        val dispatcher = divView.viewComponent.bindingDispatcher
        val holdersPool = dispatcher.preparedViewHoldersPool
        if (
            // Already visible collections keep normal RecyclerView binding and recycling.
            parent.isAttachedToWindow ||
            // Prepared holders must belong to an async batch so cancellation can release them.
            !holdersPool.isCollecting ||
            // Holder mutations must be collected by this dispatcher on its binding thread.
            !dispatcher.isCollectingMainThreadActions
        ) return
        clear()
        val nativeTypes = nativeViewTypes()
        val generation = dispatcher.currentGeneration
        for (position in positions) {
            if (holdersPool.pendingHolderCount >= MAX_PREPARED_ITEMS || !isGenerationCurrent(generation)) break
            itemAt(position)?.let { block ->
                prepare(parent, position, block, generation, allocateViewType(nativeTypes))
            }
        }
    }

    private fun prepare(parent: ViewGroup, position: Int, block: DivBlock, generation: Int, viewType: Int) {
        val holder = binding.create(parent, viewType)
        val runtime = divView.runtimeStore
        val holdersPool = divView.viewComponent.bindingDispatcher.preparedViewHoldersPool
        lateinit var item: PreparedCollectionItem<H>
        item = PreparedCollectionItem(
            holder = holder,
            block = block,
            isOutdated = {
                // Cleanup or a superseding bind invalidates the whole preparation batch.
                !isGenerationCurrent(generation) ||
                    // A holder must not reuse bindings from a replaced document runtime.
                    runtime !== divView.runtimeStore ||
                    // RecyclerView may replace its adapter before the first layout.
                    !isAdapterCurrent() ||
                    // Updated or moved items need binding against their current model.
                    itemAt(position) !== block
            },
            onBound = { holdersPool.unregister(item) },
            onRelease = {
                synchronized(this) {
                    if (prepared[position]?.item === item) prepared.remove(position)
                }
                holdersPool.unregister(item)
                (holder.itemView as DivViewWrapper).releaseAndRemovePreparedChildren(divView)
            },
        )
        synchronized(this) { prepared[position] = Entry(holder, viewType, generation, item) }
        holdersPool.register(item)
        var scheduled = false
        try {
            binding.bind(holder, block, position)
            divView.runMainThreadAction(item::ready)
            scheduled = true
        } finally {
            if (!scheduled) item.release()
        }
    }

    override fun create(parent: ViewGroup, viewType: Int): H {
        val item = synchronized(this) { prepared.values.firstOrNull { it.viewType == viewType }?.item }
        return item?.onCreateViewHolder() ?: binding.create(parent, viewType)
    }

    override fun bind(holder: H, block: DivBlock, position: Int) {
        val entry = synchronized(this) { prepared[position] }
        if (entry?.item?.onBindViewHolder(holder, block) == true) {
            onPreparedItemConsumed(holder, block)
            return
        }
        val source = synchronized(this) { prepared.values.firstOrNull { it.holder === holder } }
        // A holder reused at another position abandons its original prepared binding.
        if (source !== entry) source?.item?.invalidate()
        binding.bind(holder, block, position)
    }

    override fun clear() {
        val items = synchronized(this) { prepared.values.toList().also { prepared.clear() } }
        try {
            items.forEach { runCatching(it.item::invalidate).onFailure(divView::logError) }
        } finally {
            binding.clear()
        }
    }

    private fun allocateViewType(nativeTypes: Set<Int>): Int = synchronized(this) {
        while (nextViewType == -1 || nextViewType in nativeTypes ||
            prepared.values.any { it.viewType == nextViewType }) {
            nextViewType++
        }
        nextViewType++
        nextViewType - 1
    }

    private fun isGenerationCurrent(generation: Int): Boolean =
        divView.viewComponent.bindingDispatcher.currentGeneration == generation

    private class Entry<H : DivCollectionViewHolder>(
        val holder: H,
        val viewType: Int,
        val generation: Int,
        val item: PreparedCollectionItem<H>,
    )

    private companion object {
        const val MAX_PREPARED_ITEMS = 8
    }
}
