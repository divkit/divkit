package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div2.Div

internal interface DivAdapter<VH: RecyclerView.ViewHolder, T: Any> {

    val items: MutableList<T>

    val visibleItems: List<T>

    val visibleDivs: List<Div>

    val visibilityMap: MutableMap<T, Boolean>

    fun getItemDiv(position: Int): Div

    fun updateVisibleItems()

    fun notifyItemRemoved(index: Int)

    fun notifyItemRangeInserted(index: Int, count: Int)

    fun VH.bindItem(position: Int)
}
