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
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.isUnspecified
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
 * [DivBlockLayoutParams.matchParentMaxWidth] and [DivBlockLayoutParams.matchParentMaxHeight]
 * bound the size, including margins, that a match_parent element fills when the parent measures
 * it with unbounded constraints, e.g. a gallery item along the scroll axis. The element's own
 * min_size and max_size take precedence over them.
 */
@Composable
internal fun Modifier.size(
    div: Div,
    layoutParams: DivBlockLayoutParams,
    margins: PaddingValues?,
): Modifier {
    val matchParentMaxWidth = layoutParams.matchParentMaxWidth
        .excludeMargins(margins, isWidth = true)
    val matchParentMaxHeight = layoutParams.matchParentMaxHeight
        .excludeMargins(margins, isWidth = false)
    val data = div.value()
    val aspectRatio = data.observedAspectRatio()
    val horizontalAlignment = data.alignmentHorizontal?.observedValue()?.toHorizontalAlignment()
        ?: layoutParams.defaultHorizontalAlignment
    val width = data.width
    val isImage = div is Div.Image || div is Div.GifImage
    if (isImage && aspectRatio != null && width is DivSize.WrapContent) {
        val verticalAlignment = data.alignmentVertical?.observedValue()?.toVerticalAlignment()
            ?: layoutParams.defaultVerticalAlignment
        return wrapWidth(width = width.value, alignment = horizontalAlignment)
            .imageAspectRatio(
                ratio = aspectRatio,
                height = data.height,
                alignment = combineAlignment(horizontalAlignment, verticalAlignment),
                fillMatchParentHeight = layoutParams.fillMatchParentHeight,
                matchParentMaxHeight = matchParentMaxHeight,
                // DivImageView keeps the measured width and derives height from aspect,
                // even for fixed/match_parent height. DivGifImageView can resize width.
                matchHeightConstraintsFirst = div is Div.GifImage,
            )
            .applySizeBounds(width.value.minSize, width.value.maxSize, isWidth = true)
    }
    return this
        .width(
            width = width,
            horizontalAlignment = horizontalAlignment,
            fillMatchParent = layoutParams.fillMatchParentWidth,
            matchParentMax = matchParentMaxWidth,
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
                fillMatchParent = layoutParams.fillMatchParentHeight,
                matchParentMax = matchParentMaxHeight,
            )
        }
}

@Composable
private fun Modifier.width(
    width: DivSize,
    horizontalAlignment: Alignment.Horizontal,
    fillMatchParent: Boolean,
    matchParentMax: Dp,
): Modifier {
    return when (width) {
        is DivSize.MatchParent -> applySizeBounds(
            width.value.minSize,
            width.value.maxSize,
            isWidth = true
        )
            .applyIf(matchParentMax.isSpecified) { widthIn(max = matchParentMax) }
            .applyIf(fillMatchParent) { fillMaxWidth() }

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
    fillMatchParent: Boolean,
    matchParentMax: Dp,
): Modifier {
    return when (height) {
        is DivSize.MatchParent -> applySizeBounds(
            height.value.minSize,
            height.value.maxSize,
            isWidth = false
        )
            .applyIf(matchParentMax.isSpecified) { heightIn(max = matchParentMax) }
            .applyIf(fillMatchParent) { fillMaxHeight() }

        is DivSize.WrapContent -> {
            val isConstrained = height.value.constrained?.observedValue() == true
            wrapContentHeight(align = verticalAlignment, unbounded = !isConstrained)
                .applySizeBounds(height.value.minSize, height.value.maxSize, isWidth = false)
        }

        is DivSize.Fixed -> wrapContentHeight(align = verticalAlignment, unbounded = true)
            .requiredHeight(height.value.observedValue())
    }
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
private fun Dp.excludeMargins(margins: PaddingValues?, isWidth: Boolean): Dp {
    if (isUnspecified || margins == null) return this
    val marginsSum = if (isWidth) {
        margins.calculateStartPadding(LayoutDirection.Ltr) + margins.calculateEndPadding(LayoutDirection.Ltr)
    } else {
        margins.calculateTopPadding() + margins.calculateBottomPadding()
    }
    return (this - marginsSum).coerceAtLeast(0.dp)
}
