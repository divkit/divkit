package com.yandex.div.compose.views.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiMeasureLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observedAspectRatio
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.views.container.wrap.ContainerWrapHorizontalView
import com.yandex.div.compose.views.container.wrap.ContainerWrapVerticalView
import com.yandex.div.compose.views.modifiers.clipToBounds
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContainer.LayoutMode
import com.yandex.div2.DivContainer.Orientation
import com.yandex.div2.DivSize
import com.yandex.div2.DivWrapContentSize
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
internal fun DivContainerView(
    modifier: Modifier,
    data: DivContainer
) {
    val modifier = if (data.clipToBounds.observedValue()) {
        modifier.clipToBounds(
            data = data,
            hasWildcardExtensions = divContext.component.wildcardExtensions.isNotEmpty(),
        )
    } else {
        modifier
    }

    val width = data.width
    val wrapContentAspectRatio = if (width is DivSize.WrapContent) data.observedAspectRatio() else null
    if (width !is DivSize.WrapContent || wrapContentAspectRatio == null) {
        ContainerLayout(modifier, data)
        return
    }
    WrapContentAspectLayout(
        ratio = wrapContentAspectRatio,
        minWidth = width.value.observedMinSize(),
        modifier = modifier,
    ) {
        ContainerLayout(Modifier, data)
    }
}

/**
 * The min_size the size modifiers apply to the width, see `Modifier.size`: bounds with min_size
 * above max_size are ignored.
 */
@Composable
private fun DivWrapContentSize.observedMinSize(): Dp {
    val min = minSize?.observedValue() ?: return 0.dp
    val max = maxSize?.observedValue()
    return if (max != null && min > max) 0.dp else min
}

@Composable
@NonRestartableComposable
private fun ContainerLayout(modifier: Modifier, data: DivContainer) {
    when (data.orientation.observedValue()) {
        Orientation.HORIZONTAL -> when (data.layoutMode.observedValue()) {
            LayoutMode.WRAP -> ContainerWrapHorizontalView(modifier, data)
            LayoutMode.NO_WRAP -> ContainerHorizontalView(modifier, data)
        }

        Orientation.VERTICAL -> when (data.layoutMode.observedValue()) {
            LayoutMode.WRAP -> ContainerWrapVerticalView(modifier, data)
            LayoutMode.NO_WRAP -> ContainerVerticalView(modifier, data)
        }

        Orientation.OVERLAP -> ContainerOverlapView(modifier, data)
    }
}

/**
 * Sizes a wrap-content container to `width / ratio` in height. The content is measured once, under
 * the incoming bounds, to find the width it wraps, then again with that width and the derived
 * height exactly, so that match-parent children fill the aspect height and constrained children
 * shrink to it. The first pass measures instead of asking the content for its intrinsic width:
 * lazy galleries, pagers and tabs are built on SubcomposeLayout and cannot answer intrinsic
 * queries (a parent's own intrinsic query is still forwarded to the content, as for any
 * container). Overflow beyond the parent's bounds is aligned by the element's wrap-content size
 * modifiers, as for a fixed size. Intrinsic queries pass the width without the size modifiers'
 * bounds, so [minWidth] repeats min_size there.
 */
@Suppress("DEPRECATION")
@Composable
private fun WrapContentAspectLayout(
    ratio: Float,
    minWidth: Dp,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    MultiMeasureLayout(
        modifier = modifier,
        content = content,
        measurePolicy = remember(ratio, minWidth) { WrapContentAspectMeasurePolicy(ratio, minWidth) },
    )
}

private class WrapContentAspectMeasurePolicy(
    private val ratio: Float,
    private val minWidth: Dp,
) : MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val measurable = measurables.single()
        val contentWidth = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0)).width
        val width = constraints.constrainWidth(contentWidth)
        val childConstraints = Constraints.fitPrioritizingWidth(width, width, heightOf(width), heightOf(width))
        val placeable = measurable.measure(childConstraints)
        val size = constraints.constrain(IntSize(childConstraints.maxWidth, childConstraints.maxHeight))
        return layout(size.width, size.height) {
            placeable.place(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Int): Int =
        contentWidth(measurables)

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Int): Int =
        contentWidth(measurables)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int =
        intrinsicHeight(measurables, width)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int =
        intrinsicHeight(measurables, width)

    private fun IntrinsicMeasureScope.intrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int =
        heightOf(min(max(contentWidth(measurables), minWidth.roundToPx()), width))

    private fun contentWidth(measurables: List<IntrinsicMeasurable>): Int =
        measurables.single().maxIntrinsicWidth(Constraints.Infinity)

    private fun heightOf(width: Int): Int = (width / ratio).roundToInt()
}
