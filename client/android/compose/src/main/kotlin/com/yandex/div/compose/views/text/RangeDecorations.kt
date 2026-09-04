package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observedPxValue
import com.yandex.div.compose.views.modifiers.onDivVisibilityChanged
import com.yandex.div.internal.core.TextRangeCloudBackground
import com.yandex.div.internal.core.TextRangeParticleSystem
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivText
import com.yandex.div2.DivTextRangeBackground
import com.yandex.div2.DivTextRangeBorder
import com.yandex.div2.DivTextRangeMask
import kotlin.math.max
import kotlin.math.roundToInt

@Immutable
internal data class AnnotatedText(
    val text: androidx.compose.ui.text.AnnotatedString,
    val decorations: List<TextRangeDecoration>,
)

@Immutable
internal data class TextRangeDecoration(
    val start: Int,
    val end: Int,
    val background: RangeBackground?,
    val border: RangeBorder?,
    val mask: RangeMask?,
) {
    val hidesText: Boolean get() = mask != null
}

@Immutable
internal sealed interface RangeBackground {
    data class Solid(val color: Color) : RangeBackground

    data class Cloud(
        val color: Color,
        val cornerRadiusPx: Float,
        val paddingsPx: RangePaddings,
    ) : RangeBackground
}

@Immutable
internal data class RangeBorder(
    val cornerRadiusPx: Float,
    val stroke: RangeStroke?,
)

@Immutable
internal data class RangeStroke(
    val color: Color,
    val widthPx: Float,
)

@Immutable
internal sealed interface RangeMask {
    val color: Color

    data class Solid(override val color: Color) : RangeMask

    data class Particles(
        override val color: Color,
        val density: Float,
        val isAnimated: Boolean,
        val particleSizePx: Float,
    ) : RangeMask
}

@Immutable
internal data class RangePaddings(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
)

@Composable
internal fun DivText.Range.observeDecoration(start: Int, end: Int): TextRangeDecoration? {
    if (background == null && border == null && mask == null) return null

    val background = background?.observeBackground()
    val border = if (background is RangeBackground.Cloud) null else border?.observeBorder()
    val mask = mask?.observeMask()
    if (background == null && border?.stroke == null && mask == null) return null

    return remember(start, end, background, border, mask) {
        TextRangeDecoration(start, end, background, border, mask)
    }
}

@Composable
private fun DivTextRangeBackground.observeBackground(): RangeBackground {
    return when (this) {
        is DivTextRangeBackground.Solid -> {
            val color = value.color.observedColorValue()
            remember(color) { RangeBackground.Solid(color) }
        }

        is DivTextRangeBackground.Cloud -> {
            val color = value.color.observedColorValue()
            val cornerRadiusPx = value.cornerRadius.observedPxValue(DivSizeUnit.DP)
            val paddingsPx = value.paddings.observePaddings()
            remember(color, cornerRadiusPx, paddingsPx) {
                RangeBackground.Cloud(color, cornerRadiusPx, paddingsPx)
            }
        }
    }
}

@Composable
private fun DivTextRangeBorder.observeBorder(): RangeBorder {
    val cornerRadiusPx = cornerRadius?.observedPxValue(DivSizeUnit.DP) ?: 0f
    val stroke = stroke?.let { stroke ->
        val color = stroke.color.observedColorValue()
        val widthPx = stroke.width.observedPxValue(stroke.unit)
        remember(color, widthPx) { RangeStroke(color, widthPx) }
    }
    return remember(cornerRadiusPx, stroke) { RangeBorder(cornerRadiusPx, stroke) }
}

@Composable
private fun DivTextRangeMask.observeMask(): RangeMask? {
    return when (this) {
        is DivTextRangeMask.Solid -> {
            if (!value.isEnabled.observedValue()) return null
            val color = value.color.observedColorValue()
            remember(color) { RangeMask.Solid(color) }
        }

        is DivTextRangeMask.Particles -> {
            if (!value.isEnabled.observedValue()) return null
            val color = value.color.observedColorValue()
            val densityValue = value.density.observedFloatValue()
            val isAnimated = value.isAnimated.observedValue()
            val particleSize = value.particleSize
            val particleSizePx = particleSize.value.observedPxValue(particleSize.unit)
            remember(color, densityValue, isAnimated, particleSizePx) {
                RangeMask.Particles(color, densityValue, isAnimated, particleSizePx)
            }
        }
    }
}

