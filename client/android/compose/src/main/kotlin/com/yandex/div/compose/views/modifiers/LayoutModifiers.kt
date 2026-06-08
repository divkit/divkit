package com.yandex.div.compose.views.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

internal fun Modifier.constrainUnboundedMax(isWidth: Boolean): Modifier = layout { measurable, constraints ->
    val adjustedConstraints = constraints.copy(
        maxWidth = constraints.maxWidth.constrainIfInfinity(
            minSize = constraints.minWidth,
            shouldConstrain = isWidth,
        ),
        maxHeight = constraints.maxHeight.constrainIfInfinity(
            minSize = constraints.minHeight,
            shouldConstrain = !isWidth,
        ),
    )
    val placeable = measurable.measure(adjustedConstraints)
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(0, 0)
    }
}

internal fun Modifier.fillMaxCrossAxisIfBounded(isHorizontal: Boolean): Modifier = layout { measurable, constraints ->
    val adjustedConstraints = when {
        isHorizontal && constraints.hasBoundedHeight -> constraints.copy(minHeight = constraints.maxHeight)
        !isHorizontal && constraints.hasBoundedWidth -> constraints.copy(minWidth = constraints.maxWidth)
        else -> constraints
    }
    val placeable = measurable.measure(adjustedConstraints)
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(0, 0)
    }
}

internal fun Modifier.onMeasureConstraints(
    onConstraints: (Constraints) -> Unit,
): Modifier = layout { measurable, constraints ->
    onConstraints(constraints)
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(0, 0)
    }
}

internal fun Modifier.fixedIntrinsics(
    width: Dp? = null,
    height: Dp? = null,
): Modifier = then(FixedIntrinsicsModifier(width, height))

private fun Int.constrainIfInfinity(minSize: Int, shouldConstrain: Boolean): Int {
    return if (shouldConstrain && this == Constraints.Infinity) {
        minSize.coerceAtLeast(0)
    } else {
        this
    }
}

private class FixedIntrinsicsModifier(
    private val width: Dp?,
    private val height: Dp?,
) : LayoutModifier {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
    ): Int = width?.roundToPx() ?: measurable.maxIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int,
    ): Int = width?.roundToPx() ?: measurable.minIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
    ): Int = height?.roundToPx() ?: measurable.maxIntrinsicHeight(width)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int,
    ): Int = height?.roundToPx() ?: measurable.minIntrinsicHeight(width)
}
