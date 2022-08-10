package com.yandex.div.core.view2

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivInput
import com.yandex.div2.DivPager
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSize
import com.yandex.div2.DivSlider
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import javax.inject.Inject
import kotlin.math.max

@Mockable
@DivScope
internal class DivValidator @Inject constructor() : DivVisitor<Boolean>() {

    fun validate(div: Div, resolver: ExpressionResolver) = visit(div, resolver)

    override fun visit(data: DivText, resolver: ExpressionResolver) = true

    override fun visit(data: DivImage, resolver: ExpressionResolver) = true

    override fun visit(data: DivGifImage, resolver: ExpressionResolver) = true

    override fun visit(data: DivSeparator, resolver: ExpressionResolver) = true

    override fun visit(data: DivContainer, resolver: ExpressionResolver) = true

    override fun visit(data: DivGrid, resolver: ExpressionResolver): Boolean {
        val columnCount = data.columnCount.evaluate(resolver)
        val heights = IntArray(columnCount)
        var column: Int
        var matchParentWidthCount = 0
        var matchParentHeightCount = 0

        data.items.forEach {
            val minHeight = heights.minOrNull() ?: 0
            column = heights.indexOf(minHeight)
            for (i in heights.indices) {
                heights[i] = max(0, heights[i] - minHeight)
            }

            val div = it.value()
            val columnSpan = div.columnSpan?.evaluate(resolver) ?: 1
            val rowSpan = div.rowSpan?.evaluate(resolver) ?: 1
            if (column + columnSpan > columnCount) return false  // a cell laps over column count

            for (i in column until column + columnSpan) {
                if (heights[i] > 0) return false  // two cells has overlapping spans
                heights[i] = rowSpan
            }

            if (div.width is DivSize.MatchParent) matchParentWidthCount++
            if (div.height is DivSize.MatchParent) matchParentHeightCount++
        }

        if (data.width is DivSize.WrapContent && matchParentWidthCount == data.items.size) return false
        if (data.height is DivSize.WrapContent && matchParentHeightCount == data.items.size) return false

        return heights.all { it == heights.first() } // the last row filled by cells
    }

    override fun visit(data: DivGallery, resolver: ExpressionResolver) = true

    override fun visit(data: DivPager, resolver: ExpressionResolver) = true

    override fun visit(data: DivTabs, resolver: ExpressionResolver) = true

    override fun visit(data: DivState, resolver: ExpressionResolver) = true

    override fun visit(data: DivCustom, resolver: ExpressionResolver) = true

    override fun visit(data: DivIndicator, resolver: ExpressionResolver) = true

    override fun visit(data: DivSlider, resolver: ExpressionResolver) = true

    override fun visit(data: DivInput, resolver: ExpressionResolver) = true
}
