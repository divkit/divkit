package com.yandex.div.core.view2.divs

import android.view.ViewGroup
import com.yandex.div.internal.core.DivBlock

internal interface CollectionItemBinding<H : DivCollectionViewHolder> {
    fun create(parent: ViewGroup, viewType: Int): H

    fun bind(holder: H, block: DivBlock, position: Int)

    fun viewType(position: Int, originalType: Int): Int = originalType

    fun clear() = Unit

}

internal class DefaultCollectionItemBinding<H : DivCollectionViewHolder>(
    private val createHolder: (ViewGroup) -> H,
    private val bindHolder: (H, DivBlock, Int) -> Unit = { holder, block, position -> holder.bind(block, position) },
) : CollectionItemBinding<H> {

    override fun create(parent: ViewGroup, viewType: Int): H = createHolder(parent)

    override fun bind(holder: H, block: DivBlock, position: Int) = bindHolder(holder, block, position)
}