@Composable
private fun DivEdgeInsets?.observePaddings(): RangePaddings {
    this ?: return ZERO_RANGE_PADDINGS

    val unit = unit.observedValue()
    val topPx = top.observedPxValue(unit)
    val bottomPx = bottom.observedPxValue(unit)
    val leftPx: Float
    val rightPx: Float
    if (start != null || end != null) {
        val layoutDirection = LocalLayoutDirection.current
        val startPx = start?.observedPxValue(unit) ?: 0f
        val endPx = end?.observedPxValue(unit) ?: 0f
        if (layoutDirection == LayoutDirection.Rtl) {
            leftPx = endPx
            rightPx = startPx
        } else {
            leftPx = startPx
            rightPx = endPx
        }
    } else {
        leftPx = left.observedPxValue(unit)
        rightPx = right.observedPxValue(unit)
    }
    return remember(leftPx, topPx, rightPx, bottomPx) {
        RangePaddings(leftPx, topPx, rightPx, bottomPx)
    }
}

@Composable
internal fun Modifier.drawTextRangeDecorations(
    layoutResult: State<TextLayoutResult?>,
    decorations: List<TextRangeDecoration>,
): Modifier {
    if (decorations.isEmpty()) return this

    val hasAnimatedParticles = decorations.any {
        (it.mask as? RangeMask.Particles)?.isAnimated == true
    }
    val displayDensity = LocalDensity.current.density
    val particleDrawings = decorations.mapIndexed { index, decoration ->
        key(index, decoration.start, decoration.end) {
            (decoration.mask as? RangeMask.Particles)?.let { mask ->
                remember(mask.density, mask.particleSizePx, displayDensity) {
                    ParticleDrawing(decoration.start, decoration.end, mask.density, mask.particleSizePx, displayDensity)
                }
            }
        }
    }
    val visibility = if (hasAnimatedParticles) remember { mutableStateOf(false) } else null
    val onVisibilityChanged = visibility?.let {
        remember(it) { { visible: Boolean -> it.value = visible } }
    }
    val frameTimeNanos = rememberParticleAnimationTime(visibility?.value == true)
    val modifier = if (onVisibilityChanged == null) {
        this
    } else {
        onDivVisibilityChanged(minFractionVisible = 0f, onVisibilityChanged = onVisibilityChanged)
    }

    return modifier.drawWithCache {
        val layout = layoutResult.value
        if (layout == null) {
            return@drawWithCache onDrawWithContent { drawContent() }
        }

        val geometries = decorations.mapIndexedNotNull { index, decoration ->
            buildDecorationGeometry(layout, decoration, particleDrawings[index])
        }
        onDrawWithContent {
            geometries.forEach { it.drawBackground(this) }
            drawContent()
            val currentFrameTimeNanos = frameTimeNanos.value
            geometries.forEach { it.drawMask(this, currentFrameTimeNanos) }
        }
    }
}

@Composable
internal fun rememberParticleAnimationTime(isRunning: Boolean): State<Long> {
    val elapsedTimeNanos = remember { mutableLongStateOf(0L) }
    LaunchedEffect(isRunning) {
        if (isRunning) {
            var previousFrame = withFrameNanos { it }
            while (true) {
                val frame = withFrameNanos { it }
                elapsedTimeNanos.longValue += (frame - previousFrame).coerceAtLeast(0L)
                previousFrame = frame
            }
        }
    }
    return elapsedTimeNanos
}

private fun buildDecorationGeometry(
    layout: TextLayoutResult,
    decoration: TextRangeDecoration,
    particleDrawing: ParticleDrawing?,
): DecorationGeometry? {
    val lineRects = layout.lineRects(decoration.start, decoration.end)
    if (lineRects.isEmpty()) return null

    val backgroundPath = when (val background = decoration.background) {
        is RangeBackground.Solid -> lineRects.toRangePath(decoration.border?.cornerRadiusPx ?: 0f)
        is RangeBackground.Cloud -> lineRects.toCloudPath(background.cornerRadiusPx, background.paddingsPx)
        null -> null
    }
    val borderPath = if (decoration.background is RangeBackground.Cloud) {
        null
    } else {
        decoration.border?.stroke?.let { stroke ->
            lineRects.toRangePath(
                cornerRadiusPx = decoration.border.cornerRadiusPx,
                insetPx = stroke.widthPx / 2f,
            )
        }
    }
    val borderStroke = decoration.border?.stroke?.widthPx?.let(::Stroke)
    particleDrawing?.updateBounds(lineRects)
    return DecorationGeometry(decoration, lineRects, backgroundPath, borderPath, borderStroke, particleDrawing)
}

