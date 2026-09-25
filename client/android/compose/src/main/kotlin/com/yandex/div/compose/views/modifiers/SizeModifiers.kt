package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.aspectRatio
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
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div.compose.utils.aspect
import com.yandex.div.compose.utils.combineAlignment
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div.compose.views.modifiers.image.imageAspectRatio
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnitValue
import com.yandex.div2.DivWrapContentSize

@Composable
internal fun Modifier.size(
    div: Div,
    fillMatchParentWidth: Boolean = true,
    fillMatchParentHeight: Boolean = true,
    defaultHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    defaultVerticalAlignment: Alignment.Vertical = Alignment.Top,
): Modifier {
    val data = div.value()
    val aspectRatio = div.observedAspectRatio()
    val horizontalAlignment = data.alignmentHorizontal?.observedValue()
    val width = data.width
    val isImage = div is Div.Image || div is Div.GifImage
    if (isImage && aspectRatio != null && width is DivSize.WrapContent) {
        return wrapWidth(width.value, horizontalAlignment)
            .imageAspectRatio(
                ratio = aspectRatio,
                height = data.height,
                alignment = combineAlignment(
                    horizontalAlignment?.toHorizontalAlignment() ?: defaultHorizontalAlignment,
                    data.alignmentVertical?.observedValue()?.toVerticalAlignment() ?: defaultVerticalAlignment,
                ),
                fillMatchParentHeight = fillMatchParentHeight,
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
            fillMatchParent = fillMatchParentWidth,
        )
        .applyIfNotNull(aspectRatio) { ratio ->
            aspectRatio(ratio)
        }
        .applyIf(aspectRatio == null) {
            height(
                height = data.height,
                verticalAlignment = data.alignmentVertical?.observedValue(),
                fillMatchParent = fillMatchParentHeight,
            )
        }
}

@Composable
private fun Div.observedAspectRatio(): Float? {
    val aspect = value().aspect
    val aspectRatio = aspect?.ratio?.observedFloatValue() ?: return null
    return if (aspectRatio > 0f) aspectRatio else null
}

@Composable
private fun Modifier.width(
    width: DivSize,
    horizontalAlignment: DivAlignmentHorizontal? = null,
    fillMatchParent: Boolean,
): Modifier {
    val align = horizontalAlignment?.toHorizontalAlignment() ?: Alignment.Start
    return when (width) {
        is DivSize.MatchParent -> applySizeBounds(
            width.value.minSize,
            width.value.maxSize,
            isWidth = true
        )
            .applyIf(fillMatchParent) { fillMaxWidth() }

        is DivSize.WrapContent -> wrapWidth(width.value, horizontalAlignment)
            .applySizeBounds(width.value.minSize, width.value.maxSize, isWidth = true)

        is DivSize.Fixed -> wrapContentWidth(align = align, unbounded = true)
            .requiredWidth(width.value.observedValue())
    }
}

@Composable
private fun Modifier.wrapWidth(width: DivWrapContentSize, alignment: DivAlignmentHorizontal?): Modifier =
    wrapContentWidth(
        align = alignment?.toHorizontalAlignment() ?: Alignment.Start,
        unbounded = width.constrained?.observedValue() != true,
    )

@Composable
private fun Modifier.height(
    height: DivSize,
    verticalAlignment: DivAlignmentVertical? = null,
    fillMatchParent: Boolean,
): Modifier {
    val align = verticalAlignment?.toVerticalAlignment() ?: Alignment.Top
    return when (height) {
        is DivSize.MatchParent -> applySizeBounds(
            height.value.minSize,
            height.value.maxSize,
            isWidth = false
        )
            .applyIf(fillMatchParent) { fillMaxHeight() }

        is DivSize.WrapContent -> {
            val isConstrained = height.value.constrained?.observedValue() == true
            wrapContentHeight(align = align, unbounded = !isConstrained)
                .applySizeBounds(height.value.minSize, height.value.maxSize, isWidth = false)
        }

        is DivSize.Fixed -> wrapContentHeight(align = align, unbounded = true)
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

