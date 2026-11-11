package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div.compose.utils.combineAlignment
import com.yandex.div.compose.utils.observedAspectRatio
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div.compose.views.DivBlockLayoutParams
import com.yandex.div.compose.views.modifiers.image.imageAspectRatio
import com.yandex.div2.Div
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnitValue
import com.yandex.div2.DivWrapContentSize

/**
 * [DivBlockLayoutParams.matchParentWidth] and [DivBlockLayoutParams.matchParentHeight] resolve the
 * match_parent axes against the parent, see [MatchParentSize]; the element's own min_size and
 * max_size take precedence over them.
 */
@Composable
internal fun Modifier.size(
    div: Div,
    layoutParams: DivBlockLayoutParams,
    margins: PaddingValues?,
): Modifier {
    val matchParentWidth = layoutParams.matchParentWidth.excludeMargins(margins, isWidth = true)
    val matchParentHeight = layoutParams.matchParentHeight.excludeMargins(margins, isWidth = false)
    val data = div.value()
    val aspectRatio = data.observedAspectRatio()
    val horizontalAlignment = data.alignmentHorizontal?.observedValue()?.toHorizontalAlignment()
        ?: layoutParams.defaultHorizontalAlignment
    val width = data.width
    if (aspectRatio != null && width is DivSize.WrapContent) {
        if (div is Div.Image || div is Div.GifImage) {
            val verticalAlignment = data.alignmentVertical?.observedValue()?.toVerticalAlignment()
                ?: layoutParams.defaultVerticalAlignment
            return wrapWidth(width = width.value, alignment = horizontalAlignment)
                .imageAspectRatio(
                    ratio = aspectRatio,
                    height = data.height,
                    alignment = combineAlignment(horizontalAlignment, verticalAlignment),
                    matchParentHeight = matchParentHeight,
                    // DivImageView keeps the measured width and derives height from aspect,
                    // even for fixed/match_parent height. DivGifImageView can resize width.
                    matchHeightConstraintsFirst = div is Div.GifImage,
                )
                .applySizeBounds(width.value.minSize, width.value.maxSize, isWidth = true)
        }
        // The stock aspectRatio derives the height from a bounded width, which a wrap-content
        // container does not have: DivContainerView measures the width its content wraps first.
        if (div is Div.Container) {
            val verticalAlignment = data.alignmentVertical?.observedValue()?.toVerticalAlignment()
                ?: layoutParams.defaultVerticalAlignment
            return width(width, horizontalAlignment, matchParentWidth)
                .wrapContentHeight(align = verticalAlignment, unbounded = true)
        }
    }
    return this
        .width(
            width = width,
            horizontalAlignment = horizontalAlignment,
            matchParent = matchParentWidth,
        )
        .applyIfNotNull(aspectRatio) { ratio ->
            aspectRatio(ratio)
        }
        .applyIf(aspectRatio == null) {
            val verticalAlignment = data.alignmentVertical?.observedValue()?.toVerticalAlignment()
                ?: layoutParams.defaultVerticalAlignment
            height(
                height = data.height,
                verticalAlignment = verticalAlignment,
                matchParent = matchParentHeight,
            )
        }
}

@Composable
private fun Modifier.width(
    width: DivSize,
    horizontalAlignment: Alignment.Horizontal,
    matchParent: MatchParentSize,
): Modifier {
    return when (width) {
        is DivSize.MatchParent -> applySizeBounds(width.value.minSize, width.value.maxSize, isWidth = true)
            .matchParentWidth(matchParent)

        is DivSize.WrapContent -> wrapWidth(width.value, horizontalAlignment)
            .applySizeBounds(width.value.minSize, width.value.maxSize, isWidth = true)

        is DivSize.Fixed -> wrapContentWidth(align = horizontalAlignment, unbounded = true)
            .requiredWidth(width.value.observedValue())
    }
}

@Composable
private fun Modifier.wrapWidth(
    width: DivWrapContentSize,
    alignment: Alignment.Horizontal,
): Modifier {
    return wrapContentWidth(
        align = alignment,
        unbounded = width.constrained?.observedValue() != true,
    )
}

@Composable
private fun Modifier.height(
    height: DivSize,
    verticalAlignment: Alignment.Vertical,
    matchParent: MatchParentSize,
): Modifier {
    return when (height) {
        is DivSize.MatchParent -> applySizeBounds(height.value.minSize, height.value.maxSize, isWidth = false)
            .matchParentHeight(matchParent)

        is DivSize.WrapContent -> {
            val isConstrained = height.value.constrained?.observedValue() == true
            wrapContentHeight(align = verticalAlignment, unbounded = !isConstrained)
                .applySizeBounds(height.value.minSize, height.value.maxSize, isWidth = false)
        }

        is DivSize.Fixed -> wrapContentHeight(align = verticalAlignment, unbounded = true)
            .requiredHeight(height.value.observedValue())
    }
}

private fun Modifier.matchParentWidth(size: MatchParentSize): Modifier = when (size) {
    MatchParentSize.Fill -> fillMaxWidth()
    MatchParentSize.Content -> this
    is MatchParentSize.Bounded -> widthIn(max = size.max).fillMaxWidth()
}

private fun Modifier.matchParentHeight(size: MatchParentSize): Modifier = when (size) {
    MatchParentSize.Fill -> fillMaxHeight()
    MatchParentSize.Content -> this
    is MatchParentSize.Bounded -> heightIn(max = size.max).fillMaxHeight()
}

@Composable
private fun Modifier.applySizeBounds(
    minSize: DivSizeUnitValue?,
    maxSize: DivSizeUnitValue?,
    isWidth: Boolean,
): Modifier {
    val min = minSize?.observedValue()
    val max = maxSize?.observedValue()
    if (min == null && max == null) return this
    if (min != null && max != null && min > max) return this
    return if (isWidth) {
        widthIn(min = min ?: Dp.Unspecified, max = max ?: Dp.Unspecified)
    } else {
        heightIn(min = min ?: Dp.Unspecified, max = max ?: Dp.Unspecified)
    }
}

// A match_parent bound covers the element together with its margins.
private fun MatchParentSize.excludeMargins(margins: PaddingValues?, isWidth: Boolean): MatchParentSize {
    if (this !is MatchParentSize.Bounded || margins == null) return this
    val marginsSum = if (isWidth) {
        margins.calculateStartPadding(LayoutDirection.Ltr) + margins.calculateEndPadding(LayoutDirection.Ltr)
    } else {
        margins.calculateTopPadding() + margins.calculateBottomPadding()
    }
    return MatchParentSize.Bounded((max - marginsSum).coerceAtLeast(0.dp))
}
