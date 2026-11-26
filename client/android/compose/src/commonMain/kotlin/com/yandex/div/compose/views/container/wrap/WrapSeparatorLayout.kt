package com.yandex.div.compose.views.container.wrap

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
internal class WrapSeparatorLayout(
    private val itemSeparator: SeparatorDrawInfo?,
    private val lineSeparator: SeparatorDrawInfo?,
    private val isHorizontal: Boolean,
) {
    // Separators between children take the place of the usual spacing on their axis.
    internal inline fun itemSpacing(defaultSpacing: () -> Dp): Dp =
        itemSeparator?.betweenSize(alongWidth = isHorizontal) ?: defaultSpacing()

    internal inline fun lineSpacing(defaultSpacing: () -> Dp): Dp =
        lineSeparator?.betweenSize(alongWidth = !isHorizontal) ?: defaultSpacing()

    @Composable
    fun rememberGeometry(childCount: Int): WrapSeparatorGeometry? {
        if ((itemSeparator == null && lineSeparator == null) || childCount == 0) return null
        val geometry = remember(isHorizontal) { WrapSeparatorGeometry(isHorizontal) }
        SideEffect {
            geometry.resize(childCount)
        }
        return geometry
    }

    fun decorate(modifier: Modifier, geometry: WrapSeparatorGeometry?, isRtl: Boolean): Modifier =
        modifier
            .separatorEdgePadding(
                horizontal = if (isHorizontal) itemSeparator else lineSeparator,
                vertical = if (isHorizontal) lineSeparator else itemSeparator,
            )
            .drawWrapSeparators(geometry, isHorizontal, itemSeparator, lineSeparator, isRtl)
}

/** Separators at the edges take space around the lines. */
private fun Modifier.separatorEdgePadding(horizontal: SeparatorDrawInfo?, vertical: SeparatorDrawInfo?): Modifier {
    if (horizontal == null && vertical == null) return this
    return padding(
        start = horizontal?.edgeSize(atStart = true, alongWidth = true) ?: 0.dp,
        end = horizontal?.edgeSize(atStart = false, alongWidth = true) ?: 0.dp,
        top = vertical?.edgeSize(atStart = true, alongWidth = false) ?: 0.dp,
        bottom = vertical?.edgeSize(atStart = false, alongWidth = false) ?: 0.dp,
    )
}

internal fun Modifier.trackChildPlacement(
    geometry: WrapSeparatorGeometry?,
    index: Int,
): Modifier {
    geometry ?: return this
    return onPlaced { layoutCoordinates ->
        val position = layoutCoordinates.positionInParent()
        geometry.updateChild(
            index = index,
            rect = Rect(
                left = position.x,
                top = position.y,
                right = position.x + layoutCoordinates.size.width,
                bottom = position.y + layoutCoordinates.size.height,
            ),
        )
    }
}

private fun Modifier.drawWrapSeparators(
    geometry: WrapSeparatorGeometry?,
    isHorizontal: Boolean,
    itemSeparator: SeparatorDrawInfo?,
    lineSeparator: SeparatorDrawInfo?,
    isRtl: Boolean,
): Modifier {
    geometry ?: return this
    return drawWithCache {
        val itemSeparatorPx = itemSeparator?.toPx(this)
        val lineSeparatorPx = lineSeparator?.toPx(this)
        onDrawWithContent {
            drawContent()
            drawWrapSeparators(geometry, isHorizontal, itemSeparatorPx, lineSeparatorPx, isRtl)
        }
    }
}
