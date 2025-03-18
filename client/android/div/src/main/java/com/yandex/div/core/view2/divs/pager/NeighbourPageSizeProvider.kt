package com.yandex.div.core.view2.divs.pager

import android.util.DisplayMetrics
import com.yandex.div.core.util.ViewProperty
import com.yandex.div.core.view2.divs.toPxF
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivNeighbourPageSize
import com.yandex.div2.DivPager.ItemAlignment

internal class NeighbourPageSizeProvider(
    mode: DivNeighbourPageSize,
    resolver: ExpressionResolver,
    metrics: DisplayMetrics,
    private val parentSize: ViewProperty<Int>,
    itemSpacing: Float,
    private val paddings: DivPagerPaddingsHolder,
    private val alignment: ItemAlignment,
) : DivPagerPageSizeProvider(parentSize, paddings, alignment), FixedPageSizeProvider {

    private val neighbourPageWidth = mode.neighbourPageWidth.toPxF(metrics, resolver)

    override val neighbourSize = neighbourPageWidth + itemSpacing

    override val itemSize: Float get() {
        return when (alignment) {
            ItemAlignment.START -> parentSize.get() - paddings.start - neighbourSize
            ItemAlignment.CENTER -> parentSize.get() - neighbourSize * 2
            ItemAlignment.END -> parentSize.get() - paddings.end - neighbourSize
        }
    }

    override val hasOffScreenPages = neighbourPageWidth > 0

    override fun getItemSize(position: Int) = itemSize
}
