package com.yandex.div.core.util

import android.view.Gravity
import android.view.View
import com.yandex.div.internal.widget.DivGravity
import kotlin.math.roundToInt

internal fun View.getIndices(start: Int, count: Int) =
    if (isLayoutRtl()) start + count - 1 downTo start else start until start + count

internal fun getSpaceAroundPart(freeSpace: Float, childCount: Int) = freeSpace / (childCount * 2)

internal fun getSpaceBetweenPart(freeSpace: Float, childCount: Int) =
    if (childCount == 1) 0f else freeSpace / (childCount - 1)

internal fun getSpaceEvenlyPart(freeSpace: Float, childCount: Int) = freeSpace / (childCount + 1)

internal fun getOffsets(freeSpace: Float, gravity: Int, childCount: Int) = when (gravity) {
    Gravity.LEFT, Gravity.TOP -> OffsetsHolder()
    Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL -> OffsetsHolder(firstChildOffset = freeSpace / 2)
    Gravity.RIGHT, Gravity.BOTTOM -> OffsetsHolder(firstChildOffset = freeSpace)
    DivGravity.SPACE_AROUND_HORIZONTAL, DivGravity.SPACE_AROUND_VERTICAL -> {
        getSpaceAroundPart(freeSpace, childCount).let {
            OffsetsHolder(
                firstChildOffset = it,
                spaceBetweenChildren = it * 2,
                edgeDividerOffset = (it / 2).roundToInt()
            )
        }
    }
    DivGravity.SPACE_BETWEEN_HORIZONTAL, DivGravity.SPACE_BETWEEN_VERTICAL ->
        OffsetsHolder(spaceBetweenChildren = getSpaceBetweenPart(freeSpace, childCount))
    DivGravity.SPACE_EVENLY_HORIZONTAL, DivGravity.SPACE_EVENLY_VERTICAL -> {
        getSpaceEvenlyPart(freeSpace, childCount).let { OffsetsHolder(
            firstChildOffset = it,
            spaceBetweenChildren = it,
            edgeDividerOffset = (it / 2).roundToInt()
        ) }
    }
    else -> throw java.lang.IllegalStateException("Invalid gravity is set: $gravity")
}

internal class OffsetsHolder(
    val firstChildOffset: Float = 0f,
    val spaceBetweenChildren: Float = 0f,
    val edgeDividerOffset: Int = 0
)
