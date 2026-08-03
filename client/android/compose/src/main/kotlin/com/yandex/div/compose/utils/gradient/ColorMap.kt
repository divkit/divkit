package com.yandex.div.compose.utils.gradient

import androidx.compose.runtime.Immutable

@Immutable
internal data class ColorMap(
    val colors: IntArray,
    val positions: FloatArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ColorMap) return false

        if (!colors.contentEquals(other.colors)) return false
        if (!positions.contentEquals(other.positions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = colors.contentHashCode()
        result = 31 * result + positions.contentHashCode()
        return result
    }
}
