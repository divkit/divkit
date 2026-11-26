package com.yandex.div.compose.views.container.wrap

import androidx.compose.ui.geometry.Rect

/**
 * Stores child bounds and reuses line descriptors between draw passes.
 *
 * [prepare] is called from draw, so it must not allocate. Capacity is adjusted from a
 * composition side effect before children are placed.
 */
internal class WrapSeparatorGeometry(
    private val isHorizontal: Boolean,
) {
    private val childRects = mutableListOf<Rect>()
    private val lines = mutableListOf<WrapLine>()

    private var preparedWidth = Float.NaN
    private var preparedRtl = false
    private var isDirty = true

    var lineCount: Int = 0
        private set

    fun resize(childCount: Int) {
        if (childRects.size == childCount) return
        while (childRects.size < childCount) childRects.add(Rect.Zero)
        while (childRects.size > childCount) childRects.removeAt(childRects.lastIndex)
        // Every child may start a line of its own.
        while (lines.size < childCount) lines.add(WrapLine())
        isDirty = true
    }

    fun updateChild(index: Int, rect: Rect) {
        if (index !in childRects.indices || childRects[index] == rect) {
            return
        }
        childRects[index] = rect
        isDirty = true
    }

    /** Rebuilds line descriptors when child placement or layout direction changed. */
    fun prepare(containerWidth: Float, isRtl: Boolean) {
        if (!isDirty && preparedWidth == containerWidth && preparedRtl == isRtl) return
        preparedWidth = containerWidth
        preparedRtl = isRtl
        rebuildLines(containerWidth, isRtl)
        isDirty = false
    }

    fun lineAt(index: Int): WrapLine {
        require(index in 0 until lineCount)
        return lines[index]
    }

    fun isChildPlaced(index: Int): Boolean = childRects[index] != Rect.Zero

    /** Where the child starts along its line; horizontal positions follow the layout direction. */
    fun childMainStart(index: Int, containerWidth: Float, isRtl: Boolean): Float =
        childRects[index].start(alongWidth = isHorizontal, containerWidth, isRtl)

    fun childMainEnd(index: Int, containerWidth: Float, isRtl: Boolean): Float =
        childRects[index].end(alongWidth = isHorizontal, containerWidth, isRtl)

    // A child that starts where the current line ends across the container starts the next line.
    private fun rebuildLines(containerWidth: Float, isRtl: Boolean) {
        lineCount = 0
        for (index in childRects.indices) {
            val rect = childRects[index]
            if (rect == Rect.Zero) continue
            val crossStart = rect.start(alongWidth = !isHorizontal, containerWidth, isRtl)
            val crossEnd = rect.end(alongWidth = !isHorizontal, containerWidth, isRtl)
            val mainStart = rect.start(alongWidth = isHorizontal, containerWidth, isRtl)
            val mainEnd = rect.end(alongWidth = isHorizontal, containerWidth, isRtl)
            if (lineCount == 0 || crossStart >= lines[lineCount - 1].crossAxisMax) {
                lines[lineCount++].start(index, crossStart, crossEnd, mainStart, mainEnd)
            } else {
                lines[lineCount - 1].extend(index, crossStart, crossEnd, mainStart, mainEnd)
            }
        }
    }
}

/** Where the rect starts along the width or the height; along the width it follows the layout direction. */
private fun Rect.start(alongWidth: Boolean, containerWidth: Float, isRtl: Boolean): Float = when {
    !alongWidth -> top
    isRtl -> containerWidth - right
    else -> left
}

private fun Rect.end(alongWidth: Boolean, containerWidth: Float, isRtl: Boolean): Float = when {
    !alongWidth -> bottom
    isRtl -> containerWidth - left
    else -> right
}

internal class WrapLine {
    var firstIndex: Int = 0
        private set
    var lastIndex: Int = 0
        private set
    var crossAxisMin: Float = 0f
        private set
    var crossAxisMax: Float = 0f
        private set
    var mainAxisMin: Float = 0f
        private set
    var mainAxisMax: Float = 0f
        private set

    fun start(index: Int, crossStart: Float, crossEnd: Float, mainStart: Float, mainEnd: Float) {
        firstIndex = index
        lastIndex = index
        crossAxisMin = crossStart
        crossAxisMax = crossEnd
        mainAxisMin = mainStart
        mainAxisMax = mainEnd
    }

    fun extend(index: Int, crossStart: Float, crossEnd: Float, mainStart: Float, mainEnd: Float) {
        lastIndex = index
        crossAxisMin = minOf(crossAxisMin, crossStart)
        crossAxisMax = maxOf(crossAxisMax, crossEnd)
        mainAxisMin = minOf(mainAxisMin, mainStart)
        mainAxisMax = maxOf(mainAxisMax, mainEnd)
    }
}
