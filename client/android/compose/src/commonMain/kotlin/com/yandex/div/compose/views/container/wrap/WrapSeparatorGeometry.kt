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
        val currentSize = childRects.size
        when {
            currentSize < childCount -> {
                repeat(childCount - currentSize) {
                    childRects.add(Rect.Zero)
                }
                if (lines.size < childCount) {
                    repeat(childCount - lines.size) {
                        lines.add(WrapLine())
                    }
                }
                isDirty = true
            }

            currentSize > childCount -> {
                childRects.subList(childCount, currentSize).clear()
                isDirty = true
            }
        }
    }

    fun updateChild(index: Int, rect: Rect) {
        if (index !in childRects.indices || childRects[index] == rect) {
            return
        }
        childRects[index] = rect
        isDirty = true
    }

    /**
     * Rebuilds line descriptors only when child placement or layout direction changed.
     *
     * @return `true` when descriptors were rebuilt.
     */
    fun prepare(containerWidth: Float, isRtl: Boolean): Boolean {
        if (!isDirty && preparedWidth == containerWidth && preparedRtl == isRtl) {
            return false
        }

        preparedWidth = containerWidth
        preparedRtl = isRtl
        rebuildLines(containerWidth, isRtl)
        isDirty = false
        return true
    }

    fun lineAt(index: Int): WrapLine {
        require(index in 0 until lineCount)
        return lines[index]
    }

    fun isChildPlaced(index: Int): Boolean = childRects[index] != Rect.Zero

    fun childRectAt(index: Int): Rect = childRects[index]

    fun logicalLeft(rect: Rect, containerWidth: Float, isRtl: Boolean): Float =
        if (isRtl) containerWidth - rect.right else rect.left

    fun logicalRight(rect: Rect, containerWidth: Float, isRtl: Boolean): Float =
        if (isRtl) containerWidth - rect.left else rect.right

    private fun rebuildLines(containerWidth: Float, isRtl: Boolean) {
        lineCount = 0

        var lineFirstIndex = -1
        var lineLastIndex = -1
        var crossAxisMin = 0f
        var crossAxisMax = 0f

        var index = 0
        while (index < childRects.size) {
            val rect = childRects[index]
            if (rect != Rect.Zero) {
                val crossStart = if (isHorizontal) {
                    rect.top
                } else {
                    logicalLeft(rect, containerWidth, isRtl)
                }
                val crossEnd = if (isHorizontal) {
                    rect.bottom
                } else {
                    logicalRight(rect, containerWidth, isRtl)
                }

                when {
                    lineFirstIndex < 0 -> {
                        lineFirstIndex = index
                        lineLastIndex = index
                        crossAxisMin = crossStart
                        crossAxisMax = crossEnd
                    }

                    crossStart >= crossAxisMax -> {
                        writeLine(
                            firstIndex = lineFirstIndex,
                            lastIndex = lineLastIndex,
                            crossAxisMin = crossAxisMin,
                            crossAxisMax = crossAxisMax,
                        )
                        lineFirstIndex = index
                        lineLastIndex = index
                        crossAxisMin = crossStart
                        crossAxisMax = crossEnd
                    }

                    else -> {
                        lineLastIndex = index
                        crossAxisMin = minOf(crossAxisMin, crossStart)
                        crossAxisMax = maxOf(crossAxisMax, crossEnd)
                    }
                }
            }
            index++
        }

        if (lineFirstIndex >= 0) {
            writeLine(
                firstIndex = lineFirstIndex,
                lastIndex = lineLastIndex,
                crossAxisMin = crossAxisMin,
                crossAxisMax = crossAxisMax,
            )
        }
    }

    private fun writeLine(
        firstIndex: Int,
        lastIndex: Int,
        crossAxisMin: Float,
        crossAxisMax: Float,
    ) {
        lines[lineCount].update(
            firstIndex = firstIndex,
            lastIndex = lastIndex,
            crossAxisMin = crossAxisMin,
            crossAxisMax = crossAxisMax,
        )
        lineCount++
    }
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

    fun update(
        firstIndex: Int,
        lastIndex: Int,
        crossAxisMin: Float,
        crossAxisMax: Float,
    ) {
        this.firstIndex = firstIndex
        this.lastIndex = lastIndex
        this.crossAxisMin = crossAxisMin
        this.crossAxisMax = crossAxisMax
    }
}
