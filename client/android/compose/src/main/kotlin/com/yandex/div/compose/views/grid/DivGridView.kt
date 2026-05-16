package com.yandex.div.compose.views.grid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.combineAlignment
import com.yandex.div.compose.utils.isWrapContent
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div2.DivBase
import com.yandex.div2.DivGrid
import com.yandex.div2.DivVisibility

@OptIn(ExperimentalGridApi::class)
@Composable
internal fun DivGridView(modifier: Modifier, data: DivGrid) {
    val items = data.items.orEmpty().filter {
        it.value().visibility.observedValue() != DivVisibility.GONE
    }
    val columnCount = data.columnCount.observedValue().toInt().coerceAtLeast(DEFAULT_COLUMN_COUNT)
    val (columnTracks, rowTracks) = items.computeTracks(
        columnCount = columnCount,
        widthWrapContent = data.width.isWrapContent,
        heightWrapContent = data.height.isWrapContent,
    )
    val contentAlignment = combineAlignment(
        data.contentAlignmentHorizontal.observedValue().toHorizontalAlignment(),
        data.contentAlignmentVertical.observedValue().toVerticalAlignment(),
    )

    Box(modifier, contentAlignment = contentAlignment) {
        Grid(
            config = {
                columnTracks.forEach { column(it) }
                rowTracks.forEach { row(it) }
            }
        ) {
            items.forEach { item ->
                val base = item.value()
                val columnSpan = base.columnSpan?.observedValue()?.toInt() ?: DEFAULT_SPAN
                val rowSpan = base.rowSpan?.observedValue()?.toInt() ?: DEFAULT_SPAN
                DivBlockView(
                    data = item,
                    modifier = Modifier
                        .applyIf(columnSpan > 1 && rowSpan == 1) {
                            then(UnboundedIntrinsicHeight)
                        }
                        .gridItem(
                            columnSpan = columnSpan,
                            rowSpan = rowSpan,
                            alignment = base.toGridItemAlignment(),
                        )
                )
            }
        }
    }
}

@Composable
private fun DivBase.toGridItemAlignment(): Alignment = combineAlignment(
    alignmentHorizontal?.observedValue()?.toHorizontalAlignment() ?: Alignment.Start,
    alignmentVertical?.observedValue()?.toVerticalAlignment() ?: Alignment.Top,
)

private object UnboundedIntrinsicHeight : LayoutModifier {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int): Int =
        measurable.maxIntrinsicHeight(Constraints.Infinity)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int): Int =
        measurable.minIntrinsicHeight(Constraints.Infinity)
}

private const val DEFAULT_COLUMN_COUNT = 1
