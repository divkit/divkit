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
import com.yandex.div.compose.utils.observedDpValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toHorizontalAlignment
import com.yandex.div.compose.utils.toVerticalAlignment
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnitValue

@Composable
internal fun Modifier.size(div: Div): Modifier {
    val data = div.value()
    val aspectRatio = div.observedAspectRatio()
    return this
        .width(data.width, data.alignmentHorizontal?.observedValue())
        .applyIfNotNull(aspectRatio) { ratio ->
            aspectRatio(ratio)
        }
        .applyIf(aspectRatio == null) {
            height(data.height, data.alignmentVertical?.observedValue())
        }
}

@Composable
private fun Div.observedAspectRatio(): Float? {
    val aspect = when (this) {
        is Div.Container -> value.aspect
        is Div.Image -> value.aspect
        is Div.GifImage -> value.aspect
        is Div.Video -> value.aspect
        else -> null
    }
    val aspectRatio = aspect?.ratio?.observedFloatValue() ?: return null
    return if (aspectRatio > 0f) aspectRatio else null
}

@Composable
private fun Modifier.width(
    width: DivSize,
    horizontalAlignment: DivAlignmentHorizontal? = null
): Modifier {
    val align = horizontalAlignment?.toHorizontalAlignment() ?: Alignment.Start
    return when (width) {
        is DivSize.MatchParent -> applySizeBounds(
            width.value.minSize,
            width.value.maxSize,
            isWidth = true
        )
            .fillMaxWidth()

        is DivSize.WrapContent -> {
            val isConstrained = width.value.constrained?.observedValue() == true
            wrapContentWidth(align = align, unbounded = !isConstrained)
                .applySizeBounds(width.value.minSize, width.value.maxSize, isWidth = true)
        }

        is DivSize.Fixed -> wrapContentWidth(align = align, unbounded = true)
            .requiredWidth(width.value.observedValue())
    }
}

@Composable
private fun Modifier.height(
    height: DivSize,
    verticalAlignment: DivAlignmentVertical? = null
): Modifier {
    val align = verticalAlignment?.toVerticalAlignment() ?: Alignment.Top
    return when (height) {
        is DivSize.MatchParent -> applySizeBounds(
            height.value.minSize,
            height.value.maxSize,
            isWidth = false
        )
            .fillMaxHeight()

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
    val min = minSize?.toDp()
    val max = maxSize?.toDp()
    if (min == null && max == null) return this
    if (min != null && max != null && min > max) return this
    return if (isWidth) {
        widthIn(min = min ?: Dp.Unspecified, max = max ?: Dp.Unspecified)
    } else {
        heightIn(min = min ?: Dp.Unspecified, max = max ?: Dp.Unspecified)
    }
}

@Composable
private fun DivSizeUnitValue.toDp(): Dp {
    return value.observedDpValue(unit)
}
