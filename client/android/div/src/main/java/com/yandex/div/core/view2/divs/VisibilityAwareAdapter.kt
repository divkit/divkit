package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.Disposable
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div2.DivVisibility

internal abstract class VisibilityAwareAdapter<VH : RecyclerView.ViewHolder>(
    initialItems: List<DivItemBuilderResult>,
) : RecyclerView.Adapter<VH>(), ExpressionSubscriber {

    val items: List<DivItemBuilderResult>
        get() = itemList

    val visibleItems: List<DivItemBuilderResult>
        get() = buildVisibleItemList()

    private val itemList = initialItems.toMutableList()
    private val visibleItemList = mutableListOf<DivItemBuilderResult>()
    private val itemVisibilityList = initialItems.map { item -> item.isVisible }.toMutableList()
    private var isVisibleItemListValid = false

    override val subscriptions = mutableListOf<Disposable>()

    init {
        subscribeOnElements()
    }

    private fun buildVisibleItemList(): List<DivItemBuilderResult> {
        if (!isVisibleItemListValid) {
            visibleItemList.clear()
            itemList.mapIndexedNotNullTo(visibleItemList) { index, item ->
                if (itemVisibilityList[index]) item else null
            }
            isVisibleItemListValid = true
        }
        return visibleItemList
    }

    fun addItem(
        position: Int,
        item: DivItemBuilderResult,
        visibility: DivVisibility = item.visibility
    ) {
        val isVisible = visibility == DivVisibility.VISIBLE

        itemList.add(position, item)
        itemVisibilityList.add(position, isVisible)
        isVisibleItemListValid = false

        if (isVisible) {
            notifyVisibleItemInserted(position)
        }
    }

    fun addItems(
        position: Int,
        items: Collection<DivItemBuilderResult>
    ) {
        itemList.addAll(position, items)
        itemVisibilityList.addAll(position, items.map { item -> item.isVisible })
        isVisibleItemListValid = false

        items.forEachIndexed { index, item ->
            val isVisible = item.visibility == DivVisibility.VISIBLE
            if (isVisible) {
                notifyVisibleItemInserted(position + index)
            }
        }
    }

    fun setItem(
        position: Int,
        item: DivItemBuilderResult,
        visibility: DivVisibility = item.visibility
    ) {
        val isVisible = visibility == DivVisibility.VISIBLE
        val wasVisible = itemVisibilityList[position]

        itemList[position] = item
        itemVisibilityList[position] = isVisible
        if (isVisible || wasVisible) {
            isVisibleItemListValid = false
        }

        when {
            wasVisible && !isVisible -> notifyVisibleItemRemoved(position)
            !wasVisible && isVisible -> notifyVisibleItemInserted(position)
            wasVisible && isVisible -> notifyVisibleItemChanged(position)
        }
    }

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        val isVisible = itemVisibilityList.removeAt(position)
        isVisibleItemListValid = false

        if (isVisible) {
            notifyVisibleItemRemoved(position)
        }
    }

    private fun visiblePositionOf(position: Int): Int {
        var visiblePosition = 0
        for (i in 0 until position) {
            if (itemVisibilityList[i]) visiblePosition++
        }
        return visiblePosition
    }

    override fun getItemCount() = visibleItems.size

    protected fun subscribeOnElements() {
        closeAllSubscription()
        itemList.forEachIndexed { index, item ->
            val subscription = item.div.value().visibility.observe(item.expressionResolver) { visibility ->
                updateItemVisibility(index, visibility)
            }
            addSubscription(subscription)
        }
    }

    private fun updateItemVisibility(position: Int, visibility: DivVisibility = itemList[position].visibility) {
        val isVisible = visibility == DivVisibility.VISIBLE
        val wasVisible = itemVisibilityList[position]
        if (isVisible == wasVisible) {
            return
        }

        itemVisibilityList[position] = isVisible
        isVisibleItemListValid = false

        if (wasVisible) {
            notifyVisibleItemRemoved(position)
        } else {
            notifyVisibleItemInserted(position)
        }
    }

    private fun notifyVisibleItemRemoved(position: Int) = notifyRawItemRemoved(visiblePositionOf(position))

    private fun notifyVisibleItemInserted(position: Int) = notifyRawItemInserted(visiblePositionOf(position))

    private fun notifyVisibleItemChanged(position: Int) = notifyRawItemChanged(visiblePositionOf(position))

    protected open fun notifyRawItemRemoved(position: Int) = notifyItemRemoved(position)

    protected open fun notifyRawItemInserted(position: Int) = notifyItemInserted(position)

    protected open fun notifyRawItemChanged(position: Int) = notifyItemChanged(position)
}

private val DivItemBuilderResult.visibility: DivVisibility
    get() = div.value().visibility.evaluate(expressionResolver)

private val DivItemBuilderResult.isVisible: Boolean
    get() = visibility == DivVisibility.VISIBLE
