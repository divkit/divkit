package com.yandex.div.compose.tooltips

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import com.yandex.div2.DivTooltip
import kotlin.math.roundToInt

/**
 * Positions a tooltip popup relative to an explicit [anchorBounds] (in window
 * coordinates) rather than the implicit anchor of the [androidx.compose.ui.window.Popup]
 * itself.
 */
internal class TooltipPositionProvider(
    private val position: DivTooltip.Position,
    private val anchorBounds: Rect?,
    private val offset: IntOffset,
    private val fullScreen: Boolean = false,
) : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        return if (fullScreen) IntOffset.Zero else calculateTooltipPosition(windowSize, popupContentSize)
    }

    fun calculateTooltipPosition(windowSize: IntSize, popupContentSize: IntSize): IntOffset {
        val anchor = this.anchorBounds ?: return IntOffset.Zero
        val anchorWidth = anchor.width.roundToInt()
        val anchorHeight = anchor.height.roundToInt()
        val anchorLeft = anchor.left.roundToInt()
        val anchorTop = anchor.top.roundToInt()

        val xOffset = when (position) {
            DivTooltip.Position.LEFT,
            DivTooltip.Position.TOP_LEFT,
            DivTooltip.Position.BOTTOM_LEFT -> -popupContentSize.width

            DivTooltip.Position.TOP_RIGHT,
            DivTooltip.Position.RIGHT,
            DivTooltip.Position.BOTTOM_RIGHT -> anchorWidth

            DivTooltip.Position.TOP,
            DivTooltip.Position.BOTTOM,
            DivTooltip.Position.CENTER -> (anchorWidth - popupContentSize.width) / 2
        }

        val yOffset = when (position) {
            DivTooltip.Position.TOP_LEFT,
            DivTooltip.Position.TOP,
            DivTooltip.Position.TOP_RIGHT -> -popupContentSize.height

            DivTooltip.Position.BOTTOM_LEFT,
            DivTooltip.Position.BOTTOM,
            DivTooltip.Position.BOTTOM_RIGHT -> anchorHeight

            DivTooltip.Position.LEFT,
            DivTooltip.Position.RIGHT,
            DivTooltip.Position.CENTER -> (anchorHeight - popupContentSize.height) / 2
        }

        val x = anchorLeft + xOffset + offset.x
        val y = anchorTop + yOffset + offset.y
        return if (fullScreen) {
            IntOffset(x, y)
        } else {
            IntOffset(
                x.coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0)),
                y.coerceIn(0, (windowSize.height - popupContentSize.height).coerceAtLeast(0)),
            )
        }
    }
}
