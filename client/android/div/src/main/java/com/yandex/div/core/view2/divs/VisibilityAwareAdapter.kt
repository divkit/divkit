package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.Disposable
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div2.Div
import com.yandex.div2.DivVisibility

internal abstract class VisibilityAwareAdapter<VH : RecyclerView.ViewHolder, T: Any>(
    items: List<T>,
    private val bindingContext: BindingContext,
) : RecyclerView.Adapter<VH>(),
    DivAdapter<VH, T>,
    ExpressionSubscriber {

    override val items = items.toMutableList()

    private val _visibleItems = mutableListOf<IndexedValue<T>>()
    override val visibleItems: List<T> = _visibleItems.dropIndex()

    override val visibilityMap = mutableMapOf<T, Boolean>()

    override val subscriptions = mutableListOf<Disposable>()

    init {
        updateVisibleItems()
    }

    final override fun updateVisibleItems() {
        _visibleItems.clear()
        visibilityMap.clear()

        indexedItems.forEach {
            val isVisible = it.value.getVisibility(bindingContext).isVisible

            visibilityMap[it.value] = isVisible
            if (isVisible) {
                _visibleItems.add(it)
            }
        }
    }

    fun subscribeOnElements() {
        indexedItems.forEach { item ->
            val subscription = item.value.observeVisibility(bindingContext) { item.updateVisibility(it) }
            addSubscription(subscription)
        }
    }

    private val indexedItems
        get() = items.withIndex()

    private fun IndexedValue<T>.updateVisibility(newVisibility: DivVisibility) {
        val wasVisible = visibilityMap[value] ?: false
        val isVisible = newVisibility.isVisible

        if (!wasVisible && isVisible) {
            val position = _visibleItems.insertionSortPass(this)
            notifyItemInserted(position)
        } else if (wasVisible && !isVisible) {
            val position = _visibleItems.indexOf(this)
            _visibleItems.removeAt(position)
            notifyItemRemoved(position)
        }

        visibilityMap[value] = isVisible
    }

    companion object {
        internal fun <T:Any> T.getVisibility(context: BindingContext): DivVisibility? {
            return when (this) {
                is Div -> value().visibility.evaluate(context.expressionResolver)
                is DivItemBuilderResult -> div.value().visibility.evaluate(expressionResolver)
                else -> null
            }
        }

        internal fun <T:Any> T.observeVisibility(
            context: BindingContext,
            callback: (DivVisibility) -> Unit
        ): Disposable {
            return when (this) {
                is Div -> value().visibility.observe(context.expressionResolver, callback)
                is DivItemBuilderResult -> div.value().visibility.observe(expressionResolver, callback)
                else -> Disposable.NULL
            }
        }

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
