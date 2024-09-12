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

    private val _visibleItems = mutableListOf<DivItemBuilderResult>()
    val visibleItems: List<DivItemBuilderResult> get() = _visibleItems

    private val visibilityMap = mutableMapOf<DivItemBuilderResult, Boolean>()

    override val subscriptions = mutableListOf<Disposable>()

    init {
        initVisibleItems()
        subscribeOnElements()
    }

    private fun initVisibleItems() {
        items.forEach {
            val isVisible = it.visibility != DivVisibility.GONE
            visibilityMap[it] = isVisible
            if (isVisible) {
                _visibleItems.add(it)
            }
        }
    }

    private val DivItemBuilderResult.visibility get() = div.value().visibility.evaluate(expressionResolver)

    fun subscribeOnElements() {
        closeAllSubscription()
        items.forEachIndexed { i, item ->
            val subscription = item.div.value().visibility.observe(item.expressionResolver) {
                updateItemVisibility(i, it)
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
            val position = findPrevVisibleItemIndex(rawIndex) + 1
            _visibleItems.add(position, item)
            notifyRawItemInserted(position)
        } else if (wasVisible && !isVisible) {
            val position = _visibleItems.indexOf(item)
            _visibleItems.removeAt(position)
            notifyRawItemRemoved(position)
        }

        visibilityMap[item] = isVisible
    }

    private fun findPrevVisibleItemIndex(index: Int): Int {
        for (i in index - 1 downTo 0) {
            if (visibilityMap[items[i]] == true) return i
        }
        return -1
    }

    override fun getItemCount() = visibleItems.size
}
