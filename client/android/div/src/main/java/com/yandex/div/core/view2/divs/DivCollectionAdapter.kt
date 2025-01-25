package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.downloader.DivPatchApply
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.toDivItemBuilderResult
import com.yandex.div2.DivVisibility

internal abstract class DivCollectionAdapter<VH: RecyclerView.ViewHolder>(
    items: List<DivItemBuilderResult>,
) : VisibilityAwareAdapter<VH>(items) {

    override fun getItemViewType(position: Int): Int {
        val item = visibleItems.getOrNull(position) ?: return 0
        return item.div.value().reuseId?.evaluate(item.expressionResolver).hashCode()
    }

    fun applyPatch(
        recyclerView: RecyclerView?,
        divPatchCache: DivPatchCache,
        bindingContext: BindingContext,
    ): Boolean {
        val patch = divPatchCache.getPatch(bindingContext.divView.dataTag) ?: return false
        val divPatchApply = DivPatchApply(patch)

        val appliedToListPatchIds = mutableSetOf<String>()
        var index = 0

        while (index < items.size) {
            val childItem = items[index]
            val patchId = childItem.div.value().id
            val patchDivs = patchId?.let {
                divPatchCache.getPatchDivListById(bindingContext.divView.dataTag, it)
            }

            if (patchDivs != null) {
                updateItemVisibility(index, DivVisibility.GONE)
                items.removeAt(index)

                val patchItems = patchDivs.toDivItemBuilderResult(bindingContext.expressionResolver)
                items.addAll(index, patchItems)
                patchItems.indices.forEach {
                    updateItemVisibility(index + it)
                }

                index += patchDivs.size - 1
                appliedToListPatchIds.add(patchId)
            }

            index++
        }

        // Apply patch inside items if needed
        patch.patches.keys.filter { it !in appliedToListPatchIds }.forEach { idToFind ->
            for (i in 0 until items.size) {
                val childDiv = items[i].div
                divPatchApply.patchDivChild(
                    parentView = recyclerView ?: bindingContext.divView,
                    childDiv,
                    idToFind,
                    bindingContext.expressionResolver
                )?.let { newDiv ->
                    updateItemVisibility(i, DivVisibility.GONE)
                    items[i] = DivItemBuilderResult(newDiv, bindingContext.expressionResolver)
                    updateItemVisibility(i)
                    return@forEach
                }
            }
        }

        if (appliedToListPatchIds.isEmpty()) return false

        subscribeOnElements()
        return true
    }

    open fun setItems(newItems: List<DivItemBuilderResult>) {
        val diffUtilCallback = DiffUtilCallback(items, newItems)
        val updateCallback = UpdateCallBack(newItems)
        DiffUtil.calculateDiff(diffUtilCallback).dispatchUpdatesTo(updateCallback)
        subscribeOnElements()
    }

    private class DiffUtilCallback(
        private val oldItems: List<DivItemBuilderResult>,
        private val newItems: List<DivItemBuilderResult>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldItems.size

        override fun getNewListSize() = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems.getOrNull(oldItemPosition)
            val newItem = newItems.getOrNull(newItemPosition) ?: return oldItem == null
            oldItem ?: return false

            oldItem.suppressMissingVariableException(true)
            newItem.suppressMissingVariableException(true)
            return oldItem.div.equals(newItem.div, oldItem.expressionResolver, newItem.expressionResolver).also {
                oldItem.suppressMissingVariableException(false)
                newItem.suppressMissingVariableException(false)
            }
        }

        private fun DivItemBuilderResult.suppressMissingVariableException(suppress: Boolean) {
            (expressionResolver as? ExpressionResolverImpl)?.suppressMissingVariableException = suppress
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
    }

    private inner class UpdateCallBack(private val newItems: List<DivItemBuilderResult>) : ListUpdateCallback {

        override fun onInserted(position: Int, count: Int) {
            val newItemPosition = if (position + count > newItems.size) newItems.size - count else position
            for (i in 0 until count) {
                items.add(position + i, newItems[newItemPosition + i])
                updateItemVisibility(position + i)
            }
        }

        override fun onRemoved(position: Int, count: Int) {
            for (i in 0 until count) {
                updateItemVisibility(position, DivVisibility.GONE)
                items.removeAt(position)
            }
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            onRemoved(fromPosition, 1)
            onInserted(toPosition, 1)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) = Unit
    }
}
