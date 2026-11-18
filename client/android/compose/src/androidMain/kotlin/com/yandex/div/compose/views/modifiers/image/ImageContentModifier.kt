package com.yandex.div.compose.views.modifiers.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.times
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div2.DivBase
import com.yandex.div2.DivImage
import com.yandex.div2.DivSize
import kotlin.math.abs
import kotlin.math.roundToInt

private const val ASPECT_RATIO_EPSILON = 0.0000001f

@Composable
internal fun Modifier.imageContent(
    data: DivBase,
    imagePainter: Painter?,
    previewPainter: Painter?,
    contentScale: ContentScale,
    hasPlaceholder: Boolean,
    alignment: Alignment,
    colorFilter: ColorFilter?,
): Modifier = then(
    ImageContentElement(
        imagePainter = imagePainter,
        previewPainter = previewPainter,
        hasPlaceholder = hasPlaceholder,
        contentScale = contentScale,
        alignment = alignment,
        colorFilter = colorFilter,
        scaleToDensity = contentScale is FixedScale ||
            data.width is DivSize.WrapContent && data.height is DivSize.WrapContent,
        adjustViewBounds = data is DivImage,
        wrapsWidth = data.width is DivSize.WrapContent,
        wrapsHeight = data.height is DivSize.WrapContent,
        reporter = LocalComponent.current.reporter,
    )
)

private data class ImageContentElement(
    val imagePainter: Painter?,
    val previewPainter: Painter?,
    val hasPlaceholder: Boolean,
    val contentScale: ContentScale,
    val alignment: Alignment,
    val colorFilter: ColorFilter?,
    val scaleToDensity: Boolean,
    val adjustViewBounds: Boolean,
    val wrapsWidth: Boolean,
    val wrapsHeight: Boolean,
    val reporter: DivReporter,
) : ModifierNodeElement<ImageContentNode>() {
    override fun create() = ImageContentNode(this)

    override fun update(node: ImageContentNode) {
        val previous = node.params
        node.params = this
        if (hasDifferentSizeProperties(previous)) node.invalidateMeasurement()
        node.invalidateDraw()
    }

    private fun hasDifferentSizeProperties(previous: ImageContentElement): Boolean {
        if (imagePainter !== previous.imagePainter) return true
        if (previewPainter !== previous.previewPainter) return true
        if (hasPlaceholder != previous.hasPlaceholder) return true
        if (scaleToDensity != previous.scaleToDensity) return true
        if (adjustViewBounds != previous.adjustViewBounds) return true
        if (wrapsWidth != previous.wrapsWidth) return true
        return wrapsHeight != previous.wrapsHeight
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "imageContent"
    }
}

private class ImageContentNode(
    var params: ImageContentElement,
) : Modifier.Node(), LayoutModifierNode, DrawModifierNode {
    private var reportedSize: IntSize? = null

    // Drawing-only changes (alignment, scaling and tint) do not affect the resolved layout size.
    override val shouldAutoInvalidate = false

    override fun ContentDrawScope.draw() {
        if (params.imagePainter == null && params.previewPainter == null) {
            drawContent()
            return
        }
        // Keep clipping local to drawing, without introducing a layer or a semantics shape.
        clipRect {
            params.previewPainter?.let { drawPainter(it) }
            params.imagePainter?.let { drawPainter(it) }
            this@draw.drawContent()
        }
    }

    private fun DrawScope.drawPainter(painter: Painter) {
        val intrinsicSize = painter.intrinsicSize
        val sourceWidth = if (intrinsicSize.isSpecified && intrinsicSize.width.isFinite()) {
            intrinsicSize.width
        } else {
            size.width
        }
        val sourceHeight = if (intrinsicSize.isSpecified && intrinsicSize.height.isFinite()) {
            intrinsicSize.height
        } else {
            size.height
        }
        val sourceSize = Size(sourceWidth, sourceHeight)
        val scaledSize = if (size.width != 0f && size.height != 0f) {
            sourceSize * params.contentScale.computeScaleFactor(sourceSize, size)
        } else {
            Size.Zero
        }
        val position = params.alignment.align(
            IntSize(scaledSize.width.roundToInt(), scaledSize.height.roundToInt()),
            IntSize(size.width.roundToInt(), size.height.roundToInt()),
            layoutDirection,
        )
        translate(position.x.toFloat(), position.y.toFloat()) {
            with(painter) { draw(size = scaledSize, colorFilter = params.colorFilter) }
        }
    }

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
        val painterSize = params.imagePainter?.finiteIntrinsicSize()
            ?: params.previewPainter?.finiteIntrinsicSize()
        // ImageView treats a ColorDrawable placeholder as 1x1 px for adjustViewBounds.
        val intrinsicSize = painterSize ?: if (params.hasPlaceholder) Size(1f, 1f) else Size.Zero
        val scale = if (painterSize != null && params.scaleToDensity) density else 1f
        val intrinsicWidth = (intrinsicSize.width * scale).roundToInt().coerceIn(0, MAX_LAYOUT_SIZE)
        val intrinsicHeight = (intrinsicSize.height * scale).roundToInt().coerceIn(0, MAX_LAYOUT_SIZE)
        val width = constraints.constrainWidth(intrinsicWidth)
        val height = constraints.constrainHeight(intrinsicHeight)
        if (!params.adjustViewBounds || intrinsicWidth == 0 || intrinsicHeight == 0) {
            return IntSize(width, height)
        }

        val ratio = intrinsicWidth.toFloat() / intrinsicHeight
        return if (abs(width.toFloat() / height - ratio) > ASPECT_RATIO_EPSILON) {
            adjustToAspectRatio(constraints, width, height, ratio)
        } else {
            IntSize(width, height)
        }
    }

    private fun adjustToAspectRatio(
        constraints: Constraints,
        width: Int,
        height: Int,
        ratio: Float,
    ): IntSize {
        var adjustedWidth = width
        var adjustedHeight = height
        var widthAdjusted = false
        if (!constraints.hasFixedWidth) {
            val desiredWidth = (ratio * height).toInt()
            if (constraints.hasFixedHeight) {
                adjustedWidth = constraints.constrainWidth(desiredWidth)
            }
            if (desiredWidth <= adjustedWidth) {
                adjustedWidth = constraints.constrainWidth(desiredWidth)
                widthAdjusted = true
            }
        }
        if (!widthAdjusted && !constraints.hasFixedHeight) {
            val desiredHeight = (adjustedWidth / ratio).toInt()
            if (constraints.hasFixedWidth) {
                adjustedHeight = constraints.constrainHeight(desiredHeight)
            }
            if (desiredHeight <= adjustedHeight) {
                adjustedHeight = constraints.constrainHeight(desiredHeight)
            }
        }
        return IntSize(adjustedWidth, adjustedHeight)
    }
}

private fun Painter.finiteIntrinsicSize(): Size? = intrinsicSize.takeIf {
    it.isSpecified && it.width.isFinite() && it.height.isFinite()
}
