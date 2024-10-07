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
    private val indexedItems get() = items.withIndex()

    private val _visibleItems = mutableListOf<IndexedValue<DivItemBuilderResult>>()
    val visibleItems: List<DivItemBuilderResult> = object : AbstractList<DivItemBuilderResult>() {

        override fun get(index: Int) = _visibleItems[index].value

        override val size get() = _visibleItems.size
    }

    private val visibilityMap = mutableMapOf<DivItemBuilderResult, Boolean>()

    override val subscriptions = mutableListOf<Disposable>()

    init {
        initVisibleItems()
        subscribeOnElements()
    }

    private fun initVisibleItems() {
        indexedItems.forEach {
            val isVisible = it.value.visibility != DivVisibility.GONE
            visibilityMap[it.value] = isVisible
            if (isVisible) {
                _visibleItems.add(it)
            }
        }
    }

    private val DivItemBuilderResult.visibility get() = div.value().visibility.evaluate(expressionResolver)

    fun subscribeOnElements() {
        closeAllSubscription()
        indexedItems.forEach { item ->
            val subscription = item.value.div.value().visibility.observe(item.value.expressionResolver) {
                updateItemVisibility(item.index, it)
            }
            addSubscription(subscription)
        }
    }

    protected open fun notifyRawItemRemoved(position: Int) = notifyItemRemoved(position)

    protected open fun notifyRawItemInserted(position: Int) = notifyItemInserted(position)

    protected open fun notifyRawItemChanged(position: Int) = notifyItemChanged(position)

    protected fun updateItemVisibility(
        rawIndex: Int,
        newVisibility: DivVisibility = items[rawIndex].visibility,
    ) {
        val item = items[rawIndex]
        val wasVisible = visibilityMap[item] ?: false
        val isVisible = newVisibility != DivVisibility.GONE

        if (!wasVisible && isVisible) {
            val position = _visibleItems
                .indexOfFirst { it.index > rawIndex }
                .takeUnless { it == -1 } ?: _visibleItems.size
            _visibleItems.add(position, IndexedValue(rawIndex, item))
            notifyRawItemInserted(position)
        } else if (wasVisible && !isVisible) {
            val position = _visibleItems.indexOfFirst { it.value == item }
            _visibleItems.removeAt(position)
            notifyRawItemRemoved(position)
        }

        visibilityMap[item] = isVisible
    }

    override fun getItemCount() = visibleItems.size
}
