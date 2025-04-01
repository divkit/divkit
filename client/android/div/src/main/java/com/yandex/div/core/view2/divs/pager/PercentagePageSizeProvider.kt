package com.yandex.div.core.view2.divs.pager

import com.yandex.div.core.util.ViewProperty
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivPageSize
import com.yandex.div2.DivPager.ItemAlignment

internal class PercentagePageSizeProvider(
    mode: DivPageSize,
    resolver: ExpressionResolver,
    private val parentSize: ViewProperty<Int>,
    private val paddings: DivPagerPaddingsHolder,
    private val alignment: ItemAlignment,
) : DivPagerPageSizeProvider(parentSize, paddings, alignment), FixedPageSizeProvider {

    private val pageWidthPercentage = mode.pageWidth.value.evaluate(resolver)

    override val itemSize: Float get() {
        return (parentSize.get() * pageWidthPercentage / 100).toFloat()
    }

    override val neighbourSize: Float get() {
        return when (alignment) {
            ItemAlignment.START -> parentSize.get() - paddings.start - itemSize
            ItemAlignment.CENTER -> (parentSize.get() - itemSize) / 2
            ItemAlignment.END -> parentSize.get() - paddings.end - itemSize
        }
    }

    override val hasOffScreenPages = pageWidthPercentage < 100

    override fun getItemSize(position: Int) = itemSize
}
