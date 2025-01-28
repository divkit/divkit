package com.yandex.div.core.view2.divs.pager

import android.util.DisplayMetrics
import com.yandex.div.core.view2.divs.toPxF
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivNeighbourPageSize

internal class NeighbourPageSizeProvider(
    mode: DivNeighbourPageSize,
    resolver: ExpressionResolver,
    metrics: DisplayMetrics,
    parentSize: Int,
    itemSpacing: Float,
) : DivPagerPageSizeProvider, FixedPageSizeProvider {

    private val neighbourPageWidth = mode.neighbourPageWidth.toPxF(metrics, resolver)

    override val neighbourSize = neighbourPageWidth + itemSpacing

    override val itemSize = parentSize - neighbourSize * 2

    override val hasOffScreenPages = neighbourPageWidth > 0

    override fun getItemSize(position: Int) = itemSize
}