private fun TextLayoutResult.lineRects(start: Int, end: Int): List<RangeLine> {
    if (lineCount == 0) return emptyList()

    val lastLine = lineCount - 1
    val renderedTextEnd = getLineEnd(
        lastLine,
        visibleEnd = layoutInput.overflow == TextOverflow.Ellipsis && isLineEllipsized(lastLine),
    )
    val safeStart = start.coerceIn(0, renderedTextEnd)
    val safeEnd = end.coerceIn(safeStart, renderedTextEnd)
    if (safeStart >= safeEnd) return emptyList()

    val startLine = getLineForOffset(safeStart)
    val endLine = getLineForOffset((safeEnd - 1).coerceAtLeast(safeStart))
    val lines = (startLine..endLine).mapNotNull { line ->
        val lineStart = max(safeStart, getLineStart(line))
        val rawLineEnd = getLineEnd(line, visibleEnd = false)
        val lineEnd = if (safeEnd >= rawLineEnd) {
            getLineEnd(line, visibleEnd = true)
        } else {
            safeEnd
        }
        if (lineStart >= lineEnd) return@mapNotNull null

        val rects = selectionRects(lineStart, lineEnd, getLineTop(line), getLineBottom(line))
        if (rects.isEmpty()) return@mapNotNull null

        val startBox = getBoundingBox(lineStart)
        val endBox = getBoundingBox(lineEnd - 1)
        val startX = if (getBidiRunDirection(lineStart) == ResolvedTextDirection.Ltr) startBox.left else startBox.right
        val endX = if (getBidiRunDirection(lineEnd - 1) == ResolvedTextDirection.Ltr) endBox.right else endBox.left
        SelectedRangeLine(rects, startX, endX)
    }
    return lines.flatMapIndexed { index, line ->
        line.rects.map { rect ->
            RangeLine(
                rect,
                line.startX.takeIf { index == 0 && it in rect.left..rect.right },
                line.endX.takeIf { index == lines.lastIndex && it in rect.left..rect.right },
            )
        }
    }
}

private fun TextLayoutResult.selectionRects(start: Int, end: Int, top: Float, bottom: Float): List<Rect> {
    // Character bounds stay on their own line at a wrap and retain each BiDi run's visual position.
    // Merge only touching visual intervals: a logical range can leave gaps on a mixed-direction line.
    val rects = (start until end).mapNotNull { offset ->
        val box = getBoundingBox(offset)
        val rect = Rect(box.left, top, box.right, bottom)
        rect.takeUnless { it.isEmpty }
    }
    val merged = mutableListOf<Rect>()
    for (rect in rects.sortedBy { it.left }) {
        val previous = merged.lastOrNull()
        if (previous != null && rect.left <= previous.right) {
            merged[merged.lastIndex] = Rect(previous.left, previous.top, max(previous.right, rect.right), previous.bottom)
        } else {
            merged += rect
        }
    }
    return merged
}

private fun List<RangeLine>.toRangePath(cornerRadiusPx: Float, insetPx: Float = 0f): Path {
    val path = Path()
    forEach { line ->
        val rect = line.rect
        val insetRect = Rect(
            left = rect.left + insetPx,
            top = rect.top + insetPx,
            right = rect.right - insetPx,
            bottom = rect.bottom - insetPx,
        )
        if (insetRect.width <= 0f || insetRect.height <= 0f) return@forEach

        val radius = max(0f, cornerRadiusPx - insetPx)
        val leftRadius = if (line.startX == rect.left || line.endX == rect.left) radius else 0f
        val rightRadius = if (line.startX == rect.right || line.endX == rect.right) radius else 0f
        path.addRoundRect(
            RoundRect(
                rect = insetRect,
                topLeft = CornerRadius(leftRadius),
                topRight = CornerRadius(rightRadius),
                bottomRight = CornerRadius(rightRadius),
                bottomLeft = CornerRadius(leftRadius),
            )
        )
    }
    return path
}

