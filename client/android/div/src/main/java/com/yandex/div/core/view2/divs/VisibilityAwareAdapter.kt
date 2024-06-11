package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.Disposable
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div2.DivVisibility

internal abstract class VisibilityAwareAdapter<VH : RecyclerView.ViewHolder>(
    items: List<DivItemBuilderResult>,
) : RecyclerView.Adapter<VH>(),
    ExpressionSubscriber {

    val items = items.toMutableList()

    private val _visibleItems = mutableListOf<IndexedValue<DivItemBuilderResult>>()
    val visibleItems: List<DivItemBuilderResult> = _visibleItems.dropIndex()

    private val visibilityMap = mutableMapOf<DivItemBuilderResult, Boolean>()

    override val subscriptions = mutableListOf<Disposable>()

    init {
        updateVisibleItems()
        subscribeOnElements()
    }

    fun updateVisibleItems() {
        _visibleItems.clear()
        visibilityMap.clear()

        indexedItems.forEach {
            val isVisible = it.value.div.value().visibility.evaluate(it.value.expressionResolver).isVisible

            visibilityMap[it.value] = isVisible
            if (isVisible) {
                _visibleItems.add(it)
            }
        }
    }

    fun subscribeOnElements() {
        indexedItems.forEach { item ->
            val subscription = item.value.div.value().visibility.observe(item.value.expressionResolver) {
                item.updateVisibility(it)
            }
            addSubscription(subscription)
        }
    }

    val DivItemBuilderResult.isVisible get() = visibilityMap[this] == true

    protected open fun notifyRawItemRemoved(position: Int) = notifyItemRemoved(position)

    protected open fun notifyRawItemInserted(position: Int) = notifyItemInserted(position)

    protected open fun notifyRawItemRangeInserted(positionStart: Int, itemCount: Int) =
        notifyItemRangeInserted(positionStart, itemCount)

    private val indexedItems
        get() = items.withIndex()

    private fun IndexedValue<DivItemBuilderResult>.updateVisibility(newVisibility: DivVisibility) {
        val wasVisible = visibilityMap[value] ?: false
        val isVisible = newVisibility.isVisible

        if (!wasVisible && isVisible) {
            val position = _visibleItems.insertionSortPass(this)
            notifyRawItemInserted(position)
        } else if (wasVisible && !isVisible) {
            val position = _visibleItems.indexOf(this)
            _visibleItems.removeAt(position)
            notifyRawItemRemoved(position)
        }

        visibilityMap[value] = isVisible
    }

    override fun getItemCount() = visibleItems.size

    companion object {
        internal val DivVisibility?.isVisible get() = this != null && this != DivVisibility.GONE

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
