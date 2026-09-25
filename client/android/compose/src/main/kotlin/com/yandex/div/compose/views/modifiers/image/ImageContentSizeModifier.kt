package com.yandex.div.compose.views.modifiers.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div2.DivBase
import com.yandex.div2.DivImage
import com.yandex.div2.DivSize
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
internal fun Modifier.imageContentSize(
    data: DivBase,
    imagePainter: Painter?,
    previewPainter: Painter?,
    contentScale: ContentScale,
    hasPlaceholder: Boolean,
): Modifier = then(
    ImageContentSizeElement(
        imagePainter = imagePainter,
        previewPainter = previewPainter,
        hasPlaceholder = hasPlaceholder,
        scaleToDensity = contentScale is FixedScale ||
            data.width is DivSize.WrapContent && data.height is DivSize.WrapContent,
        adjustViewBounds = data is DivImage,
        wrapsWidth = data.width is DivSize.WrapContent,
        wrapsHeight = data.height is DivSize.WrapContent,
        reporter = LocalComponent.current.reporter,
    )
)

private data class ImageContentSizeElement(
    val imagePainter: Painter?,
    val previewPainter: Painter?,
    val hasPlaceholder: Boolean,
    val scaleToDensity: Boolean,
    val adjustViewBounds: Boolean,
    val wrapsWidth: Boolean,
    val wrapsHeight: Boolean,
    val reporter: DivReporter,
) : ModifierNodeElement<ImageContentSizeNode>() {
    override fun create() = ImageContentSizeNode(this)

    override fun update(node: ImageContentSizeNode) {
        node.params = this
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "imageContentSize"
    }
}

private class ImageContentSizeNode(
    var params: ImageContentSizeElement,
) : Modifier.Node(), LayoutModifierNode {
    private var reportedSize: IntSize? = null

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val childConstraints = if (constraints.hasFixedWidth && constraints.hasFixedHeight) {
            constraints
        } else {
            val size = resolveSize(constraints)
            val fixedConstraints = Constraints.fitPrioritizingWidth(
                minWidth = size.width,
                maxWidth = size.width,
                minHeight = size.height,
                maxHeight = size.height,
            )
            if (fixedConstraints.maxWidth != size.width || fixedConstraints.maxHeight != size.height) {
                if (reportedSize != size) {
                    params.reporter.reportWarning("Image intrinsic size exceeds Compose layout limits: $size")
                    reportedSize = size
                }
            } else {
                reportedSize = null
            }
            fixedConstraints
        }
        val placeable = measurable.measure(childConstraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int) =
        intrinsicWidth(height)

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int) =
        intrinsicWidth(height)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) =
        intrinsicHeight(width)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) =
        intrinsicHeight(width)

    private fun Density.intrinsicWidth(height: Int): Int = resolveSize(
        Constraints.fitPrioritizingHeight(
            minWidth = 0,
            maxWidth = Constraints.Infinity,
            minHeight = if (!params.wrapsHeight && height != Constraints.Infinity) height else 0,
            maxHeight = height,
        )
    ).width

    private fun Density.intrinsicHeight(width: Int): Int = resolveSize(
        Constraints.fitPrioritizingWidth(
            minWidth = if (!params.wrapsWidth && width != Constraints.Infinity) width else 0,
            maxWidth = width,
            minHeight = 0,
            maxHeight = Constraints.Infinity,
        )
    ).height

    private fun Density.resolveSize(constraints: Constraints): IntSize {
        val painterSize = params.imagePainter?.intrinsicSize?.takeIf { it.isSpecified && it.width.isFinite() && it.height.isFinite() }
            ?: params.previewPainter?.intrinsicSize?.takeIf { it.isSpecified && it.width.isFinite() && it.height.isFinite() }
        // ImageView treats a ColorDrawable placeholder as 1x1 px for adjustViewBounds.
        val intrinsicSize = painterSize ?: if (params.hasPlaceholder) Size(1f, 1f) else Size.Zero
        val scale = if (painterSize != null && params.scaleToDensity) density else 1f
        val intrinsicWidth = (intrinsicSize.width * scale).roundToInt().coerceIn(0, MAX_LAYOUT_SIZE)
        val intrinsicHeight = (intrinsicSize.height * scale).roundToInt().coerceIn(0, MAX_LAYOUT_SIZE)
        var width = constraints.constrainWidth(intrinsicWidth)
        var height = constraints.constrainHeight(intrinsicHeight)
        if (!params.adjustViewBounds || intrinsicWidth == 0 || intrinsicHeight == 0) {
            return IntSize(width, height)
        }

        val ratio = intrinsicWidth.toFloat() / intrinsicHeight
        if (abs(width.toFloat() / height - ratio) > 0.0000001f) {
            var widthAdjusted = false
            if (!constraints.hasFixedWidth) {
                val desiredWidth = (ratio * height).toInt()
                if (constraints.hasFixedHeight) {
                    width = constraints.constrainWidth(desiredWidth)
                }
                if (desiredWidth <= width) {
                    width = constraints.constrainWidth(desiredWidth)
                    widthAdjusted = true
                }
            }
            if (!widthAdjusted && !constraints.hasFixedHeight) {
                val desiredHeight = (width / ratio).toInt()
                if (constraints.hasFixedWidth) {
                    height = constraints.constrainHeight(desiredHeight)
                }
                if (desiredHeight <= height) {
                    height = constraints.constrainHeight(desiredHeight)
                }
            }
        }
        return IntSize(width, height)
    }
}
