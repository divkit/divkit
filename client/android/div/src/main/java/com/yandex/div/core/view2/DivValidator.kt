package com.yandex.div.core.view2

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.internal.core.DivVisitor
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivSize
import javax.inject.Inject
import kotlin.math.max

@Mockable
@DivScope
internal class DivValidator @Inject constructor() : DivVisitor<Boolean>() {

    fun validate(div: Div, resolver: ExpressionResolver) = visit(div, resolver)

    override fun defaultVisit(data: Div, resolver: ExpressionResolver) = true

    override fun visit(data: Div.Grid, resolver: ExpressionResolver): Boolean {
        val grid = data.value
        val columnCount = grid.columnCount.evaluate(resolver).toIntSafely()
        val heights = IntArray(columnCount)
        var column: Int
        var matchParentWidthCount = 0
        var matchParentHeightCount = 0

        val items = grid.nonNullItems
        items.forEach {
            val minHeight = heights.minOrNull() ?: 0
            column = heights.indexOf(minHeight)
            for (i in heights.indices) {
                heights[i] = max(0, heights[i] - minHeight)
            }

            val div = it.value()
            val columnSpan = div.columnSpan?.evaluate(resolver)?.toIntSafely() ?: 1
            val rowSpan = div.rowSpan?.evaluate(resolver)?.toIntSafely() ?: 1
            if (column + columnSpan > columnCount) return false  // a cell laps over column count

            for (i in column until column + columnSpan) {
                if (heights[i] > 0) return false  // two cells has overlapping spans
                heights[i] = rowSpan
            }

            if (div.width is DivSize.MatchParent) matchParentWidthCount++
            if (div.height is DivSize.MatchParent) matchParentHeightCount++
        }

        if (grid.width is DivSize.WrapContent && matchParentWidthCount == items.size) return false
        if (grid.height is DivSize.WrapContent && matchParentHeightCount == items.size) return false

        return heights.all { it == heights.first() } // the last row filled by cells
    }
}