private fun List<RangeLine>.toCloudPath(cornerRadiusPx: Float, paddings: RangePaddings): Path {
    val lines = map { line ->
        val rect = line.rect
        TextRangeCloudBackground.LineBounds(
            left = (rect.left - paddings.left).roundToInt(),
            top = (rect.top - paddings.top).roundToInt(),
            right = (rect.right + paddings.right).roundToInt(),
            bottom = (rect.bottom + paddings.bottom).roundToInt(),
        )
    }
    val path = Path()
    val sink = object : TextRangeCloudBackground.PathSink {
        override fun moveTo(x: Float, y: Float) = path.moveTo(x, y)
        override fun relativeLineTo(dx: Float, dy: Float) = path.relativeLineTo(dx, dy)
        override fun relativeQuadraticTo(dx1: Float, dy1: Float, dx2: Float, dy2: Float) =
            path.relativeQuadraticTo(dx1, dy1, dx2, dy2)
        override fun close() = path.close()
    }
    val rows = indices.groupBy { this[it].rect.top }.values.map { row ->
        val merged = mutableListOf<TextRangeCloudBackground.LineBounds>()
        for (index in row) {
            val line = lines[index]
            val previous = merged.lastOrNull()
            if (previous != null && line.left <= previous.right) {
                merged[merged.lastIndex] = previous.copy(right = max(previous.right, line.right))
            } else {
                merged += line
            }
        }
        merged
    }
    val radius = cornerRadiusPx.roundToInt()
    if (rows.all { it.size == 1 }) {
        TextRangeCloudBackground.buildPath(rows.map { it.single() }, radius, sink)
        return path
    }

    // At a BiDi fork/merge, join every overlapping pair, not just adjacent list entries.
    // Subpaths share one nonzero-filled path, so overlapping joins do not compound alpha.
    val connected = rows.map { BooleanArray(it.size) }
    for (row in 0 until rows.lastIndex) {
        for (upper in rows[row].indices) {
            for (lower in rows[row + 1].indices) {
                val first = rows[row][upper]
                val second = rows[row + 1][lower]
                if (first.left >= second.right || second.left >= first.right) continue
                TextRangeCloudBackground.buildPath(
                    listOf(first, second), radius, sink, mergeCloseBounds = false,
                )
                connected[row][upper] = true
                connected[row + 1][lower] = true
            }
        }
    }
    for (row in rows.indices) {
        for (index in rows[row].indices) {
            if (!connected[row][index]) TextRangeCloudBackground.buildPath(listOf(rows[row][index]), radius, sink)
        }
    }
    return path
}

private data class DecorationGeometry(
    val decoration: TextRangeDecoration,
    val lineRects: List<RangeLine>,
    val backgroundPath: Path?,
    val borderPath: Path?,
    val borderStroke: Stroke?,
    val particleSystem: ParticleDrawing?,
) {
    fun drawBackground(scope: DrawScope) = with(scope) {
        val background = decoration.background
        val path = backgroundPath
        if (background != null && path != null) {
            val color = when (background) {
                is RangeBackground.Solid -> background.color
                is RangeBackground.Cloud -> background.color
            }
            drawPath(path, color)
        }
        val stroke = decoration.border?.stroke
        val strokePath = borderPath
        val style = borderStroke
        if (stroke != null && strokePath != null && style != null) {
            drawPath(strokePath, stroke.color, style = style)
        }
    }

    fun drawMask(scope: DrawScope, frameTimeNanos: Long) = with(scope) {
        when (val mask = decoration.mask) {
            is RangeMask.Solid -> lineRects.forEach { drawRect(mask.color, it.rect.topLeft, it.rect.size) }
            is RangeMask.Particles -> particleSystem?.draw(this, mask.color, frameTimeNanos, mask.isAnimated)
            null -> Unit
        }
    }
}

private data class RangeLine(
    val rect: Rect,
    val startX: Float?,
    val endX: Float?,
)

private data class SelectedRangeLine(
    val rects: List<Rect>,
    val startX: Float,
    val endX: Float,
)

private class ParticleDrawing(
    private val start: Int,
    private val end: Int,
    private val density: Float,
    private val particleSizePx: Float,
    private val displayDensity: Float,
) {
    private var lines: List<RangeLine>? = null
    private var particleSystem: TextRangeParticleSystem? = null
    private var lastFrameTimeNanos = Long.MIN_VALUE

    fun updateBounds(lines: List<RangeLine>) {
        if (this.lines == lines) return
        this.lines = lines
        particleSystem = TextRangeParticleSystem(
            bounds = lines.map { line ->
                TextRangeParticleSystem.Bounds(line.rect.left, line.rect.top, line.rect.right, line.rect.bottom)
            },
            start = start,
            end = end,
            density = density,
            particleSizePx = particleSizePx,
            displayDensity = displayDensity,
        )
        lastFrameTimeNanos = Long.MIN_VALUE
    }

    fun draw(scope: DrawScope, color: Color, frameTimeNanos: Long, isAnimated: Boolean) = with(scope) {
        val particleSystem = particleSystem ?: return@with
        if (isAnimated) {
            if (lastFrameTimeNanos != Long.MIN_VALUE) {
                val elapsedSeconds = (frameTimeNanos - lastFrameTimeNanos).coerceAtLeast(0L) / 1_000_000_000f
                particleSystem.advance(elapsedSeconds)
            }
        }
        lastFrameTimeNanos = frameTimeNanos
        particleSystem.particles.forEach { particle ->
            drawCircle(
                color = color,
                radius = particle.radius,
                center = androidx.compose.ui.geometry.Offset(particle.constrainedX, particle.constrainedY),
            )
        }
    }
}

private val ZERO_RANGE_PADDINGS = RangePaddings(0f, 0f, 0f, 0f)
