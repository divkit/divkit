package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toDp
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnitValue

@Composable
internal fun Modifier.width(
    width: DivSize,
    horizontalAlignment: DivAlignmentHorizontal? = null
): Modifier {
    val align = horizontalAlignment?.toHorizontalAlignment() ?: Alignment.Start
    return when (width) {
        is DivSize.MatchParent -> fillMaxWidth()
        is DivSize.WrapContent -> {
            val isConstrained = width.value.constrained?.observedValue() == true
            wrapContentWidth(align = align, unbounded = !isConstrained)
                .applyWrapContentWidthBounds(width)
        }
        is DivSize.Fixed -> wrapContentWidth(align = align, unbounded = true)
            .requiredWidth(width.value.value.observedValue().toDp())
    }
}

@Composable
internal fun Modifier.height(
    height: DivSize,
    verticalAlignment: DivAlignmentVertical? = null
): Modifier {
    val align = verticalAlignment?.toVerticalAlignment() ?: Alignment.Top
    return when (height) {
        is DivSize.MatchParent -> fillMaxHeight()
        is DivSize.WrapContent -> {
            val isConstrained = height.value.constrained?.observedValue() == true
            wrapContentHeight(align = align, unbounded = !isConstrained)
                .applyWrapContentHeightBounds(height)
        }
        is DivSize.Fixed -> wrapContentHeight(align = align, unbounded = true)
            .requiredHeight(height.value.value.observedValue().toDp())
    }
}
@Composable
private fun Modifier.applyWrapContentWidthBounds(size: DivSize.WrapContent): Modifier {
    val (minWidth, maxWidth) = size.wrapContentSizeBounds()
    if (minWidth == null && maxWidth == null) return this
    return widthIn(
        min = minWidth ?: Dp.Unspecified,
        max = maxWidth ?: Dp.Unspecified,
    )
}

@Composable
private fun Modifier.applyWrapContentHeightBounds(size: DivSize.WrapContent): Modifier {
    val (minHeight, maxHeight) = size.wrapContentSizeBounds()
    if (minHeight == null && maxHeight == null) return this
    return heightIn(
        min = minHeight ?: Dp.Unspecified,
        max = maxHeight ?: Dp.Unspecified,
    )
}

@Composable
private fun DivSize.WrapContent.wrapContentSizeBounds(): Pair<Dp?, Dp?> {
    val minSize = value.minSize?.toDpSize()
    val maxSize = value.maxSize?.toDpSize()
    if (minSize != null && maxSize != null && minSize > maxSize) {
        return null to null
    }
    return minSize to maxSize
}

@Composable
private fun DivSizeUnitValue.toDpSize(): Dp {
    return value.observedValue().toFloat().toDp(unit.observedValue())
}

