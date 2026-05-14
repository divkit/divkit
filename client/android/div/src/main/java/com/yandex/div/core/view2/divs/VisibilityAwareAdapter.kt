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

    /**
     * Items that occupy a slot in the recycler.
     *
     * Includes both [DivVisibility.VISIBLE] and [DivVisibility.INVISIBLE] items, since the latter
     * must reserve layout space (mirrors `View.INVISIBLE` behavior applied by `DivBaseBinder`).
     * Only [DivVisibility.GONE] items are excluded.
     */
    val visibleItems: List<DivItemBuilderResult>
        get() = buildVisibleItemList()

    private val itemList = initialItems.toMutableList()
    private val visibleItemList = mutableListOf<DivItemBuilderResult>()
    private val itemReservesSpaceList = initialItems.map { item -> item.reservesSpace }.toMutableList()
    private var isVisibleItemListValid = false

    override val subscriptions = mutableListOf<Disposable>()

    init {
        subscribeOnElements()
    }

    private fun buildVisibleItemList(): List<DivItemBuilderResult> {
        if (!isVisibleItemListValid) {
            visibleItemList.clear()
            itemList.mapIndexedNotNullTo(visibleItemList) { index, item ->
                if (itemReservesSpaceList[index]) item else null
            }
            isVisibleItemListValid = true
        }
        return visibleItemList
    }

    fun addItems(
        position: Int,
        items: Collection<DivItemBuilderResult>
    ) {
        itemList.addAll(position, items)
        itemReservesSpaceList.addAll(position, items.map { item -> item.reservesSpace })
        isVisibleItemListValid = false

        items.forEachIndexed { index, item ->
            if (item.reservesSpace) {
                notifyVisibleItemInserted(position + index)
            }
        }
    }

    fun setItem(
        position: Int,
        item: DivItemBuilderResult,
        visibility: DivVisibility = item.visibility
    ) {
        val reservesSpace = visibility != DivVisibility.GONE
        val wasReservingSpace = itemReservesSpaceList[position]

        itemList[position] = item
        itemReservesSpaceList[position] = reservesSpace
        if (reservesSpace || wasReservingSpace) {
            isVisibleItemListValid = false
        }

        when {
            wasReservingSpace && !reservesSpace -> notifyVisibleItemRemoved(position)
            !wasReservingSpace && reservesSpace -> notifyVisibleItemInserted(position)
            wasReservingSpace && reservesSpace -> notifyVisibleItemChanged(position)
        }
    }

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        val wasReservingSpace = itemReservesSpaceList.removeAt(position)
        isVisibleItemListValid = false

        if (wasReservingSpace) {
            notifyVisibleItemRemoved(position)
        }
    }

    private fun visiblePositionOf(position: Int): Int {
        var visiblePosition = 0
        for (i in 0 until position) {
            if (itemReservesSpaceList[i]) visiblePosition++
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
        val reservesSpace = visibility != DivVisibility.GONE
        val wasReservingSpace = itemReservesSpaceList[position]
        if (reservesSpace == wasReservingSpace) {
            return
        }

        itemReservesSpaceList[position] = reservesSpace
        isVisibleItemListValid = false

        if (wasReservingSpace) {
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

private val DivItemBuilderResult.reservesSpace: Boolean
    get() = visibility != DivVisibility.GONE
