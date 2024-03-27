package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.downloader.DivPatchApply
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivVisibility

internal abstract class DivPatchableAdapter<VH : RecyclerView.ViewHolder>(
    divs: List<Div>,
    private val bindingContext: BindingContext,
) : RecyclerView.Adapter<VH>(), ExpressionSubscriber {
    private val _items = divs.toMutableList()
    val items: List<Div>
        get() = _items

    private val _activeItems = mutableListOf<IndexedValue<Div>>()
    val activeItems: List<Div> = _activeItems.dropIndex()

    private val activityMap = mutableMapOf<Div, Boolean>()

    init {
        updateActiveItems()
    }

    private fun updateActiveItems() {
        _activeItems.clear()
        activityMap.clear()

        indexedItems.forEach {
            val isActive = it.value.isActive(bindingContext.expressionResolver)

            activityMap[it.value] = isActive
            if (isActive) {
                _activeItems.add(it)
            }
        }
    }

    fun subscribeOnElements() {
        indexedItems.forEach { item ->
            val div = item.value
            val subscription = div.value().visibility.observe(bindingContext.expressionResolver) {
                item.updateVisibility(it)
            }

            addSubscription(subscription)
        }
    }

    private val indexedItems
        get() = _items.withIndex()

    private fun IndexedValue<Div>.updateVisibility(newVisibility: DivVisibility) {
        val wasActive = activityMap[value] ?: false
        val isActive = newVisibility.isActive()

        if (!wasActive && isActive) {
            val position = _activeItems.insertionSortPass(this)
            notifyItemInserted(position)
        } else if (wasActive && !isActive) {
            val position = _activeItems.indexOf(this)
            _activeItems.removeAt(position)
            notifyItemRemoved(position)
        }

        activityMap[value] = isActive
    }

    fun applyPatch(
        recyclerView: RecyclerView?,
        divPatchCache: DivPatchCache,
    ): Boolean {
        val patch = divPatchCache.getPatch(bindingContext.divView.dataTag) ?: return false
        val divPatchApply = DivPatchApply(patch)

        val appliedToListPatchIds = mutableSetOf<String>()
        var index = 0
        var activeIndex = 0

        while (index < _items.size) {
            val childDiv = _items[index]
            val patchId = childDiv.value().id
            val patchDivs = patchId?.let {
                divPatchCache.getPatchDivListById(bindingContext.divView.dataTag, it)
            }

            val isActive = activityMap[childDiv] == true

            if (patchDivs != null) {
                _items.removeAt(index)
                if (isActive) {
                    notifyItemRemoved(activeIndex)
                }

                _items.addAll(index, patchDivs)

                val activeItemsInserted = patchDivs.count { it.isActive(bindingContext.expressionResolver) }
                notifyItemRangeInserted(activeIndex, activeItemsInserted)

                index += patchDivs.size - 1
                activeIndex += activeItemsInserted - 1
                appliedToListPatchIds.add(patchId)
            }

            if (isActive) {
                activeIndex++
            }

            index++
        }

        // Apply patch inside items if needed
        patch.patches.keys.filter { it !in appliedToListPatchIds }.forEach { idToFind ->
            for (i in 0 until _items.size) {
                val childDiv = _items[i]
                divPatchApply.patchDivChild(
                    parentView = recyclerView ?: bindingContext.divView,
                    childDiv,
                    idToFind,
                    bindingContext.expressionResolver
                )?.let { newDiv ->
                    _items[i] = newDiv
                    return@forEach
                }
            }
        }

        updateActiveItems()

        return appliedToListPatchIds.isNotEmpty()
    }

    companion object {
        private fun Div.isActive(resolver: ExpressionResolver): Boolean =
            value().visibility.evaluate(resolver).isActive()

        private fun DivVisibility?.isActive() =
            this != DivVisibility.GONE

        private fun <T : Any> List<IndexedValue<T>>.dropIndex(): List<T> =
            object : AbstractList<T>() {
                override fun get(index: Int): T = this@dropIndex[index].value

                override val size: Int
                    get() = this@dropIndex.size
            }

        private fun <T : Any> MutableList<IndexedValue<T>>.insertionSortPass(item: IndexedValue<T>): Int {
            val position = indexOfFirst { it.index > item.index }.takeUnless { it == -1 } ?: size
            add(position, item)
            return position
        }
    }
}
