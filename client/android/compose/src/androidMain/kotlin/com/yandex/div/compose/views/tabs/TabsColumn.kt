package com.yandex.div.compose.views.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth

/**
 * Stacks the title row, the optional separator and the pages vertically.
 *
 * Unlike `Column`, which loosens the minimum height of every child, the last child (the pages)
 * keeps the height mode the tabs received: an exact height stays exact, so `match_parent`
 * pages fill it, while a bounded `wrap_content` height (`max_size`, `constrained`) lets the
 * pages wrap up to the limit — the way the View renderer forwards EXACTLY / AT_MOST specs to
 * its pager.
 */
@Composable
internal fun TabsColumn(modifier: Modifier, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        var usedHeight = 0
        val placeables = measurables.mapIndexed { index, measurable ->
            val remainingHeight = if (constraints.hasBoundedHeight) {
                (constraints.maxHeight - usedHeight).coerceAtLeast(0)
            } else {
                Constraints.Infinity
            }
            val isPages = index == measurables.lastIndex
            val placeable = measurable.measure(
                Constraints(
                    maxWidth = constraints.maxWidth,
                    minHeight = if (isPages && constraints.hasFixedHeight) remainingHeight else 0,
                    maxHeight = remainingHeight,
                )
            )
            usedHeight += placeable.height
            placeable
        }
        val width = constraints.constrainWidth(placeables.maxOfOrNull { it.width } ?: 0)
        layout(width, constraints.constrainHeight(usedHeight)) {
            var y = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(0, y)
                y += placeable.height
            }
        }
    }
}
