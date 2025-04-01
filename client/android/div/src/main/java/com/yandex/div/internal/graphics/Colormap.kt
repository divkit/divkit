package com.yandex.div.internal.graphics

import com.yandex.div.core.actions.logError
import com.yandex.div.core.view2.Div2View

internal class Colormap(
    val colors: IntArray,
    val positions: FloatArray? = null
) {

    init {
        require(colors.size == (positions?.size ?: colors.size))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Colormap

        if (!colors.contentEquals(other.colors)) return false
        if (!positions.contentEquals(other.positions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = colors.contentHashCode()
        result = 31 * result + (positions?.contentHashCode() ?: 0)
        return result
    }

    companion object {

        @JvmField
        val EMPTY = Colormap(IntArray(0))
    }
}

internal fun Colormap.checkIsNotEmpty(divView: Div2View): Colormap {
    if (this == Colormap.EMPTY) {
        divView.logError(
            IllegalStateException("Colors for linear gradient are not provided. Please check if 'colors' or 'color_map' properties are defined")
        )
    }
    return this
}
