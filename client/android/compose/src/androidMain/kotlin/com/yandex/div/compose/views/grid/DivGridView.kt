package com.yandex.div.compose.views.grid

import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.wrapContentSize
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
import com.yandex.div.compose.collection.observeStaticItems
import com.yandex.div.compose.collection.observeVisible
import com.yandex.div.compose.context.WithDivKey
import com.yandex.div.compose.di.WithLocalComponent
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.combineAlignment
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.isWrapContent
import com.yandex.div.compose.utils.observeHorizontalMarginsSum
import com.yandex.div.compose.utils.observeVerticalMarginsSum
import com.yandex.div.compose.utils.observedAlignment
import com.yandex.div.compose.utils.observedAspectRatio
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.fixedIntrinsics
import com.yandex.div2.DivBase
import com.yandex.div2.DivGrid

@OptIn(ExperimentalGridApi::class)
@Composable
internal fun DivGridView(modifier: Modifier, data: DivGrid) {
    val collectionItems = data.observeStaticItems(data.items)
    val items = collectionItems.observeVisible()
    val components = collectionItems.components
    val columnCount = data.columnCount.observedIntValue().coerceAtLeast(DEFAULT_COLUMN_COUNT)
    val (columnTracks, rowTracks) = items.computeTracks(
        components = components,
        columnCount = columnCount,
        widthWrapContent = data.width.isWrapContent,
        heightWrapContent = data.height.isWrapContent,
    )
    Grid(
        modifier = modifier.wrapContentSize(
            align = observedAlignment(
                data.contentAlignmentHorizontal,
                data.contentAlignmentVertical
            ),
            unbounded = false,
        ),
        config = {
            columnTracks.resolve(constraints.maxWidth, this).forEach { column(it) }
            rowTracks.resolve(constraints.maxHeight, this).forEach { row(it) }
        }
    ) {
        items.forEach { item ->
            WithDivKey(item) {
                WithLocalComponent(components?.get(item)) {
                    val divBase = item.value()
                    val columnSpan = divBase.columnSpan.observedIntValue(DEFAULT_SPAN)
                    val rowSpan = divBase.rowSpan.observedIntValue(DEFAULT_SPAN)
                    DivBlockView(
                        data = item,
                        modifier = Modifier
                            .matchParentMarginsIntrinsics(divBase)
                            .applyIf(columnSpan > 1 && rowSpan == 1) {
                                then(UnboundedIntrinsicHeight)
                            }
                            .gridItem(
                                columnSpan = columnSpan,
                                rowSpan = rowSpan,
                                alignment = divBase.toGridItemAlignment()
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun DivBase.toGridItemAlignment(): Alignment = combineAlignment(
    alignmentHorizontal?.observedValue()?.toHorizontalAlignment() ?: Alignment.Start,
    alignmentVertical?.observedValue()?.toVerticalAlignment() ?: Alignment.Top,
)

/**
 * An auto track takes only the margins from a match_parent cell: the View grid's first pass measures a
 * match_parent axis at zero, so neither the content nor the size bounds of the cell size the track.
 */
@Composable
private fun Modifier.matchParentMarginsIntrinsics(divBase: DivBase): Modifier {
    val isHeightMatchParent = divBase.height.isMatchParent && divBase.observedAspectRatio() == null
    return fixedIntrinsics(
        width = divBase.observeHorizontalMarginsSum().takeIf { divBase.width.isMatchParent },
        height = divBase.observeVerticalMarginsSum().takeIf { isHeightMatchParent },
    )
}

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
