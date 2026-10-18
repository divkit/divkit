package com.yandex.div.compose.views.pager

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal fun childModifier(
    isHorizontal: Boolean,
    viewportSize: Dp,
    crossAxisSizes: PagerCrossAxisSizes?,
    pageSize: Dp?,
    startPadding: Dp,
    endPadding: Dp,
): Modifier {
    val scrollAxisModifier = scrollAxisSizeModifier(pageSize, isHorizontal, viewportSize, startPadding, endPadding)
    return crossAxisSizeModifier(isHorizontal, crossAxisSizes).then(scrollAxisModifier)
}

/**
 * Without [crossAxisSizes] the pager bounds its cross axis and pages fill it. Otherwise the pager wraps the largest
 * natural page size and every page is at least that large, so that smaller pages are aligned within it, while the
 * unbounded maximum lets a growing page report its natural size.
 */
private fun crossAxisSizeModifier(isHorizontal: Boolean, crossAxisSizes: PagerCrossAxisSizes?): Modifier {
    if (crossAxisSizes == null) {
        return if (isHorizontal) Modifier.fillMaxHeight() else Modifier.fillMaxWidth()
    }

    return Modifier.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.withCrossAxisSize(crossAxisSizes.max, isHorizontal))
        layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }
}

private fun scrollAxisSizeModifier(pageSize: Dp?, isHorizontal: Boolean, viewportSize: Dp, startPadding: Dp, endPadding: Dp): Modifier {
    if (pageSize != null) {
        return if (isHorizontal) Modifier.width(pageSize) else Modifier.height(pageSize)
    }
    val maxSize = (viewportSize - startPadding - endPadding).coerceAtLeast(0.dp)
    return if (isHorizontal) Modifier.widthIn(max = maxSize) else Modifier.heightIn(max = maxSize)
}
