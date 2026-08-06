package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.yandex.div.core.expression.asImpl
import com.yandex.div.core.state.DivPathUtils.getItemIds
import com.yandex.div.internal.core.DivBlock

internal abstract class DivCollectionAdapter<VH: DivCollectionViewHolder>(
    items: List<DivBlock>,
) : VisibilityAwareAdapter<VH>(items) {

    private var ids = items.getItemIds()

    override fun getItemViewType(position: Int): Int {
        val item = visibleItems.getOrNull(position) ?: return 0
        return item.div.value().reuseId?.evaluate(item.expressionResolver).hashCode()
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = visibleItems[position]
        holder.bind(item, position)
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        holder.updateState()
    }

    open fun setItems(newItems: List<DivBlock>) {
        val diffUtilCallback = DiffUtilCallback(items, newItems)
        val updateCallback = UpdateCallBack(newItems)
        DiffUtil.calculateDiff(diffUtilCallback).dispatchUpdatesTo(updateCallback)
        updateIds()
        subscribeOnElements()
    }

    private fun updateIds() {
        ids = items.getItemIds()
    }

    private class DiffUtilCallback(
        private val oldItems: List<DivBlock>,
        private val newItems: List<DivBlock>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldItems.size

        override fun getNewListSize() = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems.getOrNull(oldItemPosition)
            val newItem = newItems.getOrNull(newItemPosition)

            val oldReuseId = oldItem?.div?.value()?.reuseId?.evaluate(oldItem.expressionResolver)
            val newReuseId = newItem?.div?.value()?.reuseId?.evaluate(newItem.expressionResolver)

            return if (oldReuseId != null || newReuseId != null) {
                oldReuseId == newReuseId
            } else {
                areContentsTheSame(oldItem, newItem)
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            areContentsTheSame(oldItems.getOrNull(oldItemPosition), newItems.getOrNull(newItemPosition))

        private fun areContentsTheSame(oldItem: DivBlock?, newItem: DivBlock?): Boolean {
            if (oldItem == null || newItem == null) {
                return oldItem == newItem
            }

            oldItem.suppressMissingVariableException(true)
            newItem.suppressMissingVariableException(true)
            return oldItem.div.equals(newItem.div, oldItem.expressionResolver, newItem.expressionResolver).also {
                oldItem.suppressMissingVariableException(false)
                newItem.suppressMissingVariableException(false)
            }
        }

        private fun DivBlock.suppressMissingVariableException(suppress: Boolean) {
            expressionResolver.asImpl?.suppressMissingVariableException = suppress
        }
    }

    private inner class UpdateCallBack(private val newItems: List<DivBlock>) : ListUpdateCallback {

        override fun onInserted(position: Int, count: Int) {
            val newItemPosition = if (position + count > newItems.size) newItems.size - count else position
            addItems(position, newItems.subList(newItemPosition, newItemPosition + count))
        }

        override fun onRemoved(position: Int, count: Int) {
            repeat(count) {
                removeItem(position)
            }
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            onRemoved(fromPosition, 1)
            onInserted(toPosition, 1)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) = Unit
    }
}
