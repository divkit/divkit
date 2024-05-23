package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.internal.core.DivItemBuilderResult

internal interface DivCollectionAdapterHelper<VH: RecyclerView.ViewHolder> : DivAdapter<VH, DivItemBuilderResult> {

    override val visibleDivs get() = visibleItems.map { it.div }

    override fun getItemDiv(position: Int) = items[position].div
}
